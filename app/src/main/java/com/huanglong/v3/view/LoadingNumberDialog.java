package com.huanglong.v3.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.huanglong.v3.R;

/**
 * Created by bin on 2017/11/28.
 * 加载中 带进度条...
 */

public class LoadingNumberDialog {

    private Dialog dialog;
    private CircleProgressBarView progressBarView;


    public LoadingNumberDialog(Context context) {
        initDialog(context);
    }


    public void showDialog() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    public void setProgress(int progress) {
        if (progressBarView != null) {
            progressBarView.setProgress(progress);
        }
    }

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }


    private void initDialog(Context context) {
        final View diaView = View.inflate(context, R.layout.dialog_loading_number_layout, null);
        dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(diaView);
        dialog.setCanceledOnTouchOutside(false);
        progressBarView = diaView.findViewById(R.id.loading_progress);
    }

}
