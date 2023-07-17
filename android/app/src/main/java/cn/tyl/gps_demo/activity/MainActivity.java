package cn.tyl.gps_demo.activity;

import static cn.tyl.gps_demo.util.ThreadPool.executorService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.elvishew.xlog.XLog;
import com.google.common.util.concurrent.ListenableFuture;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import cn.tyl.gps_demo.R;
import cn.tyl.gps_demo.databinding.ActivityMainBinding;
import cn.tyl.gps_demo.entity.GPSData;
import cn.tyl.gps_demo.entity.ShareData;
import cn.tyl.gps_demo.service.GPSService;
import cn.tyl.gps_demo.service.MyWorker;
import cn.tyl.gps_demo.thread.GPSRunnable;
import cn.tyl.gps_demo.util.HttpUtil;
import cn.tyl.gps_demo.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;//页面对象
    private MainViewModel mainViewModel;//数据存储
    private AMapLocationClient mLocationClient = null;//高德定位客户端
    Future<Integer> gpsFutureTask;//gps 数据获取线程
    boolean gpsTaskOpen = false;//是否开启了gps获取线程
    private Set<GPSData> gpsDataSet = new HashSet<>();//存储最近10条定位数据
    WorkManager workManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //1.绑定视图
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setContentView(binding.getRoot());

        //2.初始化ViewModel
        mainViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(MainViewModel.class);
        //2.1将viewModel传入页面
        binding.setModel(mainViewModel);
        //2.2设置生命周期监听
        binding.setLifecycleOwner(this);

        //3.监听viewModel中数据的变化
        //initViewModelListener();

        //4.检查并申请权限
        checkGPSPermission();

        //5.监听按钮点击事件
        initOnClickListener();



    }

    /**
     * 初始化高德定位
     */
    private void initGaoDeGPS() {
        //高德定位sdk
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);


        try {

            mLocationClient = ShareData.initClient(getApplicationContext());

            //定位配置
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            mLocationOption.setOnceLocation(true);

            mLocationClient.setLocationOption(mLocationOption);


            if (mLocationClient == null) {
                XLog.e("mLocationClient 为null");
            }

            mLocationClient.setLocationListener(aMapLocation -> {
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

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 检查并申请gps权限
     */
    private void checkGPSPermission() {

        XXPermissions.with(this)
                // 申请单个权限
                .permission(Permission.ACCESS_FINE_LOCATION)
                // 申请多个权限
                .permission(Permission.ACCESS_COARSE_LOCATION)

                .permission(Permission.READ_EXTERNAL_STORAGE)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)

                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            toast("获取定位权限成功");
                        } else {
                            toast("获取部分权限成功，但部分权限未正常授予");
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            toast("被永久拒绝授权，请手动授予定位权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(MainActivity.this, permissions);
                        } else {
                            toast("获取定位权限失败");
                        }
                    }
                });
    }

    /**
     * 开启线程，不断的获取gps数据
     */
    private void initGpsThread() {

        XLog.d("initGpsThread: gpsTaskOpen: " + gpsTaskOpen);
        if (!gpsTaskOpen) {
            gpsTaskOpen = true;
            //线程开始执行
            GPSRunnable gpsRunnable = new GPSRunnable(mLocationClient);
            gpsFutureTask = executorService.submit(gpsRunnable);
        } else {
            XLog.d("initGpsThread: gps已开启，不要重复开启线程");
        }
    }


    private GPSService.InnerBinder remoteBinder;//服务向外暴露的内部类，用于调用方法
    /**
     * 这个是决定，服务链接时要做什么，服务断开时要做什么
     */
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            XLog.d("服务绑定成功");

            //得到了FirstService里面的InnerBinder
            remoteBinder = (GPSService.InnerBinder) service;

            XLog.d("初始化高德定位");
            initGaoDeGPS();

            XLog.d("开启gps数据获取线程");
            initGpsThread();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            XLog.d("服务断开连接");
            remoteBinder = null;

            XLog.d("停用gps");
            mLocationClient.stopLocation();

            boolean cancel = gpsFutureTask.cancel(true);
            XLog.d("停止线程，结果为:" + cancel);

            //关闭线程池
            executorService.shutdown();
        }
    };

    /**
     * 初始化服务
     */
    private void initService() {
        XLog.d("初始化服务");
        Intent intent = new Intent();
        intent.setClass(this, GPSService.class);

        boolean isBind = bindService(intent, conn, BIND_AUTO_CREATE);
        XLog.d("绑定完成，结果为：" + isBind);
    }

    /**
     * 初始化点击事件监听
     */
    private void initOnClickListener() {
        assert binding.openBtn != null;
        binding.readCountTv.setOnClickListener(v -> {
            mainViewModel.addReadCount();
        });

        binding.openBtn.setOnClickListener(v -> {
            XLog.d("启动workRequest");
//            initService();
            PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES)
                    .build();

            workManager = WorkManager
                    .getInstance(getApplicationContext());
            workManager.enqueueUniquePeriodicWork("gpsWork11", ExistingPeriodicWorkPolicy.KEEP,workRequest);
        });

        binding.closeBtn.setOnClickListener(v -> {
            XLog.d("解绑服务");
            unbindService(conn);
            gpsTaskOpen = false;
        });

        binding.testBtn.setOnClickListener(v -> {


        });

    }

    /**
     * 对viewmodel中的变量进行监听
     */
    private void initViewModelListener() {
        mainViewModel.getReadCount().observe(this, newReadCount -> {
            XLog.d("initViewModelListener: newReadCount=" + newReadCount);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        XLog.d("onDestroy: mainact 被暂停");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XLog.d("onDestroy: mainact 被销毁");
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT);
    }
}