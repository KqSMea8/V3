package com.huanglong.v3.activities.circle;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.imagepreview.ImagePreviewActivity;
import com.huanglong.v3.adapter.circle.RelCirImgAdapter;
import com.huanglong.v3.im.view.ListPickerDialog;
import com.huanglong.v3.model.WechatPayBean;
import com.huanglong.v3.model.circle.RelImageBean;
import com.huanglong.v3.model.homepage.LiveClassBean;
import com.huanglong.v3.model.mine.UserInfoBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.netutils.OssRequest;
import com.huanglong.v3.utils.Common;
import com.huanglong.v3.utils.ItemLongClickListener;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.KeyBoardUtils;
import com.huanglong.v3.utils.SelectPictureUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.utils.WXUtils;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/3/12.
 * 发布友圈
 */
@ContentView(R.layout.activity_release_circle)
public class ReleaseCircleActivity extends BaseFragmentActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.release_circle_pic_list)
    private RecyclerView pic_list;
    @ViewInject(R.id.release_circle_edt)
    private EditText edt_circle;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;
    @ViewInject(R.id.release_circle_type)
    private RadioGroup circle_type;
    @ViewInject(R.id.release_circle_class_lin)
    private LinearLayout class_lin;
    @ViewInject(R.id.release_circle_class)
    private TextView tv_class;
    @ViewInject(R.id.release_circle_class_line)
    private View class_line;


    private RelCirImgAdapter relCirImgAdapter;
    private List<String> selImage = new ArrayList<>();

    private int index = 0;//图片下标

    private String imgUrls = "";

    private int circleType = 1;////1- 友圈；2-社圈

    private List<LiveClassBean> liveClassBeans;
    private String class_id = "";

    private List<String> str_class = new ArrayList<>();
    private String circle;
    private int type = 1;//1.个人 2.企业
    private int power;//企业发布权限

    private WechatPayBean wechatPayBean;
    private WeChatBroadcastReceiver weChatBroadcastReceiver;

    private OssRequest ossRequest;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("发布友圈");
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
        circle_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.release_circle_type_one) {
                    circleType = 1;
                    class_lin.setVisibility(View.GONE);
                    class_line.setVisibility(View.GONE);
                } else {
                    circleType = 2;
                    class_lin.setVisibility(View.VISIBLE);
                    class_line.setVisibility(View.VISIBLE);
                }
            }
        });


        relCirImgAdapter.setItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                String imgUrl = (String) obj;
                if (type == 1) {
                    if (TextUtils.equals("add", imgUrl)) {
                        SelectPictureUtils.selectPicture(ReleaseCircleActivity.this, true, 10 - selImage.size(), 0);
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


        requestClass();
        requestPersonal();
        weChatBroadcastReceiver = new WeChatBroadcastReceiver();
        registerReceiver();

    }

    @Event(value = {R.id.title_back, R.id.title_tv_right, R.id.release_circle_class_lin})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                ReleaseCircleActivity.this.finish();
                break;
            case R.id.title_tv_right:
                KeyBoardUtils.hideKeyboard(this);
                circle = edt_circle.getText().toString().trim();
                if (selImage.size() < 2 && TextUtils.isEmpty(circle)) {
                    ToastUtils.showToast("请选择发布图片或输入发布内容");
                    return;
                }
                if (circleType == 2) {
                    String str_class = tv_class.getText().toString().trim();
                    if (TextUtils.isEmpty(str_class)) {
                        ToastUtils.showToast("请选择分类");
                        return;
                    }
                    for (LiveClassBean liveClassBean : liveClassBeans) {
                        if (TextUtils.equals(str_class, liveClassBean.getName())) {
                            class_id = liveClassBean.getId();
                        }
                    }
                }
                if (type == 1) {
                    publishCircle();
                } else {
                    requestPower();
                }

                break;
            case R.id.release_circle_class_lin:
                if (str_class != null && str_class.size() > 0) {
                    String[] str_jobs = str_class.toArray(new String[str_class.size()]);
                    new ListPickerDialog().show(str_jobs, getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tv_class.setText(str_jobs[which]);
                        }
                    });
                }
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
//                            requestReleaseCircle();
//                        } else {
//                            requestUploadImage(selImage.get(index));
//                        }
//                    } else {
//                        if (index == selImage.size() - 1) {
//                            requestReleaseCircle();
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
     * 发布社圈
     */
    private void publishCircle() {
        if (selImage.size() >= 2) {
            showDialog();
            ossRequest.uploadFile(selImage.get(index));
//            requestUploadImage(selImage.get(index));
        } else {
            requestReleaseCircle();
        }
    }

    /**
     * 发布动态
     */
    private void requestReleaseCircle() {
        if (!TextUtils.isEmpty(imgUrls)) {
            imgUrls = imgUrls.substring(1, imgUrls.length());
        }
        RequestParams params = MRequestParams.getUidParams(Api.add_quan);
        params.addBodyParameter("content", circle);
        params.addBodyParameter("imgs", imgUrls);
        params.addBodyParameter("type", circleType + "");
        if (circleType == 2) {
            params.addBodyParameter("category_id", class_id);
        }
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("发布成功");
                    setResult(RESULT_OK);
                    ReleaseCircleActivity.this.finish();
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
                index = 0;
                imgUrls = "";
            }
        });
    }


    /**
     * 请求社圈类别
     */
    private void requestClass() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.Quan_cate_list);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    liveClassBeans = gson.fromJson(json, new TypeToken<LinkedList<LiveClassBean>>() {
                    }.getType());
                    if (liveClassBeans != null && liveClassBeans.size() > 0) {
                        str_class.clear();
                        for (LiveClassBean liveClassBean : liveClassBeans) {
                            str_class.add(liveClassBean.getName());
                        }
                    }
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

            }
        });
    }


    /**
     * 请求个人资料
     */
    private void requestPersonal() {

        RequestParams params = MRequestParams.getNoTokenParams(Api.getUserInfo);
        params.addBodyParameter("uid", UserInfoUtils.getUid());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    UserInfoBean personalBean = gson.fromJson(json, UserInfoBean.class);
                    if (personalBean != null) {
                        type = personalBean.getType();
                    }
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

            }
        });
    }

    /**
     * 请求企业发布权限
     */
    private void requestPower() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_limit_send);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("type", circleType + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    power = Integer.parseInt(json);
                    if (power > 0) {
                        publishCircle();
                    } else {
                        requestPay();
                    }
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

            }
        });
    }

    /**
     * 请求支付接口
     */
    private void requestPay() {
        showDialog();
        RequestParams params = MRequestParams.getNoTokenParams(Api.pay);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("pay_type", "1");
        params.addBodyParameter("pay_amount", "0.01");
        params.addBodyParameter("type", circleType == 1 ? "11" : "12");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    wechatPayBean = gson.fromJson(json, WechatPayBean.class);
                    if (wechatPayBean != null) {
                        WXUtils.wxPay(ReleaseCircleActivity.this, wechatPayBean);
                    }
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
     * 注册微信分享广播
     */

    private void registerReceiver() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.PAY_WECHAT_ACTION);
        ReleaseCircleActivity.this.registerReceiver(weChatBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReleaseCircleActivity.this.unregisterReceiver(weChatBroadcastReceiver);
        if (ossRequest != null) {
            ossRequest.cancelTask();
        }
    }

    /**
     * 微信支付成功后接收广播处理
     *
     * @author hbb
     */
    private class WeChatBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Common.PAY_WECHAT_ACTION)) return;
            String type = intent.getStringExtra(Common.PAY_WECHAT_KEY);
            if (TextUtils.equals("success", type)) {
//                ToastUtils.showToast("支付成功");
                publishCircle();
            } else if (TextUtils.equals("cancel", type)) {
                ToastUtils.showToast("取消支付");
            } else {
                ToastUtils.showToast("支付失败");
            }
        }
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
                        requestReleaseCircle();
                    } else {
                        ossRequest.uploadFile(selImage.get(index));
                    }
                } else {
                    if (index == selImage.size() - 1) {
                        requestReleaseCircle();
                    } else {
                        ossRequest.uploadFile(selImage.get(index));
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


}
