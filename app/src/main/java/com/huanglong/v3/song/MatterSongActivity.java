package com.huanglong.v3.song;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.song.adapter.SongEffAdapter;
import com.huanglong.v3.song.model.SongEffBean;
import com.huanglong.v3.utils.FileUtils;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.KeyBoardUtils;

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
 * 素材歌曲页面
 */
@ContentView(R.layout.activity_matter_song)
public class MatterSongActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.matter_song_list)
    private RecyclerView song_list;
    @ViewInject(R.id.matter_song_search)
    private EditText edt_search;


    private String playUrl = "";

    private int lastPosition = -1;

    private int flag;
    private String cacheFilePath;

    private SongEffAdapter songEffAdapter;


    private String category_id;
    private String keyword;
    private List<SongEffBean> matterSongBeans;

    private String filerPath = FileUtils.appPath + "/song";
    private SongEffBean songEffBean;

    private int currentPosition;
    private List<SongEffBean> songEffBeans;

    private String mp3EffPath;
    private String lrcEffPath;
    private String mp3Name;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        song_list.setLayoutManager(layoutManager);
        songEffAdapter = new SongEffAdapter();
        song_list.setAdapter(songEffAdapter);
    }

    @Override
    protected void logic() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        tv_title.setText(title);
        category_id = intent.getStringExtra("category_id");

        requestSongList();

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    keyword = textView.getText().toString().trim();
                    requestSongList();
                }
                KeyBoardUtils.hideKeyboard(MatterSongActivity.this);
                return true;
            }
        });


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

    @Event(value = {R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

    /**
     * 请求歌曲列表
     */
    private void requestSongList() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.music_song_list);
        params.addBodyParameter("category_id", category_id);
        params.addBodyParameter("keyword", keyword);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    matterSongBeans = gson.fromJson(json, new TypeToken<LinkedList<SongEffBean>>() {
                    }.getType());
                    songEffAdapter.setData(matterSongBeans);
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
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
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
        finish();
    }

}
