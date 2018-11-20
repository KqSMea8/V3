package com.huanglong.v3.activities.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseFragment;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.homepage.KSFAdapter;
import com.huanglong.v3.model.homepage.KSFBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.voice.custom.CustomDialog;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/7.
 * K歌关注
 */

public class KSFFragment extends BaseFragment implements XRecyclerView.LoadingListener {


    @ViewInject(R.id.k_song_list)
    private XRecyclerView song_list;

    private int PAGE = 1;

    private KSFAdapter ksfAdapter;

    private List<KSFBean> ksfAllBeans = new ArrayList<>();
    private int currentPosition;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_k_song, container, false);
        return view;
    }

    @Override
    protected void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        song_list.setLayoutManager(layoutManager);
        song_list.setLoadingListener(this);
        ksfAdapter = new KSFAdapter();
        song_list.setAdapter(ksfAdapter);

        song_list.refresh();

    }

    @Override
    protected void logic() {

        ksfAdapter.setOnItemClickListener(new ItemTypeClickListener() {
            @Override
            public void onItemClick(Object obj, int position, int type) {
                KSFBean ksfBean = (KSFBean) obj;
                currentPosition = position;
                if (type == 1) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), KSonDetActivity.class);
                    intent.putExtra("ksfBean", ksfBean);
                    startActivity(intent);
                } else if (type == 2) {
                    CustomDialog mCustomDialog = new CustomDialog(getActivity(), "是否删除该音频？") {

                        @Override
                        public void EnsureEvent() {
                            requestKSongDelete(ksfBean.getId());
                            dismiss();
                        }
                    };
                    mCustomDialog.setCanceledOnTouchOutside(false);
                    mCustomDialog.show();
                } else if (type == 3) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PersonalPageActivity.class);
                    intent.putExtra("uid", ksfBean.getMember_id());
                    startActivity(intent);
                }

            }

            @Override
            public void onItemViewClick(Object obj, int position, int type, View view) {

            }

        });

    }

    /**
     * 请求K个列表数据
     */
    private void requestKSongData() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.music_follow_list);
        params.addBodyParameter("user_id", UserInfoUtils.getUid());
        params.addBodyParameter("page", PAGE + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<KSFBean> ksfBeans = gson.fromJson(json, new TypeToken<LinkedList<KSFBean>>() {
                    }.getType());
                    if (PAGE == 1) {
                        ksfAllBeans.clear();
                        ksfAllBeans.addAll(ksfBeans);
                    } else {
                        ksfAllBeans.addAll(ksfBeans);
                    }
                    ksfAdapter.setData(ksfAllBeans);
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
                if (PAGE == 1) {
                    song_list.refreshComplete();
                } else {
                    song_list.loadMoreComplete();
                }
            }
        });


    }

    @Override
    public void onRefresh() {
        PAGE = 1;
        requestKSongData();
    }

    @Override
    public void onLoadMore() {
        PAGE++;
        requestKSongData();
    }


    /**
     * 我发布的K歌删除
     *
     * @param musicId
     */
    private void requestKSongDelete(String musicId) {
        RequestParams params = MRequestParams.getNoTokenParams(Api.user_music_delete);
        params.addBodyParameter("music_id", musicId);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    ToastUtils.showToast("删除成功");
                    ksfAllBeans.remove(currentPosition);
                    ksfAdapter.notifyDataSetChanged();
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
