package com.huanglong.v3.activities.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/3/24.
 * 个人主页的K歌
 */

public class PersonalKFragment extends BaseFragment {
    @ViewInject(R.id.personal_list)
    private RecyclerView personal_list;

    private KSHAdapter kshAdapter;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_live, container, false);
        return view;
    }

    @Override
    protected void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        personal_list.setLayoutManager(layoutManager);
        kshAdapter = new KSHAdapter();
        personal_list.setAdapter(kshAdapter);
        requestKSong();
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
     * 我的K歌
     */
    private void requestKSong() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.mymusic_recommend);
        params.addBodyParameter("member_id", PersonalPageActivity.instance.follower_id);
        params.addBodyParameter("page", "1");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<KSFBean> ksfBeans = gson.fromJson(json, new TypeToken<LinkedList<KSFBean>>() {
                    }.getType());
                    kshAdapter.setData(ksfBeans);
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
