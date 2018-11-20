package com.huanglong.v3.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.circle.SocialCircleActivity;
import com.huanglong.v3.activities.homepage.KSongActivity;
import com.huanglong.v3.activities.homepage.LiveActivity;
import com.huanglong.v3.activities.homepage.ProgramActivity;
import com.huanglong.v3.activities.homepage.VFActivity;
import com.huanglong.v3.activities.homepage.VideoActivity;
import com.huanglong.v3.activities.message.AllClassActivity;
import com.huanglong.v3.activities.message.BlePacActivity;
import com.huanglong.v3.activities.message.ContactsFragment;
import com.huanglong.v3.activities.message.ConversationFragment;
import com.huanglong.v3.conversation.GroupDetailsActivity;
import com.huanglong.v3.activities.message.ScanActivity;
import com.huanglong.v3.activities.message.SearchFriendsActivity;
import com.huanglong.v3.activities.message.YellowPageActivity;
import com.huanglong.v3.adapter.TabAdapter;
import com.huanglong.v3.adapter.message.ViewPageAdapter;
import com.huanglong.v3.conversation.GroupListActivity;
import com.huanglong.v3.conversation.TemConversationActivity;
import com.huanglong.v3.im.contacts.ProfileActivity;
import com.huanglong.v3.model.QRInfoBean;
import com.huanglong.v3.model.home.JobClassBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.PopupUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.tencent.TIMConversationType;
import com.zyyoona7.lib.EasyPopup;
import com.zyyoona7.lib.HorizontalGravity;
import com.zyyoona7.lib.VerticalGravity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/1/11.
 * 会话页面
 */

public class MsgFragment extends BaseFragment implements View.OnClickListener {

    @ViewInject(R.id.conversation_view_pager)
    private ViewPager view_pager;
    @ViewInject(R.id.conversation_view_pager_point_one)
    private ImageView point_one;
    @ViewInject(R.id.conversation_view_pager_point_two)
    private ImageView point_two;
    @ViewInject(R.id.home_tab)
    private TabLayout home_tab;
    @ViewInject(R.id.home_view_pager)
    private ViewPager view_pager_content;
    @ViewInject(R.id.home_lin)
    private LinearLayout home_lin;
    @ViewInject(R.id.home_plus)
    private ImageView home_plus;
    @ViewInject(R.id.home_tem_conversation_count)
    private TextView tv_tem_msg_unread;


    private List<View> views = new ArrayList<>();
    private List<String> tabTitleList = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();

    private TabAdapter tabAdapter;

    private EasyPopup mCirclePop;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        return view;
    }

    @Override
    protected void initView() {

        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getActivity());
        view_pager.setAdapter(viewPageAdapter);
        initPagerView();
        viewPageAdapter.setData(views);
        requestJobClass();

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
        initPop();
    }

    /**
     * 初始化设置值popwindow
     */
    private void initPop() {
        mCirclePop = PopupUtils.initPopup(getActivity(), R.layout.pop_home_menu, home_lin);

        mCirclePop.getView(R.id.pop_add_friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCirclePop.dismiss();
                Intent intent = new Intent();
                intent.setClass(getActivity(), SearchFriendsActivity.class);
                startActivity(intent);
            }
        });

        mCirclePop.getView(R.id.pop_add_found_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCirclePop.dismiss();
                Intent intent = new Intent();
                intent.setClass(getActivity(), GroupListActivity.class);
                startActivity(intent);
            }
        });

        mCirclePop.getView(R.id.pop_add_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCirclePop.dismiss();
                Intent intent = new Intent();
                intent.setClass(getActivity(), ScanActivity.class);
                startActivityForResult(intent, 1000);
            }
        });
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
        requestTemComUnRead();

    }

    @Event(value = {R.id.home_plus, R.id.home_search, R.id.home_tab_more, R.id.home_page_live, R.id.home_tem_conversation_rel})
    private void monClick(View view) {
        switch (view.getId()) {
            case R.id.home_plus:
                mCirclePop.showAtAnchorView(home_plus, VerticalGravity.BELOW, HorizontalGravity.CENTER, -100, 30);
                break;
            case R.id.home_search:
                Intent intent = new Intent();
                intent.setClass(getActivity(), SearchFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.home_tab_more://全部分类
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(), AllClassActivity.class);
                startActivity(intent1);
                break;
            case R.id.home_tem_conversation_rel:
                Intent intent2 = new Intent();
                intent2.setClass(getActivity(), TemConversationActivity.class);
                startActivity(intent2);
                break;
        }
    }


    /**
     * 请求职业分类
     */
    private void requestJobClass() {

        RequestParams params = MRequestParams.getNoTokenParams(Api.job_class);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<JobClassBean> jobClassBeans = gson.fromJson(json, new TypeToken<LinkedList<JobClassBean>>() {
                    }.getType());
                    if (jobClassBeans != null) {
                        initTabItem(jobClassBeans);
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
     * 初始化 tabItem
     *
     * @param jobClassBeans
     */
    private void initTabItem(List<JobClassBean> jobClassBeans) {
        tabTitleList.clear();
        tabTitleList.add("消息");
        fragments.add(new ConversationFragment());
        for (int i = 0; i < jobClassBeans.size(); i++) {
            tabTitleList.add(jobClassBeans.get(i).getName());
            fragments.add(new ContactsFragment(jobClassBeans.get(i).getId()));
        }
        if (tabTitleList.size() > 5) {
            home_tab.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            home_tab.setTabMode(TabLayout.MODE_FIXED);
        }

        for (int i = 0; i < tabTitleList.size(); i++) {
            home_tab.addTab(home_tab.newTab().setText(tabTitleList.get(i)), i);
        }
        tabAdapter = new TabAdapter(getFragmentManager(), tabTitleList);
        view_pager_content.setAdapter(tabAdapter);
        tabAdapter.setFragmentData(fragments);
        home_tab.setupWithViewPager(view_pager_content);

        home_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view_pager_content.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    if (data == null) return;
                    String info = data.getStringExtra("result");
                    Gson gson = V3Application.getGson();
                    QRInfoBean qrInfoBean = gson.fromJson(info, QRInfoBean.class);
                    if (qrInfoBean != null) {
                        if (qrInfoBean.getType() == QRInfoBean.TYPE_CHAT) {
                            if (qrInfoBean.getChatType() == TIMConversationType.C2C) {
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), ProfileActivity.class);
                                intent.putExtra("identify", qrInfoBean.getChatId());
                                startActivity(intent);
                            } else if (qrInfoBean.getChatType() == TIMConversationType.Group) {
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), GroupDetailsActivity.class);
                                intent.putExtra("groupId", qrInfoBean.getChatId());
                                intent.putExtra("getGroupName", qrInfoBean.getChatTitle());
                                startActivity(intent);
                            }

                        }
                    }

                    break;
            }
        }

    }

    /**
     * 临时消息未读书
     */
    private void requestTemComUnRead() {

        RequestParams params = MRequestParams.getNoTokenParams(Api.user_message_count);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    tv_tem_msg_unread.setVisibility(View.VISIBLE);
                    tv_tem_msg_unread.setText(json);
                } else {
                    tv_tem_msg_unread.setVisibility(View.GONE);
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
