package com.example.ChatApp.network;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class Doc implements Serializable{
    public static int type_index = 0;
    public static final int TYPE_PPT = type_index++;
    public static final int TYPE_DOC = type_index++;
    public static final int TYPE_XLS = type_index++;
    public static final int TYPE_IMG = type_index++;

    public String fileId;
    public String fileName;
    public String fileSize;
    public String fileUrl;
    public int fileType = -1;
    public ArrayList<Bitmap> fileImageList;//원본보기, 첨부 이미지

    public Doc(String fileName) {
        this.fileName = fileName;
    }

    public Doc(String id, String name) {
        this.fileId = id;
        this.fileName = name;
    }

    public Doc(String id, String name, String size, String url) {
        this.fileId = id;
        this.fileName = name;
        this.fileSize = size;
        this.fileUrl = url;
    }
}
