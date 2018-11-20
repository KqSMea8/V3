package com.huanglong.v3.activities.homepage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.TabAdapter;
import com.huanglong.v3.adapter.homepage.GiftAvatarAdapter;
import com.huanglong.v3.im.model.TextMessage;
import com.huanglong.v3.model.homepage.GiftUserBean;
import com.huanglong.v3.model.homepage.KDetBean;
import com.huanglong.v3.model.homepage.KSFBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.song.SongEffActivity;
import com.huanglong.v3.song.SongRecordActivity;
import com.huanglong.v3.utils.CommentChatView;
import com.huanglong.v3.utils.Common;
import com.huanglong.v3.utils.DateUtils;
import com.huanglong.v3.utils.FileUtils;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.KeyBoardUtils;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.PopShareUtils;
import com.huanglong.v3.utils.QQUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.utils.WXUtils;
import com.huanglong.v3.view.CommentInput;
import com.huanglong.v3.view.CustomPop;
import com.huanglong.v3.view.LoadingNumberDialog;
import com.huanglong.v3.view.LrcView;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bin on 2018/4/9.
 * K歌详情
 */
@ContentView(R.layout.activity_k_song_details)
public class KSonDetActivity extends BaseFragmentActivity implements CommentChatView {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.title_rel)
    private RelativeLayout title_bar_lin;
    @ViewInject(R.id.k_song_cover)
    private ImageView img_cover;
    @ViewInject(R.id.k_song_lrc_view)
    private LrcView lrc_view;
    @ViewInject(R.id.k_song_desk_pause)
    private ImageView img_desk_pause;
    @ViewInject(R.id.k_song_seek_bar)
    private SeekBar seek_bar;
    @ViewInject(R.id.k_song_current_duration)
    private TextView tv_current_duration;
    @ViewInject(R.id.k_song_total_duration)
    private TextView tv_total_duration;
    @ViewInject(R.id.k_song_user_avatar)
    private ImageView img_avatar;
    @ViewInject(R.id.k_song_user_nickname)
    private TextView tv_nickname;
    @ViewInject(R.id.k_song_number)
    private TextView tv_number;
    @ViewInject(R.id.k_song_time)
    private TextView tv_time;
    @ViewInject(R.id.k_song_content)
    private TextView tv_content;
    @ViewInject(R.id.k_song_user_list)
    private RecyclerView user_list;
    @ViewInject(R.id.k_song_tab)
    private TabLayout song_tab;
    @ViewInject(R.id.k_song_view_pager)
    private ViewPager view_pager;
    @ViewInject(R.id.k_song_details_lin)
    private RelativeLayout song_details_lin;
    @ViewInject(R.id.k_song_details_zan)
    private TextView tv_zan;
    @ViewInject(R.id.k_song_details_zan_img)
    private ImageView img_zan;
    @ViewInject(R.id.k_song_details_follow)
    private TextView tv_follow;
    @ViewInject(R.id.k_song_gift_count)
    private TextView tv_gift_count;

    private CommentInput input;

    private String filerPath = FileUtils.appPath + "/song";

    public static KSonDetActivity instance;

    private List<Fragment> fragments = new ArrayList<>();
    private TabAdapter tabAdapter;


    private Handler handler = new Handler();

    private List<String> tab_title = new ArrayList<>();


    private KSFBean ksfBean;

    private MediaPlayer mMediaPlayer;
    private String playUrl = "";
    private GiftAvatarAdapter giftAvatarAdapter;
    private CustomPop customPop;

    public String music_id;
    public String member_id;
    private EditText edt_comment;
    private KSCommentFragment kSCommentFragment;


    private LoadingNumberDialog loadingNumberDialog;
    private String lrcPath;
    private int is_zan;
    private int is_followed;
    private KDetBean kDetBean;

    private boolean isGift = false;
    private int zan_count;

    private PopShareUtils popShareUtils;
    private int follow_count;


    private WeChatShareBroadcast weChatBroadcastReceiver;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        instance = this;
        mImmersionBar = ImmersionBar.with(this);
