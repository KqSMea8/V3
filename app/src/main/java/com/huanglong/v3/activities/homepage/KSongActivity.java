package com.huanglong.v3.activities.homepage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.adapter.TabAdapter;
import com.huanglong.v3.song.SongEffActivity;
import com.huanglong.v3.song.SongPublishActivity;
import com.huanglong.v3.utils.FileUtils;
import com.huanglong.v3.utils.LocationAMapUtils;
import com.huanglong.v3.utils.PopupUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.zyyoona7.lib.EasyPopup;
import com.zyyoona7.lib.HorizontalGravity;
import com.zyyoona7.lib.VerticalGravity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bin on 2018/4/7.
 * K歌页面
 */
@ContentView(R.layout.activity_k_song)
public class KSongActivity extends BaseFragmentActivity implements ViewPager.OnPageChangeListener {

    @ViewInject(R.id.k_song_tab)
    private RadioGroup radio_tab;
    @ViewInject(R.id.k_song_viewpager)
    private ViewPager viewpager;
    @ViewInject(R.id.k_song_lin)
    private LinearLayout song_lin;
    @ViewInject(R.id.k_song_publish)
    private TextView tv_publish;

    private List<Fragment> fragments = new ArrayList<>();

    private TabAdapter tabAdapter;

    private LocationAMapUtils locationAMapUtils;
    public String str_longitude, str_latitude;

    public static KSongActivity instance;

    private KSFFragment ksfFragment;
    private KSHFragment kshFragment;
    private KSNFragment ksnFragment;

    private EasyPopup mCirclePop;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        instance = this;
        radio_tab.check(R.id.k_song_tab_hot);
        locationAMapUtils = new LocationAMapUtils(this);
        initPop();

    }

    @Override
    protected void logic() {
        ksfFragment = new KSFFragment();
        kshFragment = new KSHFragment();
        ksnFragment = new KSNFragment();
        fragments.add(ksfFragment);
        fragments.add(kshFragment);
        fragments.add(ksnFragment);
        tabAdapter = new TabAdapter(this.getSupportFragmentManager(), null);
        viewpager.setAdapter(tabAdapter);
        tabAdapter.setFragmentData(fragments);
        viewpager.setOnPageChangeListener(this);
        viewpager.setCurrentItem(1);
        radio_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.k_song_tab_follow) {
                    viewpager.setCurrentItem(0);
                } else if (i == R.id.k_song_tab_hot) {
                    viewpager.setCurrentItem(1);
                } else if (i == R.id.k_song_tab_nearby) {
                    viewpager.setCurrentItem(2);
                }
            }
        });

        locationAMapUtils.setLocationClient(new LocationAMapUtils.LocationClientOption() {

            @Override
            public void onLocationSuccess(AMapLocation location) {
                str_longitude = String.valueOf(location.getLongitude());
                str_latitude = String.valueOf(location.getLatitude());
            }

            @Override
            public void onLocationFail(int errorCode, String errorMsg) {

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

    @Event(value = {R.id.title_back, R.id.k_song_publish})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                KSongActivity.this.finish();
                break;
            case R.id.k_song_publish:
                mCirclePop.showAtAnchorView(tv_publish, VerticalGravity.BELOW, HorizontalGravity.CENTER, 0, 0);
                break;
        }
    }

    /**
     * 初始化设置值popwindow
     */
    private void initPop() {
        mCirclePop = PopupUtils.initPopup(getActivity(), R.layout.pop_k_song_menu, song_lin);
        TextView tv1 = mCirclePop.getView(R.id.pop_k_song);
        TextView tv2 = mCirclePop.getView(R.id.pop_k_song_file);
        tv1.setText("录       制");
        tv2.setText("上传K歌");

        mCirclePop.getView(R.id.pop_k_song).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCirclePop.dismiss();
                Intent intent = new Intent();
                intent.setClass(KSongActivity.this, SongEffActivity.class);
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
            startActivityForResult(Intent.createChooser(intent, "选择文件"), 1000);
        } catch (android.content.ActivityNotFoundException ex) {
            ToastUtils.showToast("亲，木有文件管理器啊-_-!!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    Uri uri = data.getData();
                    File file = FileUtils.uri2File(KSongActivity.this, uri);
                    Intent intent = new Intent();
                    intent.setClass(this, SongPublishActivity.class);
                    intent.putExtra("path", file.getAbsolutePath());
                    intent.putExtra("lrcUrl", "");
                    intent.putExtra("sucai_url", "");
                    startActivityForResult(intent, 1001);
                    break;
                case 1001:
                    onRefresh();
                    break;
            }
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            radio_tab.check(R.id.k_song_tab_follow);
        } else if (position == 1) {
            radio_tab.check(R.id.k_song_tab_hot);
        } else if (position == 2) {
            radio_tab.check(R.id.k_song_tab_nearby);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 刷新数据
     */
    public void onRefresh() {
        ksfFragment.onRefresh();
        kshFragment.onRefresh();
        ksnFragment.onRefresh();
    }


}
