package com.huanglong.v3.im.model;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.huanglong.v3.R;
import com.huanglong.v3.im.adapter.ChatAdapter;
import com.huanglong.v3.im.presenter.FriendshipManagerPresenter;
import com.huanglong.v3.im.utils.TimeUtil;
import com.huanglong.v3.im.view.CircleImageView;
import com.huanglong.v3.utils.MImageOptions;
import com.tencent.TIMConversationType;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageStatus;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import org.xutils.x;

import java.io.Serializable;
import java.util.List;

/**
 * 消息数据基类
 */
public abstract class Message implements Serializable{

    protected final String TAG = "Message";

    TIMMessage message;

    private boolean hasTime;

    /**
     * 消息描述信息
     */
    private String desc;


    public TIMMessage getMessage() {
        return message;
    }


    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    public abstract void showMessage(ChatAdapter.ViewHolder viewHolder, Context context);

    /**
     * 获取显示气泡
     *
     * @param viewHolder 界面样式
     */
    public RelativeLayout getBubbleView(final ChatAdapter.ViewHolder viewHolder) {
        viewHolder.systemMessage.setVisibility(hasTime ? View.VISIBLE : View.GONE);
        viewHolder.systemMessage.setText(TimeUtil.getChatTimeStr(message.timestamp()));
        showDesc(viewHolder);
        if (message.isSelf()) {
            viewHolder.leftPanel.setVisibility(View.GONE);
            viewHolder.rightPanel.setVisibility(View.VISIBLE);
            CircleImageView avatar = viewHolder.rightPanel.findViewById(R.id.rightAvatar);
            showSelfAvatar(avatar);
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatAdapter.navToDetail(message);
                }
            });

            return viewHolder.rightMessage;
        } else {
            viewHolder.leftPanel.setVisibility(View.VISIBLE);
            viewHolder.rightPanel.setVisibility(View.GONE);
            CircleImageView avatar = viewHolder.leftPanel.findViewById(R.id.leftAvatar);
            showOtherAvatar(avatar);
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatAdapter.navToDetail(message);
                }
            });
            //群聊显示名称，群名片>个人昵称>identify
            if (message.getConversation().getType() == TIMConversationType.Group) {
                viewHolder.sender.setVisibility(View.VISIBLE);
                String name = "";
                if (message.getSenderGroupMemberProfile() != null)
                    name = message.getSenderGroupMemberProfile().getNameCard();
                if (name.equals("") && message.getSenderProfile() != null)
                    name = message.getSenderProfile().getNickName();
                if (name.equals("")) name = message.getSender();
                viewHolder.sender.setText(name);
            } else {
                viewHolder.sender.setVisibility(View.GONE);
            }
            return viewHolder.leftMessage;
        }

    }

    /**
     * 显示消息状态
     *
     * @param viewHolder 界面样式
     */
    public void showStatus(ChatAdapter.ViewHolder viewHolder) {
        switch (message.status()) {
            case Sending:
                viewHolder.error.setVisibility(View.GONE);
                viewHolder.sending.setVisibility(View.VISIBLE);
                break;
            case SendSucc:
                viewHolder.error.setVisibility(View.GONE);
                viewHolder.sending.setVisibility(View.GONE);
                break;
            case SendFail:
                viewHolder.error.setVisibility(View.VISIBLE);
                viewHolder.sending.setVisibility(View.GONE);
                viewHolder.leftPanel.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 判断是否是自己发的
     */
    public boolean isSelf() {
        return message.isSelf();
    }

    /**
     * 获取消息摘要
     */
    public abstract String getSummary();

    String getRevokeSummary() {
        if (message.status() == TIMMessageStatus.HasDeleted) {
            return getSender() + "撤回了一条消息";
        }
        return null;
    }

    /**
     * 保存消息或消息文件
     */
    public abstract void save();


    /**
     * 删除消息
     */
    public void remove() {
        if (message != null) {
            message.remove();
        }
    }


    /**
     * 是否需要显示时间获取
     */
    public boolean getHasTime() {
        return hasTime;
    }


    /**
     * 是否需要显示时间设置
     *
     * @param message 上一条消息
     */
    public void setHasTime(TIMMessage message) {
        if (message == null) {
            hasTime = true;
            return;
        }
        hasTime = this.message.timestamp() - message.timestamp() > 300;
    }


    /**
     * 消息是否发送失败
     */
    public boolean isSendFail() {
        return message.status() == TIMMessageStatus.SendFail;
    }

    /**
     * 清除气泡原有数据
     */
    protected void clearView(ChatAdapter.ViewHolder viewHolder) {
        getBubbleView(viewHolder).removeAllViews();
        getBubbleView(viewHolder).setOnClickListener(null);
    }

    /**
     * 显示撤回的消息
     */
    boolean checkRevoke(ChatAdapter.ViewHolder viewHolder) {
        if (message.status() == TIMMessageStatus.HasDeleted) {
            viewHolder.leftPanel.setVisibility(View.GONE);
            viewHolder.rightPanel.setVisibility(View.GONE);
            viewHolder.systemMessage.setVisibility(View.VISIBLE);
            viewHolder.systemMessage.setText(getSummary());
            return true;
        }
        return false;
    }

    /**
     * 获取发送者
     */
    public String getSender() {
        if (message.getSender() == null) return "";
        return message.getSender();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    private void showDesc(ChatAdapter.ViewHolder viewHolder) {

        if (desc == null || desc.equals("")) {
            viewHolder.rightDesc.setVisibility(View.GONE);
        } else {
            viewHolder.rightDesc.setVisibility(View.VISIBLE);
            viewHolder.rightDesc.setText(desc);
        }
    }

    /**
     * 显示自己的头像
     *
     * @param avatar
     */
    private void showSelfAvatar(CircleImageView avatar) {
//        String peer = message.getConversation().getPeer();


        FriendshipManagerPresenter.getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                x.image().bind(avatar, timUserProfile.getFaceUrl(), MImageOptions.getCircularImageOptions());
            }
        });
    }


    /**
     * 显示其他用户的头像
     *
     * @param avatar
     */
    private void showOtherAvatar(CircleImageView avatar) {
        String identifier = "";
        if (message.getConversation().getType() == TIMConversationType.Group) {
            TIMUserProfile senderProfile = message.getSenderProfile();
            identifier = senderProfile.getIdentifier();
        } else {
            identifier = message.getConversation().getPeer();
        }
        FriendshipManagerPresenter.getOneUsersProfile(identifier, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                if (timUserProfiles != null && timUserProfiles.size() > 0) {
                    x.image().bind(avatar, timUserProfiles.get(0).getFaceUrl(), MImageOptions.getCircularImageOptions());
                }
            }
        });

    }
}
