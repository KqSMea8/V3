package com.huanglong.v3.smallvideo.videoeditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.smallvideo.SelEffectActivity;
import com.huanglong.v3.smallvideo.TCVideoPublisherActivity;
import com.huanglong.v3.smallvideo.utils.FileUtils;
import com.huanglong.v3.smallvideo.utils.PlayState;
import com.huanglong.v3.smallvideo.videoeditor.bgm.view.TCBGMPannel;
import com.huanglong.v3.smallvideo.videoeditor.bubble.TCBubbleViewInfoManager;
import com.huanglong.v3.smallvideo.videoeditor.common.ActionSheetDialog;
import com.huanglong.v3.smallvideo.videoeditor.filter.TCStaticFilterViewInfoManager;
import com.huanglong.v3.smallvideo.videoeditor.motion.TCMotionViewInfoManager;
import com.huanglong.v3.smallvideo.videoeditor.paster.TCPasterViewInfoManager;
import com.huanglong.v3.smallvideo.videoeditor.time.TCTimeViewInfoManager;
import com.huanglong.v3.smallvideo.videoeditor.utils.TCEditerUtil;
import com.huanglong.v3.smallvideo.widget.VideoWorkProgressFragment;
import com.huanglong.v3.utils.ToastUtils;
import com.tencent.liteav.basic.log.TXCLog;

import com.tencent.ugc.TXRecordCommon;
import com.tencent.ugc.TXUGCRecord;
import com.tencent.ugc.TXVideoEditConstants;
import com.tencent.ugc.TXVideoEditer;
import com.tencent.ugc.TXVideoInfoReader;

import org.xutils.view.annotation.ContentView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;


/**
 * Created by hans on 2017/11/6.
 */
