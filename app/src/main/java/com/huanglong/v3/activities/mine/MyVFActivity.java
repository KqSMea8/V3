package com.huanglong.v3.activities.mine;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.homepage.AlbumListActivity;
import com.huanglong.v3.adapter.homepage.SoundBookAdapter;
import com.huanglong.v3.model.homepage.SoundBookBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.FileUtils;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.PopupUtils;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.voice.SoundPublishActivity;
import com.huanglong.v3.voice.SoundRecordActivity;
import com.huanglong.v3.voice.custom.CustomDialog;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zyyoona7.lib.EasyPopup;
import com.zyyoona7.lib.HorizontalGravity;
import com.zyyoona7.lib.VerticalGravity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/21.
 * 我的音频
 */
@ContentView(R.layout.activity_my_vf)
public class MyVFActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.my_vf_list)
    private XRecyclerView vf_list;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;
    @ViewInject(R.id.orange_title_bar_lin)
    private LinearLayout title_bar_lin;
    @ViewInject(R.id.my_vf_lin)
    private LinearLayout my_vf_lin;

    private int page = 1;

    private SoundBookAdapter soundBookAdapter;
    private List<SoundBookBean> soundBookBeanAll = new ArrayList<>();
    private int currentPosition;


    private EasyPopup mCirclePop;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("我的音频");
        tv_right.setText("发布");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        vf_list.setLayoutManager(layoutManager);
        vf_list.setLoadingListener(this);

        soundBookAdapter = new SoundBookAdapter();
        vf_list.setAdapter(soundBookAdapter);
        initPop() ;

    }

    @Override
    protected void logic() {
        vf_list.refresh();

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

    @Event(value = {R.id.title_back, R.id.title_tv_right})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_tv_right:
                mCirclePop.showAtAnchorView(title_bar_lin, VerticalGravity.BELOW, HorizontalGravity.RIGHT, -200, 0);
                break;
        }
    }


    /**
     * 初始化设置值popwindow
     */
    private void initPop() {
        mCirclePop = PopupUtils.initPopup(MyVFActivity.this, R.layout.pop_k_song_menu, my_vf_lin);
        TextView tv1 = mCirclePop.getView(R.id.pop_k_song);
        TextView tv2 = mCirclePop.getView(R.id.pop_k_song_file);
        tv1.setText("录       制");
        tv2.setText("上传音频");
        mCirclePop.getView(R.id.pop_k_song).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCirclePop.dismiss();
                Intent intent = new Intent();
                intent.setClass(MyVFActivity.this, SoundRecordActivity.class);
                startActivity(intent);
            }
        });

        mCirclePop.getView(R.id.pop_k_song_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCirclePop.dismiss();
                chooseFile();
            }
        });

    }


    /**
     * 打开系统文件管理器选择文件
     */
    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件"), 1001);
        } catch (android.content.ActivityNotFoundException ex) {
            ToastUtils.showToast("亲，木有文件管理器啊-_-!!");
        }
    }


    /**
     * 请求我的音频
     */
    private void requestVF() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.my_book_recommend);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        params.addBodyParameter("page", page + "");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    List<SoundBookBean> soundBookBean = gson.fromJson(json, new TypeToken<LinkedList<SoundBookBean>>() {
                    }.getType());
                    if (page == 1) {
                        soundBookBeanAll.clear();
                        soundBookBeanAll.addAll(soundBookBean);
                    } else {
                        soundBookBeanAll.addAll(soundBookBean);
                    }
                    soundBookAdapter.setData(soundBookBeanAll);
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
                if (page == 1) {
                    vf_list.refreshComplete();
                } else {
                    vf_list.loadMoreComplete();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        page = 1;
        requestVF();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestVF();
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
                    soundBookBeanAll.remove(currentPosition);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1001:
                    Uri uri = data.getData();
                    File file = FileUtils.uri2File(MyVFActivity.this, uri);
                    Intent intent = new Intent();
                    intent.setClass(this, SoundPublishActivity.class);
                    intent.putExtra("soundpath", file.getAbsolutePath());
                    startActivityForResult(intent, 1001);
                    break;
            }
        }
    }
}
