package com.huanglong.v3.live.push;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.im.utils.TCLoginMgr;
import com.huanglong.v3.live.TCBaseActivity;
import com.huanglong.v3.live.im.TCChatEntity;
import com.huanglong.v3.live.im.TCChatMsgListAdapter;
import com.huanglong.v3.live.im.TCChatRoomMgr;
import com.huanglong.v3.live.im.TCSimpleUserInfo;
import com.huanglong.v3.live.im.TCUserAvatarListAdapter;
import com.huanglong.v3.live.model.LiveGiftBean;
import com.huanglong.v3.live.userinfo.TCUserInfoMgr;
import com.huanglong.v3.live.utils.SHARE_MEDIA;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.live.utils.TCPusherMgr;
import com.huanglong.v3.live.utils.TCUtils;
import com.huanglong.v3.live.utils.TXPhoneStateListener;
import com.huanglong.v3.live.widget.BeautyDialogFragment;
import com.huanglong.v3.live.widget.DetailDialogFragment;
import com.huanglong.v3.live.widget.TCAudioControl;
import com.huanglong.v3.live.widget.TCDanmuMgr;
import com.huanglong.v3.live.widget.TCHeartLayout;
import com.huanglong.v3.live.widget.TCInputTextMsgDialog;
import com.huanglong.v3.live.widget.TCSwipeAnimationController;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.UserInfoUtils;
import com.tencent.TIMElemType;
import com.tencent.TIMMessage;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.TXLog;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import master.flame.danmaku.controller.IDanmakuView;

/**
 * Created by RTMP on 2016/8/4
 */
@ContentView(R.layout.activity_publish)
public class TCLivePublisherActivity extends TCBaseActivity implements ITXLivePushListener, View.OnClickListener, TCInputTextMsgDialog.OnTextSendListener, TCChatRoomMgr.TCChatRoomListener, BeautyDialogFragment.OnBeautyParamsChangeListener {
    private static final String TAG = TCLivePublisherActivity.class.getSimpleName();


    @ViewInject(R.id.live_follow)
    private TextView tv_follow;
    @ViewInject(R.id.rl_publish_root)
    private RelativeLayout relativeLayout;
    @ViewInject(R.id.video_view)
    private TXCloudVideoView mTXCloudVideoView;
    @ViewInject(R.id.rl_controllLayer)
    private RelativeLayout mControllLayer;
    //头像列表控件
    @ViewInject(R.id.rv_user_avatar)
    private RecyclerView mUserAvatarList;
    @ViewInject(R.id.im_msg_listview)
    private ListView mListViewMsg;
    //点赞动画
    @ViewInject(R.id.heart_layout)
    private TCHeartLayout mHeartLayout;
    @ViewInject(R.id.flash_btn)
    private Button mFlashView;
    @ViewInject(R.id.tv_broadcasting_time)
    private TextView mBroadcastTime;
    @ViewInject(R.id.iv_head_icon)
    private ImageView mHeadIcon;
    @ViewInject(R.id.tv_member_counts)
    private TextView mMemberCount;
    @ViewInject(R.id.danmakuView)
    private IDanmakuView danmakuView;
    @ViewInject(R.id.btn_audio_ctrl)
    private Button mBtnAudioCtrl;
    @ViewInject(R.id.layoutAudioControlContainer)
    private TCAudioControl mAudioCtrl;
    @ViewInject(R.id.audio_plugin)
    private LinearLayout mAudioPluginLayout;
    @ViewInject(R.id.btn_audio_effect)
    private Button mBtnAudioEffect;
    @ViewInject(R.id.btn_audio_close)
    private Button mBtnAudioClose;
    @ViewInject(R.id.netbusy_tv)
    private TextView mNetBusyTips;
    @ViewInject(R.id.live_gift_img)
    private ImageView img_gift;

    private Handler mNetBusyHandler;

    public static TCLivePublisherActivity instance;

