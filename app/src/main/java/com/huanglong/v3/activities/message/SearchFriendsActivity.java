package com.huanglong.v3.activities.message;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.barlibrary.ImmersionBar;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.message.UserAdapter;
import com.huanglong.v3.conversation.ChatActivity;
import com.huanglong.v3.conversation.TemFriendsActivity;
import com.huanglong.v3.model.contacts.FriRelBean;
import com.huanglong.v3.model.contacts.UserInfoBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.KeyBoardUtils;
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
 * Created by bin on 2018/3/10.
 * 搜索好友
 */
@ContentView(R.layout.activity_search_friends)
public class SearchFriendsActivity extends BaseActivity implements ItemClickListener {

    @ViewInject(R.id.search_edt)
    private EditText edt_search;
    @ViewInject(R.id.search_list)
    private RecyclerView search_list;
    @ViewInject(R.id.search_cancel)
    private TextView tv_cancel;

    private UserAdapter userAdapter;
    private List<UserInfoBean> userInfoBeans;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {

        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarDarkFont(true);
//        mImmersionBar;   //所有子类都将继承这些相同的属性
        mImmersionBar.statusBarColor(R.color.transparent)
                .keyboardEnable(true)
                .init();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        search_list.setLayoutManager(layoutManager);
        userAdapter = new UserAdapter();
        search_list.setAdapter(userAdapter);
        userAdapter.setItemClickListener(this);

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = textView.getText().toString().trim();
                    searchData(key);
                    return true;
                }
                return true;
            }
        });
    }

    @Override
    protected void logic() {

    }

    @Event(value = {R.id.search_cancel})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_cancel:
                KeyBoardUtils.hideKeyboard(this);
                SearchFriendsActivity.this.finish();
                break;
        }
    }

    /**
     * 搜索用户
     *
     * @param key
     */
    private void searchData(String key) {
        showDialog();
        RequestParams params = MRequestParams.getNoTokenParams(Api.search_user);
        params.addBodyParameter("nickname", key);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    userInfoBeans = gson.fromJson(json, new TypeToken<LinkedList<UserInfoBean>>() {
                    }.getType());
                    userAdapter.setData(userInfoBeans);
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
                dismissDialog();
            }
        });
    }


    @Override
    public void onItemClick(Object obj, int position) {
        UserInfoBean userInfoBean = (UserInfoBean) obj;
        requestFriendsRelationship(userInfoBean.getId(), userInfoBean.getNickname());

//        Intent intent = new Intent();
//        intent.setClass(SearchFriendsActivity.this, AddFriendActivity.class);
//        intent.putExtra("id", userInfoBean.getIdentifier());
//        intent.putExtra("name", userInfoBean.getNickname());
//        intent.putExtra("is_open", userInfoBean.getIs_open());
//        intent.putExtra("blance", userInfoBean.getBlance());
//        startActivity(intent);
//        SearchFriendsActivity.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            KeyBoardUtils.hideKeyboard(this);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 请求好友关系
     */
    private void requestFriendsRelationship(String friend_id, String nickname) {
        showDialog();
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
                            ChatActivity.navToChat(getActivity(), friend_id, TIMConversationType.C2C, nickname);
                            finish();
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), TemFriendsActivity.class);
                            intent.putExtra("id", friend_id);
                            intent.putExtra("name", nickname);
                            intent.putExtra("is_free", friRelBean.getFee());
                            startActivity(intent);
                            finish();
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
                dismissDialog();
            }
        });

    }
}
