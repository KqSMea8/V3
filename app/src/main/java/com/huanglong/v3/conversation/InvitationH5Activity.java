package com.huanglong.v3.conversation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.im.contacts.AddFriendActivity;
import com.huanglong.v3.im.model.CustomMessage;
import com.huanglong.v3.im.model.FriendProfile;
import com.huanglong.v3.im.presenter.FriendshipManagerPresenter;
import com.huanglong.v3.im.presenter.GroupManagerPresenter;
import com.huanglong.v3.model.contacts.FriRelBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.imcore.FriendshipManager;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by bin on 2018/6/16.
 * 邀请的H5页面
 */
@ContentView(R.layout.activity_invitation_h5)
public class InvitationH5Activity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.invitation_web)
    private WebView web_view;

    private String groupId, groupAvatar, groupName;
    private String group_url = "https://www.huachenedu.cn/v3/h5/Test.html";
    private String personal_url = "https://www.huachenedu.cn/v3/h5/Test1.html";
    private String full_url;


    private int type;//1、群，2、个人
    private int fee;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void logic() {

        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        groupAvatar = intent.getStringExtra("groupAvatar");
        groupName = intent.getStringExtra("groupName");
        type = intent.getIntExtra("type", 1);
        fee = intent.getIntExtra("fee", 0);
        if (type == 1) {
            tv_title.setText("群邀请");
            full_url = group_url + "?avatar=" + groupAvatar + "&group_id=" + dealGroupId(groupId) + "&group_name=" + groupName + "&member_id=" + UserInfoUtils.getUid();
        } else {
            tv_title.setText("个人名片");
            full_url = personal_url + "?avatar=" + groupAvatar + "&nick_name=" + groupName + "&member_id=" + groupId + "&user_id=" + UserInfoUtils.getUid();
        }
        initWebView(full_url);
    }

    @Event(value = {R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

    /**
     * 初始化设置webView
     *
     * @param url
     */
    private void initWebView(String url) {

//        web_view.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.removeAllViews();
//                view.loadUrl(url);
//                return true;
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//            }
//        });
//
//        web_view.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int progress) {
//                super.onProgressChanged(view, progress);
//            }
//
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//
//            }
//
//        });
//
//        web_view.setDownloadListener(new DownloadListener() {
//            @Override
//            public void onDownloadStart(String url, String userAgent,
//                                        String contentDisposition, String mimetype,
//                                        long contentLength) {
//            }
//        });


        ButtonClick click = new ButtonClick();
        //这里添加JS的交互事件，这样H5就可以调用原生的代码

        WebSettings webSettings = web_view.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setSupportZoom(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBlockNetworkImage(false);
//        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        web_view.loadUrl(url);
        web_view.addJavascriptInterface(click, "AndroidWebView");

    }

    class ButtonClick {

        //这是 button.click0() 的触发事件
        //H5调用方法：javascript:button.click0()
        @JavascriptInterface
        public void showInfoFromJs() {
            LogUtil.e("web view js login");
            if (type == 1) {
                applyJoinGroup();
            } else {
                if (!TextUtils.equals(groupId, UserInfoUtils.getUid())) {
                    requestFriendsRelationship(groupId);
                } else {
                    ToastUtils.showToast("不能添加自己为好友");
                }

            }

        }

    }


    /**
     * 申请接入群
     */
    private void applyJoinGroup() {
        GroupManagerPresenter.applyJoinGroup(groupId, "", new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("applyJoinGroup error code:" + i + " msg:" + s);
                ToastUtils.showToast("加群失败");
            }

            @Override
            public void onSuccess() {
                ChatActivity.navToChat(InvitationH5Activity.this, groupId, TIMConversationType.Group, groupName);
                InvitationH5Activity.this.finish();
            }
        });
    }

    /**
     * 出去群ID
     *
     * @param groupId
     * @return
     */
    private String dealGroupId(String groupId) {
        if (TextUtils.isEmpty(groupId)) return "";
        String new_groupId = "";
        if (groupId.contains("@TGS#")) {
            new_groupId = groupId.replace("@TGS#", "");
        }
        return new_groupId;
    }


    /**
     * 请求好友关系
     */
    private void requestFriendsRelationship(String friend_id) {
        showDialog();
        RequestParams params = MRequestParams.getNoTokenParams(Api.queryfriendlyrelation);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("friend_id", friend_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    FriRelBean friRelBean = gson.fromJson(json, FriRelBean.class);
                    if (friRelBean != null) {
                        int is_friend = friRelBean.getIs_friend();
                        if (is_friend == 1) {
                            ToastUtils.showToast("已成为好友");
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), AddFriendActivity.class);
                            intent.putExtra("id", groupId);
                            intent.putExtra("name", groupName);
                            intent.putExtra("is_open", fee);
                            startActivity(intent);
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
                dismissDialog();
            }
        });
    }

}
