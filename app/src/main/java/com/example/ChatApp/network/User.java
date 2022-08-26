package com.example.ChatApp.network;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.android.datatransport.backend.cct.BuildConfig;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {

    public int userId;
    public String userName;
    public String uniqueName;
    public String positionName;
    public String deptCode;
    public String deptName;
    public String mobilePhone;
    public String officePhone;
    public String email;
    public String companyCode;
    public String companyName;
    public int accessLevel;
    public String lastChannelId;
    public int languageType;
    public int requestId;

    public Bitmap profileImg;
    public boolean isOnline = false;
    public boolean isAlarm = false;
    public boolean isNew = false;

    private String status;
    public int statusWeb;
    public int statusPc;
    public int statusAndroid;
    public int statusIOS;
    public int statusAndroidTablet;
    public int statusIOSTablet;
    public String mobileIcon;
    public String NotificationSound;

    public String translationLanguageType;
    public String translationSourceLanguageType;
    public String isShowWriting;
    public String backYN;
    public String JobRole = "";

    //PL,팀장 표기 (개인프로필 팝업) - 2021.2.17(jpstnNam 추가)
    public String jpstnNam;

    //근무조 정보 추가 - 2021.2.18(wgrpNam 추가)
    public String wgrpNam;

    public int messageedittime;

    //일정검색 개선 - 2020.10.6 (추가:채널 일정 검색 기간)
    public int ewsdays = 1;

    public ArrayList<HashMap<String, String>> security;


    public User(int id, String name, boolean online){
        userId = id;
        userName = name;
        isOnline = online;
    }

    public User(int id, String name, String uniqueName,
                String positionName, String deptCode, String deptName, String mobilePhone, String officePhone, String email,
                String companyCode, String companyName, int accessLevel, String lastChannelId, int languageType, String jpstnNam, String wgrpNam){
        this.userId = id;
        this.userName = name;
        this.uniqueName = uniqueName;
        this.positionName = positionName;
        this.deptCode = deptCode;
        this.deptName = deptName;
        this.mobilePhone = mobilePhone;
        this.officePhone = officePhone;
        this.email = email;
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.accessLevel = accessLevel;
        this.lastChannelId = lastChannelId;
        this.languageType = languageType;

        //PL,팀장 표기 (개인프로필 팝업) - 2021.2.17(jpstnNam 추가)
        this.jpstnNam = jpstnNam.trim();
        //근무조 정보 추가 - 2021.2.18(wgrpNam 추가)
        this.wgrpNam = wgrpNam;

        if(BuildConfig.DEBUG) Common.log("D", "User","jpstnNam= " + this.jpstnNam  + ", wgrpNam= " + this.wgrpNam);

    }

    //프로필 조회시
    public User(int id, String name, String uniqueName,
                String positionName, String deptCode, String deptName, String mobilePhone, String officePhone, String email,
                String companyCode, String companyName, int accessLevel, String lastChannelId, int languageType,
                ArrayList<HashMap<String, String>> security, String translationLanguageType, String translationSourceLanguageType, String isShowWriting, String backYN, int messageedittime, String ewsdays, String jpstnNam, String wgrpNam){
        this.userId = id;
        this.userName = name;
        this.uniqueName = uniqueName;
        this.positionName = positionName;
        this.deptCode = deptCode;
        this.deptName = deptName;
        this.mobilePhone = mobilePhone;
        this.officePhone = officePhone;
        this.email = email;
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.accessLevel = accessLevel;
        this.lastChannelId = lastChannelId;
        this.languageType = languageType;
        this.security = security;
        this.translationLanguageType = translationLanguageType;
        this.translationSourceLanguageType = translationSourceLanguageType;
        this.isShowWriting = isShowWriting;
        this.backYN = backYN;
        this.messageedittime = messageedittime;
        //일정검색 개선 - 2020.10.6 (추가:채널 일정 검색 기간)
        if(!TextUtils.isEmpty(ewsdays)) this.ewsdays = Integer.parseInt(ewsdays);

        //PL,팀장 표기 (개인프로필 팝업) - 2021.2.17(jpstnNam 추가)
        this.jpstnNam = jpstnNam.trim();
        //근무조 정보 추가 - 2021.2.18(wgrpNam 추가)
        this.wgrpNam = wgrpNam;

        if(BuildConfig.DEBUG) Common.log("D", "User","jpstnNam= " + this.jpstnNam  + ", wgrpNam= " + this.wgrpNam);
    }


    public User(int id, String name, String uniqueName,
                String positionName, String deptCode, String deptName, String mobilePhone, String officePhone, String email,
                int accessLevel, String lastChannelId, String isAlarmYn, int isNew, String status, String mobileIcon, String jpstnNam, String wgrpNam){
        this.userId = id;
        this.userName = name;
        this.uniqueName = uniqueName;
        this.positionName = positionName;
        this.deptCode = deptCode;
        this.deptName = deptName;
        this.mobilePhone = mobilePhone;
        this.officePhone = officePhone;
        this.email = email;
        this.accessLevel = accessLevel;
        this.lastChannelId = lastChannelId;
        this.isAlarm = isAlarmYn.equals("Y")?true:false;
        this.isNew = isNew!=0?true:false;
        this.status = status;
        if(status.length()>=4){
            statusWeb = Integer.parseInt(status.substring(0, 1));
            statusPc = Integer.parseInt(status.substring(1, 2));
            statusAndroid = Integer.parseInt(status.substring(2, 3));
            statusIOS = Integer.parseInt(status.substring(3, 4));
            if(status.length()>=6){
                statusAndroidTablet = Integer.parseInt(status.substring(4, 5));
                statusIOSTablet = Integer.parseInt(status.substring(5, 6));
            }
        }
        this.mobileIcon = mobileIcon;

        //PL,팀장 표기 (개인프로필 팝업) - 2021.2.17(jpstnNam 추가)
        this.jpstnNam = jpstnNam.trim();

        //근무조 정보 추가 - 2021.2.18(wgrpNam 추가)
        this.wgrpNam = wgrpNam;

        if(BuildConfig.DEBUG) Common.log("D", "User","jpstnNam= " + this.jpstnNam  + ", wgrpNam= " + this.wgrpNam);
    }

    public void setStatus(String status){
        if(status.length()>=4){
            statusWeb = Integer.parseInt(status.substring(0, 1));
            statusPc = Integer.parseInt(status.substring(1, 2));
            statusAndroid = Integer.parseInt(status.substring(2, 3));
            statusIOS = Integer.parseInt(status.substring(3, 4));
            if(status.length()>=6){
                statusAndroidTablet = Integer.parseInt(status.substring(4, 5));
                statusIOSTablet = Integer.parseInt(status.substring(5, 6));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("userId = ").append(userId).append("\n");
        sb.append("userName = ").append(userName).append("\n");
        sb.append("jpstnNam = ").append(jpstnNam).append("\n");
        sb.append("uniqueName = ").append(uniqueName).append("\n");
        sb.append("positionName = ").append(positionName).append("\n");
        sb.append("deptCode = ").append(deptCode).append("\n");
        sb.append("deptName = ").append(deptName).append("\n");
        sb.append("mobilePhone = ").append(mobilePhone).append("\n");
        sb.append("wgrpNam = ").append(wgrpNam).append("\n");
        sb.append("officePhone = ").append(officePhone).append("\n");
        sb.append("email = ").append(email).append("\n");
        sb.append("companyCode = ").append(companyCode).append("\n");
        sb.append("companyName = ").append(companyName).append("\n");
        sb.append("accessLevel = ").append(accessLevel).append("\n");
        sb.append("lastChannelId = ").append(lastChannelId).append("\n");
        sb.append("languageType = ").append(languageType).append("\n");
        sb.append("isAlarm = ").append(isAlarm).append("\n");
        sb.append("isNew = ").append(isNew).append("\n");
        sb.append("status = ").append(status).append("\n");
        sb.append("mobileIcon = ").append(mobileIcon).append("\n");
        sb.append("ewsdays = ").append(ewsdays);

        return sb.toString();
    }

    public String getLastChannelId() {
        return lastChannelId;
    }

}

