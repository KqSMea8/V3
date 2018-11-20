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
import com.huanglong.v3.adapter.message.AllClassAdapter;
import com.huanglong.v3.model.home.JobClassBean;
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
 * Created by bin on 2018/3/13.
 * 全部分类
 */
@ContentView(R.layout.activity_all_class)
public class AllClassActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.all_class_list)
    private RecyclerView all_class_list;

    private AllClassAdapter allClassAdapter;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("全部分类");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        all_class_list.setLayoutManager(layoutManager);
        allClassAdapter = new AllClassAdapter();
        all_class_list.setAdapter(allClassAdapter);


    }

    @Override
    protected void logic() {
        requestJobClass();

        allClassAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                JobClassBean jobClassBean = (JobClassBean) obj;
                Intent intent = new Intent();
                intent.setClass(AllClassActivity.this, JobClassActivity.class);
                intent.putExtra("title", jobClassBean.getName());
                intent.putExtra("cid", jobClassBean.getId());
                startActivity(intent);
            }
        });
    }

    @Event(value = {R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                AllClassActivity.this.finish();
                break;
        }
    }

    /**
     * 请求职业分类
     */
    private void requestJobClass() {

        RequestParams params = MRequestParams.getNoTokenParams(Api.job_class);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<JobClassBean> jobClassBeans = gson.fromJson(json, new TypeToken<LinkedList<JobClassBean>>() {
                    }.getType());
                    if (jobClassBeans != null) {
                        allClassAdapter.setData(jobClassBeans);
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
