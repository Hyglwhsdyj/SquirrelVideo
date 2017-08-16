package com.songshu.squirrelvideo.common;

import io.vov.vitamio.MediaPlayer;

/**
 * Created by yb on 15-7-4.
 */
public class Const {

    private static final String TAG = Const.class.getSimpleName() + ":";

    public static String DOMAIN;

    public static String TITLE;

    public static String CHANNEL;

    public static String SSID;

    public static String PRODUCTID;

    public static String DEVICE;

    public static String MODEL;

    public static int CURRENT_INTENT_VIDEO_TYPE = -1;

    // public static String VIDEO_WEB_API_DEV = "http://dev.v.duole.com";
//     public static String VIDEO_WEB_API_REL = "http://v.duole.com";
//     public static String VIDEO_WEB_API_DEV = "http://v.duole.com";
//    public static final String VIDEO_WEB_API_DEV = "http://118.192.93.50:9020";
//    public static final String VIDEO_WEB_API_REL = "http://118.192.93.50:9020";
    public static final String VIDEO_WEB_API_DEV = "http://yule.songshuyun.com";
    public static final String VIDEO_WEB_API_REL = "http://yule.songshu.cc";


    public static final String APP_NAME = "squirrelvideo";
    public static final String CACHE_NAME = "cache";
    public static final String CACHE_RETROFIT_NAME = "retrofit";
    public static final String CACHE_IMAGE_LOADER_NAME = "imageloader";
    public static final String CACHE_DOWN_LOAD_MANAGER = "downloadmanager";
    public static final String CACHE_LOCAL_MEDIA_THUMBNAIL = "localmediathumbnail";
    public static final String CACHE_LOG = "log";

    public static String INTERNAL_SDCARD;
    public static String EXTERNAL_SDCARD;
    public static String PARENT_SDCARD = "/";


    public static final String IMAGELOADER_LOCAL_MEDIA_PROFIX = "file://";
    public static final String LOCAL_MEDIA_CATEGORY_SEPETER = ":)";

    public static final int TYPE_NO_NETWORK = 1;
    public static final int TYPE_MOBBLE_NETWORK = 2;
    public static final int TYPE_WIFI_NETWORK = 3;


    /**
     * 播放器用到的缓冲大小  默认为1M，单位为byte
     */
    public static final int LARGE_BUFFER_SIZE = 512 * 1024;
    public static final int MIDDLE_BUFFER_SIZE = 256 * 1024;
    public static final int SMALL_BUFFER_SIZE = 128 * 1024;
    public static final float DEFAULT_ASPECT_RATIO = 0f;
    public static final float DEFAULT_STREAM_VOLUME = 1.0f;
    public static final boolean DEFAULT_DEINTERLACE = false;
    public static final int DEFAULT_VIDEO_QUALITY = MediaPlayer.VIDEOQUALITY_LOW;


    public static final String MYWIFEANDIBIRTH = "11200819";
}
