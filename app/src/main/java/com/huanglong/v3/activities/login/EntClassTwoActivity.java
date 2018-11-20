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
import com.huanglong.v3.utils.ToastUtils;

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
public class EntClassTwoActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;
    @ViewInject(R.id.ent_class_list)
    private RecyclerView class_list;

    private ClassAdapter classAdapter;


    private String parent_id;
    private String sel_class_id;
    private String sel_class_name;


    private List<ClassBean> classBeans;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("选择二级分类");
        tv_right.setText("确定");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        class_list.setLayoutManager(layoutManager);
        classAdapter = new ClassAdapter(1);
        class_list.setAdapter(classAdapter);
        classAdapter.isIcon = false;
        classAdapter.selType(ClassAdapter.SINGLE_SELECTED);

    }

    @Override
    protected void logic() {

        Intent intent = getIntent();
        parent_id = intent.getStringExtra("parent_id");

        requestEntClass(parent_id);

        classAdapter.setItemOnClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                for (ClassBean classBean : classBeans) {
                    classBean.setSelected(false);
                }
                ClassBean classBean1 = (ClassBean) obj;
                classBean1.setSelected(true);
                classAdapter.notifyDataSetChanged();
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
                for (ClassBean classBean : classBeans) {
                    boolean selected = classBean.isSelected();
                    if (selected) {
                        sel_class_id = classBean.getId();
                        sel_class_name = classBean.getName();
                    }
                }

                if (!TextUtils.isEmpty(sel_class_id)) {
                    Intent intent = new Intent();
                    intent.putExtra("class_id", sel_class_id);
                    intent.putExtra("class_name", sel_class_name);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    ToastUtils.showToast("请选择分类");
                }

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
                    classBeans = gson.fromJson(json, new TypeToken<LinkedList<ClassBean>>() {
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

}
