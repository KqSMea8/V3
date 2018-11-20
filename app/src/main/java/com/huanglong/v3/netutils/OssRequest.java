package com.huanglong.v3.netutils;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.huanglong.v3.utils.DateUtils;

import org.xutils.common.util.LogUtil;

import java.util.UUID;

/**
 * Created by bin on 2018/5/21.
 * ali Oss请求工具类
 */

public class OssRequest {

    private Context context;

    private OSSClient oss;
    private CallBack callBack;
    private OSSAsyncTask task;


    public OssRequest(Context context) {
        this.context = context;
        initOss();
    }

    /**
     * 初始化Oss
     */
    private void initOss() {
        String endpoint = "http://oss-cn-beijing.aliyuncs.com";
        // 推荐使用OSSAuthCredentialsProvider，token过期后会自动刷新。
        String stsServer = "https://www.huachenedu.cn/sts-server/sts.php";
        OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(stsServer);
        //config
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        oss = new OSSClient(context, endpoint, credentialProvider, conf);
    }

    /**
     * 上传文件请求
     *
     * @param filePath
     */
    public void uploadFile(String filePath) {
        String suffix = filePath.substring(filePath.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;
        String date = DateUtils.getCurrentDateNoPoint();
        String objectKey = date + "/" + fileName;
// 构造上传请求
        PutObjectRequest put = new PutObjectRequest("weisan", objectKey, filePath);
// 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                LogUtil.d("currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });
        task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                LogUtil.d("PutObject" + "UploadSuccess");
                if (callBack != null) {
                    callBack.onSuccess(request, result, objectKey);
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
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

                if (callBack != null) {
                    callBack.onFailure(request, clientExcepion, serviceException);
                }

            }
        });
    }


    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }


    public interface CallBack {
        void onSuccess(PutObjectRequest request, PutObjectResult result, String objectKey);

        void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException);
    }

    /**
     * 取消请求
     */
    public void cancelTask() {
        if (task != null) {
            task.cancel();
        }
    }


}
