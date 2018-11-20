package com.huanglong.v3.activities.carddistinguish;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.live.push.TCPublishSettingActivity;
import com.huanglong.v3.model.circle.RelImageBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.Common;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.SelectPictureUtils;
import com.huanglong.v3.utils.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * Created by bin on 2018/3/21.
 * 身份证识别
 */
@ContentView(R.layout.activity_card_distinguish)
public class CardDistinguishActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.card_id_real_name)
    private EditText edt_real_name;
    @ViewInject(R.id.card_id_number)
    private EditText edt_id_number;
    @ViewInject(R.id.card_selected_id_img)
    private ImageView img_id_card;

    private String idCardPath = "";

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("实名认证");
    }

    @Override
    protected void logic() {

    }

    @Event(value = {R.id.title_back, R.id.card_dis, R.id.card_selected_id_img})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                CardDistinguishActivity.this.finish();
                break;
            case R.id.card_dis:
                uploadImage();
                break;
            case R.id.card_selected_id_img:
                SelectPictureUtils.selectedIdCard(this, 700, 438);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Common.IMAGE_PICKER://PictureConfig.CHOOSE_REQUEST:
                    if (data != null) {
                        List<String> pathList = data.getStringArrayListExtra("result");
                        idCardPath = pathList.get(0);
                        x.image().bind(img_id_card, idCardPath, MImageOptions.getNormalImageNotCropOptions());

                    }
                    break;
            }
        }
    }

    /**
     * 上传身份证图片
     */
    private void uploadImage() {

        String real_name = edt_real_name.getText().toString().trim();
        if (TextUtils.isEmpty(real_name)) {
            ToastUtils.showToast("请输入真实姓名");
            return;
        }
        String id_number = edt_id_number.getText().toString().trim();
        if (TextUtils.isEmpty(id_number)) {
            ToastUtils.showToast("请输入身份证号");
            return;
        }

        if (TextUtils.isEmpty(idCardPath)) {
            ToastUtils.showToast("请选择身份证图片");
            return;
        }

        RequestParams params = MRequestParams.getNoTokenParams(Api.uploadimg);
        params.setMultipart(true);
        params.addBodyParameter("imglist", new File(idCardPath));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    RelImageBean relImageBean = gson.fromJson(json, RelImageBean.class);
                    if (relImageBean != null) {
                        requestSubmit(id_number, real_name, relImageBean.getUrl());
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
     * 提交信息
     *
     * @param idcard
     * @param truename
     * @param idcard_url
     */
    private void requestSubmit(String idcard, String truename, String idcard_url) {
        RequestParams params = MRequestParams.getUidParams(Api.userCertificate);
        params.addBodyParameter("idcard", idcard);
        params.addBodyParameter("truename", truename);
        params.addBodyParameter("idcard_url", idcard_url);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Intent intent = new Intent();
                    intent.setClass(CardDistinguishActivity.this, TCPublishSettingActivity.class);
                    startActivity(intent);
                    CardDistinguishActivity.this.finish();
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
