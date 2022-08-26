package com.example.ChatApp.network.http;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import java.util.HashMap;

/*
StringBuffer _query = new StringBuffer();
    _query.append("param1=").append(param1).append("&");
    _query.append("param2=").append(param2).append("&");
    _query.append("file=").append(mFilePath);

SendServer sendServerStart = new SendServer(this, httpAsyncTaskResultListener);
sendServerStart.setExcuteBaseParam(HttpAsyncTask.HTTP_POST, "http://urls.com/urls");
sendServerStart.setExcuteQueryParam(_query.toString());
sendServerStart.SendData();

HttpAsyncTask.HttpAsyncTaskResultListener httpAsyncTaskResultListener = new HttpAsyncTask.HttpAsyncTaskResultListener() {
    @Override
    public void onResult(HashMap<String, Object> resultMap) {
        int status = -1;
        String response = null;

        try {
            status   = ((Integer) resultMap.get(HttpAsyncTask.STATUS_CODE));    // API 통신 결과 코드
            response = ((String) resultMap.get(HttpAsyncTask.RESPONSE_MSG));    // API 통신 결과 메세지

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        switch(status) {
            case 200:
                break;
        }
    }
};
*/
public class HttpClient {

    private Context mContext;
    private HttpAsyncTask.HttpAsyncTaskResultListener mResultListener;
    private String mMethod;
    private String mURL;
    private HashMap<String, String> mHeaderMap;
    private String mQuery;
    private HashMap<String, String> mMap;

    public HttpClient(Context context, HttpAsyncTask.HttpAsyncTaskResultListener listener){
        mContext = context;
        mResultListener = listener;
    }

    public void setParam(String method, String url){
        mMethod = method;
        mURL = url;
    }

    public void setQueryParam(String query){
        mQuery = query;
    }

    public void setHeader(HashMap headerMap){
        mHeaderMap = headerMap;
    }

    public void setMapParam(HashMap<String, String> map){
        mMap = map;
    }

    public void excute(){
        if (Build.VERSION.SDK_INT >= 11) {
            new HttpAsyncTask(mContext, mResultListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mMethod, mURL, mHeaderMap, mMap, mQuery);
        }else{
            new HttpAsyncTask(mContext, mResultListener).execute(mMethod, mURL, mHeaderMap, mMap, mQuery);
        }
    }
}
