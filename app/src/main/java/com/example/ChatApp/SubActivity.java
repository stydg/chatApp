package com.example.ChatApp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ChatApp.network.MessageManager;
import com.example.ChatApp.network.ServerInterfaceManager;

import org.json.JSONException;
import org.json.JSONObject;

public class SubActivity extends AppCompatActivity {

    Button btn_start;
    EditText uid;
    String TAG = "SubActivity"; //log 출력시 출력할 tag
    Context mContext;
    String userid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        btn_start = (Button) findViewById(R.id.btn_start);
        uid = (EditText) findViewById(R.id.uid);

        mContext =this;

        //btn_start Button의 Click이벤트
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uid.getText().toString().equals("")) {
                    Toast.makeText(SubActivity.this, "사용할 아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
//                    Intent intent = new Intent(SubActivity.this, MainActivity.class);
//                    String id = uid.getText().toString();
//                    intent.putExtra("uid", id); //uid란 이름으로 id값 넣어서 보냄
//                    startActivity(intent);
                    uid.setText("SJYUN");
                    userid =uid.getText().toString();
                    loginProc();
                }
            }
        });
        //EditText Enter key 방지
        uid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) return true;
                return false;
            }
        });

    }

    private void loginProc() {
        MessageManager.getInstance().clear();
        ServerInterfaceManager.getInstance().clearHandler();
        ServerInterfaceManager.getInstance().login(userid, "", "", new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                String result = (String) msg.getData().getSerializable("result");
                if (result == null || result.equals("null") || result.equals("fail") || result.equals("false")) {
                    Log.e(TAG, "Server connection fail.");

                    return;
                }
                try {
                    JSONObject resultObject = new JSONObject(result);
                    if (resultObject.getString("body").equals("fail")) {
                        Log.e(TAG, "Server connection fail.");
                        return;
                    }
//                    String pw = etPassword.getText().toString().trim();
//                    ServerInterfaceManager.getInstance().loginCheck(TextUtils.isEmpty(pw)?"1":pw);

/*                    String prevUserId = PrefManager.getInstance(mContext).getValue(Common.PREF_LOGIN_USER_ID, "");
                    String userId = etEmployeeNumber.getText().toString();
                    if (!prevUserId.equals(userId)) {
                        //유저 바뀜, 데이터 삭제
                        PrefManager.getInstance(mContext).clear();
                        if (cbRemember.isChecked()) {
                            PrefManager.getInstance(mContext).putValue(Common.PREF_EMPLOYEE_NUMBER, etEmployeeNumber.getText().toString());
                        }
                    }
                    PrefManager.getInstance(mContext).putValue(Common.PREF_LOGIN_USER_ID, userId);
                    PrefManager.getInstance(mContext).putValue(Common.PREF_IS_ACTIVE, true);*/

                    MainActivity.isConnected = true;
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("uid", userid);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();

//                    ServerInterfaceManager.getInstance().sendErrlog("Login", "JSONException");
                }
            }
        });
    }
}
