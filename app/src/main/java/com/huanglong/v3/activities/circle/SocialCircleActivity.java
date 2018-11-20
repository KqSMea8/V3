package com.huanglong.v3.activities.circle;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.TabAdapter;
import com.huanglong.v3.model.homepage.LiveClassBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/3/17.
 * 社圈
 */
@ContentView(R.layout.activity_social_circle)
public class SocialCircleActivity extends BaseFragmentActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;
    @ViewInject(R.id.circle_lin)
    public LinearLayout circle_lin;
    @ViewInject(R.id.circle_tab)
    private TabLayout circle_tab;
    @ViewInject(R.id.circle_view_pager)
    private ViewPager view_pager;


    private List<LiveClassBean> liveClassBeans;
    private List<String> tab_name = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private TabAdapter tabAdapter;

    public static SocialCircleActivity instance;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        instance = this;
        tv_title.setText("社圈");
        tv_right.setText("发布");

    }

    @Override
    protected void logic() {
        requestClass();
    }

    @Event(value = {R.id.title_tv_right, R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_tv_right:
                Intent intent = new Intent();
                intent.setClass(getActivity(), ReleaseCircleActivity.class);
                startActivityForResult(intent, 1000);
                break;
            case R.id.title_back:
                SocialCircleActivity.this.finish();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    int currentItem = view_pager.getCurrentItem();
                    SocialCircleFragment socialCircleFragment = (SocialCircleFragment) fragments.get(currentItem);
                    socialCircleFragment.onRefresh();
                    break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    /**
     * 请求社圈类别
     */
    private void requestClass() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.Quan_cate_list);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    liveClassBeans = gson.fromJson(json, new TypeToken<LinkedList<LiveClassBean>>() {
                    }.getType());
                    if (liveClassBeans != null && liveClassBeans.size() > 0) {
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
        tab_name.add("全部");
        fragments.add(new SocialCircleFragment("-1"));

        for (int i = 0; i < liveClassBeans.size(); i++) {
            tab_name.add(liveClassBeans.get(i).getName());
            fragments.add(new SocialCircleFragment(liveClassBeans.get(i).getId()));
        }
        if (tab_name.size() > 5) {
            circle_tab.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            circle_tab.setTabMode(TabLayout.MODE_FIXED);
        }

        for (int i = 0; i < tab_name.size(); i++) {
            circle_tab.addTab(circle_tab.newTab().setText(tab_name.get(i)), i);
        }
        tabAdapter = new TabAdapter(this.getSupportFragmentManager(), tab_name);
        view_pager.setAdapter(tabAdapter);
        tabAdapter.setFragmentData(fragments);
        circle_tab.setupWithViewPager(view_pager);
        circle_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

}
