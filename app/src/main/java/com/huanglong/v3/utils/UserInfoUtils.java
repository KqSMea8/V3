package com.huanglong.v3.utils;

import com.huanglong.v3.V3Application;
import com.huanglong.v3.model.login.LoginBean;

/**
 * Created by bin on 2018/1/17.
 * 用户信息 操作类
 */

public class UserInfoUtils {

    /**
     * 保存用户数据
     *
     * @param userBean
     */
    public static void saveUserInfo(LoginBean userBean) {
        PreferencesUtils.putString(V3Application.getInstance(), "token", userBean.getToken());
        PreferencesUtils.putString(V3Application.getInstance(), "uid", userBean.getUid());
        PreferencesUtils.putString(V3Application.getInstance(), "sig", userBean.getSig());
        PreferencesUtils.putString(V3Application.getInstance(), "username", userBean.getUsername());
        PreferencesUtils.putString(V3Application.getInstance(), "avatar", userBean.getHead_image());
        PreferencesUtils.putString(V3Application.getInstance(), "nickname", userBean.getNickname());
        PreferencesUtils.putInt(V3Application.getInstance(), "user_type", userBean.getType());
    }

    /**
     * 获取token
     *
     * @return
     */
    public static String getToken() {
        return PreferencesUtils.getString(V3Application.getInstance(), "token");
    }

    /**
     * 获取用户类型
     *
     * @return
     */
    public static int getUserType() {
        return PreferencesUtils.getInt(V3Application.getInstance(), "user_type", 1);
    }

    /**
     * 获取用户昵称
     *
     * @return
     */
    public static String getUserName() {
        return PreferencesUtils.getString(V3Application.getInstance(), "username");
    }


    /**
     * 获取用户头像
     *
     * @return
     */
    public static String getAvatar() {
        return PreferencesUtils.getString(V3Application.getInstance(), "avatar");
    }

    /**
     * 获取腾讯云登入sig
     *
     * @return
     */
    public static String getSid() {
        return PreferencesUtils.getString(V3Application.getInstance(), "sig");
    }

    /**
     * 获取用户ID
     *
     * @return
     */
    public static String getUid() {
        return PreferencesUtils.getString(V3Application.getInstance(), "uid");
    }

    /**
     * 设置自动登录
     *
     * @param isAuto
     */
    public static void setAutoLogin(boolean isAuto) {
        PreferencesUtils.putBoolean(V3Application.getInstance(), "auto", isAuto);
    }

    /**
     * 获取自动登录
     *
     * @return
     */
    public static boolean getAutoLogin() {
        return PreferencesUtils.getBoolean(V3Application.getInstance(), "auto", false);
    }

    /**
     * 获取用户昵称
     *
     * @return
     */
    public static String getNickName() {
        return PreferencesUtils.getString(V3Application.getInstance(), "nickname");
    }

    /**
     * 保存录音版本
     *
     * @param code
     */
    public static void saveRecordCode(int code) {
        PreferencesUtils.putInt(V3Application.getInstance(), "record_code", code);
    }

    /**
     * 获取录音版本
     *
     * @return
     */
    public static int getRecordCode() {
        return PreferencesUtils.getInt(V3Application.getInstance(), "record_code", 0);
    }

    /**
     * 保存消息免打扰状态
     *
     * @param b
     */
    public static void saveGroupMsgStatus(String groupId, boolean b) {
        PreferencesUtils.putBoolean(V3Application.getInstance(), "v3_" + groupId, b);
    }

    /**
     * 获取消息免打扰
     *
     * @param groupId
     */
    public static boolean getGroupMsgStatus(String groupId) {
        return PreferencesUtils.getBoolean(V3Application.getInstance(), "v3_" + groupId, false);
    }


}
