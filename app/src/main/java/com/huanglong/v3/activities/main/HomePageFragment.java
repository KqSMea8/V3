package com.huanglong.v3.activities.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.circle.SocialCircleActivity;
import com.huanglong.v3.activities.homepage.AlbumListActivity;
import com.huanglong.v3.activities.homepage.KSonDetActivity;
import com.huanglong.v3.activities.homepage.KSongActivity;
import com.huanglong.v3.activities.homepage.LiveActivity;
import com.huanglong.v3.activities.homepage.ProgramActivity;
import com.huanglong.v3.activities.homepage.VFActivity;
import com.huanglong.v3.activities.homepage.VideoActivity;
import com.huanglong.v3.activities.message.BlePacActivity;
import com.huanglong.v3.activities.message.YellowPageActivity;
import com.huanglong.v3.activities.mine.FlexibleActivity;
import com.huanglong.v3.activities.mine.FlexibleDetailsActivity;
import com.huanglong.v3.adapter.homepage.ActRemAdapter;
import com.huanglong.v3.adapter.homepage.KSHAdapter;
import com.huanglong.v3.adapter.homepage.LiveAdapter;
import com.huanglong.v3.adapter.homepage.SoundBookAdapter;
import com.huanglong.v3.adapter.homepage.VideoAdapter;
import com.huanglong.v3.adapter.message.ViewPageAdapter;
import com.huanglong.v3.live.push.TCLivePlayerActivity;
import com.huanglong.v3.live.utils.TCConstants;
import com.huanglong.v3.model.homepage.ActivityBean;
import com.huanglong.v3.model.homepage.BannerBean;
import com.huanglong.v3.model.homepage.HomePageBean;
import com.huanglong.v3.model.homepage.KSFBean;
import com.huanglong.v3.model.homepage.LiveBean;
import com.huanglong.v3.model.homepage.SoundBookBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.smallvideo.play.TCVideoInfo;
import com.huanglong.v3.smallvideo.play.TCVodPlayerActivity;
import com.huanglong.v3.utils.BannerView;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.voice.custom.CustomDialog;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/1/11.
 * 主页
 */

public class HomePageFragment extends BaseFragment implements View.OnClickListener {

    @ViewInject(R.id.convenientBanner)
    private ConvenientBanner convenientBanner;
    @ViewInject(R.id.conversation_view_pager)
    private ViewPager view_pager;
    @ViewInject(R.id.conversation_view_pager_point_one)
    private ImageView point_one;
    @ViewInject(R.id.conversation_view_pager_point_two)
    private ImageView point_two;
    @ViewInject(R.id.home_page_activity_recommend)
    private RecyclerView activity_recommend_list;
    @ViewInject(R.id.home_page_live_recommend)
    private RecyclerView live_recommend_list;
    @ViewInject(R.id.home_page_video_recommend)
    private RecyclerView video_recommend_list;
    @ViewInject(R.id.home_refresh)
    private SwipeRefreshLayout home_refresh;
    @ViewInject(R.id.home_page_music_list)
    private RecyclerView music_list;
    @ViewInject(R.id.home_page_k_song_list)
    private RecyclerView k_song_list;
    @ViewInject(R.id.title_back)
    private LinearLayout title_back;
    @ViewInject(R.id.title_name)
    private TextView tv_title;


    private List<View> views = new ArrayList<>();
    private List<String> pic = new ArrayList<>();

    private ActRemAdapter actRemAdapter;
    private LiveAdapter liveAdapter;
    private VideoAdapter videoAdapter;
    private KSHAdapter kshAdapter;
    private SoundBookAdapter soundBookAdapter;


