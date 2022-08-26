package com.example.ChatApp.network;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by kb-20 on 2017-04-04.
 */

public class LanguageUtil {

    /**
     * 언어 저장 (ko, en)
     * @return
     */
    public static void saveLanguage(Context context, String lauguageValue){
        if(!TextUtils.isEmpty(lauguageValue)) {
            PrefManager.getInstance(context).putValue(Common.PREF_LANGUAGE, lauguageValue);
        }else{
            PrefManager.getInstance(context).remove(Common.PREF_LANGUAGE);
        }
    }

    /**
     * 현재 언어 반환
     * @return
     */
    public static String loadLanguage(Context context){
        String currentLang = PrefManager.getInstance(context).getValue(Common.PREF_LANGUAGE, "");
        if(TextUtils.isEmpty(currentLang)){
            currentLang = getSystemLanguage();
        }
        return currentLang;
    }

    /**
     * 시스템 언어 반환
     * @return
     */
    public static String getSystemLanguage(){

        Locale locale = Locale.getDefault()/*AppStarter.applicationContext.getResources().getConfiguration().locale*/;
        String language = locale.getLanguage();

        return language;
    }

    /**
     * 언어 설정
     * @param language
     */
    public static void setAppLanguage(Context context, String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.createConfigurationContext(config);

        ((Activity) context).getBaseContext().getResources().updateConfiguration(config,
                ((Activity) context).getBaseContext().getResources().getDisplayMetrics());
    }

    public static void init(Context context) {
        String currentLang = loadLanguage(context);
        setAppLanguage(context, currentLang);
    }

    public int getSystemLanguageTypeIndex(){
        String language = getSystemLanguage();
        switch (language){
            case "ko":
                return 0;
            case "jp":
                return 1;
            case "en":
                return 2;
            case "zh":
                return 3;
        }
        return 2;
    }

    public int getAppLanguageTypeIndex(Context context){
        String language = loadLanguage(context);
        switch (language){
            case "ko":
                return 0;
            case "jp":
                return 1;
            case "en":
                return 2;
            case "zh":
                return 3;
        }
        return 2;
    }

    public String getLocalLanguageString(ArrayList<String> langList) {
        int langTypeNumer = getSystemLanguageTypeIndex();
        if(langList != null && langList.size() > langTypeNumer) return langList.get(langTypeNumer);

        return "";
    }

}
