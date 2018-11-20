package com.huanglong.v3.activities.homepage;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.smallvideo.play.Pageradapter;
import com.huanglong.v3.smallvideo.play.TCVideoInfo;
import com.huanglong.v3.smallvideo.play.VideoCommentActivity;
import com.huanglong.v3.utils.Common;
import com.huanglong.v3.utils.PopShareUtils;
import com.huanglong.v3.utils.QQUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.utils.WXUtils;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.tauth.Tencent;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by bin on 2018/4/8.
 * 视频 推荐
 */

public class VHFragment extends BaseFragment implements ITXVodPlayListener {

    @ViewInject(R.id.vertical_view_pager)
    private VerticalViewPager mVerticalViewPager;
    @ViewInject(R.id.vertical_view_pager_lin)
    private LinearLayout view_pager_lin;

    private int PLAYER_NUM = 5;
    private Pageradapter mPagerAdapter;
    private TXCloudVideoView mTXCloudVideoView;
    private TextView mTvPublisherName;
    private CircleImageView mIvAvatar;
    private TextView mTvBack;

    private ImageView mIvCover;
    // 发布者id 、视频地址、 发布者名称、 头像URL、 封面URL
    private String mPusherId, mPlayUrl, mPusherName, mAvatarUrl, mCoverUrl;
    // URL的种类 根据url的路径来判断，是flv MP4 hls
    private int mUrlPlayType;


    private int mInitTCLiveInfoPosition = 0;
    private int mLastPosition = -1;
    private int mCurrentPosition;
    private int mCurrentPlayURLPosition = -1;
    private int currentIsZan = 0;
    private String video_id;

    private List<TCVideoInfo> mTCLiveInfoList;

    private ImageView icon_praise;
    private TextView tv_praise;
    private TextView tv_comm_num;
    private TextView tv_gift_num;
    private TextView tv_follow_num;
    private ImageView img_pause;
    private TextView tv_share_num;
    private LinearLayout lin_follow;
    /**
     * SDK播放器以及配置
     */
    private TXVodPlayer mTXVodPlayer;
    private List<VHFragment.PlayerInfo> mPlayerInfoList;

    private TXVodPlayConfig mTXPlayConfig;
    private String mFileId;
    private String mTimeStamp;

    private PhoneStateListener mPhoneListener = null;

    private String cid;
    private int PAGE = 1;

    private PopShareUtils popShareUtils;

    private boolean isClickComment = false;

    private WeChatShareBroadcast weChatBroadcastReceiver;


