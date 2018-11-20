package com.huanglong.v3.live.push;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.homepage.LiveActivity;
import com.huanglong.v3.im.view.ListPickerDialog;
import com.huanglong.v3.live.TCBaseActivity;
import com.huanglong.v3.live.model.PushBean;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.live.utils.TCLocationHelper;
import com.huanglong.v3.live.utils.TCUtils;
import com.huanglong.v3.model.circle.RelImageBean;
import com.huanglong.v3.model.homepage.LiveClassBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.Common;
import com.huanglong.v3.utils.LocationAMapUtils;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.SelectPictureUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/3/22.
 * 直播设置页面
 */
@ContentView(R.layout.activity_publish_setting)
public class TCPublishSettingActivity extends TCBaseActivity {//implements TCUploadHelper.OnUploadListener {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.btn_publish)
    private TextView BtnPublish;
    //    @ViewInject(R.id.rg_bitrate)
//    private RadioGroup mRGBitrate;
//    @ViewInject(R.id.rg_record_type)
//    private RadioGroup mRGRecordType;
//    @ViewInject(R.id.rl_bitrate)
//    private RelativeLayout mRLBitrate;
    @ViewInject(R.id.btn_lbs)
    private Switch btnLBS;
    @ViewInject(R.id.live_title)
    private EditText tvTitle;
    @ViewInject(R.id.address)
    private TextView tvLBS;
    @ViewInject(R.id.cover)
    private ImageView cover;
    @ViewInject(R.id.live_class_tv)
    private TextView tv_class;


    private String coverPath;//封面图片的本地地址
    private String coverUrl;//封面图片的网路地址
    private int mRecordType = TCConstants.RECORD_TYPE_CAMERA;
    private int mBitrateType = TCConstants.BITRATE_SLOW;

    private boolean mUploading = false;

    private String cid;

    private LocationAMapUtils locationAMapUtils;
    private List<LiveClassBean> liveClassBeans;
    private List<String> str_class = new ArrayList<>();


    @Override
    protected void initView() {
        tv_title.setText("发布直播");
        btnLBS.setChecked(false);
    }

