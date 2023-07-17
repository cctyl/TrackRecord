package cn.tyl.gps_demo.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elvishew.xlog.XLog;

import cn.tyl.gps_demo.entity.GPSData;

/**
 * 存储数据的地方
 */
public class MainViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";
    private MutableLiveData<Integer> readCount;


    public MutableLiveData<Integer> getReadCount() {
        if (readCount==null){
            readCount = new MutableLiveData<>();
            readCount.setValue(0);
        }
        return readCount;
    }



    public void addReadCount(){

        readCount.setValue(readCount.getValue()+1);
        XLog.d( Thread.currentThread().getName()+"-addReadCount: "+readCount.getValue());
    }

}
