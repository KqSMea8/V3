package com.huanglong.v3.smallvideo;

import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huanglong.v3.R;
import com.huanglong.v3.live.TCBaseActivity;
import com.huanglong.v3.live.utils.TCConstants;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.xutils.view.annotation.ContentView;

import java.io.File;
import java.util.Locale;

/**
 * 录制完成后的预览界面
 * Created by carolsuo on 2017/3/21.
 */
@ContentView(R.layout.activity_video_preview)
public class TCVideoPreviewActivity extends TCBaseActivity implements View.OnClickListener, ITXLivePlayListener {
    public static final String TAG = "TCVideoPreviewActivity";

    ImageView mStartPreview;
    boolean mVideoPlay = false;
    boolean mVideoPause = false;
    boolean mAutoPause = false;

    private String mVideoPath;
    private String mCoverImagePath;
    ImageView mImageViewBg;
    private TXLivePlayer mTXLivePlayer = null;
    private TXLivePlayConfig mTXPlayConfig = null;
    private TXCloudVideoView mTXCloudVideoView;
    private SeekBar mSeekBar;
    private TextView mProgressTime;
    private long mTrackingTouchTS = 0;
    private boolean mStartSeek = false;

    public static TCVideoPreviewActivity instance;


    @Override
    protected void initView() {
        instance = this;
        mStartPreview = (ImageView) findViewById(R.id.record_preview);

        mVideoPath = getIntent().getStringExtra(TCConstants.VIDEO_RECORD_VIDEPATH);
        mCoverImagePath = getIntent().getStringExtra(TCConstants.VIDEO_RECORD_COVERPATH);

        mImageViewBg = (ImageView) findViewById(R.id.cover);
        Glide.with(this).load(Uri.fromFile(new File(mCoverImagePath))).into(mImageViewBg);
    }

    @Override
    protected void logic() {
        mTXLivePlayer = new TXLivePlayer(this);
        mTXPlayConfig = new TXLivePlayConfig();
        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mTXCloudVideoView.disableLog(true);

        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean bFromUser) {
                if (mProgressTime != null) {
                    mProgressTime.setText(String.format(Locale.CHINA, "%02d:%02d/%02d:%02d", (progress) / 60, (progress) % 60, (seekBar.getMax()) / 60, (seekBar.getMax()) % 60));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mStartSeek = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mTXLivePlayer != null) {
                    mTXLivePlayer.seek(seekBar.getProgress());
                }
                mTrackingTouchTS = System.currentTimeMillis();
                mStartSeek = false;
            }
        });
        mProgressTime = (TextView) findViewById(R.id.progress_time);
    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
