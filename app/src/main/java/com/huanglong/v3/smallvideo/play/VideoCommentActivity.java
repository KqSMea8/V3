package com.huanglong.v3.smallvideo.play;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.barlibrary.ImmersionBar;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.homepage.VideoCommAdapter;
import com.huanglong.v3.im.model.TextMessage;
import com.huanglong.v3.model.homepage.VideoCommBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.CommentChatView;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.KeyBoardUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.view.CommentInput;
import com.huanglong.v3.view.CustomPop;
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
 * Created by bin on 2018/4/27.
 * 评论列表
 */
@ContentView(R.layout.activity_video_comment)
public class VideoCommentActivity extends BaseActivity implements View.OnClickListener, XRecyclerView.LoadingListener, CommentChatView {

    @ViewInject(R.id.video_comment_count)
    private TextView tv_comment_count;
    @ViewInject(R.id.video_comment_lin)
    private RelativeLayout video_comment_lin;
    @ViewInject(R.id.video_comment_list)
    private XRecyclerView comment_list;

    private EditText edt_comment;


    private CommentInput input;


    private String video_id;
    private int page = 1;
    private CustomPop customPop;

    private List<VideoCommBean> videoCommAll = new ArrayList<>();
    private VideoCommAdapter videoCommAdapter;
    private int comment_count = 0;
    private int currentPosition;
    private List<VideoCommBean> videoCommBeans;

    private int is_zan = 0;
    private VideoCommBean videoCommBean;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        mImmersionBar = ImmersionBar.with(this);
//        mImmersionBar;   //所有子类都将继承这些相同的属性
        mImmersionBar.statusBarColor(R.color.transparent)
                .keyboardEnable(false)
                .init();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        comment_list.setLayoutManager(layoutManager);
        comment_list.setPullRefreshEnabled(false);
        comment_list.setLoadingListener(this);
        videoCommAdapter = new VideoCommAdapter();
        comment_list.setAdapter(videoCommAdapter);
        initPop();
    }

    @Override
    protected void logic() {

        Intent intent = getIntent();
        video_id = intent.getStringExtra("video_id");
        comment_count = intent.getIntExtra("comment_count", 0);
        tv_comment_count.setText(comment_count + "条评论");
        onRefresh();

        videoCommAdapter.setOnItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                videoCommBean = (VideoCommBean) obj;
                is_zan = videoCommBean.getIs_zan();
                currentPosition = position;
                if (type == 1) {
                    requestCommentZan(videoCommBean.getMember_id(), videoCommBean.getId());
                }

            }

            @Override
            public void onItemViewClick(Object obj, int position, int type, View view) {

            }
        });

    }

    @Event(value = {R.id.video_comment_close, R.id.video_comment_lin, R.id.video_comment_send_com})
    private void mOnClick(View view) {
        switch (view.getId()) {
            case R.id.video_comment_lin:
            case R.id.video_comment_close:
                finish();
                break;
            case R.id.video_comment_send_com:
                customPop.showAtLocation(video_comment_lin, Gravity.BOTTOM, 0, 0);
                break;
        }
    }


    /**
     * 初始化pop
     */
    private void initPop() {
        customPop = new CustomPop(getActivity(), R.layout.pop_comment);
        edt_comment = (EditText) customPop.getView(R.id.pop_comment_content);
        customPop.getView(R.id.pop_comment_send).setOnClickListener(this);
        input = (CommentInput) customPop.getView(R.id.input_panel);
//        customPop.setChatInput(this);
        input.setChatView(this);
    }


    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("comment_count", comment_count);
        setResult(RESULT_OK, intent);
        super.finish();
        this.overridePendingTransition(0, R.anim.exit_bottom);
    }

    /**
     * 请求评论列表
     */
    private void requestComment() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.video_comment_list);
        params.addBodyParameter("video_id", video_id);
        params.addBodyParameter("user_id", UserInfoUtils.getUid());
        params.addBodyParameter("page", page + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    videoCommBeans = gson.fromJson(json, new TypeToken<LinkedList<VideoCommBean>>() {
                    }.getType());
                    if (page == 1) {
                        videoCommAll.clear();
                        videoCommAll.addAll(videoCommBeans);
                    } else {
                        videoCommAll.addAll(videoCommBeans);
                    }
                    videoCommAdapter.setData(videoCommAll);
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
                    comment_list.refreshComplete();
                } else {
                    comment_list.loadMoreComplete();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pop_comment_send:
                KeyBoardUtils.hideKeyboard(this);
                customPop.dismiss();
                String comment = edt_comment.getText().toString().trim();
                if (TextUtils.isEmpty(comment)) {
                    return;
                }
                requestSubmitComment(comment);
                break;
        }
    }

    /**
     * 发布评论
     *
     * @param content
     */
    private void requestSubmitComment(String content) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.video_add_comment);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("content", content);
        params.addBodyParameter("video_id", video_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    comment_count++;
                    tv_comment_count.setText(comment_count + "条评论");
                    onRefresh();
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
    public void onRefresh() {
        page = 1;
        requestComment();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestComment();
    }

    /**
     * 品论点赞
     *
     * @param memberId
     * @param commentId
     */
    private void requestCommentZan(String memberId, String commentId) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.video_add_comment_upvote);
        params.addBodyParameter("user_id", memberId);
        params.addBodyParameter("follower_id", UserInfoUtils.getUid());
        params.addBodyParameter("video_comment_id", commentId);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    if (is_zan == 0) {
                        ToastUtils.showToast("点赞成功");
                        is_zan = 1;
                        videoCommBean.setIs_zan(1);
                        videoCommBean.setUpvote_count(videoCommBean.getUpvote_count() + 1);
                    } else {
                        ToastUtils.showToast("取消点赞");
                        is_zan = 0;
                        videoCommBean.setIs_zan(0);
                        int upvote_count = videoCommBean.getUpvote_count();
                        if (upvote_count > 0) {
                            videoCommBean.setUpvote_count(videoCommBean.getUpvote_count() - 1);
                        }
                    }
                    videoCommAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

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
    public void sendText() {
        customPop.dismiss();
        KeyBoardUtils.hideKeyboard(getActivity());
        TextMessage msg = new TextMessage(input.getText());
        String msgStr = TextMessage.getMsgStr(msg.getMessage(), getActivity()).toString();
        input.setText("");
        if (!TextUtils.isEmpty(msgStr)) {
            requestSubmitComment(msgStr);
        }
    }

    @Override
    public void sending() {

    }
}
