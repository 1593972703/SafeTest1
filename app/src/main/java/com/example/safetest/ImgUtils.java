package com.example.safetest;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImgUtils {

    //保存文件到指定路径

    public static boolean saveImageToGallery(Context context, Uri uriData) {

        ContentResolver resolver = context.getContentResolver();
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(resolver, uriData);
        } catch (IOException e) {
            e.printStackTrace();
        }


// 首先保存图片

        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
        String filePath = Environment.getExternalStorageDirectory().toString() + "/DCIM" + "/takePhoto.png";

        File appDir = new File(storePath);

        if (!appDir.exists()) {

            appDir.mkdir();

        }

        String fileName = System.currentTimeMillis() + ".jpg";

        File file = new File(appDir, fileName);

        try {

            FileOutputStream fos = new FileOutputStream(file);

//通过io流的方式来压缩保存图片

            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);

            fos.flush();

            fos.close();

//把文件插入到系统图库

//MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

//保存图片后发送广播通知更新数据库

            Uri uri = Uri.fromFile(file);

            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

            if (isSuccess) {

                return true;

            } else {

                return false;

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

        return false;

    }

}