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
import com.huanglong.v3.im.contacts.AddFriendActivity;
import com.huanglong.v3.model.contacts.ContactsBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.UserInfoUtils;
import com.tencent.TIMConversationType;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/12.
 * 职业分类页面
 */
@ContentView(R.layout.activity_job_class)
public class JobClassActivity extends BaseActivity implements ItemClickListener {


    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.job_class_list)
    private RecyclerView contacts_list;

    private ContactsAdapter contactsAdapter;
    private String cid;
    private ContactsBean contactsBean;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        contacts_list.setLayoutManager(layoutManager);
        contactsAdapter = new ContactsAdapter(1);
        contacts_list.setAdapter(contactsAdapter);

        contactsAdapter.setOnItemClickListener(this);

    }

    @Override
    protected void logic() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        tv_title.setText(title);
        cid = intent.getStringExtra("cid");

        requestContacts();
    }

    @Event(value = {R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                JobClassActivity.this.finish();
                break;
        }
    }

    /**
     * 请求联系人接口
     */
    private void requestContacts() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.getMemberListByCarrer);
        params.addBodyParameter("cid", cid);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<ContactsBean> contactsBeans = gson.fromJson(json, new TypeToken<LinkedList<ContactsBean>>() {
                    }.getType());
                    if (contactsBeans != null) {
                        contactsAdapter.setData(contactsBeans);
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

    @Override
    public void onItemClick(Object obj, int position) {
        contactsBean = (ContactsBean) obj;
        requestFriendsRelationship(contactsBean.getId());
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
                    if (TextUtils.equals("success", json)) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), AddFriendActivity.class);
                        intent.putExtra("id", contactsBean.getIdentifier());
                        intent.putExtra("name", contactsBean.getNickname());
                        intent.putExtra("is_open", contactsBean.getIs_open());
                        intent.putExtra("blance", contactsBean.getBlance());
                        startActivity(intent);
                    } else {
                        ChatActivity.navToChat(getActivity(), contactsBean.getIdentifier(), TIMConversationType.C2C, contactsBean.getUsername());
                        JobClassActivity.this.finish();
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


}
