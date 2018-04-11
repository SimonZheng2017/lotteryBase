package zheng.simon.com.frame.view.more;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import zheng.simon.com.frame.R;


/**
 * Created by SoLi on 2015/11/4.
 */
public class SwipeRefreshLoadMore extends SwipeRefreshLayoutExtend {
    private PagingListView mListView;
    private View emptyView;
    private boolean isAddEmptyView = false;
    private int emptyViewLayoutId;
    private boolean isAddHeaderView = false;//是否添加了HeaderView

    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private onRefrshListener mRefreshListener;

    public SwipeRefreshLoadMore(Context context) {
        this(context, null);
    }

    public SwipeRefreshLoadMore(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(attrs);
    }

    /**
     *
     */
    private void Init(AttributeSet attrs) {
        setEnabled(false);

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SwipeRefreshLoadMore);
        emptyViewLayoutId = a.getResourceId(R.styleable.SwipeRefreshLoadMore_refreshEmptyLayout, 0);
        a.recycle();
    }

    /**
     *
     */
    public void addEmptyView() {
        if (emptyViewLayoutId != 0 && emptyView == null) {
            emptyView = View.inflate(getContext(), emptyViewLayoutId, null);
        }

        if (emptyView != null) {
            removeView(emptyView);
            addView(emptyView);
            isAddEmptyView = true;
            setVisibility(VISIBLE);
        }
    }

    /**
     * @param view
     */
    public void setEmptyView(View view) {
        emptyView = view;
    }


    /**
     * @param layoutId
     */
    public void setEmptyView(int layoutId) {
        emptyViewLayoutId = layoutId;
    }

    /**
     *
     */
    public void removeEmptyView() {
        if (isAddEmptyView && emptyView != null) {
            removeView(emptyView);
            isAddEmptyView = false;
        }
    }

    /**
     * 获取ListView对象
     */
    private void getListView() {
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView instanceof PagingListView) {
                mListView = (PagingListView) childView;
                break;
            }
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (emptyView != null && isAddEmptyView) {
            emptyView.measure(MeasureSpec.makeMeasureSpec(
                    getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (emptyView != null && isAddEmptyView) {
            final int childLeft = getPaddingLeft();
            final int childTop = getPaddingTop();
            final int childWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            final int childHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
            emptyView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getListView();

        if (mListView == null || !(mListView instanceof PagingListView))
            throw new IllegalStateException("cant get listview or Listview should be extend PagingListView");
    }

    /**
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        mListView.setPageSize(pageSize);
    }

    /**
     * @param view
     */
    public void addHeaderView(View view) {
        mListView.addHeaderView(view);
        isAddHeaderView = true;
    }

    /**
     * 是否添加了header
     */
    public boolean isAddHeaderView() {
        return isAddHeaderView;
    }

    /**
     * @return
     */
    public int getHeaderViewsCount() {
        return mListView.getHeaderViewsCount();
    }

    /**
     * @param view
     */
    public void removeHeadView(View view) {
        mListView.removeHeaderView(view);
        isAddHeaderView = false;
    }

    /**
     * @param view
     */
    public void addHeadView(View view) {
        mListView.addHeaderView(view);
    }

    /**
     *
     */
    public void reset() {
        if (mListView != null)
            mListView.reset();
    }


    /**
     * @param adapter
     */
    public void setAdapter(BaseAdapter adapter) {
        mListView.setAdapter(adapter);
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mListView.getAdapter() != null) {
                    if (mListView.getAdapter().getCount() == 0)
                        addEmptyView();
                    else if (mListView.getAdapter().getCount() < mListView.getPageSize()) {
                        mListView.showNoMoreView();
                    }
                } else {
                    removeEmptyView();
                }
            }
        });
    }

    /**
     * @param listener
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mListView.setOnItemClickListener(listener);
    }


    /**
     * 加载完成
     */
    public void onRefreshComplete() {
        setRefreshing(false);
        mListView.onloadMoreComplete();
    }

    /**
     * 需要下拉刷新
     *
     * @param needPullDownRefresh
     */
    public void setNeedPullDownRefresh(boolean needPullDownRefresh) {
        setEnabled(needPullDownRefresh);
        if (needPullDownRefresh) {
            setColorSchemeResources(R.color.actionbar_color, R.color.button_color, R.color.colorPrimary, R.color.colorPrimaryDark);
            setOnRefreshListener(() -> {
                reset();
                if (mRefreshListener != null)
                    mRefreshListener.onPullDownRefresh();
            });
        } else {
            setOnRefreshListener(null);
        }
    }

    /**
     * 需要自动加载更多
     *
     * @param needPullUpRefresh
     */
    public void setNeedPullUpRefresh(boolean needPullUpRefresh) {
        mListView.setNeedLoadingMore(needPullUpRefresh);
        if (needPullUpRefresh) {
            mListView.setOnloadMorelistener(() -> {
                if (mRefreshListener != null)
                    mRefreshListener.onPullupRefresh();
            });
        } else {
            mListView.setOnloadMorelistener(null);
        }
    }

    /**
     * @param mRefreshListener
     */
    public void setRefreshListener(onRefrshListener mRefreshListener) {
        this.mRefreshListener = mRefreshListener;

    }

    /**
     * @param position
     */
    public void setSelection(int position) {
        mListView.setSelection(position);
        if (position == 0)
            reset();
    }

    /**
     * 加载更多的监听器
     *
     * @author mrsimple
     */
    public interface onRefrshListener {
        /**
         * 上拉自动加载
         */
        void onPullupRefresh();

        /**
         * 下拉刷新
         */
        void onPullDownRefresh();
    }
}
