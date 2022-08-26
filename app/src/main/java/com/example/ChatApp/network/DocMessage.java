
package com.example.ChatApp.network;

import android.graphics.Bitmap;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.HashMap;

public class DocMessage extends ChatObject {
    public ArrayList<Doc> files = new ArrayList<>();
    public HashMap<String, Bitmap> bitmapMap = new HashMap<>();

    public String fileId;
    public String fileName;


    public DocMessage(String name, String text, long time, String fileName){
        super("id", name, text, time);
        contentType = TYPE_DOCUMENT;
        this.type = "F";
    }

    public DocMessage(String address, String type, int channel_id, String message_id,
                      int register_id, String register_name, String register_uniqueName, String profile_img, String  position_name, String dept_name,
                      String register_date, String content,
                      int reply_cnt, String attach_edms_id, String attach_file, String view_mobile, String ispinned, String favorite_yn,
                      ArrayList<Comment> comment_list,
                      String sub_message_id, String gubun){
        super(register_uniqueName, register_name, content, register_date);
        switch (type){
            case "F":
            case "I":
                contentType = TYPE_DOCUMENT;
                break;
//            case "I":
//                contentType = TYPE_IMAGE;
//                break;
            default:
                contentType = TYPE_DOCUMENT;
                break;
        }

        this.address = address;
        this.type = type;
        this.channelId = channel_id;
        this.messageId = message_id;
        this.registerId = register_id;
        this.positionName = position_name;
        this.deptName = dept_name;

        this.replyCount = reply_cnt;
        this.isPinnedYn = ispinned;



        fileId = attach_edms_id;
        fileName = attach_file;
        files.add(new Doc(attach_edms_id, attach_file));

        this.viewMobileYn = view_mobile;

        this.gubun = gubun;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append(super.toString()).append("\n");
        sb.append("fileId = ").append(fileId).append("\n");
        sb.append("fileName = ").append(fileName);

        return sb.toString();
    }
}