    @Override
    protected void logic() {
        locationAMapUtils = new LocationAMapUtils(this);
//        Intent intent = getIntent();
//        cid = intent.getStringExtra("cid");
        requestLiveClass();

        locationAMapUtils.setLocationClient(new LocationAMapUtils.LocationClientOption() {

            @Override
            public void onLocationSuccess(AMapLocation location) {
                String address = location.getProvince() + location.getCity() + location.getDistrict() + location.getStreet() + location.getStreetNum();
                tvLBS.setText(address);
            }

            @Override
            public void onLocationFail(int errorCode, String errorMsg) {
                ToastUtils.showToast("定位失败");
                btnLBS.setChecked(false);
            }
        });
        mRecordType = TCConstants.RECORD_TYPE_SCREEN;
        if (!checkScrRecordPermission()) {
            ToastUtils.showToast("当前安卓系统版本过低，仅支持5.0及以上系统");
            return;
        }
        try {
            TCUtils.checkFloatWindowPermission(TCPublishSettingActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        mRGRecordType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.rb_record_camera:
//                        mRecordType = TCConstants.RECORD_TYPE_CAMERA;
//                        mRLBitrate.setVisibility(View.GONE);
//                        break;
//                    case R.id.rb_record_screen:
//                        if (!checkScrRecordPermission()) {
//                            ToastUtils.showToast("当前安卓系统版本过低，仅支持5.0及以上系统");
//                            mRGRecordType.check(R.id.rb_record_camera);
//                            return;
//                        }
//                        try {
//                            TCUtils.checkFloatWindowPermission(TCPublishSettingActivity.this);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        mRLBitrate.setVisibility(View.VISIBLE);
//                        mRecordType = TCConstants.RECORD_TYPE_SCREEN;
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
////
//        mRGBitrate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.rb_bitrate_slow:
//                        mBitrateType = TCConstants.BITRATE_SLOW;
//                        break;
//                    case R.id.rb_bitrate_normal:
//                        mBitrateType = TCConstants.BITRATE_NORMAL;
//                        break;
//                    case R.id.rb_bitrate_fast:
//                        mBitrateType = TCConstants.BITRATE_FAST;
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });


        btnLBS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tvLBS.setText(R.string.text_live_location);
                    if (TCLocationHelper.checkLocationPermission(TCPublishSettingActivity.this)) {
                        locationAMapUtils.startLocation();
                    } else {
                        ToastUtils.showToast("定位失败");
                        btnLBS.setChecked(false);
                    }
//                    if (TCLocationHelper.checkLocationPermission(TCPublishSettingActivity.this)) {
//                        if (!TCLocationHelper.getMyLocation(TCPublishSettingActivity.this, TCPublishSettingActivity.this)) {
//                            tvLBS.setText(getString(R.string.text_live_lbs_fail));
//                            //Toast.makeText(getApplicationContext(), "定位失败，请查看是否打开GPS", Toast.LENGTH_SHORT).show();
//                            btnLBS.setChecked(false);
////                            btnLBS.setChecked(false, false);
//                        }
//                    }
                } else {
                    tvLBS.setText(R.string.text_live_close_lbs);
                }
            }
        });
    }

    @Event(value = {R.id.title_back, R.id.btn_publish, R.id.cover, R.id.live_class_lin})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                TCPublishSettingActivity.this.finish();
                break;
            case R.id.btn_publish:
                jump();
                break;
            case R.id.cover:
                SelectPictureUtils.selectedIdCard(this, 700, 438);
                break;
            case R.id.live_class_lin:
                if (str_class != null && str_class.size() > 0) {
                    String[] str_jobs = str_class.toArray(new String[str_class.size()]);
                    new ListPickerDialog().show(str_jobs, getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cid = liveClassBeans.get(which).getId();
                            tv_class.setText(str_jobs[which]);
                        }
                    });
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Common.IMAGE_PICKER://PictureConfig.CHOOSE_REQUEST:
                    if (data != null) {
                        List<String> pathList = data.getStringArrayListExtra("result");
                        coverPath = pathList.get(0);
                        x.image().bind(cover, coverPath, MImageOptions.getNormalImageNotCropOptions());
                        uploadImage();
                    }
                    break;
            }
        }
    }


    /**
     * 上传身份证图片
     */
    private void uploadImage() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.uploadimg);
        params.setMultipart(true);
        params.addBodyParameter("imglist", new File(coverPath));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    RelImageBean relImageBean = gson.fromJson(json, RelImageBean.class);
                    if (relImageBean != null) {
                        coverUrl = relImageBean.getUrl();
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

    private boolean checkScrRecordPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


//    @Override
//    public void onLocationChanged(int code, double lat1, double long1, String location) {
//        if (btnLBS.isChecked()) {
//            if (0 == code) {
//                tvLBS.setText(location);
//                TCUserInfoMgr.getInstance().setLocation(location, lat1, long1, new ITCUserInfoMgrListener() {
//                    @Override
//                    public void OnQueryUserInfo(int error, String errorMsg) {
//                        // TODO: 16/8/10
//                    }
//
//                    @Override
//                    public void OnSetUserInfo(int error, String errorMsg) {
//                        if (0 != error)
//                            Toast.makeText(getApplicationContext(), "设置位置失败" + errorMsg, Toast.LENGTH_LONG).show();
//                    }
//                });
//            } else {
//                tvLBS.setText(getString(R.string.text_live_lbs_fail));
//            }
//        } else {
//            TCUserInfoMgr.getInstance().setLocation("", 0, 0, new ITCUserInfoMgrListener() {
//                @Override
//                public void OnQueryUserInfo(int error, String errorMsg) {
//                    // TODO: 16/8/10
//                }
//
//                @Override
//                public void OnSetUserInfo(int error, String errorMsg) {
//                    if (0 != error)
//                        Toast.makeText(getApplicationContext(), "设置位置失败" + errorMsg, Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//
//
//    }

//    @Override
//    public void onUploadResult(int code, String url) {
//        if (0 == code) {
//            TCUserInfoMgr.getInstance().setUserCoverPic(url, new ITCUserInfoMgrListener() {
//                @Override
//                public void OnQueryUserInfo(int error, String errorMsg) {
//                    // TODO: 16/8/10
//                }
//
//                @Override
//                public void OnSetUserInfo(int error, String errorMsg) {
//
//                }
//            });
//            RequestManager req = Glide.with(this);
//            req.load(url).into(cover);
//            Toast.makeText(this, "上传封面成功", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "上传封面失败，错误码 " + code, Toast.LENGTH_SHORT).show();
//        }
//        mUploading = false;
//    }


    private void jump() {
        //trim避免空格字符串
        if (TextUtils.isEmpty(tvTitle.getText().toString().trim())) {
            ToastUtils.showToast("请输入非空直播标题");
        } else if (TCUtils.getCharacterNum(tvTitle.getText().toString()) > TCConstants.TV_TITLE_MAX_LEN) {
            ToastUtils.showToast("直播标题过长 ,最大长度为" + TCConstants.TV_TITLE_MAX_LEN / 2);
        } else if (mUploading) {
            ToastUtils.showToast(getString(R.string.publish_wait_uploading));
        } else if (!TCUtils.isNetworkAvailable(this)) {
            ToastUtils.showToast("当前网络环境不能发布直播");
        } else if (TextUtils.isEmpty(cid)) {
            ToastUtils.showToast("请选择分类");
        } else {
            requestLive();
//            if (mRecordType == TCConstants.RECORD_TYPE_SCREEN) {
//                //录屏
//                Intent intent = new Intent(this, TCLinkMicLivePushActivity.class);
//                intent.putExtra(TCConstants.ROOM_TITLE, TextUtils.isEmpty(tvTitle.getText().toString()) ? UserInfoUtils.getUserName() : tvTitle.getText().toString());
//                intent.putExtra(TCConstants.USER_ID, TCUserInfoMgr.getInstance().getUserId());
//                intent.putExtra(TCConstants.USER_NICK, TCUserInfoMgr.getInstance().getNickname());
//                intent.putExtra(TCConstants.USER_HEADPIC, TCUserInfoMgr.getInstance().getHeadPic());
//                intent.putExtra(TCConstants.COVER_PIC, TCUserInfoMgr.getInstance().getCoverPic());
////                        intent.putExtra(TCConstants.SCR_ORIENTATION, mOrientation);
//                intent.putExtra(TCConstants.BITRATE, mBitrateType);
//                intent.putExtra(TCConstants.USER_LOC,
//                        tvLBS.getText().toString().equals(getString(R.string.text_live_lbs_fail)) ||
//                                tvLBS.getText().toString().equals(getString(R.string.text_live_location)) ?
//                                getString(R.string.text_live_close_lbs) : tvLBS.getText().toString());
//                intent.putExtra(TCConstants.SHARE_PLATFORM, SHARE_MEDIA.WEIXIN);
//                startActivity(intent);
//                finish();
//            } else {
//                requestLive();
//                //摄像头
//
//            }
        }
    }

    /**
     * 开始直播
     */
    private void requestLive() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.createLive);
        params.addBodyParameter("cid", cid);
        params.addBodyParameter("title", tvTitle.getText().toString().trim());
        params.addBodyParameter("cover_image", !TextUtils.isEmpty(coverUrl) ? coverUrl : "https://www.huachenedu.cn/v3/uploads/picture/20161206/2626dd171ead8527009f8523a937c76f.png");
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("address", tvLBS.getText().toString().trim());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    PushBean pushBean = gson.fromJson(json, PushBean.class);
                    if (pushBean != null) {
                        if (LiveActivity.instance != null) {
                            LiveActivity.instance.navToLive(tvTitle.getText().toString(), mBitrateType, tvLBS.getText().toString(), pushBean);
                        }
//                        Intent intent = new Intent(TCPublishSettingActivity.this, TCLivePublisherActivity.class);
//                        intent.putExtra(TCConstants.ROOM_TITLE,
//                                TextUtils.isEmpty(tvTitle.getText().toString()) ? TCUserInfoMgr.getInstance().getNickname() : tvTitle.getText().toString());
//                        intent.putExtra(TCConstants.USER_ID, TCUserInfoMgr.getInstance().getUserId());
//                        intent.putExtra(TCConstants.PUBLISH_URL, pushBean.getPush_url());
//                        intent.putExtra(TCConstants.USER_NICK, TCUserInfoMgr.getInstance().getNickname());
//                        intent.putExtra(TCConstants.USER_HEADPIC, pushBean.getCover_image());
//                        intent.putExtra(TCConstants.COVER_PIC, pushBean.getCover_image());
//                        intent.putExtra(TCConstants.BITRATE, mBitrateType);
//                        intent.putExtra(TCConstants.LIVE_Id, pushBean.getId());
//                        intent.putExtra(TCConstants.USER_LOC,
//                                tvLBS.getText().toString().equals(getString(R.string.text_live_lbs_fail)) ||
//                                        tvLBS.getText().toString().equals(getString(R.string.text_live_location)) ?
//                                        getString(R.string.text_live_close_lbs) : tvLBS.getText().toString());
//                        intent.putExtra(TCConstants.SHARE_PLATFORM, SHARE_MEDIA.WEIXIN);
//                        startActivity(intent);
                        finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationAMapUtils != null) {
            locationAMapUtils.stopLocation();
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
                        str_class.clear();
                        for (LiveClassBean liveClassBean : liveClassBeans) {
                            str_class.add(liveClassBean.getName());
                        }
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
