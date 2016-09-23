package com.luofangyun.shangchao.wxapi;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * Created by ntop on 15/9/4.
 */
public class WXEntryActivity extends Activity  implements IWXAPIEventHandler {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ZWXShare.getInstance().getIWXAPIinstance().handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onReq(BaseReq arg0) { }

    @Override
    public void onResp(BaseResp resp) {
        Log.e("WX_share", "resp.errCode:" + resp.errCode + ",resp.errStr:"
                + resp.errStr);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX == resp.getType()) {
                    finish();
//                    Log.e("WX_share","分享成功");
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Log.e("WX_share","分享取消");
                //分享取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Log.e("WX_share","分享拒绝");
                //分享拒绝
                break;
        }
    }
}
