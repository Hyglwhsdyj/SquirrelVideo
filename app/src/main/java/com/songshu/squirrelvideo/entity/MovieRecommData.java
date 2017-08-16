package com.songshu.squirrelvideo.entity;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by yb on 15-7-6.
 */
public class MovieRecommData implements Serializable {
    public int category_id;
    public String category_name;
    public VideoBean[] video_list;

    @Override
    public String toString() {
        return "MovieRecommData{" +
                "category_id=" + category_id +
                ", category_name='" + category_name + '\'' +
                ", video_list=" + Arrays.toString(video_list) +
                '}';
    }
}
