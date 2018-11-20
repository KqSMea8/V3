package com.huanglong.v3.activities.ksong;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;

import com.czt.mp3recorder.MP3Recorder;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.utils.CaoZuoMp3Utils;
import com.huanglong.v3.utils.ToastUtils;
import com.shuyu.waveview.AudioPlayer;
import com.shuyu.waveview.AudioWaveView;
import com.shuyu.waveview.FileUtils;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * Created by bin on 2018/ 4/29.
 * k歌录制
 */
@ContentView(R.layout.activity_k_song_record)
public class KSongRecordActivity extends BaseActivity {

    @ViewInject(R.id.record_audioWave)
    private AudioWaveView audioWave;
    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.record_status)
    private TextView tv_record_status;
    @ViewInject(R.id.record_time)
    private TextView tv_time;
    @ViewInject(R.id.record_timer)
    private Chronometer record_timer;


    private MP3Recorder mRecorder;
//    private AudioPlayer audioPlayer;

    private String filePath;

//    private WavePopWindow wavePopWindow;

    boolean mIsRecord = false;

    boolean mIsPlay = false;

    //    int duration = 0;
    int curPosition;

    private long recordingTime = 0;// 记录下来的总时间


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AudioPlayer.HANDLER_CUR_TIME://更新的时间
                    curPosition = (int) msg.obj;
//                    tv_time.setText(toTime(curPosition));
                    break;
                case AudioPlayer.HANDLER_COMPLETE://播放结束
//                    playText.setText(" ");
                    mIsPlay = false;
                    break;
                case AudioPlayer.HANDLER_PREPARED://播放开始
//                    duration = (int) msg.obj;
//                    playText.setText(toTime(curPosition) + " / " + toTime(duration));
                    break;
                case AudioPlayer.HANDLER_ERROR://播放错误
                    resolveResetPlay();
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
        tv_title.setText("录制");
//        audioWave.setWaveCount(10);

    }

    @Override
    protected void logic() {
//        resolveNormalUI();
//        audioPlayer = new AudioPlayer(getActivity(), handler);

//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                duration++;
//                tv_time.setText(toTime(duration));
//            }
//        }, 1000);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mIsRecord) {
            resolveStopRecord();
        }
//        if (mIsPlay) {
//            audioPlayer.pause();
//            audioPlayer.stop();
//        }
//        if (wavePopWindow != null) {
//            wavePopWindow.onPause();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (wavePopWindow != null) {
//            wavePopWindow.dismiss();
//            wavePopWindow = null;
//        }

    }

    @Event(value = {R.id.title_back, R.id.record_btn, R.id.record_merge})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.record_btn:
                if (mRecorder == null) {
                    startRecord();
                } else {
                    resolvePause();
                }
                break;
            case R.id.record_merge:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String s = CaoZuoMp3Utils.heBingMp3(filePath, "/storage/emulated/0/Android/data/com.hbb.v3/cache/fuxishaonv.mp3", "fuxishaonv");
                            if (!TextUtils.isEmpty(s)) {
                                LogUtil.e("---合并成功path:" + s);
                            }
                        } catch (IOException e) {
                            LogUtil.e("---合并异常:" + e.toString());
                        }

                    }
                }).start();

                break;


//            case R.id.record:
//                resolveRecord();
//                break;
//            case R.id.stop:
//                resolveStopRecord();
//                break;
//            case R.id.play:
//                resolvePlayRecord();
//                break;
//            case R.id.reset:
//                resolveResetPlay();
//            case R.id.wavePlay:
//                resolvePlayWaveRecord();
//            case R.id.recordPause:
//                resolvePause();
//                break;
//            case R.id.popWindow:
////                View viewGroup = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main, null);
////                wavePopWindow = new WavePopWindow(viewGroup, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
////                wavePopWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
//                break;
        }
    }


    /**
     * 开始录音
     */
    private void startRecord() {
        filePath = FileUtils.getAppPath();
        File file = new File(filePath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                ToastUtils.showToast("创建文件失败");
                return;
            }
        }

        int offset = dip2px(KSongRecordActivity.this, 1);
        filePath = FileUtils.getAppPath() + UUID.randomUUID().toString() + ".mp3";
        mRecorder = new MP3Recorder(new File(filePath));
        int size = getScreenWidth(getActivity()) / offset;//控件默认的间隔是1
        mRecorder.setDataList(audioWave.getRecList(), size);

