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
import com.huanglong.v3.activities.homepage.PersonalPageActivity;
import com.huanglong.v3.adapter.mine.FollowAdapter;
import com.huanglong.v3.model.mine.FollowBean;
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
 * Created by bin on 2018/6/9.
 * 视频的关注列表
 */

public class FAVFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    @ViewInject(R.id.follow_list)
    private XRecyclerView follow_list;

    private int page = 1;
    private List<FollowBean> followBeanAll = new ArrayList<>();

    private FollowAdapter followAdapter;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow, container, false);
        return view;
    }

    @Override
    protected void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        follow_list.setLayoutManager(layoutManager);
        followAdapter = new FollowAdapter();
        follow_list.setAdapter(followAdapter);

        follow_list.setLoadingListener(this);
        follow_list.refresh();


    }

    @Override
    protected void logic() {
        followAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                FollowBean followBean = (FollowBean) obj;
                Intent intent = new Intent();
                intent.setClass(getActivity(), PersonalPageActivity.class);
                intent.putExtra("uid", followBean.getMember_id());
                startActivity(intent);
            }
        });
    }

    /**
     * 请求关注列表
     */
    private void requestFollowList() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_my_guanzhu);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("type", "3");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<FollowBean> followBeans = gson.fromJson(json, new TypeToken<LinkedList<FollowBean>>() {
                    }.getType());
                    if (page == 1) {
                        followBeanAll.clear();
                        followBeanAll.addAll(followBeans);
                    } else {
                        followBeanAll.addAll(followBeans);
                    }
                    followAdapter.setData(followBeanAll);
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
                    follow_list.refreshComplete();
                } else {
                    follow_list.loadMoreComplete();
                }
            }
        });


    }

    @Override
    public void onRefresh() {
        page = 1;
        requestFollowList();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestFollowList();
    }
}

