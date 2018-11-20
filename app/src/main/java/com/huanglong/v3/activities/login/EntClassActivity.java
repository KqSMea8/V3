package com.huanglong.v3.activities.login;

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
import com.huanglong.v3.adapter.login.ClassAdapter;
import com.huanglong.v3.model.login.ClassBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/23.
 * 企业注册选择分类
 */
@ContentView(R.layout.activity_edt_class)
public class EntClassActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.ent_class_list)
    private RecyclerView class_list;

    private ClassAdapter classAdapter;

    private String class_one_name;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("选择一级分类");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        class_list.setLayoutManager(layoutManager);
        classAdapter = new ClassAdapter(1);
        classAdapter.isIcon = false;
        class_list.setAdapter(classAdapter);
        classAdapter.selType(ClassAdapter.SELECTED);
    }

    @Override
    protected void logic() {
        requestEntClass("0");

        classAdapter.setItemOnClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                ClassBean classBean = (ClassBean) obj;
                class_one_name = classBean.getName();
                Intent intent = new Intent();
                intent.setClass(EntClassActivity.this, EntClassTwoActivity.class);
                intent.putExtra("parent_id", classBean.getId());
                startActivityForResult(intent, 1000);
            }
        });

    }

    @Event(value = {R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

    /**
     * 请求分类
     *
     * @param parent_id
     */
    private void requestEntClass(String parent_id) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.industry_category);
        params.addBodyParameter("parent_id", parent_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<ClassBean> classBeans = gson.fromJson(json, new TypeToken<LinkedList<ClassBean>>() {
                    }.getType());
                    classAdapter.setData(classBeans);
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
                    if (data == null) return;
                    String class_id = data.getStringExtra("class_id");
                    String class_two_name = data.getStringExtra("class_name");
                    Intent intent = new Intent();
                    intent.putExtra("class_id", class_id);
                    intent.putExtra("class_two_name", class_two_name);
                    intent.putExtra("class_one_name", class_one_name);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    }
}
