package zheng.simon.com.frame.http;


import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import photoselector.util.FileUtil;
import zheng.simon.com.frame.BuildConfig;
import zheng.simon.com.frame.app.App;
import zheng.simon.com.frame.http.cookie.PersistentCookieJar;
import zheng.simon.com.frame.http.cookie.cache.SetCookieCache;
import zheng.simon.com.frame.http.cookie.https.HttpsUtils;
import zheng.simon.com.frame.http.cookie.persistence.SharedPrefsCookiePersistor;
import zheng.simon.com.frame.http.download.OnFileDownloadListener;
import zheng.simon.com.frame.http.download.ProgressListener;
import zheng.simon.com.frame.http.download.ProgressResponseBody;
import zheng.simon.com.frame.widget.RxJavaUtil;


/**
 * 开源库官网 http://loopj.com/android-async-http/
 *
 * @author milanoouser
 */
public class HttpUtil {

    private static OkHttpClient client; // 实例话对象
    private static OkHttpClient.Builder clientBuilder;

    static {
        clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(10, TimeUnit.SECONDS);
        clientBuilder.readTimeout(10, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(10, TimeUnit.SECONDS);
        clientBuilder.retryOnConnectionFailure(true);
        clientBuilder.cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(App.getInstance())));

        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor((new HttpLoggingInterceptor()).setLevel(HttpLoggingInterceptor.Level.BODY));
            clientBuilder.addNetworkInterceptor(new StethoInterceptor());
        }
        //支持https访问
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        clientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);

        client = clientBuilder.build();
    }

    /**
     * 使用get方法,用一个完整url获取一个string对象
     *
     * @param urlString url绝对路径
     * @param callBack  结果
     */
    public static void get(String urlString, HttpCallBack callBack) {
        Request request = new Request.Builder()
                .url(urlString)
                .get()
                .build();
        client.newCall(request).enqueue(callBack);
    }

    /**
     * 使用post方法,url里面带参数,获取一个string对象
     *
     * @param urlString   url绝对路径
     * @param formBuilder 参数
     * @param callBack    结果
     */
    public static void post(String urlString, FormBody.Builder formBuilder, HttpCallBack callBack) {
        RequestBody body = formBuilder.build();
        Request request = new Request.Builder()
                .url(urlString)
                .post(body)
                .build();
        client.newCall(request).enqueue(callBack);
    }


    /**
     * 使用post方法,上传文件
     *
     * @param urlString   url绝对路径
     * @param filebuilder 参数
     * @param callBack    结果
     */
    public static void postFile(String urlString, MultipartBody.Builder filebuilder, HttpCallBack callBack) {
        RequestBody body = filebuilder.build();
        Request request = new Request.Builder()
                .url(urlString)
                .post(body)
                .build();
        client.newCall(request).enqueue(callBack);
    }


    /**
     * 下载
     *
     * @param fileUrl  下载
     * @param savefile 本地文件存储的文件夹
     * @param listener 监听
     */
    public static void download(String fileUrl, final File savefile, final OnFileDownloadListener listener) {



        final Request request = new Request.Builder()
                .url(fileUrl)
                .build();

        final ProgressListener progressListener = new ProgressListener() {
            private int progress = -1;//onProgress到了100%的时候会执行2次，我们这里判断一下

            @Override
            public void update(final long bytesRead, final long contentLength, boolean done) {
                if (listener != null && contentLength > 0) {
                    int i = (int) ((100 * bytesRead) / contentLength);
                    if (progress == i) {
                        return;
                    }
                    progress = i;
                    listener.onProgress(progress);
                }
            }
        };


        clientBuilder.networkInterceptors().add(chain -> {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
        });


        clientBuilder.build().newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    byte[] bt = response.body().bytes();
                    if (!savefile.exists()) {
                        savefile.createNewFile();
                    }
                    FileUtil.getFileFromBytes(bt, savefile);

                    if (listener != null) {
                        RxJavaUtil.runOnUiThread(() -> listener.onResponse(savefile));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                //下载失败
                if (listener != null) {
                    RxJavaUtil.runOnUiThread(() -> listener.onFailure());
                }
            }

        });
    }

}
