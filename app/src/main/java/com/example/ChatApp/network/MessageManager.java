package com.example.ChatApp.network;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;

/*
채팅 채널/메시지 리스트 객체를 가지고 있다가 요청하면 던저주는 기능..
 */
public class MessageManager {
    public HashMap<String, API_DATA> ApiTimeMap = new HashMap<>();

    public static final String MOBILE_LOG_REGISTER = "02";
    private static int totalEvents = 1;
    public static final int refresh = totalEvents++;
    public static final int disconnect = totalEvents++;
    public static final int reconnected = totalEvents++;
    public static final int rejected = totalEvents++;
    public static final int log = totalEvents++;
    public static final int status = totalEvents++;
    public static final int reloadUnread = totalEvents++;
    public static final int reloadChannel = totalEvents++;
    public static final int reloadAlarm = totalEvents++;
    public static final int profile = totalEvents++;
    public static final int commandList = totalEvents++;
    public static final int mentionList = totalEvents++;
    public static final int command = totalEvents++;
    public static final int loginCheck = totalEvents++;
    public static final int fileAuthKey = totalEvents++;
    public static final int channelList = totalEvents++;
    public static final int clearInputMessage = totalEvents++;
    //hyTUBE 채널목록 적용 - 2020.7.21
    public static final int hytubeList = totalEvents++;
    //하이피드백 채널 분리 - 2021.4.21
    public ArrayList<ChannelObject> HFListArray = new ArrayList<>();
    public static final int HFList = totalEvents++;
    //hyTUBE 채널목록 적용 - 2020.7.21
    public ArrayList<ChannelObject> hytubeListArray = new ArrayList<>();
    public ArrayList<ChannelObject> channelListArray = new ArrayList<>();
    public ArrayList<ChannelObject> groupChannelListArray = new ArrayList<>();
    public ArrayList<DMChannel> dmListArray = new ArrayList<>();
    public ArrayList<ChatObject> chatsArray = new ArrayList<>();
    public ArrayList<ChatObject> incChatsArray = new ArrayList<>();
    public static final int moveToMessage = totalEvents++;

    //DM 즐겨찾기 탭처리 - 2020.11.16
    public static final int pushMessage = totalEvents++;
    public static final int receiveMessage = totalEvents++;
    public static final int receiveUpdateMessage = totalEvents++;
    public static final int receiveDeleteMessage = totalEvents++;

    public static final int deleteComment = totalEvents++;
    public static final int pinnedMessage = totalEvents++;
    public static final int favoriteMessage = totalEvents++;
    public static final int updateTodo = totalEvents++;
    public static final int removeTodo = totalEvents++;
    public static final int postFile = totalEvents++;
    public static final int updateChatList = totalEvents++;
    public static final int channelLoad = totalEvents++;
    public static final int selectChannelnfoAndgoLastChannel = totalEvents++;

    public static final int receiveComment = totalEvents++;
    //이모티콘 자주사용 기능(즐겨찾기) 추가 - 2021.5.17(즐겨찾기 이모티콘 사용및 해제 조회)
    public static final int selectAddLikeEmoticon = totalEvents++;
    //이모티콘 자주사용 기능(즐겨찾기) 추가 - 2021.5.17(즐겨찾기 이모티콘 등록)
    public static final int addLikeEmoticon = totalEvents++;
    //이모티콘 자주사용 기능(즐겨찾기) 추가 - 2021.5.17(즐겨찾기 이모티콘 해제)
    public static final int delLikeEmoticon = totalEvents++;

    //토론리스트 추가 -2020.8.24
    public static final int debateMessage = totalEvents++;
    //추천리스트 조회 기능추가- 2020.9.25(적용안하기로함!)
    public static final int getCommanderList = totalEvents++;


    public static final int retryTask = totalEvents++;
    public static final int resendTask = totalEvents++;

    public static final int setttingAnonymousChannel = totalEvents++;
    public static final int channelScheduleList = totalEvents++;

    public static final int broadcast = -1;
    public HashMap<String, String> notification_info = new HashMap<>();
    public static final int channelInfo = totalEvents++;
    public static final int channelInfoUpdate = totalEvents++;


    //hyTUBE 채널목록 적용 - 2020.7.21
//    public static int hytubeCount = 0; //hyTUBE 채널리스트 갯수

    private SparseArray<ArrayList<Object>> observers = new SparseArray<>();

    public ArrayList<String> unreadChannelArray = new ArrayList<>();
    public ArrayList<String> unreadDMChannelArray = new ArrayList<>();
    public ArrayList<ChatObject> unreadChannelListArray = new ArrayList<>();

    private static volatile MessageManager Instance = null;
    private Activity mActivity  =null;

    public static final int updatevoteanswer = totalEvents++;

