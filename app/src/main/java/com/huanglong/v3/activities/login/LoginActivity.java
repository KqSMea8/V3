package com.huanglong.v3.activities.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.R;
import com.huanglong.v3.activities.main.MainActivity;
import com.huanglong.v3.im.event.FriendshipEvent;
import com.huanglong.v3.im.event.GroupEvent;
import com.huanglong.v3.im.event.MessageEvent;
import com.huanglong.v3.im.event.RefreshEvent;
import com.huanglong.v3.im.utils.TCLoginMgr;
import com.huanglong.v3.live.userinfo.ITCUserInfoMgrListener;
import com.huanglong.v3.live.userinfo.TCUserInfoMgr;
import com.huanglong.v3.model.login.LoginBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.LocationAMapUtils;
import com.huanglong.v3.utils.PreferencesUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.view.LoadingDialog;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


/**
 * Created by bin on 2018/1/4.
 * 登录页面
 */

@ContentView(R.layout.activity_login_layout)
public class LoginActivity extends Activity implements TCLoginMgr.TCLoginCallback {


    @ViewInject(R.id.login_edt_account)
    private EditText edt_account;
    @ViewInject(R.id.login_edt_password)
    private EditText edt_password;
    @ViewInject(R.id.login_edt_password_display)
    private ImageView img_display;
    @ViewInject(R.id.title_back)
    private LinearLayout back;
    @ViewInject(R.id.title_name)
    private TextView title_name;
    @ViewInject(R.id.login_type)
    private RadioGroup login_type;


    private ImmersionBar mImmersionBar;
    private LoadingDialog loadingDialog;

    private String identifier;


    private boolean isChecked = false;

    private TCLoginMgr mTCLoginMgr;


    private LocationAMapUtils locationAMapUtils;

    private String latitude;
    private String longitude;

