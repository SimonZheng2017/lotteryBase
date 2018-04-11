package zheng.simon.com.frame.view.more;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by SoLi on 2015/7/2.
 */
public class PagingListView extends ListViewExtend {

    //滑动到倒数第几个的时候就开始加载
    protected int ITEM_LEFT_TO_LOAD_MORE = 2;

    private LoadingView loadingView;

    private int pageSize = 6;
    private AbsListView.OnScrollListener onScrollListener;
    private onLoadingMore loadMoreListenter;

    private boolean isNeedLoadingMore = false;
    private boolean isNeedLoadingViewShow = true;

    private boolean isLoading = false;
    private int lastItemCount = 0;

    public PagingListView(Context context) {
        this(context, null);
    }

    public PagingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }


    /**
     * @param ctx
     */
    private void Init(Context ctx) {
        loadingView = new LoadingView(ctx);

        super.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (onScrollListener != null) {
                    onScrollListener.onScrollStateChanged(view, scrollState);
                }
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        Fresco.getImagePipeline().resume();
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        Fresco.getImagePipeline().pause();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (onScrollListener != null) {
                    onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
                totalItemCount -= getHeaderViewsCount() + getFooterViewsCount();
                if (isNeedLoadingMore && totalItemCount > 0) {
                    int lastVisibleItem = firstVisibleItem + visibleItemCount;
                    if (((totalItemCount - lastVisibleItem) <= ITEM_LEFT_TO_LOAD_MORE ||
                            (totalItemCount - lastVisibleItem) == 0 && totalItemCount > visibleItemCount) && !isLoading) {

                        //由于这个项目每页返回的数量不是固定的，所以取消这个判断
                        //getAdapter().getCount() >= pageSize &&
                        if (getAdapter().getCount() >= pageSize && lastItemCount != totalItemCount) {
                            lastItemCount = totalItemCount;
                            if (loadMoreListenter != null) {
                                OnloadMoreBegin();
                                loadMoreListenter.onLoadMoreItems();
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    /**
     * @return
     */
    public AbsListView.OnScrollListener getOnScrollListener() {
        return onScrollListener;
    }

    /**
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param max
     */
    public void setNumberBeforeMoreIsCalled(int max) {
        ITEM_LEFT_TO_LOAD_MORE = max;
    }

    /**
     * 需要加载更多显示
     */
    public void setNeedLoadingMore(boolean needLoadingMore) {
        isNeedLoadingMore = needLoadingMore;
    }

    /**
     * 是否需要显示加载更多的提示视图
     *
     * @param needLoadingViewShow
     */
    public void setNeedLoadingViewShow(boolean needLoadingViewShow) {
        isNeedLoadingViewShow = needLoadingViewShow;
    }

    /**
     * 数据加载更多显示
     */
    private void OnloadMoreBegin() {
        if (isNeedShowLoadingView()) {
            addFooterView(loadingView);
        }
        loadingView.showLoadMoreView();
        isLoading = true;
    }

    /**
     * @param adapter
     */
    public void setObversiveAdapter(ListAdapter adapter) {
        setAdapter(adapter);
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (getAdapter() != null) {
                    if (getAdapter().getCount() < getPageSize()) {
                        showNoMoreView();
                    }
                }
            }
        });
    }

    /**
     * @return
     */
    private boolean isNeedShowLoadingView() {
        return isNeedLoadingViewShow && loadingView != null && getFooterViewsCount() == 0;
    }

    /**
     *
     */
    public void onloadMoreComplete() {
        if (isNeedShowLoadingView())
            addFooterView(loadingView);
//        if (!isLoadComplete && isNeedLoadingViewShow && loadingView != null) {
//            removeFooterView(loadingView);
//        }else
//        {
        loadingView.showNoMoreDataView();
//        }
        isLoading = false;
    }

    /**
     *
     */
    public void showNoMoreView() {
        if (isNeedShowLoadingView())
            addFooterView(loadingView);
        loadingView.showNoMoreDataView();
    }

    /**
     * 设置加载的视图显示
     *
     * @param resources
     */
    public void setCustomLoadingView(Object resources) {
        if (resources instanceof View) {
            loadingView.setContentView((View) resources);
        } else if (resources instanceof Integer) {
            loadingView.setContentView((Integer) resources);
        }
    }

    /**
     *
     */
    public void reset() {
        lastItemCount = 0;
    }

    /**
     * @param listener
     */
    public void setOnloadMorelistener(onLoadingMore listener) {
        loadMoreListenter = listener;
    }

    /**
     *
     */
    public interface onLoadingMore {
        /**
         * 加载更多的实现
         */
        void onLoadMoreItems();
    }
}
