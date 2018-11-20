package com.huanglong.v3.activities.homepage;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.TabAdapter;
import com.huanglong.v3.live.model.PushBean;
import com.huanglong.v3.live.push.TCLivePublisherActivity;
import com.huanglong.v3.live.push.TCPublishSettingActivity;
import com.huanglong.v3.live.userinfo.TCUserInfoMgr;
import com.huanglong.v3.live.utils.SHARE_MEDIA;
import com.huanglong.v3.live.utils.TCConstants;
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
 * Created by bin on 2018/3/21.
 * 直播界面
 */
@ContentView(R.layout.activity_live)
public class LiveActivity extends BaseFragmentActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.activity_live_tab)
    private TabLayout tab_live;
    @ViewInject(R.id.activity_live_view_pager)
    private ViewPager view_pager;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;


    private List<Fragment> fragments = new ArrayList<>();

    private TabAdapter tabAdapter;


    private List<String> tab_name = new ArrayList<>();
    private List<LiveClassBean> liveClassBeans;

    public static LiveActivity instance;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        instance = this;
        tv_title.setText("直播");
        tv_right.setText("发布直播");

    }

    @Override
    protected void logic() {
        requestLiveClass();

        tab_live.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    @Event(value = {R.id.title_back, R.id.title_tv_right})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                LiveActivity.this.finish();
                break;
            case R.id.title_tv_right:
                Intent intent = new Intent();
                intent.setClass(this, TCPublishSettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 请求直播分类
     */
    private void requestLiveClass() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.live_cate_list);
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
            fragments.add(new TalentFragment(liveClassBeans.get(i).getId()));
        }

        if (tab_name.size() > 5) {
            tab_live.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            tab_live.setTabMode(TabLayout.MODE_FIXED);
        }
        for (int i = 0; i < tab_name.size(); i++) {
            tab_live.addTab(tab_live.newTab().setText(tab_name.get(i)), i);
        }
        tabAdapter = new TabAdapter(this.getSupportFragmentManager(), tab_name);
        view_pager.setAdapter(tabAdapter);
        tabAdapter.setFragmentData(fragments);
        tab_live.setupWithViewPager(view_pager);
        tab_live.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
     * 跳转直播页面
     *
     * @param title
     * @param mBitrateType
     * @param lbs
     * @param pushBean
     */
    public void navToLive(String title, int mBitrateType, String lbs, PushBean pushBean) {
        Intent intent = new Intent(LiveActivity.this, TCLivePublisherActivity.class);
        intent.putExtra(TCConstants.ROOM_TITLE, TextUtils.isEmpty(title) ? TCUserInfoMgr.getInstance().getNickname() : title);
        intent.putExtra(TCConstants.USER_ID, TCUserInfoMgr.getInstance().getUserId());
        intent.putExtra(TCConstants.PUBLISH_URL, pushBean.getPush_url());
        intent.putExtra(TCConstants.USER_NICK, TCUserInfoMgr.getInstance().getNickname());
        intent.putExtra(TCConstants.USER_HEADPIC, pushBean.getCover_image());
        intent.putExtra(TCConstants.COVER_PIC, pushBean.getCover_image());
        intent.putExtra(TCConstants.BITRATE, mBitrateType);
        intent.putExtra(TCConstants.LIVE_Id, pushBean.getId());
        intent.putExtra(TCConstants.USER_LOC,
                lbs.equals(getString(R.string.text_live_lbs_fail)) ||
                        lbs.equals(getString(R.string.text_live_location)) ?
                        getString(R.string.text_live_close_lbs) : lbs);
        intent.putExtra(TCConstants.SHARE_PLATFORM, SHARE_MEDIA.WEIXIN);
        startActivityForResult(intent, 1000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    int currentItem = view_pager.getCurrentItem();
                    TalentFragment fragment = (TalentFragment) fragments.get(currentItem);
                    fragment.onRefresh();
                    break;
            }
        }
    }
}
