package com.luofangyun.shangchao.nohttp;

import android.content.Context;
import android.content.DialogInterface;

import com.luofangyun.shangchao.utils.UiUtils;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.NotFoundCacheError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.net.ProtocolException;

public class HttpResponseListener<T> implements OnResponseListener<T> {

    /**
     * Dialog
     */
    private WaitDialog mWaitDialog;

    private Request<?> mRequest;

    /**
     * 结果回调
     */
    private HttpListener<T> callback;

    /**
     * 是否显示dialog
     */
    private boolean isLoading;

    /**
     * @param context      context用来实例化dialog
     * @param request      请求对象
     * @param httpCallback 回调对象
     * @param canCancel    是否允许用户取消请求
     * @param isLoading    是否显示dialog
     */
    public HttpResponseListener(Context context, Request<?> request, HttpListener<T> httpCallback, boolean canCancel, boolean isLoading) {
        this.mRequest = request;
        if (context != null && isLoading) {
            mWaitDialog = new WaitDialog(context);
            mWaitDialog.setCancelable(canCancel);
            mWaitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mRequest.cancel();
                }
            });
        }
        this.callback = httpCallback;
        this.isLoading = isLoading;
    }

    /**
     * 开始请求, 这里显示一个dialog
     */
    @Override
    public void onStart(int what) {
        if (isLoading && mWaitDialog != null && !mWaitDialog.isShowing())
            mWaitDialog.show();
    }

    /**
     * 结束请求, 这里关闭dialog
     */
    @Override
    public void onFinish(int what) {
        if (isLoading && mWaitDialog != null && mWaitDialog.isShowing())
            mWaitDialog.dismiss();
    }

    /**
     * 成功回调
     */
    @Override
    public void onSucceed(int what, Response<T> response) {
        if (callback != null)
            callback.onSucceed(what, response);
    }
    /**
     * 失败回调
     */
   @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        if ( exception instanceof NetworkError) {// 网络不好
            UiUtils.ToastUtils( "请检查网络。" );
        } else if ( exception instanceof TimeoutError) {// 请求超时
            UiUtils.ToastUtils ( "请求超时，网络不好或者服务器不稳定。" );
        } else if ( exception instanceof UnKnownHostError) {// 找不到服务器
            UiUtils.ToastUtils ( "未发现指定服务器。" );
        } else if ( exception instanceof URLError) {// URL是错的
            UiUtils.ToastUtils ( "URL错误。" );
        } else if ( exception instanceof NotFoundCacheError) {
            // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            UiUtils.ToastUtils ( "没有发现缓存。" );
        } else if ( exception instanceof ProtocolException) {
            UiUtils.ToastUtils ( "系统不支持的请求方式。" );
        } else {
            UiUtils.ToastUtils ( "未知错误。" );
        }
        Logger.e ( "错误：" + exception.getMessage () );
    }

}