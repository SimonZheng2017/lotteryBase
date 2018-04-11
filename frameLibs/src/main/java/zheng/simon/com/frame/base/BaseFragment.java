package zheng.simon.com.frame.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import zheng.simon.com.frame.R;
import zheng.simon.com.frame.enummodel.LoadingType;
import zheng.simon.com.frame.http.Result;
import zheng.simon.com.frame.http.ResultCode;
import zheng.simon.com.frame.listener.DataErrorCallBack;
import zheng.simon.com.frame.view.ActionView;
import zheng.simon.com.frame.view.RootView;
import zheng.simon.com.frame.view.dialog.ProgressDialog;

/**
 * Created by zhengyouquan on 17/7/26.
 */

public abstract class BaseFragment extends Fragment implements BaseInterface {

    protected Activity act;
    protected RootView screentView;
    protected View contentView;
//    protected TopBarView topView;
    /**
     * 请求数据错误时显示的view
     */
    protected ActionView errorview;
    /**
     * 第一次加载时显示的动画
     */
    protected ActionView loadingView;
    /**
     * 数据为空时显示的占位视图
     */
    protected ActionView emptyView;
    protected LoadingType defaultLoadingType = LoadingType.TypeInside;
    public LoadingType loadingType = defaultLoadingType;

    /**
     * 加载进度条
     */
    private ProgressDialog loadDialog;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        act = activity;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (screentView == null) {
            screentView = setView();
        }

        if (screentView == null || contentView == null) {
            throw new IllegalStateException("root view and contentView must not be empty");
        }

        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) screentView.getParent();
        if (parent != null) {
            parent.removeView(screentView);
        }

        initView();
        initListener();
        initData();

        return screentView;
    }


    /**
     * 设置显示的视图
     */
    private RootView setView() {
        screentView = new RootView(act);
        contentView = screentView.setContentView(View.inflate(act, setContentViewRes(), null));
//        topView = screentView.getHeadView();
        showTopView(false);
        return screentView;
    }

    /**
     * 得到屏幕尺寸系数
     */
    public DisplayMetrics getDisplayMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * 找到子视图
     */
    public <T extends View> T findViewById(int id) {
        return contentView.findViewById(id);
    }

    /**
     * 设置内容布局id
     *
     * @return
     */
    protected abstract int setContentViewRes();

    /**
     * 初始化界面
     */
    protected abstract void initView();

    /**
     * 初始化事件监听
     */
    protected abstract void initListener();

    /**
     * 初始化数据
     */
    protected abstract void initData();


    /**
     * 添加fragment
     *
     * @param id
     * @param fragment
     */
    public void addFragment(int id, Fragment fragment) {
//		FragmentTransaction ft = getFragmentManager().beginTransaction();
        //如果用上面的方法添加，这个子fragment的onActivityResult的回调方法会在当前fragment中进行
        //使用下面这个方法添加的子fragment，及在fragment中嵌套子fragment，这个子fragment的onActivityResult方法不会回调,但是依附于的activity的onActivityResult会回调
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(id, fragment, null);
        ft.commit();
    }

    /**
     * 根据出错类型自动显示相应的提示界面
     *
     * @param code
     * @param listener
     */
    protected void errorHappen(ResultCode code, DataErrorCallBack listener) {
        errorHappen(listener, View.inflate(act, R.layout.layout_error, null), R.id.btnTry, R.id.btnTry1);
    }

    /**
     * 只有第一页出现才显示这个
     *
     * @param pageSize
     * @param result
     * @param listener
     */
    protected void errorHappen(int pageSize, Result result, DataErrorCallBack listener) {

        if (pageSize == 1)
            errorHappen(result.getCode(), listener);
        else {
            MyToast(result.getObj().toString());
        }
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

        errorview = new ActionView(act);
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
     * 得到根视图
     */
    public View getRootView() {
        return contentView;
    }

    @Override
    public void showProgress(LoadingType type) {
        loadingType = type;
        showProgress();
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


    /**
     * 设置第一次加载数据时显示的动画
     */
    private void setLoadingView() {
        screentView.removeView(errorview);
        screentView.removeView(loadingView);
        screentView.removeView(emptyView);
        errorview = loadingView = emptyView = null;

        contentView.setVisibility(View.INVISIBLE);

        loadingView = new ActionView(act);
        loadingView.addLoadingView(act);

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

        View view = View.inflate(act, R.layout.layout_empty, null);
        emptyView = new ActionView(act);
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
     * 显示加载进度条
     */
    private void showLoadDialog(boolean canCancle) {
        try {
            if (loadDialog == null) {
                loadDialog = new ProgressDialog(act);
                loadDialog.setCanceledOnTouchOutside(canCancle);
                loadDialog.setCancelable(canCancle);
            }
            if (!loadDialog.isShowing()) {
                loadDialog.show();
            }
        } catch (Exception e) {
        }
    }


    /**
     * 停止显示加载进度条
     */
    private void dismissProgressLoadDialog() {
        try {
            if (loadDialog != null && loadDialog.isShowing()) {
                loadDialog.dismiss();
            }
        } catch (Exception e) {
        }
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
//                    loadingView.stopAnim();
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
     * 是否隐藏顶部topview，显示topview时内容在topview下方
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
     * 显示Toast
     *
     * @param text 文本内容
     */
    protected void MyToast(String text) {
        Toast.makeText(act, text, Toast.LENGTH_SHORT).show();
    }


    /**
     * 显示Toast
     *
     * @param resId string资源id
     */
    protected void MyToast(int resId) {
        Toast.makeText(act, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * fragment resume
     */
    public void Resume() {
    }

    @Override
    public void onStop() {
        super.onStop();

        if (loadDialog != null && loadDialog.isShowing()) {
            loadDialog.dismiss();
        }
    }
}
