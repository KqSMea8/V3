package com.huanglong.v3.activities.homepage;

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
import com.huanglong.v3.adapter.homepage.GiftListAdapter;
import com.huanglong.v3.model.homepage.GiftListBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/6/6.
 * 离去榜
 */
@ContentView(R.layout.activity_gift_list)
public class GiftListActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.gift_list)
    private RecyclerView gift_list;

    private String music_id;

    private GiftListAdapter giftListAdapter;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("礼物榜");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gift_list.setLayoutManager(layoutManager);
        giftListAdapter = new GiftListAdapter();
        gift_list.setAdapter(giftListAdapter);

    }

    @Override
    protected void logic() {

        Intent intent = getIntent();
        music_id = intent.getStringExtra("music_id");
        requestGiftList();


        giftListAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                GiftListBean giftListBean = (GiftListBean) obj;
                Intent intent = new Intent();
                intent.setClass(GiftListActivity.this, PersonalPageActivity.class);
                intent.putExtra("uid", giftListBean.getFollower_id());
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
     * 请求礼物列表
     */
    private void requestGiftList() {

        showDialog();
        RequestParams params = MRequestParams.getNoTokenParams(Api.music_gift_list);
        params.addBodyParameter("music_id", music_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<GiftListBean> giftListBeans = gson.fromJson(json, new TypeToken<LinkedList<GiftListBean>>() {
                    }.getType());
                    giftListAdapter.setData(giftListBeans);
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
