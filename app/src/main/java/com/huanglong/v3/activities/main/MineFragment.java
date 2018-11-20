package com.huanglong.v3.activities.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.homepage.PersonalPageActivity;
import com.huanglong.v3.activities.mine.ApplyProgramActivity;
import com.huanglong.v3.activities.mine.BalanceActivity;
import com.huanglong.v3.activities.mine.FeedbackActivity;
import com.huanglong.v3.activities.mine.FollowActivity;
import com.huanglong.v3.activities.mine.LevelActivity;
import com.huanglong.v3.activities.mine.MyCircleActivity;
import com.huanglong.v3.activities.mine.MyContactsActivity;
import com.huanglong.v3.activities.mine.MyFlexibleActivity;
import com.huanglong.v3.activities.mine.MyGiftActivity;
import com.huanglong.v3.activities.mine.MyKSongActivity;
import com.huanglong.v3.activities.mine.MyLiveActivity;
import com.huanglong.v3.activities.mine.MySocialCircleActivity;
import com.huanglong.v3.activities.mine.MyVFActivity;
import com.huanglong.v3.activities.mine.MyVideoActivity;
import com.huanglong.v3.activities.mine.SelfInfoActivity;
import com.huanglong.v3.activities.mine.SettingActivity;
import com.huanglong.v3.activities.mine.ThemeActivity;
import com.huanglong.v3.model.QRInfoBean;
import com.huanglong.v3.model.mine.UserInfoBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.Common;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.PromptDialog;
import com.huanglong.v3.utils.QRCode;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.view.QRCodeDialog;
import com.tencent.TIMConversationType;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by bin on 2018/1/11.
 * 我的
 */

