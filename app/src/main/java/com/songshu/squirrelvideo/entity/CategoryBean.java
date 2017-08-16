package com.songshu.squirrelvideo.entity;

import java.io.Serializable;

/**
 * Created by yb on 15-7-8.
 */
public class CategoryBean implements Serializable {

    public String title;
    public String channel;
    public String sub_title;
    public int poster_url;
    public boolean is_selected;

    @Override
    public String toString() {
        return "CategoryBean{" +
                "title='" + title + '\'' +
                ", channel='" + channel + '\'' +
                ", sub_title='" + sub_title + '\'' +
                ", poster_url='" + poster_url + '\'' +
                ", is_selected=" + is_selected +
                '}';
    }
}
