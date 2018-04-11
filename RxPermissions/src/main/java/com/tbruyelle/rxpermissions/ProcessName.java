package com.tbruyelle.rxpermissions;

/**
 * Created by Soli on 2016/11/4.
 */

public class ProcessName {

    //当前程序运行的进程
    public static final String MainProcess = "com.showstartfans.activity";
    //图片全屏 和选择图片这里单独一个进程
    public static final String PictureDealProcess = MainProcess + ":picture";
    //融云推送进程
    public static final String RongPushProcess = "io.rong.push";
    //融云服务
    public static final String RongServicesProcess = MainProcess + ":ipc";

}
