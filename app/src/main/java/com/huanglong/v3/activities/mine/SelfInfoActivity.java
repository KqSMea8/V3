package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.huanglong.v3.adapter.PriceAdapter;
import com.huanglong.v3.im.presenter.FriendshipManagerPresenter;
import com.huanglong.v3.im.view.ListPickerDialog;
import com.huanglong.v3.model.home.JobClassBean;
import com.huanglong.v3.model.mine.UserInfoBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.Common;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.PromptDialog;
import com.huanglong.v3.utils.SelectPictureUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.tencent.TIMCallBack;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
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
 * Created by bin on 2018/3/27.
 * 个人信息页面
 */
@ContentView(R.layout.activity_self_info)
public class SelfInfoActivity extends BaseFragmentActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.self_info_avatar)
    private ImageView img_avatar;
    @ViewInject(R.id.self_info_nickname)
    private EditText edt_nickname;
    @ViewInject(R.id.self_info_real_name)
    private EditText edt_real_name;
    @ViewInject(R.id.self_info_id_card)
    private EditText edt_id_card;
    @ViewInject(R.id.self_info_back_number)
    private EditText edt_back_number;
    @ViewInject(R.id.self_info_open_back_name)
    private EditText edt_open_back_name;
    @ViewInject(R.id.self_info_branch_back_name)
    private EditText edt_branch_back_name;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;
    @ViewInject(R.id.self_info_friend_fee)
    private TextView tv_friend_fee;
    @ViewInject(R.id.self_info_mobile)
    private TextView tv_mobile;
    @ViewInject(R.id.self_info_address)
    private EditText edt_address;
    @ViewInject(R.id.self_info_media)
    private EditText edt_media;
    @ViewInject(R.id.self_info_notice)
    private EditText edt_notice;
    @ViewInject(R.id.self_info_job)
    private TextView tv_job;


    private UserInfoBean userInfoBean;

    private String avatarPath;
    private String avatarUrl;
    private String carrer_id;


    private PromptDialog promptDialog;
    private int friend_fee;
    private List<JobClassBean> jobClassBeans;
    private List<String> str_jobs = new ArrayList<>();


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("个人信息");
        tv_right.setText("保存");

        initDialog();
    }

    @Override
    protected void logic() {
        requestJobClass();

        Intent intent = getIntent();
        userInfoBean = (UserInfoBean) intent.getSerializableExtra("userInfoBean");
        if (userInfoBean != null) {
            showInfo(userInfoBean);
        }
    }

    /**
     * 显示用户信息
     *
     * @param userInfoBean
     */
    private void showInfo(UserInfoBean userInfoBean) {
        avatarUrl = userInfoBean.getHead_image();
        x.image().bind(img_avatar, avatarUrl, MImageOptions.getCircularImageOptions());
        edt_nickname.setText(userInfoBean.getNickname());
        edt_real_name.setText(userInfoBean.getReceive_name());
        edt_id_card.setText(userInfoBean.getId_card_no());
        edt_back_number.setText(userInfoBean.getReceive_account());
        edt_open_back_name.setText(userInfoBean.getReceive_bank());
        edt_branch_back_name.setText(userInfoBean.getReceive_branch_bank());
        tv_friend_fee.setText(userInfoBean.getFee() + "元");
        friend_fee = Integer.parseInt(userInfoBean.getFee());
        edt_media.setText(userInfoBean.getIntroduce());
        edt_address.setText(userInfoBean.getAddress());
        tv_job.setText(userInfoBean.getCareer_name());
        tv_mobile.setText(userInfoBean.getUsername());
        carrer_id = userInfoBean.getCareer_id();
        edt_notice.setText(userInfoBean.getNotice());

    }

    /**
     * 初始化dialog
     */
    private void initDialog() {

        String[] price = getResources().getStringArray(R.array.price_1);
        promptDialog = new PromptDialog(this, R.layout.dialog_price_layout);
        RecyclerView price_list = (RecyclerView) promptDialog.getView(R.id.dialog_price_list);
        TextView dialog_title = (TextView) promptDialog.getView(R.id.dialog_price_title);
        dialog_title.setText("请选择金额");

        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        price_list.setLayoutManager(layoutManager);

        PriceAdapter priceAdapter = new PriceAdapter();
        price_list.setAdapter(priceAdapter);
        priceAdapter.setData(price);

        priceAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                promptDialog.dismiss();
                String price = (String) obj;
                friend_fee = Integer.parseInt(price);
                tv_friend_fee.setText(friend_fee + "元");
            }
        });
        promptDialog.getView(R.id.dialog_price_lin).setOnClickListener(dialogClick);
    }

    /**
     * dialog 点击事件
     */
    private View.OnClickListener dialogClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            promptDialog.dismiss();
        }
    };

    @Event(value = {R.id.title_back, R.id.self_info_modify_avatar, R.id.title_tv_right, R.id.self_info_friend_fee_lin,
            R.id.self_info_job_lin})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                SelfInfoActivity.this.finish();
                break;
            case R.id.self_info_modify_avatar:
                SelectPictureUtils.selectPicture(SelfInfoActivity.this, false, 1, 0);
                break;
            case R.id.title_tv_right:
                requestSubmitInfo();
                break;
            case R.id.self_info_friend_fee_lin:
                promptDialog.show();
                break;
            case R.id.self_info_job_lin:
                if (str_jobs != null && str_jobs.size() > 0) {
                    String[] jobs = str_jobs.toArray(new String[str_jobs.size()]);
                    new ListPickerDialog().show(jobs, getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            carrer_id = jobClassBeans.get(which).getId();
                            tv_job.setText(jobs[which]);
                        }
                    });
                }

