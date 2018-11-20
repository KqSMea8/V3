package com.huanglong.v3.netutils;

import com.huanglong.v3.utils.UserInfoUtils;

import org.xutils.http.RequestParams;

/**
 * Created by bin on 2017/10/20.
 * 参数
 */

public class MRequestParams {

    /**
     * 获取不到token参数
     *
     * @param url
     * @return
     */
    public static RequestParams getNoTokenParams(String url) {
        RequestParams params = new RequestParams(Api.base_api + url);
        params.addHeader("Accept", "*");
        return params;
    }

    /**
     * 获取带token参数
     *
     * @param url
     * @return
     */
    public static RequestParams getUidParams(String url) {
        RequestParams params = new RequestParams(Api.base_api + url);
        params.addHeader("Accept", "*");
        params.addBodyParameter("uid", UserInfoUtils.getUid());
        return params;
    }

}
