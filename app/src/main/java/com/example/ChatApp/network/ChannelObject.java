package com.example.ChatApp.network;

import android.text.TextUtils;

import java.io.Serializable;

public class ChannelObject implements Serializable {

    public static int type_index = 0;
    public static int TYPE_MIC = type_index++;
    public static int TYPE_ITEM = type_index++;
    public static int TYPE_LOCK = type_index++;

    public int groupId; //그룹 ID 채널일 경우 -1
    public CharSequence groupName; //그룹명
    public int channelId; //채널 ID 그룹일경우 -1
    public CharSequence channelName; //채널명
    public CharSequence aliasChannelName; //채널명

    public int channelType; //0 mic 1 item 2 lock 3 anonymous 4 공지 5 hytube
    public int userCnt; //유저수
    public String mOpen; //모바일 공개여부(Y 공개채널 N 비공개채널)
    public String channelActive; //Y 활성 N 비활성
    public String docmSearch; //Y 그룹 N 채널

    public String sysopName;
    public int joinState = -1;
    public String memberYn;

    public boolean isOpenGroup = false;

    public int unreadCount = 0;

    //hyTUBE 익명방 추가- 2020.10.20
    public boolean isAnnonymous;//하이튜브이면서 익명이면 true
    public String sysName; //하이튜브( HYTUBE), 하이피드백(HYFB1 , HYFB2)

    //Hi-Feedback 기능추가- 2021.2.16
    public boolean isHifeedback;//하이피드백이면 true

    //검색 미노출 채널 가이드 표기 - 2021.1.26
    public String  docSearch;

    public ChannelObject(int id, CharSequence name){
        this.channelId = id;
        this.channelName = name;
    }
    //Hi-Feedback 기능추가- 2021.2.16(type, sysName 추가)
    public ChannelObject(int id, CharSequence name, int userCnt, int type, String sysName ){
        this.channelId = id;
        this.channelName = name;
        this.userCnt = userCnt;
        this.channelType = type;
        this.sysName = sysName;

        if(type == 3 && "HYTUBE".equals(this.sysName)) {
            this.isAnnonymous = true;

        }else{
            this.isAnnonymous = false;
        }

        if("HYFB1".equals(this.sysName) || "HYFB2".equals(this.sysName)) {
            this.isHifeedback = true;
        }else{
            this.isHifeedback = false;
        }
    }
    public ChannelObject(int id, CharSequence name, int userCnt){
        this.channelId = id;
        this.channelName = name;
        this.userCnt = userCnt;

    }
    public ChannelObject(int gId, String gName, int id, CharSequence name, int type){
        this.groupId = gId;
        this.groupName = gName;
        this.channelId = id;
        this.channelName = name;

        this.channelType = type;

    }

    public ChannelObject(int gId, String gName, int id, CharSequence name, int type, String sysName,
                         int userCnt, String open, String active, String search, String doc_search){
        this.groupId = gId;
        this.groupName = gName;
        this.channelId = id;
        this.channelName = name;
        this.channelType = type;

        //hyTUBE 익명방 추가- 2020.10.20
        this.sysName = sysName;
        if(type == 3 && "HYTUBE".equals(this.sysName)) {
            this.isAnnonymous = true;

        }else{
            this.isAnnonymous = false;
        }

        //Hi-Feedback 기능추가- 2021.2.16
        if("HYFB1".equals(this.sysName) || "HYFB2".equals(this.sysName)) {
            this.isHifeedback = true;
        }else{
            this.isHifeedback = false;
        }

//        if(BuildConfig.DEBUG) Common.log("D", "Hi-Feedback","channelId= " + this.channelId +", channel_type= " + this.channelType +", this.sysName="+this.sysName +","+ this.channelName +", "+this.isHifeedback);

        this.userCnt = userCnt;
        this.mOpen = open;
        this.channelActive = active;
        this.docmSearch = search;

        //검색 미노출 채널 가이드 표기 - 2021.1.26
        this.docSearch = doc_search;
    }

    public CharSequence getChannelName(){
        if(!TextUtils.isEmpty(aliasChannelName)) return aliasChannelName;
        else return channelName;
    }

    public CharSequence getSubChannelName(){
        if(!TextUtils.isEmpty(aliasChannelName)) return channelName;
        else return "";
    }

    public int getChannelId() {
        return channelId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("class instance = ").append(this.getClass().getSimpleName()).append(", ");
        sb.append("groupId = ").append(groupId).append(", ");
        sb.append("groupName = ").append(groupName).append(", ");
        sb.append("channelId = ").append(channelId).append(", ");
        sb.append("channelName = ").append(channelName).append(", ");
        sb.append("channelType = ").append(channelType).append(", ");
        sb.append("sysName = ").append(sysName).append(", ");
        sb.append("isAnnonymous = ").append(isAnnonymous).append(", ");
        sb.append("isHifeedback = ").append(isHifeedback).append(", ");
        sb.append("userCnt = ").append(userCnt).append(", ");
        sb.append("mOpen = ").append(mOpen).append(", ");
        sb.append("channelActive = ").append(channelActive).append(", ");
        sb.append("docmSearch = ").append(docmSearch).append(", ");
        sb.append("unreadCount = ").append(unreadCount).append(", ");
        sb.append("docSearch = ").append(docSearch);
        return sb.toString();
    }
}
