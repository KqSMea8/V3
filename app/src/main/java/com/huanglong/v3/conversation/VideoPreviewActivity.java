package com.huanglong.v3.conversation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.huanglong.v3.BaseFragmentActivity;
import com.huanglong.v3.R;
import com.huanglong.v3.im.utils.FileUtil;
import com.huanglong.v3.im.view.VideoInputDialog;
import com.huanglong.v3.im.viewfeatures.ChatView;
import com.huanglong.v3.utils.ToastUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bin on 2018/9/2.
 * 视频预览页面
 */

@ContentView(R.layout.activity_chat_video_preview)
public class VideoPreviewActivity extends BaseFragmentActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {

    @ViewInject(R.id.video)
    private SurfaceView videoSurface;

    private final int REQUEST_CODE_ASK_PERMISSIONS = 100;

    private MediaPlayer player;
    private String fileName;
    private String path;
    private static ChatView chatView;


    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void logic() {
        Intent intent = getIntent();
        fileName = intent.getStringExtra("fileName");
        if (TextUtils.isEmpty(fileName)) {
            ToastUtils.showToast("文件名错误，请重新拍摄");
            finish();
        }
        path = FileUtil.getCacheFilePath(fileName);

        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);
        player = new MediaPlayer();
        player.setOnPreparedListener(this);
        player.setLooping(true);
        try {
            player.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }


    public static void setChatView(ChatView chatView1) {
        chatView = chatView1;
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.stop();
        }
    }

    /**
     * This is called immediately after the surface is first created.
     * Implementations of this should start up whatever rendering code
     * they desire.  Note that only one thread can ever draw into
     * a {@link Surface}, so you should not draw into the Surface here
     * if your normal rendering will be in another thread.
     *
     * @param holder The SurfaceHolder whose surface is being created.
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
        player.prepareAsync();
    }

    /**
     * This is called immediately after any structural changes (format or
     * size) have been made to the surface.  You should at this point update
     * the imagery in the surface.  This method is always called at least
     * once, after {@link #surfaceCreated}.
     *
     * @param holder The SurfaceHolder whose surface has changed.
     * @param format The new PixelFormat of the surface.
     * @param width  The new width of the surface.
     * @param height The new height of the surface.
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * This is called immediately before a surface is being destroyed. After
     * returning from this call, you should no longer try to access this
     * surface.  If you have a rendering thread that directly accesses
     * the surface, you must ensure that thread is no longer touching the
     * Surface before returning from this function.
     *
     * @param holder The SurfaceHolder whose surface is being destroyed.
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.stop();
        }
    }

    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return false;
    }

    @Event(value = {R.id.back, R.id.send})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                if (this instanceof FragmentActivity) {
                    FragmentActivity fragmentActivity = (FragmentActivity) this;
                    if (requestVideo(fragmentActivity)) {
                        VideoInputDialog.show(fragmentActivity.getSupportFragmentManager());
                    }
                }
                break;
            case R.id.send:
                chatView.sendVideo(fileName);
//                ((ChatView) getActivity()).sendVideo(fileName);
                finish();
                break;
        }

    }

    private boolean requestVideo(Activity activity) {
        if (afterM()) {
            final List<String> permissionsList = new ArrayList<>();
            if ((activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.CAMERA);
            if ((activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            if (permissionsList.size() != 0) {
                activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
            int hasPermission = activity.checkSelfPermission(Manifest.permission.CAMERA);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean afterM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}
