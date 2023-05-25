package com.example.safetest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;


public class PhoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        TextView tv = findViewById(R.id.text);

        getNumber(tv);
    }

    private void getNumber(TextView tv) {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String phoneNumber1 = tm.getLine1Number();
        // String phoneNumber2 = tm.getGroupIdLevel1();
        tv.setText("本机号码是：" + "   " + phoneNumber1);
    }

    public void back(View view) {
        finish();
    }
}
