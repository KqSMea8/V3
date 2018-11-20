package com.huanglong.v3.live.push;

import android.app.Service;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.homepage.GiftActivity;
import com.huanglong.v3.activities.homepage.PersonalPageActivity;
import com.huanglong.v3.live.TCBaseActivity;
import com.huanglong.v3.live.im.TCChatEntity;
import com.huanglong.v3.live.im.TCChatMsgListAdapter;
import com.huanglong.v3.live.im.TCChatRoomMgr;
import com.huanglong.v3.live.im.TCSimpleUserInfo;
import com.huanglong.v3.live.im.TCUserAvatarListAdapter;
import com.huanglong.v3.live.model.LiveGiftBean;
import com.huanglong.v3.live.utils.SHARE_MEDIA;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.live.utils.TCFrequeControl;
import com.huanglong.v3.live.utils.TCUtils;
import com.huanglong.v3.live.widget.TCDanmuMgr;
import com.huanglong.v3.live.widget.TCHeartLayout;
import com.huanglong.v3.live.widget.TCInputTextMsgDialog;
import com.huanglong.v3.live.widget.TCSwipeAnimationController;
import com.huanglong.v3.model.homepage.GiftBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.video.TCVideoPublisherActivity;
import com.tencent.TIMElemType;
import com.tencent.TIMMessage;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLog;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.ugc.TXRecordCommon;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import master.flame.danmaku.controller.IDanmakuView;

/**
 * Created by RTMP on 2016/8/4
 */
@ContentView(R.layout.activity_play)
public class TCLivePlayerActivity extends TCBaseActivity implements ITXLivePlayListener, View.OnClickListener, TCPlayerMgr.PlayerListener, TCInputTextMsgDialog.OnTextSendListener, TCChatRoomMgr.TCChatRoomListener, TXRecordCommon.ITXVideoRecordListener {
    private static final String TAG = TCLivePlayerActivity.class.getSimpleName();


    @ViewInject(R.id.live_follow)
    private TextView tv_follow;
    @ViewInject(R.id.live_like_number)
    private TextView tv_like_number;
    @ViewInject(R.id.live_gift_img)
    private ImageView img_gift;

    protected TXCloudVideoView mTXCloudVideoView;
    private TCInputTextMsgDialog mInputTextMsgDialog;
    private ListView mListViewMsg;

    private ArrayList<TCChatEntity> mArrayListChatEntity = new ArrayList<>();
    private TCChatMsgListAdapter mChatMsgListAdapter;

    private TXLivePlayer mTXLivePlayer;
    private TXLivePlayConfig mTXPlayConfig = new TXLivePlayConfig();

    protected Handler mHandler = new Handler();

    private ImageView mHeadIcon;
    private ImageView mRecordBall;
    private TextView mtvPuserName;
    private TextView mMemberCount;
    private int mPageNum = 1;

    private String mPusherAvatar;

    private long mCurrentMemberCount = 0;
    private long mTotalMemberCount = 0;
    private long mHeartCount = 0;
    private long mLastedPraisedTime = 0;

    protected boolean mPausing = false;
    private boolean mPlaying = false;
    private String mPusherNickname;
    protected String mPusherId;
    protected String mPlayUrl = "http://2527.vod.myqcloud.com/2527_000007d04afea41591336f60841b5774dcfd0001.f0.flv";
    private String mGroupId = "";
    private String mFileId = "";
    protected String mUserId = "";
    protected String mNickname = "";
    protected String mHeadPic = "";
    private int mTimeStamp = 0;

    protected boolean mIsLivePlay;
    private int mPlayType = 0;      //点播还是直播，拉列表协议返回的
    private int mUrlPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;      //根据mIsLivePlay和url判断出的播放类型，更加具体

    protected TCPlayerMgr mTCPlayerMgr;
    private TCChatRoomMgr mTCChatRoomMgr;

    //头像列表控件
    private RecyclerView mUserAvatarList;
    private TCUserAvatarListAdapter mAvatarListAdapter;

    //点赞动画
    private TCHeartLayout mHeartLayout;
    //点赞频率控制
    private TCFrequeControl mLikeFrequeControl;

    //弹幕
    private TCDanmuMgr mDanmuMgr;
    private IDanmakuView mDanmuView;

    //点播相关
    private long mTrackingTouchTS = 0;
    private boolean mStartSeek = false;
    private boolean mVideoPause = false;
    private SeekBar mSeekBar;
    private ImageView mPlayIcon;
    private TextView mTextProgress;

    //手势动画
    private RelativeLayout mControllLayer;
    private TCSwipeAnimationController mTCSwipeAnimationController;
    protected ImageView mBgImageView;

    //分享相关
//    private UMImage mImage = null;
    private SHARE_MEDIA mShare_meidia = SHARE_MEDIA.WEIXIN;
    private String mShareUrl = TCConstants.SVR_LivePlayShare_URL;
    private String mCoverUrl = "";
    private String mTitle = ""; //标题

    //log相关
    protected boolean mShowLog = false;

    //录制相关
    private boolean mRecording = false;
    private ProgressBar mRecordProgress = null;
    private long mStartRecordTimeStamp = 0;

    private int mRecordType;
    private int mWidth, mHeight;
    private float mVideoScale;

    private int is_follow = 0;//是否关注主播
    private int like_number = 0;//获赞个数
    private int is_zan = 0;//是否点赞
    private String live_id;//直播间ID
    private boolean isGift = false;


    static class TXPhoneStateListener extends PhoneStateListener {
        WeakReference<TXLivePlayer> mPlayer;

        public TXPhoneStateListener(TXLivePlayer player) {
            mPlayer = new WeakReference<TXLivePlayer>(player);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            TXLivePlayer player = mPlayer.get();
            switch (state) {
                //电话等待接听
                case TelephonyManager.CALL_STATE_RINGING:
                    if (player != null) player.setMute(true);
                    break;
                //电话接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (player != null) player.setMute(true);
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    if (player != null) player.setMute(false);
                    break;
            }
        }
    }

    private PhoneStateListener mPhoneListener = null;

