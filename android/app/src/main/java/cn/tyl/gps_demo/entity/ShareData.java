package cn.tyl.gps_demo.entity;

import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.elvishew.xlog.XLog;

public class ShareData {

    public static AMapLocationClient mLocationClient = null;


    public static AMapLocationClient initClient(Context context) throws Exception {
        if (mLocationClient != null) {
            XLog.d("mLocationClient 已创建，请勿重复初始化");
        } else {
            mLocationClient = new AMapLocationClient(context);
        }
        return mLocationClient;
    }

    public static AMapLocationClient getClient(){
        return mLocationClient;
    }


}
