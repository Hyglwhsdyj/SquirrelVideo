package com.songshu.squirrelvideo.common;

import com.songshu.squirrelvideo.entity.HotSearchBeanNetData;
import com.songshu.squirrelvideo.entity.MovieDetailNetData;
import com.songshu.squirrelvideo.entity.MovieRecommNetData;
import com.songshu.squirrelvideo.entity.MovieSelectionNetData;
import com.songshu.squirrelvideo.entity.ParentVideoNetData;
import com.songshu.squirrelvideo.entity.SearchResultBeanNetData;
import com.songshu.squirrelvideo.entity.TeleplayDetailNetData;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by yb on 15-7-4.
 */
public interface IAppApi {

    @GET("/api/recommend/get_channel_recom_videos")
    MovieRecommNetData getMovieRecomm(@Query("channel") String channel, @Query("limit") String limit);

    @GET("/api/video/get_video_list")
    MovieSelectionNetData getMovieSelection(@Query("channel") String channel, @Query("type") String type, @Query("area") String area, @Query("year") String year, @Query("page") int page);

    @GET("/api/video/get_video_info")
    MovieDetailNetData getMovieDetail(@Query("channel") String channel, @Query("video_id") int video_id, @Query("show_resource") String show_resource);

    @GET("/api/video/get_video_info")
    TeleplayDetailNetData getTeleplayDetail(@Query("channel") String channel, @Query("video_id") int video_id, @Query("show_resource") String show_resource);

    @GET("/api/search/get_hot_word")
    HotSearchBeanNetData getHotSearch(@Query("limit") String limit, @Query("channel") String channel);

    @GET("/api/search/query")
    SearchResultBeanNetData getSearchResultData(@Query("query_str") String query_str, @Query("limit") int limit, @Query("channel") String channel);

}
