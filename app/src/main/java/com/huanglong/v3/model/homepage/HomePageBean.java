package com.huanglong.v3.model.homepage;

import com.huanglong.v3.smallvideo.play.TCVideoInfo;

import java.util.List;

/**
 * Created by bin on 2018/4/18.
 * 主页的bean
 */

public class HomePageBean {

    private List<ActivityBean> activityList;
    private List<TCVideoInfo> videoList;
    private List<LiveBean> liveList;
    private List<KSFBean> musicList;
    private List<SoundBookBean> bookList;


    public List<KSFBean> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<KSFBean> musicList) {
        this.musicList = musicList;
    }

    public List<SoundBookBean> getBookList() {
        return bookList;
    }

    public void setBookList(List<SoundBookBean> bookList) {
        this.bookList = bookList;
    }

    public List<TCVideoInfo> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<TCVideoInfo> videoList) {
        this.videoList = videoList;
    }

    public List<LiveBean> getLiveList() {
        return liveList;
    }

    public void setLiveList(List<LiveBean> liveList) {
        this.liveList = liveList;
    }

    public List<ActivityBean> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<ActivityBean> activityList) {
        this.activityList = activityList;
    }
}
