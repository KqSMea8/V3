package com.huanglong.v3.activities.homepage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.huanglong.v3.utils.FileUtils;
import com.huanglong.v3.utils.PopupUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.voice.SoundPublishActivity;
import com.huanglong.v3.voice.SoundRecordActivity;
import com.zyyoona7.lib.EasyPopup;
import com.zyyoona7.lib.HorizontalGravity;
import com.zyyoona7.lib.VerticalGravity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/5.
 * 音频界面
 */
@ContentView(R.layout.activity_v_f)
public class VFActivity extends BaseFragmentActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.activity_v_f_tab)
    private TabLayout tab_v_f;
    @ViewInject(R.id.activity_v_f_view_pager)
    private ViewPager view_pager;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;
    @ViewInject(R.id.v_f_lin)
    private LinearLayout v_f_lin;
    @ViewInject(R.id.orange_title_bar_lin)
    private LinearLayout title_bar_lin;


    private List<Fragment> fragments = new ArrayList<>();

    private TabAdapter tabAdapter;


    private List<String> tab_name = new ArrayList<>();
    private List<LiveClassBean> liveClassBeans;

    private EasyPopup mCirclePop;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("音频");
        tv_right.setText("发布");
        initPop();
    }

    @Override
    protected void logic() {
        requestSoundBookClass();

        tab_v_f.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
     * 初始化设置值popwindow
     */
    private void initPop() {
        mCirclePop = PopupUtils.initPopup(getActivity(), R.layout.pop_k_song_menu, v_f_lin);
        TextView tv1 = mCirclePop.getView(R.id.pop_k_song);
        TextView tv2 = mCirclePop.getView(R.id.pop_k_song_file);
        tv1.setText("录       制");
        tv2.setText("上传音频");
        mCirclePop.getView(R.id.pop_k_song).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCirclePop.dismiss();
                Intent intent = new Intent();
                intent.setClass(VFActivity.this, SoundRecordActivity.class);
                startActivity(intent);
            }
        });

        mCirclePop.getView(R.id.pop_k_song_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCirclePop.dismiss();
                chooseFile();
            }
        });

    }

    /**
     * 打开系统文件管理器选择文件
     */
    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件"), 1001);
        } catch (android.content.ActivityNotFoundException ex) {
            ToastUtils.showToast("亲，木有文件管理器啊-_-!!");
        }
    }

    @Event(value = {R.id.title_back, R.id.title_tv_right})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                VFActivity.this.finish();
                break;
            case R.id.title_tv_right:
                mCirclePop.showAtAnchorView(title_bar_lin, VerticalGravity.BELOW, HorizontalGravity.RIGHT, -200, 0);
                break;
        }
    }


    /**
     * 请求有声书分类
     */
    private void requestSoundBookClass() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.book_cate_list);
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
        for (int i = 0; i < liveClassBeans.size(); i++) {
            tab_name.add(liveClassBeans.get(i).getName());
            fragments.add(new SoundBookFragment(liveClassBeans.get(i).getId()));
        }

        if (tab_name.size() > 5) {
            tab_v_f.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            tab_v_f.setTabMode(TabLayout.MODE_FIXED);
        }

        for (int i = 0; i < tab_name.size(); i++) {
            tab_v_f.addTab(tab_v_f.newTab().setText(tab_name.get(i)), i);
        }
        tabAdapter = new TabAdapter(this.getSupportFragmentManager(), tab_name);
        view_pager.setAdapter(tabAdapter);
        tabAdapter.setFragmentData(fragments);
        tab_v_f.setupWithViewPager(view_pager);
        tab_v_f.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    onRefresh();
                    break;
                case 1001:
                    Uri uri = data.getData();
                    File file = FileUtils.uri2File(VFActivity.this, uri);
                    Intent intent = new Intent();
                    intent.setClass(this, SoundPublishActivity.class);
                    intent.putExtra("soundpath", file.getAbsolutePath());
                    startActivityForResult(intent, 1001);
                    break;
            }
        }
    }

    /**
     * 刷新数据
     */
    private void onRefresh() {
        for (Fragment fragment : fragments) {
            SoundBookFragment soundBookFragment = (SoundBookFragment) fragment;
            soundBookFragment.onRefresh();
        }
    }


}
