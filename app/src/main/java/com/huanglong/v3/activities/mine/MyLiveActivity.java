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
import com.huanglong.v3.adapter.homepage.PersonalLiveAdapter;
import com.huanglong.v3.live.push.TCLivePlayerActivity;
import com.huanglong.v3.live.push.TCPublishSettingActivity;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.model.homepage.LiveBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/21.
 * 我的直播
 */
@ContentView(R.layout.activity_my_live)
public class MyLiveActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.my_live_list)
    private XRecyclerView live_list;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;


    private PersonalLiveAdapter liveAdapter;
    private List<LiveBean> liveBeanAll = new ArrayList<>();

    private int page = 1;
    private int currentPosition;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("我的直播");
        tv_right.setText("发布");
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        live_list.setLayoutManager(layoutManager);
        liveAdapter = new PersonalLiveAdapter();
        live_list.setAdapter(liveAdapter);
        live_list.setLoadingListener(this);

    }

    @Override
    protected void logic() {
        live_list.refresh();

        liveAdapter.setOnItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                LiveBean livePlayBean = (LiveBean) obj;
                currentPosition = position;
                if (type == 1) {
                    Intent intent = new Intent(getActivity(), TCLivePlayerActivity.class);
                    intent.putExtra(TCConstants.PUSHER_ID, livePlayBean.getMember_id());
                    intent.putExtra(TCConstants.PUSHER_NAME, livePlayBean.getNickname());
                    intent.putExtra(TCConstants.PUSHER_AVATAR, livePlayBean.getHead_image());
                    intent.putExtra(TCConstants.HEART_COUNT, livePlayBean.getGz_count() + "");
                    intent.putExtra(TCConstants.MEMBER_COUNT, livePlayBean.getCanyu_count() + "");
                    intent.putExtra(TCConstants.GROUP_ID, livePlayBean.getGroupId());
                    intent.putExtra(TCConstants.COVER_PIC, livePlayBean.getCover_image());
                    intent.putExtra(TCConstants.TIMESTAMP, 0);
                    intent.putExtra(TCConstants.ROOM_TITLE, livePlayBean.getTitle());
                    intent.putExtra(TCConstants.PUSHER_FOLLOW, livePlayBean.getIs_followed());
                    intent.putExtra(TCConstants.LIKE_NUMBER, livePlayBean.getGz_count());
                    intent.putExtra(TCConstants.IS_LIKE, livePlayBean.getIs_zan());
                    intent.putExtra(TCConstants.LIVE_Id, livePlayBean.getId());
                    if (livePlayBean.getStatus() == 1) {//直播
                        intent.putExtra(TCConstants.PLAY_TYPE, 0);//类型 直播/点播 直播--0 点播--1
                        intent.putExtra(TCConstants.PLAY_URL, livePlayBean.getAndriod_play_url());
                    } else {//回放
                        intent.putExtra(TCConstants.FILE_ID, livePlayBean.getFileId());
                        intent.putExtra(TCConstants.PLAY_TYPE, 1);//类型 直播/点播 直播--0 点播--1
                        intent.putExtra(TCConstants.PLAY_URL, livePlayBean.getVideo_url());
                    }
                    startActivityForResult(intent, 1000);
                } else {
                    CustomDialog mCustomDialog = new CustomDialog(getActivity(), "是否删除该回播？") {
                        @Override
                        public void EnsureEvent() {
                            requestLiveDelete(livePlayBean.getId());
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
                intent.setClass(this, TCPublishSettingActivity.class);
                startActivity(intent);
                break;
        }
    }


    /**
     * 请求我的视频
     */
    private void requestLive() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.mylive_recommend);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("page", page + "");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<LiveBean> liveBeans = gson.fromJson(json, new TypeToken<LinkedList<LiveBean>>() {
                    }.getType());
                    if (page == 1) {
                        liveBeanAll.clear();
                        liveBeanAll.addAll(liveBeans);
                    } else {
                        liveBeanAll.addAll(liveBeans);
                    }
                    liveAdapter.setData(liveBeanAll);
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
                    live_list.refreshComplete();
                } else {
                    live_list.loadMoreComplete();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        page = 1;
        requestLive();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestLive();
    }

    /**
     * 删除我的回播
     *
     * @param liveId
     */
    private void requestLiveDelete(String liveId) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_live_delete);
        params.addBodyParameter("live_id", liveId);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("删除成功");
                    liveBeanAll.remove(currentPosition);
                    liveAdapter.notifyDataSetChanged();
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
