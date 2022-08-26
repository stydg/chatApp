package com.example.ChatApp.network;

import java.io.Serializable;

/**
 * Created by mobinuri on 2017-04-12.
 */

/**
 {
     "type": "rec",
     "address": "client.api./eventbus/532/SKHynix-45baefc5-5457-4f60-8a65-4ae0d93cf38f-I0100750/websocket_10.158.122.138:9000",
     "body": {
         "msg": "",
         "result": {
             "description": "네이버 메인에서 다양한 정보와 유용한 컨텐츠를 만나 보세요",
             "image": "https://s.pstatic.net/static/www/mobile/edit/2016/0705/mobile_212852414260.png",
             "title": "네이버",
             "url": "http://www.naver.com/"
            },
     "alarm_yn": "N",
     "stat": true,
     "message_id": "6171110094711210019",
     "tp": "OG1001",
     "channel_id": "200001831",
     "url": "https://www.naver.com"
 }
*/
public class NotiOpenGraphData implements Serializable {

    private String msg;
    public OpenGraphData result;
    private String alarm_yn;
    private boolean stat;
    public String message_id;
    private String tp;
    private String channel_id;
    private String url;

    public NotiOpenGraphData(String amsg, OpenGraphData aresult, String aalarm_yn, boolean astat, String amessage_id, String atp, String achannel_id, String aurl ) {
        this.msg = amsg;
        this.result = aresult;
        this.alarm_yn = aalarm_yn;
        this.stat = astat;
        this.message_id = amessage_id;
        this.tp = atp;
        this.channel_id = achannel_id;
        this.url = aurl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("{").append("\n");
        sb.append("msg = ").append(msg).append("\n");
        sb.append("result = ").append(result.toString()).append("\n");
        sb.append("alarm_yn = ").append(alarm_yn).append("\n");
        sb.append("stat = ").append(stat).append("\n");
        sb.append("message_id = ").append(message_id).append("\n");
        sb.append("tp = ").append(tp).append("\n");
        sb.append("channel_id = ").append(channel_id).append("\n");
        sb.append("url = ").append(url).append("\n");
        sb.append("}").append("\n");
        return sb.toString();
    }
}
