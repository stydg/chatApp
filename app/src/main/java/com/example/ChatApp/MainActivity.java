package com.example.ChatApp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.Group;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.ChatApp.network.ApiTimeManager;
import com.example.ChatApp.network.AppStarter;
import com.example.ChatApp.network.ChannelInfo;
import com.example.ChatApp.network.ChannelObject;
import com.example.ChatApp.network.ChatObject;
import com.example.ChatApp.network.Common;
import com.example.ChatApp.network.DMChannel;
import com.example.ChatApp.network.LanguageUtil;
import com.example.ChatApp.network.MessageManager;
import com.example.ChatApp.network.PrefChatMessageManager;
import com.example.ChatApp.network.PrefManager;
import com.example.ChatApp.network.ServerInterfaceManager;
import com.example.ChatApp.network.ServerInterfaceRetryManager;
import com.example.ChatApp.network.UnCaughtException;
import com.example.ChatApp.network.User;
import com.example.ChatApp.network.VertxClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MessageManager.Delegate {
    Context mContext;
    public static boolean isFcmNewIntent = false;
    public static final int REQUEST_ALL = 0x00000003;
    public static User profile;
    public static int LastChannelID;
    public static ChannelInfo info;
    boolean reloadFlag = false;
    public static final boolean NEED_MARKET = false;
    private boolean isRestart = false;

    boolean isPrev = false, isNext = false;

    // 채널 타입
    public static final int CHANNEL_TYPE_PRIVATE = 0; //일반채널 가입승인
    public static final int CHANNEL_TYPE_PUBLIC = 1; //공개채널 아무나가입가능
    public static final int CHANNEL_TYPE_SECRET = 2; //비공개채널 초대
    public static final int CHANNEL_TYPE_ANONYNMOUS = 3; //익명채널
    public static final int CHANNEL_TYPE_NOTICE = 4; //공지채널
    public static final int CHANNEL_TYPE_HYTUBE = 5; //hyTUBE

    //메뉴 조정
    Menu menu;
    MenuItem menuItem;


    String uid; //subActivity에서 아이디값 받아오기 위한 uid 변수

    ListView chatView; //채팅창 뷰
    ListItemAdapter adapter; //채팅창 뷰 리스트 추가 위함

    Toast toast;
    ImageButton btn_menu;
    ImageButton btn_close;
    TextView chat_title;
    EditText inputText;
    Button btn_send;

    DrawerLayout drawerLayout;
    private View nav_header; //for nav뷰 위젯제어
    ImageButton drawer_btn_menu;
    ImageButton drawer_btn_close;
    TextView drawer_chat_title;
    ImageButton editId;
    int isEdited = 0;
    EditText profileId;


    /* 서버 통신 */

    public static boolean isConnected = false;
    String TAG = "onFailure"; //log 출력시 출력할 tag
    String sendText;
    private OkHttpClient mClient;

    WebSocket webSocket; //웹소켓 객체
    private static VertxClient vertxClient;
    private static Map<String, Handler> handlerMap = new HashMap<>();
    // API 체크 권한
    public static boolean isHasApiTimeAuth = false;
    //디버그모드 활성화
    public static boolean isDebugEnable = false;
    public static boolean reloadChannelList = false;
    // Bluetooth 키보드 연결 여부
    public boolean IS_CONNECTED_BLUETOOTH_KEYBOARD;
    public static String tempMessageId;//use update or delete or oId click
    public static String moveToMessageId;
    static Handler handler = new Handler();
    int scrollHeightFromBottom = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();//subActivity로 부터 아이디 받아옴
        uid = intent.getExtras().getString("uid");
        //lastChannelID = intent.getExtras().getString("lastchannelID");
//        profileId.setText(uid);

        chatView = findViewById(R.id.chatview);
        adapter = new ListItemAdapter();
//        adapter.addItem(new ListItem("hwi jin", "안녕"));//예시 채팅
//        chatView.setAdapter(adapter);

        mClient = new OkHttpClient();
        chat_title = findViewById(R.id.chat_title);



        mContext = this;

        if (isConnected) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            Log.e("TLOG", "서버랑연결 연결되면: " );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // FileUriExposedException 오류 예외
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                builder.detectFileUriExposure();
            }

            LanguageUtil.init(this);

            // OS에 의한 Activity 재생성시 오류 수정
            // MessageManager.getInstance().clearObserver();
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Common.log("D", "getIntent", key + "=" + bundle.get(key));
            }
            String fcmChannelId = bundle.getString("channel_id", "");
            String fcmMessageId = bundle.getString("message_id", "");
            if (!TextUtils.isEmpty(fcmChannelId) && !TextUtils.isEmpty(fcmMessageId)) {
                MessageManager.getInstance().notification_info.put(fcmMessageId, fcmChannelId);
                SaveFCMIDs(Common.StringToIntDef(fcmChannelId, 0), fcmMessageId);

                isFcmNewIntent = true;
                // 재접속시 채널 이동
                ServerInterfaceRetryManager.getInstance().setMoveRequestChannel(Common.StringToIntDef(fcmChannelId, 0));
            }
            String action = getIntent().getAction();
            if ("CUBE_ACTION".equals(action)) {
                String userId = bundle.getString("user_id", "");
                String userName = bundle.getString("user_name", "");
                PrefManager.getInstance(mContext).putValue(Common.PREF_CALL_DM_USER_ID, userId);
                PrefManager.getInstance(mContext).putValue(Common.PREF_CALL_DM_USER_NAME, userName);
            }
        }

        start();
        chatView.setAdapter(adapter);
        inputText = findViewById(R.id.inputText);
        btn_send = findViewById(R.id.btn_send);

        /*메세지 보내기*/
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputText.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "메시지를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {

                    sendText = inputText.getText().toString();
                    if (info != null && info.channelId > 1000 && profile != null) {
                        ServerInterfaceManager.getInstance().sendMessage(String.valueOf(info.channelId), String.valueOf(profile.userId), sendText, "M", "I");
                    }
                    //adapter.addItem(new ListItem(uid, uid  + sendText));
                    //webSocket.send(sendText);
                }

                chatView.setSelection(adapter.getCount() - 1); //최근 채팅이 보이게함
                inputText.setText(""); //메시지 입력창 초기화
            }
        });/*메세지 보내기 끝*/

        //<--drawer-->
        //메뉴창 열기
        btn_menu = findViewById(R.id.btn_menu);
        btn_close = findViewById(R.id.btn_close);
        btn_menu.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout = findViewById(R.id.drawerLayout);
                if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                    btn_close.setVisibility(View.INVISIBLE);
                    //키보드 내리기
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
                }

            }
        });
        btn_close.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveTaskToBack(true);                        // 태스크를 백그라운드로 이동
                finishAndRemoveTask();                        // 액티비티 종료 + 태스크 리스트에서 지우기
                android.os.Process.killProcess(android.os.Process.myPid());    // 앱 프로세스 종료
            }
        });

        /*--메뉴제어--*/
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this); //nav 메뉴를 다루기 위한 리스너

        // nav_header_main.xml 파일에서 넣어놨던 header 선언
        nav_header = navigationView.getHeaderView(0);

        // header에 있는 리소스 가져오기
        profileId = (EditText) nav_header.findViewById(R.id.profileId);
        profileId.setText(uid); //사용자가 입력한 아이디값으로 변환
        drawer_chat_title = nav_header.findViewById(R.id.drawer_chat_title);
        menu = navigationView.getMenu(); //navigationview에 속한 menu 가져옴

        /*--메뉴창 닫기--*/
        drawer_btn_close = nav_header.findViewById(R.id.drawer_btn_close);
        drawer_btn_close.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout = findViewById(R.id.drawerLayout);
                if (drawerLayout.isDrawerOpen(Gravity.LEFT) & !(profileId.length() == 0) ) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    btn_close.setVisibility(View.VISIBLE); //메인화면 나가기 버튼 활성화
                    if (!(uid.equals(profileId.getText().toString()))) { //아이디 이름이 변경 된경우
                        uid = profileId.getText().toString(); //아이디 uid에 저장
                        toast = Toast.makeText(nav_header.getContext(), "아이디 저장 완료", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP | Gravity.LEFT, 200, 200);
                        toast.show();
                    }
                } else {
                    toast = Toast.makeText(nav_header.getContext(), "아이디를 입력해주세요!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.LEFT, 200, 200);
                    toast.show();
                }
            }
        });
        drawer_btn_menu = nav_header.findViewById(R.id.drawer_btn_menu);
        drawer_btn_menu.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout = findViewById(R.id.drawerLayout);
                if (drawerLayout.isDrawerOpen(Gravity.LEFT) & !(profileId.length() == 0)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    btn_close.setVisibility(View.VISIBLE);
                    if (!(uid.equals(profileId.getText().toString()))) { //아이디 이름이 변경 된경우
                        uid = profileId.getText().toString(); //아이디 uid에 저장
                        toast = Toast.makeText(nav_header.getContext(), "아이디 저장 완료", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP | Gravity.LEFT, 200, 200);
                        toast.show();
                    }
                } else {
                    toast = Toast.makeText(nav_header.getContext(), "아이디를 입력해주세요!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.LEFT, 200, 200);
                    toast.show();
                }
            }
        });
        /*--메뉴창 닫기끝--*/

        /*--프로필 아이디 편집--*/
        editId = nav_header.findViewById(R.id.editId);
        editId.setOnClickListener(new Button.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                int cliked = isEdited % 2;
                drawerLayout = findViewById(R.id.drawerLayout);
                //아이디 편집버튼을 한번 눌렀을때
                if (drawerLayout.isDrawerOpen(Gravity.LEFT) & cliked == 0) {
                    //키보드 올리기
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInputFromWindow(profileId.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

                    profileId.setSelection(profileId.length()); //커서를 끝에 위치!
                    profileId.requestFocus();
                    uid = profileId.getText().toString();
                    isEdited++;
                }
                //아이디 편집버튼을 눌러 저장하고자 할때 / 만약 아이디가 공백이면 저장x
                if (drawerLayout.isDrawerOpen(Gravity.LEFT) & cliked == 1 & !(profileId.length() == 0)) {
                    profileId.setText(profileId.getText().toString());
                    uid = profileId.getText().toString();
                    //키보드 내리기
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(profileId.getWindowToken(), 0);
                    profileId.clearFocus();
                    //수정완료 알림
                    toast = Toast.makeText(nav_header.getContext(), "아이디 수정 완료", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.LEFT, 200, 200);
                    toast.show();
                    isEdited++;
                } else if (drawerLayout.isDrawerOpen(Gravity.LEFT) & (profileId.length() == 0)) {
                    toast = Toast.makeText(nav_header.getContext(), "아이디를 입력해주세요!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.LEFT, 200, 200);
                    toast.show();
                }

            }
        });

        //EditText Enter key 방지
        profileId.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) return true;
                return false;
            }
        });
        /*--프로필 아이디 편집끝--*/

    }

    private void start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkPermission();
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(this));
        //다른 앱에서 큐브내에 지식센터 실행 - 2020.9.9
