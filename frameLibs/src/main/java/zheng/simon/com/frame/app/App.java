package zheng.simon.com.frame.app;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;

import photoselector.util.FrescoUtil;
import zheng.simon.com.frame.BuildConfig;

/**
 * Created by zhengyouquan on 06/02/2018.
 */

public class App extends MultiDexApplication{

    private static App app;


    public static App getInstance() {
        return app;
    }

    /**
     * 获取Application的引用
     *
     * @return
     */
    public static Context getAppContext() {
        return app.getApplicationContext();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        app = this;

        FrescoUtil.Init(this);

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        } else {

        }

    }
}
