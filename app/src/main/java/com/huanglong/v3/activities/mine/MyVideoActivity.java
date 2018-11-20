package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.homepage.VideoAdapter;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.smallvideo.EffectActivity;
import com.huanglong.v3.smallvideo.play.TCVideoInfo;
import com.huanglong.v3.smallvideo.play.TCVodPlayerActivity;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.voice.custom.CustomDialog;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/21.
 * 我的视频
 */
@ContentView(R.layout.activity_my_video)
public class MyVideoActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.my_video_list)
    private XRecyclerView video_list;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;

    private int page = 1;

    private VideoAdapter videoAdapter;
    private List<TCVideoInfo> videoBeanAll = new ArrayList<>();
    private int currentPosition;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("我的视频");
        tv_right.setText("发布");
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        video_list.setLayoutManager(layoutManager);
        videoAdapter = new VideoAdapter();
        video_list.setAdapter(videoAdapter);
        video_list.setLoadingListener(this);
    }

    @Override
    protected void logic() {
        video_list.refresh();


        videoAdapter.setOnItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                TCVideoInfo tcVideoInfo = (TCVideoInfo) obj;
                currentPosition = position;
                if (type == 1) {
                    Intent intent = new Intent(getActivity(), TCVodPlayerActivity.class);
                    intent.putExtra(TCConstants.PLAY_URL, tcVideoInfo.getPlay_url());
                    intent.putExtra(TCConstants.PUSHER_ID, tcVideoInfo.getMember_id());
                    intent.putExtra(TCConstants.PUSHER_NAME, tcVideoInfo.getNickname());//== null ? item.userid : item.nickname);
                    intent.putExtra(TCConstants.PUSHER_AVATAR, tcVideoInfo.getHead_image());
                    intent.putExtra(TCConstants.COVER_PIC, tcVideoInfo.getCover_img());
                    intent.putExtra(TCConstants.FILE_ID, tcVideoInfo.getFileId());// != null ? item.fileid : "");
                    intent.putExtra(TCConstants.TCLIVE_INFO_LIST, (Serializable) videoBeanAll);
//                intent.putExtra(TCConstants.TIMESTAMP, tcVideoInfo.createTime);
                    intent.putExtra(TCConstants.TCLIVE_INFO_POSITION, position);
                    intent.putExtra("video_id", tcVideoInfo.getId());
                    intent.putExtra("is_recommend", tcVideoInfo.getIs_recommend());
                    intent.putExtra("is_zan", tcVideoInfo.getIs_zan());
                    startActivityForResult(intent, 1000);
                } else {
                    CustomDialog mCustomDialog = new CustomDialog(getActivity(), "是否删除该回播？") {
                        @Override
                        public void EnsureEvent() {
                            requestTCVideoDelete(tcVideoInfo.getId());
                            dismiss();
                        }
                    };
                    mCustomDialog.setCanceledOnTouchOutside(false);
                    mCustomDialog.show();
                }

            }

            @Override
            public void onItemViewClick(Object obj, int position, int type, View view) {

            }

        });
    }


    @Event(value = {R.id.title_back, R.id.title_tv_right})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_tv_right:
                Intent intent = new Intent();
                intent.setClass(this, EffectActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        requestVideoList();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestVideoList();
    }

    /**
     * 请求视屏列表
     */
    private void requestVideoList() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.my_video_recommend);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("page", page + "");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<TCVideoInfo> videoBeans = gson.fromJson(json, new TypeToken<LinkedList<TCVideoInfo>>() {
                    }.getType());
                    if (page == 1) {
                        videoBeanAll.clear();
                        videoBeanAll.addAll(videoBeans);
                    } else {
                        videoBeanAll.addAll(videoBeans);
                    }
                    videoAdapter.setData(videoBeanAll);
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
                if (page == 1) {
                    video_list.refreshComplete();
                } else {
                    video_list.loadMoreComplete();
                }
            }
        });
    }

    /**
     * 删除视频
     *
     * @param video_id
     */
    private void requestTCVideoDelete(String video_id) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_video_delete);
        params.addBodyParameter("video_id", video_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("删除成功");
                    videoBeanAll.remove(currentPosition);
                    videoAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showToast("删除失败");
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
