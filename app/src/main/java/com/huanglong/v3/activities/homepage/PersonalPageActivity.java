package com.huanglong.v3.activities.homepage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.imagepreview.ImagePreviewActivity;
import com.huanglong.v3.activities.message.FriendsActivity;
import com.huanglong.v3.activities.message.ScanActivity;
import com.huanglong.v3.activities.message.SearchFriendsActivity;
import com.huanglong.v3.adapter.TabAdapter;
import com.huanglong.v3.adapter.homepage.PerClassAdapter;
import com.huanglong.v3.adapter.homepage.PerClassImgAdapter;
import com.huanglong.v3.conversation.ChatActivity;
import com.huanglong.v3.conversation.GroupDetailsActivity;
import com.huanglong.v3.conversation.GroupListActivity;
import com.huanglong.v3.conversation.TemFriendsActivity;
import com.huanglong.v3.im.model.CustomMessage;
import com.huanglong.v3.im.model.Message;
import com.huanglong.v3.im.model.TextMessage;
import com.huanglong.v3.im.presenter.ChatPresenter;
import com.huanglong.v3.im.viewfeatures.ChatView;
import com.huanglong.v3.model.contacts.GroupInfoBean;
import com.huanglong.v3.model.contacts.InviteCustomBean;
import com.huanglong.v3.model.homepage.PerClassBean;
import com.huanglong.v3.model.homepage.PerClassImgBean;
import com.huanglong.v3.model.mine.UserInfoBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.Common;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.PopupUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.tencent.TIMConversationType;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageDraft;
import com.zyyoona7.lib.EasyPopup;
import com.zyyoona7.lib.HorizontalGravity;
import com.zyyoona7.lib.VerticalGravity;

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
 * Created by bin on 2018/3/24.
 * 个人主页
 */
