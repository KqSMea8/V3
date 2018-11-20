package com.huanglong.v3.voice;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.activities.login.LoginActivity;
import com.huanglong.v3.activities.login.WelcomeActivity;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.utils.DeviceUtils;
import com.huanglong.v3.utils.FileUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.voice.custom.AppManager;
import com.huanglong.v3.voice.custom.CustomDialog;
import com.huanglong.v3.voice.custom.LoadingDialog;
import com.huanglong.v3.voice.custom.SwitchButton;
import com.huanglong.v3.voice.library.utils.DateUtils;
import com.huanglong.v3.voice.library.utils.DensityUtil;
import com.huanglong.v3.voice.library.utils.Pcm2Wav;
import com.huanglong.v3.voice.library.view.WaveCanvas;
import com.huanglong.v3.voice.library.view.WaveSurfaceView;
import com.huanglong.v3.voice.library.view.WaveformView;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@ContentView(R.layout.activity_sound_recording)
public class SoundRecordActivity extends BaseActivity {

    private boolean isRecord = false;
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    private static final int FREQUENCY = 16000;
    // 设置单声道声道
    private static final int CHANNELCONGIFIGURATION = AudioFormat.CHANNEL_IN_MONO;
    // 音频数据格式：每个样本16位
    private static final int AUDIOENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // 音频获取源
    public final static int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    //
//    @ViewInject(R.id.project_line)
//    View mProjectLine;
//    @ViewInject(R.id.imgmessage)
//    ImageView imgMessage;
//    @ViewInject(R.id.imgback)
//    ImageView imgBack;
    @ViewInject(R.id.status)
    private ImageView mStatus;
    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.time)
    private TextView timeCounter;
    @ViewInject(R.id.tip)
    private TextView mTip;
    @ViewInject(R.id.recording)
    private TextView mRecording;
    @ViewInject(R.id.music_layout)
    private RelativeLayout mMusicLayout;
    @ViewInject(R.id.soundtrack_layout)
    private RelativeLayout mSoundtrackLayout;
    @ViewInject(R.id.menu_layout)
    private LinearLayout mMenuLayout;
    @ViewInject(R.id.wavesfv)
    private WaveSurfaceView waveSfv;
    @ViewInject(R.id.waveview)
    private WaveformView waveView;
    @ViewInject(R.id.soundtrack)
    private SwitchButton mSoundtrack;
    @ViewInject(R.id.soundtrack_list)
    private ListView mSoundtrackList;
    @ViewInject(R.id.album_bar)
    private SeekBar mAlbumBar;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;

    private int progress = 7;
    private int mTotalTime = 0;
    private int mTimeCounter = -1;
    // 默认没在录制状态：1、录制状态 2、为暂停装填 3、为录制结束状态
    private int currentStatus = 0;
    private int swidth;
    private int recBufSize;
    // 文件名
    private String mFileName = "";
    private boolean isEdit = false;
    private boolean isBiggest = false;
    private boolean isSuspend = false;
    private boolean isMusic = false;

    private LoadingDialog mLoadingDialog;
    //    private OtherUtils mOtherUtils;
    private AudioManager mAudioManager;
    private AudioRecord mAudioRecord;
    private WaveCanvas mWaveCanvas;
    private MediaPlayer mMediaPlayer;

    private String MAX_DURATION = "01:30:00";

    private String filerPath = FileUtils.appPath + "/voice";


    private Handler mHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 1://时间记录
                    if (mTimeCounter == -1) {
                        timeCounter.setText("00:00:00");
                    } else {
                        if (DateUtils.formatSecond(mTimeCounter / 1000).equals(MAX_DURATION)) {
                            isBiggest = true;
                            currentStatus = 2;
                            mTotalTime = mTimeCounter;
                            mStatus.setImageResource(R.mipmap.icon_rec);
                            mRecording.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_record_play), null, null);
                            mRecording.setText("录制暂停");
                            ToastUtils.showToast("已达到最大录制时间");
                            timeCounter.setText(DateUtils.formatSecond(mTimeCounter / 1000));
                            if (mWaveCanvas != null) {
                                mWaveCanvas.Stop();
                                mWaveCanvas.clearMarkPosition();
                                mWaveCanvas = null;
                            }
                        } else {
                            timeCounter.setText(DateUtils.formatSecond(mTimeCounter / 1000));
                        }
                    }
                    break;
                case 2:
                    if (isEdit) {
                        timeCounter.setText(DateUtils.formatSecond(mTotalTime / 1000));
                        isEdit = false;
                    }
                    break;
                case 3:
                    timeCounter.setText(DateUtils.formatSecond(mTotalTime / 1000));
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
        tv_right.setText("发布");
        mFileName = "录音" + DeviceUtils.getVersionCode(this) + UserInfoUtils.getRecordCode() + "_syn";

        AppManager.getAppManager().addActivity(SoundRecordActivity.this);

        mLoadingDialog = LoadingDialog.getInstance(this);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        swidth = metrics.widthPixels;
        timerCounter.start();
        initControl();
        //解决surfaceView黑色闪动效果
        if (waveSfv != null) {
//            waveSfv.setLine_off(0);
            waveSfv.setZOrderOnTop(true);
            waveSfv.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        }
