package com.huanglong.v3.activities.message;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.ClassAdapter;
import com.huanglong.v3.adapter.TabAdapter;
import com.huanglong.v3.model.homepage.LiveClassBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/5/16.
 * 黄页 信息
 */

public class YelPageInfoFragment extends BaseFragment {

    @ViewInject(R.id.yel_info_menu)
    private RecyclerView menu_list;
    @ViewInject(R.id.yel_info_view_pager)
    private ViewPager view_pager;

    private ClassAdapter classAdapter;

    private int currentPosition = -1;
    private List<LiveClassBean> liveClassBeans;

    private TabAdapter tabAdapter;
    private List<Fragment> fragments = new ArrayList<>();


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yel_page_info, container, false);
        return view;
    }

    @Override
    protected void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        menu_list.setLayoutManager(layoutManager);
        classAdapter = new ClassAdapter();
        menu_list.setAdapter(classAdapter);
        requestInfoClass();
    }

    @Override
    protected void logic() {

        classAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                LiveClassBean liveClassBean = (LiveClassBean) obj;
                if (currentPosition != -1) {
                    liveClassBeans.get(currentPosition).setSelected(false);
                }
                view_pager.setCurrentItem(position);
                liveClassBean.setSelected(true);
                classAdapter.notifyDataSetChanged();
                currentPosition = position;
            }
        });


        view_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (currentPosition != -1) {
                    liveClassBeans.get(currentPosition).setSelected(false);
                }
                view_pager.setCurrentItem(position);
                liveClassBeans.get(position).setSelected(true);
                classAdapter.notifyDataSetChanged();
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 黄页信息分类
     */
    private void requestInfoClass() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.huangye_huangye_cate_list);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    liveClassBeans = gson.fromJson(json, new TypeToken<LinkedList<LiveClassBean>>() {
                    }.getType());
                    if (liveClassBeans != null && liveClassBeans.size() > 0) {
                        liveClassBeans.get(0).setSelected(true);
                        currentPosition = 0;
                    }
                    classAdapter.setData(liveClassBeans);

                    initTabItem(liveClassBeans);
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


    private void initTabItem(List<LiveClassBean> liveClassBeans) {
        fragments.clear();
        List<String> menu = new ArrayList<>();
        for (LiveClassBean liveClassBean : liveClassBeans) {
            menu.add(liveClassBean.getName());
            fragments.add(new YelPageInfoListFragment(liveClassBean.getId()));
        }
        tabAdapter = new TabAdapter(getActivity().getSupportFragmentManager(), menu);
        tabAdapter.setFragmentData(fragments);
        view_pager.setAdapter(tabAdapter);
        view_pager.setCurrentItem(0);
    }

    /**
     * 刷新数据
     */
    public void onRefresh() {
        if (fragments != null && fragments.size() > 0) {
            for (int i = 0; i < fragments.size(); i++) {
                YelPageInfoListFragment yelPageInfoListFragment = (YelPageInfoListFragment) fragments.get(i);
                if (yelPageInfoListFragment.yelInfoAdapter != null) {
                    yelPageInfoListFragment.onRefresh();
                }
            }
        }

    }

}
