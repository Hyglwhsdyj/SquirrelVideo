package com.songshu.squirrelvideo.entity;

import java.io.Serializable;

/**
 * Created by yb on 15-7-24.
 */
public class SearchResultSingleVideoBean implements Serializable {

    public int id;
    public String title;
    public String type;
    public String area;
    public String showtimes;
    public String director;
    public String last;
    public String starring;
    public String poster_url;
    public String channel;

    @Override
    public String toString() {
        return "SearchResultSingleVideoBean{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", area='" + area + '\'' +
                ", showtimes='" + showtimes + '\'' +
                ", director='" + director + '\'' +
                ", last='" + last + '\'' +
                ", starring='" + starring + '\'' +
                ", poster_url='" + poster_url + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
