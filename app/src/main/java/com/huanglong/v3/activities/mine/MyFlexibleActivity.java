package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.mine.FlexibleAdapter;
import com.huanglong.v3.model.mine.FlexibleBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.UserInfoUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/23.
 * 我的活动
 */
@ContentView(R.layout.activity_my_flexible)
public class MyFlexibleActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.my_flexible_list)
    private XRecyclerView flexible_list;


    private FlexibleAdapter flexibleAdapter;

    private List<FlexibleBean> flexibleBeanAll = new ArrayList<>();

    private int page = 1;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("我的活动");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        flexible_list.setLayoutManager(layoutManager);
        flexibleAdapter = new FlexibleAdapter();
        flexible_list.setAdapter(flexibleAdapter);
        flexible_list.setLoadingListener(this);

    }

    @Override
    protected void logic() {
        flexible_list.refresh();

        flexibleAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                FlexibleBean flexibleBean = (FlexibleBean) obj;
                Intent intent = new Intent();
                intent.setClass(getActivity(), FlexibleDetailsActivity.class);
                intent.putExtra("flexible_id", flexibleBean.getId());
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
     * 请求活动列表
     */
    private void requestFlexible() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.activity_list);
        params.addBodyParameter("type", "2");
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<FlexibleBean> flexibleBeans = gson.fromJson(json, new TypeToken<LinkedList<FlexibleBean>>() {
                    }.getType());
                    if (page == 1) {
                        flexibleBeanAll.clear();
                        flexibleBeanAll.addAll(flexibleBeans);
                    } else {
                        flexibleBeanAll.addAll(flexibleBeans);
                    }
                    flexibleAdapter.setData(flexibleBeanAll);
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
                if (page == 1) {
                    flexible_list.refreshComplete();
                } else {
                    flexible_list.loadMoreComplete();
                }
            }
        });

    }

    @Override
    public void onRefresh() {
        page = 1;
        requestFlexible();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestFlexible();
    }
}
