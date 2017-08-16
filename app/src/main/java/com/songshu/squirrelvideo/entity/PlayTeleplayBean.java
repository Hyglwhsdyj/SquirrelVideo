package com.songshu.squirrelvideo.entity;

import java.io.Serializable;

/**
 * Created by yb on 15-7-10.
 */
public class PlayTeleplayBean implements Serializable {
    /**
     * 剧集序号
     */
    public int videoNumber;
    /**
     * 视频播放地址
     */
    public String videoUrl;

    public int videoDownloadState;

    public PlayTeleplayBean(int videoNumber, String videoUrl) {
        this.videoNumber = videoNumber;
        this.videoUrl = videoUrl;
    }

    public PlayTeleplayBean() {
    }

    @Override
    public String toString() {
        return "PlayTeleplayBean{" +
                "videoNumber=" + videoNumber +
                ", videoUrl='" + videoUrl + '\'' +
                '}';
    }
}
