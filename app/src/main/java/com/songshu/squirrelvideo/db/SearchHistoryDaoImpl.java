package com.songshu.squirrelvideo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.DBSearchHistoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yb on 15-7-14.
 */
public class SearchHistoryDaoImpl implements SearchHistoryDao {


    private static final String TAG = SearchHistoryDaoImpl.class.getSimpleName() + ":";

    private DBHelper dbHelper;

    public SearchHistoryDaoImpl(Context mContext) {
        dbHelper = new DBHelper(mContext);
    }

    public SQLiteDatabase openDb() {
        return dbHelper.getWritableDatabase();
    }


    @Override
    public boolean addSearchHistory(DBSearchHistoryBean bean) {
        if (this.isCheckExist(bean.historyTitle, bean.videoChannel)) {
            return false;
        }
        SQLiteDatabase db = null;
        try {
            db = this.openDb();
            ContentValues values = new ContentValues();
            values.put("videoChannel", bean.videoChannel);
            values.put("historyTitle", bean.historyTitle);
            values.put("searchTime", bean.searchTime);
            db.insert("SearchHistory", null, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    @Override
    public boolean updataSearchHistory(DBSearchHistoryBean bean) {
        SQLiteDatabase db = null;
        try {
            db = this.openDb();
            ContentValues values = new ContentValues();
            values.put("videoChannel", bean.videoChannel);
            values.put("historyTitle", bean.historyTitle);
            values.put("searchTime", bean.searchTime);
            db.update("SearchHistory", values, "historyTitle=? and videoChannel=?", new String[]{bean.historyTitle, bean.videoChannel});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    @Override
    public List<DBSearchHistoryBean> findSearchHistorys() {
        List<DBSearchHistoryBean> list = new ArrayList<DBSearchHistoryBean>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String sql = "select * from SearchHistory where videoChannel='" + Const.CHANNEL + "' order by searchTime desc";
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String videoChannel = cursor.getString(cursor.getColumnIndex("videoChannel"));
                String historyTitle = cursor.getString(cursor.getColumnIndex("historyTitle"));
                String searchTime = cursor.getString(cursor.getColumnIndex("searchTime"));

                list.add(new DBSearchHistoryBean(historyTitle, videoChannel, searchTime));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return list;
    }

    @Override
    public boolean deleteSearchHistoryItem(String historyTitle, String videoChannel) {
        SQLiteDatabase db = null;
        try {
            db = this.openDb();
            db.delete("SearchHistory", "historyTitle=? and videoChannel=?", new String[]{historyTitle, videoChannel});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    @Override
    public boolean deleteAllSearchHistory() {
        SQLiteDatabase db = null;
        try {
            db = this.openDb();
            db.delete("SearchHistory", "videoChannel=?", new String[]{Const.CHANNEL});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }


    @Override
    public boolean isTableEmpty() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.openDb();
            cursor = db.query("SearchHistory", null, "videoChannel=?", new String[]{Const.CHANNEL}, null, null, null);
            if (cursor.getCount() > 0) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return true;
    }

    @Override
    public boolean isCheckExist(String historyTitle, String videoChannel) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.openDb();
            cursor = db.query("SearchHistory", null, "historyTitle=? and videoChannel=?", new String[]{historyTitle, videoChannel}, null, null, null);
            if (cursor.getCount() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }


}