    /**
     * 观众加入房间操作
     */
    public void joinRoom() {
        //初始化消息回调，当前存在：获取文本消息、用户加入/退出消息、群组解散消息、点赞消息、弹幕消息回调
        mTCChatRoomMgr = TCChatRoomMgr.getInstance();
        mTCPlayerMgr = TCPlayerMgr.getInstance();
        mTCPlayerMgr.setPlayerListener(this);

        if (mIsLivePlay) {
            //仅当直播时才进行执行加入直播间逻辑
            mTCChatRoomMgr.setMessageListener(this);
            mTCChatRoomMgr.joinGroup(mGroupId);
            getGroupMembersList();
        }

        mTCPlayerMgr.enterGroup(mUserId, mPusherId, mIsLivePlay ? mGroupId : mFileId, mNickname, mHeadPic, mIsLivePlay ? 0 : 1);
        startPlay();
    }

    /**
     * 初始化观看点播界面
     */
    private void initVodView() {

        mImmersionBar = ImmersionBar.with(this);
//        mImmersionBar;   //所有子类都将继承这些相同的属性
        mImmersionBar.statusBarColor(R.color.transparent)
                .keyboardEnable(false).init();

        View toolBar = findViewById(R.id.tool_bar);
        if (toolBar != null) {
            toolBar.setVisibility(View.GONE);
        }

        //左上直播信息
        mControllLayer = (RelativeLayout) findViewById(R.id.rl_controllLayer);
        mtvPuserName = (TextView) findViewById(R.id.tv_broadcasting_time);
        mtvPuserName.setText(TCUtils.getLimitString(mPusherNickname, 10));
        mRecordBall = (ImageView) findViewById(R.id.iv_record_ball);
        mRecordBall.setVisibility(View.GONE);
        mHeadIcon = (ImageView) findViewById(R.id.iv_head_icon);
        mHeadIcon.setOnClickListener(this);
        showHeadIcon(mHeadIcon, mPusherAvatar);
        mMemberCount = (TextView) findViewById(R.id.tv_member_counts);

        //初始化观众列表
        mUserAvatarList = (RecyclerView) findViewById(R.id.rv_user_avatar);
        mUserAvatarList.setVisibility(View.VISIBLE);
        mAvatarListAdapter = new TCUserAvatarListAdapter(this, mPusherId);
        mUserAvatarList.setAdapter(mAvatarListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUserAvatarList.setLayoutManager(linearLayoutManager);

        mMemberCount.setText("观众:" + String.format(Locale.CHINA, "%d", mCurrentMemberCount));

//        View head = findViewById(R.id.layout_live_pusher_info);
//        if (head != null) {
//            head.setVisibility(View.GONE);
//        }
        View progressBar = findViewById(R.id.progressbar_container);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mTextProgress = (TextView) findViewById(R.id.progress_time);
        mPlayIcon = (ImageView) findViewById(R.id.play_btn);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean bFromUser) {
                if (mTextProgress != null) {
                    mTextProgress.setText(String.format(Locale.CHINA, "%02d:%02d:%02d/%02d:%02d:%02d", progress / 3600, (progress % 3600) / 60, (progress % 3600) % 60, seekBar.getMax() / 3600, (seekBar.getMax() % 3600) / 60, (seekBar.getMax() % 3600) % 60));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mStartSeek = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mTXLivePlayer.seek(seekBar.getProgress());
                mTrackingTouchTS = System.currentTimeMillis();
                mStartSeek = false;
            }
        });

        mBgImageView = (ImageView) findViewById(R.id.background);
    }

    /**
     * 初始化观看直播界面
     */
    protected void initLiveView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rl_play_root);

        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mTCSwipeAnimationController.processEvent(event);
            }
        });

        mControllLayer = (RelativeLayout) findViewById(R.id.rl_controllLayer);
        mTCSwipeAnimationController = new TCSwipeAnimationController(this);
        mTCSwipeAnimationController.setAnimationView(mControllLayer);

        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mListViewMsg = (ListView) findViewById(R.id.im_msg_listview);
        mListViewMsg.setVisibility(View.VISIBLE);
        mHeartLayout = (TCHeartLayout) findViewById(R.id.heart_layout);
        mtvPuserName = (TextView) findViewById(R.id.tv_broadcasting_time);
        mtvPuserName.setText(TCUtils.getLimitString(mPusherNickname, 10));
        mRecordBall = (ImageView) findViewById(R.id.iv_record_ball);
        mRecordBall.setVisibility(View.GONE);

        mUserAvatarList = (RecyclerView) findViewById(R.id.rv_user_avatar);
        mUserAvatarList.setVisibility(View.VISIBLE);
        mAvatarListAdapter = new TCUserAvatarListAdapter(this, mPusherId);
        mUserAvatarList.setAdapter(mAvatarListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUserAvatarList.setLayoutManager(linearLayoutManager);

        mInputTextMsgDialog = new TCInputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);

        mHeadIcon = (ImageView) findViewById(R.id.iv_head_icon);
        showHeadIcon(mHeadIcon, mPusherAvatar);
        mMemberCount = (TextView) findViewById(R.id.tv_member_counts);

        mCurrentMemberCount++;
        mMemberCount.setText("观众:" + String.format(Locale.CHINA, "%d", mCurrentMemberCount));
        mChatMsgListAdapter = new TCChatMsgListAdapter(this, mListViewMsg, mArrayListChatEntity);
        mListViewMsg.setAdapter(mChatMsgListAdapter);

        mDanmuView = (IDanmakuView) findViewById(R.id.danmakuView);
        mDanmuView.setVisibility(View.VISIBLE);
        mDanmuMgr = new TCDanmuMgr(this);
        mDanmuMgr.setDanmakuView(mDanmuView);

        mBgImageView = (ImageView) findViewById(R.id.background);
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();

        mPusherId = intent.getStringExtra(TCConstants.PUSHER_ID);
        mPlayUrl = intent.getStringExtra(TCConstants.PLAY_URL);
        mGroupId = intent.getStringExtra(TCConstants.GROUP_ID);
        live_id = intent.getStringExtra(TCConstants.LIVE_Id);
        mPlayType = intent.getIntExtra(TCConstants.PLAY_TYPE, 0);
        mIsLivePlay = (mPlayType == 0);   //0表示是直播
        mPusherNickname = intent.getStringExtra(TCConstants.PUSHER_NAME);
        mPusherAvatar = intent.getStringExtra(TCConstants.PUSHER_AVATAR);
        mHeartCount = Long.decode(intent.getStringExtra(TCConstants.HEART_COUNT));
        mCurrentMemberCount = Long.decode(intent.getStringExtra(TCConstants.MEMBER_COUNT));
        mFileId = intent.getStringExtra(TCConstants.FILE_ID);
        is_follow = intent.getIntExtra(TCConstants.PUSHER_FOLLOW, 0);
        like_number = intent.getIntExtra(TCConstants.LIKE_NUMBER, 0);
        is_zan = intent.getIntExtra(TCConstants.IS_LIKE, 0);