@ContentView(R.layout.activity_video_editer2)   // setContentView(R.layout.activity_video_editer2);)
public class TCVideoEditerActivity extends BaseFragmentActivity implements
        View.OnClickListener,
        TCVideoEditerWrapper.TXVideoPreviewListenerWrapper,
        TXVideoEditer.TXVideoGenerateListener {
    private static final String TAG = "TCVideoEditerActivity";

    // 短视频SDK获取到的视频信息
    private TXVideoEditer mTXVideoEditer;                   // SDK接口类
    /**
     * 布局相关
     */
    private LinearLayout mLlBack;                           // 左上角返回
    private FrameLayout mVideoPlayerLayout;                 // 视频承载布局

    private VideoWorkProgressFragment mWorkLoadingProgress; // 生成视频的等待框


    private int mCurrentState = PlayState.STATE_NONE;       // 播放器当前状态

    private String mVideoOutputPath;                        // 视频输出路径
    private int mVideoResolution = -1;                      // 分辨率类型（如果是从录制过来的话才会有，这参数）

    private long mVideoDuration;                            // 视频的总时长
    private long mPreviewAtTime;                            // 当前单帧预览的时间

    private TXPhoneStateListener mPhoneListener;            // 电话监听

    private KeyguardManager mKeyguardManager;
    private int mVideoFrom;

    private String mRecordProcessedPath;
    private int mCustomBitrate;

    private TCBGMPannel mTCBGMPannel;
    private String mBgmPath;
    private int mBgmPosition = -1;
    private long mBgmDuration;
    private boolean mPublish = false;
    private RelativeLayout mLayoutResult;
    private TextView mBtnComplete;
    private TextView mTvBgm;
    private TextView mTvMotion;
    private TextView mTvSpeed;
    private TextView mTvFilter;
    private TextView mTvPaster;
    private TextView mTvSubtitle;
    private TextView mTvShear;

    public static TCVideoEditerActivity instance;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        instance = this;

        TCVideoEditerWrapper wrapper = TCVideoEditerWrapper.getInstance();

        mTXVideoEditer = wrapper.getEditer();
        if (mTXVideoEditer == null || wrapper.getTXVideoInfo() == null) {
            Toast.makeText(this, "状态异常，结束编辑", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mVideoDuration = wrapper.getTXVideoInfo().duration;
        TCVideoEditerWrapper.getInstance().setCutterStartTime(0, mVideoDuration);


        mVideoResolution = getIntent().getIntExtra(TCConstants.VIDEO_RECORD_RESOLUTION, -1);
        mCustomBitrate = getIntent().getIntExtra(TCConstants.RECORD_CONFIG_BITE_RATE, 0);

        mVideoFrom = getIntent().getIntExtra(TCConstants.VIDEO_RECORD_TYPE, TCConstants.VIDEO_RECORD_TYPE_EDIT);
        // 录制经过预处理的视频路径，在编辑后需要删掉录制源文件
        mRecordProcessedPath = getIntent().getStringExtra(TCConstants.VIDEO_EDITER_PATH);
        initViews();
        initPhoneListener();
        mKeyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
    }

    @Override
    protected void logic() {

    }

    private void initPhoneListener() {
        //设置电话监听
        if (mPhoneListener == null) {
            mPhoneListener = new TXPhoneStateListener(this);
            TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }


    private void initViews() {
        mTvBgm = (TextView) findViewById(R.id.tv_bgm);
        mTvBgm.setOnClickListener(this);
        mTvMotion = (TextView) findViewById(R.id.tv_motion);
        mTvMotion.setOnClickListener(this);
        mTvSpeed = (TextView) findViewById(R.id.tv_speed);
        mTvSpeed.setOnClickListener(this);
        mTvFilter = (TextView) findViewById(R.id.tv_filter);
        mTvFilter.setOnClickListener(this);
        mTvPaster = (TextView) findViewById(R.id.tv_paster);
        mTvPaster.setOnClickListener(this);
        mTvSubtitle = (TextView) findViewById(R.id.tv_subtitle);
        mTvSubtitle.setOnClickListener(this);
        mTvShear = (TextView) findViewById(R.id.tv_shear);
        mTvShear.setOnClickListener(this);

        mLlBack = (LinearLayout) findViewById(R.id.editer_back_ll);
        mLlBack.setOnClickListener(this);

        mBtnComplete = (TextView) findViewById(R.id.btn_complete);
        mBtnComplete.setOnClickListener(this);

        mVideoPlayerLayout = (FrameLayout) findViewById(R.id.editer_fl_video);

        mLayoutResult = (RelativeLayout) findViewById(R.id.layout_publish_success);
        mLayoutResult.setOnClickListener(this);
    }

    /**
     * ==========================================SDK播放器生命周期==========================================
     */
    private void initPlayerLayout() {
        TXVideoEditConstants.TXPreviewParam param = new TXVideoEditConstants.TXPreviewParam();
        param.videoView = mVideoPlayerLayout;
        param.renderMode = TXVideoEditConstants.PREVIEW_RENDER_MODE_FILL_EDGE;
        mTXVideoEditer.initWithPreview(param);
    }

    /**
     * 调用mTXVideoEditer.previewAtTime后，需要记录当前时间，下次播放时从当前时间开始
     * x
     *
     * @param timeMs
     */
    public void previewAtTime(long timeMs) {
        pausePlay();
        mTXVideoEditer.previewAtTime(timeMs);
        mPreviewAtTime = timeMs;
        mCurrentState = PlayState.STATE_PREVIEW_AT_TIME;
    }

    /**
     * 给子Fragment调用 （子Fragment不在意Activity中对于播放器的生命周期）
     */
    public void startPlayAccordingState(long startTime, long endTime) {
        if (mCurrentState == PlayState.STATE_STOP || mCurrentState == PlayState.STATE_NONE || mCurrentState == PlayState.STATE_PREVIEW_AT_TIME) {
            startPlay(startTime, endTime);
        } else if (mCurrentState == PlayState.STATE_PAUSE) {
            resumePlay();
        }
    }

    /**
     * 给子Fragment调用 （子Fragment不在意Activity中对于播放器的生命周期）
     */
    public void restartPlay() {
        stopPlay();
        startPlay(0, mVideoDuration);
    }

    public void startPlay(long startTime, long endTime) {
        Log.e("lyj", "activity startPlay");
        mTXVideoEditer.startPlayFromTime(startTime, endTime);
        mCurrentState = PlayState.STATE_PLAY;
    }

    public void resumePlay() {
        if (mCurrentState == PlayState.STATE_PAUSE) {
            mTXVideoEditer.resumePlay();
            mCurrentState = PlayState.STATE_RESUME;
        }
    }

    public void pausePlay() {
        if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY) {
            mTXVideoEditer.pausePlay();
            mCurrentState = PlayState.STATE_PAUSE;
        }
    }

    public void stopPlay() {
        if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY ||
                mCurrentState == PlayState.STATE_STOP || mCurrentState == PlayState.STATE_PAUSE) {
            Log.e("lyj", "activity startPlay");
            mTXVideoEditer.stopPlay();
            mCurrentState = PlayState.STATE_STOP;
        }
    }


    /**
     * ==========================================activity生命周期==========================================
     */

    @Override
    protected void onRestart() {
        super.onRestart();
        // 在oppo r9s上，锁屏后，按电源键进入解锁状态（屏保画面），也会走onRestart和onResume。因此做个保护
        if (!mKeyguardManager.inKeyguardRestrictedInputMode()) {
            initPlayerLayout();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mKeyguardManager.inKeyguardRestrictedInputMode()) {
            Log.e("lyj", "editer onResume");
            TCVideoEditerWrapper.getInstance().addTXVideoPreviewListenerWrapper(this);
            initPlayerLayout();

            restartPlay();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("lyj", "editer onPause");
        TCVideoEditerWrapper.getInstance().removeTXVideoPreviewListenerWrapper(this);
        stopPlay();
        // 若当前处于生成状态，离开当前activity，直接停止生成
        if (mCurrentState == PlayState.STATE_GENERATE) {
            stopGenerate();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
        if (mPhoneListener != null) {
            TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
        }
        if (mTXVideoEditer != null) {
            mTXVideoEditer.setVideoGenerateListener(null);
            mTXVideoEditer.release();
        }
        // 清除对TXVideoEditer的引用以及相关配置
        TCVideoEditerWrapper.getInstance().clear();

        // 清空保存的气泡字幕参数 （避免下一个视频混入上一个视频的气泡设定
        TCBubbleViewInfoManager.getInstance().clear();
        // 清空保存的贴纸参数
        TCPasterViewInfoManager.getInstance().clear();
        // 清空滤镜动效的状态
        TCMotionViewInfoManager.getInstance().clearMarkInfoList();
        // 清空时间特效的状态
        TCTimeViewInfoManager.getInstance().clearEffect();
        // 清空色调（滤镜）的状态
        TCStaticFilterViewInfoManager.getInstance().clearCurrentPosition();
    }

    /**
     * ==========================================SDK回调==========================================
     */
    @Override // 预览进度回调
    public void onPreviewProgressWrapper(int timeMs) {
    }

    @Override // 预览完成回调
    public void onPreviewFinishedWrapper() {
        TXCLog.i(TAG, "---------------onPreviewFinished-----------------");
        stopPlay();
        startPlay(0, mVideoDuration);
    }


    /**
     * 创建缩略图，并跳转至视频预览的Activity
     */
    private void createThumbFile(final TXVideoEditConstants.TXGenerateResult result) {
        AsyncTask<Void, String, String> task = new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                File outputVideo = new File(mVideoOutputPath);
                if (!outputVideo.exists())
                    return null;
                Bitmap bitmap = TXVideoInfoReader.getInstance().getSampleImage(0, mVideoOutputPath);
                if (bitmap == null)
                    return null;
                String mediaFileName = outputVideo.getAbsolutePath();
                if (mediaFileName.lastIndexOf(".") != -1) {
                    mediaFileName = mediaFileName.substring(0, mediaFileName.lastIndexOf("."));
                }
                String folder = Environment.getExternalStorageDirectory() + File.separator + TCConstants.DEFAULT_MEDIA_PACK_FOLDER + File.separator + mediaFileName;
                File appDir = new File(folder);
                if (!appDir.exists()) {
                    appDir.mkdirs();
                }

                String fileName = "thumbnail" + ".jpg";
                File file = new File(appDir, fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return file.getAbsolutePath();
            }

            @Override
            protected void onPostExecute(String s) {
                if (mVideoFrom == TCConstants.VIDEO_RECORD_TYPE_UGC_RECORD) {
                    FileUtils.deleteFile(mRecordProcessedPath);
                }
                startPreviewActivity(result, s);
            }

        };
        task.execute();
    }

    private void startPreviewActivity(TXVideoEditConstants.TXGenerateResult result, String thumbPath) {
//        ToastUtils.showToast("发布页面");

        TXUGCRecord.getInstance(getApplicationContext()).getPartsManager().deleteAllParts();
        mWorkLoadingProgress.dismiss();
        if (mPublish) {
            Intent intent = new Intent(getApplicationContext(), TCVideoPublisherActivity.class);
            intent.putExtra(TCConstants.VIDEO_RECORD_TYPE, TCConstants.VIDEO_RECORD_TYPE_EDIT);
            intent.putExtra(TCConstants.VIDEO_RECORD_RESULT, result.retCode);
            intent.putExtra(TCConstants.VIDEO_RECORD_DESCMSG, result.descMsg);
            intent.putExtra(TCConstants.VIDEO_RECORD_VIDEPATH, mVideoOutputPath);
            if (thumbPath != null)
                intent.putExtra(TCConstants.VIDEO_RECORD_COVERPATH, thumbPath);
            intent.putExtra(TCConstants.VIDEO_RECORD_DURATION, mVideoDuration);
            startActivity(intent);
        } else {
            ToastUtils.showToast("保存成功，文件管理->本地->TXUGC文件夹中");
//            mLayoutResult.setVisibility(View.VISIBLE);
//            Intent intent = new Intent(this, TCMainActivity.class);
//            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.editer_back_ll:// 返回
                back();
                break;
            case R.id.btn_complete:// 开始生成
                showPublishDialog();
                break;
            case R.id.tv_bgm:
//                intent = new Intent(this, TCVideoEffectActivity.class);
//                intent.putExtra(TCConstants.KEY_FRAGMENT, TCConstants.TYPE_EDITER_BGM);
//                startActivityForResult(intent, TCConstants.ACTIVITY_BGM_REQUEST_CODE);
                intent = new Intent(this, SelEffectActivity.class);
                intent.putExtra("flag", 1);
                intent.putExtra(TCConstants.KEY_FRAGMENT, TCConstants.TYPE_EDITER_BGM);
                startActivityForResult(intent, TCConstants.ACTIVITY_BGM_REQUEST_CODE);
                break;
            case R.id.tv_motion:
                intent = new Intent(this, TCVideoEffectActivity.class);
                intent.putExtra(TCConstants.KEY_FRAGMENT, TCConstants.TYPE_EDITER_MOTION);
                startActivity(intent);
                break;
            case R.id.tv_shear:
                intent = new Intent(this, TCVideoEffectActivity.class);
                intent.putExtra(TCConstants.KEY_FRAGMENT, TCConstants.TYPE_EDITER_SHEAR);
                startActivity(intent);
                break;
            case R.id.tv_speed:
                intent = new Intent(this, TCVideoEffectActivity.class);
                intent.putExtra(TCConstants.KEY_FRAGMENT, TCConstants.TYPE_EDITER_SPEED);
                startActivity(intent);
                break;
            case R.id.tv_filter:
                intent = new Intent(this, TCVideoEffectActivity.class);
                intent.putExtra(TCConstants.KEY_FRAGMENT, TCConstants.TYPE_EDITER_FILTER);
                startActivity(intent);
                break;
            case R.id.tv_paster:
                intent = new Intent(this, TCVideoEffectActivity.class);
                intent.putExtra(TCConstants.KEY_FRAGMENT, TCConstants.TYPE_EDITER_PASTER);
                startActivity(intent);
                break;
            case R.id.tv_subtitle:
                intent = new Intent(this, TCVideoEffectActivity.class);
                intent.putExtra(TCConstants.KEY_FRAGMENT, TCConstants.TYPE_EDITER_SUBTITLE);
                startActivity(intent);
                break;
//            case R.id.layout_publish_success:
//                intent = new Intent(this, TCMainActivity.class);
//                startActivity(intent);
//                finish();
//                break;
        }
    }

    private void showPublishDialog() {
        new ActionSheetDialog(this).builder().setCancelable(false)
                .setCancelable(false)
                .addSheetItem("保存", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        mPublish = false;
                        startGenerateVideo();
                    }
                })
                .addSheetItem("发布", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        mPublish = true;
                        startGenerateVideo();
                    }
                })
                .show();
    }

    private void back() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.setTitle(getString(R.string.tips)).setCancelable(false).setMessage(R.string.confirm_cancel_edit_content)
                .setPositiveButton(R.string.btn_return, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.wrong_click), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();
    }

    public void switchPlayVideo() {
        if (mCurrentState == PlayState.STATE_NONE || mCurrentState == PlayState.STATE_STOP) {
            TXVideoEditConstants.TXVideoInfo info = TCVideoEditerWrapper.getInstance().getTXVideoInfo();
            startPlay(0, info.duration);
        } else if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY) {
            pausePlay();
        } else if (mCurrentState == PlayState.STATE_PAUSE) {
            resumePlay();
        } else if (mCurrentState == PlayState.STATE_PREVIEW_AT_TIME) {
            startPlay(mPreviewAtTime, mVideoDuration);
        }
    }

    /**
     * =========================================视频生成相关==========================================
     */
    private void startGenerateVideo() {
        stopPlay(); // 停止播放

        // 处于生成状态
        mCurrentState = PlayState.STATE_GENERATE;
        // 生成视频输出路径
        mVideoOutputPath = TCEditerUtil.generateVideoPath();

        if (mWorkLoadingProgress == null) {
            initWorkLoadingProgress();
        }
        mWorkLoadingProgress.setProgress(0);
        mWorkLoadingProgress.setCancelable(false);
        mWorkLoadingProgress.show(getSupportFragmentManager(), "progress_dialog");

        // 添加片尾水印
        addTailWaterMark();

        mTXVideoEditer.setCutFromTime(0, mVideoDuration);
        mTXVideoEditer.setVideoGenerateListener(this);

        if (mVideoResolution == -1) {// 默认情况下都将输出720的视频
            if (mCustomBitrate != 0) { // 是否自定义码率
                mTXVideoEditer.setVideoBitrate(mCustomBitrate);
            }
            mTXVideoEditer.generateVideo(TXVideoEditConstants.VIDEO_COMPRESSED_720P, mVideoOutputPath);
        } else if (mVideoResolution == TXRecordCommon.VIDEO_RESOLUTION_360_640) {
            mTXVideoEditer.generateVideo(TXVideoEditConstants.VIDEO_COMPRESSED_360P, mVideoOutputPath);
        } else if (mVideoResolution == TXRecordCommon.VIDEO_RESOLUTION_540_960) {
            mTXVideoEditer.generateVideo(TXVideoEditConstants.VIDEO_COMPRESSED_540P, mVideoOutputPath);
        } else if (mVideoResolution == TXRecordCommon.VIDEO_RESOLUTION_720_1280) {
            mTXVideoEditer.generateVideo(TXVideoEditConstants.VIDEO_COMPRESSED_720P, mVideoOutputPath);
        }
    }

    private void stopGenerate() {
        if (mCurrentState == PlayState.STATE_GENERATE) {
            mWorkLoadingProgress.dismiss();
            Toast.makeText(TCVideoEditerActivity.this, "取消视频生成", Toast.LENGTH_SHORT).show();
            mWorkLoadingProgress.setProgress(0);
            mCurrentState = PlayState.STATE_NONE;
            if (mTXVideoEditer != null) {
                mTXVideoEditer.cancel();
            }
        }
    }

    /**
     * 添加片尾水印
     */
    private void addTailWaterMark() {
        TXVideoEditConstants.TXVideoInfo info = TCVideoEditerWrapper.getInstance().getTXVideoInfo();

        Bitmap tailWaterMarkBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.tcloud_logo);
        float widthHeightRatio = tailWaterMarkBitmap.getWidth() / (float) tailWaterMarkBitmap.getHeight();

        TXVideoEditConstants.TXRect txRect = new TXVideoEditConstants.TXRect();
        txRect.width = 0.25f; // 归一化的片尾水印，这里设置了一个固定值，水印占屏幕宽度的0.25。
        // 后面根据实际图片的宽高比，计算出对应缩放后的图片的宽度：txRect.width * videoInfo.width 和高度：txRect.width * videoInfo.width / widthHeightRatio，然后计算出水印放中间时的左上角位置
        txRect.x = (info.width - txRect.width * info.width) / (2f * info.width);
        txRect.y = (info.height - txRect.width * info.width / widthHeightRatio) / (2f * info.height);

        mTXVideoEditer.setTailWaterMark(tailWaterMarkBitmap, txRect, 3);
    }


    @Override // 生成进度回调
    public void onGenerateProgress(final float progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWorkLoadingProgress.setProgress((int) (progress * 100));
            }
        });
    }

    @Override // 生成完成
    public void onGenerateComplete(final TXVideoEditConstants.TXGenerateResult result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (result.retCode == TXVideoEditConstants.GENERATE_RESULT_OK) {
                    // 生成成功
                    createThumbFile(result);
                } else {
                    Toast.makeText(TCVideoEditerActivity.this, result.descMsg, Toast.LENGTH_SHORT).show();
                }
                mCurrentState = PlayState.STATE_NONE;
            }
        });
    }

    /**
     * ==========================================进度条==========================================
     */
    private void initWorkLoadingProgress() {
        if (mWorkLoadingProgress == null) {
            mWorkLoadingProgress = new VideoWorkProgressFragment();
            mWorkLoadingProgress.setOnClickStopListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopGenerate();
                }
            });
        }
        mWorkLoadingProgress.setProgress(0);
    }

    /*********************************************监听电话状态**************************************************/
    static class TXPhoneStateListener extends PhoneStateListener {
        WeakReference<TCVideoEditerActivity> mEditer;

        public TXPhoneStateListener(TCVideoEditerActivity editer) {
            mEditer = new WeakReference<TCVideoEditerActivity>(editer);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            TCVideoEditerActivity activity = mEditer.get();
            if (activity == null) return;
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:  //电话等待接听
                case TelephonyManager.CALL_STATE_OFFHOOK:  //电话接听
                    // 生成状态 取消生成
                    if (activity.mCurrentState == PlayState.STATE_GENERATE) {
                        activity.stopGenerate();
                    }
                    // 直接停止播放
                    Log.e("lyj", "onCallStateChanged stopPlay");
                    activity.stopPlay();
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    // 重新开始播放
                    Log.e("lyj", "onCallStateChanged restartPlay");
                    activity.restartPlay();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }
}
