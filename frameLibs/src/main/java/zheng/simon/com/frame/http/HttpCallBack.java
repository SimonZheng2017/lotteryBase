package zheng.simon.com.frame.http;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 *
 */

public abstract class HttpCallBack extends BaseUICallBack {


    @Override
    public void onResponse_UI(String resultStr, Call call, Response response) {
        OnSuccess(resultStr, call, response);
        OnFinish();
    }

    @Override
    public void onFailure_UI(Call call, IOException e) {
        e.printStackTrace();
        OnFailure(call, e);
        OnFinish();
    }


    public abstract void OnSuccess(String resultStr, Call call, Response response);

    public abstract void OnFailure(Call call, IOException e);

    public abstract void OnFinish();

}
