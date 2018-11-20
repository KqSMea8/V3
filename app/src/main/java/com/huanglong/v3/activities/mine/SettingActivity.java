package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.activities.login.LoginActivity;
import com.huanglong.v3.activities.login.EditPasswordActivity;
import com.huanglong.v3.activities.main.MainActivity;
import com.huanglong.v3.im.utils.TCLoginMgr;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.tencent.TIMCallBack;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by bin on 2018/4/10.
 * 设置页面
 */
@ContentView(R.layout.activity_setting)
public class SettingActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;

    private TCLoginMgr mTCLoginMgr;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("设置");
    }

    @Override
    protected void logic() {
        mTCLoginMgr = TCLoginMgr.getInstance();

    }

    @Event(value = {R.id.title_back, R.id.setting_logout, R.id.setting_modify_password, R.id.setting_about_me})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                SettingActivity.this.finish();
                break;
            case R.id.setting_logout:
                logout();
                break;
            case R.id.setting_modify_password:
                Intent intent = new Intent();
                intent.setClass(this, EditPasswordActivity.class);
                intent.putExtra("flag", 3);
                startActivity(intent);
                break;
            case R.id.setting_about_me:
                Intent intent1 = new Intent();
                intent1.setClass(this, AboutMeActivity.class);
                startActivity(intent1);
                break;

        }
    }

    /**
     * 退出登录
     */
    private void logout() {
        mTCLoginMgr.logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                ToastUtils.showToast("退出登录失败");
            }

            @Override
            public void onSuccess() {
                Intent intent = new Intent();
                intent.setClass(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                UserInfoUtils.setAutoLogin(false);
                SettingActivity.this.finish();
                if (MainActivity.instance != null) {
                    MainActivity.instance.finish();
                }

            }
        });
    }

}
