package com.huanglong.v3.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.huanglong.v3.R;

/**
 * Created by bin on 2017/12/2.
 * 分享pop window
 */

public class PopShareUtils extends PopupWindow {


    public static int WECHAT_CIRCLE = 1;// 微信朋友圈
    public static int WECHAT_FRIENDS = 2;// 微信好友
    public static int QQ = 3;// QQ
    public static int QQ_ZONE = 4;// QQ空间
    public static int COPY_LINK = 5;// 复制链接

    private Context context;
    private View mMenuView;

    private OnClickListener onClickListener;

    public PopShareUtils(Context context) {
        this.context = context;
        initView();
    }

    /***
     * 初始化UI
     */
    private void initView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_share_layout, null);
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
        // 单击弹出窗以外处 关闭弹出窗
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.pop_share_lin).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        mMenuView.findViewById(R.id.pop_share_wechat_circle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(WECHAT_CIRCLE);
                dismiss();
            }
        });
        mMenuView.findViewById(R.id.pop_share_wechat_friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(WECHAT_FRIENDS);
                dismiss();
            }
        });

        mMenuView.findViewById(R.id.pop_share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(QQ);
                dismiss();
            }
        });

        mMenuView.findViewById(R.id.pop_share_qq_zone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(QQ_ZONE);
                dismiss();
            }
        });

        mMenuView.findViewById(R.id.pop_share_copy_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(COPY_LINK);
                dismiss();
            }
        });


    }


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public int getWECHAT_CIRCLE() {
        return WECHAT_CIRCLE;
    }

    public void setWECHAT_CIRCLE(int WECHAT_CIRCLE) {
        this.WECHAT_CIRCLE = WECHAT_CIRCLE;
    }


    public interface OnClickListener {
        void onClick(int type);
    }

}
