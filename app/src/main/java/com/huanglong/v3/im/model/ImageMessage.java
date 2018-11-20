package com.huanglong.v3.im.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.imagepreview.ImagePreviewActivity;
import com.huanglong.v3.im.adapter.ChatAdapter;
import com.huanglong.v3.im.utils.FileUtil;
import com.huanglong.v3.utils.FileUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.tencent.TIMCallBack;
import com.tencent.TIMImage;
import com.tencent.TIMImageElem;
import com.tencent.TIMImageType;
import com.tencent.TIMMessage;

import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片消息数据
 */
public class ImageMessage extends Message {

    private static final String TAG = "ImageMessage";
    private boolean isDownloading;

    private String savePath = FileUtils.appPath + "/picture";

    public ImageMessage(TIMMessage message) {
        this.message = message;
    }

    public ImageMessage(String path) {
        this(path, false);
    }

    /**
     * 图片消息构造函数
     *
     * @param path  图片路径
     * @param isOri 是否原图发送
     */
    public ImageMessage(String path, boolean isOri) {
        message = new TIMMessage();
        TIMImageElem elem = new TIMImageElem();
        elem.setPath(path);
        elem.setLevel(isOri ? 0 : 1);
        message.addElement(elem);
    }


    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(final ChatAdapter.ViewHolder viewHolder, final Context context) {
        clearView(viewHolder);
        if (checkRevoke(viewHolder)) return;
        TIMImageElem e = (TIMImageElem) message.getElement(0);
        switch (message.status()) {
            case Sending:
                ImageView imageView = new ImageView(V3Application.getInstance());
                imageView.setImageBitmap(getThumb(e.getPath()));
                clearView(viewHolder);
                getBubbleView(viewHolder).addView(imageView);
                break;
            case SendSucc:
                for (final TIMImage image : e.getImageList()) {
                    if (image.getType() == TIMImageType.Thumb) {
                        final String uuid = image.getUuid();
                        if (FileUtil.isCacheFileExist(uuid)) {
                            showThumb(viewHolder, uuid);
                        } else {
                            image.getImage(FileUtil.getCacheFilePath(uuid), new TIMCallBack() {
                                @Override
                                public void onError(int code, String desc) {//获取图片失败
                                    //错误码code和错误描述desc，可用于定位请求失败原因
                                    //错误码code含义请参见错误码表
                                    LogUtil.e("getImage failed. code: " + code + " errmsg: " + desc);
                                }

                                @Override
                                public void onSuccess() {//成功，参数为图片数据
                                    showThumb(viewHolder, uuid);
                                }
                            });
                        }
                    }
                    if (image.getType() == TIMImageType.Original) {
                        showOriginal(viewHolder, image.getUrl());
//                        setImageEvent(viewHolder, uuid,context);
                        getBubbleView(viewHolder).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                navToImageview(image, context);
                            }
                        });
                    }
                    break;
                }
                showStatus(viewHolder);
        }
    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        String str = getRevokeSummary();
        if (str != null) return str;
        return V3Application.getInstance().getString(R.string.summary_image);
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {
        FileUtils.makeDirs2(savePath);
        final TIMImageElem e = (TIMImageElem) message.getElement(0);
        for (TIMImage image : e.getImageList()) {
            if (image.getType() == TIMImageType.Original) {
                final String uuid = image.getUuid();
                String imgPath = savePath + "/" + uuid + ".jpg";
                File file = new File(imgPath);
                if (file.exists()) {
                    ToastUtils.showToast(V3Application.getInstance().getString(R.string.save_exist));
                    return;
                }

                image.getImage(savePath + "/" + uuid + ".jpg", new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        LogUtil.e("getFile failed. code: " + i + " errmsg: " + s);
                    }

                    @Override
                    public void onSuccess() {
                        ToastUtils.showToast(V3Application.getInstance().getString(R.string.save_succ));
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(file);
                        intent.setData(uri);
                        V3Application.getInstance().sendBroadcast(intent);

                    }
                });
            }
        }
    }

    /**
     * 生成缩略图
     * 缩略图是将原图等比压缩，压缩后宽、高中较小的一个等于198像素
     * 详细信息参见文档
     */

    private Bitmap getThumb(String path) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int reqWidth, reqHeight, width = options.outWidth, height = options.outHeight;
        if (width > height) {
            reqWidth = 198;
            reqHeight = (reqWidth * height) / width;
        } else {
            reqHeight = 198;
            reqWidth = (width * reqHeight) / height;
        }
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        try {
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;
            Matrix mat = new Matrix();
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    mat.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    mat.postRotate(180);
                    break;
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 显示缩略图
     *
     * @param viewHolder
     * @param filename
     */
    private void showThumb(final ChatAdapter.ViewHolder viewHolder, String filename) {
        Bitmap bitmap = BitmapFactory.decodeFile(FileUtil.getCacheFilePath(filename));
        ImageView imageView = new ImageView(V3Application.getInstance());
        imageView.setImageBitmap(bitmap);
        getBubbleView(viewHolder).addView(imageView);
    }

    /**
     * 显示原图
     *
     * @param viewHolder
     * @param filename
     */
    private void showOriginal(final ChatAdapter.ViewHolder viewHolder, String filename) {
        ImageView imageView = new ImageView(V3Application.getInstance());
        Glide.with(V3Application.getInstance()).load(filename).override(500, 500).into(imageView);
        getBubbleView(viewHolder).addView(imageView);
    }


    private void setImageEvent(final ChatAdapter.ViewHolder viewHolder, final String fileName, final Context context) {
        getBubbleView(viewHolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(fileName)) {
                    List<String> imgs = new ArrayList<>();
                    imgs.add(fileName);
                    Intent intent = new Intent(context, ImagePreviewActivity.class);
                    intent.putStringArrayListExtra("imgs", (ArrayList<String>) imgs);
                    intent.putExtra("index", 0);
                    context.startActivity(intent);
                    imgs.clear();
                    imgs = null;
                }
            }
        });
    }

    private void navToImageview(final TIMImage image, final Context context) {
        String url = image.getUrl();
        if (!TextUtils.isEmpty(url)) {
            List<String> imgs = new ArrayList<>();
            imgs.add(url);
            Intent intent = new Intent(context, ImagePreviewActivity.class);
            intent.putStringArrayListExtra("imgs", (ArrayList<String>) imgs);
            intent.putExtra("index", 0);
            context.startActivity(intent);
            imgs.clear();
            imgs = null;
        }
//        if (FileUtil.isCacheFileExist(image.getUuid())) {
//            String path = FileUtil.getCacheFilePath(image.getUuid());
//            File file = new File(path);
//            if (file.length() < image.getSize()) {
//                ToastUtils.showToast(V3Application.getInstance().getString(R.string.downloading));
//                return;
//            }
//            Intent intent = new Intent(context, ImageViewActivity.class);
//            intent.putExtra("filename", image.getUuid());
//            context.startActivity(intent);
//        } else {
//            if (!isDownloading) {
//                isDownloading = true;
//                image.getImage(FileUtil.getCacheFilePath(image.getUuid()), new TIMCallBack() {
//                    @Override
//                    public void onError(int i, String s) {
//                        //错误码code和错误描述desc，可用于定位请求失败原因
//                        //错误码code含义请参见错误码表
//                        Log.e(TAG, "getImage failed. code: " + i + " errmsg: " + s);
//                        ToastUtils.showToast(V3Application.getInstance().getString(R.string.download_fail));
//                        isDownloading = false;
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        isDownloading = false;
//                        Intent intent = new Intent(context, ImageViewActivity.class);
//                        intent.putExtra("filename", image.getUuid());
//                        context.startActivity(intent);
//                    }
//                });
//            } else {
//                ToastUtils.showToast(V3Application.getInstance().getString(R.string.downloading));
//            }
//        }
    }

}