    public static MessageManager getInstance() {
        MessageManager localInstance = Instance;
        if (localInstance == null) {
            synchronized (MessageManager.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new MessageManager();
                }
            }
        }
        return localInstance;
    }

    public MessageManager() {

    }
    public synchronized int getChatsArrayIndex(String _messageId) {
        int rslt_idx = -1;
        if (chatsArray.size() > 0) {
            for (int i = 0; i < chatsArray.size(); i++) {
                if (TextUtils.equals(_messageId, chatsArray.get(i).messageId)) {
                    rslt_idx = i;
                    break;
                }
            }
        }

        return rslt_idx;
    }
    public void clear() {
        if (Common.DEBUG) Common.log("E", "ActivityEvent", "MessageManager-clear");
        ObserversClear();

        channelListArray.clear();
        groupChannelListArray.clear();
        dmListArray.clear();
        chatsArray.clear();
        incChatsArray.clear();
        unreadChannelArray.clear();
        unreadDMChannelArray.clear();
        unreadChannelListArray.clear();

        //hyTUBE 채널목록 적용 - 2020.7.21
        hytubeListArray.clear();

        //하이피드백 채널 분리 - 2021.4.21
        HFListArray.clear();
    }
    public void ObserversClear() {
        observers.clear();
    }
    public void setApiTimeMapStartTime(String _api_name) {
        API_DATA _api_data = this.ApiTimeMap.get(_api_name);
        if (_api_data != null) {
            _api_data.StartTime = System.currentTimeMillis();
            this.ApiTimeMap.put(_api_name, _api_data);
        }
    }
    public void setApiTimeMapEndTime(String _api_name) {
        API_DATA _api_data = this.ApiTimeMap.get(_api_name);
        if (_api_data != null) {
            _api_data.EndTime = System.currentTimeMillis();
            this.ApiTimeMap.put(_api_name, _api_data);
        }
    }

    public interface Delegate {
        void didReceivedNotification(int id, Object... args);
    }

    public void postNotification(int id, Object... args) {
        try {Log.e("TLOG  포스트노티피케이션", Integer.toString(id));
            if (id == broadcast) {
                for (int i = 0; i < observers.size(); i++) {
                    ArrayList<Object> objects = observers.get(observers.keyAt(i));
                    if (objects != null && !objects.isEmpty()) {
                        for (int a = 0; a < objects.size(); a++) {
                            Object obj = objects.get(a);
                            Log.e("TLOG", "브로드케스트 후 didReceived 호출함 ");
                            ((Delegate) obj).didReceivedNotification(id, args);
                        }
                    }
                }
                return;
            }
            ArrayList<Object> objects = observers.get(id);
            if (objects != null && !objects.isEmpty()) {
                for (int a = 0; a < objects.size(); a++) {
                    Object obj = objects.get(a);
                    Log.e("TLOG", id+" didReceived 호출함 ");
                    ((Delegate) obj).didReceivedNotification(id, args);
                }
            }
        } catch (Exception e) {
            Log.e("TLOG", id+"가 didReceived 못부르고 에러남 ");
        }
    }
    public String getChannelName(int channelId) {
        for (ChannelObject channel : channelListArray) {
            if (channel.channelId == channelId)
                return (String) channel.channelName;
        }
        for (ChannelObject channel : groupChannelListArray) {
            if (channel.channelId == channelId)
                return (String) channel.channelName;
        }
        for (ChannelObject channel : dmListArray) {
            if (channel.channelId == channelId)
                return (String) channel.channelName;
        }
        //hyTUBE 채널목록 적용 - 2020.7.21
        for (ChannelObject channel : hytubeListArray) {
            if (channel.channelId == channelId)
                return (String) channel.channelName;
        }

        //하이피드백 채널 분리 - 2021.4.21
        for (ChannelObject channel : HFListArray) {
            if (channel.channelId == channelId)
                return (String) channel.channelName;
        }

        return null;
    }
    public boolean hasChannel(String channelId) {
        boolean result = false;

        try {
            for (ChannelObject channel : channelListArray) {
                if (channel.channelId == Integer.parseInt(channelId)) result = true;
            }
            for (ChannelObject channel : groupChannelListArray) {
                if (channel.channelId == Integer.parseInt(channelId)) result = true;
            }
            for (ChannelObject channel : dmListArray) {
                if (channel.channelId == Integer.parseInt(channelId)) result = true;
            }
            //hyTUBE 채널목록 적용 - 2020.7.21
            for (ChannelObject channel : hytubeListArray) {
                if (channel.channelId == Integer.parseInt(channelId)) result = true;
            }
            //하이피드백 채널 분리 - 2021.4.21
            for (ChannelObject channel : HFListArray) {
                if (channel.channelId == Integer.parseInt(channelId)) result = true;
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
    public synchronized void addApiTime(String _api_name) {
        API_DATA _api_data = new API_DATA(_api_name);
        this.ApiTimeMap.put(_api_name, _api_data);
    }
    public synchronized void clearApiTimeMap() {
        this.ApiTimeMap.clear();
    }
    public void addObserver(Object observer, int id) {
        ArrayList<Object> objects = observers.get(id);
        if (objects == null) {
            observers.put(id, (objects = new ArrayList<>()));
        }
        if (objects.contains(observer)) {
            return;
        }
        objects.add(observer);
    }
    public boolean hasObserver(int id) {
        Common.log("W", "hasObserver", "observers size = " + observers.size());
        ArrayList<Object> objects = observers.get(id);
        if (objects != null) {
            return true;
        } else {
            return false;
        }
    }


}
