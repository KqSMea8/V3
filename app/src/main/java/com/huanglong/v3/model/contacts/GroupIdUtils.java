package com.huanglong.v3.model.contacts;

import android.text.TextUtils;

/**
 * Created by bin on 2018/4/17.
 * 群ID处理工具类
 */

public class GroupIdUtils {

    /**
     * 群ID 处理 去掉@TGS#
     *
     * @param groupID
     * @return
     */
    public static String deelGroupId(String groupID) {
        if (TextUtils.isEmpty(groupID)) return groupID;
        if (groupID.startsWith("@TGS#")) {
            groupID = groupID.replace("@TGS#", "");
        }
        return groupID;
    }

}
