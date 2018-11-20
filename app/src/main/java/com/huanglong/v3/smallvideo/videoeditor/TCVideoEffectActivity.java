package com.huanglong.v3.smallvideo.videoeditor;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huanglong.v3.R;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.smallvideo.BaseEditFragment;
import com.huanglong.v3.smallvideo.SelEffectActivity;
import com.huanglong.v3.smallvideo.TCVideoPreviewActivity;
import com.huanglong.v3.smallvideo.utils.FileUtils;
import com.huanglong.v3.smallvideo.utils.PlayState;
import com.huanglong.v3.smallvideo.utils.TCUtils;
import com.huanglong.v3.smallvideo.videoeditor.bubble.TCBubbleFragment;
import com.huanglong.v3.smallvideo.videoeditor.common.widget.videotimeline.VideoProgressController;
import com.huanglong.v3.smallvideo.videoeditor.common.widget.videotimeline.VideoProgressView;
import com.huanglong.v3.smallvideo.videoeditor.cutter.TCCutterFragment;
import com.huanglong.v3.smallvideo.videoeditor.filter.TCStaticFilterFragment;
import com.huanglong.v3.smallvideo.videoeditor.motion.TCMotionFragment;
import com.huanglong.v3.smallvideo.videoeditor.paster.TCPasterFragment;
import com.huanglong.v3.smallvideo.videoeditor.paster.view.TCPasterSelectView;
import com.huanglong.v3.smallvideo.videoeditor.time.TCTimeFragment;
import com.huanglong.v3.smallvideo.videoeditor.utils.TCEditerUtil;
import com.huanglong.v3.smallvideo.widget.VideoWorkProgressFragment;
import com.tencent.liteav.basic.log.TXCLog;

import com.tencent.ugc.TXRecordCommon;
import com.tencent.ugc.TXVideoEditConstants;
import com.tencent.ugc.TXVideoEditer;
import com.tencent.ugc.TXVideoInfoReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;


/**
 * Created by hans on 2017/11/6.
 */

