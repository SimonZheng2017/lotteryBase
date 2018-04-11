package zheng.simon.com.frame.widget;

import android.content.Context;

/**
 * Created by zhengyouquan on 06/02/2018.
 */

public class SPUtil {

    private static SPUtil spUtil;
    private Context ctx;

    private SPUtil(Context ctx) {
        this.ctx = ctx;
    }

    public static SPUtil getInstance(Context ctx) {
        if (spUtil != null) {
            return spUtil;
        } else {
            return new SPUtil(ctx);
        }
    }


    public void saveUUID(String UUID) {
        MySharedPreferences sp = new MySharedPreferences(ctx, MySharedPreferences.SETTING, Context.MODE_MULTI_PROCESS);
        sp.putValue("DEVICE_ID", UUID);
    }

    public String getUUID() {
        MySharedPreferences sp = new MySharedPreferences(ctx, MySharedPreferences.SETTING, Context.MODE_MULTI_PROCESS);
        return sp.getValue("DEVICE_ID", "");
    }

}


