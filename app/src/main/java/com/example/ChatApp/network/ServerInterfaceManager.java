package com.example.ChatApp.network;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.ChatApp.MainActivity;
import com.example.ChatApp.R;
import com.example.ChatApp.network.http.HttpAsyncTask;
import com.example.ChatApp.network.http.HttpClient;
import com.google.android.datatransport.backend.cct.BuildConfig;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServerInterfaceManager {
    Context mContext;
    private static final String TAG = "ServerInterfaceManager";
    private static VertxClient vertxClient;

    private static Map<String, Handler> handlerMap = new HashMap<>();

    private static volatile ServerInterfaceManager Instance = null;

    //이모티콘 최근사용 기능 추가 -2020.6.24
    private static String group = "";
    //hyTUBE 채널목록 적용 - 2020.7.21
    private static int mType = 0; //0:CHANNELS, 1:MESSAGES, 2:hyTUBE, 3:Hi-Feedback
    //검색 restAPI로 변경 - 2020.8.26
    private static Map<String, String> searchMessageListMap = new HashMap<>();//같은 검색 조건 결과인지 비교
    public static final boolean NEED_MARKET = false;
    public static final boolean IS_TOKTOK = false;


    public static synchronized ServerInterfaceManager getInstance() {
        ServerInterfaceManager localInstance = Instance;
        if (localInstance == null) {
            synchronized (ServerInterfaceManager.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new ServerInterfaceManager();
                }
            }
        }
        return localInstance;
    }

    //interface function//////////////////////////////////////////////////////////////////////////////////////////////////
    public void clearHandler() {
        handlerMap.clear();
        if (vertxClient != null) {
            try {
                vertxClient.clearWebview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            vertxClient = null;
        }
    }

    /* 모바일 로그 */
    public void sendMobileLog(String action, int channel_id) {
        if (vertxClient != null) {
            if (MainActivity.isHasApiTimeAuth) {
                MessageManager.getInstance().setApiTimeMapStartTime("websocket.apiMobileLog");
            }
            handlerMap.put(API.apiMobileLog, MobileLogHandler);
            vertxClient.sendMobileLog(action, channel_id);
        }
    }
    private Handler MobileLogHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean resultOk = false;
            String result = (String) msg.getData().getSerializable("result");
            if (MainActivity.isHasApiTimeAuth) {
                MessageManager.getInstance().setApiTimeMapEndTime("websocket.apiMobileLog");
            }
//            if(!TextUtils.isEmpty(result)) {
//                try {
//                    JSONObject resultObject = new JSONObject(result);
//                    if (!TextUtils.equals(resultObject.getString("body"), "fail")) {
//                        JSONObject bodyObject = resultObject.getJSONObject("body");
//                        resultOk = bodyObject.getBoolean("result");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            if(resultOk) {
//                //true 대한 처리
//            } else {
//                //false에 대한 처리
//            }
        }
    };

    /*채팅 메시지 전송*/
    public void sendMessage(String channelId, String userId, String content, String type, String mode) {
        // 전송 결과를 기다리는중 중복 메시지 전송 불가 처리
        if (ServerInterfaceRetryManager.WaitSendMessageResult
                && TextUtils.equals(content, ServerInterfaceRetryManager.LastSendMessage)
                && System.currentTimeMillis() - ServerInterfaceRetryManager.LastSendMessageTime < 5000) {
            return;
        } else {
            ServerInterfaceRetryManager.WaitSendMessageResult = true;
            ServerInterfaceRetryManager.LastSendMessage = content;
            ServerInterfaceRetryManager.LastSendMessageTime = System.currentTimeMillis();
            handlerMap.put(API.addMessage, sendMessageHandler);
            vertxClient.sendMessage(channelId, userId, HtmlUtils.escapeHtml(content), type, mode, "", "");
        }
    }
    /*
     * 채팅 메시지 전송
     * */
    private Handler sendMessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.getData().getSerializable("result");
            try {
                JSONObject resultObject = new JSONObject(result);
                if (!resultObject.getString("body").equals("fail")) {
                    MessageManager.getInstance().postNotification(MessageManager.clearInputMessage, "");
                    JSONObject bodyObject = resultObject.getJSONObject("body");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    /*채널 입장 서버에 값 요청보냄*/
    public void registerChannelId(String channelId) {
        //Register 로그 저장
        ServerInterfaceManager.getInstance().sendMobileLog(MessageManager.MOBILE_LOG_REGISTER, Integer.valueOf(channelId));
        if (vertxClient != null) {
            handlerMap.put("hynix.client." + channelId, registChannelHandler);
            vertxClient.registerChannelId(channelId);
        }
    }
    /*
     * 채널 입장 서버로 요청내용에 대한 msg받음\
     * */
    private Handler registChannelHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.getData().getSerializable("result");
            try {
                JSONObject resultObject = new JSONObject(result);
                if (!resultObject.getString("body").equals("fail")) {
                    JSONObject bodyObject = resultObject.getJSONObject("body");

                    // OpenGraph 처리
                    if (bodyObject.has("result") && bodyObject.has("tp") && bodyObject.has("url")) {
                        setOpenGraphData(bodyObject);
                        return;
                    }

                    //receive message/////////////////////////////////////////////////////////////////////
                    ChatObject chat = makeChatObject(bodyObject);
                    //Log.d(getClass().getSimpleName(), "receive message");
                    if (chat != null) {
                        if (chat instanceof Comment) {
                            MessageManager.getInstance().postNotification(MessageManager.receiveComment, chat);
                        } else {
                            if (chat instanceof PostMessage) {
                                // Log.d(getClass().getSimpleName(), ((PostMessage) chat).toString());
                            } else if (chat instanceof DocMessage) {
                                // Log.d(getClass().getSimpleName(), ((DocMessage) chat).toString());
                            } else if (chat instanceof ChatMessage) {
                                // Log.d(getClass().getSimpleName(), ((ChatMessage) chat).toString());
                            } else if (chat instanceof Comment) {
                                // Log.d(getClass().getSimpleName(), ((Comment) chat).toString());
                            }
                            MessageManager.getInstance().postNotification(MessageManager.receiveMessage, chat);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    // OpenGraph 처리
    private void setOpenGraphData(JSONObject ogBodyObj) {
        if (ogBodyObj.has("result")) {
            OpenGraphData aresult = null;

            try {

                JSONObject ogObj = ogBodyObj.getJSONObject("result");
                if (ogObj != null && ogObj.has("url")) {
                    String _description = getJsonString(ogObj, "description");
                    String _image = getJsonString(ogObj, "image");
                    String _title = getJsonString(ogObj, "title");
                    String _url = getJsonString(ogObj, "url");
                    aresult = new OpenGraphData(_description, _image, _title, _url);
                } else {
                    return;
                }

                String amsg = getJsonString(ogBodyObj, "msg");
                String aalarm_yn = getJsonString(ogBodyObj, "alarm_yn");
                boolean astat = getJsonBoolean(ogBodyObj, "stat");
                String amessage_id = getJsonString(ogBodyObj, "message_id");
                String atp = getJsonString(ogBodyObj, "tp");
                String achannel_id = getJsonString(ogBodyObj, "channel_id");
                String aurl = getJsonString(ogBodyObj, "url");

                // 현재 접속채널과 Push 된 채널정보가 일치할 경우만 OpenGraph 업데이트
                if (MainActivity.info != null && Common.StringToIntDef(achannel_id, 0) == MainActivity.info.channelId) {
                    NotiOpenGraphData notiOgData = new NotiOpenGraphData(amsg, aresult, aalarm_yn, astat, amessage_id, atp, achannel_id, aurl);
                    /*if (MainActivity.chatMessageAdapter != null) {
                        MainActivity.chatMessageAdapter.setOpenGraphPushData(amessage_id, notiOgData);
                    }*/
                }
            } catch (JSONException e) {
            }
        }
    }
    /*채널 나감*/
    public void unregisterChannelId(String channelId) {
        if (vertxClient != null) {
            handlerMap.remove("hynix.client." + channelId);
            vertxClient.unregisterChannelId(channelId);
        }
    }
    /*로그인*/
    public void login(String id, String pass, String phone, Handler handler) {
        if (BuildConfig.DEBUG) Common.log("E", getClass().getSimpleName(), "login");
        handlerMap.put("auth", handler); //auth라는 이름의 api에 핸들러붙이고 추가함
        handlerMap.put("push", pushHandler);
        if (!NEED_MARKET) {
            new LoginAsyncTask().execute(id, pass, phone);
        } else {
            String uuid = PrefManager.getInstance(AppStarter.applicationContext).getValue("uuid", "");
            if (IS_TOKTOK) {
                try {
                    //String mdn = TokTok.getMdn(AppStarter.applicationContext);
                    String appId = AppStarter.applicationContext.getResources().getString(R.string.app_id);
                   /* Map<String, String> map = TokTok.getAuthKeyEncPwd(AppStarter.applicationContext, mdn, appId);
                    String companyCd = map.get("COMPANY_CD");
                    String authKey = map.get("AUTHKEY");
                    String encPwd = map.get("ENC_PWD");*/
                    String appVer = Common.getAppVersion();
                    String osName = Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName();
                    String osVersion = Build.VERSION.RELEASE;
                    if (!osVersion.contains(".")) {
                        osVersion = osVersion + ".0";
                    }
                    String osAPIVersion = String.valueOf(Build.VERSION.SDK_INT);
                    String deviceModel = Build.MODEL;

                    StringBuilder sb = new StringBuilder();
                    sb.append("primitive=").append("COMMON_COMMON_EMPINFO").append("&");
                   // sb.append("mdn=").append(mdn).append("&");
                    sb.append("appId=").append(appId).append("&");
   /*                 sb.append("companyCd=").append(companyCd).append("&");
                    sb.append("authKey=").append(authKey).append("&");
                    sb.append("encPwd=").append(encPwd).append("&");*/
                    //특화앱
                    if (BuildConfig.FLAVOR.equals("realServerSpecial")) {
                        sb.append("uniqueId=").append(uuid).append("&");
                    }
                    sb.append("appVer=").append(appVer).append("&");
                    sb.append("osName=").append(osName).append("&");
                    //sb.append("osVersion=").append(osVersion).append("&");
                    sb.append("osVersion=").append(osAPIVersion).append("&");
                    sb.append("deviceModel=").append(deviceModel);

                    HttpClient httpClient = new HttpClient(null, gmpResultListener);
                    httpClient.setParam(HttpAsyncTask.HTTP_POST, Common.GMP_URL);//경유 Common.GMP_URL    직접 "https://m.toktok.sk.com:9443/service.pe"
                    httpClient.setQueryParam(sb.toString());
                    httpClient.excute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
               /* String appId = ApplicationManager.getAppId(AppStarter.applicationContext);
                String authKey = SKActivityDelegate.getAuthKeyUseAppId(AppStarter.applicationContext, appId);
                String profileNo = SKActivityDelegate.getProfileNo(AppStarter.applicationContext);*/
                TelephonyManager manager = (TelephonyManager) AppStarter.applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
                String countryID = manager.getSimCountryIso().toLowerCase();

                StringBuilder sb = new StringBuilder();
        /*        sb.append("profileNo=").append(profileNo).append("&");
                sb.append("authKey=").append(authKey).append("&");
                sb.append("appId=").append(appId).append("&");*/
                //특화앱
                if (BuildConfig.FLAVOR.equals("realServerSpecial")) {
                    sb.append("uniqueId=").append(uuid).append("&");
                    sb.append("whether=").append("special").append("&");
                }
                sb.append("lang=").append(countryID);

                HttpClient httpClient = new HttpClient(null, httpAsyncTaskResultListener);
                //공용앱 로그인 URL 변경 - 2020.7.21
                String store_url ="";

                if (BuildConfig.FLAVOR.equals("realServerSpecial")) {
                    store_url = Common.SPECIAL_STORE_URL;
                }else {
                    store_url = Common.STORE_URL;
                }
                httpClient.setParam(HttpAsyncTask.HTTP_GET, store_url);
                httpClient.setQueryParam(sb.toString());
                httpClient.excute();
            }
        }
    }
    public void disconnect() {
        if (vertxClient != null) vertxClient.disconnect();
    }
    public void reconnect(Handler handler) {
        if (BuildConfig.DEBUG) Common.log("E", getClass().getSimpleName(), "reconnect");
        if (vertxClient != null) {
            handlerMap.put("auth", handler);
            vertxClient.reconnect();
        } else {
            checkConnect();
        }
    }
    public void checkConnect() {
        if (vertxClient == null) {
            if (BuildConfig.DEBUG)
                Common.log("E", getClass().getSimpleName(), "vertxClient is null ==> new VertxClient()");
            String sessionkey = PrefManager.getInstance(AppStarter.applicationContext).getValue(Common.PREF_SESSION_KEY, "");
            if (vertxClient != null) {
                vertxClient.disconnect();
            }
            vertxClient = new VertxClient(sessionkey);
            vertxClient.setOnSockJsListener(listener);
        } else {
            vertxClient.checkConnect();
        }
    }
    //handler/////////////////////////////////////////////////////////////////////////////////////////////
    private Handler pushHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.getData().getSerializable("result");
            try {
                JSONObject resultObject = new JSONObject(result);
                String address = getJsonString(resultObject, "address");
                String sessionKey = PrefManager.getInstance(AppStarter.applicationContext).getValue(Common.PREF_SESSION_KEY, "");
                //DM리스트 정렬(최신메시지 상위 이동) 로직 추가 -2020.7.2
//                if(!TextUtils.isEmpty(address) && address.startsWith("hynix.client.5")){
//                    Common.log("D", "pushHandler", "pushHandler resultObject = " + resultObject);
//
//                }
                if (!address.contains(sessionKey)) return;
                if (resultObject.getString("body").equals("fail")) {

                } else if (resultObject.getString("body").equals("rejected")) {
                    MessageManager.getInstance().postNotification(MessageManager.rejected);
                } else {
                    JSONObject bodyObject = resultObject.getJSONObject("body");

                    String tp = getJsonString(bodyObject, "tp");

                    if (tp.startsWith("EML") || tp.startsWith("APL") ||
                            tp.startsWith("TOD") || tp.startsWith("CAL")) {
                        Handler handler = handlerMap.get(API.noticeAPI);
                        if (handler != null) {
                            Message msgNotice = handler.obtainMessage();

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("result", result);
                            msgNotice.setData(bundle);
                            handler.sendMessage(msgNotice);
                        }
                    }


                    if (BuildConfig.DEBUG) {
                        Common.log("D", "pushHandler", "pushHandler tp = " + tp);

                        Common.log("D", "pushHandler", "pushHandler result = " + result);

                    }
                    switch (tp) {
                        case "CHL1004": //채널 개설
                        case "CHL1033": //채널삭제 완료
                        case "CHL1009": //채널 나가기
                        case "CHL1006"://채널정보 수정
                        {
                            String alarm_yn = getJsonString(bodyObject, "alarm_yn");

                            JSONObject infoObject = bodyObject.getJSONObject("channel_info");
                            int channel_id = getJsonInt(infoObject, "channel_id");
                            String channel_name = getJsonString(infoObject, "channel_name");
                            String aliasChannelName = getJsonString(infoObject, "aliasChannelName");
                            if (!TextUtils.isEmpty(aliasChannelName))
                                channel_name = aliasChannelName;
                            ArrayList<String> channel_name_m = jsonToArrayList(infoObject.getJSONArray("channel_name_m"));
                            channel_name = channel_name_m.get(new LanguageUtil().getSystemLanguageTypeIndex());
                            //채널
                            int channel_type = getJsonInt(infoObject, "channel_type");

                            //hyTUBE 익명방 추가- 2020.10.20
                            String  sysName = getJsonString(infoObject, "sysName");//하이튜브( HYTUBE), 하이피드백(HYFB1 , HYFB2)
//                            if(BuildConfig.DEBUG) Common.log("D", "CHECKHYTUBE","channel_type CHL1006(채널정보 수정)= " + channel_type +", sysName="+sysName +","+ channel_name);

                            String m_open = getJsonString(infoObject, "m_open");
                            String channel_intro = getJsonString(infoObject, "intro");
                            String channel_notice = getJsonString(infoObject, "notice");
                            String doc_search = getJsonString(infoObject, "doc_search");
                            String docm_search = getJsonString(infoObject, "docm_search");
                            //DM채널
                            String channel_members = getJsonString(infoObject, "channel_members");

                           /* if(MainActivity.info.channelId == channel_id){
                                MainActivity.info.channelName = channel_name;
                                MainActivity.info.channelType = channel_type;
                                MainActivity.info.channelIntro = channel_intro;
                                MainActivity.info.channelNotice = channel_notice;
                            }*/

                            MessageManager.getInstance().postNotification(MessageManager.channelInfoUpdate);
                            if (MainActivity.info != null && channel_id == MainActivity.info.channelId) {
                                getChannelInfo(String.valueOf(channel_id));
                            }

                            String _channel_id = String.valueOf(channel_id);
                            if (_channel_id.startsWith("2")) {
                                //하이피드백 채널 분리 - 2021.4.21
//                                if(sysName.startsWith("HYFB"))
//                                    //ServerInterfaceManager.getInstance().selectHFChannelList();
//                                //hyTUBE 채널 분리 - 2020.10.30
//                                else if("HYTUBE".equals(sysName))
//                                    //ServerInterfaceManager.getInstance().selectHyTUBEChannelList();
//                                else
                                ServerInterfaceManager.getInstance().selectChannelList();
                            }
               /*             else {
                                //DM 즐겨찾기 탭처리 - 2020.11.16
                                if(FragmentMessageList.isDMFavoriteList)
                                    ServerInterfaceManager.getInstance().selectDMFavoriteChannel();
                                else
                                    ServerInterfaceManager.getInstance().selectDMChannelList(FragmentMessageList.isFav);
                            }*/
                        }
                        break;
                        case "CHL1015": // 채널 익명 설정
                        {
                            int channel_id_ = getJsonInt(bodyObject, "channel_id");
                            MessageManager.getInstance().postNotification(MessageManager.setttingAnonymousChannel, channel_id_);
                        }
                        break;
                        case "END1001": {
                            String message = getJsonString(bodyObject, "msg");
                            MessageManager.getInstance().postNotification(MessageManager.rejected, message);
                        }
                        break;
//                        case "CNF1006"://언어설정변경
//                            break;
                        case "CNF1009"://unread 채널
                        case "CNF2009"://unread DM채널
                            MessageManager.getInstance().postNotification(MessageManager.reloadUnread);
                            break;
//                        case "CNF1011"://Channel sort
//                            MessageManager.getInstance().postNotification(MessageManager.reloadChannel);
//                            break;
//                        case "CNF1010":
//                            MessageManager.getInstance().postNotification(MessageManager.reloadAlarm);
//                            break;
                        case "CNF1014": //채널명 alias 수정
                        case "EDM1001": {
                            ArrayList<Doc> list = new ArrayList<>();
                            boolean stat = getJsonBoolean(bodyObject, "stat");
                            if (bodyObject.has("resultArray")) {
                                JSONArray resultArray = bodyObject.getJSONArray("resultArray");
                                for (int i = 0; i < resultArray.length(); i++) {
                                    JSONObject row = resultArray.getJSONObject(i);

                                    try {
                                        JSONObject fileJsonObj = row.getJSONObject("AttachFile");
                                        row = fileJsonObj;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String fileId = getJsonString(row, "AttachFileID");
                                    String fileName = getJsonString(row, "AttachFileTitle");
                                    String fileSize = getJsonString(row, "AttachFileSize");
                                    String fileUrl = getJsonString(row, "AttachFileURL");

                                    list.add(new Doc(fileId, fileName, fileSize, fileUrl));
                                }
                            }
                            MessageManager.getInstance().postNotification(MessageManager.postFile, list);
                        }
                        break;

                        // OpenGraph
                        case "OG1001":

                        // 채널별 일정
                        case "CAL2010": {
                            if (!bodyObject.has("resultArray")) {
                                MessageManager.getInstance().postNotification(MessageManager.channelScheduleList, null, true);
                            } else {
                                JSONObject resultArray = bodyObject.getJSONObject("resultArray");
                                MessageManager.getInstance().postNotification(MessageManager.channelScheduleList, resultArray, true);
                            }
                            break;
                        }
                        default:
                            //receive message/////////////////////////////////////////////////////////////////////
                            ChatObject chat = makeChatObject(bodyObject);
                            if (chat != null) {
                                if (chat.channelId != 0) {
                                    MessageManager.getInstance().postNotification(MessageManager.pushMessage, chat);
                                }
                            }
                            break;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public static ArrayList<String> jsonToArrayList(JSONArray jsonArray) {
        ArrayList<String> list = new ArrayList<String>();
        if (jsonArray != null) {
            int len = jsonArray.length();
            try {
                for (int i = 0; i < len; i++) {
                    list.add(jsonArray.get(i).toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static boolean getJsonBoolean(JSONObject jsonObject, String name) {
        try {
            return jsonObject.getBoolean(name);
        } catch (JSONException e) {
            return false;
        }
    }

    public static ChatObject makeChatObject(JSONObject jsonObject) {
        String address = getJsonString(jsonObject, "address");
        String type = getJsonString(jsonObject, "type"); //M 메시지 N 핀메시지 F 파일 I 이미지 O  C 첨부글
        int index = getJsonInt(jsonObject, "index");
        int channel_id = getJsonInt(jsonObject, "channel_id");
        String channel_name = getJsonString(jsonObject, "channel_name");
        String aliasChannelName = getJsonString(jsonObject, "aliasChannelName");
        if (!TextUtils.isEmpty(aliasChannelName)) channel_name = aliasChannelName;
        int node_id = getJsonInt(jsonObject, "node_id"); //-1
        String message_id = getJsonString(jsonObject, "message_id");
        String comment_id = getJsonString(jsonObject, "comment_id");
        int register_id = getJsonInt(jsonObject, "register_id");
        String register_name = getJsonString(jsonObject, "register_name");
        String register_uniqueName = getJsonString(jsonObject, "register_uniqueName");
        String profile_img = getJsonString(jsonObject, "profile_img");
        String position_name = getJsonString(jsonObject, "position_name");
        String dept_name = getJsonString(jsonObject, "dept_name");
        ArrayList<String> register_name_m = null;
        try {
            register_name_m = jsonToArrayList(jsonObject.getJSONArray("register_name_m"));
            register_name = register_name_m.get(new LanguageUtil().getSystemLanguageTypeIndex());
        } catch (JSONException e) {
        }
        ArrayList<String> position_name_m = null;
        try {
            position_name_m = jsonToArrayList(jsonObject.getJSONArray("position_name_m"));
            position_name = position_name_m.get(new LanguageUtil().getSystemLanguageTypeIndex());
        } catch (JSONException e) {
        }
        ArrayList<String> dept_name_m = null;
        try {
            dept_name_m = jsonToArrayList(jsonObject.getJSONArray("dept_name_m"));
            dept_name = dept_name_m.get(new LanguageUtil().getSystemLanguageTypeIndex());
        } catch (JSONException e) {
        }
        String register_date = getJsonString(jsonObject, "register_date");
        String dateline = getJsonString(jsonObject, "dateline");
        String categorypath = getJsonString(jsonObject, "categorypath");
        String content = getJsonString(jsonObject, "content");
        String fontColor = getJsonString(jsonObject, "fontColor");
        int reply_cnt = getJsonInt(jsonObject, "reply_cnt");
        String attach_edms_id = getJsonString(jsonObject, "attach_edms_id");
        String file_type = getJsonString(jsonObject, "file_type"); //"txt" "image\/png"
        String attach_file = getJsonString(jsonObject, "attach_file");
        String attach_image = getJsonString(jsonObject, "attach_image"); //"test.jpg", 이미지 아니면 항목 없음
        ArrayList<String> framefile = new ArrayList<>();
        if (jsonObject.has("framefile")) {
            try {
                JSONArray frameJson = jsonObject.getJSONArray("framefile");
                for (int j = 0; j < frameJson.length(); j++) {
                    JSONObject frow = frameJson.getJSONObject(j);
                    String frameFileName = getJsonString(frow, "filename");

                    framefile.add(frameFileName);
                }
            } catch (JSONException e) {
            }
        }

        String view_mobile = getJsonString(jsonObject, "view_mobile"); //모바일 권한 Y N
        String mobileViewYN = getJsonString(jsonObject, "mobileViewYN"); //모바일 권한 Y N
        String view_search = getJsonString(jsonObject, "view_search");
        String ispinned = getJsonString(jsonObject, "ispinned");
        String favorite_yn = getJsonString(jsonObject, "favorite_yn");
        String alarm_yn = getJsonString(jsonObject, "alarm_yn");
        String link_cid = getJsonString(jsonObject, "link_cid");
        String ori_type = getJsonString(jsonObject, "ori_type");
        OriMessage ori_message  = null;



        String link_oid = getJsonString(jsonObject, "link_oid");
        String link_title = getJsonString(jsonObject, "link_title");
        String link_url = getJsonString(jsonObject, "link_url");
        //int status                  = getJsonInt(jsonObject, "status");
        String lock_type = getJsonString(jsonObject, "lock_type"); //"CHECKOUT", 포스트
        String service = getJsonString(jsonObject, "service");

        //메시지 검색시
        String sub_message_id = getJsonString(jsonObject, "sub_message_id"); //중심 메시지 ID
        String gubun = getJsonString(jsonObject, "gubun"); //PRE, CURR, NEXT 앞뒤 플래그
        String p_img_ = getJsonString(jsonObject, "p_img"); //익명 이미지
        int like_cnt_ = getJsonInt(jsonObject, "like_cnt");


        if (!TextUtils.isEmpty(type)) {
            switch (type) {
                case "M"://메시지/url
                    //case "R"://VC요청

                    ChatMessage chtObj = new ChatMessage(address, type, channel_id, message_id,
                            register_id, register_name, register_uniqueName, profile_img, position_name, dept_name,
                            register_date, categorypath, content, fontColor, reply_cnt,
                            mobileViewYN, ispinned, favorite_yn,
                            link_cid, link_oid, link_title, link_url ,sub_message_id, gubun, ori_type, ori_message);
                    // 익명 이미지 처리
                    chtObj.p_img = p_img_;
                    //추천수 처리
                    chtObj.like_cnt = like_cnt_;
                    // OpenGraph 처리
                    JSONArray ogListArray = null;
                    try {
                        if (jsonObject.has("og")) {
                            ogListArray = jsonObject.getJSONArray("og");
                            if (ogListArray != null && ogListArray.length() > 0) {
                                chtObj.og = new ArrayList<>();
                                for (int i = 0; i < ogListArray.length(); i++) {
                                    JSONObject ogObj = ogListArray.getJSONObject(i);
                                    String adescription = getJsonString(ogObj, "description");
                                    String aimage = getJsonString(ogObj, "image");
                                    String atitle = getJsonString(ogObj, "title");
                                    String aurl = getJsonString(ogObj, "url");
                                    chtObj.og.add(new OpenGraphData(adescription, aimage, atitle, aurl));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return chtObj;
                case "P"://Post
                    PostMessage pstObj = new PostMessage(address, type, channel_id, message_id,
                            register_id, register_name, register_uniqueName, profile_img, position_name, dept_name,
                            register_date, categorypath, content, reply_cnt,
                            mobileViewYN, ispinned, favorite_yn,
                            link_oid, link_title, link_url, sub_message_id, gubun);
                    // 익명 이미지 처리
                    pstObj.p_img = p_img_;
                    //추천수 처리
                    pstObj.like_cnt = like_cnt_;
                    //IFlow 처리
                    if (jsonObject.has("iFlowCommentData")) {
                        pstObj.isIf = true;
                    }
                    return pstObj;
                case "N"://Pinned add
                case "C"://Comment
                case "O"://Pinned remomve
                case "B"://Bot
                    //oId 이용 첨부글 조회
                    ChatMessage pnnObj = new ChatMessage(address, type, channel_id, message_id,
                            register_id, register_name, register_uniqueName, profile_img, position_name, dept_name,
                            register_date, categorypath, content, fontColor, reply_cnt,
                            mobileViewYN, ispinned, favorite_yn,
                            link_cid, link_oid, link_title, link_url, sub_message_id, gubun, ori_type, ori_message );
                    // 익명 이미지 처리
                    pnnObj.p_img = p_img_;
                    //추천수 처리
                    pnnObj.like_cnt = like_cnt_;
                    return pnnObj;
                case "R"://원격
                    ChatMessage rmoObj = new ChatMessage(address, type, channel_id, message_id,
                            register_id, register_name, register_uniqueName, profile_img, position_name, dept_name,
                            register_date, categorypath, AppStarter.applicationContext.getString(R.string.system_no_service_type_r_message), fontColor, reply_cnt,
                            mobileViewYN, ispinned, favorite_yn,
                            link_cid, link_oid, link_title, link_url, sub_message_id, gubun,ori_type, ori_message);
                    // 익명 이미지 처리
                    rmoObj.p_img = p_img_;
                    //추천수 처리
                    rmoObj.like_cnt = like_cnt_;
                    return rmoObj;
                case "X"://Rich Notification
                case "Y"://Rich Notification
                    /*String answer = getJsonString(jsonObject, "rnanswer");
                    content = getJsonString(jsonObject, "rndata");
                    //content = content.replace("\"bodystyle\":\"grid\"",  "\"bodystyle\":\"none\"");
                    RichNotificationMessage richNotificationMessageObj = new RichNotificationMessage(address, type, channel_id, message_id,
                            register_id, register_name, register_uniqueName, profile_img, position_name, dept_name,
                            register_date, categorypath, content, fontColor, reply_cnt,
                            mobileViewYN, ispinned, favorite_yn,
                            link_cid, link_oid, link_title, link_url, sub_message_id, gubun, answer);
                    // 익명 이미지 처리
                    richNotificationMessageObj.p_img = p_img_;
                    //추천수 처리
                    richNotificationMessageObj.like_cnt = like_cnt_;
                    return richNotificationMessageObj;*/
                default:
                    ChatMessage dftObj = new ChatMessage(address, type, channel_id, message_id,
                            register_id, register_name, register_uniqueName, profile_img, position_name, dept_name,
                            register_date, categorypath, AppStarter.applicationContext.getString(R.string.system_no_service_type_message), fontColor, reply_cnt,
                            mobileViewYN, ispinned, favorite_yn,
                            link_cid, link_oid, link_title, link_url,sub_message_id, gubun, ori_type, ori_message);
                    // 익명 이미지 처리
                    dftObj.p_img = p_img_;
                    //추천수 처리
                    dftObj.like_cnt = like_cnt_;
                    return dftObj;
            }
        }

        if (service.equals("updatePinned")) {
            MessageManager.getInstance().postNotification(MessageManager.pinnedMessage, message_id, ispinned);
        }
        if (service.equals("postUpdate")) {
            MessageManager.getInstance().postNotification(MessageManager.receiveUpdateMessage, channel_id, message_id, link_title, content);
        }
        if (service.equals("delete")) {
            MessageManager.getInstance().postNotification(MessageManager.receiveDeleteMessage, channel_id, message_id);
        }
        if (service.equals("removecomment")) {
            MessageManager.getInstance().postNotification(MessageManager.deleteComment, channel_id, message_id, comment_id, reply_cnt);
            return null;
        }
        if (service.equals("todoupdate")) {
            String seq = getJsonString(jsonObject, "seq");
            String status = getJsonString(jsonObject, "status");
            String duedate = getJsonString(jsonObject, "duedate");
            int progress = getJsonInt(jsonObject, "progress");
            MessageManager.getInstance().postNotification(MessageManager.updateTodo, channel_id, message_id, seq, status, duedate, progress);
            return null;
        }
        if (service.equals("removetodo")) {
            String seq = getJsonString(jsonObject, "seq");
            MessageManager.getInstance().postNotification(MessageManager.removeTodo, channel_id, message_id, seq);
            return null;
        }
        if (service.equals("updatevoteanswer")) {
            try {
                JSONObject answerObject = jsonObject.getJSONObject("answer");
                int id = getJsonInt(answerObject, "id");
                int answerCnt = getJsonInt(answerObject, "answerCnt");
                boolean check = getJsonBoolean(answerObject, "check");
                String answer = getJsonString(answerObject, "answer");
                int answer_register_id = getJsonInt(answerObject, "register_id");
                String answer_register_name = getJsonString(answerObject, "register_name");
                String answer_dept_name = getJsonString(answerObject, "dept_name");
                String answer_position_name = getJsonString(answerObject, "position_name");

                MessageManager.getInstance().postNotification(MessageManager.updatevoteanswer, channel_id, message_id, id, answerCnt, check, answer, answer_register_id, answer_register_name, answer_dept_name, answer_position_name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        //입력중
        if (service.equals("startWritingMessage")) {

        }
        if (service.equals("stopWritingMessage")) {

        }

        return null;
    }

    //login///////////////////////////////////////////////////////////////////////////////////////////////
    public class LoginAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            if (params.length > 0)
                return loginWeb(params[0], params[1], params[2]);
            else
                return PrefManager.getInstance(AppStarter.applicationContext).getValue(Common.PREF_SESSION_KEY, "");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result == null || result.equals("fail")) {
                listener.onError("{\"api\":\"auth\", \"message\":\"connect failed\"}");
            } else {
                if (vertxClient != null) {
                    vertxClient.disconnect();
                }
                vertxClient = new VertxClient(result);
                vertxClient.setOnSockJsListener(listener);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }
    private String loginWeb(String id, String pass, String phone) {
        try {
//            if(pass.length()==0) pass = "1";
//            pass = URLEncoder.encode(pass);
            String url = Common.WEB_URL + "?USER=" + id + "&PASS=" + pass + "&CLIENT=2&PHONE=" + phone;
            if (TextUtils.isEmpty(pass)) {
                //http://110.45.156.137:10209/loginMOBILE
                url = Common.MOBILE_URL + "?USER=" + id + "&CLIENT=2";
            }
            Log.d(getClass().getSimpleName(), "connect to " + url);
            HttpURLConnection httpURLConnection = null;
            if (Common.WEB_URL.startsWith("https")) {
                SSLConnect ssl = new SSLConnect();
                httpURLConnection = ssl.postHttps(url, 1000, 1000);
            } else {
                httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            }

            String result = null;
            try {
                InputStreamReader reader = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader br = new BufferedReader(reader);

                result = br.readLine();
                Log.d(getClass().getSimpleName(), "result = " + result);
                while (true) {
                    String s = br.readLine();
                    if (s == null) break;
                }
            } catch (SocketTimeoutException e) {
            }

            return result;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private VertxClient.OnSockJsListener listener = new VertxClient.OnSockJsListener() {
        @Override
        public void onOpen(String json) {
//            DialogManager.getInstance().dismiss(DialogManager.PROGRESS);
            ServerInterfaceRetryManager.clearTryReconnectCount();
            //MessageManager.getInstance().postNotification(MessageManager.log, "onOpen", json);
            MessageManager.getInstance().postNotification(MessageManager.status, 1);
            ServerInterfaceRetryManager.getInstance().changeNetworkStatus(ServerInterfaceRetryManager.NetworkStatusType.Open, "onOpen:" + json);
        }

        @Override
        public void onMessage(String json) {
           //MessageManager.getInstance().postNotification(MessageManager.log, "onMessage", json);
            ServerInterfaceRetryManager.setLastRecvMessageTime();
            try {
                JSONObject jObj = new JSONObject(json);
                String api = jObj.getString("api");
                String result = getJsonString(jObj, "result");

                if (MainActivity.isHasApiTimeAuth) {
                    if (TextUtils.equals(api, "websocket.apiNotificationKey")) {
                        MessageManager.getInstance().setApiTimeMapEndTime("websocket.apiNotificationKey");
                    }
                }

                if (handlerMap.size() > 0) {
                    Handler handler = handlerMap.get(api); //auth 핸들러 가져옴
                    if (handler != null) {
                        Message msg = handler.obtainMessage();

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("api", api);
                        bundle.putSerializable("result", result);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(String json) {
            //MessageManager.getInstance().postNotification(MessageManager.log, "onError", json);
            try {
                JSONObject jObj = new JSONObject(json);
                String api = jObj.getString("api");
                String message = jObj.getString("message");

                if (handlerMap.size() > 0) {
                    Handler handler = handlerMap.get(api);
                    if (handler != null) {
                        Message msg = handler.obtainMessage();

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("api", api);
                        bundle.putSerializable("message", message);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ServerInterfaceRetryManager.getInstance().changeNetworkStatus(ServerInterfaceRetryManager.NetworkStatusType.Error, "onError:" + json);
        }

        @Override
        public void onClose(String json) {
            //MessageManager.getInstance().postNotification(MessageManager.log, "onClose", json);
            MessageManager.getInstance().postNotification(MessageManager.disconnect);
            ServerInterfaceRetryManager.getInstance().changeNetworkStatus(ServerInterfaceRetryManager.NetworkStatusType.Close, "onClose:" + json);
        }

        @Override
        public void onStatus(int status) {
            /*
              EventBus.CONNECTING = 0;
              EventBus.OPEN = 1;
              EventBus.CLOSING = 2;
              EventBus.CLOSED = 3;
            * */
            MessageManager.getInstance().postNotification(MessageManager.status, status);
        }
    };

    HttpAsyncTask.HttpAsyncTaskResultListener httpAsyncTaskResultListener = new HttpAsyncTask.HttpAsyncTaskResultListener() {
        @Override
        public void onResult(HashMap<String, Object> resultMap) {
            try {
                int status = ((Integer) resultMap.get(HttpAsyncTask.STATUS_CODE));    // API 통신 결과 코드
                String response = ((String) resultMap.get(HttpAsyncTask.RESPONSE_MSG));    // API 통신 결과 메세지
                switch (status) {
                    case HttpURLConnection.HTTP_OK:
                        if (response == null || response.equals("fail")) {
                            listener.onError("{\"api\":\"auth\", \"message\":\"connect failed\"}");
                        } else {
                            if (vertxClient != null) {
                                vertxClient.disconnect();
                            }
                            vertxClient = new VertxClient(response);
                            vertxClient.setOnSockJsListener(listener);
                        }
                        break;
                    default:
                        listener.onError("{\"api\":\"auth\", \"message\":\"connect failed\"}");
                        break;
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
                listener.onError("{\"api\":\"auth\", \"message\":\"connect failed\"}");
            }
        }
    };
    HttpAsyncTask.HttpAsyncTaskResultListener gmpResultListener = new HttpAsyncTask.HttpAsyncTaskResultListener() {
        @Override
        public void onResult(HashMap<String, Object> resultMap) {
            try {
                int status = ((Integer) resultMap.get(HttpAsyncTask.STATUS_CODE));    // API 통신 결과 코드
                String response = ((String) resultMap.get(HttpAsyncTask.RESPONSE_MSG));    // API 통신 결과 메세지

                switch (status) {
                    case HttpURLConnection.HTTP_OK:
                        try {
                            JSONObject resultJsonObject = new JSONObject(response);
                            String result = getJsonString(resultJsonObject, "result");
                            String resultMessage = getJsonString(resultJsonObject, "resultMessage");
                            String uuid = getJsonString(resultJsonObject, "uuid");
                            if ("1000".equals(result)) {
                                if (vertxClient != null) {
                                    vertxClient.disconnect();
                                }
                                vertxClient = new VertxClient(uuid);
                                vertxClient.setOnSockJsListener(listener);
                            } else {
                                listener.onMessage("{\"api\":\"auth\", \"result\":" + response + "}");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onError("{\"api\":\"auth\", \"message\":\"JSONException\"}");
                        }
                        break;
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
                listener.onError("{\"api\":\"auth\", \"message\":\"NullPointerException\"}");
            }
        }
    };
    /*채널 목록*/
    public void selectChannelList() {
        if (vertxClient != null) {
            if (MainActivity.isHasApiTimeAuth) {
                MessageManager.getInstance().setApiTimeMapStartTime("bizrunner.selectChannelList");
            }
            handlerMap.put(API.selectChannelList, channelListHandler); //서버로 받은 msg 핸들러가 받아서 처리
            vertxClient.selectChannelList(); //서버로 조회할 내용 보냄
        }
    }

    /*노티키 등록*/
    public void notiKey(Task<String> notiKey, String baiduKey, String mmc) {
        if (vertxClient != null) {
            if (MainActivity.isHasApiTimeAuth) {
                MessageManager.getInstance().setApiTimeMapStartTime("websocket.apiNotificationKey");
            }
            vertxClient.notiKey(notiKey, baiduKey, mmc);
        }
    }
    /*사용자 프로필 정보*/
    public void selectProfile() {
//        if (BuildConfig.DEBUG) { // (YS)
//            handlerMap.put(API.selectProfileSecurity, profileSecurityHandler);
//        } else {
        if (vertxClient != null) {
            if (MainActivity.isHasApiTimeAuth) {
                MessageManager.getInstance().setApiTimeMapStartTime("websocket.selectProfile");
            }
            handlerMap.put(API.selectProfile, profileHandler);
            vertxClient.selectProfile();
        }
    }
    private Handler profileHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.getData().getSerializable("result");
            try {
                if (MainActivity.isHasApiTimeAuth) {
                    MessageManager.getInstance().setApiTimeMapEndTime("websocket.selectProfile");
                }
                JSONObject resultObject = new JSONObject(result);
                if (!resultObject.getString("body").equals("fail")) {
                    JSONObject bodyObject = resultObject.getJSONObject("body");

                    int userID = getJsonInt(bodyObject, "userID");
                    String nameLang = getJsonString(bodyObject, "nameLang");
                    String uniqueName = getJsonString(bodyObject, "uniqueName");
                    String positionName = getJsonString(bodyObject, "positionName");
                    String deptCode = getJsonString(bodyObject, "deptCode");
                    String deptName = getJsonString(bodyObject, "deptName");
                    String mobilePhone = getJsonString(bodyObject, "mobilePhone");
                    String officePhone = getJsonString(bodyObject, "officePhone");
                    String email = getJsonString(bodyObject, "email");
                    String subEmail = getJsonString(bodyObject, "subEmail");
                    String VC = getJsonString(bodyObject, "VC");
                    String employee = getJsonString(bodyObject, "employee");

                    //일정검색 개선 - 2020.10.6 (추가:채널 일정 검색 기간)
                    String ewsdays = getJsonString(bodyObject, "ewsdays");//채널 일정 검색 기간

                    String lastChannelID = getJsonString(bodyObject, "lastChannelID");
                    String companyCode = getJsonString(bodyObject, "companyCode");
                    String companyName = getJsonString(bodyObject, "companyName");

                    //PL,팀장 표기 (개인프로필 팝업) - 2021.2.17(jpstnNam 추가)
                    String jpstnNam = getJsonString(bodyObject, "jpstnNam");

                    //근무조 정보 추가 - 2021.2.18(wgrpNam 추가)
                    String wgrpNam = getJsonString(bodyObject, "wgrpNam");

                    String subCompanyCode = getJsonString(bodyObject, "subCompanyCode");
                    String subCompanyName = getJsonString(bodyObject, "subCompanyName");
                    int languageType = getJsonInt(bodyObject, "languageType");
                    int accessLevel = getJsonInt(bodyObject, "accessLevel");
                    String historycount = getJsonString(bodyObject, "historycount");
                    String location = getJsonString(bodyObject, "location");
                    String idletime = getJsonString(bodyObject, "idletime");
                    String chatbotpm = getJsonString(bodyObject, "chatbotpm");
                    String theme = getJsonString(bodyObject, "theme");
                    String translationLanguageType = getJsonString(bodyObject, "tranlationLanguageType");
                    if (TextUtils.isEmpty(translationLanguageType)) translationLanguageType = "en";
                    String translationSourceLanguageType = getJsonString(bodyObject, "translationSourcelanguagetype");
                    if (TextUtils.isEmpty(translationSourceLanguageType))
                        translationSourceLanguageType = "en";
                    String thumbnail = getJsonString(bodyObject, "thumbnail");
                    String isshowwriting = getJsonString(bodyObject, "isshowwriting");
                    String backYN = getJsonString(bodyObject, "backYN");
                    int messageedittime = getJsonInt(bodyObject, "messageedittime");

                    ArrayList<HashMap<String, String>> security = new ArrayList<>();
                    JSONArray securityListJson = bodyObject.getJSONArray("securityList");
                    {
                        for (int i = 0; i < securityListJson.length(); i++) {
                            JSONObject row = securityListJson.getJSONObject(i);
                            String openYN = getJsonString(row, "openYN");
                            String appType = getJsonString(row, "appType");
                            String msgType = getJsonString(row, "msgType");
                            HashMap<String, String> map = new HashMap<>();
                            map.put("openYN", openYN);
                            map.put("appType", appType);
                            map.put("msgType", msgType);
                            security.add(map);
                        }
                    }


                    User userProfile = new User(userID, nameLang, uniqueName, positionName, deptCode, deptName, mobilePhone, officePhone, email, companyCode, companyName, accessLevel, lastChannelID, languageType, security, translationLanguageType, translationSourceLanguageType, isshowwriting, backYN, messageedittime, ewsdays, jpstnNam, wgrpNam);
                    MessageManager.getInstance().postNotification(MessageManager.profile, userProfile);

                    /*int preChannelID = Integer.parseInt(userProfile.lastChannelId);
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("lastChannelID", (MessageManager.getInstance().getChannelName(preChannelID)));*/
                } else {
                    ServerInterfaceManager.getInstance().sendErrlog("selectProfile", "no profile data");
                    ServerInterfaceRetryManager.getInstance().resendSelectProfile();
                }

            } catch (JSONException e) {
                ServerInterfaceManager.getInstance().sendErrlog("selectProfile", "JSONException");
                ServerInterfaceRetryManager.getInstance().resendSelectProfile();
                e.printStackTrace();
            }
        }
    };
    /*
     * 채널 목록 조회
     * */
    private Handler channelListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String result = (String) msg.getData().getSerializable("result");
            try {
                if (MainActivity.isHasApiTimeAuth) {
                    MessageManager.getInstance().setApiTimeMapEndTime("bizrunner.selectChannelList");
                }
                JSONObject resultObject = new JSONObject(result); //json형식을 객체형식으로 변환 생성
//                if(BuildConfig.DEBUG) Common.log("D", "channelListHandler","channelListHandler = " + resultObject);
                if (!resultObject.getString("body").equals("fail")) {
                    JSONObject bodyObject = resultObject.getJSONObject("body");

                    //채널리스트 통합(채널리스트에 hyfeedback, hytube 통합) - 2021.6.25
                    //하이피드백 리스트

                    //하이튜브 리스트
                    //
                    MessageManager.getInstance().channelListArray.clear();
                    if(bodyObject.has("bizWorksChannelList")) {
                        JSONArray channelListJson = bodyObject.getJSONArray("bizWorksChannelList");
//                        if (BuildConfig.DEBUG)
//                            Common.log("D", "channelListHandler", "channelListJson = " + channelListJson);

                        for (int i = 0; i < channelListJson.length(); i++) { //채널 2개 있음으로 2번 반복
                            JSONObject row = channelListJson.getJSONObject(i);
                            JSONObject info = row.getJSONObject("channel_info");
                            int channel_group_id = getJsonInt(info, "channel_group_id");
                            String channel_group_name = getJsonString(info, "channel_group_name");
                            int channel_id = getJsonInt(info, "channel_id");
                            String channel_name = getJsonString(info, "channel_name");
                            String aliasChannelName = getJsonString(info, "aliasChannelName");
                            if (!TextUtils.isEmpty(aliasChannelName))
                                channel_name = aliasChannelName;
                            int channel_type = getJsonInt(info, "channel_type");//0 공개 1 전체공개 2 비공개 3 익명 4 공지 5 하이튜브

                            //hyTUBE 익명방 추가- 2020.10.20
                            String sysName = getJsonString(info, "sysName");//하이튜브( HYTUBE), 하이피드백(HYFB1 , HYFB2)

                            int channel_level = getJsonInt(info, "channel_level");
                            int cntU = getJsonInt(info, "userCnt");
                            String m_open = getJsonString(info, "m_open");//YN
                            String active = getJsonString(info, "active");
                            String docm_search = getJsonString(info, "docm_search");//YN
                            String doc_search = getJsonString(info, "doc_search");//YN

                            // 채널 중복 삽입 체크
                            boolean isHaveChannel = false;
                            for (ChannelObject channel : MessageManager.getInstance().channelListArray) {
                                if (channel_id != -1 && channel.channelId == channel_id) {
                                    isHaveChannel = true;
                                    break;
                                }
                            }
                            if (isHaveChannel) continue;
                            if ("Y".equals(m_open) || channel_group_id > 0) {
//                                if (BuildConfig.DEBUG)
//                                    Common.log("D", "CHECKHYTUBE", "channel_type channelListArray= " + channel_type + ", sysName=" + sysName + "," + channel_name);
                                MessageManager.getInstance().channelListArray.add(new ChannelObject(channel_group_id, channel_group_name, channel_id, channel_name, channel_type, sysName, cntU, m_open, active, docm_search, doc_search));
                            }

                            //여기서 채널명으로 메뉴 이름 변경 어떰?

                        }
                    }

                    MessageManager.getInstance().groupChannelListArray.clear();
                    if(bodyObject.has("groupInChannelList")) {
                        JSONArray groupChannelListJson = bodyObject.getJSONArray("groupInChannelList");
                        for (int i = 0; i < groupChannelListJson.length(); i++) {
                            JSONObject row = groupChannelListJson.getJSONObject(i);
                            JSONObject info = row.getJSONObject("channel_info");
                            int channel_group_id = getJsonInt(info, "channel_group_id");
                            String channel_group_name = getJsonString(info, "channel_group_name");
                            int channel_id = getJsonInt(info, "channel_id");
                            String channel_name = getJsonString(info, "channel_name");
                            String aliasChannelName = getJsonString(info, "aliasChannelName");
                            if (!TextUtils.isEmpty(aliasChannelName))
                                channel_name = aliasChannelName;
                            int channel_type = getJsonInt(info, "channel_type");

                            //hyTUBE 익명방 추가- 2020.10.20
                            String sysName = getJsonString(info, "sysName");//하이튜브( HYTUBE), 하이피드백(HYFB1 , HYFB2)

                            int cntU = getJsonInt(info, "userCnt");
                            String m_open = getJsonString(info, "m_open");
                            String active = getJsonString(info, "active");
                            String docm_search = getJsonString(info, "docm_search");
                            //검색 미노출 채널 가이드 표기 - 2021.1.26
                            String doc_search = getJsonString(info, "doc_search");//YN
                            // 채널 중복 삽입 체크
                            boolean isHaveChannel = false;
                            for (ChannelObject channel : MessageManager.getInstance().groupChannelListArray) {
                                if (channel.channelId == channel_id) {
                                    isHaveChannel = true;
                                    break;
                                }
                            }
                            if (isHaveChannel) continue;
                            if ("Y".equals(m_open)) {
//                            if (BuildConfig.DEBUG)
//                                Common.log("D", "CHECKHYTUBE", "channel_type groupChannelListArray= " + channel_type + ", sysName=" + sysName +","+ channel_name);
                                MessageManager.getInstance().groupChannelListArray.add(new ChannelObject(channel_group_id, channel_group_name, channel_id, channel_name, channel_type, sysName, cntU, m_open, active, docm_search, doc_search));
                            }
                        }
                    }

                }

                MessageManager.getInstance().postNotification(MessageManager.channelList);
                Log.e("TLOG", "채널리스트 포스트됨? oo");
                //채널리스트 통합(채널리스트에 hyfeedback, hytube 통합) - 2021.6.25


            } catch (JSONException e) {
                ServerInterfaceManager.getInstance().sendErrlog("channelList", "JSONException");

            }
        }
    };

    /*채널 정보*/
    public void getChannelInfo(String channelId) {
        if (vertxClient != null) {
            if (MainActivity.isHasApiTimeAuth) {
                MessageManager.getInstance().setApiTimeMapStartTime("websocket.selectChannelInfoSummary");
            }
            handlerMap.put(API.selectChannelInfoSummary, channelInfoHandler);
            vertxClient.getChannelInfo(channelId);
        }
    }
    /*
     * 채널 Info 조회
     * */
    private Handler channelInfoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.getData().getSerializable("result");
            try {
                ServerInterfaceRetryManager.getInstance().removeRetry(API.selectChannelInfoSummary);
                if (MainActivity.isHasApiTimeAuth) {
                    MessageManager.getInstance().setApiTimeMapEndTime("websocket.selectChannelInfoSummary");
                }
                JSONObject resultObject = new JSONObject(result);

                if (!resultObject.getString("body").equals("fail")) {
                    JSONObject bodyObject = resultObject.getJSONObject("body");

                    // 익명 설정 여부
                    boolean check_nick = getJsonBoolean(bodyObject, "nick_check");
                    String unread_message_id = getJsonString(bodyObject, "unread_message_id");

                    JSONObject infoObject = bodyObject.getJSONObject("channel_info");

                    int channel_id = getJsonInt(infoObject, "channel_id");
                    String channel_name = getJsonString(infoObject, "channel_name");
                    String aliasChannelName = getJsonString(infoObject, "aliasChannelName");
                    /*ArrayList<String> channel_name_m = null;
                    try {
                        channel_name_m = jsonToArrayList(infoObject.getJSONArray("channel_name_m"));
                        channel_name = channel_name_m.get(new LanguageUtil().getSystemLanguageTypeIndex());
                    } catch (JSONException e) {}*/
                    int channel_member_cnt = getJsonInt(infoObject, "channel_member_cnt");
                    //채널
                    int channel_group_id = getJsonInt(infoObject, "channel_group_id");
                    String channel_group_name = getJsonString(infoObject, "channel_group_name");
                    String channel_sysop_name = getJsonString(infoObject, "channel_sysop_name");
                    ArrayList<Integer> sysop_ids = new ArrayList<>();
                    if (!channel_sysop_name.equals("")) {
                        JSONArray sysopArray = infoObject.getJSONArray("channel_sysop_id");
                        for (int i = 0; i < sysopArray.length(); i++) {
                            int sysop_id = sysopArray.getInt(i);
                            sysop_ids.add(sysop_id);
                        }
                    }
                    int channel_type = getJsonInt(infoObject, "channel_type");

                    //hyTUBE 익명방 추가- 2020.10.20
                    String  sysName = getJsonString(infoObject, "sysName");//하이튜브( HYTUBE), 하이피드백(HYFB1 , HYFB2)

                    String m_open = getJsonString(infoObject, "m_open");
                    String channel_intro = getJsonString(infoObject, "channel_intro");
                    //채널 공지 개선 - 2020.12.3
                    String notice = getJsonString(infoObject, "channel_notice");
                    String notice1 = getJsonString(infoObject, "channel_notice1");
                    String notice2 = getJsonString(infoObject, "channel_notice2");
                    ArrayList<String> notices = new ArrayList<>();
                    if(!channel_sysop_name.equals("")) {
//                        JSONArray noticeArray = infoObject.getJSONArray("channel_notice");
//                        for (int i = 0; i < 3; i++) {
//                            String notice = noticeArray.getString(i);
                        if(!TextUtils.isEmpty(notice) && !notice.trim().equals("")) notices.add(notice);
                        if(!TextUtils.isEmpty(notice1)&& !notice1.trim().equals("")) notices.add(notice1);
                        if(!TextUtils.isEmpty(notice2)&& !notice2.trim().equals("")) notices.add(notice2);
//                        }
                    }
                    //채널 공지 펼침 여부
//                    JSONObject channel_set = bodyObject.getJSONObject("channel_set");
//                    String NOTICEYN = getJsonString(channel_set, "NOTICEYN");

                    String doc_search = getJsonString(infoObject, "doc_search");
                    String docm_search = getJsonString(infoObject, "docm_search");
                    String alarmYN = getJsonString(infoObject, "alarmYN");
                    String memberAddYn = getJsonString(infoObject, "memberadd_yn");
                    //DM채널
                    String channel_members = getJsonString(infoObject, "channel_members");
                    String last_message = getJsonString(infoObject, "last_message");
                    String flag = getJsonString(infoObject, "flag");
                    boolean chatbot = getJsonBoolean(infoObject, "chatbot");
                    boolean hrbot = getJsonBoolean(infoObject, "hrbot");
                    boolean _isFreezing = getJsonBoolean(bodyObject, "isFreezing");
//                    String thumbUrl = getJsonString(infoObject, "thumbUrl");

//                    if(chatbot && hrbot){
//                        MessageManager.getInstance().postNotification(MessageManager.channelInfo);
//                        return;
//                    }

                    /*JSONObject msgCountObject = bodyObject.getJSONObject("channel_msg_count");
                    int pinned_count = getJsonInt(msgCountObject, "pinned_count");
                    int file_count = getJsonInt(msgCountObject, "file_count");
                    int post_count = getJsonInt(msgCountObject, "post_count");*/

                    /*구성원 팝업창 Open채널폭파시 추가 개발 -2020.6.23
                      -  구성원 팝업창 Open채널폭파시, 폭파후 15초내 구성원이 해당채널 입장시 중지요청 팝업창 보이도록 처리
                     */
                    if (bodyObject.has("delInfo")) {

                        JSONObject delInfoObject = bodyObject.getJSONObject("delInfo");
                        boolean isComplete = getJsonBoolean(delInfoObject, "result");
                        String errorCode = getJsonString(delInfoObject, "code");

                        String startTime = getJsonString(delInfoObject, "start_time");
                        String currentTime = getJsonString(delInfoObject, "current_time");
                        int duration = getJsonInt(delInfoObject, "duration");

                    }
                    ChannelInfo channel = new ChannelInfo(channel_id, channel_name, channel_type, sysName, channel_intro, "N", notices, channel_sysop_name, sysop_ids, channel_members, m_open, check_nick, doc_search);
                    channel.aliasChannelName = aliasChannelName;
                    channel.dmType = flag;
                    channel.userCnt = channel_member_cnt;
                    channel.alarmYN = alarmYN;
                    channel.memberAddYn = memberAddYn;
                    channel.chatbot = chatbot;
                    channel.hrbot = hrbot;
                    channel.isFreezing = _isFreezing;
                    //hyTUBE 익명방 추가- 2020.10.20
                    channel.channelType = channel_type;
                    channel.sysName = sysName;

                    //검색 미노출 채널 가이드 표기 - 2021.1.26
                    channel.docSearch = doc_search;

                    // 익명 채널 이미지 정보
                    String channel_notics = getJsonString(bodyObject, "notice");
                    if (!TextUtils.isEmpty(channel_notics)) {
                        JSONArray noticsArray = bodyObject.getJSONArray("notice");
                        for (int i = 0; i < noticsArray.length(); i++) {
                            JSONObject row = noticsArray.getJSONObject(i);
                            int row_seq = getJsonInt(row, "seq");
                            if (row_seq == 1) {
                                row_seq = 1;
                            }
                            int row_channelID = getJsonInt(row, "channelID");
                            String row_url = getJsonString(row, "url");
                            int row_sortkey = getJsonInt(row, "sortkey");

                        }
                    }

                    MessageManager.getInstance().postNotification(MessageManager.channelInfo, channel, unread_message_id);
                } else {

                    // 채널 정보 조회 실패시 재요청 방지처리 해제
                    MessageManager.getInstance().postNotification(MessageManager.updateChatList, "-1", -999, -999, -1);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    /* 채널 정보 조회 후 채널 이동 */
    public void selectChannelnfoAndgoLastChannel(String channelId) {
        if (vertxClient != null) {
            handlerMap.put(API.selectChannelInfoSummary, channelInfoAndgoLastChannelHandler);
            vertxClient.getChannelInfo(channelId);
        }
    }
    /*
     * 채널 Info 조회후 최근 채널 이동
     * */
    private Handler channelInfoAndgoLastChannelHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.getData().getSerializable("result");
            try {
                ServerInterfaceRetryManager.getInstance().removeRetry(API.selectChannelInfoSummary);
                JSONObject resultObject = new JSONObject(result);

                if (!resultObject.getString("body").equals("fail")) {
                    JSONObject bodyObject = resultObject.getJSONObject("body");
                    // 익명 설정 여부
                    boolean check_nick = getJsonBoolean(bodyObject, "nick_check");
                    String unread_message_id = getJsonString(bodyObject, "unread_message_id");

                    JSONObject infoObject = bodyObject.getJSONObject("channel_info");
                    int channel_id = getJsonInt(infoObject, "channel_id");
                    String channel_name = getJsonString(infoObject, "channel_name");
                    String aliasChannelName = getJsonString(infoObject, "aliasChannelName");

                    int channel_member_cnt = getJsonInt(infoObject, "channel_member_cnt");
                    //채널
                    int channel_group_id = getJsonInt(infoObject, "channel_group_id");
                    String channel_group_name = getJsonString(infoObject, "channel_group_name");
                    String channel_sysop_name = getJsonString(infoObject, "channel_sysop_name");
                    ArrayList<Integer> sysop_ids = new ArrayList<>();
                    if (!channel_sysop_name.equals("")) {
                        JSONArray sysopArray = infoObject.getJSONArray("channel_sysop_id");
                        for (int i = 0; i < sysopArray.length(); i++) {
                            int sysop_id = sysopArray.getInt(i);
                            sysop_ids.add(sysop_id);
                        }
                    }
                    int channel_type = getJsonInt(infoObject, "channel_type");

                    //hyTUBE 익명방 추가- 2020.10.20
                    String  sysName = getJsonString(infoObject, "sysName");//하이튜브( HYTUBE), 하이피드백(HYFB1 , HYFB2)

                    String m_open = getJsonString(infoObject, "m_open");
                    String channel_intro = getJsonString(infoObject, "channel_intro");
                    //채널 공지 개선 - 2020.12.3
                    String channel_notice = getJsonString(infoObject, "channel_notice");
                    String channel_notice1 = getJsonString(infoObject, "channel_notice1");
                    String channel_notice2 = getJsonString(infoObject, "channel_notice2");
                    ArrayList<String> notices = new ArrayList<>();
                    if(!channel_sysop_name.equals("")) {
//                        JSONArray noticeArray = infoObject.getJSONArray("channel_notice");
//                        for (int i = 0; i < 3; i++) {
//                            String notice = noticeArray.getString(i);
                        if(!TextUtils.isEmpty(channel_notice) && !channel_notice.trim().equals("")) notices.add(channel_notice);
                        if(!TextUtils.isEmpty(channel_notice1)&& !channel_notice1.trim().equals("")) notices.add(channel_notice1);
                        if(!TextUtils.isEmpty(channel_notice2)&& !channel_notice2.trim().equals("")) notices.add(channel_notice2);
//                        }
                    }
                    //채널 공지 펼침 여부
                    JSONObject channel_set = bodyObject.getJSONObject("channel_set");
                    String NOTICEYN = getJsonString(channel_set, "NOTICEYN");

                    String doc_search = getJsonString(infoObject, "doc_search");
                    String docm_search = getJsonString(infoObject, "docm_search");
                    String alarmYN = getJsonString(infoObject, "alarmYN");
                    String memberAddYn = getJsonString(infoObject, "memberadd_yn");
                    //DM채널
                    String channel_members = getJsonString(infoObject, "channel_members");
                    String last_message = getJsonString(infoObject, "last_message");
                    String flag = getJsonString(infoObject, "flag");
                    boolean chatbot = getJsonBoolean(infoObject, "chatbot");
                    boolean hrbot = getJsonBoolean(infoObject, "hrbot");

                    /*구성원 팝업창 Open채널폭파시 추가 개발 -2020.6.23
                      -  구성원 팝업창 Open채널폭파시, 폭파후 15초내 구성원이 해당채널 입장시 중지요청 팝업창 보이도록 처리
                     */
                    if (bodyObject.has("delInfo")) {
                        JSONObject delInfoObject = bodyObject.getJSONObject("delInfo");

                        boolean isComplete = getJsonBoolean(delInfoObject, "result");
                        String errorCode = getJsonString(delInfoObject, "code");

                        String startTime = getJsonString(delInfoObject, "start_time");
                        String currentTime = getJsonString(delInfoObject, "current_time");
                        int duration = getJsonInt(delInfoObject, "duration");

                    }

                    // 채널 중복 삽입 체크
                    boolean isHaveChannel = false;
                    String _channel_id = String.valueOf(channel_id);
                    if (_channel_id.startsWith("5")) {
                        for (ChannelObject channel : MessageManager.getInstance().dmListArray) {
                            if (channel.channelId == channel_id) {
                                isHaveChannel = true;
                                break;
                            }
                        }
                        if (!isHaveChannel) {
                            if (channel_member_cnt == 1)
                                MessageManager.getInstance().dmListArray.add(0, new DMChannel(channel_id, channel_name, "-1", last_message, channel_members, channel_member_cnt, "N", "N"));
                            else
                                MessageManager.getInstance().dmListArray.add(new DMChannel(channel_id, channel_name, "-1", last_message, channel_members, channel_member_cnt, "N", "N"));
                        }
                    } else {
                        //하이피드백 채널 분리 - 2021.4.21
                        if(sysName.startsWith("HYFB")){
                            for (ChannelObject channel : MessageManager.getInstance().HFListArray) {
                                if (channel_id != -1 && channel.channelId == channel_id) {
                                    isHaveChannel = true;
                                    break;
                                }
                            }

                            if (!isHaveChannel) {
//                            if(BuildConfig.DEBUG) Common.log("D", "CHECKHYTUBE","channel_type channelListArray= " + channel_type +", sysName="+sysName +","+ channel_name);
                                MessageManager.getInstance().HFListArray.add(new ChannelObject(channel_group_id, channel_group_name, channel_id, channel_name, channel_type, sysName, channel_member_cnt, m_open, "Y", docm_search,doc_search));
                            }

                        }
                        //hyTUBE 채널 분리 - 2020.10.30
                        else if("HYTUBE".equals(sysName)){
                            for (ChannelObject channel : MessageManager.getInstance().hytubeListArray) {
                                if (channel_id != -1 && channel.channelId == channel_id) {
                                    isHaveChannel = true;
                                    break;
                                }
                            }

                            if (!isHaveChannel) {
//                            if(BuildConfig.DEBUG) Common.log("D", "CHECKHYTUBE","channel_type channelListArray= " + channel_type +", sysName="+sysName +","+ channel_name);
                                MessageManager.getInstance().hytubeListArray.add(new ChannelObject(channel_group_id, channel_group_name, channel_id, channel_name, channel_type, sysName, channel_member_cnt, m_open, "Y", docm_search,doc_search));
                            }

                        }else {

                            for (ChannelObject channel : MessageManager.getInstance().channelListArray) {
                                if (channel_id != -1 && channel.channelId == channel_id) {
                                    isHaveChannel = true;
                                    break;
                                }
                            }

                            if (!isHaveChannel) {
//                            if(BuildConfig.DEBUG) Common.log("D", "CHECKHYTUBE","channel_type channelListArray= " + channel_type +", sysName="+sysName +","+ channel_name);
                                MessageManager.getInstance().channelListArray.add(new ChannelObject(channel_group_id, channel_group_name, channel_id, channel_name, channel_type, sysName, channel_member_cnt, m_open, "Y", docm_search,doc_search));
                            }
                        }
                    }

                    MessageManager.getInstance().postNotification(MessageManager.selectChannelnfoAndgoLastChannel, _channel_id);
                } else {
                    // 채널 정보 조회 실패시 재요청 방지처리 해제
                    MessageManager.getInstance().postNotification(MessageManager.selectChannelnfoAndgoLastChannel, "-1");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    /*채널 메시지 목록 조회*/
    public void getChannelMessageList(String channelId, String messageId, String type) {

        if (vertxClient != null) {
            if ("NEXT".equals(type)) {
                handlerMap.put(API.selectMessageList, selectMessageNextListHandler);
            } else {
                if ("RECENT".equals(type) || "FIND".equals(type)) {
                    MessageManager.getInstance().chatsArray.clear();
                    MessageManager.getInstance().incChatsArray.clear();
                    //if (MainActivity.chatMessageAdapter != null) {
                       // MainActivity.chatMessageAdapter.ClearOpenGraphPushQueue();
                    }
                }
                handlerMap.put(API.selectMessageList, selectMessageListHandler);
            }
            if (MainActivity.isHasApiTimeAuth) {
                MessageManager.getInstance().setApiTimeMapStartTime("websocket.selectMessageList");
            }
            vertxClient.getChannelMessageList(channelId, messageId, type);
        }


    private Handler selectMessageNextListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.getData().getSerializable("result");
            try {

                if (MainActivity.isHasApiTimeAuth) {
                    MessageManager.getInstance().setApiTimeMapEndTime("websocket.selectMessageList");
                }
                JSONObject resultObject = new JSONObject(result);
                if (!resultObject.getString("body").equals("fail")) {Log.e("TLOG", "----------------------- handler in");
                    ServerInterfaceRetryManager.getInstance().removeRetry(API.selectMessageList);
                    ServerInterfaceRetryManager.clearDuplicateMessageCheckingInfo();
                    String firstMessageId = MessageManager.getInstance().chatsArray.size() > 0 ? MessageManager.getInstance().chatsArray.get(MessageManager.getInstance().chatsArray.size() - 1).messageId : "";
                    JSONObject bodyObject = resultObject.getJSONObject("body");
                    int isPrev = getJsonInt(bodyObject, "isPrev");
                    int isNext = getJsonInt(bodyObject, "isNext");
                    JSONArray listArray = bodyObject.getJSONArray("list");
                    // 메시지 중복 삽입 방지
                    if (listArray.length() > 0) {
                        JSONObject row = listArray.getJSONObject(0);
                        ChatObject chat = makeChatObject(row);
                        if (MessageManager.getInstance().getChatsArrayIndex(chat.messageId) == -1) {
                            for (int i = 0; i < listArray.length(); i++) {
                                row = listArray.getJSONObject(i);
                                chat = makeChatObject(row);
                                if (chat != null) {
                                    MessageManager.getInstance().chatsArray.add(chat);
                                }
                            }

                            MessageManager.getInstance().postNotification(MessageManager.updateChatList, firstMessageId, -1, isNext, listArray.length());
                        }
                    }
                } else {
                    ServerInterfaceRetryManager.getInstance().resendRetry(API.selectMessageList);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    /*
     * 채팅 목록 조회
     * */
    private Handler selectMessageListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.getData().getSerializable("result");
            try {
                if (MainActivity.isHasApiTimeAuth) {
                    MessageManager.getInstance().setApiTimeMapEndTime("websocket.selectMessageList");
                }
                JSONObject resultObject = new JSONObject(result);
                if (!resultObject.getString("body").equals("fail")) {
                    ServerInterfaceRetryManager.getInstance().removeRetry(API.selectMessageList);
                    ServerInterfaceRetryManager.clearDuplicateMessageCheckingInfo();
                    String lastMessageId = MessageManager.getInstance().chatsArray.size() > 0 ? MessageManager.getInstance().chatsArray.get(0).messageId : "";
                    JSONObject bodyObject = resultObject.getJSONObject("body");
                    int isPrev = getJsonInt(bodyObject, "isPrev");
                    int isNext = getJsonInt(bodyObject, "isNext");
                    JSONArray listArray = bodyObject.getJSONArray("list");
//                    try {
//                        JSONArray linkedListArray = bodyObject.getJSONArray("linkedData");
//                        for (int i = 0; i < linkedListArray.length(); i++) {
//                            JSONObject row = linkedListArray.getJSONObject(i);
//
//                            ChatObject chat = makeChatObject(row);
//                            if (chat != null) {
//                                try {
//                                    MessageManager.getInstance().incChatsArray.add(i, chat);
//
//                                } catch (Exception e) {
//
//                                }
//                            }
//                        }
//                    } catch (JSONException e) {
//                    }
                    for (int i = 0; i < listArray.length(); i++) {
                        JSONObject row = listArray.getJSONObject(i);
                        Log.d("tag", "jgg"+row.toString());
                        ChatObject chat = makeChatObject(row);
                        Log.e("tag", "jgg"+ chat);
                        if (chat != null) {
                           /* for (ChatObject subChat : MessageManager.getInstance().incChatsArray) {
                                if (chat instanceof ChatMessage) {
                                    if (((ChatMessage) chat).linkOId.equals(subChat.messageId)) {
                                        ((ChatMessage) chat).incChat = subChat;
                                    }
                                }
                            }*/
                            try {
                                MessageManager.getInstance().chatsArray.add(i, chat);
                            } catch (Exception e) {

                            }
                        }
                    }
                    MessageManager.getInstance().postNotification(MessageManager.updateChatList, lastMessageId, isPrev, isNext, listArray.length());
                } else {
                    ServerInterfaceRetryManager.getInstance().resendRetry(API.selectMessageList);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    /*채팅 메시지 정보*/
    public void getMessageInfo(String channelId, String messageId, Handler handler) {
        if (vertxClient != null) {
            handlerMap.put(API.selectMessage, /*selectMessageHandler*/handler);
            vertxClient.getMessageInfo(channelId, messageId, 0, 0);
        }
    }

    public void getMessageInfo(String channelId, String messageId, int recordCount, int page, Handler handler) {
        if (vertxClient != null) {
            handlerMap.put(API.selectMessage, /*selectMessageHandler*/handler);
            vertxClient.getMessageInfo(channelId, messageId, recordCount, page);
        }
    }


    /*통신 에러시 서버 로깅*/
    public void sendErrlog(String status, String msg) {
        String userId = PrefManager.getInstance(AppStarter.applicationContext).getValue(Common.PREF_LOGIN_USER_ID, "guest");
        StringBuilder sb = new StringBuilder();
        sb.append("client=").append("2").append("&");
        sb.append("status=").append(status).append("&");
        sb.append("msg=").append(msg);

        String url;
        if (BuildConfig.FLAVOR.equals("devServer")) {
            url = "http://166.125.252.176:9000/api/mobilestatus/";
        } else {
            url = "https://cubemobile.skhynix.com:9000/api/mobilestatus/";
        }
        HttpClient httpClient = new HttpClient(null, new HttpAsyncTask.HttpAsyncTaskResultListener() {
            @Override
            public void onResult(HashMap<String, Object> resultMap) {

            }
        });
        httpClient.setParam(HttpAsyncTask.HTTP_POST, url + userId);
        httpClient.setQueryParam(sb.toString());
        httpClient.excute();
    }

    public static int getJsonInt(JSONObject jsonObject, String name) {
        try {
            return jsonObject.getInt(name);
        } catch (JSONException e) {
            return 0;
        }
    }
    public static String getJsonString(JSONObject jsonObject, String name) {
        try {
            String value = jsonObject.getString(name);
            if (value == null || "null".equals(value)) value = "";
            return value;
        } catch (JSONException e) {
            return "";
        }


    }
}
