package com.huanglong.v3.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.activities.H5Activity;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by bin on 2018/4/12.
 * 企业注册页面
 */

public class EntRegFragment extends BaseFragment {

    @ViewInject(R.id.register_get_code)
    private TextView tv_get_code;
    @ViewInject(R.id.register_edt_account)
    private EditText edt_account;
    @ViewInject(R.id.register_edt_password)
    private EditText edt_password;
    @ViewInject(R.id.register_edt_password_again)
    private EditText edt_password_again;
    @ViewInject(R.id.register_code)
    private EditText edt_code;
    @ViewInject(R.id.register_edt_ent_abb)
    private EditText edt_ent_abb;
    @ViewInject(R.id.register_edt_main_business)
    private TextView edt_main_business;
    @ViewInject(R.id.register_edt_contacts)
    private EditText edt_contacts;
    @ViewInject(R.id.register_edt_ent_sel_class)
    private TextView tv_class;
    @ViewInject(R.id.register_edt_address)
    private EditText edt_address;
    @ViewInject(R.id.register_ent_agreement)
    private CheckBox agreement;

    private boolean isClick = true;

    private String str_phone;
    private String str_code;
    private String str_contacts;
    private String str_ent_abb;
    private String str_main_business;
    private String str_password;
    private String str_password_display;
    private String class_id;
    private String str_address;

    private boolean isReadAgreement = false;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ent_register, container, false);
        return view;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void logic() {
        agreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isReadAgreement = b;
            }
        });
    }


    @Event(value = {R.id.register_get_code, R.id.register_btn, R.id.register_edt_ent_sel_class_lin, R.id.register_ent_tv_agreement})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_get_code:
                if (isClick) {
                    str_phone = edt_account.getText().toString().trim();
                    if (!TextUtils.isEmpty(str_phone)) {
                        requestCode();
                    } else {
                        ToastUtils.showToast("请输入手机号");
                    }
                }
                break;
            case R.id.register_btn:
                if (validateInfo()) {
                    requestSubmit();
                }
                break;
            case R.id.register_edt_ent_sel_class_lin:
                Intent intent = new Intent();
                intent.setClass(getActivity(), EntClassActivity.class);
                startActivityForResult(intent, 1000);
                break;
            case R.id.register_ent_tv_agreement:
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(), H5Activity.class);
                intent1.putExtra("title", "用户注册协议");
                intent1.putExtra("url", Api.agreement);
                startActivity(intent1);
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void requestCode() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.get_code_register);
        params.addBodyParameter("mobile", str_phone);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    timer.start();
                    ToastUtils.showToast("验证码已发送，请注意查收");
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
     * 验证输入信息
     *
     * @return
     */
    private boolean validateInfo() {

        str_phone = edt_account.getText().toString().trim();
        if (TextUtils.isEmpty(str_phone)) {
            ToastUtils.showToast("请输入手机号");
            return false;
        }

        str_code = edt_code.getText().toString().trim();
        if (TextUtils.isEmpty(str_code)) {
            ToastUtils.showToast("请输入验证码");
            return false;
        }
        str_password = edt_password.getText().toString().trim();
        if (TextUtils.isEmpty(str_password)) {
            ToastUtils.showToast("请输入密码");
            return false;
        }
        str_password_display = edt_password_again.getText().toString().trim();
        if (TextUtils.isEmpty(str_password_display)) {
            ToastUtils.showToast("请输入确认密码");
            return false;
        }

        if (!TextUtils.equals(str_password, str_password_display)) {
            ToastUtils.showToast("两次输入密码不一样");
            return false;
        }
        str_contacts = edt_contacts.getText().toString().trim();
        if (TextUtils.isEmpty(str_contacts)) {
            ToastUtils.showToast("请输入联系人");
            return false;
        }

        str_ent_abb = edt_ent_abb.getText().toString().trim();
        if (TextUtils.isEmpty(str_ent_abb)) {
            ToastUtils.showToast("请输入企业简称");
            return false;
        }

        str_main_business = edt_main_business.getText().toString().trim();
        if (TextUtils.isEmpty(str_main_business)) {
            ToastUtils.showToast("请输入主营业务");
            return false;
        }
        str_address = edt_address.getText().toString();

        if (TextUtils.isEmpty(str_address)) {
            ToastUtils.showToast("请输入企业地址");
            return false;
        }
        if (TextUtils.isEmpty(class_id)) {
            ToastUtils.showToast("请选择分类");
            return false;
        }

        if (!isReadAgreement) {
            ToastUtils.showToast("请先阅读用户注册协议");
            return false;
        }



        return true;
    }


    /**
     * 提交注册/忘记密码
     */
    private void requestSubmit() {

        RequestParams params = MRequestParams.getNoTokenParams(Api.register);
        params.addBodyParameter("username", str_phone);
        params.addBodyParameter("password", str_password);
        params.addBodyParameter("code", str_code);
        params.addBodyParameter("type", "2");
        params.addBodyParameter("device_type", "a");
        params.addBodyParameter("nickname", str_contacts);
        params.addBodyParameter("short_name", str_ent_abb);
        params.addBodyParameter("industry_id", class_id);
        params.addBodyParameter("main_scope", str_main_business);
        params.addBodyParameter("address", str_address);
        params.addBodyParameter("location", RegisterActivity.instance.str_location);
        params.addBodyParameter("longitude", RegisterActivity.instance.str_longitude);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("注册成功");
                    if (RegisterActivity.instance != null) {
                        RegisterActivity.instance.closeActivity(str_phone);
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
     * 验证码倒计时
     */
    private CountDownTimer timer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            isClick = false;
            tv_get_code.setText((millisUntilFinished / 1000) + "秒后可重发");
        }

        @Override
        public void onFinish() {
            isClick = true;
            tv_get_code.setEnabled(true);
            tv_get_code.setText(R.string.send_code);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    if (data == null) return;
                    class_id = data.getStringExtra("class_id");
                    String class_one_name = data.getStringExtra("class_one_name");
                    String class_two_name = data.getStringExtra("class_two_name");
                    tv_class.setText(class_one_name + "-" + class_two_name);
                    break;
            }
        }
    }
}