//        mRecorder.setWaveSpeed(100);
//        // 高级用法
//        int size1 = (getScreenWidth(getActivity()) / 2) / dip2px(getActivity(), 1);
//        mRecorder.setWaveSpeed(100);
//        mRecorder.setDataList(audioWave.getRecList(), size1);
//        audioWave.setDrawStartOffset((getScreenWidth(getActivity()) / 2));
//        audioWave.setDrawReverse(true);
//
//        // 自定义paint
//        Paint paint = new Paint();
////        paint.setColor(Color.GRAY);
//        paint.setStrokeWidth(10);
//        audioWave.setLinePaint(paint);
//        audioWave.setOffset(offset);
//
//        mRecorder.setErrorHandler(new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (msg.what == MP3Recorder.ERROR_TYPE) {
//                    ToastUtils.showToast("没有麦克风权限");
//                    resolveError();
//                }
//            }
//        });

        //audioWave.setBaseRecorder(mRecorder);

        try {
            record_timer.setBase(SystemClock.elapsedRealtime());//计时器清零
            record_timer.start();
            mRecorder.start();
            audioWave.startView();
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtils.showToast("录音出现异常");
            resolveError();
            return;
        }
//        resolveRecordUI();
        mIsRecord = true;
    }

    /**
     * 停止录音
     */
    private void resolveStopRecord() {
//        resolveStopUI();
        if (mRecorder != null && mRecorder.isRecording()) {
            mRecorder.setPause(false);
            mRecorder.stop();
            audioWave.stopView();
        }
        recordingTime = 0;
        record_timer.stop();
        mIsRecord = false;
        tv_record_status.setText("停止录音");
    }

    /**
     * 录音异常
     */
    private void resolveError() {
//        resolveNormalUI();
        FileUtils.deleteFile(filePath);
        filePath = "";
        if (mRecorder != null && mRecorder.isRecording()) {
            mRecorder.stop();
            audioWave.stopView();
        }
    }

    /**
     * 播放
     */
    private void resolvePlayRecord() {
        if (TextUtils.isEmpty(filePath) || !new File(filePath).exists()) {
            ToastUtils.showToast("文件不存在");
            return;
        }
        tv_time.setText("00:00");
        mIsPlay = true;
//        audioPlayer.playUrl(filePath);
//        resolvePlayUI();
    }

    /**
     * 播放
     */
    private void resolvePlayWaveRecord() {
        if (TextUtils.isEmpty(filePath) || !new File(filePath).exists()) {
            ToastUtils.showToast("文件不存在");
            return;
        }
//        resolvePlayUI();
//        Intent intent = new Intent(getActivity(), WavePlayActivity.class);
//        intent.putExtra("uri", filePath);
//        startActivity(intent);
    }

    /**
     * 重置
     */
    private void resolveResetPlay() {
        filePath = "";
        tv_time.setText("00:00");
        if (mIsPlay) {
            mIsPlay = false;
//            audioPlayer.pause();
        }
//        resolveNormalUI();
    }

    /**
     * 暂停
     */
    private void resolvePause() {
        if (!mIsRecord)
            return;
//        resolvePauseUI();
        if (mRecorder.isPause()) {
//            resolveRecordUI();
            audioWave.setPause(false);
            mRecorder.setPause(false);
            record_timer.setBase(SystemClock.elapsedRealtime() - recordingTime);// 跳过已经记录了的时间，起到继续计时的作用
            tv_record_status.setText("暂停录音");
        } else {
            record_timer.stop();
            recordingTime = SystemClock.elapsedRealtime() - record_timer.getBase();// 保存这次记录了的时间
            audioWave.setPause(true);
            mRecorder.setPause(true);
            tv_record_status.setText("继续录音");
        }
    }

    private String toTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String dateString = formatter.format(time);
        return dateString;
    }
//
//    private void resolveNormalUI() {
//        record.setEnabled(true);
//        recordPause.setEnabled(false);
//        stop.setEnabled(false);
//        play.setEnabled(false);
//        wavePlay.setEnabled(false);
//        reset.setEnabled(false);
//    }
//
//    private void resolveRecordUI() {
//        record.setEnabled(false);
//        recordPause.setEnabled(true);
//        stop.setEnabled(true);
//        play.setEnabled(false);
//        wavePlay.setEnabled(false);
//        reset.setEnabled(false);
//    }
//
//    private void resolveStopUI() {
//        record.setEnabled(true);
//        stop.setEnabled(false);
//        recordPause.setEnabled(false);
//        play.setEnabled(true);
//        wavePlay.setEnabled(true);
//        reset.setEnabled(true);
//    }
//
//    private void resolvePlayUI() {
//        record.setEnabled(false);
//        stop.setEnabled(false);
//        recordPause.setEnabled(false);
//        play.setEnabled(true);
//        wavePlay.setEnabled(true);
//        reset.setEnabled(true);
//    }
//
//    private void resolvePauseUI() {
//        record.setEnabled(false);
//        recordPause.setEnabled(true);
//        stop.setEnabled(false);
//        play.setEnabled(false);
//        wavePlay.setEnabled(false);
//        reset.setEnabled(false);
//    }


    /**
     * 获取屏幕的宽度px
     *
     * @param context 上下文
     * @return 屏幕宽px
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕的高度px
     *
     * @param context 上下文
     * @return 屏幕高px
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
        return outMetrics.heightPixels;
    }

    /**
     * dip转为PX
     */
    public static int dip2px(Context context, float dipValue) {
        float fontScale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * fontScale + 0.5f);
    }

}
