package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.utils.PromptDialog;
import com.huanglong.v3.utils.ToastUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by bin on 2018/2/5.
 * 提现页面
 */
@ContentView(R.layout.activity_withdraw)
public class WithdrawActivity extends BaseActivity implements View.OnClickListener {


    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.withdrawals_radio_group)
    private RadioGroup radio_group;
    @ViewInject(R.id.withdrawals_password)
    private EditText edt_password;


    private int withdraw_money = 200;

    private PromptDialog promptDialog;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("提现");

        initDialog();

    }

    /**
     * 初始化dialog
     */
    private void initDialog() {
        promptDialog = new PromptDialog(this, R.layout.dialog_hint_currency);

        TextView dialog_content = (TextView) promptDialog.getView(R.id.dialog_comment_content);
        dialog_content.setText("请选完善信息");
        promptDialog.getView(R.id.dialog_comment_cancel).setOnClickListener(this);
        promptDialog.getView(R.id.dialog_comment_confirm).setOnClickListener(this);

    }

    @Override
    protected void logic() {

        radio_group.check(R.id.withdrawals_radio_one);
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.withdrawals_radio_one) {
                    withdraw_money = 200;
                } else if (i == R.id.withdrawals_radio_two) {
                    withdraw_money = 300;
                } else if (i == R.id.withdrawals_radio_three) {
                    withdraw_money = 500;
                }
            }
        });

        promptDialog.setOnClickListener(new PromptDialog.OnClickListener() {
            @Override
            public void onClick(int flag) {
                if (flag == 2) {
                    Intent intent = new Intent();
                    intent.setClass(WithdrawActivity.this, SelfInfoActivity.class);
                    startActivity(intent);
                }
            }
        });

    }


    @Event(value = {R.id.title_back, R.id.withdrawals_submit})
    private void monClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                WithdrawActivity.this.finish();
                break;
            case R.id.withdrawals_submit:
                requestSubmit();
                break;
        }
    }

    /**
     * 提交提现
     */
    private void requestSubmit() {

        String password = edt_password.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.showToast("请输入支付密码");
            return;
        }
        ToastUtils.showToast("申请提现成功");
//        showDialog();
//        RequestParams params = MRequestParams.getUidParams(Api.foranotherepay);
//        params.addBodyParameter("pay_amount", withdraw_money + "");
//        params.addBodyParameter("paypassword", password);
//
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
////                String code = JsonHandleUtils.getJsonValue("code", result);
////                if (TextUtils.equals("1005", code)) {
////                    promptDialog.show();
////                    return;
////                }
//                String json = JsonHandleUtils.JsonHandle(result);
//                if (!TextUtils.isEmpty(json)) {
//                    ToastUtils.showToast("提现成功");
//                }
//
//
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
//                dismissDialog();
//            }
//        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_comment_cancel:
                promptDialog.dismiss();
                break;
            case R.id.dialog_comment_confirm:
                promptDialog.dismiss();
                break;
        }
    }
}
