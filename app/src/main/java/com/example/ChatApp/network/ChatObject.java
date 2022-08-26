package com.example.ChatApp.network;

import android.text.Spanned;
import android.text.TextUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ChatObject implements Serializable{

    public static int type_index = 0;
    public static int TYPE_EMPTY = type_index++;
    public static int TYPE_MESSAGE = type_index++;
    public static int TYPE_COMMENT = type_index++;
    public static int TYPE_POST = type_index++;
    public static int TYPE_DOCUMENT = type_index++;
    public static int TYPE_IMAGE = type_index++;
    public static int TYPE_VIDEO = type_index++;
    public static int TYPE_SYSTEM = type_index++;
    public static int TYPE_SYSTEM_NEW = type_index++;
    public static int TYPE_BOT = type_index++;
    public static int TYPE_EMOTICON = type_index++;
    public static int TYPE_TODO = type_index++;
    public static int TYPE_VOTE = type_index++;
    public static int TYPE_RICHNOTIFICATION = type_index++;
    public static int TYPE_HYDISK_FAV = type_index++;

    //토론리스트 추가 -2020.8.24
    public static int TYPE_DEBATE= type_index++;

    public int contentType;

    public String address;
    public int channelId;
    public int nodeId;
    public String messageId;

    public int registerId;
    public String registerName;
    public String registerUniqueName;
    public String registerDate;
    public String profile_img;
    public String deptName;
    public String positionName;

    public String messageText;
    public String isPinnedYn;
    public String favoriteYn;
    public int replyCount;
    public String type;
    public String viewMobileYn;
    public String view_search;
    public String alarmYn;
    public int status;
    public String service;

    public long timeMillis;
    public String dateKey;
    public String monthKey;

    //메시지 검색
    public String subMessageId;
    public String gubun;

    // 익명 이미지
    public String p_img = "";

    // 추천수
    public int like_cnt = 0;

    public ChatObject(String text){
        this("", "", text, Calendar.getInstance().getTimeInMillis());
    }

    public ChatObject(String id, String name, String text){
        this(id, name, text, Calendar.getInstance().getTimeInMillis());
    }

    public ChatObject(String id, String name, String text, long time){
        registerUniqueName = id;
        registerName = name;
        messageText = text;
        timeMillis = time;

        makeDateKey(time);
    }

    public ChatObject(String id, String name, String text, String register_date){
        registerUniqueName = id;
        registerName = name;
        messageText = text;
        registerDate = register_date;

        //시간 String을 long타입 milli로 변경
        if(!TextUtils.isEmpty(register_date)){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
                Date date = sdf.parse(register_date);
                timeMillis = date.getTime();
            } catch (ParseException e) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd kk:mm");
                    Date date = sdf.parse(register_date);
                    timeMillis = date.getTime();
                } catch (ParseException e1) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                        Date date = sdf.parse(register_date);
                        timeMillis = date.getTime();
                    } catch (ParseException e2) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                            Date date = sdf.parse(register_date);
                            timeMillis = date.getTime();
                        } catch (ParseException e3) {
                            e.printStackTrace();
                            e1.printStackTrace();
                            e2.printStackTrace();
                            e3.printStackTrace();
                        }
                    }
                }
            }
        }

        timeMillis = Common.convertUtcToLocalTime(timeMillis);
        makeDateKey(timeMillis);
    }

    public void makeDateKey(long time){
        Calendar rightNow = new GregorianCalendar();
        rightNow.setTimeInMillis(time);
        int dateYear = rightNow.get(Calendar.YEAR);
        int dateMonth = rightNow.get(Calendar.MONTH);
        int dateDay = rightNow.get(Calendar.DAY_OF_MONTH);
        dateKey = String.format("%d_%02d_%02d", dateYear, dateMonth+1, dateDay);
        monthKey = String.format("%d_%02d", dateYear, dateMonth+1);
    }

    public String getDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
        return sdf.format(timeMillis);
    }

    public String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
        return sdf.format(timeMillis);
    }

    public String getName(){
        return registerName;
    }
    public Spanned getSpannedMessage(){
        return Common.fromHtml(Common.replaceTag(messageText));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("class instance = ").append(this.getClass().getSimpleName()).append("\n");
        sb.append("channelId = ").append(channelId).append("\n");
        sb.append("messageId = ").append(messageId).append("\n");
        sb.append("registerId = ").append(registerId).append("\n");
        sb.append("registerName = ").append(registerName).append("\n");
        sb.append("registerUniqueName = ").append(registerUniqueName).append("\n");
        sb.append("deptName = ").append(deptName).append("\n");
        sb.append("positionName = ").append(positionName).append("\n");
        sb.append("registerDate = ").append(registerDate).append("\n");
        sb.append("  └ UTC = ").append(getDateTime()).append("\n");
        sb.append("  └ dateKey = ").append(dateKey).append("\n");
        sb.append("  └ monthKey = ").append(monthKey).append("\n");
        sb.append("messageText = ").append(messageText).append("\n");
        sb.append("isPinnedYn = ").append(isPinnedYn).append("\n");
        sb.append("favoriteYn = ").append(favoriteYn).append("\n");
        sb.append("replyCount = ").append(replyCount).append("\n");
        sb.append("type = ").append(type).append("\n");
        sb.append("p_img = ").append(p_img);
        return sb.toString();
    }

    public String toShortString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("channelId = ").append(channelId).append("\n");
        sb.append("messageId = ").append(messageId).append("\n");
        sb.append("registerId = ").append(registerId).append("\n");
        sb.append("registerDate = ").append(registerDate).append("\n");
        sb.append("messageText = ").append(messageText);
        return sb.toString();
    }
}
