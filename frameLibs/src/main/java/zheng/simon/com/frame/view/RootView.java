package zheng.simon.com.frame.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import zheng.simon.com.frame.R;

/**
 * Created by Simon
 * 日期：on 2016/4/25.
 */
public class RootView extends FrameLayout {

    private LayoutInflater mInflater;
//    protected TopBarView topView;
    private FrameLayout rl_content;  //内容布局

    public RootView(Context context) {
        this(context, null);
    }


    public RootView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RootView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Init(context);
    }

    /**
     * @param ctx
     */
    private void Init(Context ctx) {
        mInflater = LayoutInflater.from(ctx);
        mInflater.inflate(R.layout.activity_base, this, true);
//        topView = (TopBarView) findViewById(R.id.topView);
        rl_content = findViewById(R.id.rl_content);

    }


//    public TopBarView getHeadView() {
//        return topView;
//    }


    /**
     * @param view
     */
    public void removeView(View view) {
        if (view == null) return;

        rl_content.removeView(view);
    }

    /**
     * 设置主要显示视图
     *
     * @param child
     */
    public View setContentView(View child) {
        if (child != null) {
            rl_content.addView(child);
        }

        return child;
    }


    /**
     * 设置主要显示视图
     *
     * @param resoucreId
     */
    public View setContentView(int resoucreId) {
        View child = null;
        if (resoucreId != 0) {
            child = mInflater.inflate(resoucreId, null);
            setContentView(child);
        }

        return child;
    }


    /**
     * 设置内容布局被headview盖住
     */
    public void setContentViewLayout(boolean isCover) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        if (!isCover) {
            params.setMargins(0, getResources().getDimensionPixelOffset(R.dimen.top_view_height), 0, 0);
        }
        rl_content.setLayoutParams(params);
    }


    /**
     * @param resoucreId
     * @return
     */
    public View getContentView(int resoucreId) {
        return mInflater.inflate(resoucreId, null);
    }


    /**
     * @param view
     */
    private void settext(TextView view, Object text) {
        if (text instanceof Integer) {
            view.setText((int) text);
        } else {
            view.setText((CharSequence) text);
        }
    }


}
