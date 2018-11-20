package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.mine.AccountDetailsAdapter;
import com.huanglong.v3.model.mine.AccountDetailsBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
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
 * Created by bin on 2018/1/31.
 * 账户明细
 */
@ContentView(R.layout.activity_account_details)
public class AccountDetailsActivity extends BaseActivity implements XRecyclerView.LoadingListener {


    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.account_details_list)
    private XRecyclerView details_list;

    private int page = 1;

    private List<AccountDetailsBean> accountDetailsBeans = new ArrayList<>();

    private AccountDetailsAdapter accountDetailsAdapter;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("账户明细");


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        details_list.setLayoutManager(linearLayoutManager);
        accountDetailsAdapter = new AccountDetailsAdapter();
        details_list.setAdapter(accountDetailsAdapter);

        details_list.setLoadingListener(this);
    }

    @Override
    protected void logic() {


        details_list.refresh();
    }


    @Event(value = {R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                AccountDetailsActivity.this.finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        requestAccountDetails();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestAccountDetails();
    }

    /**
     * 请求账户明细
     */
    private void requestAccountDetails() {
        final RequestParams params = MRequestParams.getUidParams(Api.user_withdraw);
        params.addBodyParameter("page", page + "");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<AccountDetailsBean> accountDetailsBean = gson.fromJson(json, new TypeToken<LinkedList<AccountDetailsBean>>() {
                    }.getType());
                    if (accountDetailsBean != null) {
                        if (page == 1) {
                            accountDetailsBeans.clear();
                            accountDetailsBeans.addAll(accountDetailsBean);
                        } else {
                            accountDetailsBeans.addAll(accountDetailsBean);
                        }

                        accountDetailsAdapter.setData(accountDetailsBeans);
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
                if (page == 1) {
                    details_list.refreshComplete();
                } else {
                    details_list.loadMoreComplete();
                }
            }
        });

    }

}
