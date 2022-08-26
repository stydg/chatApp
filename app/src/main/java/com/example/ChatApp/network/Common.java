package com.example.ChatApp.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ChatApp.BuildConfig;
import com.example.ChatApp.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Common {
    public static int ui_index = 0;

    public static final String LOG_TAG_FCM = "[FCM]";
    public static final String LOG_TAG_UPDATE = "[UPDATE]";
    public static final String LOG_TAG_ITEM = "[ITEM]";
    public static final String LOG_TAG_NETWORK = "[NETWORK]";
    public static final String LOG_TAG_EVENT = "[EVENT]";
    static public String PREF_BAIDU_CHANNELID = "BAIDU_CHANNELID";

    public static final int COUNT_LIST_AT_ONCE = 30; // 서동호 책임님 요청으로 변경 (2018/04/03)
    static public String KEY_PREFERENCE = "com.skhynix.client";

    static public String PREF_SESSION_KEY = "SESSION_KEY";
    static public String PREF_LANGUAGE = "LANGUAGE";
    static public String PREF_LOGIN_USER_ID = "LOGIN_USER_ID";
    public static final String WSHOST = "http://110.45.156.137";
    public static final String WSPORT = "10209";
    public static String WEB_URL = WSHOST+":" + WSPORT + "/loginPC";
    public static String MOBILE_URL = WSHOST+":" + WSPORT + "/loginMOBILE";
    public static String SPECIAL_STORE_URL = WSHOST+":" + WSPORT + "/loginSPECIAL";
    public static String STORE_URL = WSHOST+":" + WSPORT + "/loginSTORE";
    public static String GMP_URL = WSHOST+":" + WSPORT + "/loginGMP";
    public static final boolean DEBUG = Boolean.parseBoolean("true");
    public static final String FLAVOR = "devServer";

    static public int currentUIMode;
    static public int UI_MODE_CHANNEL = ui_index++;
    static public String PREF_FCM_CHANNEL_ID = "FCM_CHANNEL_ID";
    static public String PREF_FCM_MESSAGE_ID = "FCM_MESSAGE_ID";
    static public String PREF_CALL_DM_USER_ID = "CALL_DM_USER_ID";
    static public String PREF_CALL_DM_USER_NAME = "CALL_DM_USER_NAME";

    // 알림 소리 상태 저장
    static public String PREF_NOTIFICATION_SOUND = "PREF_NOTIFICATION_SOUND";

    static public String PREF_LAST_CHANNEL_ID = "LAST_CHANNEL_ID";
    static public String PREF_LAST_CHANNEL_NAME = "LAST_CHANNEL_NAME";
    static public String PREF_LAST_CHANNEL_MODE = "LAST_CHANNEL_MODE";
    static public int UI_MODE_MESSAGE = ui_index++;
    static public String PREF_IS_ACTIVE = "IS_ACTIVE";

    public static void toast(String msg) {
        Toast.makeText(AppStarter.applicationContext, msg, Toast.LENGTH_SHORT).show();
    }
    public static void toast(Context c, String msg) {
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }
    // 날짜비교
    public static int compareDate(String date1, String date2){
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd");
        Date day1 = null;
        Date day2 = null;

        try {
            day1 = sdf1.parse(date1);
        } catch (Exception e1) {
            try {
                day1 = sdf2.parse(date1);
            } catch (Exception e2) {
                try {
                    day1 = sdf3.parse(date1);
                } catch (Exception e3) {
                    try {
                        day1 = sdf4.parse(date1);
                    } catch (Exception e4) {
                        e4.printStackTrace();
                        return -1;
                    }
                }
            }
        }
        try {
            day2 = sdf1.parse(date2);
        } catch (Exception e1) {
            try {
                day2 = sdf2.parse(date2);
            } catch (Exception e2) {
                try {
                    day2 = sdf3.parse(date2);
                } catch (Exception e3) {
                    try {
                        day2 = sdf4.parse(date2);
                    } catch (Exception e4) {
                        e4.printStackTrace();
                        return 1;
                    }
                }
            }
        }

        return compareDay(day1.getTime(), day2.getTime());
    }
    public static int compareDay(long time1, long time2) {
        Date day1 = new Date(time1);
        Date day2 = new Date(time2);

        return day1.compareTo( day2 );
    }
    public static int compareTime(String time1, String time2){
        SimpleDateFormat sdf1 = new SimpleDateFormat("kk:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("kk:mm");
        Date day1 = null;
        Date day2 = null;

        try {
            day1 = sdf1.parse(time1);
        } catch (Exception e3) {
            try {
                day1 = sdf2.parse(time1);
            } catch (Exception e4) {
                e4.printStackTrace();
            }
        }
        try {
            day2 = sdf1.parse(time2);
        } catch (Exception e3) {
            try {
                day2 = sdf2.parse(time2);
            } catch (Exception e4) {
                e4.printStackTrace();
            }
        }

        return compareDay(day1.getTime(), day2.getTime());
    }

    public static boolean isDateString(String input){
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd");
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf4 = new SimpleDateFormat("MMdd");
        Date day = null;

        try {
            day = sdf1.parse(input);
        } catch (ParseException e1) {
            try {
                day = sdf2.parse(input);
            } catch (ParseException e2) {
                try {
                    day = sdf3.parse(input);
                } catch (ParseException e3) {
                    try {
                        day = sdf4.parse(input);
                    } catch (ParseException e4) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }


    public static void openKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
            activity.getCurrentFocus().clearFocus();
        } catch (Exception e) {
        }
    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            activity.getCurrentFocus().clearFocus();
        } catch (Exception e) {
        }
    }

    public static void setLocale(Context context, Locale locale) {
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static void screenCaptureEnable(Activity activity, boolean enable){
        if(enable){
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }else{
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    public static String makePhoneNumber(String phoneNumber) {
        String regEx = "(\\d{3})(\\d{3,4})(\\d{4})";

        if(!Pattern.matches(regEx, phoneNumber)) return null;

        return phoneNumber.replaceAll(regEx, "$1-$2-$3");

    }

    public static String replaceTag(String str){
        //Log.d("replaceTag", str);
        str = Html.escapeHtml(str);
        //Log.d("replaceTag", str);

        Matcher mat;
        //.(해당자리 한글자) .*(모든글자)
        //^시작에 +한번이상 a*(0번이상반복) a+(한번이상반복) a?(유무) a{5}(5회반복) a{3,}(3회이상반복) a{3,5}(3회이상5회이하반복) [abc](abc중하나) [^abc](abc제외) [a-zA-Z](문자) [0-9](숫자) 끝에$
        //\d숫자 \D숫자아님 \s공백 \S공백제외 \w대소문자밑줄숫자 \W제외 \p{Lower}소문자 \p{Upper}대문자 \p{Alpha}영문자 \p{Digit}숫자 \p{Alnum}영숫자
        // http://devfalledinmac.tistory.com/13
        Pattern tag;


          //메시지에 "\n"이 포함된 경우 줄바꿈처리하는 오류 수정 -2020/5.11
//        str = str.replace("\\n","<br />").replace("&#10;","<br />");//엔터

//          str = str.replace(" ","&nbsp;").replace("&#10;","<br />");//엔터
           str = str.replace(" ","&nbsp;");

//        tag = Pattern.compile("([\\p{Alnum}]+)://(?!"+BuildConfig.DOMAIN+")([a-zA-Z0-9.\\-&/%=?:@#$(),.+;~_\\[\\]\\{\\}]+)", Pattern.CASE_INSENSITIVE);//웹주소
//        mat = tag.matcher(str);
//        str = mat.replaceAll("<font color='#FF34A9DA'>$0</font>");

//        tag = Pattern.compile("\\*\\*(.*?)\\*\\*", Pattern.CASE_INSENSITIVE);//*~~~~~*
//        mat = tag.matcher(str);
//        str = mat.replaceAll("<b>$1</b>");

        str = patternMatch( str, "\\*\\*(.*?)\\*\\*", Pattern.CASE_INSENSITIVE, "**", "", "<b>","</b>");

//        tag = Pattern.compile("__(.*?)__", Pattern.CASE_INSENSITIVE);//__~~~~~__
//        mat = tag.matcher(str);
//        str = mat.replaceAll("<i>$1</i>");

        str = patternMatch( str, "__(.*?)__", Pattern.CASE_INSENSITIVE, "__", "", "<i>","</i>");

        tag = Pattern.compile("\\{\\{@(CHANNEL|ALL)::([0-9]+)\\}\\}", Pattern.CASE_INSENSITIVE);//{{@CHANNEL::(channelID)}}
        mat = tag.matcher(str);
        str = mat.replaceAll("<font color='#34A9DA'>@$1</font>");

        tag = Pattern.compile("\\{\\{(@.*?)::([xX]?[0-9]+)\\}\\}", Pattern.CASE_INSENSITIVE);//{{(@이름)::(userID)}}
        mat = tag.matcher(str);
        str = mat.replaceAll("<a href='com.skhynix.client://?userid=$2'><font color='#34A9DA'>$1</font></a>");

        /* 강조 표기 1단계 삭제 버그조치- 2020.9.22
        1단계 삭제 및 나머지 단계 하향 조치
        5단계 추가(기존 4단계) */
        str = patternMatch( str, "\\[\\[\\[\\[\\[&nbsp;(.*?)&nbsp;\\]\\]\\]\\]\\]", Pattern.CASE_INSENSITIVE, "&nbsp;", "&#10;", "<font color='#FF0000'><big><big><big><b><u>","</u></b></big></big></big></font>");

//        tag = Pattern.compile("\\[\\[\\[\\[ (.*?) \\]\\]\\]\\]", Pattern.CASE_INSENSITIVE);
//        mat = tag.matcher(str);
//        str = mat.replaceAll("<font color='#FF0000'><big><big><big><b><u>$1</u></b></big></big></big></font>");
        str = patternMatch( str, "\\[\\[\\[\\[&nbsp;(.*?)&nbsp;\\]\\]\\]\\]", Pattern.CASE_INSENSITIVE, "&nbsp;", "&#10;", "<font color='#FF0000'><big><big><b><u>","</u></b></big></big></font>");

//        tag = Pattern.compile("\\[\\[\\[ (.*?) \\]\\]\\]", Pattern.CASE_INSENSITIVE);
//        mat = tag.matcher(str);
//        str = mat.replaceAll("<font color='#FF0000'><big><big><b><u>$1</u></b></big></big></font>");
        str = patternMatch( str, "\\[\\[\\[&nbsp;(.*?)&nbsp;\\]\\]\\]", Pattern.CASE_INSENSITIVE, "&nbsp;", "&#10;", "<font color='#FF0000'><big><b><u>","</u></b></big></font>");

//        tag = Pattern.compile("\\[\\[ (.*?) \\]\\]", Pattern.CASE_INSENSITIVE);
//        mat = tag.matcher(str);
//        str = mat.replaceAll("<font color='#FF0000'><big><b><u>$1</u></b></big></font>");
        str = patternMatch( str, "\\[\\[&nbsp;(.*?)&nbsp;\\]\\]", Pattern.CASE_INSENSITIVE, "&nbsp;", "&#10;", "<font color='#FF0000'><big><b>","</b></big></font>");

        //강조표시 Underline 없는 유형 추가 - 2020.8.5
//        tag = Pattern.compile("\\[ (.*?) \\]", Pattern.CASE_INSENSITIVE);
//        mat = tag.matcher(str);
//        str = mat.replaceAll("<font color='#FF0000'><big><b>$1</b></big></font>");
//        str = patternMatch( str, "\\[&nbsp;(.*?)&nbsp;\\]", Pattern.CASE_INSENSITIVE, "&nbsp;",  "&#10;", "<font color='#FF0000'><big><b>","</b></big></font>");

         /* 강조 표기 1단계 삭제 버그조치- 2020.9.22
        1단계 삭제 및 나머지 단계 하향 조치
        5단계 추가(기존 4단계) */
//        str = patternMatch( str, "\\[&nbsp;(.*?)&nbsp;\\]", Pattern.CASE_INSENSITIVE, "&nbsp;",  "&#10;", "<font color='#FF0000'><big><b>","</b></big></font>");
         str = patternMatch( str, "\\(\\(\\(\\(\\(&nbsp;(.*?)&nbsp;\\)\\)\\)\\)\\)", Pattern.CASE_INSENSITIVE, "&nbsp;", "&#10;", "<font color='#0000FF'><big><big><big><b><u>","</u></b></big></big></big></font>");

//        tag = Pattern.compile("\\(\\(\\(\\( (.*?) \\)\\)\\)\\)", Pattern.CASE_INSENSITIVE);
//        mat = tag.matcher(str);
//        str = mat.replaceAll("<font color='#0000FF'><big><big><big><b><u>$1</u></b></big></big></big></font>");
        str = patternMatch( str, "\\(\\(\\(\\(&nbsp;(.*?)&nbsp;\\)\\)\\)\\)", Pattern.CASE_INSENSITIVE, "&nbsp;", "&#10;", "<font color='#0000FF'><big><big><b><u>","</u></b></big></big></font>");

//        tag = Pattern.compile("\\(\\(\\( (.*?) \\)\\)\\)", Pattern.CASE_INSENSITIVE);
//        mat = tag.matcher(str);
//        str = mat.replaceAll("<font color='#0000FF'><big><big><b><u>$1</u></b></big></big></font>");
        str = patternMatch( str, "\\(\\(\\(&nbsp;(.*?)&nbsp;\\)\\)\\)", Pattern.CASE_INSENSITIVE, "&nbsp;", "&#10;", "<font color='#0000FF'><big><b><u>","</u></b></big></font>");

//        tag = Pattern.compile("\\(\\( (.*?) \\)\\)", Pattern.CASE_INSENSITIVE);
//        mat = tag.matcher(str);
//        str = mat.replaceAll("<font color='#0000FF'><big><b><u>$1</u></b></big></font>");
        str = patternMatch( str, "\\(\\(&nbsp;(.*?)&nbsp;\\)\\)", Pattern.CASE_INSENSITIVE, "&nbsp;", "&#10;", "<font color='#0000FF'><big><b>","</b></big></font>");

        //강조표시 Underline 없는 유형 추가 - 2020.8.5
//        tag = Pattern.compile("\\( (.*?) \\)", Pattern.CASE_INSENSITIVE);
//        mat = tag.matcher(str);
//        str = mat.replaceAll("<font color='#0000FF'><big><b>$1</b></big></font>");
//        str = patternMatch( str, "\\(&nbsp;(.*?)&nbsp;\\)", Pattern.CASE_INSENSITIVE, "&nbsp;", "&#10;", "<font color='#0000FF'><big><b>","</b></big></font>");

           /* 강조 표기 1단계 삭제 버그조치- 2020.9.22
        1단계 삭제 및 나머지 단계 하향 조치
        5단계 추가(기존 4단계) */
//        str = patternMatch( str, "\\(&nbsp;(.*?)&nbsp;\\)", Pattern.CASE_INSENSITIVE, "&nbsp;", "&#10;", "<font color='#0000FF'><big><b>","</b></big></font>");

        str = str.replace("&#10;","<br />");//엔터

//     전화번호 정규식
//        tag = Pattern.compile( "^\\d{2,3}-\\d{3,4}-\\d{4}$");
//        mat = tag.matcher(str);
//        str = mat.replaceAll("<a href='tel:'><font color='#34A9DA'>$0</font></a>");


        //str = String.valueOf(Common.fromHtml(str));
        //Log.d("replaceTag", str);
        return str;
    }
    //강조 표기시 개행 있을경우 미동작 처리- 2020.9.11
    private static String patternMatch(String str, String regex, int flags, String lastStr, String containStr, String startHtmlStr, String lastHtmlStr){
        Pattern tag;
        Matcher mat;
        String matchingStr ="";
        tag = Pattern.compile(regex, flags);
        mat = tag.matcher(str);
        while(mat.find()) {
            if(!TextUtils.isEmpty(lastStr))
                 matchingStr = mat.group().substring(mat.group().indexOf(lastStr)+lastStr.length(),mat.group().lastIndexOf(lastStr));
            else
                 matchingStr = mat.group();
              //강조 표기시 개행 있을경우 미동작 처리
            if(TextUtils.isEmpty(containStr) || !mat.group().contains(containStr))
                str = str.replace(mat.group(), startHtmlStr+matchingStr+lastHtmlStr);

        }
        return str;
    }
    public static boolean isMatching(String regularExpression, String input){
        Pattern pattern =  Pattern.compile(regularExpression, Pattern.CASE_INSENSITIVE);
        Matcher mat = pattern.matcher(input);
        return mat.matches();
    }
    //회의알림봇에 외부(휴대폰) 클릭시 전화걸기 기능 추가- 2021.1.21
    public static void textviewCallLink(final View view, final TextView textview) {
        String str = textview.getText().toString();
        Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher matcher, String s) {
                return "";
            }
        };
        Pattern pattern = Pattern.compile("\\d{2,3}-\\d{3,4}-\\d{4}");
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()) {
            String num = matcher.group().replace("-","");
            Linkify.addLinks(textview, pattern, "tel:" + num , null, filter);
       }
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: a)
            sb.append(String.format("%02x ", b&0xff));
        return sb.toString();
    }

    //<uses-permission android:name="android.permission.DUMP"/>
    public static String getLogcatLog() {
        StringBuilder log = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec("logcat -d -v time");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
                log.append("\n");
            }
        } catch (IOException e) {

        }
        return log.toString();
    }

    public static void clearLogcatLog() {
        try {
            @SuppressWarnings("unused")
            Process process = Runtime.getRuntime().exec("logcat -c");
        } catch (IOException e) {

        }
    }

    // Date
    public static String getToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(Calendar.getInstance().getTime());
        return today;
    }
    //일정검색 개선 - 2020.10.6
    //날짜 비교
    public static int getDateCompare(String day1, String day2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date day_1 = null;
        Date day_2= null;
        try {
            day_1 = sdf.parse(day1);
            day_2 = sdf.parse(day2);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        int compare = day_1.compareTo(day_2);

        return compare;
    }

    public static String getLastMeetingBookableDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(Common.getLastMeetingBookableTimeInMillis());
        return today;
    }

    public static long getLastMeetingBookableTimeInMillis() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(Calendar.getInstance().getTime());
        cal.add(Calendar.DAY_OF_MONTH, 21);
        return cal.getTimeInMillis();
    }

    public static String getAddTime(int afterHour, int afterMinute, int afterSecond) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, afterHour);
        c.add(Calendar.MINUTE, afterMinute);
        c.add(Calendar.SECOND, afterSecond);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        return hour + ":" + (minute < 10 ? "0" : "") + minute + ":" + (second < 10 ? "0" : "") + second;
    }

    public static String getFromTheDay(String theDay, int numOfDay) {
        String[] array = theDay.split("-");
        String yearOfTheDay = array[0];
        String monthOfTheDay = array[1];
        String dayOfTheDay = array[2];
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(yearOfTheDay), Integer.parseInt(monthOfTheDay)-1, Integer.parseInt(dayOfTheDay));
        c.add(Calendar.DAY_OF_MONTH, numOfDay);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        return year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day;
    }

    public static String getFromTheHour(String theDay, int numOfHour) {
        String[] array = theDay.split("-");
        String yearOfTheDay = array[0];
        String monthOfTheDay = array[1];
        String dayOfTheDay = array[2];
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(yearOfTheDay), Integer.parseInt(monthOfTheDay)-1, Integer.parseInt(dayOfTheDay));
        c.add(Calendar.HOUR_OF_DAY, numOfHour);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return sdf.format(c);
    }

    public static String getShortDay(String theDay) {
        if(theDay.equals(getToday())) {
            return "TODAY";
        } else {
            return theDay;
        }
    }

    public static boolean isBelongToDay(String theDay, String startDay, String endDay) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        try {
            Date theDate = format1.parse(theDay);
            Date startDate = null;
            Date endDate = null;
            try {
                startDate = format1.parse(startDay);
                endDate = format1.parse(endDay);
            } catch (ParseException e) {
                //스케쥴 타입 변경됨
                startDate = format2.parse(startDay);
                endDate = format2.parse(endDay);
            }

            if(theDate.compareTo(startDate) <= 0) return false;
            if(theDate.compareTo(endDate) >= 0) return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static final int StartDayOfTheDay = 1;
    public static final int BelongToTheDay = 2;
    public static final int EndDayOfTheDay = 3;
    public static final int BothDayOfTheDay = 4;
    public static int getDayTypeOfTheDay(String theDay, String startDay, String endDay) {
        String startDate = startDay.split(" ")[0].replace("/", "-");
        String endDate = endDay.split(" ")[0].replace("/", "-");

        if(theDay.equals(startDate)) {
            if(theDay.equals(endDate)) return BothDayOfTheDay;
            return StartDayOfTheDay;
        } else if(theDay.equals(endDate)) {
            return EndDayOfTheDay;
        }

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy/MM/dd hh:mm");

        try {
            Date BaseStartDate = format1.parse(theDay + " 00:00");
            Date BaseEndDate = format1.parse(getFromTheDay(theDay, 1) + " 00:00");
            Date sDate = format2.parse(startDay);
            Date eDate = format2.parse(endDay);

            if(sDate.compareTo(BaseStartDate) < 0) {
                if(eDate.compareTo(BaseStartDate) >= 0) {
                    return BelongToTheDay;
                }
            }
            if(eDate.compareTo(BaseEndDate) >= 0) {
                if(sDate.compareTo(BaseEndDate) < 0) {
                    return BelongToTheDay;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean isBeforeTime(String t1, String t2) {
        //Log.d("h", "kk");
        return true;
    }

    public static long convertUtcToLocalTime(long timeMillis){
        TimeZone tz = TimeZone.getDefault();
        int offset = tz.getOffset(timeMillis);
        long longLocalTime = timeMillis + offset;
        return longLocalTime;
    }

    public static long convertLocalTimeToUtc(long timeMillis){
        TimeZone tz = TimeZone.getDefault();
        int offset = tz.getOffset(timeMillis);
        long longUTC = timeMillis - offset;
        return longUTC;
    }

    public static boolean isWorkingTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");
        try {
            Date startWorkTime = sdf.parse("08:00:00");
            Date endWorkTime = sdf.parse("18:00:00");

            GregorianCalendar cal = new GregorianCalendar();
            Date currentTime = sdf.parse(sdf.format(cal.getTime()));
            int day_of_week = cal.get ( Calendar.DAY_OF_WEEK );
            if(day_of_week>=2 && day_of_week<=6){
                if(currentTime.after(startWorkTime) && currentTime.before(endWorkTime)){
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*https://blog.asamaru.net/2015/12/15/android-app-finish/*/
    public static void killApp(Activity activity, boolean killSafely) {
        if (killSafely) {
            if(activity!=null) activity.finishAffinity();
        /*
         * Notify the system to finalize and collect all objects of the app
         * on exit so that the virtual machine running the app can be killed
         * by the system without causing issues. NOTE: If this is set to
         * true then the virtual machine will not be killed until all of its
         * threads have closed.
         */
            System.runFinalizersOnExit(true);

        /*
         * Force the system to close the app down completely instead of
         * retaining it in the background. The virtual machine that runs the
         * app will be killed. The app will be completely created as a new
         * app in a new virtual machine running in a new process if the user
         * starts the app again.
         */
            System.exit(0);
        } else {
        /*
         * Alternatively the process that runs the virtual machine could be
         * abruptly killed. This is the quickest way to remove the app from
         * the device but it could cause problems since resources will not
         * be finalized first. For example, all threads running under the
         * process will be abruptly killed when the process is abruptly
         * killed. If one of those threads was making multiple related
         * changes to the database, then it may have committed some of those
         * changes but not all of those changes when it was abruptly killed.
         */
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public static void updateIconBadgeCount(Context context, int count) {
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        // Component를 정의
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", getLauncherClassName(context));
        // 카운트를 넣어준다.
        intent.putExtra("badge_count", count);
        // Version이 3.1이상일 경우에는 Flags를 설정하여 준다.
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        }
        // send
        context.sendBroadcast(intent);
    }

    private static String getLauncherClassName(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(context.getPackageName());
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
        if(resolveInfoList != null && resolveInfoList.size() > 0) {
            return resolveInfoList.get(0).activityInfo.name;
        }
        return null;
    }

    public static String getAppVersion(){
        String appVer;
        try {
            PackageManager pm = AppStarter.applicationContext.getPackageManager();
            PackageInfo pi;
            pi = pm.getPackageInfo(AppStarter.applicationContext.getPackageName(), 0);
            appVer = pi.versionName;
        } catch (Exception e) {
            appVer = "1.0.0";
        }
        return appVer;
    }

    public static String TrimString(String astr){
        if (TextUtils.isEmpty(astr) || TextUtils.equals(astr.toUpperCase(), "NULL") || TextUtils.equals(astr.toUpperCase(), "NIL")) {
            return "";
        } else {
            return astr.trim();
        }
    }

    public static int StringToIntDef(String aStringNo, int defaultNo){
        try {
            return Integer.valueOf(aStringNo);
        } catch (Exception e) {
            return defaultNo;
        }
    }

    public static float StringTofloatDef(String aStringNo, float defaultNo){
        try {
            return Float.valueOf(aStringNo);
        } catch (Exception e) {
            return defaultNo;
        }
    }

    public static long StringToLongDef(String aStringNo, long defaultNo){
        try {
            return Long.valueOf(aStringNo);
        } catch (Exception e) {
            return defaultNo;
        }
    }

    // 일정 시간 경과 여부 체크
    public static boolean checkToTime(long T_Time, int T_Sleep) {

        long S_TIME = System.currentTimeMillis();
        long D_Time = 0;

        if(T_Time < S_TIME) {
            D_Time = T_Time + T_Sleep;
            if(D_Time<=S_TIME) {
                return true;
            }
        } else {
            if((T_Time + T_Sleep) <= S_TIME) {
                return true;
            }
        }

        return false;
    }

    // 권한 여부 체크시 사용하는 해시 함수
    public static String getSHA512(String input){
        String toReturn = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(input.getBytes("utf8"));
            toReturn = String.format("%0128x", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    // 앱 서명값을 조회
    public static String getCertSignature(Context _context){
        PackageManager _pm = _context.getPackageManager();
        String _packageName = _context.getPackageName();
        String _cert = "";

        try {
            PackageInfo _packageInfo = _pm.getPackageInfo(_packageName, PackageManager.GET_SIGNATURES);
            android.content.pm.Signature _certSignature = _packageInfo.signatures[0];
            MessageDigest _msgDigest = MessageDigest.getInstance("SHA1");
            _msgDigest.update(_certSignature.toByteArray());
            _cert = Base64.encodeToString(_msgDigest.digest(), Base64.DEFAULT);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return _cert;
    }

    //txt 파일 열람시 한글깨짐 방지 보완 - 2020.3.12
    public static String getChangeString(String strings, byte[] txtBytes){
        String txt = strings;
        String[] charset = {
                "euc-kr",
                "utf-8",
                "UTF-16BE",
                "UTF-16LE",
                "UTF-32BE",
                "UTF-32LE",
                "MS949",
                "latin1",
                "x-windows-949",
                "KSC5601",
                "cp949",
                "8859_1",
                "Cp1252",
                "Cp850",
                "ISO-2022-KR"
        };

        for (int i = 0; i < charset.length; i++) {
            try {
                txt = new String(txtBytes, charset[i]);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (!txt.contains("�") && !txt.contains("￾")) break;
        }
        return txt;
    }
    //Html.fromHtml deprecation 처리 추가 - 2020.8.12
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    //엑셀 파일은 뷰어 호출로 처리 - 2021.3.17(get MacAddress)
    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }
    public static  void showJasonData(String tag, String json) {
        String temp_json = json;
        int log_index = 1;
        try {
            while (temp_json.length() > 0) {
                if (temp_json.length() > 4000) {
                    Log.d(tag, "json - " + log_index + " : "
                            + temp_json.substring(0, 4000));
                    temp_json = temp_json.substring(4000);
                    log_index++;
                } else {
                    Log.d(tag, "json - " + log_index + " :" + temp_json);
                    break;
                }
            }
        } catch (Exception e) {
        }
    }
    public static void logLineBreak(String tag, String str) {
        if (str.length() > 3000) {    // 텍스트가 3000자 이상이 넘어가면 줄
            Log.i(tag, str.substring(0, 3000));
            logLineBreak(tag, str.substring(3000));
        } else {
            Log.i(tag, str);
        }
    }
    public static void log(String level, String tag, String msg){
        if(!BuildConfig.DEBUG && !MainActivity.isDebugEnable){
            return;
        }
        StackTraceElement[] a = new Throwable().getStackTrace();
        String whereIs = "at "+a[1].getClassName() + "("+a[1].getFileName()+":"+a[1].getLineNumber()+")";
        /*StringBuffer sb = new StringBuffer();
        for (int i = 1; i < a.length; i++) {
            sb.append("\tat ").append(a[1].getClassName()).append("(").append(a[i].getFileName()).append(":").append(a[i].getLineNumber()).append(")").append("\n");
        }
        whereIs = sb.toString();*/
        while( msg.length() > 0 ) {
            if( msg.length() > 2000 ) {
                switch (level) {
                    case "I":
                        Log.i( tag, msg.substring( 0, 2000 ));
                        break;
                    case "D":
                        Log.d( tag, msg.substring( 0, 2000 ));
                        break;
                    case "W":
                        Log.w( tag, msg.substring( 0, 2000 ));
                        break;
                    case "E":
                        Log.e( tag, msg.substring( 0, 2000 ));
                        break;
                    default:
                        Log.v( tag, msg.substring( 0, 2000 ));
                        break;
                }
                msg = msg.substring( 2000 );
            } else {
                switch (level) {
                    case "I":
                        Log.i( tag, msg + "\n" + whereIs );
                        break;
                    case "D":
                        Log.d( tag, msg + "\n" + whereIs );
                        break;
                    case "W":
                        Log.w( tag, msg + "\n" + whereIs );
                        break;
                    case "E":
                        Log.e( tag, msg + "\n" + whereIs );
                        break;
                    default:
                        Log.v( tag, msg + "\n" + whereIs );
                        break;
                }
                break;
            }
        }
    }

}

