package cn.tyl.gps_demo.thread;


import com.amap.api.location.AMapLocationClient;
import com.elvishew.xlog.XLog;

import java.util.concurrent.Callable;


public class GPSRunnable implements Callable<Integer> {


    private AMapLocationClient mapLocationClient;

    public GPSRunnable( AMapLocationClient mapLocationClient) {
        this.mapLocationClient = mapLocationClient;
    }

    /**
     * 定时获取gps数据，然后发起请求
     */
    @Override
    public Integer call() throws Exception {
        XLog.d( "run: 线程开始执行 ");

        //虚假标记，永远不会被改变,用于后面编译检查返回值

        int i = 100;
        while (i>1){
            XLog.d( "call: "+Thread.currentThread().getName()+"-循环中...");

            //开始获取gps信息
            XLog.d( "call: 启动gps");

            //获取定位
            mapLocationClient.startLocation();
            Thread.sleep(5000);

            XLog.d( "call: 循环结束...");
            //10分钟获取一次gps
            Thread.sleep(10000);

        }
        XLog.d( "call: 退出了循环");

        return 0;
    }
}
