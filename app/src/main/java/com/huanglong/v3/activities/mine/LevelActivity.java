package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.model.WechatPayBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.Common;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.utils.WXUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by bin on 2018/7/12.
 * 会员等级
 */
@ContentView(R.layout.activity_level)
public class LevelActivity extends BaseActivity {


    @ViewInject(R.id.title_name)
    private TextView tv_title;

    private WeChatBroadcastReceiver weChatBroadcastReceiver;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("会员充值");
    }

    @Override
    protected void logic() {
        weChatBroadcastReceiver = new WeChatBroadcastReceiver();
        registerReceiver();
    }


    @Event(value = {R.id.title_back, R.id.level_buy_one_stars, R.id.level_buy_two_stars, R.id.level_buy_three_stars, R.id.level_buy_four_stars})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.level_buy_one_stars:
                requestPay("12");
                break;
            case R.id.level_buy_two_stars:
                requestPay("30");
                break;
            case R.id.level_buy_three_stars:
                requestPay("60");
                break;
            case R.id.level_buy_four_stars:
                requestPay("108");
                break;
        }
    }


    /**
     * 请求支付接口
     */
    private void requestPay(String pay_amount) {
        showDialog();
        RequestParams params = MRequestParams.getNoTokenParams(Api.pay);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("pay_type", "1");
        params.addBodyParameter("pay_amount", pay_amount);
        params.addBodyParameter("type", "2");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    WechatPayBean wechatPayBean = gson.fromJson(json, WechatPayBean.class);
                    if (wechatPayBean != null) {
                        WXUtils.wxPay(LevelActivity.this, wechatPayBean);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                JsonHandleUtils.netError(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                dismissDialog();
            }
        });
    }

    /**
     * 注册微信分享广播
     */

    private void registerReceiver() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.PAY_WECHAT_ACTION);
        LevelActivity.this.registerReceiver(weChatBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LevelActivity.this.unregisterReceiver(weChatBroadcastReceiver);
    }

    /**
     * 微信支付成功后接收广播处理
     *
     * @author hbb
     */
    private class WeChatBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Common.PAY_WECHAT_ACTION)) return;
            String type = intent.getStringExtra(Common.PAY_WECHAT_KEY);
            if (TextUtils.equals("success", type)) {
                ToastUtils.showToast("支付成功");

            } else if (TextUtils.equals("cancel", type)) {
                ToastUtils.showToast("取消支付");
            } else {
                ToastUtils.showToast("支付失败");
            }
        }
    }
}
