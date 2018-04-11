package photoselector.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.util.List;

import photoselector.model.PhotoModel;
import photoselector.ui.PhotoSelectorActivity;

/**
 * 通用工具类
 *
 * @author chenww
 */
public class CommonUtils {

    /**
     * 开启activity(带参数)
     */
    public static void launchActivity(Context context, Class<?> activity, Bundle bundle) {
        Intent intent = new Intent(context, activity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 多选的方式打开,不限制图片数
     */
    public static void OpenMoreSelect(Activity activity, int requestCode) {
        OpenMoreSelect(activity, Integer.MAX_VALUE, requestCode);
    }

    /**
     * 选择多种图片
     *
     * @param activity
     * @param canSelectNum 一次能选择的最大数量，如果为Integer.MAX_VALUE表示无限制选择
     * @param requestCode
     */
    public static void OpenMoreSelect(Activity activity, int canSelectNum, int requestCode) {
        OpenMoreSelect(activity, canSelectNum, requestCode, false);
    }


    public static void OpenMoreSelect(Activity activity, int canSelectNum, int requestCode, boolean isCamera) {
        if (canSelectNum > 0) {
            Intent intent = new Intent(activity, PhotoSelectorActivity.class);
            intent.putExtra(PhotoSelectorActivity.IS_SELECTOR_MORE, canSelectNum > 1 ? true : false);
            intent.putExtra(PhotoSelectorActivity.MaxSelctNum, canSelectNum);
            intent.putExtra(PhotoSelectorActivity.IS_CARMA, isCamera);
            activity.startActivityForResult(intent, requestCode);
        } else {
            Toast.makeText(activity, "不能再选图片了！！", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * @param fragment
     * @param canSelectNum
     * @param requestCode
     */
    public static void OpenMoreSelect(Fragment fragment, int canSelectNum, int requestCode) {
        if (canSelectNum > 0) {
            Intent intent = new Intent(fragment.getContext(), PhotoSelectorActivity.class);
            intent.putExtra(PhotoSelectorActivity.IS_SELECTOR_MORE, canSelectNum > 1 ? true : false);
            intent.putExtra(PhotoSelectorActivity.MaxSelctNum, canSelectNum);
            fragment.startActivityForResult(intent, requestCode);
        } else {
            Toast.makeText(fragment.getContext(), "不能再选图片了！！", Toast.LENGTH_SHORT).show();
        }
    }


    /***
     * 单选的方式打开
     */
    public static void OpenSingleSelect(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, PhotoSelectorActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 判断字符串是否为空
     *
     * @param text
     * @return true null false !null
     */
    public static boolean isNull(CharSequence text) {
        if (text == null || "".equals(text.toString().trim()) || "null".equals(text))
            return true;
        return false;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidthPixels(Context context) {
        // 获取当前屏幕
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeightPixels(Context context) {
        // 获取当前屏幕
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();

        boolean hasNav = checkDeviceHasNavigationBar(context);
        if (hasNav) {
            return dm.heightPixels - getNavigationBarHeight(context);
        }

        return dm.heightPixels;
    }

    /**
     * 检测是否有虚拟按键
     *
     * @param context
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }

        return hasNavigationBar;

    }

    /**
     * 获取虚拟按键的高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        int navigationBarHeight = 0;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0 && checkDeviceHasNavigationBar(context)) {
            navigationBarHeight = rs.getDimensionPixelSize(id);
        }
        return navigationBarHeight;
    }

    /**
     * 获取当前进程名字
     *
     * @param ctx
     * @return
     */
    public static String getCurrentProcessName(Context ctx) {
        final int processId = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo info : manager.getRunningAppProcesses()) {
            if (processId == info.pid) {
                return info.processName;
            }
        }

        return "";
    }

    /**
     * @param context
     * @param requestCode
     * @param resultCode
     * @param data
     * @param MyrequestCode
     * @return
     */
    public static List<PhotoModel> onActivityResult(Context context, int requestCode, int resultCode, Intent data, int MyrequestCode) {

        if (requestCode == MyrequestCode && resultCode == ((Activity) context).RESULT_OK) {
            // 调用相册的返回
            if (data != null && data.getExtras() != null) {
                @SuppressWarnings("unchecked")
                List<PhotoModel> photos = (List<PhotoModel>) data.getExtras()
                        .getSerializable("photos");

                return photos;
            }
        }
        return null;
    }
}
