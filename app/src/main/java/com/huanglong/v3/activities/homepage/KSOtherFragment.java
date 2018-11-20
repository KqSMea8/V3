package com.huanglong.v3.activities.homepage;

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
import com.huanglong.v3.adapter.homepage.OtherZuoPinAdapter;
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
 * Created by bin on 2018/4/9.
 * k歌详情的其他作品
 */

public class KSOtherFragment extends BaseFragment implements XRecyclerView.LoadingListener {


    @ViewInject(R.id.k_song_details_list)
    private XRecyclerView details_list;

    private int page = 1;

    private OtherZuoPinAdapter otherZuoPinAdapter;

    private List<KSFBean> ksfAllBeans = new ArrayList<>();


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_k_song_details, container, false);
        return view;
    }

    @Override
    protected void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        details_list.setLayoutManager(layoutManager);
        details_list.setLoadingListener(this);
        otherZuoPinAdapter = new OtherZuoPinAdapter();
        details_list.setAdapter(otherZuoPinAdapter);

        details_list.refresh();
    }

    @Override
    protected void logic() {
        otherZuoPinAdapter.setItemOnClickListener(new ItemClickListener() {
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
     * 其他作品
     */
    private void requestOtherZuoPin() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.music_zuopin_list);
        params.addBodyParameter("music_id", KSonDetActivity.instance.music_id);
        params.addBodyParameter("member_id", KSonDetActivity.instance.member_id);
        params.addBodyParameter("page", page + "");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    if (!TextUtils.isEmpty(json)) {
                        Gson gson = V3Application.getGson();
                        List<KSFBean> ksfBeans = gson.fromJson(json, new TypeToken<LinkedList<KSFBean>>() {
                        }.getType());
                        if (page == 1) {
                            ksfAllBeans.clear();
                            ksfAllBeans.addAll(ksfBeans);
                        } else {
                            ksfAllBeans.addAll(ksfBeans);
                        }
                        otherZuoPinAdapter.setData(ksfAllBeans);
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
                if (page == 1) {
                    details_list.refreshComplete();
                } else {
                    details_list.loadMoreComplete();
                }
            }
        });


    }

    @Override
    public void onRefresh() {
        page = 1;
        requestOtherZuoPin();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestOtherZuoPin();
    }
}
