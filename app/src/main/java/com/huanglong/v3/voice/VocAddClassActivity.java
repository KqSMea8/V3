package com.huanglong.v3.voice;

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
import com.huanglong.v3.model.circle.RelImageBean;
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
 * Created by bin on 2018/5/3.
 * 音频添加分类
 */
@ContentView(R.layout.activity_voc_add_class)
public class VocAddClassActivity extends BaseFragmentActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.voc_add_class_cover)
    private ImageView img_cover;
    @ViewInject(R.id.voc_add_class_title)
    private EditText edt_title;
    @ViewInject(R.id.voc_add_class_tv)
    private TextView tv_class;

    private String imgUrls;
    private String coverPath;
    private String str_voc_title;
    private String class_id = "";

    private List<LiveClassBean> liveClassBeans;
    private List<String> classes = new ArrayList<>();


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("添加分类");
    }

    @Override
    protected void logic() {
        requestVocClass();
    }

    @Event(value = {R.id.title_back, R.id.voc_add_class_submit, R.id.voc_add_class_cover, R.id.voc_add_class_lin})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.voc_add_class_submit:
                if (validateInfo()) {
                    requestUploadImage();
                }
                break;
            case R.id.voc_add_class_cover:
                SelectPictureUtils.selectedIdCard(this, 700, 438);
                break;
            case R.id.voc_add_class_lin:
                if (classes != null && classes.size() > 0) {
                    String[] str_class = classes.toArray(new String[classes.size()]);
                    new ListPickerDialog().show(str_class, getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String str_cla = str_class[which];
                            tv_class.setText(str_cla);
                            for (LiveClassBean liveClassBean : liveClassBeans) {
                                String name = liveClassBean.getName();
                                if (TextUtils.equals(str_cla, name)) {
                                    class_id = liveClassBean.getId();
                                    break;
                                }
                            }
                        }
                    });
                }
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
                        coverPath = pathList.get(0);
                        x.image().bind(img_cover, coverPath, MImageOptions.getNormalImageNotCropOptions());
                    }
                    break;
            }
        }
    }

    /**
     * 验证信息
     *
     * @return
     */
    private boolean validateInfo() {
        if (TextUtils.isEmpty(coverPath)) {
            ToastUtils.showToast("请选择封面");
            return false;
        }
        str_voc_title = edt_title.getText().toString().trim();

        if (TextUtils.isEmpty(str_voc_title)) {
            ToastUtils.showToast("请输入标题");
            return false;
        }

        if (TextUtils.isEmpty(class_id)) {
            ToastUtils.showToast("请选择分类");
            return false;
        }
        return true;
    }


    /**
     * 请求分类
     */
    private void requestVocClass() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.book_cate_list);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    liveClassBeans = gson.fromJson(json, new TypeToken<LinkedList<LiveClassBean>>() {
                    }.getType());
                    if (liveClassBeans != null && liveClassBeans.size() > 0) {
                        classes.clear();
                        for (LiveClassBean liveClassBean : liveClassBeans) {
                            classes.add(liveClassBean.getName());
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

    /**
     * 上传图片
     */
    private void requestUploadImage() {
        showDialog();
        RequestParams params = MRequestParams.getNoTokenParams(Api.uploadimg);
        params.setMultipart(true);
        params.addBodyParameter("imglist", new File(coverPath));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    RelImageBean relImageBean = gson.fromJson(json, RelImageBean.class);
                    if (relImageBean != null) {
                        imgUrls = relImageBean.getUrl();
                        requestSubmitInfo();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dismissDialog();
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
     */
    private void requestSubmitInfo() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.book_add_category);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("title", str_voc_title);
        params.addBodyParameter("cover_img", imgUrls);
        params.addBodyParameter("category_id", class_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("添加成功");
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