//                Intent intent = new Intent();
//                intent.setClass(getActivity(), EntClassActivity.class);
//                startActivityForResult(intent, 1000);
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
                            avatarPath = pathList.get(0);
                            x.image().bind(img_avatar, avatarPath, MImageOptions.getCircularImageOptions());
                        }
                    }
                    break;
//                case 1000:
//                    if (data == null) return;
//                    carrer_id = data.getStringExtra("class_id");
//                    String class_one_name = data.getStringExtra("class_one_name");
//                    String class_two_name = data.getStringExtra("class_two_name");
//                    tv_job.setText(class_one_name + "-" + class_two_name);
//                    break;
            }
        }
    }

    /**
     * 提交用户信息
     */
    private void requestSubmitInfo() {

        String nickname = edt_nickname.getText().toString().trim();
        if (TextUtils.isEmpty(nickname)) {
            ToastUtils.showToast("请输入昵称");
            return;
        }
        String real_name = edt_real_name.getText().toString().trim();
        if (TextUtils.isEmpty(real_name)) {
            ToastUtils.showToast("请输入真实姓名");
            return;
        }

        String id_card = edt_id_card.getText().toString().trim();
//        if (TextUtils.isEmpty(id_card)) {
//            ToastUtils.showToast("请输入身份证号");
//            return;
//        }
//
//        if (TextUtils.isEmpty(carrer_id)) {
//            ToastUtils.showToast("请选择职位");
//            return;
//        }

        String back_number = TextUtils.isEmpty(edt_back_number.getText().toString().trim()) ? "" : edt_back_number.getText().toString().trim();
        String open_back_name = TextUtils.isEmpty(edt_open_back_name.getText().toString().trim()) ? "" : edt_open_back_name.getText().toString().trim();
        String branch_back_name = TextUtils.isEmpty(edt_branch_back_name.getText().toString().trim()) ? "" : edt_branch_back_name.getText().toString().trim();

        String address = TextUtils.isEmpty(edt_address.getText().toString().trim()) ? "" : edt_address.getText().toString().trim();
        String introduce = TextUtils.isEmpty(edt_media.getText().toString().trim()) ? "" : edt_media.getText().toString().trim();
        String notice = TextUtils.isEmpty(edt_notice.getText().toString().trim()) ? "" : edt_notice.getText().toString().trim();


        showDialog();
        final UserInfo userInfo = new UserInfo();
        userInfo.uid = UserInfoUtils.getUid();
        userInfo.nickname = nickname;
        userInfo.receive_name = real_name;
        userInfo.id_card_no = id_card;
        userInfo.fee = friend_fee + "";
        userInfo.receive_account = back_number;
        userInfo.receive_bank = open_back_name;
        userInfo.receive_branch_bank = branch_back_name;
        userInfo.introduce = introduce;
        userInfo.notice = notice;
        userInfo.bank_id = "0";
        userInfo.carrer_id = carrer_id;
        userInfo.address = address;


        Gson gson = V3Application.getGson();
        String json = gson.toJson(userInfo);
        LogUtil.e("---json:" + json);
        RequestParams params = MRequestParams.getNoTokenParams(Api.updateUserInfo);
        params.setMultipart(true);
        if (!TextUtils.isEmpty(avatarPath)) {
            params.addBodyParameter("imglist", new File(avatarPath));
        }
        params.addBodyParameter("userJson", json);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    UserInfoBean userInfoBean = gson.fromJson(json, UserInfoBean.class);
                    if (userInfoBean != null) {
                        ToastUtils.showToast("修改成功");
                        setIMUserInfo(userInfo);
                        setResult(RESULT_OK);
                        SelfInfoActivity.this.finish();
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
                dismissDialog();
            }
        });

    }

    /**
     * 设置腾讯云上的用户信息
     */
    private void setIMUserInfo(UserInfo userInfo) {

        avatarUrl = userInfoBean.getHead_image();
        if (!TextUtils.isEmpty(avatarUrl)) {
            FriendshipManagerPresenter.setMyUserHeadPic(avatarUrl, new TIMCallBack() {
                @Override
                public void onError(int i, String s) {
                    ToastUtils.showToast("头像设置失败:" + s);
                }

                @Override
                public void onSuccess() {
                    ToastUtils.showToast("头像设置成功");
                }
            });
        }
        String nickname = userInfoBean.getNickname();
        if (!TextUtils.isEmpty(nickname)) {
            FriendshipManagerPresenter.setMyNick(userInfo.nickname, new TIMCallBack() {
                @Override
                public void onError(int i, String s) {
                    ToastUtils.showToast("昵称设置失败:" + s);
                }

                @Override
                public void onSuccess() {
                    ToastUtils.showToast("昵称设置成功");
                }
            });

        }

//        FriendshipManagerPresenter.getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
//            @Override
//            public void onError(int i, String s) {
//
//            }
//
//            @Override
//            public void onSuccess(TIMUserProfile timUserProfile) {
//                String faceUrl = timUserProfile.getFaceUrl();
//                LogUtil.e("------avatar:" + faceUrl);
//            }
//        });

    }


    /**
     * 请求职业分类
     */
    private void requestJobClass() {

        RequestParams params = MRequestParams.getNoTokenParams(Api.job_class);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    jobClassBeans = gson.fromJson(json, new TypeToken<LinkedList<JobClassBean>>() {
                    }.getType());
                    if (jobClassBeans != null && jobClassBeans.size() > 0) {
                        str_jobs.clear();
                        for (JobClassBean jobClassBean : jobClassBeans) {
                            str_jobs.add(jobClassBean.getName());
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


    private class UserInfo {
        public String uid;
        public String nickname;
        public String receive_name;
        public String id_card_no;
        public String fee;
        public String receive_account;
        public String receive_bank;
        public String bank_id;
        public String receive_branch_bank;
        public String mobile;
        public String introduce;
        public String notice;
        public String carrer_id;
        public String address;
    }

}
