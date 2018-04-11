package zheng.simon.com.frame.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by zhengyouquan on 05/02/2018.
 */

public class Result {

    /**
     * 成功失败
     */
    private ResultCode code;

    private Object obj;

    private String state = "";

    public Result(ResultCode success, Object obj) {
        this.code = success;
        this.obj = obj;
    }

    public ResultCode getCode() {
        return code;
    }

    public boolean isSuccess() {
        return code == ResultCode.RESULT_OK;
    }

    public Object getObj() {
        if (obj == null) {
            return "";
        }
        return obj;
    }


    public void setObj(Object obj) {
        this.obj = obj;
    }


    /**
     * @return 返回结果必须有result 字段
     */
    public String getResult() {
        try {
            JSONObject json = JSON.parseObject(getObj().toString());
            if (json != null && json.containsKey("result")) {
                String result = json.getString("result");
                if (TextUtils.isEmpty(result) || "[]".equals(result)) {
                    return "";
                }
                return result;
            }
            return "";

        } catch (Exception e) {
            return "";
        }
    }


    public void setCode(ResultCode code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
