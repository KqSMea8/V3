package com.huanglong.v3.smallvideo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.homepage.MatterClassAdapter;
import com.huanglong.v3.adapter.homepage.MatterSongAdapter;
import com.huanglong.v3.im.utils.FileUtil;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.model.homepage.MatterClassBean;
import com.huanglong.v3.model.homepage.MatterSongBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.smallvideo.videorecord.TCVideoRecordActivity;
import com.huanglong.v3.utils.DateUtils;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.yhy.gvp.listener.OnItemClickListener;
import com.yhy.gvp.widget.GridViewPager;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/25.
 * 音效界面
 */
@ContentView(R.layout.activity_effect)
public class SelEffectActivity extends BaseActivity {

    @ViewInject(R.id.title_back)
    private LinearLayout back;
    @ViewInject(R.id.title_tv_left)
    private TextView tv_left;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;
    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.effect_class_list)
    private GridViewPager class_list;
    @ViewInject(R.id.effect_hot_list)
    private RecyclerView hot_list;


    private MatterClassAdapter matterClassAdapter;

    private MatterSongAdapter matterSongAdapter;
    private List<MatterSongBean> matterSongBeans;

    //    private MediaPlayer mediaPlayer;
    private String playUrl = "";

    private int lastPosition = -1;

    private int flag;
    private String cacheFilePath;
    private long duration;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        back.setVisibility(View.GONE);
        tv_left.setVisibility(View.GONE);
        tv_title.setText("音效");
        tv_right.setText("直接开拍");


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        hot_list.setLayoutManager(layoutManager);
        matterSongAdapter = new MatterSongAdapter();
        hot_list.setAdapter(matterSongAdapter);
    }

    @Override
    protected void logic() {

        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", 0);
        if (flag != 0) {
            tv_right.setVisibility(View.GONE);
        }
        requestEffectClass();
        requestHotSong();

//        mediaPlayer = new MediaPlayer();
//
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                mediaPlayer.stop();
//                mediaPlayer.release();
//            }
//        });

        matterSongAdapter.setOnItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                MatterSongBean matterSongBean = (MatterSongBean) obj;
                duration = DateUtils.reverseTimeParse(matterSongBean.getDuration());
                if (type == 1) {
                    playUrl = matterSongBean.getPlay_url();
                    if (position == lastPosition) {
                        boolean camera = matterSongBean.isCamera();
//                        if (mediaPlayer != null) {
//                            if (mediaPlayer.isPlaying()) {
//                                mediaPlayer.pause();
//                            } else {
//                                playSound();
//                            }
//                        }
                        if (camera) {
                            matterSongBean.setCamera(false);
                        } else {
                            matterSongBean.setCamera(true);
                        }
                    } else {
                        if (lastPosition != -1) {
                            matterSongBeans.get(lastPosition).setCamera(false);
                        }
                        matterSongBean.setCamera(true);
//                        playSound();
                    }
                    lastPosition = position;
                    matterSongAdapter.notifyDataSetChanged();
                } else if (type == 2) {
                    String play_url = matterSongBean.getPlay_url();
                    if (!TextUtils.isEmpty(play_url)) {
                        String fileName = play_url.substring(play_url.lastIndexOf("/") + 1, play_url.length());
                        cacheFilePath = FileUtil.getCacheFilePath(fileName);
                        boolean cacheFileExist = FileUtil.isCacheFileExist(cacheFilePath);
                        if (cacheFileExist) {
                            if (flag == 1) {
                                Intent intent = new Intent();
                                intent.putExtra(TCConstants.BGM_POSITION, position);
                                intent.putExtra(TCConstants.BGM_PATH, cacheFilePath);
                                setResult(TCConstants.ACTIVITY_BGM_REQUEST_CODE, intent);
                                finish();
                            }
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

    @Event(value = {R.id.effect_cancel, R.id.title_tv_right, R.id.effect_search})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.effect_cancel:
                finish();
                break;
            case R.id.title_tv_right:
                Intent intent = new Intent();
                intent.setClass(this, TCVideoRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.effect_search:
                Intent intent1 = new Intent();
                intent1.setClass(this, MatterSongActivity.class);
                intent1.putExtra("title", "搜索歌曲");
                startActivity(intent1);
                break;
        }
    }

    /**
     * 请求分类
     */
    private void requestEffectClass() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.video_cate_list);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<MatterClassBean> matterClassBeans = gson.fromJson(json, new TypeToken<LinkedList<MatterClassBean>>() {
                    }.getType());
                    if (matterClassBeans != null && matterClassBeans.size() > 0) {
                        matterClassAdapter = new MatterClassAdapter(R.layout.item_matter_class, matterClassBeans);
                        class_list.setGVPAdapter(matterClassAdapter);
                        matterClassAdapter.setOnItemClickListener(new OnItemClickListener<MatterClassBean>() {

                            @Override
                            public void onItemClick(View view, int position, MatterClassBean data) {
                                Intent intent1 = new Intent();
                                intent1.setClass(SelEffectActivity.this, MatterSongActivity.class);
                                intent1.putExtra("category_id", data.getId());
                                intent1.putExtra("title", data.getName());
                                startActivity(intent1);
                            }
                        });
                    }

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
     * 请求热门歌曲
     */
    private void requestHotSong() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.sucai_hot_list);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    matterSongBeans = gson.fromJson(json, new TypeToken<LinkedList<MatterSongBean>>() {
                    }.getType());
                    matterSongAdapter.setData(matterSongBeans);
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
     * 播放声音
     */
//    private void playSound() {
//        if (TextUtils.isEmpty(playUrl)) {
//            ToastUtils.showToast("无效的播放地址");
//            return;
//        }
//        mediaPlayer.reset();
//        try {
//            // 设置指定的流媒体地址
//            mediaPlayer.setDataSource(playUrl);
//            // 设置音频流的类型
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            // 通过异步的方式装载媒体资源
//            mediaPlayer.prepareAsync();
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    int duration = mp.getDuration();
//                    String total_time = DateUtils.timeParse(duration);
//                    mediaPlayer.start();
//                }
//            });
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            ToastUtils.showToast("播放失败");
//        }
//    }
    @Override
    protected void onDestroy() {
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            mediaPlayer.pause();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
//            mediaPlayer.start();
//        }
    }

    /**
     * 下载素材
     */
    private void downloadMP3() {

        RequestParams requestParams = new RequestParams(playUrl);
        requestParams.setSaveFilePath(cacheFilePath);

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
//                ToastUtils.showToast("下载成功");
                backToEditActivity(0, result.getAbsolutePath());

//                LogUtil.e("----result:" + result.getAbsolutePath());
//                Intent intent = new Intent();
//                intent.putExtra(TCConstants.BGM_POSITION, lastPosition);
//                intent.putExtra(TCConstants.BGM_PATH, cacheFilePath);
//                setResult(TCConstants.ACTIVITY_BGM_REQUEST_CODE, intent);
//                finish();
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


    private void backToEditActivity(int position, String path) {
        Intent intent = new Intent();
        intent.putExtra(TCConstants.BGM_POSITION, position);
        intent.putExtra(TCConstants.BGM_PATH, path);
        intent.putExtra(TCConstants.BGM_DURATION, duration);
        setResult(RESULT_OK, intent);
        finish();
    }

}
