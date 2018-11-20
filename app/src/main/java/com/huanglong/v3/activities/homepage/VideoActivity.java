package com.huanglong.v3.activities.homepage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;

import com.amap.api.location.AMapLocation;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.gyf.barlibrary.ImmersionBar;
import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.adapter.TabAdapter;
import com.huanglong.v3.smallvideo.EffectActivity;
import com.huanglong.v3.utils.LocationAMapUtils;
import com.huanglong.v3.utils.ToastUtils;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bin on 2018/4/7.
 * 视频界面
 */
@ContentView(R.layout.activity_video)
public class VideoActivity extends BaseFragmentActivity implements ViewPager.OnPageChangeListener {

    @ViewInject(R.id.video_tab)
    private RadioGroup radio_tab;
    @ViewInject(R.id.video_viewpager)
    private ViewPager viewpager;

    private List<Fragment> fragments = new ArrayList<>();

    private TabAdapter tabAdapter;

    private LocationAMapUtils locationAMapUtils;
    public String str_longitude, str_latitude;

    public static VideoActivity instance;
    private VHFragment vhFragment;
    private VFFragment vfFragment;
    private VNFragment vnFragment;

    @Override

    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {

        mImmersionBar = ImmersionBar.with(this);
//        mImmersionBar;   //所有子类都将继承这些相同的属性
        mImmersionBar.statusBarColor(R.color.orange_FC6C57)
                .keyboardEnable(false)
                .init();

        instance = this;
        radio_tab.check(R.id.video_tab_hot);
        locationAMapUtils = new LocationAMapUtils(this);
        locationAMapUtils.setLocationClient(new LocationAMapUtils.LocationClientOption() {

            @Override
            public void onLocationSuccess(AMapLocation location) {
                str_longitude = String.valueOf(location.getLongitude());
                str_latitude = String.valueOf(location.getLatitude());
            }

            @Override
            public void onLocationFail(int errorCode, String errorMsg) {
                LogUtil.e("定位失败，code:" + errorCode + " msg:" + errorMsg);
                ToastUtils.showToast("定位失败");
            }
        });
    }

    @Override
    protected void logic() {
        vhFragment = new VHFragment();
        vfFragment = new VFFragment();
        vnFragment = new VNFragment();
        fragments.add(vfFragment);
        fragments.add(vhFragment);
        fragments.add(vnFragment);
        tabAdapter = new TabAdapter(this.getSupportFragmentManager(), null);
        viewpager.setAdapter(tabAdapter);
        tabAdapter.setFragmentData(fragments);
        viewpager.setCurrentItem(1);
        viewpager.setOnPageChangeListener(this);
        radio_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.video_tab_follow) {
                    vhFragment.onPause();
                    viewpager.setCurrentItem(0);
                } else if (i == R.id.video_tab_hot) {
                    vhFragment.onResume();
                    viewpager.setCurrentItem(1);
                } else if (i == R.id.video_tab_nearby) {
                    vhFragment.onPause();
                    viewpager.setCurrentItem(2);
                }
            }
        });

        boolean b = PermissionsUtil.hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (b) {
            locationAMapUtils.startLocation();
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {
                    locationAMapUtils.startLocation();
                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {

                }
            }, Manifest.permission.ACCESS_COARSE_LOCATION);
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (locationAMapUtils != null) {
            locationAMapUtils.stopLocation();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
        if (locationAMapUtils != null) {
            locationAMapUtils.destroyLocation();
        }
    }


    @Event(value = {R.id.title_back, R.id.video_tab_release})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                VideoActivity.this.finish();
                break;
            case R.id.video_tab_release:
                Intent intent = new Intent();
                intent.setClass(this, EffectActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            vhFragment.onPause();
            radio_tab.check(R.id.video_tab_follow);
        } else if (position == 1) {
            vhFragment.onResume();
            radio_tab.check(R.id.video_tab_hot);
        } else if (position == 2) {
            vhFragment.onPause();
            radio_tab.check(R.id.video_tab_nearby);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        radio_tab.check(R.id.video_tab_hot);
        viewpager.setCurrentItem(1);
    }

    /**
     * 刷新关注列表
     */
    public void refreshRecommend() {
        radio_tab.check(R.id.video_tab_hot);
        viewpager.setCurrentItem(1);
        vhFragment.requestLiveList();
    }

    public void refreshFollow() {
        vfFragment.refresh();
        vnFragment.refresh();
    }
}
