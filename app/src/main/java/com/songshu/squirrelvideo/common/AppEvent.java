package com.songshu.squirrelvideo.common;

import com.songshu.squirrelvideo.entity.DBDownloadBean;
import com.songshu.squirrelvideo.entity.HotSearchBeanNetData;
import com.songshu.squirrelvideo.entity.MovieDetailNetData;
import com.songshu.squirrelvideo.entity.MovieRecommNetData;
import com.songshu.squirrelvideo.entity.MovieSelectionNetData;
import com.songshu.squirrelvideo.entity.NetStatus;
import com.songshu.squirrelvideo.entity.ParentVideoBean;
import com.songshu.squirrelvideo.entity.SearchResultBeanNetData;
import com.songshu.squirrelvideo.entity.TeleplayDetailNetData;
import com.songshu.squirrelvideo.entity.TeleplaySourceBean;
import com.songshu.squirrelvideo.entity.VideoTypeBean;

/**
 * Created by yb on 15-7-4.
 */
public class AppEvent {

    private static final String TAG = AppEvent.class.getSimpleName() + ":";

    public static class NothingHappen extends BaseEvent {

    }

    public static class NetStatusEvent extends BaseEvent {
        public NetStatus netStatus;
        public Class mRequestClass;
        public String reqKey;

        public NetStatusEvent(NetStatus netStatus, Class requestClass) {
            this.netStatus = netStatus;
            mRequestClass = requestClass;
        }

        public NetStatusEvent(NetStatus netStatus, String reqKey) {
            this.netStatus = netStatus;
            this.reqKey = reqKey;
        }

        @Override
        public String toString() {
            return "NetStatusEvent{" +
                    "netStatus=" + netStatus +
                    ", mRequestClass=" + mRequestClass +
                    ", reqKey='" + reqKey + '\'' +
                    '}';
        }
    }

    public static class SubTitleListOccusChanging {
        private VideoTypeBean videoTypeBean;

        public SubTitleListOccusChanging() {
        }

        public SubTitleListOccusChanging(VideoTypeBean videoTypeBean) {
            this.videoTypeBean = videoTypeBean;
        }

        public VideoTypeBean getVideoTypeBean() {
            return videoTypeBean;
        }
    }

    public static class SucGetMovieRecommEvent extends BaseEvent {
        public String reqKey;
        public MovieRecommNetData mMovieRecommNetData;
        public String channel;
        public String limit;

        public SucGetMovieRecommEvent(String reqKey, MovieRecommNetData mMovieRecommNetData, String channel, String limit) {
            this.reqKey = reqKey;
            this.mMovieRecommNetData = mMovieRecommNetData;
            this.channel = channel;
            this.limit = limit;
        }

        @Override
        public String toString() {
            return "SucGetMovieRecommEvent{" +
                    "reqKey='" + reqKey + '\'' +
                    ", mMovieRecommNetData=" + mMovieRecommNetData +
                    ", channel='" + channel + '\'' +
                    ", limit='" + limit + '\'' +
                    '}';
        }
    }

    public static class FailGetMovieRecommEvent extends BaseEvent {
        public String reqKey;
        public String channel;
        public String limit;

        public FailGetMovieRecommEvent(String reqKey, String channel, String limit) {
            this.reqKey = reqKey;
            this.channel = channel;
            this.limit = limit;
        }

        @Override
        public String toString() {
            return "FailGetMovieRecommEvent{" +
                    "reqKey='" + reqKey + '\'' +
                    ", channel='" + channel + '\'' +
                    ", limit='" + limit + '\'' +
                    '}';
        }
    }

    public static class SucGetMovieSelectionEvent extends BaseEvent {
        public String reqKey;
        public MovieSelectionNetData mMovieSelectionNetData;
        public String channel;
        public String type;
        public String area;
        public String year;
        public int page;

        public SucGetMovieSelectionEvent(String reqKey, MovieSelectionNetData mMovieSelectionNetData, String channel, String type, String area, String year, int page) {
            this.reqKey = reqKey;
            this.mMovieSelectionNetData = mMovieSelectionNetData;
            this.channel = channel;
            this.type = type;
            this.area = area;
            this.year = year;
            this.page = page;
        }

