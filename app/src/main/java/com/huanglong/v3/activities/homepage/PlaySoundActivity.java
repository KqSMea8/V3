package com.huanglong.v3.activities.homepage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.adapter.TabAdapter;
import com.huanglong.v3.im.model.TextMessage;
import com.huanglong.v3.live.utils.TCUtils;
import com.huanglong.v3.model.homepage.AlbumBean;
import com.huanglong.v3.model.homepage.SoundBookBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.song.SongPublishActivity;
import com.huanglong.v3.utils.CommentChatView;
import com.huanglong.v3.utils.DateUtils;
import com.huanglong.v3.utils.KeyBoardUtils;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.view.CommentInput;
import com.huanglong.v3.view.CustomPop;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bin on 2018/4/9.
 * 有声书播放/flag = 1 k歌试听页面
 */
@ContentView(R.layout.activity_play_sound)
public class PlaySoundActivity extends BaseFragmentActivity implements CommentChatView {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.play_album_bg)
    private ImageView img_album_bg;
    @ViewInject(R.id.play_sound_current_duration)
    private TextView tv_current_duration;
    @ViewInject(R.id.play_sound_total_duration)
    private TextView tv_total_duration;
    @ViewInject(R.id.play_sound_seek_ber)
    private SeekBar seek_ber;
    @ViewInject(R.id.play_sound_desk_pre)
    private ImageView img_desk_pre;
    @ViewInject(R.id.play_sound_desk_pause)
    private ImageView img_desk_pause;
    @ViewInject(R.id.play_sound_desk_next)
    private ImageView img_desk_next;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;
    @ViewInject(R.id.play_sound_tab)
    private TabLayout sound_tab;
    @ViewInject(R.id.play_sound_view_pager)
    private ViewPager sound_view_pager;
    @ViewInject(R.id.play_sound_user_lin)
    private LinearLayout sound_user_lin;
    @ViewInject(R.id.play_sound_user_avatar)
    private ImageView user_avatar;
    @ViewInject(R.id.play_sound_nickname)
    private TextView user_nickname;
    @ViewInject(R.id.play_sound_comment_count)
    private TextView tv_comment_count;
    @ViewInject(R.id.play_sound_prise_count)
    private TextView tv_prise_count;
    @ViewInject(R.id.play_sound_details_lin)
    private LinearLayout details_lin;

    private CommentInput input;

    private MediaPlayer mediaPlayer;
    private String playUrl = "";
    private String lrcUrl;
    private String mp3Url;

    private List<AlbumBean> albumBeans;
    private AlbumBean currentAlbum;

    private int count = 0;
    private int flag = 0;

    private Handler handler = new Handler();
    private SoundBookBean soundBookBean;

    private List<Fragment> fragments = new ArrayList<>();
    private TabAdapter tabAdapter;
    private List<String> tab_title = new ArrayList<>();
    private BookComFragment bookComFragment;
    private BookPriseFragment bookPriseFragment;

    private CustomPop customPop;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_right.setText("发布");

        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", 0);
        String album_img = intent.getStringExtra("album_img");
        if (flag == 1) {
            playUrl = intent.getStringExtra("playUrl");
            lrcUrl = intent.getStringExtra("lrcUrl");
            mp3Url = intent.getStringExtra("mp3Url");
            img_desk_pre.setVisibility(View.GONE);
            img_desk_next.setVisibility(View.GONE);
            tv_right.setVisibility(View.VISIBLE);
            tv_title.setText("试听");
            sound_user_lin.setVisibility(View.GONE);
            sound_tab.setVisibility(View.GONE);
            sound_view_pager.setVisibility(View.GONE);

        } else {
            tv_right.setVisibility(View.GONE);
            sound_user_lin.setVisibility(View.VISIBLE);
            sound_tab.setVisibility(View.VISIBLE);
            sound_view_pager.setVisibility(View.VISIBLE);
            tv_title.setText("音频详情");
            albumBeans = (List<AlbumBean>) intent.getSerializableExtra("albumBeans");
            count = intent.getIntExtra("position", 0);
            soundBookBean = (SoundBookBean) intent.getSerializableExtra("soundBookBean");
            showInfo();
            if (albumBeans != null) {
                AlbumBean albumBean = albumBeans.get(count);
                playUrl = albumBean.getPlay_url();
                requestPlayNum(albumBean.getId());
                showCommentPrise(albumBean);
            }
        }
        TCUtils.blurBgPic(this, img_album_bg, album_img, R.drawable.bg);

        initPop();
    }

    /**
     * 显示作者信息
     */
    private void showInfo() {
        x.image().bind(user_avatar, soundBookBean.getHead_image(), MImageOptions.getCircularImageOptions());
        user_nickname.setText(soundBookBean.getNickname());

        tabAdapter = new TabAdapter(this.getSupportFragmentManager(), null);
        sound_view_pager.setAdapter(tabAdapter);
        bookComFragment = new BookComFragment();
        bookPriseFragment = new BookPriseFragment();
        fragments.add(bookComFragment);
        fragments.add(bookPriseFragment);
        tabAdapter.setFragmentData(fragments);
        sound_tab.setupWithViewPager(sound_view_pager);
        tab_title.clear();
        tab_title.add("评论");
        tab_title.add("赞");
        tabAdapter.setTitle(tab_title);


    }

    /**
     * 初始化评论的pop
     */
    private void initPop() {
        customPop = new CustomPop(getActivity(), R.layout.pop_comment);
        input = (CommentInput) customPop.getView(R.id.input_panel);
//        customPop.setChatInput(this);
        input.setChatView(this);
    }

    @Override
    protected void logic() {
        playSound();
//        rotateAnim();
        seek_ber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                String current_time = DateUtils.timeParse(progress);
                tv_current_duration.setText(current_time);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (flag != 1) {
                    count++;
                    nextSound();
                }
            }
        });
    }

