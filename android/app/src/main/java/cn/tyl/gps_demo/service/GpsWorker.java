package cn.tyl.gps_demo.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.elvishew.xlog.XLog;

import cn.tyl.gps_demo.GPSApplication;

public class GpsWorker extends Worker {


    public GpsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        XLog.d("doWork 执行了一次");
        GPSApplication.mLocationClient.startLocation();

        return Result.success();
    }
}
