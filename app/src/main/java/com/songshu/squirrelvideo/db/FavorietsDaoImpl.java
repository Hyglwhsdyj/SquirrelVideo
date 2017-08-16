package com.songshu.squirrelvideo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.DBFavoritesBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yb on 15-7-14.
 */
public class FavorietsDaoImpl implements FavorietsDao {

    /*索引	视频名称	    视频类型	  视频ID	    更新集数	        观看时间	  频道	    观看集数
    Index	VideoName	Type	  videoID	newVideoIndex	Time	  Channel	videoIndex*/

    private static final String TAG = FavorietsDaoImpl.class.getSimpleName() + ":";

//    private DBHelper dbHelper;
//
//    public FavorietsDaoImpl(Context mContext) {
//        dbHelper = new DBHelper(mContext);
//    }

    private SQLiteDatabase db;


    public FavorietsDaoImpl(Context mContext) {
        db = App.getmDBHelper().getWritableDatabase();
    }


//    /**
//     * 打开数据库
//     *
//     * @return
//     */
//    public SQLiteDatabase openDb() {
//        return dbHelper.getWritableDatabase();
//    }

    @Override
    public boolean addFavoriets(DBFavoritesBean bean) {
        // 添加前，先判断下数据库中是否已存在此信息
        if (this.isCheckExist(bean.getVideoId(), bean.getVideoChannel())) {
            return false;
        }
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
            db.insert("Favoriets", null, values);
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
    public List<DBFavoritesBean> findFavoriets() {
        List<DBFavoritesBean> list = new ArrayList<DBFavoritesBean>();
//        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
//            db = dbHelper.getReadableDatabase();
            String sql = "select * from Favoriets where videoChannel='" + Const.CHANNEL+"'";
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String videoId = cursor.getString(cursor.getColumnIndex("videoId"));
                String videoName = cursor.getString(cursor.getColumnIndex("videoName"));
                String videoType = cursor.getString(cursor.getColumnIndex("videoType"));
                String videoPic = cursor.getString(cursor.getColumnIndex("videoPic"));
                String videoChannel = cursor.getString(cursor.getColumnIndex("videoChannel"));
                String videoEpisode = cursor.getString(cursor.getColumnIndex("videoEpisode"));

                list.add(new DBFavoritesBean(videoId, videoName, videoType, videoPic, videoChannel, videoEpisode));
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
    public boolean deleteFavoriets(List<DBFavoritesBean> list) {
//        SQLiteDatabase db = null;
        try {
//            db = this.openDb();
//            db.beginTransaction();
            for (int i = 0; i < list.size(); i++) {
                db.delete("Favoriets", "videoId=? and videoChannel=?", new String[]{list.get(i).getVideoId(), list.get(i).getVideoChannel()});
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
    public boolean deleteFavoriets(String videoId, String videoChannel) {
//        SQLiteDatabase db = null;
        try {
//            db = this.openDb();
            db.delete("Favoriets", "videoId=? and videoChannel=?", new String[]{videoId, videoChannel});
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
    public boolean isTableEmpty() {
//        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
//            db = this.openDb();
            cursor = db.query("Favoriets", null, "videoChannel=?", new String[]{Const.CHANNEL}, null, null, null);
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
            cursor = db.query("Favoriets", null, "videoId=? and videoChannel=?", new String[]{videoId, videoChannel}, null, null, null);
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