@ContentView(R.layout.activity_personal_page)
public class PersonalPageActivity extends BaseFragmentActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.personal_page_view_pager)
    private ViewPager view_pager;
    @ViewInject(R.id.personal_page_tab)
    private TabLayout tab_layout;
    @ViewInject(R.id.per_page_avatar)
    private ImageView img_avatar;
    @ViewInject(R.id.personal_page_name)
    private TextView tv_nickname;
    @ViewInject(R.id.per_page_v3_number)
    private TextView tv_v3_number;
    @ViewInject(R.id.per_page_v3_phone)
    private TextView tv_phone;
    @ViewInject(R.id.per_page_age)
    private TextView tv_age;
    @ViewInject(R.id.per_page_constellation)
    private TextView tv_constellation;
    @ViewInject(R.id.per_page_job)
    private TextView tv_job;
    @ViewInject(R.id.per_page_introduce)
    private TextView tv_introduce;
    @ViewInject(R.id.per_page_gift_count)
    private TextView tv_gift_count;
    @ViewInject(R.id.per_page_fans_count)
    private TextView tv_fans_count;
    @ViewInject(R.id.per_page_v3_value)
    private TextView tv_v3_value;
    @ViewInject(R.id.per_page_notice)
    private TextView tv_notice;
    @ViewInject(R.id.per_page_follow)
    private TextView tv_follow;
    @ViewInject(R.id.per_page_add_friend)
    private TextView tv_friend;
    @ViewInject(R.id.personal_page_class_list)
    private RecyclerView class_list;
    @ViewInject(R.id.personal_page_class_img_list)
    private RecyclerView class_img_list;
    @ViewInject(R.id.personal_page_lin)
    public LinearLayout page_lin;
    @ViewInject(R.id.per_page_enterprise_icon)
    private ImageView enterprise_icon;
    @ViewInject(R.id.title_right_iv)
    private ImageView iv_more;
    @ViewInject(R.id.title_right_lin)
    private LinearLayout lin_right;

    public static PersonalPageActivity instance;

    private PerClassAdapter perClassAdapter;
    private PerClassImgAdapter perClassImgAdapter;

    private List<String> tab_title = new ArrayList<>();

    private List<Fragment> fragments = new ArrayList<>();

    private TabAdapter tabFixedItemAdapter;


    public String follower_id;
    private UserInfoBean userInfoBean;
    private int is_friended;
    private int is_followed;
    private List<PerClassBean> perClassBeans;
    private List<PerClassImgBean> perClassImgBeans;
    private int type;//用户类型 1.个人  2.企业

    private EasyPopup mCirclePop;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        instance = this;
        tab_title.clear();
        tab_title.add("直播");
        tab_title.add("视频");
        tab_title.add("K歌");
        tab_title.add("音频");
        tab_title.add("动态");
        lin_right.setVisibility(View.VISIBLE);
        iv_more.setImageResource(R.mipmap.icon_title_more);

        tabFixedItemAdapter = new TabAdapter(this.getSupportFragmentManager(), tab_title);
        view_pager.setAdapter(tabFixedItemAdapter);

        fragments.add(new PersonalLiveFragment());
        fragments.add(new PersonalVideoFragment());
        fragments.add(new PersonalKFragment());
        fragments.add(new PersonalVFFragment());
        fragments.add(new PersonalBowenFragment());
        tabFixedItemAdapter.setFragmentData(fragments);
        tab_layout.setupWithViewPager(view_pager);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        class_list.setLayoutManager(layoutManager);
        perClassAdapter = new PerClassAdapter();
        class_list.setAdapter(perClassAdapter);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        class_img_list.setLayoutManager(layoutManager1);
        perClassImgAdapter = new PerClassImgAdapter();
        class_img_list.setAdapter(perClassImgAdapter);

        initPop();
    }

    @Override
    protected void logic() {

        Intent intent = getIntent();
        follower_id = intent.getStringExtra("uid");
//        type = intent.getIntExtra("type", 1);


        if (TextUtils.equals(follower_id, UserInfoUtils.getUid())) {
            tv_friend.setVisibility(View.GONE);
            tv_follow.setVisibility(View.GONE);
        } else {
            tv_friend.setVisibility(View.VISIBLE);
            tv_follow.setVisibility(View.VISIBLE);
        }

        requestData();
        requestPerClass();

        perClassAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                if (perClassBeans != null && perClassBeans.size() > 0) {
                    for (int i = 0; i < perClassBeans.size(); i++) {
                        perClassBeans.get(i).setSelected(false);
                    }
                }
                PerClassBean perClassBean = (PerClassBean) obj;
                perClassBean.setSelected(true);
                requestClassImg(perClassBean.getId());
                perClassAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * 初始化设置值popwindow
     */
    private void initPop() {
        mCirclePop = PopupUtils.initPopup(getActivity(), R.layout.pop_sel_group_personal_menu, page_lin);


        mCirclePop.getView(R.id.pop_recommend_to_personal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCirclePop.dismiss();
                Intent intent = new Intent();
                intent.setClass(PersonalPageActivity.this, FriendsActivity.class);
                intent.putExtra("flag", 2);
                startActivityForResult(intent, 1000);

            }
        });

        mCirclePop.getView(R.id.pop_recommend_to_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCirclePop.dismiss();
                Intent intent = new Intent();
                intent.setClass(PersonalPageActivity.this, GroupListActivity.class);
                intent.putExtra("type", 1);
                startActivityForResult(intent, 1002);

            }
        });
    }

    @Event(value = {R.id.title_back, R.id.per_page_follow, R.id.per_page_add_friend, R.id.title_right_lin})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                PersonalPageActivity.this.finish();
                break;
            case R.id.per_page_follow:
                requestFollow();
                break;
            case R.id.per_page_add_friend:
                if (is_friended == 1) {
                    ChatActivity.navToChat(this, userInfoBean.getIdentifier(), TIMConversationType.C2C, userInfoBean.getNickname());
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), TemFriendsActivity.class);
                    intent.putExtra("id", userInfoBean.getIdentifier());
                    intent.putExtra("name", userInfoBean.getNickname());
                    intent.putExtra("is_free", userInfoBean.getFee());
                    startActivity(intent);
