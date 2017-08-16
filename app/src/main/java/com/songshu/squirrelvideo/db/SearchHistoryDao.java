package com.songshu.squirrelvideo.db;

import com.songshu.squirrelvideo.entity.DBSearchHistoryBean;

import java.util.List;

/**
 * Created by yb on 15-7-14.
 */
public interface SearchHistoryDao {


    /**
     * 添加
     */
    boolean addSearchHistory(DBSearchHistoryBean bean);

    /**
     * 更新
     */
    boolean updataSearchHistory(DBSearchHistoryBean bean);

    /**
     * 查询
     */
    List<DBSearchHistoryBean> findSearchHistorys();

    /**
     * 删除
     */
    boolean deleteSearchHistoryItem(String historyTitle, String videoChannel);

    /**
     * 删除指定的收藏信息
     */
    boolean deleteAllSearchHistory();

    /**
     * 检查表是否为空
     */
    boolean isTableEmpty();

    /**
     * 检查是否已经存在此信息
     */
    boolean isCheckExist(String historyTitle, String videoChannel);


}
