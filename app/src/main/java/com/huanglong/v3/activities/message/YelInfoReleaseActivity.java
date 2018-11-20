package com.huanglong.v3.activities.message;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.im.view.ListPickerDialog;
import com.huanglong.v3.model.homepage.LiveClassBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.Common;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.SelectPictureUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/5/16.
 * 黄页信息发布页面
 */
@ContentView(R.layout.activity_yel_release)
public class YelInfoReleaseActivity extends BaseFragmentActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.yel_release_cover)
    private ImageView img_cover;
    @ViewInject(R.id.yel_release_class)
    private TextView tv_class;
    @ViewInject(R.id.yel_release_title)
    private EditText edt_title;
    @ViewInject(R.id.yel_release_content)
    private EditText edt_content;


    private String coverPath;


    private List<String> selClass = new ArrayList<>();
    private List<LiveClassBean> liveClassBeans;
    private String classId;
    private String imgUrls;
    private String str_title;
    private String str_content;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("发布信息");
    }

    @Override
    protected void logic() {
        requestInfoClass();
    }

    @Event(value = {R.id.title_back, R.id.yel_release_cover, R.id.yel_release_class_lin, R.id.yel_release_submit})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.yel_release_cover:
                SelectPictureUtils.selectChatImg(YelInfoReleaseActivity.this);
                break;
            case R.id.yel_release_class_lin:
                String[] groups = selClass.toArray(new String[selClass.size()]);
                new ListPickerDialog().show(groups, getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_class.setText(groups[which]);
                        classId = liveClassBeans.get(which).getId();
                    }
                });
                break;
            case R.id.yel_release_submit:
                if (validateInfo()) {
                    requestSubmitInfo();
                }
                break;
        }
    }

    /**
     * 验证输入信息
     *
     * @return
     */
    private boolean validateInfo() {
        if (TextUtils.isEmpty(classId)) {
            ToastUtils.showToast("请选择分类");
            return false;
        }

        str_title = edt_title.getText().toString().trim();
        if (TextUtils.isEmpty(str_title)) {
            ToastUtils.showToast("请输入标题");
            return false;
        }

        str_content = edt_content.getText().toString().trim();
        if (TextUtils.isEmpty(str_content)) {
            ToastUtils.showToast("请输入标题");
            return false;
        }

        if (TextUtils.isEmpty(coverPath)) {
            ToastUtils.showToast("请输入封面图片");
            return false;
        }


        return true;
    }


    /**
     * 黄页信息分类
     */
    private void requestInfoClass() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.huangye_huangye_cate_list);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    liveClassBeans = gson.fromJson(json, new TypeToken<LinkedList<LiveClassBean>>() {
                    }.getType());
                    if (liveClassBeans != null && liveClassBeans.size() > 0) {
                        selClass.clear();
                        for (LiveClassBean liveClassBean : liveClassBeans) {
                            selClass.add(liveClassBean.getName());
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Common.IMAGE_PICKER://PictureConfig.CHOOSE_REQUEST:

                    if (data != null) {
                        List<String> pathList = data.getStringArrayListExtra("result");
                        if (pathList != null) {
                            coverPath = pathList.get(0);
                            x.image().bind(img_cover, coverPath, MImageOptions.getNormalImageOptions());
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 发布黄页
     */
    private void requestSubmitInfo() {
        showDialog();
        RequestParams params = MRequestParams.getNoTokenParams(Api.huangye_publish);
        params.setMultipart(true);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("imglist", new File(coverPath));
        params.addBodyParameter("category_id", classId);
        params.addBodyParameter("title", str_title);
        params.addBodyParameter("content", str_content);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("发布成功");
                    setResult(RESULT_OK);
                    finish();

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
