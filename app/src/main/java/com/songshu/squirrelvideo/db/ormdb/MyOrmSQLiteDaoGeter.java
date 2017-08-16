package com.songshu.squirrelvideo.db.ormdb;

/**
 * Created by yb on 15-10-30.
 */

import android.content.ContentValues;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.po.PoJoLocalMedia;
import com.songshu.squirrelvideo.utils.FileUtils;
import com.songshu.squirrelvideo.utils.L;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class MyOrmSQLiteDaoGeter<T> {

    private static final String TAG = MyOrmSQLiteDaoGeter.class.getSimpleName() + ":";

    /**
     * 新增一条记录
     */
    public int create(T po) {
        try {
            Dao dao = MyOrmSQLiteHelper.getHelper(App.getContext()).getDao(po.getClass());
            return dao.create(po);
        } catch (SQLException e) {
            L.d(TAG, "e:" + e);
        }
        return -1;
    }


    /**
     * 根据DAO查询满足所有数据库列的记录是否存在
     *
     * @param po
     * @param where
     * @return
     */
    public boolean exists(T po, Map<String, Object> where) {
        try {
            Dao dao = MyOrmSQLiteHelper.getHelper(App.getContext()).getDao(po.getClass());
            if (dao.queryForFieldValues(where).size() > 0) {
                return true;
            }
        } catch (SQLException e) {
            L.d(TAG, "e:" + e);
        }
        return false;
    }


    /**
     * 根据DAO查询满足所有数据库列的记录是否存在
     *
     * @param po
     * @return
     */
    public boolean exists(T po) {
        try {
            PoJoLocalMedia media = (PoJoLocalMedia) po;
            Dao dao = MyOrmSQLiteHelper.getHelper(App.getContext()).getDao(po.getClass());
            List<PoJoLocalMedia> mediaList = dao.queryBuilder().where().eq("title", media.title)
                    .and().eq("title", media.title)
                    .and().eq("last_modify_time", media.last_modify_time)
                    .and().eq("file_size", media.file_size)
                    .query();
            if (mediaList != null && mediaList.size() > 0) {
                return true;
            }
        } catch (SQLException e) {
            L.d(TAG, "e:" + e);
        }
        return false;
    }


    /**
     * 查询记录,如果没有则插入
     *
     * @param po
     * @param where
     * @return
     */
    public int createIfNotExists(T po, Map<String, Object> where) {
        try {
            Dao dao = MyOrmSQLiteHelper.getHelper(App.getContext()).getDao(po.getClass());
            if (dao.queryForFieldValues(where).size() < 1) {
                return dao.create(po);
            }
        } catch (SQLException e) {
            L.d(TAG, "e:" + e);
        }
        return -1;
    }


    /**
     * 查询一条记录
     */
    public List<T> queryForEq(Class<T> c, String fieldName, Object value) {
        try {
            Dao dao = MyOrmSQLiteHelper.getHelper(App.getContext()).getDao(c);
            return dao.queryForEq(fieldName, value);
        } catch (SQLException e) {
            L.d(TAG, "e:" + e);
        }
        return new ArrayList<T>();
    }


    /**
     * 查询一条记录
     */
    public List<T> queryForEqByEq(Class<T> c, String fieldName, Object value, String orderByName) {
        try {
            Dao dao = MyOrmSQLiteHelper.getHelper(App.getContext()).getDao(c);
            if (!FileUtils.getEverySDPath(App.getContext()).contains(Const.EXTERNAL_SDCARD)) {
                if (!FileUtils.getEverySDPath(App.getContext()).contains(Const.INTERNAL_SDCARD)) {
                    return new ArrayList<T>();
                } else {
                    return dao.queryBuilder()
                            .orderBy(orderByName, true)
                            .where().eq("sdcard_name", Const.INTERNAL_SDCARD).and().eq(fieldName, value)
                            .query();
                }
            } else {
                if (!FileUtils.getEverySDPath(App.getContext()).contains(Const.INTERNAL_SDCARD)) {
                    return dao.queryBuilder()
                            .orderBy(orderByName, true)
                            .where().eq("sdcard_name", Const.EXTERNAL_SDCARD).and().eq(fieldName, value)
                            .query();
                } else {
                    return dao.queryBuilder()
                            .orderBy(orderByName, true)
                            .where().eq(fieldName, value)
                            .query();
                }
            }
        } catch (SQLException e) {
            L.d(TAG, "e:" + e);
        }
        return new ArrayList<T>();
    }

    /**
     * 删除一条记录
     */
    public int remove(T po) {
        try {
            Dao dao = MyOrmSQLiteHelper.getHelper(App.getContext()).getDao(po.getClass());
            return dao.delete(po);
        } catch (SQLException e) {
            L.d(TAG, "e:" + e);
        }
        return -1;
    }


    /**
     * 根据特定条件更新特定字段
     *
     * @param c
     * @param values
     * @param columnName where字段
     * @param value      where值
     * @return
     */
    public int update(Class<T> c, ContentValues values, String columnName, Object value) {
        try {
            Dao dao = MyOrmSQLiteHelper.getHelper(App.getContext()).getDao(c);
            UpdateBuilder<T, Long> updateBuilder = dao.updateBuilder();
            updateBuilder.where().eq(columnName, value);
            for (String key : values.keySet()) {
                updateBuilder.updateColumnValue(key, values.get(key));
            }
            return updateBuilder.update();
        } catch (SQLException e) {
            L.d(TAG, "e:" + e);
        }
        return -1;
    }


    /**
     * 更新一条记录
     */
    public int update(T po) {
        try {
            Dao dao = MyOrmSQLiteHelper.getHelper(App.getContext()).getDao(po.getClass());
            return dao.update(po);
        } catch (SQLException e) {
            L.d(TAG, "e:" + e);
        }
        return -1;
    }


    /**
     * 查询所有记录
     */
    public List<T> queryForAll(Class<T> c) {
        try {
            Dao dao = MyOrmSQLiteHelper.getHelper(App.getContext()).getDao(c);
            return dao.queryForAll();
        } catch (SQLException e) {
            L.d(TAG, "e:" + e);
        }
        return new ArrayList<T>();
    }

    /**
     * 查询所有记录
     */
    public List<T> queryForAllByOrder(Class<T> c, String order) {
        try {
            Dao dao = MyOrmSQLiteHelper.getHelper(App.getContext()).getDao(c);
            if (!FileUtils.getEverySDPath(App.getContext()).contains(Const.EXTERNAL_SDCARD)) {
                if (!FileUtils.getEverySDPath(App.getContext()).contains(Const.INTERNAL_SDCARD)) {
                    return new ArrayList<T>();
                } else {
                    return dao.queryBuilder().orderBy(order, true).where().eq("sdcard_name", Const.INTERNAL_SDCARD).query();
                }
            } else {
                if (!FileUtils.getEverySDPath(App.getContext()).contains(Const.INTERNAL_SDCARD)) {
                    return dao.queryBuilder().orderBy(order, true).where().eq("sdcard_name", Const.EXTERNAL_SDCARD).query();
                } else {
                    return dao.queryBuilder().orderBy(order, true).query();
                }
            }
        } catch (SQLException e) {
            L.d(TAG, "e:" + e);
        }
        return new ArrayList<T>();
    }
}
