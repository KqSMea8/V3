package com.huanglong.v3.model.home;

/**
 * Created by bin on 2018/3/18.
 * 福包领取人bean
 */

public class BlessUserBean {

    private String nickname;
    private String head_image;
    private String id;
    private String r_id;
    private String pin_user_id;
    private String price;
    private String create_time;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getR_id() {
        return r_id;
    }

    public void setR_id(String r_id) {
        this.r_id = r_id;
    }

    public String getPin_user_id() {
        return pin_user_id;
    }

    public void setPin_user_id(String pin_user_id) {
        this.pin_user_id = pin_user_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
