package com.huanglong.v3.im.contacts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.im.presenter.FriendshipManagerPresenter;
import com.huanglong.v3.im.view.CircleImageView;
import com.huanglong.v3.im.view.LineControllerView;
import com.huanglong.v3.im.view.ListPickerDialog;
import com.huanglong.v3.im.view.NotifyDialog;
import com.huanglong.v3.im.viewfeatures.FriendshipManageView;
import com.huanglong.v3.model.WechatPayBean;
import com.huanglong.v3.model.mine.UserInfoBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.Common;
import com.huanglong.v3.utils.Constant;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.PromptDialog;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.utils.WXUtils;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendStatus;
import com.tencent.TIMValueCallBack;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Collections;
import java.util.List;

/**
 * 申请添加好友界面
 */
@ContentView(R.layout.activity_add_friend)
public class AddFriendActivity extends BaseFragmentActivity implements FriendshipManageView, View.OnClickListener {


    @ViewInject(R.id.title_name)
    private TextView tvName;
    @ViewInject(R.id.group)
    private LineControllerView groupField;
    @ViewInject(R.id.btnAdd)
    private TextView btnAdd;
    @ViewInject(R.id.editMessage)
    private EditText editMessage;
    @ViewInject(R.id.editNickname)
    private EditText editRemark;
    @ViewInject(R.id.add_friends_nickname)
    private TextView tv_nickname;
    @ViewInject(R.id.add_friends_account)
    private TextView tv_account;
    @ViewInject(R.id.avatar)
    private CircleImageView img_avatar;

    private TextView tv_dialog_content;


    private FriendshipManagerPresenter presenter;
    private String friend_id;

    private int is_free;
    private String blance = "0";


    private PromptDialog promptDialog;
    private PromptDialog promptDialog2;

    private WechatPayBean wechatPayBean;