//        waveView.setLine_offset(0);
    }

    @Override
    protected void logic() {

        PermissionsUtil.requestPermission(SoundRecordActivity.this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
            }
        }, Manifest.permission.RECORD_AUDIO);

        FileUtils.makeDirs2(filerPath);

        mMediaPlayer = new MediaPlayer();
    }

    public void initControl() {
        tv_title.setText("录音");
        mSoundtrack.setOpenSwitchColor(getResources().getColor(R.color.topic_color));
        mSoundtrack.setOnStateChangedListener(new SwitchButton.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                if (mMediaPlayer != null) {
                    mMediaPlayer.start();
                }
            }

            @Override
            public void toggleToOff() {
                if (mMediaPlayer != null) {
                    mMediaPlayer.pause();
                }
            }
        });
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAlbumBar.setMax(mAudioManager.getStreamMaxVolume(mAudioManager.STREAM_MUSIC));
//        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_SHOW_UI);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_ALLOW_RINGER_MODES);
        mAlbumBar.setProgress(progress);
        mAlbumBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SoundRecordActivity.this.progress = progress;
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_ALLOW_RINGER_MODES);
            }
        });
    }

    @Event(value = {R.id.title_back, R.id.add_music_layout, R.id.recording, R.id.listen, R.id.reset, R.id.cut, R.id.change, R.id.title_tv_right})
    private void onClick(View view) {
        final Resources res = SoundRecordActivity.this.getResources();
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.title_back:
                if (isMusic) {
                    isMusic = false;
                    waveSfv.setVisibility(View.VISIBLE);
                    mSoundtrackLayout.setVisibility(View.GONE);
                    if (mWaveCanvas != null) {
                        tv_title.setText("正在录制");
                    } else {
                        tv_title.setText("录音");
                    }
                } else {
                    CleanWave();
                    waveSfv.setVisibility(View.GONE);
                    waveView.setVisibility(View.INVISIBLE);
                    finish();
                }
                break;
            case R.id.add_music_layout:
            case R.id.change:

                Intent intent1 = new Intent();
                intent1.setClass(this, SelVocEffActivity.class);
                startActivityForResult(intent1, 1000);

//                if (mWaveCanvas != null) {
//                    currentStatus = 2;
//                    mTotalTime = mTimeCounter;
//                    mWaveCanvas.pause();
//                    mStatus.setImageResource(R.mipmap.icon_rec);
//                    mRecording.setCompoundDrawablesWithIntrinsicBounds(null, res.getDrawable(R.mipmap.icon_record_play), null, null);
//                    mRecording.setText("录制暂停");
//                    if (mMediaPlayer != null) {
//                        mMediaPlayer.pause();
//                    }
//                }
//                tv_title.setText("选择配音");
//                isMusic = true;
//                waveSfv.setVisibility(View.GONE);
//                mSoundtrackLayout.setVisibility(View.VISIBLE);
//                SoundtrackData();
                break;
            case R.id.recording:
                if (!isBiggest) {
                    if (mWaveCanvas == null || !mWaveCanvas.isRecording) {
                        currentStatus = 1;
                        mTimeCounter = 0;
                        mMenuLayout.setVisibility(View.VISIBLE);
                        mRecording.setText("麦克风正在录制中");
                        mRecording.setCompoundDrawablesWithIntrinsicBounds(null, res.getDrawable(R.mipmap.icon_suspend), null, null);
                        tv_title.setText("正在录制");
                        mStatus.setImageResource(R.mipmap.icon_rec_pre);
                        waveSfv.setVisibility(View.VISIBLE);
                        waveView.setVisibility(View.INVISIBLE);
                        tv_title.setTextColor(getResources().getColor(R.color.white));
                        if (mMediaPlayer != null) {
                            mMediaPlayer.start();
                        }
                        initAudio();
                    } else {
                        switch (currentStatus) {
                            case 1:
                                currentStatus = 2;
                                mTotalTime = mTimeCounter;
                                mWaveCanvas.pause();
                                mStatus.setImageResource(R.mipmap.icon_rec);
                                mRecording.setCompoundDrawablesWithIntrinsicBounds(null, res.getDrawable(R.mipmap.icon_record_play), null, null);
                                mRecording.setText("录制暂停");
                                if (mMediaPlayer != null) {
                                    mMediaPlayer.pause();
                                }
                                break;
                            case 2:
                                isSuspend = false;
                                currentStatus = 1;
                                mTimeCounter = mTotalTime;
                                mWaveCanvas.reStart();
                                if (!waveSfv.isShown()) {
                                    waveSfv.setVisibility(View.VISIBLE);
                                    waveView.setVisibility(View.INVISIBLE);
                                }
                                mStatus.setImageResource(R.mipmap.icon_rec_pre);
                                mRecording.setCompoundDrawablesWithIntrinsicBounds(null, res.getDrawable(R.mipmap.icon_suspend), null, null);
                                mRecording.setText("麦克风正在录制中");
                                if (mMediaPlayer != null) {
                                    mMediaPlayer.start();
                                }
                                break;
                        }
                    }
                } else {
                    ToastUtils.showToast("已达到最大录制时间");
                }
                break;
            case R.id.listen:
                intent.setData(Uri.parse(DealFile(0).getAbsolutePath()));
                intent.setClass(SoundRecordActivity.this, ListenRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.reset:
                CustomDialog mCustomDialog = new CustomDialog(SoundRecordActivity.this, "您确定要重新录制节目吗？") {

                    @Override
                    public void EnsureEvent() {
                        isSuspend = false;
                        isBiggest = false;
                        mTimeCounter = -1;
                        currentStatus = 2;
                        waveSfv.setVisibility(View.INVISIBLE);
                        mStatus.setImageResource(R.mipmap.icon_rec);
                        mRecording.setCompoundDrawablesWithIntrinsicBounds(null, res.getDrawable(R.mipmap.icon_record_play), null, null);
                        mRecording.setText("录制暂停");
                        CleanWave();
                        waveSfv.setVisibility(View.VISIBLE);
                        waveView.setVisibility(View.INVISIBLE);
                        if (mWaveCanvas != null) {
                            mWaveCanvas.Stop();
                            mWaveCanvas.clearMarkPosition();
                            mWaveCanvas = null;
                        }
                        if (mMediaPlayer != null) {
                            mMediaPlayer.stop();
                            mMediaPlayer.seekTo(0);
                        }
                        dismiss();
                    }
                };
                mCustomDialog.setCanceledOnTouchOutside(false);
                mCustomDialog.show();
                break;
            case R.id.cut:
                intent.setData(Uri.parse(DealFile(1).getAbsolutePath()));
                intent.setClass(SoundRecordActivity.this, ListenCutActivity.class);
                startActivity(intent);
                break;
            case R.id.title_tv_right:
                if (mTimeCounter > -1) {
                    Intent intent2 = new Intent();
                    intent2.setClass(this, SoundPublishActivity.class);
                    intent2.putExtra("soundpath", DealFile(2).getAbsolutePath());
                    startActivityForResult(intent2, 1002);
                } else {
                    ToastUtils.showToast("您还未录音，请先录音");
                }

                break;
//            case R.id.save:
//                /* intent.setData(Uri.parse(DealFile(2).getAbsolutePath()));
//                mTotalTime = mTimeCounter;
//                intent.putExtra("TimeLength", mTotalTime / 1000);
//                intent.setClass(SoundRecordActivity.this, RecordSaveActivity.class);
//                startActivity(intent); */
//                mOtherUtils.showStringToast("跳转到保存文件页面：" + DealFile(2).getAbsolutePath());
//                break;
        }
    }

