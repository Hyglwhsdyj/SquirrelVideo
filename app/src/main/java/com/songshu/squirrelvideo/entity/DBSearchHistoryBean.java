package com.songshu.squirrelvideo.entity;

import java.io.Serializable;

/**
 * Created by yb on 15-7-21.
 */
public class DBSearchHistoryBean implements Serializable {
    public String historyTitle;
    public String videoChannel;
    public String searchTime;

    public DBSearchHistoryBean(String historyTitle, String videoChannel, String searchTime) {
        this.historyTitle = historyTitle;
        this.videoChannel = videoChannel;
        this.searchTime = searchTime;
    }

    @Override
    public String toString() {
        return "DBSearchHistoryBean{" +
                "historyTitle='" + historyTitle + '\'' +
                ", videoChannel='" + videoChannel + '\'' +
                ", searchTime='" + searchTime + '\'' +
                '}';
    }
}
