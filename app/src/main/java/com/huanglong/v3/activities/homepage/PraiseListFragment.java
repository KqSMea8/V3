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
import com.huanglong.v3.adapter.mine.FollowAdapter;
import com.huanglong.v3.model.mine.FollowBean;
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
 * Created by bin on 2018/6/11.
 * K歌详情的赞列表
 */

public class PraiseListFragment extends BaseFragment {

    @ViewInject(R.id.praise_list)
    private RecyclerView praise_list;

    private String music_id;

    private FollowAdapter followAdapter;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_praise_list, container, false);
        return view;
    }

    @Override
    protected void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        praise_list.setLayoutManager(layoutManager);

        followAdapter = new FollowAdapter();
        praise_list.setAdapter(followAdapter);
        requestPraiseList();
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
     * 请求赞列表
     */
    private void requestPraiseList() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.music_zan_list);
        params.addBodyParameter("music_id", KSonDetActivity.instance.music_id);

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
