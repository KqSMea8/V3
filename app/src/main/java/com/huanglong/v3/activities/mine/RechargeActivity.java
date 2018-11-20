package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
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
 * Created by bin on 2018/1/28.
 * 充值界面
 */
@ContentView(R.layout.activity_recharge)
public class RechargeActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_name;
    @ViewInject(R.id.recharge_money)
    private EditText edt_money;
    @ViewInject(R.id.recharge_radio_group)
    private RadioGroup radio_group;

    private String money;
    private String order_sn;

    private WeChatBroadcastReceiver weChatBroadcastReceiver;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_name.setText("支付方式");

//        promptEditDialog = new PromptEditDialog(this);
//        promptEditDialog.setDialogStyle("提示", "请输入付款验证码", "取消", "确定", 1);
//
//        promptEditDialog.setOnClickListener(new PromptEditDialog.OnClickListener() {
//            @Override
//            public void onClick(int flag, String str) {
//                if (flag == 2) {
//                    requestKuaijie(str);
//                }
//            }
//        });

    }

    @Override
    protected void logic() {
        weChatBroadcastReceiver = new WeChatBroadcastReceiver();
        registerReceiver();
//        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                if (checkedId == R.id.recharge_qq) {
//                    pay_status = 1;
//                } else if (checkedId == R.id.recharge_wechat) {
//                    pay_status = 3;
//                } else {
//                    pay_status = 2;
//                }
//
//            }
//        });

    }

    @Event(value = {R.id.title_back, R.id.recharge_submit})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                RechargeActivity.this.finish();
                break;
            case R.id.recharge_submit:
                rechargeSubmit();
                break;
        }
    }

    /**
     * 充值
     */
    private void rechargeSubmit() {
        money = edt_money.getText().toString().trim();
        if (TextUtils.isEmpty(money)) {
            ToastUtils.showToast("请输入充值金额");
            return;
        }
        double rmb = Double.parseDouble(money);
        if (rmb < 10) {
            ToastUtils.showToast("微信充值不能少于10元");
            return;
        }
        showDialog();
        RequestParams params = MRequestParams.getUidParams(Api.Wxpay_pay);
        params.addBodyParameter("pay_type", "1");
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("type", "2");
        params.addBodyParameter("pay_amount", money);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    WechatPayBean wechatPayBean = gson.fromJson(json, WechatPayBean.class);
                    WXUtils.wxPay(getActivity(), wechatPayBean);
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
     * 快捷支付提交
     *
     * @param code
     */
//    private void requestKuaijie(String code) {
//
//        showProgressDialog();
//        RequestParams params = MRequestParams.getUidParams(Api.paycommit);
//        params.addBodyParameter("pay_amount", money);
//        params.addBodyParameter("check_code", code);
//        params.addBodyParameter("type", "1");
//        params.addBodyParameter("order_sn", order_sn);
//
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                String json = JsonHandleUtils.JsonHandle(result);
//                if (!TextUtils.isEmpty(json)) {
//                    ToastUtils.showToast("支付成功");
//                    RechargeActivity.this.finish();
//
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                JsonHandleUtils.netError(ex);
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//                dismissProgressDialog();
//            }
//        });
//    }

    /**
     * 注册微信分享广播
     */
    private void registerReceiver() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.PAY_WECHAT_ACTION);
        getActivity().registerReceiver(weChatBroadcastReceiver, filter);
    }

    /**
     * 微信分享成功后接收广播处理
     *
     * @author hbb
     */
    private class WeChatBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Common.PAY_WECHAT_ACTION)) return;
            String type = intent.getStringExtra(Common.PAY_WECHAT_KEY);
            if (TextUtils.equals("success", type)) {
                edt_money.setText("");
                ToastUtils.showToast("支付成功");
                RechargeActivity.this.finish();
            } else if (TextUtils.equals("cancel", type)) {
                ToastUtils.showToast("取消支付");
            } else {
                ToastUtils.showToast("支付失败");
            }

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (weChatBroadcastReceiver != null) {
            this.unregisterReceiver(weChatBroadcastReceiver);
        }
    }
}
