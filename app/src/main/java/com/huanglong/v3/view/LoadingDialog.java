package com.huanglong.v3.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huanglong.v3.R;

/**
 * Created by bin on 2017/11/28.
 * 加载中...
 */

public class LoadingDialog {

    private Dialog dialog;

    public LoadingDialog(Context context) {
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


    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }


    private void initDialog(Context context) {
        final View diaView = View.inflate(context, R.layout.dialog_loading_layout, null);
        dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(diaView);
        dialog.setCanceledOnTouchOutside(false);
        ImageView img = diaView.findViewById(R.id.dialog_loading_img);
        Glide.with(context).load(R.mipmap.loading_bg).into(img);
    }

}
