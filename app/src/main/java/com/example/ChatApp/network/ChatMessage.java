package com.example.ChatApp.network;

import org.w3c.dom.Comment;

import java.util.ArrayList;

public class ChatMessage extends ChatObject {
    public String fontColor;
    public String linkCid; //코멘트ID
    public Comment commentInfo;
    public String linkOId; //답글ID
    public String linkTitle; //Post타이틀
    public String linkUrl; //포스트URL
    public ArrayList<OpenGraphData> og; // OpenGraph - M타입에 og 필드 추가
    public ChatObject incChat;

    public ArrayList<Comment> commentList;

    //원본메시지정보
    public String oriType;
    public OriMessage oriMessage;

    public ChatMessage(String address, String type, int channel_id, String message_id, int register_id, String register_name, String register_uniqueName, String profile_img, String position_name, String dept_name, String register_date, String categorypath, String content, String fontColor, int reply_cnt, String link_oid, String link_title, String link_url, String name, String text){
        super("id", name, text);
        contentType = TYPE_MESSAGE;
    }

    public ChatMessage(String name, String text, long time){
        super("id", name, text, time);
        contentType = TYPE_MESSAGE;
    }

    public ChatMessage(String address, String type, int channel_id, String message_id,
                       int register_id, String register_name, String register_uniqueName, String profile_img, String position_name, String dept_name,
                       String register_date, String categorypath, String content, String fontColor,
                       int reply_cnt, String view_mobile, String ispinned, String favorite_yn,
                       String link_cid, String link_oid, String link_title, String link_url,
                       String sub_message_id, String gubun, String ori_type, OriMessage ori_message){
        super(register_uniqueName, register_name, content, register_date);
        contentType = TYPE_MESSAGE;

        this.address = address;
        this.type = type;
        this.channelId = channel_id;
        this.messageId = message_id;
        this.registerId = register_id;
        this.positionName = position_name;
        this.deptName = dept_name;

        this.fontColor = fontColor;
        this.replyCount = reply_cnt;
        this.isPinnedYn = ispinned;
        this.favoriteYn = favorite_yn;

        this.linkCid = link_cid;

        this.linkOId = link_oid;
        this.linkTitle = link_title;
        this.linkUrl = link_url;
        this.viewMobileYn = view_mobile;
        this.subMessageId = sub_message_id;
        this.gubun = gubun;
        this.oriType = ori_type;
        this.oriMessage = ori_message;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append(super.toString()).append("\n");
        sb.append("linkCid = ").append(linkCid).append("\n");
        //sb.append("commentInfo = ").append(commentInfo).append("\n");
        sb.append("linkOId = ").append(linkOId).append("\n");
        sb.append("linkTitle = ").append(linkTitle).append("\n");
        sb.append("linkUrl = ").append(linkUrl).append("\n");
        sb.append("oriType = ").append(oriType).append("\n");
        sb.append("oriMessage = ").append(oriMessage);
        return sb.toString();
    }
}
