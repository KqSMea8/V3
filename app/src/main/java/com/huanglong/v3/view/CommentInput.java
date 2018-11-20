package com.huanglong.v3.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.huanglong.v3.R;
import com.huanglong.v3.utils.CommentChatView;
import com.huanglong.v3.utils.KeyBoardUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bin on 2018/6/7.
 * 评论的 输入
 */

public class CommentInput extends RelativeLayout implements View.OnClickListener, TextWatcher {


    private LinearLayout textPanel;
    private ImageButton btnEmotion;
    private ImageButton btnSend;
    private EditText editText;
    private LinearLayout emoticonPanel;

    private CommentChatView commentChatView;


    private InputMode inputMode = InputMode.NONE;
    private boolean isEmoticonReady;


    public enum InputMode {
        TEXT,
        VOICE,
        EMOTICON,
        MORE,
        VIDEO,
        NONE,
    }

    public CommentInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.comment_input, this);
        initView();
    }


    private void initView() {
        textPanel = (LinearLayout) findViewById(R.id.text_panel);
        btnEmotion = (ImageButton) findViewById(R.id.btnEmoticon);
        btnEmotion.setOnClickListener(this);
        btnSend = (ImageButton) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);
        emoticonPanel = (LinearLayout) findViewById(R.id.emoticonPanel);
        editText = (EditText) findViewById(R.id.input);
        editText.addTextChangedListener(this);
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    updateView(InputMode.TEXT);
                }
            }
        });

    }

    private void updateView(InputMode mode) {
        if (mode == inputMode) return;
        leavingCurrentState();
        switch (inputMode = mode) {
            case TEXT:
                if (editText.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
                break;
            case EMOTICON:
                if (!isEmoticonReady) {
                    prepareEmoticon();
                }
                emoticonPanel.setVisibility(VISIBLE);
                break;
        }
    }

    private void leavingCurrentState() {
        switch (inputMode) {
            case TEXT:
                KeyBoardUtils.closeKeybord(editText, getContext());
//                View view = ((Activity) getContext()).getCurrentFocus();
//                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                editText.clearFocus();
                break;
            case EMOTICON:
                emoticonPanel.setVisibility(GONE);
                break;
        }
    }

    /**
     * 加载表情
     */
    private void prepareEmoticon() {
        if (emoticonPanel == null) return;
        for (int i = 0; i < 5; ++i) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
            for (int j = 0; j < 7; ++j) {

                try {
                    AssetManager am = getContext().getAssets();
                    final int index = 7 * i + j;
                    InputStream is = am.open(String.format("emoticon/%d.gif", index));
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    Matrix matrix = new Matrix();
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    matrix.postScale(3.5f, 3.5f);
                    final Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                            width, height, matrix, true);
                    ImageView image = new ImageView(getContext());
                    image.setImageBitmap(resizedBitmap);
                    image.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
                    linearLayout.addView(image);
                    image.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String content = String.valueOf(index);
                            SpannableString str = new SpannableString(String.valueOf(index));
                            ImageSpan span = new ImageSpan(getContext(), resizedBitmap, ImageSpan.ALIGN_BASELINE);
                            str.setSpan(span, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            editText.append(str);
                        }
                    });
                    is.close();
                } catch (IOException e) {

                }

            }
            emoticonPanel.addView(linearLayout);
        }
        isEmoticonReady = true;
    }

    /**
     * 关联聊天界面逻辑
     */
    public void setChatView(CommentChatView commentChatView) {
        this.commentChatView = commentChatView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_send) {
            if (commentChatView != null) {
                commentChatView.sendText();
            }
        }
        if (id == R.id.btnEmoticon) {
            updateView(inputMode == InputMode.EMOTICON ? InputMode.TEXT : InputMode.EMOTICON);
        }

    }

    /**
     * 获取输入框文字
     */
    public Editable getText() {
        return editText.getText();
    }

    /**
     * 设置输入框文字
     */
    public void setText(String text) {
        editText.setText(text);
    }


    /**
     * 设置输入模式
     */
    public void setInputMode(InputMode mode) {
        updateView(mode);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


}