////        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
////        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
////        requestWindowFeature(Window.FEATURE_NO_TITLE);
////
////        setContentView(R.layout.activity_video_preview);
//
//
//
//
//
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_delete:
                deleteVideo();
                break;
            case R.id.record_download:
                downloadRecord();
                break;
            case R.id.record_publish:
                startPublish();
                break;
            case R.id.record_preview:
                if (mVideoPlay) {
                    if (mVideoPause) {
                        mTXLivePlayer.resume();
                        mStartPreview.setBackgroundResource(R.drawable.icon_record_pause);
                        mVideoPause = false;
                    } else {
                        mTXLivePlayer.pause();
                        mStartPreview.setBackgroundResource(R.drawable.icon_record_start);
                        mVideoPause = true;
                    }
                } else {
                    startPlay();
                }
                break;
            default:
                break;
        }

    }

    private boolean startPlay() {
        mStartPreview.setBackgroundResource(R.drawable.icon_record_pause);
        mTXLivePlayer.setPlayerView(mTXCloudVideoView);
        mTXLivePlayer.setPlayListener(this);

        mTXLivePlayer.enableHardwareDecode(false);
        mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);

        mTXLivePlayer.setConfig(mTXPlayConfig);

        int result = mTXLivePlayer.startPlay(mVideoPath, TXLivePlayer.PLAY_TYPE_LOCAL_VIDEO); // result返回值：0 success;  -1 empty url; -2 invalid url; -3 invalid playType;
        if (result != 0) {
            mStartPreview.setBackgroundResource(R.drawable.icon_record_start);
            return false;
        }

        mImageViewBg.setVisibility(View.GONE);
        mVideoPlay = true;
        return true;
    }

    private static ContentValues initCommonContentValues(File saveFile) {
        ContentValues values = new ContentValues();
        long currentTimeInSeconds = System.currentTimeMillis();
        values.put(MediaStore.MediaColumns.TITLE, saveFile.getName());
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, saveFile.getName());
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, currentTimeInSeconds);
        values.put(MediaStore.MediaColumns.DATE_ADDED, currentTimeInSeconds);
        values.put(MediaStore.MediaColumns.DATA, saveFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.SIZE, saveFile.length());
        return values;
    }

    private void downloadRecord() {
        File file = new File(mVideoPath);
        if (file.exists()) {
            try {
                File newFile = new File(Environment.getExternalStorageDirectory() + File.separator + TCConstants.DEFAULT_MEDIA_PACK_FOLDER + File.separator + file.getName());
                if (!newFile.getParentFile().exists()) {
                    newFile.getParentFile().mkdirs();
                }
                file.renameTo(newFile);
                ContentValues values = initCommonContentValues(file);
                values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, System.currentTimeMillis());
                values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
                this.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                updateMediaDataBase(newFile.getAbsolutePath());//更新媒体库
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        finish();
    }


    private void updateMediaDataBase(String filename) {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;// 获得当前sdk版本
        if (currentApiVersion < 19) {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + filename)));
        } else {
            MediaScannerConnection.scanFile(this, new String[]{filename},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        }
    }

    private void deleteVideo() {
        //删除文件
        File file = new File(mVideoPath);
        if (file.exists()) {
            file.delete();

            finish();
        }
    }

    private void startPublish() {
        //TODO 发布
//        Intent intent = new Intent(getApplicationContext(), TCVideoPublisherActivity.class);
//        intent.putExtra(TCConstants.VIDEO_RECORD_TYPE, TCConstants.VIDEO_RECORD_TYPE_PUBLISH);
//        intent.putExtra(TCConstants.VIDEO_RECORD_VIDEPATH, mVideoPath);
//        intent.putExtra(TCConstants.VIDEO_RECORD_COVERPATH, mCoverImagePath);
//        startActivity(intent);
//        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTXCloudVideoView.onResume();
        if (mVideoPlay && mAutoPause) {
            mTXLivePlayer.resume();
            mAutoPause = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTXCloudVideoView.onPause();
        if (mVideoPlay && !mVideoPause) {
            mTXLivePlayer.pause();
            mAutoPause = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
        mTXCloudVideoView.onDestroy();
        stopPlay(true);
    }

    protected void stopPlay(boolean clearLastFrame) {
        if (mTXLivePlayer != null) {
            mTXLivePlayer.setPlayListener(null);
            mTXLivePlayer.stopPlay(clearLastFrame);
            mVideoPlay = false;
        }
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.setLogText(null, param, event);
        }
        if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            if (mStartSeek) {
                return;
            }
            int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
            int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION);
            long curTS = System.currentTimeMillis();
            // 避免滑动进度条松开的瞬间可能出现滑动条瞬间跳到上一个位置
            if (Math.abs(curTS - mTrackingTouchTS) < 500) {
                return;
            }
            mTrackingTouchTS = curTS;

            if (mSeekBar != null) {
                mSeekBar.setProgress(progress);
            }
            if (mProgressTime != null) {
                mProgressTime.setText(String.format(Locale.CHINA, "%02d:%02d/%02d:%02d", (progress) / 60, progress % 60, (duration) / 60, duration % 60));
            }

            if (mSeekBar != null) {
                mSeekBar.setMax(duration);
            }
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {

            showErrorAndQuit(TCConstants.ERROR_MSG_NET_DISCONNECTED);

        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            if (mProgressTime != null) {
                mProgressTime.setText(String.format(Locale.CHINA, "%s", "00:00/00:00"));
            }
            if (mSeekBar != null) {
                mSeekBar.setProgress(0);
            }
            stopPlay(false);
            startPlay();
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }
}
