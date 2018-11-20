package com.huanglong.v3;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.gyf.barlibrary.ImmersionBar;
import com.huanglong.v3.view.LoadingDialog;

import org.xutils.x;

/**
 * 5
 * Created by bin on 2018/1/4.
 * baseActivity
 */

public abstract class BaseActivity extends Activity {

    private LoadingDialog loadingDialog;


    public com.huanglong.v3.voice.custom.LoadingDialog mLoadingDialog;
    public ImmersionBar mImmersionBar;
    public Bundle savedInstanceState;


//    @ViewInject(R.id.title_name)
//    private TextView tv_name;
//    @ViewInject(R.id.title_rel)
//    private RelativeLayout title_rel;
//    @ViewInject(R.id.title_tv_right)
//    private TextView tv_right;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        this.savedInstanceState = savedInstanceState;
        mImmersionBar = ImmersionBar.with(this);
//        mImmersionBar;   //所有子类都将继承这些相同的属性
        mImmersionBar.statusBarColor(R.color.transparent)
                .keyboardEnable(true)
                .init();

        loadingDialog = new LoadingDialog(this);
        mLoadingDialog = com.huanglong.v3.voice.custom.LoadingDialog.getInstance(this);
        initView();
        logic();
    }


    /**
     * 初始化设置控件
     */
    protected abstract Activity getActivity();

//    /**
//     * 标题控件
//     *
//     * @return
//     */
//    public TextView getTitleText() {
//        return tv_name;
//    }
//
//    /**
//     * 右边 text 控件
//     *
//     * @return
//     */
//    public TextView getRightText() {
//        return tv_right;
//    }
//
//    /**
//     * 隐藏标题栏
//     */
//    public void setGoneTitle() {
//        title_rel.setVisibility(View.GONE);
//    }

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
        if (loadingDialog != null) {
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
