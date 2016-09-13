package com.luofangyun.shangchao.nohttp;

import com.yolanda.nohttp.rest.Response;


/**
 * 接受回调结果
 * </br>
 * Created in Nov 4, 2015 12:54:50 PM
 *
 * @author YOLANDA
 */
public interface HttpListener<T> {

    public void onSucceed(int what, Response<T> response);

    public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis);

}
