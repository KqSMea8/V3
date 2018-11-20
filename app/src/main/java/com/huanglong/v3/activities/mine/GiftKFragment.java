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
import com.huanglong.v3.activities.homepage.PersonalPageActivity;
import com.huanglong.v3.adapter.mine.MGiftAdapter;
import com.huanglong.v3.model.mine.MGiftBean;
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
 * Created by bin on 2018/7/10.
 * 我的礼物 K歌
 */

public class GiftKFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    @ViewInject(R.id.my_gift_list)
    private XRecyclerView gift_list;

    private int page = 1;


    private List<MGiftBean> mGiftBeanList = new ArrayList<>();
    private MGiftAdapter mGiftAdapter;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_gift, container, false);
    }

    @Override
    protected void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gift_list.setLayoutManager(layoutManager);

        gift_list.setLoadingListener(this);

        mGiftAdapter = new MGiftAdapter();
        gift_list.setAdapter(mGiftAdapter);

        gift_list.refresh();

    }

    @Override
    protected void logic() {

        mGiftAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                MGiftBean mGiftBean = (MGiftBean) obj;
                Intent intent = new Intent();
                intent.setClass(getActivity(), PersonalPageActivity.class);
                intent.putExtra("uid", mGiftBean.getMember_id());
                startActivity(intent);
            }
        });

    }


    @Override
    public void onRefresh() {
        page = 1;
        requestGiftList();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestGiftList();
    }

    /**
     * 请求礼物列表
     */
    private void requestGiftList() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_my_gift);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("type", "1");
        params.addBodyParameter("page", page + "");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = new Gson();
                    List<MGiftBean> mGiftBeans = gson.fromJson(json, new TypeToken<LinkedList<MGiftBean>>() {
                    }.getType());
                    if (page == 1) {
                        mGiftBeanList.clear();
                        mGiftBeanList.addAll(mGiftBeans);
                    } else {
                        mGiftBeanList.addAll(mGiftBeans);
                    }
                    mGiftAdapter.setData(mGiftBeanList);
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
                    gift_list.refreshComplete();
                } else {
                    gift_list.loadMoreComplete();
                }
            }
        });

    }


}
