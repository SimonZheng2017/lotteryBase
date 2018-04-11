package zheng.simon.com.frame.http.modle;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by zhengyouquan on 16/11/23.
 */

public class ResultModel implements Parcelable {

    private String msg;

    private String result;

    private String state;


    protected ResultModel(Parcel in) {
        msg = in.readString();
        result = in.readString();
        state = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msg);
        dest.writeString(result);
        dest.writeString(state);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResultModel> CREATOR = new Creator<ResultModel>() {
        @Override
        public ResultModel createFromParcel(Parcel in) {
            return new ResultModel(in);
        }

        @Override
        public ResultModel[] newArray(int size) {
            return new ResultModel[size];
        }
    };

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
