package com.songshu.squirrelvideo.db;

import android.database.Cursor;

import com.golshadi.majid.core.enums.TaskStates;
import com.songshu.squirrelvideo.entity.DBDownloadBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yb on 15-7-14.
 */
public interface DownloadDao {


    /**
     * 添加
     */
    boolean addDownload(DBDownloadBean bean);


    /**
     * 查询
     *
     * @param videoId
     * @param channel
     * @return
     */
    DBDownloadBean findDownload(String videoId, String channel);

    /**
     * 查询
     *
     * @param videoHtmlUrl
     * @return
     */
    DBDownloadBean findDownload(String videoHtmlUrl);

    /**
     * 根据HtmlUrl地址去找任务的状态
     * @param videoHtmlUrl
     * @return
     */
    int findDownloadByHtmlUrl(String videoHtmlUrl);


    /**
     * 更新
     */
    boolean updateDownload(DBDownloadBean bean);


    /**
     * 删除
     *
     * @param videoId
     * @param videoChannel
     * @return
     */
    boolean deleteDownload(String videoId, String videoChannel);


    /**
     * 检查表是否为空
     */
    boolean isTableEmpty();


    /**
     * 检查是否已经存在此信息
     */
    boolean isCheckExist(String videoId, String videoChannel);


    /**
     * 根据taskid查找对象
     *
     * @param taskId
     * @return
     */
    DBDownloadBean findDownloadByTaskId(String taskId);

    /**
     * 查找所有任务
     *
     * @return
     */
    List<DBDownloadBean> findDownload();


    /**
     * 查找所有正在下载的任务
     * @return
     */
    List<DBDownloadBean> findAllDownloadingDownload();


    /**
     * 返回数据库中正在处理下载状态的任务数量
     * @return
     */
    int findAllDownloadingDownloadNums();
}