    //    private TextView mDetailTime, mDetailAdmires, mDetailWatchCount;
    private BeautyDialogFragment.BeautyParams mBeautyParams = new BeautyDialogFragment.BeautyParams();

    private TCInputTextMsgDialog mInputTextMsgDialog;

    private ArrayList<TCChatEntity> mArrayListChatEntity = new ArrayList<>();
    private TCChatMsgListAdapter mChatMsgListAdapter;

    private BeautyDialogFragment mBeautyDialogFragment;
    //    private SeekBar mWhiteningSeekBar;
//    private SeekBar mBeautySeekBar;


    private TCUserAvatarListAdapter mAvatarListAdapter;
    private float mScreenHeight;


    private long mSecond = 0;
    private Timer mBroadcastTimer;
    private BroadcastTimerTask mBroadcastTimerTask;

    private long lTotalMemberCount = 0;
    private long lMemberCount = 0;
    private long lHeartCount = 0;

    public TXLivePusher mTXLivePusher;
    protected TXLivePushConfig mTXPushConfig = new TXLivePushConfig();

    protected Handler mHandler = new Handler();

    private boolean mFlashOn = false;
    protected boolean mPasuing = false;

    protected String mPushUrl;
    private String mRoomId;
    protected String mUserId;
    private String mTitle;
    private String mCoverPicUrl;
    private String mHeadPicUrl;
    private String mNickName;
    private String mLocation;

    private TCPusherMgr mTCPusherMgr;
    private TCChatRoomMgr mTCChatRoomMgr;

    //弹幕
    private TCDanmuMgr mDanmuMgr;

    private TCSwipeAnimationController mTCSwipeAnimationController;
    //分享相关
    private SHARE_MEDIA mShare_meidia = SHARE_MEDIA.MORE;
    private String mShareUrl = TCConstants.SVR_LivePlayShare_URL;
    private boolean mSharedNotPublished = true; //分享之后还未开始推流
    //log相关
    protected boolean mShowLog = false;
    private String live_id;

    private PhoneStateListener mPhoneListener = null;

    @Override
    public void onReceiveExitMsg() {
        super.onReceiveExitMsg();
        TXLog.d(TAG, "publisher broadcastReceiver receive exit app msg");
        //在被踢下线的情况下，执行退出前的处理操作：停止推流、关闭群组
        stopRecordAnimation();
        mTXCloudVideoView.onPause();

        stopPublish();
        quitRoom();
    }

    @Override
    protected void initView() {
        mImmersionBar = ImmersionBar.with(this);
//        mImmersionBar;   //所有子类都将继承这些相同的属性
        mImmersionBar.statusBarColor(R.color.transparent)
                .keyboardEnable(false).init();
        instance = this;
        tv_follow.setVisibility(View.GONE);
        Intent intent = getIntent();
        mUserId = intent.getStringExtra(TCConstants.USER_ID);
        mPushUrl = intent.getStringExtra(TCConstants.PUBLISH_URL);
        mTitle = intent.getStringExtra(TCConstants.ROOM_TITLE);
        mCoverPicUrl = intent.getStringExtra(TCConstants.COVER_PIC);
        mHeadPicUrl = intent.getStringExtra(TCConstants.USER_HEADPIC);
        mNickName = intent.getStringExtra(TCConstants.USER_NICK);
        mLocation = intent.getStringExtra(TCConstants.USER_LOC);
        mShare_meidia = (SHARE_MEDIA) intent.getSerializableExtra(TCConstants.SHARE_PLATFORM);
        live_id = intent.getStringExtra(TCConstants.LIVE_Id);
        mBroadcastTime.setText(mNickName);
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mTCSwipeAnimationController.processEvent(event);
            }
        });
        mTCSwipeAnimationController = new TCSwipeAnimationController(this);
        mTCSwipeAnimationController.setAnimationView(mControllLayer);
        mAvatarListAdapter = new TCUserAvatarListAdapter(this, TCLoginMgr.getInstance().getLastUserInfo().identifier);
        mUserAvatarList.setAdapter(mAvatarListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUserAvatarList.setLayoutManager(linearLayoutManager);
        mInputTextMsgDialog = new TCInputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);
