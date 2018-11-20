package com.huanglong.v3.song;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.activities.homepage.KSongActivity;
import com.huanglong.v3.activities.homepage.PlaySoundActivity;
import com.huanglong.v3.utils.DateUtils;
import com.huanglong.v3.utils.FileUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.view.LrcView;
import com.huanglong.v3.voice.custom.CustomDialog;
import com.huanglong.v3.voice.library.utils.DensityUtil;
import com.huanglong.v3.voice.library.utils.Pcm2Wav;
import com.huanglong.v3.voice.library.view.WaveCanvas;
import com.huanglong.v3.voice.library.view.WaveSurfaceView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.IOException;

/**
 * Created by bin on 2018/5/3.
 * k歌录制
 */
@ContentView(R.layout.activity_song_record)
public class SongRecordActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.song_record_lrc)
    private LrcView lrc_view;
    @ViewInject(R.id.recording)
    private TextView mRecording;
    @ViewInject(R.id.song_record_total_time)
    private TextView tv_total_time;
    @ViewInject(R.id.song_record_current_time)
    private TextView tv_current_time;
    @ViewInject(R.id.song_record_progress)
    private ProgressBar progress;
    @ViewInject(R.id.wavesfv)
    private WaveSurfaceView waveSfv;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;


    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    private static final int FREQUENCY = 16000;
    // 设置单声道声道
    private static final int CHANNELCONGIFIGURATION = AudioFormat.CHANNEL_IN_MONO;
    // 音频数据格式：每个样本16位
    private static final int AUDIOENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // 音频获取源
    public final static int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;


    private String filerPath = FileUtils.appPath + "/song";

    private String MAX_DURATION = "6:00";
    private long startTimeMillis;
    private boolean isRecording = false;


    private String mp3Path;
    private String lrcPath;
    private String mp3Name;
    private String cover;
    private String lrcUrl;
    private String mp3Url;
    private int duration;

    private MediaPlayer mMediaPlayer;

    private String total_time = "00:00";
    private String current_time = "00:00";

    private boolean isBiggest = false;

    // 默认没在录制状态：1、录制状态 2、为暂停装填 3、为录制结束状态
    private int currentStatus = 0;
    private int width;
    private int recBufSize;
    private AudioRecord mAudioRecord;
    private int int_swidth;
    private String mFileName;
    private WaveCanvas mWaveCanvas;
    private boolean isReset = false;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == 1) {
                if (TextUtils.isEmpty(mp3Path)) {
                    mWaveCanvas.pause();
                    mRecording.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_record_play), null, null);
                    String absolutePath = DealFile(1).getAbsolutePath();
                    Intent intent = new Intent();
                    intent.setClass(SongRecordActivity.this, PlaySoundActivity.class);
                    intent.putExtra("flag", 1);
                    intent.putExtra("playUrl", absolutePath);
                    startActivityForResult(intent, 1000);
                } else {
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                        mWaveCanvas.pause();
                        mRecording.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_record_play), null, null);
                    }
                }
            }
        }
    };


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("K歌");
        tv_right.setText("下一步");

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
    }

    @Override
    protected void logic() {

        Intent intent = getIntent();
        mp3Path = intent.getStringExtra("mp3Path");
        lrcPath = intent.getStringExtra("lrcPath");
        mp3Name = intent.getStringExtra("mp3Name");
        lrcUrl = intent.getStringExtra("lrcUrl");
        mp3Url = intent.getStringExtra("mp3Url");
        cover = intent.getStringExtra("cover");
        if (TextUtils.isEmpty(mp3Name)) {
            mFileName = "k_song_" + System.currentTimeMillis() + "_syn";
        } else {
            mFileName = mp3Name + "_syn";
        }
        if (TextUtils.isEmpty(mp3Path)) {
            duration = 6 * 60 * 1000;
            progress.setMax(duration);
            total_time = DateUtils.timeParse(duration);
            MAX_DURATION = total_time;
            tv_total_time.setText(total_time);
        }
        if (!TextUtils.isEmpty(lrcPath)) {
            lrc_view.loadLrc(new File(lrcPath));
        }

//        lrc_view.setOnPlayClickListener(new LrcView.OnPlayClickListener() {
//            @Override
//            public boolean onPlayClick(long time) {
//                mMediaPlayer.seekTo((int) time);
//                if (!mMediaPlayer.isPlaying()) {
//                    mMediaPlayer.start();
//                    handler.post(runnable);
//                }
//                return true;
//            }
//        });
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                long time = mMediaPlayer.getCurrentPosition();
                progress.setProgress((int) time);
                lrc_view.updateTime(time);
                current_time = DateUtils.timeParse((int) time);
                tv_current_time.setText(current_time);
            } else {
                long currentTime = System.currentTimeMillis();
                long time = currentTime - startTimeMillis;
                progress.setProgress((int) time);
                current_time = DateUtils.timeParse((int) time);
                tv_current_time.setText(current_time);
            }
            if (TextUtils.equals(current_time, MAX_DURATION)) {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            } else {
                handler.postDelayed(this, 1000);
            }

        }
    };

    @Event(value = {R.id.title_back, R.id.recording, R.id.title_tv_right, R.id.reset})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                closeActivity();
                break;
            case R.id.recording:
                boolean b = PermissionsUtil.hasPermission(SongRecordActivity.this, Manifest.permission.RECORD_AUDIO);
                if (b) {
                    if (isReset) {
                        startTimeMillis = System.currentTimeMillis();
                    }
                    isReset = false;
                    if (TextUtils.isEmpty(mp3Path)) {
                        if (mWaveCanvas == null) {
                            startTimeMillis = System.currentTimeMillis();
                            mRecording.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_suspend), null, null);
                            handler.post(runnable);
                            initAudio();
                            isRecording = true;
                        } else {
                            if (isRecording) {
                                handler.removeCallbacks(runnable);
                                mWaveCanvas.pause();
                                mRecording.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_record_play), null, null);
                                isRecording = false;
                            } else {
                                handler.post(runnable);
                                mWaveCanvas.reStart();
                                mRecording.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_suspend), null, null);
                                isRecording = true;
                            }
                        }
                    } else {
                        if (mMediaPlayer == null) {
                            playMp3();
                            mRecording.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_suspend), null, null);
                            handler.post(runnable);
                            initAudio();
                        } else {
                            if (mMediaPlayer.isPlaying()) {
                                mRecording.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_record_play), null, null);
                                mMediaPlayer.pause();
                                mWaveCanvas.pause();
                            } else {
                                mWaveCanvas.reStart();
                                mMediaPlayer.start();
                                mRecording.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_suspend), null, null);
                            }
                        }
                    }
                } else {
                    PermissionsUtil.requestPermission(SongRecordActivity.this, new PermissionListener() {
                        @Override
                        public void permissionGranted(@NonNull String[] permission) {

                        }

                        @Override
                        public void permissionDenied(@NonNull String[] permission) {

                        }
                    }, Manifest.permission.RECORD_AUDIO);
                }
                break;
            case R.id.title_tv_right:
                isReset = false;
                if (mMediaPlayer != null) {
                    CustomDialog mCustomDialog = new CustomDialog(SongRecordActivity.this, "是否结束当前录制？") {

                        @Override
                        public void EnsureEvent() {
//                            if (mMediaPlayer.isPlaying()) {
                            mRecording.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_record_play), null, null);
                            mMediaPlayer.stop();
                            mMediaPlayer.seekTo(0);
                            mWaveCanvas.Stop();
//                            }
// else {
//                                String absolutePath = DealFile(2).getAbsolutePath();
//                                Intent intent = new Intent();
//                                intent.setClass(SongRecordActivity.this, PlaySoundActivity.class);
//                                intent.putExtra("flag", 1);
//                                intent.putExtra("album_img", cover);
//                                intent.putExtra("playUrl", absolutePath);
//                                intent.putExtra("lrcUrl", lrcUrl);
//                                startActivityForResult(intent, 1000);
//                            }
                            reduction();
                            dismiss();
                        }
                    };
                    mCustomDialog.setCanceledOnTouchOutside(false);
                    mCustomDialog.show();