//        mImmersionBar;   //所有子类都将继承这些相同的属性
        mImmersionBar.statusBarColor(R.color.transparent)
                .keyboardEnable(true)
                .init();
        title_bar_lin.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));


        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        user_list.setLayoutManager(layoutManager);
        giftAvatarAdapter = new GiftAvatarAdapter();
        user_list.setAdapter(giftAvatarAdapter);

        tabAdapter = new TabAdapter(this.getSupportFragmentManager(), null);
        view_pager.setAdapter(tabAdapter);
        kSCommentFragment = new KSCommentFragment();
        fragments.add(kSCommentFragment);
        fragments.add(new KSOtherFragment());
        fragments.add(new PraiseListFragment());
        tabAdapter.setFragmentData(fragments);
        song_tab.setupWithViewPager(view_pager);

        loadingNumberDialog = new LoadingNumberDialog(this);

        FileUtils.makeDirs(filerPath);

        Intent intent = getIntent();
        ksfBean = (KSFBean) intent.getSerializableExtra("ksfBean");
        if (ksfBean != null) {
            member_id = ksfBean.getMember_id();
            tv_title.setText(ksfBean.getTitle());
            x.image().bind(img_cover, ksfBean.getCover_img(), MImageOptions.getNormalImageOptions());
            playUrl = ksfBean.getPlay_url();
            music_id = ksfBean.getId();
            requestData();
        } else {
            ToastUtils.showToast("网络异常");
        }
        initPop();
    }

    /**
     * 初始化评论的pop
     */
    private void initPop() {
        customPop = new CustomPop(getActivity(), R.layout.pop_comment);
        input = (CommentInput) customPop.getView(R.id.input_panel);
//        customPop.setChatInput(this);
        input.setChatView(this);
//        edt_comment = (EditText) customPop.getView(R.id.pop_comment_content);
//        customPop.getView(R.id.pop_comment_send).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                customPop.dismiss();
//                KeyBoardUtils.hideKeyboard(getActivity());
//                String comment = edt_comment.getText().toString().trim();
//                if (!TextUtils.isEmpty(comment)) {
//                    requestComment(comment);
//                }
//                edt_comment.setText("");
//            }
//        });


        popShareUtils = new PopShareUtils(this);
        popShareUtils.setOnClickListener(new PopShareUtils.OnClickListener() {
            @Override
            public void onClick(int type) {
//                registerShareReceiver();
                String shareUrl = Api.share_k_song_url.replace("ID", music_id).replace("Uid", kDetBean.getMember_id());
                if (type == PopShareUtils.WECHAT_CIRCLE) {
                    WXUtils.shareWeChat(KSonDetActivity.this, shareUrl, Common.K_SONG_SHARE_TITLE, Common.K_SONG_SHARE_DES, true, kDetBean.getCover_img());
                } else if (type == PopShareUtils.WECHAT_FRIENDS) {
                    WXUtils.shareWeChat(KSonDetActivity.this, shareUrl, Common.K_SONG_SHARE_TITLE, Common.K_SONG_SHARE_DES, false, kDetBean.getCover_img());
                } else if (type == PopShareUtils.QQ) {
                    QQUtils.shareQQ(KSonDetActivity.this, Common.K_SONG_SHARE_TITLE, Common.K_SONG_SHARE_DES, shareUrl, kDetBean.getCover_img());
                } else if (type == PopShareUtils.QQ_ZONE) {
                    QQUtils.shareQQZone(KSonDetActivity.this, Common.K_SONG_SHARE_TITLE, Common.K_SONG_SHARE_DES, shareUrl, kDetBean.getCover_img());
                } else if (type == PopShareUtils.COPY_LINK) {
                    QQUtils.copyLink(KSonDetActivity.this, shareUrl);
                }

//                WXUtils.shareFriends(KSonDetActivity.this, shareUrl, kDetBean.getSucai_name(), kDetBean.getNickname() + "", type);
            }

        });


    }


    @Override
    protected void logic() {
        lrc_view.setOnPlayClickListener(new LrcView.OnPlayClickListener() {
            @Override
            public boolean onPlayClick(long time) {
                mMediaPlayer.seekTo((int) time);
                if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    handler.post(runnable);
                }
                return true;
            }
        });

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(seekBar.getProgress());
                lrc_view.updateTime(seekBar.getProgress());
            }
        });

        song_tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view_pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        giftAvatarAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                GiftUserBean giftUserBean = (GiftUserBean) obj;
                Intent intent = new Intent();
                intent.setClass(KSonDetActivity.this, PersonalPageActivity.class);
                intent.putExtra("uid", giftUserBean.getFollower_id());
                startActivity(intent);
            }
        });

        weChatBroadcastReceiver = new WeChatShareBroadcast();
        registerReceiver();


    }

    @Event(value = {R.id.title_back, R.id.k_song_desk_pause, R.id.k_song_details_comment, R.id.k_song_details_zan, R.id.k_song_details_follow
            , R.id.k_song_user_avatar, R.id.k_song_details_gift, R.id.k_song_details_song, R.id.k_song_gift_count, R.id.k_song_details_share
            , R.id.k_song_details_zan_img, R.id.k_song_user_list_more})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                KSonDetActivity.this.finish();
                break;
            case R.id.k_song_desk_pause:
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) {
                        img_desk_pause.setImageResource(R.mipmap.desk_play_prs);
                        mMediaPlayer.pause();
                        handler.post(runnable);
                    } else {
                        img_desk_pause.setImageResource(R.mipmap.desk_pause_prs);
                        mMediaPlayer.start();
                        handler.removeCallbacks(runnable);
                    }
                }
                break;
            case R.id.k_song_details_comment:
                customPop.showAtLocation(song_details_lin, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.k_song_details_zan:
                Intent intent3 = new Intent();
                intent3.setClass(this, PraiseListActivity.class);
                intent3.putExtra("music_id", music_id);
                startActivity(intent3);
                break;
            case R.id.k_song_details_follow:
                requestFollow();
                break;
            case R.id.k_song_user_avatar:
                Intent intent = new Intent();
                intent.setClass(this, PersonalPageActivity.class);
                intent.putExtra("uid", member_id);
                startActivity(intent);
                break;
            case R.id.k_song_details_gift:
                isGift = true;
                Intent intent2 = new Intent();
                intent2.setClass(getActivity(), GiftActivity.class);
                intent2.putExtra("userId", member_id);
                intent2.putExtra("type", 2);
                intent2.putExtra("info_id", music_id);
                startActivityForResult(intent2, 1002);
                getActivity().overridePendingTransition(R.anim.enter_bottom, R.anim.exit_bottom);
                break;
            case R.id.k_song_details_song:
                backToEditActivity();
                break;
            case R.id.k_song_gift_count:
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(), GiftListActivity.class);
                intent1.putExtra("music_id", music_id);
                startActivity(intent1);
                break;
            case R.id.k_song_details_share:
                popShareUtils.showAtLocation(song_details_lin, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.k_song_details_zan_img:
                requestZan();
                break;
            case R.id.k_song_user_list_more:
                Intent intent4 = new Intent();
                intent4.setClass(getActivity(), GiftListActivity.class);
                intent4.putExtra("music_id", music_id);
                startActivity(intent4);
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
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.reset();
        try {
//            showDialog();
            // 设置指定的流媒体地址
            mMediaPlayer.setDataSource(playUrl);
            // 设置音频流的类型
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 通过异步的方式装载媒体资源
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
//                    dismissDialog();
                    // 装载完毕 开始播放流媒体
                    tv_current_duration.setText("00:00");
                    seek_bar.setProgress(0);
                    int duration = mp.getDuration();
                    String total_time = DateUtils.timeParse(duration);
                    tv_total_duration.setText(total_time);
                    seek_bar.setMax(duration);
                    mMediaPlayer.start();
                    handler.post(runnable);
//                    // 避免重复播放，把播放按钮设置为不可用
//                    btn_play.setEnabled(false);
                }
            });


            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    handler.removeCallbacks(runnable);
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
//            dismissDialog();
            ToastUtils.showToast("播放失败");
        }

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer.isPlaying()) {
                long time = mMediaPlayer.getCurrentPosition();
                lrc_view.updateTime(time);
                seek_bar.setProgress((int) time);
                String total_time = DateUtils.timeParse((int) time);
                tv_current_duration.setText(total_time);
            }
            handler.postDelayed(this, 1000);
        }
    };

    /**
     * 请求数据
     */
    private void requestData() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.music_detail);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("music_id", ksfBean.getId());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    kDetBean = gson.fromJson(json, KDetBean.class);
                    if (kDetBean != null) {
                        showInfo(kDetBean);
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
     * 显示信息
     *
     * @param kDetBean
     */
    private void showInfo(KDetBean kDetBean) {
        x.image().bind(img_avatar, kDetBean.getHead_image(), MImageOptions.getCircularImageOptions());
        tv_nickname.setText(kDetBean.getNickname());
        tv_number.setText(kDetBean.getClick_count() + "     " + kDetBean.getPoints());
        tv_time.setText(kDetBean.getCreate_time());
        tv_content.setText(kDetBean.getContent());
        giftAvatarAdapter.setData(kDetBean.getGift());
        tab_title.clear();
        tab_title.add("评论(" + kDetBean.getTotal() + ")");
        tab_title.add("其他作品");
        tab_title.add("赞");
        tabAdapter.setTitle(tab_title);
        tv_gift_count.setText("累计：" + kDetBean.getGift().size());


        is_zan = kDetBean.getIs_zan();
        zan_count = kDetBean.getZan_count();
        tv_zan.setText(zan_count + "");
        setPraiseIcon(is_zan != 0);

        is_followed = kDetBean.getIs_followed();
        follow_count = kDetBean.getGuanzhu_count();
        setFollowIcon(is_followed != 0, false);

//        if (is_followed == 0) {
//            tv_follow.setText("+关注");
//            tv_follow.setBackgroundResource(R.drawable.box_orange_white_circular);
//            tv_follow.setTextColor(ContextCompat.getColor(this, R.color.red_EF3338));
//        } else {
//            img_follow.setImageResource(R.mipmap.icon_follow_n);
//            tv_follow.setText("取消关注");
//            tv_follow.setBackgroundResource(R.drawable.box_orange_circular);
//            tv_follow.setTextColor(ContextCompat.getColor(this, R.color.white));
//        }

        String lrc_url = kDetBean.getLrc_url();
        if (!TextUtils.isEmpty(lrc_url)) {
            String fileName = lrc_url.substring(lrc_url.lastIndexOf("/") + 1, lrc_url.length());
            lrcPath = FileUtils.getFilePath(filerPath, fileName);
            boolean fileExists = FileUtils.isFileExists(lrcPath);
            if (fileExists) {
                lrc_view.loadLrc(new File(lrcPath));
                playSound();
            } else {
                downloadLrc(lrc_url);
            }
        } else {
            playSound();
        }

    }

    /**
     * 请求评论
     *
     * @param comment
     */
    private void requestComment(String comment) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.music_add_comment);
        params.addBodyParameter("user_id", UserInfoUtils.getUid());
        params.addBodyParameter("content", comment);
        params.addBodyParameter("music_id", music_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    kSCommentFragment.requestComment();
                    if (KSongActivity.instance != null) {
                        KSongActivity.instance.onRefresh();
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
     * 下载歌词
     */
    private void downloadLrc(String lrcUrl) {
        loadingNumberDialog.showDialog();
        RequestParams params = new RequestParams(lrcUrl);
        params.setSaveFilePath(lrcPath);
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                long l = (current / total) * 100;
                loadingNumberDialog.setProgress(Integer.valueOf(String.valueOf(l)));
            }

            @Override
            public void onSuccess(File result) {
                lrc_view.loadLrc(result.getAbsoluteFile());
                playSound();
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
                loadingNumberDialog.dismissDialog();
            }
        });
    }

    /**
     * 请求关注
     */
    private void requestFollow() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.music_add_follow);
        params.addBodyParameter("follower_id", UserInfoUtils.getUid());
        params.addBodyParameter("user_id", kDetBean.getMember_id());

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    if (is_followed == 0) {
                        is_followed = 1;
                        setFollowIcon(true, true);
                    } else {
                        is_followed = 0;
                        setFollowIcon(false, true);
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
     * 点赞
     */
    private void requestZan() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.music_music_upvote);
        params.addBodyParameter("user_id", member_id);
        params.addBodyParameter("follower_id", UserInfoUtils.getUid());
        params.addBodyParameter("music_id", music_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    if (is_zan == 0) {
                        is_zan = 1;
                        ToastUtils.showToast("点赞成功");
                        zan_count++;
                        setPraiseIcon(true);
                    } else {
                        ToastUtils.showToast("取消点赞");
                        is_zan = 0;
                        if (zan_count > 0) {
                            zan_count--;
                        }
                        setPraiseIcon(false);
                    }
                    tv_zan.setText(zan_count + "");
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
        handler.removeCallbacks(runnable);
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        this.unregisterReceiver(weChatBroadcastReceiver);
        instance = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isGift) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                img_desk_pause.setImageResource(R.mipmap.desk_play_prs);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isGift) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
                img_desk_pause.setImageResource(R.mipmap.desk_pause_prs);
            }
        }
        isGift = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, new QQUtils.BaseUiListener());
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1002:
                    requestData();
                    if (KSongActivity.instance != null) {
                        KSongActivity.instance.onRefresh();
                    }
                    break;
            }
        }
    }

    private String mp3Name;
    private String mp3EffPath;

    private void backToEditActivity() {
        mp3Name = kDetBean.getSucai_name();
        if (!TextUtils.isEmpty(mp3Name)) {
            String fileName = mp3Name + ".mp3";
            mp3EffPath = FileUtils.getFilePath(filerPath, fileName);
            boolean mp3Exists = FileUtils.isFileExists(mp3EffPath);
            if (mp3Exists) {
                Intent intent1 = new Intent();
                intent1.setClass(this, SongRecordActivity.class);
                intent1.putExtra("mp3Path", kDetBean.getSucai_url());
                intent1.putExtra("lrcPath", lrcPath);
                intent1.putExtra("lrcUrl", kDetBean.getLrc_url());
                intent1.putExtra("mp3Name", mp3Name);
                intent1.putExtra("cover", kDetBean.getCover_img());
                intent1.putExtra("mp3Url", kDetBean.getSucai_url());
                startActivity(intent1);
            } else {
                downloadMp3Eff();
            }
        } else {
            Intent intent = new Intent();
            intent.setClass(this, SongEffActivity.class);
            startActivity(intent);
        }


    }

    /**
     * 下载mp3素材
     */
    private void downloadMp3Eff() {
        showDialog();
        RequestParams requestParams = new RequestParams(kDetBean.getSucai_url());
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
                Intent intent1 = new Intent();
                intent1.setClass(KSonDetActivity.this, SongRecordActivity.class);
                intent1.putExtra("mp3Path", kDetBean.getSucai_url());
                intent1.putExtra("lrcPath", lrcPath);
                intent1.putExtra("lrcUrl", kDetBean.getLrc_url());
                intent1.putExtra("mp3Name", mp3Name);
                intent1.putExtra("cover", kDetBean.getCover_img());
                intent1.putExtra("mp3Url", kDetBean.getSucai_url());
                startActivity(intent1);
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
     * 设置点赞的icon
     *
     * @param isZan
     */
    private void setPraiseIcon(boolean isZan) {
//        Drawable leftDrawable;
        if (isZan) {
            img_zan.setImageResource(R.mipmap.icon_zan_press);
//            leftDrawable = getResources().getDrawable(R.mipmap.icon_zan_press);
        } else {
            img_zan.setImageResource(R.mipmap.icon_zan);
//            leftDrawable = getResources().getDrawable(R.mipmap.icon_zan);
        }
//        leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
//        tv_zan.setCompoundDrawables(leftDrawable, null, null, null);
    }

    /**
     * 设置点关注的icon
     *
     * @param isZan
     */
    private void setFollowIcon(boolean isZan, boolean isClick) {
        Drawable leftDrawable;
        if (isZan) {
            if (isClick) {
                follow_count++;
            }
            leftDrawable = getResources().getDrawable(R.mipmap.icon_follow_press);
        } else {
            if (isClick) {
                if (follow_count > 0) {
                    follow_count--;
                }
            }
            leftDrawable = getResources().getDrawable(R.mipmap.icon_follow_normal);
        }
        leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
        tv_follow.setCompoundDrawables(leftDrawable, null, null, null);
        tv_follow.setText(follow_count + "");
    }


    @Override
    public void sendText() {
        customPop.dismiss();
        KeyBoardUtils.hideKeyboard(getActivity());
        TextMessage msg = new TextMessage(input.getText());
        String msgStr = TextMessage.getMsgStr(msg.getMessage(), getActivity()).toString();
        input.setText("");
        if (!TextUtils.isEmpty(msgStr)) {
            requestComment(msgStr);
        }
    }

    @Override
    public void sending() {

    }

    /**
     * 注册微信分享广播
     */

    private void registerReceiver() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.BINDING_WECHAT_ACTION);
        this.registerReceiver(weChatBroadcastReceiver, filter);
    }


    /**
     * 微信支付成功后接收广播处理
     *
     * @author hbb
     */
    private class WeChatShareBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Common.BINDING_WECHAT_ACTION)) return;
            String type = intent.getStringExtra(Common.BINDING_WECHAT_KEY);
            if (TextUtils.equals("success", type)) {
                ToastUtils.showToast("分享成功");
                requestShareAdd();
            } else if (TextUtils.equals("cancel", type)) {
                ToastUtils.showToast("取消分享");
            } else {
                ToastUtils.showToast("分享失败");
            }
        }
    }

    /**
     * 分享成功请求
     */
    private void requestShareAdd() {

        RequestParams params = MRequestParams.getNoTokenParams(Api.music_add_share);
        params.addBodyParameter("music_id", music_id);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    if (KSongActivity.instance != null) {
                        KSongActivity.instance.onRefresh();
                    }
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("add_share error " + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }


}
