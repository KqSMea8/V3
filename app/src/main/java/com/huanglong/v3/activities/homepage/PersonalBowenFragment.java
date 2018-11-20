package com.huanglong.v3.activities.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.SpaceAdapter;
import com.huanglong.v3.model.circle.CommentBean;
import com.huanglong.v3.model.circle.PraiseBean;
import com.huanglong.v3.model.login.SpaceBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.KeyBoardUtils;
import com.huanglong.v3.utils.PopShareUtils;
import com.huanglong.v3.utils.PopupUtils;
import com.huanglong.v3.utils.PromptDialog;
import com.huanglong.v3.utils.QQUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.utils.WXUtils;
import com.huanglong.v3.view.CustomPop;
import com.zyyoona7.lib.EasyPopup;
import com.zyyoona7.lib.HorizontalGravity;
import com.zyyoona7.lib.VerticalGravity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/3/24.
 * 个人主页的博文
 */

public class PersonalBowenFragment extends BaseFragment implements View.OnClickListener {

    @ViewInject(R.id.personal_list)
    private RecyclerView personal_list;
    @ViewInject(R.id.social_circle_lin)
    private LinearLayout social_circle_lin;

    private EditText edt_comment;
    private TextView tv_zan;

    public static PersonalBowenFragment instance;


    private SpaceAdapter spaceAdapter;
    private List<SpaceBean> spaceAllBeans = new ArrayList<>();

    private int page = 1;

    private int zan_type;

    private EasyPopup more_pop;
    private CustomPop customPop;
    private SpaceBean spaceBean;
    public CommentBean commentBean;
    public int commentPosition;

    private int commentType;//1.评论 2.回复

    private PromptDialog dialog;
//    private PopShareUtils popShareUtils;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_live, container, false);
        return view;
    }

    @Override
    protected void initView() {

        instance = this;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        personal_list.setLayoutManager(layoutManager);
        spaceAdapter = new SpaceAdapter();
        personal_list.setAdapter(spaceAdapter);

        initPop();
        initDialog();

        requestCircle();


    }

    @Override
    protected void logic() {

        spaceAdapter.setOnItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                spaceBean = (SpaceBean) obj;
                //type 1.点击头像
                if (type == SpaceAdapter.CLICK_TYPE_AVATAR) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PersonalPageActivity.class);
                    intent.putExtra("uid", spaceBean.getMember_id());
                    startActivity(intent);
                } else if (type == SpaceAdapter.CLICK_TYPE_REPLY) {
                    String member_id = commentBean.getMember_id();
                    if (TextUtils.equals(member_id, UserInfoUtils.getUid())) {
                        dialog.show();
                    } else {
                        if (commentBean == null) {
                            return;
                        }
                        commentType = 2;
                        edt_comment.setHint("回复:" + commentBean.getNickname());
                        customPop.showAtLocation(PersonalPageActivity.instance.page_lin, Gravity.BOTTOM, 0, 0);
                    }
                } else if (type == SpaceAdapter.CLICK_TYPE_SHARE) {
//                    popShareUtils.showAtLocation(social_circle_lin, Gravity.BOTTOM, 0, 0);
                }
            }

            @Override
            public void onItemViewClick(Object obj, int position, int type, View view) {
                spaceBean = (SpaceBean) obj;
                if (type == SpaceAdapter.CLICK_TYPE_MORE) {
                    boolean isZan = spaceAdapter.isZan;
                    if (isZan) {
                        tv_zan.setText("取消");
                        zan_type = 1;
                    } else {
                        tv_zan.setText("赞");
                        zan_type = 0;
                    }
                    more_pop.showAtAnchorView(view, VerticalGravity.CENTER, HorizontalGravity.LEFT, -10, 0);
                }

            }
        });

