package com.huanglong.v3.activities.message;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.message.ContactsAdapter;
import com.huanglong.v3.conversation.ChatActivity;
import com.huanglong.v3.model.contacts.ContactsBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.tencent.TIMConversationType;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/3/16.
 * 好友列表
 */
@ContentView(R.layout.activity_friends)
public class FriendsActivity extends BaseActivity implements ItemClickListener {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.friends_list)
    private RecyclerView friends_list;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;

    private ContactsAdapter contactsAdapter;
    private List<ContactsBean> contactsBean;

    private List<ContactsBean> contactsSelected = new ArrayList<>();
    private List<String> identifiers = new ArrayList<>();

    public static FriendsActivity instance;


    private int flag;//1.创建群 ,2.群加人


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        instance = this;
        tv_title.setText("联系人");


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        friends_list.setLayoutManager(layoutManager);
        contactsAdapter = new ContactsAdapter(2);
        friends_list.setAdapter(contactsAdapter);

        contactsAdapter.setOnItemClickListener(this);


    }

    @Override
    protected void logic() {
        requestFriends();

        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", 1);
        if (flag == 1) {
            tv_right.setText("发起群聊");
        } else if (flag == 2) {
            tv_right.setText("确认");
        }
    }

    @Event(value = {R.id.title_back, R.id.title_tv_right})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                FriendsActivity.this.finish();
                break;
            case R.id.title_tv_right:
                getSelContacts();
                if (contactsSelected.size() == 0) {
                    ToastUtils.showToast("请选择联系人");
                } else {
                    if (flag == 1) {
                        createGroup();
                    } else if (flag == 2) {
                        selFriends();
                    }

                }
                break;
        }
    }


    /**
     * 请求好友列表
     */
    private void requestFriends() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.queryqunfriends);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    contactsBean = gson.fromJson(json, new TypeToken<LinkedList<ContactsBean>>() {
                    }.getType());
                    if (contactsBean != null) {
                        contactsAdapter.setData(contactsBean);
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
     * 获取选中的联系人
     */
    private void getSelContacts() {
        if (contactsBean == null) {
            return;
        }
        contactsSelected.clear();
        identifiers.clear();
        for (int i = 0; i < contactsBean.size(); i++) {
            boolean selected = contactsBean.get(i).isSelected();
            if (selected) {
                contactsSelected.add(contactsBean.get(i));
                identifiers.add(contactsBean.get(i).getIdentifier());
            }
        }


    }

    @Override
    public void onItemClick(Object obj, int position) {
        ContactsBean contactsBean = (ContactsBean) obj;
        boolean selected = contactsBean.isSelected();
        if (selected) {
            contactsBean.setSelected(false);
        } else {
            contactsBean.setSelected(true);
        }
        contactsAdapter.notifyDataSetChanged();

    }

    /**
     * 创建群组
     */
    private void createGroup() {
        if (contactsSelected.size() == 1) {
            ChatActivity.navToChat(this, contactsSelected.get(0).getIdentifier(), TIMConversationType.C2C, contactsSelected.get(0).getUsername());
        } else {
            String groupName = "";
            for (int i = 0; i < contactsSelected.size(); i++) {
                groupName = groupName + "," + contactsSelected.get(i).getUsername();
                if (i == 3) {
                    break;
                }
            }
            Intent intent = new Intent();
            intent.setClass(FriendsActivity.this, CreateGroupActivity.class);
            intent.putExtra("identifiers", (Serializable) identifiers);
            startActivity(intent);

        }
    }

    /**
     * 选择朋友
     */
    private void selFriends() {
        Intent intent = new Intent();
        intent.putExtra("identifiers", (Serializable) identifiers);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }
}
