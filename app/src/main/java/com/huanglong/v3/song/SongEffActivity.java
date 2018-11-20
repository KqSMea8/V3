package com.huanglong.v3.song;

import android.app.Activity;
import android.content.Intent;
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
import com.huanglong.v3.adapter.homepage.MatterClassAdapter;
import com.huanglong.v3.model.homepage.MatterClassBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.song.adapter.SongEffAdapter;
import com.huanglong.v3.song.model.SongEffBean;
import com.huanglong.v3.utils.FileUtils;
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
 * Created by bin on 2018/5/3.
 * k歌的素材页面
 */
@ContentView(R.layout.activity_eff_song)
public class SongEffActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.eff_song_list)
    private RecyclerView eff_list;
    @ViewInject(R.id.effect_class_list)
    private GridViewPager class_list;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;

    private SongEffAdapter songEffAdapter;
    private MatterClassAdapter matterClassAdapter;

    private String keyword;
    private int currentPosition;
    private List<SongEffBean> songEffBeans;

    private String filerPath = FileUtils.appPath + "/song";
    private SongEffBean songEffBean;

    private String mp3EffPath;
    private String lrcEffPath;
    private String mp3Name;

    public static SongEffActivity instance;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("选择素材");
        tv_right.setText("K歌");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        eff_list.setLayoutManager(layoutManager);
        songEffAdapter = new SongEffAdapter();
        eff_list.setAdapter(songEffAdapter);

    }

    @Override
    protected void logic() {
        instance = this;
        FileUtils.makeDirs(filerPath);
        requestEff();
        requestEffectClass();

        songEffAdapter.setOnItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                songEffBean = (SongEffBean) obj;
                if (type == 1) {
                    if (position == currentPosition) {
                        boolean isRecord = songEffBean.getIsRecord();
                        if (isRecord) {
                            songEffBean.setIsRecord(false);
                        } else {
                            songEffBean.setIsRecord(true);
                        }
                    } else {
                        songEffBeans.get(currentPosition).setIsRecord(false);
                        songEffBean.setIsRecord(true);
                    }
                    currentPosition = position;
                    songEffAdapter.notifyDataSetChanged();
                } else {
                    mp3Name = songEffBean.getName();
                    String fileName = mp3Name + ".mp3";
                    mp3EffPath = FileUtils.getFilePath(filerPath, fileName);
                    String lrcName = mp3Name + ".lrc";
                    lrcEffPath = FileUtils.getFilePath(filerPath, lrcName);
                    boolean mp3Exists = FileUtils.isFileExists(mp3EffPath);
                    boolean lrcExists = FileUtils.isFileExists(lrcEffPath);
                    if (mp3Exists && lrcExists) {
                        backToEditActivity();
                    } else {
                        downloadMp3Eff();
                    }
                }
            }

            @Override
            public void onItemViewClick(Object obj, int position, int type, View view) {

            }
        });
    }

    @Event(value = {R.id.title_back, R.id.title_tv_right})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_tv_right:
                Intent intent = new Intent();
                intent.setClass(this, SongRecordActivity.class);
//                intent.putExtra("mp3Path", mp3EffPath);
//                intent.putExtra("lrcPath", lrcEffPath);
//                intent.putExtra("lrcUrl", songEffBean.getLrc_url());
//                intent.putExtra("mp3Name", mp3Name);
//                intent.putExtra("cover", songEffBean.getImg_url());
//                intent.putExtra("mp3Url", songEffBean.getPlay_url());
                startActivity(intent);
                break;
        }
    }

    /**
     * 请求k歌素材列表
     */
    private void requestEff() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.music_sucai_hot_list);
        params.addBodyParameter("keyword", keyword);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    songEffBeans = gson.fromJson(json, new TypeToken<LinkedList<SongEffBean>>() {
                    }.getType());
                    songEffAdapter.setData(songEffBeans);
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
     * 下载mp3素材
     */
    private void downloadMp3Eff() {
        showDialog();
        RequestParams requestParams = new RequestParams(songEffBean.getPlay_url());
        requestParams.setSaveFilePath(mp3EffPath);

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
                downloadLrcEff();
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
     * 下载歌词
     */
    private void downloadLrcEff() {
        showDialog();
        RequestParams requestParams = new RequestParams(songEffBean.getLrc_url());
        requestParams.setSaveFilePath(lrcEffPath);
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
                backToEditActivity();
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
     * 跳转录制页面
     */
    private void backToEditActivity() {
        Intent intent = new Intent();
        intent.setClass(this, SongRecordActivity.class);
        intent.putExtra("mp3Path", mp3EffPath);
        intent.putExtra("lrcPath", lrcEffPath);
        intent.putExtra("lrcUrl", songEffBean.getLrc_url());
        intent.putExtra("mp3Name", mp3Name);
        intent.putExtra("cover", songEffBean.getImg_url());
        intent.putExtra("mp3Url", songEffBean.getPlay_url());
        startActivity(intent);
    }


    /**
     * 请求分类
     */
    private void requestEffectClass() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.music_cate_list);
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
                                intent1.setClass(SongEffActivity.this, MatterSongActivity.class);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
