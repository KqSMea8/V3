package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.model.mine.UserInfoBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;

/**
 * Created by bin on 2018/1/28.
 * 余额页面
 */
@ContentView(R.layout.activity_balance)
public class BalanceActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.balance_money)
    private TextView tv_money;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;

    private String balance;

    private UserInfoBean personalBean;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("余额");
        tv_right.setText("账户明细");

    }

    @Override
    protected void logic() {

    }

    @Event(value = {R.id.title_back, R.id.balance_recharge, R.id.title_tv_right, R.id.balance_withdrawals})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                BalanceActivity.this.finish();
                break;
            case R.id.balance_recharge:
                Intent intent = new Intent();
                intent.setClass(this, RechargeActivity.class);
                startActivity(intent);
                break;
            case R.id.title_tv_right:
                Intent intent1 = new Intent();
                intent1.setClass(this, AccountDetailsActivity.class);
                startActivity(intent1);
                break;
            case R.id.balance_withdrawals:
                Intent intent2 = new Intent();
                intent2.setClass(this, WithdrawActivity.class);
                startActivity(intent2);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPersonal();
    }

    /**
     * 保留两位小数
     *
     * @param balance
     * @return
     */
    private String getMoney(double balance) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(balance);
    }

    /**
     * 请求个人资料
     */
    private void requestPersonal() {

        RequestParams params = MRequestParams.getUidParams(Api.getUserInfo);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    personalBean = gson.fromJson(json, UserInfoBean.class);
                    if (personalBean != null) {
                        tv_money.setText(getMoney(personalBean.getBlance() == 0 ? 0 : personalBean.getBlance()));

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

}
