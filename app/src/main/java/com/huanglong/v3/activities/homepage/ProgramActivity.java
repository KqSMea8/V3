package com.huanglong.v3.activities.homepage;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
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
 * Created by bin on 2018/10/9.
 * 小程序页面
 */

@ContentView(R.layout.activity_program)
public class ProgramActivity extends BaseFragmentActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.title_right_iv)
    private ImageView right_iv;
    @ViewInject(R.id.title_right_lin)
    private LinearLayout lin_right;
    @ViewInject(R.id.activity_program_tab)
    private TabLayout tab_program;
    @ViewInject(R.id.activity_program_view_pager)
    private ViewPager view_pager;


    private List<Fragment> fragments = new ArrayList<>();

    private TabAdapter tabAdapter;

    private List<String> tab_name = new ArrayList<>();
    private List<LiveClassBean> liveClassBeans;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("小程序");
        lin_right.setVisibility(View.VISIBLE);
        right_iv.setImageResource(R.mipmap.icon_white_search);
    }

    @Override
    protected void logic() {


        requestProgramClass();

        tab_program.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    @Event(value = {R.id.title_back, R.id.title_right_lin})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_right_lin:
                Intent intent = new Intent();
                intent.setClass(this, SearchProgramActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 请求小程序分类
     */
    private void requestProgramClass() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_wx_cate_list);

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
            fragments.add(new ProgramFragment(liveClassBeans.get(i).getId()));
        }

        if (tab_name.size() > 5) {
            tab_program.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            tab_program.setTabMode(TabLayout.MODE_FIXED);
        }
        for (int i = 0; i < tab_name.size(); i++) {
            tab_program.addTab(tab_program.newTab().setText(tab_name.get(i)), i);
        }
        tabAdapter = new TabAdapter(this.getSupportFragmentManager(), tab_name);
        view_pager.setAdapter(tabAdapter);
        tabAdapter.setFragmentData(fragments);
        tab_program.setupWithViewPager(view_pager);
        tab_program.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
