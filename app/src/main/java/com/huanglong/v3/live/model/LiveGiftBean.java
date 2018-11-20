package com.huanglong.v3.live.model;

/**
 * Created by bin on 2018/5/30.
 * 直播送出的礼物
 */

public class LiveGiftBean {

    private String sendName;
    private String sendId;
    private String giftUrl;
    private String giftName;

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getGiftUrl() {
        return giftUrl;
    }

    public void setGiftUrl(String giftUrl) {
        this.giftUrl = giftUrl;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }
}
