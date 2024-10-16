package cn.tyl.gps_demo;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.room.Room;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.PatternFlattener;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;

import java.time.LocalDateTime;
import java.util.List;

import cn.tyl.gps_demo.dao.ApplicationDatabase;
import cn.tyl.gps_demo.dao.GpsDataDao;
import cn.tyl.gps_demo.entity.GPSData;
import cn.tyl.gps_demo.util.HttpUtil;

public class GPSApplication extends Application {
    private static Context context;
    private static String TAG = "GPSApplication";
    public static AMapLocationClient mLocationClient = null;



    public static boolean isRun = false;
    public static LocalDateTime lastSendTime;
    public static LocalDateTime lastGpsTime;
    public static String  respStr;
    private ApplicationDatabase mApplicationDatabase;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        Log.d(TAG, "程序启动: ");
        context = getApplicationContext();
        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(BuildConfig.DEBUG ? LogLevel.ALL             // 指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
                        : LogLevel.NONE)
                .tag("GPSDM")
                .build();
        Printer androidPrinter = new AndroidPrinter(true);  // 通过 android.util.Log 打印日志的打印器,也就是打印到控制台
        Printer filePrinter = new FilePrinter                          // 打印日志到文件的打印器
                .Builder(Environment.getExternalStorageDirectory()
                .getPath() + "/gps/xlog")                              // 指定保存日志文件的路径
                .flattener(new PatternFlattener("{d yyyy/MM/dd HH:mm:ss} {l}|{t}: {m}"))  // 指定日志平铺器，默认为 DefaultFlattener
                .fileNameGenerator(new DateFileNameGenerator())        // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
                .backupStrategy(new NeverBackupStrategy())             // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
                .build();

        XLog.init(                                                     // 初始化 XLog
                config,                                                // 指定日志配置，如果不指定，会默认使用 new LogConfiguration.Builder().build()
                androidPrinter,
                filePrinter);


        mApplicationDatabase = Room
                .databaseBuilder(this,
                        ApplicationDatabase.class,
                        "gps_database" //数据库文件的名字，之前是在SQLiteOpenHelper的构造函数里面指定的
                )
                //允许迁移数据库，（如果不设置，那么数据库发生变更时，room就会默认删除原本数据库，再创建信数据库，那么原本的数据就会丢失）
                .addMigrations()
                //运行在主线程中操作数据库（默认情况下room不能在主线程操作数据库，因为是耗时操作）
                .allowMainThreadQueries()
                .build()
        ;

        Log.d(TAG, "onCreate: 数据库启动完毕,数据库对象状态："+mApplicationDatabase);

        XLog.d("存储位置为：" + Environment.getExternalStorageDirectory().getPath());

        initGaoDeGPS(context);



    }
    private static GPSApplication mApp;
    public static GPSApplication getInstance(){
        return mApp;
    }


    public ApplicationDatabase getApplicationDatabase() {
        return mApplicationDatabase;
    }

    /**
     * 获取全局上下文
     */
    public static Context getContext() {
        return context;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initGaoDeGPS(Context context) {
        XLog.d("initGaoDeGPS");
        Log.d(TAG, "initGaoDeGPS: ");
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
                XLog.d("监听到地址：" + aMapLocation);
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        GpsDataDao gpsDataDao = GPSApplication.getInstance().getApplicationDatabase().gpsDataDao();
                        lastGpsTime = LocalDateTime.now();
                        XLog.d("成功获得定位，结果为:" + aMapLocation.getLongitude() + "-" + aMapLocation.getLatitude());
                        XLog.d("关闭gps");
                        mLocationClient.stopLocation();
                        //数据加入集合中
                        gpsDataDao.insert(new GPSData(aMapLocation.getLongitude(), aMapLocation.getLatitude()));


                        //判断集合内元素数量，是否超过30个
                        int count = gpsDataDao.count();
                        if (count >= 30) {
                            sendGpsDataToServer();
                        } else {
                            XLog.d("本次数据已加入集合，此时集合长度为：" + count);
                        }

                    } else {
                        String locationDetail = aMapLocation.getLocationDetail();
                        XLog.d("定位错误，信息为：" + locationDetail);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            XLog.e("定位错误，信息为：" + e.getMessage(), e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void sendGpsDataToServer() {
        lastSendTime = LocalDateTime.now();
        XLog.d("集合已满，发送一次请求");

        //从数据库中查询数据
        GpsDataDao gpsDataDao = GPSApplication.getInstance().getApplicationDatabase().gpsDataDao();
        List<GPSData> all = gpsDataDao.findAll();

        //发送请求
        HttpUtil.uploadMany(all);

        //清空集合
        gpsDataDao.deleteAll();

        XLog.d("清空集合，此时集合长度为：" + gpsDataDao.count());
    }

}
