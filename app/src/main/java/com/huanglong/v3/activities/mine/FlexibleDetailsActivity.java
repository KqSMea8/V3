package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.model.mine.FlexibleBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by bin on 2018/4/17.
 * 活动详情
 */
@ContentView(R.layout.activity_flexible_details)
public class FlexibleDetailsActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.flexible_details_img)
    private ImageView img_cover;
    @ViewInject(R.id.flexible_details_is_free)
    private TextView tv_is_free;
    @ViewInject(R.id.flexible_details_title)
    private TextView tv_flexible_title;
    @ViewInject(R.id.flexible_details_enroll)
    private TextView tv_enroll;
    @ViewInject(R.id.flexible_details_time)
    private TextView tv_flexible_time;
    @ViewInject(R.id.flexible_details_region)
    private TextView tv_flexible_region;
    @ViewInject(R.id.flexible_details_max_enroll_number)
    private TextView tv_max_enroll_number;
    @ViewInject(R.id.flexible_details_content)
    private WebView flexible_content;
    @ViewInject(R.id.flexible_details_btn_enroll)
    private Button btn_enroll;


    private String flexible_id;
    private int is_enroll;
    private String price = "0";


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("活动详情");
        initWebView();
    }

    @Override
    protected void logic() {
        Intent intent = getIntent();
        flexible_id = intent.getStringExtra("flexible_id");
        requestDetails();
    }

    @Event(value = {R.id.title_back, R.id.flexible_details_btn_enroll})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                FlexibleDetailsActivity.this.finish();
                break;
            case R.id.flexible_details_btn_enroll:
                if (is_enroll == 0) {
                    Intent intent = new Intent();
                    intent.setClass(this, FlexibleEnrollActivity.class);
                    intent.putExtra("flexible_id", flexible_id);
                    intent.putExtra("price", price);
                    startActivityForResult(intent, 1000);
                } else {
                    ToastUtils.showToast("你已报名");
                }
                break;
        }
    }

    /**
     * 初始化设置webview
     */
    private void initWebView() {
        flexible_content.getSettings().setJavaScriptEnabled(true);
        flexible_content.getSettings().setUseWideViewPort(true);
        flexible_content.getSettings().setLoadWithOverviewMode(true);

    }

    /**
     * 请求活动详情
     */
    private void requestDetails() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.activity_detail);
        params.addBodyParameter("id", flexible_id);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    FlexibleBean flexibleBean = gson.fromJson(json, FlexibleBean.class);
                    showInfo(flexibleBean);
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
     * 显示详情
     *
     * @param flexibleBean
     */
    private void showInfo(FlexibleBean flexibleBean) {
        price = flexibleBean.getPrice();
        x.image().bind(img_cover, flexibleBean.getImg_url(), MImageOptions.getNormalImageOptions());
        tv_flexible_title.setText(flexibleBean.getTitle());
        tv_enroll.setText(flexibleBean.getEnroll_count() + "报名");
        tv_flexible_time.setText(flexibleBean.getStart_time() + "~" + flexibleBean.getEnd_time());
        tv_flexible_region.setText(flexibleBean.getCity());
        tv_max_enroll_number.setText(flexibleBean.getActivity_count() + "人");
        flexible_content.loadDataWithBaseURL(null, flexibleBean.getContent(), "text/html", "utf-8", null);
        int is_fee = flexibleBean.getIs_fee();
        if (is_fee == 0) {
            tv_is_free.setVisibility(View.VISIBLE);
        } else {
            tv_is_free.setVisibility(View.GONE);
        }
        is_enroll = flexibleBean.getIs_enroll();
        if (is_enroll == 0) {
            btn_enroll.setText("我要报名");
            btn_enroll.setBackgroundResource(R.drawable.frame_orange);
        } else {
            btn_enroll.setText("你已报名");
            btn_enroll.setBackgroundResource(R.drawable.box_gray);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    is_enroll = 1;
                    btn_enroll.setText("你已报名");
                    btn_enroll.setBackgroundResource(R.drawable.box_gray);
                    break;
            }
        }
    }
}
