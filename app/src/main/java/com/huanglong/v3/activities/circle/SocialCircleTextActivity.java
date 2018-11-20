package com.huanglong.v3.activities.circle;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bachors.wordtospan.WordToSpan;
import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by bin on 2018/9/15.
 * 社圈显示全文
 */
@ContentView(R.layout.activity_social_circle_text)
public class SocialCircleTextActivity extends BaseActivity {


    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.social_circle_text)
    private TextView tv_circle_text;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_title.setText("全文");

        Intent intent = getIntent();
        String content = intent.getStringExtra("content");

        WordToSpan link = new WordToSpan();
        link.setColorTAG(ContextCompat.getColor(V3Application.getInstance(), R.color.topic_color))
                .setColorURL(ContextCompat.getColor(V3Application.getInstance(), R.color.waveform_selected))
                .setUnderlineURL(true)
                .setLink(content)
                .into(tv_circle_text)
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

    @Override
    protected void logic() {

    }

    @Event(value = {R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

}
