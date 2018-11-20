package com.huanglong.v3.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.huanglong.v3.R;
import com.huanglong.v3.model.WechatPayBean;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.io.File;

/**
 * Created by bin on 2017/11/21.
 * 微信相关功能工具类
 */

public class WXUtils {

    private static IWXAPI api;

    /**
     * 注册微信
     */
    public static void regToWx(Context context) {
        api = WXAPIFactory.createWXAPI(context, Common.WX_APPID, true);
        api.registerApp(Common.WX_APPID);
    }

    /**
     * 微信登录
     *
     * @param context
     */
    public static void wxLogin(Context context) {
        api = WXAPIFactory.createWXAPI(context, Common.WX_APPID, true);
        api.registerApp(Common.WX_APPID);
        if (!JudgeWeChatInstall(api)) {
            return;
        }

        SendAuth.Req req = new SendAuth.Req();
        req.scope = Common.WX_APP_SCOPE;
        req.state = Common.WX_APP_STATE;
        api.sendReq(req);
    }

    /**
     * 微信支付
     *
     * @param context
     */
    public static void wxPay(Context context, WechatPayBean wechatPayBean) {
        api = WXAPIFactory.createWXAPI(context, Common.WX_APPID, true);
        api.registerApp(Common.WX_APPID);
        if (!JudgeWeChatInstall(api)) {
            return;
        }
        PayReq request = new PayReq();
        request.appId = Common.WX_APPID;
        request.partnerId = wechatPayBean.getPartnerid();
        request.prepayId = wechatPayBean.getPrepayid();
        request.packageValue = "Sign=WXPay";
        request.nonceStr = wechatPayBean.getNoncestr();
        request.timeStamp = wechatPayBean.getTimestamp();
        request.sign = wechatPayBean.getSign();

        api.sendReq(request);

    }


    /**
     * 判断是否安装微信客户端
     *
     * @param api
     * @return
     */
    public static boolean JudgeWeChatInstall(IWXAPI api) {
        /**绑定微信*/
        if (!api.isWXAppInstalled()) {
            ToastUtils.showToast("请先安装微信应用");
            return false;
        }
//        if (!api.isWXAppSupportAPI()) {
//            ToastUtils.showToast("请先更新微信应用");
//            return false;
//        }
        return true;
    }

    /**
     * 微信分享
     *
     * @param context
     * @param url
     * @param title
     * @param des
     * @param isFriends
     * @param iconFile
     */
    public static void shareFriends(Context context, File iconFile, String url, String title, String des, boolean isFriends) {

        api = WXAPIFactory.createWXAPI(context, Common.WX_APPID, true);
        api.registerApp(Common.WX_APPID);
        if (!JudgeWeChatInstall(api)) {
            return;
        }

        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        msg.title = title;
        msg.description = des;
        Bitmap thumb;
        if (iconFile == null) {
            thumb = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_app);
        } else {
            thumb = getSmallBitmap(iconFile.getPath());
//            thumb = BitmapFactory.decodeFile(iconFile.getAbsolutePath(), getBitmapOption(3));
        }
        msg.thumbData = Util.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = isFriends ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;

        api.sendReq(req);
    }


    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


    /**
     * 分享至微信或者朋友圈
     *
     * @param context
     * @param webUrl
     * @param title
     * @param description
     * @param isFriends   true、微信 false、朋友圈
     */
    public static void shareWeChat(final Context context, final String webUrl, final String title, final String description, boolean isFriends, String image_url) {
        if (!TextUtils.isEmpty(image_url)) {
            x.image().loadFile(image_url, MImageOptions.getNormalImageOptions(), new Callback.CacheCallback<File>() {
                File file;

                @Override
                public void onSuccess(File file) {
                    if (file != null) {
                        this.file = file;
                    }
                }

                @Override
                public void onFinished() {
                    LogUtil.i("share cover path " + file.getAbsolutePath());
                    shareFriends(context, file, webUrl, title, description, isFriends);
                }

                @Override
                public void onError(Throwable arg0, boolean arg1) {
                    JsonHandleUtils.netError(arg0);
                }

                @Override
                public void onCancelled(CancelledException arg0) {
                }

                @Override
                public boolean onCache(File file) {
                    this.file = file;
                    return false;
                }
            });
        } else {
            shareFriends(context, null, webUrl, title, description, isFriends);
        }
    }

    /**
     * @param filePath
     * @return
     */
    private static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = PictureUtil.calculateInSampleSize(options, 50, 50);
        options.inJustDecodeBounds = false;
        //		options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        options.inPurgeable = true;
        return BitmapFactory.decodeFile(filePath, options);
    }

}
