package com.example.ChatApp.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.io.File;

public class PrefManager {
    private SharedPreferences mPreference;
    private static PrefManager instance;

    public PrefManager(Context context){
        if(mPreference == null){
            mPreference = context.getSharedPreferences(Common.KEY_PREFERENCE, context.MODE_PRIVATE);
        }
    }

    public static synchronized PrefManager getInstance(Context context){
        if(instance == null){
            instance = new PrefManager(context);
        }
        return instance;
    }


    public synchronized boolean  resetPrefManager(Context context) {
        boolean rslt = false;
        clear();
        mPreference = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            rslt = context.deleteSharedPreferences(Common.KEY_PREFERENCE);
        } else {
            try {
                File _file = new File("/data/data/"+context.getPackageName() +"/shared_prefs/"+Common.KEY_PREFERENCE+".xml");
                if (_file.exists()){
                    _file.delete();
                    rslt = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mPreference = context.getSharedPreferences(Common.KEY_PREFERENCE, context.MODE_PRIVATE);
        return rslt;
    }

    public boolean putValue(String key, int value) {
        if (mPreference != null) {
            SharedPreferences.Editor edit = mPreference.edit();
            edit.putInt(key, value);
            return edit.commit();
        } else {
            return false;
        }
    }

    public int getValue(String key, int defualValue) {
        if (mPreference != null) {
            return mPreference.getInt(key, defualValue);
        } else {
            return -1;
        }
    }

    public boolean putValue(String key, String value) {
        if (mPreference != null) {
            boolean rslt = false;
            SharedPreferences.Editor edit = mPreference.edit();
            edit.putString(key, value);
            rslt = edit.commit();

            if (!rslt) {

            }
            return rslt;
        } else {
            return false;
        }
    }

    public String getValue(String key, String defualValue) {
        if (mPreference != null) {
            return mPreference.getString(key, defualValue);
        } else {
            return "";
        }
    }
    public boolean putValue(String key, boolean value) {
        if (mPreference != null) {
            SharedPreferences.Editor edit = mPreference.edit();
            edit.putBoolean(key, value);
            return edit.commit();
        } else {
            return false;
        }
    }

    public boolean getValue(String key, boolean defualValue) {
        if (mPreference != null) {
            return mPreference.getBoolean(key, defualValue);
        } else {
            return false;
        }
    }

    public boolean remove(String key){
        if (mPreference != null) {
            SharedPreferences.Editor edit = mPreference.edit();
            edit.remove(key);
            return edit.commit();
        } else {
            return false;
        }
    }

    public boolean clear() {
        if (mPreference != null) {
            SharedPreferences.Editor edit = mPreference.edit();
            edit.clear();
            return edit.commit();
        } else {
            return false;
        }
    }
}
