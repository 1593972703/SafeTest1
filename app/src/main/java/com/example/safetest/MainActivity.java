package com.example.safetest;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private Button btnRecord, btnScreen;
    private TextView tvPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRecord = findViewById(R.id.btn_record);
        btnScreen = findViewById(R.id.btn_screen);
        tvPic = findViewById(R.id.tv_pic);


        btnRecord.setOnClickListener(v -> {
            startActivity(new Intent(this, RecordActivity.class));
        });

        TextView textView = findViewById(R.id.text);
        textView.setText("当前app版本号：" + BuildConfig.VERSION_NAME);

        SpUtil instance = SpUtil.SpUtilHolder.getInstance();
        instance.init(this);


//        if (instance.getBoolean("isFirst", true)) {
//            instance.setBoolean("isFirst", false);
//            Intent intent = new Intent();
//            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            Uri uri = Uri.fromParts("package", getPackageName(), null);
//            intent.setData(uri);
//            startActivity(intent);
//        }

//        getPermission();


    }

    private void getPermission() {
        XXPermissions.with(this)
                // 申请多个权限
//                .permission(Permission.Group.CONTACTS)
                .permission(Permission.Group.STORAGE)

                .permission(Permission.CALL_PHONE)
                .permission(Permission.SEND_SMS)
                .permission(Permission.RECEIVE_SMS)
                .permission(Permission.READ_SMS)
                .permission(Permission.READ_CALL_LOG)
                .permission(Permission.WRITE_CALL_LOG)
                .permission(Permission.READ_PHONE_STATE)
                .permission(Permission.ACCESS_FINE_LOCATION)
                .permission(Permission.READ_PHONE_NUMBERS)

//                .permission(Permission.BLUETOOTH_CONNECT)
//                .permission(Permission.BLUETOOTH_SCAN)
//                .permission(Permission.BLUETOOTH_ADVERTISE)
//                .permission(Permission.READ_CONTACTS)
//                .permission(Permission.WRITE_CONTACTS)
//
//                .permission(Permission.WRITE_EXTERNAL_STORAGE)
//                .permission(Permission.READ_EXTERNAL_STORAGE)
//                .permission(Permission.CAMERA)
//                .permission(Permission.WRITE_SETTINGS)
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            toastStr("获取部分权限成功，但部分权限未正常授予");
                            return;
                        }
                        toastStr("获取录音和日历权限成功");
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        if (doNotAskAgain) {
                            toastStr("被永久拒绝授权，请手动授予录音和日历权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(MainActivity.this, permissions);
                        } else {
                            toastStr("获取录音和日历权限失败");
                        }
                    }
                });
    }


    public void screenshot(View v) { //截屏

        boolean granted = XXPermissions.isGranted(this, Permission.Group.STORAGE);
        if (!granted) {
            XXPermissions.with(this).permission(Permission.Group.STORAGE).request(null);
            return;
        }
        try {
            // 获取当前手机窗口
            View view = getWindow().getDecorView().getRootView();
            // 创建Bitmap对象，并设置宽高和截图方式
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            // 将view绘制到bitmap上
            view.draw(canvas);
            // 保存截图到指定目录下
            String filePath = Environment.getExternalStorageDirectory().toString() + "/DCIM" + "/screenshot.png";
            FileOutputStream fos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            // 发送广播，通知系统刷新相册
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(filePath));
            intent.setData(uri);
            sendBroadcast(intent);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "截图完成", Toast.LENGTH_SHORT).show();
                }
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void deletePhoneRecord(View v) {

        boolean granted = XXPermissions.isGranted(this, Permission.WRITE_CALL_LOG);
        if (!granted) {
            XXPermissions.with(this).permission(Permission.WRITE_CALL_LOG).request(null);
            return;
        }
        int delete = getContentResolver().delete(CallLog.Calls.CONTENT_URI, null, null);//删除通话记录
        if (delete != -1) {
            toastStr("删除通话记录成功");
        }


    }

    public void blueTooth(View v) { //蓝牙
        startActivity(new Intent(this, BlueToothActivity.class));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1111:
                String filePath = Environment.getExternalStorageDirectory().getPath() + "/DCIM" + "/takePhoto.png";
//                Uri fileUri = FileProvider.getUriForFile(
//                        this,
//                        getPackageName() + ".fileprovider",
//                        new File(filePath));
                Uri fileUri = Uri.fromFile(new File(filePath));
//                保存图片后发送广播通知更新数据库
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));
                toastStr("拍照完成");
                break;
        }


    }


    private void toastStr(String str) {
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();

    }


    public void takePhoto(View view) {  //相机
        boolean granted = XXPermissions.isGranted(this, Permission.WRITE_EXTERNAL_STORAGE, Permission.CAMERA);
        if (!granted) {
            XXPermissions.with(this).permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.CAMERA).request(null);
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/DCIM" + "/takePhoto.png";
        Uri fileUri = null;


        fileUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".fileprovider",
                new File(filePath));
