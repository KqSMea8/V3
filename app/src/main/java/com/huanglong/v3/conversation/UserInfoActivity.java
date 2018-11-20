package com.huanglong.v3.conversation;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by bin on 2018/3/15.
 * 用户信息页面
 */
@ContentView(R.layout.activity_user_info)
public class UserInfoActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("详细资料");
    }

    @Override
    protected void logic() {

    }

    @Event(value = {R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {

        }
    }

}
