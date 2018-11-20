package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.google.gson.Gson;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.imagepreview.ImagePreviewActivity;
import com.huanglong.v3.adapter.circle.RelCirImgAdapter;
import com.huanglong.v3.model.circle.RelImageBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.netutils.OssRequest;
import com.huanglong.v3.utils.Common;
import com.huanglong.v3.utils.ItemLongClickListener;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.SelectPictureUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bin on 2018/4/21.
 * 发布主题
 */
@ContentView(R.layout.activity_theme)
public class ThemeActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.theme_class_pic_list)
    private RecyclerView pic_list;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;
    @ViewInject(R.id.theme_class)
    private TextView tv_class;

    private RelCirImgAdapter relCirImgAdapter;
    private List<String> selImage = new ArrayList<>();


    private String class_id;
    private int index = 0;//图片下标
    private String imgUrls = "";

    private OssRequest ossRequest;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("发布主题");
        tv_right.setText("发布");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        pic_list.setLayoutManager(gridLayoutManager);

        relCirImgAdapter = new RelCirImgAdapter();
        pic_list.setAdapter(relCirImgAdapter);
    }

    @Override
    protected void logic() {
        initOSS();

        selImage.add("add");
        relCirImgAdapter.setData(selImage);

        relCirImgAdapter.setItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                String imgUrl = (String) obj;
                if (type == 1) {
                    if (TextUtils.equals("add", imgUrl)) {
                        SelectPictureUtils.selectPicture(ThemeActivity.this, true, 10 - selImage.size(), 0);
                    } else {
                        List<String> imgs = new ArrayList<>();
                        for (String url : selImage) {
                            if (!TextUtils.equals("add", url)) {
                                imgs.add(url);
                            }
                        }
                        Intent intent = new Intent();
                        intent.setClass(V3Application.getInstance(), ImagePreviewActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putStringArrayListExtra("imgs", (ArrayList<String>) imgs);
                        intent.putExtra("index", 0);
                        intent.putExtra("isDownLoad", false);
                        V3Application.getInstance().startActivity(intent);
                        imgs.clear();
                        imgs = null;
                    }
                } else {
                    if (selImage.contains(imgUrl)) {
                        selImage.remove(imgUrl);
                        if (selImage.size() < 9) {
                            if (!selImage.contains("add")) {
                                selImage.add("add");
                            }
                        }
                        relCirImgAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onItemViewClick(Object obj, int position, int type, View view) {

            }

        });


        relCirImgAdapter.setItemLongClickListener(new ItemLongClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                relCirImgAdapter.isDelete = true;
                relCirImgAdapter.notifyDataSetChanged();
            }
        });

    }

    @Event(value = {R.id.title_back, R.id.title_tv_right, R.id.theme_sel_class})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_tv_right:
                if (TextUtils.isEmpty(class_id)) {
                    ToastUtils.showToast("请选择分类");
                    return;
                }
                if (selImage.size() >= 2) {
                    showDialog();
                    ossRequest.uploadFile(selImage.get(index));
//                    requestUploadImage(selImage.get(index));
                } else {
                    ToastUtils.showToast("请选择图片");
                }
                break;
            case R.id.theme_sel_class:
                Intent intent = new Intent();
                intent.setClass(this, ThemeClassActivity.class);
                startActivityForResult(intent, 1001);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Common.IMAGE_PICKER://PictureConfig.CHOOSE_REQUEST:
                    if (data != null) {
                        List<String> pathList = data.getStringArrayListExtra("result");
                        if (pathList != null) {
                            selImage.addAll(selImage.size() - 1, pathList);
                            if (selImage.size() > 9) {
                                if (selImage.contains("add")) {
                                    selImage.remove("add");
                                }
                            }
                            relCirImgAdapter.setData(selImage);
                        }
                    }
                    break;
                case 1001:
                    if (data == null) return;
                    class_id = data.getStringExtra("class_id");
                    String class_name = data.getStringExtra("class_name");
                    tv_class.setText(class_name);
                    break;
            }
        }
    }

    /**
     * 上传图片
     *
     * @param imgPath
     */
//    private void requestUploadImage(String imgPath) {
//        showDialog();
//        RequestParams params = MRequestParams.getNoTokenParams(Api.uploadimg);
//        params.setMultipart(true);
//        params.addBodyParameter("imglist", new File(imgPath));
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                String json = JsonHandleUtils.JsonHandle(result);
//                if (!TextUtils.isEmpty(json)) {
//                    Gson gson = V3Application.getGson();
//                    RelImageBean relImageBean = gson.fromJson(json, RelImageBean.class);
//                    if (relImageBean != null) {
//                        imgUrls = imgUrls + "," + relImageBean.getUrl();
//                    }
//                    index++;
//                    if (!selImage.contains("add")) {
//                        if (index == selImage.size()) {
//                            requestReleaseTheme();
//                        } else {
//                            requestUploadImage(selImage.get(index));
//                        }
//                    } else {
//                        if (index == selImage.size() - 1) {
//                            requestReleaseTheme();
//                        } else {
//                            requestUploadImage(selImage.get(index));
//                        }
//                    }
//
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                index = 0;
//                imgUrls = "";
//                dismissDialog();
//                JsonHandleUtils.netError(ex);
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
//    }

    /**
     * 发布主题
     */
    private void requestReleaseTheme() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_add_subject_pic);
        params.addBodyParameter("sid", class_id);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("imgurl", imgUrls.substring(1, imgUrls.length()));

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("发布成功");
                    class_id = "";
                    tv_class.setText("");
                    selImage.clear();
                    selImage.add("add");
                    relCirImgAdapter.notifyDataSetChanged();
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
                dismissDialog();
            }
        });

    }

    /**
     * 初始化OSS
     */
    private void initOSS() {
//

        ossRequest = new OssRequest(this.getApplicationContext());

        ossRequest.setCallBack(new OssRequest.CallBack() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result, String objectKey) {
                LogUtil.d("PutObject" + "UploadSuccess");
//                soundUrl = "http://weisan.oss-cn-beijing.aliyuncs.com/" + objectKey;
//                dismissDialog();
//                requestSubmitInfo();
                imgUrls = imgUrls + "," + "http://weisan.oss-cn-beijing.aliyuncs.com/" + objectKey;
                index++;
                if (!selImage.contains("add")) {
                    if (index == selImage.size()) {
                        requestReleaseTheme();
                    } else {
                        ossRequest.uploadFile(selImage.get(index));
//                        requestUploadImage(selImage.get(index));
                    }
                } else {
                    if (index == selImage.size() - 1) {
                        requestReleaseTheme();
                    } else {
                        ossRequest.uploadFile(selImage.get(index));
//                        requestUploadImage(selImage.get(index));
                    }
                }

            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                dismissDialog();
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    LogUtil.e("ErrorCode:" + serviceException.getErrorCode());
                    LogUtil.e("RequestId:" + serviceException.getRequestId());
                    LogUtil.e("HostId:" + serviceException.getHostId());
                    LogUtil.e("RawMessage:" + serviceException.getRawMessage());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ossRequest != null) {
            ossRequest.cancelTask();
        }
    }
}
