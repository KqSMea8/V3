package com.huanglong.v3.voice;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.netutils.OssRequest;
import com.huanglong.v3.utils.DateUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;

/**
 * Created by bin on 2018/5/3.
 * 音频发布页面
 */
@ContentView(R.layout.activity_sound_publish)
public class SoundPublishActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.sound_publish_play)
    private ImageView img_play;
    @ViewInject(R.id.sound_publish_title)
    private EditText edt_voc_title;
    @ViewInject(R.id.sound_publish_class)
    private TextView tv_chapter;


    private String soundpath;
    private String soundUrl;
    private String str_voc_title;
    private String chapter_id;

    private MediaPlayer mMediaPlayer;

    private boolean isStartPlay = true;

    private OssRequest ossRequest;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("发布音频");
        initOSS();
    }

    @Override
    protected void logic() {
        mMediaPlayer = new MediaPlayer();
        Intent intent = getIntent();
        soundpath = intent.getStringExtra("soundpath");

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                img_play.setImageResource(R.mipmap.icon_suspend);
                mediaPlayer.stop();
                mediaPlayer.release();
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
                soundUrl = "http://weisan.oss-cn-beijing.aliyuncs.com/" + objectKey;
                dismissDialog();
                requestSubmitInfo();
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
     * 播放声音
     */
    private void playSound() {
        if (TextUtils.isEmpty(soundpath)) {
            ToastUtils.showToast("无效的播放地址");
            return;
        }
        mMediaPlayer.reset();
        try {
            // 设置指定的流媒体地址
            mMediaPlayer.setDataSource(soundpath);
            // 设置音频流的类型
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 通过异步的方式装载媒体资源
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    int duration = mp.getDuration();
                    String total_time = DateUtils.timeParse(duration);
                    mMediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtils.showToast("播放失败");
        }
    }


    @Event(value = {R.id.title_back, R.id.sound_publish_play, R.id.sound_publish_class_lin, R.id.sound_publish_submit})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.sound_publish_play:
                if (isStartPlay) {
                    img_play.setImageResource(R.mipmap.icon_suspend);
                    playSound();
                    isStartPlay = false;
                } else {
                    if (mMediaPlayer.isPlaying()) {
                        img_play.setImageResource(R.mipmap.icon_record_play);
                        mMediaPlayer.pause();
                    } else {
                        img_play.setImageResource(R.mipmap.icon_suspend);
                        mMediaPlayer.start();
                    }
                }
                break;
            case R.id.sound_publish_class_lin:
                Intent intent = new Intent();
                intent.setClass(this, VocClassActivity.class);
                startActivityForResult(intent, 1000);
                break;
            case R.id.sound_publish_submit:
                if (validateInfo()) {
                    requestUploadFile();
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    if (data == null) return;
                    chapter_id = data.getStringExtra("class_id");
                    String chapter_name = data.getStringExtra("class_name");
                    tv_chapter.setText(chapter_name);
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            img_play.setImageResource(R.mipmap.icon_record_play);
            mMediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (ossRequest != null) {
            ossRequest.cancelTask();
        }

    }


    /**
     * 验证信息
     *
     * @return
     */
    private boolean validateInfo() {
        str_voc_title = edt_voc_title.getText().toString().trim();
        if (TextUtils.isEmpty(str_voc_title)) {
            ToastUtils.showToast("请输入标题");
            return false;
        }

        if (TextUtils.isEmpty(chapter_id)) {
            ToastUtils.showToast("请选择分类");
            return false;
        }
        return true;
    }

    /**
     * 上传音频文件
     */
    private void requestUploadFile() {
        showDialog();
        ossRequest.uploadFile(soundpath);

//        RequestParams params = MRequestParams.getNoTokenParams(Api.uploadimg);
//        params.setMultipart(true);
//        params.addBodyParameter("imglist", new File(soundpath));
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                String json = JsonHandleUtils.JsonHandle(result);
//                if (!TextUtils.isEmpty(json)) {
//                    Gson gson = V3Application.getGson();
//                    RelImageBean relImageBean = gson.fromJson(json, RelImageBean.class);
//                    if (relImageBean != null) {
//                        soundUrl = relImageBean.getUrl();
//                        requestSubmitInfo();
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
    }


    /**
     * 上传音效
     */
    private void requestSubmitInfo() {

        RequestParams params = MRequestParams.getNoTokenParams(Api.book_publish);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("book_id", chapter_id);
        params.addBodyParameter("name", str_voc_title);
        params.addBodyParameter("play_url", soundUrl);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("上传成功");
                    setResult(TCConstants.ACTIVITY_BGM_REQUEST_CODE);
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


}
