package zheng.simon.com.frame.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * Activity管理类
 */
public class AppManager {

    /**
     * Actvity存储栈
     */
    private static Stack<Activity> mActivityStack;

    /**
     * AppManager单例对象
     */
    private static AppManager mAppManager;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getInstance() {
        if (mAppManager == null) {
            mAppManager = new AppManager();
        }
        return mAppManager;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }
        mActivityStack.add(activity);
    }

    /**
     * 获取栈顶Activity（堆栈中最后一个压入的）
     */
    public Activity getTopActivity() {
        if (mActivityStack != null && mActivityStack.size() > 0)
            return mActivityStack.lastElement();
        return null;
    }

    /**
     * 结束栈顶Activity（堆栈中最后一个压入的）
     */
    public void killTopActivity() {
        Activity activity = getTopActivity();
        if (activity != null) {
            killActivity(activity);
        }
    }

    /**
     * 结束指定的Activity
     */
    public void killActivity(Activity activity) {
        if (activity != null) {
            mActivityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public synchronized void killActivity(Class<?>... calsses) {

        if (mActivityStack == null || mActivityStack.isEmpty())
            return;

        List<Activity> activities = new ArrayList<>();

        for (Class cls : calsses) {
            for (Activity activity : mActivityStack) {
                if (activity.getClass().equals(cls)) {
                    activities.add(activity);
                }
            }
        }

        for (Activity activity : activities) {
            killActivity(activity);
        }

    }

    /**
     * 结束所有Activity
     */
    public void killAllActivity() {
        if (mActivityStack == null || mActivityStack.isEmpty()) {
            return;
        }
        for (int i = 0, size = mActivityStack.size(); i < size; i++) {
            if (null != mActivityStack.get(i)) {
                mActivityStack.get(i).finish();
            }
        }
        mActivityStack.clear();
    }

    /**
     * 结束除了当前的其他所有Activity
     *
     * @param activity
     */
    public void killOthersActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        for (int i = 0, size = mActivityStack.size(); i < size; i++) {
            if (null != mActivityStack.get(i) && activity != mActivityStack.get(i)) {
                mActivityStack.get(i).finish();
            }
        }
        mActivityStack.clear();
        mActivityStack.add(activity);
    }

    /**
     * 判断Activity是否存在
     *
     * @param className
     * @return
     */
    public boolean existActivity(String className) {
        Activity activity = getActivityByName(className);
        if (activity != null && !activity.isDestroyed()) {
            return true;
        }
        return false;
    }

    /**
     * 根据名字查找Activity
     *
     * @param className
     * @return
     */
    public Activity getActivityByName(String className) {
        Activity activity = null;
        for (int i = 0, size = mActivityStack.size(); i < size; i++) {
            if (null != mActivityStack.get(i)) {
                if (mActivityStack.get(i).getClass().getName().equals(className)) {
                    activity = mActivityStack.get(i);
                }
            }
        }
        return activity;
    }

    /**
     * 删除并结束掉Activity
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {

        int pos = -1;
        if (activity != null && mActivityStack != null) {
            for (int i = 0, size = mActivityStack.size(); i < size; i++) {
                if (null != mActivityStack.get(i)) {
                    if (activity == mActivityStack.get(i)) {
                        pos = i;
                        activity.finish();
                    }
                }
            }
            if (pos != -1) {
                mActivityStack.remove(pos);
            }
        }
    }

    /**
     * 从栈里删除activity
     *
     * @param activity
     */
    public void remove(Activity activity) {
        if (activity != null && mActivityStack != null) {
            mActivityStack.remove(activity);
        }
    }

//    /**
//     * 退出应用程序
//     */
//    public void AppExit(MainActivity activity) {
//        try {
//            //退出也能接收到融云的消息提醒push
//            activity.onBackPressed();
//        } catch (Exception e) {
//        }
//    }

}
