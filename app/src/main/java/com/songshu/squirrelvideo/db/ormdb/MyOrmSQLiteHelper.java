package com.songshu.squirrelvideo.db.ormdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.entity.po.PoJoLocalMedia;
import com.songshu.squirrelvideo.utils.L;

import java.sql.SQLException;

/**
 * Created by yb on 15-10-30.
 */
public class MyOrmSQLiteHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = MyOrmSQLiteHelper.class.getSimpleName() + ":";

    private static final String DATABASE_NAME = "localmedia.db";
    private static final int DATABASE_VERSION = 1;

    public MyOrmSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public MyOrmSQLiteHelper() {
        super(App.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }


    private static MyOrmSQLiteHelper instance;

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized MyOrmSQLiteHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (MyOrmSQLiteHelper.class) {
                if (instance == null)
                    instance = new MyOrmSQLiteHelper(context);
            }
        }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, PoJoLocalMedia.class);
        } catch (SQLException e) {
            L.d(TAG, "e:" + e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, PoJoLocalMedia.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            L.d(TAG, "e:" + e);
        }
    }

}
