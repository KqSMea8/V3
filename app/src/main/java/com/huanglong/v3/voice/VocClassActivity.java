package com.huanglong.v3.voice;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.voice.adapter.VocClassAdapter;
import com.huanglong.v3.voice.entity.ChapterBean;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/5/3.
 * 选择分类
 */
@ContentView(R.layout.activity_voc_class)
public class VocClassActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;
    @ViewInject(R.id.voc_class_list)
    private RecyclerView class_list;

    private VocClassAdapter vocClassAdapter;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("选择分类");
        tv_right.setText("添加");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        class_list.setLayoutManager(layoutManager);
        vocClassAdapter = new VocClassAdapter();
        class_list.setAdapter(vocClassAdapter);

    }

    @Override
    protected void logic() {
        requestVocClass();

        vocClassAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                ChapterBean liveClassBean = (ChapterBean) obj;
                Intent intent = new Intent();
                intent.putExtra("class_name", liveClassBean.getTitle());
                intent.putExtra("class_id", liveClassBean.getId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    @Event(value = {R.id.title_back, R.id.title_tv_right})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_tv_right:
                Intent intent = new Intent();
                intent.setClass(this, VocAddClassActivity.class);
                startActivityForResult(intent, 1000);
                break;
        }

    }

    /**
     * 请求分类
     */
    private void requestVocClass() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.book_first_cate_list);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<ChapterBean> chapterBean = gson.fromJson(json, new TypeToken<LinkedList<ChapterBean>>() {
                    }.getType());
                    vocClassAdapter.setData(chapterBean);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    requestVocClass();
                    break;
            }
        }
    }
}
