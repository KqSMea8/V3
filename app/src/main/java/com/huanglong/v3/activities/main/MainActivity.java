package com.huanglong.v3.activities.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.activities.circle.ReleaseCircleActivity;
import com.huanglong.v3.activities.homepage.LiveActivity;
import com.huanglong.v3.activities.login.LoginActivity;
import com.huanglong.v3.activities.login.WelcomeActivity;
import com.huanglong.v3.im.presenter.FriendshipManagerPresenter;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.smallvideo.EffectActivity;
import com.huanglong.v3.song.SongEffActivity;
import com.huanglong.v3.utils.Common;
import com.huanglong.v3.utils.Constant;
import com.huanglong.v3.utils.PromptDialog;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.view.PromptEditDialog;
import com.hubcloud.adhubsdk.AdListener;
import com.hubcloud.adhubsdk.SplashAd;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendAllowType;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


@ContentView(R.layout.activity_main)
public class MainActivity extends BaseFragmentActivity {

    public static MainActivity instance = null;

    @ViewInject(R.id.main_tabs_radio_group)
    private RadioGroup main_tabs;
    @ViewInject(R.id.close)
    private ImageView mCloseButton;
    @ViewInject(R.id.panel)
    private LinearLayout mPanelView;
    @ViewInject(R.id.main_rel)
    public RelativeLayout main_rel;
    @ViewInject(R.id.main_adsFl)
    private FrameLayout adsParent;

    private SplashAd splashAd;

    private TextView tv_dialog_content;

    public boolean isReward = false;


    private PromptDialog dialog;


    private Animation mCloseRotateAnimation;

    private WeChatBroadcastReceiver weChatBroadcastReceiver;

    private final Fragment fragmentArray[] = {new MsgFragment(), new FriendsFragment(), new HomePageFragment(), new MineFragment()};

    private Timer timer;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //do something
                LogUtil.i("-----time:" + System.currentTimeMillis());
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected Activity getActivity() {
        return this;
    }


    @Override
    protected void initView() {

        mImmersionBar.statusBarColor(R.color.orange_FC6C57)
                .keyboardEnable(true)
                .init();

        //获取Fragment管理器
        FragmentManager manager = getSupportFragmentManager();
        //获取事物(使用v4包下)
        FragmentTransaction transaction = manager.beginTransaction();
        //默认选中HomepageFragment替换Framelayout
        transaction.replace(R.id.contentPanel, fragmentArray[0]);
        //提交事物
        transaction.commit();

        //默认点击首页
        main_tabs.check(R.id.tab_msg);

        main_tabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                //写法与默认点击页面的相同
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                switch (checkedId) {
                    case R.id.tab_msg:
                        transaction.replace(R.id.contentPanel, fragmentArray[0]);
                        break;
                    case R.id.tab_friends:
                        transaction.replace(R.id.contentPanel, fragmentArray[1]);
                        break;
                    case R.id.tab_recommend:
                        transaction.replace(R.id.contentPanel, fragmentArray[2]);
                        break;
                    case R.id.tab_mine:
                        transaction.replace(R.id.contentPanel, fragmentArray[3]);
                        break;
                }
                transaction.commit();
            }
        });
        initDialog();
        mCloseRotateAnimation = AnimationUtils.loadAnimation(this, R.anim.close_rotate);
        File externalFilesDir = this.getExternalFilesDir(null);
        String licenceFile = externalFilesDir.getAbsolutePath() + "/" + "TXUgcSDK.licence";
        File fileLicence = new File(licenceFile);
        if (!fileLicence.exists()) {
            getLicence();
        }

    }

    /**
     * 切换tab
     *
     * @param index
     */
    public void switchTab(int index) {
        switch (index) {
            case 0:
                main_tabs.check(R.id.tab_msg);
                break;
            case 1:
                main_tabs.check(R.id.tab_friends);
                break;
            case 2:
                main_tabs.check(R.id.tab_recommend);
                break;
            case 3:
                main_tabs.check(R.id.tab_mine);
                break;
        }
    }

    private long time;


    @Override
    protected void logic() {
        instance = this;
        weChatBroadcastReceiver = new WeChatBroadcastReceiver();

        registerReceiver();


        FriendshipManagerPresenter.setFriendAllowType(TIMFriendAllowType.TIM_FRIEND_NEED_CONFIRM, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("---设置加好友验证失败," + i + " " + s);
                tv_dialog_content.setText("该账号在其他设备登录过，请重新登录");
                dialog.show();
            }

            @Override
            public void onSuccess() {
                LogUtil.e("---设置加好友验证成功");
            }
        });
        showSplash();
        timer = new Timer();
        timer.schedule(new RequestTimerTask(), 1000, 5*60*1000);

    }

    /**
     * 展示开屏广告
     */
    private void showSplash() {
        splashAd = new SplashAd(MainActivity.this, adsParent, listener, Constant.Ad_SPLASH_ID);
        splashAd.setCloseButtonPadding(10, 20, 10, 10);
    }

    /**
     * 初始化dialog
     */
    private void initDialog() {
        dialog = new PromptDialog(getActivity(), R.layout.dialog_hint_currency);
        tv_dialog_content = (TextView) dialog.getView(R.id.dialog_comment_content);
        dialog.getView(R.id.dialog_comment_cancel).setVisibility(View.GONE);
        dialog.getView(R.id.dialog_comment_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });
