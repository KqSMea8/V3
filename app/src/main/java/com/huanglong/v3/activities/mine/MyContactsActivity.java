package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.homepage.PersonalPageActivity;
import com.huanglong.v3.adapter.mine.AddBooAdapter;
import com.huanglong.v3.model.mine.AddBooBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.KeyBoardUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.tencent.TIMMessage;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/26.
 * 我的联系人
 */
@ContentView(R.layout.activity_my_contacts)
public class MyContactsActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.my_contacts_list)
    private RecyclerView contacts_list;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;
    @ViewInject(R.id.my_contacts_search)
    private EditText edt_search;

    private AddBooAdapter addBooAdapter;
    private int currentPosition = -1;

    private int flag = 0; //0.通讯录 1.选择联系人
    private List<AddBooBean> addBooBeans;

    private List<AddBooBean> selAddBooBeans = new ArrayList<>();

    private TIMMessage timMsg;

    private String keyword = "";
    private String member_id = "";


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_right.setText("确定");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        contacts_list.setLayoutManager(layoutManager);
        addBooAdapter = new AddBooAdapter();
        contacts_list.setAdapter(addBooAdapter);
    }

    @Override
    protected void logic() {

        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", 0);
        if (flag == 1) {
            member_id = intent.getStringExtra("member_id");
            edt_search.setVisibility(View.GONE);
            addBooAdapter.isShowRadio = true;
            tv_right.setVisibility(View.VISIBLE);
            tv_title.setText("选择联系人");
        } else {
            edt_search.setVisibility(View.VISIBLE);
            addBooAdapter.isShowRadio = false;
            tv_right.setVisibility(View.GONE);
            tv_title.setText("通讯录");
        }

        requestContacts();

        addBooAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                AddBooBean addBooBean = (AddBooBean) obj;
                if (flag == 1) {
                    boolean selected = addBooBean.isSelected();
                    if (selected) {
                        addBooBean.setSelected(false);
                    } else {
                        addBooBean.setSelected(true);
                    }
                    addBooAdapter.notifyDataSetChanged();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PersonalPageActivity.class);
                    intent.putExtra("uid", addBooBean.getFriend_id());
                    startActivity(intent);
                }

            }
        });

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    KeyBoardUtils.hideKeyboard(MyContactsActivity.this);
                    keyword = textView.getText().toString().trim();
                    requestContacts();
                    return true;
                }
                return false;
            }
        });


        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                if (TextUtils.isEmpty(s)) {
                    keyword = "";
                    requestContacts();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    @Event(value = {R.id.title_back, R.id.title_tv_right})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_tv_right:
                getSelContacts();
                if (selAddBooBeans.size() > 0) {
                    sendForward();
                } else {
                    ToastUtils.showToast("请选择联系人");
                }
                break;
        }
    }

    /**
     * 获取选中的联系人
     */
    private void getSelContacts() {
        selAddBooBeans.clear();
        for (AddBooBean addBooBean : addBooBeans) {
            boolean selected = addBooBean.isSelected();
            if (selected) {
                selAddBooBeans.add(addBooBean);
            }
        }
    }

    /**
     * 转发
     */
    private void sendForward() {
        Intent intent = new Intent();
        intent.putExtra("selPeople", (Serializable) selAddBooBeans);
        setResult(RESULT_OK, intent);
        finish();
//        for (AddBooBean addBooBean : selAddBooBeans) {
//            TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, addBooBean.getMember_id());
////            conversation.sendMessage();
//        }

    }

    /**
     * 请求我的通讯录
     */
    private void requestContacts() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.addressbook_list);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("keyword", keyword);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (addBooBeans != null) {
                    addBooBeans.clear();
                }
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    addBooBeans = gson.fromJson(json, new TypeToken<LinkedList<AddBooBean>>() {
                    }.getType());
                }
                if (!TextUtils.isEmpty(member_id)) {
                    for (int i = 0; i < addBooBeans.size(); i++) {
                        String user_id = addBooBeans.get(i).getUser_id();
                        if (TextUtils.equals(member_id, user_id)) {
                            addBooBeans.remove(addBooBeans.get(i));
                            i--;
                        }
                    }
                }
                addBooAdapter.setData(addBooBeans);
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

}
