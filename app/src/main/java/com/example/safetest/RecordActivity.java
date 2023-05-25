package com.example.safetest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safetest.mediaprojection.interfaces.MediaProjectionNotificationEngine;
import com.example.safetest.mediaprojection.interfaces.MediaRecorderCallback;
import com.example.safetest.mediaprojection.utils.MediaProjectionHelper;
import com.example.safetest.screen.NotificationHelper;

import java.io.File;


public class RecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initData();
        ImageView img = findViewById(R.id.img);

        //动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.img_animation);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
        img.startAnimation(animation);

        findViewById(R.id.btn_start).setOnClickListener(view -> doServiceStart());

        findViewById(R.id.btn_end).setOnClickListener(view -> doServiceStop());

    }


    public void back(View view) {
        finish();
    }

    private void initData() {
        MediaProjectionHelper.getInstance().setNotificationEngine(() -> {
            String title = getString(R.string.service_start);
            return NotificationHelper.getInstance().createSystem()
                    .setOngoing(true)// 常驻通知栏
                    .setTicker(title)
                    .setContentText(title)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .build();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MediaProjectionHelper.getInstance().createVirtualDisplay(requestCode, resultCode, data, true, true);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Uri data1 = data.getData();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data1));

                //拿到视频保存地址
                String s = data1.toString();
                String[] split = s.split(":");
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }
    }

    /**
     * 启动媒体投影服务
     */
    private void doServiceStart() {
        MediaProjectionHelper.getInstance().startService(this);
    }

    /**
     * 停止媒体投影服务
     */
    private void doServiceStop() {
        MediaProjectionHelper.getInstance().stopService(this);
    }


    /**
     * 开始屏幕录制
     */
    public void doMediaRecorderStart() {
        MediaProjectionHelper.getInstance().startMediaRecorder(new MediaRecorderCallback() {
            @Override
            public void onSuccess(File file) {
                super.onSuccess(file);

                LogUtil.i("MediaRecorder onSuccess: " + file.getAbsolutePath());
                Toast.makeText(getApplication(), getString(R.string.content_media_recorder_result, file.getAbsolutePath()), Toast.LENGTH_LONG).show();
//                Uri fileUri = FileProvider.getUriForFile(
//                        MainActivity.this,
//                        getPackageName() + ".fileprovider",
//                        file);
                Uri fileUri = Uri.fromFile(file);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));

            }

            @Override
            public void onFail() {
                super.onFail();

                LogUtil.e("MediaRecorder onFail");
            }
        });
    }

    /**
     * 停止屏幕录制
     */
    public void doMediaRecorderStop() {
        MediaProjectionHelper.getInstance().stopMediaRecorder();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        doServiceStop();
    }
}
