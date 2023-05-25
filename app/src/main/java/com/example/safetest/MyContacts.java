package com.example.safetest;

/**
 * Created by Administrator on 2019/6/21.
 */

public class MyContacts {
    public String id;
    public String name;
    public String phone;
    public String note;

    @Override
    public String toString() {
        return "{\"name\":" + "\""+name+"\""+ "\"id\":"+"\""+id+"\""+","+"\"phone\":"+"\""+phone+"\"}";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


}
