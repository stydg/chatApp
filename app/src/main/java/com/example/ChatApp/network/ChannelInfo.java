package com.example.ChatApp.network;

import android.text.TextUtils;

import java.util.ArrayList;

public class ChannelInfo extends DMChannel {

    public String channelIntro;
    //채널 공지 개선 - 2020.12.3
//    public String channelNotice;
    public ArrayList<String> channelNotice;
    public String sysopName;
    public ArrayList<Integer> sysopIds;
    public String members;
    public boolean nick_check;
    public String dmType;

    public String alarmYN;
    public String memberAddYn;
    public boolean chatbot;
    public boolean hrbot;
    public boolean isFreezing;
    public boolean NOTICEYN;  //채널 공지 펼침 여부

    public ChannelInfo(int channelId, String channelName, int type, String sysName, String intro, String NOTICEYN, ArrayList<String> channelNotice, String sysopName, ArrayList<Integer> sysopIds, String members, String open, boolean nick_chk, String doc_search){
        super(channelId, channelName);
        this.channelType = type;
        this.channelIntro = intro;
        this.channelNotice = channelNotice;
        this.sysopName = sysopName;
        this.sysopIds = sysopIds;
        this.members = members;
        this.mOpen = open;
        this.nick_check = nick_chk;
        //채널 공지 개선 - 2020.12.3(채널 공지 펼침 여부 추가)
        if(!TextUtils.isEmpty(NOTICEYN) && !NOTICEYN.equals("N")) this.NOTICEYN = true;
        else  this.NOTICEYN = false;

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

 //       if(BuildConfig.DEBUG) Common.log("D", "Hi-Feedback","channelId= " + this.channelId +", channel_type= " + this.channelType +", this.sysName="+this.sysName +","+ this.channelName +", "+this.isHifeedback);

        //검색 미노출 채널 가이드 표기 - 2021.1.26
        this.docSearch = doc_search;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append(super.toString()).append(", ");
        sb.append("channelIntro = ").append(channelIntro).append(", ");
        sb.append("channelNotice = ").append(channelNotice).append(", ");
        sb.append("sysopName = ").append(sysopName).append(", ");
        sb.append("type = ").append(channelType).append(", ");
        sb.append("sysName = ").append(sysName).append(", ");
        sb.append("isAnnonymous = ").append(isAnnonymous).append(", ");
        sb.append("isHifeedback = ").append(isHifeedback).append(", ");
        sb.append("members = ").append(members).append(", ");
        sb.append("docSearch = ").append(docSearch);
        return sb.toString();
    }
}
