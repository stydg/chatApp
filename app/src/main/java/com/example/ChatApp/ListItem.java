package com.example.ChatApp;

public class ListItem {
    private String uname;
    private String message;

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    ListItem(String uname, String message){
        this.uname = uname;
        this.message = message;
    }
}
