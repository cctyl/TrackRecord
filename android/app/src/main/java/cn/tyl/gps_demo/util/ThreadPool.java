package cn.tyl.gps_demo.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    public static final ExecutorService executorService = Executors.newFixedThreadPool(3);
}
