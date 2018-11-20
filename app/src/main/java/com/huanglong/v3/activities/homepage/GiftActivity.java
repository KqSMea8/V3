package com.huanglong.v3.activities.homepage;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.homepage.GiftAdapter;
import com.huanglong.v3.model.homepage.GiftBean;
import com.huanglong.v3.model.mine.UserInfoBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.voice.custom.CustomDialog;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/5/11.
 * 礼物的界面
 */
@ContentView(R.layout.activity_gift)
public class GiftActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @ViewInject(R.id.gift_list)
    private XRecyclerView gift_list;
    @ViewInject(R.id.gift_coin_count)
    private TextView tv_gift_coin;

    private int PAGE = 1;
    private List<GiftBean> giftAllBeans = new ArrayList<>();
    private GiftAdapter giftAdapter;

    private int currentPosition = -1;
    private UserInfoBean personalBean;
    private GiftBean giftBean;

    private String userId;
    private int type;//1-视频；2-k歌；3-直播
    private String info_id;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {

        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gift_list.setLayoutManager(layoutManager);
        gift_list.setLoadingListener(this);
        gift_list.setPullRefreshEnabled(false);
        giftAdapter = new GiftAdapter();
        gift_list.setAdapter(giftAdapter);
    }

    @Override
    protected void logic() {

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        type = intent.getIntExtra("type", 1);
        info_id = intent.getStringExtra("info_id");

        requestPersonal();
        gift_list.refresh();

        giftAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                giftBean = (GiftBean) obj;
                if (currentPosition != -1) {
                    giftAllBeans.get(currentPosition).setSelected(false);
                }
                giftBean.setSelected(true);
                currentPosition = position;
                giftAdapter.notifyDataSetChanged();
            }
        });

    }

    @Event(value = {R.id.gift_out_side, R.id.gift_confirm})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.gift_out_side:
                finish();
                break;
            case R.id.gift_confirm:
                if (giftBean != null) {
                    int v_coin = giftBean.getV_coin();
                    int blance = (int) personalBean.getBlance();
                    if (blance > v_coin) {
                        requestSendGift();
                    } else {
                        CustomDialog customDialog = new CustomDialog(this, "对不起，您的余额不足？\n是否去充值") {
                            @Override
                            public void EnsureEvent() {
                                dismiss();
                            }
                        };
                        customDialog.setCanceledOnTouchOutside(false);
                        customDialog.show();
                    }
                } else {
                    ToastUtils.showToast("请选择需要发送的礼物");
                }

                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0, R.anim.exit_bottom);
    }

    /**
     * 请求礼物列表
     */
    private void requestGiftList() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.liveplay_gift_list);
        params.addBodyParameter("page", PAGE + "");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<GiftBean> giftBeans = gson.fromJson(json, new TypeToken<LinkedList<GiftBean>>() {
                    }.getType());
                    if (PAGE == 1) {
                        giftAllBeans.clear();
                        giftAllBeans.addAll(giftBeans);
                    } else {
                        giftAllBeans.addAll(giftBeans);
                    }
                    giftAdapter.setData(giftAllBeans);
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
                if (PAGE == 1) {
                    gift_list.refreshComplete();
                } else {
                    gift_list.loadMoreComplete();
                }
            }
        });

    }

    @Override
    public void onRefresh() {
        PAGE = 1;
        requestGiftList();
    }

    @Override
    public void onLoadMore() {
        PAGE++;
        requestGiftList();

    }


    /**
     * 请求个人资料
     */
    private void requestPersonal() {

        RequestParams params = MRequestParams.getUidParams(Api.getUserInfo);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    personalBean = gson.fromJson(json, UserInfoBean.class);
                    if (personalBean != null) {
                        tv_gift_coin.setText(getMoney(personalBean.getBlance() == 0 ? 0 : personalBean.getBlance()));
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
     * 保留两位小数
     *
     * @param balance
     * @return
     */
    private String getMoney(double balance) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(balance);
    }

    /**
     * 发送礼物
     */
    private void requestSendGift() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.liveplay_send_gift);
        params.addBodyParameter("user_id", userId);
        params.addBodyParameter("follower_id", UserInfoUtils.getUid());
        params.addBodyParameter("type", type + "");
        params.addBodyParameter("gift_id", giftBean.getId());
        params.addBodyParameter("info_id", info_id);


        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("礼物赠送成功");
                    if (type == 3) {
                        Intent intent = new Intent();
                        intent.putExtra("gift", giftBean);
                        setResult(RESULT_OK, intent);
                    } else {
                        setResult(RESULT_OK);
                    }
                    finish();
                } else {
                    ToastUtils.showToast("礼物赠送失败");
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
