package com.huanglong.v3.smallvideo.videoeditor.bgm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.smallvideo.utils.TCUtils;
import com.huanglong.v3.smallvideo.videoeditor.time.view.RangeSlider;


/**
 * Created by vinsonswang on 2017/12/8.
 */

public class TCBGMPannel extends RelativeLayout implements SeekBar.OnSeekBarChangeListener, RangeSlider.OnRangeChangeListener, View.OnClickListener {
    private Context mContext;
    private SeekBar mMicVolumeSeekBar;
    private SeekBar mBGMVolumeSeekBar;
    private int mMicVolume = 100;
    private int mBGMVolume = 100;
    private BGMChangeListener mBGMChangeListener;
    private RangeSlider mRangeSlider;
    private long mBgmDuration;
    private Button mBtnConfirm;
    private TextView mTVStartTime;
    private ImageView mBtnReplace;
    private ImageView mBtnDelete;
    private TextView mTXMicVolume;

    public TCBGMPannel(Context context) {
        super(context);
        init(context);
    }

    public TCBGMPannel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TCBGMPannel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_bgm_edit, this);
        mMicVolumeSeekBar = (SeekBar) findViewById(R.id.seekbar_mic_volume);
        mBGMVolumeSeekBar = (SeekBar) findViewById(R.id.seekbar_bgm_volume);
        mMicVolumeSeekBar.setOnSeekBarChangeListener(this);
        mBGMVolumeSeekBar.setOnSeekBarChangeListener(this);

        mTXMicVolume = (TextView) findViewById(R.id.tv_mic_volume);

        mRangeSlider = (RangeSlider) findViewById(R.id.bgm_range_slider);
        mRangeSlider.setRangeChangeListener(this);

        mBtnConfirm = (Button) findViewById(R.id.btn_bgm_confirm);
        mBtnConfirm.setOnClickListener(this);
        mBtnReplace = (ImageView) findViewById(R.id.btn_bgm_replace);
        mBtnReplace.setOnClickListener(this);
        mBtnDelete = (ImageView) findViewById(R.id.btn_bgm_delete);
        mBtnDelete.setOnClickListener(this);

        mTVStartTime = (TextView) findViewById(R.id.tv_bgm_start_time);
        mTVStartTime.setText(String.format(getResources().getString(R.string.bgm_start_position), "00:00"));
    }

    public void setMicVolumeINVisible(){
        mTXMicVolume.setVisibility(View.GONE);
        mMicVolumeSeekBar.setVisibility(View.GONE);
    }

    public void hideOkButton() {
        mBtnConfirm.setVisibility(View.GONE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.seekbar_mic_volume) {
            mMicVolume = progress;
            if (mBGMChangeListener != null) {
                mBGMChangeListener.onMicVolumeChanged(mMicVolume / (float) 100);
            }
        } else if (seekBar.getId() == R.id.seekbar_bgm_volume) {
            mBGMVolume = progress;
            if (mBGMChangeListener != null) {
                mBGMChangeListener.onBGMVolumChanged(mBGMVolume / (float) 100);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void setBgmDuration(long duration) {
        mBgmDuration = duration;
    }

    public void setOnBGMChangeListener(BGMChangeListener volumeChangeListener) {
        mBGMChangeListener = volumeChangeListener;
    }

    public void resetRangePos() {
        mRangeSlider.resetRangePos();
    }

    /******** RangeSlider callback start *********/
    @Override
    public void onKeyDown(int type) {

    }

    @Override
    public void onKeyUp(int type, int leftPinIndex, int rightPinIndex) {
        long leftTime = mBgmDuration * leftPinIndex / 100; //ms
        long rightTime = mBgmDuration * rightPinIndex / 100;

        if (mBGMChangeListener != null) {
            mBGMChangeListener.onBGMTimeChanged(leftTime, rightTime);
        }

        mTVStartTime.setText(String.format(getResources().getString(R.string.bgm_start_position), TCUtils.millsecondToMinuteSecond((int) leftTime)));
    }

    /******** RangeSlider callback end *********/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bgm_confirm:
                if (mBGMChangeListener != null) {
                    mBGMChangeListener.onClickConfirm();
                }
                break;
            case R.id.btn_bgm_replace:
                if (mBGMChangeListener != null) {
                    mBGMChangeListener.onClickReplace();
                }
                break;
            case R.id.btn_bgm_delete:
                if (mBGMChangeListener != null) {
                    mBGMChangeListener.onClickDelete();
                }
                break;
        }
    }

    public void updateBGMStartTime(long startTime) {
        mTVStartTime.setText(String.format(getResources().getString(R.string.bgm_start_position), TCUtils.millsecondToMinuteSecond((int) startTime)));
    }

    public interface BGMChangeListener {
        // 操作当前BGM
        void onMicVolumeChanged(float volume);

        void onBGMVolumChanged(float volume);

        void onBGMTimeChanged(long startTime, long endTime);

        void onClickReplace();

        void onClickDelete();

        void onClickConfirm();
    }
}
