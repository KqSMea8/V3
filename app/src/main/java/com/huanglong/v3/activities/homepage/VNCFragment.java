package com.huanglong.v3.activities.homepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import com.huanglong.v3.utils.UserInfoUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/8.
 * 视频分类的
 */

@SuppressLint("ValidFragment")
public class VNCFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    @ViewInject(R.id.video_nearby_list)
    private XRecyclerView video_list;

    private String cid;
    private int PAGE = 1;

    private List<TCVideoInfo> videoAllBeans = new ArrayList<>();
    private VideoAdapter videoAdapter;

    public boolean isCreate = false;


    public VNCFragment(String cid) {
        this.cid = cid;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_nearby_class, container, false);
        return view;
    }

    @Override
    protected void initView() {
        isCreate = true;
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        video_list.setLayoutManager(layoutManager);
        videoAdapter = new VideoAdapter();
        video_list.setAdapter(videoAdapter);
        video_list.setLoadingListener(this);

        video_list.refresh();
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
                    intent.putExtra(TCConstants.TCLIVE_INFO_LIST, (Serializable) videoAllBeans);
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
     * 请求视频列表
     */
    private void requestVideoList() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.video_location_list);
        params.addBodyParameter("user_id", UserInfoUtils.getUid());
        params.addBodyParameter("page", PAGE + "");
        params.addBodyParameter("category_id", cid);
        params.addBodyParameter("longitude", VideoActivity.instance.str_longitude);
        params.addBodyParameter("latitude", VideoActivity.instance.str_latitude);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<TCVideoInfo> videoBeans = gson.fromJson(json, new TypeToken<LinkedList<TCVideoInfo>>() {
                    }.getType());
                    if (PAGE == 1) {
                        videoAllBeans.clear();
                        videoAllBeans.addAll(videoBeans);
                    } else {
                        videoAllBeans.addAll(videoBeans);
                    }
                    videoAdapter.setData(videoAllBeans);
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
                if (PAGE == 1) {
                    video_list.refreshComplete();
                } else {
                    video_list.loadMoreComplete();
                }
            }
        });

    }

    @Override
    public void onRefresh() {
        PAGE = 1;
        requestVideoList();
    }

    @Override
    public void onLoadMore() {
        PAGE++;
        requestVideoList();
    }
}
