package com.example.ChatApp.network;

import java.util.Calendar;

/*
채팅 채널/메시지 리스트 객체를 가지고 있다가 요청하면 던저주는 기능..
 */
public class ApiTimeManager {

    private static volatile ApiTimeManager Instance = null;

    public static ApiTimeManager getInstance() {
        ApiTimeManager localInstance = Instance;
        if (localInstance == null) {
            synchronized (ApiTimeManager.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new ApiTimeManager();
                }
            }
        }
        return localInstance;
    }

    public ApiTimeManager(){

    }

    private long start_time = 0;

    public void startApi() {
        if(start_time == 0) {
            start_time = Calendar.getInstance().getTimeInMillis();
        }
    }

    public long stopApi() {
        if(start_time == 0) {
            return -1;
        }
        long passed_time = Calendar.getInstance().getTimeInMillis() - start_time;
        start_time = 0;
        return passed_time;
    }
}
