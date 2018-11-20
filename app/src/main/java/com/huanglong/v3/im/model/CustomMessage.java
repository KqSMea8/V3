package com.huanglong.v3.im.model;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.conversation.InvitationH5Activity;
import com.huanglong.v3.im.adapter.ChatAdapter;
import com.huanglong.v3.model.contacts.InviteCustomBean;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.voice.utils.PxUtil;
import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;

/**
 * 自定义消息
 */
public class CustomMessage extends Message {


    private String TAG = getClass().getSimpleName();

    private final int TYPE_TYPING = 14;

    private Type type;
    private String desc;
    private String data;

    public CustomMessage(TIMMessage message) {
        this.message = message;
        TIMCustomElem elem = (TIMCustomElem) message.getElement(0);
        String desc = elem.getDesc();
        String s = new String(elem.getData());
//        String s = elem.getData().toString();
//        LogUtil.e("---custom msg " + s);
//        type = Type.INVITE;
        if (TextUtils.equals("邀请你加入群聊", desc)) {
            type = Type.INVITE;
//            parse(elem.getData());
        } else if (TextUtils.equals("[名片]", desc)) {
            type = Type.CARD;
        }
    }

    public CustomMessage(Type type, String data) {
        this.type = type;
        message = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(data.getBytes());
        if (type == Type.INVITE) {
            elem.setDesc("邀请你加入群聊");
        } else if (type == Type.CARD) {
            elem.setDesc("[名片]");
        }
//        if (type == Type.INVITE) {
//            elem.setDesc("邀请你加入群聊");
//        }
        message.addElement(elem);
    }


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    private void parse(byte[] data) {
        String str = "";
        try {
            str = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(str)) return;

        if (type == Type.INVITE) {
            Gson gson = V3Application.getGson();
            InviteCustomBean inviteCustomBean = gson.fromJson(str, InviteCustomBean.class);
        } else {
            type = Type.INVALID;
            try {
                JSONObject jsonObj = new JSONObject(str);
                int action = jsonObj.getInt("userAction");
                switch (action) {
                    case TYPE_TYPING:
                        type = Type.TYPING;
                        this.data = jsonObj.getString("actionParam");
                        if (this.data.equals("EIMAMSG_InputStatus_End")) {
                            type = Type.INVALID;
                        }
                        break;
                }

            } catch (JSONException e) {
                Log.e(TAG, "parse json error");

            }
        }
    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        switch (type) {
            case INVITE:
                showInvitation(viewHolder, context);
                break;
            case CARD:
                showInvitation(viewHolder, context);
                break;
        }
    }

    /**
     * 显示群邀请item
     *
     * @param viewHolder
     * @param context
     */
    private void showInvitation(ChatAdapter.ViewHolder viewHolder, Context context) {
        clearView(viewHolder);
        if (checkRevoke(viewHolder)) return;
        TIMCustomElem timCustomElem = (TIMCustomElem) message.getElement(0);
        String s = new String(timCustomElem.getData());

        Gson gson = V3Application.getGson();
        InviteCustomBean inviteCustomBean = gson.fromJson(s, InviteCustomBean.class);


        LinearLayout linearLayout = new LinearLayout(V3Application.getInstance());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(V3Application.getInstance());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setTextColor(V3Application.getInstance().getResources().getColor(isSelf() ? R.color.white : R.color.black));
        tv.setText(timCustomElem.getDesc());
        linearLayout.addView(tv);

        LinearLayout linearLayout2 = new LinearLayout(V3Application.getInstance());
        linearLayout2.setOrientation(LinearLayout.HORIZONTAL);

        TextView tv2 = new TextView(V3Application.getInstance());
        tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tv2.setTextColor(V3Application.getInstance().getResources().getColor(R.color.gray_66));
        tv2.setText(inviteCustomBean.getDes());
        linearLayout2.addView(tv2);


        ImageView imageView = new ImageView(V3Application.getInstance());
        x.image().bind(imageView, inviteCustomBean.getGroupAvatar(), MImageOptions.getNormalImageOptions());
        linearLayout2.addView(imageView);

        linearLayout.addView(linearLayout2);

        getBubbleView(viewHolder).addView(linearLayout);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        lp.width = PxUtil.dip2px(V3Application.getInstance(), 200);
        linearLayout.setLayoutParams(lp);

        LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        lp2.width = PxUtil.dip2px(V3Application.getInstance(), 40);
        lp2.height = PxUtil.dip2px(V3Application.getInstance(), 40);
        lp2.leftMargin = PxUtil.dip2px(V3Application.getInstance(), 10);
        imageView.setLayoutParams(lp2);

        LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        lp3.width = PxUtil.dip2px(V3Application.getInstance(), 140);
        tv2.setLayoutParams(lp3);

        getBubbleView(viewHolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navToLocation(inviteCustomBean, context);
            }
        });

        showStatus(viewHolder);
    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        switch (type) {
            case INVITE:
                return "群邀请";
            case CARD:
                return "[名片]";
        }
        return "[连接]";
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    public enum Type {
        TYPING,
        INVALID,
        INVITE,
        CARD
    }

    /**
     * 跳转位置预览页面
     *
     * @param context
     */
    private void navToLocation(InviteCustomBean inviteCustomBean, final Context context) {
        Intent intent = new Intent();
        intent.setClass(context, InvitationH5Activity.class);
        intent.putExtra("groupId", inviteCustomBean.getGroupId());
        intent.putExtra("groupAvatar", inviteCustomBean.getGroupAvatar());
        intent.putExtra("groupName", inviteCustomBean.getGroupName());
        if (type == Type.INVITE) {
            intent.putExtra("type", 1);
        } else {
            intent.putExtra("type", 2);
            intent.putExtra("fee", inviteCustomBean.getFee());
        }
        context.startActivity(intent);
    }

}
