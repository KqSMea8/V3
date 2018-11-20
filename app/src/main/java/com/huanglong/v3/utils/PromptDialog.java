package com.huanglong.v3.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huanglong.v3.R;

/**
 * Created by bin on 2018/1/27.
 * 各种提示框的 dialog
 */

public class PromptDialog extends Dialog {

    private final View view;
    private Context context;
    private int type;


    private TextView tv_title, tv_content;
    private TextView btn_cancel, btn_confirm;
    private LinearLayout lin_cancel;
    private RelativeLayout dialog_lin;

    private OnClickListener onClickListener;

    private boolean isCancelOutSide = true;//点击外面是都取消

    /**
     * @param context
     */
    public PromptDialog(Context context, int layout) {
        super(context, R.style.custom_dialog);
        this.context = context;
        view = LayoutInflater.from(context).inflate(layout, null);
        setContentView(view);
        this.setCanceledOnTouchOutside(true);
//        initView();
    }

    /**
     * 设置点击外面是都取消
     *
     * @param isCancelOutSide
     */
    public void setCancelOutSide(boolean isCancelOutSide) {
        this.isCancelOutSide = isCancelOutSide;
    }

//    /**
//     * 初始化设置控件
//     */
//    private void initView() {
//        dialog_lin = view.findViewById(R.id.dialog_comment_lin);
//        tv_title = view.findViewById(R.id.dialog_title);
//        tv_content = view.findViewById(R.id.dialog_comment_content);
//        btn_cancel = view.findViewById(R.id.dialog_comment_cancel);
//        btn_confirm = view.findViewById(R.id.dialog_comment_confirm);
////        lin_cancel = view.findViewById(R.id.dialog_cancel_lin);
//
//        btn_cancel.setOnClickListener(this);
//        btn_confirm.setOnClickListener(this);
//        dialog_lin.setOnClickListener(this);
//    }


    public View getView(int id) {
        return view.findViewById(id);
    }


//    /**
//     * 设置dialog文字显示
//     *
//     * @param title
//     * @param content
//     * @param btnLeft
//     * @param btnRight
//     */
//    public void setDialogStyle(String title, String content, String btnLeft, String btnRight) {
//        tv_title.setText(title);
//        tv_content.setText(content);
//        if (TextUtils.isEmpty(btnLeft)) {
//            lin_cancel.setVisibility(View.GONE);
//        } else {
//            lin_cancel.setVisibility(View.VISIBLE);
//            btn_cancel.setText(btnLeft);
//        }
//        btn_confirm.setText(btnRight);
//    }


//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.dialog_comment_confirm:
//                onClickListener.onClick(2);
//                if (isCancelOutSide) {
//                    dismiss();
//                }
//                break;
//            case R.id.dialog_comment_cancel:
//                onClickListener.onClick(1);
//                dismiss();
//                break;
//            case R.id.dialog_comment_lin:
//                if (isCancelOutSide) {
//                    dismiss();
//                }
//                break;
//        }
//    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public interface OnClickListener {
        void onClick(int flag);
    }

}