public class TCVideoEffectActivity extends FragmentActivity implements
        View.OnClickListener,
        TCVideoEditerWrapper.TXVideoPreviewListenerWrapper,
        TXVideoEditer.TXVideoGenerateListener {
    private static final String TAG = "TCVideoEditerActivity";

    // 短视频SDK获取到的视频信息
    private TXVideoEditer mTXVideoEditer;                   // SDK接口类
    /**
     * 布局相关
     */
    private ImageView mLlBack;                              // 左上角返回
    private FrameLayout mVideoPlayerLayout;                 // 视频承载布局
    private ImageView mIvPlay;                              // 播放按钮
    private Button mTvDone;

    private VideoWorkProgressFragment mWorkLoadingProgress; // 生成视频的等待框


    private BaseEditFragment mCurrentFragment,              // 标记当前的Fragment
            mTimeFragment,                                  // 时间特效的Fragment
            mStaticFilterFragment,                          // 静态滤镜的Fragment
            mMotionFragment,                                // 动态滤镜的Fragment
            mBGMSettingFragment,                            // BGM设置的Fragment
            mPasterFragment,                                // 贴纸的Fragment
            mCutterFragment,                                // 裁剪的Fragment
            mBubbleFragment;                                // 气泡字幕的Fragment
    public TCPasterSelectView mTCPasterSelectView;

    private int mCurrentState = PlayState.STATE_NONE;       // 播放器当前状态

    private String mVideoOutputPath;                        // 视频输出路径
    private int mVideoResolution = -1;                      // 分辨率类型（如果是从录制过来的话才会有，这参数）

    private long mVideoDuration;                            // 视频的总时长
    private long mPreviewAtTime;                            // 当前单帧预览的时间

    private TXPhoneStateListener mPhoneListener;            // 电话监听

    private KeyguardManager mKeyguardManager;
    private int mVideoFrom;

    public static TCVideoEffectActivity instance;


    /**
     * 缩略图进度条相关
     */
    private VideoProgressView mVideoProgressView;
    public VideoProgressController mVideoProgressController;
    private VideoProgressController.VideoProgressSeekListener mVideoProgressSeekListener = new VideoProgressController.VideoProgressSeekListener() {
        @Override
        public void onVideoProgressSeek(long currentTimeMs) {
            TXCLog.i(TAG, "onVideoProgressSeek, currentTimeMs = " + currentTimeMs);

            previewAtTime(currentTimeMs);
        }

        @Override
        public void onVideoProgressSeekFinish(long currentTimeMs) {
            TXCLog.i(TAG, "onVideoProgressSeekFinish, currentTimeMs = " + currentTimeMs);

            previewAtTime(currentTimeMs);
        }
    };
    private String mRecordProcessedPath;
    private int mCustomBitrate;
    private int mFragmentType;
    private TextView mTvCurrent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_effect);
        instance = this;
        TCVideoEditerWrapper wrapper = TCVideoEditerWrapper.getInstance();
        wrapper.addTXVideoPreviewListenerWrapper(this);

        mTXVideoEditer = wrapper.getEditer();
        if (mTXVideoEditer == null || wrapper.getTXVideoInfo() == null) {
            Toast.makeText(this, "状态异常，结束编辑", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mVideoDuration = wrapper.getTXVideoInfo().duration;
        TCVideoEditerWrapper.getInstance().setCutterStartTime(0, mVideoDuration);

        mFragmentType = getIntent().getIntExtra(TCConstants.KEY_FRAGMENT, 0);
        mVideoResolution = getIntent().getIntExtra(TCConstants.VIDEO_RECORD_RESOLUTION, -1);
        mCustomBitrate = getIntent().getIntExtra(TCConstants.RECORD_CONFIG_BITE_RATE, 0);

        mVideoFrom = getIntent().getIntExtra(TCConstants.VIDEO_RECORD_TYPE, TCConstants.VIDEO_RECORD_TYPE_EDIT);
        // 录制经过预处理的视频路径，在编辑后需要删掉录制源文件
        mRecordProcessedPath = getIntent().getStringExtra(TCConstants.VIDEO_EDITER_PATH);

        initViews();
        initPhoneListener();
        initVideoProgressLayout();
        previewVideo();// 开始预览视频
        mKeyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
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
        mLlBack = (ImageView) findViewById(R.id.editer_back_ll);
        mLlBack.setOnClickListener(this);
        mTvDone = (Button) findViewById(R.id.editer_tv_done);
        mTvDone.setOnClickListener(this);
        mVideoPlayerLayout = (FrameLayout) findViewById(R.id.editer_fl_video);

        mIvPlay = (ImageView) findViewById(R.id.iv_play);
        mIvPlay.setOnClickListener(this);

        mTvCurrent = (TextView) findViewById(R.id.tv_current);
    }

    /**
     * ==========================================SDK播放器生命周期==========================================
     */
    private void previewVideo() {
        showFragmentByType(mFragmentType);
        initVideoProgressLayout();  // 初始化进度布局
        initPlayerLayout();         // 初始化预览视频布局
        startPlay(0, mVideoDuration);  // 开始播放
    }

    private void showFragmentByType(int type) {
        switch (type) {
            case TCConstants.TYPE_EDITER_BGM:
                showBGMFragment();
                break;
            case TCConstants.TYPE_EDITER_MOTION:
                showMotionFragment();
                break;
            case TCConstants.TYPE_EDITER_SPEED:
                showTimeFragment();
                break;
            case TCConstants.TYPE_EDITER_FILTER:
                showFilterFragment();
                break;
            case TCConstants.TYPE_EDITER_PASTER:
                showPasterFragment();
                break;
            case TCConstants.TYPE_EDITER_SUBTITLE:
                showBubbleFragment();
                break;
            case TCConstants.TYPE_EDITER_SHEAR:
                showCutterFragment();
                break;

        }
    }

    private void initVideoProgressLayout() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int screenWidth = point.x;
        mVideoProgressView = (VideoProgressView) findViewById(R.id.editer_video_progress_view);
        mVideoProgressView.setViewWidth(screenWidth);

        List<Bitmap> thumbnailList = TCVideoEditerWrapper.getInstance().getAllThumbnails();
        mVideoProgressView.setThumbnailData(thumbnailList);

        mVideoProgressController = new VideoProgressController(mVideoDuration);
        mVideoProgressController.setVideoProgressView(mVideoProgressView);
        mVideoProgressController.setVideoProgressSeekListener(mVideoProgressSeekListener);
        mVideoProgressController.setVideoProgressDisplayWidth(screenWidth);
    }

    public void switchReverse() {
        mVideoProgressView.setReverse();
    }

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
        mTXVideoEditer.startPlayFromTime(startTime, endTime);
        mCurrentState = PlayState.STATE_PLAY;
        mIvPlay.setImageResource(R.mipmap.ic_pause_normal);

        mCurrentFragment.notifyStartPlay();
    }


    public void resumePlay() {
        if (mCurrentState == PlayState.STATE_PAUSE) {
            mTXVideoEditer.resumePlay();
            mCurrentState = PlayState.STATE_RESUME;
            mIvPlay.setImageResource(R.mipmap.ic_pause_normal);

            mCurrentFragment.notifyResumePlay();
        }
    }

    public void pausePlay() {
        if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY) {
            mTXVideoEditer.pausePlay();
            mCurrentState = PlayState.STATE_PAUSE;
            mIvPlay.setImageResource(R.mipmap.ic_play_normal);

            mCurrentFragment.notifyPausePlay();
        }
    }

    public void stopPlay() {
        if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY ||
                mCurrentState == PlayState.STATE_STOP || mCurrentState == PlayState.STATE_PAUSE) {
            mTXVideoEditer.stopPlay();
            mCurrentState = PlayState.STATE_STOP;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mIvPlay.setImageResource(R.mipmap.ic_play_normal);
                }
            });
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
            Log.e("activity", "effect onResume");
            restartPlay();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("activity", "effect onPause");
        pausePlay();
        // 若当前处于生成状态，离开当前activity，直接停止生成
        if (mCurrentState == PlayState.STATE_GENERATE) {
            stopGenerate();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mBGMSettingFragment != null && mBGMSettingFragment.isAdded()) {
            mBGMSettingFragment.onActivityResult(requestCode, resultCode, data);
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
            Log.e("activity", "effect onDestroy");
//            stopPlay();
            mTXVideoEditer.setVideoGenerateListener(null);
//            mTXVideoEditer.release();
        }
        TCVideoEditerWrapper.getInstance().removeTXVideoPreviewListenerWrapper(this);
        // 清除对TXVideoEditer的引用以及相关配置
