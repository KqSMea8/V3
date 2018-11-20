package com.huanglong.v3.activities.homepage;

import android.annotation.SuppressLint;
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
import com.huanglong.v3.adapter.homepage.SoundBookAdapter;
import com.huanglong.v3.model.homepage.SoundBookBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.voice.custom.CustomDialog;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by bin on 2018/4/5.
 * 有声书的Fragment/
 */

@SuppressLint("ValidFragment")
public class SoundBookFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    @ViewInject(R.id.sound_book_list)
    private XRecyclerView book_list;

    private String cid;

    private SoundBookAdapter soundBookAdapter;
    private List<SoundBookBean> soundBookBean;
    private int currentPosition;


    public SoundBookFragment(String cid) {
        this.cid = cid;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sound_book, container, false);
        return view;
    }

    @Override
    protected void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        book_list.setLayoutManager(layoutManager);
        soundBookAdapter = new SoundBookAdapter();
        book_list.setAdapter(soundBookAdapter);
        book_list.setLoadingListener(this);
        book_list.setLoadingMoreEnabled(false);


    }

    @Override
    protected void logic() {
        book_list.refresh();

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
                    CustomDialog mCustomDialog = new CustomDialog(getActivity(), "是否删除该音频？") {
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
     * 请求有声音频分类
     */
    private void requestClass() {
        RequestParams params = MRequestParams.getUidParams(Api.book_list);
        params.addBodyParameter("category_id", cid);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    soundBookBean = gson.fromJson(json, new TypeToken<LinkedList<SoundBookBean>>() {
                    }.getType());
                    if (soundBookBean != null && soundBookBean.size() > 0) {
                        soundBookAdapter.setData(soundBookBean);
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
                book_list.refreshComplete();
            }
        });
    }

    @Override
    public void onRefresh() {
        requestClass();
    }

    @Override
    public void onLoadMore() {

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
