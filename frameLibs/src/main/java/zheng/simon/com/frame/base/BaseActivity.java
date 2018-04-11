package zheng.simon.com.frame.base;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import zheng.simon.com.frame.R;
import zheng.simon.com.frame.enummodel.LoadingType;
import zheng.simon.com.frame.http.Result;
import zheng.simon.com.frame.http.ResultCode;
import zheng.simon.com.frame.listener.DataErrorCallBack;
import zheng.simon.com.frame.utils.AppManager;
import zheng.simon.com.frame.view.ActionView;
import zheng.simon.com.frame.view.RootView;
import zheng.simon.com.frame.view.dialog.ProgressDialog;
import zheng.simon.com.frame.widget.RepeatedClickHandler;


/**
 * Created by zhengyouquan on 16/12/1.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, BaseInterface {

    protected Activity act;
    /**
     * 内容容器
     */
    public RootView screentView;
    //    protected TopBarView topView;
    protected View contentView;

    protected ActionView errorview;
    /**
     * 数据为空时显示的占位视图
     */
    protected ActionView emptyView;
    protected ActionView loadingView;
    /**
     * 默认加载类型
     */
    protected LoadingType defaultLoadingType = LoadingType.TypeInside;
    public LoadingType loadingType = defaultLoadingType;
    /**
     * 解决连点问题
     */
    private RepeatedClickHandler repeatedClickHandler;

    /**
     * 加载动画
     */
    protected ProgressDialog loadDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screentView = setView();
        if (screentView == null || contentView == null) {
            throw new IllegalStateException("content view and contentView must not be empty");
        }

        setContentView(screentView);
        AppManager.getInstance().addActivity(this);
        repeatedClickHandler = new RepeatedClickHandler();
        act = this;

        initView(savedInstanceState);
        initListener();
        initData();
    }

    /**
     * 设置显示的视图
     */
    private RootView setView() {
        RootView view = new RootView(this);
        contentView = view.setContentView(View.inflate(this, setContentViewRes(), null));
//        topView = view.getHeadView();
        return view;
    }


    /**
     * 设置内容布局
     *
     * @return
     */
    protected abstract int setContentViewRes();

    /**
     * 初始化界面
     */
    protected abstract void initView(@Nullable Bundle savedInstanceState);

    /**
     * 初始化事件监听
     */
    protected abstract void initListener();

    /**
     * 初始化数据
     */
    protected abstract void initData();


    @Override
    public void onClick(View view) {
        if (repeatedClickHandler != null) {
            repeatedClickHandler.handleRepeatedClick(view);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        dismissProgress();
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (loadDialog != null && loadDialog.isShowing()) {
            loadDialog.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadDialog = null;
        AppManager.getInstance().remove(this);
    }


    /**
     * 添加fragment
     *
     * @param id
     * @param fragment
     */
    protected void addFragment(int id, Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(id, fragment, null);
        ft.commit();
    }


    /**
     * 设置第一次加载数据时显示的动画
     */
    private void setLoadingView() {
        screentView.removeView(errorview);
        screentView.removeView(loadingView);
        screentView.removeView(emptyView);
        errorview = loadingView = emptyView = null;

        contentView.setVisibility(View.INVISIBLE);

        loadingView = new ActionView(this);
        loadingView.addLoadingView(this);

        screentView.setContentView(loadingView);
    }


    public void dismissProgress() {
        dismissProgressLoadDialog();

        dissShowProgressInside();
        //恢复到默认值
        if (loadingType != defaultLoadingType) {
            loadingType = defaultLoadingType;
        }
    }


    /**
     *
     */
    private void dissShowProgressInside() {
        viewAnimation(contentView, errorview);
        viewAnimation(contentView, loadingView);
        viewAnimation(contentView, emptyView);
    }


    /**
     * 设置空数据显示的视图
     *
     * @param imageRes
     * @param tipTxt
     */
    protected void setEmptyView(int imageRes, String tipTxt) {
        screentView.removeView(emptyView);
        emptyView = null;

        View view = View.inflate(this, R.layout.layout_empty, null);
        emptyView = new ActionView(this);
        emptyView.addProgressView(view);

        if (imageRes != 0) {
            ((ImageView) view.findViewById(R.id.image_empty)).setImageResource(imageRes);
        }

        if (!TextUtils.isEmpty(tipTxt)) {
            ((TextView) view.findViewById(R.id.txt_emtpy)).setText(tipTxt);
        }


        contentView.setVisibility(View.INVISIBLE);
        screentView.setContentView(emptyView);

    }


    /**
     * 根据出错类型自动显示相应的提示界面
     *
     * @param code
     * @param listener
     */
    protected void errorHappen(ResultCode code, DataErrorCallBack listener) {
            errorHappen(listener, View.inflate(this, R.layout.layout_error, null), R.id.btnTry, R.id.btnTry1);
    }

    /**
     * 只有第一页出现才显示这个
     *
     * @param pageNo
     * @param result
     * @param listener
     */
    protected void errorHappen(int pageNo, Result result, DataErrorCallBack listener) {

        if (pageNo == 1)
            errorHappen(result.getCode(), listener);
        else
            MyToast(result.getObj().toString());

    }

    /**
     * 错误发生了
     *
     * @param listener
     * @param view
     * @param id
     */
    protected void errorHappen(final DataErrorCallBack listener, View view, int... id) {
        screentView.removeView(errorview);
        errorview = null;

        contentView.setVisibility(View.INVISIBLE);

        errorview = new ActionView(this);
        errorview.addActionView(listener, view, id);

        if (loadingView == null)
            screentView.setContentView(errorview);
        else
            viewAnimation(errorview, loadingView);
    }

    /**
     * 是否显示对话框
     *
     * @param show
     */
    public void ifShowProgress(boolean show) {
        if (show)
            showProgress();
    }

    /**
     * @param diss
     */
    public void ifDissmissProgress(boolean diss) {
        if (diss)
            dismissProgress();
    }


    /**
     * 显示加载进度条
     */
    public void showLoadDialog(boolean canCancle) {
        try {
            if (!isFinishing()) {
                if (loadDialog == null) {
                    loadDialog = new ProgressDialog(this);
                    loadDialog.setCanceledOnTouchOutside(canCancle);
                    loadDialog.setCancelable(canCancle);
                }
                if (!loadDialog.isShowing()) {
                    loadDialog.show();
                }
            }
        } catch (Exception e) {
        }
    }


    /**
     * 停止显示加载进度条
     */
    public void dismissProgressLoadDialog() {
        try {
            if (!isFinishing()) {
                if (loadDialog != null && loadDialog.isShowing()) {
                    loadDialog.dismiss();
                }
            }
        } catch (Exception e) {
        }
    }



    public void showProgress() {
        showProgress(true);
    }


    public void showProgress(boolean canCancle) {
        if (loadingType == LoadingType.TypeDialog) {
            showLoadDialog(canCancle);
        } else if (loadingType == LoadingType.TypeInside) {
            setLoadingView();
        }
    }


    public void showProgress(LoadingType type) {
        loadingType = type;
        showProgress();
    }

    /**
     * 是否隐藏顶部topview，显示topview时内容没有被覆盖
     *
     * @param isShow
     */
    protected void showTopView(boolean isShow) {
        if (isShow) {
//            topView.setVisibility(View.VISIBLE);
            screentView.setContentViewLayout(false);
        } else {
//            topView.setVisibility(View.GONE);
            screentView.setContentViewLayout(true);
        }

    }


    /**
     * 是否隐藏顶部topview
     *
     * @param isShow
     * @param isCover 是否在覆盖在内容布局上面
     */
    protected void showTopView(boolean isShow, boolean isCover) {
        if (isShow) {
//            topView.setVisibility(View.VISIBLE);
        } else {
//            topView.setVisibility(View.GONE);
        }

        if (isCover)
            screentView.setContentViewLayout(isCover);
    }


    /**
     * @param showView
     * @param dissMissView
     */
    private void viewAnimation(final View showView, final View dissMissView) {
        if (dissMissView == null) return;

        if (showView != null && showView.getVisibility() == View.VISIBLE &&
                dissMissView.getVisibility() != View.VISIBLE) {
            screentView.removeView(dissMissView);
            return;
        }

        if (showView == contentView && showView.getVisibility() != View.VISIBLE)
            showView.setVisibility(View.VISIBLE);

        if (contentView != null && showView != contentView)
            screentView.setContentView(showView);

        screentView.removeView(dissMissView);
        screentView.setContentView(dissMissView);

        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(ObjectAnimator.ofFloat(showView, "alpha", 0.0f, 1.0f), ObjectAnimator.ofFloat(dissMissView, "alpha", 1, 0.0f));
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                screentView.removeView(dissMissView);
                if (dissMissView == errorview) {
                    errorview = null;
                }
                if (dissMissView == loadingView) {
                    loadingView = null;
                }
                if (dissMissView == emptyView) {
                    emptyView = null;
                }
            }
        });
        set.setDuration(300).start();
    }


    /**
     * 设置状态栏是否透明
     */
    public boolean initStatusMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

//            topView.setStatusView();
            return true;
        } else {
            return false;
        }
    }


    /**
     * 显示Toast
     *
     * @param text 文本内容
     */
    protected void MyToast(String text) {
        if (TextUtils.isEmpty(text))
            return;
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


    /**
     * 显示Toast
     *
     * @param resId string资源id
     */
    protected void MyToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }



}
