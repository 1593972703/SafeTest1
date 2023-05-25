package com.example.safetest;

import android.annotation.SuppressLint;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class BlueToothActivity extends AppCompatActivity {

    private Bitmap myBitmap;
    String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        ImageView image = findViewById(R.id.image);
        String filePath = Environment.getExternalStorageDirectory().toString() + "/DCIM" + "/screenshot.png";
        myBitmap = getLoacalBitmap(filePath); //从本地取图片(在cdcard中获取)  //
        image.setImageBitmap(myBitmap);


        Button btnShare = findViewById(R.id.btn_share);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        if (!TextUtils.isEmpty(type)) {
            btnShare.setText("文件分享");
        }
    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void share(View view) {
        if (TextUtils.isEmpty(type)) {
            blueToothSendFile();
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), myBitmap, "IMG" + Calendar.getInstance().getTime(), null));
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, "title"));
        }


    }

    private void blueToothSendFile() {
        try {
            Intent localIntent = null;
            localIntent = new Intent();
            localIntent.setAction(Intent.ACTION_SEND);
            String path = Environment.getExternalStorageDirectory().toString() + "/DCIM" + "/screenshot.png";

            File tempfiles = new File(path);
            if (!tempfiles.exists()) {
                return;
            }
//            Uri contentUri = FileProvider.getUriForFile(Objects.requireNonNull(this).getApplicationContext(), "com.example.xiao.fileprovider", tempfiles);
            localIntent.setType("image/*");
            localIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            localIntent.setPackage("com.android.bluetooth");

            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), myBitmap, "IMG" + Calendar.getInstance().getTime(), null));
            localIntent.putExtra(Intent.EXTRA_STREAM, uri);

            startActivityForResult(localIntent, 9527);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void back(View view) {
        finish();
    }
}


