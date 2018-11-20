package com.huanglong.v3.live;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;

import com.gyf.barlibrary.ImmersionBar;
import com.huanglong.v3.R;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.live.utils.TCUtils;
import com.huanglong.v3.live.videochoose.ErrorDialogFragment;

import org.xutils.x;

/**
 * Created by bin on 2018/3/22.
 * 小直播的baseActivity
 */

public abstract class TCBaseActivity extends FragmentActivity {

    public ImmersionBar mImmersionBar;

    private static final String TAG = TCBaseActivity.class.getSimpleName();

    //错误消息弹窗
    private ErrorDialogFragment mErrDlgFragment;


    //被踢下线广播监听
    private LocalBroadcastManager mLocalBroadcatManager;
    private BroadcastReceiver mExitBroadcastReceiver;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        mImmersionBar = ImmersionBar.with(this);
//        mImmersionBar;   //所有子类都将继承这些相同的属性
        mImmersionBar.statusBarColor(R.color.transparent)
                .keyboardEnable(true)
                .init();
        mLocalBroadcatManager = LocalBroadcastManager.getInstance(this);
        mExitBroadcastReceiver = new ExitBroadcastRecevier();
        mLocalBroadcatManager.registerReceiver(mExitBroadcastReceiver, new IntentFilter(TCConstants.EXIT_APP));

        mErrDlgFragment = new ErrorDialogFragment();

        initView();
        logic();

    }


    /**
     * 初始化设置控件
     */
    protected abstract void initView();

    /**
     * 逻辑
     */
    protected abstract void logic();

    public void onReceiveExitMsg() {
        TCUtils.showKickOutDialog(this);
    }

    public class ExitBroadcastRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TCConstants.EXIT_APP)) {
                //在被踢下线的情况下，执行退出前的处理操作：停止推流、关闭群组
                onReceiveExitMsg();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcatManager.unregisterReceiver(mExitBroadcastReceiver);
    }


    protected void showErrorAndQuit(String errorMsg) {

        if (!mErrDlgFragment.isAdded() && !this.isFinishing()) {
            Bundle args = new Bundle();
            args.putString("errorMsg", errorMsg);
            mErrDlgFragment.setArguments(args);
            mErrDlgFragment.setCancelable(false);

            //此处不使用用.show(...)的方式加载dialogfragment，避免IllegalStateException
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(mErrDlgFragment, "loading");
            transaction.commitAllowingStateLoss();
        }
    }

}
