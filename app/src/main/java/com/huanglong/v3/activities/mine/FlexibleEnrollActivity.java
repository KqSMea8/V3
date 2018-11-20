package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import com.huanglong.v3.utils.KeyBoardUtils;
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
 * Created by bin on 2018/4/17.
 * 活动报名
 */
@ContentView(R.layout.activity_flexible_enroll)
public class FlexibleEnrollActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.flexible_enroll_real_name)
    private EditText edt_name;
    @ViewInject(R.id.flexible_enroll_mobile)
    private EditText edt_mobile;
    @ViewInject(R.id.flexible_enroll_price)
    private TextView tv_price;


    private String flexible_id;
    private String price;

    private WechatPayBean wechatPayBean;

    private WeChatBroadcastReceiver weChatBroadcastReceiver;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("报名详情");
        weChatBroadcastReceiver = new WeChatBroadcastReceiver();
    }

    @Override
    protected void logic() {
        Intent intent = getIntent();
        flexible_id = intent.getStringExtra("flexible_id");
        price = intent.getStringExtra("price");

        KeyBoardUtils.openKeybord(edt_name, this);

        edt_mobile.setText(UserInfoUtils.getUserName());
        tv_price.setText(price);
        registerReceiver();
    }

    /**
     * 注册微信分享广播
     */

    private void registerReceiver() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.PAY_WECHAT_ACTION);
        FlexibleEnrollActivity.this.registerReceiver(weChatBroadcastReceiver, filter);
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
                FlexibleEnrollActivity.this.setResult(RESULT_OK);
                FlexibleEnrollActivity.this.finish();
            } else if (TextUtils.equals("cancel", type)) {
                ToastUtils.showToast("取消支付");
            } else {
                ToastUtils.showToast("支付失败");
            }
        }
    }

    @Event(value = {R.id.title_back, R.id.flexible_enroll_btn})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                FlexibleEnrollActivity.this.finish();
                break;
            case R.id.flexible_enroll_btn:
                requestEnroll();
                break;
        }
    }

    /**
     * 活动报名请求
     */
    private void requestEnroll() {

        String real_name = edt_name.getText().toString().trim();
        if (TextUtils.isEmpty(real_name)) {
            ToastUtils.showToast("请输入真实姓名");
            return;
        }

        String mobile = edt_mobile.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.showToast("请输入联系方式");
            return;
        }
        RequestParams params = MRequestParams.getNoTokenParams(Api.activity_enroll);
        params.addBodyParameter("acitivity_id", flexible_id);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("truename", real_name);
        params.addBodyParameter("mobile", mobile);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    requestPay();
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
     * 加群支付
     */
    private void requestPay() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.pay);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("pay_type", "1");
        params.addBodyParameter("pay_amount", price);
        params.addBodyParameter("type", "8");
        params.addBodyParameter("activity_id", flexible_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    wechatPayBean = gson.fromJson(json, WechatPayBean.class);
                    if (wechatPayBean != null) {
                        WXUtils.wxPay(FlexibleEnrollActivity.this, wechatPayBean);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (weChatBroadcastReceiver != null) {
            FlexibleEnrollActivity.this.unregisterReceiver(weChatBroadcastReceiver);
        }
    }

}
