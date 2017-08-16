package com.songshu.squirrelvideo.entity;

import java.io.Serializable;

/**
 * Created by yb on 15-7-7.
 */
public class Pictures implements Serializable {
    public String big;
    public String small;

    @Override
    public String toString() {
        return "Pictures{" +
                "big='" + big + '\'' +
                ", small='" + small + '\'' +
                '}';
    }
}
