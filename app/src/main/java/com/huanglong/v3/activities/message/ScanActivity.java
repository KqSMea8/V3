package com.huanglong.v3.activities.message;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.utils.ToastUtils;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;


/**
 * Created by bin on 2017/10/11.
 * 扫描二维码
 */
@ContentView(R.layout.activity_scan_layout)
public class ScanActivity extends BaseActivity implements QRCodeView.Delegate {
    private static final String TAG = ScanActivity.class.getSimpleName();

    @ViewInject(R.id.zxingview)
    private ZXingView mQRCodeView;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void logic() {

    }


    @Event(value = {R.id.bar_back_scan})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.bar_back_scan:
                finish();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mQRCodeView.showScanRect();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        mQRCodeView.stopSpot();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        LogUtil.i("scan result:" + result);
//        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        mQRCodeView.startSpot();
        if (!TextUtils.isEmpty(result)) {
            if (result.contains("http")) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(result);
                intent.setData(content_url);
                startActivity(intent);
                ScanActivity.this.finish();
                return;
            } else {
                ToastUtils.showToast(result);
                Intent intent = new Intent();
                intent.putExtra("result", result);
                setResult(RESULT_OK, intent);
                ScanActivity.this.finish();
                return;
            }
        }
        ToastUtils.showToast("二维码扫描错误");
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        LogUtil.e("打开相机出错");
    }


//        @Override
//        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
//            super.onActivityResult(requestCode, resultCode, data);
//
//            mQRCodeView.showScanRect();
//
//            if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
//                final String picturePath = BGAPhotoPickerActivity.getSelectedImages(data).get(0);
//
//            /*
//            这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
//            请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github.com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
//             */
//                new AsyncTask<Void, Void, String>() {
//                    @Override
//                    protected String doInBackground(Void... params) {
//                        return QRCodeDecoder.syncDecodeQRCode(picturePath);
//                    }
//
//                    @Override
//                    protected void onPostExecute(String result) {
//                        if (TextUtils.isEmpty(result)) {
//                            Toast.makeText(ScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(ScanActivity.this, result, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }.execute();

}

