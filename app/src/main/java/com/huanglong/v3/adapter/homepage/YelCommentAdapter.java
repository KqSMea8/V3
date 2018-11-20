package com.huanglong.v3.adapter.homepage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.home.YelCommentBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/6/8.
 * 黄页评论的adapter
 */

public class YelCommentAdapter extends RecyclerView.Adapter<YelCommentAdapter.ViewHolder> {

    private List<YelCommentBean> yelCommentBeans;
    private ItemClickListener itemClickListener;


    public void setData(List<YelCommentBean> yelCommentBeans) {
        this.yelCommentBeans = yelCommentBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_yel_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(yelCommentBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (yelCommentBeans != null) {
            return yelCommentBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_avatar, img_type;
        private TextView tv_nickname, tv_content, tv_time;

        public ViewHolder(View itemView) {
            super(itemView);

            img_avatar = itemView.findViewById(R.id.item_yel_comment_avatar);
            tv_nickname = itemView.findViewById(R.id.item_yel_comment_nickname);
            tv_content = itemView.findViewById(R.id.item_yel_comment_content);
            tv_time = itemView.findViewById(R.id.item_yel_comment_time);
            img_type = itemView.findViewById(R.id.item_yel_comment_type);

        }

        public void setData(YelCommentBean yelCommentBean, int position) {

            x.image().bind(img_avatar, yelCommentBean.getHead_image(), MImageOptions.getCircularImageOptions());
            tv_nickname.setText(yelCommentBean.getNickname());
            tv_content.setText(yelCommentBean.getContent());
            tv_time.setText(yelCommentBean.getCreate_time());

            int type = yelCommentBean.getType();
            if (type == 2) {
                img_type.setVisibility(View.VISIBLE);
            } else {
                img_type.setVisibility(View.GONE);
            }


            img_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(yelCommentBean, position);
                    }
                }
            });

        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
