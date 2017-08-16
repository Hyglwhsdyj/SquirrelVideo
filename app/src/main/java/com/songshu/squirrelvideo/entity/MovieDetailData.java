package com.songshu.squirrelvideo.entity;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by yb on 15-7-9.
 */
public class MovieDetailData implements Serializable {
    public String id;
    public String title;
    public String alias;
    public String intro;
    public String type;
    public String area;
    public String showtimes;
    public String director;
    public String starring;
    public MovieSourceBean[] source;
    public TeleplaySourceBean[] teleplay_source;
    public String info_url;
    public String poster_url;
    public String count_play;
    public String status;
    public String create_time;
    public String update_time;

    public String last;
    public String part;
    public boolean done;

    @Override
    public String toString() {
        return "MovieDetailData{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", alias='" + alias + '\'' +
                ", intro='" + intro + '\'' +
                ", type='" + type + '\'' +
                ", area='" + area + '\'' +
                ", showtimes='" + showtimes + '\'' +
                ", director='" + director + '\'' +
                ", starring='" + starring + '\'' +
                ", source=" + Arrays.toString(source) +
                ", source=" + Arrays.toString(teleplay_source) +
                ", info_url='" + info_url + '\'' +
                ", poster_url='" + poster_url + '\'' +
                ", count_play='" + count_play + '\'' +
                ", status='" + status + '\'' +
                ", create_time='" + create_time + '\'' +
                ", update_time='" + update_time + '\'' +
                ", last='" + last + '\'' +
                ", part='" + part + '\'' +
                ", done=" + done +
                '}';
    }
}
