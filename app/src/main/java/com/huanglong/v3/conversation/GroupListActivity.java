package com.huanglong.v3.conversation;

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
import com.huanglong.v3.activities.message.FriendsActivity;
import com.huanglong.v3.adapter.message.GroupAdapter;
import com.huanglong.v3.im.event.GroupEvent;
import com.huanglong.v3.im.model.GroupInfo;
import com.huanglong.v3.im.model.GroupProfile;
import com.huanglong.v3.im.model.ProfileSummary;
import com.huanglong.v3.im.presenter.GroupManagerPresenter;
import com.huanglong.v3.im.viewfeatures.GroupInfoView;
import com.huanglong.v3.model.contacts.GroupBean;
import com.huanglong.v3.model.contacts.GroupInfoBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.KeyBoardUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMGroupDetailInfo;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by bin on 2018/4/2.
 * 群列表
 */
@ContentView(R.layout.activity_group_list)
public class GroupListActivity extends BaseActivity implements GroupInfoView, Observer {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;
    @ViewInject(R.id.group_list)
    private RecyclerView group_list;
    @ViewInject(R.id.group_search_edt)
    private EditText edt_search;

    private GroupAdapter groupAdapter;

    private GroupManagerPresenter groupManagerPresenter;
    private List<GroupInfoBean> groupInfo = new ArrayList<>();

    private static GroupListActivity instance;

    private int type = 0;//1.选择


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("群聊");

        ;
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        if (type == 1) {
            tv_right.setText("确定");
        } else {
            tv_right.setText("创建群聊");
        }


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        group_list.setLayoutManager(layoutManager);
        groupAdapter = new GroupAdapter();
        group_list.setAdapter(groupAdapter);

        groupManagerPresenter = new GroupManagerPresenter(this);

