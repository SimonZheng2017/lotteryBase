package com.github.lzyzsd.jsbridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("SetJavaScriptEnabled")
public class BridgeWebView extends WebView implements WebViewJavascriptBridge {

    public static final String toLoadJs = "WebViewJavascriptBridge.js";
    Map<String, CallBackFunction> responseCallbacks = new HashMap<>();
    Map<String, BridgeHandler> messageHandlers = new HashMap<>();
    BridgeHandler defaultHandler = new DefaultHandler();
    private long uniqueId = 0;
    private ArrayList<String> mDetailImageList = new ArrayList<>();
    private OnImageClickListener listener;
    private WebViewClient mClient;

    private List<Message> startupMessage = new ArrayList<>();

    public BridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BridgeWebView(Context context) {
        super(context);
        init();
    }

    public static int dip2px(float dipValue, Context ctx) {
        return (int) (dipValue * ctx.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * @param handler default handler,handle messages send by js without assigned handler name,
     *                if js message has handler name, it will be handled by named handlers registered by native
     */
    public void setDefaultHandler(BridgeHandler handler) {
        this.defaultHandler = handler;
    }

    /**
     *
     */
    private void init() {
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        // 支持通过js打开新的窗口
        getSettings().setJavaScriptEnabled(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getSettings().setAppCacheEnabled(true);// 开启缓存
        getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 缓存优先模式
        getSettings().setAppCacheMaxSize(8 * 1024 * 1024);// 设置最大缓存为8M
//        getSettings().setSupportMultipleWindows(true);
        getSettings().setUseWideViewPort(true);
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setAllowFileAccess(true);
        getSettings().setLightTouchEnabled(true);
        getSettings().setDomStorageEnabled(true);
        getSettings().setDefaultTextEncodingName("gbk");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        setWebViewClient(generateBridgeWebViewClient());

        addJavascriptInterface(new JavaScriptObject(), "injectedObject");

        //webview在安卓5.0之前默认允许其加载混合网络协议内容
        // 在安卓5.0之后，默认不允许加载http与https混合内容，需要设置webview允许其加载混合网络协议内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

    }

    /**
     * @return
     */
    protected BridgeWebViewClient generateBridgeWebViewClient() {
        return new BridgeWebViewClient(this, mClient, listener);
    }

    /**
     * @param client
     */
    public void setWebViewClientFromSide(WebViewClient client) {
        mClient = client;
        setWebViewClient(generateBridgeWebViewClient());
    }

    /**
     * @return
     */
    public List<Message> getStartupMessage() {
        return startupMessage;
    }

    /**
     * @param startupMessage
     */
    public void setStartupMessage(List<Message> startupMessage) {
        this.startupMessage = startupMessage;
    }

    /**
     * @param url
     */
    void handlerReturnData(String url) {
        String functionName = BridgeUtil.getFunctionFromReturnUrl(url);
        CallBackFunction f = responseCallbacks.get(functionName);
        String data = BridgeUtil.getDataFromReturnUrl(url);
        if (f != null) {
            f.onCallBack(data);
            responseCallbacks.remove(functionName);
            return;
        }
    }

    @Override
    public void send(String data) {
        send(data, null);
    }

    @Override
    public void send(String data, CallBackFunction responseCallback) {
        doSend(null, data, responseCallback);
    }

    /**
     * @param handlerName
     * @param data
     * @param responseCallback
     */
    private void doSend(String handlerName, String data, CallBackFunction responseCallback) {
        Message m = new Message();
        if (!TextUtils.isEmpty(data)) {
            m.setData(data);
        }
        if (responseCallback != null) {
            String callbackStr = String.format(BridgeUtil.CALLBACK_ID_FORMAT, ++uniqueId + (BridgeUtil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis()));
            responseCallbacks.put(callbackStr, responseCallback);
            m.setCallbackId(callbackStr);
        }
        if (!TextUtils.isEmpty(handlerName)) {
            m.setHandlerName(handlerName);
        }
        queueMessage(m);
    }

    /**
     * @param m
     */
    private void queueMessage(Message m) {
        if (startupMessage != null) {
            startupMessage.add(m);
        } else {
            dispatchMessage(m);
        }
    }

    /**
     * @param m
     */
    void dispatchMessage(Message m) {
        String messageJson = m.toJson();
        //escape special characters for json string
        messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
        messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
        String javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            this.loadUrl(javascriptCommand);
        }
    }

    /**
     *
     */
    void flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, new CallBackFunction() {

                @Override
                public void onCallBack(String data) {
                    // deserializeMessage
                    List<Message> list = null;
                    try {
                        list = Message.toArrayList(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        Message m = list.get(i);
                        String responseId = m.getResponseId();
                        // 是否是response
                        if (!TextUtils.isEmpty(responseId)) {
                            CallBackFunction function = responseCallbacks.get(responseId);
                            String responseData = m.getResponseData();
                            function.onCallBack(responseData);
                            responseCallbacks.remove(responseId);
                        } else {
                            CallBackFunction responseFunction = null;
                            // if had callbackId
                            final String callbackId = m.getCallbackId();
                            if (!TextUtils.isEmpty(callbackId)) {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        Message responseMsg = new Message();
                                        responseMsg.setResponseId(callbackId);
                                        responseMsg.setResponseData(data);
                                        queueMessage(responseMsg);
                                    }
                                };
                            } else {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        // do nothing
                                    }
                                };
                            }
                            BridgeHandler handler;
                            if (!TextUtils.isEmpty(m.getHandlerName())) {
                                handler = messageHandlers.get(m.getHandlerName());
                            } else {
                                handler = defaultHandler;
                            }
                            if (handler != null) {
                                handler.handler(m.getData(), responseFunction);
                            }
                        }
                    }
                }
            });
        }
    }

    public void loadUrl(String jsUrl, CallBackFunction returnCallback) {
        this.loadUrl(jsUrl);
        responseCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback);
    }

    /**
     * register handler,so that javascript can call it
     *
     * @param handlerName
     * @param handler
     */
    public void registerHandler(String handlerName, BridgeHandler handler) {
        if (handler != null) {
            messageHandlers.put(handlerName, handler);
        }
    }

    /**
     * call javascript registered handler
     *
     * @param handlerName
     * @param data
     * @param callBack
     */
    public void callHandler(String handlerName, String data, CallBackFunction callBack) {
        doSend(handlerName, data, callBack);
    }


    /**
     * 把Html中的Img标签中的style设置为空
     *
     * @param html
     * @return
     */
    private String dealAttr(String html) {
        Document doc = Jsoup.parse(html);

        //图片
        Elements es = doc.getElementsByTag("img");
        for (Element e : es) {
            String imgUrl = e.attr("src");
            if (!TextUtils.isEmpty(imgUrl)) {

                //过滤掉file开头的本地文件
                if (imgUrl.startsWith("file")) {
                    e.attr("src", "");
                } else {
                    mDetailImageList.add(imgUrl);
                    e.attr("style", "");
                    e.attr("width", "");
                    e.attr("height", "");
                    e.attr("onclick", "openImage('" + imgUrl + "')");
                    //正则匹配.0x0.
                    String regex = "\\..+x.+\\.";
                    Pattern compile = Pattern.compile(regex);
                    Matcher matcher = compile.matcher(imgUrl);
                    if (matcher.find()) {
                        e.attr("src", getSrc(imgUrl, getScreentWidth() - dip2px(30, getContext()), 0));
                    }
                }

            }
        }

        //<a>标签中的 target="_blank" 使用，如果是"_blank"，加前缀，然后打开手机本地浏览器，反之打开app内部浏览器
        Elements a = doc.getElementsByTag("a");
        for (Element el : a) {
            el.attr("target", "_self");
        }


        Elements iframe = doc.getElementsByTag("iframe");
        for (Element el : iframe) {
            String width = el.attr("width");
//            el.attr("width", getScreentWidth() + "");
            if (!TextUtils.isEmpty(width))
                el.attr("height", (Integer.valueOf(width) * 9 / 16) + "");
        }
        return doc.html();
    }

    /**
     * @return
     */
    private int getScreentWidth() {
        return getContext().getApplicationContext().getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 设置htlm文本
     *
     * @param html
     */
    public void setHtmlData(String html) {
        loadDataWithBaseURL(null, dealAttr(html), "text/html", "utf-8", null);
    }

    /**
     * @return
     */
    private int findPosition(String url) {
        for (int i = mDetailImageList.size() - 1; i >= 0; i--) {
            if (mDetailImageList.get(i).equals(url))
                return i;
        }
        return 0;
    }

    /**
     * @param listener
     */
    public void setOnImageOpenListener(OnImageClickListener listener) {
        this.listener = listener;
        setWebViewClient(generateBridgeWebViewClient());
    }

    /**
     * 3.6.6服务器将图片添加到七牛，客户端需要对图片进行新的判断规则
     *
     * @param imgUrl
     * @param width
     * @param height
     * @return
     */
    public String getSrc(String imgUrl, int width, int height) {
        if (TextUtils.isEmpty(imgUrl)) {
            return "";
        }

        if (imgUrl.contains("file")) {
            return "";
        }

        //3.6.6之前的图片包含group，需要将老版的图片进行处理
        if (imgUrl.contains("group")) {
            int i = imgUrl.lastIndexOf(".");
            int j = imgUrl.substring(0, i).lastIndexOf(".");
            imgUrl = imgUrl.substring(0, j) + ".0x0" + imgUrl.substring(i);

        }

        if (imgUrl.indexOf("?") != -1) {
            imgUrl = imgUrl.substring(0, imgUrl.indexOf("?"));
        }


        if (width != 0 && height == 0) {//高为0
            imgUrl = imgUrl + "?imageMogr2/thumbnail/" + width + "x/gravity/Center/crop/" + width + "x";
        } else if (width == 0 && height != 0) { //宽为0

            imgUrl = imgUrl + "?imageMogr2/thumbnail/x" + height + "/gravity/Center/crop/x" + height;
        } else if (width != 0 && height != 0) {

            imgUrl = imgUrl + "?imageMogr2/thumbnail/!" + width + "x" + height + "r/gravity/Center/crop/!" + width + "x" + height;
        }

        return imgUrl;
    }

    public interface OnImageClickListener {
        void onImageClick(int currentPosition, ArrayList<String> urls);

        void onUrlClick(String url);
    }

    /**
     *
     */
    private class JavaScriptObject {

        @JavascriptInterface
        public void openImage(String url) {
            if (listener != null) {
                listener.onImageClick(findPosition(url), mDetailImageList);
            }
        }
    }

}
