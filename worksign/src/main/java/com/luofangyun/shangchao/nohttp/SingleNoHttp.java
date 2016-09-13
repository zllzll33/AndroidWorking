package com.luofangyun.shangchao.nohttp;

import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.RequestQueue;
/**
 * Nohhtp单例
 */
public class SingleNoHttp {
    public static SingleNoHttp instance;
    public        RequestQueue request;


    private SingleNoHttp() {
    }

    public static synchronized SingleNoHttp getInstance() {
        if (instance == null) {
            instance = new SingleNoHttp();
        }
        return instance;
    }

    public RequestQueue getRequest() {
        request = NoHttp.newRequestQueue();    //创建一个消息队列
        return request;
    }

}