        GroupEvent.getInstance().addObserver(this);
    }

    @Override
    protected void logic() {
        instance = this;


        getGroupList();

        groupAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                GroupInfoBean timGroupBaseInfo = (GroupInfoBean) obj;
                if (type == 1) {
                    boolean selected = timGroupBaseInfo.isSelected();
                    if (selected) {
                        timGroupBaseInfo.setSelected(false);
                    } else {
                        timGroupBaseInfo.setSelected(true);
                    }
                    groupAdapter.notifyDataSetChanged();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(GroupListActivity.this, GroupDetailsActivity.class);
                    intent.putExtra("groupId", timGroupBaseInfo.getGroup_id());
                    intent.putExtra("getGroupName", timGroupBaseInfo.getGroup_name());
                    startActivity(intent);
                }

//                ChatActivity.navToChat(GroupListActivity.this, timGroupBaseInfo.getGroupId(), TIMConversationType.Group, timGroupBaseInfo.getGroupName());
            }
        });


        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    KeyBoardUtils.hideKeyboard(GroupListActivity.this);
                    String key = textView.getText().toString().trim();
                    searchGroupList(key);
                    return true;
                }
                return true;
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String trim = edt_search.getText().toString().trim();
//                if (TextUtils.isEmpty(trim)) {
//                    getGroupList();
//                }
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
                GroupListActivity.this.finish();
                break;
            case R.id.title_tv_right:
                if (type == 1) {
                    List<GroupInfoBean> selGroup = getSelGroup();
                    if (selGroup.size() > 0) {
                        Intent intent = new Intent();
                        intent.putExtra("selGroupList", (Serializable) selGroup);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        ToastUtils.showToast("请选择你要发送的群");
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), FriendsActivity.class);
                    intent.putExtra("flag", 1);
                    startActivity(intent);
                }
                break;
        }
    }

    /**
     * 获取选择的的群列表
     *
     * @return
     */
    private List<GroupInfoBean> getSelGroup() {
        List<GroupInfoBean> selGroup = new ArrayList<>();
        if (groupInfo != null && groupInfo.size() > 0) {
            for (int i = 0; i < groupInfo.size(); i++) {
                boolean selected = groupInfo.get(i).isSelected();
                if (selected) {
                    selGroup.add(groupInfo.get(i));
                }
            }
        }
        return selGroup;
    }

    /**
     * 获取群列表
     */
    public void getGroupList() {
        edt_search.setText("");
        List<ProfileSummary> timGroupBaseInfoAll = GroupInfo.getInstance().getGroupListByType(GroupInfo.publicGroup);
        if (timGroupBaseInfoAll != null && timGroupBaseInfoAll.size() > 0) {
            for (ProfileSummary profileSummary : timGroupBaseInfoAll) {
                GroupInfoBean groupInfoBean = new GroupInfoBean();
                groupInfoBean.setGroup_id(profileSummary.getIdentify());
                groupInfoBean.setGroup_name(profileSummary.getName());
                groupInfoBean.setGroup_avatar(profileSummary.getAvatarUrl());
                groupInfoBean.setDescription(profileSummary.getDescription());
                groupInfo.add(groupInfoBean);
            }
        }
        groupAdapter.setData(groupInfo, type);
    }

    /**
     * 搜索群列表
     */
    private void searchGroupList(String key) {
        requestSearchGroup(key);
    }

    @Override
    public void showGroupInfo(List<TIMGroupDetailInfo> groupInfos) {
        if (groupInfos == null || groupInfos.size() == 0) {
            ToastUtils.showToast("暂无该群");
            return;
        }
        groupInfo.clear();
        for (TIMGroupDetailInfo timGroupDetailInfo : groupInfos) {
            GroupProfile groupProfile = new GroupProfile(timGroupDetailInfo);
            GroupInfoBean groupInfoBean = new GroupInfoBean();
            groupInfoBean.setGroup_id(groupProfile.getIdentify());
            groupInfoBean.setGroup_name(groupProfile.getName());
            groupInfoBean.setGroup_avatar(groupProfile.getAvatarUrl());
            groupInfoBean.setDescription(groupProfile.getDescription());
            groupInfo.add(groupInfoBean);
//            timGroupBaseInfoAll.add(new GroupProfile(timGroupDetailInfo));
        }
        groupAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
        GroupEvent.getInstance().deleteObserver(this);
    }

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof GroupEvent) {
            if (data instanceof GroupEvent.NotifyCmd) {
                GroupEvent.NotifyCmd cmd = (GroupEvent.NotifyCmd) data;
                switch (cmd.type) {
                    case DEL:
                        delGroup((String) cmd.data);
                        break;
                    case ADD:
                        addGroup((TIMGroupCacheInfo) cmd.data);
                        break;
                    case UPDATE:
                        updateGroup((TIMGroupCacheInfo) cmd.data);
                        break;
                }
            }
        }
    }


    private void delGroup(String groupId) {
        Iterator<GroupInfoBean> it = groupInfo.iterator();
        while (it.hasNext()) {
            GroupInfoBean item = it.next();
            if (item.getGroup_id().equals(groupId)) {
                it.remove();
                groupAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    private void addGroup(TIMGroupCacheInfo info) {
        if (info != null && info.getGroupInfo().getGroupType().equals(GroupInfo.publicGroup)) {
            GroupProfile groupProfile = new GroupProfile(info);
            GroupInfoBean groupInfoBean = new GroupInfoBean();
            groupInfoBean.setGroup_id(groupProfile.getIdentify());
            groupInfoBean.setGroup_name(groupProfile.getName());
            groupInfoBean.setGroup_avatar(groupProfile.getAvatarUrl());
            groupInfoBean.setDescription(groupProfile.getDescription());
            groupInfo.add(groupInfoBean);
//            timGroupBaseInfoAll.add(profile);
            groupAdapter.notifyDataSetChanged();
        }
    }

    private void updateGroup(TIMGroupCacheInfo info) {
        TIMGroupDetailInfo groupInfo = info.getGroupInfo();
        delGroup(groupInfo.getGroupId());
        addGroup(info);
    }

    /**
     * 搜索群
     *
     * @param key
     */
    private void requestSearchGroup(String key) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.search_group);
        params.addBodyParameter("name", key);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<GroupBean> groupBeans = gson.fromJson(json, new TypeToken<LinkedList<GroupBean>>() {
                    }.getType());
                    if (groupBeans != null && groupBeans.size() > 0) {
                        List<String> groupId = new ArrayList<>();
                        for (GroupBean groupBean : groupBeans) {
                            groupId.add(groupBean.getGroupId());
//                            GroupProfile profile = GroupInfo.getInstance().getGroupProfile(GroupInfo.publicGroup, groupBean.getGroupId());
//                            timGroupBaseInfoAll.add(profile);
                        }
                        groupManagerPresenter.searchGroupByID(groupId);
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


}
