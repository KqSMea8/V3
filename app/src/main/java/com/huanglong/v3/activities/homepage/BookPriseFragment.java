package com.huanglong.v3.activities.homepage;

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

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/6/20.
 * 音频详情的赞页面
 */

public class BookPriseFragment extends BaseFragment {

    @ViewInject(R.id.book_comment_list)
    private RecyclerView comment_list;


    private FollowAdapter followAdapter;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fargment_book_comment, container, false);
        return view;
    }

    @Override
    protected void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        comment_list.setLayoutManager(layoutManager);
        followAdapter = new FollowAdapter();
        comment_list.setAdapter(followAdapter);

    }

    @Override
    protected void logic() {

    }

    /**
     * 请求音频赞列表
     *
     * @param book_id
     */
    public void requestPriseList(String book_id) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.book_zan_list);
        params.addBodyParameter("sub_book_id", book_id);

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
