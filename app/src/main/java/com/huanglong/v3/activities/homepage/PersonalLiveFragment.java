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
import com.huanglong.v3.adapter.homepage.PersonalLiveAdapter;
import com.huanglong.v3.live.push.TCLivePlayerActivity;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.model.homepage.LiveBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.voice.custom.CustomDialog;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/3/24.
 * 个人主页的直播
 */

public class PersonalLiveFragment extends BaseFragment {

    @ViewInject(R.id.personal_list)
    private RecyclerView live_list;

    private PersonalLiveAdapter liveAdapter;
    private int currentPosition;

    private List<LiveBean> liveBeans;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_live, container, false);
        return view;
    }


    @Override
    protected void initView() {

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        live_list.setLayoutManager(layoutManager);
        liveAdapter = new PersonalLiveAdapter();
        live_list.setAdapter(liveAdapter);


        requestLive();

    }

    @Override
    protected void logic() {

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

    /**
     * 请求我的视频
     */
    private void requestLive() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.mylive_recommend);
        params.addBodyParameter("member_id", PersonalPageActivity.instance.follower_id);
        params.addBodyParameter("page", "1");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    liveBeans = gson.fromJson(json, new TypeToken<LinkedList<LiveBean>>() {
                    }.getType());
                    liveAdapter.setData(liveBeans);
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
                    liveBeans.remove(currentPosition);
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
