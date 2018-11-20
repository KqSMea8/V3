package com.huanglong.v3.activities.message;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.message.BlePacAdapter;
import com.huanglong.v3.model.home.BlessingBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.UserInfoUtils;
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
 * Created by bin on 2018/3/17.
 * 福包页面
 */
@ContentView(R.layout.activity_blessing_packet)
public class BlePacActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;
    @ViewInject(R.id.bless_packet_list)
    private XRecyclerView bless_packet_list;

    private BlePacAdapter blePacAdapter;

    private int page = 1;

    private List<BlessingBean> blessingAllBeans = new ArrayList<>();

    public static BlePacActivity instance;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        instance = this;
        tv_title.setText("福包");
        tv_right.setText("发福包");


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        bless_packet_list.setLayoutManager(layoutManager);
        blePacAdapter = new BlePacAdapter();
        bless_packet_list.setAdapter(blePacAdapter);
        bless_packet_list.setLoadingListener(this);


    }

    @Override
    protected void logic() {
        bless_packet_list.refresh();

        blePacAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                BlessingBean blessingBean = (BlessingBean) obj;
                Intent intent = new Intent();
                intent.setClass(BlePacActivity.this, BlePacDetActivity.class);
                intent.putExtra("blessingBean", blessingBean);
                startActivity(intent);
            }
        });

    }


    @Event(value = {R.id.title_back, R.id.title_tv_right})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                BlePacActivity.this.finish();
                break;
            case R.id.title_tv_right:
                Intent intent = new Intent();
                intent.setClass(this, SendBlessActivity.class);
                startActivityForResult(intent, 1000);
                break;
        }
    }

    /**
     * 请求福包列表
     */
    private void requestBlessingData() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.fubao_list);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("page", page + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<BlessingBean> blessingBeans = gson.fromJson(json, new TypeToken<LinkedList<BlessingBean>>() {
                    }.getType());
                    if (blessingBeans != null) {
                        if (page == 1) {
                            blessingAllBeans.clear();
                            blessingAllBeans.addAll(blessingBeans);
                        } else {
                            blessingAllBeans.addAll(blessingBeans);
                        }
                    }
                    blePacAdapter.setData(blessingBeans);
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
                    bless_packet_list.refreshComplete();
                } else {
                    bless_packet_list.loadMoreComplete();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    @Override
    public void onRefresh() {
        page = 1;
        requestBlessingData();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestBlessingData();
    }
}
