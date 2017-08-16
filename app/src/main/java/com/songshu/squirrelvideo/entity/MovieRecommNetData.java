package com.songshu.squirrelvideo.entity;

import java.util.Arrays;

/**
 * Created by yb on 15-7-6.
 */
public class MovieRecommNetData extends NetStatusNew {

    public MovieRecommData[] data;

    @Override
    public String toString() {
        return "MovieRecommNetData{" +
                "data=" + Arrays.toString(data) +
                '}';
    }
}
