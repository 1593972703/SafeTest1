package com.example.safetest;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);


        TextView textView = findViewById(R.id.text);
        try {
            textView.setText(getSmsInPhone());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }





    }


    @SuppressLint("LongLogTag")
    public String getSmsInPhone() {
        final String SMS_URI_ALL = "content://sms/";
        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_SEND = "content://sms/sent";
        final String SMS_URI_DRAFT = "content://sms/draft";

        StringBuilder smsBuilder = new StringBuilder();

        try {
            ContentResolver cr = getContentResolver();
            String[] projection = new String[]{"_id", "address", "person",
                    "body", "date", "type", "thread_id"};
            Uri uri = Uri.parse(SMS_URI_INBOX);
            Cursor cur = cr.query(uri, projection, null, null, "date desc");

            if (cur.moveToFirst()) {
                String name;
                String phoneNumber;
                String smsbody;
                String date;
                String type;

                int nameColumn = cur.getColumnIndex("person");
                int thread_idColumn = cur.getColumnIndex("thread_id");
                int phoneNumberColumn = cur.getColumnIndex("address");
                int smsbodyColumn = cur.getColumnIndex("body");
                int dateColumn = cur.getColumnIndex("date");
                int typeColumn = cur.getColumnIndex("type");

                do {
                    name = cur.getString(nameColumn);
                    phoneNumber = cur.getString(phoneNumberColumn);
                    smsbody = cur.getString(smsbodyColumn);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date d = new Date(Long.parseLong(cur.getString(dateColumn)));
                    date = dateFormat.format(d);

                    int typeId = cur.getInt(typeColumn);
                    if (typeId == 1) {
                        type = "接收";
                    } else if (typeId == 2) {
                        type = "发送";
                    } else {
                        type = "";
                    }

                    if (smsBuilder.toString().contains("]")) {
                        return smsBuilder.toString();
                    }

                    smsBuilder.append("[" + "\n");
//                    smsBuilder.append(name + "," + "\n");
                    smsBuilder.append("电话号码：" + phoneNumber + "," + "\n");
                    smsBuilder.append("短信内容：" + smsbody + "," + "\n");
                    smsBuilder.append("时间：" + date + "," + "\n");
//                    smsBuilder.append(type);
                    smsBuilder.append("] ");
                    if (smsbody == null) smsbody = "";
                } while (cur.moveToNext());
            } else {
                smsBuilder.append("no result!");
            }

            smsBuilder.append("getSmsInPhone has executed!");
        } catch (Exception ex) {
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();

            Log.d("SQLiteException in getSmsInPhone", ex.getMessage());
        }
        return smsBuilder.toString();
    }

    public void back(View view) {
        finish();
    }
}
