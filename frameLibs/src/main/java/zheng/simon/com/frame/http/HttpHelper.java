package zheng.simon.com.frame.http;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

import okhttp3.Response;
import zheng.simon.com.frame.app.HttpConfig;
import zheng.simon.com.frame.R;
import zheng.simon.com.frame.http.modle.ResultModel;
import zheng.simon.com.frame.widget.Mytools;

/**
 * Created by zhengyouquan on 05/02/2018.
 */

public class HttpHelper {

    /**
     * get请求访问接口
     *
     * @param url      接口相对路径
     * @param params   参数
     * @param callBack 回调接口
     */
    public static void get(Context ctx, String url, ApiParams params, ApiCallBack callBack) {

        if (!Mytools.isNetworkAvailable(ctx)) {
            Result result = new Result(ResultCode.NETWORK_TROBLE, ctx.getString(R.string.error_network_unavailable));
            if (callBack != null) {
                callBack.receive(result);
            }
        } else {
            if (params == null)
                params = new ApiParams();
            HttpUtil.get(HttpConfig.SERVER + url + params.getParams(!url.endsWith("&") && !url.endsWith("?")), new MyHttpCallBack(ctx, callBack));
        }
    }

    /**
     * 访问接口
     *
     * @param url      接口相对路径
     * @param params   参数
     * @param callBack 回调接口
     */
    public static void post(Context ctx, String url, ApiParams params, ApiCallBack callBack) {

        if (!Mytools.isNetworkAvailable(ctx)) {
            Result result = new Result(ResultCode.NETWORK_TROBLE, ctx.getString(R.string.error_network_unavailable));
            if (callBack != null) {
                callBack.receive(result);
            }
        } else {

            if (params == null)
                params = new ApiParams();

            HttpUtil.post(HttpConfig.SERVER + url, params.getPostParams(), new MyHttpCallBack(ctx, callBack));
        }
    }


    /**
     * 访问接口
     *
     * @param url      接口相对路径
     * @param params   参数
     * @param callBack 回调接口
     */
    public static void postFile(Context ctx, String url, ApiParams params, ApiCallBack callBack) {

        if (!Mytools.isNetworkAvailable(ctx)) {
            Result result = new Result(ResultCode.NETWORK_TROBLE, ctx.getString(R.string.error_network_unavailable));
            if (callBack != null) {
                callBack.receive(result);
            }
        } else {
            if (params == null)
                params = new ApiParams();
            HttpUtil.postFile(HttpConfig.SERVER + url, params.getFileUploadParams(), new MyHttpCallBack(ctx, callBack));
        }
    }

    /**
     * 统一处理回调
     */
    private static class MyHttpCallBack extends HttpCallBack {

        protected Result result;// 返回结果
        private Context ctx;
        private ApiCallBack callBack;

        public MyHttpCallBack(final Context ctx, final ApiCallBack callBack) {
            this.ctx = ctx;
            this.callBack = callBack;
        }

        /**
         * 处理返回结果的方法
         *
         * @param content
         * @param failStr
         * @return
         */
        private Result getResultByContent(String content, String failStr) {
            Result result;
            ResultModel json = JSON.parseObject(content, ResultModel.class);
            if ("1".equals(json.getState())) {
                result = new Result(ResultCode.RESULT_OK, content);
            } else {
                String msg = failStr;
                if (!TextUtils.isEmpty(json.getMsg())) {
                    msg = json.getMsg();
                }

                result = new Result(ResultCode.RESULT_FAILED, msg);

            }

            return result;
        }


        @Override
        public void OnSuccess(String resultStr, okhttp3.Call call, Response response) {
            try {
                result = getResultByContent(resultStr, ctx.getString(R.string.failed_reason));
            } catch (Exception e) {
                e.printStackTrace();
                if (TextUtils.isEmpty(resultStr)) {
                    resultStr = ctx.getString(R.string.failed_reason);
                }
                result = new Result(ResultCode.RESULT_FAILED, resultStr);
            }
        }

        @Override
        public void OnFailure(okhttp3.Call call, IOException e) {
            result = new Result(ResultCode.RESULT_FAILED, ctx.getString(R.string.error_network_unavailable));
        }

        @Override
        public void OnFinish() {
            if (callBack != null && result != null) {
                if (ctx instanceof Activity) {
                    Activity act = (Activity) ctx;
                    if (act.isFinishing()) {
                        return;
                    }
                }
                callBack.receive(result);
            }
        }
    }
}
