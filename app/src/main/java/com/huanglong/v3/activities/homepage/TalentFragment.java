package com.huanglong.v3.activities.homepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.homepage.LivePlayAdapter;
import com.huanglong.v3.live.push.TCLivePlayerActivity;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.model.homepage.LivePlayBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.UserInfoUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/3/21.
 * 直播中的才艺
 */

@SuppressLint("ValidFragment")
public class TalentFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    public static final int START_LIVE_PLAY = 100;

    @ViewInject(R.id.talent_fragment_list)
    private XRecyclerView talent_list;

    private LivePlayAdapter livePlayAdapter;

    private String classId;
    private int page = 1;

    private List<LivePlayBean> livePlayAll = new ArrayList<>();

    public TalentFragment(String classId) {
        this.classId = classId;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_talent, container, false);
        return view;
    }

    @Override
    protected void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        talent_list.setLayoutManager(layoutManager);
        livePlayAdapter = new LivePlayAdapter();
        talent_list.setAdapter(livePlayAdapter);
        talent_list.setNestedScrollingEnabled(false);
        talent_list.setLoadingListener(this);
    }

    @Override
    protected void logic() {
        talent_list.refresh();
        livePlayAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                LivePlayBean livePlayBean = (LivePlayBean) obj;
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
//                    intent.putExtra(TCConstants.FILE_ID, livePlayBean.getFileId());
                    intent.putExtra(TCConstants.PLAY_TYPE, 0);//类型 直播/点播 直播--0 点播--1
                    intent.putExtra(TCConstants.PLAY_URL, livePlayBean.getAndriod_play_url());
                } else {//回放
                    intent.putExtra(TCConstants.FILE_ID, livePlayBean.getFileId());
                    intent.putExtra(TCConstants.PLAY_TYPE, 1);//类型 直播/点播 直播--0 点播--1
                    intent.putExtra(TCConstants.PLAY_URL, livePlayBean.getVideo_url());
//                    intent.putExtra(TCConstants.PLAY_URL, "http://1254437419.vod2.myqcloud.com/2f17c2cfvodtransgzp1254437419/e6e516067447398155540062047/v.f220.m3u8");
                }
                startActivityForResult(intent, START_LIVE_PLAY);
            }
        });
    }

    /**
     * 请求列表数据
     */
    private void requestTalent() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.liveplay_list);
        params.addBodyParameter("user_id", UserInfoUtils.getUid());
        params.addBodyParameter("cid", classId);
        params.addBodyParameter("page", page + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<LivePlayBean> livePlayBeans = gson.fromJson(json, new TypeToken<LinkedList<LivePlayBean>>() {
                    }.getType());
                    if (page == 1) {
                        livePlayAll.clear();
                        livePlayAll.addAll(livePlayBeans);
                    } else {
                        livePlayAll.addAll(livePlayBeans);
                    }
                    livePlayAdapter.setData(livePlayAll);
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
                    talent_list.refreshComplete();
                } else {
                    talent_list.loadMoreComplete();
                }
            }
        });
    }


    @Override
    public void onRefresh() {
        page = 1;
        requestTalent();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestTalent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case START_LIVE_PLAY:
                    talent_list.refresh();
                    break;
            }
        }

    }
}
