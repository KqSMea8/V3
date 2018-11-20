package com.huanglong.v3.activities.homepage;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.TabAdapter;
import com.huanglong.v3.model.homepage.LiveClassBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.view.ViewPagerSlide;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/8.
 * 视频 关注
 */

public class VFFragment extends BaseFragment {

    @ViewInject(R.id.activity_video_follow_tab)
    private TabLayout tab_video;
    @ViewInject(R.id.activity_video_follow_view_pager)
    private ViewPagerSlide view_pager;

    private List<Fragment> fragments = new ArrayList<>();

    private TabAdapter tabAdapter;
    private boolean isCreate = false;


    private List<String> tab_name = new ArrayList<>();
    private List<LiveClassBean> liveClassBeans;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_follow_tab, container, false);
        return view;
    }

    @Override
    protected void initView() {
        isCreate = true;
        requestLiveClass();


    }

    @Override
    protected void logic() {

        tab_video.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view_pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * 请求直播分类
     */
    private void requestLiveClass() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.video_video_cate_list);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    liveClassBeans = gson.fromJson(json, new TypeToken<LinkedList<LiveClassBean>>() {
                    }.getType());
                    if (liveClassBeans != null && liveClassBeans.size() > 0) {
                        LiveClassBean liveClassBean = new LiveClassBean();
                        liveClassBean.setName("全部");
                        liveClassBean.setId("-1");
                        liveClassBeans.add(0, liveClassBean);
                        initTabItem(liveClassBeans);
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

            }
        });
    }

    /**
     * 初始化 tabItem
     *
     * @param liveClassBeans
     */
    private void initTabItem(List<LiveClassBean> liveClassBeans) {
        tab_name.clear();
        fragments.clear();
        for (int i = 0; i < liveClassBeans.size(); i++) {
            tab_name.add(liveClassBeans.get(i).getName());
            fragments.add(new VFCFragment(liveClassBeans.get(i).getId()));
        }

        if (tab_name.size() > 5) {
            tab_video.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            tab_video.setTabMode(TabLayout.MODE_FIXED);
        }

        for (int i = 0; i < tab_name.size(); i++) {
            tab_video.addTab(tab_video.newTab().setText(tab_name.get(i)), i);
        }
        tabAdapter = new TabAdapter(getActivity().getSupportFragmentManager(), tab_name);
        view_pager.setAdapter(tabAdapter);
        view_pager.setScanScroll(false);
        tabAdapter.setFragmentData(fragments);

        tab_video.setupWithViewPager(view_pager);
        tab_video.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view_pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isCreate = false;
    }

    /**
     * 刷新数据
     */
    public void refresh() {
        if (isCreate) {
            for (int i = 0; i < fragments.size(); i++) {
                VFCFragment vfcFragment = (VFCFragment) fragments.get(i);
                boolean isCreate = vfcFragment.isCreate;
                if (isCreate) {
                    vfcFragment.onRefresh();
                }
            }
        }
    }

}
