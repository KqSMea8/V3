package com.huanglong.v3.activities.message;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.message.ContactsAdapter;
import com.huanglong.v3.conversation.ChatActivity;
import com.huanglong.v3.conversation.TemFriendsActivity;
import com.huanglong.v3.model.contacts.ContactsBean;
import com.huanglong.v3.model.contacts.FriRelBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.UserInfoUtils;
import com.tencent.TIMConversationType;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/3/5.
 * 联系人的 Fragment
 */
@SuppressLint("ValidFragment")
public class ContactsFragment extends BaseFragment implements ItemClickListener {


    @ViewInject(R.id.contacts_list)
    private RecyclerView contacts_list;


    private ContactsAdapter contactsAdapter;

    private String cid;
    private ContactsBean contactsBean;

    public ContactsFragment(String cid) {
        this.cid = cid;
    }

    public ContactsFragment() {
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        return view;
    }

    @Override
    protected void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        contacts_list.setLayoutManager(layoutManager);
        contactsAdapter = new ContactsAdapter(1);
        contacts_list.setAdapter(contactsAdapter);

        contactsAdapter.setOnItemClickListener(this);

        requestContacts();
    }

    @Override
    protected void logic() {

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
                    Gson gson = V3Application.getGson();
                    FriRelBean friRelBean = gson.fromJson(json, FriRelBean.class);
                    if (friRelBean != null) {
                        int is_friend = friRelBean.getIs_friend();
                        if (is_friend == 1) {
                            ChatActivity.navToChat(getActivity(), contactsBean.getIdentifier(), TIMConversationType.C2C, contactsBean.getUsername());
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), TemFriendsActivity.class);
                            intent.putExtra("id", contactsBean.getIdentifier());
                            intent.putExtra("name", contactsBean.getNickname());
                            intent.putExtra("is_free", friRelBean.getFee());
                            startActivity(intent);

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

}