    class PlayerInfo {
        public TXVodPlayer txVodPlayer;
        public String playURL;
        public boolean isBegin;
    }


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_hot_tab, container, false);
        return view;
    }

    @Override
    protected void initView() {
        initViews();
        initPhoneListener();
        requestLiveList();
        initPop();
    }

    @Override
    protected void logic() {
        weChatBroadcastReceiver = new WeChatShareBroadcast();
        registerReceiver();
    }

    /**
     * 请求直播分类
     */
    public void requestLiveList() {
        showDialog();
        RequestParams params = MRequestParams.getNoTokenParams(Api.video_recommend_list);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    mTCLiveInfoList = gson.fromJson(json, new TypeToken<LinkedList<TCVideoInfo>>() {
                    }.getType());
                    if (mTCLiveInfoList != null && mTCLiveInfoList.size() > 0) {
                        if (mTCLiveInfoList.size() >= 5) {
                            PLAYER_NUM = 5;
                        } else {
                            PLAYER_NUM = mTCLiveInfoList.size();
                        }
                        video_id = mTCLiveInfoList.get(0).getId();
                        currentIsZan = mTCLiveInfoList.get(0).getIs_zan();
                        mPlayUrl = mTCLiveInfoList.get(0).getPlay_url();
                        initPlayerSDK();
                        mPagerAdapter.setData(mTCLiveInfoList);
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
                dismissDialog();

            }
        });
    }

    private void initViews() {
//        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.player_cloud_view);
//        mTvPublisherName = (TextView) findViewById(R.id.player_tv_publisher_name);
//        mIvAvatar = (CircleImageView) findViewById(R.id.player_civ_avatar);
//        mIvCover = (ImageView) findViewById(R.id.player_iv_cover);

//
//        if (mPusherName != null)
//            mTvPublisherName.setText(mPusherName);
//        TCUtils.blurBgPic(this, mIvCover, mCoverUrl, R.drawable.bg);
//        Glide.with(this).load(mAvatarUrl).error(R.drawable.face).into(mIvAvatar);


        mVerticalViewPager.setOffscreenPageLimit(2);
        mPagerAdapter = new Pageradapter(getActivity());
        mVerticalViewPager.setAdapter(mPagerAdapter);

        mVerticalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                TXLog.i(TAG, "mVerticalViewPager, onPageScrolled position = " + position);
//                mCurrentPosition = position;
            }

            @Override
            public void onPageSelected(int position) {
                img_pause.setVisibility(View.GONE);
//                TXLog.i(TAG, "mVerticalViewPager, who is first onPageSelected position = " + position);
                video_id = mTCLiveInfoList.get(position).getId();
                currentIsZan = mTCLiveInfoList.get(position).getIs_zan();
                mCurrentPosition = position;
                // 滑动界面，首先让之前的播放器暂停，并seek到0
                LogUtil.i("滑动后，让之前的播放器暂停，mTXVodPlayer = " + mTXVodPlayer);
                if (mTXVodPlayer != null) {
                    mTXVodPlayer.seek(0);
                    mTXVodPlayer.pause();
                }

                if (mCurrentPosition > mLastPosition) {
                    // 向上滑动
                    if (mLastPosition > 1 && mLastPosition < mTCLiveInfoList.size() - 3) {
                        LogUtil.i("向上滑动，需要移动播放器，mLastPosition = " + mLastPosition + ", mCurrentPosition = " + mCurrentPosition);
                        // 第一个位置的播放器移动到最后，用新的url预加载
                        VHFragment.PlayerInfo removedPlayerInfo = mPlayerInfoList.remove(0);
                        TCVideoInfo nextTCLiveInfo = mTCLiveInfoList.get(mCurrentPosition + 2);
                        String nextPlayerURL = nextTCLiveInfo.getPlay_url();//TextUtils.isEmpty(nextTCLiveInfo.hlsPlayUrl) ? nextTCLiveInfo.playurl : nextTCLiveInfo.hlsPlayUrl;
                        removedPlayerInfo.playURL = nextPlayerURL;
                        removedPlayerInfo.isBegin = false;
//                        if(checkPlayUrl(nextPlayerURL)){
//                            removedPlayerInfo.txVodPlayer.startPlay(nextPlayerURL);
//                        }
                        mPlayerInfoList.add(removedPlayerInfo);
                    }
                } else {
                    // 向下滑动
                    if (mLastPosition > 2 && mLastPosition < mTCLiveInfoList.size() - 2) {
                        LogUtil.i("向下滑动，需要移动播放器，mLastPosition = " + mLastPosition + ", mCurrentPosition = " + mCurrentPosition);
                        // 最后一个位置的播放器移动到最前面，用新的url预加载
                        VHFragment.PlayerInfo removedPlayerInfo = mPlayerInfoList.remove(PLAYER_NUM - 1);
                        TCVideoInfo previousTCLiveInfo = mTCLiveInfoList.get(mCurrentPosition - 2);
                        String previousPlayerURL = previousTCLiveInfo.getPlay_url();//TextUtils.isEmpty(previousTCLiveInfo.hlsPlayUrl) ? previousTCLiveInfo.playurl : previousTCLiveInfo.hlsPlayUrl;
                        removedPlayerInfo.playURL = previousPlayerURL;
                        removedPlayerInfo.isBegin = false;
                        mPlayerInfoList.add(0, removedPlayerInfo);
                    }
                }
                mLastPosition = mCurrentPosition;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mVerticalViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
//                TXLog.i(TAG, "mVerticalViewPager, who is first transformPage pisition = " + position);
                if (position != 0) {
                    return;
                }

                if (mCurrentPlayURLPosition == mCurrentPosition) {
                    LogUtil.i("mVerticalViewPager, mCurrentPlayURLPosition == mCurrentPosition = " + mCurrentPosition);
                    return;
                }

                ViewGroup viewGroup = (ViewGroup) page;
                // 让上一个视频的封面显示
//                if(mIvCover != null){
//                    mIvCover.setVisibility(View.VISIBLE);
//                }
//                mIvCover = (ImageView) viewGroup.findViewById(R.id.player_iv_cover);
                mTXCloudVideoView = (TXCloudVideoView) viewGroup.findViewById(R.id.player_cloud_view);
                mTXCloudVideoView.setBackgroundResource(R.mipmap.icon_video_ing);
                mIvCover = viewGroup.findViewById(R.id.player_cloud_ing);
                mIvCover.setVisibility(View.VISIBLE);
                icon_praise = viewGroup.findViewById(R.id.item_video_praise_icon);
                tv_praise = viewGroup.findViewById(R.id.item_video_praise);
                tv_comm_num = viewGroup.findViewById(R.id.item_video_comment);
                tv_gift_num = viewGroup.findViewById(R.id.item_video_gift);
                tv_follow_num = viewGroup.findViewById(R.id.item_video_follow);
                img_pause = viewGroup.findViewById(R.id.item_video_pause);
                tv_share_num = viewGroup.findViewById(R.id.item_video_share);
                viewGroup.findViewById(R.id.player_civ_avatar).setOnClickListener(praiseClick);
                viewGroup.findViewById(R.id.item_video_praise_lin).setOnClickListener(praiseClick);
                viewGroup.findViewById(R.id.item_video_comment_lin).setOnClickListener(praiseClick);
                viewGroup.findViewById(R.id.item_video_gift_lin).setOnClickListener(praiseClick);
                viewGroup.findViewById(R.id.item_video_share_lin).setOnClickListener(praiseClick);
                lin_follow = viewGroup.findViewById(R.id.item_video_follow_lin);
                lin_follow.setOnClickListener(praiseClick);
                mTXCloudVideoView.setOnClickListener(praiseClick);

                int playerPosition;

                if (mCurrentPlayURLPosition < 0) {
                    // 第一次进入界面播放
                    VHFragment.PlayerInfo playerInfo;
                    if (mInitTCLiveInfoPosition <= 2) {
                        playerPosition = mInitTCLiveInfoPosition;
                        playerInfo = mPlayerInfoList.get(playerPosition);
                    } else if (mInitTCLiveInfoPosition >= mTCLiveInfoList.size() - 3) {
                        playerPosition = PLAYER_NUM - (mTCLiveInfoList.size() - mInitTCLiveInfoPosition);
                        playerInfo = mPlayerInfoList.get(playerPosition);
                    } else {
                        playerPosition = 2;
                        playerInfo = mPlayerInfoList.get(playerPosition);
                    }

                    playerInfo.txVodPlayer.setPlayerView(mTXCloudVideoView);
                    if (checkPlayUrl(playerInfo.playURL)) {
                        playerInfo.txVodPlayer.startPlay(playerInfo.playURL);
                    }

//                    playerInfo.txVodPlayer.setVodListener(new ITXVodPlayListener() {
//                        @Override
//                        public void onPlayEvent(TXVodPlayer txVodPlayer, int i, Bundle bundle) {
//
//                        }
//
//                        @Override
//                        public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {
//
//                        }
//                    });


                    mTXVodPlayer = playerInfo.txVodPlayer;
                    mCurrentPlayURLPosition = mCurrentPosition;
                    LogUtil.i("第一次进入界面播放，当前播放器 mTXVodPlayer = " + mTXVodPlayer);
                    // 预加载下一个视频，暂时先加载后一个视频
                    if (mInitTCLiveInfoPosition < mTCLiveInfoList.size() - 1) {
                        VHFragment.PlayerInfo nextPlayerInfo = mPlayerInfoList.get(playerPosition + 1);
                        if (checkPlayUrl(nextPlayerInfo.playURL)) {
                            nextPlayerInfo.txVodPlayer.setAutoPlay(true);
                            nextPlayerInfo.txVodPlayer.startPlay(nextPlayerInfo.playURL);
                        }
                    }
                    return;
                }

                VHFragment.PlayerInfo playerInfo;
                if (mCurrentPosition <= 2) {
                    LogUtil.i("滑动后播放, 选择播放器位置 = " + mCurrentPosition);
                    playerPosition = mCurrentPosition;
                    playerInfo = mPlayerInfoList.get(playerPosition);
                } else if (mCurrentPosition >= mTCLiveInfoList.size() - 3) {
                    LogUtil.i("滑动后播放, 选择播放器位置 = " + (PLAYER_NUM - (mTCLiveInfoList.size() - mCurrentPosition)));
                    playerPosition = PLAYER_NUM - (mTCLiveInfoList.size() - mCurrentPosition);
                    playerInfo = mPlayerInfoList.get(playerPosition);
                } else {
                    // 取中间的播放器播放
                    LogUtil.i("滑动后播放, 选择中间播放器位置 = " + 2);
                    playerPosition = 2;
                    playerInfo = mPlayerInfoList.get(playerPosition);
                }
                playerInfo.txVodPlayer.setPlayerView(mTXCloudVideoView);
                LogUtil.i("playerInfo isBegin = " + playerInfo.isBegin + ", mTXVodPlayer = " + playerInfo.txVodPlayer);
                if (playerInfo.isBegin) {
                    playerInfo.txVodPlayer.resume();
                } else {
                    if (checkPlayUrl(playerInfo.playURL)) {
                        playerInfo.txVodPlayer.startPlay(playerInfo.playURL);
                    }
                }
                mTXVodPlayer = playerInfo.txVodPlayer;
                mPlayUrl = playerInfo.playURL;
                mCurrentPlayURLPosition = mCurrentPosition;
                // 预加载下一个视频，暂时先加载后一个视频
                if (mCurrentPosition < mTCLiveInfoList.size() - 1) {
                    VHFragment.PlayerInfo nextPlayerInfo = mPlayerInfoList.get(playerPosition + 1);
                    if (nextPlayerInfo.isBegin) {
                        return;
                    }
                    if (checkPlayUrl(nextPlayerInfo.playURL)) {
                        nextPlayerInfo.txVodPlayer.startPlay(nextPlayerInfo.playURL);
                    }
                }
            }
        });
    }

    /**
     * 初始化POP
     */
    private void initPop() {
        popShareUtils = new PopShareUtils(getActivity());
        popShareUtils.setOnClickListener(new PopShareUtils.OnClickListener() {
            @Override
            public void onClick(int type) {
                //TODO
//                registerShareReceiver();
                String shareUrl = Api.share_video_url.replace("ID", mTCLiveInfoList.get(mCurrentPosition).getId());
                if (type == PopShareUtils.WECHAT_CIRCLE) {
                    WXUtils.shareWeChat(getActivity(), shareUrl, "小视频", mTCLiveInfoList.get(mCurrentPosition).getContent(), true, mTCLiveInfoList.get(mCurrentPosition).getCover_img());
                } else if (type == PopShareUtils.WECHAT_FRIENDS) {
                    WXUtils.shareWeChat(getActivity(), shareUrl, "小视频", mTCLiveInfoList.get(mCurrentPosition).getContent(), false, mTCLiveInfoList.get(mCurrentPosition).getCover_img());
                } else if (type == PopShareUtils.QQ) {
                    QQUtils.shareQQ(getActivity(), "小视频", mTCLiveInfoList.get(mCurrentPosition).getContent(), shareUrl, mTCLiveInfoList.get(mCurrentPosition).getCover_img());
                } else if (type == PopShareUtils.QQ_ZONE) {
                    QQUtils.shareQQZone(getActivity(), "小视频", mTCLiveInfoList.get(mCurrentPosition).getContent(), shareUrl, mTCLiveInfoList.get(mCurrentPosition).getCover_img());
                } else if (type == PopShareUtils.COPY_LINK) {
                    QQUtils.copyLink(getActivity(), shareUrl);
                }
//                WXUtils.shareWeChat(getActivity(), shareUrl, "小视频", mTCLiveInfoList.get(mCurrentPosition).getContent(), true, mTCLiveInfoList.get(mCurrentPosition).getCover_img());
            }

        });

    }


    /**
     * 校验地址
     *
     * @param playUrl
     * @return
     */
    private boolean checkPlayUrl(String playUrl) {
        if (TextUtils.isEmpty(playUrl) || (!playUrl.startsWith("http://") && !playUrl.startsWith("https://") && !playUrl.startsWith("rtmp://"))) {
            ToastUtils.showToast("播放地址不合法，目前仅支持rtmp,flv,hls,mp4播放方式!");
            return false;
        }
        if (playUrl.startsWith("http://") || playUrl.startsWith("https://")) {
            if (playUrl.contains(".flv")) {
                mUrlPlayType = TXLivePlayer.PLAY_TYPE_VOD_FLV;
            } else if (playUrl.contains(".m3u8")) {
                mUrlPlayType = TXLivePlayer.PLAY_TYPE_VOD_HLS;
            } else if (playUrl.toLowerCase().contains(".mp4")) {
                mUrlPlayType = TXLivePlayer.PLAY_TYPE_VOD_MP4;
            } else {
                ToastUtils.showToast("播放地址不合法，点播目前仅支持flv,hls,mp4播放方式!");
                return false;
            }
        } else {
            ToastUtils.showToast("播放地址不合法，点播目前仅支持flv,hls,mp4播放方式!");
            return false;
        }
        return true;
    }


    @Override
    public void onPlayEvent(TXVodPlayer player, int event, Bundle param) {
        if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
            if (player == mTXVodPlayer) {
                int width = param.getInt(TXLiveConstants.EVT_PARAM1);
                int height = param.getInt(TXLiveConstants.EVT_PARAM2);
                if (width > height) {
                    mTXVodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
                } else {
                    mTXVodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
                }
            }
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
//            stopPlay(false);
            restartPlay();
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {// 视频I帧到达，开始播放
            LogUtil.i("onPlayEvent, event I FRAME, player = " + player);
            mIvCover.setVisibility(View.GONE);
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            mIvCover.setVisibility(View.GONE);
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            LogUtil.i("onPlayEvent, event begin, player = " + player);
            mIvCover.setVisibility(View.GONE);
            for (int i = 0; i < PLAYER_NUM; i++) {
                VHFragment.PlayerInfo playerInfo = mPlayerInfoList.get(i);
                if (playerInfo.txVodPlayer == player) {
                    playerInfo.isBegin = true;
                    break;
                }
            }
        }
    }

    @Override
    public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {

    }


    protected void stopPlay(boolean clearLastFrame) {
        if (mTXVodPlayer != null) {
//            mTXVodPlayer.setPlayListener(null);
            mTXVodPlayer.stopPlay(clearLastFrame);
        }
    }

    private void restartPlay() {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.startPlay(mPlayUrl);
        }
    }


    private void initPlayerSDK() {
        mTXPlayConfig = new TXVodPlayConfig();
        mTXPlayConfig.setAutoRotate(true);
        mTXPlayConfig.setCacheFolderPath(Environment.getExternalStorageDirectory().getPath() + "/txcache");
        mTXPlayConfig.setMaxCacheItems(3);
        mPlayerInfoList = new ArrayList<>();

        if (mTCLiveInfoList.size() <= 5) {
            for (int i = 0; i < mTCLiveInfoList.size(); i++) {
                VHFragment.PlayerInfo playerInfo = new VHFragment.PlayerInfo();
                playerInfo.txVodPlayer = new TXVodPlayer(getActivity());
                playerInfo.txVodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
                playerInfo.txVodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
                playerInfo.txVodPlayer.setVodListener(this);
                playerInfo.txVodPlayer.setConfig(mTXPlayConfig);
                playerInfo.txVodPlayer.setAutoPlay(true);
                TCVideoInfo tcLiveInfo;
                tcLiveInfo = mTCLiveInfoList.get(i);
                playerInfo.playURL = tcLiveInfo.getPlay_url();
                playerInfo.txVodPlayer.startPlay(playerInfo.playURL);
                mPlayerInfoList.add(playerInfo);
            }
        } else {
            for (int i = 0; i < 5; i++) {
                VHFragment.PlayerInfo playerInfo = new VHFragment.PlayerInfo();
                playerInfo.txVodPlayer = new TXVodPlayer(getActivity());
                playerInfo.txVodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
                playerInfo.txVodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
                playerInfo.txVodPlayer.setVodListener(this);
                playerInfo.txVodPlayer.setConfig(mTXPlayConfig);
                playerInfo.txVodPlayer.setAutoPlay(true);
                TCVideoInfo tcLiveInfo;
                if (mInitTCLiveInfoPosition <= 2) {
                    tcLiveInfo = mTCLiveInfoList.get(i);
                } else if (mInitTCLiveInfoPosition >= mTCLiveInfoList.size() - 3) {
                    tcLiveInfo = mTCLiveInfoList.get(mTCLiveInfoList.size() - (PLAYER_NUM - i));
                } else {
                    tcLiveInfo = mTCLiveInfoList.get(mInitTCLiveInfoPosition - (2 - i));
                }
//            playerInfo.playURL = TextUtils.isEmpty(tcLiveInfo.hlsPlayUrl) ? tcLiveInfo.playurl : tcLiveInfo.hlsPlayUrl;
                playerInfo.playURL = tcLiveInfo.getPlay_url();
                playerInfo.txVodPlayer.startPlay(playerInfo.playURL);
//            }
                mPlayerInfoList.add(playerInfo);
            }
        }


        mVerticalViewPager.setCurrentItem(0);

//        mTXVodPlayer = new TXLivePlayer(this);
//        mTXVodPlayer.setPlayerView(mTXCloudVideoView);
//        mTXVodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
//        mTXVodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
//        mTXVodPlayer.setPlayListener(this);
//        mTXVodPlayer.setConfig(mTXPlayConfig);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!isClickComment) {
            if (mTXCloudVideoView != null) {
                mTXCloudVideoView.onResume();
            }
            if (mTXVodPlayer != null) {
                mTXVodPlayer.resume();
            }
        }
        isClickComment = false;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (!isClickComment) {
            if (mTXCloudVideoView != null) {
                mTXCloudVideoView.onPause();
            }
            if (mTXVodPlayer != null) {
                mTXVodPlayer.pause();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onDestroy();
            mTXCloudVideoView = null;
        }
        stopPlay(true);
        mTXVodPlayer = null;

        if (mPhoneListener != null) {
            TelephonyManager tm = (TelephonyManager) getActivity().getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
            mPhoneListener = null;
        }

        getActivity().unregisterReceiver(weChatBroadcastReceiver);
    }


    /**
     * ==========================================来电监听==========================================
     */
    private void initPhoneListener() {
        if (mPhoneListener == null)
            mPhoneListener = new VHFragment.TXPhoneStateListener(mTXVodPlayer);
        TelephonyManager tm = (TelephonyManager) getActivity().getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }


    static class TXPhoneStateListener extends PhoneStateListener {
        WeakReference<TXVodPlayer> mPlayer;

        public TXPhoneStateListener(TXVodPlayer player) {
            mPlayer = new WeakReference<TXVodPlayer>(player);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            TXVodPlayer player = mPlayer.get();
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


    View.OnClickListener praiseClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isClickComment = false;
            switch (view.getId()) {
                case R.id.item_video_praise_lin:
                    requestPraise();
                    break;
                case R.id.item_video_comment_lin:
                    isClickComment = true;
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), VideoCommentActivity.class);
                    intent.putExtra("video_id", video_id);
                    intent.putExtra("comment_count", mTCLiveInfoList.get(mCurrentPosition).getComment_count());
                    startActivityForResult(intent, 1000);
                    getActivity().overridePendingTransition(R.anim.enter_bottom, R.anim.exit_bottom);
                    break;
                case R.id.player_civ_avatar:
                    Intent intent1 = new Intent();
                    intent1.setClass(getActivity(), PersonalPageActivity.class);
                    intent1.putExtra("uid", mTCLiveInfoList.get(mCurrentPosition).getMember_id());
                    startActivity(intent1);
                    break;
                case R.id.item_video_follow_lin:
                    int is_followed = mTCLiveInfoList.get(mCurrentPosition).getIs_followed();
                    if (is_followed == 0) {
                        requestFollow();
                    } else {
                        ToastUtils.showToast("您已关注，无需在关注");
                    }
                    break;

                case R.id.player_cloud_view:
                    if (mTXVodPlayer != null) {
                        boolean playing = mTXVodPlayer.isPlaying();
                        if (playing) {
                            img_pause.setVisibility(View.VISIBLE);
                            mTXVodPlayer.pause();
                        } else {
                            img_pause.setVisibility(View.GONE);
                            mTXVodPlayer.resume();
                        }
                    }
                    break;
                case R.id.item_video_gift_lin:
                    isClickComment = true;
                    Intent intent2 = new Intent();
                    intent2.setClass(getActivity(), GiftActivity.class);
                    intent2.putExtra("userId", mTCLiveInfoList.get(mCurrentPosition).getMember_id());
                    intent2.putExtra("type", 1);
                    intent2.putExtra("info_id", mTCLiveInfoList.get(mCurrentPosition).getId());
//                    intent2.putExtra("video_id", video_id);
//                    intent2.putExtra("comment_count", mTCLiveInfoList.get(mCurrentPosition).getComment_count());
                    startActivityForResult(intent2, 1002);
                    getActivity().overridePendingTransition(R.anim.enter_bottom, R.anim.exit_bottom);
                    break;
                case R.id.item_video_share_lin:
                    popShareUtils.showAtLocation(view_pager_lin, Gravity.BOTTOM, 0, 0);
                    break;
            }
        }
    };

    /**
     * 点赞/取消点赞
     */
    private void requestPraise() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.add_upvote);
        params.addBodyParameter("video_id", video_id);
        params.addBodyParameter("user_id", mTCLiveInfoList.get(mCurrentPosition).getMember_id());
        params.addBodyParameter("follower_id", UserInfoUtils.getUid());

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    if (currentIsZan == 1) {
                        ToastUtils.showToast("取消点赞");
                        currentIsZan = 0;
                        mTCLiveInfoList.get(mCurrentPosition).setIs_zan(0);
                        mPagerAdapter.notifyDataSetChanged();
                        icon_praise.setImageResource(R.mipmap.icon_video_praise);
                        int zan_count = mTCLiveInfoList.get(mCurrentPosition).getZan_count();
                        if (zan_count > 0) {
                            mTCLiveInfoList.get(mCurrentPosition).setZan_count(zan_count - 1);
                            tv_praise.setText((zan_count - 1) + "");
                        }
                    } else {
                        ToastUtils.showToast("点赞成功");
                        currentIsZan = 1;
                        mTCLiveInfoList.get(mCurrentPosition).setIs_zan(1);
                        mPagerAdapter.notifyDataSetChanged();
                        icon_praise.setImageResource(R.mipmap.icon_video_praise_red);
                        int zan_count = mTCLiveInfoList.get(mCurrentPosition).getZan_count();
                        mTCLiveInfoList.get(mCurrentPosition).setZan_count(zan_count + 1);
                        tv_praise.setText((zan_count + 1) + "");

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, new QQUtils.BaseUiListener());
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    if (data == null) return;
                    int comment_count = data.getIntExtra("comment_count", 0);
                    mTCLiveInfoList.get(mCurrentPosition).setComment_count(comment_count);
                    if (comment_count > 10000) {
                        tv_comm_num.setText((comment_count / 10000) + "w");
                    } else {
                        tv_comm_num.setText(comment_count + "");
                    }
                    break;
                case 1002:
                    int gift_count = mTCLiveInfoList.get(mCurrentPosition).getGift_count() + 1;
                    mTCLiveInfoList.get(mCurrentPosition).setComment_count(gift_count);
                    if (gift_count > 10000) {
                        tv_gift_num.setText((gift_count / 10000) + "w");
                    } else {
                        tv_gift_num.setText(gift_count + "");
                    }
                    break;
            }
        }
    }

    /**
     * 请求关注
     */
    private void requestFollow() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.video_add_follow);
        params.addBodyParameter("user_id", mTCLiveInfoList.get(mCurrentPosition).getMember_id());
        params.addBodyParameter("follower_id", UserInfoUtils.getUid());

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("关注成功");
                    int guanz_count = mTCLiveInfoList.get(mCurrentPosition).getGuanz_count();
                    tv_follow_num.setText((guanz_count + 1) + "");
                    TCVideoInfo tcVideoInfo = mTCLiveInfoList.get(mCurrentPosition);
                    String member_id = tcVideoInfo.getMember_id();
                    for (TCVideoInfo tcVideoInfo2 : mTCLiveInfoList) {
                        if (TextUtils.equals(tcVideoInfo2.getMember_id(), member_id)) {
                            tcVideoInfo2.setIs_followed(1);
                            tcVideoInfo2.setGuanz_count(guanz_count + 1);
                        }
                    }
                    mPagerAdapter.notifyDataSetChanged();
                    if (VideoActivity.instance != null) {
                        VideoActivity.instance.refreshFollow();
                    }
//                    img_follow.setVisibility(View.GONE);
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
     * 注册微信分享广播
     */

    private void registerReceiver() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.BINDING_WECHAT_ACTION);
        getActivity().registerReceiver(weChatBroadcastReceiver, filter);
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
                int share_count = mTCLiveInfoList.get(mCurrentPosition).getShare_count();
                tv_share_num.setText((share_count + 1) + "");
                mTCLiveInfoList.get(mCurrentPosition).setShare_count(share_count + 1);
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

        RequestParams params = MRequestParams.getNoTokenParams(Api.video_add_share);
        params.addBodyParameter("video_id", mTCLiveInfoList.get(mCurrentPosition).getId());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
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
