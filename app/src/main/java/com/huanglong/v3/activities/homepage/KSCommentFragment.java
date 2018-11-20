package com.huanglong.v3.activities.homepage;

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
import com.huanglong.v3.adapter.homepage.KSComAdapter;
import com.huanglong.v3.model.homepage.KSComBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/9.
 * k歌详情的评论
 */

public class KSCommentFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    @ViewInject(R.id.k_song_details_list)
    private XRecyclerView song_details_list;

    private KSComAdapter ksComAdapter;
    private int page = 1;
    private List<KSComBean> ksComBeanList = new ArrayList<>();

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_k_song_details, container, false);
        return view;
    }

    @Override
    protected void initView() {


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        song_details_list.setLayoutManager(layoutManager);
        ksComAdapter = new KSComAdapter();

        song_details_list.setAdapter(ksComAdapter);
        song_details_list.setPullRefreshEnabled(false);
        song_details_list.setLoadingMoreEnabled(true);
        song_details_list.setLoadingListener(this);
        requestComment();
    }

    @Override
    protected void logic() {

    }

    /**
     * 请求评论列表
     */
    public void requestComment() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.music_comment_list);
        params.addBodyParameter("music_id", KSonDetActivity.instance.music_id);
        params.addBodyParameter("page", page + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<KSComBean> ksComBeans = gson.fromJson(json, new TypeToken<LinkedList<KSComBean>>() {
                    }.getType());
                    if (page == 1) {
                        ksComBeanList.clear();
                        ksComBeanList.addAll(ksComBeans);
                    } else {
                        ksComBeanList.addAll(ksComBeans);
                    }
                    ksComAdapter.setData(ksComBeanList);


//                    total
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
                    song_details_list.refreshComplete();
                } else {
                    song_details_list.loadMoreComplete();
                }
            }
        });
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        page++;
        requestComment();
    }
}
