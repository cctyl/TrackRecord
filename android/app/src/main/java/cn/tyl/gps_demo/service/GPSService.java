package cn.tyl.gps_demo.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.elvishew.xlog.XLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.tyl.gps_demo.R;
import cn.tyl.gps_demo.activity.MainActivity;
import cn.tyl.gps_demo.receiver.ScreenStatusReceiver;

public class GPSService extends Service {
    private static final String TAG = "GPSService";

    public GPSService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registSreenStatusReceiver();
    }

    @Override
    public IBinder onBind(Intent intent) {

        notification(intent);


        return new InnerBinder();
    }

    private void notification(Intent intent) {
        XLog.d( "onBind: 绑定服务");
        String ID = "cn.tyl.gps_demo";	//这里的id里面输入自己的项目的包的路径
        String NAME = "LEFTBAR";
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder notification; //创建服务对象
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(channel);
            notification = new NotificationCompat.Builder(GPSService.this).setChannelId(ID);
        } else {
            notification = new NotificationCompat.Builder(GPSService.this);
        }
        notification.setContentTitle("定位")
                .setContentText("记录生活每一天")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .build();
        Notification notification1 = notification.build();
        startForeground(1,notification1);
    }


    /**
     * 本方法运行于主线程，不要在这里做耗时操作
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        XLog.d("onStartCommand 启动服务");
        notification(intent);
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }

    /**
     * 内部类，用这个InnerBinder 来调用服务内部的方法
     * 外界首先拿到InnerBinder对象，然后调用InnerBinder里面的方法，InnerBinder再调用当前服务的方法
     */
    public class InnerBinder extends Binder {
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mScreenStatusReceiver!=null){
            Log.d(TAG, "onDestroy: 取消广播监听");
            unregisterReceiver(mScreenStatusReceiver);
        }
        XLog.d( "onDestroy: 服务被销毁");
    }

    private ScreenStatusReceiver mScreenStatusReceiver;
    private void registSreenStatusReceiver() {
        Log.d(TAG, "registSreenStatusReceiver: 注册服务");
        mScreenStatusReceiver = new ScreenStatusReceiver();
        IntentFilter screenStatusIF = new IntentFilter();
        screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
        screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStatusReceiver, screenStatusIF);
    }


}