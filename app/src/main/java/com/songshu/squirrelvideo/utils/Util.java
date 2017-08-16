package com.songshu.squirrelvideo.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.songshu.squirrelvideo.activity.HomeActivity;
import com.songshu.squirrelvideo.activity.MainActivity;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.entity.MovieRecommData;

import java.security.MessageDigest;
import java.util.List;

/**
 * Created by yb on 15-7-4.
 */
public class Util {

    private static final String TAG = Util.class.getSimpleName() + ":";

    public static final String DEBUG_MODE = "persist.songshu.devmode";

    public static boolean isDebugMode(Context context) {
        return SystemPropertiesProxy.getBoolean(context, DEBUG_MODE, false);
    }

    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static <T> boolean compareTwoArrayLength(T[] a, T[] b) {
        if (a == null || b == null) return false;
        if (a.length != b.length) return false;
        if (a instanceof MovieRecommData[]) {
            MovieRecommData[] tmp_a = (MovieRecommData[]) a;
            MovieRecommData[] tmp_b = (MovieRecommData[]) b;
            for (int i = 0; i < a.length; i++) {
                if (tmp_a[i].video_list == null || tmp_b[i].video_list == null) return false;
                if (tmp_a[i].video_list.length != tmp_b[i].video_list.length) return false;
            }
        }
        return true;
    }

    public static void exitThisApp(Context mContext) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(startMain);
            System.exit(0);
        } else {// android2.1
            ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            am.restartPackage(mContext.getPackageName());
        }
    }


    public static void queryAllRunningAppInfo(Context mContext) {
        ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
            int pid = appProcess.pid; // pid
            String processName = appProcess.processName; // 进程名
            L.d(TAG, "processName: " + processName + "  pid: " + pid);
            if ("com.songshu.squirrelvideo:vitamio".equals(processName)) {
                L.d(TAG, "................................准备清理....................................");
                mActivityManager.killBackgroundProcesses("com.songshu.squirrelvideo");
                break;
            }
        }
    }


    public static final String FLAG_USER_CLEAN_THE_APP_UNNORMAL = "flag_user_clean_the_app_unnormal";

    /**
     * 获取系统中正在运行的任务信息（强调下，任务是多个activity的集合）
     * 获取正在运行的任务这里一定要注意，这里我们获取的时候，
     * 你的任务或者其中的activity可能没结束，但是当你在后边使用的时候，很有可能已经被kill了哦。
     * 意思很简单，系统返给你的正在运行的task，是暂态的，仅仅代表你调用该方法时系统中的状态，
     * 至于后边是否发生了该变，系统概不负责、
     */
    public static void queryAllRunningAppActivitys() {
        int maxNum = 40;
        ActivityManager activityManager = (ActivityManager) App.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(maxNum);
        for (ActivityManager.RunningTaskInfo taskInfo : runningTasks) {
            L.d(TAG, "...................一个任务信息开始：");
            L.d(TAG, "...................当前任务中正处于运行状态的activity数目:" + taskInfo.numRunning);
            L.d(TAG, "...................当前任务中的activity数目: " + taskInfo.numActivities);
            L.d(TAG, "...................启动当前任务的activity名称:" + taskInfo.baseActivity.getClassName());
            L.d(TAG, "...................启动当前任务的activity名称:" + taskInfo.topActivity.getClassName());
            if (taskInfo.numRunning == 0) {
                App.getSharedPref().edit().putBoolean(FLAG_USER_CLEAN_THE_APP_UNNORMAL, true).commit();
            }
        }
    }

    public static void checkAndFinishVideoAct(Activity activity) {
        if (activity != null) {
            boolean result = App.getSharedPref().getBoolean(FLAG_USER_CLEAN_THE_APP_UNNORMAL, false);
            if (result) {
                L.d(TAG, "............Finish Video Act............" + activity.getClass().getSimpleName());
                activity.finish();
                App.getSharedPref().edit().putBoolean(FLAG_USER_CLEAN_THE_APP_UNNORMAL, false).commit();
            }
        }

    }

    public static void checkAndFinishOtherAct(Activity activity) {
        if (activity != null && MainActivity.isDiffEntrance && !HomeActivity.isHomeActivityOnResume) {
            L.d(TAG, "............Finish Other Act............" + activity.getClass().getSimpleName());
            activity.finish();
            startHomeActivity();
        }
    }


    private static void startHomeActivity() {
        App.getActivityManager().emptyStackExceptMainAct();
        Intent intent = MainActivity.homeIntent;
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(intent);
    }
}
