package com.songshu.squirrelvideo.entity;

import java.io.Serializable;

/**
 * Created by yb on 15-7-9.
 */
public class MovieSourceBean implements Serializable {
    public String name;
    public String title;
    public String icon_url;
    public String values;

    @Override
    public String toString() {
        return "MovieSourceBean{" +
                "name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", icon_url='" + icon_url + '\'' +
                ", values=" + values +
                '}';
    }
}
