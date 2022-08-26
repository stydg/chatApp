package com.example.ChatApp.network;

import java.io.Serializable;

/**
 * Created by mobinuri on 2017-04-12.
 */

/**
 "og": [
            {
                 "description": "네이버 메인에서 다양한 정보와 유용한 컨텐츠를 만나 보세요",
                 "image": "https://s.pstatic.net/static/www/mobile/edit/2016/0705/mobile_212852414260.png",
                 "title": "네이버",
                 "url": "http://www.naver.com/"
             }
        ],
*/
public class OpenGraphData implements Serializable {

    public String description;
    public String image;
    public String title;
    public String url;

    public OpenGraphData(String adescription, String aimage, String atitle, String aurl) {
        this.description = adescription;
        this.image = aimage;
        this.title = atitle;
        this.url = aurl;

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("{").append("\n");
        sb.append("description = ").append(description).append("\n");
        sb.append("image = ").append(image).append("\n");
        sb.append("title = ").append(title).append("\n");
        sb.append("url = ").append(url).append("\n");
        sb.append("}").append("\n");
        return sb.toString();
    }
}
