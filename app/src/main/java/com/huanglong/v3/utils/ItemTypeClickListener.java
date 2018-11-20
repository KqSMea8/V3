package com.huanglong.v3.utils;

import android.view.View;

/**
 * Created by bin on 2018/1/17.
 * list item 监听
 */

public interface ItemTypeClickListener {

    void onItemClick(Object obj, int position, int type);

    void onItemViewClick(Object obj, int position, int type, View view);
}
