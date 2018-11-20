package com.huanglong.v3.activities.message;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.activities.homepage.PersonalPageActivity;
import com.huanglong.v3.adapter.message.MapActivity;
import com.huanglong.v3.model.home.YellowBean;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by bin on 2018/3/18.
 * 黄页详情页面
 */
@ContentView(R.layout.activity_yellow_details)
public class YelPagDetActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.yellow_company_name)
    private TextView tv_company_name;
    @ViewInject(R.id.yellow_company_main_scope)
    private TextView tv_main_scope;
    @ViewInject(R.id.yellow_company_contacts)
    private TextView tv_contacts;
    @ViewInject(R.id.yellow_company_mobile)
    private TextView tv_mobile;
    @ViewInject(R.id.yellow_company_address)
    private TextView tv_address;
    @ViewInject(R.id.yellow_company_avatar)
    private ImageView img_avatar;


    private String uid;
    private String mobile;
    private String latitude;
    private String longitude;
    private String address;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("详情");


    }

    @Override
    protected void logic() {
        Intent intent = getIntent();
        YellowBean yellowBean = (YellowBean) intent.getSerializableExtra("yellowBean");
        showDetails(yellowBean);
    }


    @Event(value = {R.id.title_back, R.id.yellow_company_info_lin, R.id.yellow_company_mobile_lin, R.id.yellow_company_address_lin})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                YelPagDetActivity.this.finish();
                break;
            case R.id.yellow_company_info_lin:
                Intent intent = new Intent();
                intent.setClass(this, PersonalPageActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
                break;
            case R.id.yellow_company_mobile_lin:
                Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobile));
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                break;
            case R.id.yellow_company_address_lin:
                Intent intent1 = new Intent();
                intent1.setClass(this, MapActivity.class);
                intent1.putExtra("latitude", latitude);
                intent1.putExtra("longitude", longitude);
                intent1.putExtra("address", address);
                startActivity(intent1);
                break;
        }
    }

//    /**
//     * 请求黄页详情
//     */
//    private void requestYellowLDetails() {
//        RequestParams params = MRequestParams.getNoTokenParams(Api.huangye_detail);
//        params.addBodyParameter("id", id);
//
//        x.http().get(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                String json = JsonHandleUtils.JsonHandle(result);
//                if (!TextUtils.isEmpty(json)) {
//                    Gson gson = V3Application.getGson();
//                    YellowBean yellowBean = gson.fromJson(json, YellowBean.class);
//
//                    showDetails(yellowBean);
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
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
//
//    }

    /**
     * 显示黄页详情
     *
     * @param yellowBean
     */
    private void showDetails(YellowBean yellowBean) {
        mobile = yellowBean.getUsername();
        uid = yellowBean.getId();
        latitude = yellowBean.getLatitude();
        longitude = yellowBean.getLongitude();
        address = yellowBean.getAddress();
        tv_company_name.setText(yellowBean.getShort_name());
        tv_main_scope.setText(yellowBean.getMain_scope());
        tv_contacts.setText(yellowBean.getNickname());
        tv_mobile.setText(yellowBean.getUsername());
        tv_address.setText(yellowBean.getAddress());
        x.image().bind(img_avatar, yellowBean.getHead_image(), MImageOptions.getNormalImageOptions());

    }

}
