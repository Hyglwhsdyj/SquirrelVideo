package com.songshu.squirrelvideo.entity;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by yb on 15-7-9.
 */
public class TeleplayDetailData implements Serializable {
    public String id;
    public String title;
    public String alias;
    public String intro;
    public String type;
    public String area;
    public String showtimes;
    public String director;
    public String starring;
    public TeleplaySourceBean[] source;
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


    public boolean isSourceUNchanged(TeleplaySourceBean[] source) {
        if (source != null && source.length == this.source.length) {
            for (int i = 0; i < source.length; i++) {
                if (!this.source[i].isEqual(source[i].values.toString())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
