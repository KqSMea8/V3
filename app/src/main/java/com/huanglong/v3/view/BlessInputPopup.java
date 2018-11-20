package com.huanglong.v3.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.huanglong.v3.R;
import com.huanglong.v3.activities.message.BlePacDetActivity;
import com.huanglong.v3.utils.KeyBoardUtils;

/**
 * Created by bin on 2018/3/18.
 * 福包输入pop
 */

public class BlessInputPopup extends PopupWindow {


    private Context context;
    private View mMenuView;
    private EditText edt_koulin;

    private OnClickListener onClickListener;

    public BlessInputPopup(Context context) {
        this.context = context;
        initView();
    }

    /***
     * 初始化设置控件
     */
    private void initView() {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_bless_layout, null);

        edt_koulin = mMenuView.findViewById(R.id.pop_bless_koulin);


        // 导入布局
        this.setContentView(mMenuView);
        // 设置动画效果
        this.setAnimationStyle(R.style.BottomPopAnim);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置可触
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x0ffffff);
        this.setBackgroundDrawable(dw);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 单击弹出窗以外处 关闭弹出窗
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.pop_bless_bottom).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        KeyBoardUtils.hideKeyboard(BlePacDetActivity.instance);
                        dismiss();
                    }
                }
                return true;
            }
        });

        mMenuView.findViewById(R.id.pop_bless_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyBoardUtils.hideKeyboard(BlePacDetActivity.instance);
                onClickListener.onClick(edt_koulin.getText().toString().trim());
                dismiss();
                edt_koulin.setText("");
            }
        });

    }

    public EditText getEditText() {
        return edt_koulin;
    }


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(String str);
    }

}
