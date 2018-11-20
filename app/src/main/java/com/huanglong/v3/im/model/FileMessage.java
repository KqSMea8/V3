package com.huanglong.v3.im.model;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.im.adapter.ChatAdapter;
import com.huanglong.v3.utils.FileUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.TransformationUtils;
import com.huanglong.v3.voice.utils.PxUtil;
import com.tencent.TIMCallBack;
import com.tencent.TIMFileElem;
import com.tencent.TIMMessage;

import java.io.File;

/**
 * 文件消息
 */
public class FileMessage extends Message {


    private String savePath = FileUtils.appPath + "/file";

    public FileMessage(TIMMessage message) {
        this.message = message;
    }

    public FileMessage(String filePath) {
        message = new TIMMessage();
        TIMFileElem elem = new TIMFileElem();
        elem.setPath(filePath);
        elem.setFileName(filePath.substring(filePath.lastIndexOf("/") + 1));
        message.addElement(elem);
    }


    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        clearView(viewHolder);
        if (checkRevoke(viewHolder)) return;
        TIMFileElem e = (TIMFileElem) message.getElement(0);
        LinearLayout linearLayout = new LinearLayout(V3Application.getInstance());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout linearLayout2 = new LinearLayout(V3Application.getInstance());
        linearLayout2.setOrientation(LinearLayout.VERTICAL);

        TextView tvFileName = new TextView(V3Application.getInstance());
        tvFileName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tvFileName.setTextColor(V3Application.getInstance().getResources().getColor(isSelf() ? R.color.white : R.color.black));
        tvFileName.setText(e.getFileName());

        TextView tvFileSize = new TextView(V3Application.getInstance());
        tvFileSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvFileSize.setTextColor(V3Application.getInstance().getResources().getColor(isSelf() ? R.color.white : R.color.gray_99));
        double fileSize = e.getFileSize();
        double l = fileSize / 1000 / 1000;
        tvFileSize.setText(TransformationUtils.doubleDecimal(l) + "MB");

        linearLayout2.addView(tvFileName);
        linearLayout2.addView(tvFileSize);

        linearLayout.addView(linearLayout2);

        ImageView imageView = new ImageView(V3Application.getInstance());
        imageView.setBackgroundResource(R.mipmap.icon_chat_file);
        linearLayout.addView(imageView);

        getBubbleView(viewHolder).addView(linearLayout);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        lp.width = PxUtil.dip2px(V3Application.getInstance(), 200);

        LinearLayout.LayoutParams lpIv = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        lpIv.width = PxUtil.dip2px(V3Application.getInstance(), 40);
        lpIv.height = PxUtil.dip2px(V3Application.getInstance(), 40);
        imageView.setLayoutParams(lpIv);

        LinearLayout.LayoutParams lpTv = (LinearLayout.LayoutParams) linearLayout2.getLayoutParams();
        lpTv.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lpTv.weight = 1;
        linearLayout2.setLayoutParams(lpTv);

        showStatus(viewHolder);
    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        String str = getRevokeSummary();
        if (str != null) return str;
        return V3Application.getInstance().getString(R.string.summary_file);
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {
        if (message == null) return;
        FileUtils.makeDirs2(savePath);
        final TIMFileElem e = (TIMFileElem) message.getElement(0);
        String[] str = e.getFileName().split("/");
        String filename = str[str.length - 1];
        String filePath = savePath + "/" + filename;
        File file = new File(filePath);
        if (file.exists()) {
            ToastUtils.showToast(V3Application.getInstance().getString(R.string.save_exist));
            return;
        }
//        if (FileUtil.isFileExist(filename, Environment.DIRECTORY_DOWNLOADS)) {
//            ToastUtils.showToast(V3Application.getInstance().getString(R.string.save_exist));
//            return;
//        }
        e.getToFile(savePath + "/" + filename, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "getFile failed. code: " + i + " errmsg: " + s);
            }

            @Override
            public void onSuccess() {
                ToastUtils.showToast("保存成功;" + savePath + "/" + filename);
            }
        });

    }
}