//        mTimeStamp = intent.getIntExtra(TCConstants.TIMESTAMP, 0);
        mTitle = intent.getStringExtra(TCConstants.ROOM_TITLE);

//        mUserId = TCUserInfoMgr.getInstance().getUserId();
//        mNickname = TCUserInfoMgr.getInstance().getNickname();
//        mHeadPic = TCUserInfoMgr.getInstance().getHeadPic();
        if (is_follow == 1) {
            tv_follow.setText("取消关注");
        } else {
            tv_follow.setText("关注");
        }
        tv_like_number.setText(like_number + "");
        mUserId = UserInfoUtils.getUid();
        mNickname = UserInfoUtils.getUserName();
        mHeadPic = UserInfoUtils.getAvatar();

        mRecordType = intent.getIntExtra(TCConstants.VIDEO_RECORD_TYPE, 0);
        if (mIsLivePlay) {
            initLiveView();
        } else {
            initVodView();
        }
    }

    @Override
    protected void logic() {

        if (mRecordType != TCConstants.VIDEO_RECORD_TYPE_UGC_RECORD) {
            mBgImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            try {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(mPlayUrl, new HashMap<String, String>());
                String widthStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String heightStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                mediaMetadataRetriever.release();
                if (!TextUtils.isEmpty(widthStr) && !TextUtils.isEmpty(heightStr)) {
                    mWidth = Integer.parseInt(widthStr);
                    mHeight = Integer.parseInt(heightStr);
                    if (mWidth != 0 && mHeight != 0) {
                        mVideoScale = mHeight / (float) mWidth;
                    }
                    if (mVideoScale != 1 && (mVideoScale < 1.3f || mVideoScale > 1.4f)) {
                        mBgImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                mBgImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }

        joinRoom();

        mCoverUrl = getIntent().getStringExtra(TCConstants.COVER_PIC);
        TCUtils.blurBgPic(this, mBgImageView, mCoverUrl, R.drawable.bg);

        initSharePara();
        mPhoneListener = new TXPhoneStateListener(mTXLivePlayer);
        TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        //在这里停留，让列表界面卡住几百毫秒，给sdk一点预加载的时间，形成秒开的视觉效果
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        initData();
    }


//    private void initData() {
//
//    }


    /**
     * 加载主播头像
     *
     * @param view   view
     * @param avatar 头像链接
     */
    private void showHeadIcon(ImageView view, String avatar) {
        TCUtils.showPicWithUrl(this, view, avatar, R.drawable.head_me);
    }

    private boolean checkPlayUrl() {
        if (TextUtils.isEmpty(mPlayUrl) || (!mPlayUrl.startsWith("http://") && !mPlayUrl.startsWith("https://") && !mPlayUrl.startsWith("rtmp://"))) {
            ToastUtils.showToast("播放地址不合法，目前仅支持rtmp,flv,hls,mp4播放方式!");
            return false;
        }
        if (mIsLivePlay) {
            if (mPlayUrl.startsWith("rtmp://")) {
                mUrlPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
            } else if ((mPlayUrl.startsWith("http://") || mPlayUrl.startsWith("https://")) && mPlayUrl.contains(".flv")) {
                mUrlPlayType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
            } else {
                ToastUtils.showToast("播放地址不合法，直播目前仅支持rtmp,flv播放方式!");
                return false;
            }
        } else {
            if (mPlayUrl.startsWith("http://") || mPlayUrl.startsWith("https://")) {
                if (mPlayUrl.contains(".flv")) {
                    mUrlPlayType = TXLivePlayer.PLAY_TYPE_VOD_FLV;
                } else if (mPlayUrl.contains(".m3u8")) {
                    mUrlPlayType = TXLivePlayer.PLAY_TYPE_VOD_HLS;
                } else if (mPlayUrl.toLowerCase().contains(".mp4")) {
                    mUrlPlayType = TXLivePlayer.PLAY_TYPE_VOD_MP4;
                } else {
                    ToastUtils.showToast("播放地址不合法，点播目前仅支持flv,hls,mp4播放方式!");
                    return false;
                }
            } else {
                ToastUtils.showToast("播放地址不合法，点播目前仅支持flv,hls,mp4播放方式!");
                return false;
            }
        }
        return true;
    }

    public String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 开始播放
     */
    protected void startPlay() {
        if (!checkPlayUrl()) {
            return;
        }

        if (mTXLivePlayer == null) {
            mTXLivePlayer = new TXLivePlayer(this);
        }
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.clearLog();
        }
        if (mPlayType == 2) {
            Log.d(TAG, "Set cache");
            mTXPlayConfig.setCacheFolderPath(getInnerSDCardPath() + "/xzbcache");
            mTXPlayConfig.setMaxCacheItems(3);
        }
        mBgImageView.setVisibility(View.VISIBLE);
        mTXLivePlayer.setPlayerView(mTXCloudVideoView);
        mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        if (mRecordType == TCConstants.VIDEO_RECORD_TYPE_UGC_RECORD) {
            if (mWidth != 0 && mHeight != 0) {
                if (mVideoScale == 1 || (mVideoScale > 1.3f && mVideoScale < 1.4f)) {
                    mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
                } else {
                    mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
                }
            } else {
                mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
            }
        } else {
            mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        }
        mTXLivePlayer.setPlayListener(this);
        mTXLivePlayer.setConfig(mTXPlayConfig);

        int result;
        result = mTXLivePlayer.startPlay(mPlayUrl, mUrlPlayType);

        if (0 != result) {
            Intent rstData = new Intent();
            if (-1 == result) {
                Log.d(TAG, TCConstants.ERROR_MSG_NOT_QCLOUD_LINK);
//                Toast.makeText(getApplicationContext(), TCConstants.ERROR_MSG_NOT_QCLOUD_LINK, Toast.LENGTH_SHORT).show();
                rstData.putExtra(TCConstants.ACTIVITY_RESULT, TCConstants.ERROR_MSG_NOT_QCLOUD_LINK);
            } else {
                Log.d(TAG, TCConstants.ERROR_RTMP_PLAY_FAILED);
//                Toast.makeText(getApplicationContext(), TCConstants.ERROR_RTMP_PLAY_FAILED, Toast.LENGTH_SHORT).show();
                rstData.putExtra(TCConstants.ACTIVITY_RESULT, TCConstants.ERROR_MSG_NOT_QCLOUD_LINK);
            }
            mTXCloudVideoView.onPause();
            stopPlay(true);
            setResult(RESULT_OK, rstData);
            finish();
        } else {
            mPlaying = true;
        }
    }

    /**
     * 停止播放
     *
     * @param clearLastFrame
     */
    protected void stopPlay(boolean clearLastFrame) {
        if (mTXLivePlayer != null) {
            mTXLivePlayer.setPlayListener(null);
            mTXLivePlayer.stopPlay(clearLastFrame);
            mPlaying = false;
        }
    }

    /**
     * 发消息弹出框
     */
    private void showInputMsgDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();

        lp.width = (display.getWidth()); //设置宽度
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.setCancelable(true);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }

    @Override
    protected void showErrorAndQuit(String errorMsg) {

        mTXCloudVideoView.onPause();
        stopPlay(true);
        Intent rstData = new Intent();
        rstData.putExtra(TCConstants.ACTIVITY_RESULT, errorMsg);
        setResult(RESULT_OK, rstData);
        super.showErrorAndQuit(errorMsg);

    }

    @Override
    public void onReceiveExitMsg() {
        super.onReceiveExitMsg();
        TXLog.d(TAG, "player broadcastReceiver receive exit app msg");
        //在被踢下线的情况下，执行退出前的处理操作：关闭rtmp连接、退出群组
        mTXCloudVideoView.onPause();
        stopPlay(true);
//        quitRoom();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                closeLive();
                break;
            case R.id.btn_like:
                if (mHeartLayout != null) {
                    mHeartLayout.addFavor();
                }
                //点赞发送请求限制
                if (mLikeFrequeControl == null) {
                    mLikeFrequeControl = new TCFrequeControl();
                    mLikeFrequeControl.init(2, 1);
                }
                requestZan();
                break;
            case R.id.btn_gift:
                isGift = true;
                Intent intent2 = new Intent();
                intent2.setClass(this, GiftActivity.class);
                intent2.putExtra("userId", mPusherId);
                intent2.putExtra("type", 3);
                intent2.putExtra("info_id", mPusherId);
                startActivityForResult(intent2, 1002);
                overridePendingTransition(R.anim.enter_bottom, R.anim.exit_bottom);
                break;
            case R.id.btn_message_input:
                showInputMsgDialog();
                break;
            case R.id.play_btn: {
                if (mPlaying) {
                    if (mVideoPause) {
                        mTXLivePlayer.resume();
                        if (mPlayIcon != null) {
                            mPlayIcon.setBackgroundResource(R.drawable.play_pause);
                        }
                    } else {
                        mTXLivePlayer.pause();
                        if (mPlayIcon != null) {
                            mPlayIcon.setBackgroundResource(R.drawable.play_start);
                        }
                    }
                    mVideoPause = !mVideoPause;
                } else {
                    if (mPlayIcon != null) {
                        mPlayIcon.setBackgroundResource(R.drawable.play_pause);
                    }
                    startPlay();
                }

            }
            break;
            case R.id.btn_share:
            case R.id.btn_vod_share:
                showShareDialog();
                break;
//            case R.id.btn_log://显示log
//            case R.id.btn_vod_log:
//                showLog();
//                break;
//            case R.id.btn_record://录屏
//                showRecordUI();
//                break;
            case R.id.record:
                switchRecord();
                break;
            case R.id.retry_record:
                retryRecord();
                break;
            case R.id.close_record:
                closeRecord();
                break;
            case R.id.live_follow:
                requestFollow();
                break;
            case R.id.iv_head_icon:
                Intent intent = new Intent();
                intent.setClass(this, PersonalPageActivity.class);
                intent.putExtra("uid", mPusherId);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGift) {
            mTXCloudVideoView.onResume();
            if (!mIsLivePlay && !mVideoPause) {
                mTXLivePlayer.resume();
            } else {
                if (mDanmuMgr != null) {
                    mDanmuMgr.resume();
                }
                if (mPausing) {
                    mPausing = false;
                    startPlay();
                }
            }
        }
        isGift = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isGift) {
            mTXCloudVideoView.onPause();
            if (!mIsLivePlay) {
                mTXLivePlayer.pause();
            } else {
                if (mDanmuMgr != null) {
                    mDanmuMgr.pause();
                }
                mPausing = true;
                stopPlay(false);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRecord(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDanmuMgr != null) {
            mDanmuMgr.destroy();
            mDanmuMgr = null;
        }
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onDestroy();
            mTXCloudVideoView = null;
        }

        stopPlay(true);
        quitRoom();
        mTXLivePlayer = null;

        TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
        mPhoneListener = null;

    }

    /**
     * 退出直播间
     */
    public void quitRoom() {
        if (mIsLivePlay) {
            mTCChatRoomMgr.quitGroup(mGroupId);
            mTCChatRoomMgr.removeMsgListener();
            mTCPlayerMgr.quitGroup(mUserId, mPusherId, mGroupId, 0);
        } else {
            mTCPlayerMgr.quitGroup(mUserId, mPusherId, mFileId, 1);
        }
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {
//        if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
////            stopLoadingAnimation();
//        } else
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
            if (mTextProgress != null) {
                mTextProgress.setText(String.format(Locale.CHINA, "%02d:%02d:%02d/%02d:%02d:%02d", progress / 3600, (progress % 3600) / 60, progress % 60, duration / 3600, (duration % 3600) / 60, duration % 60));
            }

            if (mSeekBar != null) {
                mSeekBar.setMax(duration);
            }
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
            showErrorAndQuit(TCConstants.ERROR_MSG_NET_DISCONNECTED);
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            stopPlay(false);
            mVideoPause = false;
            if (mTextProgress != null) {
                mTextProgress.setText(String.format(Locale.CHINA, "%s", "00:00:00/00:00:00"));
            }
            if (mSeekBar != null) {
                mSeekBar.setProgress(0);
            }
            if (mPlayIcon != null) {
                mPlayIcon.setBackgroundResource(R.drawable.play_start);
            }
            mBgImageView.setVisibility(View.VISIBLE);
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {
            mBgImageView.setVisibility(View.GONE);
        }
//        else if (event == TXLiveConstants.PLAY_EVT_PLAY_LOADING) {
//            startLoadingAnimation();
//        }
    }

    @Override
    public void onNetStatus(Bundle status) {
        Log.d(TAG, "Current status: " + status.toString());
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.setLogText(status, null, 0);
        }

        if (status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH) > status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)) {
            if (mTXLivePlayer != null)
                mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
        } else if (mTXLivePlayer != null)
            mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
    }

    private void notifyMsg(final TCChatEntity entity) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
