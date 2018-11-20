package com.huanglong.v3.activities.message;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.adapter.TabAdapter;
import com.huanglong.v3.utils.UserInfoUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bin on 2018/3/18.
 * 黄页
 */
@ContentView(R.layout.activity_yellow_page)
public class YellowPageActivity extends BaseFragmentActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.yellow_tab_view_pager)
    private ViewPager view_pager;
    @ViewInject(R.id.yellow_tab)
    private TabLayout yel_tab;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;


    private List<Fragment> fragments = new ArrayList<>();
    private TabAdapter tabAdapter;

    private List<String> tab_title = new ArrayList<>();
    private YelPageInfoFragment yelPageInfoFragment;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("黄页");
        int userType = UserInfoUtils.getUserType();
        if (userType == 2) {
            tv_right.setText("发布信息");
        }

        yelPageInfoFragment = new YelPageInfoFragment();
        tabAdapter = new TabAdapter(this.getSupportFragmentManager(), null);
        view_pager.setAdapter(tabAdapter);
        fragments.add(new YelPageConFragment());
        fragments.add(yelPageInfoFragment);
        tabAdapter.setFragmentData(fragments);
        yel_tab.setupWithViewPager(view_pager);

        yel_tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    tv_right.setVisibility(View.VISIBLE);
                } else {
                    tv_right.setVisibility(View.GONE);
                }
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
    protected void logic() {
        tab_title.clear();
        tab_title.add("找公司");
        tab_title.add("找信息");
        tabAdapter.setTitle(tab_title);


    }

    @Event(value = {R.id.title_back, R.id.title_tv_right})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                YellowPageActivity.this.finish();
                break;
            case R.id.title_tv_right:
                Intent intent = new Intent();
                intent.setClass(this, YelInfoReleaseActivity.class);
                startActivityForResult(intent, 1000);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    yelPageInfoFragment.onRefresh();
                    break;
            }
        }
    }
}