//        mBroadcastTime.setText(String.format(Locale.US, "%s", "00:00:00"));
        showHeadIcon(mHeadIcon, TCUserInfoMgr.getInstance().getHeadPic());
        mMemberCount.setText("观众:0");
        mChatMsgListAdapter = new TCChatMsgListAdapter(this, mListViewMsg, mArrayListChatEntity);
        mListViewMsg.setAdapter(mChatMsgListAdapter);

        mDanmuMgr = new TCDanmuMgr(this);
        mDanmuMgr.setDanmakuView(danmakuView);

        //AudioControl
        mAudioCtrl.setPluginLayout(mAudioPluginLayout);

    }

    @Override
    protected void logic() {
        mBeautyDialogFragment = new BeautyDialogFragment();
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        //初始化消息回调
        mTCChatRoomMgr = TCChatRoomMgr.getInstance();
        mTCChatRoomMgr.setMessageListener(this);
        mTCPusherMgr = TCPusherMgr.getInstance();
//        mTCPusherMgr.setPusherListener(this);
        mTCChatRoomMgr.createGroup();

        mBtnAudioCtrl.setOnClickListener(this);
    }

    private void startPublishImpl() {
        mSharedNotPublished = false;
        mSharedNotPublished = false;
        if (mTXLivePusher == null) {
            mTXLivePusher = new TXLivePusher(TCLivePublisherActivity.this);
            mBeautyDialogFragment.setBeautyParamsListner(mBeautyParams, this);
            mTXLivePusher.setPushListener(TCLivePublisherActivity.this);
            mTXPushConfig.setAutoAdjustBitrate(false);

            //切后台推流图片
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pause_publish, options);
            mTXPushConfig.setPauseImg(bitmap);
            mTXPushConfig.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO | TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO);
            mTXPushConfig.setBeautyFilter(mBeautyParams.mBeautyProgress, mBeautyParams.mWhiteProgress, mBeautyParams.mRuddyProgress);
            mTXPushConfig.setFaceSlimLevel(mBeautyParams.mFaceLiftProgress);
            mTXPushConfig.setEyeScaleLevel(mBeautyParams.mBigEyeProgress);
            mTXPushConfig.enableHighResolutionCaptureMode(false);
            mTXLivePusher.setConfig(mTXPushConfig);

            mPhoneListener = new TXPhoneStateListener(mTXLivePusher);
            TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        mAudioCtrl.setPusher(mTXLivePusher);
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.setVisibility(View.VISIBLE);
            mTXCloudVideoView.clearLog();
        }
        //mBeautySeekBar.setProgress(100);

        //设置视频质量：高清
        mTXLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_HIGH_DEFINITION, false, false);
        mTXCloudVideoView.enableHardwareDecode(true);
        mTXLivePusher.startCameraPreview(mTXCloudVideoView);
//        mTXLivePusher.setMirror(true);
        mTXLivePusher.startPusher(mPushUrl);
    }

    protected void startPublish() {
        if (checkPermission()) {
            startPublishImpl();
        }
    }

    protected void stopPublish() {
        if (mTXLivePusher != null) {
            mTXLivePusher.stopCameraPreview(false);
            mTXLivePusher.setPushListener(null);
            mTXLivePusher.stopPusher();
        }
        if (mAudioCtrl != null) {
            mAudioCtrl.unInit();
            mAudioCtrl = null;
        }
    }

    /**
     * 加载主播头像
     *
     * @param view   view
     * @param avatar 头像链接
     */
    private void showHeadIcon(ImageView view, String avatar) {
        TCUtils.showPicWithUrl(this, view, avatar, R.drawable.head_me);
    }

