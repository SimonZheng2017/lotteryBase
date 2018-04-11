package zheng.simon.com.frame.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * 屏幕分辨率等参数获取
 */
public class DisplayUtil {

    /**
     * @param ctx
     * @param spVal
     * @return
     */
    public static int sp2px(Context ctx, int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, ctx.getResources().getDisplayMetrics());
    }

    /**
     * @param ctx
     * @param var1
     * @return
     */
    public static int px2sp(Context ctx, float var1) {
        float var2 = ctx.getResources().getDisplayMetrics().scaledDensity;
        return (int) (var1 / var2 + 0.5F);
    }

    /**
     * dip转成pixels
     */
    public static int dipToPixels(int dip, Resources r) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return (int) px;
    }

    /**
     * dip转px
     *
     * @param ctx
     * @param dipValue
     * @return
     */
    public static int dip2px(Context ctx, float dipValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转dip
     *
     * @param ctx
     * @param pxValue
     * @return
     */
    public static int px2dip(Context ctx, float pxValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽度和高度，单位为px
     *
     * @param ctx
     * @return
     */
    public static Point getScreenMetrics(Context ctx) {
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        return new Point(w_screen, h_screen);

    }

    /**
     * 获取屏幕长宽比
     *
     * @param ctx
     * @return
     */
    public static float getScreenRate(Context ctx) {
        Point P = getScreenMetrics(ctx);
        float H = P.y;
        float W = P.x;
        return (H / W);
    }

    /**
     * 获取状态栏高度
     *
     * @param act
     * @return
     */
    public static int getStatusBarHeight(Activity act) {
        Rect frame = new Rect();
        act.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 检测是否有虚拟按键
     *
     * @param ctx
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context ctx) {
        boolean hasNavigationBar = false;
        Resources rs = ctx.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }

        return hasNavigationBar;

    }

    /**
     * 获取虚拟按键的高度
     *
     * @param ctx
     * @return
     */
    public static int getNavigationBarHeight(Context ctx) {
        int navigationBarHeight = 0;
        Resources rs = ctx.getResources();
        int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0 && checkDeviceHasNavigationBar(ctx)) {
            navigationBarHeight = rs.getDimensionPixelSize(id);
        }
        return navigationBarHeight;
    }

    /**
     * 根据构造函数获得当前手机的手机宽度
     */
    public static int getDensity_Width(Context ctx) {
        // 获取当前屏幕
        DisplayMetrics dm = ctx.getApplicationContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        return width;

    }

    /**
     * 根据构造函数获得当前手机的手机高度
     */
    public static int getDensity_Height(Context ctx) {
        int height;
        DisplayMetrics dm = ctx.getApplicationContext().getResources().getDisplayMetrics();
        boolean hasNav = DisplayUtil.checkDeviceHasNavigationBar(ctx);
        if (hasNav) {
            height = dm.heightPixels - DisplayUtil.getNavigationBarHeight(ctx);
        } else {
            height = dm.heightPixels;
        }

        return height;

    }


}
