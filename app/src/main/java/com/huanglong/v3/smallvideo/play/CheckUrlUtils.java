package com.huanglong.v3.smallvideo.play;

import android.text.TextUtils;

import com.huanglong.v3.utils.ToastUtils;
import com.tencent.rtmp.TXLivePlayer;

/**
 * Created by bin on 2018/4/26.
 */

public class CheckUrlUtils {

    /**
     * 校验地址
     *
     * @param playUrl
     * @return
     */
    public static boolean checkPlayUrl(String playUrl) {
//        if (TextUtils.isEmpty(playUrl) || (!playUrl.startsWith("http://") && !playUrl.startsWith("https://") && !playUrl.startsWith("rtmp://"))) {
//            ToastUtils.showToast("播放地址不合法，目前仅支持rtmp,flv,hls,mp4播放方式!");
//            return false;
//        }
//        if (playUrl.startsWith("http://") || playUrl.startsWith("https://")) {
//            if (playUrl.contains(".flv")) {
//                mUrlPlayType = TXLivePlayer.PLAY_TYPE_VOD_FLV;
//            } else if (playUrl.contains(".m3u8")) {
//                mUrlPlayType = TXLivePlayer.PLAY_TYPE_VOD_HLS;
//            } else if (playUrl.toLowerCase().contains(".mp4")) {
//                mUrlPlayType = TXLivePlayer.PLAY_TYPE_VOD_MP4;
//            } else {
//                ToastUtils.showToast("播放地址不合法，点播目前仅支持flv,hls,mp4播放方式!");
//                return false;
//            }
//        } else {
//            ToastUtils.showToast("播放地址不合法，点播目前仅支持flv,hls,mp4播放方式!");
//            return false;
//        }
        return true;
    }


}
