package com.example.ChatApp.network;

public class SystemMessage extends ChatObject {

    public SystemMessage(String text, int type){
        super(text);
        contentType = type;
        service = text;
        messageId = "";
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
