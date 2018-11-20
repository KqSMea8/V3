package com.huanglong.v3.activities.mine;

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
import com.huanglong.v3.adapter.mine.ThemeClassAdapter;
import com.huanglong.v3.model.homepage.PerClassBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.PromptDialog;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.view.PromptEditDialog;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/21.
 * 主题分类
 */
@ContentView(R.layout.activity_theme_class)
public class ThemeClassActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;
    @ViewInject(R.id.theme_class_list)
    private RecyclerView class_list;

    private TextView tv_dialog_content;

    private List<PerClassBean> perClassBeans;
    private ThemeClassAdapter themeClassAdapter;

    private PromptEditDialog promptEditDialog;

    private PromptDialog promptDialog;

    private int editType;//1.增加 ，2.修改
    private PerClassBean perClassBean;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("选择类别");
        tv_right.setText("添加");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        class_list.setLayoutManager(layoutManager);
        themeClassAdapter = new ThemeClassAdapter();
        class_list.setAdapter(themeClassAdapter);

        initDialog();

    }

    @Override
    protected void logic() {
        requestClass();


        themeClassAdapter.setOnItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                perClassBean = (PerClassBean) obj;
                if (type == 1) {//选择
                    Intent intent = new Intent();
                    intent.putExtra("class_name", perClassBean.getName());
                    intent.putExtra("class_id", perClassBean.getId());
                    setResult(RESULT_OK, intent);
                    ThemeClassActivity.this.finish();
                } else if (type == 2) {//修改
                    editType = 2;
                    promptEditDialog.setDialogStyle("修改分类", "取消", "确认", "请输入分类名", 1, 2);
                    promptEditDialog.show();
                } else if (type == 3) {//删除
                    tv_dialog_content.setText("是否删除该主题?");
                    promptDialog.show();
                }

            }

            @Override
            public void onItemViewClick(Object obj, int position, int type, View view) {

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
                editType = 1;
                promptEditDialog.setDialogStyle("添加分类", "取消", "确认", "请输入分类名", 1, 2);
                promptEditDialog.show();
                break;
        }
    }

    /**
     * 初始化dialog
     */
    private void initDialog() {
        promptDialog = new PromptDialog(getActivity(), R.layout.dialog_hint_currency);
        tv_dialog_content = (TextView) promptDialog.getView(R.id.dialog_comment_content);
        promptDialog.getView(R.id.dialog_comment_cancel).setOnClickListener(dialogClick);
        promptDialog.getView(R.id.dialog_comment_confirm).setOnClickListener(dialogClick);
        promptDialog.getView(R.id.dialog_comment_lin).setOnClickListener(dialogClick);

        promptEditDialog = new PromptEditDialog(getActivity());
        promptEditDialog.setOnClickListener(new PromptEditDialog.OnClickListener() {
            @Override
            public void onClick(int flag, String str) {
                if (!TextUtils.isEmpty(str)) {
                    requestAddClass(str);
                } else {
                    ToastUtils.showToast("请输入分类名称");
                }
            }
        });
    }


    private View.OnClickListener dialogClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.dialog_comment_cancel:
                    promptDialog.dismiss();
                    break;
                case R.id.dialog_comment_confirm:
                    promptDialog.dismiss();
                    requestDeleteTheme();
                    break;
                case R.id.dialog_comment_lin:
                    promptDialog.dismiss();
                    break;
            }
        }
    };


    /**
     * 请求我的分类接口
     */
    private void requestClass() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.member_subject_category);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    perClassBeans = gson.fromJson(json, new TypeToken<LinkedList<PerClassBean>>() {
                    }.getType());
                    themeClassAdapter.setData(perClassBeans);
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
     * 请求添加分类
     *
     * @param className
     */
    private void requestAddClass(String className) {
        RequestParams params;
        if (editType == 1) {
            params = MRequestParams.getNoTokenParams(Api.user_add_subject);
        } else {
            params = MRequestParams.getNoTokenParams(Api.edit_subject);
            params.addBodyParameter("id", perClassBean.getId());
        }
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("name", className);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    if (editType == 1) {
                        ToastUtils.showToast("添加成功");
                    } else {
                        ToastUtils.showToast("修改成功");
                    }

                    requestClass();
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
     * 删除主题
     */
    private void requestDeleteTheme() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.delete_subject);
        params.addBodyParameter("cate_id", perClassBean.getId());
        params.addBodyParameter("member_id", UserInfoUtils.getUid());

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("删除成功");
                    requestClass();
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
