package com.huanglong.v3.im.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.im.model.FriendFuture;
import com.huanglong.v3.im.view.CircleImageView;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * 好友关系链管理消息adapter
 */
public class FriendManageMessageAdapter extends ArrayAdapter<FriendFuture> {

    private int resourceId;
    private View view;
    private ViewHolder viewHolder;
    private ItemTypeClickListener itemClickListener;


    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public FriendManageMessageAdapter(Context context, int resource, List<FriendFuture> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.avatar = (CircleImageView) view.findViewById(R.id.avatar);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.des = (TextView) view.findViewById(R.id.description);
            viewHolder.status = (TextView) view.findViewById(R.id.status);
            view.setTag(viewHolder);
        }
        Resources res = getContext().getResources();
        final FriendFuture data = getItem(position);
        x.image().bind(viewHolder.avatar, data.getFaceUrl(), MImageOptions.getCircularImageOptions());

        viewHolder.name.setText(data.getName());
        viewHolder.des.setText(data.getMessage());
        viewHolder.status.setTextColor(res.getColor(R.color.text_gray1));
        switch (data.getType()) {
            case TIM_FUTURE_FRIEND_PENDENCY_IN_TYPE:
                viewHolder.status.setText(res.getString(R.string.newfri_agree));
                viewHolder.status.setTextColor(res.getColor(R.color.text_blue1));
                viewHolder.status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemClickListener != null) {
                            itemClickListener.onItemClick(data, position, 1);
                        }
                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemClickListener != null) {
                            itemClickListener.onItemClick(data, position, 1);
                        }
                    }
                });

                break;
            case TIM_FUTURE_FRIEND_PENDENCY_OUT_TYPE:
                viewHolder.status.setText(res.getString(R.string.newfri_wait));
                break;
            case TIM_FUTURE_FRIEND_DECIDE_TYPE:
                viewHolder.status.setText(res.getString(R.string.newfri_accept));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemClickListener != null) {
                            itemClickListener.onItemClick(data, position, 2);
                        }
                    }
                });
                break;
        }


        return view;
    }


    public class ViewHolder {
        ImageView avatar;
        TextView name;
        TextView des;
        TextView status;
    }

    public void setOnItemClickListener(ItemTypeClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
