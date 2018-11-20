package com.huanglong.v3.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.huanglong.v3.R;
import com.huanglong.v3.utils.CommentChatView;

/**
 * Created by bin on 2018/4/13.
 * 自定义pop
 */

public class CustomPop extends PopupWindow {

    private Context context;
    private View mMenuView;


    public CustomPop(Context context, int layoutId) {
        this.context = context;
        initView(layoutId);
    }

    /***
     * 初始化设置控件
     */
    private void initView(int layoutId) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(layoutId, null);

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
//        mMenuView.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
////                int height = mMenuView.findViewById(R.id.pop_order_lin).getTop();
////                int y = (int) event.getY();
////                if (event.getAction() == MotionEvent.ACTION_UP) {
////                    if (y < height) {
////                        dismiss();
////                    }
////                }
//                return true;
//            }
//        });
    }


    public View getView(int ResId) {
        return mMenuView.findViewById(ResId);
    }


    public void setChatInput(CommentChatView commentChatView) {
        CommentInput commentInput = mMenuView.findViewById(R.id.input_panel);
        if (commentInput != null) {
            commentInput.setChatView(commentChatView);
        }
    }


}
