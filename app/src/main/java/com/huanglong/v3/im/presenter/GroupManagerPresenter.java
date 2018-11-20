package com.huanglong.v3.im.presenter;

import android.text.TextUtils;

import com.huanglong.v3.im.model.GroupInfo;
import com.huanglong.v3.im.viewfeatures.GroupInfoView;
import com.huanglong.v3.im.viewfeatures.GroupManageMessageView;
import com.huanglong.v3.im.viewfeatures.GroupManageView;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupAddOpt;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberResult;
import com.tencent.TIMGroupPendencyGetParam;
import com.tencent.TIMGroupPendencyListGetSucc;
import com.tencent.TIMGroupReceiveMessageOpt;
import com.tencent.TIMGroupSearchSucc;
import com.tencent.TIMValueCallBack;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 群管理逻辑
 */
public class GroupManagerPresenter {

    private static final String TAG = "GroupManagerPresenter";

    private GroupManageMessageView messageView;
    private GroupInfoView infoView;
    private GroupManageView manageView;
    private long timeStamp = 0;

    public GroupManagerPresenter(GroupManageMessageView view) {
        this(view, null, null);
    }

    public GroupManagerPresenter(GroupInfoView view) {
        infoView = view;
    }

    public GroupManagerPresenter(GroupManageView view) {
        this(null, null, view);
    }

    public GroupManagerPresenter(GroupManageMessageView view1, GroupInfoView view2, GroupManageView view3) {
        messageView = view1;
        infoView = view2;
        manageView = view3;
    }


