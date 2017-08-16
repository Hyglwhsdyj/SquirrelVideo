package com.songshu.squirrelvideo.entity;

import java.io.Serializable;

/**
 * Created by yb on 15-7-16.
 */
public class FuncsDBDataBean implements Serializable {
    private static final String TAG = FuncsDBDataBean.class.getSimpleName() + ":";

    public int id;
    public String title;
    public String channel;
    public Pictures picture;

    public boolean isDeleteBtnShow = false;

    @Override
    public String toString() {
        return "FuncsDBDataBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", channel='" + channel + '\'' +
                ", picture=" + picture +
                ", isDeleteBtnShow=" + isDeleteBtnShow +
                '}';
    }
}
