package com.huanglong.v3.song.model;

/**
 * Created by bin on 2018/5/3.
 * k歌素材的bean
 */

public class SongEffBean {

    private String id;
    private String name;
    private String play_url;
    private String lrc_url;
    private String img_url;
    private String create_time;
    private String sort;
    private String member_id;
    private String introduce;
    private String duration;
    private int click_count;
    private boolean isRecord;

    public boolean getIsRecord() {
        return isRecord;
    }

    public void setIsRecord(boolean isRecord) {
        this.isRecord = isRecord;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlay_url() {
        return play_url;
    }

    public void setPlay_url(String play_url) {
        this.play_url = play_url;
    }

    public String getLrc_url() {
        return lrc_url;
    }

    public void setLrc_url(String lrc_url) {
        this.lrc_url = lrc_url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getClick_count() {
        return click_count;
    }

    public void setClick_count(int click_count) {
        this.click_count = click_count;
    }
}
