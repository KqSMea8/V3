package com.huanglong.v3.activities.homepage;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.barlibrary.ImmersionBar;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.H5Activity;
import com.huanglong.v3.adapter.homepage.ProgramAdapter;
import com.huanglong.v3.model.homepage.ProgramBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.KeyBoardUtils;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.view.QRCodeDialog;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/10/10.
 * 搜索小程序页面
 */
@ContentView(R.layout.activity_search_friends)
public class SearchProgramActivity extends BaseActivity {

    @ViewInject(R.id.search_edt)
    private EditText edt_search;
    @ViewInject(R.id.search_list)
    private RecyclerView search_list;

    private ProgramAdapter programAdapter;

    private ImageView img_group_code;

    private QRCodeDialog qrCodeDialog;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {

        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarDarkFont(true);
//        mImmersionBar;   //所有子类都将继承这些相同的属性
        mImmersionBar.statusBarColor(R.color.transparent)
                .keyboardEnable(true)
                .init();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        search_list.setLayoutManager(layoutManager);
        programAdapter = new ProgramAdapter();
        search_list.setAdapter(programAdapter);

        initDialog();
    }

    @Override
    protected void logic() {

        programAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                ProgramBean programBean = (ProgramBean) obj;
                if (programBean.getType() == 0) {//微信小程序，展示二维码
                    qrCodeDialog.show();
                    x.image().bind(img_group_code, programBean.getUrl(), MImageOptions.getNormalImageOptions());
                } else {//h5页面显示
                    Intent intent = new Intent();
                    intent.setClass(SearchProgramActivity.this, H5Activity.class);
                    intent.putExtra("title", "小程序");
                    intent.putExtra("url", programBean.getUrl());
                    startActivity(intent);
                }
            }
        });

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = textView.getText().toString().trim();
                    searchData(key);
                    return true;
                }
                return true;
            }
        });

    }

    /**
     * 初始化dialog
     */
    private void initDialog() {
        qrCodeDialog = new QRCodeDialog(getActivity());
        img_group_code = (ImageView) qrCodeDialog.getView(R.id.dialog_qr_code_img);
    }


    @Event(value = {R.id.search_cancel})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_cancel:
                KeyBoardUtils.hideKeyboard(this);
                SearchProgramActivity.this.finish();
                break;
        }
    }


    /**
     * 请求搜索数据
     *
     * @param key
     */
    private void searchData(String key) {
        showDialog();
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_getProgramList);
        params.addBodyParameter("user_id", UserInfoUtils.getUid());
        params.addBodyParameter("category_id", "-1");
        params.addBodyParameter("name", key);
        params.addBodyParameter("page", "1");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<ProgramBean> programBean = gson.fromJson(json, new TypeToken<LinkedList<ProgramBean>>() {
                    }.getType());
                    programAdapter.setData(programBean);
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
                dismissDialog();
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            KeyBoardUtils.hideKeyboard(this);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
