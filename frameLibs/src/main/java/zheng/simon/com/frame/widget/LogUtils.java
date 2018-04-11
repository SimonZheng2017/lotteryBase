package zheng.simon.com.frame.widget;

import android.text.TextUtils;
import android.util.Log;


import java.util.Locale;

import zheng.simon.com.frame.BuildConfig;


/**
 * 一个日志的工具类 可以开启和关闭打印日志 最好不要用System打印，消耗内存。
 */
public class LogUtils {

    private static boolean isLogEnabled = BuildConfig.DEBUG;// 默认开启
    private static final String defaultTag = "";// log默认的 tag
    private static final String TAG_CONTENT_PRINT = "%s.%s:%d";

    /**
     * 获得当前的 堆栈
     *
     * @return
     */
    private static StackTraceElement getCurrentStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];

    }

    /**
     * 打印的log信息 类名.方法名:行数--->msg
     *
     * @param trace
     * @return
     */
    private static String getContent(StackTraceElement trace) {
        return String.format(Locale.CHINA, TAG_CONTENT_PRINT, trace.getClassName(), trace.getMethodName(),
                trace.getLineNumber());
    }

    /**
     * debug
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg, Throwable tr) {
        if (isLogEnabled) {
            StackTraceElement[] sElements = new Throwable().getStackTrace();
            String className = sElements[1].getFileName();
            String methodName = sElements[1].getMethodName();
            int lineNumber = sElements[1].getLineNumber();

            StringBuffer buffer = new StringBuffer();
            buffer.append(className + "---");
            buffer.append(methodName + "---");

            buffer.append(lineNumber + "---");

            buffer.append(msg);
            msg = buffer.toString();
            Log.d(tag, msg, tr);
        }
    }

    /**
     * debug
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (isLogEnabled) {
            StackTraceElement[] sElements = new Throwable().getStackTrace();
            String className = sElements[1].getFileName();
            String methodName = sElements[1].getMethodName();
            int lineNumber = sElements[1].getLineNumber();

            StringBuffer buffer = new StringBuffer();
            buffer.append(className + "---");
            buffer.append(methodName + "---");

            buffer.append(lineNumber + "---");

            buffer.append(msg);
            msg = buffer.toString();
            Log.d(tag, msg);
        }
    }

    /**
     * debug
     *
     * @param msg
     */
    public static void d(String msg) {
        if (isLogEnabled) {
            StackTraceElement[] sElements = new Throwable().getStackTrace();
            String className = sElements[1].getFileName();
            String methodName = sElements[1].getMethodName();
            int lineNumber = sElements[1].getLineNumber();

            StringBuffer buffer = new StringBuffer();
            buffer.append(className + "---");
            buffer.append(methodName + "---");

            buffer.append(lineNumber + "---");

            buffer.append(msg);
            msg = buffer.toString();
            Log.d(defaultTag, msg);
        }
    }

    /**
     * error
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg, Throwable tr) {
        if (isLogEnabled) {
            StackTraceElement[] sElements = new Throwable().getStackTrace();
            String className = sElements[1].getFileName();
            String methodName = sElements[1].getMethodName();
            int lineNumber = sElements[1].getLineNumber();

            StringBuffer buffer = new StringBuffer();
            buffer.append(className + "---");
            buffer.append(methodName + "---");

            buffer.append(lineNumber + "---");

            buffer.append(msg);
            msg = buffer.toString();
            Log.e(tag, msg, tr);
        }
    }

    /**
     * error
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (isLogEnabled) {
            StackTraceElement[] sElements = new Throwable().getStackTrace();
            String className = sElements[1].getFileName();
            String methodName = sElements[1].getMethodName();
            int lineNumber = sElements[1].getLineNumber();

            StringBuffer buffer = new StringBuffer();
            buffer.append(className + "---");
            buffer.append(methodName + "---");

            buffer.append(lineNumber + "---");

            buffer.append(msg);
            msg = buffer.toString();
            Log.e(tag, msg);
        }
    }

    /**
     * error
     *
     * @param msg
     */
    public static void e(String msg) {
        if (isLogEnabled) {
            StackTraceElement[] sElements = new Throwable().getStackTrace();
            String className = sElements[1].getFileName();
            String methodName = sElements[1].getMethodName();
            int lineNumber = sElements[1].getLineNumber();

            StringBuffer buffer = new StringBuffer();
            buffer.append(className + "---");
            buffer.append(methodName + "---");

            buffer.append(lineNumber + "---");

            buffer.append(msg);
            msg = buffer.toString();
            Log.e(defaultTag, msg);
        }
    }

    /**
     * info
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg, Throwable tr) {
        if (isLogEnabled) {
            if (!TextUtils.isEmpty(msg))
                Log.i(tag, msg, tr);
        }

    }

    /**
     * info
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (isLogEnabled) {
            if (!TextUtils.isEmpty(msg))
                Log.i(tag, msg);
        }
    }

    /**
     * info
     *
     * @param msg
     */
    public static void i(String msg) {
        if (isLogEnabled) {
            if (!TextUtils.isEmpty(msg))
                Log.i(defaultTag, msg);
        }
    }

    /**
     * verbose
     *
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg, Throwable tr) {
        if (isLogEnabled) {
            if (!TextUtils.isEmpty(msg))
                Log.v(tag, msg, tr);
        }
    }

    /**
     * verbose
     *
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg) {
        if (isLogEnabled) {
            if (!TextUtils.isEmpty(msg))
                Log.v(tag, msg);
        }
    }

    /**
     * verbose
     *
     * @param msg
     */
    public static void v(String msg) {
        if (isLogEnabled) {
            if (!TextUtils.isEmpty(msg))
                Log.v(defaultTag, msg);
        }
    }

    /**
     * warn
     *
     * @param tag
     * @param msg
     */
    public static void w(String tag, String msg, Throwable tr) {
        if (isLogEnabled) {
            if (!TextUtils.isEmpty(msg))
                Log.w(tag, msg, tr);
        }
    }

    /**
     * warn
     *
     * @param tag
     * @param msg
     */
    public static void w(String tag, String msg) {
        if (isLogEnabled) {
            if (!TextUtils.isEmpty(msg))
                Log.w(tag, msg);
        }
    }

    /**
     * warn
     *
     * @param msg
     */
    public static void w(String msg) {
        if (isLogEnabled) {
            if (!TextUtils.isEmpty(msg))
                Log.w(defaultTag, msg);
        }
    }
}
