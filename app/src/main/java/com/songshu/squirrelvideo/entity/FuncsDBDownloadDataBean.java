package com.songshu.squirrelvideo.entity;

import java.io.Serializable;

/**
 * Created by yb on 15-7-16.
 */
public class FuncsDBDownloadDataBean implements Serializable {
    private static final String TAG = FuncsDBDownloadDataBean.class.getSimpleName() + ":";

    public String id;
    public String title;
    public String channel;
    public Pictures picture;
    public String episode;// 电视剧/少儿等分集作品的第几集标记
    public int download_state;
    public int videoCurrentProcess;

    public boolean isDeleteBtnShow = false;

    @Override
    public String toString() {
        return "FuncsDBDataBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", channel='" + channel + '\'' +
                ", picture=" + picture +
                ", episode=" + episode +
                ", download_state=" + download_state +
                ", videoCurrentProcess=" + videoCurrentProcess +
                ", isDeleteBtnShow=" + isDeleteBtnShow +
                '}';
    }
}
