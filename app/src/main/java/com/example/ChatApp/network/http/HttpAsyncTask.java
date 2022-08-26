package com.example.ChatApp.network.http;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ChatApp.network.Common;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpAsyncTask extends AsyncTask<Object, Void, HashMap<String, Object>> {
    private HttpAsyncTaskResultListener listener;
    private Context context;

    public static final String STATUS_CODE  = "STATUS_CODE";
    public static final String RESPONSE_MSG = "RESPONSE_MSG";
    public static final String STATUS = "status";
    public static final String DATA = "data";

    public static final String HTTP_GET   = "GET";
    public static final String HTTP_POST  = "POST";
    public static final String HTTP_MULTI = "MULTI_PART";

    public static final int CONNECTION_TIMEOUT = 30*1000;

    public HttpAsyncTask(Context context, HttpAsyncTaskResultListener listener) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected HashMap<String, Object> doInBackground(Object... params) {
        String method = params[0].toString();
        String url    = params[1].toString();
        HashMap headerMap = (HashMap<String, String>)params[2];
        HashMap<String, String> postParam  = (HashMap<String, String>)params[3];
        String query  = params[4].toString();

        return requestHttp(method, url, headerMap, postParam, query);
    }

    @Override
    protected void onPostExecute(HashMap<String, Object> resultMap) {
        Log.e(this.getClass().getSimpleName(), "[resultMap] ===> " + resultMap);
        this.listener.onResult(resultMap);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }



    // Http Utility Start //////////////////////////////////////////////////////////////////////////
    public HashMap<String, Object> requestHttp(String method, String url, HashMap<String, String> headerMap, HashMap<String, String> postParam, String query) {
        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        StringBuffer output = new StringBuffer();

        try {
            //영상 업로드 가능하도록 개발 - 2020.10.7
            int  uploadType = 0; //0:camera, 1:image, 2:video
            if(method.startsWith(HTTP_MULTI) && method.length() > HTTP_MULTI.length() ){
                uploadType = Integer.parseInt(method.charAt(HTTP_MULTI.length())+""); //0:camera, 1:image, 2:video
                method = method.substring(0, HTTP_MULTI.length());
            }

            if(HTTP_MULTI.equals(method)) {

                String[] _query = query.split("&");

/*                AndroidUploader uploader = new AndroidUploader(url);
                uploader.setFormFeilds(postParam);

                resultMap = uploader.uploadPicture(_query, uploadType);*/
            } else {

                if (HTTP_GET.equals(method)) {
                    url = url + "?" + query;
                }

                Log.e(this.getClass().getSimpleName(), "[URL] ===> " + url);
                Log.e(this.getClass().getSimpleName(), "[PARAMS] ===> " + query);

                HttpURLConnection httpURLConnection = null;
                if(url.startsWith("https")){
                    trustAllHosts();
                    /*SSLConnect ssl = new SSLConnect();
                    httpURLConnection = ssl.postHttps(url, 1000, 1000);*/
                    httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                }else{
                    httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                }

                if (httpURLConnection != null) {
                    httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                    httpURLConnection.setRequestMethod(method);

                    if(headerMap!=null){
                        Iterator iterator = headerMap.keySet().iterator();

                        while (iterator.hasNext()) {
                            String _key = iterator.next().toString();
                            Common.log("D", this.getClass().getSimpleName(), "[RequestProperty] ===> " + _key + ", " + headerMap.get(_key).toString());
                            httpURLConnection.setRequestProperty(_key, headerMap.get(_key).toString());
                        }
                    }

                    if (HTTP_POST.equals(method)) {
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setChunkedStreamingMode(0);

                        OutputStream out = new BufferedOutputStream(httpURLConnection.getOutputStream());
                        out.write(query.getBytes("UTF-8"));
                        out.flush();
                        out.close();
                    }

                    int responseCode = httpURLConnection.getResponseCode();

                    switch (responseCode) {
                        case HttpURLConnection.HTTP_OK:
                            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                            String line = null;

                            while ((line = reader.readLine()) != null) {
                                output.append(line);
                            }

                            reader.close();
                            httpURLConnection.disconnect();

                            break;
                    }

                    resultMap.put(STATUS_CODE, responseCode);
                    resultMap.put(RESPONSE_MSG, output.toString());
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        return resultMap;
    }

    public interface HttpAsyncTaskResultListener {
        public void onResult(HashMap<String, Object> resultMap);
    }

    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Http Utility End ////////////////////////////////////////////////////////////////////////////
}
