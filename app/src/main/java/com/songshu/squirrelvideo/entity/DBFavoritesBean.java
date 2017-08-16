package com.songshu.squirrelvideo.entity;

/**
 * Created by yb on 15-7-14.
 */
public class DBFavoritesBean {

    /*索引	视频名称	    视频类型	  视频ID	    更新集数	        观看时间	  频道	    观看集数
    Index	VideoName	Type	  videoID	newVideoIndex	Time	  Channel	videoIndex*/

    private String videoId;
    private String videoName;
    private String videoType;
    private String videoPic;
    private String videoChannel;
    private String videoEpisode;

    public DBFavoritesBean() {
    }

    public DBFavoritesBean(String videoId, String videoName, String videoType, String videoPic, String videoChannel) {
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoType = videoType;
        this.videoPic = videoPic;
        this.videoChannel = videoChannel;
    }

    public DBFavoritesBean(String videoId, String videoName, String videoType, String videoPic, String videoChannel, String videoEpisode) {
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoType = videoType;
        this.videoPic = videoPic;
        this.videoChannel = videoChannel;
        this.videoEpisode = videoEpisode;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getVideoPic() {
        return videoPic;
    }

    public void setVideoPic(String videoPic) {
        this.videoPic = videoPic;
    }

    public String getVideoChannel() {
        return videoChannel;
    }

    public void setVideoChannel(String videoChannel) {
        this.videoChannel = videoChannel;
    }

    public String getVideoEpisode() {
        return videoEpisode;
    }

    public void setVideoEpisode(String videoEpisode) {
        this.videoEpisode = videoEpisode;
    }

    @Override
    public String toString() {
        return "DBFavoritesBean{" +
                "videoId='" + videoId + '\'' +
                ", videoName='" + videoName + '\'' +
                ", videoType='" + videoType + '\'' +
                ", videoPic='" + videoPic + '\'' +
                ", videoChannel='" + videoChannel + '\'' +
                ", videoEpisode='" + videoEpisode + '\'' +
                '}';
    }
}