    private HomePageBean homePageBean;
    private List<SoundBookBean> soundBookBean;
    private int currentPosition;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        return view;
    }

    @Override
    protected void initView() {
        tv_title.setText("主页");
        title_back.setVisibility(View.GONE);

        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getActivity());
        view_pager.setAdapter(viewPageAdapter);
        initPagerView();
        viewPageAdapter.setData(views);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        activity_recommend_list.setLayoutManager(gridLayoutManager);
        actRemAdapter = new ActRemAdapter();
        activity_recommend_list.setAdapter(actRemAdapter);
        activity_recommend_list.setNestedScrollingEnabled(false);


        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager1.setOrientation(GridLayoutManager.VERTICAL);
        live_recommend_list.setLayoutManager(gridLayoutManager1);
        live_recommend_list.setNestedScrollingEnabled(false);
        liveAdapter = new LiveAdapter();
        live_recommend_list.setAdapter(liveAdapter);


        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager2.setOrientation(GridLayoutManager.VERTICAL);
        video_recommend_list.setLayoutManager(gridLayoutManager2);
        video_recommend_list.setNestedScrollingEnabled(false);
        videoAdapter = new VideoAdapter();
        video_recommend_list.setAdapter(videoAdapter);


        GridLayoutManager gridLayoutManager3 = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager3.setOrientation(GridLayoutManager.VERTICAL);
        music_list.setLayoutManager(gridLayoutManager3);
        music_list.setNestedScrollingEnabled(false);
        kshAdapter = new KSHAdapter();
        music_list.setAdapter(kshAdapter);

        LinearLayoutManager layoutManager4 = new LinearLayoutManager(getActivity());
        layoutManager4.setOrientation(LinearLayoutManager.VERTICAL);
        k_song_list.setNestedScrollingEnabled(false);
        k_song_list.setLayoutManager(layoutManager4);
        soundBookAdapter = new SoundBookAdapter();
        k_song_list.setAdapter(soundBookAdapter);

        requestBanner();
        requestRecommend();

    }

    //初始化view
    private void initPagerView() {
        //创建view布局
        View view1 = View.inflate(getActivity(), R.layout.item_conversation_pager_one, null);
        View view2 = View.inflate(getActivity(), R.layout.item_conversation_pager_two, null);
        //把view布局添加到集合
        views.add(view1);
        views.add(view2);

        view1.findViewById(R.id.msg_social_circle).setOnClickListener(this);
        view1.findViewById(R.id.msg_blessing_packet).setOnClickListener(this);
        view1.findViewById(R.id.home_page_yellow_page).setOnClickListener(this);
        view1.findViewById(R.id.home_page_v_f).setOnClickListener(this);
        view1.findViewById(R.id.msg_video).setOnClickListener(this);
        view2.findViewById(R.id.home_page_live).setOnClickListener(this);
        view2.findViewById(R.id.home_page_k_song).setOnClickListener(this);
        view2.findViewById(R.id.home_page_small_program).setOnClickListener(this);
    }

    @Override
    protected void logic() {
        view_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    point_one.setImageResource(R.drawable.red_circular);
                    point_two.setImageResource(R.drawable.pink_circular);
                } else {
                    point_one.setImageResource(R.drawable.pink_circular);
                    point_two.setImageResource(R.drawable.red_circular);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        actRemAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                ActivityBean activityBean = (ActivityBean) obj;
                Intent intent = new Intent();
                intent.setClass(getActivity(), FlexibleDetailsActivity.class);
                intent.putExtra("flexible_id", activityBean.getId());
                startActivity(intent);
            }
        });

        liveAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                LiveBean livePlayBean = (LiveBean) obj;
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
            }
        });

        home_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestRecommend();
            }
        });


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
                    intent.putExtra(TCConstants.TCLIVE_INFO_LIST, (Serializable) homePageBean.getVideoList());
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


        soundBookAdapter.setOnItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                SoundBookBean soundBookBean = (SoundBookBean) obj;
                currentPosition = position;
                if (type == 1) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), AlbumListActivity.class);
                    intent.putExtra("soundBookBean", soundBookBean);
                    startActivity(intent);
                } else {
                    CustomDialog mCustomDialog = new CustomDialog(getActivity(), "是否删除该音频？") {
                        @Override
                        public void EnsureEvent() {
                            requestDelete(soundBookBean.getId());
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

        kshAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                KSFBean ksfBean = (KSFBean) obj;
                Intent intent = new Intent();
                intent.setClass(getActivity(), KSonDetActivity.class);
                intent.putExtra("ksfBean", ksfBean);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.msg_social_circle:
                Intent intent = new Intent();
                intent.setClass(getActivity(), SocialCircleActivity.class);
                startActivity(intent);
                break;
            case R.id.msg_blessing_packet:
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(), BlePacActivity.class);
                startActivity(intent1);
                break;
            case R.id.home_page_yellow_page:
                Intent intent2 = new Intent();
                intent2.setClass(getActivity(), YellowPageActivity.class);
                startActivity(intent2);
                break;
            case R.id.home_page_live:
                Intent intent3 = new Intent();
                intent3.setClass(getActivity(), LiveActivity.class);
                startActivity(intent3);
                break;
            case R.id.home_page_v_f:
                Intent intent4 = new Intent();
                intent4.setClass(getActivity(), VFActivity.class);
                startActivity(intent4);
                break;
            case R.id.home_page_k_song:
                Intent intent5 = new Intent();
                intent5.setClass(getActivity(), KSongActivity.class);
                startActivity(intent5);
                break;
            case R.id.msg_video:
                Intent intent6 = new Intent();
                intent6.setClass(getActivity(), VideoActivity.class);
                startActivity(intent6);
                break;
            case R.id.home_page_small_program:
                Intent intent7 = new Intent();
                intent7.setClass(getActivity(), ProgramActivity.class);
                startActivity(intent7);
                break;
        }
    }

    /**
     * 请求广告banner
     */
    private void requestBanner() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.banner_index);
        params.addBodyParameter("position", "2");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<BannerBean> bannerBeans = gson.fromJson(json, new TypeToken<LinkedList<BannerBean>>() {
                    }.getType());
                    if (bannerBeans != null) {
                        pic.clear();
                        for (int i = 0; i < bannerBeans.size(); i++) {
                            pic.add(bannerBeans.get(i).getBanner_path());
                        }
                        BannerView.setData(pic, convenientBanner);
                    }
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
     * 请求主页推荐
     */
    private void requestRecommend() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.homepage_recommend);
        params.addBodyParameter("user_id", UserInfoUtils.getUid());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    homePageBean = gson.fromJson(json, HomePageBean.class);
                    showInfo(homePageBean);
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
                home_refresh.setRefreshing(false);
            }
        });
    }

    /**
     * ui显示信息
     *
     * @param homePageBean
     */
    private void showInfo(HomePageBean homePageBean) {
        actRemAdapter.setData(homePageBean.getActivityList());
        liveAdapter.setData(homePageBean.getLiveList());
        videoAdapter.setData(homePageBean.getVideoList());
        kshAdapter.setData(homePageBean.getMusicList());

        soundBookBean = homePageBean.getBookList();
        soundBookAdapter.setData(soundBookBean);
    }

    @Event(value = {R.id.home_page_activity_recommend_more, R.id.icon_live_recommend_more, R.id.home_page_video_recommend_more,
            R.id.home_page_music_more, R.id.home_page_k_song_more})
    private void myOnCLick(View view) {
        switch (view.getId()) {
            case R.id.home_page_activity_recommend_more:
                Intent intent = new Intent();
                intent.setClass(getActivity(), FlexibleActivity.class);
                startActivity(intent);
                break;
            case R.id.icon_live_recommend_more:
                Intent intent2 = new Intent();
                intent2.setClass(getActivity(), LiveActivity.class);
                startActivity(intent2);
                break;
            case R.id.home_page_video_recommend_more:
                Intent intent3 = new Intent();
                intent3.setClass(getActivity(), VideoActivity.class);
                startActivity(intent3);
                break;

            case R.id.home_page_music_more:
                Intent intent4 = new Intent();
                intent4.setClass(getActivity(), VFActivity.class);
                startActivity(intent4);
                break;
            case R.id.home_page_k_song_more:
                Intent intent5 = new Intent();
                intent5.setClass(getActivity(), KSongActivity.class);
                startActivity(intent5);
                break;

        }
    }


    /**
     * 删除有声书
     *
     * @param book_id
     */
    private void requestDelete(String book_id) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_book_delete);
        params.addBodyParameter("book_id", book_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("删除成功");
                    soundBookBean.remove(currentPosition);
                    soundBookAdapter.notifyDataSetChanged();
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
