package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by bin on 2018/5/17.
 * 反馈
 */
@ContentView(R.layout.activity_feedback)
public class FeedbackActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.feedback_content)
    private EditText edt_content;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("意见反馈");
    }

    @Override
    protected void logic() {

    }

    @Event(value = {R.id.title_back, R.id.feedback_submit})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.feedback_submit:
                requestFeedback();
                break;
        }
    }

    /**
     * 提交反馈信息
     */
    public void requestFeedback() {

        String content = edt_content.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showToast("请输入反馈内容");
            return;
        }
        showDialog();
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_my_suggest);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("content", content);
        params.addBodyParameter("device_type", "a");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("感谢您的反馈");
                    edt_content.setText("");
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

}
