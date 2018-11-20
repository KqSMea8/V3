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
import com.huanglong.v3.adapter.mine.FollowAdapter;
import com.huanglong.v3.model.mine.FollowBean;
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
 * Created by bin on 2018/6/9.
 * 赞列表
 */
@ContentView(R.layout.activity_praise)
public class PraiseListActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.praise_list)
    private RecyclerView praise_list;


    private String music_id;

    private FollowAdapter followAdapter;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("赞列表");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        praise_list.setLayoutManager(layoutManager);

        followAdapter = new FollowAdapter();
        praise_list.setAdapter(followAdapter);

    }

    @Override
    protected void logic() {

        Intent intent = getIntent();
        music_id = intent.getStringExtra("music_id");

        requestPraiseList();

        followAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                FollowBean followBean = (FollowBean) obj;
                Intent intent = new Intent();
                intent.setClass(PraiseListActivity.this, PersonalPageActivity.class);
                intent.putExtra("uid", followBean.getMember_id());
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
     * 请求赞列表
     */
    private void requestPraiseList() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.music_zan_list);
        params.addBodyParameter("music_id", music_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<FollowBean> followBeans = gson.fromJson(json, new TypeToken<LinkedList<FollowBean>>() {
                    }.getType());
                    followAdapter.setData(followBeans);
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
