package com.huanglong.v3.model;

import com.tencent.TIMConversationType;

/**
 * Created by bin on 2018/5/16.
 * 二维码信息
 */

public class QRInfoBean {

    public static final int TYPE_CHAT = 1;

    private int type;
    private TIMConversationType chatType;
    private String chatTitle;
    private String chatId;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public TIMConversationType getChatType() {
        return chatType;
    }

    public void setChatType(TIMConversationType chatType) {
        this.chatType = chatType;
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
