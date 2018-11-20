package com.huanglong.v3.activities.message;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.huanglong.v3.utils.MImageOptions;
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
 * Created by bin on 2018/3/18.
 * 发福包
 */
@ContentView(R.layout.activity_send_bless_packet)
public class SendBlessActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.send_bless_avatar)
    private ImageView img_avatar;
    @ViewInject(R.id.sends_bless_title)
    private EditText edt_title;
    @ViewInject(R.id.sends_bless_content)
    private EditText edt_content;
    @ViewInject(R.id.sends_bless_password)
    private EditText edt_password;
    @ViewInject(R.id.sends_bless_money)
    private EditText edt_money;
    @ViewInject(R.id.sends_bless_number)
    private EditText edt_number;


    private String title, content, password, money, number;
    private WechatPayBean wechatPayBean;

    private WeChatBroadcastReceiver weChatBroadcastReceiver;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("发福包");

//        edt_title.setText("111");
//        edt_content.setText("111");
//        edt_password.setText("111");
//        edt_money.setText("10");
//        edt_number.setText("5");

        weChatBroadcastReceiver = new WeChatBroadcastReceiver();

    }

    @Override
    protected void logic() {
        x.image().bind(img_avatar, UserInfoUtils.getAvatar(), MImageOptions.getCircularImageOptions());

    }

    /**
     * 注册微信分享广播
     */
    private void registerReceiver() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.PAY_WECHAT_ACTION);
        SendBlessActivity.this.registerReceiver(weChatBroadcastReceiver, filter);
    }

    @Event(value = {R.id.title_back, R.id.sends_bless_submit})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                SendBlessActivity.this.finish();
                break;
            case R.id.sends_bless_submit:
                getInputInfo();
                requestWechatPay();
                break;
        }
    }

    /**
     * 获取输入信息
     */
    private void getInputInfo() {
        title = edt_title.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            ToastUtils.showToast("请输入福包标题");
            return;
        }
        content = edt_content.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showToast("请输入福包内容");
            return;
        }
        password = edt_password.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.showToast("请输入福包口令");
            return;
        }
        money = edt_money.getText().toString().trim();
        if (TextUtils.isEmpty(money)) {
            ToastUtils.showToast("请输入福包赏金");
            return;
        }
        if (TextUtils.equals("0", money)) {
            ToastUtils.showToast("赏金不能为0");
            return;
        }

        number = edt_number.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            ToastUtils.showToast("请输入福包数量");
            return;
        }
        if (TextUtils.equals("0", number)) {
            ToastUtils.showToast("数量不能为0");
            return;
        }
    }


    /**
     * 请求微信支付
     */
    private void requestWechatPay() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.pay);
        params.addBodyParameter("pay_type", "6");
        params.addBodyParameter("pay_amount", money);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    wechatPayBean = gson.fromJson(json, WechatPayBean.class);
                    if (wechatPayBean != null) {
                        registerReceiver();
                        WXUtils.wxPay(SendBlessActivity.this, wechatPayBean);
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

            }
        });

    }

    /**
     * 发布福包
     */
    private void requestSendPacket() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.fubao_create);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("title", title);
        params.addBodyParameter("content", content);
        params.addBodyParameter("price", money);
        params.addBodyParameter("account", number);
        params.addBodyParameter("secret_pwd", password);
        params.addBodyParameter("order_id", wechatPayBean.getOrder_id());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    SendBlessActivity.this.finish();
                    if (BlePacActivity.instance != null){
                        BlePacActivity.instance.onRefresh();
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

            }
        });


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
                //TODO
//                ToastUtils.showToast("支付成功");
                requestSendPacket();
            } else if (TextUtils.equals("cancel", type)) {
                ToastUtils.showToast("取消支付");
            } else {
                ToastUtils.showToast("支付失败");
            }
            SendBlessActivity.this.unregisterReceiver(this);// 不需要时注销
        }
    }


}
