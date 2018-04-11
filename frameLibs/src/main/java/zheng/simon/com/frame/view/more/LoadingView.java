package zheng.simon.com.frame.view.more;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import zheng.simon.com.frame.R;


/**
 * Created by SoLi on 2015/7/3.
 */
public class LoadingView extends FrameLayout {

    private View noDataView,loadMoreView;

    private Context mCtx;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Init(context);
    }

    /**
     *
     * @param context
     */
    private void Init(Context context )
    {
        mCtx = context;
        LayoutInflater.from(context).inflate(R.layout.default_loading_view, this, true);

        noDataView = findViewById(R.id.noDataView);
        loadMoreView = findViewById(R.id.loadMoreView);
    }

    /**
     * 添加自定义视图，目前用不着
     * @param loadingView
     */
    public void setContentView(View loadingView)
    {
//        if (getChildAt(0) instanceof FrameLayout)
//        {
//            ((FrameLayout) getChildAt(0)).removeAllViews();
//            ((FrameLayout) getChildAt(0)).addView(loadingView);
//        }

        removeAllViews();
        addView(loadingView);
    }

    /**
     *添加自定义视图，目前用不着
     * @param layoutId
     */
    public void setContentView(int layoutId)
    {
        setContentView(LayoutInflater.from(mCtx).inflate(layoutId,null));
    }

    /**
     * 显示加载更多视图，数据加载中
     */
    public void showLoadMoreView()
    {
        noDataView.setVisibility(GONE);
        loadMoreView.setVisibility(VISIBLE);
    }

    /**
     *
     */
    public void showNoMoreDataView()
    {
        noDataView.setVisibility(VISIBLE);
        loadMoreView.setVisibility(GONE);
    }
}
