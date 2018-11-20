package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.homepage.ProgramActivity;
import com.huanglong.v3.im.view.ListPickerDialog;
import com.huanglong.v3.model.circle.RelImageBean;
import com.huanglong.v3.model.home.JobClassBean;
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
 * Created by bin on 2018/10/9.
 * 申请小程序页面
 */
@ContentView(R.layout.activity_apply_program)
public class ApplyProgramActivity extends BaseFragmentActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.apply_program_company_name)
    private EditText edt_company_name;
    @ViewInject(R.id.apply_program_contacts)
    private EditText edt_contacts;
    @ViewInject(R.id.apply_program_mobile)
    private EditText edt_mobile;
    @ViewInject(R.id.apply_program_type)
    private RadioGroup program_type;
    @ViewInject(R.id.apply_program_avatar)
    private ImageView iv_avatar;
    @ViewInject(R.id.apply_program_class)
    private TextView tv_class;
    @ViewInject(R.id.apply_program_name)
    private EditText edt_program_name;


    private int programType = 0;

    private String avatarPath;
    private String imgUrls;

    private List<LiveClassBean> liveClassBeans;
    private List<String> str_class = new ArrayList<>();
    private String class_id;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("申请小程序");

        program_type.check(R.id.apply_program_type_wechat);


    }

    @Override
    protected void logic() {
        requestProgramClass();
        program_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.apply_program_type_wechat) {
                    programType = 0;
                } else {
                    programType = 1;
                }
            }
        });

    }

    @Event(value = {R.id.title_back, R.id.apply_program_submit, R.id.apply_program_modify_avatar, R.id.apply_program_class_lin})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.apply_program_submit:
                uploadImg();
                break;
            case R.id.apply_program_modify_avatar:
                SelectPictureUtils.selectPicture(ApplyProgramActivity.this, false, 1, 0);
                break;
            case R.id.apply_program_class_lin:
                if (str_class != null && str_class.size() > 0) {
                    String[] jobs = str_class.toArray(new String[str_class.size()]);
                    new ListPickerDialog().show(jobs, getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            class_id = liveClassBeans.get(which).getId();
                            tv_class.setText(jobs[which]);
                        }
                    });
                }
                break;
        }
    }


    /**
     * 上传封面图片
     */
    private void uploadImg() {
        if (TextUtils.isEmpty(avatarPath)) {
            ToastUtils.showToast("请选择小程序头像");
            return;
        }
        showDialog();
        RequestParams params = MRequestParams.getNoTokenParams(Api.uploadimg);
        params.setMultipart(true);
        params.addBodyParameter("imglist", new File(avatarPath));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    RelImageBean relImageBean = gson.fromJson(json, RelImageBean.class);
                    if (relImageBean != null) {
                        imgUrls = relImageBean.getUrl();
                        requestSubmit();
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
     * 提交申请小程序
     */
    private void requestSubmit() {

        String company_name = edt_company_name.getText().toString().trim();
        if (TextUtils.isEmpty(company_name)) {
            ToastUtils.showToast("请输入公司名称");
            return;
        }

        String contacts = edt_contacts.getText().toString().trim();
        if (TextUtils.isEmpty(contacts)) {
            ToastUtils.showToast("请输入联系人");
            return;
        }

        String mobile = edt_mobile.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.showToast("请输入联系方式");
            return;
        }

        String program_name = edt_program_name.getText().toString().trim();
        if (TextUtils.isEmpty(program_name)) {
            ToastUtils.showToast("请输入小程序名称");
            return;
        }

        if (TextUtils.isEmpty(class_id)) {
            ToastUtils.showToast("请选择分类");
            return;
        }


        RequestParams params = MRequestParams.getNoTokenParams(Api.user_apply_program);
        params.addBodyParameter("c_name", company_name);
        params.addBodyParameter("linkman", contacts);
        params.addBodyParameter("phone", mobile);
        params.addBodyParameter("logo", imgUrls);
        params.addBodyParameter("user_id", UserInfoUtils.getUid());
        params.addBodyParameter("category_id", class_id);
        params.addBodyParameter("type", programType + "");
        params.addBodyParameter("name", program_name);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("申请成功");
                    Intent intent7 = new Intent();
                    intent7.setClass(getActivity(), ProgramActivity.class);
                    startActivity(intent7);
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

    /**
     * 请求小程序分类
     */
    private void requestProgramClass() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_wx_cate_list);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    liveClassBeans = gson.fromJson(json, new TypeToken<LinkedList<LiveClassBean>>() {
                    }.getType());
                    if (liveClassBeans != null && liveClassBeans.size() > 0) {
                        str_class.clear();
                        for (LiveClassBean liveClassBean : liveClassBeans) {
                            str_class.add(liveClassBean.getName());
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
                case Common.IMAGE_PICKER:
                    if (data != null) {
                        List<String> pathList = data.getStringArrayListExtra("result");
                        if (pathList != null && pathList.size() > 0) {
                            avatarPath = pathList.get(0);
                            x.image().bind(iv_avatar, avatarPath, MImageOptions.getCircularImageOptions());
                        }
                    }
                    break;
            }
        }
    }
}
