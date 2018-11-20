package com.huanglong.v3.activities.message;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.barlibrary.ImmersionBar;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.homepage.PersonalPageActivity;
import com.huanglong.v3.activities.mine.BalanceActivity;
import com.huanglong.v3.adapter.message.BlessUserAdapter;
import com.huanglong.v3.model.home.BlessUserBean;
import com.huanglong.v3.model.home.BlessingBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.KeyBoardUtils;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.view.BlessInputPopup;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/3/18.
 * 福包详情页面
 */
@ContentView(R.layout.activity_blessing_packet_detail)
public class BlePacDetActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.title_rel)
    private RelativeLayout orange_title_bar_lin;
    @ViewInject(R.id.bless_packet_details_title)
    private TextView tv_blessing_title;
    @ViewInject(R.id.bless_packet_details_content)
    private TextView tv_blessing_content;
    @ViewInject(R.id.bless_packet_details_list)
    private RecyclerView details_list;
    @ViewInject(R.id.bless_packet_details_avatar)
    private ImageView img_avatar;
    @ViewInject(R.id.bless_packet_details_receive_num)
    private TextView tv_receive_num;
    @ViewInject(R.id.bless_packet_details_lin)
    private LinearLayout details_lin;
    @ViewInject(R.id.bless_packet_details_money_lin)
    private LinearLayout money_lin;
    @ViewInject(R.id.bless_packet_details_money)
    private TextView tv_money;
    @ViewInject(R.id.bless_packet_details_submit)
    private Button btn_submit;
    @ViewInject(R.id.bless_packet_details_pressword)
    private TextView tv_ble_pac_pressword;
    @ViewInject(R.id.bless_packet_details_enterprise)
    private ImageView img_enterprise;

    private BlessingBean blessingBean;

    private BlessUserAdapter blessUserAdapter;

    private BlessInputPopup blessInputPopup;

    public static BlePacDetActivity instance;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        instance = this;
        mImmersionBar = ImmersionBar.with(this);
//        mImmersionBar;   //所有子类都将继承这些相同的属性
        mImmersionBar.statusBarColor(R.color.transparent)
                .keyboardEnable(false)
                .init();

        tv_title.setText("福包详情");
        orange_title_bar_lin.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        details_list.setLayoutManager(layoutManager);
        blessUserAdapter = new BlessUserAdapter();
        details_list.setAdapter(blessUserAdapter);
        blessInputPopup = new BlessInputPopup(this);
    }

    @Override
    protected void logic() {

        Intent intent = getIntent();
        blessingBean = (BlessingBean) intent.getSerializableExtra("blessingBean");

        String title = blessingBean.getTitle();
        String content = blessingBean.getContent();
        tv_blessing_title.setText(title);
        tv_blessing_content.setText(content);
        x.image().bind(img_avatar, blessingBean.getHead_image(), MImageOptions.getCircularImageOptions());
        tv_receive_num.setText("已领取0/" + blessingBean.getAccount() + "个");
        tv_ble_pac_pressword.setText(blessingBean.getSecret_pwd() + "(口令)");
        requestUserList();
        showMoney();
        int type = blessingBean.getType();
        if (type == 2) {
            img_enterprise.setVisibility(View.VISIBLE);
        } else {
            img_enterprise.setVisibility(View.GONE);
        }

        blessInputPopup.setOnClickListener(new BlessInputPopup.OnClickListener() {
            @Override
            public void onClick(String str) {
                if (TextUtils.isEmpty(str)) {
                    ToastUtils.showToast("请输入口令");
                } else {
                    requestOpenBless(str);
                }
            }
        });
    }

    /**
     * 显示按钮
     */
    private void showMoney() {
        int flag = blessingBean.getFlag();
        if (flag == 1) {
            money_lin.setVisibility(View.VISIBLE);
            tv_money.setText(blessingBean.getPrice());
            btn_submit.setVisibility(View.GONE);
        } else {
            money_lin.setVisibility(View.GONE);
            btn_submit.setVisibility(View.VISIBLE);
        }


    }

    @Event(value = {R.id.title_back, R.id.bless_packet_details_submit, R.id.bless_packet_i_sender,
            R.id.bless_packet_details_avatar, R.id.bless_packet_put_forward})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                BlePacDetActivity.this.finish();
                break;
            case R.id.bless_packet_details_submit:
                KeyBoardUtils.openKeybord(blessInputPopup.getEditText(), BlePacDetActivity.this);
                blessInputPopup.showAtLocation(details_lin, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.bless_packet_i_sender:
                Intent intent = new Intent();
                intent.setClass(this, SendBlessActivity.class);
                startActivity(intent);
                break;
            case R.id.bless_packet_details_avatar:
                Intent intent1 = new Intent();
                intent1.setClass(this, PersonalPageActivity.class);
                intent1.putExtra("uid", blessingBean.getMember_id());
                startActivity(intent1);
                break;
            case R.id.bless_packet_put_forward:
                Intent intent2 = new Intent();
                intent2.setClass(this, BalanceActivity.class);
                startActivity(intent2);
                break;
        }
    }

    /**
     * 福包领取人数
     */
    private void requestUserList() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.zhongjiang_list);
        params.addBodyParameter("id", blessingBean.getId());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<BlessUserBean> blessUserBeans = gson.fromJson(json, new TypeToken<LinkedList<BlessUserBean>>() {
                    }.getType());
                    blessUserAdapter.setData(blessUserBeans);
                    tv_receive_num.setText("已领取" + blessUserBeans.size() + "/" + blessingBean.getAccount() + "个");
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
     * 打开福包
     *
     * @param key
     */
    private void requestOpenBless(String key) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.open_redpackage);
        params.addBodyParameter("secret", key);
        params.addBodyParameter("id", blessingBean.getId());
        params.addBodyParameter("member_id", UserInfoUtils.getUid());

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    blessingBean.setFlag(1);
                    showMoney();
                    requestUserList();
                    if (BlePacActivity.instance != null) {
                        BlePacActivity.instance.onRefresh();
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
