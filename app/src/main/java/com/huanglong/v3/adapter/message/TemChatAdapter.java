package com.huanglong.v3.adapter.message;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.homepage.TemChatBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/5/11.
 * 临时聊天的adapter
 */

public class TemChatAdapter extends RecyclerView.Adapter<TemChatAdapter.ViewHolder> {

    private List<TemChatBean> temChatBeans;
    private ItemClickListener itemClickListener;


    private int SELF = 1;
    private int OTHER = 2;

    private int mPosition = -1;

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public void removeItem(int position) {
        temChatBeans.remove(position);
        notifyDataSetChanged();
    }

    public String getChatId(int position) {
        return temChatBeans.get(position).getId();
    }


    public void setData(List<TemChatBean> temChatBeans) {
        this.temChatBeans = temChatBeans;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == SELF) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_self, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_other, parent, false);
        }
        return new ViewHolder(view);
    }


    @Override
    public int getItemViewType(int position) {
        int sendered = temChatBeans.get(position).getSendered();
        if (sendered == 0) {
            return OTHER;
        } else {
            return SELF;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.getData(temChatBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (temChatBeans != null) {
            return temChatBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView tv_time, tv_content;
        private ImageView img_avatar;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_time = itemView.findViewById(R.id.item_chat_time);
            tv_content = itemView.findViewById(R.id.item_chat_content);
            img_avatar = itemView.findViewById(R.id.item_chat_avatar);

            view.setOnCreateContextMenuListener(this);

        }

        public void getData(TemChatBean temChatBean, int position) {
            tv_time.setText(temChatBean.getCreate_time());
            tv_content.setText(temChatBean.getContent());
            x.image().bind(img_avatar, temChatBean.getHead_image(), MImageOptions.getCircularImageOptions());

            img_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(temChatBean, position);
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mPosition = position;
                    return false;
                }
            });

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, 0, 0, "删除");
        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