//    /**
//     * 选中动画
//     */
//    public void rotateAnim() {
//        Animation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        anim.setFillAfter(true); // 设置保持动画最后的状态
//        anim.setDuration(6000); // 设置动画时间
//        anim.setInterpolator(new LinearInterpolator()); // 设置插入器
//        anim.setRepeatCount(Animation.INFINITE);
//        img_album.startAnimation(anim);
//    }


    @Event(value = {R.id.title_back, R.id.play_sound_desk_pre, R.id.play_sound_desk_pause, R.id.play_sound_desk_next, R.id.title_tv_right,
            R.id.play_sound_prise_count, R.id.play_sound_comment_count})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                PlaySoundActivity.this.finish();
                break;
            case R.id.play_sound_desk_pre:
                if (count == 0) {
                    count = 0;
                } else {
                    count--;
                }
                prevSound();
                break;
            case R.id.play_sound_desk_pause:
                playAndPause();
                break;
            case R.id.play_sound_desk_next:
                count++;
                nextSound();
                break;
            case R.id.title_tv_right:
                Intent intent = new Intent();
                intent.setClass(this, SongPublishActivity.class);
                intent.putExtra("path", playUrl);
                intent.putExtra("lrcUrl", lrcUrl);
                intent.putExtra("sucai_url", mp3Url);
                startActivityForResult(intent, 1000);
                break;
            case R.id.play_sound_prise_count:
                requestPrise();
                break;
            case R.id.play_sound_comment_count:
                customPop.showAtLocation(details_lin, Gravity.BOTTOM, 0, 0);
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
        try {
            showDialog();
            // 设置指定的流媒体地址
            mediaPlayer.setDataSource(playUrl);
            // 设置音频流的类型
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 通过异步的方式装载媒体资源
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    dismissDialog();
                    // 装载完毕 开始播放流媒体
                    tv_current_duration.setText("00:00");
                    seek_ber.setProgress(0);
                    int duration = mp.getDuration();
                    String total_time = DateUtils.timeParse(duration);
                    tv_total_duration.setText(total_time);
                    seek_ber.setMax(duration);
                    seek_ber.setProgress(50);
                    mediaPlayer.start();
                    handler.post(runnable);
//                    // 避免重复播放，把播放按钮设置为不可用
//                    btn_play.setEnabled(false);
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
            dismissDialog();
            ToastUtils.showToast("播放失败");
        }

    }

    /**
     * 下一首
     */
    private void nextSound() {
        handler.removeCallbacks(runnable);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (albumBeans != null) {
            AlbumBean albumBean = albumBeans.get(count % albumBeans.size());
            tv_title.setText(albumBean.getTitle());
            playUrl = albumBean.getPlay_url();
            showCommentPrise(albumBean);
        }
        tv_current_duration.setText("00:00");
        seek_ber.setProgress(0);
        playSound();
    }

    /**
     * 上一首
     */
    private void prevSound() {
        handler.removeCallbacks(runnable);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (albumBeans != null) {
            AlbumBean albumBean = albumBeans.get(count % albumBeans.size());
            tv_title.setText(albumBean.getTitle());
            playUrl = albumBean.getPlay_url();
            showCommentPrise(albumBean);
        }
        tv_current_duration.setText("00:00");
        seek_ber.setProgress(0);
        playSound();
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()) {
                long time = mediaPlayer.getCurrentPosition();
                seek_ber.setProgress((int) time);
                String current_time = DateUtils.timeParse((int) time);
                tv_current_duration.setText(current_time);
            }
            handler.postDelayed(this, 300);
        }
    };

    /**
     * 暂停或播放
     */
    private void playAndPause() {
        if (mediaPlayer.isPlaying()) {
            img_desk_pause.setImageResource(R.mipmap.desk_play_prs);
            mediaPlayer.pause();
            handler.removeCallbacks(runnable);
        } else {
            img_desk_pause.setImageResource(R.mipmap.desk_pause_prs);
            mediaPlayer.start();
            handler.post(runnable);
        }
    }


    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    setResult(RESULT_OK);
                    finish();
                    break;
            }
        }
    }

    /**
     * 请求播放量
     */
    private void requestPlayNum(String book_id) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.book_anlysize_program);
        params.addBodyParameter("sub_book_id", book_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                LogUtil.e("play num add " + json);
                if (AlbumListActivity.instance != null) {
                    AlbumListActivity.instance.requestAlbumList();
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
     * 显示赞。评论
     */
    private void showCommentPrise(AlbumBean albumBean) {
        currentAlbum = albumBean;
        int is_zan = albumBean.getIs_zan();
        setPraiseIcon(is_zan == 0 ? false : true);
        tv_comment_count.setText(albumBean.getComment_count() + "");
        tv_prise_count.setText(albumBean.getZan_count() + "");
        if (bookComFragment != null) {
            bookComFragment.requestCommentList(albumBean.getId());
        }
        if (bookPriseFragment != null) {
            bookPriseFragment.requestPriseList(albumBean.getId());
        }
    }

    /**
     * 设置点赞的icon
     *
     * @param isZan
     */
    private void setPraiseIcon(boolean isZan) {
        Drawable leftDrawable;
        if (isZan) {
            leftDrawable = getResources().getDrawable(R.mipmap.icon_zan_press);
        } else {
            leftDrawable = getResources().getDrawable(R.mipmap.icon_zan);
        }
        leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
        tv_prise_count.setCompoundDrawables(leftDrawable, null, null, null);
    }

    /**
     * 点赞
     */
    private void requestPrise() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.book_book_upvote);
        params.addBodyParameter("follower_id", UserInfoUtils.getUid());
        params.addBodyParameter("sub_book_id", currentAlbum.getId());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    int is_zan = currentAlbum.getIs_zan();
                    if (is_zan == 0) {
                        currentAlbum.setIs_zan(1);
                        currentAlbum.setZan_count(currentAlbum.getZan_count() + 1);
                        tv_prise_count.setText(currentAlbum.getZan_count() + "");
                        setPraiseIcon(true);

                    } else {
                        currentAlbum.setIs_zan(0);
                        setPraiseIcon(false);
                        currentAlbum.setZan_count(currentAlbum.getZan_count() - 1);
                        tv_prise_count.setText(currentAlbum.getZan_count() + "");
                    }
                    if (AlbumListActivity.instance != null) {
                        AlbumListActivity.instance.requestAlbumList();
                    }

                    if (bookPriseFragment != null) {
                        bookPriseFragment.requestPriseList(currentAlbum.getId());
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
     * 发布评论
     *
     * @param comment
     */
    private void requestComment(String comment) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.book_add_comment);
        params.addBodyParameter("user_id", UserInfoUtils.getUid());
        params.addBodyParameter("content", comment);
        params.addBodyParameter("book_id", currentAlbum.getId());


        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    if (AlbumListActivity.instance != null) {
                        AlbumListActivity.instance.requestAlbumList();
                    }
                    currentAlbum.setComment_count(currentAlbum.getComment_count() + 1);
                    tv_comment_count.setText(currentAlbum.getComment_count() + "");
                    if (bookComFragment != null) {
                        bookComFragment.requestCommentList(currentAlbum.getId());
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

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            playAndPause();
        }
    }
}
