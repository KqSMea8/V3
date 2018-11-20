package com.huanglong.v3.smallvideo.play;

import android.content.Context;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huanglong.v3.R;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;

import org.xutils.common.util.LogUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by bin on 2018/4/26.
 */

public class Pageradapter extends PagerAdapter {

    private List<TCVideoInfo> mTCLiveInfoList;

    private Context context;

    public Pageradapter(Context context) {
        this.context = context;
    }

    public void setData(List<TCVideoInfo> mTCLiveInfoList) {
        this.mTCLiveInfoList = mTCLiveInfoList;
        notifyDataSetChanged();
    }

//    protected PlayerInfo instantiatePlayerInfo(int position) {
//        LogUtil.d("instantiatePlayerInfo " + position);
//
//        PlayerInfo playerInfo = new PlayerInfo();
//        TXVodPlayer vodPlayer = new TXVodPlayer(context);
//        vodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
//        vodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
//        vodPlayer.setVodListener(context);
//        TXVodPlayConfig config = new TXVodPlayConfig();
//        config.setCacheFolderPath(Environment.getExternalStorageDirectory().getPath() + "/txcache");
//        config.setMaxCacheItems(5);
//        vodPlayer.setConfig(config);
//        vodPlayer.setAutoPlay(false);
//
//        TCVideoInfo tcLiveInfo = mTCLiveInfoList.get(position);
//        playerInfo.playURL = TextUtils.isEmpty(tcLiveInfo.getPlay_url()) ? tcLiveInfo.getPlay_url() : tcLiveInfo.getPlay_url();
//        playerInfo.txVodPlayer = vodPlayer;
////        playerInfo.reviewstatus = tcLiveInfo.review_status;
//        playerInfo.pos = position;
//        playerInfoList.add(playerInfo);
//
//        return playerInfo;
//    }


    @Override
    public int getCount() {
        if (mTCLiveInfoList != null) {
            return mTCLiveInfoList.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LogUtil.i("Pageradapter instantiateItem, position = " + position);
        TCVideoInfo tcLiveInfo = mTCLiveInfoList.get(position);

        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.view_player_content, null);
        view.setId(position);
//        // 封面
//        ImageView coverImageView = (ImageView) view.findViewById(R.id.player_iv_cover);
//        TCUtils.blurBgPic(context, coverImageView, tcLiveInfo.getCover_img(), R.drawable.bg);
        // 头像
        CircleImageView ivAvatar = (CircleImageView) view.findViewById(R.id.player_civ_avatar);
        Glide.with(context).load(tcLiveInfo.getHead_image()).error(R.drawable.head_me).into(ivAvatar);
        // 姓名
//        TextView tvName = (TextView) view.findViewById(R.id.player_tv_publisher_name);
//        if (!TextUtils.isEmpty(tcLiveInfo.getNickname())) {
//            tvName.setText(tcLiveInfo.getNickname());
//        }

        TextView tv_praise = view.findViewById(R.id.item_video_praise);
        int zan_count = tcLiveInfo.getZan_count();
        if (zan_count > 10000) {
            tv_praise.setText((zan_count / 10000) + "w");
        } else {
            tv_praise.setText(zan_count + "");
        }

        TextView tv_comment = view.findViewById(R.id.item_video_comment);
        int comment_count = tcLiveInfo.getComment_count();
        if (comment_count > 10000) {
            tv_comment.setText((comment_count / 10000) + "w");
        } else {
            tv_comment.setText(comment_count + "");
        }

        TextView tv_share = view.findViewById(R.id.item_video_share);
        int share_count = tcLiveInfo.getShare_count();
        if (share_count > 10000) {
            tv_share.setText((share_count / 10000) + "w");
        } else {
            tv_share.setText(share_count + "");
        }

        TextView tv_location = view.findViewById(R.id.item_video_location);
        String location = tcLiveInfo.getLocation();
        if (!TextUtils.isEmpty(location)) {
            tv_location.setVisibility(View.VISIBLE);
            tv_location.setText(location);
        } else {
            tv_location.setVisibility(View.GONE);
        }

        TextView tv_content = view.findViewById(R.id.item_video_content);
        tv_content.setText(tcLiveInfo.getContent());

        ImageView icon_praise = view.findViewById(R.id.item_video_praise_icon);
        int is_zan = tcLiveInfo.getIs_zan();
        if (is_zan == 1) {
            icon_praise.setImageResource(R.mipmap.icon_video_praise_red);
        } else {
            icon_praise.setImageResource(R.mipmap.icon_video_praise);
        }

        TextView tv_follow = view.findViewById(R.id.item_video_follow);
        tv_follow.setText(tcLiveInfo.getGuanz_count() + "");

//        ImageView img_follow = view.findViewById(R.id.item_video_praise_follow);
//        int is_followed = tcLiveInfo.getIs_followed();
//        if (is_followed == 1) {
//            img_follow.setVisibility(View.VISIBLE);
//        } else {
//            img_follow.setVisibility(View.GONE);
//        }

        int gift_count = tcLiveInfo.getGift_count();
        TextView tv_gift = view.findViewById(R.id.item_video_gift);
        if (gift_count > 10000) {
            tv_gift.setText((gift_count / 10000) + "w");
        } else {
            tv_gift.setText(gift_count + "");
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        LogUtil.i("Pageradapter destroyItem, position = " + position);
        container.removeView((View) object);
    }
}
