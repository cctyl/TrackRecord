package cn.tyl.gps_demo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenStatusReceiver extends BroadcastReceiver {
    private static final String SCREEN_ON = "android.intent.action.SCREEN_ON";
    private static final String SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    private static final String TAG = "ScreenStatusReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: 监听到屏幕亮暗");
//        if (SCREEN_ON.equals(intent.getAction())) {
//
//        } else if (SCREEN_OFF.equals(intent.getAction())) {
//        }
    }
}