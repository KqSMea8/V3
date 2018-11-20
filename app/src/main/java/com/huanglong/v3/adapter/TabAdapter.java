package com.huanglong.v3.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by bin on 2018/3/5.
 * tab 的 adapter
 */

public class TabAdapter extends FragmentStatePagerAdapter {

    private List<String> title;

    private List<Fragment> fragments;

    public TabAdapter(FragmentManager fm, List<String> title) {
        super(fm);
        this.title = title;
    }


    public void setTitle(List<String> title) {
        this.title = title;
        notifyDataSetChanged();
    }

    /**
     * 添加fragment数据
     *
     * @param fragments
     */
    public void setFragmentData(List<Fragment> fragments) {
        this.fragments = fragments;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int index) {
        Fragment frag = fragments.get(index);
        return frag;
    }

    @Override
    public int getCount() {
        if (fragments != null) {
            return fragments.size();
        }
        return 0;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if (title != null) {
            return title.get(position);
        }
        return "";
    }
}


