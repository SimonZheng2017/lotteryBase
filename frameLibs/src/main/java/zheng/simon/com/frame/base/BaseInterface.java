package zheng.simon.com.frame.base;


import zheng.simon.com.frame.enummodel.LoadingType;

/**
 * Created by soli on 17-8-4.
 */

public interface BaseInterface {

    void showProgress();

    void showProgress(LoadingType type);

    void dismissProgress();
}
