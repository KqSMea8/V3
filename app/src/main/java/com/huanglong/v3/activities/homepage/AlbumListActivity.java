package com.huanglong.v3.activities.homepage;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.adapter.homepage.AlbumListAdapter;
import com.huanglong.v3.model.homepage.AlbumBean;
import com.huanglong.v3.model.homepage.SoundBookBean;
import com.huanglong.v3.netutils.Api;
import com.huanglong.v3.netutils.JsonHandleUtils;
import com.huanglong.v3.netutils.MRequestParams;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.UserInfoUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bin on 2018/4/8.
 * 有声书节目列表
 */
@ContentView(R.layout.activity_list_album)
public class AlbumListActivity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.album_list)
    private XRecyclerView act_list;


    private SoundBookBean soundBookBean;
    private String book_id;


    private View head_view;

    private AlbumListAdapter albumListAdapter;
    private List<AlbumBean> albumBeans;

    public static AlbumListActivity instance;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {

        instance = this;
        tv_title.setText("专辑详情");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        act_list.setLayoutManager(layoutManager);
        act_list.setLoadingMoreEnabled(false);
        act_list.setPullRefreshEnabled(false);
//        act_list.refresh();

        head_view = LayoutInflater.from(this).inflate(R.layout.item_sound_book, act_list, false);
        act_list.addHeaderView(head_view);
        albumListAdapter = new AlbumListAdapter();
        act_list.setAdapter(albumListAdapter);

    }

    @Override
    protected void logic() {
        Intent intent = getIntent();
        soundBookBean = (SoundBookBean) intent.getSerializableExtra("soundBookBean");
        book_id = soundBookBean.getId();
        showAlbumInfo(head_view);
        requestAlbumList();

        albumListAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {
                AlbumBean albumBean = (AlbumBean) obj;
                Intent intent1 = new Intent();
                intent1.setClass(AlbumListActivity.this, PlaySoundActivity.class);
                intent1.putExtra("albumBeans", (Serializable) albumBeans);
                intent1.putExtra("position", position);
                intent1.putExtra("soundBookBean", soundBookBean);
                startActivity(intent1);
            }
        });

    }

    @Event(value = {R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                AlbumListActivity.this.finish();
                break;
        }
    }

    /**
     * 显示专辑信息
     *
     * @param view
     */
    private void showAlbumInfo(View view) {

        ImageView img_icon = view.findViewById(R.id.item_sound_book_img);
        TextView tv_title = view.findViewById(R.id.item_sound_book_title);
        TextView tv_content = view.findViewById(R.id.item_sound_book_content);
        TextView tv_play = view.findViewById(R.id.item_sound_book_play);
        TextView tv_drama = view.findViewById(R.id.item_sound_book_drama);
        TextView tv_author = view.findViewById(R.id.item_sound_book_author);

        x.image().bind(img_icon, soundBookBean.getCover_img(), MImageOptions.getNormalImageOptions());
        tv_title.setText(soundBookBean.getTitle());
        tv_content.setText(soundBookBean.getIntroduce());
        tv_play.setText(soundBookBean.getPlay_count() + "");
        tv_drama.setText(soundBookBean.getCollect_count() + "集");
        tv_author.setText("作者:" + soundBookBean.getNickname());


        tv_author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String member_id = soundBookBean.getMember_id();
                Intent intent = new Intent();
                intent.setClass(AlbumListActivity.this, PersonalPageActivity.class);
                intent.putExtra("uid", member_id);
                startActivity(intent);
            }
        });

    }


    /**
     * 请求专辑列表
     */
    public void requestAlbumList() {
        RequestParams params = MRequestParams.getNoTokenParams(Api.book_program);
        params.addBodyParameter("book_id", book_id);
        params.addBodyParameter("member_id", UserInfoUtils.getUid());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = JsonHandleUtils.JsonHandle(result);
                if (!TextUtils.isEmpty(json)) {
                    Gson gson = V3Application.getGson();
                    albumBeans = gson.fromJson(json, new TypeToken<LinkedList<AlbumBean>>() {
                    }.getType());
                    albumListAdapter.setData(albumBeans);
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
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
