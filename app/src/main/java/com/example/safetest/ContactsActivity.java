package com.example.safetest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {


    private List<String> list = new ArrayList<>();
    private List<MyContacts> myContactsList = new ArrayList<>();
    private int type;
    EditText editText;
    Button button;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        editText = findViewById(R.id.edit);
        button = findViewById(R.id.button);


        try {
            type = getIntent().getIntExtra("type", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        editText.setVisibility(type == 1 ? View.VISIBLE : View.GONE);
        button.setVisibility(type == 1 ? View.VISIBLE : View.GONE);
        if (type != 1) {

            myContactsList = ContactUtils.getAllContacts(this);
            for (MyContacts myContacts : myContactsList) {
                String cons = type == 1 ? myContacts.name + ":  " + myContacts.phone : myContacts.name;
                list.add(cons);
            }
            ListView listView = findViewById(R.id.listView);
            listView.setVisibility(type == 1 ? View.GONE : View.VISIBLE);

            ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, list);
            listView.setAdapter(stringArrayAdapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {

                if (type == 2) {
                    Intent intent = new
                            Intent(Intent.ACTION_EDIT, Uri.parse("content://com.android.contacts/contacts/" + myContactsList.get(position).id));
                    startActivity(intent);
                } else if (type == 1) {
                    boolean granted = XXPermissions.isGranted(this, Permission.CALL_PHONE);
                    if (!granted) {
                        XXPermissions.with(this).permission(Permission.CALL_PHONE).request(null);
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + myContactsList.get(position).phone));
                    startActivity(intent);
                }

            });
        }

    }

    public void back(View view) {
        finish();
    }

    @SuppressLint("MissingPermission")
    public void call(View view) {
        EditText editText = findViewById(R.id.edit);
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + editText.getText().toString()));
        boolean granted = XXPermissions.isGranted(this, Permission.CALL_PHONE);
        if (!granted) {
            XXPermissions.with(this).permission(Permission.CALL_PHONE).request(null);
            return;
        }
        startActivity(intent);
    }
}
