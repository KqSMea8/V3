package com.huanglong.v3.activities.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.homepage.SoundBookAdapter;
import com.huanglong.v3.model.homepage.SoundBookBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.voice.custom.CustomDialog;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/3/24.
 * 个人主页的音频
 */

public class PersonalVFFragment extends BaseFragment {

    @ViewInject(R.id.personal_list)
    private RecyclerView personal_list;

    private SoundBookAdapter soundBookAdapter;
    private int currentPosition;
    private List<SoundBookBean> soundBookBean;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_live, container, false);
        return view;
    }

    @Override
    protected void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        personal_list.setLayoutManager(layoutManager);
        soundBookAdapter = new SoundBookAdapter();
        personal_list.setAdapter(soundBookAdapter);
        requestVFList();

    }

    @Override
    protected void logic() {
        soundBookAdapter.setOnItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                SoundBookBean soundBookBean = (SoundBookBean) obj;
                currentPosition = position;
                if (type == 1) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), AlbumListActivity.class);
                    intent.putExtra("soundBookBean", soundBookBean);
                    startActivity(intent);
                } else {
                    CustomDialog mCustomDialog = new CustomDialog(getActivity(), "是否删除该回播？") {
                        @Override
                        public void EnsureEvent() {
                            requestDelete(soundBookBean.getId());
                            dismiss();
                        }
                    };
                    mCustomDialog.setCanceledOnTouchOutside(false);
                    mCustomDialog.show();
                }

            }

            @Override
            public void onItemViewClick(Object obj, int position, int type, View view) {

            }
        });
    }

    /**
     * 请求我的音频
     */
    private void requestVFList() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.my_book_recommend);
        params.addBodyParameter("member_id", PersonalPageActivity.instance.follower_id);
        params.addBodyParameter("page", "1");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    soundBookBean = gson.fromJson(json, new TypeToken<LinkedList<SoundBookBean>>() {
                    }.getType());
                    soundBookAdapter.setData(soundBookBean);
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
     * 删除有声书
     *
     * @param book_id
     */
    private void requestDelete(String book_id) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_book_delete);
        params.addBodyParameter("book_id", book_id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("删除成功");
                    soundBookBean.remove(currentPosition);
                    soundBookAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showToast("删除失败");
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
