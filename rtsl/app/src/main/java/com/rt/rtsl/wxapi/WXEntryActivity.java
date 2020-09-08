package com.rt.rtsl.wxapi;


import android.util.Log;

import com.rt.rtsl.utils.ToastUtil;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.umeng.socialize.weixin.view.WXCallbackActivity;


public class WXEntryActivity extends WXCallbackActivity
{
    @Override
    public void onResp(BaseResp resp) {
        super.onResp(resp);
        if(resp instanceof SendAuth.Resp){
            return;
        }
        if(resp instanceof SendMessageToWX.Resp){
            String result = null;
            switch (resp.errCode) {

                case BaseResp.ErrCode.ERR_OK:
                    result = "分享成功";
                    break;

                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    result = "分享取消";
                    break;

                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    result = "分享被拒绝";
                    break;

                default:
                    Log.e("tag", "resp.errCode=" + resp.errCode + " " + resp.errStr);
                    result = "分享返回";
                    break;
            }
            ToastUtil.show(this, result);
        }

    }
}