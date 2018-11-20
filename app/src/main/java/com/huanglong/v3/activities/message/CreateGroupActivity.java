package com.huanglong.v3.activities.message;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.conversation.ChatActivity;
import com.huanglong.v3.im.model.GroupInfo;
import com.huanglong.v3.im.presenter.GroupManagerPresenter;
import com.huanglong.v3.model.circle.RelImageBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.Common;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.SelectPictureUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.tencent.TIMConversationType;
import com.tencent.TIMValueCallBack;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * Created by bin on 2018/3/16.
 * 创建群
 */
@ContentView(R.layout.activity_create_group)
public class CreateGroupActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.create_group_name)
    private EditText group_name;
    @ViewInject(R.id.create_group_avatar)
    private ImageView group_avatar;
    @ViewInject(R.id.create_group_is_free)
    private Switch seek_is_free;
    @ViewInject(R.id.create_group_price_line)
    private View price_line;
    @ViewInject(R.id.create_group_price)
    private EditText edt_price;

    private List<String> identifiers;
    private String faceUrl = "";

    private String facePath = "";

    private int is_free = 0;
    private String price = "0";


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("创建群");
    }

    @Override
    protected void logic() {

        Intent intent = getIntent();
        identifiers = (List<String>) intent.getSerializableExtra("identifiers");

        seek_is_free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    is_free = 1;
                    price_line.setVisibility(View.VISIBLE);
                    edt_price.setVisibility(View.VISIBLE);
                } else {
                    price = "0";
                    is_free = 0;
                    price_line.setVisibility(View.GONE);
                    edt_price.setVisibility(View.GONE);
                }
            }
        });


    }

    @Event(value = {R.id.title_back, R.id.create_group_submit, R.id.create_group_avatar})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                CreateGroupActivity.this.finish();
                break;
            case R.id.create_group_avatar:
                SelectPictureUtils.selectPicture(CreateGroupActivity.this, false, 1, 300);
                break;
            case R.id.create_group_submit:
                if (!TextUtils.isEmpty(facePath)) {
                    uploadFace();
                } else {
                    createGroup(faceUrl);
                }
                break;

        }
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
                            facePath = pathList.get(0);
                            x.image().bind(group_avatar, facePath, MImageOptions.getCircularImageOptions());
                        }
                    }
                    break;
            }
        }

    }

    /**
     * 上传头像
     */
    private void uploadFace() {
        showDialog();
        RequestParams params = MRequestParams.getNoTokenParams(Api.uploadimg);
        params.setMultipart(true);
        params.addBodyParameter("imglist", new File(facePath));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    RelImageBean relImageBean = gson.fromJson(json, RelImageBean.class);
                    if (relImageBean != null) {
                        faceUrl = relImageBean.getUrl();
                    }
                    createGroup(faceUrl);
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


    //创建群
    private void createGroup(String faceUrl) {
        showDialog();
        String groupName = group_name.getText().toString().trim();
        if (TextUtils.isEmpty(groupName)) {
            ToastUtils.showToast("请输入群名称");
            return;
        }
        if (is_free == 1) {
            price = edt_price.getText().toString().trim();
            if (TextUtils.isEmpty(price)) {
                ToastUtils.showToast("请输入收费金额");
                return;
            }
        }

        GroupManagerPresenter.createGroup(faceUrl, groupName, is_free, price, GroupInfo.publicGroup, identifiers, new TIMValueCallBack<String>() {
            @Override
            public void onError(int code, String desc) {
                dismissDialog();
                LogUtil.e("create group failed. code: " + code + " errmsg: " + desc);
                ToastUtils.showToast("创建失败");
            }

            @Override
            public void onSuccess(String s) {
                dismissDialog();
                ChatActivity.navToChat(CreateGroupActivity.this, s, TIMConversationType.Group, groupName);
                CreateGroupActivity.this.finish();
                if (FriendsActivity.instance != null) {
                    FriendsActivity.instance.finish();
                }
            }
        });

    }

}
