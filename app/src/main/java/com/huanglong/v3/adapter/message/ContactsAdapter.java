package com.huanglong.v3.adapter.message;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.model.contacts.ContactsBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.TransformationUtils;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/1/20.
 * 联系人的adapter
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {


    private ItemClickListener itemClickListener;
    private List<ContactsBean> contactsBeans;

    private int type;//1.好友列表 2.选择好友

    public ContactsAdapter(int type) {
        this.type = type;
    }


    public void setData(List<ContactsBean> contactsBeans) {
        this.contactsBeans = contactsBeans;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(contactsBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (contactsBeans != null) {
            return contactsBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name, tv_location, tv_age;
        private View view;
        private ImageView img_radio, img_avatar, img_enterprise;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_name = itemView.findViewById(R.id.item_contacts_name);
            tv_location = itemView.findViewById(R.id.item_contacts_location);
            img_radio = itemView.findViewById(R.id.item_contacts_radio);
            tv_age = itemView.findViewById(R.id.item_contacts_age);
            img_avatar = itemView.findViewById(R.id.item_contacts_avatar);
            img_enterprise = itemView.findViewById(R.id.item_contacts_enterprise);

        }

        public void setData(final ContactsBean contactsBean, final int position) {
            x.image().bind(img_avatar, contactsBean.getHead_image(), MImageOptions.getCircularImageOptions());
            tv_name.setText(contactsBean.getNickname());
            String age = contactsBean.getAge();
            if (TextUtils.isEmpty(age)) {
                tv_age.setText("0岁");
            } else {
                tv_age.setText(age + "岁");
            }
            double distance = contactsBean.getDistance();
            if (distance > 1000) {
                String dis = TransformationUtils.doubleDecimal(distance / 1000);
                tv_location.setText(dis + "km");
            } else {
                tv_location.setText(distance + "m");
            }

            int gender = contactsBean.getGender();
            if (gender == 1) {
                Drawable drawable = V3Application.getInstance().getResources().getDrawable(R.mipmap.icon_man);// 找到资源图片
                // 这一步必须要做，否则不会显示。
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置图片宽高
                tv_age.setCompoundDrawables(drawable, null, null, null);// 设置到控件中
            } else {
                Drawable drawable = V3Application.getInstance().getResources().getDrawable(R.mipmap.icon_woman);// 找到资源图片
                // 这一步必须要做，否则不会显示。
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置图片宽高
                tv_age.setCompoundDrawables(drawable, null, null, null);// 设置到控件中
            }

            int enterprise = contactsBean.getType();
            if (enterprise == 1) {
                img_enterprise.setVisibility(View.INVISIBLE);
            } else {
                img_enterprise.setVisibility(View.VISIBLE);
            }


            if (type == 2) {
                img_radio.setVisibility(View.VISIBLE);
                boolean selected = contactsBean.isSelected();
                if (selected) {
                    img_radio.setImageResource(R.mipmap.icon_radio_press);
                } else {
                    img_radio.setImageResource(R.mipmap.icon_radio_normal);
                }

            } else {
                img_radio.setVisibility(View.GONE);
            }


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(contactsBean, position);
                }
            });

        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
