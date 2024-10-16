package cn.tyl.gps_demo.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import cn.tyl.gps_demo.entity.GPSData;

@Database(entities = {GPSData.class},version = 1,exportSchema = true)
public abstract class ApplicationDatabase extends RoomDatabase {

    //每增加一张表，就要加一个类似的方法
    public abstract GpsDataDao gpsDataDao();
}