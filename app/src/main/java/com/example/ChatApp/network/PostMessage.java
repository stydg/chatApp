package com.example.ChatApp.network;

import org.w3c.dom.Comment;

import java.util.ArrayList;

public class PostMessage extends ChatObject {

    public String categoryPath;
    public ArrayList<Comment> commentList;
    public String linkOId;
    public String linkTitle;
    public String linkUrl;
    public boolean isIf = false;

    public PostMessage(String address, String type, int channel_id, String message_id, int register_id, String register_name, String register_uniqueName, String profile_img, String position_name, String dept_name, String register_date, String categorypath, String content, int reply_cnt, String link_oid, String link_title, String link_url, String name, String text){
        super("id", name, text);
        contentType = TYPE_POST;
    }

    public PostMessage(String name, String title, String text, long time){
        super("id", name, text, time);
        contentType = TYPE_POST;
        linkTitle = title;
    }

    public PostMessage(String address, String type, int channel_id, String message_id,
                       int register_id, String register_name, String register_uniqueName, String profile_img, String  position_name, String dept_name,
                       String register_date, String categorypath, String content,
                       int reply_cnt, String view_mobile, String ispinned, String favorite_yn,
                       String link_oid, String link_title, String link_url,
                       String sub_message_id, String gubun){
        super(register_uniqueName, register_name, content, register_date);
        contentType = TYPE_POST;

        this.address = address;
        this.type = type;
        this.channelId = channel_id;
        this.messageId = message_id;
        this.registerId = register_id;
        this.positionName = position_name;
        this.deptName = dept_name;

        this.replyCount = reply_cnt;
        this.isPinnedYn = ispinned;
        this.favoriteYn = favorite_yn;


        this.categoryPath = categorypath;
        this.linkOId = link_oid;
        this.linkTitle = link_title;
        this.linkUrl = link_url;

        this.viewMobileYn = view_mobile;
        this.subMessageId = sub_message_id;
        this.gubun = gubun;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
