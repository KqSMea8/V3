package com.huanglong.v3.conversation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.utils.LocationAMapUtils;
import com.huanglong.v3.utils.ToastUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by bin on 2018/5/14.
 * 地图上显示位置
 */
@ContentView(R.layout.activity_show_place)
public class ShowPlaceActivity extends BaseActivity {//implements AMapLocationListener, LocationSource {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.map)
    private MapView mapView;
    @ViewInject(R.id.show_location_des)
    private TextView tv_location_des;


    private AMap aMap;

    private LatLonPoint searchLatlonPoint;

    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;


    private LocationAMapUtils locationAMapUtils;

    private boolean isLocation = false;

    private Marker locationMarker;

    private double lat, lon;
    private double locLatitude, locLongitude;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("位置信息");
        mapView.onCreate(savedInstanceState);
        locationAMapUtils = new LocationAMapUtils(this);
        init();
    }

    @Override
    protected void logic() {

        Intent intent = getIntent();
        String des = intent.getStringExtra("des");
        tv_location_des.setText(des);
        lat = intent.getDoubleExtra("lat", 0);
        lon = intent.getDoubleExtra("lon", 0);

        LatLng latLng = new LatLng(lat, lon);
        final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.purple_pin)));
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

        locationAMapUtils.startLocation();

        locationAMapUtils.setLocationClient(new LocationAMapUtils.LocationClientOption() {
            @Override
            public void onLocationSuccess(AMapLocation location) {
                locLatitude = location.getLatitude();
                locLongitude = location.getLongitude();
                LatLng latLng = new LatLng(locLatitude, locLongitude);
                final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_location)));
                if (isLocation) {
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                }
            }

            @Override
            public void onLocationFail(int errorCode, String errorMsg) {
                ToastUtils.showToast("定位失败");
            }
        });

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        locationAMapUtils.stopLocation();
//        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        locationAMapUtils.destroyLocation();
    }

    @Event(value = {R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

    /**
     * 初始化
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                isLocation = true;
                locationAMapUtils.startLocation();
            }

            @Override
            public void deactivate() {

            }
        });// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
//        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

//    @Override
//    public void onLocationChanged(AMapLocation amapLocation) {
//
//        if (mListener != null && amapLocation != null) {
//            if (amapLocation != null
//                    && amapLocation.getErrorCode() == 0) {
//                mListener.onLocationChanged(amapLocation);
//                LatLng curLatlng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
////                searchLatlonPoint = new LatLonPoint(curLatlng.latitude, curLatlng.longitude);
////                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatlng, 16f));
//
//            } else {
//                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
//                Log.e("AmapErr", errText);
//            }
//        }
//    }

//    @Override
//    public void activate(OnLocationChangedListener listener) {
//        mListener = listener;
//        if (mlocationClient == null) {
//            mlocationClient = new AMapLocationClient(this);
//            mLocationOption = new AMapLocationClientOption();
//            //设置定位监听
//            mlocationClient.setLocationListener(this);
//            //设置为高精度定位模式
//            mLocationOption.setOnceLocation(true);
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//            //设置定位参数
//            mlocationClient.setLocationOption(mLocationOption);
//            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//            // 在定位结束后，在合适的生命周期调用onDestroy()方法
//            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//            mlocationClient.startLocation();
//        }
//
//    }
//
//    /**
//     * 停止定位
//     */
//    @Override
//    public void deactivate() {
//        mListener = null;
//        if (mlocationClient != null) {
//            mlocationClient.stopLocation();
//            mlocationClient.onDestroy();
//        }
//        mlocationClient = null;
//    }

    private void addMarkerInScreenCenter(LatLng locationLatLng) {
//        LatLng latLng = aMap.getCameraPosition().target;
        Point screenPosition = aMap.getProjection().toScreenLocation(locationLatLng);
        locationMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.purple_pin)));
        //设置Marker在屏幕上,不跟随地图移动
        locationMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
        locationMarker.setZIndex(1);

    }
}
