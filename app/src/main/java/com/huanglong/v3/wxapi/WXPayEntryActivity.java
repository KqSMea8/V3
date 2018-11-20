package com.huanglong.v3.wxapi;

import android.app.Activity;
import android.content.Intent;

import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.utils.Common;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by bin on 2017/12/1.
 * 微信支付回调类
 */

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void logic() {
        api = WXAPIFactory.createWXAPI(this, Common.WX_APPID, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (BaseResp.ErrCode.ERR_OK == baseResp.errCode) {//成功
                Intent intent = new Intent();
                intent.setAction(Common.PAY_WECHAT_ACTION);
                intent.putExtra(Common.PAY_WECHAT_KEY, "success");
                sendBroadcast(intent);
                WXPayEntryActivity.this.finish();
            } else if (BaseResp.ErrCode.ERR_USER_CANCEL == baseResp.errCode) {  //支付取消
                Intent intent = new Intent();
                intent.setAction(Common.PAY_WECHAT_ACTION);
                intent.putExtra(Common.PAY_WECHAT_KEY, "cancel");
                sendBroadcast(intent);
                WXPayEntryActivity.this.finish();
            } else { //支付失败
                Intent intent = new Intent();
                intent.setAction(Common.PAY_WECHAT_ACTION);
                intent.putExtra(Common.PAY_WECHAT_KEY, "fail");
                sendBroadcast(intent);
                WXPayEntryActivity.this.finish();
            }
        }
    }
}
