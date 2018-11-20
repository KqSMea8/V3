package com.huanglong.v3.activities.message;

import android.annotation.SuppressLint;
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
import com.huanglong.v3.adapter.message.YelInfoAdapter;
import com.huanglong.v3.model.YelInfoBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/5/16.
 * 黄页 信息 列表
 */

@SuppressLint("ValidFragment")
public class YelPageInfoListFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    @ViewInject(R.id.yellow_page_list)
    private XRecyclerView yel_info_list;

    private String classId;
    private int page = 1;
    private List<YelInfoBean> yelInfoBeansAll = new ArrayList<>();

    public YelInfoAdapter yelInfoAdapter;


    public YelPageInfoListFragment(String classId) {
        this.classId = classId;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yel_page_con, container, false);
        return view;
    }

    @Override
    protected void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        yel_info_list.setLayoutManager(layoutManager);
        yel_info_list.setLoadingListener(this);
        yelInfoAdapter = new YelInfoAdapter();
        yel_info_list.setAdapter(yelInfoAdapter);


        yel_info_list.refresh();

    }

    @Override
    protected void logic() {

        yelInfoAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                YelInfoBean yelInfoBean = (YelInfoBean) obj;
                Intent intent = new Intent();
                intent.setClass(getActivity(), YelPageInfoDetActivity.class);
                intent.putExtra("yelInfoBean", yelInfoBean);
                startActivity(intent);
            }
        });
    }

    /**
     * 黄页信息分类
     */
    private void requestInfoList() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.huangye_huangye_list);
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("category_id", classId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<YelInfoBean> yelInfoBeans = gson.fromJson(json, new TypeToken<LinkedList<YelInfoBean>>() {
                    }.getType());
                    if (page == 1) {
                        yelInfoBeansAll.clear();
                        yelInfoBeansAll.addAll(yelInfoBeans);
                    } else {
                        yelInfoBeansAll.addAll(yelInfoBeans);
                    }
                    yelInfoAdapter.setData(yelInfoBeansAll);
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
                    yel_info_list.refreshComplete();
                } else {
                    yel_info_list.loadMoreComplete();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        page = 1;
        requestInfoList();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestInfoList();
    }
}
