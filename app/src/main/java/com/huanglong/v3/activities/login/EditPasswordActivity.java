package com.huanglong.v3.activities.login;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.activities.main.MainActivity;
import com.huanglong.v3.im.utils.TCLoginMgr;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ToastUtils;
import com.tencent.TIMCallBack;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by bin on 2018/1/9.
 * 编辑密码页面（忘记面，修改密码）
 */
@ContentView(R.layout.activity_register_layout)
public class EditPasswordActivity extends BaseActivity {


    @ViewInject(R.id.title_name)
    private TextView tv_name;
    @ViewInject(R.id.title_rel)
    private RelativeLayout title_rel;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;

    @ViewInject(R.id.register_btn)
    private Button register_btn;
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
    @ViewInject(R.id.register_type)
    private RadioGroup register_type;

    private TCLoginMgr mTCLoginMgr;


    private int flag; //1.注册 2.忘记密码 3.修改密码
    private boolean isClick = true;

    private String str_phone;
    private String str_code;
    private String str_password;
    private String str_password_display;

    private int accountType = 0;//0.个人  1.企业


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", 0);

        if (flag == 1) {
            tv_name.setText(R.string.register);
            register_btn.setText(R.string.register);
            register_type.setVisibility(View.VISIBLE);
        } else if (flag == 2) {
            tv_name.setText(R.string.forget_password);
            register_btn.setText(R.string.submit);
            register_type.setVisibility(View.GONE);
        } else if (flag == 3) {
            tv_name.setText(R.string.modify_password);
            register_btn.setText(R.string.submit);
            register_type.setVisibility(View.GONE);
        }


    }


    @Override
    protected void logic() {
        mTCLoginMgr = TCLoginMgr.getInstance();

        register_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.register_type_personal) {
                    accountType = 0;
                } else {
                    accountType = 1;
                }
            }
        });
    }


    @Event(value = {R.id.title_back, R.id.register_btn, R.id.register_get_code})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                EditPasswordActivity.this.finish();
                break;
            case R.id.register_btn:
                if (validateInfo()) {
                    requestSubmit();
                }
                break;
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
        }
    }

    /**
     * 获取验证码
     */
    private void requestCode() {
        String url;
        if (flag == 1) {
            url = Api.get_code_register;
        } else {
            url = Api.get_code_forget;
        }
        RequestParams params = MRequestParams.getNoTokenParams(url);
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
        str_code = edt_code.getText().toString().trim();
        str_password = edt_password.getText().toString().trim();
        str_password_display = edt_password_again.getText().toString().trim();
        str_phone = edt_account.getText().toString().trim();
        if (TextUtils.isEmpty(str_phone)) {
            ToastUtils.showToast("请输入手机号");
            return false;
        }

        if (TextUtils.isEmpty(str_phone)) {
            ToastUtils.showToast("请输入手机号");
            return false;
        }
        if (TextUtils.isEmpty(str_code)) {
            ToastUtils.showToast("请输入验证码");
            return false;
        }
        if (TextUtils.isEmpty(str_password)) {
            ToastUtils.showToast("请输入密码");
            return false;
        }
        if (TextUtils.isEmpty(str_password_display)) {
            ToastUtils.showToast("请输入确认密码");
            return false;
        }

        if (!TextUtils.equals(str_password, str_password_display)) {
            ToastUtils.showToast("两次输入密码不一样");
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    /**
     * 提交注册/忘记密码
     */
    private void requestSubmit() {
        String url;
        if (flag == 1) {
            url = Api.register;
        } else {
            url = Api.forget_password;
        }
        RequestParams params = MRequestParams.getNoTokenParams(url);
        params.addBodyParameter("username", str_phone);
        params.addBodyParameter("password", str_password);
        params.addBodyParameter("code", str_code);
        if (flag == 1) {
            params.addBodyParameter("type", accountType + "");
            params.addBodyParameter("device_type", "a");
        }

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    if (flag == 1) {
                        ToastUtils.showToast("注册成功");
                    } else if (flag == 3) {
                        ToastUtils.showToast("密码修改成功");
                        logout();
                    } else {
                        ToastUtils.showToast("密码修改成功");
                    }
                    EditPasswordActivity.this.finish();
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
     * 退出登录
     */
    private void logout() {
        mTCLoginMgr.logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("退出登录失败，code:" + i + ",msg:" + s);

            }

            @Override
            public void onSuccess() {
                LogUtil.e("logout success");
            }
        });

        Intent intent = new Intent();
        intent.setClass(EditPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        EditPasswordActivity.this.finish();
        if (MainActivity.instance != null) {
            MainActivity.instance.finish();
        }
    }

}
