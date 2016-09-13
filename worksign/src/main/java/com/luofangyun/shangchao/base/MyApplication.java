package com.luofangyun.shangchao.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.baidu.mapapi.SDKInitializer;
import com.igexin.sdk.PushManager;
import com.luofangyun.shangchao.utils.PrefUtils;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;

public class MyApplication extends Application {
    private static Context context;
    private static Handler handler;
    private static int     mainThreadId;
    public static String              TAG        = "MyApplication";
    private static MyApplication mInstance = null;
    public String phoneNumber;

    // SDK参数，会自动从Manifest文件中读取，第三方无需修改下列变量，请修改AndroidManifest.xml文件中相应的meta-data信息。
    // 修改方式参见个推SDK文档
    private String appkey = "";
    private String appsecret = "";
    private String appid = "";
    @Override
    public void onCreate() {
        phoneNumber = PrefUtils.getString(this, "phoneNumber", null);
        super.onCreate();
       /**
         * 个推初始化
         */
        SDKInitializer.initialize(this);
        PushManager.getInstance().initialize(this.getApplicationContext());

        //初始化NoHttp
        NoHttp.init(this);
        //打开NoHttp调试模式
        Logger.setDebug(true);
        //设置Log
        Logger.setTag("worksign");
        //初始化context对象
        context = getApplicationContext();
        //初始化handler
        handler = new Handler();
        //获取主线程的线程id  myTid()在哪个方法被调用，返回的就是那个方法所在的线程id
        mainThreadId = android.os.Process.myTid();
    }
    public static MyApplication getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }
}



