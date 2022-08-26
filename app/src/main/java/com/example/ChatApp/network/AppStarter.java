package com.example.ChatApp.network;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;

public class AppStarter extends Application {
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;

    private static boolean isBackground = true;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationContext = getApplicationContext();
        applicationHandler = new Handler(applicationContext.getMainLooper());

        startPushService();
        applicationContext.startService(new Intent(applicationContext, ServiceConnection.class));

        listenForForeground();
        listenForScreenTurningOff();


    }

    public static void startPushService() {
//        SharedPreferences preferences = applicationContext.getSharedPreferences("Notifications", MODE_PRIVATE);
//
//        if (preferences.getBoolean("pushService", true)) {
//            applicationContext.startService(new Intent(applicationContext, NotificationsService.class));
//        } else {
//            stopPushService();
//        }
    }

    public static void stopPushService() {
//        applicationContext.stopService(new Intent(applicationContext, NotificationsService.class));
//
//        PendingIntent pintent = PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, NotificationsService.class), 0);
//        AlarmManager alarm = (AlarmManager)applicationContext.getSystemService(Context.ALARM_SERVICE);
//        alarm.cancel(pintent);
    }


    private void initPlayServices() {
//        AndroidUtilities.runOnUIThread(new Runnable() {
//            @Override
//            public void run() {
//                if (checkPlayServices()) {
//                    if (UserConfig.pushString != null && UserConfig.pushString.length() != 0) {
//                        FileLog.d("tmessages", "GCM regId = " + UserConfig.pushString);
//                    } else {
//                        FileLog.d("tmessages", "GCM Registration not found.");
//                    }
//
//                    //if (UserConfig.pushString == null || UserConfig.pushString.length() == 0) {
//                    Intent intent = new Intent(applicationContext, GcmRegistrationIntentService.class);
//                    startService(intent);
//                    //} else {
//                    //    FileLog.d("tmessages", "GCM regId = " + UserConfig.pushString);
//                    //}
//                } else {
//                    FileLog.d("tmessages", "No valid Google Play Services APK found.");
//                }
//            }
//        }, 1000);
    }




    private Activity mCurrentActivity = null;
    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity activity){
        this.mCurrentActivity = activity;
    }

    ///////////////////////////////////////////////////////////foreground check
    private void listenForForeground() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (isBackground) {
                    isBackground = false;
                    notifyForeground();
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    private void listenForScreenTurningOff() {
        IntentFilter screenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isBackground = true;
                notifyBackground();
            }
        }, screenStateFilter);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            isBackground = true;
            notifyBackground();
        }

    }

    private void notifyForeground() {
        // This is where you can notify listeners, handle session tracking, etc
    }

    private void notifyBackground() {
        // This is where you can notify listeners, handle session tracking, etc
    }

    public static boolean isBackground() {
        return isBackground;
    }
}