        @Override
        public String toString() {
            return "SucGetMovieSelectionEvent{" +
                    "reqKey='" + reqKey + '\'' +
                    ", mMovieSelectionNetData=" + mMovieSelectionNetData +
                    ", channel='" + channel + '\'' +
                    ", type='" + type + '\'' +
                    ", area='" + area + '\'' +
                    ", year='" + year + '\'' +
                    ", page=" + page +
                    '}';
        }
    }

    public static class FailGetMovieSelectionEvent extends BaseEvent {
        public String reqKey;
        public String channel;
        public String type;
        public String area;
        public String year;
        public int page;

        public FailGetMovieSelectionEvent(String reqKey, String channel, String type, String area, String year, int page) {
            this.reqKey = reqKey;
            this.channel = channel;
            this.type = type;
            this.area = area;
            this.year = year;
            this.page = page;
        }

        @Override
        public String toString() {
            return "FailGetMovieRecommEvent{" +
                    "reqKey='" + reqKey + '\'' +
                    ", channel='" + channel + '\'' +
                    ", type='" + type + '\'' +
                    ", area='" + area + '\'' +
                    ", year='" + year + '\'' +
                    ", page=" + page +
                    '}';
        }
    }


    public static class SucGetMovieDetailEvent extends BaseEvent {
        public String reqKey;
        public MovieDetailNetData mMovieDetailNetData;
        public String channel;
        public int video_id;

        public SucGetMovieDetailEvent(String reqKey, MovieDetailNetData mMovieDetailNetData, String channel, int video_id) {
            this.reqKey = reqKey;
            this.mMovieDetailNetData = mMovieDetailNetData;
            this.channel = channel;
            this.video_id = video_id;
        }

        @Override
        public String toString() {
            return "SucGetMovieDetailEvent{" +
                    "reqKey='" + reqKey + '\'' +
                    ", mMovieDetailNetData=" + mMovieDetailNetData +
                    ", channel='" + channel + '\'' +
                    ", video_id='" + video_id + '\'' +
                    '}';
        }
    }

    public static class FailGetMovieDetailEvent extends BaseEvent {
        public String reqKey;
        public String channel;
        public int video_id;

        public FailGetMovieDetailEvent(String reqKey, String channel, int video_id) {
            this.reqKey = reqKey;
            this.channel = channel;
            this.video_id = video_id;
        }

        @Override
        public String toString() {
            return "FailGetMovieRecommEvent{" +
                    "reqKey='" + reqKey + '\'' +
                    ", channel='" + channel + '\'' +
                    ", video_id='" + video_id + '\'' +
                    '}';
        }
    }


    public static class SucGetTeleplayDetailEvent extends BaseEvent {
        public String reqKey;
        public TeleplayDetailNetData mTeleplayDetailNetData;
        //        public MovieDetailNetData mMovieDetailNetData;
        public String channel;
        public int video_id;

        public SucGetTeleplayDetailEvent(String reqKey, TeleplayDetailNetData mTeleplayDetailNetData, String channel, int video_id) {
            this.reqKey = reqKey;
            this.mTeleplayDetailNetData = mTeleplayDetailNetData;

            for (TeleplaySourceBean bean : this.mTeleplayDetailNetData.data.source) {
                bean.transform();
            }
//            this.mMovieDetailNetData = new MovieDetailNetData();
//            transform(mTeleplayDetailNetData, mMovieDetailNetData);
            this.channel = channel;
            this.video_id = video_id;
        }

