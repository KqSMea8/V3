package com.huanglong.v3.activities.login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.gyf.barlibrary.BarHide;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.activities.main.MainActivity;
import com.huanglong.v3.im.event.FriendshipEvent;
import com.huanglong.v3.im.event.GroupEvent;
import com.huanglong.v3.im.event.MessageEvent;
import com.huanglong.v3.im.event.RefreshEvent;
import com.huanglong.v3.im.utils.TCLoginMgr;
import com.huanglong.v3.live.userinfo.ITCUserInfoMgrListener;
import com.huanglong.v3.live.userinfo.TCUserInfoMgr;
import com.huanglong.v3.utils.Constant;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.hubcloud.adhubsdk.AdListener;
import com.hubcloud.adhubsdk.SplashAd;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;


/**
 * Created by bin on 2018/4/12.
 * 启动页面
 */
@ContentView(R.layout.activity_welcome)
public class WelcomeActivity extends BaseActivity implements TCLoginMgr.TCLoginCallback {

    @ViewInject(R.id.welcome_img)
    private ImageView img;
    @ViewInject(R.id.adsFl)
    private FrameLayout adsParent;

    private TCLoginMgr mTCLoginMgr;
    //    private CountDownTimer countDownTimer;
    public boolean canJumpImmediately = false;
    boolean hasJump = false;
    private SplashAd splashAd;

    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        mImmersionBar.statusBarColor(R.color.transparent)
                .keyboardEnable(true)
                .hideBar(BarHide.FLAG_HIDE_BAR)
                .init();

//        AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
//        alpha.setDuration(2000);
//        img.startAnimation(alpha);

    }

    @Override
    protected void logic() {

        mTCLoginMgr = TCLoginMgr.getInstance();
        mTCLoginMgr.setTCLoginCallback(this);

        RefreshEvent.getInstance();
        FriendshipEvent.getInstance().init();
        GroupEvent.getInstance().init();
//        initCountDown();

        PermissionsUtil.requestPermission(WelcomeActivity.this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
//                countDownTimer.start();
                splashAd = new SplashAd(WelcomeActivity.this, adsParent, listener, Constant.Ad_SPLASH_ID);
                splashAd.setCloseButtonPadding(10, 20, 10, 10);
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {

            }
        }, permissions);


//
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                PermissionsUtil.requestPermission(WelcomeActivity.this, new PermissionListener() {
//                    @Override
//                    public void permissionGranted(@NonNull String[] permission) {
//                        boolean autoLogin = UserInfoUtils.getAutoLogin();
//                        if (autoLogin) {
//                            String sid = UserInfoUtils.getSid();
//                            String uid = UserInfoUtils.getUid();
//                            if (TextUtils.isEmpty(sid) || TextUtils.isEmpty(uid)) {
//                                Intent intent = new Intent();
//                                intent.setClass(WelcomeActivity.this, LoginActivity.class);
//                                startActivity(intent);
//                                WelcomeActivity.this.finish();
//                            } else {
//                                mTCLoginMgr.imLogin(UserInfoUtils.getUid(), UserInfoUtils.getSid());
//                            }
//                        } else {
//                            Intent intent = new Intent();
//                            intent.setClass(WelcomeActivity.this, LoginActivity.class);
//                            startActivity(intent);
//                            WelcomeActivity.this.finish();
//                        }
//                    }
//
//                    @Override
//                    public void permissionDenied(@NonNull String[] permission) {
//
//                    }
//                }, permissions);
//            }
//        }, 2000);
    }

    /**
     * 初始化设置倒计时
     */
//    private void initCountDown() {
//        countDownTimer = new CountDownTimer(8000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                jumpAcivity();
//            }
//        };
//    }


    /**
     * 广告监听
     */
    private AdListener listener = new AdListener() {
        @Override
        public void onAdLoaded() {
            LogUtil.i("onAdLoaded");
        }

        @Override
        public void onAdShown() {
            LogUtil.i("onAdShown");
            img.setVisibility(View.GONE);
//            countDownTimer.cancel();
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            LogUtil.i("onAdFailedToLoad");
            jump();
        }

        @Override
        public void onAdClosed() {
            LogUtil.i("onAdDismissed");
//                jumpWhenCanClick(); // 跳转至您的应用主界面
//            countDownTimer.cancel();
            jumpWhenCanClick(); // 跳转至您的应用主界面
//            if (!canJumpImmediately) {
//                jumpActivity();
//            }
        }

        @Override
        public void onAdClicked() {
            LogUtil.i("onAdClick");
//            canJumpImmediately = true;
            // 设置开屏可接受点击时，该回调可用
        }
    };

    /**
     * 跳转页面
     */
    private void jumpActivity() {
        boolean autoLogin = UserInfoUtils.getAutoLogin();
        if (autoLogin) {
            String sid = UserInfoUtils.getSid();
            String uid = UserInfoUtils.getUid();
            if (TextUtils.isEmpty(sid) || TextUtils.isEmpty(uid)) {
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            } else {
                mTCLoginMgr.imLogin(UserInfoUtils.getUid(), UserInfoUtils.getSid());
            }
        } else {
            Intent intent = new Intent();
            intent.setClass(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            WelcomeActivity.this.finish();
        }
    }

    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    private void jump() {
        jumpActivity();
    }

    /**
     * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。故此时需要增加canJumpImmediately判断。 另外，点击开屏还需要在onResume中调用jumpWhenCanClick接口。
     */
    private void jumpWhenCanClick() {
        LogUtil.i("-----canJumpImmediately:" + canJumpImmediately);
        if (canJumpImmediately) {
            jumpActivity();
        } else {
            canJumpImmediately = true;
        }
    }


    @Override
    public void onSuccess() {
        //初始化消息监听
        MessageEvent.getInstance();
        Intent intent = new Intent();
        intent.setClass(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        TCUserInfoMgr.getInstance().setUserId(UserInfoUtils.getUid(), new ITCUserInfoMgrListener() {
            @Override
            public void OnQueryUserInfo(int error, String errorMsg) {
                // TODO: 16/8/10
            }

            @Override
            public void OnSetUserInfo(int error, String errorMsg) {
                if (0 != error)
                    ToastUtils.showToast("设置 User ID 失败" + errorMsg);
            }
        });
        WelcomeActivity.this.finish();
    }

    @Override
    public void onFailure(int code, String msg) {
        LogUtil.e("--登录失败,code:" + code + " msg:" + msg);
        Intent intent = new Intent();
        intent.setClass(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        WelcomeActivity.this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        //删除登录回调
//        mTCLoginMgr.removeTCLoginCallback();
        canJumpImmediately = false;
        LogUtil.i("-------onPause:" + canJumpImmediately);
    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        LogUtil.i("onPause:" + canJumpImmediately);
//        canJumpImmediately = false;
//
//    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置登录回调,resume设置回调避免被registerActivity冲掉
//        mTCLoginMgr.setTCLoginCallback(this);
//        mTCLoginMgr.imLogin(UserInfoUtils.getUid(), UserInfoUtils.getSid());
        LogUtil.i("-------onResume:" + canJumpImmediately);
        if (canJumpImmediately) {
            jumpActivity();
        }
        canJumpImmediately = true;
    }
}
