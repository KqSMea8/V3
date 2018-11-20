package com.huanglong.v3.conversation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.homepage.PersonalPageActivity;
import com.huanglong.v3.activities.message.FriendsActivity;
import com.huanglong.v3.im.adapter.GroupMemberAdapter;
import com.huanglong.v3.im.contacts.EditActivity;
import com.huanglong.v3.im.model.CustomMessage;
import com.huanglong.v3.im.model.GroupInfo;
import com.huanglong.v3.im.model.GroupMemberProfile;
import com.huanglong.v3.im.model.Message;
import com.huanglong.v3.im.model.ProfileSummary;
import com.huanglong.v3.im.model.TextMessage;
import com.huanglong.v3.im.presenter.ChatPresenter;
import com.huanglong.v3.im.presenter.GroupManagerPresenter;
import com.huanglong.v3.im.viewfeatures.ChatView;
import com.huanglong.v3.im.viewfeatures.GroupManageView;
import com.huanglong.v3.model.QRInfoBean;
import com.huanglong.v3.model.WechatPayBean;
import com.huanglong.v3.model.circle.RelImageBean;
import com.huanglong.v3.model.contacts.GroupIdUtils;
import com.huanglong.v3.model.contacts.GroupVerBean;
import com.huanglong.v3.model.contacts.InviteCustomBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.Common;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.PromptDialog;
import com.huanglong.v3.utils.QRCode;
import com.huanglong.v3.utils.SelectPictureUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.utils.WXUtils;
import com.huanglong.v3.view.QRCodeDialog;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberRoleType;
import com.tencent.TIMGroupReceiveMessageOpt;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageDraft;
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
import java.util.List;

/**
 * Created by bin on 2018/4/16.
 * 群详情
 */
