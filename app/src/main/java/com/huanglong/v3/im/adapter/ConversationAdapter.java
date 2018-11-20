package com.huanglong.v3.im.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.im.model.Conversation;
import com.huanglong.v3.im.model.FriendshipConversation;
import com.huanglong.v3.im.model.GroupManageConversation;
import com.huanglong.v3.im.model.NomalConversation;
import com.huanglong.v3.im.utils.TimeUtil;
import com.huanglong.v3.im.view.CircleImageView;
import com.huanglong.v3.utils.MImageOptions;
import com.tencent.TIMConversationType;

import org.xutils.x;

import java.util.List;

/**
 * 会话界面adapter
 */
public class ConversationAdapter extends ArrayAdapter<Conversation> {

    private int resourceId;
    private View view;
    private ViewHolder viewHolder;


    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ConversationAdapter(Context context, int resource, List<Conversation> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) view.findViewById(R.id.name);
            viewHolder.avatar = (CircleImageView) view.findViewById(R.id.avatar);
            viewHolder.lastMessage = (TextView) view.findViewById(R.id.last_message);
            viewHolder.time = (TextView) view.findViewById(R.id.message_time);
            viewHolder.unread = (TextView) view.findViewById(R.id.unread_num);
            viewHolder.groupName = (TextView) view.findViewById(R.id.group_name);
            viewHolder.con_type = view.findViewById(R.id.con_type);
            view.setTag(viewHolder);
        }

        final Conversation data = getItem(position);
        viewHolder.groupName.setVisibility(View.GONE);
        viewHolder.con_type.setVisibility(View.GONE);
        if (data instanceof FriendshipConversation) {//好友管理消息类
            viewHolder.tvName.setText(data.getName());
            viewHolder.avatar.setImageResource(data.getAvatar());
        } else if (data instanceof GroupManageConversation) {//群管理消息类
            viewHolder.tvName.setText(data.getName());
            viewHolder.avatar.setImageResource(data.getAvatar());
        } else if (data instanceof NomalConversation) {//正常聊天的消息类
            NomalConversation nomalConversation = (NomalConversation) data;
            String faceUrl = nomalConversation.getFaceUrl();
            viewHolder.tvName.setText(nomalConversation.getName());
            viewHolder.groupName.setVisibility(View.GONE);
            TIMConversationType type = nomalConversation.getType();
            if (type == TIMConversationType.C2C) {//单聊
                x.image().bind(viewHolder.avatar, faceUrl, MImageOptions.getCircularImageOptions());
                viewHolder.groupName.setVisibility(View.VISIBLE);
                String groupName = nomalConversation.getGroupName();
                viewHolder.groupName.setText(groupName);
                if (position > 0 && getCount() > position) {
                    Conversation data2 = getItem(position - 1);
                    if (data2 instanceof NomalConversation) {
                        NomalConversation nomalConversation2 = (NomalConversation) data2;
                        String groupName1 = nomalConversation2.getGroupName();
                        if (TextUtils.equals(groupName1, groupName)) {
                            viewHolder.groupName.setVisibility(View.GONE);
                        }
                    }
                }
            } else if (type == TIMConversationType.Group) {
                viewHolder.con_type.setVisibility(View.VISIBLE);
                x.image().bind(viewHolder.avatar, faceUrl, MImageOptions.getGroupAvatarImageOptions());
            }
        }
        viewHolder.lastMessage.setText(data.getLastMessageSummary());
        viewHolder.time.setText(TimeUtil.getTimeStr(data.getLastMessageTime()));
        long unRead = data.getUnreadNum();
        if (unRead <= 0) {
            viewHolder.unread.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.unread.setVisibility(View.VISIBLE);
            String unReadStr = String.valueOf(unRead);
            if (unRead > 99) {
                unReadStr = getContext().getResources().getString(R.string.time_more);
            }
            viewHolder.unread.setText(unReadStr);
        }
        return view;
    }

    public class ViewHolder {
        public TextView tvName;
        public CircleImageView avatar;
        public TextView lastMessage;
        public TextView time;
        public TextView unread;
        public TextView groupName;
        public ImageView con_type;
    }
}
