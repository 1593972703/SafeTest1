package com.example.safetest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SpUtil {
  //private static SpUtil mSpUtil;
  private SharedPreferences mSp;

  //private  Context mContext;
  private SpUtil() {
  }

  //  private SpUtil(Context con) {
//    if (mSp == null) {
//      mSp = PreferenceManager
//        .getDefaultSharedPreferences(con);
//
//    }
//  }
  public static class SpUtilHolder {
    private static final SpUtil instance = new SpUtil();

    public static SpUtil getInstance() {
      return SpUtilHolder.instance;
    }
  }


  public void init(Context context) {
    if (mSp == null) {
      mSp = PreferenceManager
       .getDefaultSharedPreferences(context);
    }
  }

//  public static SpUtil instance(Context con) {
//
//    if (mSpUtil == null) {
//      synchronized (con) {
//        if (mSpUtil == null) {
//          mSpUtil = new SpUtil(con);
//        }
//      }
//    }
//    return mSpUtil;
//  }

  public void setBoolean(String key, boolean value) {
    mSp.edit().putBoolean(key, value).apply();

  }

  public boolean getBoolean(String key, boolean defValue) {
    return mSp.getBoolean(key, defValue);
  }

  public boolean setString(String key, String value) {
    return mSp.edit().putString(key, value).commit();
  }

  public String getString(String key, String defValue) {
    return mSp.getString(key, defValue);
  }

  public void setLong(String key, long value) {
    mSp.edit().putLong(key, value).apply();
  }

  public long getLong(String key, long defValue) {
    return mSp.getLong(key, defValue);
  }

  public void setInt(String key, int value) {
    mSp.edit().putInt(key, value).apply();
  }

  public int getInt(String key, int defValue) {
    return mSp.getInt(key, defValue);
  }

  public void remove(String key) {
    mSp.edit().remove(key).apply();
  }

  public void removeAll() {
    mSp.edit().clear().apply();
  }
}