@ContentView(R.layout.activity_group_details)
public class GroupDetailsActivity extends BaseActivity implements EditActivity.EditInterface, GroupManageView, View.OnClickListener {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.group_details_member_list)
    private RecyclerView member_list;
    @ViewInject(R.id.group_details_name)
    private TextView tv_group_name;
    @ViewInject(R.id.group_details_avatar)
    private ImageView img_group_avatar;
    @ViewInject(R.id.group_details_send)
    private Button btn_send;
    @ViewInject(R.id.group_details_sign_out)
    private Button btn_sign_out;
    @ViewInject(R.id.group_details_msg_disturb)
    private Switch msg_disturb;
    @ViewInject(R.id.group_details_msg_disturb_lin)
    private LinearLayout msg_disturb_lin;
    @ViewInject(R.id.group_details_qr_code)
    private RelativeLayout qr_code_rel;


    private ImageView img_group_code;

    private String groupId;
    private String getGroupName;

    private List<ProfileSummary> members = new ArrayList<>();

    private GroupMemberAdapter groupMemberAdapter;

    private boolean inGroup = true;
    private TIMGroupMemberRoleType role;

    private final int MODIFY_GROUP_NAME = 1000;

    private TextView tv_dialog_content;

    private PromptDialog promptDialog;
    private int dialogType;//1.支付提示 2.解散群提示 3.退出群提示

    private String price = "0";

    private WechatPayBean wechatPayBean;

    private WeChatBroadcastReceiver weChatBroadcastReceiver;

    private QRCodeDialog qrCodeDialog;
    private String overId;
    private String faceUrl = "";

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("群资料");

        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        member_list.setLayoutManager(layoutManager);
        groupMemberAdapter = new GroupMemberAdapter();
        member_list.setAdapter(groupMemberAdapter);
        initDialogClick();
        initDialog();
        weChatBroadcastReceiver = new WeChatBroadcastReceiver();

    }

    @Override
    protected void logic() {
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        getGroupName = intent.getStringExtra("getGroupName");

        inGroup = GroupInfo.getInstance().isInGroup(groupId);
        if (inGroup) {
            queryInGroupInfo();
            btn_sign_out.setVisibility(View.VISIBLE);
            btn_send.setText("发送消息");
            role = GroupInfo.getInstance().getRole(groupId);
            if (role == TIMGroupMemberRoleType.Owner) {
                btn_sign_out.setText("解散该群");
            } else {
                btn_sign_out.setText("退出该群");
            }
            msg_disturb_lin.setVisibility(View.VISIBLE);
            qr_code_rel.setVisibility(View.VISIBLE);
        } else {
            queryNotGroupInfo();
            btn_sign_out.setVisibility(View.GONE);
            msg_disturb_lin.setVisibility(View.GONE);
            qr_code_rel.setVisibility(View.GONE);
            btn_send.setText("加入该群");
        }

        boolean b = UserInfoUtils.getGroupMsgStatus(groupId);
        msg_disturb.setChecked(b);
        registerReceiver();

        msg_disturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                UserInfoUtils.saveGroupMsgStatus(groupId, b);
                setMsgDisturb(b);
            }
        });


        groupMemberAdapter.setItemOnClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                ProfileSummary profileSummary = (ProfileSummary) obj;
                if (type == 1) {//加人
                    Intent intent = new Intent();
                    intent.setClass(GroupDetailsActivity.this, FriendsActivity.class);
                    intent.putExtra("flag", 2);
                    startActivityForResult(intent, 1000);
                } else if (type == 2) {//踢人
                    Intent intent1 = new Intent();
                    intent1.setClass(GroupDetailsActivity.this, GroupMemberActivity.class);
                    intent1.putExtra("groupId", groupId);
                    startActivityForResult(intent1, 1002);
                } else {
                    Intent intent1 = new Intent();
                    intent1.setClass(GroupDetailsActivity.this, PersonalPageActivity.class);
                    intent1.putExtra("uid", profileSummary.getIdentify());
                    startActivity(intent1);
                }
            }

            @Override
            public void onItemViewClick(Object obj, int position, int type, View view) {

            }
        });

    }

    /**
     * 设置消息免打扰
     *
     * @param isDisturb
     */
    private void setMsgDisturb(boolean isDisturb) {
        TIMGroupReceiveMessageOpt receiveAndNotify;
        if (isDisturb) {
            receiveAndNotify = TIMGroupReceiveMessageOpt.ReceiveAndNotify;
        } else {
            receiveAndNotify = TIMGroupReceiveMessageOpt.ReceiveNotNotify;
        }

        GroupManagerPresenter.setMsgDisturb(groupId, receiveAndNotify, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                LogUtil.i("MsgDisturb: code" + i + " msg:" + s);
            }

            @Override
            public void onSuccess() {
                LogUtil.i("MsgDisturb: success");
            }
        });
    }

    /**
     * 初始化设置dialog
     */
    private void initDialogClick() {
        promptDialog = new PromptDialog(GroupDetailsActivity.this, R.layout.dialog_hint_currency);

        promptDialog.getView(R.id.dialog_comment_lin).setOnClickListener(this);
        tv_dialog_content = (TextView) promptDialog.getView(R.id.dialog_comment_content);
        promptDialog.getView(R.id.dialog_comment_cancel).setOnClickListener(this);
        promptDialog.getView(R.id.dialog_comment_confirm).setOnClickListener(this);

    }

    /**
     * 初始化二维码code
     */
    private void initDialog() {
        qrCodeDialog = new QRCodeDialog(this);
        img_group_code = (ImageView) qrCodeDialog.getView(R.id.dialog_qr_code_img);
    }

    /**
     * 注册微信分享广播
     */

    private void registerReceiver() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.PAY_WECHAT_ACTION);
        GroupDetailsActivity.this.registerReceiver(weChatBroadcastReceiver, filter);
    }

    /**
     * 微信支付成功后接收广播处理
     *
     * @author hbb
     */
    private class WeChatBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Common.PAY_WECHAT_ACTION)) return;
            String type = intent.getStringExtra(Common.PAY_WECHAT_KEY);
            if (TextUtils.equals("success", type)) {
                applyJoinGroup();
            } else if (TextUtils.equals("cancel", type)) {
                ToastUtils.showToast("取消支付");
            } else {
                ToastUtils.showToast("支付失败");
            }
        }
    }


    /**
     * 查询在用户在这个群里面的群信息
     */
    private void queryInGroupInfo() {
        GroupManagerPresenter.getOneGroupDetailInfo(groupId, new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("group details error-->code:" + i + " msg:" + s);
            }

            @Override
            public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                if (timGroupDetailInfos == null || timGroupDetailInfos.size() == 0) return;
                TIMGroupDetailInfo timGroupDetailInfo = timGroupDetailInfos.get(0);
                tv_group_name.setText(timGroupDetailInfo.getGroupName());
                faceUrl = timGroupDetailInfo.getFaceUrl();
                x.image().bind(img_group_avatar, timGroupDetailInfo.getFaceUrl(), MImageOptions.getCircularImageOptions());
            }
        });
        getGroupMember();
    }

    /**
     * 获取群成员
     */
    private void getGroupMember() {
        GroupManagerPresenter.getGroupMembers(groupId, new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("group member error-->code:" + i + " msg:" + s);
            }

            @Override
            public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
                if (timGroupMemberInfos == null) return;
                if (members != null) {
                    members.clear();
                }
                for (TIMGroupMemberInfo item : timGroupMemberInfos) {
                    GroupMemberProfile groupMemberProfile = new GroupMemberProfile(item);
                    TIMGroupMemberRoleType role = item.getRole();
                    if (role == TIMGroupMemberRoleType.Owner) {
                        overId = groupMemberProfile.getIdentify();
                        members.add(0, groupMemberProfile);
                    } else {
                        members.add(groupMemberProfile);
                    }
                }

                if (TextUtils.equals(UserInfoUtils.getUid(), overId)) {
                    TIMGroupMemberInfo addItem = new TIMGroupMemberInfo();
                    addItem.setUser("add");
                    GroupMemberProfile addProfile = new GroupMemberProfile(addItem);
                    members.add(addProfile);

                    TIMGroupMemberInfo reduceItem = new TIMGroupMemberInfo();
                    reduceItem.setUser("reduce");
                    GroupMemberProfile reduceProfile = new GroupMemberProfile(reduceItem);
                    members.add(reduceProfile);

                }
                groupMemberAdapter.setData(members);
            }
        });
    }

    /**
     * 查询在用户不在这个群里面的群信息
     */
    private void queryNotGroupInfo() {
        GroupManagerPresenter.getGroupPublicInfo(groupId, new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("group details error-->code:" + i + " msg:" + s);
            }

            @Override
            public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                if (timGroupDetailInfos == null || timGroupDetailInfos.size() == 0) return;
                tv_group_name.setText(timGroupDetailInfos.get(0).getGroupName());
                x.image().bind(img_group_avatar, timGroupDetailInfos.get(0).getFaceUrl(), MImageOptions.getCircularImageOptions());
            }
        });
    }

    @Event(value = {R.id.title_back, R.id.group_details_send, R.id.group_details_sign_out, R.id.group_details_modify_name, R.id.group_details_modify_avatar
            , R.id.group_details_qr_code, R.id.group_details_space})
    private void mOnClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                GroupDetailsActivity.this.finish();
                break;
            case R.id.group_details_send:
                if (inGroup) {
                    ChatActivity.navToChat(GroupDetailsActivity.this, groupId, TIMConversationType.Group, getGroupName);
                    GroupDetailsActivity.this.finish();
                } else {
                    requestApply();
                }
                break;
            case R.id.group_details_sign_out:
                if (role == TIMGroupMemberRoleType.Owner) {
                    dialogType = 2;
                    tv_dialog_content.setText("是否解散改群组？");
                    promptDialog.show();

                } else {
                    dialogType = 3;
                    tv_dialog_content.setText("是否退出改群组？");
                    promptDialog.show();
                }
                break;
            case R.id.group_details_modify_name:
                if (inGroup) {
                    EditActivity.navToEdit(GroupDetailsActivity.this, "修改群名称", tv_group_name.getText().toString(), MODIFY_GROUP_NAME, this, 30);
                }
                break;
            case R.id.group_details_modify_avatar:
                if (TextUtils.equals(overId, UserInfoUtils.getUid())) {
                    SelectPictureUtils.selectPicture(GroupDetailsActivity.this, false, 1, 300);
                } else {
                    ToastUtils.showToast("只能群主才能修改群头像");
                }
                break;
            case R.id.group_details_qr_code:
                qrCodeDialog.show();
                img_group_code.setImageBitmap(QRCode.createQRCode(GroupDetailsActivity.this, getQRCodeInfo()));
                break;
            case R.id.group_details_space:
                if (!TextUtils.isEmpty(overId)) {
                    Intent intent = new Intent();
                    intent.setClass(GroupDetailsActivity.this, PersonalPageActivity.class);
                    intent.putExtra("uid", overId);
                    startActivity(intent);
                }
                break;
        }
    }

    /**
     * 解散群
     */
    private void dismissGroup() {
        GroupManagerPresenter.dismissGroup(groupId, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("dismissGroup error code:" + i + " msg:" + s);
            }

            @Override
            public void onSuccess() {
                ToastUtils.showToast("群解散成功");
                GroupDetailsActivity.this.finish();
            }
        });
    }

    /**
     * 退出群
     */
    private void quitGroup() {
        GroupManagerPresenter.quitGroup(groupId, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("quitGroup error code:" + i + " msg:" + s);
            }

            @Override
            public void onSuccess() {
                ToastUtils.showToast("群退出成功");
                GroupDetailsActivity.this.finish();
            }
        });
    }


    @Override
    public void onEdit(String text, TIMCallBack callBack) {
        tv_group_name.setText(text);
        GroupManagerPresenter.modifyGroupName(groupId, text, callBack);
    }

    /**
     * 加群验证
     */
    private void requestApply() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.judy_group);
        params.addBodyParameter("group_id", GroupIdUtils.deelGroupId(groupId));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    GroupVerBean groupVerBean = gson.fromJson(json, GroupVerBean.class);
                    if (groupVerBean != null) {
                        int is_fee = groupVerBean.getIs_fee();
                        if (is_fee == 1) {
                            dialogType = 1;
                            price = groupVerBean.getPrice();
                            tv_dialog_content.setText("添加该好友需支付" + groupVerBean.getPrice() + "元，是否添加？");
                            promptDialog.show();
                        } else {
                            applyJoinGroup();
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
     * 申请接入群
     */
    private void applyJoinGroup() {
        GroupManagerPresenter.applyJoinGroup(groupId, "", new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("applyJoinGroup error code:" + i + " msg:" + s);
            }

            @Override
            public void onSuccess() {
                ChatActivity.navToChat(GroupDetailsActivity.this, groupId, TIMConversationType.Group, getGroupName);
                GroupDetailsActivity.this.finish();
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_comment_lin:
                promptDialog.dismiss();
                break;
            case R.id.dialog_comment_cancel:
                promptDialog.dismiss();
                break;
            case R.id.dialog_comment_confirm:
                promptDialog.dismiss();
                if (dialogType == 1) {
                    requestPay();
                } else if (dialogType == 2) {
                    dismissGroup();
                } else if (dialogType == 3) {
                    quitGroup();
                }
                break;
        }
    }

    /**
     * 加群支付
     */
    private void requestPay() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.pay);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("pay_type", "1");
        params.addBodyParameter("pay_amount", price);
        params.addBodyParameter("type", "0");
        params.addBodyParameter("group_id", GroupIdUtils.deelGroupId(groupId));

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    wechatPayBean = gson.fromJson(json, WechatPayBean.class);
                    if (wechatPayBean != null) {
                        WXUtils.wxPay(GroupDetailsActivity.this, wechatPayBean);
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
    protected void onDestroy() {
        super.onDestroy();
        if (weChatBroadcastReceiver != null) {
            GroupDetailsActivity.this.unregisterReceiver(weChatBroadcastReceiver);
        }
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
        qrInfoBean.setChatId(groupId);
        qrInfoBean.setChatTitle(getGroupName);
        qrInfoBean.setChatType(TIMConversationType.Group);
        String qrInfo = gson.toJson(qrInfoBean).toString();
        return qrInfo;
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
                            String facePath = pathList.get(0);
                            x.image().bind(img_group_avatar, facePath, MImageOptions.getCircularImageOptions());
                            uploadFace(facePath);
                        }
                    }
                    break;
                case 1000:
                    if (data == null) return;
                    List<String> identifiers = (List<String>) data.getSerializableExtra("identifiers");
                    inviteFriends(identifiers);
                    break;
                case 1002:
                    if (data == null) return;
                    int removeType = data.getIntExtra("removeType", 0);
                    if (removeType == 2) {
                        dismissGroup();
                    } else if (removeType == 1) {
                        getGroupMember();
                    }
                    break;
            }
        }
    }


    /**
     * 上传头像
     */
    private void uploadFace(String facePath) {
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
                        String faceUrl = relImageBean.getUrl();
                        modifyGroupAvatar(faceUrl);
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
     * 修改群头像
     *
     * @param faceUrl
     */
    private void modifyGroupAvatar(String faceUrl) {
        GroupManagerPresenter.modifyGroupAvatar(groupId, faceUrl, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                LogUtil.i("group avatar modify fail msg " + s);
            }

            @Override
            public void onSuccess() {
                LogUtil.i("group avatar modify success");
            }
        });
    }


    /**
     * 邀请朋友进入该群
     *
     * @param identifiers
     */
    private void inviteFriends(List<String> identifiers) {

        if (identifiers == null || identifiers.size() == 0) return;

        for (String id : identifiers) {
            // xml 协议的自定义消息
            InviteCustomBean customMsgEntry = new InviteCustomBean();
            customMsgEntry.setGroupId(groupId);
            customMsgEntry.setDes("\"" + UserInfoUtils.getNickName() + "\"邀请你加入" + getGroupName + "群,进入可查看详情");
            customMsgEntry.setGroupName(getGroupName);
            customMsgEntry.setGroupAvatar(faceUrl);
            Gson gson = V3Application.getGson();
            String customMsgJson = gson.toJson(customMsgEntry);
            Message message = new CustomMessage(CustomMessage.Type.INVITE, customMsgJson);

            ChatPresenter presenter = new ChatPresenter(new ChatView() {
                @Override
                public void showMessage(TIMMessage message) {

                }

                @Override
                public void showMessage(List<TIMMessage> messages) {

                }

                @Override
                public void clearAllMessage() {

                }

                @Override
                public void onSendMessageSuccess(TIMMessage message) {

                }

                @Override
                public void onSendMessageFail(int code, String desc, TIMMessage message) {

                }

                @Override
                public void sendImage() {

                }

                @Override
                public void sendPhoto() {

                }

                @Override
                public void sendText() {

                }

                @Override
                public void sendFile() {

                }

                @Override
                public void startSendVoice() {

                }

                @Override
                public void endSendVoice() {

                }

                @Override
                public void sendVideo(String fileName) {

                }

                @Override
                public void cancelSendVoice() {

                }

                @Override
                public void sending() {

                }

                @Override
                public void showDraft(TIMMessageDraft draft) {

                }

                @Override
                public void selectLocation() {

                }
            }, id, TIMConversationType.C2C);

            presenter.sendMessage(message.getMessage());
            Message message1 = new TextMessage(UserInfoUtils.getNickName() + "邀请你加入群");
            presenter.sendMessage(message1.getMessage());
            ToastUtils.showToast("邀请发送成功");
        }
    }
}
