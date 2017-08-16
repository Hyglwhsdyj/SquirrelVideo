package com.songshu.squirrelvideo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yb on 15-7-14.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName() + ":";

    private static final String DB_NAME = "squirrel_video.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // 收藏表
    private static final String sql1 = "CREATE TABLE IF NOT EXISTS Favoriets(" +
            "_id integer primary key autoincrement," +
            "videoName string, " +
            "videoType string, " +
            "videoId string, " +
            "videoPic string, " +
            "videoChannel string," +
            "videoEpisode string)";


    // 历史记录表
    private static final String sql2 = "CREATE TABLE IF NOT EXISTS History(" +
            "_id integer primary key autoincrement," + // 索引
            "videoName string, " + // 视频名称
            "videoType string, " + // 视频类型
            "videoId string, " + //视频ID
            "videoPic string, " + //
            "videoChannel string, " + // 频道
            "videoEpisode string, " + // 观看集数
            "time string, " + //观看时间
            "isComplete string, " + //是否看完 0未完成,1完成
            "videoIndex string, " + // 观看到第几集
            "watchDate string)"; // 记录观看时间 便于排序


    // 搜索记录表
    private static final String sql3 = "CREATE TABLE IF NOT EXISTS SearchHistory(" +
            "_id integer primary key autoincrement," +
            "videoChannel string, " +
            "historyTitle string, " +
            "searchTime string)";


    // 下载记录表
    private static final String sql4 = "CREATE TABLE IF NOT EXISTS Download(" +
            "_id integer primary key autoincrement," +
            "videoId string, " +
            "videoChannel string, " +
            "videoName string, " +
            "videoPosterUrl string, " +
            "videoSegment string, " +
            "videoNetUrl string, " +
            "videoLocalUrl string, " +
            "videoDownloadState string, " +
            "videoTaskId string, " +
            "videoDownDate string, " +
            "videoCurrentProcess string, " +
            "videoHtmlUrl string, " +
            "videoWhichEpisode string)";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
        db.execSQL(sql4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "Favoriets");
        db.execSQL("DROP TABLE IF EXISTS " + "History");
        db.execSQL("DROP TABLE IF EXISTS " + "SearchHistory");
        db.execSQL("DROP TABLE IF EXISTS " + "Download");
        onCreate(db);
    }
}
