package com.huanglong.v3.adapter;

import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bachors.wordtospan.WordToSpan;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.circle.SocialCircleFragment;
import com.huanglong.v3.activities.circle.SocialCircleTextActivity;
import com.huanglong.v3.activities.homepage.PersonalBowenFragment;
import com.huanglong.v3.activities.homepage.PersonalPageActivity;
import com.huanglong.v3.activities.imagepreview.ImagePreviewActivity;
import com.huanglong.v3.activities.main.FriendsFragment;
import com.huanglong.v3.activities.mine.MyCircleActivity;
import com.huanglong.v3.adapter.circle.CircleImgAdapter;
import com.huanglong.v3.adapter.circle.CommentAdapter;
import com.huanglong.v3.adapter.circle.ZanListAdapter;
import com.huanglong.v3.model.circle.CircleImgBean;
import com.huanglong.v3.model.circle.CommentBean;
import com.huanglong.v3.model.circle.PraiseBean;
import com.huanglong.v3.model.login.SpaceBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.ToastUtils;
import com.huanglong.v3.utils.UserInfoUtils;
import com.huanglong.v3.view.LikesView;
import com.lcodecore.extextview.ExpandTextView;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bin on 2018/1/16.
 * 空间的adapter
 */

public class SpaceAdapter extends RecyclerView.Adapter<SpaceAdapter.ViewHolder> {


    private List<SpaceBean> spaceBeans;
    private ItemTypeClickListener itemTypeClickListener;

    public static final int CLICK_TYPE_AVATAR = 1;//点击头像
    public static final int CLICK_TYPE_MORE = 2;//等级更多
    public static final int CLICK_TYPE_REPLY = 3;//评论回复
    public static final int CLICK_TYPE_DELETE = 4;//社圈删除
    public static final int CLICK_TYPE_REWARD = 5;//社圈打赏
    public static final int CLICK_TYPE_SHARE = 6;//社圈分享


    public boolean isZan = false;

    public boolean isGift = true;


