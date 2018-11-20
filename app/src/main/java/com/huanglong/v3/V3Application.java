package com.huanglong.v3;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.huanglong.v3.im.utils.Foreground;
import com.huanglong.v3.im.utils.TCIMInitMgr;
import com.huanglong.v3.im.utils.TCLog;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.live.utils.TCHttpEngine;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.utils.Constant;
import com.huanglong.v3.utils.FileUtils;
import com.hubcloud.adhubsdk.AdHub;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.ugc.TXUGCBase;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;

import org.xutils.common.util.LogUtil;
import org.xutils.x;

/**
 * Created by bin on 2018/1/4.
 * myApplication
 */

public class V3Application extends Application {

    private boolean isDebug = true;

    private static V3Application instance;

    private static Gson gson;

//    private static ImagePicker imagePicker;


    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(isDebug);
        instance = this;
        gson = new Gson();
        FileUtils.makeDir(FileUtils.appFiles);
        Foreground.init(this);
        initImSdk();
        // 自定义图片加载器
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
        AdHub.initialize(this, Constant.Ad_ID);//2399   6784
//
//        imagePicker = ImagePicker.getInstance();
//        imagePicker.setImageLoader(new PicassoImageLoader());
    }


    public static synchronized V3Application getInstance() {
        if (instance == null) {
            instance = new V3Application();
        }
        return instance;
    }


    public static synchronized Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    /**
     * 初始化腾讯云SDK
     */
    public void initImSdk() {

        String sdkVersionStr = TXLiveBase.getSDKVersionStr();
        LogUtil.e("--sdkVersionStr:" + sdkVersionStr);
        //启动bugly组件，bugly组件为腾讯提供的用于crash上报和分析的开放组件，如果您不需要该组件，可以自行移除
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setAppVersion(TXLiveBase.getSDKVersionStr());
        CrashReport.initCrashReport(getApplicationContext(), TCConstants.BUGLY_APPID, true, strategy);
        TCIMInitMgr.init(getApplicationContext());

//        //设置rtmpsdk log回调，将log保存到文件
//        TXLiveBase.setListener();
        //设置rtmpsdk log回调，将log保存到文件
//        TXLiveBase.getInstance().listener = new TCLog(getApplicationContext());
        //设置rtmpsdk log回调，将log保存到文件
        TXLiveBase.setListener(new TCLog(getApplicationContext()));
        //初始化httpengine
        TCHttpEngine.getInstance().initContext(getApplicationContext());
        //初始化设置短视频key和licence
        TXUGCBase.getInstance().setLicence(this, Api.licenceUrl, Api.licenceKey);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
