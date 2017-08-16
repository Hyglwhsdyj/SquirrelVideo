package com.songshu.squirrelvideo.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.db.ormdb.MyOrmSQLiteDaoGeter;
import com.songshu.squirrelvideo.entity.po.PoJoLocalMedia;
import com.songshu.squirrelvideo.manager.LocalMediaManager;
import com.songshu.squirrelvideo.manager.TitleManager;
import com.songshu.squirrelvideo.utils.DensityUtil;
import com.songshu.squirrelvideo.utils.FileUtils;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.PinyinUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import io.vov.vitamio.ThumbnailUtils;
import io.vov.vitamio.provider.MediaStore;

/**
 * Created by yb on 15-10-30.
 */

/**
 * 媒体扫描
 */
public class MediaScannerService extends Service implements Runnable {
    private static final String TAG = MediaScannerService.class.getSimpleName() + ":";

    /**
     * 首次扫描
     */
    public static final String PREF_KEY_FIRST = "application_first";

    /**
     * 服务名称
     */
    private static final String SERVICE_NAME = "com.songshu.squirrelvideo.service.MediaScannerService";

    /**
     * 扫描文件夹
     */
    public static final String EXTRA_DIRECTORY = "scan_directory";

    /**
     * 扫描文件
     */
    public static final String EXTRA_FILE_PATH = "scan_file";
    public static final String EXTRA_MIME_TYPE = "mimetype";

    /**
     * 当前状态
     */
    private volatile int mServiceStatus = SCAN_STATUS_NORMAL;

    /**
     * 扫描平常状态
     */
    public static final int SCAN_STATUS_NORMAL = -1;

    /**
     * 开始扫描
     */
    public static final int SCAN_STATUS_START = 0;

    /**
     * 正在扫描 扫描到一个视频文件
     */
    public static final int SCAN_STATUS_RUNNING = 1;

    /**
     * 扫描完成
     */
    public static final int SCAN_STATUS_END = 2;


    private ArrayList<IMediaScannerObserver> observers = new ArrayList<IMediaScannerObserver>();
    private ConcurrentHashMap<String, String> mScanMap = new ConcurrentHashMap<String, String>();


    private MyOrmSQLiteDaoGeter<PoJoLocalMedia> mMyOrmSQLiteDaoGeter;

    @Override
    public void onCreate() {
        super.onCreate();
        mMyOrmSQLiteDaoGeter = new MyOrmSQLiteDaoGeter<PoJoLocalMedia>();
    }

    /**
     * 是否正在运行
     */
    public static boolean isRunning() {
        ActivityManager manager = (ActivityManager) App.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SERVICE_NAME.equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null)
            parseIntent(intent);

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 解析Intent
     */
    private void parseIntent(final Intent intent) {
        final Bundle arguments = intent.getExtras();
        if (arguments != null) {
            if (arguments.containsKey(EXTRA_DIRECTORY)) {
                String directory = arguments.getString(EXTRA_DIRECTORY);
                L.d(TAG, "directory onStartCommand:" + directory);
                //扫描文件夹
                if (!mScanMap.containsKey(directory))
                    mScanMap.put(directory, "");
            } else if (arguments.containsKey(EXTRA_FILE_PATH)) {
                //单文件
                String filePath = arguments.getString(EXTRA_FILE_PATH);
                L.d(TAG, "file onStartCommand:" + filePath);
                if (!TextUtils.isEmpty(filePath)) {
                    if (!mScanMap.containsKey(filePath))
                        mScanMap.put(filePath, arguments.getString(EXTRA_MIME_TYPE));
                }
            }
        }

        if (mServiceStatus == SCAN_STATUS_NORMAL || mServiceStatus == SCAN_STATUS_END) {
            new Thread(this).start();
        }
    }

    @Override
    public void run() {
        scan();
    }


    /**
     * 扫描
     */
    private void scan() {
        //开始扫描
        notifyObservers(SCAN_STATUS_START, null);

        while (mScanMap.keySet().size() > 0) {
            String path = "";
            for (String key : mScanMap.keySet()) {
                path = key;
                break;
            }
            if (mScanMap.containsKey(path)) {
                String mimeType = mScanMap.get(path);
                if ("".equals(mimeType)) {
                    scanDirectory(path);
                } else {
                    scanFile(path, mimeType);
                }

                L.d(TAG, "......扫描子任务完毕......" + path);
                //扫描完成一个
                mScanMap.remove(path);
                L.d(TAG, "......当前任务数量......" + mScanMap.size());

            }

            //任务之间歇息一秒
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                L.d(TAG, "e:" + e);
            }
        }

        //全部扫描完成
        notifyObservers(SCAN_STATUS_END, null);

        //第一次扫描
        if (App.getSharedPref().getBoolean(PREF_KEY_FIRST, true))
            App.getSharedPref().edit().putBoolean(PREF_KEY_FIRST, false).commit();