    /**
     * 设置数据
     *
     * @param spaceBeans
     */
    public void setData(List<SpaceBean> spaceBeans) {
        this.spaceBeans = spaceBeans;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_space_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(spaceBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (spaceBeans != null) {
            return spaceBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_NickName, tv_time, tv_location, tv_reward;
        private ImageView avatar, img, img_more, img_delete, img_enterprise, img_share;
        private TextView tv_content, tv_title;
        private RecyclerView img_list;
        private LinearLayout zan_comment_lin, zan_lin, comment_lin;
        private RecyclerView comment_list;
        private LikesView likesView;

        private CommentAdapter commentAdapter;
        private CircleImgAdapter circleImgAdapter;
        private ZanListAdapter zanListAdapter;


        public ViewHolder(View itemView) {
            super(itemView);
            tv_NickName = itemView.findViewById(R.id.item_space_user_nickname);
            tv_time = itemView.findViewById(R.id.item_space_time);
            tv_content = (TextView) itemView.findViewById(R.id.item_space_content);
            tv_title = (TextView) itemView.findViewById(R.id.item_space_title);
            tv_location = itemView.findViewById(R.id.item_space_location);
            avatar = itemView.findViewById(R.id.item_space_user_avatar);
            img = itemView.findViewById(R.id.item_space_img);
            img_list = itemView.findViewById(R.id.item_space_img_list);
            img_more = itemView.findViewById(R.id.item_space_more);
            zan_comment_lin = itemView.findViewById(R.id.item_space_zan_comment);
            zan_lin = itemView.findViewById(R.id.item_space_zan_lin);
            comment_lin = itemView.findViewById(R.id.item_space_comment_lin);
            comment_list = itemView.findViewById(R.id.item_space_comment_list);
            img_delete = itemView.findViewById(R.id.item_space_delete);
            tv_reward = itemView.findViewById(R.id.item_space_reward);
            img_enterprise = itemView.findViewById(R.id.item_space_enterprise);
            likesView = itemView.findViewById(R.id.item_space_likeView);
            img_share = itemView.findViewById(R.id.item_space_share);

            GridLayoutManager layoutManager = new GridLayoutManager(V3Application.getInstance(), 3);
            layoutManager.setOrientation(GridLayoutManager.VERTICAL);
            img_list.setLayoutManager(layoutManager);
            circleImgAdapter = new CircleImgAdapter();
            img_list.setAdapter(circleImgAdapter);

            LinearLayoutManager layoutManager1 = new LinearLayoutManager(V3Application.getInstance());
            layoutManager1.setOrientation(GridLayoutManager.VERTICAL);
            comment_list.setLayoutManager(layoutManager1);
            commentAdapter = new CommentAdapter();
            comment_list.setAdapter(commentAdapter);

        }

        public void setData(final SpaceBean spaceBean, final int position) {
            zan_comment_lin.setVisibility(View.GONE);
            zan_lin.setVisibility(View.GONE);
            comment_lin.setVisibility(View.GONE);
            tv_location.setVisibility(View.GONE);
            tv_NickName.setText(spaceBean.getNickname());
            tv_time.setText(spaceBean.getCreate_time());
            tv_content.setVisibility(View.GONE);
            x.image().bind(avatar, spaceBean.getHead_image(), MImageOptions.getCircularImageOptions());

            String content = spaceBean.getContent();
            if (!TextUtils.isEmpty(content)) {
                tv_title.setVisibility(View.VISIBLE);
                if (content.length() > 100) {
                    tv_title.setMaxLines(1);
                    tv_title.setBackgroundColor(ContextCompat.getColor(V3Application.getInstance(), R.color.gray_DCDCDC));
                    tv_title.setText(content);
                    tv_title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(V3Application.getInstance(), SocialCircleTextActivity.class);
                            intent.putExtra("content", content);
                            V3Application.getInstance().startActivity(intent);
                        }
                    });

                } else {
                    tv_title.setMaxLines(10);
                    tv_title.setBackgroundColor(ContextCompat.getColor(V3Application.getInstance(), R.color.white));
                    WordToSpan link = new WordToSpan();
                    link.setColorTAG(ContextCompat.getColor(V3Application.getInstance(), R.color.topic_color))
                            .setColorURL(ContextCompat.getColor(V3Application.getInstance(), R.color.waveform_selected))
                            .setUnderlineURL(true)
                            .setLink(content)
                            .into(tv_title)
                            .setClickListener(new WordToSpan.ClickListener() {
                                @Override
                                public void onClick(String type, String text) {
//                                    // type: "tag", "mail", "url", "phone", "mention" or "custom"
//                                    ToastUtils.showToast("Type: " + type + "\nText: " + text);
                                    if (TextUtils.equals("url", type)) {
                                        Intent intent = new Intent();
                                        intent.setData(Uri.parse(text));//Url 就是你要打开的网址
                                        intent.setAction(Intent.ACTION_VIEW);
                                        V3Application.getInstance().startActivity(intent); //启动浏览器
                                    }

                                }
                            });
                }
            } else {
                tv_title.setVisibility(View.GONE);
                tv_title.setBackgroundColor(ContextCompat.getColor(V3Application.getInstance(), R.color.white));
                tv_title.setText(content);
            }


//


            int type = spaceBean.getType();
            if (type == 2) {
                img_enterprise.setVisibility(View.VISIBLE);
            } else {
                img_enterprise.setVisibility(View.GONE);
            }

            final List<CircleImgBean> data = spaceBean.getData();
            img.setVisibility(View.GONE);
            img_list.setVisibility(View.GONE);
            if (data != null || data.size() != 0) {
                if (data.size() == 1) {
                    img.setVisibility(View.VISIBLE);
                    x.image().bind(img, data.get(0).getImageurl(), MImageOptions.getNormalImageOptions());
                } else {
                    img_list.setVisibility(View.VISIBLE);
                    circleImgAdapter.setData(data);
                }
            }

            List<CommentBean> comment_list = spaceBean.getComment_list();
            if (comment_list != null && comment_list.size() > 0) {
                zan_comment_lin.setVisibility(View.VISIBLE);
                comment_lin.setVisibility(View.VISIBLE);
                commentAdapter.setData(comment_list);
            }

            List<PraiseBean> upvote_list = spaceBean.getUpvote_list();
            if (upvote_list != null && upvote_list.size() > 0) {
                zan_comment_lin.setVisibility(View.VISIBLE);
                zan_lin.setVisibility(View.VISIBLE);
                likesView.setList(upvote_list);
                likesView.notifyDataSetChanged();
                likesView.setOnItemClickListener(new LikesView.OnItemClickListener() {
                    @Override
                    public void OnItemClick(PraiseBean praiseBean) {
                        Intent intent = new Intent();
                        intent.setClass(V3Application.getInstance(), PersonalPageActivity.class);
                        intent.putExtra("uid", praiseBean.getMember_id());
                        V3Application.getInstance().startActivity(intent);
                    }
                });


            }

            String member_id = spaceBean.getMember_id();
            if (TextUtils.equals(member_id, UserInfoUtils.getUid())) {
                img_delete.setVisibility(View.VISIBLE);
            } else {
                img_delete.setVisibility(View.GONE);
            }

            if (isGift) {
                tv_reward.setVisibility(View.VISIBLE);
                tv_reward.setText(spaceBean.getDashang_total() + "");
            } else {
                tv_reward.setVisibility(View.GONE);
            }


            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        itemTypeClickListener.onItemClick(spaceBean, position, CLICK_TYPE_AVATAR);
                    }
                }
            });


            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<String> imgs = new ArrayList<>();
                    for (CircleImgBean circleImgBean : data) {
                        imgs.add(circleImgBean.getImageurl());
                    }
                    Intent intent = new Intent();
                    intent.setClass(V3Application.getInstance(), ImagePreviewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putStringArrayListExtra("imgs", (ArrayList<String>) imgs);
                    intent.putExtra("index", 0);
                    V3Application.getInstance().startActivity(intent);
                    imgs.clear();
                    imgs = null;
                }
            });

            circleImgAdapter.setOnItemClickListener(new ItemClickListener() {
                @Override
                public void onItemClick(Object obj, int position) {
                    List<String> imgs = new ArrayList<>();
                    for (CircleImgBean circleImgBean : data) {
                        imgs.add(circleImgBean.getImageurl());
                    }
                    Intent intent = new Intent();
                    intent.setClass(V3Application.getInstance(), ImagePreviewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putStringArrayListExtra("imgs", (ArrayList<String>) imgs);
                    intent.putExtra("index", position);
                    V3Application.getInstance().startActivity(intent);
                    imgs.clear();
                    imgs = null;
                }
            });


            img_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        isZan = false;
                        for (PraiseBean praiseBean : upvote_list) {
                            String member_id = praiseBean.getMember_id();
                            if (TextUtils.equals(member_id, UserInfoUtils.getUid())) {
                                isZan = true;
                                break;
                            }
                        }
                        itemTypeClickListener.onItemViewClick(spaceBean, position, CLICK_TYPE_MORE, img_more);
                    }
                }
            });


            commentAdapter.setOnItemClickListener(new ItemClickListener() {
                @Override
                public void onItemClick(Object obj, int position) {
                    CommentBean commentBean = (CommentBean) obj;
                    if (itemTypeClickListener != null) {
                        if (FriendsFragment.instance != null) {
                            FriendsFragment.instance.commentBean = commentBean;
                            FriendsFragment.instance.commentPosition = position;
                        }
                        if (SocialCircleFragment.instance != null) {
                            SocialCircleFragment.instance.commentBean = commentBean;
                            SocialCircleFragment.instance.commentPosition = position;
                        }
                        if (MyCircleActivity.instance != null) {
                            MyCircleActivity.instance.commentBean = commentBean;
                            MyCircleActivity.instance.commentPosition = position;
                        }
                        if (PersonalBowenFragment.instance != null) {
                            PersonalBowenFragment.instance.commentBean = commentBean;
                            PersonalBowenFragment.instance.commentPosition = position;
                        }
                        itemTypeClickListener.onItemClick(spaceBean, position, CLICK_TYPE_REPLY);
                    }
                }
            });


            img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        itemTypeClickListener.onItemClick(spaceBean, position, CLICK_TYPE_DELETE);
                    }
                }
            });

            tv_reward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        itemTypeClickListener.onItemClick(spaceBean, position, CLICK_TYPE_REWARD);
                    }
                }
            });


            tv_title.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // 得到剪贴板管理器
                    ClipboardManager cmb = (ClipboardManager) V3Application.getInstance()
                            .getSystemService(V3Application.getInstance().CLIPBOARD_SERVICE);
                    cmb.setText(tv_title.getText().toString().trim());
                    ToastUtils.showToast("复制成功");
                    return true;
                }
            });


            img_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemTypeClickListener != null) {
                        itemTypeClickListener.onItemClick(spaceBean, position, CLICK_TYPE_SHARE);
                    }
                }
            });


        }


    }

    public void setOnItemClickListener(ItemTypeClickListener itemTypeClickListener) {
        this.itemTypeClickListener = itemTypeClickListener;
    }
}
