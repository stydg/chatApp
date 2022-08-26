package com.example.ChatApp.network;

import android.text.TextUtils;

import com.example.ChatApp.BuildConfig;
import com.example.ChatApp.MainActivity;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

// 메시지 재전송 관리
public class ServerInterfaceRetryManager {
    private static VertxClient vertxClient;
    private static volatile ServerInterfaceRetryManager Instance = null;

    private enum TaskType {None, WaitResult, CheckNetwork, Timeout, AccessChannel}

    private TaskType mTaskType;

    public enum NetworkStatusType {None, Open, Close, Error,}

    private NetworkStatusType mNetworkStatusType;

    private Map<String, String> packetMap;
    private TimerTask mTask;
    private Timer mTimer;
    private int mRetryCount;
    private long mAceessFailChannelID = 0;
    private int mRetryCountCommandList;
    private int mRetryCountProfile;
    public static boolean isGoLastChannel = false;

    private static final long INTERVAL_WAIT_RECV_PACKET = 3000;
    private static final long INTERVAL_CHECK_NETWORK_STATUS = 12000;
    private static final long INTERVAL_WAIT_ACCEES_CHANNEL = 45000;
    public static final int SHOW_PROGRESS_DIALOG_MESSAGE = 1;
    public static final int HIDE_PROGRESS_DIALOG_MESSAGE = 2;
    public static final int SHOW_RETRY_TASK_TIMEOUT_MESSAGE = 3;
    public static final int MAX_RETRY_COUNT = 3;

    public static Boolean WaitSendMessageResult = false;
    public static String LastSendMessage = "";
    public static long LastSendMessageTime = System.currentTimeMillis();
    public static long LastRecvMessageTime = System.currentTimeMillis();
    public static int tryReconnectCount = 0;

