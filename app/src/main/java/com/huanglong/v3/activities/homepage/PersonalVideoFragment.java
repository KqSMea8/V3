package com.huanglong.v3.activities.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.homepage.VideoAdapter;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.smallvideo.play.TCVideoInfo;
import com.huanglong.v3.smallvideo.play.TCVodPlayerActivity;
import com.huanglong.v3.utils.ItemTypeClickListener;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/3/24.
 * 个人主页的视频
 */

public class PersonalVideoFragment extends BaseFragment {

    @ViewInject(R.id.personal_list)
    private RecyclerView personal_list;

    private VideoAdapter videoAdapter;

    private List<TCVideoInfo> videoBeans;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_live, container, false);
        return view;
    }

    @Override
    protected void initView() {

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        personal_list.setLayoutManager(layoutManager);
        videoAdapter = new VideoAdapter();
        personal_list.setAdapter(videoAdapter);
        requestVideoList();

    }

    @Override
    protected void logic() {
        videoAdapter.setOnItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                TCVideoInfo tcVideoInfo = (TCVideoInfo) obj;
                if (type == 1) {
                    Intent intent = new Intent(getActivity(), TCVodPlayerActivity.class);
                    intent.putExtra(TCConstants.PLAY_URL, tcVideoInfo.getPlay_url());
                    intent.putExtra(TCConstants.PUSHER_ID, tcVideoInfo.getMember_id());
                    intent.putExtra(TCConstants.PUSHER_NAME, tcVideoInfo.getNickname());//== null ? item.userid : item.nickname);
                    intent.putExtra(TCConstants.PUSHER_AVATAR, tcVideoInfo.getHead_image());
                    intent.putExtra(TCConstants.COVER_PIC, tcVideoInfo.getCover_img());
                    intent.putExtra(TCConstants.FILE_ID, tcVideoInfo.getFileId());// != null ? item.fileid : "");
                    intent.putExtra(TCConstants.TCLIVE_INFO_LIST, (Serializable) videoBeans);
//                intent.putExtra(TCConstants.TIMESTAMP, tcVideoInfo.createTime);
                    intent.putExtra(TCConstants.TCLIVE_INFO_POSITION, position);
                    intent.putExtra("video_id", tcVideoInfo.getId());
                    intent.putExtra("is_recommend", tcVideoInfo.getIs_recommend());
                    intent.putExtra("is_zan", tcVideoInfo.getIs_zan());
                    startActivityForResult(intent, 1000);
                }
            }

            @Override
            public void onItemViewClick(Object obj, int position, int type, View view) {

            }

        });
    }

    /**
     * 请求视屏列表
     */
    private void requestVideoList() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.my_video_recommend);
        params.addBodyParameter("member_id", PersonalPageActivity.instance.follower_id);
        params.addBodyParameter("page", "1");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    videoBeans = gson.fromJson(json, new TypeToken<LinkedList<TCVideoInfo>>() {
                    }.getType());
                    videoAdapter.setData(videoBeans);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                JsonHandleUtils.netError(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

}
