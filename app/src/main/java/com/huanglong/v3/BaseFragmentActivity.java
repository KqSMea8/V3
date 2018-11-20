package com.huanglong.v3;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.gyf.barlibrary.ImmersionBar;
import com.huanglong.v3.view.LoadingDialog;

import org.xutils.x;

/**
 * Created by bin on 2018/1/12.
 * BaseFragmentActivity
 */

public abstract class BaseFragmentActivity extends FragmentActivity {


    public ImmersionBar mImmersionBar;
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        mImmersionBar = ImmersionBar.with(this);
//        mImmersionBar;   //所有子类都将继承这些相同的属性
        mImmersionBar.statusBarColor(R.color.orange_FC6C57)
                .keyboardEnable(true)
                .init();
        loadingDialog = new LoadingDialog(this);
        initView();
        logic();
    }

    /**
     * 初始化设置控件
     */
    protected abstract Activity getActivity();


    /**
     * 初始化设置控件
     */
    protected abstract void initView();

    /**
     * 逻辑
     */
    protected abstract void logic();


    /**
     * 显示press
     */
    protected void showDialog() {
        if (loadingDialog != null) {
            loadingDialog.showDialog();
        }
    }

    /**
     * 隐藏press
     */
    protected void dismissDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismissDialog();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null)
            mImmersionBar.destroy();  //必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
    }

}
