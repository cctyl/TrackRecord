package cn.tyl.gps_demo.util;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import java.util.Locale;

import cn.tyl.gps_demo.GPSApplication;

public class DeviceUtils {

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return  语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取androidId
     * @return
     */
    public static String getAndroidId() {

        return Settings.Secure.getString(GPSApplication.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 相机是否可用
     *
     * @return
     */
    public static boolean isSupportCamera() {
        PackageManager packageManager = GPSApplication.getContext().getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    /**
     * 获取手机厂商
     *  HuaWei
     * @return 手机厂商
     */
    public static String getPhoneBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机型号
     * @return 手机型号
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 获取当前手机系统版本号
     * Android     10
     * @return 系统版本号
     */
    public static String getVersionRelease() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取当前手机设备名
     * 设备统一型号,不是"关于手机"的中设备名
     * @return 设备名
     */
    public static String getDeviceName() {
        return Build.DEVICE;
    }

    /**
     * HUAWEI HWELE ELE-AL00 10
     * @return
     */
    public static String getPhoneDetail() {
        return DeviceUtils.getPhoneBrand() + " " + DeviceUtils.getDeviceName() + " " + DeviceUtils.getPhoneModel() + " " + DeviceUtils.getVersionRelease();
    }

    /**
     * 获取手机主板名
     *
     * @return  主板名
     */
    public static String getDeviceBoard() {
        return Build.BOARD;
    }


    /**
     * 获取手机厂商名
     * HuaWei
     * @return  手机厂商名
     */
    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }



}
