package com.huanglong.v3.smallvideo.play;

import android.view.View;

import com.tencent.rtmp.TXVodPlayer;

/**
 * Created by bin on 2018/9/7.
 */

public class PlayerInfo {

    public TXVodPlayer txVodPlayer;
    public String playURL;
    public boolean isBegin;
    public View playerView;
    public int pos;
    public int reviewstatus;
}
