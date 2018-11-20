package com.huanglong.v3.song;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.google.gson.Gson;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.model.circle.RelImageBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.netutils.OssRequest;
import com.huanglong.v3.utils.Common;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.SelectPictureUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * Created by bin on 2018/5/5.
 * k歌发布页面
 */
@ContentView(R.layout.activity_song_publich)
public class SongPublishActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.song_publish_cover)
    private ImageView img_cover;
    @ViewInject(R.id.song_publish_content)
    private EditText edt_content;

    private String coverPath;
    private String mp3Path;
    private String imgUrls;
    private String mp3Url;
    private String content;
    private String lrcUrl;
    private String sucai_url;

    private OssRequest ossRequest;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("发布K歌");
        initOSS();

    }

    @Override
    protected void logic() {
        Intent intent = getIntent();
        mp3Path = intent.getStringExtra("path");
        lrcUrl = intent.getStringExtra("lrcUrl");
        sucai_url = intent.getStringExtra("sucai_url");
    }

    @Event(value = {R.id.title_back, R.id.song_publish_cover, R.id.song_publish_submit})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.song_publish_cover:
                SelectPictureUtils.selectedIdCard(this, 700, 438);
                break;
            case R.id.song_publish_submit:
                if (TextUtils.isEmpty(coverPath)) {
                    ToastUtils.showToast("请选择封面图片");
                    return;
                }
                content = edt_content.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showToast("请输入内容");
                    return;
                }
                uploadImg();
//                OssUploadMp3();
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
                        x.image().bind(img_cover, coverPath, MImageOptions.getNormalImageNotCropOptions());
                    }
                    break;
            }
        }
    }

    /**
     * 上传封面图片
     */
    private void uploadImg() {
        showDialog();
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
                        imgUrls = relImageBean.getUrl();
                        OssUploadMp3();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dismissDialog();
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
     * 上传声音文件
     */
//    private void uploadMp3() {
//        RequestParams params = MRequestParams.getNoTokenParams(Api.uploadimg);
//        params.setMultipart(true);
//        params.addBodyParameter("imglist", new File(mp3Path));
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                String json = JsonHandleUtils.JsonHandle(result);
//                if (!TextUtils.isEmpty(json)) {
//                    Gson gson = V3Application.getGson();
//                    RelImageBean relImageBean = gson.fromJson(json, RelImageBean.class);
//                    if (relImageBean != null) {
//                        mp3Url = relImageBean.getUrl();
//                        requestPublish();
//                    }
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                dismissDialog();
//                JsonHandleUtils.netError(ex);
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
//    }

    /**
     * 发布k歌
     */
    private void requestPublish() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.music_publish);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("play_url", mp3Url);
        params.addBodyParameter("cover_img", imgUrls);
        params.addBodyParameter("content", content);
        params.addBodyParameter("lrc_url", lrcUrl);
        params.addBodyParameter("sucai_url", sucai_url);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("发布成功");
                    setResult(RESULT_OK);
                    finish();
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

    /**
     * 初始化OSS
     */
    private void initOSS() {
        ossRequest = new OssRequest(this.getApplicationContext());


        ossRequest.setCallBack(new OssRequest.CallBack() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result, String objectKey) {
                LogUtil.d("PutObject" + "UploadSuccess");
                mp3Url = "http://weisan.oss-cn-beijing.aliyuncs.com/" + objectKey;
                dismissDialog();
                requestPublish();
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                dismissDialog();
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    LogUtil.e("ErrorCode:" + serviceException.getErrorCode());
                    LogUtil.e("RequestId:" + serviceException.getRequestId());
                    LogUtil.e("HostId:" + serviceException.getHostId());
                    LogUtil.e("RawMessage:" + serviceException.getRawMessage());
                }
            }
        });

    }

    /**
     * 上传MP3
     */
    private void OssUploadMp3() {
        if (TextUtils.isEmpty(mp3Path)) {
            ToastUtils.showToast("mp3Path 为空");
            return;
        }
        ossRequest.uploadFile(mp3Path);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ossRequest != null) {
            ossRequest.cancelTask();
        }
    }
}
