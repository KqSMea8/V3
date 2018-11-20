package com.huanglong.v3.model.homepage;

/**
 * Created by bin on 2018/5/11.
 * 临时聊天
 */

public class TemChatBean {

    private String receive_name;
    private String head_image;
    private String content;
    private String create_time;
    private String id;
    private int sendered;
    private int recievered;
    private String user_id;


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getReceive_name() {
        return receive_name;
    }

    public void setReceive_name(String receive_name) {
        this.receive_name = receive_name;
    }

    public String getHead_image() {
        return head_image;
    }

    public void setHead_image(String head_image) {
        this.head_image = head_image;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSendered() {
        return sendered;
    }

    public void setSendered(int sendered) {
        this.sendered = sendered;
    }

    public int getRecievered() {
        return recievered;
    }

    public void setRecievered(int recievered) {
        this.recievered = recievered;
    }
}
