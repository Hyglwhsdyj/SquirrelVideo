package com.songshu.squirrelvideo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.golshadi.majid.core.enums.TaskStates;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.DBDownloadBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yb on 15-7-14.
 */
public class DownloadDaoImpl implements DownloadDao {

    private static final String TAG = DownloadDaoImpl.class.getSimpleName() + ":";

    private SQLiteDatabase db;


    public DownloadDaoImpl() {
        db = App.getmDBHelper().getWritableDatabase();
    }


    @Override
    public boolean addDownload(DBDownloadBean bean) {
        if (this.isCheckExist(bean.videoId, bean.videoChannel)) return false;
        try {
            ContentValues values = new ContentValues();
            values.put("videoId", bean.videoId);
            values.put("videoChannel", bean.videoChannel);
            values.put("videoName", bean.videoName);
            values.put("videoPosterUrl", bean.videoPosterUrl);
            values.put("videoSegment", bean.videoSegment);
            values.put("videoNetUrl", bean.videoNetUrl);
            values.put("videoLocalUrl", bean.videoLocalUrl);
            values.put("videoDownloadState", bean.videoDownloadState);
            values.put("videoTaskId", bean.videoTaskId);
            values.put("videoDownDate", bean.videoDownDate);
            values.put("videoCurrentProcess", bean.videoCurrentProcess);
            values.put("videoHtmlUrl", bean.videoHtmlUrl);
            values.put("videoWhichEpisode", bean.videoWhichEpisode);
            db.insert("Download", null, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return false;
    }

    @Override
    public List<DBDownloadBean> findAllDownloadingDownload() {
        List<DBDownloadBean> list = new ArrayList<DBDownloadBean>();
        Cursor cursor = null;
        try {
            String sql = "select * from Download where videoDownloadState in (" + TaskStates.DOWNLOADING + "," + TaskStates.INIT + "," + TaskStates.READY + ") and videoChannel='" + Const.CHANNEL + "' order by videoDownDate desc";
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String videoId = cursor.getString(cursor.getColumnIndex("videoId"));
                String videoChannel = cursor.getString(cursor.getColumnIndex("videoChannel"));
                String videoName = cursor.getString(cursor.getColumnIndex("videoName"));
                String videoPosterUrl = cursor.getString(cursor.getColumnIndex("videoPosterUrl"));
                int videoSegment = cursor.getInt(cursor.getColumnIndex("videoSegment"));
                String videoNetUrl = cursor.getString(cursor.getColumnIndex("videoNetUrl"));
                String videoLocalUrl = cursor.getString(cursor.getColumnIndex("videoLocalUrl"));
                int videoDownloadState = cursor.getInt(cursor.getColumnIndex("videoDownloadState"));
                String videoTaskId = cursor.getString(cursor.getColumnIndex("videoTaskId"));
                long videoDownDate = cursor.getLong(cursor.getColumnIndex("videoDownDate"));
                int videoCurrentProcess = cursor.getInt(cursor.getColumnIndex("videoCurrentProcess"));
                String videoHtmlUrl = cursor.getString(cursor.getColumnIndex("videoHtmlUrl"));
                String videoWhichEpisode = cursor.getString(cursor.getColumnIndex("videoWhichEpisode"));

                list.add(new DBDownloadBean(videoId, videoChannel, videoName, videoPosterUrl, videoSegment, videoNetUrl, videoLocalUrl, videoDownloadState, videoTaskId, videoDownDate, videoCurrentProcess, videoHtmlUrl,videoWhichEpisode));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }


    @Override
    public int findAllDownloadingDownloadNums() {
        int nums = 0;
        Cursor cursor = null;
        try {
            cursor = db.query(true, "Download", null, "videoDownloadState=? and videoChannel=?", new String[]{TaskStates.DOWNLOADING + "", Const.CHANNEL}, null, null, null, null);
            nums = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return nums;
    }


    @Override
    public List<DBDownloadBean> findDownload() {
        List<DBDownloadBean> list = new ArrayList<DBDownloadBean>();
        Cursor cursor = null;
        try {
            String sql = "select * from Download where videoChannel='" + Const.CHANNEL + "' order by videoDownDate desc";
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String videoId = cursor.getString(cursor.getColumnIndex("videoId"));
                String videoChannel = cursor.getString(cursor.getColumnIndex("videoChannel"));
                String videoName = cursor.getString(cursor.getColumnIndex("videoName"));
                String videoPosterUrl = cursor.getString(cursor.getColumnIndex("videoPosterUrl"));
                int videoSegment = cursor.getInt(cursor.getColumnIndex("videoSegment"));
                String videoNetUrl = cursor.getString(cursor.getColumnIndex("videoNetUrl"));
                String videoLocalUrl = cursor.getString(cursor.getColumnIndex("videoLocalUrl"));
                int videoDownloadState = cursor.getInt(cursor.getColumnIndex("videoDownloadState"));
                String videoTaskId = cursor.getString(cursor.getColumnIndex("videoTaskId"));
                long videoDownDate = cursor.getLong(cursor.getColumnIndex("videoDownDate"));
                int videoCurrentProcess = cursor.getInt(cursor.getColumnIndex("videoCurrentProcess"));
                String videoHtmlUrl = cursor.getString(cursor.getColumnIndex("videoHtmlUrl"));
                String videoWhichEpisode = cursor.getString(cursor.getColumnIndex("videoWhichEpisode"));

                list.add(new DBDownloadBean(videoId, videoChannel, videoName, videoPosterUrl, videoSegment, videoNetUrl, videoLocalUrl, videoDownloadState, videoTaskId, videoDownDate, videoCurrentProcess, videoHtmlUrl,videoWhichEpisode));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    @Override
    public DBDownloadBean findDownload(String id, String channel) {
        Cursor cursor = null;
        try {
            cursor = db.query(true, "Download", null, "videoId=? and videoChannel=?", new String[]{id, channel}, null, null, null, "1");
            while (cursor.moveToNext()) {
                String videoId = cursor.getString(cursor.getColumnIndex("videoId"));
                String videoChannel = cursor.getString(cursor.getColumnIndex("videoChannel"));
                String videoName = cursor.getString(cursor.getColumnIndex("videoName"));
                String videoPosterUrl = cursor.getString(cursor.getColumnIndex("videoPosterUrl"));
                int videoSegment = cursor.getInt(cursor.getColumnIndex("videoSegment"));
                String videoNetUrl = cursor.getString(cursor.getColumnIndex("videoNetUrl"));
                String videoLocalUrl = cursor.getString(cursor.getColumnIndex("videoLocalUrl"));
                int videoDownloadState = cursor.getInt(cursor.getColumnIndex("videoDownloadState"));
                String videoTaskId = cursor.getString(cursor.getColumnIndex("videoTaskId"));
                long videoDownDate = cursor.getLong(cursor.getColumnIndex("videoDownDate"));
                int videoCurrentProcess = cursor.getInt(cursor.getColumnIndex("videoCurrentProcess"));
                String videoHtmlUrl = cursor.getString(cursor.getColumnIndex("videoHtmlUrl"));
                String videoWhichEpisode = cursor.getString(cursor.getColumnIndex("videoWhichEpisode"));
                return new DBDownloadBean(videoId, videoChannel, videoName, videoPosterUrl, videoSegment, videoNetUrl, videoLocalUrl, videoDownloadState, videoTaskId, videoDownDate, videoCurrentProcess, videoHtmlUrl,videoWhichEpisode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    @Override
    public DBDownloadBean findDownload(String oriUrl) {
        Cursor cursor = null;
        try {
            cursor = db.query(true, "Download", null, "videoChannel=? and videoHtmlUrl=?", new String[]{Const.CHANNEL, oriUrl}, null, null, null, "1");
            while (cursor.moveToNext()) {
                String videoId = cursor.getString(cursor.getColumnIndex("videoId"));
                String videoChannel = cursor.getString(cursor.getColumnIndex("videoChannel"));
                String videoName = cursor.getString(cursor.getColumnIndex("videoName"));
                String videoPosterUrl = cursor.getString(cursor.getColumnIndex("videoPosterUrl"));
                int videoSegment = cursor.getInt(cursor.getColumnIndex("videoSegment"));
                String videoNetUrl = cursor.getString(cursor.getColumnIndex("videoNetUrl"));
                String videoLocalUrl = cursor.getString(cursor.getColumnIndex("videoLocalUrl"));
                int videoDownloadState = cursor.getInt(cursor.getColumnIndex("videoDownloadState"));
                String videoTaskId = cursor.getString(cursor.getColumnIndex("videoTaskId"));
                long videoDownDate = cursor.getLong(cursor.getColumnIndex("videoDownDate"));
                int videoCurrentProcess = cursor.getInt(cursor.getColumnIndex("videoCurrentProcess"));
                String videoHtmlUrl = cursor.getString(cursor.getColumnIndex("videoHtmlUrl"));
                String videoWhichEpisode = cursor.getString(cursor.getColumnIndex("videoWhichEpisode"));
                return new DBDownloadBean(videoId, videoChannel, videoName, videoPosterUrl, videoSegment, videoNetUrl, videoLocalUrl, videoDownloadState, videoTaskId, videoDownDate, videoCurrentProcess, videoHtmlUrl,videoWhichEpisode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    @Override
    public DBDownloadBean findDownloadByTaskId(String taskId) {
        Cursor cursor = null;
        try {
            cursor = db.query(true, "Download", null, "videoChannel=? and videoTaskId LIKE ?", new String[]{Const.CHANNEL, "%" + taskId + "%"}, null, null, null, "1");
            while (cursor.moveToNext()) {
                String videoId = cursor.getString(cursor.getColumnIndex("videoId"));
                String videoChannel = cursor.getString(cursor.getColumnIndex("videoChannel"));
                String videoName = cursor.getString(cursor.getColumnIndex("videoName"));
                String videoPosterUrl = cursor.getString(cursor.getColumnIndex("videoPosterUrl"));
                int videoSegment = cursor.getInt(cursor.getColumnIndex("videoSegment"));
                String videoNetUrl = cursor.getString(cursor.getColumnIndex("videoNetUrl"));
                String videoLocalUrl = cursor.getString(cursor.getColumnIndex("videoLocalUrl"));
                int videoDownloadState = cursor.getInt(cursor.getColumnIndex("videoDownloadState"));
                String videoTaskId = cursor.getString(cursor.getColumnIndex("videoTaskId"));
                long videoDownDate = cursor.getLong(cursor.getColumnIndex("videoDownDate"));
                int videoCurrentProcess = cursor.getInt(cursor.getColumnIndex("videoCurrentProcess"));
                String videoHtmlUrl = cursor.getString(cursor.getColumnIndex("videoHtmlUrl"));
                String videoWhichEpisode = cursor.getString(cursor.getColumnIndex("videoWhichEpisode"));
                return new DBDownloadBean(videoId, videoChannel, videoName, videoPosterUrl, videoSegment, videoNetUrl, videoLocalUrl, videoDownloadState, videoTaskId, videoDownDate, videoCurrentProcess, videoHtmlUrl,videoWhichEpisode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    @Override
    public int findDownloadByHtmlUrl(String videoHtmlUrl){
        Cursor cursor = null;
        try {
            cursor = db.query(true, "Download", null, "videoChannel=? and videoHtmlUrl=?", new String[]{Const.CHANNEL, videoHtmlUrl}, null, null, null, "1");
            while (cursor.moveToNext()) {
                return cursor.getInt(cursor.getColumnIndex("videoDownloadState"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return TaskStates.NOINCLUDE;
    }


    @Override
    public boolean updateDownload(DBDownloadBean bean) {
        try {
            ContentValues values = new ContentValues();
            values.put("videoId", bean.videoId);
            values.put("videoChannel", bean.videoChannel);
            values.put("videoName", bean.videoName);
            values.put("videoSegment", bean.videoSegment);
            values.put("videoNetUrl", bean.videoNetUrl);
            values.put("videoLocalUrl", bean.videoLocalUrl);
            values.put("videoDownloadState", bean.videoDownloadState);
            values.put("videoTaskId", bean.videoTaskId);
            values.put("videoDownDate", bean.videoDownDate);
            values.put("videoCurrentProcess", bean.videoCurrentProcess);
            values.put("videoHtmlUrl", bean.videoHtmlUrl);
            values.put("videoWhichEpisode", bean.videoWhichEpisode);
            db.update("Download", values, "videoId=? and videoChannel=?", new String[]{bean.videoId, bean.videoChannel});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return false;
    }


    @Override
    public boolean deleteDownload(String videoId, String videoChannel) {
        try {
            db.delete("Download", "videoId=? and videoChannel=?", new String[]{videoId, videoChannel});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return false;
    }


    @Override
    public boolean isTableEmpty() {
        Cursor cursor = null;
        try {
            cursor = db.query("Download", null, "videoChannel=?", new String[]{Const.CHANNEL}, null, null, null);
            if (cursor.getCount() > 0) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return true;
    }


    @Override
    public boolean isCheckExist(String videoId, String videoChannel) {
        Cursor cursor = null;
        try {
            cursor = db.query("Download", null, "videoId=? and videoChannel=?", new String[]{videoId, videoChannel}, null, null, null);
            if (cursor.getCount() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }
}
