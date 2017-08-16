package com.songshu.squirrelvideo.entity;

import java.util.Arrays;

/**
 * Created by yb on 15-7-21.
 */
public class HotSearchBeanNetData extends NetStatusNew {
    public HotSearchBean[] data;

    @Override
    public String toString() {
        return "HotSearchBeanNetData{" +
                "data=" + Arrays.toString(data) +
                '}';
    }
}