        @Override
        public String toString() {
            return "SucGetMovieDetailEvent{" +
                    "reqKey='" + reqKey + '\'' +
                    ", mTeleplayDetailNetData=" + mTeleplayDetailNetData +
                    ", channel='" + channel + '\'' +
                    ", video_id='" + video_id + '\'' +
                    '}';
        }
    }

//    public static void transform(TeleplayDetailNetData mTeleplayDetailNetData, MovieDetailNetData mMovieDetailNetData) {
//        mMovieDetailNetData.code = mTeleplayDetailNetData.code;
//        mMovieDetailNetData.msg = mTeleplayDetailNetData.msg;
//        mMovieDetailNetData.time = mTeleplayDetailNetData.time;
//        mMovieDetailNetData.expires = mTeleplayDetailNetData.expires;
//        mMovieDetailNetData.data = new MovieDetailData();
//        mMovieDetailNetData.data.id = mTeleplayDetailNetData.data.id;
//        mMovieDetailNetData.data.title = mTeleplayDetailNetData.data.title;
//        mMovieDetailNetData.data.alias = mTeleplayDetailNetData.data.alias;
//        mMovieDetailNetData.data.intro = mTeleplayDetailNetData.data.intro;
//        mMovieDetailNetData.data.type = mTeleplayDetailNetData.data.type;
//        mMovieDetailNetData.data.area = mTeleplayDetailNetData.data.area;
//        mMovieDetailNetData.data.showtimes = mTeleplayDetailNetData.data.showtimes;
//        mMovieDetailNetData.data.director = mTeleplayDetailNetData.data.director;
//        mMovieDetailNetData.data.starring = mTeleplayDetailNetData.data.starring;
//        mMovieDetailNetData.data.info_url = mTeleplayDetailNetData.data.info_url;
//        mMovieDetailNetData.data.poster_url = mTeleplayDetailNetData.data.poster_url;
//        mMovieDetailNetData.data.count_play = mTeleplayDetailNetData.data.count_play;
//        mMovieDetailNetData.data.status = mTeleplayDetailNetData.data.status;
//        mMovieDetailNetData.data.create_time = mTeleplayDetailNetData.data.create_time;
//        mMovieDetailNetData.data.update_time = mTeleplayDetailNetData.data.update_time;
//        mMovieDetailNetData.data.last = mTeleplayDetailNetData.data.last;
//        mMovieDetailNetData.data.part = mTeleplayDetailNetData.data.part;
//        mMovieDetailNetData.data.done = mTeleplayDetailNetData.data.done;
//        mMovieDetailNetData.data.teleplay_source = mTeleplayDetailNetData.data.source;
//    }

    public static class FailGetTeleplayDetailEvent extends BaseEvent {
        public String reqKey;
        public String channel;
        public int video_id;

        public FailGetTeleplayDetailEvent(String reqKey, String channel, int video_id) {
            this.reqKey = reqKey;
            this.channel = channel;
            this.video_id = video_id;
        }

        @Override
        public String toString() {
            return "FailGetMovieRecommEvent{" +
                    "reqKey='" + reqKey + '\'' +
                    ", channel='" + channel + '\'' +
                    ", video_id='" + video_id + '\'' +
                    '}';
        }
    }

    public static class DBOccusChanging {
        public DBOccusChanging() {

        }
    }

    public static class SucGetHotSearchEvent extends BaseEvent {
        public String reqKey;
        public HotSearchBeanNetData mHotSearchBeanNetData;
        public String limit;
        public String channel;

        public SucGetHotSearchEvent(String reqKey, HotSearchBeanNetData mHotSearchBeanNetData, String limit, String channel) {
            this.reqKey = reqKey;
            this.mHotSearchBeanNetData = mHotSearchBeanNetData;
            this.limit = limit;
            this.channel = channel;
        }

        @Override
        public String toString() {
            return "SucGetMovieDetailEvent{" +
                    "reqKey='" + reqKey + '\'' +
                    ", mHotSearchBeanNetData=" + mHotSearchBeanNetData +
                    ", limit='" + limit + '\'' +
                    ", channel='" + channel + '\'' +
                    '}';
        }
    }

    public static class FailGetHotSearchEvent extends BaseEvent {
        public String reqKey;
        public String limit;
        public String channel;

        public FailGetHotSearchEvent(String reqKey, String limit, String channel) {
            this.reqKey = reqKey;
            this.limit = limit;
            this.limit = channel;
        }

        @Override
        public String toString() {
            return "FailGetMovieRecommEvent{" +
                    "reqKey='" + reqKey + '\'' +
                    ", limit='" + limit + '\'' +
                    ", channel='" + channel + '\'' +
                    '}';
        }
    }


    public static class SucGetSearchResultDataEvent extends BaseEvent {
        public String reqKey;
        public SearchResultBeanNetData mSearchResultBeanNetData;
        public String query_str;
        public int limit;
        public String channel;

