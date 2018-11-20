package com.huanglong.v3.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.huanglong.v3.R;

/**
 * Created by bin on 2018/5/15.
 * 展示二维码的dialog
 */

public class QRCodeDialog extends Dialog {

    private Context context;
    private View view;

    public QRCodeDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog);
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_qr_code, null);
        setContentView(view);
        this.setCanceledOnTouchOutside(true);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        view.findViewById(R.id.dialog_qr_code_img_rel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    /**
     * get View
     *
     * @param ResId
     * @return
     */
    public View getView(int ResId) {
        return view.findViewById(ResId);
    }


}