    private int type = 1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initView();
        logic();
    }


    /**
     * 初始化设置控件
     */
    private void initView() {
        title_name.setText(R.string.login);
        back.setVisibility(View.GONE);

        mImmersionBar = ImmersionBar.with(this);
//        mImmersionBar;   //所有子类都将继承这些相同的属性
        mImmersionBar.statusBarDarkFont(true, 0.5f)
                .keyboardEnable(true)
                .init();

        mTCLoginMgr = TCLoginMgr.getInstance();
        mTCLoginMgr.setTCLoginCallback(this);
    }

    /**
     * 逻辑处理
     */
    protected void logic() {
        loadingDialog = new LoadingDialog(this);
        initImUserConfig();
        edt_account.setText(PreferencesUtils.getString(V3Application.getInstance(), "account"));
        edt_password.setText(PreferencesUtils.getString(V3Application.getInstance(), "password"));

        locationAMapUtils = new LocationAMapUtils(this);
        locationAMapUtils.startLocation();
        locationAMapUtils.setLocationClient(new LocationAMapUtils.LocationClientOption() {
            @Override
            public void onLocationSuccess(AMapLocation location) {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
            }

            @Override
            public void onLocationFail(int errorCode, String errorMsg) {
                ToastUtils.showToast("位置获取失败");
            }
        });

        login_type.check(R.id.login_type_one);

        login_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.login_type_one) {
                    type = 1;
                } else {
                    type = 2;
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        //设置登录回调,resume设置回调避免被registerActivity冲掉
        mTCLoginMgr.setTCLoginCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //删除登录回调
        mTCLoginMgr.removeTCLoginCallback();
    }

    @Event(value = {R.id.login_btn, R.id.login_edt_password_display, R.id.login_register, R.id.login_forget_password})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                String nickName = edt_account.getText().toString().trim();
                String password = edt_password.getText().toString().trim();
                if (!TextUtils.isEmpty(nickName)) {
                    requestLogin(nickName, password);
                } else {
                    ToastUtils.showToast("请输入账号");
                }
                break;
            case R.id.login_edt_password_display:
                if (!isChecked) {//选择状态 显示明文--设置为可见的密码
                    isChecked = true;
                    edt_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    img_display.setImageResource(R.mipmap.icon_display);
                } else {//默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    isChecked = false;
                    edt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    img_display.setImageResource(R.mipmap.icon_hides);
                }
                break;
            case R.id.login_register:
                Intent intent1 = new Intent();
                intent1.setClass(this, RegisterActivity.class);
                startActivityForResult(intent1, 1000);
                break;
            case R.id.login_forget_password:
                Intent intent2 = new Intent();
                intent2.setClass(this, EditPasswordActivity.class);
                intent2.putExtra("flag", 2);
                startActivity(intent2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    if (data != null) {
                        String mobile = data.getStringExtra("mobile");
                        edt_account.setText(mobile);
                        edt_password.setText("");
                    }
                    break;
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null)
            mImmersionBar.destroy();  //必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
    }

    /**
     * 腾讯云初始化用户关系
     */
    private void initImUserConfig() {
//        TIMUserConfig userConfig = new TIMUserConfig();
//        userConfig.setUserStatusListener(new TIMUserStatusListener() {
//            @Override
//            public void onForceOffline() {
//                //被踢下线时回调
//
//            }
//
//            @Override
//            public void onUserSigExpired() {
//                //票据过期时回调
//            }
//        })
//                //设置连接状态事件监听器
//                .setConnectionListener(new TIMConnListener() {
//                    @Override
//                    public void onConnected() {
//
//                    }
//
//                    @Override
//                    public void onDisconnected(int i, String s) {
//
//                    }
//
//                    @Override
//                    public void onWifiNeedAuth(String s) {
//
//                    }
//                })
//                //设置群组事件监听器
//                .setGroupEventListener(new TIMGroupEventListener() {
//                    @Override
//                    public void onGroupTipsEvent(TIMGroupTipsElem elem) {
//                        LogUtil.i("onGroupTipsEvent, type: " + elem.getTipsType());
//                    }
//                });

//        RefreshEvent.getInstance().init();
//        GroupEvent.getInstance().init(userConfig);
//        userConfig = FriendshipEvent.getInstance().init(userConfig);
//        TIMManager.getInstance().setUserConfig(userConfig);
        RefreshEvent.getInstance();
        FriendshipEvent.getInstance().init();
        GroupEvent.getInstance().init();
    }

    /**
     * 请求登录接口
     *
     * @param account
     * @param password
     */
    private void requestLogin(final String account, final String password) {
        showDialog();
        RequestParams params = MRequestParams.getNoTokenParams(Api.login);
        params.addBodyParameter("username", account);
        params.addBodyParameter("password", password);
        params.addBodyParameter("type", type + "");


        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String str = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(str)) {
                    Gson gson = new Gson();
                    LoginBean loginBean = gson.fromJson(str, LoginBean.class);
                    UserInfoUtils.saveUserInfo(loginBean);
                    UserInfoUtils.setAutoLogin(true);
                    PreferencesUtils.putString(V3Application.getInstance(), "account", account);
                    PreferencesUtils.putString(V3Application.getInstance(), "password", password);
                    updataLocation(loginBean.getUid());
                    String sig = loginBean.getSig();
                    if (!TextUtils.isEmpty(sig)) {
                        imLogin(loginBean.getUid(), sig);
                    }

                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("error:" + ex.toString());
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
     * 腾讯云登录
     */
    private void imLogin(String identifier, String userSig) {
        this.identifier = identifier;

        mTCLoginMgr.imLogin(identifier, userSig);

//        TIMUser user = new TIMUser();
//        user.setIdentifier(identifier);
//
//        TIMManager.getInstance().login(Constant.SDK_APPID, user, userSig, this);

        // identifier为用户名，userSig 为用户登录凭证
//        TIMManager.getInstance().login(nickName, userSig, new TIMCallBack() {
//            @Override
//            public void onError(int code, String desc) {
//                //错误码code和错误描述desc，可用于定位请求失败原因
//                //错误码code列表请参见错误码表
//                ToastUtils.showToast("登录失败，msg:" + code + ";" + desc);
//                LogUtil.d("login failed. code: " + code + " errmsg: " + desc);
//            }
//
//            @Override
//            public void onSuccess() {
//                LogUtil.d("login succ");
//                TCUserInfoMgr.getInstance().setUserId(nickName, new ITCUserInfoMgrListener() {
//                    @Override
//                    public void OnQueryUserInfo(int error, String errorMsg) {
//                        // TODO: 16/8/10
//                    }
//
//                    @Override
//                    public void OnSetUserInfo(int error, String errorMsg) {
//                        if (0 != error)
//                            Toast.makeText(getApplicationContext(), "设置 User ID 失败" + errorMsg, Toast.LENGTH_LONG).show();
//                    }
//                });
//
//                Intent intent = new Intent();
//                intent.setClass(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
//                LoginActivity.this.finish();
//            }
//        });

    }

    /**
     * 显示press
     */
    protected void showDialog() {
        if (loadingDialog != null) {
            loadingDialog.showDialog();
        }
    }

    /**
     * 隐藏press
     */
    protected void dismissDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismissDialog();
        }
    }

    @Override
    public void onSuccess() {
        //初始化消息监听
        MessageEvent.getInstance();
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        TCUserInfoMgr.getInstance().setUserId(identifier, new ITCUserInfoMgrListener() {
            @Override
            public void OnQueryUserInfo(int error, String errorMsg) {
                // TODO: 16/8/10
            }

            @Override
            public void OnSetUserInfo(int error, String errorMsg) {
                if (0 != error)
                    ToastUtils.showToast("设置 User ID 失败" + errorMsg);
            }
        });

        LoginActivity.this.finish();
    }

    @Override
    public void onFailure(int code, String msg) {
        //错误码code和错误描述desc，可用于定位请求失败原因
        //错误码code列表请参见错误码表
        ToastUtils.showToast("登录失败，msg:" + code + ";" + msg);
    }

    /**
     * 更新位置
     *
     * @param uid
     */
    private void updataLocation(String uid) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_updateUserLocation);
        params.addBodyParameter("member_id", uid);
        params.addBodyParameter("longitude", longitude);
        params.addBodyParameter("latitude", latitude);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

}
