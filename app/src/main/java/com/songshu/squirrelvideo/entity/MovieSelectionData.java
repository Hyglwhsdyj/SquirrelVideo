package com.songshu.squirrelvideo.entity;

import java.io.Serializable;

/**
 * Created by yb on 15-7-7.
 */
public class MovieSelectionData implements Serializable {
    public int id;
    public String title;
    public String poster_url;

    //电视剧 动画片 综艺节目才有的字段
    public String last;
    public String uptime;
    public boolean done;

    @Override
    public String toString() {
        return "MovieSelectionData{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", poster_url='" + poster_url + '\'' +
                ", last='" + last + '\'' +
                ", update='" + uptime + '\'' +
                ", done=" + done +
                '}';
    }
}