//                    reduction();
                } else {
//                    ToastUtils.showToast("您还未录音");
                    CustomDialog mCustomDialog = new CustomDialog(SongRecordActivity.this, "是否结束当前录制？") {

                        @Override
                        public void EnsureEvent() {
                            mRecording.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_record_play), null, null);
                            mWaveCanvas.Stop();
                            reduction();
                            dismiss();
                            String absolutePath = DealFile(1).getAbsolutePath();
                            Intent intent = new Intent();
                            intent.setClass(SongRecordActivity.this, PlaySoundActivity.class);
                            intent.putExtra("flag", 1);
                            intent.putExtra("playUrl", absolutePath);
                            startActivityForResult(intent, 1000);
                        }
                    };
                    mCustomDialog.setCanceledOnTouchOutside(false);
                    mCustomDialog.show();
                }
                break;
            case R.id.reset:

                CustomDialog mCustomDialog = new CustomDialog(SongRecordActivity.this, "您确定要重新录制歌曲吗？") {

                    @Override
                    public void EnsureEvent() {
                        isReset = true;
                        isBiggest = false;
                        currentStatus = 2;
                        waveSfv.setVisibility(View.INVISIBLE);
                        CleanWave();
                        mRecording.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_record_play), null, null);
                        if (mWaveCanvas != null) {
                            mWaveCanvas.Stop();
                            mWaveCanvas.clearMarkPosition();
                            mWaveCanvas = null;
                        }
                        if (mMediaPlayer != null) {
                            mMediaPlayer.stop();
                            mMediaPlayer.seekTo(0);
                        }
                        reduction();
                        dismiss();
                    }
                };
                mCustomDialog.setCanceledOnTouchOutside(false);
                mCustomDialog.show();
                break;
        }
    }

    /**
     * 播放音乐
     */
    private void playMp3() {
        if (TextUtils.isEmpty(mp3Path)) {
            ToastUtils.showToast("无效的播放地址");
            return;
        }
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mp3Path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    isBiggest = true;
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    reduction();
                    if (!isReset) {
                        String absolutePath = DealFile(1).getAbsolutePath();
                        Intent intent = new Intent();
                        intent.setClass(SongRecordActivity.this, PlaySoundActivity.class);
                        intent.putExtra("flag", 1);
                        intent.putExtra("album_img", cover);
                        intent.putExtra("playUrl", absolutePath);
                        intent.putExtra("lrcUrl", lrcUrl);
                        intent.putExtra("mp3Url", mp3Url);
                        startActivityForResult(intent, 1000);
                    }
                }
            });
            duration = mMediaPlayer.getDuration();
            progress.setMax(duration);
            total_time = DateUtils.timeParse(duration);
            MAX_DURATION = total_time;
            tv_total_time.setText(total_time);

        } catch (IOException e) {
            ToastUtils.showToast("配音文件异常，请换一个重试");
            e.printStackTrace();
        }
    }

    /**
     * 还原
     */
    private void reduction() {
        current_time = "00:00";
        progress.setProgress(0);
        if (!TextUtils.isEmpty(mp3Path)) {
            lrc_view.updateTime(0);
        }
        handler.removeCallbacks(runnable);
        tv_current_time.setText(current_time);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mWaveCanvas.pause();
            mRecording.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_suspend), null, null);
        } else {
            if (mWaveCanvas != null) {
                mWaveCanvas.pause();
                mRecording.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_suspend), null, null);
            }
        }
    }

    /**
     * 初始化录音
     */
    private void initAudio() {
        recBufSize = AudioRecord.getMinBufferSize(FREQUENCY,
                CHANNELCONGIFIGURATION, AUDIOENCODING);//设置录音缓冲区(一般为20ms,1280)
        mAudioRecord = new AudioRecord(AUDIO_SOURCE,// 指定音频来源，这里为麦克风
                FREQUENCY, // 16000HZ采样频率
                CHANNELCONGIFIGURATION,// 录制通道
                AUDIO_SOURCE,// 录制编码格式
                recBufSize);
        mWaveCanvas = new WaveCanvas();
        mWaveCanvas.baseLine = waveSfv.getHeight() / 2;
        mWaveCanvas.Start(mAudioRecord, recBufSize, waveSfv, mFileName, filerPath + "/", new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                return true;
            }
        }, (width - DensityUtil.dip2px(10)) / 2, this);
    }


    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