//        TCVideoEditerWrapper.getInstance().clear();

        // 清空保存的气泡字幕参数 （避免下一个视频混入上一个视频的气泡设定
//        TCBubbleViewInfoManager.getInstance().clear();
        // 清空保存的贴纸参数
//        TCPasterViewInfoManager.getInstance().clear();
    }

    /**
     * ==========================================SDK回调==========================================
     */
    @Override // 预览进度回调
    public void onPreviewProgressWrapper(final int timeMs) {
        // 视频的进度回调是异步的，如果不是处于播放状态，那么无需修改进度
        if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mVideoProgressController.setCurrentTimeMs(timeMs);
                    mTvCurrent.setText(TCUtils.duration(timeMs));
                }
            });
        }
    }

    @Override // 预览完成回调
    public void onPreviewFinishedWrapper() {
        TXCLog.d(TAG, "---------------onPreviewFinished-----------------");
        stopPlay();
        if ((mMotionFragment != null && mMotionFragment.isAdded() && !mMotionFragment.isHidden()) ||
                (mTimeFragment != null && mTimeFragment.isAdded() && !mTimeFragment.isHidden())) {
            // 处于动态滤镜或者时间特效界面,忽略 不做任何操作
        } else {
            // 如果当前不是动态滤镜界面或者时间特效界面，那么会自动开始重复播放
            startPlay(0, mVideoDuration);
        }
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
        Intent intent = new Intent(getApplicationContext(), TCVideoPreviewActivity.class);
        intent.putExtra(TCConstants.VIDEO_RECORD_TYPE, TCConstants.VIDEO_RECORD_TYPE_EDIT);
        intent.putExtra(TCConstants.VIDEO_RECORD_RESULT, result.retCode);
        intent.putExtra(TCConstants.VIDEO_RECORD_DESCMSG, result.descMsg);
        intent.putExtra(TCConstants.VIDEO_RECORD_VIDEPATH, mVideoOutputPath);
        if (thumbPath != null)
            intent.putExtra(TCConstants.VIDEO_RECORD_COVERPATH, thumbPath);
        intent.putExtra(TCConstants.VIDEO_RECORD_DURATION, mVideoDuration);
        startActivity(intent);
        finish();
    }

    /**
     * ==========================================工具栏的点击回调==========================================
     */
    private void showTimeFragment() {
        if (mTimeFragment == null) {
            mTimeFragment = new TCTimeFragment();
        }
        showFragment(mTimeFragment, "time_fragment");
    }

    private void showFilterFragment() {
        if (mStaticFilterFragment == null) {
            mStaticFilterFragment = new TCStaticFilterFragment();
        }
        showFragment(mStaticFilterFragment, "static_filter_fragment");
    }

    private void showMotionFragment() {
        if (mMotionFragment == null) {
            mMotionFragment = new TCMotionFragment();
        }
        showFragment(mMotionFragment, "motion_fragment");
    }

    private void showPasterFragment() {
        if (mPasterFragment == null) {
            mPasterFragment = new TCPasterFragment();
        }
        showFragment(mPasterFragment, "paster_fragment");
    }

    private void showBubbleFragment() {
        if (mBubbleFragment == null) {
            mBubbleFragment = new TCBubbleFragment();
        }
        showFragment(mBubbleFragment, "bubble_fragment");
    }

    private void showCutterFragment() {
        if (mCutterFragment == null) {
            mCutterFragment = new TCCutterFragment();
        }
        showFragment(mCutterFragment, "cutter_fragment");
    }


    private void showBGMFragment() {

        Intent intent = new Intent();
        intent.setClass(this, SelEffectActivity.class);
        startActivity(intent);


//        if (mBGMSettingFragment == null) {
//            mBGMSettingFragment = new TCBGMSettingFragment();
//        }
//        showFragment(mBGMSettingFragment, "bgm_setting_fragment");
    }

    private void showFragment(BaseEditFragment fragment, String tag) {
        if (fragment == mCurrentFragment) return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
        }
        if (!fragment.isAdded()) {
            transaction.add(R.id.editer_fl_container, fragment, tag);
        } else {
            transaction.show(fragment);
        }
        mCurrentFragment = fragment;
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editer_back_ll:// 返回
//                mTXVideoEditer.release();
                finish();
                break;
            case R.id.editer_tv_done:// 开始生成
                finish();
