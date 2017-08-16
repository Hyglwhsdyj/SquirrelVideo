package com.songshu.squirrelvideo.entity;

import java.io.Serializable;

/**
 * Created by yb on 15-7-7.
 */
public class VideoTypeTransformBean implements Serializable {

    public String sub_title;
    public String channel;
    public String type;
    public int poster_url;
    public String area;
    public String year;

    public VideoTypeTransformBean() {
    }

    public VideoTypeTransformBean(String sub_title, String channel, String type, int poster_url) {
        this.sub_title = sub_title;
        this.channel = channel;
        this.type = type;
        this.poster_url = poster_url;
    }
}
