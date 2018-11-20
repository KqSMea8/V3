package com.huanglong.v3.im.contacts;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.conversation.ChatActivity;
import com.huanglong.v3.im.event.FriendshipEvent;
import com.huanglong.v3.im.model.FriendProfile;
import com.huanglong.v3.im.model.FriendshipInfo;
import com.huanglong.v3.im.presenter.FriendshipManagerPresenter;
import com.huanglong.v3.im.view.CircleImageView;
import com.huanglong.v3.im.view.LineControllerView;
import com.huanglong.v3.im.view.ListPickerDialog;
import com.huanglong.v3.im.viewfeatures.FriendshipManageView;
import com.huanglong.v3.utils.Constant;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.ToastUtils;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendStatus;
import com.tencent.TIMValueCallBack;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Collections;
import java.util.List;

@ContentView(R.layout.activity_profile)
public class ProfileActivity extends BaseFragmentActivity implements FriendshipManageView {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.profile_name)
    private TextView tv_nickname;
    @ViewInject(R.id.profile_v3_number)
    private LineControllerView v3_number;
    @ViewInject(R.id.profile_remark)
    private LineControllerView remark;
    @ViewInject(R.id.profile_group)
    private LineControllerView category;
    @ViewInject(R.id.profile_blackList)
    private LineControllerView black;
    @ViewInject(R.id.profile_avatar)
    private CircleImageView img_avatar;

    private final int CHANGE_CATEGORY_CODE = 100;
    private final int CHANGE_REMARK_CODE = 200;

    private FriendshipManagerPresenter friendshipManagerPresenter;
    private String identify, categoryStr;

    private FriendProfile profile;

    public static void navToProfile(Context context, String identify) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("identify", identify);
        context.startActivity(intent);
    }


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("个人资料");
        identify = getIntent().getStringExtra("identify");
        friendshipManagerPresenter = new FriendshipManagerPresenter(this);
//        showProfile(identify);
    }

    @Override
    protected void logic() {
        profile = FriendshipInfo.getInstance().getProfile(identify);
        LogUtil.d("show profile isFriend " + (profile != null));
        if (profile == null) {
            return;
        }

        showProfile();
        black.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FriendshipManagerPresenter.addBlackList(Collections.singletonList(identify), new TIMValueCallBack<List<TIMFriendResult>>() {
                        @Override
                        public void onError(int i, String s) {
                            LogUtil.e("add black list error " + s);
                        }

                        @Override
                        public void onSuccess(List<TIMFriendResult> timFriendResults) {
                            if (timFriendResults.get(0).getStatus() == TIMFriendStatus.TIM_FRIEND_STATUS_SUCC) {
                                ToastUtils.showToast(getString(R.string.profile_black_succ));
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    @Event(value = {R.id.title_back, R.id.profile_remark, R.id.profile_group, R.id.profile_btnDel, R.id.profile_btnChat})
    private void mOnClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                ProfileActivity.this.finish();
                break;
            case R.id.profile_remark:
                EditActivity.navToEdit(ProfileActivity.this, "修改备注", remark.getContent(), CHANGE_REMARK_CODE, new EditActivity.EditInterface() {
                    @Override
                    public void onEdit(String text, TIMCallBack callBack) {
                        FriendshipManagerPresenter.setRemarkName(profile.getIdentify(), text, callBack);
                    }
                }, 20);
                break;
            case R.id.profile_group:
//                String[] groups = groupList.toArray(new String[groupList.size()]);
                String[] groups = Constant.groupsName;
                new ListPickerDialog().show(groups, getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(categoryStr)) return;
                        if (groups[which].equals(categoryStr)) return;
                        friendshipManagerPresenter.changeFriendGroup(identify,
                                categoryStr.equals(getString(R.string.default_group_name)) ? null : categoryStr,
                                groups[which].equals(getString(R.string.default_group_name)) ? null : groups[which]);
                    }
                });
                break;
            case R.id.profile_btnDel:
                friendshipManagerPresenter.delFriend(identify);
                break;
            case R.id.profile_btnChat:
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("identify", identify);
                intent.putExtra("type", TIMConversationType.C2C);
                startActivity(intent);
                finish();
                break;
        }
    }

    /**
     * 显示用户信息
     */
    public void showProfile() {
        x.image().bind(img_avatar, profile.getAvatarUrl(), MImageOptions.getCircularImageOptions());
        tv_nickname.setText(profile.getNickName());
        v3_number.setContent(profile.getIdentify());
        remark.setContent(profile.getRemark());
        //一个用户可以在多个分组内，客户端逻辑保证一个人只存在于一个分组
        category.setContent(categoryStr = profile.getGroupName());


        friendshipManagerPresenter.getBlackList(new TIMValueCallBack<List<String>>() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("黑名单获取失败，code:" + i + " msg:" + s);
            }

            @Override
            public void onSuccess(List<String> strings) {
                if (strings.contains(identify)) {
                    black.setSwitch(true);
                } else {
                    black.setSwitch(false);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHANGE_CATEGORY_CODE) {
            if (resultCode == RESULT_OK) {
//                LineControllerView category = (LineControllerView) findViewById(R.id.group);
                category.setContent(categoryStr = data.getStringExtra("category"));
            }
        } else if (requestCode == CHANGE_REMARK_CODE) {
            if (resultCode == RESULT_OK) {
                remark.setContent(data.getStringExtra(EditActivity.RETURN_EXTRA));
//                LineControllerView remark = (LineControllerView) findViewById(R.id.remark);
//                remark.setContent(data.getStringExtra(EditActivity.RETURN_EXTRA));

            }
        }

    }

    /**
     * 添加好友结果回调
     *
     * @param status 返回状态
     */
    @Override
    public void onAddFriend(TIMFriendStatus status) {

    }

    /**
     * 删除好友结果回调
     *
     * @param status 返回状态
     */
    @Override
    public void onDelFriend(TIMFriendStatus status) {
        switch (status) {
            case TIM_FRIEND_STATUS_SUCC:
                ToastUtils.showToast(getString(R.string.profile_del_succeed));
                if (FriendshipManageMessageActivity.instance != null) {
                    FriendshipManageMessageActivity.instance.refreshData();
                }
                finish();
                break;
            case TIM_FRIEND_STATUS_UNKNOWN:
                ToastUtils.showToast(getString(R.string.profile_del_fail));
                break;
        }

    }

    /**
     * 修改好友分组回调
     *
     * @param status    返回状态
     * @param groupName 分组名
     */
    @Override
    public void onChangeGroup(TIMFriendStatus status, String groupName) {
//        LineControllerView category = (LineControllerView) findViewById(R.id.group);
        if (groupName == null) {
            groupName = getString(R.string.default_group_name);
        }
        switch (status) {
            case TIM_FRIEND_STATUS_UNKNOWN:
                ToastUtils.showToast(getString(R.string.change_group_error));
            case TIM_FRIEND_STATUS_SUCC:
                category.setContent(groupName);
                FriendshipEvent.getInstance().OnFriendGroupChange();
                break;
            default:
                ToastUtils.showToast(getString(R.string.change_group_error));
                category.setContent(getString(R.string.default_group_name));
                break;
        }
    }
}
