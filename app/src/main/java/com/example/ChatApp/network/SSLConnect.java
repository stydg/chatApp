package com.example.ChatApp.network;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLConnect {
    // always verify the host - dont check for certificate
    final  HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - don't check for any certificate
     */
    private void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
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

//    public static boolean trustAllHosts(HttpsURLConnection _httpURlConnection) {
//        boolean _rst = false;
//        SSLSocketFactory _sslSocketFactory = getCertSslSocketFactory(AppStarter.applicationContext);
//        if (_sslSocketFactory != null){
//            try {
//                _httpURlConnection.setSSLSocketFactory(_sslSocketFactory);
//                _rst = true;
//            } catch (Exception e) {
//            }
//        }
//
//        return true;
//    }
//
//    public static SSLSocketFactory getCertSslSocketFactory(Context context) {
//        try {
//            CertificateFactory cf = CertificateFactory.getInstance("X.509");
//            //InputStream caInput = context.getResources().openRawResource(R.raw.skhynix_com);
//            InputStream caInput = context.getResources().openRawResource(R.raw.ca_bundle);
//            Certificate ca = null;
//            try {
//                ca = cf.generateCertificate(caInput);
//                //System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
//                if (BuildConfig.DEBUG) Common.log("E", "ActivityEvent", "SSLConnect-getCertSslSocketFactory-ca=" + ((X509Certificate) ca).getSubjectDN());
//            } catch (CertificateException e) {
//                e.printStackTrace();
//            } finally {
//                caInput.close();
//            }
//
//            String keyStoreType = KeyStore.getDefaultType();
//            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//            keyStore.load(null, null);
//            if (ca == null) {
//                return null;
//            }
//            keyStore.setCertificateEntry("ca", ca);
//
//            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//            tmf.init(keyStore);
//
//            SSLContext sslContext= SSLContext.getInstance("TLS");
//            sslContext.init(null, tmf.getTrustManagers(), null);
//
//            return sslContext.getSocketFactory();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public HttpsURLConnection postHttps(String url, int connTimeout, int readTimeout) {
        trustAllHosts();

        HttpsURLConnection https = null;
        try {
            https = (HttpsURLConnection) new URL(url).openConnection();
            https.setHostnameVerifier(DO_NOT_VERIFY);
            https.setConnectTimeout(connTimeout);
            https.setReadTimeout(readTimeout);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return https;
    }
}