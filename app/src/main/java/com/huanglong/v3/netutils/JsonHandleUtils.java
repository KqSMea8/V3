package com.huanglong.v3.netutils;

import android.text.TextUtils;

import com.huanglong.v3.R;
import com.huanglong.v3.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

/**
 * Created by bin on 2017/10/20.
 * Json处理工具类
 */

public class JsonHandleUtils {


    /**
     * 处理接口返回json
     *
     * @param result
     * @return
     */
    public static String JsonHandle(String result) {
        if (TextUtils.isEmpty(result)) {
            ToastUtils.showToast(R.string.net_error);
            return "";
        }
        LogUtil.i(result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has("code")) {
                int code = jsonObject.getInt("code");
                if (code == 1 || code == 200) {
                    String data = jsonObject.getString("data");
                    if (!TextUtils.isEmpty(data) && !TextUtils.equals("null", data)) {
                        return data;
                    }
                    return "fail";
                } else if (code == 1000) {
//                    LoginOverdue.loginOverdue();
//                    Intent intent = new Intent();
//                    intent.setClass(context, LoginActivity.class);
//                    context.startActivityForResult(intent, Common.LOGIN);
                    return "";
                } else if (code == 8003) {
                    return "8003";
                } else {
                    String msg = jsonObject.getString("msg");
                    if (!TextUtils.isEmpty(msg) && !TextUtils.equals("null", msg)) {
                        ToastUtils.showToast(msg);
                    } else {
                        ToastUtils.showToast("未知错误");
                    }
                    if (code == -3001) {
                        return "-3001";
                    }
                    return "";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ToastUtils.showToast(R.string.net_error);
        return "";
    }

    /**
     * 网络请求异常
     *
     * @param throwable
     */
    public static void netError(Throwable throwable) {
        LogUtil.e(throwable.toString());
        ToastUtils.showToast(R.string.net_error);
    }

}
