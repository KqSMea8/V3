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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.H5Activity;
import com.huanglong.v3.model.home.JobClassBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.DateUtils;
import com.huanglong.v3.utils.PickerUtils;
import com.huanglong.v3.utils.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/12.
 * 个人注册页面
 */

public class PerRegFragment extends BaseFragment {

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


    @ViewInject(R.id.register_gander)
    private RadioGroup register_gander;
    @ViewInject(R.id.register_nickname)
    private EditText edt_nickname;
    @ViewInject(R.id.register_signature)
    private EditText edt_signature;
    @ViewInject(R.id.register_sel_birthday)
    private TextView tv_sel_birthday;
    @ViewInject(R.id.register_sel_job)
    private TextView tv_sel_job;
    @ViewInject(R.id.register_ent_agreement)
    private CheckBox agreement;


    private boolean isClick = true;

    private String str_phone;
    private String str_code;
    private String str_nickname;
    private String str_signature;
    private String str_birthday;
    private String str_password;
    private String str_password_display;
    private String str_job;

    private int gander = 2;

    private List<JobClassBean> jobClassBeans;

    private List<String> jobs = new ArrayList<>();

    private boolean isReadAgreement = false;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_per_register, container, false);
        return view;
    }

    @Override
    protected void initView() {

        PickerUtils.initTimePickerView(getActivity(), "选择日期", new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_sel_birthday.setText(DateUtils.formatTime(date));
            }
        });

    }

    @Override
    protected void logic() {

        register_gander.check(R.id.register_gander_man);
        register_gander.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.register_gander_man) {
                    gander = 2;
                } else {
                    gander = 1;
                }
            }
        });
        requestJobClass();

        agreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isReadAgreement = b;
            }
        });
    }

    @Event(value = {R.id.register_get_code, R.id.register_btn, R.id.register_sel_birthday, R.id.register_sel_job,
            R.id.register_ent_tv_agreement})
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
            case R.id.register_sel_birthday:
                PickerUtils.showTimePicker(str_birthday);
                break;
            case R.id.register_sel_job:
                if (RegisterActivity.instance != null) {
                    RegisterActivity.instance.showSelJob(tv_sel_job, jobs);
                }
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
        str_nickname = edt_nickname.getText().toString().trim();
        if (TextUtils.isEmpty(str_nickname)) {
            ToastUtils.showToast("请输入昵称");
            return false;
        }
        str_birthday = tv_sel_birthday.getText().toString().trim();
        if (TextUtils.equals(getString(R.string.selected_birthday), str_birthday)) {
            ToastUtils.showToast("请选择出生日期");
            return false;
        }

        str_job = tv_sel_job.getText().toString().trim();
        if (TextUtils.equals(getString(R.string.selected_job), str_job)) {
            ToastUtils.showToast("请选择职位");
            return false;
        }

        if (!isReadAgreement) {
            ToastUtils.showToast("请先阅读用户注册协议");
            return false;
        }

        str_signature = edt_signature.getText().toString().trim();


        return true;
    }


    /**
     * 提交注册/忘记密码
     */
    private void requestSubmit() {
        String carrer_id = "";
        for (JobClassBean jobClassBean : jobClassBeans) {
            if (TextUtils.equals(str_job, jobClassBean.getName())) {
                carrer_id = jobClassBean.getId();
            }
        }
        RequestParams params = MRequestParams.getNoTokenParams(Api.register);
        params.addBodyParameter("username", str_phone);
        params.addBodyParameter("password", str_password);
        params.addBodyParameter("code", str_code);
        params.addBodyParameter("type", "1");
        params.addBodyParameter("device_type", "a");
        params.addBodyParameter("nickname", str_nickname);
        params.addBodyParameter("gender", gander + "");
        params.addBodyParameter("birthday", str_birthday);
        params.addBodyParameter("signature", str_signature);
        params.addBodyParameter("carrer_id", carrer_id);
        params.addBodyParameter("province", RegisterActivity.instance.province);
        params.addBodyParameter("city", RegisterActivity.instance.city);
        params.addBodyParameter("region", RegisterActivity.instance.region);

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


    /**
     * 请求职业分类
     */
    private void requestJobClass() {

        RequestParams params = MRequestParams.getNoTokenParams(Api.job_class);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    jobClassBeans = gson.fromJson(json, new TypeToken<LinkedList<JobClassBean>>() {
                    }.getType());
                    if (jobClassBeans != null) {
                        jobs.clear();
                        for (JobClassBean jobClassBean : jobClassBeans) {
                            jobs.add(jobClassBean.getName());
                        }
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