//                    Intent intent = new Intent();
//                    intent.setClass(this, AddFriendActivity.class);
//                    intent.putExtra("blance", userInfoBean.getFee());
//                    intent.putExtra("is_open", userInfoBean.getIs_open());
//                    intent.putExtra("id", userInfoBean.getIdentifier());
//                    intent.putExtra("name", userInfoBean.getNickname());
//                    startActivity(intent);
                }
                break;
            case R.id.title_right_lin:
                mCirclePop.showAtAnchorView(lin_right, VerticalGravity.BELOW, HorizontalGravity.CENTER, -100, 0);
                break;
        }
    }

    /**
     * 请求个人资料
     */
    private void requestData() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_getHomepage);
        params.addBodyParameter("uid", UserInfoUtils.getUid());
        params.addBodyParameter("follower_id", follower_id);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    userInfoBean = gson.fromJson(json, UserInfoBean.class);
                    if (userInfoBean != null) {
                        showInfo(userInfoBean);
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
     * 显示信息
     *
     * @param userInfoBean
     */
    private void showInfo(UserInfoBean userInfoBean) {

        x.image().bind(img_avatar, userInfoBean.getHead_image(), MImageOptions.getCircularImageOptions());
        tv_nickname.setText(userInfoBean.getNickname());
        tv_v3_number.setText("v3号:" + userInfoBean.getId());

        tv_age.setText(userInfoBean.getAge() + "岁");

        int type = userInfoBean.getType();
        if (type == 2) {
            tv_title.setText("公司空间");
            tv_phone.setText(userInfoBean.getUsername());
            tv_phone.setVisibility(View.VISIBLE);
            enterprise_icon.setVisibility(View.VISIBLE);
        } else {
            tv_phone.setVisibility(View.GONE);
            enterprise_icon.setVisibility(View.GONE);
            tv_title.setText("个人主页");
        }

        String constellation = userInfoBean.getConstellation();
        tv_constellation.setVisibility(TextUtils.isEmpty(constellation) ? View.GONE : View.VISIBLE);
        tv_constellation.setText(constellation);
        String career_name = userInfoBean.getCareer_name();
        tv_job.setVisibility(TextUtils.isEmpty(career_name) ? View.GONE : View.VISIBLE);
        tv_job.setText(career_name);
        tv_introduce.setText(userInfoBean.getIntroduce());
        tv_gift_count.setText(userInfoBean.getGift_count() + "");
        tv_fans_count.setText(userInfoBean.getFans_count() + "");
        tv_v3_value.setText(userInfoBean.getV3_count() + "");

        is_followed = userInfoBean.getIs_followed();
        if (is_followed == 1) {
            tv_follow.setText("取消关注");
        } else {
            tv_follow.setText("关注");
        }
        is_friended = userInfoBean.getIs_friended();
        if (is_friended == 1) {
            tv_friend.setText("发消息");
        } else {
            tv_friend.setText("+好友");
        }

        int gender = userInfoBean.getGender();
        if (gender == 1) {
            Drawable drawable = getResources().getDrawable(R.mipmap.icon_man);// 找到资源图片
            // 这一步必须要做，否则不会显示。
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置图片宽高
            tv_age.setCompoundDrawables(drawable, null, null, null);// 设置到控件中
        } else {
            Drawable drawable = getResources().getDrawable(R.mipmap.icon_woman);// 找到资源图片
            // 这一步必须要做，否则不会显示。
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置图片宽高
            tv_age.setCompoundDrawables(drawable, null, null, null);// 设置到控件中
        }
//        List<NoticeBean> notice = userInfoBean.getNotice();
        String notice = userInfoBean.getNotice();
        if (!TextUtils.isEmpty(notice)) {
            tv_notice.setVisibility(View.VISIBLE);
            tv_notice.setText(notice);
        } else {
            tv_notice.setVisibility(View.GONE);
        }
    }

    /**
     * 关注或取消关注
     */
    private void requestFollow() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_add_zan);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("follower_id", follower_id);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    if (is_followed == 1) {
                        ToastUtils.showToast("取消关注成功");
                        is_followed = 0;
                        tv_follow.setText("关注");
                    } else {
                        ToastUtils.showToast("关注成功");
                        is_followed = 1;
                        tv_follow.setText("取消关注");
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
     * 请求主页分类
     */
    private void requestPerClass() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.member_subject_category);
        params.addBodyParameter("member_id", follower_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    perClassBeans = gson.fromJson(json, new TypeToken<LinkedList<PerClassBean>>() {
                    }.getType());
                    if (perClassBeans != null && perClassBeans.size() > 0) {
                        perClassBeans.get(0).setSelected(true);
                        requestClassImg(perClassBeans.get(0).getId());
                    }
                    perClassAdapter.setData(perClassBeans);
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
     * 分类的图片
     */
    private void requestClassImg(String sid) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.member_subject_show);
        params.addBodyParameter("sid", sid);
        params.addBodyParameter("member_id", follower_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    perClassImgBeans = gson.fromJson(json, new TypeToken<LinkedList<PerClassImgBean>>() {
                    }.getType());
                    perClassImgAdapter.setData(perClassImgBeans);
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
     * 删除主图图片
     */
    public void delThemePic(int position, ImagePreviewActivity.delImgCallBack delImgCallBack) {
        if (perClassImgBeans == null || perClassImgBeans.size() <= position) return;
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_delete_subject_pic);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("zhuti_id", perClassImgBeans.get(position).getId());

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("删除成功");
                    perClassImgBeans.remove(position);
                    perClassImgAdapter.notifyDataSetChanged();
                    delImgCallBack.Success();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    if (data == null) return;
                    List<String> identifiers = (List<String>) data.getSerializableExtra("identifiers");
                    inviteFriends(identifiers, 1);
                    break;
                case 1002:
                    if (data == null) return;
                    List<GroupInfoBean> groupInfoBeans = (List<GroupInfoBean>) data.getSerializableExtra("selGroupList");
                    sendCard(groupInfoBeans);
                    break;
            }
        }
    }

    /**
     * 发送名片到群
     *
     * @param groupInfoBeans
     */
    private void sendCard(List<GroupInfoBean> groupInfoBeans) {
        List<String> identifiers = new ArrayList<>();
        for (GroupInfoBean groupInfoBean : groupInfoBeans) {
            identifiers.add(groupInfoBean.getGroup_id());
        }
        inviteFriends(identifiers, 2);
    }

    /**
     * 邀请朋友进入该群
     *
     * @param identifiers
     * @param type        1.个人聊天，2.群聊
     */
    private void inviteFriends(List<String> identifiers, int type) {

        if (identifiers == null || identifiers.size() == 0 || userInfoBean == null) return;

        for (String id : identifiers) {
            // xml 协议的自定义消息
            InviteCustomBean customMsgEntry = new InviteCustomBean();
            customMsgEntry.setGroupId(userInfoBean.getId());
            customMsgEntry.setDes("昵称:" + userInfoBean.getNickname() + "\nV3号:" + userInfoBean.getId());
            customMsgEntry.setGroupName(userInfoBean.getNickname());
            customMsgEntry.setGroupAvatar(userInfoBean.getHead_image());
            customMsgEntry.setFee(TextUtils.isEmpty(userInfoBean.getFee()) ? 0 : Integer.parseInt(userInfoBean.getFee()));
            Gson gson = V3Application.getGson();
            String customMsgJson = gson.toJson(customMsgEntry);
            Message message = new CustomMessage(CustomMessage.Type.CARD, customMsgJson);

            ChatPresenter presenter = new ChatPresenter(new ChatView() {
                @Override
                public void showMessage(TIMMessage message) {

                }

                @Override
                public void showMessage(List<TIMMessage> messages) {

                }

                @Override
                public void clearAllMessage() {

                }

                @Override
                public void onSendMessageSuccess(TIMMessage message) {

                }

                @Override
                public void onSendMessageFail(int code, String desc, TIMMessage message) {

                }

                @Override
                public void sendImage() {

                }

                @Override
                public void sendPhoto() {

                }

                @Override
                public void sendText() {

                }

                @Override
                public void sendFile() {

                }

                @Override
                public void startSendVoice() {

                }

                @Override
                public void endSendVoice() {

                }

                @Override
                public void sendVideo(String fileName) {

                }

                @Override
                public void cancelSendVoice() {

                }

                @Override
                public void sending() {

                }

                @Override
                public void showDraft(TIMMessageDraft draft) {

                }

                @Override
                public void selectLocation() {

                }
            }, id, type == 1 ? TIMConversationType.C2C : TIMConversationType.Group);

            presenter.sendMessage(message.getMessage());
            Message message1 = new TextMessage("[名片]");
            presenter.sendMessage(message1.getMessage());
//            ToastUtils.showToast("邀请发送成功");
        }
    }

}
