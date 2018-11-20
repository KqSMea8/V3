package com.huanglong.v3.activities.homepage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;

/**
 * Created by bin on 2018/3/21.
 * 直播中的游戏
 */

@SuppressLint("ValidFragment")
public class GameFragment extends BaseFragment{

    private String classId;

    public GameFragment(String classId) {
        this.classId = classId;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game,container,false);
        return view;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void logic() {

    }
}