        public SucGetSearchResultDataEvent(String reqKey, SearchResultBeanNetData mSearchResultBeanNetData, String query_str, int limit, String channel) {
            this.reqKey = reqKey;
            this.mSearchResultBeanNetData = mSearchResultBeanNetData;
            this.query_str = query_str;
            this.channel = channel;
            this.limit = limit;
        }

        @Override
        public String toString() {
            return "SucGetMovieRecommEvent{" +
                    "reqKey='" + reqKey + '\'' +
                    ", mSearchResultBeanNetData=" + mSearchResultBeanNetData +
                    ", query_str='" + query_str + '\'' +
                    ", channel='" + channel + '\'' +
                    ", limit='" + limit + '\'' +
                    '}';
        }
    }

    public static class FailGetSearchResultDataEvent extends BaseEvent {
        public String reqKey;
        public String query_str;
        public int limit;
        public String channel;

        public FailGetSearchResultDataEvent(String reqKey, String query_str, int limit, String channel) {
            this.reqKey = reqKey;
            this.query_str = query_str;
            this.channel = channel;
            this.limit = limit;
        }

        @Override
        public String toString() {
            return "FailGetMovieRecommEvent{" +
                    "reqKey='" + reqKey + '\'' +
                    ", query_str='" + query_str + '\'' +
                    ", channel='" + channel + '\'' +
                    ", limit='" + limit + '\'' +
                    '}';
        }
    }


    public static class SucGetVideoRealPathEvent extends BaseEvent {
        public String reqKey;
        public ParentVideoBean mParentVideoData;
        public String channel;
        public String source;
        public String play_url;


        public SucGetVideoRealPathEvent(String reqKey, ParentVideoBean mParentVideoData, String channel, String source, String play_url) {
            this.reqKey = reqKey;
            this.mParentVideoData = mParentVideoData;
            this.channel = channel;
            this.source = source;
            this.play_url = play_url;
        }

        @Override
        public String toString() {
            return "SucGetMovieDetailEvent{" +
                    "reqKey='" + reqKey + '\'' +
                    ", mParentVideoNetData=" + mParentVideoData +
                    ", channel='" + channel + '\'' +
                    ", source='" + source + '\'' +
                    ", play_url='" + play_url + '\'' +
                    '}';
        }
    }

    public static class FailGetVideoRealPathEvent extends BaseEvent {
        public String reqKey;
        public String channel;
        public String source;
        public String play_url;

        public FailGetVideoRealPathEvent(String reqKey, String channel, String source, String play_url) {
            this.reqKey = reqKey;
            this.channel = channel;
            this.source = source;
            this.play_url = play_url;
        }

        @Override
        public String toString() {
            return "FailGetMovieRecommEvent{" +
                    "reqKey='" + reqKey + '\'' +
                    ", channel='" + channel + '\'' +
                    ", source='" + source + '\'' +
                    ", play_url='" + play_url + '\'' +
                    '}';
        }
    }


    /**
     * ***********************************************************************************************************************
     * App内部交流的Event
     * ***********************************************************************************************************************
     */

    public static class DownloadConnectionLostEvent extends BaseEvent {
        public DBDownloadBean bean;

        public DownloadConnectionLostEvent(DBDownloadBean bean) {
            this.bean = bean;
        }

    }

    public static class DownloadNewPreComeEvent extends BaseEvent {

        public DownloadNewPreComeEvent() {
        }

    }

    public static class DownloadStartedEvent extends BaseEvent {
        public DBDownloadBean bean;

        public DownloadStartedEvent(DBDownloadBean bean) {
            this.bean = bean;
        }
    }

    public static class DownloadPausedEvent extends BaseEvent {
        public DBDownloadBean bean;

        public DownloadPausedEvent(DBDownloadBean bean) {
            this.bean = bean;
        }
    }

    public static class DownloadProcessEvent extends BaseEvent {
        public DBDownloadBean bean;

        public DownloadProcessEvent(DBDownloadBean bean) {
            this.bean = bean;
        }
    }

    public static class DownloadFinishedEvent extends BaseEvent {
        public DBDownloadBean bean;

        public DownloadFinishedEvent(DBDownloadBean bean) {
            this.bean = bean;
        }
    }


    public static class ReachMaxDownloadTaskNumEvent extends BaseEvent {

        public ReachMaxDownloadTaskNumEvent() {

        }
    }

}
