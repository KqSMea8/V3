package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.TabAdapter;
import com.huanglong.v3.model.homepage.BannerBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.BannerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/16.
 * 活动页面
 */
@ContentView(R.layout.activity_flexible)
public class FlexibleActivity extends BaseFragmentActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_name;
    @ViewInject(R.id.convenientBanner)
    private ConvenientBanner convenientBanner;
    @ViewInject(R.id.flexible_view_pager)
    private ViewPager flexible_view_pager;
    @ViewInject(R.id.flexible_tab)
    private TabLayout flexible_tab;

    private List<String> pic = new ArrayList<>();

    private TabAdapter tabAdapter;

    private String[] tab_title = {"官方大赛", "我的大赛"};

    private List<Fragment> fragments = new ArrayList<>();


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_name.setText("活动");

        tabAdapter = new TabAdapter(getSupportFragmentManager(), Arrays.asList(tab_title));
        flexible_view_pager.setAdapter(tabAdapter);
        flexible_tab.setupWithViewPager(flexible_view_pager);
    }

    @Override
    protected void logic() {
        requestBanner();
        fragments.add(new OfficialFleFragment());
        fragments.add(new MyFleFragment());
        tabAdapter.setFragmentData(fragments);

        flexible_tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                flexible_view_pager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Event(value = {R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                FlexibleActivity.this.finish();
                break;
        }
    }

    /**
     * 请求广告banner
     */
    private void requestBanner() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.banner_index);
        params.addBodyParameter("position", "1");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<BannerBean> bannerBeans = gson.fromJson(json, new TypeToken<LinkedList<BannerBean>>() {
                    }.getType());
                    if (bannerBeans != null) {
                        pic.clear();
                        for (int i = 0; i < bannerBeans.size(); i++) {
                            pic.add(bannerBeans.get(i).getBanner_path());
                        }
                        BannerView.setData(pic, convenientBanner);
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
}
