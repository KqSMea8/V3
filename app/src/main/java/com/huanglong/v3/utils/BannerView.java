package com.huanglong.v3.utils;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.huanglong.v3.R;
import com.huanglong.v3.adapter.LocalImageHolderView;

import java.util.List;

/**
 * Created by bin on 2017/10/17.
 * banner 工具类
 */

public class BannerView {

    public static void setData(List<String> datas, ConvenientBanner mBanner) {
        if (datas == null) {
            return;
        }
        mBanner.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, datas);
        if (datas.size() == 1) {
            mBanner.startTurning(2000000);
        } else {
            mBanner.startTurning(3000);
        }
        //自定义你的Holder，实现更多复杂的界面，不一定是图片翻页，其他任何控件翻页亦可。
        mBanner.setPointViewVisible(true);
        //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
        mBanner.setPageIndicator(new int[]{R.drawable.gray_radius, R.drawable.white_radius});
        //设置指示器的方向
        mBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        //设置两秒延时自动轮播
//        mBanner.startTurning(2000);

        //设置翻页的效果，不需要翻页效果可用不设
        //.setPageTransformer(Transformer.DefaultTransformer);    集成特效之后会有白屏现象，新版已经分离，如果要集成特效的例子可以看Demo的点击响应。
//        convenientBanner.setManualPageable(false);//设置不能手动影响
    }
}
