package com.songshu.squirrelvideo.entity;

import java.io.Serializable;

/**
 * Created by yb on 15-8-3.
 */
public class DBDownloadBean implements Serializable {

    public String videoId;// 视频的服务器端唯一id
    public String videoChannel;// 视频的类型
    public String videoName;// 视频的名字
    public String videoPosterUrl;// 视频截图的地址
    public int videoSegment;// 视频的第几个分段
    public String videoNetUrl;// 视频的网络播放地址 分段的原因
    public String videoLocalUrl;// 视频的本地地址 分段的原因
    public int videoDownloadState;// 视频下载状态
    public String videoTaskId;// 视频的下载标记/id
    public long videoDownDate;// 视频下载任务的添加时间
    public int videoCurrentProcess;// 视频下载当前已完成的进度
    public String videoHtmlUrl;// 视频原来的html地址

    public String videoWhichEpisode;// 电视剧/少儿等分集作品的第几集标记

    public DBDownloadBean() {
    }

    public DBDownloadBean(String videoId, String videoChannel, String videoName, String videoPosterUrl,int videoSegment, String videoNetUrl, String videoLocalUrl, int videoDownloadState, String videoTaskId, long videoDownDate, int videoCurrentProcess,String videoHtmlUrl,String videoWhichEpisode) {
        this.videoId = videoId;
        this.videoChannel = videoChannel;
        this.videoName = videoName;
        this.videoPosterUrl = videoPosterUrl;
        this.videoSegment = videoSegment;
        this.videoNetUrl = videoNetUrl;
        this.videoLocalUrl = videoLocalUrl;
        this.videoDownloadState = videoDownloadState;
        this.videoTaskId = videoTaskId;
        this.videoDownDate = videoDownDate;
        this.videoCurrentProcess = videoCurrentProcess;
        this.videoHtmlUrl = videoHtmlUrl;
        this.videoWhichEpisode = videoWhichEpisode;
    }

    @Override
    public String toString() {
        return "DBDownloadBean{" +
                "videoId='" + videoId + '\'' +
                ", videoChannel='" + videoChannel + '\'' +
                ", videoName='" + videoName + '\'' +
                ", videoPosterUrl='" + videoPosterUrl + '\'' +
                ", videoSegment=" + videoSegment +
                ", videoNetUrl='" + videoNetUrl + '\'' +
                ", videoLocalUrl='" + videoLocalUrl + '\'' +
                ", videoDownloadState=" + videoDownloadState +
                ", videoTaskId='" + videoTaskId + '\'' +
                ", videoDownDate='" + videoDownDate + '\'' +
                ", videoCurrentProcess='" + videoCurrentProcess + '\'' +
                ", videoHtmlUrl='" + videoHtmlUrl + '\'' +
                ", videoWhichEpisode='" + videoWhichEpisode + '\'' +
                '}';
    }
}
