package com.huanglong.v3.smallvideo.videoeditor.motion;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.smallvideo.BaseEditFragment;
import com.huanglong.v3.smallvideo.videoeditor.TCVideoEditerWrapper;
import com.huanglong.v3.smallvideo.videoeditor.TCVideoEffectActivity;
import com.huanglong.v3.smallvideo.videoeditor.common.widget.videotimeline.ColorfulProgress;
import com.huanglong.v3.smallvideo.videoeditor.common.widget.videotimeline.VideoProgressController;
import com.tencent.ugc.TXVideoEditConstants;
import com.tencent.ugc.TXVideoEditer;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hans on 2017/11/7.
 * <p>
 * 动态滤镜特效的设置Fragment
 */
public class TCMotionFragment extends BaseEditFragment implements View.OnClickListener, View.OnTouchListener {
    private static final String TAG = "TCMotionFragment";

    private ImageButton mBtnSpirit, mBtnSplit, mBtnLightWave, mBtnDark;
    private CircleImageView mBtnSpiritSelect, mBtnSplitSelect, mBtnLightWaveSelect, mBtnDarkSelect;
    private TextView mRlPlayer;
    private boolean mIsOnTouch; // 是否已经有按下的
    private TXVideoEditer mTXVideoEditer;

    private long mVideoDuration;
    private ColorfulProgress mColorfulProgress;
    private VideoProgressController mActivityVideoProgressController;
    private ImageView mIvDelete;
    private boolean mStartMark;
    private AnimationDrawable mDrawableSpirit;
    private AnimationDrawable mDrawableSplit;
    private AnimationDrawable mDrawableLightWave;
    private AnimationDrawable mDrawableDark;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_motion, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TCVideoEditerWrapper wrapper = TCVideoEditerWrapper.getInstance();
        mTXVideoEditer = wrapper.getEditer();
        if (mTXVideoEditer != null) {
            mVideoDuration = wrapper.getTXVideoInfo().duration;
        }
        mActivityVideoProgressController = ((TCVideoEffectActivity) getActivity()).getVideoProgressViewController();
        initViews(view);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mColorfulProgress != null) {
            mColorfulProgress.setVisibility(hidden ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        List<ColorfulProgress.MarkInfo> markInfoList = mColorfulProgress.getMarkInfoList();
        TCMotionViewInfoManager.getInstance().setMarkInfoList(markInfoList);

        mDrawableSpirit = (AnimationDrawable) mBtnSpirit.getDrawable();
        mDrawableSpirit.stop();

        mDrawableSplit = (AnimationDrawable) mBtnSplit.getDrawable();
        mDrawableSplit.stop();

        mDrawableLightWave = (AnimationDrawable) mBtnLightWave.getDrawable();
        mDrawableLightWave.stop();

        mDrawableDark = (AnimationDrawable) mBtnDark.getDrawable();
        mDrawableDark.stop();
    }

    private void initViews(View view) {
        mBtnSpiritSelect = (CircleImageView) view.findViewById(R.id.btn_soul_select);
        mBtnSplitSelect = (CircleImageView) view.findViewById(R.id.btn_split_select);
        mBtnLightWaveSelect = (CircleImageView) view.findViewById(R.id.btn_light_wave_select);
        mBtnDarkSelect = (CircleImageView) view.findViewById(R.id.btn_black_select);

        mBtnSpirit = (ImageButton) view.findViewById(R.id.btn_soul);
        mBtnSplit = (ImageButton) view.findViewById(R.id.btn_split);
        mBtnLightWave = (ImageButton) view.findViewById(R.id.btn_light_wave);
        mBtnDark = (ImageButton) view.findViewById(R.id.btn_black);
        mBtnSpirit.setOnTouchListener(this);
        mBtnSplit.setOnTouchListener(this);
        mBtnLightWave.setOnTouchListener(this);
        mBtnDark.setOnTouchListener(this);

        mIvDelete = (ImageView) view.findViewById(R.id.iv_undo);
        mIvDelete.setOnClickListener(this);

        mRlPlayer = (TextView) view.findViewById(R.id.motion_rl_play);
        mRlPlayer.setOnClickListener(this);

        mColorfulProgress = new ColorfulProgress(getContext());
        mColorfulProgress.setWidthHeight(mActivityVideoProgressController.getThumbnailPicListDisplayWidth(), getResources().getDimensionPixelOffset(R.dimen.bin_50_dip));
        mColorfulProgress.setMarkInfoList(TCMotionViewInfoManager.getInstance().getMarkInfoList());
        mActivityVideoProgressController.addColorfulProgress(mColorfulProgress);


        mBtnSpirit.setImageResource(R.drawable.anim_effect1);
        mDrawableSpirit = (AnimationDrawable) mBtnSpirit.getDrawable();
        mDrawableSpirit.start();

        mBtnSplit.setImageResource(R.drawable.anim_effect2);
        mDrawableSplit = (AnimationDrawable) mBtnSplit.getDrawable();
        mDrawableSplit.start();

        mBtnLightWave.setImageResource(R.drawable.anim_effect3);
        mDrawableLightWave = (AnimationDrawable) mBtnLightWave.getDrawable();
        mDrawableLightWave.start();

        mBtnDark.setImageResource(R.drawable.anim_effect4);
        mDrawableDark = (AnimationDrawable) mBtnDark.getDrawable();
        mDrawableDark.start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_undo:
                deleteLastMotion();
                break;
            case R.id.motion_rl_play:

                ((TCVideoEffectActivity) getActivity()).switchPlayVideo();
                break;
        }
    }

    private void deleteLastMotion() {
        ColorfulProgress.MarkInfo markInfo = mColorfulProgress.deleteLastMark();
        if (markInfo != null) {
            mActivityVideoProgressController.setCurrentTimeMs(markInfo.startTimeMs);
            TCVideoEffectActivity parentActivity = (TCVideoEffectActivity) getActivity();
            parentActivity.previewAtTime(markInfo.startTimeMs);
        }

        mTXVideoEditer.deleteLastEffect();
        if (mColorfulProgress.getMarkListSize() > 0) {
            showDeleteBtn();
        } else {
            hideDeleteBtn();
        }
    }

    public void showDeleteBtn() {
        if (mColorfulProgress.getMarkListSize() > 0) {
            mIvDelete.setVisibility(View.VISIBLE);
        }
    }

    public void hideDeleteBtn() {
        if (mColorfulProgress.getMarkListSize() == 0) {
            mIvDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mIsOnTouch && motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        }
        if (view.getId() == R.id.btn_soul) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mBtnSpiritSelect.setVisibility(View.VISIBLE);
                pressMotion(TXVideoEditConstants.TXEffectType_SOUL_OUT);
                mIsOnTouch = true;
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                mBtnSpiritSelect.setVisibility(View.INVISIBLE);
                upMotion(TXVideoEditConstants.TXEffectType_SOUL_OUT);
                mIsOnTouch = false;
            }
            return false;
        }

        if (view.getId() == R.id.btn_split) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mBtnSplitSelect.setVisibility(View.VISIBLE);
                pressMotion(TXVideoEditConstants.TXEffectType_SPLIT_SCREEN);
                mIsOnTouch = true;
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                mBtnSplitSelect.setVisibility(View.INVISIBLE);
                upMotion(TXVideoEditConstants.TXEffectType_SPLIT_SCREEN);
                mIsOnTouch = false;
            }
            return false;
        }

        if (view.getId() == R.id.btn_light_wave) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mBtnLightWaveSelect.setVisibility(View.VISIBLE);
                pressMotion(TXVideoEditConstants.TXEffectType_ROCK_LIGHT);
                mIsOnTouch = true;
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                mBtnLightWaveSelect.setVisibility(View.INVISIBLE);
                upMotion(TXVideoEditConstants.TXEffectType_ROCK_LIGHT);
                mIsOnTouch = false;
            }
            return false;
        }

        if (view.getId() == R.id.btn_black) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mBtnDarkSelect.setVisibility(View.VISIBLE);
                pressMotion(TXVideoEditConstants.TXEffectType_DARK_DRAEM);
                mIsOnTouch = true;
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                mBtnDarkSelect.setVisibility(View.INVISIBLE);
                upMotion(TXVideoEditConstants.TXEffectType_DARK_DRAEM);
                mIsOnTouch = false;
            }
            return false;
        }

        return false;
    }

    private void pressMotion(int type) {
        // 未开始播放 则开始播放
        long currentTime = mActivityVideoProgressController.getCurrentTimeMs();

        if (currentTime == mVideoDuration) {
            mStartMark = false;
            return;
        }
        mStartMark = true;
        ((TCVideoEffectActivity) getActivity()).startPlayAccordingState(currentTime, TCVideoEditerWrapper.getInstance().getCutterEndTime());
        mTXVideoEditer.startEffect(type, currentTime);

        switch (type) {
            case TXVideoEditConstants.TXEffectType_SOUL_OUT:
//                mBtnSpirit.setBackgroundResource(R.drawable.shape_motion_spirit_press);
                // 进度条开始变颜色
                mColorfulProgress.startMark(getResources().getColor(R.color.spirit_out_color_press));
                break;
            case TXVideoEditConstants.TXEffectType_SPLIT_SCREEN:
//                mBtnSplit.setBackgroundResource(R.drawable.shape_motion_split_press);

                mColorfulProgress.startMark(getResources().getColor(R.color.screen_split_press));
                break;
            case TXVideoEditConstants.TXEffectType_ROCK_LIGHT:
//                mBtnLightWave.setBackgroundResource(R.drawable.shape_motion_light_wave_press);

                mColorfulProgress.startMark(getResources().getColor(R.color.light_wave_press));
                break;
            case TXVideoEditConstants.TXEffectType_DARK_DRAEM:
//                mBtnDark.setBackgroundResource(R.drawable.shape_motion_dark_press);

                mColorfulProgress.startMark(getResources().getColor(R.color.dark_illusion_press));
                break;
        }
    }

    private void upMotion(int type) {
        if (!mStartMark) {
            return;
        }
        switch (type) {
            case TXVideoEditConstants.TXEffectType_SOUL_OUT:
//                mBtnSpirit.setBackgroundResource(R.drawable.shape_motion_spirit);
                break;
            case TXVideoEditConstants.TXEffectType_SPLIT_SCREEN:
//                mBtnSplit.setBackgroundResource(R.drawable.shape_motion_split);
                break;
            case TXVideoEditConstants.TXEffectType_ROCK_LIGHT:
//                mBtnLightWave.setBackgroundResource(R.drawable.shape_motion_light_wave);
                break;
            case TXVideoEditConstants.TXEffectType_DARK_DRAEM:
//                mBtnDark.setBackgroundResource(R.drawable.shape_motion_dark);
                break;
        }

        // 暂停播放
        ((TCVideoEffectActivity) getActivity()).pausePlay();
        // 进度条结束标记
        mColorfulProgress.endMark();

        // 特效结束时间
        long currentTime = mActivityVideoProgressController.getCurrentTimeMs();
        mTXVideoEditer.stopEffect(type, currentTime);
        // 显示撤销的按钮
        showDeleteBtn();
    }
}
