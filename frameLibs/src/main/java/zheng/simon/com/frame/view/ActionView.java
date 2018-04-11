package zheng.simon.com.frame.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import zheng.simon.com.frame.R;
import zheng.simon.com.frame.listener.DataErrorCallBack;

public class ActionView extends FrameLayout {

    private Context ctx;

    private AnimationDrawable drawable;

    public ActionView(Context context) {
        this(context, null);
    }

    public ActionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ctx = context;
    }

    /**
     * 添加有相应处理事件视图
     *
     * @param listener    操作回调
     * @param contentView 显示的视图
     * @param id          需要操作的资源id
     */
    public void addActionView(final DataErrorCallBack listener, View contentView, int... id) {

        addView(contentView);

        for (int index : id) {
            if (listener != null) {
                findViewById(index).setOnClickListener(view -> listener.onRetry());
            } else {
                findViewById(index).setVisibility(GONE);
            }
        }
    }

    /**
     * @param layout
     */
    public void addProgressView(int layout) {
        LayoutInflater.from(ctx).inflate(layout, this, true);
    }


    public void addLoadingView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_loading, this, true);
    }



    /**
     * @param view
     */
    public void addProgressView(View view) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        addView(view);
    }


}
