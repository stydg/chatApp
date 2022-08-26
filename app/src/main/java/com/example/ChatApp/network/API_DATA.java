package com.example.ChatApp.network;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class API_DATA implements Serializable{

    public String ApiName;
    public long  StartTime;
    public long EndTime;

    public API_DATA(String _apiName) {
        ApiName = _apiName;
        StartTime = System.currentTimeMillis();
        EndTime = System.currentTimeMillis();
    }


    public String getStartTime(){
        SimpleDateFormat _format = new SimpleDateFormat ( "HH:mm:ss");
        String _format_time = _format.format(StartTime);
        return _format_time;
    }

    public String getEndTime(){
        SimpleDateFormat _format = new SimpleDateFormat ( "HH:mm:ss");
        String _format_time = _format.format(EndTime);
        return _format_time;
    }

    public String getTime(){
        if (StartTime > EndTime){
            return  "-";
        } else {
            SimpleDateFormat _format = new SimpleDateFormat("s.SSS");
            String _format_time = _format.format(EndTime - StartTime);
            return _format_time;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("ApiName = ").append(ApiName).append(", ");
        sb.append("StartTime = ").append(StartTime).append(", ");
        sb.append("EndTime = ").append(EndTime);

        return sb.toString();
    }
}
