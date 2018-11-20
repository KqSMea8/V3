package com.huanglong.v3.utils;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by bin on 2018/9/5.
 * QQ工具类
 */

public class QQUtils {

    /**
     * 分享到QQ
     *
     * @param context
     * @param title
     * @param summary
     * @param target_url
     * @param imgURL
     */
    public static void shareQQ(Activity context, String title, String summary, String target_url, String imgURL) {
        Tencent mTencent = Tencent.createInstance(Common.QQ_APP_ID, context);

        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);//TextUtils.isEmpty(title) ? "未知" : title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);//summary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, target_url);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgURL);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "微叁");
        mTencent.shareToQQ(context, params, new BaseUiListener());
    }

    /**
     * 分享到QQ空间
     *
     * @param activity
     * @param target_url
     * @param imgURL
     */
    public static void shareQQZone(Activity activity, String title, String summary, String target_url, String imgURL) {

        ArrayList<String> imageUrls = new ArrayList<String>();
        imageUrls.add(imgURL);

        Tencent mTencent = Tencent.createInstance(Common.QQ_APP_ID, activity);
        final Bundle params = new Bundle();
        //分享类型
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, target_url);//必填
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        mTencent.shareToQzone(activity, params, new BaseUiListener());
    }

    public static class BaseUiListener implements IUiListener {
//        @Override
//        public void onComplete(JSONObject response) {
////            mBaseMessageText.setText("onComplete:");
////            mMessageText.setText(response.toString());
//            doComplete(response);
//        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onComplete(Object o) {
            JSONObject response = (JSONObject) o;
            LogUtil.i("QQ share complete " + response);

        }

        @Override
        public void onError(UiError e) {
            LogUtil.e("QQ share error code:" + e.errorCode + ", msg:" + e.errorMessage + ", detail:" + e.errorDetail);
        }

        @Override
        public void onCancel() {
//            showResult("onCancel", "");
        }
    }

    /**
     * 复制链接
     *
     * @param context
     * @param link
     */
    public static void copyLink(Context context, String link) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(link);
        ToastUtils.showToast("复制成功");
    }


}