        //停止服务
        stopSelf();
    }


    /**
     * 扫描文件
     */
    private void scanFile(String path, String mimeType) {
        L.d(TAG, "......scanFile......path:" + path + " , mimeType:" + mimeType);
        save(new PoJoLocalMedia(path, mimeType));
    }


    /**
     * 扫描文件夹
     */
    private void scanDirectory(String path) {
        L.d(TAG, "......scanDirectory......path:" + path);
        eachAllMedias(new File(path));
    }


    /**
     * 递归查找视频
     */
    private void eachAllMedias(File f) {
        L.d(TAG, "......eachAllMedias......f:" + f);
        if (f != null && f.exists() && f.isDirectory() && !f.getName().startsWith(".") && !LocalMediaManager.needIgnore(f.getName())) {
            File[] files = f.listFiles();
            if (files != null) {
                for (File file : f.listFiles()) {
                    if (file.isDirectory()) {
                        if (!file.getName().startsWith(".")
                                && !file.getName().startsWith("_"))
                            eachAllMedias(file);
                    } else if (file.exists()
                            && !file.getName().startsWith(".")
                            && !file.getName().startsWith("_")
                            && file.canRead()
                            && FileUtils.isVideo(file)) {
                        save(new PoJoLocalMedia(file));
                    }
                }
            }
        }
    }

    /**
     * 保存入库
     *
     * @throws FileNotFoundException
     */
    private void save(PoJoLocalMedia media) {
        //检测
        if (!mMyOrmSQLiteDaoGeter.exists(media)) {
            try {
                if (media.title != null && media.title.length() > 0)
                    media.title_key = PinyinUtils.chineneToSpell(media.title.charAt(0) + "");
            } catch (Exception ex) {
                L.d(TAG, "ex:" + ex);

            }
            media.last_access_time = System.currentTimeMillis();

            //提取缩略图
            extractThumbnail(media);
            media.mime_type = FileUtils.getMimeType(media.path);

            media.category = TitleManager.LOCAL_SUB_TITLE_ENTAIR;
            for (String category : TitleManager.SUBTITLE_DEFAULT_LOCAL) {
                if (media.path.contains(category)) {
                    media.category = category;
                }
            }

            //入库
            mMyOrmSQLiteDaoGeter.create(media);

            L.d(TAG, "......save.......保存文件至数据库......" + media);

            //扫描到一个
            notifyObservers(SCAN_STATUS_RUNNING, media);
        }
    }


    /**
     * 提取生成缩略图
     */
    private void extractThumbnail(PoJoLocalMedia media) {
        L.d(TAG, "......extractThumbnail......media:" + media);
        final Context ctx = App.getContext();
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(ctx, media.path, MediaStore.Video.Thumbnails.MINI_KIND);
        try {
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(ThumbnailUtils.TARGET_SIZE_MINI_THUMBNAIL_WIDTH, ThumbnailUtils.TARGET_SIZE_MINI_THUMBNAIL_HEIGHT, Bitmap.Config.RGB_565);
            }
            media.width = bitmap.getWidth();
            media.height = bitmap.getHeight();
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, DensityUtil.dip2px(ctx, ThumbnailUtils.TARGET_SIZE_MICRO_THUMBNAIL_WIDTH), DensityUtil.dip2px(ctx, ThumbnailUtils.TARGET_SIZE_MICRO_THUMBNAIL_HEIGHT), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            if (bitmap != null) {
                File nailFile = new File(FileUtils.getLocalMediaThumbnailCacheFile(), media.title + "_" + media.title_key + "_thumbnail" + ".jpg");
                media.thumb_path = nailFile.getAbsolutePath();
                L.d(TAG, "..................media.thumb_path.............................." + media.thumb_path);
                FileOutputStream iStream = new FileOutputStream(nailFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, iStream);
                iStream.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            L.d(TAG, "ex:" + ex);
        } finally {
            if (bitmap != null)
                bitmap.recycle();
        }
    }


    // ~~~ 状态改变

    /**
     * 通知状态改变
     */
    private void notifyObservers(int flag, PoJoLocalMedia media) {
        mHandler.sendMessage(mHandler.obtainMessage(flag, media));
    }

    /**
     * 增加观察者
     */
    public void addObserver(IMediaScannerObserver s) {
        synchronized (this) {
            if (!observers.contains(s)) {
                observers.add(s);
            }
        }
    }

    /**
     * 删除观察者
     */
    public synchronized void deleteObserver(IMediaScannerObserver s) {
        observers.remove(s);
    }

    /**
     * 删除所有观察者
     */
    public synchronized void deleteObservers() {
        observers.clear();
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            for (IMediaScannerObserver s : observers) {
                if (s != null) {
                    mServiceStatus = msg.what;
                    s.update(msg.what, (PoJoLocalMedia) msg.obj);
                }
            }
        }
    };


    public interface IMediaScannerObserver {
        /**
         * @param flag  0 开始扫描 1 正在扫描 2 扫描完成
         * @param media 扫描到的视频文件
         */
        public void update(int flag, PoJoLocalMedia media);
    }


    // ~~~ Binder

    private final MediaScannerServiceBinder mBinder = new MediaScannerServiceBinder();

    public class MediaScannerServiceBinder extends Binder {
        public MediaScannerService getService() {
            return MediaScannerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
