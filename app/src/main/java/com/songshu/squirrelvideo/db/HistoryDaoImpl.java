package com.songshu.squirrelvideo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.DBHistoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yb on 15-7-14.
 */
public class HistoryDaoImpl implements HistoryDao {

    private static final String TAG = HistoryDaoImpl.class.getSimpleName() + ":";

//    private DBHelper dbHelper;
//
//    public HistoryDaoImpl(Context mContext) {
//        dbHelper = new DBHelper(mContext);
//    }
//
//    public SQLiteDatabase openDb() {
//        return dbHelper.getWritableDatabase();
//    }


    private SQLiteDatabase db;


    public HistoryDaoImpl(Context mContext) {
        db = App.getmDBHelper().getWritableDatabase();
    }


    @Override
    public boolean addHistory(DBHistoryBean bean) {
        if (this.isCheckExist(bean.getVideoId(), bean.getVideoChannel())) return false;
//        SQLiteDatabase db = null;
        try {
//            db = this.openDb();
            ContentValues values = new ContentValues();
            values.put("videoName", bean.getVideoName());
            values.put("videoType", bean.getVideoType());
            values.put("videoId", bean.getVideoId());
            values.put("videoPic", bean.getVideoPic());
            values.put("videoChannel", bean.getVideoChannel());
            values.put("videoEpisode", bean.getVideoEpisode());
            values.put("time", bean.getTime());
            values.put("isComplete", bean.getIsComplete());
            values.put("videoIndex", bean.getVideoIndex());
            values.put("watchDate", bean.getWatchDate());
            db.insert("History", null, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            if (db != null) {
//                db.close();
//            }
        }
        return false;
    }

    @Override
    public List<DBHistoryBean> findHistory() {
        List<DBHistoryBean> list = new ArrayList<DBHistoryBean>();
//        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
//            db = dbHelper.getReadableDatabase();
            String sql = "select * from History where videoChannel='" + Const.CHANNEL + "' order by watchDate desc";
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String videoName = cursor.getString(cursor.getColumnIndex("videoName"));
                String videoType = cursor.getString(cursor.getColumnIndex("videoType"));
                String videoId = cursor.getString(cursor.getColumnIndex("videoId"));
                String videoPic = cursor.getString(cursor.getColumnIndex("videoPic"));
                String videoChannel = cursor.getString(cursor.getColumnIndex("videoChannel"));
                String videoEpisode = cursor.getString(cursor.getColumnIndex("videoEpisode"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String isComplete = cursor.getString(cursor.getColumnIndex("isComplete"));
                String videoIndex = cursor.getString(cursor.getColumnIndex("videoIndex"));
                String watchDate = cursor.getString(cursor.getColumnIndex("watchDate"));

                list.add(new DBHistoryBean(videoName, videoType, videoId, videoPic, videoChannel, videoEpisode, time, isComplete, videoIndex, watchDate));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
//            if (db != null) {
//                db.close();
//            }
        }
        return list;
    }

    @Override
    public DBHistoryBean findHistory(String id, String channel) {
//        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
//            db = dbHelper.getReadableDatabase();
            cursor = db.query(true, "History", null, "videoId=? and videoChannel=?", new String[]{id, channel}, null, null, null, "1");
            while (cursor.moveToNext()) {
                String videoName = cursor.getString(cursor.getColumnIndex("videoName"));
                String videoType = cursor.getString(cursor.getColumnIndex("videoType"));
                String videoId = cursor.getString(cursor.getColumnIndex("videoId"));
                String videoPic = cursor.getString(cursor.getColumnIndex("videoPic"));
                String videoChannel = cursor.getString(cursor.getColumnIndex("videoChannel"));
                String videoEpisode = cursor.getString(cursor.getColumnIndex("videoEpisode"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String isComplete = cursor.getString(cursor.getColumnIndex("isComplete"));
                String videoIndex = cursor.getString(cursor.getColumnIndex("videoIndex"));
                String watchDate = cursor.getString(cursor.getColumnIndex("watchDate"));

                return new DBHistoryBean(videoName, videoType, videoId, videoPic, videoChannel, videoEpisode, time, isComplete, videoIndex, watchDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
//            if (db != null) {
//                db.close();
//            }
        }
        return null;
    }

    @Override
    public boolean deleteHistorys(List<DBHistoryBean> list) {
//        SQLiteDatabase db = null;
        try {
//            db = this.openDb();
//            db.beginTransaction();
            for (int i = 0; i < list.size(); i++) {
                db.delete("History", "videoId=? and videoChannel=?", new String[]{list.get(i).getVideoId(), list.get(i).getVideoChannel()});
            }
//            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            if (db != null) {
//                db.endTransaction();
//                db.close();
//            }
        }
        return false;
    }

    @Override
    public boolean deleteHistory(String videoId, String videoChannel) {
//        SQLiteDatabase db = null;
        try {
//            db = this.openDb();
            db.delete("History", "videoId=? and videoChannel=?", new String[]{videoId, videoChannel});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            if (db != null) {
//                db.close();
//            }
        }
        return false;
    }

    @Override
    public boolean updateHistory(DBHistoryBean bean) {
//        SQLiteDatabase db = null;
        try {
//            db = this.openDb();

            ContentValues values = new ContentValues();
            values.put("videoName", bean.getVideoName());
            values.put("videoType", bean.getVideoType());
            values.put("videoId", bean.getVideoId());
            values.put("videoPic", bean.getVideoPic());
            values.put("videoChannel", bean.getVideoChannel());
            values.put("videoEpisode", bean.getVideoEpisode());
            values.put("time", bean.getTime());
            values.put("isComplete", bean.getIsComplete());
            values.put("videoIndex", bean.getVideoIndex());
            values.put("watchDate", bean.getWatchDate());
            db.update("History", values, "videoId=? and videoChannel=?", new String[]{bean.getVideoId(), bean.getVideoChannel()});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            if (db != null) {
//                db.close();
//            }
        }
        return false;
    }

    @Override
    public boolean isTableEmpty() {
//        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
//            db = this.openDb();
            cursor = db.query("History", null, "videoChannel=?", new String[]{Const.CHANNEL}, null, null, null);
            if (cursor.getCount() > 0) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
//            if (db != null) {
//                db.close();
//            }
        }
        return true;
    }

    @Override
    public boolean isCheckExist(String videoId, String videoChannel) {
//        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
//            db = this.openDb();
            cursor = db.query("History", null, "videoId=? and videoChannel=?", new String[]{videoId, videoChannel}, null, null, null);
            if (cursor.getCount() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
//            if (db != null) {
//                db.close();
//            }
        }
        return false;
    }
}
