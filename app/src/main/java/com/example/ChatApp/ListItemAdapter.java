package com.example.ChatApp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ChatApp.network.ChatMessage;
import com.example.ChatApp.network.ChatObject;
import com.example.ChatApp.network.Common;
import com.example.ChatApp.network.DocMessage;
import com.example.ChatApp.network.MessageManager;
import com.example.ChatApp.network.PostMessage;
import com.example.ChatApp.network.ServerInterfaceManager;
import com.example.ChatApp.network.SystemMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class ListItemAdapter extends BaseAdapter {
    ArrayList<ListItem> items = new ArrayList<ListItem>();
    Context context;
    boolean message_left;

    List<ChatObject> messages = new ArrayList();

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        context = parent.getContext();
        ListItem listItem = items.get(position);

        //chatview_item을 inflate해서 convertview에 참조함
        if(convertview == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertview = inflater.inflate(R.layout.chatview_item, parent, false);
        }

        //화면에 보일 데이터를 참조함

        TextView unameText = convertview.findViewById(R.id.uname);
        TextView messageText =  convertview.findViewById(R.id.message);
        ImageView imgProfile_left = convertview.findViewById(R.id.imgProfile_left);
        ImageView imgProfile_right = convertview.findViewById(R.id.imgProfile_right);

        //데이터를 set함
        unameText.setText(listItem.getUname());
        messageText.setText(listItem.getMessage());
        messageText.setTextColor(Color.parseColor("#000000"));

        /*Intent intent = getIntent();//subActivity로 부터 아이디 받아옴
        uid = intent.getExtras().getString("uid");*/


        String uid ="SJYUN";
        // client문자를 포함한 text면 우측 9 패치 이미지로 채팅 버블을 출력 / client와 server 문자열은 삭제해줌
        if (messageText.getText().toString().contains(uid)) {
            message_left = false;
            messageText.setBackground(this.context.getResources().getDrawable(R.drawable.chat_bubble_me));
            messageText.setText(messageText.getText().toString().replace(uid, ""));
        }else {
            message_left = true;
            messageText.setBackground(this.context.getResources().getDrawable(R.drawable.chat_bubble_other));
        }

        // client가 보낸건지 server가 보낸건지에 따라 오,왼 정렬
        LinearLayout chatContainer_outside = (LinearLayout)convertview.findViewById(R.id.chat_layout_outside);
        LinearLayout chatContainer_inside = (LinearLayout)convertview.findViewById(R.id.chat_layout_inside);
        int align;
        if(message_left){
            align = Gravity.LEFT;
            imgProfile_left.setVisibility(View.VISIBLE);
            imgProfile_right.setVisibility(View.INVISIBLE);
        }else {
            align = Gravity.RIGHT;
            imgProfile_right.setVisibility(View.VISIBLE);
            imgProfile_left.setVisibility(View.INVISIBLE);
        }
        chatContainer_outside.setGravity(align);
        chatContainer_inside.setGravity(align);

        return convertview;
    }

    //list에 아이템 추가
    public void addItem(ListItem item){
        items.add(item);
    }


    public void addMessage(ChatObject message) {
        if(messages == null){
            messages = new ArrayList<>();
        }
        // 타 채널 메시지 등록 방지
        if (message == null || (getChannelID() > -1 && getChannelID() != message.channelId)) return;
        //최종 메시지와 데이트키 비교 -> 날짜 구분선 삽입
        String datekey = "";
        ChatObject last = messages.size()>0?messages.get(messages.size()-1):null;
        if(last!=null){
            datekey = last.dateKey;
        }
        if(!datekey.equals(message.dateKey) && message instanceof ChatMessage){

            Calendar rightNow = new GregorianCalendar();
            int dateYear = rightNow.get(Calendar.YEAR);
            int dateMonth = rightNow.get(Calendar.MONTH);
            int dateDay = rightNow.get(Calendar.DAY_OF_YEAR);
           /* String today_dateKey = String.format("%d_%02d_%02d", dateYear, dateMonth, dateDay);
            if(today_dateKey.equals(message.dateKey)){
                messages.add(new SystemMessage("Today"));
            }else{*/
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
            messages.add(new SystemMessage(sdf.format(message.timeMillis), ChatObject.TYPE_SYSTEM));
            /*}*/


//            notifyItemInserted(getLastPosition()-1);
        }
        // push 메시지로 먼저 도착한 OpenGraph가 있는지 체크하고 해당 OpenGraph 정보를 추가
//        message = checkOpenGraphData(message);
//        messages.add(message);
//        notifyItemInserted(getLastPosition()-1); //추가되었으니 추가된걸 그려라

        if(message instanceof ChatMessage && ((ChatMessage) message).commentInfo!=null){
            for(int i=0; i<messages.size(); i++) {
                ChatObject chat = messages.get(i);
             /*   if(chat.messageId.equals(((ChatMessage) message).commentInfo.messageId)){
                    chat.replyCount++;
//                    notifyItemChanged(i);
                    break;*/
                }
            }
        }
    public void clear(){
        messages.clear();
        notifyDataSetChanged();
    }

    public void listViewclear(){
        items.clear();//리스트뷰 초기화
        notifyDataSetChanged();
    }
    public int getLastPosition(){
        return messages.size();
    }
    public int getPosition(String messageId){
        for(int i=0; i<messages.size(); i++){
            ChatObject chat = messages.get(i);
            if (!(chat instanceof SystemMessage)) {
                if(chat.messageId.equals(messageId))
                    return i;
            }
        }
        return -1;
    }
    public void addMessages(ArrayList<ChatObject> _messages) {
        //this.messages = initList(_messages);
        notifyDataSetChanged();
    }
    public int getChannelID() {
        if (messages != null && messages.size() > 0){
            ChatObject _message = this.messages.get(messages.size() - 1);
            return _message.channelId;
        } else {
            return -1;
        }
    }
}
