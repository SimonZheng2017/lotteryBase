package photoselector.ui;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.taihe.photoselect.R;

/**
 * Created by SoLi on 2015/12/25.
 */
public class SlidedownView {

    private boolean isFromDownToUp = false;

    private Context ctx;

    private ViewGroup viewParent;
    private FrameLayout rootView;
    private View childView;
    private int duration = 300;

    private boolean isShowing = false;

    private onLifecallBack callback;
    private ValueAnimator.AnimatorUpdateListener update;
    private ObjectAnimator.AnimatorListener listener;

    public SlidedownView(Context mctx, ViewGroup attacth) {
        ctx = mctx;
        viewParent = attacth;
        InitrootView();
    }

    /**
     * @param view
     */
    public void setContentView(View view) {
        childView = view;
    }

    /**
     * 从底部往上滑动
     */
    public void setScrollFromDownToUp() {
        isFromDownToUp = true;
    }

    /**
     *
     */
    private void InitrootView() {
        rootView = new FrameLayout(ctx);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.setOnClickListener(v -> {
            if (isShowing)
                HideView();
        });

        update = valueAnimator -> {
            double value = childView.getMeasuredHeight() - Math.abs(Double.valueOf(valueAnimator.getAnimatedValue("translationY").toString()));
            double apha = value / childView.getMeasuredHeight() * 1.0d;
            setRootViewBackgroundColor(apha > 0.6d ? 0.6 : apha);

            childView.invalidate();
            if (!isShowing() && apha == 0.0) {
                viewParent.removeView(rootView);
            }
        };

        listener = new ObjectAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (callback != null)
                    callback.start(isShowing() ? true : false);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (callback != null)
                    callback.isDisplay(isShowing() ? true : false);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        };
    }

    /**
     * @param
     */
    public void showView() {

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        if (isFromDownToUp) {
            params.gravity = Gravity.BOTTOM;
        }

        rootView.removeView(childView);
        rootView.addView(childView, params);
        viewParent.addView(rootView);

//        childView.measure(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        childView.requestLayout();// This is needed so the animation can use the measured with/height
        ViewTreeObserver observer = childView.getViewTreeObserver();
        if (observer != null) {
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        childView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        childView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                    isShowing = true;
                    ObjectAnimator animator = ObjectAnimator.ofFloat(childView, "translationY", isFromDownToUp ? childView.getMeasuredHeight() : -childView.getMeasuredHeight(), 0);
                    animator.setDuration(duration);
                    animator.addUpdateListener(update);
                    animator.addListener(listener);
                    animator.start();
                }
            });
        }

    }

    /**
     * @param apha
     */
    private void setRootViewBackgroundColor(double apha) {
        rootView.setBackgroundColor(getColorWithAlpha(apha, ctx.getResources().getColor(R.color.color_black)));
    }

    /**
     * @param alpha
     * @param baseColor
     * @return
     */
    private int getColorWithAlpha(double alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }

    /**
     * @return
     */
    public boolean isShowing() {
        return isShowing;
    }

    /**
     *
     */
    public void HideView() {
        isShowing = false;
        ObjectAnimator animator = ObjectAnimator.ofFloat(childView, "translationY", 0, isFromDownToUp ? childView.getMeasuredHeight() : -childView.getMeasuredHeight());
        animator.setDuration(duration);
        animator.addUpdateListener(update);
        animator.addListener(listener);
        animator.start();
    }

    /**
     *
     */
    public void ToggleView() {
        try {
            if (!isShowing()) {
                showView();
            } else {
                HideView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setCallback(onLifecallBack callback) {
        this.callback = callback;
    }

    /**
     *
     */
    public interface onLifecallBack {
        void isDisplay(boolean isShow);

        void start(boolean isShow);
    }
}
