package com.huanglong.v3.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.model.circle.PraiseBean;

import java.util.List;

/**
 * Created by bin on 2018/7/4.
 */

@SuppressLint("AppCompatCustomView")
public class LikesView extends TextView {

    private Context mContext;
    private List<PraiseBean> list;

    public LikesView(Context context) {
        this(context, null);
    }

    public LikesView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    /**
     * 设置点赞数据
     *
     * @param list
     */
    public void setList(List<PraiseBean> list) {
        this.list = list;
    }

    /**
     * 刷新点赞列表
     */
    public void notifyDataSetChanged() {
        if (list == null || list.size() <= 0) {
            return;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(setImageSpan());
        for (int i = 0; i < list.size(); i++) {
            PraiseBean item = list.get(i);
            String nickname = item.getNickname();
            if (TextUtils.isEmpty(nickname)){
                continue;
            }
            builder.append(setClickableSpan(item.getNickname(), item));
            if (i != list.size() - 1) {
                builder.append(" , ");
            } else {
                builder.append(" ");
            }
        }

        setText(builder);
        setMovementMethod(new CircleMovementMethod(0xffcccccc, 0xffcccccc));
//        setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 设置评论用户名字点击事件
     *
     * @param item
     * @param bean
     * @return
     */
    public SpannableString setClickableSpan(final String item, final PraiseBean bean) {
        final SpannableString string = new SpannableString(item);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // TODO: 2017/9/3 评论用户名字点击事件
//                ToastUtils.showToast(bean.getNickname());
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(bean);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                // 设置显示的文字颜色
                ds.setColor(ContextCompat.getColor(V3Application.getInstance(), R.color.gray_888888));
                ds.setUnderlineText(false);
            }
        };

        string.setSpan(span, 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return string;
    }

    /**
     * 设置点赞图标
     *
     * @return
     */
    private SpannableString setImageSpan() {
        String text = "  ";
        SpannableString imgSpanText = new SpannableString(text);
        imgSpanText.setSpan(new ImageSpan(getContext(), R.mipmap.icon_zan, DynamicDrawableSpan.ALIGN_BASELINE),
                0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return imgSpanText;
    }


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void OnItemClick(PraiseBean praiseBean);
    }

}