//    public void SoundtrackData() {
//        mLoadingDialog.createLoadingDialog(SoundRecordActivity.this, "加载音乐中");
//        Cursor cursor = null;
//        List<SoundtrackInfo> mediaList = null;
//        try {
//            cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
//                    null, MediaStore.Audio.AudioColumns.IS_MUSIC);
//            if (cursor == null) {
//                mTip.setVisibility(View.VISIBLE);
//                if (mLoadingDialog != null) {
//                    mLoadingDialog.CloseDialog();
//                }
//
//                mOtherUtils.showStringToast("搜索音乐文件完成，未发现音频文件");
//                return;
//            }
//            int count = cursor.getCount();
//            if (count <= 0) {
//                mTip.setVisibility(View.VISIBLE);
//                if (mLoadingDialog != null) {
//                    mLoadingDialog.CloseDialog();
//                }
//                mOtherUtils.showStringToast("搜索音乐文件完成，未发现音频文件");
//                return;
//            }
//            mediaList = new ArrayList<>();
//            SoundtrackInfo mSoundtrackInfo;
//            // String[] columns = cursor.getColumnNames();
//            while (cursor.moveToNext()) {
//                mSoundtrackInfo = new SoundtrackInfo();
//                mSoundtrackInfo.Id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
//                mSoundtrackInfo.Title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
//                mSoundtrackInfo.DisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
//                mSoundtrackInfo.Duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
//                mSoundtrackInfo.Size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
//
//                /* if (!checkIsMusic(mSoundtrackInfo.Duration, mSoundtrackInfo.Size)) {
//                    continue;
//                } */
//                mSoundtrackInfo.Artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
//                mSoundtrackInfo.Path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
//                mediaList.add(mSoundtrackInfo);
//            }
//        } catch (Exception e) {
//            if (mLoadingDialog != null) {
//                mLoadingDialog.CloseDialog();
//            }
//            mOtherUtils.showStringToast("加载音乐文件异常，请重试");
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//        if (mediaList.size() > 0) {
//            mTip.setVisibility(View.GONE);
//            mSoundtrackList.setVisibility(View.VISIBLE);
//            final SoundtrackAdapter mSoundtrackAdapter = new SoundtrackAdapter(SoundRecordActivity.this, mediaList);
//            mSoundtrackList.setAdapter(mSoundtrackAdapter);
//            mSoundtrackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                    isMusic = false;
//                    mSoundtrack.setState(true);
//                    mMusicLayout.setVisibility(View.VISIBLE);
//                    mSoundtrackLayout.setVisibility(View.GONE);
//                    mMediaPlayer = new MediaPlayer();
//                    try {
//                        mMediaPlayer.setDataSource(mSoundtrackAdapter.getSoundtrackInfo(position).Path);
//                        mMediaPlayer.prepare();
//                        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                            @Override
//                            public void onCompletion(MediaPlayer mediaPlayer) {
//                                mMediaPlayer.stop();
//                                mMediaPlayer.release();
//                                mMediaPlayer = null;
//                            }
//                        });
//                    } catch (IOException e) {
//                        mOtherUtils.showStringToast("配音文件异常，请换一个重试");
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } else {
//            mTip.setVisibility(View.VISIBLE);
//            mSoundtrackList.setVisibility(View.GONE);
//        }
//        if (mLoadingDialog != null) {
//            mLoadingDialog.CloseDialog();
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == TCConstants.ACTIVITY_BGM_REQUEST_CODE) {
            switch (requestCode) {
                case 1000:
                    if (data == null) return;
                    String bgm_path = data.getStringExtra(TCConstants.BGM_PATH);
                    mFileName = data.getStringExtra(TCConstants.BGM_NAME) + "_syn";
                    isMusic = false;
                    mSoundtrack.setState(true);
                    mMusicLayout.setVisibility(View.VISIBLE);
                    mSoundtrackLayout.setVisibility(View.GONE);
                    if (mMediaPlayer != null) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    }
                    mMediaPlayer = new MediaPlayer();
                    try {
                        mMediaPlayer.setDataSource(bgm_path);
                        mMediaPlayer.prepare();
                        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                mMediaPlayer.stop();
                                mMediaPlayer.release();
                                mMediaPlayer = null;
                            }
                        });
                    } catch (IOException e) {
                        ToastUtils.showToast("配音文件异常，请换一个重试");
                        e.printStackTrace();
                    }
                    break;
                case 1002:
                    setResult(RESULT_OK);
                    finish();
                    break;
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (isMusic) {
            isMusic = false;
            waveSfv.setVisibility(View.VISIBLE);
            mSoundtrackLayout.setVisibility(View.GONE);
            if (mWaveCanvas != null) {
                tv_title.setText("正在录制");
            } else {
                tv_title.setText("录音");
            }
        } else {
            CleanWave();
            waveSfv.setVisibility(View.GONE);
            waveView.setVisibility(View.INVISIBLE);
            finish();
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
        }, (swidth - DensityUtil.dip2px(10)) / 2, this);
    }

    private Timer TimerSpeed;
    private Thread timerCounter = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                TimerTask timerTask_speed = new TimerTask() {
                    @Override
                    public void run() {
                        if (!isSuspend) {
                            if (mTimeCounter != -1 && currentStatus != 2) {
                                mTimeCounter = mTimeCounter + 100;
                                mHandler.sendEmptyMessage(1);
                                mHandler.sendEmptyMessage(2);
                            }
                        }
                    }
                };
                if (TimerSpeed == null) {
                    TimerSpeed = new Timer();
                }
                TimerSpeed.schedule(timerTask_speed, 0, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (progress == 0) {
                    progress = 0;
                    ToastUtils.showToast("已达到最小音量" + progress);
                    return true;
                }
                --progress;
                mAlbumBar.setProgress(progress);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (progress == 15) {
                    progress = 15;
                    ToastUtils.showToast("已达到最小音量" + progress);
                    return true;
                }
                ++progress;
                mAlbumBar.setProgress(progress);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public File DealFile(int Type) {
        mLoadingDialog.createLoadingDialog(SoundRecordActivity.this);
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

    // 清除时间点
    public void CleanWave() {
        File mFile1 = new File(filerPath + "/" + mFileName + ".wav");
        File mFile2 = new File(filerPath + "/" + mFileName + ".pcm");
        if (mFile1.exists() && mFile2.exists()) {
            mFile1.delete();
            mFile2.delete();
        }
        mHandler.sendEmptyMessage(1);
    }

    @Override
    protected void onPause() {
        if (mWaveCanvas != null) {
            isSuspend = true;
            mWaveCanvas.pause();
            currentStatus = 2;
            mTotalTime = mTimeCounter;
            mStatus.setImageResource(R.mipmap.icon_rec);
            Resources res = SoundRecordActivity.this.getResources();
            mRecording.setCompoundDrawablesWithIntrinsicBounds(null, res.getDrawable(R.mipmap.icon_record_play), null, null);
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWaveCanvas != null) {
            if (mWaveCanvas.isRecording) {
                mWaveCanvas.Stop();
            }
            mWaveCanvas.clear();
            mWaveCanvas = null;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

}
