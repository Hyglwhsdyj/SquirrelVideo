package com.songshu.squirrelvideo.db;

import com.songshu.squirrelvideo.entity.DBHistoryBean;

import java.util.List;

/**
 * Created by yb on 15-7-14.
 */
public interface HistoryDao {

    /**
     * 添加历史记录
     */
    boolean addHistory(DBHistoryBean bean);
    /**
     * 查询历史记录
     */
    List<DBHistoryBean> findHistory();

    /**
     * 根据指定id和频道查询
     * @param id
     * @param channel
     * @return
     */
    DBHistoryBean findHistory(String id, String channel);
    /**
     * 删除历史记录
     */
    boolean deleteHistorys(List<DBHistoryBean> list);
    /**
     * 删除指定的历史
     * @param videoId
     * @param videoChannel
     * @return
     */
    boolean deleteHistory(String videoId, String videoChannel);
    /**
     * 更改历史记录
     */
    boolean updateHistory(DBHistoryBean bean);
    /**
     * 检查表是否为空
     */
    boolean isTableEmpty();
    /**
     * 检查是否已经存在此信息
     */
    boolean isCheckExist(String videoId, String videoChannel);
}
