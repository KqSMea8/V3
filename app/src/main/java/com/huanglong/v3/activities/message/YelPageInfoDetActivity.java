package com.huanglong.v3.activities.message;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.homepage.PersonalPageActivity;
import com.huanglong.v3.adapter.homepage.YelCommentAdapter;
import com.huanglong.v3.im.model.TextMessage;
import com.huanglong.v3.model.YelInfoBean;
import com.huanglong.v3.model.home.YelCommentBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.CommentChatView;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.KeyBoardUtils;
import com.huanglong.v3.utils.MImageOptions;
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

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/5/17.
 * 黄页信息的详情
 */
@ContentView(R.layout.activity_yel_page_info_details)
public class YelPageInfoDetActivity extends BaseActivity implements CommentChatView {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.yel_page_info_list)
    private XRecyclerView info_list;
    @ViewInject(R.id.yel_page_info_lin)
    private LinearLayout info_lin;

    private ImageView img_cover;
    private TextView tv_yel_title;
    private TextView tv_yel_content;
    private TextView tv_con;

    private CommentInput input;


    private YelInfoBean yelInfoBean;

    private YelCommentAdapter yelCommentAdapter;

    private CustomPop customPop;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("详情");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        info_list.setLayoutManager(layoutManager);

        View headView = LayoutInflater.from(this).inflate(R.layout.activity_yel_page_info_details_head, info_list, false);
        img_cover = headView.findViewById(R.id.yel_page_info_dat_cover);
        tv_yel_title = headView.findViewById(R.id.yel_page_info_dat_title);
        tv_yel_content = headView.findViewById(R.id.yel_page_info_dat_content);
        tv_con = headView.findViewById(R.id.yel_page_info_dat_con);

        info_list.addHeaderView(headView);
        info_list.setLoadingMoreEnabled(false);
        info_list.setPullRefreshEnabled(false);

        yelCommentAdapter = new YelCommentAdapter();
        info_list.setAdapter(yelCommentAdapter);


        initPop();
    }

    @Override
    protected void logic() {
        Intent intent = getIntent();
        yelInfoBean = (YelInfoBean) intent.getSerializableExtra("yelInfoBean");
        showInfo();
        requestCommentList();


        yelCommentAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                YelCommentBean yelCommentBean = (YelCommentBean) obj;
                Intent intent1 = new Intent();
                intent1.setClass(YelPageInfoDetActivity.this, PersonalPageActivity.class);
                intent1.putExtra("uid", yelCommentBean.getMember_id());
                startActivity(intent1);
            }
        });
    }

    private void initPop() {
        customPop = new CustomPop(getActivity(), R.layout.pop_comment);
        input = (CommentInput) customPop.getView(R.id.input_panel);
//        customPop.setChatInput(this);
        input.setChatView(this);
    }

    /**
     * UI显示信息
     */
    private void showInfo() {
        x.image().bind(img_cover, yelInfoBean.getCover_img(), MImageOptions.getNormalImageOptions());
        tv_yel_title.setText("标题：" + yelInfoBean.getTitle());
        tv_yel_content.setText("内容：" + yelInfoBean.getContent());
        tv_con.setText("公司名称：" + yelInfoBean.getShort_name());

        tv_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(YelPageInfoDetActivity.this, PersonalPageActivity.class);
                intent.putExtra("uid", yelInfoBean.getMember_id());
                startActivity(intent);
            }
        });
    }

    @Event(value = {R.id.title_back, R.id.yel_page_info_comment})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.yel_page_info_comment:
                customPop.showAtLocation(info_lin, Gravity.BOTTOM, 0, 0);
                break;
        }
    }

    /**
     * 请求黄页评论列表
     */
    private void requestCommentList() {

        RequestParams params = MRequestParams.getNoTokenParams(Api.huangye_comment_list);
        params.addBodyParameter("hy_id", yelInfoBean.getId());

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<YelCommentBean> yelCommentBeans = gson.fromJson(json, new TypeToken<LinkedList<YelCommentBean>>() {
                    }.getType());
                    yelCommentAdapter.setData(yelCommentBeans);
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

    /**
     * 提交评论
     *
     * @param comment
     */
    private void requestSubmitComment(String comment) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.huangye_add_comment);
        params.addBodyParameter("user_id", UserInfoUtils.getUid());
        params.addBodyParameter("content", comment);
        params.addBodyParameter("hy_id", yelInfoBean.getId());

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("评论成功");
                    requestCommentList();
                } else {
                    ToastUtils.showToast("评论失败");
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
