package com.huanglong.v3.adapter.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.gyf.barlibrary.ImmersionBar;
import com.huanglong.v3.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by bin on 2018/4/23.
 * 地图显示位置
 */
@ContentView(R.layout.activity_map)
public class MapActivity extends Activity {

    @ViewInject(R.id.map_view)
    private MapView mMapView;

    public ImmersionBar mImmersionBar;

    private CameraUpdate cameraUpdate;

    private AMap aMap;


    private String latitude;
    private String longitude;
    private String address;


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        mImmersionBar = ImmersionBar.with(this);
//        mImmersionBar;   //所有子类都将继承这些相同的属性
        mImmersionBar.statusBarColor(R.color.transparent)
                .keyboardEnable(true)
                .init();

        mMapView.onCreate(savedInstanceState);
        logic();
    }

    private void logic() {
        aMap = mMapView.getMap();//获取地图对象
        Intent intent = getIntent();
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        address = intent.getStringExtra("address");

        if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
            LatLng latLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
            //设置显示比例
            aMap.moveCamera(CameraUpdateFactory.zoomTo(15));

//            final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(address).snippet(""));
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));//这个是关键  如果不设置的话中心点是北京，汇出现目标点在地图上显示不了
            aMap.addMarker(new MarkerOptions().position(latLng).title(address).snippet(""));

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (mImmersionBar != null)
            mImmersionBar.destroy();  //必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
    }

}
