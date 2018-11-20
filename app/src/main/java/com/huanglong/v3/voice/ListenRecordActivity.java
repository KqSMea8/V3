package com.huanglong.v3.voice;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.voice.custom.AppManager;
import com.huanglong.v3.voice.custom.CustomTextView;
import com.huanglong.v3.voice.custom.LoadingDialog;
import com.huanglong.v3.voice.library.inter.ScrollViewListener;
import com.huanglong.v3.voice.library.utils.DateUtils;
import com.huanglong.v3.voice.library.utils.DensityUtil;
import com.huanglong.v3.voice.library.utils.FileUtils;
import com.huanglong.v3.voice.library.utils.SamplePlayer;
import com.huanglong.v3.voice.library.utils.SoundFiles;
import com.huanglong.v3.voice.library.view.ObservableScrollView;
import com.huanglong.v3.voice.library.view.WaveSurfaceView;
import com.huanglong.v3.voice.library.view.WaveformView_2;
import com.huanglong.v3.voice.utils.OtherUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


@ContentView(R.layout.activity_listen_record)
public class ListenRecordActivity extends BaseActivity implements ScrollViewListener {


    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.recording)
    private CustomTextView mRecording;
    @ViewInject(R.id.listen_cut)
    private CustomTextView mListenCut;
    @ViewInject(R.id.scrollview)
    private ObservableScrollView mScrollView;
    @ViewInject(R.id.content_layout)
    private LinearLayout mContentLayout;
    @ViewInject(R.id.time_layout)
    private LinearLayout mTimeLayout;
    @ViewInject(R.id.wavesfv)
    private WaveSurfaceView mWaveSfv;
    @ViewInject(R.id.waveview)
    private WaveformView_2 mWaveView;

    private int mCurrentPosition = 0;
    private int isListen = 0;
    private int mTotalLength;
    private int mTotalTime;
    private int mTimeCounter = -1;
    private int width;
    private int height;
    private String mFileName;
    private boolean isPlayed = false;
    private boolean mLoadingKeepGoing;
    private float mDensity;
    private File mFile;

    private LoadingDialog mLoadingDialog;
    private OtherUtils mOtherUtils;
    private MediaPlayer mMediaPlayer;
    private SoundFiles mSoundFiles;
    private SamplePlayer mSamplePlayer;
    private Thread mLoadSoundFileThread;

    private Handler myHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        mScrollView.scrollTo((mTotalLength * mMediaPlayer.getCurrentPosition()) / mMediaPlayer.getDuration(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    mTotalTime = mWaveView.pixelsToMillisecsTotal() / 1000;
                    TimeSize();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        AppManager.getAppManager().addActivity(ListenRecordActivity.this);
        mLoadingDialog = LoadingDialog.getInstance(this);
        mOtherUtils = OtherUtils.getInstance(this);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        Intent intent = getIntent();
        if (intent != null) {
            isListen = intent.getIntExtra("isListen", 0);
            if (isListen == 1) {
                mListenCut.setVisibility(View.GONE);
//                mListenSave.setVisibility(View.GONE);
            }
            mFileName = intent.getData().toString();
            initControl();
        } else {
            mOtherUtils.showStringToast("数据异常，请重试");
        }
    }

    @Override
    protected void logic() {

    }

    public void initControl() {
        tv_title.setText("试听");
        mContentLayout.setPadding(width / 2 - DensityUtil.dip2px(0), 0, width / 2 - DensityUtil.dip2px(0), 0);
        FileUtils.createDirectory();
        if (mWaveSfv != null) {
            //解决surfaceView黑色闪动效果
            mWaveSfv.setLine_off(42);
            //解决surfaceView黑色闪动效果
            mWaveSfv.setZOrderOnTop(true);
            mWaveSfv.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        }
        mWaveView.setLine_offset(42);
        TimeSize();
        LoadFromFile();
        TimerCounter.start();
    }

    /**
     * 开始播放音频文件
     */
    protected void StartPlay() {
        final Resources res = ListenRecordActivity.this.getResources();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mRecording.setCompoundDrawablesWithIntrinsicBounds(null, res.getDrawable(R.mipmap.icon_record_play), null, null);
            mRecording.setText("播放");
            mMediaPlayer.pause();
            mTimeCounter = -1;
        } else {
            mRecording.setCompoundDrawablesWithIntrinsicBounds(null, res.getDrawable(R.mipmap.icon_suspend), null, null);
            mRecording.setText("暂停");
            if (mMediaPlayer == null) {
                mScrollView.scrollTo(0, 0);
                try {
                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.setDataSource(mFile.getAbsolutePath());
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                    mTotalTime = mMediaPlayer.getDuration();
                    mTimeCounter = 0;
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public synchronized void onCompletion(MediaPlayer arg0) {
                            mRecording.setCompoundDrawablesWithIntrinsicBounds(null, res.getDrawable(R.mipmap.icon_record_play), null, null);
                            mRecording.setText("播放");
                            mScrollView.scrollTo(mTotalLength, 0);
                            mTimeCounter = -1;
                            mMediaPlayer = null;
                        }
                    });
                } catch (IOException e) {
                    Toast.makeText(this, "文件播放出错！", Toast.LENGTH_SHORT).show();
                }
            } else {
                mTimeCounter = 0;
                mMediaPlayer.start();
            }
        }
    }

    /**
     * 载入wav文件显示波形
     */
    private void LoadFromFile() {
        try {
            Thread.sleep(300);//让文件写入完成后再载入波形 适当的休眠下
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mFile = new File(mFileName);
        mLoadingKeepGoing = true;
        mLoadSoundFileThread = new Thread() {
            public void run() {
                try {
                    mSoundFiles = SoundFiles.create(mFile.getAbsolutePath(), null);
                    if (mSoundFiles == null) {
                        return;
                    }
                    mSamplePlayer = new SamplePlayer(mSoundFiles);
                } catch (final Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (mLoadingKeepGoing) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            finishOpeningSoundFile1();
                            mWaveSfv.setVisibility(View.INVISIBLE);
                            mWaveView.setVisibility(View.VISIBLE);
                        }
                    };
                    ListenRecordActivity.this.runOnUiThread(runnable);
                }
            }
        };
        mLoadSoundFileThread.start();
    }

    /**
     * waveview载入波形完成
     */
    private void finishOpeningSoundFile1() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;
        mWaveView.setSoundFile(mSoundFiles);
        mWaveView.recomputeHeights(mDensity);
        myHandler.sendEmptyMessage(2);
    }

    /**
     * 音频的时间刻度
     */
    private void TimeSize() {
        mTimeLayout.removeAllViews();
        mTotalLength = mTotalTime * DensityUtil.dip2px(60);
        mContentLayout.setLayoutParams(new FrameLayout.LayoutParams(mTotalTime * DensityUtil.dip2px(60), LinearLayout.LayoutParams.MATCH_PARENT));
        for (int i = 0; i < mTotalTime; i++) {
            LinearLayout line1 = new LinearLayout(this);
            line1.setOrientation(LinearLayout.HORIZONTAL);
            line1.setLayoutParams(new LinearLayout.LayoutParams(DensityUtil.dip2px(60), LinearLayout.LayoutParams.WRAP_CONTENT));
            line1.setGravity(Gravity.BOTTOM);
            line1.setBackgroundResource(R.mipmap.icon_listen_scale);

            TextView timeText = new TextView(this);
            timeText.setText(DateUtils.formatTime(i));
            timeText.setWidth(DensityUtil.dip2px(60) - 2);
            timeText.setTextSize(10);
            timeText.setPadding(10, 8, 0, 0);
            TextPaint paint = timeText.getPaint();
            paint.setFakeBoldText(false); //字体加粗设置
            timeText.setTextColor(Color.parseColor("#FFFFFF"));
            line1.addView(timeText);
            mTimeLayout.addView(line1);
        }
    }

    private Timer mTimerSpeed;
    private Thread TimerCounter = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                TimerTask timerTask_speed = new TimerTask() {
                    @Override
                    public void run() {
                        if (mTimeCounter != -1) {
                            mTimeCounter = mTimeCounter + 1;
                            myHandler.sendEmptyMessage(1);
                        }
                    }
                };
                if (mTimerSpeed == null) {
                    mTimerSpeed = new Timer();
                }
                mTimerSpeed.schedule(timerTask_speed, 0, 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy, boolean s) {
        mCurrentPosition = x;
        mScrollView.scrollTo(x, 0);
    }

    @Event(value = {R.id.title_back, R.id.listen_cut, R.id.recording})
    private void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.listen_cut:
                intent.setData(Uri.parse(mFile.getAbsolutePath()));
                intent.setClass(ListenRecordActivity.this, ListenCutActivity.class);
                startActivity(intent);
                finish();
                break;
//            case R.id.listen_save:
                /* intent.setData(Uri.parse(mFile.getAbsolutePath()));
                if (isPlayed) {
                    intent.putExtra("TimeLength", mTotalTime / 1000);
                } else {
                    intent.putExtra("TimeLength", mTotalTime);
                }
                intent.setClass(ListenRecordActivity.this, RecordSaveActivity.class);
                startActivity(intent); */
//                mOtherUtils.showStringToast("跳转到保存文件页面：" + mFile.getAbsolutePath());
//                break;
            case R.id.recording:
                isPlayed = true;
                StartPlay();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            Resources res = ListenRecordActivity.this.getResources();
            mRecording.setCompoundDrawablesWithIntrinsicBounds(null, res.getDrawable(R.mipmap.icon_record_play), null, null);
            mRecording.setText("播放");
            mMediaPlayer.pause();
        }
    }

    /**
     * activity销毁之前需先销毁播放器
     */
    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = null;
        super.onDestroy();
    }

}
