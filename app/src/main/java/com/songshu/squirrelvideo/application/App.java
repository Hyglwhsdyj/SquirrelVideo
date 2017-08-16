package com.songshu.squirrelvideo.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.db.DBHelper;
import com.songshu.squirrelvideo.mail.GlobalExceptionHandler;
import com.songshu.squirrelvideo.manager.LocalMediaManager;
import com.songshu.squirrelvideo.manager.MyActivityManager;
import com.songshu.squirrelvideo.manager.MyDownloadManager;
import com.songshu.squirrelvideo.service.AppRetrofitSpiceService;
import com.songshu.squirrelvideo.utils.FileUtils;
import com.songshu.squirrelvideo.utils.ImageLoaderUtils;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.SSUtil;
import com.songshu.squirrelvideo.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by yb on 15-7-4.
 */
public class App extends Application {

    private static final String TAG = App.class.getSimpleName() + ":";

    private static App mApp;
    private static DB mSnappyDb;
    private static SpiceManager mSpiceManager;
    private static Toast mToast;
    private static SharedPreferences mShared;
    private static MyActivityManager myActivityManager;
    private static DBHelper mDBHelper;
    private static MyDownloadManager mMyDownloadManager;

    @Override
    public void onCreate() {
        L.d(TAG, "App --> onCreate");
        super.onCreate();
        initUMeng();
        initData();
        initLogRecordFile();
        initSpiceManger();
        initSharedPreferenced();
        initImageLoader();
        initMyActivityManger();
        initDbHelper();
        initDownloadManagerPro();
        initLocalMediaFiles();

        GlobalExceptionHandler.init(App.getContext());
    }


    private void initUMeng() {
        MobclickAgent.openActivityDurationTrack(false);
    }

    private void initData() {
        mApp = this;
        Const.DOMAIN = Util.isDebugMode(this) ? Const.VIDEO_WEB_API_DEV : Const.VIDEO_WEB_API_REL;
        Const.SSID = SSUtil.getSSCodeNum();
        Const.PRODUCTID = SSUtil.getSSProductVersion();
        Const.DEVICE = Build.DEVICE;
        Const.MODEL = Build.MODEL;
        L.d(TAG, "地址 : " + Const.DOMAIN + " , 松鼠号 : " + Const.SSID + " , 版本号 : " + Const.PRODUCTID + " , 设备名 : " + Const.DEVICE + " , 模型 : " + Const.MODEL);
    }

    private void initLogRecordFile() {
        FileUtils.getRecordFile();
    }

    private void initSpiceManger() {
        mSpiceManager = new SpiceManager(AppRetrofitSpiceService.class);
        mSpiceManager.start(this);
    }

    private void initSharedPreferenced() {
        mShared = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void initImageLoader() {
        ImageLoaderUtils.init(getContext());
    }

    private void initMyActivityManger() {
        myActivityManager = MyActivityManager.getScreenManager();
    }

    private void initDbHelper() {
        mDBHelper = new DBHelper(getContext());
        mDBHelper.getReadableDatabase();
    }

    private void initDownloadManagerPro() {
        mMyDownloadManager = new MyDownloadManager(getContext());
    }


    private void initLocalMediaFiles() {
        LocalMediaManager.initLocalMediaFiles();
    }

    /**
     * *************************************************************************
     */


    public static SpiceManager getSpiceManager() {
        return mSpiceManager;
    }

    public static DB getSnappyDb() {
        try {
            mSnappyDb = DBFactory.open(getContext());
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return mSnappyDb;
    }

    public static void closeSnappyDb() {
        try {
            mSnappyDb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public static SharedPreferences getSharedPref() {
        return mShared;
    }

    public static synchronized Context getContext() {
        return getApplication();
    }

    public static synchronized App getApplication() {
        return mApp;
    }

    public static MyDownloadManager getMyDownloadManager() {
        return mMyDownloadManager;
    }

    public static void showToast(int resId) {
        showToast(getContext().getString(resId));
    }

    public static void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }


    public static DBHelper getmDBHelper() {
        return mDBHelper;
    }

    public static MyActivityManager getActivityManager() {
        return myActivityManager;
    }
}