public class MineFragment extends BaseFragment {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.title_back)
    private LinearLayout back;
    @ViewInject(R.id.mine_user_nickname)
    private TextView tv_nickname;
    @ViewInject(R.id.mine_user_avatar)
    private ImageView img_avatar;
    @ViewInject(R.id.mine_user_level)
    private TextView tv_level;

    private TextView tv_dialog_content;


    private UserInfoBean userInfoBean;

    private ImageView img_group_code;

    private QRCodeDialog qrCodeDialog;

    private PromptDialog dialog;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        return view;
    }

    @Override
    protected void initView() {
        tv_title.setText("我的");
        back.setVisibility(View.GONE);
        initDialog();
//        tv_nickname.setText(UserInfoUtils.getUserName());

    }


    @Override
    protected void logic() {
        requestUserInfo();

    }


    @Event(value = {R.id.mine_self_info, R.id.mine_setting, R.id.mine_live, R.id.mine_video, R.id.mine_social_circle, R.id.mine_activity,
            R.id.mine_sound, R.id.mine_k_song, R.id.mine_theme, R.id.mine_wallet, R.id.mine_contacts, R.id.mine_user_qr_code,
            R.id.mine_social_circle2, R.id.mine_feedback, R.id.mine_custom_service, R.id.mine_user_avatar, R.id.mine_user_nickname,
            R.id.mine_custom_follow, R.id.mine_custom_gift, R.id.mine_level, R.id.mine_small_program})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.mine_self_info:
                if (userInfoBean != null) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), SelfInfoActivity.class);
                    intent.putExtra("userInfoBean", userInfoBean);
                    startActivityForResult(intent, 1000);
                }
                break;
            case R.id.mine_setting:
                Intent intent = new Intent();
                intent.setClass(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.mine_live:
                Intent intent2 = new Intent();
                intent2.setClass(getActivity(), MyLiveActivity.class);
                startActivity(intent2);
                break;
            case R.id.mine_video:
                Intent intent3 = new Intent();
                intent3.setClass(getActivity(), MyVideoActivity.class);
                startActivity(intent3);
                break;
            case R.id.mine_social_circle:
                Intent intent9 = new Intent();
                intent9.setClass(getActivity(), MyCircleActivity.class);
                startActivity(intent9);
                break;
            case R.id.mine_activity:
                Intent intent4 = new Intent();
                intent4.setClass(getActivity(), MyFlexibleActivity.class);
                startActivity(intent4);
                break;
            case R.id.mine_sound:
                Intent intent5 = new Intent();
                intent5.setClass(getActivity(), MyVFActivity.class);
                startActivity(intent5);
                break;
            case R.id.mine_k_song:
                Intent intent6 = new Intent();
                intent6.setClass(getActivity(), MyKSongActivity.class);
                startActivity(intent6);
                break;
            case R.id.mine_theme:
                Intent intent7 = new Intent();
                intent7.setClass(getActivity(), ThemeActivity.class);
                startActivity(intent7);
                break;
            case R.id.mine_wallet:
                Intent intent8 = new Intent();
                intent8.setClass(getActivity(), BalanceActivity.class);
                startActivity(intent8);
                break;
            case R.id.mine_contacts:
                Intent intent10 = new Intent();
                intent10.setClass(getActivity(), MyContactsActivity.class);
                startActivity(intent10);
                break;
            case R.id.mine_user_qr_code:
                qrCodeDialog.show();
                img_group_code.setImageBitmap(QRCode.createQRCode(getActivity(), getQRCodeInfo()));
                break;
            case R.id.mine_social_circle2:
                Intent intent11 = new Intent();
                intent11.setClass(getActivity(), MySocialCircleActivity.class);
                startActivity(intent11);
                break;
            case R.id.mine_feedback:
                Intent intent12 = new Intent();
                intent12.setClass(getActivity(), FeedbackActivity.class);
                startActivity(intent12);
                break;
            case R.id.mine_custom_service:
                tv_dialog_content.setText("是否拨打客服电话\n" + Common.CUSTOME_SERVICE_PHONE);
                dialog.show();
                break;
            case R.id.mine_user_avatar:
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(), PersonalPageActivity.class);
                intent1.putExtra("uid", UserInfoUtils.getUid());
                startActivity(intent1);
                break;
            case R.id.mine_user_nickname:
                Intent intent13 = new Intent();
                intent13.setClass(getActivity(), PersonalPageActivity.class);
                intent13.putExtra("uid", UserInfoUtils.getUid());
                startActivity(intent13);
                break;
            case R.id.mine_custom_follow:
                Intent intent14 = new Intent();
                intent14.setClass(getActivity(), FollowActivity.class);
                startActivity(intent14);
                break;
            case R.id.mine_custom_gift:
                Intent intent15 = new Intent();
                intent15.setClass(getActivity(), MyGiftActivity.class);
                startActivity(intent15);
                break;
            case R.id.mine_level:
                Intent intent16 = new Intent();
                intent16.setClass(getActivity(), LevelActivity.class);
                startActivity(intent16);
                break;
            case R.id.mine_small_program:
                Intent intent17 = new Intent();
                intent17.setClass(getActivity(), ApplyProgramActivity.class);
                startActivity(intent17);
                break;
        }
    }

    /**
     * 初始化二维码code
     */
    private void initDialog() {

        dialog = new PromptDialog(getActivity(), R.layout.dialog_hint_currency);
        tv_dialog_content = (TextView) dialog.getView(R.id.dialog_comment_content);
        dialog.getView(R.id.dialog_comment_cancel).setOnClickListener(dialogClick);
        dialog.getView(R.id.dialog_comment_confirm).setOnClickListener(dialogClick);
        dialog.getView(R.id.dialog_comment_lin).setOnClickListener(dialogClick);


        qrCodeDialog = new QRCodeDialog(getActivity());
        img_group_code = (ImageView) qrCodeDialog.getView(R.id.dialog_qr_code_img);
    }

    /**
     * dialog点击事件
     */
    private View.OnClickListener dialogClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialog.dismiss();
            switch (view.getId()) {
                case R.id.dialog_comment_confirm:
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Common.CUSTOME_SERVICE_PHONE));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;

            }
        }
    };

    /**
     * 请求个人资料
     */
    private void requestUserInfo() {
        RequestParams params = MRequestParams.getUidParams(Api.getUserInfo);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    userInfoBean = gson.fromJson(json, UserInfoBean.class);
                    if (userInfoBean != null) {
                        tv_nickname.setText(userInfoBean.getNickname());
                        x.image().bind(img_avatar, userInfoBean.getHead_image(), MImageOptions.getCircularImageOptions());
                        tv_level.setText("V3号:" + userInfoBean.getId());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    requestUserInfo();
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (qrCodeDialog != null && qrCodeDialog.isShowing()) {
            qrCodeDialog.dismiss();
        }
    }


    /**
     * 获取二维码信息
     *
     * @return
     */
    private String getQRCodeInfo() {
        Gson gson = V3Application.getGson();
        QRInfoBean qrInfoBean = new QRInfoBean();
        qrInfoBean.setType(QRInfoBean.TYPE_CHAT);
        qrInfoBean.setChatId(UserInfoUtils.getUid());
        qrInfoBean.setChatTitle(UserInfoUtils.getNickName());
        qrInfoBean.setChatType(TIMConversationType.C2C);
        String qrInfo = gson.toJson(qrInfoBean).toString();
        return qrInfo;
    }
}
