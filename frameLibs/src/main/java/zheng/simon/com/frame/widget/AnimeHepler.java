package zheng.simon.com.frame.widget;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;


import zheng.simon.com.frame.R;
import zheng.simon.com.frame.view.RecycleEmptyView;

public class AnimeHepler {

    /**
     * @param view
     */
    public static void removeHalfRefreshView(View view) {
        ViewGroup parentView = (ViewGroup) view.getParent();
        if (parentView != null) {
            removeItem(parentView, view, R.id.id_half_refresh_view);
        }
    }


    /**
     * 给recyclerView加一个数据为空时EmptyView recyclerView外层最好用FrameLayout单独包着
     *
     * @param context
     * @param rv
     * @param resourceId
     * @param message
     * @param listener
     */
    public static void setNoDataEmptyView(Context context, RecycleEmptyView rv, int resourceId, String message, int paddingTop, OnClickListener listener) {
        ViewGroup parentView = (ViewGroup) rv.getParent();

        removeItem(parentView, rv, R.id.id_recycler_empty);

        View inflate = getEmptyView(context, resourceId, message, listener);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, paddingTop > 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : ViewGroup.LayoutParams.MATCH_PARENT);
        if (paddingTop > 0)
            inflate.setPadding(0, paddingTop, 0, 0);
        parentView.addView(inflate, params);
        rv.setEmptyView(inflate);
        rv.setTag(R.id.id_recycler_empty, inflate);
    }



    /**
     * @param context
     * @param rv
     * @param rid
     * @param message
     * @param listener
     */
    public static void setNoDataEmptyView(Context context, RecycleEmptyView rv, Integer rid, String message, OnClickListener listener) {
        setNoDataEmptyView(context, rv, rid, message, 0, listener);
    }


    /**
     * 给listView加一个数据为空时EmptyView listview外层最好用FrameLayout单独包着
     *
     * @param context
     * @param lv
     * @param resourceId
     * @param message
     * @param listener
     */
    public static void setNoDataEmptyView(Context context, AbsListView lv, int resourceId, String message, OnClickListener listener) {
        ViewGroup parentView = (ViewGroup) lv.getParent();

        removeItem(parentView, lv, R.id.id_listView_empty);

        View inflate = getEmptyView(context, resourceId, message, listener);
        parentView.addView(inflate);
        lv.setEmptyView(inflate);
        lv.setTag(R.id.id_listView_empty, inflate);
    }

    /**
     * @param context
     * @param lv
     */
    public static void removeAllItem(Context context, AbsListView lv) {
        ViewGroup parentView = (ViewGroup) lv.getParent();
        removeItem(parentView, lv, R.id.id_listView_empty);
        lv.setEmptyView(null);
    }

    /**
     * 删除上一个EmptyView
     *
     * @param parentView
     * @param lv
     * @param index
     */
    private static void removeItem(ViewGroup parentView, View lv, int index) {

        Object tag = lv.getTag(index);
        if (tag != null && tag instanceof View) {
            parentView.removeView((View) tag);
            lv.setTag(index, null);
        }
    }

    /**
     * 得到一个数据为空时的EmptyView
     *
     * @param context
     * @param resourceId
     * @param str
     * @param click
     * @return
     */
    public static View getEmptyView(Context context, int resourceId, String str, OnClickListener click) {

        View emptyView = View.inflate(context, R.layout.layout_empty, null);
        TextView txt_emtpy = emptyView.findViewById(R.id.txt_emtpy);
        txt_emtpy.setText(str);

        if (resourceId != 0) {
            ImageView image_empty = emptyView.findViewById(R.id.image_empty);
            image_empty.setImageResource(resourceId);
        }

        if (click != null) {
            emptyView.setOnClickListener(click);
        }

        return emptyView;

    }


    /**
     * 给view设置动画效果，并设置动画结束后回调
     *
     * @param context
     * @param v
     * @param anim
     * @param ae
     */
    public static void startAnimation(Context context, View v, Animation anim, final OnAnimEnd ae) {
        startAnimation(context, v, 0, anim, ae);
    }

    /**
     * 给view设置动画效果，并设置动画结束后回调
     *
     * @param context
     * @param v
     * @param animationId
     * @param ae
     */
    private static void startAnimation(Context context, View v, int animationId, Animation anim, final OnAnimEnd ae) {
        if (anim == null) {
            anim = AnimationUtils.loadAnimation(context, animationId);
        }

        if (v == null)
            return;

        v.startAnimation(anim);

        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                if (ae != null) {
                    ae.end();
                }
            }
        });

    }

    public interface OnAnimEnd {
        void end();
    }

    /**
     * 点击后先缩小再放大
     *
     * @return
     */
    public static AnimationSet animSmall2Big() {

        AlphaAnimation alpha = new AlphaAnimation(0.5f, 1);
        alpha.setDuration(200);
        alpha.setInterpolator(new AccelerateInterpolator());

        ScaleAnimation scale1 = new ScaleAnimation(0.6f, 1.2f, 0.6f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scale1.setDuration(200);
        scale1.setInterpolator(new AccelerateInterpolator());
        ScaleAnimation scale2 = new ScaleAnimation(1.2f, 0.8f, 1.2f, 0.8f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scale2.setDuration(300);
        scale2.setInterpolator(new DecelerateInterpolator());
        ScaleAnimation scale3 = new ScaleAnimation(0.8f, 1f, 0.8f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scale3.setDuration(200);
        scale3.setInterpolator(new DecelerateInterpolator());

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(alpha);
        set.addAnimation(scale1);
        set.addAnimation(scale2);
        set.addAnimation(scale3);
        return set;
    }

    /***
     *
     * @param v
     */
    public static void setOnTouch(View v) {
//        v.setOnTouchListener((view, event) -> setTouch(view, event));
    }

    /**
     * @param view
     * @param event
     * @return
     */
    private static boolean setTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                view.setAlpha(0.6f);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                view.setAlpha(1.0f);
                break;
            default:
                break;
        }
        return false;
    }

}