//                if(entity.getType() == TCConstants.PRAISE) {
//                    if(mArrayListChatEntity.contains(entity))
//                        return;
//                }
                if (mArrayListChatEntity.size() > 1000) {
                    while (mArrayListChatEntity.size() > 900) {
                        mArrayListChatEntity.remove(0);
                    }
                }

                mArrayListChatEntity.add(entity);
                mChatMsgListAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 加入群组/退出群组回调
     *
     * @param errCode 错误码
     */
    @Override
    public void onRequestCallback(int errCode) {
        if (errCode != 0) {
            if (TCConstants.ERROR_GROUP_NOT_EXIT == errCode) {
                showErrorAndQuit(TCConstants.ERROR_MSG_GROUP_NOT_EXIT);
            } else if (TCConstants.ERROR_QALSDK_NOT_INIT == errCode) {
                ((V3Application) getApplication()).initImSdk();
                joinRoom();
            } else {
                showErrorAndQuit(TCConstants.ERROR_MSG_JOIN_GROUP_FAILED + errCode);
            }
        } else {
            if (null != mTXLivePlayer) {
                getGroupMembersList();
            }
        }
    }

    @Override
    public void onJoinGroupCallback(int code, String msg) {
        if (code == 0) {
            Log.d(TAG, "onJoin group success" + msg);
        } else if (TCConstants.NO_LOGIN_CACHE == code) {
            TXLog.d(TAG, "onJoin group failed" + msg);
            showErrorAndQuit(TCConstants.ERROR_MSG_NO_LOGIN_CACHE);
        } else {
            TXLog.d(TAG, "onJoin group failed" + msg);
            showErrorAndQuit(TCConstants.ERROR_MSG_JOIN_GROUP_FAILED + code);
        }
    }

    public void onSendMsgCallback(int errCode, TIMMessage timMessage) {
        //消息发送成功后回显
        if (errCode == 0) {
            TIMElemType elemType = timMessage.getElement(0).getType();
            if (elemType == TIMElemType.Text) {
                Log.d(TAG, "onSendTextMsgsuccess:" + errCode);
            } else if (elemType == TIMElemType.Custom) {
                //custom消息存在消息回调,此处显示成功失败
                Log.d(TAG, "onSendCustomMsgsuccess:" + errCode);
            }
        } else {
            Log.d(TAG, "onSendMsgfail:" + errCode);
        }

    }

    @Override
    public void onReceiveMsg(int type, TCSimpleUserInfo userInfo, String content) {
        //TODO
        switch (type) {
            case TCConstants.IMCMD_ENTER_LIVE:
                handleMemberJoinMsg(userInfo);
                break;
            case TCConstants.IMCMD_EXIT_LIVE:
                handleMemberQuitMsg(userInfo);
                break;
            case TCConstants.IMCMD_PRAISE:
                handlePraiseMsg(userInfo, content);
                break;
            case TCConstants.IMCMD_PAILN_TEXT:
                handleTextMsg(userInfo, content);
                break;
            case TCConstants.IMCMD_DANMU:
                handleDanmuMsg(userInfo, content);
                break;
            case TCConstants.LIVE_GIFT:
                handleLiveGift(userInfo, content);
                break;
            default:
                break;
        }
    }

    /**
     * 直播接收到礼物处理
     *
     * @param userInfo
     * @param content
     */
    private void handleLiveGift(TCSimpleUserInfo userInfo, String content) {
        Gson gson = V3Application.getGson();
        LiveGiftBean liveGiftBean = gson.fromJson(content, LiveGiftBean.class);

        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName(userInfo.nickname);
        entity.setContext("送了一个礼物:" + liveGiftBean.getGiftName());
        entity.setType(TCConstants.LIVE_GIFT);
        notifyMsg(entity);

        img_gift.setVisibility(View.VISIBLE);
        x.image().bind(img_gift, liveGiftBean.getGiftUrl(), MImageOptions.getNormalImageOptions());

        new Handler().postDelayed(new Runnable() {
            public void run() {
                img_gift.setVisibility(View.GONE);
            }
        }, 1000);

    }

    /**
     * 有人加入直播处理
     *
     * @param userInfo
     */
    public void handleMemberJoinMsg(TCSimpleUserInfo userInfo) {

        ToastUtils.showToast("有用户进来了:" + userInfo.userid + "," + userInfo.nickname);

        //更新头像列表 返回false表明已存在相同用户，将不会更新数据
        if (!mAvatarListAdapter.addItem(userInfo))
            return;
        mCurrentMemberCount++;
        mTotalMemberCount++;
        mMemberCount.setText("观众:" + String.format(Locale.CHINA, "%d", mCurrentMemberCount));

        //左下角显示用户加入消息
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("通知");
        if (userInfo.nickname.equals(""))
            entity.setContext(userInfo.userid + "加入直播");
        else
            entity.setContext(userInfo.nickname + "加入直播");
        entity.setType(TCConstants.MEMBER_ENTER);
        notifyMsg(entity);
    }

    /**
     * 有人退出直播处理
     *
     * @param userInfo
     */
    public void handleMemberQuitMsg(TCSimpleUserInfo userInfo) {

        if (mCurrentMemberCount > 0 && mTotalMemberCount > 0) {
            mTotalMemberCount--;
            mCurrentMemberCount--;
        } else
            Log.d(TAG, "接受多次退出请求，目前人数为负数");

        mMemberCount.setText("观众:" + String.format(Locale.CHINA, "%d", mCurrentMemberCount));

        mAvatarListAdapter.removeItem(userInfo.userid);

        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("通知");
        if (userInfo.nickname.equals(""))
            entity.setContext(userInfo.userid + "退出直播");
        else
            entity.setContext(userInfo.nickname + "退出直播");
        entity.setType(TCConstants.MEMBER_EXIT);
        notifyMsg(entity);
    }


    @Override
    public void onGroupDelete() {
        showErrorAndQuit(TCConstants.ERROR_MSG_LIVE_STOPPED);
    }

    /**
     * 点赞处理
     *
     * @param userInfo
     * @param msg
     */
    public void handlePraiseMsg(TCSimpleUserInfo userInfo, String msg) {
        if (TextUtils.equals(msg, TCConstants.ZAN)) {
            TCChatEntity entity = new TCChatEntity();
            entity.setSenderName("通知");
            if (userInfo.nickname.equals(""))
                entity.setContext(userInfo.userid + "点了个赞");
            else
                entity.setContext(userInfo.nickname + "点了个赞");
            if (mHeartLayout != null) {
                mHeartLayout.addFavor();
            }
            mHeartCount++;
            entity.setType(TCConstants.MEMBER_ENTER);
            notifyMsg(entity);
        } else {
            mHeartCount--;
        }
    }

    /**
     * 弹幕处理
     *
     * @param userInfo
     * @param text
     */
    public void handleDanmuMsg(TCSimpleUserInfo userInfo, String text) {
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName(userInfo.nickname);
        entity.setContext(text);
        entity.setType(TCConstants.TEXT_TYPE);

        notifyMsg(entity);
        if (mDanmuMgr != null) {
            mDanmuMgr.addDanmu(userInfo.headpic, userInfo.nickname, text);
        }
    }

    /**
     * 普通消息处理
     *
     * @param userInfo
     * @param text
     */
    public void handleTextMsg(TCSimpleUserInfo userInfo, String text) {
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName(userInfo.nickname);
        entity.setContext(text);
        entity.setType(TCConstants.TEXT_TYPE);

        notifyMsg(entity);
    }

    /**
     * 拉取用户头像列表
     */
    public void getGroupMembersList() {
        mTCPlayerMgr.fetchGroupMembersList(mPusherId, mIsLivePlay ? mGroupId : mFileId, mPageNum, 20,
                new TCPlayerMgr.OnGetMembersListener() {
                    @Override
                    public void onGetMembersList(int retCode, int totalCount, List<TCSimpleUserInfo> membersList) {
                        if (retCode == 0) {
                            mTotalMemberCount = totalCount;
                            mCurrentMemberCount = totalCount;
                            mMemberCount.setText("观众:" + mTotalMemberCount);
                            for (TCSimpleUserInfo userInfo : membersList)
                                mAvatarListAdapter.addItem(userInfo);
                        } else {
                            TXLog.d(TAG, "getGroupMembersList failed");
                        }
                    }
                });
    }

    /**
     * TextInputDialog发送回调
     *
     * @param msg       文本信息
     * @param danmuOpen 是否打开弹幕
     */
    @Override
    public void onTextSend(String msg, boolean danmuOpen) {
        if (msg.length() == 0)
            return;
        try {
            byte[] byte_num = msg.getBytes("utf8");
            if (byte_num.length > 160) {
                Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        //消息回显
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("我:");
        entity.setContext(msg);
        entity.setType(TCConstants.TEXT_TYPE);
        notifyMsg(entity);
        if (danmuOpen) {
            if (mDanmuMgr != null) {
                mDanmuMgr.addDanmu(mHeadPic, mNickname, msg);
            }
            mTCChatRoomMgr.sendDanmuMessage(msg);
        } else {
            mTCChatRoomMgr.sendTextMessage(msg);
        }
    }

    private void initSharePara() {
        try {
            mShareUrl = mShareUrl + "?sdkappid=" + java.net.URLEncoder.encode(String.valueOf(TCConstants.IMSDK_APPID), "utf-8")
                    + "&acctype=" + java.net.URLEncoder.encode(String.valueOf(TCConstants.IMSDK_ACCOUNT_TYPE), "utf-8")
                    + "&userid=" + java.net.URLEncoder.encode(mPusherId, "utf-8")
                    + "&type=" + java.net.URLEncoder.encode(String.valueOf(mPlayType), "utf-8")
                    + "&fileid=" + java.net.URLEncoder.encode(String.valueOf(mFileId), "utf-8")
                    + "&ts=" + java.net.URLEncoder.encode(String.valueOf(mTimeStamp), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        if (mCoverUrl == null || mCoverUrl.isEmpty()) {
//            mImage = new UMImage(TCLivePlayerActivity.this.getApplicationContext(), R.drawable.bg);
//        } else {
//            mImage = new UMImage(TCLivePlayerActivity.this.getApplicationContext(), mCoverUrl);
//        }
    }

    /**
     * 展示分享界面
     */
    private void showShareDialog() {

//        View view = getLayoutInflater().inflate(R.layout.share_dialog, null);
//        final AlertDialog mDialog = new AlertDialog.Builder(this, R.style.ConfirmDialogStyle).create();
//        mDialog.show();// 显示创建的AlertDialog，并显示，必须放在Window设置属性之前
//
//        Window window = mDialog.getWindow();
//        if (window != null) {
//            window.setContentView(view);//这一步必须指定，否则不出现弹窗
//            WindowManager.LayoutParams mParams = window.getAttributes();
//            mParams.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
//            mParams.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
//            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
//            window.setBackgroundDrawableResource(android.R.color.white);
//            window.setAttributes(mParams);
//        }
//
//        Button btn_wx = (Button) view.findViewById(R.id.btn_share_wx);
//        Button btn_circle = (Button) view.findViewById(R.id.btn_share_circle);
//        Button btn_qq = (Button) view.findViewById(R.id.btn_share_qq);
//        Button btn_qzone = (Button) view.findViewById(R.id.btn_share_qzone);
//        Button btn_wb = (Button) view.findViewById(R.id.btn_share_wb);
//        Button btn_cancle = (Button) view.findViewById(R.id.btn_share_cancle);
//
//        btn_wx.setOnClickListener(mShareBtnClickListen);
//        btn_circle.setOnClickListener(mShareBtnClickListen);
//        btn_qq.setOnClickListener(mShareBtnClickListen);
//        btn_qzone.setOnClickListener(mShareBtnClickListen);
//        btn_wb.setOnClickListener(mShareBtnClickListen);
//        btn_cancle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mDialog.dismiss();
//            }
//        });
    }

//    private View.OnClickListener mShareBtnClickListen = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            switch (view.getId()) {
//                case R.id.btn_share_wx:
//                    mShare_meidia = SHARE_MEDIA.WEIXIN;
//                    break;
//                case R.id.btn_share_circle:
//                    mShare_meidia = SHARE_MEDIA.WEIXIN_CIRCLE;
//                    break;
//                case R.id.btn_share_qq:
//                    mShare_meidia = SHARE_MEDIA.QQ;
//                    break;
//                case R.id.btn_share_qzone:
//                    mShare_meidia = SHARE_MEDIA.QZONE;
//                    break;
//                case R.id.btn_share_wb:
//                    mShare_meidia = SHARE_MEDIA.SINA;
//                    break;
//                default:
//                    break;
//            }
//
//            ShareAction shareAction = new ShareAction(TCLivePlayerActivity.this);
//
//            UMWeb web = new UMWeb(mShareUrl);
//            web.setThumb(mImage);
//            web.setTitle(mTitle);
//            shareAction.withMedia(web);
//            shareAction.withText(mtvPuserName.getText() + "正在直播");
//            shareAction.setCallback(umShareListener);
//            shareAction.setPlatform(mShare_meidia).share();
//        }
//    };

//    private UMShareListener umShareListener = new UMShareListener() {
//        @Override
//        public void onStart(SHARE_MEDIA platform) {
//            Log.d("plat", "platform" + platform);
//        }
//
//        @Override
//        public void onResult(SHARE_MEDIA platform) {
//            Log.d("plat", "platform" + platform);
//            Toast.makeText(TCLivePlayerActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onError(SHARE_MEDIA platform, Throwable t) {
//            Toast.makeText(TCLivePlayerActivity.this, "分享失败" + t.getMessage(), Toast.LENGTH_LONG).show();
//        }
//
//        @Override
//        public void onCancel(SHARE_MEDIA platform) {
//            Toast.makeText(TCLivePlayerActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
//        }
//    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
//        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
//        com.umeng.socialize.utils.Log.d("result", "onActivityResult");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1002:
                    if (data == null) return;
                    GiftBean giftBean = (GiftBean) data.getSerializableExtra("gift");
                    sendGift(giftBean);
                    break;
            }
        }
    }

    protected void showLog() {
        mShowLog = !mShowLog;
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.disableLog(!mShowLog);
        }
        ImageView liveLog = (ImageView) findViewById(R.id.btn_log);
        ImageView vodLog = (ImageView) findViewById(R.id.btn_vod_log);
        if (mShowLog) {
            if (liveLog != null) liveLog.setBackgroundResource(R.drawable.icon_log_on);
            if (vodLog != null) vodLog.setBackgroundResource(R.drawable.icon_log_on);

        } else {
            if (liveLog != null) liveLog.setBackgroundResource(R.drawable.icon_log_off);
            if (vodLog != null) vodLog.setBackgroundResource(R.drawable.icon_log_off);
        }
    }

    protected void showRecordUI() {
        View tool = (View) findViewById(R.id.tool_bar);
        if (tool != null) {
            tool.setVisibility(View.GONE);
        }
        View rcord = (View) findViewById(R.id.record_layout);
        if (rcord != null) {
            rcord.setVisibility(View.VISIBLE);
        }
        if (mDanmuView != null) {
            mDanmuView.setVisibility(View.GONE);
        }
        if (mUserAvatarList != null) {
            mUserAvatarList.setVisibility(View.GONE);
        }
        if (mHeartLayout != null) {
            mHeartLayout.setVisibility(View.GONE);
        }
        if (mListViewMsg != null) {
            mListViewMsg.setVisibility(View.GONE);
        }

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rl_play_root);
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return v.onTouchEvent(event);
            }
        });
    }

    public void hideRecordUI() {
        View tool = (View) findViewById(R.id.tool_bar);
        if (tool != null) {
            tool.setVisibility(View.VISIBLE);
        }
        if (mRecordProgress != null) {
            mRecordProgress.setProgress(0);
        }
        View rcord = (View) findViewById(R.id.record_layout);
        if (rcord != null) {
            rcord.setVisibility(View.GONE);
        }

        if (mDanmuView != null) {
            mDanmuView.setVisibility(View.VISIBLE);
        }
        if (mUserAvatarList != null) {
            mUserAvatarList.setVisibility(View.VISIBLE);
        }
        if (mHeartLayout != null) {
            mHeartLayout.setVisibility(View.VISIBLE);
        }
        if (mListViewMsg != null) {
            mListViewMsg.setVisibility(View.VISIBLE);
        }

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rl_play_root);
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mTCSwipeAnimationController != null) {
                    return mTCSwipeAnimationController.processEvent(event);
                } else {
                    return v.onTouchEvent(event);
                }
            }
        });
    }

    private void switchRecord() {
        if (mRecording) {
            stopRecord(true);
        } else {
            startRecord();
        }
    }

    private void retryRecord() {
        if (mRecording) {
            if (mTXLivePlayer != null) {
                mTXLivePlayer.setVideoRecordListener(null);

            }
            if (mTXLivePlayer != null) {
                mTXLivePlayer.stopRecord();
            }
            ImageView liveRecord = (ImageView) findViewById(R.id.record);
            if (liveRecord != null) liveRecord.setBackgroundResource(R.drawable.start_record);
            mRecording = false;

            if (mRecordProgress != null) {
                mRecordProgress.setProgress(0);
            }
        }
    }

    private void closeRecord() {
        if (mRecording && mTXLivePlayer != null) {
            mTXLivePlayer.stopRecord();
            mTXLivePlayer.setVideoRecordListener(null);
        }
        hideRecordUI();

        ImageView liveRecord = (ImageView) findViewById(R.id.record);
        if (liveRecord != null) liveRecord.setBackgroundResource(R.drawable.start_record);
        mRecording = false;
    }

    private void stopRecord(boolean showToast) {
        // 录制时间要大于5s
        if (System.currentTimeMillis() <= mStartRecordTimeStamp + 5 * 1000) {
            if (showToast) {
                showTooShortToast();
                return;
            } else {
                if (mTXLivePlayer != null) {
                    mTXLivePlayer.setVideoRecordListener(null);
                }
            }
        }
        if (mTXLivePlayer != null) {
            mTXLivePlayer.stopRecord();
        }
        ImageView liveRecord = (ImageView) findViewById(R.id.record);
        if (liveRecord != null) liveRecord.setBackgroundResource(R.drawable.start_record);
        mRecording = false;
    }

    private void startRecord() {
        mRecordProgress = (ProgressBar) findViewById(R.id.record_progress);

        mTXLivePlayer.setVideoRecordListener(this);
        mTXLivePlayer.startRecord(TXRecordCommon.RECORD_TYPE_STREAM_SOURCE);
        ImageView liveRecord = (ImageView) findViewById(R.id.record);
        if (liveRecord != null) liveRecord.setBackgroundResource(R.drawable.stop_record);
        mRecording = true;
        mStartRecordTimeStamp = System.currentTimeMillis();
    }

    private void showTooShortToast() {
        if (mRecordProgress != null) {
            int statusBarHeight = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }

            int[] position = new int[2];
            mRecordProgress.getLocationOnScreen(position);
            Toast toast = Toast.makeText(this, "至少录到这里", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, position[0], position[1] - statusBarHeight - 110);
            toast.show();
        }
    }

    @Override
    public void onRecordEvent(int event, Bundle param) {

    }

    @Override
    public void onRecordProgress(long milliSecond) {
        if (mRecordProgress != null) {
            float progress = milliSecond / 60000.0f;
            mRecordProgress.setProgress((int) (progress * 100));
            if (milliSecond >= 60000.0f) {
                stopRecord(true);
            }
        }
    }

    @Override
    public void onRecordComplete(TXRecordCommon.TXRecordResult result) {
        Intent intent = new Intent(TCLivePlayerActivity.this.getApplicationContext(), TCVideoPublisherActivity.class);
        intent.putExtra(TCConstants.VIDEO_RECORD_TYPE, TCConstants.VIDEO_RECORD_TYPE_PLAY);
        intent.putExtra(TCConstants.VIDEO_RECORD_RESULT, result.retCode);
        intent.putExtra(TCConstants.VIDEO_RECORD_DESCMSG, result.descMsg);
        intent.putExtra(TCConstants.VIDEO_RECORD_VIDEPATH, result.videoPath);
        intent.putExtra(TCConstants.VIDEO_RECORD_COVERPATH, result.coverPath);
        startActivity(intent);
        hideRecordUI();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            closeLive();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 关闭直播
     */
    private void closeLive() {
        if (mIsLivePlay) {
            mTCChatRoomMgr.sendExitRoom();
            Intent rstData = new Intent();
            long memberCount = mCurrentMemberCount - 1;
            rstData.putExtra(TCConstants.MEMBER_COUNT, memberCount >= 0 ? memberCount : 0);
            rstData.putExtra(TCConstants.HEART_COUNT, mHeartCount);
            rstData.putExtra(TCConstants.PUSHER_ID, mPusherId);
            setResult(RESULT_OK, rstData);
        }
        finish();
    }

    /**
     * 点赞
     */
    private void requestZan() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.liveplay_add_upvote);
        params.addBodyParameter("user_id", mPusherId);
        params.addBodyParameter("follower_id", UserInfoUtils.getUid());
        params.addBodyParameter("live_id", live_id);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    if (is_zan == 0) {
                        is_zan = 1;
                        like_number++;
                        if (mLikeFrequeControl.canTrigger()) {
                            mHeartCount++;
                            //向后台发送点赞信息
                            mTCPlayerMgr.addHeartCount(mPusherId);
                            //向ChatRoom发送点赞消息
                            mTCChatRoomMgr.sendPraiseMessage(TCConstants.ZAN);
                        }
                    } else {
                        is_zan = 0;
                        like_number--;
                        if (mLikeFrequeControl.canTrigger()) {
                            mHeartCount--;
                            //向后台发送点赞信息
                            mTCPlayerMgr.addHeartCount(mPusherId);
                            //向ChatRoom发送点赞消息
                            mTCChatRoomMgr.sendPraiseMessage(TCConstants.CANCEL_ZAN);
                        }
                    }

                    tv_like_number.setText(like_number + "");

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
     * 关注或则取消关注
     */
    private void requestFollow() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.liveplay_add_guanzhu);
        params.addBodyParameter("member_id", mPusherId);
        params.addBodyParameter("follower_id", UserInfoUtils.getUid());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    if (is_follow == 1) {
                        ToastUtils.showToast("取消关注");
                        is_follow = 0;
                        tv_follow.setText("关注");
                    } else {
                        ToastUtils.showToast("关注成功");
                        is_follow = 1;
                        tv_follow.setText("取消关注");
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
     * 发送礼物
     *
     * @param giftBean
     */
    private void sendGift(GiftBean giftBean) {
        TCChatEntity entity = new TCChatEntity();
        entity.setSendId(UserInfoUtils.getUid());
        entity.setSenderName("我:");
        entity.setContext("送了" + giftBean.getName());
        entity.setType(TCConstants.LIVE_GIFT);
        notifyMsg(entity);

        LiveGiftBean liveGiftBean = new LiveGiftBean();
        liveGiftBean.setGiftName(giftBean.getName());
        liveGiftBean.setGiftUrl(giftBean.getImg_url());
        Gson gson = V3Application.getGson();
        String msg = gson.toJson(liveGiftBean);
        mTCChatRoomMgr.sendLiveGift(msg);

    }

}
