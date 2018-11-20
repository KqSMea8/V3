package com.huanglong.v3.smallvideo;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.homepage.VideoActivity;
import com.huanglong.v3.im.view.ListPickerDialog;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.model.homepage.LiveClassBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.smallvideo.videoeditor.TCVideoEditerActivity;
import com.huanglong.v3.smallvideo.videoeditor.TCVideoEffectActivity;
import com.huanglong.v3.smallvideo.videoeditor.TCVideoPreprocessActivity;
import com.huanglong.v3.smallvideo.videorecord.TCVideoRecordActivity;
import com.huanglong.v3.smallvideo.videoupload.TXUGCPublish;
import com.huanglong.v3.smallvideo.videoupload.TXUGCPublishTypeDef;
import com.huanglong.v3.utils.LocationAMapUtils;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.tencent.rtmp.TXLiveConstants;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/26.
 * 小视频发布页面
 */
@ContentView(R.layout.activity_video_publisher2)
public class TCVideoPublisherActivity extends BaseFragmentActivity implements TXUGCPublishTypeDef.ITXVideoPublishListener {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.video_publisher_cover)
    private ImageView img_cover;
    @ViewInject(R.id.video_publisher_location)
    private TextView tv_location;
    @ViewInject(R.id.video_publisher_category)
    private TextView tv_category;
    @ViewInject(R.id.video_publisher_is_public)
    private TextView tv_is_public;
    @ViewInject(R.id.video_publisher_content)
    private EditText edt_content;


    private String mVideoPath;
    private String mCoverPath;
    private String mCosSignature;
    private String str_location;
    private String content;

    private int mRotation;
    private boolean mDisableCache;
    private String mLocalVideoPath;
    private boolean mIsPlayRecordType;

    private String[] is_public = {"公开", "隐私", "好友可见"};
    private int public_status = 0;
    private String category_id;


    private LocationAMapUtils locationAMapUtils;

    private Handler mHandler = new Handler();

    private TXUGCPublish mVideoPublish;
    private List<LiveClassBean> liveClassBeans;

    private List<String> str_class = new ArrayList<>();


    @Override
    protected Activity getActivity() {
        return null;
    }

    @Override
    protected void initView() {
        tv_title.setText("发布");
        tv_is_public.setText("公开");
        locationAMapUtils = new LocationAMapUtils(this);

    }

    @Override
    protected void logic() {

        mVideoPath = getIntent().getStringExtra(TCConstants.VIDEO_RECORD_VIDEPATH);
        mCoverPath = getIntent().getStringExtra(TCConstants.VIDEO_RECORD_COVERPATH);
        mRotation = getIntent().getIntExtra(TCConstants.VIDEO_RECORD_ROTATION, TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mDisableCache = getIntent().getBooleanExtra(TCConstants.VIDEO_RECORD_NO_CACHE, false);
        mLocalVideoPath = getIntent().getStringExtra(TCConstants.VIDEO_RECORD_VIDEPATH);
        mIsPlayRecordType = getIntent().getIntExtra(TCConstants.VIDEO_RECORD_TYPE, 0) == TCConstants.VIDEO_RECORD_TYPE_PLAY;

        x.image().bind(img_cover, mCoverPath, MImageOptions.getNormalImageOptions());
        requestCosSignature();
        requestLiveClass();

        locationAMapUtils.setLocationClient(new LocationAMapUtils.LocationClientOption() {
            @Override
            public void onLocationSuccess(AMapLocation location) {
                str_location = location.getProvince() + location.getCity() + location.getDistrict() + location.getStreet() + location.getStreetNum();
                tv_location.setText(str_location);
            }

            @Override
            public void onLocationFail(int errorCode, String errorMsg) {
                ToastUtils.showToast("定位失败");
            }
        });
    }

    @Event(value = {R.id.title_back, R.id.video_publisher_submit, R.id.video_publisher_category_lin, R.id.video_publisher_is_public_lin,
            R.id.video_publisher_location})
    private void onClicl(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.video_publisher_submit:
                if (validate()) {
                    requestPublisherTen();
                }
                break;
            case R.id.video_publisher_category_lin:
                if (str_class != null && str_class.size() > 0) {
                    String[] str_jobs = str_class.toArray(new String[str_class.size()]);
                    new ListPickerDialog().show(str_jobs, getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String str_job = str_jobs[which];
                            tv_category.setText(str_job);
                            for (LiveClassBean liveClassBean : liveClassBeans) {
                                if (TextUtils.equals(str_job, liveClassBean.getName())) {
                                    category_id = liveClassBean.getId();
                                    break;
                                }
                            }
                        }
                    });
                }
                break;
            case R.id.video_publisher_is_public_lin:
                new ListPickerDialog().show(is_public, getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = is_public[which];
                        tv_is_public.setText(s);
                        if (TextUtils.equals(s, "公开")) {
                            public_status = 0;
                        } else if (TextUtils.equals(s, "隐私")) {
                            public_status = 2;
                        } else {
                            public_status = 1;
                        }
                    }
                });
                break;
            case R.id.video_publisher_location:
                locationAMapUtils.startLocation();
                break;
        }
    }

    /**
     * 验证输入信息
     *
     * @return
     */
    private boolean validate() {
        content = edt_content.getText().toString().trim();
        if (TextUtils.isEmpty(category_id)) {
            ToastUtils.showToast("请选择分类");
            return false;
        }

        return true;
    }


    /**
     * 请求直播分类
     */
    private void requestLiveClass() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.video_video_cate_list);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    liveClassBeans = gson.fromJson(json, new TypeToken<LinkedList<LiveClassBean>>() {
                    }.getType());
                    if (liveClassBeans != null && liveClassBeans.size() > 0) {
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


    /**
     * 上传视频到腾讯端
     */
    private void requestPublisherTen() {

        showDialog();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mVideoPublish == null)
                    mVideoPublish = new TXUGCPublish(TCVideoPublisherActivity.this.getApplicationContext(), UserInfoUtils.getUid());
                mVideoPublish.setListener(TCVideoPublisherActivity.this);

                TXUGCPublishTypeDef.TXPublishParam param = new TXUGCPublishTypeDef.TXPublishParam();
                param.signature = mCosSignature;
                param.videoPath = mVideoPath;
                param.coverPath = mCoverPath;
                int publishCode = mVideoPublish.publishVideo(param);
                if (publishCode != 0) {
                    dismissDialog();
                    ToastUtils.showToast("发布失败");
//                    mTVPublish.setText("发布失败，错误码：" + publishCode);
                }

//                IntentFilter intentFilter = new IntentFilter();
//                intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//                if (null == mNetchangeReceiver) {
//                    mNetchangeReceiver = new NetchangeReceiver();
//                }
//                TCVideoPublisherActivity.this.getApplicationContext().registerReceiver(mNetchangeReceiver, intentFilter);
            }
        });
    }

    /**
     * 获取签名
     */
    private void requestCosSignature() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.generate_signatrue);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    mCosSignature = json;
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
    public void onPublishProgress(long uploadBytes, long totalBytes) {

    }

    @Override
    public void onPublishComplete(TXUGCPublishTypeDef.TXPublishResult result) {
        LogUtil.e("----onPublishComplete:上传成功--》retCode:" + result.retCode);
        LogUtil.e("----onPublishComplete retCode:" + result.retCode + ",videoId:" + result.videoId + ",descMsg:" + result.descMsg
                + ",videoURL:" + result.videoURL + ",coverURL:" + result.coverURL);
        requestPublisher(result.videoURL, result.coverURL, result.videoId);
    }

    /**
     * 发布视屏到自己服务器
     */
    private void requestPublisher(String play_url, String cover_url, String fileId) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.video_publish);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("name", "小视频");
        params.addBodyParameter("cover_img", cover_url);
        params.addBodyParameter("location", str_location);
        params.addBodyParameter("is_public", public_status + "");
        params.addBodyParameter("content", content);
        params.addBodyParameter("category_id", category_id);
        params.addBodyParameter("play_url", play_url);
        params.addBodyParameter("fileId", fileId);


        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("发布成功");
                    if (VideoActivity.instance != null) {
                        VideoActivity.instance.refreshRecommend();
                    }
                    closeActivity();
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
                dismissDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationAMapUtils.stopLocation();
    }

    /**
     * 关闭Activity
     */
    private void closeActivity() {

        if (TCVideoPreviewActivity.instance != null) {
            TCVideoPreviewActivity.instance.finish();
        }
        if (TCVideoEditerActivity.instance != null) {
            TCVideoEditerActivity.instance.finish();
        }

        if (TCVideoEffectActivity.instance != null) {
            TCVideoEffectActivity.instance.finish();
        }
        if (TCVideoPreprocessActivity.instance != null) {
            TCVideoPreprocessActivity.instance.finish();
        }
        if (TCVideoRecordActivity.instance != null) {
            TCVideoRecordActivity.instance.finish();
        }
        if (EffectActivity.instance != null) {
            EffectActivity.instance.finish();
        }
        finish();
    }

}
