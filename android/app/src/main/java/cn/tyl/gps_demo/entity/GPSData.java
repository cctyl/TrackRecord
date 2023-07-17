package cn.tyl.gps_demo.entity;

import android.os.Build;

import cn.tyl.gps_demo.util.DeviceUtils;

public class GPSData {


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GPSData gpsData = (GPSData) o;

        if (!longitude.equals(gpsData.longitude)) return false;
        return latitude.equals(gpsData.latitude);
    }

    @Override
    public int hashCode() {
        int result = longitude.hashCode();
        result = 31 * result + latitude.hashCode();
        return result;
    }

    //经度
    private Double longitude;

    //纬度
    private Double latitude;

    //机型
    private String model;


    //定位时间
    private long time;


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public GPSData(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.model = DeviceUtils.getPhoneDetail();
        this.time = System.currentTimeMillis();
    }
}
