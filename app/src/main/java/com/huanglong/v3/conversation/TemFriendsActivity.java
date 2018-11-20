package com.huanglong.v3.conversation;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.homepage.PersonalPageActivity;
import com.huanglong.v3.adapter.message.TemChatAdapter;
import com.huanglong.v3.im.contacts.AddFriendActivity;
import com.huanglong.v3.im.model.TextMessage;
import com.huanglong.v3.model.contacts.FriRelBean;
import com.huanglong.v3.model.homepage.TemChatBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.CommentChatView;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.KeyBoardUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.view.CommentInput;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tencent.TIMConversationType;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/5/11.
 * 临时好友的聊天页面
 */
@ContentView(R.layout.activity_tem_friends)
public class TemFriendsActivity extends BaseActivity implements XRecyclerView.LoadingListener, CommentChatView {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;
    @ViewInject(R.id.tem_chat_list)
    private XRecyclerView chat_list;
    @ViewInject(R.id.pop_comment_content)
    private EditText edt_content;
    @ViewInject(R.id.input_panel)
    private CommentInput input;


    private TemChatAdapter temChatAdapter;

    private String friendId;
    private int is_free;
    private String friendName;
    private int is_friend = 0;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chat_list.setLayoutManager(layoutManager);
        chat_list.setLoadingListener(this);
        chat_list.setLoadingMoreEnabled(false);
        temChatAdapter = new TemChatAdapter();
        chat_list.setAdapter(temChatAdapter);


        Intent intent = getIntent();
        friendId = intent.getStringExtra("id");
        friendName = intent.getStringExtra("name");
        is_free = intent.getIntExtra("is_free", 0);
//        intent.getStringExtra("blance");
        tv_title.setText(friendName);

        input.setChatView(this);
//        registerForContextMenu(chat_list);

    }

    @Override
    protected void logic() {
        chat_list.refresh();
        requestFriendsRelationship(friendId);

        temChatAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                TemChatBean temChatBean = (TemChatBean) obj;
                Intent intent = new Intent();
                intent.setClass(TemFriendsActivity.this, PersonalPageActivity.class);
                intent.putExtra("uid", temChatBean.getUser_id());
                startActivity(intent);
            }
        });

    }

    @Event(value = {R.id.title_back, R.id.title_tv_right, R.id.pop_comment_send})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_tv_right:
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), AddFriendActivity.class);
//                intent.putExtra("id", friendId);
//                intent.putExtra("name", friendName);
//                intent.putExtra("is_open", is_free);
//                startActivity(intent);
                if (is_friend == 1) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PersonalPageActivity.class);
                    intent.putExtra("uid", friendId);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), AddFriendActivity.class);
                    intent.putExtra("id", friendId);
                    intent.putExtra("name", friendName);
                    intent.putExtra("is_open", is_free);
                    startActivity(intent);
                }
                break;
            case R.id.pop_comment_send:
                KeyBoardUtils.hideKeyboard(this);
                String content = edt_content.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    sendChat(content);
                    edt_content.setText("");
                }
                break;

        }
    }


    @Override
    public void onRefresh() {
        requestChatList();
    }

    @Override
    public void onLoadMore() {

    }

    /**
     * 请求聊天列表
     */
    private void requestChatList() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_temporary_friend_list);
        params.addBodyParameter("reciever", friendId);
        params.addBodyParameter("sender", UserInfoUtils.getUid());

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<TemChatBean> temChatBeans = gson.fromJson(json, new TypeToken<LinkedList<TemChatBean>>() {
                    }.getType());
                    temChatAdapter.setData(temChatBeans);
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
                chat_list.refreshComplete();
            }
        });

    }

    /**
     * 发送聊天消息
     *
     * @param content
     */
    private void sendChat(String content) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_temporary_friend_add_chat);
        params.addBodyParameter("sender", UserInfoUtils.getUid());
        params.addBodyParameter("reciever", friendId);
        params.addBodyParameter("content", content);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    requestChatList();
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
    public void sendText() {
        KeyBoardUtils.hideKeyboard(getActivity());
        TextMessage msg = new TextMessage(input.getText());
        String msgStr = TextMessage.getMsgStr(msg.getMessage(), getActivity()).toString();
        input.setText("");
        if (!TextUtils.isEmpty(msgStr)) {
            sendChat(msgStr);
        }
    }

    @Override
    public void sending() {

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        delTemChat(temChatAdapter.getChatId(temChatAdapter.getPosition()));
        return super.onContextItemSelected(item);
    }


    /**
     * 请求好友关系
     */
    private void requestFriendsRelationship(String friend_id) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.queryfriendlyrelation);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("friend_id", friend_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    FriRelBean friRelBean = gson.fromJson(json, FriRelBean.class);
                    if (friRelBean != null) {
                        is_friend = friRelBean.getIs_friend();
                        if (is_friend == 1) {
                            tv_right.setText("查看个人主页");
                        } else {
                            tv_right.setText("加好友");
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
     * 删除临时聊天消息
     *
     * @param id
     */
    private void delTemChat(String id) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_friend_dailog_delete);
        params.addBodyParameter("id", id);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    temChatAdapter.removeItem(temChatAdapter.getPosition());
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

