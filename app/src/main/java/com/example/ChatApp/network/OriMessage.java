package com.example.ChatApp.network;

public class OriMessage extends ChatObject {
    public String fontColor;
    public String linkCid; //코멘트ID
    public String linkOId; //답글ID
    public String linkTitle; //Post타이틀
    public String linkUrl; //포스트URL
    public String updateDate;
    public String messageText;
    public ChatObject incChat;



    public OriMessage(String name, String text){
        super("id", name, text);
        contentType = TYPE_MESSAGE;
    }

    public OriMessage(String name, String text, long time){
        super("id", name, text, time);
        contentType = TYPE_MESSAGE;
    }

    public OriMessage(String address, String type, int channel_id, String message_id,
                      int register_id, String register_name, String register_uniqueName, String position_name, String dept_name,
                      String register_date, String categorypath, String content, String fontColor,
                      int reply_cnt, String view_mobile, String ispinned, String favorite_yn,
                      String link_cid, String link_oid, String link_title, String link_url,
                      String sub_message_id, String gubun, String update_date)
    {
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
        this.updateDate = update_date;
        this.messageText = content;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("channelId = ").append(channelId).append("\n");
        sb.append("messageId = ").append(messageId).append("\n");
        sb.append("registerId = ").append(registerId).append("\n");
        sb.append("registerDate = ").append(registerDate).append("\n");
        sb.append("updateDate = ").append(updateDate).append("\n");
        sb.append("messageText = ").append(messageText);
        return sb.toString();
    }

}