//        fileUri = Uri.fromFile(new File(filePath)); // create a file to save the video
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, 1111);
    }


    public void copy(View view) {//复制文字
        // 从 API11 开始 android 推荐使用 android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的 android.text.ClipboardManager，虽然提示 deprecated，但不影响使用。
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText("测试被复制的文字");
        toastStr("已复制到剪切板");
    }


    public void fileShare(View view) {
        Intent intent = new Intent(this, BlueToothActivity.class);
        intent.putExtra("type", "文件");
        startActivity(intent);
    }


    public void sendMessage(View view) { //发送短信

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android-dir/mms-sms");
        startActivity(intent);
    }

    public void getMessage(View view) {//获取短信
        boolean granted = XXPermissions.isGranted(this, Permission.READ_SMS);
        if (!granted) {
            XXPermissions.with(this).permission(Permission.READ_SMS).request(null);
            return;
        }
        startActivity(new Intent(this, SmsActivity.class));
    }

    public void goWeb(View view) {
        startActivity(new Intent(this, WebActivity.class));
    }

    public void getGPS(View view) {
        boolean granted = XXPermissions.isGranted(this, Permission.ACCESS_FINE_LOCATION);
        if (!granted) {
            XXPermissions.with(this).permission(Permission.ACCESS_FINE_LOCATION).request(null);
            return;
        }
        startActivity(new Intent(this, MapActivity.class));
    }

    public void getCall(View view) {//获取通讯录信息
        boolean granted = XXPermissions.isGranted(this, Permission.READ_CONTACTS, Permission.READ_CALL_LOG);
        if (!granted) {
            XXPermissions.with(this).permission(Permission.READ_CONTACTS, Permission.READ_CALL_LOG).request(null);
            return;
        }
        startActivity(new Intent(this, ContactsActivity.class));

    }

    public void call(View view) {//拨打电话
        Intent intent = new Intent(this, ContactsActivity.class);
        intent.putExtra("type", 1);
        startActivity(intent);

    }

    public void editPhoneRecord(View view) { //编辑通话记录
        Intent intent = new Intent(this, ContactsActivity.class);
        intent.putExtra("type", 2);
        startActivity(intent);
    }

    public void deleteSms(View view) {

        final String myPackageName = getPackageName();
        Log.d("TAG", "onCreate: " + myPackageName);
        Log.d("TAG", "onCreate: " + Telephony.Sms.getDefaultSmsPackage(this));
        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
            startActivity(intent);
            Log.d("liueg", "11111111111");
        } else {
            long id = getSmsInPhone();
            Uri mUri = Uri.parse("content://sms/" + id);
            int delete = getContentResolver().delete(mUri, null, null);
            if (delete > 0) {
                toastStr("短信删除成功");
            }

        }

    }

    public long getSmsInPhone() {
        long threadId = 0;

        final String SMS_URI_ALL = "content://sms/";
        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_SEND = "content://sms/sent";
        final String SMS_URI_DRAFT = "content://sms/draft";


        try {
            ContentResolver cr = getContentResolver();
            String[] projection = new String[]{"_id", "address", "person",
                    "body", "date", "type", "thread_id"};
            Uri uri = Uri.parse(SMS_URI_INBOX);
            Cursor cur = cr.query(uri, projection, null, null, "date desc");

            assert cur != null;
            if (cur.moveToFirst()) {
                threadId = cur.getInt(0);
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();

        }
        return threadId;
    }


    public void goWeb4G(View view) {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("type", "移动网络");
        startActivity(intent);
    }

    public void goWebWifi(View view) {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("type", "WIFI");
        startActivity(intent);
    }

    public void getPhone(View view) {
        startActivity(new Intent(this, PhoneActivity.class));
    }


    public void downPic(View view) {

        String url = "https://t7.baidu.com/it/u=1699454570,2952675447&fm=85&app=131&size=f242,150&n=0&f=JPEG&fmt=auto?s=1BF6E816C8B47F800B7547C402007026&sec=1682614800&t=9d9b1f27709eb86366ecb059682e339f";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        request.setDestinationInExternalPublicDir("/DCIM", "download.png");
        request.setDestinationInExternalFilesDir(this, "", "download.png");
        request.
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                );

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                String filePath = getExternalFilesDir(null).getAbsolutePath().toString() + "/download.png";
                toastStr("图片下载完成 路径：" + filePath);
                tvPic.setText("图片路径：" + filePath);
                Log.d("TAG", "run: " + filePath);
                Uri fileUri = FileProvider.getUriForFile(
                        MainActivity.this,
                        getPackageName() + ".fileprovider",
                        new File(filePath));

//                Uri fileUri = Uri.fromFile(new File(filePath));
//                保存图片后发送广播通知更新数据库
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));

//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));


            }
        }, 2000);
    }


}