    public static synchronized ServerInterfaceRetryManager getInstance() {
        ServerInterfaceRetryManager localInstance = Instance;
        if (localInstance == null) {
            synchronized (ServerInterfaceRetryManager.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new ServerInterfaceRetryManager();
                }
            }
        }
        return localInstance;
    }

    public static void incTryReconnectCount() {
        tryReconnectCount++;
    }
    public ServerInterfaceRetryManager() {
        mTaskType = TaskType.None;
        mNetworkStatusType = NetworkStatusType.None;
        packetMap = new HashMap<>();
        //clearDuplicateMessageCheckingInfo();
        mRetryCountCommandList = 0;
        mRetryCountProfile = 0;
    }
    public long getMoveRequestChannel() {
        long rslt = 0;

        //모바일 noti 클릭시에 채널로 점프가 안되는 오류 수정 - 2020.5.8 이동원TL 요청사항
//        if (System.currentTimeMillis() <= moveRequestChannelTime + 5000) {
        rslt = moveRequestChannelID;
//        }
        setMoveRequestChannel(0);
        return rslt;
    }

    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void removeRetry(String _key) {
        removePacketMap(_key);
        if (mTaskType == TaskType.CheckNetwork) {
            MessageManager.getInstance().postNotification(MessageManager.retryTask, HIDE_PROGRESS_DIALOG_MESSAGE);
        }
        if (packetMap.size() > 0) {
            createWaitUI();
        } else {
            cancelRetry();
        }
    }
    private synchronized void removePacketMap(String key) {
        if (packetMap.size() > 0) {
            if (packetMap.containsKey(key)) {
                packetMap.remove(key);
            }
        }
        mRetryCount = 0;
    }

    private void createWaitUI() {
        cancelTimer();
        cancelTask();
        if (AppStarter.isBackground()) return;
        mTaskType = TaskType.WaitResult;
        createTimer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                createCheckNetworkUI();
            }
        };

        mTimer.schedule(mTask, INTERVAL_WAIT_RECV_PACKET);
        if (BuildConfig.DEBUG) {
            Common.log("E", "ServerInterfaceRetryManager", "createWaitUI()");
        }
    }
    private void createTimer() {
        cancelTimer();
        mTimer = new Timer();
    }
    private void createCheckNetworkUI() {
        cancelTimer();
        cancelTask();
        if (AppStarter.isBackground()) return;
        mTaskType = TaskType.CheckNetwork;
        //MessageManager.getInstance().postNotification(MessageManager.retryTask, SHOW_PROGRESS_DIALOG_MESSAGE);
        createTimer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                executeRetry();
            }
        };

        mTimer.schedule(mTask, INTERVAL_CHECK_NETWORK_STATUS);
        if (BuildConfig.DEBUG) {
            Common.log("E", "ServerInterfaceRetryManager", "createCheckNetworkUI()");
        }
    }
    public void executeRetry() {
        if (AppStarter.isBackground()) return;
        if (mRetryCount < MAX_RETRY_COUNT - 1) {
            createResendUI();
        } else {
            mTaskType = TaskType.Timeout;
            showTimeoutMessage();
            cancelRetry();
        }
    }

    private void createResendUI() {
        cancelTimer();
        cancelTask();
        if (AppStarter.isBackground()) return;
        resendRetry();
        mTaskType = TaskType.CheckNetwork;
        //MessageManager.getInstance().postNotification(MessageManager.retryTask, SHOW_PROGRESS_DIALOG_MESSAGE);
        createTimer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                executeRetry();
            }
        };
        mTimer.schedule(mTask, INTERVAL_CHECK_NETWORK_STATUS);
        if (BuildConfig.DEBUG) {
            Common.log("E", "ServerInterfaceRetryManager", "createResendUI()");
        }
    }
    public void resendRetry() {
        if (AppStarter.isBackground() || packetMap.size() == 0) return;
        mRetryCount++;
        resendPacketMap();
    }
    public void resendRetry(String _api) {
        if (AppStarter.isBackground() || packetMap.size() == 0 || mRetryCount >= MAX_RETRY_COUNT - 1) {
            if (TextUtils.equals(_api, API.selectMessageList)) {
                removeRetry(API.selectMessageList);
                clearDuplicateMessageCheckingInfo();
            }
            MessageManager.getInstance().postNotification(MessageManager.updateChatList, "-1", 0, 0, -1);
            return;
        }
        mRetryCount++;
        resendPacketMap(_api);
    }
    public static void clearDuplicateMessageCheckingInfo() {
        WaitSendMessageResult = false;
        LastSendMessage = "";
        LastSendMessageTime = System.currentTimeMillis();
    }
    private synchronized void resendPacketMap() {
        if (packetMap.size() > 0) {
            Iterator<String> keys = packetMap.keySet().iterator();
            while (keys.hasNext()) {
                String _key = keys.next();
                String _packet = (String) packetMap.get(_key);
                MessageManager.getInstance().postNotification(MessageManager.resendTask, _packet);
            }
        }
    }
    private synchronized void resendPacketMap(String _api_) {
        if (packetMap.size() > 0) {
            String _packet = (String) packetMap.get(_api_);
            MessageManager.getInstance().postNotification(MessageManager.resendTask, _packet);
        }
    }

    private void showTimeoutMessage() {
        MessageManager.getInstance().postNotification(MessageManager.retryTask, SHOW_RETRY_TASK_TIMEOUT_MESSAGE);
    }

    public void changeNetworkStatus(NetworkStatusType networkStatusType, String message) {
        if (mNetworkStatusType != networkStatusType) {
            mNetworkStatusType = networkStatusType;
            if (mNetworkStatusType == NetworkStatusType.Open) {
                cancelRetry();
            }
            /*if (BuildConfig.DEBUG && MainActivity.profile != null && (TextUtils.equals(MainActivity.profile.uniqueName.toUpperCase().toString(), "I0101301") || TextUtils.equals(MainActivity.profile.uniqueName.toUpperCase().toString(), "I0100749"))) { // YS-DEBUG
                Common.toast(message);
            }*/
        }
    }

    public void cancelRetry() {
//        DialogManager.getInstance().dismissAll();
        clearPacketMap();
        mRetryCount = 0;
        mRetryCountCommandList = 0;
        mRetryCountProfile = 0;
        cancelTimer();
        cancelTask();
        mTaskType = TaskType.None;
        if (BuildConfig.DEBUG) {
            Common.log("E", "ServerInterfaceRetryManager", "cancelRetry()");
        }
    }

    // 네트워크 재접속시에 채널 이동할 경우 해당 채널로 가지 않고 서버에서 전송된 최근 접속 채널로 이동하는 오류 수정
    private long moveRequestChannelID = 0;
    private long moveRequestChannelTime = System.currentTimeMillis();

    public void setMoveRequestChannel(long _chid) {
        moveRequestChannelID = _chid;
        moveRequestChannelTime = System.currentTimeMillis();
    }
    private void cancelTask() {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
    }

    private synchronized void clearPacketMap() {
        packetMap.clear();
    }
    public static void clearTryReconnectCount() {
        tryReconnectCount = 0;
    }

    public static void setLastRecvMessageTime() {
        LastRecvMessageTime = System.currentTimeMillis();
    }
    public static boolean isTryReconnect() {
        boolean rslt = false;
        if (System.currentTimeMillis() > LastRecvMessageTime + 5000 && tryReconnectCount < 5) {
            rslt = true;
        }
        return rslt;
    }
    public void resendSelectProfile() {
        if (mRetryCountProfile < 2) {
            ServerInterfaceManager.getInstance().selectProfile();
            mRetryCountProfile++;
        }

    }


}
