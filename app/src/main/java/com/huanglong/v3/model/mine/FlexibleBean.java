package com.huanglong.v3.model.mine;

/**
 * Created by bin on 2018/4/16.
 * 活动的bean
 */

public class FlexibleBean {

    private String id;
    private String city;
    private int type;
    private String title;
    private String content;
    private String end_time;
    private String start_time;
    private String member_id;
    private int approved;//0:未审核；1:已审核;2:活动报名截止
    private String img_url;
    private int enroll_count;
    private int activity_count;
    private int is_fee;//0-免费；1-收费
    private String distance_day;
    private int zuopin_count;
    private int is_enroll;//0-未报名 1.已报名
    private String price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getIs_enroll() {
        return is_enroll;
    }

    public void setIs_enroll(int is_enroll) {
        this.is_enroll = is_enroll;
    }

    public String getDistance_day() {
        return distance_day;
    }

    public void setDistance_day(String distance_day) {
        this.distance_day = distance_day;
    }

    public int getZuopin_count() {
        return zuopin_count;
    }

    public void setZuopin_count(int zuopin_count) {
        this.zuopin_count = zuopin_count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getEnroll_count() {
        return enroll_count;
    }

    public void setEnroll_count(int enroll_count) {
        this.enroll_count = enroll_count;
    }

    public int getActivity_count() {
        return activity_count;
    }

    public void setActivity_count(int activity_count) {
        this.activity_count = activity_count;
    }

    public int getIs_fee() {
        return is_fee;
    }

    public void setIs_fee(int is_fee) {
        this.is_fee = is_fee;
    }
}
