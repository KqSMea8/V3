package com.huanglong.v3.utils;


import com.huanglong.v3.im.viewfeatures.MvpView;

/**
 * 评论输入界面的接口
 */
public interface CommentChatView extends MvpView {


    /**
     * 发送文字消息
     */
    void sendText();


    /**
     * 正在发送
     */
    void sending();


}
