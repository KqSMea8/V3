package com.huanglong.v3.wxapi;

import android.app.Activity;
import android.content.Intent;

import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.utils.Common;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.xutils.common.util.LogUtil;

/**
 * Created by bin on 2017/11/21.
 * 微信回调类
 */

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

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
    public void onResp(BaseResp resp) {
        int errCode = resp.errCode;
        if (errCode == BaseResp.ErrCode.ERR_OK) {
            String code = "";
            try {
                code = ((SendAuth.Resp) resp).code;
            } catch (Exception e) {
                LogUtil.e(e.toString());
            }
            Intent intent = new Intent();
            intent.setAction(Common.BINDING_WECHAT_ACTION);
            intent.putExtra(Common.BINDING_WECHAT_KEY, "success");
            intent.putExtra(Common.BINDING_WECHAT_VALUE, code);
            sendBroadcast(intent);
            WXEntryActivity.this.finish();
        } else if (errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
            Intent intent = new Intent();
            intent.setAction(Common.BINDING_WECHAT_ACTION);
            intent.putExtra(Common.BINDING_WECHAT_KEY, "cancel");
            sendBroadcast(intent);
            WXEntryActivity.this.finish();
        } else if (errCode == BaseResp.ErrCode.ERR_SENT_FAILED) {
            Intent intent = new Intent();
            intent.setAction(Common.BINDING_WECHAT_ACTION);
            intent.putExtra(Common.BINDING_WECHAT_KEY, "failed");
            sendBroadcast(intent);
            WXEntryActivity.this.finish();
        } else {
            Intent intent = new Intent();
            intent.setAction(Common.BINDING_WECHAT_ACTION);
            intent.putExtra(Common.BINDING_WECHAT_KEY, "failed");
            sendBroadcast(intent);
            WXEntryActivity.this.finish();
        }
    }
}
