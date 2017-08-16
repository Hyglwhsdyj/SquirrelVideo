package com.songshu.squirrelvideo.db;

import com.songshu.squirrelvideo.entity.DBFavoritesBean;

import java.util.List;

/**
 * Created by yb on 15-7-14.
 */
public interface FavorietsDao {

    /*索引	视频名称	    视频类型	  视频ID	    更新集数	        观看时间	  频道	    观看集数
    Index	VideoName	Type	  videoID	newVideoIndex	Time	  Channel	videoIndex*/

    /**
     * 添加收藏信息
     */
    boolean addFavoriets(DBFavoritesBean bean);

    /**
     * 查询收藏信息
     */
    List<DBFavoritesBean> findFavoriets();

    /**
     * 删除收藏信息
     */
    boolean deleteFavoriets(List<DBFavoritesBean> list);

    /**
     * 删除指定的收藏信息
     * @param videoId
     * @param videoChannel
     * @return
     */
    boolean deleteFavoriets(String videoId, String videoChannel);

    /**
     * 检查表是否为空
     */
    boolean isTableEmpty();

    /**
     * 检查是否已经存在此信息
     */
    boolean isCheckExist(String videoId, String videoChannel);


}
