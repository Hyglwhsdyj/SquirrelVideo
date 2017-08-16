package com.songshu.squirrelvideo.entity;

import java.util.Arrays;

/**
 * Created by yb on 15-7-7.
 */
public class MovieSelectionNetData extends NetStatusNew {
    public MovieSelectionData[] data;

    @Override
    public String toString() {
        return "MovieSelectionNetData{" +
                "data=" + Arrays.toString(data) +
                '}';
    }
}
