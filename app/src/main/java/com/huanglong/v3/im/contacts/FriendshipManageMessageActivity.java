package com.huanglong.v3.im.contacts;

import android.app.Activity;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.im.adapter.FriendManageMessageAdapter;
import com.huanglong.v3.im.model.FriendFuture;
import com.huanglong.v3.im.presenter.FriendshipManagerPresenter;
import com.huanglong.v3.im.viewfeatures.FriendshipMessageView;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.voice.custom.CustomDialog;
import com.tencent.TIMFriendFutureItem;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFutureFriendType;
import com.tencent.TIMValueCallBack;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友申请处理类
 */
@ContentView(R.layout.activity_friendship_manage_message)
public class FriendshipManageMessageActivity extends BaseActivity implements FriendshipMessageView {

    @ViewInject(R.id.list)
    private ListView listView;
    @ViewInject(R.id.title_name)
    private TextView tv_title;

    private FriendshipManagerPresenter presenter;
    private List<FriendFuture> list = new ArrayList<>();
    private FriendManageMessageAdapter adapter;
    private final int FRIENDSHIP_REQ = 100;
    private int index;


    public static FriendshipManageMessageActivity instance;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("好友申请");
    }

    @Override
    protected void logic() {
        instance = this;
        adapter = new FriendManageMessageAdapter(this, R.layout.item_two_line, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.get(position).getType() == TIMFutureFriendType.TIM_FUTURE_FRIEND_PENDENCY_IN_TYPE) {
                    index = position;
//                    Intent intent = new Intent(FriendshipManageMessageActivity.this, FriendshipHandleActivity.class);
//                    intent.putExtra("id", list.get(position).getIdentify());
//                    intent.putExtra("word", list.get(position).getMessage());
//                    startActivityForResult(intent, FRIENDSHIP_REQ);
                } else if (list.get(position).getType() == TIMFutureFriendType.TIM_FUTURE_FRIEND_DECIDE_TYPE) {

                }

            }
        });

        registerForContextMenu(listView);


        presenter = new FriendshipManagerPresenter(this);
        presenter.getFriendshipMessage();

        adapter.setOnItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                FriendFuture data = (FriendFuture) obj;
                if (type == 1) {

                    CustomDialog customDialog = new CustomDialog(FriendshipManageMessageActivity.this, "是否同意该好友？\n备注：" + data.getMessage()) {
                        @Override
                        public void EnsureEvent() {
                            FriendshipManagerPresenter.acceptFriendRequest(data.getIdentify(), new TIMValueCallBack<TIMFriendResult>() {
                                @Override
                                public void onError(int i, String s) {

                                }

                                @Override
                                public void onSuccess(TIMFriendResult timFriendResult) {
                                    ProfileActivity.navToProfile(FriendshipManageMessageActivity.this, data.getIdentify());
                                    data.setType(TIMFutureFriendType.TIM_FUTURE_FRIEND_DECIDE_TYPE);
                                    adapter.notifyDataSetChanged();
                                    FriendshipManageMessageActivity.this.finish();
                                }
                            });
                            dismiss();
                        }
                    };
                    customDialog.setCanceledOnTouchOutside(false);
                    customDialog.show();
                } else {
                    ProfileActivity.navToProfile(FriendshipManageMessageActivity.this, data.getIdentify());
                    FriendshipManageMessageActivity.this.finish();
                }
            }

            @Override
            public void onItemViewClick(Object obj, int position, int type, View view) {

            }
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        FriendFuture friendFuture = list.get(info.position);
        menu.add(0, 1, Menu.NONE, getString(R.string.del_message));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        FriendFuture friendFuture = list.get(info.position);
        switch (item.getItemId()) {
            case 1:
                if (friendFuture != null) {

//                    if (presenter.delConversation(conversation.getType(), conversation.getIdentify())) {
//                        conversationAllList.remove(conversation);
//                        if (conversationList.contains(conversation)) {
//                            conversationList.remove(conversation);
//                        }
                    adapter.notifyDataSetChanged();
//                    }
                }
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Event(value = {R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

    /**
     * 获取好友关系链管理最后一条系统消息的回调
     *
     * @param message     最后一条消息
     * @param unreadCount 未读数
     */
    @Override
    public void onGetFriendshipLastMessage(TIMFriendFutureItem message, long unreadCount) {

    }

    /**
     * 获取好友关系链管理最后一条系统消息的回调
     *
     * @param message 消息列表
     */
    @Override
    public void onGetFriendshipMessage(List<TIMFriendFutureItem> message) {
        if (message != null && message.size() != 0) {
            for (TIMFriendFutureItem item : message) {
                list.add(new FriendFuture(item));
            }
            presenter.readFriendshipMessage(message.get(0).getAddTime());

        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FRIENDSHIP_REQ) {
            if (resultCode == RESULT_OK) {
                if (index >= 0 && index < list.size()) {
                    boolean isAccept = data.getBooleanExtra("operate", true);
                    if (isAccept) {
                        list.get(index).setType(TIMFutureFriendType.TIM_FUTURE_FRIEND_DECIDE_TYPE);
                    } else {
                        list.remove(index);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    /**
     * 舒心数据
     */
    public void refreshData() {
        if (presenter != null) {
            presenter.getFriendshipMessage();
        }
    }
}
