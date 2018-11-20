package com.huanglong.v3.model.home;

/**
 * Created by bin on 2018/6/8.
 * 黄页评论的bean
 */

public class YelCommentBean {

    private String id;
    private String hy_id;
    private String content;
    private String member_id;
    private String create_time;
    private String nickname;
    private String head_image;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHy_id() {
        return hy_id;
    }

    public void setHy_id(String hy_id) {
        this.hy_id = hy_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
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

    public String getHead_image() {
        return head_image;
    }

    public void setHead_image(String head_image) {
        this.head_image = head_image;
    }
}
