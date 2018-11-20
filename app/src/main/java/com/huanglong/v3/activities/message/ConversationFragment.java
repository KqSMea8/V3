package com.huanglong.v3.activities.message;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.im.adapter.ConversationAdapter;
import com.huanglong.v3.im.model.Conversation;
import com.huanglong.v3.im.model.CustomMessage;
import com.huanglong.v3.im.model.FriendshipConversation;
import com.huanglong.v3.im.model.GroupManageConversation;
import com.huanglong.v3.im.model.MessageFactory;
import com.huanglong.v3.im.model.NomalConversation;
import com.huanglong.v3.im.presenter.ConversationPresenter;
import com.huanglong.v3.im.presenter.FriendshipManagerPresenter;
import com.huanglong.v3.im.presenter.GroupManagerPresenter;
import com.huanglong.v3.im.utils.PushUtil;
import com.huanglong.v3.im.viewfeatures.ConversationView;
import com.huanglong.v3.im.viewfeatures.FriendshipMessageView;
import com.huanglong.v3.im.viewfeatures.GroupManageMessageView;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendFutureItem;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMMessage;
import com.tencent.TIMGroupPendencyItem;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/3/5.
 * 会话的 Fragment
 */

public class ConversationFragment extends BaseFragment implements ConversationView, FriendshipMessageView, GroupManageMessageView {

    @ViewInject(R.id.conversation_list)
    private ListView listView;

