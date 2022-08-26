package com.example.ChatApp.network;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefChatMessageManager {
    private SharedPreferences mPreference;
    private static PrefChatMessageManager instance;

    public PrefChatMessageManager(Context context){
        if(mPreference == null){
            mPreference = context.getSharedPreferences(Common.KEY_PREFERENCE + ".chatmessage", context.MODE_PRIVATE);

        }
    }

    public static synchronized PrefChatMessageManager getInstance(Context context){
        if(instance == null){
            instance = new PrefChatMessageManager(context);
        }
        return instance;
    }



    public boolean putMessage(String key, String value) {
        SharedPreferences.Editor edit = mPreference.edit();
        edit.putString(key, value);
        return edit.commit();
    }

    public String  getMessage(String key, String defualValue) {
        return mPreference.getString(key, defualValue);
    }

    public boolean remove(String key){
        SharedPreferences.Editor edit = mPreference.edit();
        edit.remove(key);
        return edit.commit();
    }

    public boolean clear() {
        SharedPreferences.Editor edit = mPreference.edit();
        edit.clear();
        return edit.commit();
    }
}
