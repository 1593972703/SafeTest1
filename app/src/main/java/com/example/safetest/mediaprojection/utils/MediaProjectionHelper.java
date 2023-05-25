package com.example.safetest.mediaprojection.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.example.safetest.LogUtil;
import com.example.safetest.MainActivity;
import com.example.safetest.R;
import com.example.safetest.RecordActivity;
import com.example.safetest.mediaprojection.interfaces.MediaProjectionNotificationEngine;
import com.example.safetest.mediaprojection.interfaces.MediaRecorderCallback;
import com.example.safetest.mediaprojection.interfaces.ScreenCaptureCallback;
import com.example.safetest.mediaprojection.service.MediaProjectionService;

import java.io.File;


/**
 * 媒体投影 帮助类
 * Created by lishilin on 2020/03/18
 */
public class MediaProjectionHelper {

    public static final int REQUEST_CODE = 10086;

    private static class InstanceHolder {
        private static final MediaProjectionHelper instance = new MediaProjectionHelper();
    }

    public static MediaProjectionHelper getInstance() {
        return InstanceHolder.instance;
    }

    private MediaProjectionHelper() {
        super();
    }

    private MediaProjectionNotificationEngine notificationEngine;

    private MediaProjectionManager mediaProjectionManager;
    private DisplayMetrics displayMetrics;

    private ServiceConnection serviceConnection;
    private MediaProjectionService mediaProjectionService;

    /**
     * 设置 通知引擎
     *
     * @param notificationEngine notificationEngine
     */
    public void setNotificationEngine(MediaProjectionNotificationEngine notificationEngine) {
        this.notificationEngine = notificationEngine;
    }

    /**
     * 启动媒体投影服务
     *
     * @param activity activity
     */
    public void startService(RecordActivity activity) {
        boolean isStart = false;
        if (mediaProjectionManager != null) {
            return;
        }

        // 启动媒体投影服务
        mediaProjectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mediaProjectionManager != null) {
            activity.startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
        }

        // 此处宽高需要获取屏幕完整宽高，否则截屏图片会有白/黑边
        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);

        // 绑定服务
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.e("TAG", "onServiceConnected: " );
                if (service instanceof MediaProjectionService.MediaProjectionBinder) {
                    mediaProjectionService = ((MediaProjectionService.MediaProjectionBinder) service).getService();
                    mediaProjectionService.setNotificationEngine(notificationEngine);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.doMediaRecorderStart();

                        }
                    },3000);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e("TAG", "onServiceDisconnected: " );

                mediaProjectionService = null;
                activity.doMediaRecorderStop();

            }
        };
        MediaProjectionService.bindService(activity, serviceConnection);
    }


    /**
     * 停止媒体投影服务
     *
     * @param context context
     */
    public void stopService(Context context) {
        mediaProjectionService = null;

        if (serviceConnection != null) {
            MediaProjectionService.unbindService(context, serviceConnection);
            serviceConnection = null;
        }

        displayMetrics = null;

        mediaProjectionManager = null;
    }





    /**
     * 创建VirtualDisplay(onActivityResult中调用)
     *
     * @param requestCode           requestCode
     * @param resultCode            resultCode
     * @param data                  data
     * @param isScreenCaptureEnable 是否可以屏幕截图
     * @param isMediaRecorderEnable 是否可以媒体录制
     */
    public void createVirtualDisplay(int requestCode, int resultCode, Intent data, boolean isScreenCaptureEnable, boolean isMediaRecorderEnable) {
        if (mediaProjectionService == null) {
            return;
        }
        if (requestCode != REQUEST_CODE) {
            return;
        }
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        mediaProjectionService.createVirtualDisplay(resultCode, data, displayMetrics, isScreenCaptureEnable, isMediaRecorderEnable);
    }

    /**
     * 屏幕截图
     *
     * @param callback callback
     */
    public void capture(ScreenCaptureCallback callback) {
        if (mediaProjectionService == null) {
            callback.onFail();
            return;
        }
        mediaProjectionService.capture(callback);
    }

    /**
     * 开始 屏幕录制
     *
     * @param callback callback
     */
    public void startMediaRecorder(MediaRecorderCallback callback) {
        if (mediaProjectionService == null) {
            Log.e("TAG", "startMediaRecorder: " + null );
            callback.onFail();
            return;
        }
        mediaProjectionService.startRecording(callback);
    }

    /**
     * 停止 屏幕录制
     */
    public void stopMediaRecorder() {
        if (mediaProjectionService == null) {
            return;
        }
        mediaProjectionService.stopRecording();
    }

}
