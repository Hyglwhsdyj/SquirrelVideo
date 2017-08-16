package com.songshu.squirrelvideo.entity;

import java.io.Serializable;

/**
 * Created by yb on 15-7-6.
 */
public class VideoBean implements Serializable {
    public int id;
    public String title;
    public String channel;
    public String remark;
    public Pictures picture;

    //电视剧 动画片 综艺节目才有的字段
    public String last;
    public String uptime;
    public boolean done;

    @Override
    public String toString() {
        return "VideoBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", channel='" + channel + '\'' +
                ", remark='" + remark + '\'' +
                ", picture=" + picture +
                ", last='" + last + '\'' +
                ", uptime='" + uptime + '\'' +
                ", done=" + done +
                '}';
    }
}