    /**
     * 获取群管理最有一条消息,和未读消息数
     * 包括：加群等已决和未决的消息
     */
    public void getGroupManageLastMessage() {

        TIMGroupPendencyGetParam param = new TIMGroupPendencyGetParam();
        param.setNumPerPage(1);
        param.setTimestamp(0);
        TIMGroupManager.getInstance().getGroupPendencyList(param, new TIMValueCallBack<TIMGroupPendencyListGetSucc>() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("onError code" + i + " msg " + s);
            }

            @Override
            public void onSuccess(TIMGroupPendencyListGetSucc timGroupPendencyListGetSucc) {
                if (messageView != null && timGroupPendencyListGetSucc.getPendencies().size() > 0) {
                    messageView.onGetGroupManageLastMessage(timGroupPendencyListGetSucc.getPendencies().get(0),
                            timGroupPendencyListGetSucc.getPendencyMeta().getUnReadCount());
                }
            }
        });
    }


    /**
     * 获取群管理消息
     *
     * @param pageSize 每次拉取数量
     */
    public void getGroupManageMessage(int pageSize) {
        TIMGroupPendencyGetParam param = new TIMGroupPendencyGetParam();
        param.setNumPerPage(pageSize);
        param.setTimestamp(timeStamp);
        TIMGroupManager.getInstance().getGroupPendencyList(param, new TIMValueCallBack<TIMGroupPendencyListGetSucc>() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("onError code " + i + " msg " + s);
            }

            @Override
            public void onSuccess(TIMGroupPendencyListGetSucc timGroupPendencyListGetSucc) {
                if (messageView != null) {
                    messageView.onGetGroupManageMessage(timGroupPendencyListGetSucc.getPendencies());
                }
            }
        });
    }


    /**
     * 按照群名称搜索群
     *
     * @param key 关键字
     */
    public void searchGroupByName(String key) {
        long flag = 0;
        flag |= TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_NAME;
        flag |= TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_GROUP_TYPE;
        flag |= TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_OWNER_UIN;
        flag |= TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_INTRODUCTION;
        TIMGroupManager.getInstance().searchGroup(key, flag, null, 0, 30, new TIMValueCallBack<TIMGroupSearchSucc>() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("onError code" + i + " msg " + s);
            }

            @Override
            public void onSuccess(TIMGroupSearchSucc timGroupSearchSucc) {
                if (infoView == null) return;
                infoView.showGroupInfo(timGroupSearchSucc.getInfoList());
            }
        });
    }


    /**
     * 按照群ID搜索群
     *
     * @param groupId 群组ID
     */
    public void searchGroupByID(List<String> groupId) {
        TIMGroupManager.getInstance().getGroupPublicInfo(groupId, new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("onError code" + i + " msg " + s);
            }

            @Override
            public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                if (infoView == null) return;
                infoView.showGroupInfo(timGroupDetailInfos);
            }
        });
    }


    /**
     * 申请加入群
     *
     * @param groupId  群组ID
     * @param reason   申请理由
     * @param callBack 回调
     */
    public static void applyJoinGroup(String groupId, String reason, TIMCallBack callBack) {
        TIMGroupManager.getInstance().applyJoinGroup(groupId, reason, callBack);
    }


    /**
     * 将群管理消息标记为已读
     *
     * @param timeStamp 最后一条消息的时间戳
     * @param callBack  回调
     */
    public static void readGroupManageMessage(long timeStamp, TIMCallBack callBack) {
        TIMGroupManager.getInstance().reportGroupPendency(timeStamp, callBack);
    }


    /**
     * 创建群
     *
     * @param faceUrl  群头像
     * @param name     群名称
     * @param is_free  是否收费
     * @param price    收费金额
     * @param type     群类型
     * @param members  群成员
     * @param callBack 回调
     */
    public static void createGroup(String faceUrl, String name, int is_free, String price, String type, List<String> members, TIMValueCallBack<String> callBack) {
        List<TIMGroupMemberInfo> memberinfos = new ArrayList<>();
        for (String member : members) {
            TIMGroupMemberInfo newMember = new TIMGroupMemberInfo();
            newMember.setUser(member);
            memberinfos.add(newMember);
        }
        TIMGroupManager.CreateGroupParam groupGroupParam = TIMGroupManager.getInstance().new CreateGroupParam();
        groupGroupParam.setGroupName(name);
        groupGroupParam.setMembers(memberinfos);
        groupGroupParam.setAddOption(TIMGroupAddOpt.TIM_GROUP_ADD_ANY);
        groupGroupParam.setCustomInfo("is_free", String.valueOf(is_free).getBytes());
        groupGroupParam.setCustomInfo("price", price.getBytes());
        groupGroupParam.setGroupType(type);
        if (!TextUtils.isEmpty(faceUrl)) {
            groupGroupParam.setFaceUrl(faceUrl);
        }
        TIMGroupManager.getInstance().createGroup(groupGroupParam, callBack);
    }

    /**
     * 创建群
     *
     * @param name     群名称
     * @param type     群类型
     * @param members  群成员
     * @param callBack 回调
     */
    public static void createGroup2(String name, String type, List<String> members, TIMValueCallBack<String> callBack) {
        List<TIMGroupMemberInfo> memberinfos = new ArrayList<>();
        for (String member : members) {
            TIMGroupMemberInfo newMember = new TIMGroupMemberInfo();
            newMember.setUser(member);
            memberinfos.add(newMember);
        }
        TIMGroupManager.CreateGroupParam groupGroupParam = TIMGroupManager.getInstance().new CreateGroupParam();
        groupGroupParam.setGroupName(name);
        groupGroupParam.setMembers(memberinfos);
        groupGroupParam.setGroupType(type);
        TIMGroupManager.getInstance().createGroup(groupGroupParam, callBack);

    }

    /**
     * 创建聊天室
     *
     * @param name
     * @param callBack
     */
    public static void createAVChatRoomGroup(String name, TIMValueCallBack<String> callBack) {
        TIMGroupManager.CreateGroupParam groupGroupParam = TIMGroupManager.getInstance().new CreateGroupParam();
        groupGroupParam.setGroupType(GroupInfo.chatRoom);
        groupGroupParam.setGroupName(name);
        groupGroupParam.setCustomInfo("is_free", String.valueOf(0).getBytes());
        groupGroupParam.setCustomInfo("price", "0".getBytes());
        TIMGroupManager.getInstance().createGroup(groupGroupParam, callBack);
    }

    /**
     * 删除群
     *
     * @param mRoomId
     * @param timCallBack
     */
    public static void deleteGroup(String mRoomId, TIMCallBack timCallBack) {
        TIMGroupManager.getInstance().deleteGroup(mRoomId, timCallBack);
    }

    /**
     * 退出群
     *
     * @param groupId  群组ID
     * @param callBack 回调
     */
    public static void quitGroup(String groupId, TIMCallBack callBack) {
        TIMGroupManager.getInstance().quitGroup(groupId, callBack);
    }


    /**
     * 解散群
     *
     * @param groupId  群组ID
     * @param callBack 回调
     */
    public static void dismissGroup(String groupId, TIMCallBack callBack) {
        TIMGroupManager.getInstance().deleteGroup(groupId, callBack);
    }


    /**
     * 邀请入群
     *
     * @param groupId  群组ID
     * @param members  邀请的好友
     * @param callBack 回调
     */
    public static void inviteGroup(String groupId, List<String> members, TIMValueCallBack<List<TIMGroupMemberResult>> callBack) {
        TIMGroupManager.getInstance().inviteGroupMember(groupId, members, callBack);
    }

    /**
     * 删除群成员
     *
     * @param groupId
     * @param user
     * @param cb
     */
    public static void deleteGroupMember(String groupId, List<String> user, TIMValueCallBack<List<TIMGroupMemberResult>> cb) {
        TIMGroupManager.getInstance().deleteGroupMember(groupId, user, cb);
    }

    /**
     * 获取我加入的群列表
     *
     * @param callBack
     */
    public static void getGroupList(TIMValueCallBack<List<TIMGroupBaseInfo>> callBack) {
        TIMGroupManager.getInstance().getGroupList(callBack);
    }

    /**
     * 查询群资料
     *
     * @param groupId
     * @param callBack
     */
    public static void getOneGroupDetailInfo(String groupId, TIMValueCallBack<List<TIMGroupDetailInfo>> callBack) {
        List<String> groupIds = new ArrayList<>();
        groupIds.add(groupId);
        TIMGroupManager.getInstance().getGroupDetailInfo(groupIds, callBack);
        groupIds.clear();
        groupIds = null;
    }

    /**
     * 非成员查询群资料
     *
     * @param groupId
     * @param callBack
     */
    public static void getGroupPublicInfo(String groupId, TIMValueCallBack<List<TIMGroupDetailInfo>> callBack) {
        List<String> groupIds = new ArrayList<>();
        groupIds.add(groupId);
        TIMGroupManager.getInstance().getGroupPublicInfo(groupIds, callBack);
        groupIds.clear();
        groupIds = null;
    }


    /**
     * 获取群成员
     *
     * @param groupId
     * @param callBack
     */
    public static void getGroupMembers(String groupId, TIMValueCallBack<List<TIMGroupMemberInfo>> callBack) {
        TIMGroupManager.getInstance().getGroupMembers(groupId, callBack);
    }

    /**
     * 修改群名称
     *
     * @param groupId
     * @param groupName
     * @param callBack
     */
    public static void modifyGroupName(String groupId, String groupName, TIMCallBack callBack) {
        TIMGroupManager.getInstance().modifyGroupName(groupId, groupName, callBack);
    }


    /**
     * 修改群头像
     *
     * @param groupId
     * @param faceUrl
     * @param callBack
     */
    public static void modifyGroupAvatar(String groupId, String faceUrl, TIMCallBack callBack) {
        TIMGroupManager.getInstance().modifyGroupFaceUrl(groupId, faceUrl, callBack);
    }


//    public static boolean isReceiveMessage(String groupId){
//
//        return TIMGroupManager.getInstance().Ms
//
//    }

    /**
     * 设置消息免打扰
     *
     * @param groupId
     * @param opt
     * @param cb
     */
    public static void setMsgDisturb(String groupId, TIMGroupReceiveMessageOpt opt, TIMCallBack cb) {
        TIMGroupManager.getInstance().modifyReceiveMessageOpt(groupId, opt, cb);
    }


}
