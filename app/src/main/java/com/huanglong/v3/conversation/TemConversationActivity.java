package com.huanglong.v3.conversation;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.activities.mine.MyContactsActivity;
import com.huanglong.v3.adapter.message.TemConversationAdapter;
import com.huanglong.v3.im.model.FileMessage;
import com.huanglong.v3.im.model.ImageMessage;
import com.huanglong.v3.im.model.Message;
import com.huanglong.v3.model.homepage.TemConBean;
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

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/7/19.
 * 临时会话记录
 */
@ContentView(R.layout.activity_tem_conversation)
public class TemConversationActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.tem_con_list)
    private RecyclerView tem_con_list;

    private TemConversationAdapter temConversationAdapter;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("临时会话");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        tem_con_list.setLayoutManager(layoutManager);

        temConversationAdapter = new TemConversationAdapter();
        tem_con_list.setAdapter(temConversationAdapter);


    }

    @Override
    protected void logic() {
        requestMsg();

        temConversationAdapter.setOnClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                TemConBean temConBean = (TemConBean) obj;
                Intent intent = new Intent();
                intent.setClass(TemConversationActivity.this, TemFriendsActivity.class);
                intent.putExtra("id", temConBean.getId());
                intent.putExtra("name", temConBean.getNickname());
                intent.putExtra("is_free", temConBean.getFee());
                startActivity(intent);
            }
        });

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
     * 临时会话记录
     */
    private void requestMsg() {

        RequestParams params = MRequestParams.getNoTokenParams(Api.user_short_message_list);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = new Gson();
                    List<TemConBean> temConBeans = gson.fromJson(json, new TypeToken<LinkedList<TemConBean>>() {
                    }.getType());
                    temConversationAdapter.setData(temConBeans);
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
