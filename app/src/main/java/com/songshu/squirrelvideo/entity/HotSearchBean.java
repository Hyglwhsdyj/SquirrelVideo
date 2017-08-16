package com.songshu.squirrelvideo.entity;

import java.io.Serializable;

/**
 * Created by yb on 15-7-21.
 */
public class HotSearchBean implements Serializable {
    public int id;
    public String content;
    public int click;
    public int heat;
    public String update_time;
    public String create_time;
    public String channel;

    @Override
    public String toString() {
        return "HotSearchBean{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", click='" + click + '\'' +
                ", heat='" + heat + '\'' +
                ", update_time='" + update_time + '\'' +
                ", create_time='" + create_time + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