    private List<Conversation> conversationList = new LinkedList<>();
    private List<Conversation> conversationAllList = new LinkedList<>();
    private ConversationAdapter adapter;
    private ConversationPresenter presenter;
    private FriendshipManagerPresenter friendshipManagerPresenter;
    private GroupManagerPresenter groupManagerPresenter;
    private List<String> groupList;
    private FriendshipConversation friendshipConversation;
    private GroupManageConversation groupManageConversation;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        return view;
    }

    @Override
    protected void initView() {

        adapter = new ConversationAdapter(getActivity(), R.layout.item_conversation, conversationAllList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                conversationAllList.get(position).navToDetail(getActivity());
                if (conversationAllList.get(position) instanceof GroupManageConversation) {
                    groupManagerPresenter.getGroupManageLastMessage();
                }

            }
        });
        friendshipManagerPresenter = new FriendshipManagerPresenter(this);
        groupManagerPresenter = new GroupManagerPresenter(this);
        presenter = new ConversationPresenter(this);
        presenter.getConversation();
        registerForContextMenu(listView);

    }

    @Override
    protected void logic() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
        PushUtil.getInstance().reset();
    }

    /**
     * 获取群管理最后一条系统消息的回调
     *
     * @param message     最后一条消息
     * @param unreadCount 未读数
     */
    @Override
    public void onGetGroupManageLastMessage(TIMGroupPendencyItem message, long unreadCount) {
        if (groupManageConversation == null) {
            groupManageConversation = new GroupManageConversation(message);
            conversationList.add(groupManageConversation);
        } else {
            groupManageConversation.setLastMessage(message);
        }
        groupManageConversation.setUnreadCount(unreadCount);
        sort();
        refresh();
    }

    /**
     * 获取群管理系统消息的回调
     *
     * @param message 分页的消息列表
     */
    @Override
    public void onGetGroupManageMessage(List<TIMGroupPendencyItem> message) {
        groupManagerPresenter.getGroupManageLastMessage();
    }

    /**
     * 获取好友关系链管理系统最后一条消息的回调
     *
     * @param message     最后一条消息
     * @param unreadCount 未读数
     */
    @Override
    public void onGetFriendshipLastMessage(TIMFriendFutureItem message, long unreadCount) {
        if (friendshipConversation == null) {
            friendshipConversation = new FriendshipConversation(message);
            conversationList.add(friendshipConversation);
        } else {
            friendshipConversation.setLastMessage(message);
        }
        friendshipConversation.setUnreadCount(unreadCount);
        sort();
        refresh();
    }

    /**
     * 获取好友关系链管理最后一条系统消息的回调
     *
     * @param message 消息列表
     */
    @Override
    public void onGetFriendshipMessage(List<TIMFriendFutureItem> message) {
        friendshipManagerPresenter.getFriendshipLastMessage();
    }


    /**
     * 初始化界面或刷新界面
     *
     * @param conversationList
     */
    @Override
    public void initView(List<TIMConversation> conversationList) {
        this.conversationList.clear();
        groupList = new ArrayList<>();
        for (TIMConversation item : conversationList) {
            switch (item.getType()) {
                case C2C:
                case Group:
                    this.conversationList.add(new NomalConversation(item));
                    groupList.add(item.getPeer());
                    break;
            }
        }
        friendshipManagerPresenter.getFriendshipLastMessage();
        groupManagerPresenter.getGroupManageLastMessage();
    }

    /**
     * 更新最新消息显示
     *
     * @param message 最后一条消息
     */
    @Override
    public void updateMessage(TIMMessage message) {
        if (message == null) {
            adapter.notifyDataSetChanged();
            return;
        }
        if (message.getConversation().getType() == TIMConversationType.System) {
            groupManagerPresenter.getGroupManageLastMessage();
            return;
        }
        if (MessageFactory.getMessage(message) instanceof CustomMessage) return;
        NomalConversation conversation = new NomalConversation(message.getConversation());
        Iterator<Conversation> iterator = conversationList.iterator();
        while (iterator.hasNext()) {
            Conversation c = iterator.next();
            if (conversation.equals(c)) {
                conversation = (NomalConversation) c;
                iterator.remove();
                break;
            }
        }
        conversation.setLastMessage(MessageFactory.getMessage(message));
        conversationList.add(conversation);
        sort();
        refresh();
    }

    /**
     * 更新好友关系链消息
     */
    @Override
    public void updateFriendshipMessage() {
        friendshipManagerPresenter.getFriendshipLastMessage();
    }

    /**
     * 删除会话
     *
     * @param identify
     */
    @Override
    public void removeConversation(String identify) {
        Iterator<Conversation> iterator = conversationAllList.iterator();
        while (iterator.hasNext()) {
            Conversation conversation = iterator.next();
            if (conversation.getIdentify() != null && conversation.getIdentify().equals(identify)) {
                iterator.remove();
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * 更新群信息
     *
     * @param info
     */
    @Override
    public void updateGroupInfo(TIMGroupCacheInfo info) {
        for (Conversation conversation : conversationAllList) {
            if (conversation.getIdentify() != null && conversation.getIdentify().equals(info.getGroupInfo().getGroupId())) {
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * 刷新
     */
    @Override
    public void refresh() {
        adapter.notifyDataSetChanged();
        //TODO 设置未读书
//        if (getActivity() instanceof  MainActivity)
//            ((MainActivity) getActivity()).setMsgUnread(getTotalUnreadNum() == 0);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Conversation conversation = conversationAllList.get(info.position);
        if (conversation instanceof NomalConversation) {
            menu.add(0, 1, Menu.NONE, getString(R.string.conversation_del));
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        NomalConversation conversation = (NomalConversation) conversationAllList.get(info.position);
        switch (item.getItemId()) {
            case 1:
                if (conversation != null) {
                    if (presenter.delConversation(conversation.getType(), conversation.getIdentify())) {
                        conversationAllList.remove(conversation);
                        if (conversationList.contains(conversation)) {
                            conversationList.remove(conversation);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private long getTotalUnreadNum() {
        long num = 0;
        for (Conversation conversation : conversationAllList) {
            num += conversation.getUnreadNum();
        }
        return num;
    }

    /**
     * 重新排序，归类
     */
    private void sort() {
        conversationAllList.clear();
        Collections.sort(conversationList);
        for (int i = 0; i < conversationList.size(); i++) {
            Conversation conversation = conversationList.get(i);
            if (conversation instanceof FriendshipConversation || conversation instanceof GroupManageConversation) {
                conversationAllList.add(0, conversation);
            } else {
                NomalConversation nomalConversation = (NomalConversation) conversation;
                if (nomalConversation.getType() != TIMConversationType.C2C) {
                    conversationAllList.add(conversation);
                }
            }
        }

        for (int i = 0; i < conversationList.size(); i++) {
            Conversation conversation = conversationList.get(i);
            if (conversation instanceof NomalConversation) {
                NomalConversation nomalConversation = (NomalConversation) conversation;
                String groupName = nomalConversation.getGroupName();
                if (TextUtils.equals("亲友", groupName)) {
                    conversationAllList.add(conversation);
                }
            }
        }

        for (int i = 0; i < conversationList.size(); i++) {
            Conversation conversation = conversationList.get(i);
            if (conversation instanceof NomalConversation) {
                NomalConversation nomalConversation = (NomalConversation) conversation;
                String groupName = nomalConversation.getGroupName();
                if (TextUtils.equals("朋友", groupName)) {
                    conversationAllList.add(conversation);
                }
            }
        }


//        Map<String, List<Conversation>> map = new HashMap<>();
//        List<Conversation> stuList = null;
//        for (Conversation stu : conversationList) {
//            String identify = stu.getIdentify();
//            FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
//            if (profile != null) {
//                if (map.get(profile.getGroupName()) != null) {
//                    stuList = map.get(profile.getGroupName());
//                    stuList.add(stu);
//                } else {
//                    stuList = new ArrayList<>();
//                    stuList.add(stu);
//
//                }
//                map.put(profile.getGroupName(), stuList);
//            }
//        }
//
//        List<Conversation> resultList = new ArrayList<>();
//        for (Map.Entry<String, List<Conversation>> entry : map.entrySet()) {
//            resultList.addAll(entry.getValue());
//        }
//        conversationAllList.addAll(resultList);
    }

}
