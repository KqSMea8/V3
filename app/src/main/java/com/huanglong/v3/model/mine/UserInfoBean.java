package com.huanglong.v3.model.mine;

import java.io.Serializable;

/**
 * Created by bin on 2018/3/27.
 * 个人信息的bean
 */

public class UserInfoBean implements Serializable {

    private String id;
    private String username;
    private String password;
    private String nickname;
    private int type;
    private String regdate;
    private String short_name;
    private String head_image;
    private int gender;
    private int age;
    private int status;
    private String salt;
    private String token;
    private String device_type;
    private String career_id;
    private String main_scope;
    private String signature;
    private String province;
    private String city;
    private String region;
    private String constellation;
    private String introduce;
    private int is_formal;
    private double blance;
    private String receive_name;
    private String id_card_no;
    private String receive_account;
    private String receive_bank;
    private String receive_branch_bank;
    private String identifier;
    private int is_open;
    private String idcard_url;
    private String career_name;
    private int gift_count;
    private int fans_count;
    private int v3_count;
//    private List<NoticeBean> notice;
    private String notice;
    private int is_followed;
    private int is_friended;
    private String fee;
    private String jiaqun_fee;
    private String address;

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getNotice() {
        return notice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJiaqun_fee() {
        return jiaqun_fee;
    }

    public void setJiaqun_fee(String jiaqun_fee) {
        this.jiaqun_fee = jiaqun_fee;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public int getIs_followed() {
        return is_followed;
    }

    public void setIs_followed(int is_followed) {
        this.is_followed = is_followed;
    }

    public int getIs_friended() {
        return is_friended;
    }

    public void setIs_friended(int is_friended) {
        this.is_friended = is_friended;
    }

//    public List<NoticeBean> getNotice() {
//        return notice;
//    }
//
//    public void setNotice(List<NoticeBean> notice) {
//        this.notice = notice;
//    }

    public int getGift_count() {
        return gift_count;
    }

    public void setGift_count(int gift_count) {
        this.gift_count = gift_count;
    }

    public int getFans_count() {
        return fans_count;
    }

    public void setFans_count(int fans_count) {
        this.fans_count = fans_count;
    }

    public int getV3_count() {
        return v3_count;
    }

    public void setV3_count(int v3_count) {
        this.v3_count = v3_count;
    }

    public String getCareer_name() {
        return career_name;
    }

    public void setCareer_name(String career_name) {
        this.career_name = career_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getHead_image() {
        return head_image;
    }

    public void setHead_image(String head_image) {
        this.head_image = head_image;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getCareer_id() {
        return career_id;
    }

    public void setCareer_id(String career_id) {
        this.career_id = career_id;
    }

    public String getMain_scope() {
        return main_scope;
    }

    public void setMain_scope(String main_scope) {
        this.main_scope = main_scope;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
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

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getIs_formal() {
        return is_formal;
    }

    public void setIs_formal(int is_formal) {
        this.is_formal = is_formal;
    }

    public double getBlance() {
        return blance;
    }

    public void setBlance(double blance) {
        this.blance = blance;
    }

    public String getReceive_name() {
        return receive_name;
    }

    public void setReceive_name(String receive_name) {
        this.receive_name = receive_name;
    }

    public String getId_card_no() {
        return id_card_no;
    }

    public void setId_card_no(String id_card_no) {
        this.id_card_no = id_card_no;
    }

    public String getReceive_account() {
        return receive_account;
    }

    public void setReceive_account(String receive_account) {
        this.receive_account = receive_account;
    }

    public String getReceive_bank() {
        return receive_bank;
    }

    public void setReceive_bank(String receive_bank) {
        this.receive_bank = receive_bank;
    }

    public String getReceive_branch_bank() {
        return receive_branch_bank;
    }

    public void setReceive_branch_bank(String receive_branch_bank) {
        this.receive_branch_bank = receive_branch_bank;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getIs_open() {
        return is_open;
    }

    public void setIs_open(int is_open) {
        this.is_open = is_open;
    }

    public String getIdcard_url() {
        return idcard_url;
    }

    public void setIdcard_url(String idcard_url) {
        this.idcard_url = idcard_url;
    }
}
