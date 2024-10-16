package cn.tyl.gps_demo.entity;

import android.os.Build;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import cn.tyl.gps_demo.util.DeviceUtils;

@Entity(tableName = "gps_data")
public class GPSData {


    @Override
    @Ignore
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GPSData gpsData = (GPSData) o;

        if (!longitude.equals(gpsData.longitude)) return false;
        return latitude.equals(gpsData.latitude);
    }

    @Override
    @Ignore
    public int hashCode() {
        int result = longitude.hashCode();
        result = 31 * result + latitude.hashCode();
        return result;
    }


    //表示主键自增
    @PrimaryKey(autoGenerate = true)
    public Integer id;

    //经度

    public Double longitude;

    //纬度
    public Double latitude;

    //机型
    public String model;


    //定位时间
    public long time;

    public GPSData() {
    }

    @Ignore
    public GPSData(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.model = DeviceUtils.getPhoneDetail();
        this.time = System.currentTimeMillis();
    }

    @Override
    @Ignore
    public String toString() {
        return "GPSData{" +
                "id=" + id +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", model='" + model + '\'' +
                ", time=" + time +
                '}';
    }
}
