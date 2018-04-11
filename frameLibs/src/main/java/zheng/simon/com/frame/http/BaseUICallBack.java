package zheng.simon.com.frame.http;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zheng.simon.com.frame.widget.RxJavaUtil;

/**
 * 回调之后在主线程进行
 */

public abstract class BaseUICallBack implements Callback {


    @Override
    public void onResponse(Call call, Response response) throws IOException {
        //先处理数据，再开线程，否则会出现UI线程处理网络的异常
        String result = null;
        try {
            result = response.body().string();
        } catch (IOException e) {
            onFailure(call, e);
        }
        final String f_result = result;
        RxJavaUtil.runOnUiThread(() -> onResponse_UI(f_result, call, response));
    }

    @Override
    public void onFailure(Call call, IOException e) {
        e.printStackTrace();
        RxJavaUtil.runOnUiThread(() -> onFailure_UI(call, e));
    }

    public abstract void onResponse_UI(String resultStr, Call call, Response response);

    public abstract void onFailure_UI(Call call, IOException e);
}
