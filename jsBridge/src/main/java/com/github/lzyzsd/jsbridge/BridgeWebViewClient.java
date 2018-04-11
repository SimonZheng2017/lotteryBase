package com.github.lzyzsd.jsbridge;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by bruce on 10/28/15.
 */
public class BridgeWebViewClient extends WebViewClient {

    private String mPrevUrl;

    private BridgeWebView webView;
    private WebViewClient mClient;
    private BridgeWebView.OnImageClickListener listener;

    public BridgeWebViewClient(BridgeWebView webView, WebViewClient client, BridgeWebView.OnImageClickListener mListener) {
        this.webView = webView;
        mClient = client;
        listener = mListener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        boolean isDeal = false;
        if (mClient != null)
            isDeal = mClient.shouldOverrideUrlLoading(view, url);

        if (isDeal) return true;

        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (url.startsWith(BridgeUtil.YY_RETURN_DATA)) { // 如果是返回数据
            webView.handlerReturnData(url);
            return true;
        } else if (url.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) { //
            webView.flushMessageQueue();
            return true;
        }

        return dealOurOwn(view, url);
    }

    /**
     * @param view
     * @param url
     * @return
     */
    private boolean dealOurOwn(WebView view, String url) {
        if (mPrevUrl != null) {
            if (!mPrevUrl.equals(url)) {
                if (!url.startsWith("http")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        view.getContext().startActivity(intent);
                    } catch (Exception var6) {
                        var6.printStackTrace();
                    }
                    return true;
                } else {
                    mPrevUrl = url;
                    if (listener != null) {
                        if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                            listener.onUrlClick(url);
                            return true;
                        }
                    } else {
                        return super.shouldOverrideUrlLoading(view, url);
                    }
                }
            } else {
                return false;
            }
        } else {
            mPrevUrl = url;
            if (listener != null) {
                if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                    listener.onUrlClick(url);
                    return true;
                }
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (mClient != null)
            mClient.onPageStarted(view, url, favicon);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (mClient != null)
            mClient.onPageFinished(view, url);
        super.onPageFinished(view, url);

        if (BridgeWebView.toLoadJs != null) {
            BridgeUtil.webViewLoadLocalJs(view, BridgeWebView.toLoadJs);
        }

        if (webView.getStartupMessage() != null) {
            for (Message m : webView.getStartupMessage()) {
                webView.dispatchMessage(m);
            }
            webView.setStartupMessage(null);
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (mClient != null)
            mClient.onReceivedError(view, errorCode, description, failingUrl);
        super.onReceivedError(view, errorCode, description, failingUrl);
    }


    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
    }


}