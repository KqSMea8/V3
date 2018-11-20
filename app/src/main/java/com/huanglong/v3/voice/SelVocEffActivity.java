package com.huanglong.v3.voice;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.DateUtils;
import com.huanglong.v3.utils.FileUtils;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.voice.adapter.MatterVocAdapter;
import com.huanglong.v3.voice.entity.MatterVocBean;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/5/2.
 * 有声书 选择素材
 */
@ContentView(R.layout.activity_sel_voc_eff)
public class SelVocEffActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.sel_voc_eff_list)
    private RecyclerView eff_list;

    private String keyword = "";
    private List<MatterVocBean> matterVocBeans;

    private MatterVocAdapter matterVocAdapter;

    private MediaPlayer mediaPlayer;
    private String playUrl = "";

    private String bgmName;

    private String filerPath = FileUtils.appPath + "/" + "voice";

    private String voicePath;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("选择素材");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        eff_list.setLayoutManager(layoutManager);
        matterVocAdapter = new MatterVocAdapter();
        eff_list.setAdapter(matterVocAdapter);

        FileUtils.makeDirs(filerPath);

    }

    @Override
    protected void logic() {

        requestSuCaiList();


        matterVocAdapter.setOnItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                //type 1.播放音乐，2.下载音乐
                MatterVocBean matterVocBean = (MatterVocBean) obj;
                bgmName = matterVocBean.getIntroduce();
                playUrl = matterVocBean.getPlay_url();
                if (type == 1) {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        } else {
                            playSound();
                        }
                    }
                } else if (type == 2) {
                    if (!TextUtils.isEmpty(playUrl)) {
                        String fileName = playUrl.substring(playUrl.lastIndexOf("/") + 1, playUrl.length());
                        voicePath = FileUtils.getFilePath(filerPath, fileName);
                        boolean fileExists = FileUtils.isFileExists(voicePath);
                        if (fileExists) {
                            backToEditActivity(position, voicePath, bgmName);
                        } else {
                            downloadMP3();
                        }
                    }

                }

            }

            @Override
            public void onItemViewClick(Object obj, int position, int type, View view) {

            }
        });

    }

    @Event(value = {R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }


    /**
     * 播放声音
     */
    private void playSound() {
        if (TextUtils.isEmpty(playUrl)) {
            ToastUtils.showToast("无效的播放地址");
            return;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });


        try {
            // 设置指定的流媒体地址
            mediaPlayer.setDataSource(playUrl);
            // 设置音频流的类型
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 通过异步的方式装载媒体资源
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    int duration = mp.getDuration();
                    String total_time = DateUtils.timeParse(duration);
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtils.showToast("播放失败");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }


    @Override
    protected void onDestroy() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }


    /**
     * 请求素材列表
     */
    private void requestSuCaiList() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.book_song_list);
        params.addBodyParameter("keyword", keyword);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    matterVocBeans = gson.fromJson(json, new TypeToken<LinkedList<MatterVocBean>>() {
                    }.getType());
                    matterVocAdapter.setData(matterVocBeans);
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
     * 下载素材
     */
    private void downloadMP3() {
        showDialog();
        RequestParams requestParams = new RequestParams(playUrl);
        requestParams.setSaveFilePath(voicePath);

        x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }

            @Override
            public void onSuccess(File result) {
                backToEditActivity(0, result.getAbsolutePath(), bgmName);
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
     * 返回前一页
     *
     * @param position
     * @param path
     */
    private void backToEditActivity(int position, String path, String name) {
        Intent intent = new Intent();
        intent.putExtra(TCConstants.BGM_POSITION, position);
        intent.putExtra(TCConstants.BGM_NAME, name);
        intent.putExtra(TCConstants.BGM_PATH, path);
        setResult(TCConstants.ACTIVITY_BGM_REQUEST_CODE, intent);
        finish();
    }

}
