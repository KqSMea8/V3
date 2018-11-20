package com.huanglong.v3.adapter.circle;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.homepage.PersonalPageActivity;
import com.huanglong.v3.model.circle.CommentBean;
import com.huanglong.v3.utils.ItemClickListener;

import java.util.List;

/**
 * Created by bin on 2018/4/13.
 * 评论的adapter
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<CommentBean> commentBeans;
    private ItemClickListener itemClickListener;


    public void setData(List<CommentBean> commentBeans) {
        this.commentBeans = commentBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(commentBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (commentBeans != null) {
            return commentBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_comment;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_comment = itemView.findViewById(R.id.item_comment_tv);
        }

        public void setData(CommentBean commentBean, int position) {
            String reply_user_id = commentBean.getReply_user_id();
            if (TextUtils.isEmpty(reply_user_id)) {
                String content = commentBean.getNickname() + ":" + commentBean.getContent();

//                TextMessage msg = new TextMessage(new SpannableStringBuilder(content));
//                SpannableStringBuilder builder = TextMessage.getMsgStr(msg.getMessage(), V3Application.getInstance());
                SpannableStringBuilder builder = new SpannableStringBuilder(content);
                builder.setSpan(new ReplyClick(commentBean, position), commentBean.getNickname().length(), content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new CommentClick(commentBean.getNickname(), commentBean.getMember_id()), 0, commentBean.getNickname().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                //一定要记得设置，不然点击不生效
                tv_comment.setMovementMethod(LinkMovementMethod.getInstance());
                tv_comment.setText(builder);
            } else {
                String content = commentBean.getNickname() + "回复" + commentBean.getReply_nickname() + ":" + commentBean.getContent();
                SpannableStringBuilder builder = new SpannableStringBuilder(content);
                builder.setSpan(new CommentClick(commentBean.getNickname(), commentBean.getMember_id()), 0, commentBean.getNickname().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new CommentClick(commentBean.getReply_nickname(), commentBean.getReply_user_id()), commentBean.getNickname().length() + 2, commentBean.getNickname().length() + 2 + commentBean.getReply_nickname().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new ReplyClick(commentBean, position), commentBean.getNickname().length() + 2 + commentBean.getReply_nickname().length(), content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_comment.setMovementMethod(LinkMovementMethod.getInstance());
                tv_comment.setText(builder);
            }

//            tv_comment.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (itemClickListener != null) {
//                        itemClickListener.onItemClick(commentBean, position);
//                    }
//                }
//            });

        }
    }


    private class CommentClick extends ClickableSpan {

        private String commentName;
        private String commentId;

        public CommentClick(String commentName, String commentId) {
            this.commentName = commentName;
            this.commentId = commentId;
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent();
            intent.setClass(V3Application.getInstance(), PersonalPageActivity.class);
            intent.putExtra("uid", commentId);
            V3Application.getInstance().startActivity(intent);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ContextCompat.getColor(V3Application.getInstance(), R.color.blue_45C3FB));
        }
    }


    private class ReplyClick extends ClickableSpan {

        private CommentBean commentBean;
        private int position;

        public ReplyClick(CommentBean commentBean, int position) {
            this.commentBean = commentBean;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(commentBean, position);
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ContextCompat.getColor(V3Application.getInstance(), R.color.gray_33));
        }

    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
