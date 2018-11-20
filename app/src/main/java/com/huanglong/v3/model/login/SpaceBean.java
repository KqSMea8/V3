package com.huanglong.v3.model.login;

import com.huanglong.v3.model.circle.CircleImgBean;
import com.huanglong.v3.model.circle.CommentBean;
import com.huanglong.v3.model.circle.PraiseBean;

import java.util.List;

/**
 * Created by bin on 2018/1/16.
 * 友圈bean
 */

public class SpaceBean {

    private String id;
    private String title;
    private String content;
    private String member_id;
    private String create_time;
    private int type;
    private String mobile;
    private String nickname;
    private String v3_number;
    private String province;
    private String city;
    private String region;
    private String head_image;
    private List<CircleImgBean> data;
    private List<CommentBean> comment_list;
    private List<PraiseBean> upvote_list;
    private int dashang_total;
    private int b_type;

    public int getB_type() {
        return b_type;
    }

    public void setB_type(int b_type) {
        this.b_type = b_type;
    }

    public int getDashang_total() {
        return dashang_total;
    }

    public void setDashang_total(int dashang_total) {
        this.dashang_total = dashang_total;
    }

    public List<CommentBean> getComment_list() {
        return comment_list;
    }

    public void setComment_list(List<CommentBean> comment_list) {
        this.comment_list = comment_list;
    }

    public List<PraiseBean> getUpvote_list() {
        return upvote_list;
    }

    public void setUpvote_list(List<PraiseBean> upvote_list) {
        this.upvote_list = upvote_list;
    }

    public List<CircleImgBean> getData() {
        return data;
    }

    public void setData(List<CircleImgBean> data) {
        this.data = data;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getV3_number() {
        return v3_number;
    }

    public void setV3_number(String v3_number) {
        this.v3_number = v3_number;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
