package com.huanglong.v3.activities.imagepreview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.homepage.PersonalPageActivity;
import com.huanglong.v3.utils.FileUtils;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.PromptDialog;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.view.LoadingNumberDialog;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * Created by bin on 2018/4/13.
 * 图片预览
 */
@ContentView(R.layout.activity_img_preview)
public class ImagePreviewActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.img_preview_viewpager)
    private ViewPager preview_viewpager;
    @ViewInject(R.id.orange_title_bar_lin)
    private LinearLayout title_bar_lin;
    @ViewInject(R.id.img_preview_down)
    private ImageView img_download;
    @ViewInject(R.id.img_preview_del)
    private ImageView img_delete;


    private TextView tv_dialog_hint;

    private List<String> imgs;
    private int index;
    private boolean isDownLoad = true;
    private boolean isDelete = false;

    private ImagePreviewAdapter imagePreviewAdapter;

    private String savePath = FileUtils.appPath + "/picture";

    private LoadingNumberDialog loadingNumberDialog;

    private PromptDialog promptDialog;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        title_bar_lin.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        title_bar_lin.setVisibility(View.GONE);

        loadingNumberDialog = new LoadingNumberDialog(this);

        imagePreviewAdapter = new ImagePreviewAdapter(this);
        preview_viewpager.setAdapter(imagePreviewAdapter);

        initDialog();
    }


    @Override
    protected void logic() {
        Intent intent = getIntent();
        imgs = intent.getStringArrayListExtra("imgs");
        index = intent.getIntExtra("index", 0);
        if (imgs == null || imgs.size() == 0) {
            ImagePreviewActivity.this.finish();
        }
        tv_title.setText((index + 1) + "/" + imgs.size());
        imagePreviewAdapter.setData(imgs);
        preview_viewpager.setCurrentItem(index);

        isDownLoad = intent.getBooleanExtra("isDownLoad", true);
        if (isDownLoad) {
            img_download.setVisibility(View.VISIBLE);
        } else {
            img_download.setVisibility(View.GONE);
        }

        isDelete = intent.getBooleanExtra("isDelete", false);
        if (isDelete) {
            img_delete.setVisibility(View.VISIBLE);
        } else {
            img_delete.setVisibility(View.GONE);
        }


        preview_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_title.setText((position + 1) + "/" + imgs.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        imagePreviewAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                ImagePreviewActivity.this.finish();
            }
        });

    }

    @Event(value = {R.id.title_back, R.id.img_preview_down, R.id.img_preview_del})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                ImagePreviewActivity.this.finish();
                break;
            case R.id.img_preview_down:
                FileUtils.makeDirs2(savePath);
                String imgUrl = imgs.get(preview_viewpager.getCurrentItem());
                downloadPicture(imgUrl);
                break;
            case R.id.img_preview_del:
                tv_dialog_hint.setText("收删除该主图图片");
                promptDialog.show();
                break;
        }
    }

    /**
     * 初始化设置dialog
     */
    private void initDialog() {
        promptDialog = new PromptDialog(getActivity(), R.layout.dialog_hint_currency);
        tv_dialog_hint = (TextView) promptDialog.getView(R.id.dialog_comment_content);
        promptDialog.getView(R.id.dialog_comment_cancel).setOnClickListener(dialogClick);
        promptDialog.getView(R.id.dialog_comment_confirm).setOnClickListener(dialogClick);
        promptDialog.getView(R.id.dialog_comment_lin).setOnClickListener(dialogClick);
    }

    /**
     *
     */
    private View.OnClickListener dialogClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.dialog_comment_cancel:
                    promptDialog.dismiss();
                    break;
                case R.id.dialog_comment_confirm:
                    promptDialog.dismiss();
                    if (PersonalPageActivity.instance != null) {
                        PersonalPageActivity.instance.delThemePic(preview_viewpager.getCurrentItem(), new delImgCallBack() {
                            @Override
                            public void Success() {
                                int currentItem = preview_viewpager.getCurrentItem();
                                imgs.remove(preview_viewpager.getCurrentItem());
                                if (imgs.size() == 0) {
                                    finish();
                                }
                                imagePreviewAdapter.setData(imgs);
                                if (currentItem > 0) {
                                    preview_viewpager.setCurrentItem(currentItem - 1);
                                }
                            }
                        });
                    }
                    break;
                case R.id.dialog_comment_lin:
                    promptDialog.dismiss();
                    break;
            }
        }
    };


    /**
     * 下载图片
     */
    private void downloadPicture(String imgUrl) {
        String[] str = imgUrl.split("/");
        String filename = str[str.length - 1];
        String filePath = savePath + "/" + filename + ".jpg";
        File file = new File(filePath);
        if (file.exists()) {
            ToastUtils.showToast(V3Application.getInstance().getString(R.string.save_exist));
            return;
        }

        RequestParams params = new RequestParams(imgUrl);
        params.setSaveFilePath(filePath);
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                loadingNumberDialog.showDialog();
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                long l = (current / total) * 100;
                loadingNumberDialog.setProgress(Integer.valueOf(String.valueOf(l)));
            }

            @Override
            public void onSuccess(File result) {
                ToastUtils.showToast("下载成功");
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                ImagePreviewActivity.this.sendBroadcast(intent);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                loadingNumberDialog.dismissDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingNumberDialog != null && loadingNumberDialog.isShowing()) {
            loadingNumberDialog.dismissDialog();
        }
    }

    public interface delImgCallBack {
        void Success();
    }


}
