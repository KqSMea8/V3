package com.huanglong.v3.model.circle;

/**
 * Created by bin on 2018/4/13.
 * 评论的bean
 */

public class CommentBean {

    private String id;
    private String blog_id;
    private String member_id;
    private String reply_user_id;
    private String content;
    private String create_time;
    private String nickname;
    private String reply_nickname;

    public String getReply_nickname() {
        return reply_nickname;
    }

    public void setReply_nickname(String reply_nickname) {
        this.reply_nickname = reply_nickname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(String blog_id) {
        this.blog_id = blog_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getReply_user_id() {
        return reply_user_id;
    }

    public void setReply_user_id(String reply_user_id) {
        this.reply_user_id = reply_user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
