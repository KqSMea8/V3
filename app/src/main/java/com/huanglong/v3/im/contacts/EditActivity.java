package com.huanglong.v3.im.contacts;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.utils.ToastUtils;
import com.tencent.TIMCallBack;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * 修改文本页面
 */
@ContentView(R.layout.activity_edit)
public class EditActivity extends BaseActivity implements TIMCallBack {


    @ViewInject(R.id.editContent)
    private EditText input;
    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.title_tv_right)
    private TextView tv_right;

    private static EditInterface editAction;
    public final static String RETURN_EXTRA = "result";
    private static String defaultString;

    private static int lenLimit;

    /**
     * 启动修改文本界面
     *
     * @param context    fragment context
     * @param title      界面标题
     * @param defaultStr 默认文案
     * @param reqCode    请求码，用于识别返回结果
     * @param action     操作回调
     */
    public static void navToEdit(Fragment context, String title, String defaultStr, int reqCode, EditInterface action) {
        Intent intent = new Intent(context.getActivity(), EditActivity.class);
        intent.putExtra("title", title);
        context.startActivityForResult(intent, reqCode);
        defaultString = defaultStr;
        editAction = action;
    }


    /**
     * 启动修改文本界面
     *
     * @param context    activity context
     * @param title      界面标题
     * @param defaultStr 默认文案
     * @param reqCode    请求码，用于识别返回结果
     * @param action     操作回调
     */
    public static void navToEdit(Activity context, String title, String defaultStr, int reqCode, EditInterface action) {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra("title", title);
        context.startActivityForResult(intent, reqCode);
        defaultString = defaultStr;
        editAction = action;
    }


    /**
     * 启动修改文本界面
     *
     * @param context    fragment context
     * @param title      界面标题
     * @param defaultStr 默认文案
     * @param reqCode    请求码，用于识别返回结果
     * @param action     操作回调
     * @param limit      输入长度限制
     */
    public static void navToEdit(Fragment context, String title, String defaultStr, int reqCode, EditInterface action, int limit) {
        Intent intent = new Intent(context.getActivity(), EditActivity.class);
        intent.putExtra("title", title);
        context.startActivityForResult(intent, reqCode);
        defaultString = defaultStr;
        editAction = action;
        lenLimit = limit;
    }


    /**
     * 启动修改文本界面
     *
     * @param context    activity context
     * @param title      界面标题
     * @param defaultStr 默认文案
     * @param reqCode    请求码，用于识别返回结果
     * @param action     操作回调
     * @param limit      输入长度限制
     */
    public static void navToEdit(Activity context, String title, String defaultStr, int reqCode, EditInterface action, int limit) {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra("title", title);
        context.startActivityForResult(intent, reqCode);
        defaultString = defaultStr;
        editAction = action;
        lenLimit = limit;
    }

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {
        tv_right.setText("确定");
        String str_title = getIntent().getStringExtra("title");
        tv_title.setText(str_title);

//        getIntent().getStringExtra("title");
//        TemplateTitle title = (TemplateTitle) findViewById(R.id.editTitle);
//        title.setTitleText(getIntent().getStringExtra("title"));
//        title.setMoreTextAction(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    @Override
    protected void logic() {
        if (defaultString != null) {
            input.setText(defaultString);
            input.setSelection(defaultString.length());
        }
        if (lenLimit != 0) {
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(lenLimit)});
        }
    }

    @Event(value = {R.id.title_back, R.id.title_tv_right})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                EditActivity.this.finish();
                break;
            case R.id.title_tv_right:
                editAction.onEdit(input.getText().toString(), EditActivity.this);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        defaultString = null;
        editAction = null;
        lenLimit = 0;
    }

    @Override
    public void onError(int i, String s) {
        ToastUtils.showToast(getString(R.string.edit_error));
    }

    @Override
    public void onSuccess() {
        Intent intent = new Intent();
        intent.putExtra(RETURN_EXTRA, input.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    public interface EditInterface {
        void onEdit(String text, TIMCallBack callBack);
    }
}