//        dialog.getView(R.id.dialog_comment_lin).setOnClickListener(this);

    }

    @Event(value = {R.id.tab_plus, R.id.close, R.id.panel, R.id.plus_circle, R.id.plus_live, R.id.plus_k_song, R.id.plus_video})
    private void onClick(View view) {

        switch (view.getId()) {
            case R.id.tab_plus:
                openPanelView();
                break;
            case R.id.close:
                closePanelView();
                break;
            case R.id.panel:
                closePanelView();
                break;
            case R.id.plus_circle:
                closePanelView();
                Intent intent = new Intent();
                intent.setClass(this, ReleaseCircleActivity.class);
                startActivity(intent);
                break;
            case R.id.plus_live:
                closePanelView();
                Intent intent1 = new Intent();
                intent1.setClass(this, LiveActivity.class);
                startActivity(intent1);
                break;
            case R.id.plus_k_song:
                closePanelView();
                Intent intent2 = new Intent();
                intent2.setClass(this, SongEffActivity.class);
                startActivity(intent2);
                break;
            case R.id.plus_video:
                closePanelView();
                Intent intent3 = new Intent();
                intent3.setClass(this, EffectActivity.class);
                startActivity(intent3);
                break;
        }
    }


    /**
     * 广告监听
     */
    private AdListener listener = new AdListener() {
        @Override
        public void onAdLoaded() {
            LogUtil.i("----onAdLoaded");
        }

        @Override
        public void onAdShown() {
            LogUtil.i("----onAdShown");
//            if (timer != null) {
//                timer.cancel();
//            }
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            LogUtil.i("----onAdFailedToLoad");
//            if (timer != null) {
//                timer.schedule(task, 200000, 200000);
//            }
        }

        @Override
        public void onAdClosed() {
            LogUtil.i("----onAdDismissed");
//            if (timer != null) {
//                timer.schedule(task, 200000, 200000);
//            }
        }

        @Override
        public void onAdClicked() {
            LogUtil.i("----onAdClick");
        }
    };

    // 打开面板视图
    private void openPanelView() {
        mPanelView.setVisibility(View.VISIBLE);

        mCloseButton.startAnimation(mCloseRotateAnimation);
    }


    // 关闭面板视图
    private void closePanelView() {
        mPanelView.setVisibility(View.GONE);
    }


    /**
     * 注册微信分享广播
     */

    private void registerReceiver() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.PAY_WECHAT_ACTION);
        getActivity().registerReceiver(weChatBroadcastReceiver, filter);
    }

    /**
     * 微信支付成功后接收广播处理
     *
     * @author hbb
     */
    private class WeChatBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Common.PAY_WECHAT_ACTION)) return;
            String type = intent.getStringExtra(Common.PAY_WECHAT_KEY);
            if (TextUtils.equals("success", type)) {
                if (isReward) {
                    ToastUtils.showToast("打赏成功");
                }
                FriendsFragment fragment = (FriendsFragment) fragmentArray[1];
                fragment.onRefresh();
            } else if (TextUtils.equals("cancel", type)) {
                ToastUtils.showToast("取消支付");
            } else {
                ToastUtils.showToast("支付失败");
            }
            isReward = false;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (weChatBroadcastReceiver != null) {
            MainActivity.this.unregisterReceiver(weChatBroadcastReceiver);
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    /**
     * 获取
     */
    private void getLicence() {
        File externalFilesDir = this.getExternalFilesDir(null);
        RequestParams params = new RequestParams(Api.licenceUrl);
        params.setSaveFilePath(externalFilesDir.getAbsolutePath() + "/" + "TXUgcSDK.licence");
        x.http().get(params, new Callback.ProgressCallback<File>() {
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
                LogUtil.e("----licence success:" + result.getAbsolutePath());
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


    class RequestTimerTask extends TimerTask {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);

        }
    }


}
