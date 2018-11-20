package com.huanglong.v3.im.model;

import android.content.Context;
import android.text.TextUtils;

import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.conversation.ChatActivity;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;

/**
 * 好友或群聊的会话
 */
public class NomalConversation extends Conversation {


    private TIMConversation conversation;

    //最后一条消息
    private Message lastMessage;


    public NomalConversation(TIMConversation conversation) {
        this.conversation = conversation;
        type = conversation.getType();
        identify = conversation.getPeer();
    }


    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }


    @Override
    public int getAvatar() {
        switch (type) {
            case C2C:
                return R.drawable.head_other;
            case Group:
                return R.mipmap.head_group;
        }
        return 0;
    }

    /**
     * 跳转到聊天界面或会话详情
     *
     * @param context 跳转上下文
     */
    @Override
    public void navToDetail(Context context) {
        ChatActivity.navToChat(context, identify, type, name);
    }

    /**
     * 获取最后一条消息摘要
     */
    @Override
    public String getLastMessageSummary() {

        if (conversation.hasDraft()) {
            TextMessage textMessage = new TextMessage(conversation.getDraft());
            if (lastMessage == null || lastMessage.getMessage().timestamp() < conversation.getDraft().getTimestamp()) {
                return V3Application.getInstance().getString(R.string.conversation_draft) + textMessage.getSummary();
            } else {
                return lastMessage.getSummary();
            }
        } else {
            if (lastMessage == null) return "";
            return lastMessage.getSummary();
        }

//        TIMConversationExt ext = new TIMConversationExt(conversation);
//        if (ext.hasDraft()) {
//            TextMessage textMessage = new TextMessage(ext.getDraft());
//            if (lastMessage == null || lastMessage.getMessage().timestamp() < ext.getDraft().getTimestamp()) {
//                return V3Application.getInstance().getString(R.string.conversation_draft) + textMessage.getSummary();
//            } else {
//                return lastMessage.getSummary();
//            }
//        } else {
//            if (lastMessage == null) return "";
//            return lastMessage.getSummary();
//        }
    }

    /**
     * 获取名称
     */
    @Override
    public String getName() {
        if (type == TIMConversationType.Group) {
            name = GroupInfo.getInstance().getGroupName(identify);
            if (name.equals("")) name = identify;
        } else {
            FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
            name = profile == null ? identify : profile.getName();
        }
        return name;
    }

    /**
     * 获取分组名称
     *
     * @return
     */
    public String getGroupName() {
        String groupName = "";
        if (type == TIMConversationType.C2C) {
            FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
            groupName = profile == null ? "朋友" : profile.getGroupName();
        }
        return groupName;
    }

    /**
     * 获取分组名称
     *
     * @return
     */
    public String getFaceUrl() {
        String faceUrl = "";
        if (type == TIMConversationType.C2C) {
            FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
            faceUrl = profile == null ? "" : profile.getAvatarUrl();
        } else if (type == TIMConversationType.Group) {
            faceUrl = GroupInfo.getInstance().getGroupAvatar(identify);
            if (TextUtils.isEmpty(faceUrl)) faceUrl = "";
        }
        return faceUrl;
    }


    /**
     * 获取未读消息数量
     */
    @Override
    public long getUnreadNum() {
        if (conversation == null) return 0;
        return conversation.getUnreadMessageNum();

//        if (conversation == null) return 0;
//        TIMConversationExt ext = new TIMConversationExt(conversation);
//        return ext.getUnreadMessageNum();
    }

    /**
     * 将所有消息标记为已读
     */
    @Override
    public void readAllMessage() {
        if (conversation != null) {
            conversation.setReadMessage();
        }

//        if (conversation != null) {
//            TIMConversationExt ext = new TIMConversationExt(conversation);
//            ext.setReadMessage(null, null);
//        }
    }


    /**
     * 获取最后一条消息的时间
     */
    @Override
    public long getLastMessageTime() {
        if (conversation.hasDraft()) {
            if (lastMessage == null || lastMessage.getMessage().timestamp() < conversation.getDraft().getTimestamp()) {
                return conversation.getDraft().getTimestamp();
            } else {
                return lastMessage.getMessage().timestamp();
            }
        }
        if (lastMessage == null) return 0;
        return lastMessage.getMessage().timestamp();

//        TIMConversationExt ext = new TIMConversationExt(conversation);
//        if (ext.hasDraft()) {
//            if (lastMessage == null || lastMessage.getMessage().timestamp() < ext.getDraft().getTimestamp()) {
//                return ext.getDraft().getTimestamp();
//            } else {
//                return lastMessage.getMessage().timestamp();
//            }
//        }
//        if (lastMessage == null) return 0;
//        return lastMessage.getMessage().timestamp();
    }

    /**
     * 获取会话类型
     */
    public TIMConversationType getType() {
        return conversation.getType();
    }
}
