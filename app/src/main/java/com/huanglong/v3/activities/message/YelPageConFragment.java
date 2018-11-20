package com.huanglong.v3.activities.message;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.login.ClassAdapter;
import com.huanglong.v3.adapter.message.YellowAdapter;
import com.huanglong.v3.model.home.YellowBean;
import com.huanglong.v3.model.login.ClassBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.PopupUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zyyoona7.lib.EasyPopup;
import com.zyyoona7.lib.HorizontalGravity;
import com.zyyoona7.lib.VerticalGravity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/5/16.
 * 黄页公司
 */

public class YelPageConFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    @ViewInject(R.id.yellow_page_list)
    private XRecyclerView yellow_list;
    @ViewInject(R.id.yellow_page_menu_list)
    private RecyclerView menu_list;
    @ViewInject(R.id.yellow_page_con_lin)
    private LinearLayout yellow_page_con_lin;
    @ViewInject(R.id.yellow_page_menu_lin)
    private LinearLayout menu_lin;

    private RecyclerView menu_one;

    private List<YellowBean> yellowAllBeans = new ArrayList<>();

    private int page = 1;
    private String class_id = "";
    private int requestType;//t=0一级分类（），t=1，二级分类
    private String keword = "";

    private YellowAdapter yellowAdapter;
    private ClassAdapter classAdapter;
    private ClassAdapter classOneAdapter;
    private ClassAdapter classTwoAdapter;
    private List<ClassBean> classBeans;
    private List<ClassBean> classTwoBeans;

    private EasyPopup menu_pop;

    private int currentPosition = 0;
    private int currentOnePosition = 0;
    private int currentTwoPosition = -1;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yel_page_con, container, false);
        return view;
    }

    @Override
    protected void initView() {
        menu_lin.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        menu_list.setLayoutManager(layoutManager1);
        classAdapter = new ClassAdapter(2);
        classAdapter.isIcon = false;
        menu_list.setAdapter(classAdapter);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        yellow_list.setLayoutManager(layoutManager);
        yellow_list.setLoadingListener(this);
        yellowAdapter = new YellowAdapter();
        yellow_list.setAdapter(yellowAdapter);


        requestEntClass("0", 1);

        initPop();
    }

    @Override
    protected void logic() {
        yellowAdapter.setOnItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                YellowBean yellowBean = (YellowBean) obj;
                if (type == 1) {
                    String phone = yellowBean.getUsername();
                    if (!TextUtils.isEmpty(phone)) {
                        Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent2);
                    }
                } else if (type == 2) {
                    Intent intent2 = new Intent();
                    intent2.setClass(getActivity(), YelPagDetActivity.class);
                    intent2.putExtra("yellowBean", yellowBean);
                    startActivity(intent2);
                }
            }

            @Override
            public void onItemViewClick(Object obj, int position, int type, View view) {

            }
        });


        classAdapter.setItemOnClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                ClassBean classBean = (ClassBean) obj;
                classBeans.get(currentPosition).setSelected(false);
                classBean.setSelected(true);
                class_id = classBean.getId();
                requestType = 1;
                classAdapter.notifyDataSetChanged();
                classOneAdapter.notifyDataSetChanged();
                currentPosition = position;
                menu_one.scrollToPosition(position);
                menu_list.scrollToPosition(position);
                requestEntClass(class_id, 2);
                yellow_list.refresh();
            }
        });
    }

    @Event(value = {R.id.yellow_page_menu_more})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.yellow_page_menu_more:
                menu_pop.showAtAnchorView(menu_list, VerticalGravity.BELOW, HorizontalGravity.ALIGN_LEFT, 0, 0);
                break;
        }
    }

    /**
     * 初始化设置pop
     */
    private void initPop() {
        menu_pop = PopupUtils.initMatchPopup(getActivity(), R.layout.pop_yel_con_class, yellow_page_con_lin);

        menu_one = menu_pop.getView(R.id.yel_con_menu_one);
        RecyclerView menu_two = menu_pop.getView(R.id.yel_con_menu_two);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        menu_one.setLayoutManager(layoutManager);
        classOneAdapter = new ClassAdapter(1);
        classOneAdapter.isIcon = false;
        menu_one.setAdapter(classOneAdapter);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        menu_two.setLayoutManager(layoutManager1);
        classTwoAdapter = new ClassAdapter(1);
        classTwoAdapter.isIcon = false;
        menu_two.setAdapter(classTwoAdapter);


        classOneAdapter.setItemOnClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                ClassBean classBean = (ClassBean) obj;
                classBeans.get(currentOnePosition).setSelected(false);
                classBean.setSelected(true);
                class_id = classBean.getId();
                currentOnePosition = position;
                classOneAdapter.notifyDataSetChanged();
                classAdapter.notifyDataSetChanged();
                requestEntClass(class_id, 2);
            }
        });


        classTwoAdapter.setItemOnClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                menu_pop.dismiss();
                ClassBean classBean = (ClassBean) obj;
                class_id = classBean.getId();
                classAdapter.notifyDataSetChanged();
                classOneAdapter.notifyDataSetChanged();
                menu_list.scrollToPosition(currentOnePosition);
                requestType = 2;
                yellow_list.refresh();
            }
        });

    }


    /**
     * 请求黄页列表
     */
    private void requestYellowList() {

        RequestParams params = MRequestParams.getNoTokenParams(Api.huangye_list);
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("user_id", UserInfoUtils.getUid());
        params.addBodyParameter("industry_id", class_id);
        params.addBodyParameter("t", requestType + "");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<YellowBean> yellowBeans = gson.fromJson(json, new TypeToken<LinkedList<YellowBean>>() {
                    }.getType());
                    if (yellowBeans != null) {
                        if (page == 1) {
                            yellowAllBeans.clear();
                            yellowAllBeans.addAll(yellowBeans);
                        } else {
                            yellowAllBeans.addAll(yellowBeans);
                        }
                    }
                    yellowAdapter.setData(yellowAllBeans);
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
                    yellow_list.refreshComplete();
                } else {
                    yellow_list.loadMoreComplete();
                }
            }
        });

    }

    @Override
    public void onRefresh() {
        page = 1;
        requestYellowList();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestYellowList();
    }

    /**
     * 请求分类
     *
     * @param parent_id
     * @param type      1.一直分类 2.二级分类
     */
    private void requestEntClass(String parent_id, int type) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.industry_category);
        params.addBodyParameter("parent_id", parent_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    if (type == 1) {
                        classBeans = gson.fromJson(json, new TypeToken<LinkedList<ClassBean>>() {
                        }.getType());
                        if (classBeans != null && classBeans.size() > 0) {
                            ClassBean classBean = new ClassBean();
                            classBean.setId("-1");
                            classBean.setName("全部");
                            classBeans.add(0, classBean);
                            classBeans.get(0).setSelected(true);
                            classAdapter.setData(classBeans);
                            classOneAdapter.setData(classBeans);
                            requestEntClass(classBeans.get(0).getId(), 2);
                            class_id = classBeans.get(0).getId();
                            requestType = 1;
                            yellow_list.refresh();
                        }
                    } else {
                        classTwoBeans = gson.fromJson(json, new TypeToken<LinkedList<ClassBean>>() {
                        }.getType());
                        classTwoAdapter.setData(classTwoBeans);
                        if (classTwoBeans == null || classTwoBeans.size() == 0) {
                            menu_pop.dismiss();
                            requestType = 1;
                            yellow_list.refresh();
                        }
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
