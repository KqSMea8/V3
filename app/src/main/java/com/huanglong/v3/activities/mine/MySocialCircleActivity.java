package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.circle.ReleaseCircleActivity;
import com.huanglong.v3.activities.homepage.PersonalPageActivity;
import com.huanglong.v3.adapter.SpaceAdapter;
import com.huanglong.v3.model.WechatPayBean;
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
import com.huanglong.v3.view.PromptEditDialog;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zyyoona7.lib.EasyPopup;
import com.zyyoona7.lib.HorizontalGravity;
import com.zyyoona7.lib.VerticalGravity;

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
 * Created by bin on 2018/3/17.
 * 社圈
 */
@ContentView(R.layout.activity_my_social_circle)
public class MySocialCircleActivity extends BaseFragmentActivity implements XRecyclerView.LoadingListener, View.OnClickListener {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.circle_list)
    private XRecyclerView circle_list;
    @ViewInject(R.id.circle_lin)
    private LinearLayout circle_lin;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;


    private EditText edt_comment;
    private TextView tv_zan;

    private SpaceAdapter spaceAdapter;
    private List<SpaceBean> spaceAllBeans = new ArrayList<>();

    private int page = 1;
    private int zan_type;
    private String cid;

    private EasyPopup more_pop;
    private CustomPop customPop;
    private SpaceBean spaceBean;
    public CommentBean commentBean;
    public int commentPosition;

    private int commentType;//1.评论 2.回复
    private PromptDialog dialog;

    private PromptEditDialog promptEditDialog;

    private TextView tv_dialog_content;
    private int dialogType;//1.删除评论 2.删除社圈
    private WechatPayBean wechatPayBean;
    private int currentPosition;

    public static MySocialCircleActivity instance;
    private PopShareUtils popShareUtils;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        instance = null;
        tv_title.setText("我的社圈");
        tv_right.setText("发布");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        circle_list.setLayoutManager(linearLayoutManager);
        circle_list.setLoadingListener(this);
        spaceAdapter = new SpaceAdapter();
        circle_list.setAdapter(spaceAdapter);

        initPop();
        initDialog();
    }

    @Override
    protected void logic() {
        circle_list.refresh();

        spaceAdapter.setOnItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                spaceBean = (SpaceBean) obj;
                currentPosition = position;
                //type 1.点击头像
                if (type == SpaceAdapter.CLICK_TYPE_AVATAR) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PersonalPageActivity.class);
                    intent.putExtra("uid", spaceBean.getMember_id());
                    startActivity(intent);
                } else if (type == SpaceAdapter.CLICK_TYPE_REPLY) {
                    String member_id = commentBean.getMember_id();
                    if (TextUtils.equals(member_id, UserInfoUtils.getUid())) {
                        dialogType = 1;
                        tv_dialog_content.setText("是否删除该条评论");
                        dialog.show();
                    } else {
                        if (commentBean == null) {
                            return;
                        }
                        commentType = 2;
                        edt_comment.setHint("回复:" + commentBean.getNickname());
                        customPop.showAtLocation(circle_lin, Gravity.BOTTOM, 0, 0);
                    }
                } else if (type == SpaceAdapter.CLICK_TYPE_DELETE) {
                    dialogType = 2;
                    tv_dialog_content.setText("是否删除该条社圈");
                    dialog.show();
                } else if (type == spaceAdapter.CLICK_TYPE_REWARD) {
                    promptEditDialog.setDialogStyle("打赏", "取消", "确认", "请输入打赏金额(1-10元)", 2, 0);
                    promptEditDialog.show();
                } else if (type == SpaceAdapter.CLICK_TYPE_SHARE) {
                    popShareUtils.showAtLocation(circle_lin, Gravity.BOTTOM, 0, 0);
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

    }

    @Event(value = {R.id.title_back, R.id.title_tv_right})
    private void mOnClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                MySocialCircleActivity.this.finish();
                break;
            case R.id.title_tv_right:
                Intent intent = new Intent();
                intent.setClass(getActivity(), ReleaseCircleActivity.class);
                startActivityForResult(intent, 1000);
                break;
        }
    }

    /**
     * 初始化设置pop
     */
    private void initPop() {
        more_pop = PopupUtils.initRightPopup(getActivity(), R.layout.pop_space_more, circle_lin);
        tv_zan = more_pop.getView(R.id.pop_space_zan);
        tv_zan.setOnClickListener(this);
        more_pop.getView(R.id.pop_space_comment).setOnClickListener(this);

        customPop = new CustomPop(getActivity(), R.layout.pop_comment);
        edt_comment = (EditText) customPop.getView(R.id.pop_comment_content);
        customPop.getView(R.id.pop_comment_send).setOnClickListener(this);


        popShareUtils = new PopShareUtils(getActivity());
        popShareUtils.setOnClickListener(new PopShareUtils.OnClickListener() {
            @Override
            public void onClick(int type) {
                String shareUrl = Api.share_qun.replace("ID", spaceBean.getId());
                if (type == PopShareUtils.WECHAT_CIRCLE) {
                    WXUtils.shareWeChat(getActivity(), shareUrl, "社圈分享", "社圈分享", true, spaceBean.getHead_image());
                } else if (type == PopShareUtils.WECHAT_FRIENDS) {
                    WXUtils.shareWeChat(getActivity(), shareUrl, "社圈分享", "社圈分享", false, spaceBean.getHead_image());
                } else if (type == PopShareUtils.QQ) {
                    QQUtils.shareQQ(getActivity(), "社圈分享", "社圈分享", shareUrl, spaceBean.getHead_image());
                } else if (type == PopShareUtils.QQ_ZONE) {
                    QQUtils.shareQQZone(getActivity(), "社圈分享", "社圈分享", shareUrl, spaceBean.getHead_image());
                } else if (type == PopShareUtils.COPY_LINK) {
                    QQUtils.copyLink(getActivity(), shareUrl);
                }
            }
        });
    }

    /**
     * 初始化dialog
     */
    private void initDialog() {
        dialog = new PromptDialog(getActivity(), R.layout.dialog_hint_currency);
        tv_dialog_content = (TextView) dialog.getView(R.id.dialog_comment_content);
        dialog.getView(R.id.dialog_comment_cancel).setOnClickListener(this);
        dialog.getView(R.id.dialog_comment_confirm).setOnClickListener(this);
        dialog.getView(R.id.dialog_comment_lin).setOnClickListener(this);

        promptEditDialog = new PromptEditDialog(getActivity());
        promptEditDialog.setOnClickListener(new PromptEditDialog.OnClickListener() {
            @Override
            public void onClick(int flag, String str) {
                if (flag == 2) {
                    requestPay(str);
                }
            }
        });

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
                customPop.showAtLocation(circle_lin, Gravity.BOTTOM, 0, 0);
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
                if (dialogType == 1) {
                    requestCommentDelete();
                } else {
                    requestDeleteCircle();
                }
                break;
        }
    }

    /**
     * 请求友圈数据
     */
    private void requestCircle() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_myblog_shequan_recommend);
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<SpaceBean> spaceBeans = gson.fromJson(json, new TypeToken<LinkedList<SpaceBean>>() {
                    }.getType());
                    if (page == 1) {
                        spaceAllBeans.clear();
                        spaceAllBeans.addAll(spaceBeans);
                    } else {
                        spaceAllBeans.addAll(spaceBeans);
                    }
                    spaceAdapter.setData(spaceAllBeans);
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
                    circle_list.refreshComplete();
                } else {
                    circle_list.loadMoreComplete();
                }
            }
        });

    }


    @Override
    public void onRefresh() {
        page = 1;
        requestCircle();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestCircle();
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
    private void requestCommentDelete() {
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


    /**
     * 加群支付
     */
    private void requestPay(String price) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.pay);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("pay_type", "1");
        params.addBodyParameter("pay_amount", price);
        params.addBodyParameter("type", "9");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    wechatPayBean = gson.fromJson(json, WechatPayBean.class);
                    if (wechatPayBean != null) {
                        WXUtils.wxPay(getActivity(), wechatPayBean);
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
     * 删除社圈
     */
    private void requestDeleteCircle() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.delete_blog);
        params.addBodyParameter("blog_id", spaceBean.getId());
        params.addBodyParameter("type", "2");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("删除成功");
                    spaceAllBeans.remove(currentPosition);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    circle_list.refresh();
                    break;
            }
        }
    }
}
