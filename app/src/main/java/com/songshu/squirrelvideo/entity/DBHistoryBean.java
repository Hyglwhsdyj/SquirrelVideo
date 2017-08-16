package com.songshu.squirrelvideo.entity;

/**
 * Created by yb on 15-7-14.
 */
public class DBHistoryBean {
    private String videoName;
    private String videoType;
    private String videoId;
    private String videoPic;
    private String videoChannel;
    private String videoEpisode;

    private String time;
    private String isComplete;
    private String videoIndex;
    private String watchDate;

    public DBHistoryBean() {
    }

    public DBHistoryBean(String videoName, String videoType, String videoId, String videoPic, String videoChannel, String time, String watchDate) {
        this.videoName = videoName;
        this.videoType = videoType;
        this.videoId = videoId;
        this.videoPic = videoPic;
        this.videoChannel = videoChannel;
        this.time = time;
        this.watchDate = watchDate;
    }

    public DBHistoryBean(String videoName, String videoType, String videoId, String videoPic, String videoChannel, String videoEpisode, String time, String isComplete, String videoIndex, String watchDate) {
        this.videoName = videoName;
        this.videoType = videoType;
        this.videoId = videoId;
        this.videoPic = videoPic;
        this.videoChannel = videoChannel;
        this.videoEpisode = videoEpisode;
        this.time = time;
        this.isComplete = isComplete;
        this.videoIndex = videoIndex;
        this.watchDate = watchDate;
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

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(String isComplete) {
        this.isComplete = isComplete;
    }

    public String getVideoIndex() {
        return videoIndex;
    }

    public void setVideoIndex(String videoIndex) {
        this.videoIndex = videoIndex;
    }

    public String getWatchDate() {
        return watchDate;
    }

    public void setWatchDate(String watchDate) {
        this.watchDate = watchDate;
    }

    @Override
    public String toString() {
        return "HistoryBean{" +
                "videoName='" + videoName + '\'' +
                ", videoType='" + videoType + '\'' +
                ", videoId='" + videoId + '\'' +
                ", videoPic='" + videoPic + '\'' +
                ", videoChannel='" + videoChannel + '\'' +
                ", videoEpisode='" + videoEpisode + '\'' +
                ", time='" + time + '\'' +
                ", isComplete='" + isComplete + '\'' +
                ", videoIndex='" + videoIndex + '\'' +
                ", watchDate='" + watchDate + '\'' +
                '}';
    }

}