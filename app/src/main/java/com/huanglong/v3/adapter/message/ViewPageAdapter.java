package com.huanglong.v3.adapter.message;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by bin on 2018/3/1.
 * 首页的pager
 */

public class ViewPageAdapter extends PagerAdapter {
    private List<View> views;
    private Context context;

    //有参构造
    public ViewPageAdapter(Context context) {
        super();
        this.context = context;
    }

    /**
     * 添加数据
     *
     * @param views
     */
    public void setData(List<View> views) {
        this.views = views;
        notifyDataSetChanged();
    }

    //获得长度
    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    //展示的view
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //获得展示的view
        View view = views.get(position);
        //添加到容器
        container.addView(view);
        //返回显示的view
        return view;
    }

    //销毁view
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //从容器中移除view
        container.removeView((View) object);
    }


}
