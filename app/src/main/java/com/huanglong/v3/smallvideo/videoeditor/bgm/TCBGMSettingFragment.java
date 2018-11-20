package com.huanglong.v3.smallvideo.videoeditor.bgm;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huanglong.v3.R;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.smallvideo.BaseEditFragment;
import com.huanglong.v3.smallvideo.utils.DialogUtil;
import com.huanglong.v3.smallvideo.videoeditor.TCVideoEditerWrapper;
import com.huanglong.v3.smallvideo.videoeditor.bgm.utils.TCBGMInfo;
import com.huanglong.v3.smallvideo.videoeditor.bgm.view.TCBGMPannel;
import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.ugc.TXVideoEditer;

import java.io.IOException;

/**
 * RangeSlider
 * Created by hans on 2017/11/6.
 * <p>
 * bgm设置的fragment
 */
public class TCBGMSettingFragment extends BaseEditFragment {
    private static final String TAG = "TCBGMSettingFragment";
    private View mContentView;

    /**
     * 控制面板相关
     */
    private int mBgmPosition = -1;
    private TCBGMPannel mTCBGMPannel;
    private String mBGMPath;
    private int mBgmDuration;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBGMPath = TCVideoEditerWrapper.getInstance().getSelectedBGM();
        if(TextUtils.isEmpty(mBGMPath)){
            chooseBGM();
        }
    }

    private void chooseBGM() {
        Intent bgmIntent = new Intent(getActivity(), BGMSelectActivity.class);
        bgmIntent.putExtra(TCConstants.BGM_POSITION, mBgmPosition);
        startActivityForResult(bgmIntent, TCConstants.ACTIVITY_BGM_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        mBGMPath = data.getStringExtra(TCConstants.BGM_PATH);
        mBgmPosition = data.getIntExtra(TCConstants.BGM_POSITION, -1);
        TCVideoEditerWrapper.getInstance().saveBGM(mBGMPath);
        if (TextUtils.isEmpty(mBGMPath)) {
            return;
        }
        TXVideoEditer editer = TCVideoEditerWrapper.getInstance().getEditer();
        int result = editer.setBGM(mBGMPath);
        if (result != 0) {
            DialogUtil.showDialog(getContext(), "视频编辑失败", "背景音仅支持MP3格式或M4A音频");
        }
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(mBGMPath);
            mediaPlayer.prepare();
            mBgmDuration = mediaPlayer.getDuration();
            TXCLog.i(TAG, "onActivityResult, BgmDuration = " + mBgmDuration);
            mediaPlayer.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
        editer.setBGMStartTime(0, mBgmDuration);
        editer.setBGMVolume(0.5f);
        editer.setVideoVolume(0.5f);
        if (mTCBGMPannel != null) {
            mTCBGMPannel.setBgmDuration(mBgmDuration);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bgm, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContentView = view;
        initMusicPanel(view);
    }

    /**
     * BGM信息配置
     *
     * @param info 为null不设置BGM
     */
    private boolean onSetBGMInfo(TCBGMInfo info) {
        return false;
    }

    /**
     * bgm 播放时间区间设置
     */
    private void onSetBGMStartTime(long startTime, long endTime) {
        TXVideoEditer editer = TCVideoEditerWrapper.getInstance().getEditer();
        editer.setBGMStartTime(startTime, endTime);
    }

    /**
     * ==============================================音乐列表相关==============================================
     */
    private void initMusicPanel(View view) {
        mTCBGMPannel = (TCBGMPannel) view.findViewById(R.id.tc_record_bgm_pannel);
        mTCBGMPannel.hideOkButton();
        mTCBGMPannel.setOnBGMChangeListener(new TCBGMPannel.BGMChangeListener() {
            @Override
            public void onMicVolumeChanged(float volume) {
                TXVideoEditer editer = TCVideoEditerWrapper.getInstance().getEditer();
                editer.setVideoVolume(volume);
            }

            @Override
            public void onBGMVolumChanged(float volume) {
                TXVideoEditer editer = TCVideoEditerWrapper.getInstance().getEditer();
                editer.setBGMVolume(volume);
            }

            @Override
            public void onBGMTimeChanged(long startTime, long endTime) {
                onSetBGMStartTime(startTime, endTime);
                if (mTCBGMPannel != null) {
                    mTCBGMPannel.updateBGMStartTime(startTime);
                }
            }

            @Override
            public void onClickReplace() {
                chooseBGM();
            }

            @Override
            public void onClickDelete() {
                TXVideoEditer editer = TCVideoEditerWrapper.getInstance().getEditer();
                editer.setBGM(null);

                TCVideoEditerWrapper.getInstance().saveBGM(null);
            }

            @Override
            public void onClickConfirm() {
            }
        });
    }

}
