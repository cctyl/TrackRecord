package cn.tyl.gps_demo.activity;



import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.elvishew.xlog.XLog;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import cn.tyl.gps_demo.GPSApplication;
import cn.tyl.gps_demo.R;
import cn.tyl.gps_demo.dao.GpsDataDao;
import cn.tyl.gps_demo.databinding.ActivityMainBinding;
import cn.tyl.gps_demo.entity.GPSData;
import cn.tyl.gps_demo.service.GPSService;
import cn.tyl.gps_demo.service.GpsWorker;
import cn.tyl.gps_demo.thread.GPSRunnable;
import cn.tyl.gps_demo.viewmodel.MainViewModel;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;//页面对象
    private MainViewModel mainViewModel;//数据存储
    WorkManager workManager;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private GpsDataDao gpsDataDao ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //1.绑定视图
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setContentView(binding.getRoot());

        //2.初始化ViewModel
        mainViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MainViewModel.class);
        //2.1将viewModel传入页面
        binding.setModel(mainViewModel);
        //2.2设置生命周期监听
        binding.setLifecycleOwner(this);

        //3.监听viewModel中数据的变化
        //initViewModelListener();

        //4.检查并申请权限
        checkGPSPermission();

        //初始化数据库
        gpsDataDao = GPSApplication.getInstance().getApplicationDatabase().gpsDataDao();

        //5.监听按钮点击事件
        initOnClickListener();

        if (!GPSApplication.isRun){
            GPSApplication.isRun = true;
            //6.启动worker
            startWorker();

            //7.启动service
            initService();
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

                .permission(Permission.READ_EXTERNAL_STORAGE).permission(Permission.WRITE_EXTERNAL_STORAGE)

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
     * 初始化服务
     */
    private void initService() {
        XLog.d("初始化服务");
        Intent intent = new Intent();
        intent.setClass(this, GPSService.class);

        startService(intent);

    }

    /**
     * 初始化点击事件监听
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private void initOnClickListener() {

        binding.openBtn.setOnClickListener(v -> {
            toast("启动服务");
            XLog.d("启动服务");
            initService();
        });

        binding.closeBtn.setOnClickListener(v -> {
            toast("解绑服务");
            XLog.d("解绑服务");
            Intent intent = new Intent();
            intent.setClass(this, GPSService.class);
            stopService(intent);
        });

        binding.testBtn.setOnClickListener(v -> {
            int count = gpsDataDao.count();
            toast("当前共有" + count + "数据待发送");
            binding.tvLastDataNum.setText(count+"条");
            if ( count>0){
                GPSData gpsData = gpsDataDao.findFirst();
                binding.tvLongitude.setText(""+gpsData.longitude);
                binding.tvLatitude.setText(""+gpsData.latitude);
            }else {
                binding.tvLongitude.setText("暂无数据");
                binding.tvLatitude.setText("暂无数据");
            }

            if (GPSApplication.lastSendTime!=null){
                binding.tvLastSendTime.setText(dateTimeFormatter.format(GPSApplication.lastSendTime));
            }else {
                binding.tvLastSendTime.setText("无记录");
            }
            if (GPSApplication.lastGpsTime!=null) {
                binding.tvLastGpsTime.setText(dateTimeFormatter.format(GPSApplication.lastGpsTime));
            }else {
                binding.tvLastGpsTime.setText("无记录");
            }


            Log.d(TAG, "initOnClickListener: respStr="+GPSApplication.respStr );
            binding.etRespJson.setText(GPSApplication.respStr==null?"":GPSApplication.respStr);


        });

        binding.locationBtn.setOnClickListener(view -> {

            toast("开始进行一次定位");
            GPSApplication.mLocationClient.startLocation();

        });

        binding.sendBtn.setOnClickListener(view -> {
            if (gpsDataDao.count() > 0) {
                GPSApplication.sendGpsDataToServer();
            } else {
               toast("当前无数据可发送");
            }
        });



        binding.insertData.setOnClickListener(view -> {


            GPSData gpsData = new GPSData(102.11, 105.92);
            Long id = gpsDataDao.insert(gpsData);
            Log.d(TAG, "initOnClickListener: 新增一条完成，id="+id);

            List<GPSData> dataList = new ArrayList<>(5);
            for (int i = 0; i < 5; i++) {
                dataList.add(new GPSData(102.11, 105.92));
            }
            gpsDataDao.insert(dataList);
            Log.d(TAG, "initOnClickListener: 新增多条完成，idSize="+dataList.size());

        });

        binding.delData.setOnClickListener(view -> {


            int i = gpsDataDao.deleteAll();
            Log.d(TAG, "initOnClickListener: 删除全部完成，i="+i);

        });

        binding.findAll.setOnClickListener(view -> {
            int count = gpsDataDao.count();
            Log.d(TAG, "initOnClickListener: 当前剩余数据条数："+count);


            if (count>0) {
                GPSData first = gpsDataDao.findFirst();
                Log.d(TAG, "initOnClickListener: 第一条数据是：" + first.toString());

                List<GPSData> all = gpsDataDao.findAll();
                for (GPSData gpsData : all) {
                    Log.d(TAG, gpsData.toString());
                }
            }else {
                Log.d(TAG, "initOnClickListener: 暂无数据");
            }

        });


    }

    private void startWorker() {
        XLog.d("启动workRequest");
        toast("启动worker");
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(GpsWorker.class, 30, TimeUnit.MINUTES).build();
        workManager = WorkManager.getInstance(getApplicationContext());
        workManager.enqueueUniquePeriodicWork("gpsWork11", ExistingPeriodicWorkPolicy.KEEP, workRequest);
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
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}