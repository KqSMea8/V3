package com.huanglong.v3.model.homepage;

import java.util.List;

/**
 * Created by bin on 2018/4/9.
 * k歌详情的bean
 */

public class KDetBean {

    private String id;
    private String member_id;
    private String cover_img;
    private String content;
    private String play_url;
    private String title;
    private String create_time;
    private int is_top;
    private int click_count;
    private String points;
    private int comment_count;
    private int distance;
    private String nickname;
    private String head_image;
    private String signature;
    private List<GiftUserBean> gift;
    private String lrc_url;
    private int is_followed;
    private int is_zan;
    private String sucai_url;
    private String sucai_name;
    private int zan_count;
    private int guanzhu_count;
    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getGuanzhu_count() {
        return guanzhu_count;
    }

    public void setGuanzhu_count(int guanzhu_count) {
        this.guanzhu_count = guanzhu_count;
    }

    public int getZan_count() {
        return zan_count;
    }

    public void setZan_count(int zan_count) {
        this.zan_count = zan_count;
    }

    public String getSucai_name() {
        return sucai_name;
    }

    public void setSucai_name(String sucai_name) {
        this.sucai_name = sucai_name;
    }

    public String getSucai_url() {
        return sucai_url;
    }

    public void setSucai_url(String sucai_url) {
        this.sucai_url = sucai_url;
    }

    public int getIs_followed() {
        return is_followed;
    }

    public void setIs_followed(int is_followed) {
        this.is_followed = is_followed;
    }

    public int getIs_zan() {
        return is_zan;
    }

    public void setIs_zan(int is_zan) {
        this.is_zan = is_zan;
    }

    public String getLrc_url() {
        return lrc_url;
    }

    public void setLrc_url(String lrc_url) {
        this.lrc_url = lrc_url;
    }

    public List<GiftUserBean> getGift() {
        return gift;
    }

    public void setGift(List<GiftUserBean> gift) {
        this.gift = gift;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getCover_img() {
        return cover_img;
    }

    public void setCover_img(String cover_img) {
        this.cover_img = cover_img;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPlay_url() {
        return play_url;
    }

    public void setPlay_url(String play_url) {
        this.play_url = play_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getIs_top() {
        return is_top;
    }

    public void setIs_top(int is_top) {
        this.is_top = is_top;
    }

    public int getClick_count() {
        return click_count;
    }

    public void setClick_count(int click_count) {
        this.click_count = click_count;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHead_image() {
        return head_image;
    }

    public void setHead_image(String head_image) {
        this.head_image = head_image;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
