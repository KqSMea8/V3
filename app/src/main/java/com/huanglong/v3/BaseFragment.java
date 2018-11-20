package com.huanglong.v3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huanglong.v3.view.LoadingDialog;

import org.xutils.x;

/**
 * Created by hbb on 2017/6/28.
 * baseFragment
 */

public abstract class BaseFragment extends Fragment {

    private View mParentView;

    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mParentView == null) {
            mParentView = getContentView(inflater, container, savedInstanceState);
            x.view().inject(this, mParentView);
            initView();
        }
//        else {
//            ((ViewGroup) mParentView.getParent()).removeView(mParentView);
//        }
        return mParentView;
    }

    protected abstract View getContentView(LayoutInflater inflater, ViewGroup container,
                                           Bundle savedInstanceState);


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        logic();
    }


    protected abstract void initView();

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

}
