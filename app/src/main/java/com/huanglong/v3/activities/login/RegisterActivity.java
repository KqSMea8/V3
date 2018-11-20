package com.huanglong.v3.activities.login;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.adapter.TabAdapter;
import com.huanglong.v3.im.view.ListPickerDialog;
import com.huanglong.v3.utils.LocationAMapUtils;
import com.huanglong.v3.utils.ToastUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bin on 2018/4/12.
 * 注册页面
 * activity_register_layout
 */

@ContentView(R.layout.activity_register)
public class RegisterActivity extends BaseFragmentActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.register_tab)
    private TabLayout register_tab;
    @ViewInject(R.id.register_view_pager)
    private ViewPager view_page;

    public static RegisterActivity instance;


    private List<Fragment> fragments = new ArrayList<>();

    private TabAdapter tabAdapter;

    private String[] tab_title = {"个人", "企业"};

    private LocationAMapUtils locationAMapUtils;

    public String province;
    public String city;
    public String region;

    public String str_location;
    public String str_longitude;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("注册");

        tabAdapter = new TabAdapter(getSupportFragmentManager(), null);
        view_page.setAdapter(tabAdapter);
        register_tab.setupWithViewPager(view_page);
        tabAdapter.setTitle(Arrays.asList(tab_title));

    }

    @Override
    protected void logic() {
        instance = this;
        fragments.add(new PerRegFragment());
        fragments.add(new EntRegFragment());
        tabAdapter.setFragmentData(fragments);

        locationAMapUtils = new LocationAMapUtils(this);
        locationAMapUtils.startLocation();
        locationAMapUtils.setLocationClient(new LocationAMapUtils.LocationClientOption() {
            @Override
            public void onLocationSuccess(AMapLocation location) {
                province = location.getProvince();
                city = location.getCity();
                region = location.getDistrict();
                str_location = String.valueOf(location.getLatitude());
                str_longitude = String.valueOf(location.getLongitude());
            }

            @Override
            public void onLocationFail(int errorCode, String errorMsg) {
                ToastUtils.showToast("定位失败");
            }
        });
    }

    /**
     * 注册成功关闭
     *
     * @param mobile
     */
    public void closeActivity(String mobile) {
        Intent intent = new Intent();
        intent.putExtra("mobile", mobile);
        setResult(RESULT_OK, intent);
        RegisterActivity.this.finish();
    }

    @Event(value = {R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                RegisterActivity.this.finish();
                break;
        }
    }

    /**
     * 选择分类
     *
     * @param tv
     * @param jobs
     */
    public void showSelJob(TextView tv, List<String> jobs) {
        String[] str_jobs = jobs.toArray(new String[jobs.size()]);
        new ListPickerDialog().show(str_jobs, getSupportFragmentManager(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv.setText(str_jobs[which]);
            }
        });
    }

    @Override
    protected void onDestroy() {
        instance = null;
        locationAMapUtils.stopLocation();
        super.onDestroy();
    }
}
