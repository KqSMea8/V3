package com.huanglong.v3.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.huanglong.v3.R;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.config.ISListConfig;

/**
 * Created by bin on 2018/1/24.
 * 选择图片工具类
 */

public class SelectPictureUtils {

    /**
     * 选择图片
     *
     * @param context
     * @param selectionMode 选择模式 true 多选 ， false 单选
     * @param maxSelectNum  最多选择几张
     * @param size          裁剪尺寸
     */
    public static void selectPicture(Activity context, boolean selectionMode, int maxSelectNum, int size) {
        if (size != 0) {
            // 自由配置选项
            ISListConfig config = new ISListConfig.Builder()
                    // 是否多选, 默认true
                    .multiSelect(selectionMode)
                    // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                    .rememberSelected(false)
                    // “确定”按钮背景色
                    .btnBgColor(ContextCompat.getColor(context, R.color.orange_FC6C57))
                    // “确定”按钮文字颜色
                    .btnTextColor(ContextCompat.getColor(context, R.color.white))
                    // 使用沉浸式状态栏
                    .statusBarColor(ContextCompat.getColor(context, R.color.orange_FC6C57))
                    // 返回图标ResId
                    .backResId(R.mipmap.icon_white_back)
                    // 标题
                    .title("选择图片")
                    // 标题文字颜色
                    .titleColor(ContextCompat.getColor(context, R.color.white))
                    // TitleBar背景色
                    .titleBgColor(ContextCompat.getColor(context, R.color.orange_FC6C57))
                    // 裁剪大小。needCrop为true的时候配置
//                    .cropSize(1, 1, 200, 200)
                    .needCrop(false)
                    // 第一个是否显示相机，默认true
                    .needCamera(true)
                    // 最大选择图片数量，默认9
                    .maxNum(maxSelectNum)
                    .build();
            // 跳转到图片选择器
            ISNav.getInstance().toListActivity(context, config, Common.IMAGE_PICKER);

        } else {
            // 自由配置选项
            ISListConfig config = new ISListConfig.Builder()
                    // 是否多选, 默认true
                    .multiSelect(selectionMode)
                    // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                    .rememberSelected(false)
                    // “确定”按钮背景色
                    .btnBgColor(ContextCompat.getColor(context, R.color.orange_FC6C57))
                    // “确定”按钮文字颜色
                    .btnTextColor(ContextCompat.getColor(context, R.color.white))
                    // 使用沉浸式状态栏
                    .statusBarColor(ContextCompat.getColor(context, R.color.orange_FC6C57))
                    // 返回图标ResId
                    .backResId(R.mipmap.icon_white_back)
                    // 标题
                    .title("选择图片")
                    // 标题文字颜色
                    .titleColor(ContextCompat.getColor(context, R.color.white))
                    // TitleBar背景色
                    .titleBgColor(ContextCompat.getColor(context, R.color.orange_FC6C57))
                    .needCrop(true)
                    // 裁剪大小。needCrop为true的时候配置
                    .cropSize(1, 1, 350, 350)
                    // 第一个是否显示相机，默认true
                    .needCamera(true)
                    // 最大选择图片数量，默认9
                    .maxNum(maxSelectNum)
                    .build();
            // 跳转到图片选择器
            ISNav.getInstance().toListActivity(context, config, Common.IMAGE_PICKER);
        }
    }

    /**
     * 选择身份证图片
     *
     * @param context
     * @param cropWidth
     * @param cropHeight
     */
    public static void selectedIdCard(Activity context, int cropWidth, int cropHeight) {
//        ImagePicker imagePicker = V3Application.getImagePicker();
//        imagePicker.setShowCamera(true);  //显示拍照按钮
//        imagePicker.setMultiMode(false);
//        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
//        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
//        imagePicker.setSelectLimit(1);    //选中数量限制
//        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
//        imagePicker.setFocusWidth(cropWidth);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
//        imagePicker.setFocusHeight(cropHeight);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
//        imagePicker.setOutPutX(cropHeight);//保存文件的宽度。单位像素
//        imagePicker.setOutPutY(cropHeight);//保存文件的高度。单位像素
//        Intent intent = new Intent(context, ImageGridActivity.class);
//        context.startActivityForResult(intent, Common.IMAGE_PICKER);

        ISListConfig config = new ISListConfig.Builder()
                // 是否多选, 默认true
                .multiSelect(false)
                // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                .rememberSelected(false)
                // “确定”按钮背景色
                .btnBgColor(ContextCompat.getColor(context, R.color.orange_FC6C57))
                // “确定”按钮文字颜色
                .btnTextColor(ContextCompat.getColor(context, R.color.white))
                // 使用沉浸式状态栏
                .statusBarColor(ContextCompat.getColor(context, R.color.orange_FC6C57))
                // 返回图标ResId
                .backResId(R.mipmap.icon_white_back)
                // 标题
                .title("选择图片")
                // 标题文字颜色
                .titleColor(ContextCompat.getColor(context, R.color.white))
                // TitleBar背景色
                .titleBgColor(ContextCompat.getColor(context, R.color.orange_FC6C57))
                .needCrop(true)
                // 裁剪大小。needCrop为true的时候配置
                .cropSize(16, 9, cropWidth, cropHeight)
                // 第一个是否显示相机，默认true
                .needCamera(true)
                // 最大选择图片数量，默认9
                .maxNum(1)
                .build();
        // 跳转到图片选择器
        ISNav.getInstance().toListActivity(context, config, Common.IMAGE_PICKER);

    }

    /**
     * 聊天选择图片
     *
     * @param context
     */
    public static void selectChatImg(Activity context) {
        ISListConfig config = new ISListConfig.Builder()
                // 是否多选, 默认true
                .multiSelect(false)
                // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                .rememberSelected(false)
                // “确定”按钮背景色
                .btnBgColor(ContextCompat.getColor(context, R.color.orange_FC6C57))
                // “确定”按钮文字颜色
                .btnTextColor(ContextCompat.getColor(context, R.color.white))
                // 使用沉浸式状态栏
                .statusBarColor(ContextCompat.getColor(context, R.color.orange_FC6C57))
                // 返回图标ResId
                .backResId(R.mipmap.icon_white_back)
                // 标题
                .title("选择图片")
                // 标题文字颜色
                .titleColor(ContextCompat.getColor(context, R.color.white))
                // TitleBar背景色
                .titleBgColor(ContextCompat.getColor(context, R.color.orange_FC6C57))
                .needCrop(false)
                // 第一个是否显示相机，默认true
                .needCamera(true)
                // 最大选择图片数量，默认9
                .maxNum(1)
                .build();
        // 跳转到图片选择器
        ISNav.getInstance().toListActivity(context, config, Common.IMAGE_PICKER);
    }


    /**
     * 缓存清除
     *
     * @param activity
     */
    public static void deleteCacheDirFile(Context activity) {
        //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
        //PictureFileUtils.deleteCacheDirFile(activity);
    }


}
