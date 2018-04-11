package zheng.simon.com.frame.view.more;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.AbsListView;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import zheng.simon.com.frame.R;
import zheng.simon.com.frame.widget.DisplayUtil;

/**
 *
 */
public class QuickReturnListViewOnScrollListener implements AbsListView.OnScrollListener {

    private boolean isEnable = false;//超过一屏幕后才相应
    private final View mFooter;
    private final int mMinFooterTranslation;
    private final boolean mIsSnappable; // Can Quick Return view snap into place?
    private final boolean isJustShowAndHid;
    //当控制同一视图显示的,默认不是控制同一视图
    private int destTag = 0, currentTag = 0;

    private Dictionary<Integer, Integer> sListViewItemHeights = new Hashtable<>();
    private boolean isAnimationing;
    private int mPrevScrollY = 0;
    private int mFooterDiffTotal = 0;
    private List<AbsListView.OnScrollListener> mExtraOnScrollListenerList = new ArrayList<>();

    private QuickReturnListViewOnScrollListener(Builder builder) {
        mFooter = builder.mFooter;
        mMinFooterTranslation = builder.mMinFooterTranslation;
        mIsSnappable = builder.mIsSnappable;
        isJustShowAndHid = builder.isJustShowAndHid;
        destTag = builder.destTag;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        for (AbsListView.OnScrollListener listener : mExtraOnScrollListenerList) {
            listener.onScrollStateChanged(view, scrollState);
        }
        if (currentTag != destTag)
            return;

        if (isJustShowAndHid)
            return;

        if (isEnable && mIsSnappable && scrollState == SCROLL_STATE_IDLE) {
            int midFooter = mMinFooterTranslation / 2;
            if (-mFooterDiffTotal > 0 && -mFooterDiffTotal < midFooter) { // slide up
                ObjectAnimator anim = ObjectAnimator.ofFloat(mFooter, "translationY", mFooter.getTranslationY(), 0);
                anim.setDuration(100);
                anim.start();
                mFooterDiffTotal = 0;
            } else if (-mFooterDiffTotal < mMinFooterTranslation && -mFooterDiffTotal >= midFooter) { // slide down
                ObjectAnimator anim = ObjectAnimator.ofFloat(mFooter, "translationY", mFooter.getTranslationY(), mMinFooterTranslation);
                anim.setDuration(100);
                anim.start();
                mFooterDiffTotal = -mMinFooterTranslation;
            }
        }
    }

    /**
     * 原理就是  消失item项的高度 + 第一个视图的getTop,注意这里的第一个视图并不是所见视图的第一个，而是滑出屏幕视图的第一个
     *
     * @param lv
     * @return
     */
    public int getScrollY(AbsListView lv) {
        //AbsListView.getChildAt(0) 不是一直都是数据上的第一个，这个是重用
        View c = lv.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = lv.getFirstVisiblePosition();
        int scrollY = -(c.getTop());

        sListViewItemHeights.put(lv.getFirstVisiblePosition(), c.getHeight());

        if (scrollY < 0)
            scrollY = 0;

        for (int i = 0; i < firstVisiblePosition; ++i) {
            if (sListViewItemHeights.get(i) != null) // (this is a sanity check)
            {
                scrollY += sListViewItemHeights.get(i); //add all heights of the views that are gone
            }
        }
        return scrollY;
    }

    /**
     * @param isShow
     */
    private void animationShowHideView(boolean isShow) {
        if (mFooter != null) {

            try {
                int canOperation = (Integer) mFooter.getTag(R.id.id_data);
                if (canOperation == 0)
                    return;
            } catch (Exception e) {
            }

            if (isAnimationing)
                return;

            if (isShow && mFooter.getVisibility() != View.VISIBLE) {
                animationView(true, mFooter);
            } else if (!isShow && mFooter.getVisibility() == View.VISIBLE) {
                animationView(false, mFooter);
            }
        }
    }

    /**
     * @param isShow
     * @param view
     */
    private synchronized void animationView(final boolean isShow, final View view) {
        float fromAlpha = isShow ? 0.0f : 1.0f;
        float toAlpha = isShow ? 1.0f : 0.0f;

        if (isShow)
            view.setVisibility(View.VISIBLE);

        isAnimationing = true;
        ObjectAnimator anim = ObjectAnimator.ofFloat(mFooter, "alpha", fromAlpha, toAlpha);
        anim.setDuration(250);
        anim.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimationing = false;
                if (!isShow)
                    view.setVisibility(View.GONE);
            }
        });
        anim.start();
    }

    @Override
    public void onScroll(AbsListView listview, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        for (AbsListView.OnScrollListener listener : mExtraOnScrollListenerList) {
            listener.onScroll(listview, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        if (currentTag != destTag)
            return;

        int scrollY = getScrollY(listview);

        isEnable = scrollY > DisplayUtil.getDensity_Height(listview.getContext());
        animationShowHideView(isEnable);

        if (isJustShowAndHid)
            return;

        int diff = mPrevScrollY - scrollY;

        if (diff != 0) {
            if (diff < 0) { // scrolling up
                mFooterDiffTotal = Math.max(mFooterDiffTotal + diff, -mMinFooterTranslation);
            } else { // scrolling down
                mFooterDiffTotal = Math.min(Math.max(mFooterDiffTotal + diff, -mMinFooterTranslation), 0);
            }

            mFooter.setTranslationY(-mFooterDiffTotal);
        }

        mPrevScrollY = scrollY;
    }

    public int getCurrentTag() {
        return currentTag;
    }

    public void setCurrentTag(int currentTag) {
        this.currentTag = currentTag;
    }

    /**
     * @param listener
     */
    public void registerExtraOnScrollListener(AbsListView.OnScrollListener listener) {
        mExtraOnScrollListenerList.add(listener);
    }

    public static class Builder {
        private View mFooter = null;
        private int mMinFooterTranslation = 0;
        private boolean mIsSnappable = true;
        private boolean isJustShowAndHid = false;
        private int destTag = 0;

        public Builder footer(View footer) {
            mFooter = footer;
            return this;
        }

        public Builder minFooterTranslation(int minFooterTranslation) {
            mMinFooterTranslation = minFooterTranslation;
            return this;
        }

        /**
         * 滑动一半自动滑动，是否开启  默认开启
         *
         * @param isSnappable
         * @return
         */
        public Builder isSnappable(boolean isSnappable) {
            mIsSnappable = isSnappable;
            return this;
        }

        /**
         * 当满足一定条件的时候，是否只让视图显示或者隐藏  默认关闭 这里主要是圈子首页使用
         *
         * @param misJustShowAndHid
         * @return
         */
        public Builder isJustShowAndHide(boolean misJustShowAndHid) {
            isJustShowAndHid = misJustShowAndHid;
            return this;
        }

        /**
         * 当控制同一视图的时候，让谁来掌控，默认
         *
         * @param tag
         * @return
         */
        public Builder setDestTag(int tag) {
            destTag = tag;
            return this;
        }

        public QuickReturnListViewOnScrollListener build() {
            return new QuickReturnListViewOnScrollListener(this);
        }
    }
}