//        popShareUtils = new PopShareUtils(getActivity());
//        popShareUtils.setOnClickListener(new PopShareUtils.OnClickListener() {
//            @Override
//            public void onClick(int type) {
//                String shareUrl = Api.share_qun.replace("ID", spaceBean.getId());
//                if (type == PopShareUtils.WECHAT_CIRCLE) {
//                    WXUtils.shareWeChat(getActivity(), shareUrl, "社圈分享", "社圈分享", true, spaceBean.getHead_image());
//                } else if (type == PopShareUtils.WECHAT_FRIENDS) {
//                    WXUtils.shareWeChat(getActivity(), shareUrl, "社圈分享", "社圈分享", false, spaceBean.getHead_image());
//                } else if (type == PopShareUtils.QQ) {
//                    QQUtils.shareQQ(getActivity(), "社圈分享", "社圈分享", shareUrl, spaceBean.getHead_image());
//                } else if (type == PopShareUtils.QQ_ZONE) {
//                    QQUtils.shareQQZone(getActivity(), "社圈分享", "社圈分享", shareUrl, spaceBean.getHead_image());
//                } else if (type == PopShareUtils.COPY_LINK) {
//                    QQUtils.copyLink(getActivity(), shareUrl);
//                }
//            }
//        });


    }

    /**
     * 初始化设置pop
     */
    private void initPop() {
        more_pop = PopupUtils.initRightPopup(getActivity(), R.layout.pop_space_more, PersonalPageActivity.instance.page_lin);
        tv_zan = more_pop.getView(R.id.pop_space_zan);
        tv_zan.setOnClickListener(this);
        more_pop.getView(R.id.pop_space_comment).setOnClickListener(this);


        customPop = new CustomPop(getActivity(), R.layout.pop_comment);
        edt_comment = (EditText) customPop.getView(R.id.pop_comment_content);
        customPop.getView(R.id.pop_comment_send).setOnClickListener(this);
    }

    /**
     * 初始化dialog
     */
    private void initDialog() {
        dialog = new PromptDialog(getActivity(), R.layout.dialog_hint_currency);

        TextView tv_content = (TextView) dialog.getView(R.id.dialog_comment_content);
        tv_content.setText("是否删除该条评论");

        dialog.getView(R.id.dialog_comment_cancel).setOnClickListener(this);
        dialog.getView(R.id.dialog_comment_confirm).setOnClickListener(this);
        dialog.getView(R.id.dialog_comment_lin).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pop_space_zan:
                more_pop.dismiss();
                requestZan();
                break;
            case R.id.pop_space_comment:
                more_pop.dismiss();
                commentType = 1;
                edt_comment.setHint("评论");
                customPop.showAtLocation(PersonalPageActivity.instance.page_lin, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.pop_comment_send:
                customPop.dismiss();
                KeyBoardUtils.hideKeyboard(getActivity());
                String comment = edt_comment.getText().toString().trim();
                if (!TextUtils.isEmpty(comment)) {
                    requestComment(commentType, comment);
                }
                edt_comment.setText("");
                break;
            case R.id.dialog_comment_cancel:
                dialog.dismiss();
                break;
            case R.id.dialog_comment_lin:
                dialog.dismiss();
                break;
            case R.id.dialog_comment_confirm:
                dialog.dismiss();
                requestDelete();
                break;
        }
    }

    /**
     * 请求我的动态
     */
    private void requestCircle() {

        RequestParams params = MRequestParams.getNoTokenParams(Api.myblog_recommend);
        params.addBodyParameter("member_id", PersonalPageActivity.instance.follower_id);
        params.addBodyParameter("page", "1");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<SpaceBean> spaceBeans = gson.fromJson(json, new TypeToken<LinkedList<SpaceBean>>() {
                    }.getType());
                    spaceAdapter.setData(spaceBeans);

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
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    /**
     * 点赞或取消顶赞
     */
    private void requestZan() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.quan_add_blog_upvote);
        params.addBodyParameter("user_id", UserInfoUtils.getUid());
        params.addBodyParameter("blog_id", spaceBean.getId());
        params.addBodyParameter("type", zan_type + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    List<PraiseBean> upvote_list = spaceBean.getUpvote_list();
                    if (upvote_list == null) {
                        upvote_list = new ArrayList<>();
                    }
                    if (zan_type == 1) {
                        for (PraiseBean praiseBean : upvote_list) {
                            String member_id = praiseBean.getMember_id();
                            if (TextUtils.equals(member_id, UserInfoUtils.getUid())) {
                                upvote_list.remove(praiseBean);
                            }
                        }
                    } else {
                        PraiseBean praiseBean = new PraiseBean();
                        praiseBean.setMember_id(UserInfoUtils.getUid());
                        praiseBean.setNickname(UserInfoUtils.getNickName());
                        upvote_list.add(praiseBean);
                    }
                    spaceAdapter.notifyDataSetChanged();
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
     * 请求评论或回复
     *
     * @param type    1.评论 2.回复
     * @param comment
     */
    private void requestComment(int type, String comment) {
        String url = "";
        if (type == 1) {
            url = Api.blog_add_comment;
        } else {
            url = Api.blog_reply_blog;
        }
        RequestParams params = MRequestParams.getNoTokenParams(url);
        params.addBodyParameter("user_id", UserInfoUtils.getUid());
        params.addBodyParameter("content", comment);
        params.addBodyParameter("blog_id", spaceBean.getId());
        if (type == 2) {
            params.addBodyParameter("reply_user_id", commentBean.getMember_id());
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    if (type == 1) {
                        List<CommentBean> comment_list = spaceBean.getComment_list();
                        if (comment_list == null) {
                            comment_list = new ArrayList<>();
                        }
                        CommentBean commentBean1 = new CommentBean();
                        commentBean1.setBlog_id(spaceBean.getId());
                        commentBean1.setContent(comment);
                        commentBean1.setId(json);
                        commentBean1.setNickname(UserInfoUtils.getNickName());
                        commentBean1.setMember_id(UserInfoUtils.getUid());
                        comment_list.add(0, commentBean1);
                        spaceAdapter.notifyDataSetChanged();
                    } else {
                        List<CommentBean> comment_list = spaceBean.getComment_list();
                        if (comment_list == null) {
                            comment_list = new ArrayList<>();
                        }
                        CommentBean commentBean1 = new CommentBean();
                        commentBean1.setBlog_id(spaceBean.getId());
                        commentBean1.setContent(comment);
                        commentBean1.setId(json);
                        commentBean1.setNickname(UserInfoUtils.getNickName());
                        commentBean1.setMember_id(UserInfoUtils.getUid());
                        commentBean1.setReply_user_id(commentBean.getMember_id());
                        commentBean1.setReply_nickname(commentBean.getNickname());
                        comment_list.add(0, commentBean1);
                        spaceAdapter.notifyDataSetChanged();
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
     * 删除评论
     */
    private void requestDelete() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.quan_delete_comment);
        params.addBodyParameter("user_id", UserInfoUtils.getUid());
        params.addBodyParameter("blog_comment_id", commentBean.getId());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("删除成功");
                    List<CommentBean> comment_list = spaceBean.getComment_list();
                    if (comment_list != null && comment_list.size() > commentPosition) {
                        comment_list.remove(commentPosition);
                        spaceAdapter.notifyDataSetChanged();
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
}