    private WeChatBroadcastReceiver weChatBroadcastReceiver;
    private UserInfoBean personalBean;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tvName.setText("详细资料");
        initDialogClick();
        weChatBroadcastReceiver = new WeChatBroadcastReceiver();
    }

    @Override
    protected void logic() {
        blance = getIntent().getStringExtra("blance");
        is_free = getIntent().getIntExtra("is_open", 0);
        friend_id = getIntent().getStringExtra("id");
        tv_nickname.setText(getIntent().getStringExtra("name"));
        tv_account.setText("v3号:" + friend_id);
//        idField.setContent(id);
        requestPersonal();

        presenter = new FriendshipManagerPresenter(this);
        registerReceiver();

    }

    @Event(value = {R.id.btnAdd, R.id.group, R.id.title_back})
    private void monClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                if (TextUtils.equals(friend_id, UserInfoUtils.getUid())) {
                    ToastUtils.showToast("不能添加自己为好友");
                    return;
                }
                if (personalBean != null) {
                    String fee = personalBean.getFee();
                    if (!TextUtils.isEmpty(fee)) {
                        is_free = Integer.parseInt(fee);
                        if (is_free == 0) {
                            String s = editMessage.getText().toString();
                            presenter.addFriend(friend_id, editRemark.getText().toString(), TextUtils.isEmpty(groupField.getContent()) ? getString(R.string.default_group_name) : groupField.getContent(), "对方向您支付了0元，备注信息" + s);
                        } else {
                            tv_dialog_content.setText("添加该好友需支付" + is_free + "元，是否添加？");
                            promptDialog.show();
                        }
                    }
                }

                break;
            case R.id.group:
                String[] groups = Constant.groupsName;
                new ListPickerDialog().show(groups, getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        groupField.setContent(groups[which]);
                    }
                });
                break;
            case R.id.title_back:
                AddFriendActivity.this.finish();
                break;
        }
    }

    /**
     * 初始化设置dialog
     */
    private void initDialogClick() {
        promptDialog = new PromptDialog(AddFriendActivity.this, R.layout.dialog_hint_currency);

        promptDialog.getView(R.id.dialog_comment_lin).setOnClickListener(this);
        tv_dialog_content = (TextView) promptDialog.getView(R.id.dialog_comment_content);
        promptDialog.getView(R.id.dialog_comment_cancel).setOnClickListener(this);
        promptDialog.getView(R.id.dialog_comment_confirm).setOnClickListener(this);

        promptDialog2 = new PromptDialog(AddFriendActivity.this, R.layout.dialog_payment);

        promptDialog2.getView(R.id.dialog_payment_lin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptDialog2.dismiss();
            }
        });
    }

    /**
     * 添加好友结果回调
     *
     * @param status 返回状态
     */
    @Override
    public void onAddFriend(TIMFriendStatus status) {
        switch (status) {
            case TIM_ADD_FRIEND_STATUS_PENDING:
                Toast.makeText(this, getResources().getString(R.string.add_friend_succeed), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case TIM_FRIEND_STATUS_SUCC:
                Toast.makeText(this, getResources().getString(R.string.add_friend_added), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case TIM_ADD_FRIEND_STATUS_FRIEND_SIDE_FORBID_ADD:
                Toast.makeText(this, getResources().getString(R.string.add_friend_refuse_all), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case TIM_ADD_FRIEND_STATUS_IN_OTHER_SIDE_BLACK_LIST:
                Toast.makeText(this, getResources().getString(R.string.add_friend_to_blacklist), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case TIM_ADD_FRIEND_STATUS_IN_SELF_BLACK_LIST:
                NotifyDialog dialog = new NotifyDialog();
                dialog.show(getString(R.string.add_friend_del_black_list), getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FriendshipManagerPresenter.delBlackList(Collections.singletonList(friend_id), new TIMValueCallBack<List<TIMFriendResult>>() {
                            @Override
                            public void onError(int i, String s) {
                                Toast.makeText(AddFriendActivity.this, getResources().getString(R.string.add_friend_del_black_err), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(List<TIMFriendResult> timFriendResults) {
                                Toast.makeText(AddFriendActivity.this, getResources().getString(R.string.add_friend_del_black_succ), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                break;
            default:
                Toast.makeText(this, getResources().getString(R.string.add_friend_error), Toast.LENGTH_SHORT).show();
                break;
        }

    }

    /**
     * 删除好友结果回调
     *
     * @param status 返回状态
     */
    @Override
    public void onDelFriend(TIMFriendStatus status) {

    }

    /**
     * 修改好友分组回调
     *
     * @param status    返回状态
     * @param groupName 分组名
     */
    @Override
    public void onChangeGroup(TIMFriendStatus status, String groupName) {

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
                requestPay();
                break;
        }
    }

    /**
     * 请求支付接口
     */
    private void requestPay() {
        showDialog();
        RequestParams params = MRequestParams.getNoTokenParams(Api.pay);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("friend_id", friend_id);
        params.addBodyParameter("pay_type", "1");
        params.addBodyParameter("pay_amount", is_free + "");
        params.addBodyParameter("type", "1");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    wechatPayBean = gson.fromJson(json, WechatPayBean.class);
                    if (wechatPayBean != null) {
                        WXUtils.wxPay(AddFriendActivity.this, wechatPayBean);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                JsonHandleUtils.netError(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                dismissDialog();
            }

            @Override
            public void onFinished() {

            }
        });
    }


    /**
     * 注册微信分享广播
     */

    private void registerReceiver() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.PAY_WECHAT_ACTION);
        AddFriendActivity.this.registerReceiver(weChatBroadcastReceiver, filter);
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
                ToastUtils.showToast("支付成功");
                String s = editMessage.getText().toString();
                presenter.addFriend(friend_id, editRemark.getText().toString(), TextUtils.isEmpty(groupField.getContent()) ? getString(R.string.default_group_name) : groupField.getContent(), "对方向您支付了" + blance + "元，备注信息" + s);
            } else if (TextUtils.equals("cancel", type)) {
                ToastUtils.showToast("取消支付");
            } else {
                ToastUtils.showToast("支付失败");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (weChatBroadcastReceiver != null) {
            AddFriendActivity.this.unregisterReceiver(weChatBroadcastReceiver);
        }
    }


    /**
     * 请求个人资料
     */
    private void requestPersonal() {

        RequestParams params = MRequestParams.getNoTokenParams(Api.getUserInfo);
        params.addBodyParameter("uid", friend_id);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    personalBean = gson.fromJson(json, UserInfoBean.class);
                    if (personalBean != null) {
                        x.image().bind(img_avatar, personalBean.getHead_image(), MImageOptions.getCircularImageOptions());
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
