package com.huanglong.v3.conversation;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.adapter.message.GroupMemberListAdapter;
import com.huanglong.v3.im.model.GroupMemberProfile;
import com.huanglong.v3.im.presenter.GroupManagerPresenter;
import com.huanglong.v3.model.contacts.MGroupMemberBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.PromptDialog;
import com.huanglong.v3.utils.ToastUtils;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberResult;
import com.tencent.TIMGroupMemberRoleType;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bin on 2018/6/6.
 * 群成员列表
 */
@ContentView(R.layout.activity_group_member)
public class GroupMemberActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;
    @ViewInject(R.id.group_member_list)
    private RecyclerView member_list;

    private String groupId;
    private String overId;

    private List<MGroupMemberBean> members = new ArrayList<>();
    private List<String> selMembers = new ArrayList<>();

    private List<String> memberIds = new ArrayList<>();


    private GroupMemberListAdapter groupMemberListAdapter;
    private PromptDialog promptDialog;

    private TextView tv_dialog_content;

    private int removeType;//1.移除成员，2.解散群组


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("群成员");
        tv_right.setText("确认");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        member_list.setLayoutManager(layoutManager);
        groupMemberListAdapter = new GroupMemberListAdapter();
        member_list.setAdapter(groupMemberListAdapter);
        initDialog();
    }

    @Override
    protected void logic() {
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");

        getGroupMember();


        groupMemberListAdapter.setItemOnClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                MGroupMemberBean mGroupMemberBean = (MGroupMemberBean) obj;
                String id = mGroupMemberBean.getId();
                if (!TextUtils.equals(id, overId)) {
                    boolean selected = mGroupMemberBean.isSelected();
                    if (selected) {
                        mGroupMemberBean.setSelected(false);
                    } else {
                        mGroupMemberBean.setSelected(true);
                    }
                    groupMemberListAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showToast("群主不可移除");
                }
            }
        });

    }

    @Event(value = {R.id.title_back, R.id.title_tv_right})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_tv_right:
                getSelMember();
                if (selMembers.size() > 0 && selMembers.size() < members.size() - 1) {
                    removeType = 1;
                    tv_dialog_content.setText("是否移除这些成员？");
                    promptDialog.show();
                } else if (selMembers.size() == members.size() - 1) {
                    removeType = 2;
                    tv_dialog_content.setText("是否解散该群组？");
                    promptDialog.show();
                } else {
                    ToastUtils.showToast("请选择需要移除的群成员");
                }
                break;
        }
    }

    /**
     * 初始化dailog
     */
    private void initDialog() {
        promptDialog = new PromptDialog(getActivity(), R.layout.dialog_hint_currency);
        tv_dialog_content = (TextView) promptDialog.getView(R.id.dialog_comment_content);
        promptDialog.getView(R.id.dialog_comment_cancel).setOnClickListener(dialogClick);
        promptDialog.getView(R.id.dialog_comment_confirm).setOnClickListener(dialogClick);
        promptDialog.getView(R.id.dialog_comment_lin).setOnClickListener(dialogClick);
    }

    /**
     * dialog 点击事件
     */
    private View.OnClickListener dialogClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            promptDialog.dismiss();
            switch (view.getId()) {
                case R.id.dialog_comment_confirm:
                    if (removeType == 1) {
                        deleteGroupMember();
                    } else if (removeType == 2) {
                        Intent intent = new Intent();
                        intent.putExtra("removeType", removeType);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    break;
            }
        }
    };


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
                if (memberIds != null) {
                    memberIds.clear();
                }
                for (TIMGroupMemberInfo item : timGroupMemberInfos) {
                    TIMGroupMemberRoleType role = item.getRole();
                    memberIds.add(item.getUser());
                    if (role == TIMGroupMemberRoleType.Owner) {
                        overId = item.getUser();
                    }
                }
                getMemberInfo();

            }
        });
    }

    /**
     * 获取群成员详细信息
     */
    private void getMemberInfo() {

        GroupMemberProfile.getMemberInfo(memberIds, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                for (TIMUserProfile timUserProfile : timUserProfiles) {
                    MGroupMemberBean mGroupMemberBean = new MGroupMemberBean();
                    mGroupMemberBean.setId(timUserProfile.getIdentifier());
                    mGroupMemberBean.setFaceUrl(timUserProfile.getFaceUrl());
                    mGroupMemberBean.setNickname(timUserProfile.getNickName());
                    mGroupMemberBean.setSelected(false);
                    members.add(mGroupMemberBean);
                }
                groupMemberListAdapter.setData(members, overId);
            }
        });
    }

    /**
     * 获取选中的成员
     */
    private void getSelMember() {
        if (selMembers == null) {
            selMembers = new ArrayList<>();
        }
        selMembers.clear();
        for (MGroupMemberBean mGroupMemberBean : members) {
            boolean selected = mGroupMemberBean.isSelected();
            if (selected) {
                selMembers.add(mGroupMemberBean.getId());
            }
        }
    }

    /**
     * 删除群成员
     */
    private void deleteGroupMember() {
        GroupManagerPresenter.deleteGroupMember(groupId, selMembers, new TIMValueCallBack<List<TIMGroupMemberResult>>() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("deleteGroupMember code:" + i + " msg:" + s);
                ToastUtils.showToast("删除群成员失败");
            }

            @Override
            public void onSuccess(List<TIMGroupMemberResult> timGroupMemberResults) {
                ToastUtils.showToast("删除群成员成功");
                Intent intent = new Intent();
                intent.putExtra("removeType", removeType);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

}
