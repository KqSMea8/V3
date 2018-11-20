package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.adapter.TabAdapter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bin on 2018/7/10.
 * 我的礼物
 */
@ContentView(R.layout.activity_my_gift)
public class MyGiftActivity extends BaseFragmentActivity {


    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.my_gift_tab_view_pager)
    private ViewPager view_pager;
    @ViewInject(R.id.my_gift_tab)
    private TabLayout yel_tab;

    private List<Fragment> fragments = new ArrayList<>();
    private TabAdapter tabAdapter;

    private List<String> tab_title = new ArrayList<>();


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("我的礼物");

        tabAdapter = new TabAdapter(this.getSupportFragmentManager(), null);
        view_pager.setAdapter(tabAdapter);
        fragments.add(new GiftKFragment());
        fragments.add(new GiftVFragment());
        fragments.add(new GiftLFragment());
        tabAdapter.setFragmentData(fragments);
        yel_tab.setupWithViewPager(view_pager);

        yel_tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                if (tab.getPosition() == 1) {
//                    tv_right.setVisibility(View.VISIBLE);
//                } else {
//                    tv_right.setVisibility(View.GONE);
//                }
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
        tab_title.add("K歌");
        tab_title.add("视频");
        tab_title.add("直播");
        tabAdapter.setTitle(tab_title);
    }


    @Event(value = {R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }
}
