package com.example.ChatApp.network;

public class DMChannel extends ChannelObject {

    public String messageId; //마지막 메시지 ID
    public String lastMessage; //마지막 메시지
    public String members; //여러명일경우에만 {유저1, 유저2, 유저3...}
    public String leave; //Y N
    public String favorite; //즐겨찾기 Y N
    public boolean unread = false;

    public DMChannel(int channelId, String channelName){
        super(channelId, channelName);
    }

    public DMChannel(int channelId, String channelName, String lastMessageId, String lastMessage, String members, int userCnt, String leave, String favorite){
        super(channelId, channelName, userCnt);
        this.messageId = lastMessageId;
        this.lastMessage = lastMessage;
        this.members = members;
        this.leave = leave;
        this.favorite = favorite;

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append(super.toString()).append(", ");
        sb.append("messageId = ").append(messageId).append(", ");
        sb.append("lastMessage = ").append(lastMessage).append(", ");
        sb.append("members = ").append(members).append(", ");
        sb.append("leave = ").append(leave).append(", ");
        sb.append("favorite = ").append(favorite);

        return sb.toString();
    }
}