//                startGenerateVideo();
                break;
            case R.id.iv_play:// 播放
                TXCLog.i(TAG, "editer_ib_play clicked, mCurrentState = " + mCurrentState);
                switchPlayVideo();
                break;
        }
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

    public int getmCurrentState() {
        return mCurrentState;
    }


    /**
     * =========================================视频生成相关==========================================
     */
    private void startGenerateVideo() {
        stopPlay(); // 停止播放

        // 处于生成状态
        mCurrentState = PlayState.STATE_GENERATE;
        // 防止
        mTvDone.setEnabled(false);
        mTvDone.setClickable(false);
        // 生成视频输出路径
        mVideoOutputPath = TCEditerUtil.generateVideoPath();

        mIvPlay.setImageResource(R.mipmap.ic_play_normal);

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
            mTvDone.setEnabled(true);
            mTvDone.setClickable(true);
            mWorkLoadingProgress.dismiss();
            Toast.makeText(TCVideoEffectActivity.this, "取消视频生成", Toast.LENGTH_SHORT).show();
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
    public void onGenerateProgress(float progress) {
        mWorkLoadingProgress.setProgress((int) (progress * 100));
    }

    @Override // 生成完成
    public void onGenerateComplete(TXVideoEditConstants.TXGenerateResult result) {
        if (result.retCode == TXVideoEditConstants.GENERATE_RESULT_OK) {
            // 生成成功
            createThumbFile(result);
        } else {
            Toast.makeText(TCVideoEffectActivity.this, result.descMsg, Toast.LENGTH_SHORT).show();
        }
        mTvDone.setEnabled(true);
        mTvDone.setClickable(true);
        mCurrentState = PlayState.STATE_NONE;
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

    public VideoProgressController getVideoProgressViewController() {
        return mVideoProgressController;
    }


    /*********************************************监听电话状态**************************************************/
    static class TXPhoneStateListener extends PhoneStateListener {
        WeakReference<TCVideoEffectActivity> mEditer;

        public TXPhoneStateListener(TCVideoEffectActivity editer) {
            mEditer = new WeakReference<TCVideoEffectActivity>(editer);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            TCVideoEffectActivity activity = mEditer.get();
            if (activity == null) return;
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:  //电话等待接听
                case TelephonyManager.CALL_STATE_OFFHOOK:  //电话接听
                    // 生成状态 取消生成
                    if (activity.mCurrentState == PlayState.STATE_GENERATE) {
                        activity.stopGenerate();
                    }
                    // 直接停止播放
                    activity.stopPlay();
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    // 重新开始播放
                    activity.restartPlay();
                    break;
            }
        }
    }
}
