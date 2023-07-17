package cn.tyl.gps_demo;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.PatternFlattener;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.ConsolePrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy;
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;

public class GPSApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

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

        XLog.d("存储位置为："+Environment.getExternalStorageDirectory().getPath());
    }
    /**
     * 获取全局上下文*/
    public static Context getContext() {
        return context;
    }

}
