package com.songshu.squirrelvideo.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * 系统版本信息类
 */
public class DeviceUtils {

    /**
     * >=2.2
     */
    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * >=2.3
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * >=3.0 LEVEL:11
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * >=3.1
     */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * >=4.0 14
     */
    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * 获得设备型号
     *
     * @return
     */
    public static String getDeviceModel() {
        return StringUtils.trim(Build.MODEL);
    }

    /**
     * 检测是否魅族手机
     */
    public static boolean isMeizu() {
        return getDeviceModel().toLowerCase().indexOf("meizu") != -1;
    }

    /**
     * 检测是否HTC手机
     */
    public static boolean isHTC() {
        return getDeviceModel().toLowerCase().indexOf("htc") != -1;
    }

    public static boolean isXiaomi() {
        return getDeviceModel().toLowerCase().indexOf("xiaomi") != -1;
    }

    /**
     * 获得设备制造商
     *
     * @return
     */
    public static String getManufacturer() {
        return StringUtils.trim(Build.MANUFACTURER);
    }

    public static int getSDKVersionInt() {
        return Build.VERSION.SDK_INT;
    }

    @SuppressWarnings("deprecation")
    public static String getSDKVersion() {
        return Build.VERSION.SDK;
    }

    /**
     * 判断是否是平板电脑
     *
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isHoneycombTablet(Context context) {
        return hasHoneycomb() && isTablet(context);
    }

    /**
     * 获取屏幕高度
     */
    @SuppressWarnings("deprecation")
    public static int getScreenHeight(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getHeight();
    }

    /**
     * 获取屏幕宽度
     */
    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * 获得设备屏幕密度
     */
    public static float getScreenDensity(Context context) {
        DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
        return metrics.density;
    }

    /**
     * 获取屏幕尺寸
     *
     * @param w
     * @param h
     * @param context
     * @return
     */
    public static int[] getScreenSize(int w, int h, Context context) {
        int phoneW = getScreenWidth(context);
        int phoneH = getScreenHeight(context);

        if (w * phoneH > phoneW * h) {
            phoneH = phoneW * h / w;
        } else if (w * phoneH < phoneW * h) {
            phoneW = phoneH * w / h;
        }

        return new int[]{phoneW, phoneH};
    }

    public static int[] getScreenSize(int w, int h, int phoneW, int phoneH) {
        if (w * phoneH > phoneW * h) {
            phoneH = phoneW * h / w;
        } else if (w * phoneH < phoneW * h) {
            phoneW = phoneH * w / h;
        }
        return new int[]{phoneW, phoneH};
    }

    /**
     * 设置屏幕亮度
     */
    public static void setBrightness(final Activity context, float f) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = f;
        if (lp.screenBrightness > 1.0f)
            lp.screenBrightness = 1.0f;
        else if (lp.screenBrightness < 0.01f)
            lp.screenBrightness = 0.01f;
        context.getWindow().setAttributes(lp);
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftInput(Context ctx, View v) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 这个方法可以实现输入法在窗口上切换显示，如果输入法在窗口上已经显示，则隐藏，如果隐藏，则显示输入法到窗口上
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示软键盘
     */
    public static void showSoftInput(Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);// (v,
        // InputMethodManager.SHOW_FORCED);
    }

    /**
     * 软键盘是否已经打开
     *
     * @return
     */
    protected boolean isHardKeyboardOpen(Context ctx) {
        return ctx.getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO;
    }

    /**
     * 获取CPU信息
     *
     * @return
     */
    public static String getCpuInfo() {
        String cpuInfo = "";
        try {
            if (new File("/proc/cpuinfo").exists()) {
                FileReader fr = new FileReader("/proc/cpuinfo");
                BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
                cpuInfo = localBufferedReader.readLine();
                localBufferedReader.close();

                if (cpuInfo != null) {
                    cpuInfo = cpuInfo.split(":")[1].trim().split(" ")[0];
                }
            }
        } catch (IOException e) {
        } catch (Exception e) {
        }
        return cpuInfo;
    }

    /**
     * 开启跳转新的APK
     *
     * @param ctx
     * @param packageName
     */
    public static void startApkActivity(final Context ctx, String packageName) {
        PackageManager pm = ctx.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(packageName, 0);
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage(pi.packageName);

            List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);

            ResolveInfo ri = apps.iterator().next();
            if (ri != null) {
                String className = ri.activityInfo.name;
                intent.setComponent(new ComponentName(packageName, className));
                ctx.startActivity(intent);
            }
        } catch (NameNotFoundException e) {
        }
    }

    /**
     * 获取手机串号
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephone = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephone.getDeviceId();
        return imei;
    }

    /**
     * 获取设备序列号
     */
    public static String getSimSerialNumber(Context context) {
        TelephonyManager telephone = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String sn = telephone.getSimSerialNumber();
        return sn;
    }

    /**
     * 获取本机型号
     */
    public static String getPhoneType() {
        String phoneType = Build.MODEL;
        return phoneType;
    }

    /**
     * 获取无线mac地址
     */
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * ==== 从读取的CPU信息中截取架构版本 ====
     */
    public static String CPUarchitecture(String result) {
        String s = result;
        // (7I)
        int pos;
        pos = s.indexOf("("); // 从(开始 往后
        String str_temp = new String();
        str_temp = s.substring(pos + 1, s.length()); // v7I 以后的

        pos = str_temp.indexOf(")"); // 从)开始 往前
        str_temp = str_temp.substring(0, pos); // )以前的
        // v7I
        pos = str_temp.indexOf("v"); // 从v开始
        str_temp = str_temp.substring(pos + 1, str_temp.length() - 1);
        // 7
        return str_temp;
    }

    /**
     * 拼接m3u8文件的地址
     */
    public static String createM3u8Url(String path) {
        path = "http://wt1.nazhengshu.com/51zc_aes/m3u8"
                // path = "http://wt1.nazhengshu.com/51zc/m3u8"
                + path.substring(path.indexOf(".com/") + 4) + ".m3u8";
        return path;
    }

    public static String createEncryptM3u8Url(String path) {
        path = "http://wt1.nazhengshu.com/51zc_aes/m3u8" + path.substring(path.indexOf(".com/") + 4) + ".m3u8";
        // "51zc_aes"
        return path;
    }

    /**
     * 获取程序版本名称
     */
    public static String getVersion(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "获取版本名异常";
        }
    }

    /**
     * 获取程序版本号
     */
    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        int version = packageInfo.versionCode;
        return version;
    }

    /**
     * 检查SDcard是否存在
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    public static String getSDTotalSize(Context mContext) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(mContext, blockSize * totalBlocks);
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    public static long getSDTotalSizeData(Context mContext) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return blockSize * totalBlocks;
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public static String getSDAvailableSize(Context mContext) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(mContext, blockSize * availableBlocks);
    }

    public static long getSDAvailableSizeData(Context mContext) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;
    }


    /**
     * 获取已经用掉的控件
     *
     * @param mContext
     * @return
     */
    public static String getAlreadyUseSize(Context mContext) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(mContext, blockSize * totalBlocks - blockSize * availableBlocks);
    }


    /**
     * 获得机身内存总大小
     *
     * @return
     */
    public static String getRomTotalSize(Context mContext) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(mContext, blockSize * totalBlocks);
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    public static String getRomAvailableSize(Context mContext) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(mContext, blockSize * availableBlocks);
    }

}