//        if(BuildConfig.DEBUG) Common.log("D", "PopupMenuManager","action>>> " + Common.intentGetAction);
        new Thread() {
            @Override
            public void run() {
                initBaidu();
            }
        }.start();

        if ("Mock".equals(Common.FLAVOR)) {
            Common.currentUIMode = Common.UI_MODE_CHANNEL;
            initExamData();
        } else {
            initData();

        }


        // http://192.168.0.78:5000/echo
        // test 서버 : wss://demo.piesocket.com/v3/channel_1?api_key=VCXCEuvhGcBDP7XhiJJUDvR1e1D3eiVjgZ9VRiaV&notify_self
//        Request request = new Request.Builder().url("wss://demo.piesocket.com/v3/channel_1?api_key=VCXCEuvhGcBDP7XhiJJUDvR1e1D3eiVjgZ9VRiaV&notify_self").build();
//        EchoWebSocketListener listener = new EchoWebSocketListener();
//        webSocket = mClient.newWebSocket(request, listener); //okhttp통해 (핸드쉐이킹 후 최초접속됨) 웹소켓 객체 생성
//        mClient.dispatcher().executorService(); //.shutdown()붙이면 끊어진 후 다시 연결x, 웹소켓 활성화?

    }/* start()끝 */

    public void SaveFCMIDs(int _fcmChannelId, String _fcmMessageId) {
        boolean _isSaveCID = PrefManager.getInstance(mContext).putValue(Common.PREF_FCM_CHANNEL_ID, _fcmChannelId);
        boolean _isSaveMID = PrefManager.getInstance(mContext).putValue(Common.PREF_FCM_MESSAGE_ID, _fcmMessageId);
        if (!_isSaveCID || !_isSaveMID) {
            PrefManager.getInstance(mContext).resetPrefManager(mContext);
            PrefManager.getInstance(mContext).putValue(Common.PREF_FCM_CHANNEL_ID, _fcmChannelId);
            PrefManager.getInstance(mContext).putValue(Common.PREF_FCM_MESSAGE_ID, _fcmMessageId);
        }
    }

    private void initExamData() {
        setChannelTitle("#예시 타이틀");

        /*MessageManager.getInstance().channelListArray.addAll(ExamDataMaker.getChannelList());
        chatMessageAdapter.addMessages(ExamDataMaker.getChatList());
        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, chatMessageAdapter.getLastPosition());*/
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        ArrayList<String> _permissions = new ArrayList();
        int grantCnt = 0;

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE);
            _permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            grantCnt++;
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            _permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            grantCnt++;
        }
        if (checkSelfPermission(Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_SETTINGS);
            _permissions.add(Manifest.permission.WRITE_SETTINGS);
        } else {
            grantCnt++;
        }

        if (!_permissions.isEmpty()) {
            requestPermissions(_permissions.toArray(new String[_permissions.size()]), REQUEST_ALL);
        } else {
            if (grantCnt == 3) {
            }
        }
    }

    public void initBaidu() {
        Common.log("E", "BAIDU", "==================baidu start service====================");
    }

    public void setChannelTitle(String title) {
        chat_title.setText(title);
    }


    /* 웹소켓 리스너 클래스 정의*/
    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int CLOSE_STATUS = 1000;

        @Override //웹소켓 연결 성공시 실행
        public void onOpen(WebSocket webSocket, Response response) {
            Log.e("TLOG", "전송 데이터 확인 : " + webSocket + " : " + response);
            webSocket.send("ready"); //서버에 연결 확인용 메시지 전송
        }

        @Override //서버로 부터 데이터 수신시 실행
        public void onMessage(WebSocket webSocket, String message) {
            if (message.equals("ready"))
                print("<connected server>");  //연결 성공을 화면에 출력
            else if (message.equals(sendText)) { //보낸 메시지와 받은 메시지가 동일한 경우
                print("reciveText");//내가 실행하고 싶은 코드
            }
        }

        //        @Override
