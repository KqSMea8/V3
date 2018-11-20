package com.huanglong.v3.view;

import android.app.Dialog;
import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huanglong.v3.R;


/**
 * Created by bin on 2018/1/27.
 * 各种编辑的 dialog
 */

public class PromptEditDialog extends Dialog implements View.OnClickListener {

    private final View view;
    private Context context;
    private int type;


    private TextView tv_title;
    private EditText edt_content;
    private Button btn_cancel, btn_confirm;
    private LinearLayout lin_cancel;
    private RelativeLayout dialog_lin;

    private OnClickListener onClickListener;

    /**
     * @param context
     */
    public PromptEditDialog(Context context) {
        super(context, R.style.custom_dialog);
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_prompt_edit, null);
        setContentView(view);
        this.setCanceledOnTouchOutside(true);
        initView();
    }

    /**
     * 初始化设置控件
     */
    private void initView() {
        dialog_lin = view.findViewById(R.id.dialog_edt_lin);
        tv_title = view.findViewById(R.id.dialog_edt_title);
        edt_content = view.findViewById(R.id.dialog_edt_content);
        btn_cancel = view.findViewById(R.id.dialog_edt_cancel);
        btn_confirm = view.findViewById(R.id.dialog_edt_confirm);
        lin_cancel = view.findViewById(R.id.dialog_edt_cancel_lin);

        btn_cancel.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        dialog_lin.setOnClickListener(this);
    }


    /**
     * 设置dialog文字显示
     *
     * @param title
     * @param btnLeft
     * @param btnRight
     * @param type     输入类型 1.文本类型 ，2.数字类型
     */
    public void setDialogStyle(String title, String btnLeft, String btnRight, String hint, int type, int length) {
        tv_title.setText(title);
        edt_content.setHint(hint);
        if (length > 0) {
            edt_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        }
        if (TextUtils.isEmpty(btnLeft)) {
            lin_cancel.setVisibility(View.GONE);
        } else {
            lin_cancel.setVisibility(View.VISIBLE);
            btn_cancel.setText(btnLeft);
        }
        btn_confirm.setText(btnRight);
        if (type == 1) {
            edt_content.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            edt_content.setInputType((InputType.TYPE_CLASS_NUMBER));
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_edt_confirm:
                String content = edt_content.getText().toString().trim();
                onClickListener.onClick(2, content);
                dismiss();
                break;
            case R.id.dialog_edt_cancel:
                dismiss();
                break;
            case R.id.dialog_edt_lin:
                dismiss();
                break;
        }
        edt_content.setText("");
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public interface OnClickListener {
        void onClick(int flag, String str);

    }

}
