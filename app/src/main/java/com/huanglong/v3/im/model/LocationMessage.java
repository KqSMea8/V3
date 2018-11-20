package com.huanglong.v3.im.model;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.conversation.ShowPlaceActivity;
import com.huanglong.v3.im.adapter.ChatAdapter;
import com.huanglong.v3.voice.utils.PxUtil;
import com.tencent.TIMLocationElem;
import com.tencent.TIMMessage;

/**
 * 位置消息
 */
public class LocationMessage extends Message {


    public LocationMessage(TIMMessage message) {
        this.message = message;
    }

    public LocationMessage(String des, double lat, double lon) {
        message = new TIMMessage();
        TIMLocationElem elem = new TIMLocationElem();
        elem.setDesc(des);
        elem.setLatitude(lat);
        elem.setLongitude(lon);
        message.addElement(elem);
    }


    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        clearView(viewHolder);
        if (checkRevoke(viewHolder)) return;
        TIMLocationElem e = (TIMLocationElem) message.getElement(0);

        LinearLayout linearLayout = new LinearLayout(V3Application.getInstance());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(V3Application.getInstance());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setTextColor(V3Application.getInstance().getResources().getColor(isSelf() ? R.color.white : R.color.black));
        tv.setText(e.getDesc());
        linearLayout.addView(tv);
        ImageView imageView = new ImageView(V3Application.getInstance());
        imageView.setBackgroundResource(R.mipmap.icon_default_map);
        linearLayout.addView(imageView);

        getBubbleView(viewHolder).addView(linearLayout);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        lp.width = PxUtil.dip2px(V3Application.getInstance(), 200);
        linearLayout.setLayoutParams(lp);
        LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        lp2.width = PxUtil.dip2px(V3Application.getInstance(), 200);
        lp2.height = PxUtil.dip2px(V3Application.getInstance(), 80);
        imageView.setLayoutParams(lp2);

        getBubbleView(viewHolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navToLocation(e, context);
            }
        });

        showStatus(viewHolder);


    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        String str = getRevokeSummary();
        if (str != null) return str;
        return V3Application.getInstance().getString(R.string.summary_location);
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    /**
     * 跳转位置预览页面
     *
     * @param locationElem
     * @param context
     */
    private void navToLocation(final TIMLocationElem locationElem, final Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ShowPlaceActivity.class);
        intent.putExtra("des", locationElem.getDesc());
        intent.putExtra("lat", locationElem.getLatitude());
        intent.putExtra("lon", locationElem.getLongitude());
        context.startActivity(intent);
    }
}
