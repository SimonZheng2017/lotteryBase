package zheng.simon.com.frame.widget;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhengyouquan on 05/02/2018.
 */

public class Mytools {


    public static boolean isPhone(String phone) {
        if (TextUtils.isEmpty(phone))
            return false;
        return phone.matches("^1[3|4|5|7|8|9]\\d{9}$");
    }


    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }


    /**
     * MD5加密，32位
     *
     * @param str
     * @return
     */
    public static String getMD5_32(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }


    public static String getUUID(Context ctx) {
        SPUtil spUtileBiz = SPUtil.getInstance(ctx);
        String IMEI = spUtileBiz.getUUID();
        if (TextUtils.isEmpty(IMEI)) {
            if (RxPermissions.getInstance(ctx).isGranted(Manifest.permission.READ_PHONE_STATE)) {
                TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
                String imei = telephonyManager.getDeviceId();
                if (imei != null && imei.trim().length() > 0) {
                    IMEI = imei;
                    spUtileBiz.saveUUID(IMEI);
                }
            }

            if (TextUtils.isEmpty(IMEI)) {
                IMEI = getUUID();
                spUtileBiz.saveUUID(IMEI);
            }
        }
        return IMEI;
    }


    /**
     * @return
     */
    private static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        // 去掉"-"符号
        String temp = str.substring(0, 8) + str.substring(9, 13)
                + str.substring(14, 18) + str.substring(19, 23)
                + str.substring(24);
        return temp;
    }


    /**
     * app版本号
     *
     * @param ctx
     * @return
     */
    public static String getAppVersionName(Context ctx) {
        try {
            PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            if (packageInfo != null) {
                return packageInfo.versionName;
            }
        } catch (Exception e) {

        }
        return "1.0.0";
    }


    /**
     * 获取版本编号versionCode
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            return info.versionCode;
        } catch (Exception e) {
            return 0;
        }
    }


    public static float getVersionFloat(String versionStr) {
        String[] split = versionStr.split("\\.");
        String version = "";
        for (int i = 0; i < split.length; i++) {
            if (i == 0) {
                version = split[i] + ".";
            } else {
                version = version + split[i];
            }
        }

        if (TextUtils.isEmpty(version)) {
            version = "0";
        }

        return Float.parseFloat(version);

    }


    /**
     * 获取版本名字versionName
     *
     * @return
     */
    public static String getVersionStr(Context ctx) {
        try {
            PackageManager manager = ctx.getPackageManager();
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            return "0.0";
        }
    }


    /**
     * @param ctx
     * @param packageName
     * @return 是否安装了某一个app
     */
    public static boolean isAppInstalled(Context ctx, String packageName) {
        // 获取到一个PackageManager的instance
        final PackageManager packageManager = ctx.getPackageManager();
        // PERMISSION_GRANTED = 0
        List<PackageInfo> mPackageInfo = packageManager.getInstalledPackages(0);
        boolean flag = false;
        if (mPackageInfo != null) {
            String tempName = null;
            for (int i = 0; i < mPackageInfo.size(); i++) {
                // 获取到AP包名
                tempName = mPackageInfo.get(i).packageName;
                if (tempName != null && tempName.equals(packageName)) {

                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * @return String 生成32位的随机数作为id
     */
    public static String get32RandomID() {
        return UUID.randomUUID().toString().trim().replaceAll("-", "");
    }


    /**
     * 获取手机操作系统版本
     *
     * @return
     */
    public static int getSDKVersionNumber() {
        int sdkVersion;
        try {
            sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            sdkVersion = 0;
        }
        return sdkVersion;
    }


    /**
     * 检查网络是否可用
     */
    public static boolean isNetworkAvailable(Context ctx) {
        NetworkInfo ifo = ((ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (ifo != null) {
            if (ifo.isAvailable()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


    /**
     * 是否连接wifi
     *
     * @param icontext
     * @return
     */
    public static boolean isWifiActive(Context icontext) {
        Context context = icontext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info;
        if (connectivity != null) {
            info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断数据连接的类型
     *
     * @param networkType
     * @return
     */
    public static int getNetworkClass(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:

                return 2;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return 3;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return 4;
            default:
                return 0;
        }
    }


    /**
     * 得到网络类型，0是未知或未连上网络，1为WIFI，2为2g，3为3g，4为4g
     *
     * @return
     */
    public static int getNetType(Application app) {
        ConnectivityManager connectMgr = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);

        int type = 0;
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return type;
        }

        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
                type = 1;
                break;
            case ConnectivityManager.TYPE_MOBILE:
                type = getNetworkClass(info.getSubtype());
                break;

            default:
                type = 0;
                break;
        }
        return type;
    }





}
