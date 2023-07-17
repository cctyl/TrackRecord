package cn.tyl.gps_demo.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.elvishew.xlog.XLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.tyl.gps_demo.entity.GPSData;
import cn.tyl.gps_demo.util.HttpUtil;

public class MyWorker extends Worker {
    private Set<GPSData> gpsDataSet = new HashSet<>(10);//存储最近10条定位数据

    public static AMapLocationClient mLocationClient = null;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        XLog.d("MyWorker构造函数调用一次");
        initGaoDeGPS(context);
    }

    private void initGaoDeGPS(Context context) {
        XLog.d("myworker initGaoDeGPS");
        //高德定位sdk
        AMapLocationClient.updatePrivacyShow(context, true, true);
        AMapLocationClient.updatePrivacyAgree(context, true);


        try {
            mLocationClient = new AMapLocationClient(context);
            //定位配置
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            mLocationOption.setOnceLocation(true);

            mLocationClient.setLocationOption(mLocationOption);


            if (mLocationClient == null) {
                XLog.e("mLocationClient 为null");
            }

            mLocationClient.setLocationListener(aMapLocation -> {
                XLog.d("监听到地址："+aMapLocation);
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {

                        XLog.d("成功获得定位，结果为:" + aMapLocation.getLongitude() + "-" + aMapLocation.getLatitude());
                        XLog.d("关闭gps");
                        mLocationClient.stopLocation();
                        //数据加入集合中
                        gpsDataSet.add(new GPSData(aMapLocation.getLongitude(), aMapLocation.getLatitude()));


                        //判断集合内元素数量，是否超过10个
                        if (gpsDataSet.size() >= 10) {
                            XLog.d("集合已满，发送一次请求");

                            //发送请求
                            HttpUtil.uploadMany(gpsDataSet);
                            //清空集合
                            gpsDataSet.clear();
                            XLog.d("清空集合，此时集合长度为："+gpsDataSet.size());
                        }else {
                            XLog.d("本次数据已加入集合，此时集合长度为："+gpsDataSet.size());
                        }

                    }else {
                        String locationDetail = aMapLocation.getLocationDetail();
                        XLog.d("定位错误，信息为："+locationDetail);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @NonNull
    @Override
    public Result doWork() {
        XLog.d("doWork 执行了一次");
        mLocationClient.startLocation();

        return Result.success();
    }
}