//    private ObjectAnimator mObjAnim;

    /**
     * 开启红点与计时动画
     */
    private void startRecordAnimation() {
//        mObjAnim = ObjectAnimator.ofFloat(mRecordBall, "alpha", 1f, 0f, 1f);
//        mObjAnim.setDuration(1000);
//        mObjAnim.setRepeatCount(-1);
//        mObjAnim.start();
        //直播时间
        if (mBroadcastTimer == null) {
            mBroadcastTimer = new Timer(true);
            mBroadcastTimerTask = new BroadcastTimerTask();
            mBroadcastTimer.schedule(mBroadcastTimerTask, 1000, 1000);
        }
    }

    /**
     * 关闭红点与计时动画
     */
    private void stopRecordAnimation() {
//        if (null != mObjAnim)
//            mObjAnim.cancel();
        //直播时间
        if (null != mBroadcastTimer) {
            mBroadcastTimerTask.cancel();
        }
    }

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

        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("我:");
        entity.setContext(msg);
        entity.setType(TCConstants.TEXT_TYPE);
        notifyMsg(entity);

        if (danmuOpen) {
            if (mDanmuMgr != null) {
                mDanmuMgr.addDanmu(UserInfoUtils.getAvatar(), UserInfoUtils.getNickName(), msg);
            }
            mTCChatRoomMgr.sendDanmuMessage(msg);
        } else {
            mTCChatRoomMgr.sendTextMessage(msg);
        }

    }

    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }

    /**
     * 记时器
     */
    private class BroadcastTimerTask extends TimerTask {
        public void run() {
            //Log.i(TAG, "timeTask ");
            ++mSecond;
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (!mTCSwipeAnimationController.isMoving())
//                        mBroadcastTime.setText(TCUtils.formattedTime(mSecond));
//                }
//            });
//            if (MySelfInfo.getInstance().getIdStatus() == TCConstants.HOST)
//                mHandler.sendEmptyMessage(UPDAT_WALL_TIME_TIMER_TASK);
        }
    }

    /**
     * 结束统计的dialog
     */
    public void showDetailDialog() {
        //确认则显示观看detail
        stopRecordAnimation();
        DetailDialogFragment dialogFragment = new DetailDialogFragment();
        Bundle args = new Bundle();
        args.putString("time", TCUtils.formattedTime(mSecond));
        args.putString("heartCount", String.format(Locale.CHINA, "%d", lHeartCount));
        args.putString("totalMemberCount", String.format(Locale.CHINA, "%d", lTotalMemberCount));
        dialogFragment.setArguments(args);
        dialogFragment.setCancelable(false);
        if (dialogFragment.isAdded())
            dialogFragment.dismiss();
        else
            dialogFragment.show(getFragmentManager(), "");
        setResult(RESULT_OK);
        requestAnalysize();
    }

    /**
     * 显示确认消息
     *
     * @param msg     消息内容
     * @param isError true错误消息（必须退出） false提示消息（可选择是否退出）
     */
    public void showComfirmDialog(String msg, Boolean isError) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ConfirmDialogStyle);
        builder.setCancelable(true);
        builder.setTitle(msg);
        if (!isError) {
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    stopPublish();
                    quitRoom();
                    stopRecordAnimation();
                    showDetailDialog();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            //当情况为错误的时候，直接停止推流
            stopPublish();
            quitRoom();
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    stopRecordAnimation();
                    showDetailDialog();
                }
            });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }


    /**
     * 退出房间
     * 包含后台退出与IMSDK房间退出操作
     */
    public void quitRoom() {
        mTCChatRoomMgr.deleteGroup();
        mTCPusherMgr.changeLiveStatus(mUserId, TCPusherMgr.TCLiveStatus_Offline);
    }

    private void showNetBusyTips() {
        if (null == mNetBusyHandler) {
            mNetBusyHandler = new Handler(Looper.getMainLooper());
        }
        if (mNetBusyTips.isShown()) {
            return;
        }
        mNetBusyTips.setVisibility(View.VISIBLE);
        mNetBusyHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mNetBusyTips.setVisibility(View.GONE);
            }
        }, 5000);
    }

    @Override
    public void onBackPressed() {

        showComfirmDialog(TCConstants.TIPS_MSG_STOP_PUSH, false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDanmuMgr != null) {
            mDanmuMgr.resume();
        }
        mTXCloudVideoView.onResume();

        if (mPasuing) {
            mPasuing = false;

            if (mTXLivePusher != null) {
                mTXLivePusher.resumePusher();
            }
        }

        if (mTXLivePusher != null) {
            mTXLivePusher.resumeBGM();
        }

//        if (mSharedNotPublished)
//            startPublish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDanmuMgr != null) {
            mDanmuMgr.pause();
        }
        mTXCloudVideoView.onPause();
        if (mTXLivePusher != null) {
            mTXLivePusher.pauseBGM();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        mPasuing = true;
        if (mTXLivePusher != null) {
//            mTXLivePusher.stopCameraPreview(false);
            mTXLivePusher.pausePusher();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDanmuMgr != null) {
            mDanmuMgr.destroy();
            mDanmuMgr = null;
        }
        stopRecordAnimation();
        mTXCloudVideoView.onDestroy();

        stopPublish();
        mTCChatRoomMgr.removeMsgListener();
        mTCPusherMgr.setPusherListener(null);

        TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_cam:
                if (mTXLivePusher != null) {
                    mTXLivePusher.switchCamera();
                }
                break;
            case R.id.flash_btn:
                if (!mTXLivePusher.turnOnFlashLight(!mFlashOn)) {
                    Toast.makeText(getApplicationContext(), "打开闪光灯失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                mFlashOn = !mFlashOn;
                mFlashView.setBackgroundDrawable(mFlashOn ?
                        getResources().getDrawable(R.drawable.icon_flash_pressed) :
                        getResources().getDrawable(R.drawable.icon_flash));

                break;
            case R.id.beauty_btn:
                if (mBeautyDialogFragment.isAdded())
                    mBeautyDialogFragment.dismiss();
                else
                    mBeautyDialogFragment.show(getFragmentManager(), "");
                break;
            case R.id.btn_close:
                showComfirmDialog(TCConstants.TIPS_MSG_STOP_PUSH, false);
//                for(int i = 0; i< 100; i++)
//                    mHeartLayout.addFavor();
                break;
            case R.id.btn_message_input:
                showInputMsgDialog();
                break;
            case R.id.btn_audio_ctrl:
                if (null != mAudioCtrl) {
                    mAudioCtrl.setVisibility(mAudioCtrl.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
                break;
            case R.id.btn_audio_effect:
                mAudioCtrl.setVisibility(mAudioCtrl.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.btn_audio_close:
                mAudioCtrl.stopBGM();
                mAudioPluginLayout.setVisibility(View.GONE);
                mAudioCtrl.setVisibility(View.GONE);
                break;
            default:
                //mLayoutFaceBeauty.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 发消息弹出框
     */
    private void showInputMsgDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();

        lp.width = (int) (display.getWidth()); //设置宽度
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.setCancelable(true);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }

    @Override
    protected void showErrorAndQuit(String errorMsg) {

        mTXCloudVideoView.onPause();
        stopPublish();
        quitRoom();
        stopRecordAnimation();

        super.showErrorAndQuit(errorMsg);

    }

    @Override
    public void onPushEvent(int event, Bundle bundle) {
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.setLogText(null, bundle, event);
        }
        if (event < 0) {
            if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT) {//网络断开，弹对话框强提醒，推流过程中直播中断需要显示直播信息后退出
                showComfirmDialog(TCConstants.ERROR_MSG_NET_DISCONNECTED, true);
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {//未获得摄像头权限，弹对话框强提醒，并退出
                showErrorAndQuit(TCConstants.ERROR_MSG_OPEN_CAMERA_FAIL);
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL || event == TXLiveConstants.PUSH_ERR_MIC_RECORD_FAIL) { //未获得麦克风权限，弹对话框强提醒，并退出
                Toast.makeText(getApplicationContext(), bundle.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
                showErrorAndQuit(TCConstants.ERROR_MSG_OPEN_MIC_FAIL);
            } else {
                //其他错误弹Toast弱提醒，并退出
                Toast.makeText(getApplicationContext(), bundle.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();

                mTXCloudVideoView.onPause();
                TCPusherMgr.getInstance().changeLiveStatus(mUserId, TCPusherMgr.TCLiveStatus_Offline);
                stopRecordAnimation();
                setResult(RESULT_OK);
                finish();
            }
        }

        if (event == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            Log.d(TAG, "当前机型不支持视频硬编码");
            mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640);
            mTXPushConfig.setVideoBitrate(700);
            mTXPushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
            mTXPushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
            mTXPushConfig.enableHighResolutionCaptureMode(false);
            mTXLivePusher.setConfig(mTXPushConfig);
        } else if (event == TXLiveConstants.PUSH_WARNING_NET_BUSY) {
            showNetBusyTips();
        }

        if (event == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {
            TCPusherMgr.getInstance().changeLiveStatus(mUserId, TCPusherMgr.TCLiveStatus_Online);
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.setLogText(bundle, null, 0);
        }
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

//    /**
//     * 向服务器获取推流地址 回调
//     *
//     * @param errCode   错误码，0表示获取成功
//     * @param groupId   群ID
//     * @param pusherUrl 推流地址
//     * @param timeStamp 时间戳
//     */
//    @Override
//    public void onGetPusherUrl(int errCode, String groupId, String pusherUrl, String timeStamp) {
////        mPushUrl = pusherUrl;
//        startPublish();
//        if (errCode == 0) {
//            mPushUrl = pusherUrl;
//            startRecordAnimation();
//            // 友盟的分享组件并不完善，为了各种异常情况下正常推流，要多做一些事情
//            if (mShare_meidia == SHARE_MEDIA.MORE) {
//                startPublish();
//            } else {
//                startShare(timeStamp);
//
//                boolean isSupportShare = true;
////                if (mShare_meidia == SHARE_MEDIA.SINA) {
////                    isSupportShare = true;
////                } else if (mShare_meidia == SHARE_MEDIA.QZONE) {
////                    if (UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.QQ) || UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.QZONE)) {
////                        isSupportShare = true;
////                    }
////                } else if (UMShareAPI.get(this).isInstall(this, mShare_meidia)) {
////                    isSupportShare = true;
////                }
//                if (isSupportShare) {
//                    startPublish();
//                }
//            }
//        } else {
//            if (null == groupId) {
//                showErrorAndQuit(TCConstants.ERROR_MSG_CREATE_GROUP_FAILED + errCode);
//            } else {
//                showErrorAndQuit(TCConstants.ERROR_MSG_GET_PUSH_URL_FAILED + errCode);
//            }
//        }
//    }

    private void startShare(final String timeStamp) {
//        ShareAction shareAction = new ShareAction(TCLivePublisherActivity.this);
//        try {
//            mShareUrl = mShareUrl + "?sdkappid=" + java.net.URLEncoder.encode(String.valueOf(TCConstants.IMSDK_APPID), "utf-8")
//                    + "&acctype=" + java.net.URLEncoder.encode(String.valueOf(TCConstants.IMSDK_ACCOUNT_TYPE), "utf-8")
//                    + "&userid=" + java.net.URLEncoder.encode(mUserId, "utf-8")
//                    + "&type=0"
//                    + "&ts=" + java.net.URLEncoder.encode(timeStamp, "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        UMWeb web = new UMWeb(mShareUrl);
//        if (mCoverPicUrl.isEmpty()) {
//            web.setThumb(new UMImage(TCLivePublisherActivity.this.getApplicationContext(), R.drawable.bg));
//        } else {
//            web.setThumb(new UMImage(TCLivePublisherActivity.this.getApplicationContext(), mCoverPicUrl));
//        }
//        web.setTitle(mTitle);
//        shareAction.withText(mNickName + "正在直播");
//        shareAction.withMedia(web);
//        shareAction.setCallback(umShareListener);
//
//        shareAction.setPlatform(mShare_meidia).share();
//        mSharedNotPublished = true;
    }


//    private UMShareListener umShareListener = new UMShareListener() {
//        @Override
//        public void onStart(SHARE_MEDIA platform) {
//            Log.d("plat", "platform" + platform);
//        }
//
//        @Override
//        public void onResult(SHARE_MEDIA platform) {
//            Log.d("plat", "platform" + platform);
//            Toast.makeText(TCLivePublisherActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
//            //开启推流
//            TCLivePublisherActivity.this.mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (mSharedNotPublished)
//                        startPublish();
//                }
//            });
//        }
//
//        @Override
//        public void onError(SHARE_MEDIA platform, Throwable t) {
//            Toast.makeText(TCLivePublisherActivity.this,"分享失败"+t.getMessage(), Toast.LENGTH_LONG).show();
//            //开启推流
//            TCLivePublisherActivity.this.mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (mSharedNotPublished)
//                     startPublish();
//                }
//            });
//        }
//
//        @Override
//        public void onCancel(SHARE_MEDIA platform) {
//            Toast.makeText(TCLivePublisherActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
//            //开启推流
//            TCLivePublisherActivity.this.mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (mSharedNotPublished)
//                        startPublish();
//                }
//            });
//        }
//    };

//    @Override
//    public void onChangeLiveStatus(int errCode) {
//        Log.d(TAG, "onChangeLiveStatus:" + errCode);
//    }

    @Override
    public void onJoinGroupCallback(int code, String msg) {
        if (0 == code) {
            //获取推流地址
            Log.d(TAG, "onJoin group success" + msg);
//            mTCPusherMgr.getPusherUrl(mUserId, msg, mTitle, mCoverPicUrl, mNickName, mHeadPicUrl, mLocation);
            startRecordAnimation();
            bindGroup(msg);
            startPublish();
        } else if (TCConstants.NO_LOGIN_CACHE == code) {
            TXLog.d(TAG, "onJoin group failed" + msg);
            showErrorAndQuit(TCConstants.ERROR_MSG_NO_LOGIN_CACHE);
        } else {
            TXLog.d(TAG, "onJoin group failed" + msg);
            showErrorAndQuit(TCConstants.ERROR_MSG_JOIN_GROUP_FAILED + code);
        }
    }

    /**
     * 消息发送回调
     *
     * @param errCode    错误码，0代表发送成功
     * @param timMessage 发送的TIM消息
     */
    @Override
    public void onSendMsgCallback(int errCode, TIMMessage timMessage) {
        if (timMessage != null)
            if (errCode == 0) {
                TIMElemType elemType = timMessage.getElement(0).getType();
                if (elemType == TIMElemType.Text) {
                    //发送文本消息成功
                    Log.d(TAG, "onSendTextMsgsuccess:" + errCode);
                } else if (elemType == TIMElemType.Custom) {
                    Log.d(TAG, "onSendCustomMsgsuccess:" + errCode);
                }
            } else {
                Log.d(TAG, "onSendMsgfail:" + errCode + " msg:" + timMessage.getMsgId());
            }

    }

    @Override
    public void onReceiveMsg(int type, TCSimpleUserInfo userInfo, String content) {
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

    public void handleTextMsg(TCSimpleUserInfo userInfo, String text) {
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName(userInfo.nickname);
        entity.setContext(text);
        entity.setType(TCConstants.TEXT_TYPE);
        notifyMsg(entity);
    }

    /**
     * 有用户加入进来
     *
     * @param userInfo
     */
    public void handleMemberJoinMsg(TCSimpleUserInfo userInfo) {
        //更新头像列表 返回false表明已存在相同用户，将不会更新数据
        if (!mAvatarListAdapter.addItem(userInfo))
            return;
        lTotalMemberCount++;
        lMemberCount++;
        mMemberCount.setText("观众:" + String.format(Locale.CHINA, "%d", lMemberCount));

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
     * 有用户退出
     *
     * @param userInfo
     */
    public void handleMemberQuitMsg(TCSimpleUserInfo userInfo) {
        if (lMemberCount > 0 && lTotalMemberCount > 0) {
            lMemberCount--;
            lTotalMemberCount--;
        } else {
            Log.d(TAG, "接受多次退出请求，目前人数为负数");
            mMemberCount.setText("观众:" + String.format(Locale.CHINA, "%d", lMemberCount));
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
    }

    @Override
    public void triggerSearch(String query, Bundle appSearchData) {
        super.triggerSearch(query, appSearchData);
    }

    /**
     * 赞处理
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
            mHeartLayout.addFavor();
            lHeartCount++;
            //todo：修改显示类型
            entity.setType(TCConstants.PRAISE);
            notifyMsg(entity);
        } else {
            lHeartCount--;
        }

    }


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

    @Override
    public void onGroupDelete() {
        //useless
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (null != mAudioCtrl && mAudioCtrl.getVisibility() != View.GONE && ev.getRawY() < mAudioCtrl.getTop()) {
            mAudioCtrl.setVisibility(View.GONE);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /** attention to this below ,must add this**/
//        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {//是否选择，没选择就不会继续
//            if (requestCode == mAudioCtrl.REQUESTCODE) {
//                if (data == null) {
//                    Log.e(TAG, "null data");
//                } else {
//                    Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
//                    if (mAudioCtrl != null) {
//                        mAudioCtrl.processActivityResult(uri);
//                    } else {
//                        Log.e(TAG, "NULL Pointer! Get Music Failed");
//                    }
//                }
//            }
    }


    @Override
    public void onBeautyParamsChange(BeautyDialogFragment.BeautyParams params, int key) {
        switch (key) {
            case BeautyDialogFragment.BEAUTYPARAM_BEAUTY:
            case BeautyDialogFragment.BEAUTYPARAM_WHITE:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setBeautyFilter(params.mBeautyStyle, params.mBeautyProgress, params.mWhiteProgress, params.mRuddyProgress);
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_FACE_LIFT:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setFaceSlimLevel(params.mFaceLiftProgress);
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_BIG_EYE:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setEyeScaleLevel(params.mBigEyeProgress);
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_FILTER:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setFilter(TCUtils.getFilterBitmap(getResources(), params.mFilterIdx));
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_MOTION_TMPL:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setMotionTmpl(params.mMotionTmplPath);
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_GREEN:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setGreenScreenFile(TCUtils.getGreenFileName(params.mGreenIdx));
                }
                break;
            default:
                break;
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        permissions.toArray(new String[0]),
                        100);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                startPublishImpl();
                break;
            default:
                break;
        }
    }

    /**
     * 直播间绑定直播聊天室
     *
     * @param groupId
     */
    private void bindGroup(String groupId) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.live_qun);
        params.addBodyParameter("live_id", live_id);
        params.addBodyParameter("groupName", UserInfoUtils.getNickName() + "的直播间");
        params.addBodyParameter("groupId", groupId);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
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
     * 关闭直播，统计
     */
    private void requestAnalysize() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.liveplay_analysize);
        params.addBodyParameter("live_id", live_id);
        params.addBodyParameter("duration", TCUtils.formattedTime(mSecond));
        params.addBodyParameter("total_online", String.format(Locale.CHINA, "%d", lHeartCount));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
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


}
