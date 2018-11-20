package com.huanglong.v3.activities.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.homepage.KSHAdapter;
import com.huanglong.v3.model.homepage.KSFBean;
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
 * Created by bin on 2018/4/7.
 * K歌热门
 */

public class KSHFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    @ViewInject(R.id.k_song_hot_list)
    private XRecyclerView song_list;

    private int PAGE = 1;
    private List<KSFBean> ksfAllBeans = new ArrayList<>();

    private KSHAdapter kshAdapter;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_k_song_hot, container, false);
        return view;
    }

    @Override
    protected void initView() {

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        song_list.setLayoutManager(layoutManager);
        song_list.setLoadingListener(this);
        kshAdapter = new KSHAdapter();
        song_list.setAdapter(kshAdapter);
        song_list.refresh();
    }

    @Override
    protected void logic() {
        kshAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                KSFBean ksfBean = (KSFBean) obj;
                Intent intent = new Intent();
                intent.setClass(getActivity(), KSonDetActivity.class);
                intent.putExtra("ksfBean", ksfBean);
                startActivity(intent);
            }
        });
    }

    /**
     * 热门K歌
     */
    private void requestHotSong() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.music_hot_list);
        params.addBodyParameter("page", PAGE + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<KSFBean> ksfBeans = gson.fromJson(json, new TypeToken<LinkedList<KSFBean>>() {
                    }.getType());
                    if (PAGE == 1) {
                        ksfAllBeans.clear();
                        ksfAllBeans.addAll(ksfBeans);
                    } else {
                        ksfAllBeans.addAll(ksfBeans);
                    }
                    kshAdapter.setData(ksfAllBeans);
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
                if (PAGE == 1) {
                    song_list.refreshComplete();
                } else {
                    song_list.loadMoreComplete();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        PAGE = 1;
        requestHotSong();
    }

    @Override
    public void onLoadMore() {
        PAGE++;
        requestHotSong();
    }
}