//        CleanWave();
        if (mWaveCanvas != null) {
            mWaveCanvas.Stop();
            mWaveCanvas.clearMarkPosition();
            mWaveCanvas = null;
        }

        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
        super.onDestroy();
    }

    // 清除时间点
    public void CleanWave() {
        File mFile1 = new File(filerPath + "/" + mFileName + ".wav");
        File mFile2 = new File(filerPath + "/" + mFileName + ".pcm");
        if (mFile1.exists() && mFile2.exists()) {
            mFile1.delete();
            mFile2.delete();
        }
    }

    public File DealFile(int Type) {
        mLoadingDialog.createLoadingDialog(SongRecordActivity.this);
        String WavString = null;
        if (Type == 0) {
            WavString = filerPath + "/" + mFileName + ".wav";
        } else if (Type == 1) {
            WavString = filerPath + "/" + mFileName + ".wav";
        } else if (Type == 2) {
            WavString = filerPath + "/" + mFileName + ".wav";
        }
        String PcmString = filerPath + "/" + mFileName + ".pcm";
        if (PcmString.length() > 0) {
            try {
                Pcm2Wav p2w = new Pcm2Wav();
                p2w.convertAudioFiles(PcmString, WavString);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showToast("Pcm转换Wav失败！");
            }
        } else {
            ToastUtils.showToast("你操作的文件不存在！");
        }
        mLoadingDialog.CloseDialog();
        return new File(WavString);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    if (KSongActivity.instance != null) {
                        KSongActivity.instance.onRefresh();
                    }
                    if (SongEffActivity.instance != null) {
                        SongEffActivity.instance.finish();
                    }
                    finish();
                    break;
            }
        }
    }

    /**
     * 关闭activity
     */
    private void closeActivity() {
        String s = tv_current_time.getText().toString();
        if (TextUtils.equals("00:00", s)) {
            finish();
        } else {
            CustomDialog mCustomDialog = new CustomDialog(SongRecordActivity.this, "是否退出录制？") {
                @Override
                public void EnsureEvent() {
                    finish();
                }
            };
            mCustomDialog.setCanceledOnTouchOutside(false);
            mCustomDialog.show();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeActivity();
        }
        return super.onKeyDown(keyCode, event);
    }
}

