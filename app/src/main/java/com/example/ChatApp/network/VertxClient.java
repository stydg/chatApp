package com.example.ChatApp.network;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.datatransport.backend.cct.BuildConfig;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class VertxClient {
    private WebView webView;

    private Context mContext;
    private OnSockJsListener mListener;
    private static String sessionkey;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public VertxClient(String uuid) {
        mContext = AppStarter.applicationContext;
        initWebView(uuid);
    }
    public void notiKey(Task<String> notiKey, String baiduKey, String mmc){
        try {
            JSONObject bd = new JSONObject();
            bd.put("appKey", notiKey);
            bd.put("baiduKey", baiduKey);
            bd.put("TELCODE", mmc);
            bd.put("VER", Common.getAppVersion());


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.apiNotificationKey);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public void clearWebview(){
        webView.clearView();
        webView.clearCache(true);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void connectSock(String uuid) {
        StringBuilder buf = new StringBuilder("javascript:connect('");
        buf.append("http://110.45.156.137:10209/eventbus/");
        buf.append("', '");
        buf.append(uuid);
        buf.append("', '");
        buf.append(new LanguageUtil().getAppLanguageTypeIndex(mContext)+1);
        buf.append("')");
        sendWebview(buf.toString());
    }

    public void reconnect() {
        if(sessionkey==null){
            return;
        }
        StringBuilder buf = new StringBuilder("javascript:connect('");
        buf.append("http://110.45.156.137:10209/eventbus/");
        buf.append("', '");
        buf.append(sessionkey);
        buf.append("', '");
        buf.append(new LanguageUtil().getAppLanguageTypeIndex(mContext)+1);
        buf.append("')");
        sendWebview(buf.toString());
    }

    public void checkConnect() {
        sendWebview("javascript:checkConnect()");
    }

    public void disconnect() {
        sendWebview("javascript:disconnect()");
    }

    public void selectProfile(){
        StringBuilder buf = new StringBuilder("javascript:send('");
//        if (BuildConfig.DEBUG) { // (YS)
//            buf.append(API.selectProfileSecurity);
//        } else {
        buf.append(API.selectProfile);
//        }
        buf.append("', '");
        buf.append("', '");
        buf.append("')");
        sendWebview(buf.toString());
    }

    public void selectAuthSecurity(){
        StringBuilder buf = new StringBuilder("javascript:send('");
//        if (BuildConfig.DEBUG) { // (YS)
        buf.append(API.apiAuthAppSecurity);
//        } else {
//            buf.append(API.apiAuthSecurity);
//        }
        buf.append("', '");
        buf.append("', '");
        buf.append("')");
        sendWebview(buf.toString());
    }

    public void apiPostUrl(String channelId, String messageId, String action, String url){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("messageID", messageId);
            bd.put("action_id", action);
            bd.put("url", url);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.apiPostUrl);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectMessageAction(){
        StringBuilder buf = new StringBuilder("javascript:send('");
        buf.append(API.selectMessageAction);
        buf.append("', '");
        buf.append("', '");
        buf.append("')");
        sendWebview(buf.toString());
    }

    public void selectCommandList(){
        StringBuilder buf = new StringBuilder("javascript:send('");
        buf.append(API.selectCommandList);
        buf.append("', '");
        buf.append("', '");
        buf.append("')");
        sendWebview(buf.toString());
    }

    public void selectBotCommandList(int channelId){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", String.valueOf(channelId));
            hd.put("languageType", 2);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectBotCommandList);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void command(String channelId, String command){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("content", command);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.command);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //Hi-Feedback 전용 API 호출처리 - 2021.6.9(command)
  /*  public void hfAnonymous(String channelId, String _nickName, String address){
        try {
            JSONObject hd = new JSONObject();
            hd.put("languageType", MainActivity.profile.languageType);

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("nickName", _nickName);
            bd.put("address", address);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.hfcommand);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    //Hi-Feedback 전용 API 호출처리 - 2021.6.9(command)
  /*  public void hfCommand(String channelId, String command, String address){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("content", command);
            bd.put("address", address);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.hfcommand);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
    //Hi-Feedback 전용 API 호출처리 - 2021.6.9(apiChannelJoinInvite)
  /*  public void hfCommand(String channelId, String userIds, String type, String address){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("userID", userIds);
            bd.put("type", type);
            bd.put("address", address);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.hfcommand);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
*/
    public void selectChannelList(){
        sendWebview("javascript:send('"+API.selectChannelList+"', '', '')");
    }

    //hyTUBE 채널 분리 - 2020.10.30
//    public void selectHytubeList(){
//        sendWebview("javascript:send('"+API.selectHyTUBEChannelList+"', '', '')");
//    }
    //하이피드백 채널 분리 - 2021.4.21
//    public void selectHFList(){
//        sendWebview("javascript:send('"+API.selectHFchannelList+"', '', '')");
//    }

    public void selectDMChannelList(boolean isFav, String keyword){
        try {
            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectDMChannel);
            buf.append("', '");
            buf.append("', '");
            if(TextUtils.isEmpty(keyword)){
                buf.append(new JSONObject().put("isfavorite", isFav?"Y":"N").toString());
            }else{
                buf.append(new JSONObject().put("searchText", keyword).toString());
            }
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //DM 즐겨찾기 탭처리 - 2020.11.16
    public void selectDMFavoriteChannel(String keyword){
        try {
            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectDMFavoriteChannel);
            buf.append("', '");
            buf.append("', '");
            if(!TextUtils.isEmpty(keyword)){
                buf.append(new JSONObject().put("searchText", keyword).toString());
            }
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void searchChannelList(String searchText){
        try {
            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.searchChannelList);
            buf.append("', '");
            buf.append("', '");
            buf.append(new JSONObject().put("searchText", searchText).toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //채널 공지 개선 - 2020.12.3(다중공지: String notification ->  ArrayList<String> notification)
    public void createChannel(String[] channelName, String sysopId, String[] ids, int channelType, String[] deptIds, String mobileYn, String searchYn, String mobileOpenYn, String description, ArrayList<String> notification, String memberaddYn){
        ArrayList<String> list = new ArrayList<>();
        if(channelName != null){
            list.addAll(Arrays.asList(channelName));
            for(int i=0; i<5-channelName.length; i++){
                list.add(channelName[0]);
            }
        }
        StringBuffer id_sb = new StringBuffer();
        if(ids!=null) {
            int index = 0;
            for (String id : ids) {
                if (index != 0) id_sb.append(",");
                id_sb.append(id);
                index++;
            }
        }
        StringBuffer dept_sb = new StringBuffer();
        if(deptIds!=null) {
            int index = 0;
            for (String id : deptIds) {
                if (index != 0) dept_sb.append("|");
                dept_sb.append(id);
                index++;
            }
        }
        ArrayList<String> notice = notification;
        if(channelName != null){
            list.addAll(Arrays.asList(channelName));
            for(int i=0; i<5-channelName.length; i++){
                list.add(channelName[0]);
            }
        }
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelname_ko", list.get(0));
            bd.put("channelname_en", list.get(1));
            bd.put("channelname_zh", list.get(2));
            bd.put("channelname_ja", list.get(3));
            bd.put("channelname_et", list.get(4));
            bd.put("syssopid", sysopId);
            if(ids!=null) {
                bd.put("member_list", id_sb.toString());
            }
            bd.put("channel_type", String.valueOf(channelType));
            bd.put("docformobile_yn", mobileOpenYn);
            bd.put("docforsearch_yn", searchYn);
            bd.put("link_deptids", dept_sb.toString());
            bd.put("mobileopen_yn", mobileOpenYn);
            bd.put("channel_desc", description);

            //채널 공지 개선 - 2020.12.3(다중공지: String notification ->  ArrayList<String> notification)
            if(notice.size() > 0 && (!TextUtils.isEmpty(notice.get(0)) && !notice.get(0).trim().equals("")))
                bd.put("notification", notice.get(0));

            if(notice.size() > 1 && (!TextUtils.isEmpty(notice.get(1)) && !notice.get(1).trim().equals("")))
                bd.put("notification1", notice.get(1));

            if(notice.size() > 2 && (!TextUtils.isEmpty(notice.get(2)) && !notice.get(2).trim().equals("")))
                bd.put("notification2", notice.get(2));

            bd.put("memberadd_yn", memberaddYn);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.createChannel);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void insertDMChannel(String channelName, String[] ids, String[] uniqueNames) {
        insertDMChannel(channelName, "", ids, uniqueNames);
    }
    public void insertDMChannel(String channelName, String channelId, String[] ids, String[] uniqueNames){
        StringBuffer sb = new StringBuffer();
        if(ids!=null) {
            int index = 0;
            for (String id : ids) {
                if (index != 0) sb.append("|");
                sb.append(id);
                index++;
            }
        }else if(uniqueNames!=null){
            int index = 0;
            for (String uniqueName : uniqueNames) {
                if (index != 0) sb.append("|");
                sb.append(uniqueName);
                index++;
            }
        }

        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelname", channelName);
            if(!TextUtils.isEmpty(channelId)){
                bd.put("channelID", channelId);
            }
            if(ids!=null) {
                bd.put("members_id", sb.toString());
            }else if(uniqueNames!=null){
                bd.put("members_uniqueNames", sb.toString());
            }



            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.insertDMChannel);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void apiSearchChannelList(String searchKeyword){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("sWord", searchKeyword);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.apiSearchChannelList);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void apiSearchUserList(String searchKeyword){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("sWord", searchKeyword);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.apiSearchUserList);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectDMFavoriteMembers(String uniqueName, String searchText, String isAll, String startIndex, String endIndex){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("uniqueName", uniqueName);
            bd.put("searchText", searchText);
            bd.put("isAll", isAll);
            bd.put("startIndex", startIndex);
            bd.put("endIndex", endIndex);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectDMFavoriteMembers);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //추천리스트 조회 기능추가- 2020.9.25(적용안하기로함!)
    public void selectRecommanders(String uniqueName, String searchText, String isAll, String startIndex, String endIndex){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("uniqueName", uniqueName);
            bd.put("searchText", searchText);
            bd.put("isAll", isAll);
            bd.put("startIndex", startIndex);
            bd.put("endIndex", endIndex);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectRecommanders);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void apiChannelJoinInvite(String channelId, String userIds, String type){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("userID", userIds);
            bd.put("type", type);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.apiChannelJoinInvite);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //익명채널 멤버 추가 구조 개선(모바일 적용 안함) - 2020.6.8
    public void apiAnonymousJoinInvite(String channelId, String userIds){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("member_list", userIds);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updatechannelmemberadd);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void apiChannelUpdate(String channelId, String channelName){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("channelName", channelName);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.apiChannelUpdate);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateDMChannel(String channelId, String channelName){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("channelname", channelName);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateDMChannel);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateChannelAlias(String channelId, String channelName){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("aliasChannelName", channelName);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateChannelAlias);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateDMChannelAlias(String channelId, String channelName){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("aliasChannelName", channelName);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateDMChannelAlias);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getUnreadMessageCount(int type){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channel_id", "-1");


            StringBuilder buf = new StringBuilder("javascript:send('");
            if(type == 0)
                buf.append(API.unreadChannelMessageCount);
            else
                buf.append(API.unreadDMChannelMessageCount);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getUnreadMessageList(int type){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channel_id", "-1");


            StringBuilder buf = new StringBuilder("javascript:send('");

            //하이피드백 채널 분리 - 2021.4.21
            if(type != 1)
                buf.append(API.unreadChannelMessageList);
            else
                buf.append(API.unreadDMChannelMessageList);

            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void registerChannelId(String channelId){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            StringBuilder buf = new StringBuilder("javascript:register('");
            buf.append("hynix.client."+channelId);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void unregisterChannelId(String channelId){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            StringBuilder buf = new StringBuilder("javascript:unregister('");
            buf.append("hynix.client."+channelId);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getChannelMessageList(String channelId, String messageId, String type){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);
            hd.put("receiverID", -1);

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("nodeID", "");
            bd.put("selectedTag", "");
            bd.put("messageType", type);
            bd.put("messageID", messageId);
            bd.put("recordCount", Common.COUNT_LIST_AT_ONCE);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectMessageList);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");

            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getChannelInfo(String channelId){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectChannelInfoSummary);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");

            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void editPLChannel(String method, String channelId, String groupId, String groupName){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("method", method);
            bd.put("channel_id", channelId);
            bd.put("channel_group_id", groupId);
            bd.put("channel_group_name", groupName);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.editPLChannel);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void insertPLChannel(String gubun, String groupName){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("gubun", gubun);
            bd.put("channel_group_name", groupName);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.insertPLChannel);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateChannelList(String channelId, String nextChannelId, String upperChannelId, String oldUpperChannelId, String toLevel, String position){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelId", channelId);
            bd.put("nextChannelId", nextChannelId);
            bd.put("upperChannelId", upperChannelId);
            bd.put("oldUpperChannelId", oldUpperChannelId);
            bd.put("toLevel", toLevel);
            bd.put("position", position);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateChannelList);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getChannelMember(String channelId, int type, String keyword, int index){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            if(type == 0)
                bd.put("type", "ALL");//all
            else
                bd.put("type", "STANDBY");//join
            bd.put("searchText", keyword);
            bd.put("startIndex", index);


            StringBuilder buf = new StringBuilder("javascript:send('");
            if(channelId.startsWith("2"))
                buf.append(API.selectChannelInMember);
            else
                buf.append(API.selectDMChannelInMember);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //Hi-Feedback 전용 API 호출처리 - 2021.6.9(updateJoinLeave)
   /* public void hfUpdateJoinLeave(String accessUserName, int requestId, boolean accept, String address){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("accessUserName", accessUserName);
            bd.put("requestID", requestId);
            if(accept)
                bd.put("type", "ACCEPT");
            else
                bd.put("type", "REJECT");

            bd.put("address", address);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.hfcommand);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    public void updateJoinLeave(String accessUserName, int requestId, boolean accept){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("accessUserName", accessUserName);
            bd.put("requestID", requestId);
            if(accept)
                bd.put("type", "ACCEPT");
            else
                bd.put("type", "REJECT");


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateJoinLeave);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateDMChannelJoinLeave(int channelId, boolean isLeave){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("isLeave", String.valueOf(isLeave));


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateDMChannelJoinLeave);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateKickMember(String channelId, int userId){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("receiverID", userId);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateKickMember);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //Hi-Feedback 전용 API 호출처리 - 2021.6.9(updateKickMember)
  /*  public void hfUpdateKickMember(String channelId, int userId, String address){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("receiverID", userId);
            bd.put("address", address);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.hfcommand);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
    public void leaveDMKickMember(String _channelId, int _userId, String _UniqueName){
        try {
            JSONObject hd = new JSONObject();
            hd.put("languageType", 2);
            hd.put("channelID", _channelId);

            JSONObject bd = new JSONObject();
            bd.put("tagetUserID", _userId);
            bd.put("tagetUniqueName", _UniqueName);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.leaveDMKickMember);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateKickMemberAnonymous(String channelId, String _nickName){
        try {
            JSONObject hd = new JSONObject();
            hd.put("languageType", 2);

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("nickName", _nickName);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateKickMemberAnonymous);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectMemberProfile(String userId){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("registerID", userId);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectMemberProfile);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getChannelPinned(String channelId){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("bottomMessageID", "");
            bd.put("recordCount", Common.COUNT_LIST_AT_ONCE);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectPinnedMessage);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getChannelPost(String channelId){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("bottomMessageID", "");
            bd.put("recordCount", Common.COUNT_LIST_AT_ONCE);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectChannelInPostList);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getChannelFiles(String channelId){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("bottomMessageID", "");
            bd.put("recordCount", Common.COUNT_LIST_AT_ONCE);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectAttachFileList);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getNewMember(String channelId){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectChannelInMemberNewJoin);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getNewPinned(String channelId){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("bottomMessageID", "");
            bd.put("recordCount", "5");


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectPinnedMessageList);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getNewFiles(String channelId){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("bottomMessageID", "");
            bd.put("recordCount", "5");


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectAttachFileList);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getMentionList(String channelId, String userName){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("userName", userName);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.getMentionList);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectChannelTodoList(String channelId, int status, String searchArea, String searchText, String whoIs, int page){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            if(status == 0) {
                bd.put("status", "ALL");
            } else {
                bd.put("status", String.valueOf(status));
            }
            bd.put("whoIs", whoIs);
            bd.put("searchArea", searchArea);
            bd.put("searchText", searchText);
            bd.put("page", page);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectChannelTodoList);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateTodoTask(String messageId, int seq, String status){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("messageID", messageId);
            bd.put("seq", String.valueOf(seq));
            bd.put("status", status);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateTodoTask);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeTodoTask(String channelId, String messageId, int seq){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("messageID", messageId);
            bd.put("seq", String.valueOf(seq));


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.removeTodoTask);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectTodoListByMessageID(String messageId){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("messageID", messageId);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectTodoListByMessageID);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //채널 공지 개선 - 2020.12.3(채널 공지 펼침 여부 추가)
    public void channelNoticeSetting(int channelId, String NOTICEYN){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("noticeOpenYn", NOTICEYN);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.channelNoticeSetting);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //토론리스트 추가 -2020.8.24
//    public void searchDebate(int channelId, int status, String typeYN, String searchText, String startDt, String endDt, int page){
    public void searchDebate(int channelId){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
//            bd.put("status", status);
//            bd.put("commentTypeYN", typeYN);
//            bd.put("searchText", searchText);
//            bd.put("startDt", startDt);
//            bd.put("endDt", endDt);
//            bd.put("page", page);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.searchDebate);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void selectOnVotingList(int channelId, int recordCount){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("recordCount", recordCount);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectOnVotingList);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void searchVote(int channelId, int status, String typeYN, String searchText, String startDt, String endDt, int page){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("status", status);
            bd.put("commentTypeYN", typeYN);
            bd.put("searchText", searchText);
            bd.put("startDt", startDt);
            bd.put("endDt", endDt);
            bd.put("page", page);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.searchVote);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addVoteMessage(int channelId, String title, String enddate, String doubleAnswerYN, String addAnswerYN, String answerSet, String anonymousVoteYn, String commentTypeYN){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("title", title);
            bd.put("enddate", enddate);
            bd.put("doubleAnswerYN", doubleAnswerYN);
            bd.put("addAnswerYN", addAnswerYN);
            bd.put("anonymousVoteYN", anonymousVoteYn);
            if(!TextUtils.isEmpty(answerSet))bd.put("answerSet", answerSet);
            else bd.put("answerSet", "[]");
            bd.put("commentTypeYN", commentTypeYN);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.addVoteMessage);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");

            String s = bd.toString();
            String escaped = s.replace("\\", "\\\\");

            buf.append(escaped);
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateVoteMessage(int channelId, String messageId, String title, String enddate, String doubleAnswerYN, String addAnswerYN, String answerSet, String anonymousVoteYn, String commentTypeYN){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("messageID", messageId);
            bd.put("title", title);
            bd.put("enddate", enddate);
            bd.put("doubleAnswerYN", doubleAnswerYN);
            bd.put("addAnswerYN", addAnswerYN);
            bd.put("anonymousVoteYN", anonymousVoteYn);
            if(!TextUtils.isEmpty(answerSet))bd.put("answerSet", answerSet);
            else bd.put("answerSet", "[]");
            bd.put("commentTypeYN", commentTypeYN);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateVoteMessage);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");

            String s = bd.toString();
            String escaped = s.replace("\\", "\\\\");

            buf.append(escaped);
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void stopVoteMessage(int channelId, String messageId){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("messageID", messageId);
            bd.put("status", 2);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateVoteMessage);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    public void attendVote(int channelId, String messageID, int answerID, int status, String addValue, String answerSet){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("messageID", messageID);
            bd.put("answerID", answerID);
            bd.put("status", status);
            bd.put("addValue", addValue);
            bd.put("answerSet", answerSet);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.attendVote);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    */
    public void attendVote(int channelId, String messageID, String answerSet){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("messageID", messageID);
            bd.put("answerSet", answerSet);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.attendVote);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");

            String s = bd.toString();
            String escaped = s.replace("\\", "\\\\");

            buf.append(escaped);
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectVoteByMessageID(String messageID){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("messageID", messageID);
            bd.put("gubun", "D");


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectVoteByMessageID);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addVoteAnswer(int channelID, String messageID, String answerValue){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelID);

            JSONObject bd = new JSONObject();
            bd.put("messageID", messageID);
            bd.put("answerValue", answerValue);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.addVoteAnswer);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getNewPost(String channelId){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("bottomMessageID", "");
            bd.put("recordCount", "5");


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectChannelInPostList);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void searchMessageList(String apiType, String strChannelID, String strChannelTP,
                                  String strKeyword, String lastMsgId, String strAll, boolean bEveryone, String strSort, int page){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("searchType", apiType); //M
            bd.put("searchText", strKeyword);
            /*if (apiType.equals("PT") || apiType.equals("PN")) //MT 멘션 FV 즐겨찾기 MS 메시지 AT 파일 PT 포스트 PN 핀
                bd.put("bottomMessageID", lastMsgId); // lastMsgId;
            else*/
            bd.put("bottomMessageID", lastMsgId);
            bd.put("startDt", "-1");
            bd.put("endDt", "-1");
            bd.put("channelType", strChannelTP); //CH, DM
            bd.put("searchArea", strAll); //CUR, ALL
            bd.put("channelID", strChannelID); //2~ CH, 5~ DM
            bd.put("recordCount", Common.COUNT_LIST_AT_ONCE);
            bd.put("whoIs", bEveryone? "ALL":"ME"); // "ALL", "ME"
            bd.put("sorting", strSort); //"recent","register","subject"
            bd.put("page", page);


            StringBuilder buf = new StringBuilder("javascript:send('");
            //buf.append(API.searchMessageList);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String channelId, String userId, String message, String type, String mode, String oId, String filePath){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("dataMapID", -1);
            bd.put("languageType", String.valueOf(new LanguageUtil().getSystemLanguageTypeIndex()+1));
            bd.put("content", message);
            if(type.equals("M") || type.equals("P")){
                bd.put("type", type); //(M)essage,(P)ost,(F)ile,(I)mage
                bd.put("status", mode); //(I)nsert,(U)pdate
                bd.put("linkTitle", "");
                bd.put("linkUrl", "");
            } else if(type.equals("T")) {
                bd.put("status", "I");
                bd.put("image", ":"+mode+":");
                bd.put("linkUrl", "");
                bd.put("linkTitle", "");
            } else{
                hd.put("userID", userId);
                hd.put("channelID", channelId);
                bd.put("image", "");

                JSONArray ja = new JSONArray();
                JSONObject jf = new JSONObject();
                jf.put("id", oId);
                jf.put("object_name", filePath);
                String ext = filePath.substring(filePath.lastIndexOf(".")+1,filePath.length());
                if(type.equals("I")){
                    jf.put("type", "image/"+ext);
                }else if(type.equals("F")){
                    jf.put("type", ext);
                }
                jf.put("size", new File(filePath).length());
                ja.put(jf);
//                bd.put(JProperty("file", ja))

            }


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.addMessage);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getMessageInfo(String channelId, String messageId, int recordCount, int page){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("messageID", messageId);
            if(recordCount>0 && page>0){
                bd.put("recordCount", recordCount);
                bd.put("page", page);
            }


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectMessage);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateMessage(String channelId, int userId, String messageId, String content){

        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);
            hd.put("userID", userId);

            JSONObject bd = new JSONObject();
            bd.put("messageID", messageId);
            bd.put("content", content);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateMessage);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteMessage(String channelId, String messageId){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("messageID", messageId);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.deleteMessage);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addComment(String channelId, String messageId, String content){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("messageID", messageId);
            bd.put("content", content);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.addComment);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateComment(String channelId, String messageId, String commentId, String content){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("messageID", messageId);
            bd.put("commentID", commentId);
            bd.put("msg", content);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateComment);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteComment(String channelId, String messageId, String commentId){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);

            JSONObject bd = new JSONObject();
            bd.put("messageID", messageId);
            bd.put("commentID", commentId);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.deleteComment);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPost(String oId){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("oid", oId);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.apigetpost);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPostAttachFileList(String messageId){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("tp", "EDM1001");
            bd.put("messageId", messageId);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addPost(String channelId, String title, String content){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("title", title);
            bd.put("content", content);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.addPost);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updatePost(String channelId, String oId, String title, String content){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("oid", oId);
            bd.put("title", title);
            bd.put("content", content);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updatePost);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //옵션메뉴
    public void setPinMessage(String channelId, int userId, String messageId, boolean isPinned){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);
            hd.put("userID", userId);

            JSONObject bd = new JSONObject();
            bd.put("messageID", messageId);
            bd.put("isPinned", isPinned?"Y":"N");


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updatePinned);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setFavoriteChannel(int userId, String channelId, boolean isFav){
        try {
            JSONObject hd = new JSONObject();
            hd.put("userID", userId);

            JSONObject bd = new JSONObject();
            bd.put("channelID", channelId);
            bd.put("isFavorite", isFav?"Y":"N");


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateFavoriteChannel);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setFavoriteMessage(int userId, String messageId, boolean isFav){
        try {
            JSONObject hd = new JSONObject();
            hd.put("userID", userId);

            JSONObject bd = new JSONObject();
            bd.put("messageID", messageId);


            StringBuilder buf = new StringBuilder("javascript:send('");
            if(isFav)
                buf.append(API.addFavorite);
            else
                buf.append(API.removeFavorite);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void copyMessage(int fromChannelId, String messageId, int toChannelId, String content){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", fromChannelId);

            JSONObject bd = new JSONObject();
            bd.put("channelID", fromChannelId);
            bd.put("copy_messageID", messageId);
            bd.put("move_channelID", String.valueOf(toChannelId));
            bd.put("content", content);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.copyMessage);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addLikeMessage(String ChannelId, String MessageId){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelID", ChannelId);
            bd.put("messageID", MessageId);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.addLike);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void termDictionary(String text){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelMsg", text);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.termDictionary);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void translation(String text, String sourceLanguageType, String targetLanguageType){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("channelMsg", text);
            bd.put("sourceLanguageType", sourceLanguageType);
            bd.put("targetLanguageType", targetLanguageType);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.translation);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectTranslateSupportList(){
        StringBuilder buf = new StringBuilder("javascript:send('");
        buf.append(API.selectTranslatSupportTypeList);
        buf.append("', '");
        buf.append("', '");
        buf.append("')");
        sendWebview(buf.toString());
    }

    public void searchMessageListDetail(String messageId, String channelType){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("messageID", messageId);
            bd.put("channelType", channelType);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.searchMessageListDetail);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectPostContent(int languageType, int UserID, String oid){
        try {
            JSONObject hd = new JSONObject();
            hd.put("UserID", UserID);

            JSONObject bd = new JSONObject();
            bd.put("oid", oid);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectPostContent);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // for notice
    public void selectAlarmCenterlist() {
        StringBuilder buf = new StringBuilder("javascript:send('");
        buf.append(API.selectAlarmCenterList);
        buf.append("', '', '')");
        sendWebview(buf.toString());
    }

    public void updateAlarmCenter(String messageID, String registerUniqueName) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("messageID", messageID);
            bd.put("registerUniqueName", registerUniqueName);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateAlarmCenter);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getConfirmDetail(String messageID, String channelID, String confirmUserID) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("messageID", messageID);
            bd.put("channelID", channelID);
            bd.put("confirmUserID", confirmUserID);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.apiGetMobileConfirm);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void processMobileConfirm(String messageID, String channelID, String confirmUserID, String approveYN) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("messageID", messageID);
            bd.put("channelID", channelID);
            bd.put("confirmUserID", confirmUserID);
            bd.put("approveYN", approveYN);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.apiProcessMobileConfirm);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteAlarmCenter(String messageID) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("messageID", messageID);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.deleteAlarmCenter);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void searchAlarmCenterList(String searchText, String searchType, String messageID) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("searchText", searchText);
            bd.put("searchType", searchType);
            bd.put("messageID", messageID);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.deleteAlarmCenter);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectMailList(String startDate, String endDate, String pageCount, String offset,
                               String toRecipients, String ccRecipients, String searchOption, String folderUid){
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "EML1001");
            bd.put("startDate", startDate);
            bd.put("endDate", endDate);
            bd.put("pageCount", pageCount);
            bd.put("offset", offset);
            bd.put("toRecipients", toRecipients);
            bd.put("ccRecipients", ccRecipients);
            bd.put("searchOption", searchOption);
            bd.put("folderUid", folderUid);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setMailRead(String folderUid, String emailUid, String unreadToMarkOnReaded,
                            String isBodyHtml, String toRecipients, String ccRecipients){
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "EML1002");
            bd.put("folderUid", folderUid);
            bd.put("emailUid", emailUid);
            bd.put("unreadToMarkOnReaded", unreadToMarkOnReaded);
            bd.put("isBodyHtml", isBodyHtml);
            bd.put("toRecipients", toRecipients);
            bd.put("ccRecipients", ccRecipients);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getMailAttachedImageFile(String uid, String fuid, String attachName) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "EML1005");
            bd.put("uid", uid);
            bd.put("fuid", fuid);
            bd.put("attachName", attachName);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectUnreadMailList(String startDate, String endDate, String pageCount,
                                     String offset, String toRecipients, String ccRecipients, String folderUid) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "EML1006");
            bd.put("startDate", startDate);
            bd.put("endDate", endDate);
            bd.put("pageCount", pageCount);
            bd.put("offset", offset);
            bd.put("toRecipients", toRecipients);
            bd.put("ccRecipients", ccRecipients);
            bd.put("folderUid", folderUid);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectSignList(String StartDate, String EndDate, String MaxCount,
                               String CntPerPage, String CurPageNo, String Mode){
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "APL1001");
            bd.put("StartDate", StartDate);
            bd.put("EndDate", EndDate);
            bd.put("MaxCount", MaxCount);
            bd.put("CntPerPage", CntPerPage);
            bd.put("CurPageNo", CurPageNo);
            bd.put("Mode", Mode);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getSignDetail(String ApprDocPID, String ApprDocWID, String ApprDocTypeCode){
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "APL1002");
            bd.put("ApprDocPID", ApprDocPID);
            bd.put("ApprDocWID", ApprDocWID);
            bd.put("ApprDocTypeCode", ApprDocTypeCode);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getSignDetailUrl(String ApprDocPID, String ApprDocWID, String ApprDocTypeCode) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "APL1005");
            bd.put("ApprDocPID", ApprDocPID);
            bd.put("ApprDocWID", ApprDocWID);
            bd.put("ApprDocTypeCode", ApprDocTypeCode);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getSignOrgImage(String url) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "APL1009");
            bd.put("url", url);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void doSign(String ApprDocPID, String ApprDocWID, String ApproverID, String ApprGbnCode,
                       String Comment, String RtnLoginID, String ApprovalLineXml){
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "APL1003");
            bd.put("ApprDocPID", ApprDocPID);
            bd.put("ApprDocWID", ApprDocWID);
            bd.put("ApproverID", ApproverID);
            bd.put("ApprGbnCode", ApprGbnCode);
            bd.put("Comment", Comment);
            bd.put("RtnLoginID", RtnLoginID);
            bd.put("ApprovalLineXml", ApprovalLineXml);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getImageMeta(String oId, int type, String channelId){
        try {
            JSONObject bd = new JSONObject();
            if(type == 0) {
                bd.put("tp", "APL1006");//edms file
                bd.put("oid", oId);
            }
            else if(type == 1) {
                bd.put("tp", "APL1007");//post
                bd.put("url", oId);
            }
            else if(type == 2){
                bd.put("tp", "APL1009");//approval
                bd.put("url", oId);
            }
            //모바일에서 txt 파일도 열람 가능하도록  - 2020.2.6
            else if(type == 3){
                bd.put("tp", "APL1008");//txt file
                bd.put("oid", oId);
            }
            bd.put("channelID", channelId);
            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //

    public void selectWritingStart(String channelId){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectWritingMessageProfileNoticeStart);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectWritingStop(String channelId){
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelId);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectWritingMessageProfileNoticeStop);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getImageBlock(String key, int arrIdx){
        try {
            JSONObject bd = new JSONObject();
            bd.put("key", key);
            bd.put("arrIdx", arrIdx);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.apiImageByte);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestAuthKey(String key, String channelId){
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", key);
            bd.put("channelID", channelId);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.apiRequestAuthKey);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectScheduleList(String startDate, String endDate, String pageCount, String offset, String searchOption) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "CAL1004");
            bd.put("startDate", startDate);
            bd.put("endDate", endDate);
            bd.put("pageCount", pageCount);
            bd.put("offset", offset);
            bd.put("searchOption", searchOption);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectScheduleDetail(String uid) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "CAL1002");
            bd.put("uid", uid);
            bd.put("itemType", "Single");
            bd.put("jobMaster", "false");
            bd.put("jobTime", "");
            bd.put("bodyIsHtml", "true");

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getScheduleAttachedImageFile(String uid, String attachName) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "CAL1003");
            bd.put("uid", uid);
            bd.put("fileName", attachName);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addSchedule(String subject, String body, String startDatetime, String endDatetime, String importance, boolean isAllDayEvent, String legacyFreeBusyStatus, String location, int reminderMinutesBeforeStart, String required, String optional){
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "CAL1005");
            bd.put("subject", subject);
            bd.put("body", body);
            bd.put("startTime", startDatetime);
            bd.put("endTime", endDatetime);
            bd.put("importance", importance);
            bd.put("isAllDayEvent", String.valueOf(isAllDayEvent));
            bd.put("legacyFreeBusyStatus", legacyFreeBusyStatus);
            bd.put("location", location);
            bd.put("reminderMinutesBeforeStart", String.valueOf(reminderMinutesBeforeStart));
            bd.put("required", required);
            bd.put("optional", optional);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void modifySchedule(String subject, String body, String startDatetime, String endDatetime, String importance, boolean isAllDayEvent, String legacyFreeBusyStatus, String location, int reminderMinutesBeforeStart, String required, String optional, boolean jobMaster, int jobTime, String uid){
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "CAL1006");
            bd.put("subject", subject);
            bd.put("body", body);
            bd.put("startTime", startDatetime);
            bd.put("endTime", endDatetime);
            bd.put("importance", importance);
            bd.put("isAllDayEvent", String.valueOf(isAllDayEvent));
            bd.put("legacyFreeBusyStatus", legacyFreeBusyStatus);
            bd.put("location", location);
            bd.put("reminderMinutesBeforeStart", String.valueOf(reminderMinutesBeforeStart));
            bd.put("required", required);
            bd.put("optional", optional);
            bd.put("jobMaster", String.valueOf(jobMaster));
            bd.put("jobTime", String.valueOf(jobTime));
            bd.put("uid", uid);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectTodoListToday(String iPageNum, String iRecordCount){
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "TOD1004");
            bd.put("iPageNum", iPageNum);
            bd.put("iRecordCount", iRecordCount);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectTodoList(String startDate, String endDate){
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "TOD1009");
            bd.put("startDate", startDate);
            bd.put("endDate", endDate);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectTodoCategory(){
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "TOD1010");


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectTodoList(String uid){
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "TOD1005");
            bd.put("uid", uid);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setTodoConfirm(String actionType, String startDate, String endDate, String categoryNo, String rejectMsg, String taskID){
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "TOD1006");
            bd.put("actionType", actionType);
            bd.put("startDate", startDate);
            bd.put("endDate", endDate);
            bd.put("categoryNo", categoryNo);
            bd.put("rejectMsg", rejectMsg);
            bd.put("uid", taskID);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addTodo(String taskName, String taskContent, String dueDate, String exUserCategoryNo, String exUserCategoryName, String exUserID, String exUserName){
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "TOD1007");
            bd.put("taskName", taskName);
            bd.put("taskContent", taskContent);
            bd.put("dueDate", dueDate);
            bd.put("exUserCategoryNo", exUserCategoryNo);
            bd.put("exUserCategoryName", exUserCategoryName);
            bd.put("exUserID", exUserID);
            bd.put("exUserName", exUserName);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void modifyTodo(String categoryNo, String startDate, String endDate, String viewDate, String progress, String uid, String isAlarm, String subject, String content, String contentId, String fileId, String taskStatus, String updateFlag){
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "TOD1008");
            bd.put("categoryNo", categoryNo);
            bd.put("startDate", startDate);
            bd.put("endDate", endDate);
            bd.put("viewDate", viewDate);
            bd.put("progress", progress);
            bd.put("uid", uid);
            bd.put("isAlram", isAlarm);
            bd.put("subject", subject);
            bd.put("content", content);
            bd.put("contentId", contentId);
            bd.put("fileId", fileId);
            bd.put("taskStatus", taskStatus);
            bd.put("updateFlag", updateFlag);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // For Notice Count
    public void getSignCount() {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "APL1004");

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getMailFolderList(String folderLevel) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "EML1003");
            bd.put("folderLevel", folderLevel);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getMailFolderSubList(String folderUid, String folderLevel) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "EML1004");
            bd.put("folderUid", folderUid);
            bd.put("folderLevel", folderLevel);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getTodoCount(String start, String end) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "TOD1001");
            bd.put("start", start);
            bd.put("end", end);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getScheduleCount(String startDate, String endDate) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "CAL1001");
            bd.put("startDate", startDate);
            bd.put("endDate", endDate);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectSettings() {

        StringBuilder buf = new StringBuilder("javascript:send('");
        buf.append(API.selectPrivateAlarmSetting);
        buf.append("', '");
        buf.append("', '");
        buf.append("')");
        sendWebview(buf.toString());
    }

    public void updateSettings(String alarmSet, String device, String sound) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("alarmSet", alarmSet);
            bd.put("device", device);
            bd.put("sound", sound);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updatePrivateAlarmSetting);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void channelAlarmSetting(String channelID, String totalBNT) {
        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", channelID);

            JSONObject bd = new JSONObject();
            bd.put("totalBTN", totalBNT);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.channelAlarmSetting);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void channelFreezeSetting(String _channelID, boolean _isFreezing) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("channelID", _channelID);
            bd.put("isFreezing", String.valueOf(_isFreezing));

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.channelFreezingSetting);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //Hi-Feedback 전용 API 호출처리 - 2021.6.9
  /*  public void hfFreezing(String _channelID, boolean _isFreezing, String address) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("channelID", _channelID);
            bd.put("isFreezing", String.valueOf(_isFreezing));
            bd.put("address", address);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.hfcommand);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    public void updateUserLanguageType(String translationLanguageType, String translationSourceLanguageType) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("translationLanguageType", translationLanguageType);
            bd.put("translationSourcelanguagetype", translationSourceLanguageType);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateUserLanguageType);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void searchAllUserList(String searchText, int startIndex) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("searchText", searchText);
            bd.put("startIndex", startIndex);
            bd.put("languageType", String.valueOf(new LanguageUtil().getSystemLanguageTypeIndex()+1));

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.searchAllUserList);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void searchDeptList(String searchText, int startIndex) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("searchText", searchText);
            bd.put("startIndex", startIndex);
            bd.put("languageType", String.valueOf(new LanguageUtil().getSystemLanguageTypeIndex()+1));

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.searchDeptList);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectEmoticonList() {

        StringBuilder buf = new StringBuilder("javascript:send('");
        buf.append(API.selectEmoticonList);
        buf.append("', '");
        buf.append("', '");
        buf.append("')");
        sendWebview(buf.toString());
    }

    public void selectEmoticonGroup() {

        StringBuilder buf = new StringBuilder("javascript:send('");
        buf.append(API.selectEmoticonGroup);
        buf.append("', '");
        buf.append("', '");
        buf.append("')");
        sendWebview(buf.toString());
    }

    public void selectEmoticonByGropID(String groupId) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("groupId", groupId);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectEmoticonByGropID);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //이모티콘 자주사용 기능(즐겨찾기) 추가 - 2021.5.17(즐겨찾기 이모티콘 사용및 해제 조회)
    public void selectAddLikeEmoticon(String emoticonId, String groupId) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("groupId", groupId);
            bd.put("emoticonId", emoticonId);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectAddLikeEmoticon);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //이모티콘 자주사용 기능(즐겨찾기) 추가 - 2021.5.17(즐겨찾기 이모티콘 등록)
    public void addLikeEmoticon(String emoticonId, String groupId) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("groupId", groupId);
            bd.put("emoticonId", emoticonId);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.addLikeEmoticon);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //이모티콘 자주사용 기능(즐겨찾기) 추가 - 2021.5.17(즐겨찾기 이모티콘 해제)
    public void delLikeEmoticon(String emoticonId, String groupId) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("groupId", groupId);
            bd.put("emoticonId", emoticonId);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.delLikeEmoticon);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void selectAllNickIcon() {
        try {
            JSONObject bd = new JSONObject();
            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectAllNickIcon);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateNickName(String nick_name_, String nick_id_, int channel_id_) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("nickName", nick_name_);
            bd.put("nickID", nick_id_);
            bd.put("channelID", String.valueOf(channel_id_));

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.updateNickName);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectBotTypeList() {
        StringBuilder buf = new StringBuilder("javascript:send('");
        buf.append(API.selectBotTypeList);
        buf.append("', '");
        buf.append("', '");
        buf.append("')");
        sendWebview(buf.toString());
    }

    public void addMeet(String subject, String body, String startTime, String endTime, String importance, boolean isAllDayEvent,
                        String legacyFreeBusyStatus, String location, int reminderMinutesBeforeStart, String required, String optional) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "MET1001");
            bd.put("subject", subject);
            bd.put("body", body);
            bd.put("startTime", startTime);//2017-01-15
            bd.put("endTime", endTime);
            bd.put("importance", importance);//Low, Normal, High
            bd.put("isAllDayEvent", isAllDayEvent);
            bd.put("legacyFreeBusyStatus", legacyFreeBusyStatus);//Free,Tentative,Busy,OOF,WorkingElsewhere,NoData
            bd.put("location", location);
            bd.put("reminderMinutesBeforeStart", reminderMinutesBeforeStart);
            bd.put("required", required);
            bd.put("optional", optional);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void modifyMeet(String subject, String body, String startTime, String endTime, String importance, boolean isAllDayEvent,
                           String legacyFreeBusyStatus, String location, int reminderMinutesBeforeStart, String required, String optional,
                           boolean jobMaster, String jobTime, String uid) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "MET1002");
            bd.put("subject", subject);
            bd.put("body", body);
            bd.put("startTime", startTime);//2017-01-15
            bd.put("endTime", endTime);
            bd.put("importance", importance);//Low, Normal, High
            bd.put("isAllDayEvent", isAllDayEvent);
            bd.put("legacyFreeBusyStatus", legacyFreeBusyStatus);//Free,Tentative,Busy,OOF,WorkingElsewhere,NoData
            bd.put("location", location);
            bd.put("reminderMinutesBeforeStart", reminderMinutesBeforeStart);
            bd.put("required", required);
            bd.put("optional", optional);
            bd.put("jobMaster", jobMaster);
            bd.put("jobTime", jobTime);
            bd.put("uid", uid);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteMeet(String uid, String itemType, boolean jobMaster, String jobTime) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "MET1004");
            bd.put("uid", uid);
            bd.put("itemType", itemType);//Single, Occurrence, RecurringMaster, Exception
            bd.put("jobMaster", jobMaster);
            bd.put("jobTime", jobTime);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestMeet(String uid, String body, String itemType, boolean jobMaster, String jobTime, String toRecipients, String ccRecipients) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "MET1005");
            bd.put("uid", uid);
            bd.put("body", body);
            bd.put("itemType", itemType);//Single, Occurrence, RecurringMaster, Exception
            bd.put("jobMaster", jobMaster);
            bd.put("jobTime", jobTime);
            bd.put("toRecipients", toRecipients);
            bd.put("ccRecipients", ccRecipients);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectMeetList(String startDate, String endDate, int pageCount, int offset, String searchOption, String logicalOperator, String searchWord) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("tp", "MET1006");
            bd.put("startDate", startDate);
            bd.put("endDate", endDate);
            bd.put("pageCount", pageCount);
            bd.put("offset", offset);
            bd.put("searchOption", searchOption);//All, Period, Page
            bd.put("logicalOperator", logicalOperator);//AND, OR
            bd.put("searchWord", searchWord);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 용어집
    public void selectDictionary(String keyWord) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("keyWord", keyWord);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectDictionary);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //일정검색 개선 - 2020.10.6
//   public void selectChannelSchedule(int channel_id, int sort){
    public void selectChannelSchedule(int channel_id, int sort, String date){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("tp", "CAL1011");
            bd.put("channelid", String.valueOf(channel_id));
//            bd.put("start", "now");
            bd.put("start", date);
            bd.put("end", "default");
            bd.put("private", "true");
            bd.put("sort", String.valueOf(sort));

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(String api, String header, String body) {
        StringBuilder buf = new StringBuilder("javascript:send('");
        buf.append(api);
        buf.append("', '");
        buf.append(header);
        buf.append("', '");
        buf.append(body);
        buf.append("')");
        sendWebview(buf.toString());
    }

    /* 채널삭제 요청 */
    public void requestChannelDelete(int channel_id) {
        try {
            JSONObject header = new JSONObject();
            header.put("channelID", String.valueOf(channel_id));

            sendRequest(API.requestChannelDelete, header.toString(), "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //Hi-Feedback 전용 API 호출처리 - 2021.6.9(requestChannelDelete)
  /*  public void hfRequestChannelDelete(int channel_id, String address) {

        try {
            JSONObject hd = new JSONObject();
            hd.put("channelID", String.valueOf(channel_id));

            JSONObject bd = new JSONObject();
            bd.put("address", address);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.hfcommand);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    //Hi-Feedback 전용 API 호출처리 - 2021.6.9(requestChannelDeleteAdmin)
  /*  public void hfRequestChannelDeleteAdmin(int channel_id, String address) {
        try {
            JSONObject hd = new JSONObject();
            hd.put("uniqueName", MainActivity.profile.uniqueName);
            hd.put("channelID", String.valueOf(channel_id));
            hd.put("languageType", MainActivity.profile.languageType);

            JSONObject bd = new JSONObject();
            bd.put("address", address);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.hfcommand);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
    /* 채널삭제 취소 요청 */
    public void rejectChannelDelete(int channel_id) {
        try {
            JSONObject header = new JSONObject();
            header.put("channelID", String.valueOf(channel_id));

            sendRequest(API.rejectChannelDelete, header.toString(), "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveChannel(int channelId) {
        try {
            JSONObject body = new JSONObject();
            body.put("tp", "EML1022");
            body.put("channelid", String.valueOf(channelId));

            sendRequest(API.noticeAPI, "", body.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* RICH NOTIFICATION RESPONSE */
    public void richNotificationResponse(String resultJson) {
        try {
            sendRequest(API.richNotificationResponse, "", resultJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* RICH NOTIFICATION 이미지 정보 요청 */
    public void richNotificationGetimageInfo(int channel_id, String imageUrl) {
        try {
            JSONObject header = new JSONObject();
            header.put("channelID", String.valueOf(channel_id));
            header.put("client", String.valueOf(2));

            JSONObject body = new JSONObject();
            body.put("url", imageUrl);

            sendRequest(API.richNotificationGetimageInfo, header.toString(), body.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void selectAppList() {
//        try {
        JSONObject header = new JSONObject();

        JSONObject body = new JSONObject();

        sendRequest("websocket.selectAppList", header.toString(), body.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    /* 모바일 로그 */
    public void sendMobileLog(String _action, int _channel_id) {
        try {
            JSONObject header = new JSONObject();
            header.put("client", String.valueOf(2));

            JSONObject body = new JSONObject();
            body.put("action", _action);
            body.put("channelId", String.valueOf(_channel_id));

            sendRequest(API.apiMobileLog, header.toString(), body.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* 봇인사 */
    public void addBotGreetingMessage(int channel_id) {
        try {
            JSONObject header = new JSONObject();

            JSONObject body = new JSONObject();
            body.put("channelID", channel_id);

            sendRequest(API.addBotGreetingMessage, header.toString(), body.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 폴더(그룹) Unread Count 초기화
    public void unreadMessageRead(String channel_ids) {
        try {
            JSONObject header = new JSONObject();

            JSONObject body = new JSONObject();
            body.put("channel_id", channel_ids);

            sendRequest(API.unreadMessageRead, header.toString(), body.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*비상대피 임무 목록 */
    public void selectEmergencyPositionList(){
        try {
            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectEmergencyPositionList);
            buf.append("', '");
            buf.append("', '");
            buf.append("')");
            sendWebview(buf.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*비상대피 역할 */
    public void selectEmergencyPositionMission(String _dutycode) {
        try {
            JSONObject bd = new JSONObject();
            bd.put("code", _dutycode);

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectEmergencyPositionMission);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*비상대피 건물 */
    public void selectEmergencyMobileBuildList(){
        try {
            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectEmergencyMobileBuildList);
            buf.append("', '");
            buf.append("', '");
            buf.append("')");
            sendWebview(buf.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //안전관리감독자 안내화면 수정(추가 수정) - 2021.6.16(서버호출로 변경)
    public void selectSafetyPositionList(){//리스트
        try {
            JSONObject bd = new JSONObject();
            bd.put("type", "A");

            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.selectSafetyPositionList);
            buf.append("', '");
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 비밀문서함 파일 목록 요청
    public void getSecretFiles(String linkUrl){
        try {
            JSONObject hd = new JSONObject();

            JSONObject bd = new JSONObject();
            bd.put("tp", "HYDISK1001");
            bd.put("linkUrl", linkUrl);


            StringBuilder buf = new StringBuilder("javascript:send('");
            buf.append(API.noticeAPI);
            buf.append("', '");
            buf.append(hd.toString());
            buf.append("', '");
            buf.append(bd.toString());
            buf.append("')");
            sendWebview(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void initWebView(final String uuid){
        webView = new WebView(mContext);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                connectSock(uuid);
                super.onPageFinished(view, url);
            }
        });
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);
        webView.addJavascriptInterface(new WebAppInterface(mContext), "Android");
        try {
            webView.loadUrl("file:///android_asset/www/vertx.html");
        } catch (Exception e) {
            e.printStackTrace();

            webView.loadUrl("file:///android_asset/www/vertx.html");
        }
    }

    private class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void msg(String json) {
            Common.log("D", getClass().getSimpleName(), "msg:"+json);
            if(isJSONValid(json)){
                if(mListener!=null){
                    if (json.startsWith("open") || json.startsWith("reopen") ) {
                        mListener.onOpen(json);
                    } else if (json.startsWith("error")) {
                        mListener.onError(json);
                    } else if (isJSONValid(json)) {
                        mListener.onMessage(json);
                    }
                }
            } else if ((json.startsWith("open") || json.startsWith("reopen")) &&  mListener!=null){
                mListener.onOpen(json);
            }
        }

        @JavascriptInterface
        public void close(String json) {
            if(mListener!=null) {
                mListener.onClose(json);
            }
        }

        @JavascriptInterface
        public void status(int status) {
            if(mListener!=null) {
                mListener.onStatus(status);
            }
        }

        @JavascriptInterface
        public void setSession(String sessionKey) {
            Log.d(getClass().getSimpleName(), "sessionKey:"+sessionKey);
            if(sessionKey == null || sessionKey.equals("null") || sessionKey.equals("fail") || sessionKey.equals("false")){

            }else {
                if(!"fail".equals(sessionKey)) {
                    sessionkey = sessionKey;
                    PrefManager.getInstance(mContext).putValue(Common.PREF_SESSION_KEY, sessionKey);
                }
            }
        }
    }

    private void sendWebview(String jsonString){
        StringBuffer sb = new StringBuffer();
        sb.append(jsonString).append("\n");
        if(BuildConfig.DEBUG) {
            StackTraceElement[] a = new Throwable().getStackTrace();
            for (int i = 1; i < a.length; i++) {
                sb.append("\tat ").append(a[1].getClassName()).append("(").append(a[i].getFileName()).append(":").append(a[i].getLineNumber()).append(")").append("\n");
            }
        }
        Common.log("E", Common.LOG_TAG_NETWORK, sb.toString());
        try {
            webView.loadUrl(jsonString);
        } catch (Exception e) {
            e.printStackTrace();

            ServerInterfaceManager.getInstance().sendErrlog("Connect server", jsonString);

            webView.loadUrl(jsonString);
        }
    }


    public void setOnSockJsListener(OnSockJsListener listener){
        mListener = listener;
    }

    public interface OnSockJsListener {
        void onOpen(String json);
        void onMessage(String json);
        void onError(String json);
        void onClose(String json);
        void onStatus(int status);
    }

}

