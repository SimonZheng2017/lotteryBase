package zheng.simon.com.frame.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import zheng.simon.com.frame.R;
import zheng.simon.com.frame.app.App;


/**
 * Created by zhengyouquan on 17/3/2.
 */

public class ToastUtil {


    /**
     * 短时间显示Toast
     *
     * @param info 显示的内容
     */
    public static Toast showToast(String info) {
        Toast toast = Toast.makeText(App.getAppContext(), info, Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }


    /**
     * 短时间显示Toast
     *
     * @param info 显示的内容
     */
    public static Toast showToast(Activity activity, String info) {
        Toast toast = Toast.makeText(activity, info, Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }

    /**
     * 长时间显示Toast
     *
     * @param info 显示的内容
     */
    public static Toast showToastLong(Activity activity, String info) {
        Toast toast = Toast.makeText(activity, info, Toast.LENGTH_LONG);
        toast.show();
        return toast;
    }

    /**
     * 短时间显示Toast
     */
    public static Toast showToast(int resId) {
        Toast toast = Toast.makeText(App.getAppContext(), App.getAppContext().getString(resId), Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }

    /**
     * 长时间显示Toast
     */
    public static Toast showToastLong(int resId) {
        Toast toast = Toast.makeText(App.getAppContext(), resId, Toast.LENGTH_LONG);
        toast.show();
        return toast;
    }


    /**
     * toast显示在屏幕中间,长时间显示
     *
     * @param context
     * @param info
     * @return
     */
    public static Toast showToastCenter(Context context, String info, long time) {
        final Toast toast = Toast.makeText(App.getAppContext(), info, Toast.LENGTH_LONG);

        View view = View.inflate(context, R.layout.layout_toast, null);
        TextView txt_toast_info =  view.findViewById(R.id.txt_toast_info);
        txt_toast_info.setText(info);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);


        Handler handler = new Handler();
        handler.postDelayed(() -> toast.cancel(), time);

        return toast;
    }

}
