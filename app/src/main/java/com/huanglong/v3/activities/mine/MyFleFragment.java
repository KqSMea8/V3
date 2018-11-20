package com.huanglong.v3.activities.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragment;
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
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/16.
 * 官方大赛
 */

public class MyFleFragment extends BaseFragment implements XRecyclerView.LoadingListener {
    @ViewInject(R.id.flexible_list)
    private XRecyclerView flexible_list;

    private FlexibleAdapter flexibleAdapter;

    private List<FlexibleBean> flexibleBeanAll = new ArrayList<>();

    private int page = 1;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_official_flexible, container, false);
        return view;
    }

    @Override
    protected void initView() {

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