//        public void onMessage(WebSocket webSocket, ByteString bytes) {
//            print("Receive Bytes : " + bytes.hex());
//        }
        @Override //webSocket.close호출 시 실행됨
        public void onClosing(WebSocket webSocket, int code, String reason) {
            print("Closing Socket : " + code + " / " + reason);
            webSocket.close(CLOSE_STATUS, null);
            webSocket.cancel();
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
            print("Error : " + throwable.getMessage());
            Log.e(TAG, "Error : " + throwable.getMessage());
        }
    } /* 웹소켓 리스너 클래스 정의 끝 */

    /* print() */
    private void print(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message.equals("reciveText")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addItem(new ListItem(uid, "server" + sendText));//리스트에 받은메시지 추가
                            chatView.setSelection(adapter.getCount() - 1);
                        }
                    }, 2000); //내가 미루고싶은 시간 단위 : millis

                } else {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }/* print() 끝 */

    /*event bus 연결*/
    /*event bus 연결끝*/

    /*vertx 리스너 정의*/
    /*vertx 리스너 정의 끝*/

    public void initData() {
        registObserver();
        ApiTimeManager.getInstance().startApi();
        if (isHasApiTimeAuth) {
            initApiTimeMap();
        }
        reloadChannelList = false;
        IS_CONNECTED_BLUETOOTH_KEYBOARD = false;

        if (getResources() != null && getResources().getConfiguration() != null) {
            IS_CONNECTED_BLUETOOTH_KEYBOARD = getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO;
        }
        // DialogManager.getInstance().showProgressDialog(this);
//        userId = PrefManager.getInstance(getApplicationContext()).getValue(Common.PREF_LOGIN_USER_ID, "");
//        channelId = PrefManager.getInstance(getApplicationContext()).getValue(Common.PREF_LAST_CHANNEL_ID, "");
//        channelName = PrefManager.getInstance(getApplicationContext()).getValue(Common.PREF_LAST_CHANNEL_NAME, "");

        tempMessageId = null;
        moveToMessageId = null;
        scrollHeightFromBottom = 0;


        Task<String> token = FirebaseMessaging.getInstance().getToken();
        String baiduKey = PrefManager.getInstance(AppStarter.applicationContext).getValue(Common.PREF_BAIDU_CHANNELID, "");
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String simOperator = tm.getSimOperator();

        //공용앱에서만 NOTI 막기 - 2019.12.24
//        if (!BuildConfig.FLAVOR.equals("realServerSpecial")) {

        if (token != null && baiduKey != null) {
            //서버에 토큰 전달
            ServerInterfaceManager.getInstance().notiKey(token, baiduKey, simOperator);
        } else if (token != null) {
            ServerInterfaceManager.getInstance().notiKey(token, "", simOperator);
        }
//        }
        //나나 FragmentMessageList.isFav = PrefManager.getInstance(mContext).getValue(Common.PREF_IS_FAVORITE, false);

        //hyTUBE 채널 분리 - 2020.10.30
//        if (MessageManager.getInstance().channelListArray.size() + MessageManager.getInstance().dmListArray.size() > 0) {
        //하이피드백 채널 분리 - 2021.4.21
//        if (MessageManager.getInstance().channelListArray.size() + MessageManager.getInstance().dmListArray.size()
//                + MessageManager.getInstance().hytubeListArray.size() > 0) {
        if (MessageManager.getInstance().channelListArray.size() > 0) {
            ServerInterfaceManager.getInstance().selectProfile(); //서버에 send보내고 값 요청 messgemanager.profile에서 불러다씀 user나
        } else {
            handler.removeCallbacks(profileRunnable);
            handler.postDelayed(profileRunnable, 200);
        }
        ServerInterfaceManager.getInstance().selectChannelList(); //서버에 send보내고 값 요청 messageManager.channelList에 저장됨


        //hyTUBE 채널 분리 - 2020.10.30
        //ServerInterfaceManager.getInstance().selectHyTUBEChannelList();
        //하이피드백 채널 분리 - 2021.4.21
        //ServerInterfaceManager.getInstance().selectHFChannelList();
        //DM 즐겨찾기 탭처리 - 2020.11.16

/*        if(FragmentMessageList.isDMFavoriteList)
            ServerInterfaceManager.getInstance().selectDMFavoriteChannel();
        else
            ServerInterfaceManager.getInstance().selectDMChannelList(FragmentMessageList.isFav);

        ServerInterfaceManager.getInstance().selectEmoticonGroup();
        ServerInterfaceManager.getInstance().selectCommandList();
        ServerInterfaceManager.getInstance().selectTranslateSupportList();
        ServerInterfaceManager.getInstance().selectBotTypeList();
        ServerInterfaceManager.getInstance().selectSettings();*/

        //selectProfile();
        // 네트워크 연결시 채널 재접속을 위해 저장된 채널정보 초기화
        //ServerInterfaceRetryManager.getInstance().setAccessFailChannnelID(0);

    }
    private void registObserver() {

        MessageManager.getInstance().addObserver(this, MessageManager.profile);
        MessageManager.getInstance().addObserver(this, MessageManager.moveToMessage);
        MessageManager.getInstance().addObserver(this, MessageManager.channelLoad);
        MessageManager.getInstance().addObserver(this, MessageManager.channelInfo);
        MessageManager.getInstance().addObserver(this, MessageManager.channelInfoUpdate);
        MessageManager.getInstance().addObserver(this, MessageManager.updateChatList);
        MessageManager.getInstance().addObserver(this, MessageManager.pushMessage);
        MessageManager.getInstance().addObserver(this, MessageManager.receiveMessage);

        MessageManager.getInstance().addObserver(this, MessageManager.receiveUpdateMessage);
        MessageManager.getInstance().addObserver(this, MessageManager.receiveDeleteMessage);

        MessageManager.getInstance().addObserver(this, MessageManager.deleteComment);

        MessageManager.getInstance().addObserver(this, MessageManager.pinnedMessage);
        MessageManager.getInstance().addObserver(this, MessageManager.favoriteMessage);
        MessageManager.getInstance().addObserver(this, MessageManager.updateTodo);
        MessageManager.getInstance().addObserver(this, MessageManager.removeTodo);

        MessageManager.getInstance().addObserver(this, MessageManager.updatevoteanswer);

        MessageManager.getInstance().addObserver(this, MessageManager.commandList);
        MessageManager.getInstance().addObserver(this, MessageManager.mentionList);
        MessageManager.getInstance().addObserver(this, MessageManager.fileAuthKey);
        MessageManager.getInstance().addObserver(this, MessageManager.reconnected);
        //이모티콘 자주사용 기능(즐겨찾기) 추가 - 2021.5.17
        MessageManager.getInstance().addObserver(this, MessageManager.selectAddLikeEmoticon);

        MessageManager.getInstance().addObserver(this, MessageManager.setttingAnonymousChannel);

        MessageManager.getInstance().addObserver(this, MessageManager.selectChannelnfoAndgoLastChannel);


        //hyTUBE 채널 분리 - 2020.10.30
        MessageManager.getInstance().addObserver(this, MessageManager.hytubeList);
        //하이피드백 채널 분리 - 2021.4.21
        MessageManager.getInstance().addObserver(this, MessageManager.HFList);
        //비밀문서함 링크
    }
    private void initApiTimeMap() {
        MessageManager.getInstance().clearApiTimeMap();
        MessageManager.getInstance().addApiTime("websocket.apiMobileLog");
        MessageManager.getInstance().addApiTime("websocket.apiNotificationKey");
        MessageManager.getInstance().addApiTime("bizrunner.selectChannelList");

        //hyTUBE 채널 분리 - 2020.10.30
        MessageManager.getInstance().addApiTime("bizrunner.selectHyTUBEChannelList");
        //하이피드백 채널 분리 - 2021.4.21
        MessageManager.getInstance().addApiTime("bizrunner.selectHyFeedBackChannelList");

        MessageManager.getInstance().addApiTime("websocket.selectDMChannel");
        //DM 즐겨찾기 탭처리 - 2020.11.16
        MessageManager.getInstance().addApiTime("websocket.SelectDMFavoriteChannel");
        MessageManager.getInstance().addApiTime("websocket.selectEmoticonGroup");
        MessageManager.getInstance().addApiTime("websocket.selectCommandList");
        MessageManager.getInstance().addApiTime("websocket.selectTranslatSupportTypeList");
        MessageManager.getInstance().addApiTime("websocket.selectBotTypeList");
        MessageManager.getInstance().addApiTime("websocket.unreadChannelMessageCount");
        MessageManager.getInstance().addApiTime("websocket.unreadDMChannelMessageCount");
        MessageManager.getInstance().addApiTime("websocket.selectProfile");
        MessageManager.getInstance().addApiTime("websocket.apirequest_APL1004");
        MessageManager.getInstance().addApiTime("websocket.apirequest_EML1003");
        MessageManager.getInstance().addApiTime("websocket.apirequest_TOD1001");
        MessageManager.getInstance().addApiTime("websocket.apirequest_CAL1001");
        MessageManager.getInstance().addApiTime("websocket.selectChannelInfoSummary");
        MessageManager.getInstance().addApiTime("websocket.selectMessageList");
    }

    //채널 목록 조회 완료 후 프로필 정보 조회하도록 로직 수정
    Runnable profileRunnable = new Runnable() {
        @Override
        public void run() {
            int channelCount = MessageManager.getInstance().channelListArray.size() + MessageManager.getInstance().dmListArray.size();
            if (channelCount > 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ServerInterfaceManager.getInstance().selectProfile();

                    }
                });
            } else {
                handler.postDelayed(profileRunnable, 200);
            }
        }
    };

    /* nav 메뉴를 다루기 위한 리스너 */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        long _MoveRequestChannelID;
        switch (item.getItemId()) {
            case R.id.nav_ch1:
                ServerInterfaceRetryManager.getInstance().setMoveRequestChannel(200000105); // 채널아디 설정
                _MoveRequestChannelID = ServerInterfaceRetryManager.getInstance().getMoveRequestChannel(); //저장되있는 채널아디 가져옴
                if (_MoveRequestChannelID > 0) {
                    MessageManager.getInstance().postNotification(MessageManager.channelLoad, _MoveRequestChannelID); // 해당 채널아디에 대해 로드함
                } else { Log.e("tah","메뉴 1");
                    MessageManager.getInstance().postNotification(MessageManager.channelLoad, profile.lastChannelId);
                }
                Toast.makeText(this, "item1 clicked..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_ch2:
                ServerInterfaceRetryManager.getInstance().setMoveRequestChannel(200000108);
                _MoveRequestChannelID = ServerInterfaceRetryManager.getInstance().getMoveRequestChannel();
                if (_MoveRequestChannelID > 0) {

                    MessageManager.getInstance().postNotification(MessageManager.channelLoad, _MoveRequestChannelID);
                } else { Log.e("tah","메뉴2");
                    MessageManager.getInstance().postNotification(MessageManager.channelLoad, profile.lastChannelId);
                }
                Toast.makeText(this, "item2 clicked..", Toast.LENGTH_SHORT).show();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }/* nav 메뉴를 다루기 위한 리스너 끝*/

    public void setSubMenuTitle(String title) {
        drawer_chat_title.setText(title);
        if (TextUtils.isEmpty(title))
            drawer_chat_title.setVisibility(View.GONE);
        else
            drawer_chat_title.setVisibility(View.VISIBLE);
    }


    public static boolean isConnected(){
        if(!isConnected){

            /*Common.log("W", Common.LOG_TAG_NETWORK, "isConnected="+isConnected);
            StackTraceElement[] a = new Throwable().getStackTrace();
            StringBuffer sb = new StringBuffer();
            for(int i = 1; i < a.length; i++){
                sb.append("at "+a[i].getClassName() + "("+a[i].getFileName()+":"+a[i].getLineNumber()+")"+"\n");
            }
            Common.log("W", Common.LOG_TAG_NETWORK, sb.toString());*/
        }
        return isConnected;
    }
    Runnable checkReconnectRun = new Runnable() {
        @Override
        public void run() {
            if (!AppStarter.isBackground() && ServerInterfaceRetryManager.isTryReconnect()) {
                ServerInterfaceManager.getInstance().disconnect();
                ServerInterfaceManager.getInstance().reconnect(reconnectHandler);
                handler.removeCallbacks(checkReconnectRun);

                //화면 전환 속도 개선(이동원TL 요청)- 2020.5.22
//              handler.postDelayed(checkReconnectRun, 5250);
                handler.postDelayed(checkReconnectRun, 1000);

                ServerInterfaceRetryManager.incTryReconnectCount();
            }
        }
    };
    Handler reconnectHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean isLogin = false;
            String result = (String) msg.getData().getSerializable("result");
            if(result == null || result.equals("null") || result.equals("fail") || result.equals("false")){
                isLogin = false;
            }else{
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if(resultJson.has("body")){
                        String body = resultJson.getString("body");
                        if("fail".equals(body)){
                            isLogin = false;
                        }else{
                            isLogin = true;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(isLogin){
                isConnected = true;
                PrefManager.getInstance(mContext).putValue(Common.PREF_IS_ACTIVE, true);
                MessageManager.getInstance().postNotification(MessageManager.reconnected);
            }else{
                restartApp();
            }
        }
    };

    protected void restartApp(){
        isRestart = true;
        if(!NEED_MARKET){
            PrefManager.getInstance(AppStarter.applicationContext).putValue(Common.PREF_IS_ACTIVE, false);
            Common.toast("다른 기기에서 접속한 사용자입니다. 재실행합니다.");
        }

        // YS-DEBUG
        MessageManager.getInstance().clear();
        Intent intent = new Intent(this, MainActivity.class) ;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("RESTART_APP", true);
        startActivity(intent);
    }

    public void goLastChannel() {
        final int fcmChannelId = PrefManager.getInstance(mContext).getValue(Common.PREF_FCM_CHANNEL_ID, 0);
        final String fcmMessageId = PrefManager.getInstance(mContext).getValue(Common.PREF_FCM_MESSAGE_ID, "");
        Log.e("TLOG", "goLastChannel " + fcmChannelId + fcmMessageId );
        if (fcmChannelId > 0 && !TextUtils.isEmpty(fcmMessageId) && !MessageManager.getInstance().hasChannel(String.valueOf(fcmChannelId))) {
            // 채널이 존재하지 않기 때문에 채널 정보 요청 후 채널리스트에 등록하고 최근 채널 이동
            if (profile != null && !TextUtils.equals(profile.lastChannelId, String.valueOf(fcmChannelId)))
                profile.lastChannelId = String.valueOf(fcmChannelId);
            ServerInterfaceManager.getInstance().selectChannelnfoAndgoLastChannel(String.valueOf(fcmChannelId));
        } else if (profile != null && (TextUtils.isEmpty(profile.lastChannelId) || profile.lastChannelId.equals("-1") || !MessageManager.getInstance().hasChannel(profile.lastChannelId))) {
            /*if (!MessageManager.getInstance().hasChannel(profile.lastChannelId)) {
                handler.removeCallbacks(checkReconnectRun);
                DMChannel _dmch = null;
                // DM 채널 목록 70개 제한에 따른 처리
                if (_dmch != null) {
                    MessageManager.getInstance().dmListArray.add(new DMChannel(_dmch.channelId, _dmch.channelName.toString(), _dmch.messageId, _dmch.lastMessage, _dmch.members, _dmch.userCnt, _dmch.leave, _dmch.favorite));
                    goLastChannel();
                } else {
                    // 채널이 존재하지 않기 때문에 채널 정보 요청 후 채널리스트에 등록하고 최근 채널 이동
                    ServerInterfaceManager.getInstance().selectChannelnfoAndgoLastChannel(profile.lastChannelId);
                }
            } else {
                drawerLayout.openDrawer(Gravity.LEFT);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, drawerLayout);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, drawerLayout);
                handler.removeCallbacks(checkReconnectRun);

            }*/
        } else {
            if (fcmChannelId > 0 && !TextUtils.isEmpty(fcmMessageId)) {
                if (isConnected()) {
                    //if (chatMessageAdapter != null) chatMessageAdapter.clear();
                    int _fcmChannelId = Common.StringToIntDef(MessageManager.getInstance().notification_info.get(fcmMessageId), 0);
                    if (_fcmChannelId == fcmChannelId) {
                        MessageManager.getInstance().postNotification(MessageManager.moveToMessage, fcmChannelId, fcmMessageId);
                    } else if (_fcmChannelId > 0) {
                        MessageManager.getInstance().postNotification(MessageManager.moveToMessage, _fcmChannelId, fcmMessageId);
                    } else {
                        if (MessageManager.getInstance().notification_info.isEmpty()) {
                            MessageManager.getInstance().postNotification(MessageManager.moveToMessage, fcmChannelId, fcmMessageId);
                        } else {
                            Iterator<String> _iter = MessageManager.getInstance().notification_info.keySet().iterator();
                            if (_iter.hasNext()) {
                                String __fcmMessageId = _iter.next();
                                _fcmChannelId = Common.StringToIntDef(MessageManager.getInstance().notification_info.get(__fcmMessageId), 0);
                                MessageManager.getInstance().postNotification(MessageManager.moveToMessage, _fcmChannelId, __fcmMessageId);
                                MessageManager.getInstance().notification_info.remove(__fcmMessageId);
                            } else {
                                MessageManager.getInstance().postNotification(MessageManager.moveToMessage, fcmChannelId, fcmMessageId);
                            }
                        }
                    }
                    MessageManager.getInstance().notification_info.remove(fcmMessageId);
                    SaveFCMIDs(0, "");
                }
            } else {
                // 네트워크 재접속시에 채널 이동할 경우 해당 채널로 가지 않고 서버에서 전송된 최근 접속 채널로 이동하는 오류 수정
                long _MoveRequestChannelID = ServerInterfaceRetryManager.getInstance().getMoveRequestChannel();
                if (_MoveRequestChannelID > 0) {

                    MessageManager.getInstance().postNotification(MessageManager.channelLoad, _MoveRequestChannelID);
                } else { Log.e("tah","랴랴랴랴랴랴랴랴랴랴");
                    MessageManager.getInstance().postNotification(MessageManager.channelLoad, profile.lastChannelId);
                }
            }
        }
    }


    /*private void initClist() {
        menu.findItem(R.id.nav_ch1).setTitle(info.getChannelName()); // 메뉴항목의 첫번째도 변경
        channelListAdapter.addChannels(MessageManager.getInstance().channelListArray);
        notifyList();
    }
    public void notifyList(){
        //채널 목록 표시 오류 수정 - 2020.4.7
        channelListAdapter.notifyDataSetChanged();
        int size = channelListAdapter.getItemCount();
        if(size>1){
            noChannelTextView.setVisibility(View.GONE);
            channelRecyclerView.setVisibility(View.VISIBLE);

        }else{
            noChannelTextView.setVisibility(View.VISIBLE);
            channelRecyclerView.setVisibility(View.GONE);
        }

    }*/

    private void checkNewMessage() {
        int lastVisibleItem = adapter.getCount();
        if (lastVisibleItem < adapter.getLastPosition() - 10) {
            //New Message 표시
            if (scrollHeightFromBottom != 0) {
                toggleNewMessage(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toggleNewMessage(false);
                    }
                }, 1000);
            }

        }
    }

    public void toggleNewMessage(boolean isNewMessage) {
       /* final RelativeLayout newMessageLayout = (RelativeLayout) findViewById(R.id.newMessageLayout);
        newMessageLayout.setOnClickListener(onClickListener);

        Animation openAni = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) newMessageLayout.getLayoutParams();
                params.bottomMargin = (int) (newMessageLayout.getMeasuredHeight() * interpolatedTime) + (int) Common.convertDpToPixel(48, mContext);
                newMessageLayout.setLayoutParams(params);
            }
        };
        openAni.setDuration(300);
        Animation closeAni = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) newMessageLayout.getLayoutParams();
                params.bottomMargin = newMessageLayout.getMeasuredHeight() + (int) (-newMessageLayout.getMeasuredHeight() * interpolatedTime) + (int) Common.convertDpToPixel(48, mContext);
                newMessageLayout.setLayoutParams(params);
            }
        };
        closeAni.setDuration(300);
        if (isNewMessageView != isNewMessage) {
            this.isNewMessageView = isNewMessage;
            if (isNewMessage)
                newMessageLayout.startAnimation(openAni);
            else
                newMessageLayout.startAnimation(closeAni);
        }*/
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        Log.e("TLOG", id + " didREceive 들어옴... : "  );
        if (BuildConfig.DEBUG)
            Common.log("D", "MainActivity", "MainActivity-didReceivedNotification");
        if (id == MessageManager.profile) {
            Log.e("TLOG", "didREceive id가 profile인경우 " + id );
            if (BuildConfig.DEBUG)
                Common.log("D", "MainActivity", "MainActivity-didReceivedNotification = MessageManager.profile");
            if (args[0] instanceof User) {
                profile = (User) args[0];
                profile.NotificationSound = PrefManager.getInstance(mContext).getValue(Common.PREF_NOTIFICATION_SOUND, "");
                PrefManager.getInstance(mContext).putValue(Common.PREF_LOGIN_USER_ID, profile.uniqueName);
            }
            //채널 호출
            LastChannelID = Integer.parseInt(profile.getLastChannelId());//사용자가 마지막으로 접근한 채널아이디 가져옴

            goLastChannel();
        }


        if (id == MessageManager.channelLoad) {
            Log.e("TLOG", "didREceive id가 channelLoad인경우 " + id );
            if(BuildConfig.DEBUG) Common.log("D", "MainActivity","MainActivity-didReceivedNotification = MessageManager.channelLoad, reloadFlag="+reloadFlag);

            String channelId = String.valueOf(args[0]);

            Log.e("TLOG", "didREceive id가 channelLoad인경우 -> " + channelId );

//            if (channelId.startsWith("2")) {
//                Common.currentUIMode = Common.UI_MODE_CHANNEL;
//                PrefManager.getInstance(getApplicationContext()).putValue(Common.PREF_LAST_CHANNEL_MODE, Common.UI_MODE_CHANNEL);
//            } else {
//                Common.currentUIMode = Common.UI_MODE_MESSAGE;
//                PrefManager.getInstance(getApplicationContext()).putValue(Common.PREF_LAST_CHANNEL_MODE, Common.UI_MODE_MESSAGE);
//            }
//
//            if (drawerLayout.isDrawerOpen(Gravity.LEFT) && isConnected()) {
//                // 채널 목록이 닫히면서 채널정보 요청으로 인한 중복 reload 방지(채널 생성시)
//                channelreloadFlag = false;
//            }
//            drawerLayout.closeDrawers();
//            if (!isConnected()) return;
            //채널 로딩중 클릭이벤트 방지 - 2020.4.7 이동원TL 요청사항
//                DialogManager.getInstance().showProgressDialog(this);
            ServerInterfaceManager.getInstance().getChannelInfo(channelId);
         /*   messageEditCancel();
            // 임시 저장된 채팅메시지를 설정
            if (info != null && PrefChatMessageManager.getInstance(mContext).getMessage(String.valueOf(info.channelId), "").length() > 0) {
                etChatBoxTextChangedByKeyborad = false;
                etChatBox.setText(PrefChatMessageManager.getInstance(mContext).getMessage(String.valueOf(info.channelId), ""));
                if (etChatBox.getText().length() > 0) {
                    etChatBox.setSelection(etChatBox.length());
                }
            }*/
        }

        if(id == MessageManager.channelList){
            Log.e("TLOG", "didREceive id가 channelList인 경우 " + id );
            for (ChannelObject channel : MessageManager.getInstance().channelListArray) {
                if (channel.channelName != null) {
                    menu.findItem(R.id.nav_ch1).setTitle(channel.channelName);
                }
            }
            //initClist();
            //ServerInterfaceManager.getInstance().getUnreadMessageCount(0);
        }
            if (id == MessageManager.channelInfo) {
                Log.e("TLOG", "didREceive id가 channelInfo 인경우 " + id);
                if (BuildConfig.DEBUG)
                    Common.log("D", "MainActivity", "MainActivity-didReceivedNotification = MessageManager.channelInfo");
                if (args.length > 0 && args[0] instanceof ChannelObject) {
                    info = (ChannelInfo) args[0];
                    Log.e("TLOG", "didREceive id가 channelInfo 인경우 " + LastChannelID);
                    Log.e("TLOG", "didREceive id가 channelInfo 인경우 " + info.getChannelId());

                    setChannelTitle((String) info.getChannelName()); //타이틀을 해당 채널 이름으로 변경
                    setSubMenuTitle((String) info.getChannelName());

                    /**채널 목록 불러와서 설정하기**/
                    int count = 0;
                    for (ChannelObject channel : MessageManager.getInstance().channelListArray) {
                        if (channel.channelName != null) {
                            menuItem = menu.getItem(count);
                            menuItem.setTitle(channel.channelName);
                            //채널 타입에 따른 아이콘 설정
                            switch (channel.channelType) {
                                case 0:
                                    menuItem.setIcon(R.drawable.ic_menu_hash);
                                    break;
                                case 1:
                                    menuItem.setIcon(R.drawable.ic_menu_microphone);
                                    break;
                            }
                        }
                        count++;
                    }/**채널 목록 불러와서 설정하기**/

//마지막 채널이 모바일공개여부(비공개)인 경우 채널이동 로직 추가 - 2021.3.4
                    if (info != null && info.mOpen.equals("N")) {

                    } else { //마지막 채널이 공개 채널인 경우
//                if(BuildConfig.DEBUG) Common.log("D", "CHECKHYTUBE","channel_type button= " + info.channelType +", sysName="+ info.sysName +","+ info.channelName);
                        String unreadMessageId = (String) args[1];
                        PrefManager.getInstance(getApplicationContext()).putValue(Common.PREF_LAST_CHANNEL_ID, String.valueOf(info.channelId));
                        PrefManager.getInstance(getApplicationContext()).putValue(Common.PREF_LAST_CHANNEL_NAME, (String) info.channelName);


                        //initChatList();

                        String lastChannelId = PrefManager.getInstance(getApplicationContext()).getValue(Common.PREF_LAST_CHANNEL_ID, "");
                        if (!TextUtils.isEmpty(lastChannelId))
                            ServerInterfaceManager.getInstance().unregisterChannelId(lastChannelId);
                        ServerInterfaceManager.getInstance().registerChannelId(String.valueOf(info.channelId));
                        if (TextUtils.isEmpty(moveToMessageId) || isFcmNewIntent) {

                            if (TextUtils.isEmpty(unreadMessageId) || unreadMessageId.equals("-1")) {
                                isFcmNewIntent = false;
                                ServerInterfaceManager.getInstance().getChannelMessageList(String.valueOf(info.channelId), "", "RECENT");
                            } else {
                                ServerInterfaceManager.getInstance().getChannelMessageList(String.valueOf(info.channelId), unreadMessageId, "FIND");
                            }
                        } else {
                            ServerInterfaceManager.getInstance().getChannelMessageList(String.valueOf(info.channelId), moveToMessageId, "FIND");
                        }
                        MessageManager.getInstance().postNotification(MessageManager.reloadUnread);

                    }
                }
            }

            //채널 입장하고 msg받은 경우
        if (id == MessageManager.receiveMessage) {
            Log.e("TLOG", "didREceive id가 receiveMessage 인경우 " + id);
            if(BuildConfig.DEBUG) Common.log("D", "MainActivity","MainActivity-didReceivedNotification = MessageManager.receiveMessage");
            ChatObject chat = (ChatObject) args[0];
            if (info.channelId == chat.channelId) {
                //receive message
                if (MessageManager.getInstance().chatsArray.size() > 0) {
                    Log.e("TLOG", "didREceive id가 receiveMessage 1");
                    ChatObject lastMessage = MessageManager.getInstance().chatsArray.get(MessageManager.getInstance().chatsArray.size() - 1);
                    if (lastMessage.messageId.equals(chat.messageId)) { //메시지 콜백이 두번 들어오는 경우 방지
                        Log.e("TLOG", "didREceive id가 receiveMessage 1-1");
                        return;
                    }
                    if (!isNext) {
                        Log.e("TLOG", "didREceive id가 receiveMessage 2");
                        MessageManager.getInstance().chatsArray.add(chat);
                        adapter.addMessage(chat);
                        ChatObject chatbox = MessageManager.getInstance().chatsArray.get(MessageManager.getInstance().chatsArray.size() - 1);
                        if(uid.equals(chatbox.registerUniqueName)){
                            adapter.addItem(new ListItem(chatbox.registerUniqueName,uid + chatbox.messageText));
                        }else{
                            adapter.addItem(new ListItem(chatbox.registerUniqueName,chatbox.messageText));
                        }
                        if (chat.registerId == profile.userId || scrollHeightFromBottom == 0) {
                            Log.e("TLOG", "didREceive id가 receiveMessage 2-1");
                            chatView.clearFocus();
                            chatView.post(new Runnable() {
                                @Override
                                public void run() {
                                    chatView.setSelection(adapter.getCount() - 1);
                                }
                            });

                        } else {
                            Log.e("TLOG", "didREceive id가 receiveMessage 3");
                            checkNewMessage();
                        }
                    } else {
                        Log.e("TLOG", "didREceive id가 receiveMessage 4");
                        checkNewMessage();
                    }
                } else {
                    Log.e("TLOG", "didREceive id가 receiveMessage 5");
                    MessageManager.getInstance().chatsArray.add(chat);
                    adapter.addMessage(chat);
                }

                // 다중 이미지 업로드
                //영상 업로드 가능하도록 개발 - 2020.10.7(F 추가)
                if (TextUtils.equals(chat.type, "I") || TextUtils.equals(chat.type, "F") ) {
                    //repeatSendImages();
                }
            } else {
                //언리드 갱신 - 여기 들어오면 사실 문제 있는것임...다른 채널일 때는 리시브 메시지로 들어오면 안됨(push로 들어옴)
                MessageManager.getInstance().postNotification(MessageManager.reloadUnread);
            }

        }

        if (id == MessageManager.updateChatList) {
            Log.e("TLOG", "didREceive id가 updateChatList 인경우 " + id);
            if(BuildConfig.DEBUG) Common.log("D", "MainActivity","MainActivity-didReceivedNotification = MessageManager.updateChatList");
            String messageId = (String) args[0];

            int _isPrev = (int) args[1]; //-1
            int _isNext = (int) args[2]; //0
            int refreshItemCount = (int) args[3];
            handler.removeCallbacks(checkReconnectRun);

            // Invalid item position 에러 방지 -2020.6.10
//            recyclerView.getRecycledViewPool().clear();

            // Body가 fail일 경우 reloadFlag 해제
            if (TextUtils.equals("-1", messageId) && refreshItemCount == -1 || refreshItemCount == 0) {
                reloadFlag = false;System.out.println("body fail1");
                // 채널 정보 조회 실패 처리
                if (_isPrev == -999 && _isNext == -999) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
                this.isPrev = _isPrev == 1 ? true : false;
                this.isNext = _isNext == 1 ? true : false;

                return;
            }

            if (MessageManager.getInstance().chatsArray.size() == 0 || refreshItemCount == 0) {
                reloadFlag = false;System.out.println("body fail2");
                this.isPrev = _isPrev == 1 ? true : false;
                this.isNext = _isNext == 1 ? true : false;

                return;
            }
            if (MessageManager.getInstance().chatsArray.size() > 0) { //31
                System.out.println("body fail3");
                ChatObject firstMessage = MessageManager.getInstance().chatsArray.get(0);
                ChatObject lastMessage = MessageManager.getInstance().chatsArray.get(MessageManager.getInstance().chatsArray.size() - 1);
                if (adapter.getPosition(firstMessage.messageId) > 0 && adapter.getPosition(lastMessage.messageId) > 0) { //메시지 콜백이 두번 들어오는 경우 방지
                    reloadFlag = false;
                    return;
                }
            }
            adapter.listViewclear(); //리스트뷰 초기화
            /**접속한 화면 채팅그리기**/
            for(int i = 1; i < MessageManager.getInstance().chatsArray.size(); i++){
                ChatObject chatbox = MessageManager.getInstance().chatsArray.get(i);
                if(uid.equals(chatbox.registerUniqueName)){
                    adapter.addItem(new ListItem(chatbox.registerUniqueName,uid + chatbox.messageText));
                }else{
                    adapter.addItem(new ListItem(chatbox.registerUniqueName,chatbox.messageText));
                }
            }/**접속한 화면 채팅그리기**/

             //최근 채팅이 보이게함
            chatView.clearFocus();
            chatView.post(new Runnable() {
                @Override
                public void run() {
                    chatView.setSelection(adapter.getCount() - 1);
                }
            });

            if (messageId.equals("")) {//RECENT or FIND
                this.isPrev = _isPrev == 1 ? true : false;
                this.isNext = false;
//                // 동일 채널 접속시 강제 스크롤 이동 제한
//                boolean _isForceScroll = false;
//                // 이전 채널과 다른 채널일경우 Adapter 갱신
//                if (chatMessageAdapter.getChannelID() != info.channelId){
//                    if (BuildConfig.DEBUG) Common.log("E", "ActivityEvent", "MainActivity-id == MessageManager.updateChatList-_isForceScroll");
//                    initChatList(true);
//                    _isForceScroll = true;
//                }
                adapter.addMessages(MessageManager.getInstance().chatsArray);
                if (!TextUtils.isEmpty(moveToMessageId)) { //FIND 체크
                    this.isNext = _isNext == 1 ? true : false;
                    int position = adapter.getPosition(moveToMessageId);
                    Log.e("TLOG", "didREceive id가 updateChatList position " + adapter.getPosition(moveToMessageId));
                    if (position > 0) {
                        chatView.setSelection(position);//검색 해서 찾은채팅내용으로 이동
                    } else {
                        chatView.setSelection(adapter.getCount() - 1); //최근 채팅이 보이게함  // 가장 최근 대화로 포커씽
                    }
                    moveToMessageId = null;
                } else {
                    // 이전 채널과 다른 채널일 경우와 사용자에 의한 채널 이동일 경우만 스크롤 이동
                    //if (_isForceScroll) {
                    chatView.setSelection(adapter.getLastPosition()); //최근 채팅이 보이게함
                    //}

                    //챗봇 인삿말 처리
                }
            } else {// PREV or NEXT
                if (adapter.getPosition(messageId) == 1) { //Prev
                    this.isPrev = _isPrev == 1 ? true : false;
                } else if (adapter.getPosition(messageId) == adapter.getLastPosition() - 1) { //Next
                    this.isNext = _isNext == 1 ? true : false;
                }
                if (refreshItemCount > 0) {
                    //chatMessageAdapter.addMessages(MessageManager.getInstance().chatsArray, messageId);
//                    adapter.addMessagesByDiffUtils(MessageManager.getInstance().chatsArray, messageId);
                } else {
                    this.isPrev = _isPrev == 1 ? true : false;
                    this.isNext = _isNext == 1 ? true : false;
                }
            }
            //Log.w(LOG_TAG_UPDATE, "isPrev="+this.isPrev+"   isNext="+this.isNext);
            reloadFlag = false;
            if (!TextUtils.isEmpty(moveToMessageId)) {
                MessageManager.getInstance().postNotification(MessageManager.moveToMessage, info.channelId, moveToMessageId);
            }

            if (ServerInterfaceRetryManager.isGoLastChannel) {
                if (adapter != null) adapter.clear();
                ServerInterfaceRetryManager.isGoLastChannel = false;
                goLastChannel();
            } else {
                // 임시 저장된 채팅메시지를 설정
                if (info != null && PrefChatMessageManager.getInstance(mContext).getMessage(String.valueOf(info.channelId), "").length() > 0) {
                    inputText.setText(PrefChatMessageManager.getInstance(mContext).getMessage(String.valueOf(info.channelId), ""));
                    if (inputText.getText().length() > 0) {
                        inputText.setSelection(inputText.length());
//                        delayOpenKeyboard(etChatBox, 2000);
                    }
                } else {
                    inputText.setText("");
                }
            }

        }

    }
}