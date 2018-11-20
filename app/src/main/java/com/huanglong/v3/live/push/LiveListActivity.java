package com.huanglong.v3.live.push;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.carddistinguish.CardDistinguishActivity;
import com.huanglong.v3.model.AuthenBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by bin on 2018/3/29.
 * 直播列表
 */
@ContentView(R.layout.activity_live_list)
public class LiveListActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("直播");
        tv_right.setText("发布直播");
    }

    @Override
    protected void logic() {

    }

    @Event(value = {R.id.title_back, R.id.title_tv_right})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                LiveListActivity.this.finish();
                break;
            case R.id.title_tv_right:
                requestCheck();
                break;
        }
    }


    /**
     * 检查是否实名
     */
    private void requestCheck() {
        RequestParams params = MRequestParams.getUidParams(Api.getCheckUser);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    AuthenBean authenBean = gson.fromJson(json, AuthenBean.class);
                    if (authenBean != null) {
                        int is_formal = authenBean.getIs_formal();
                        if (is_formal == 1) {
                            Intent intent = new Intent();
                            intent.setClass(LiveListActivity.this, TCPublishSettingActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(LiveListActivity.this, CardDistinguishActivity.class);
                            startActivity(intent);
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
