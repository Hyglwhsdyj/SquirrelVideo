package com.songshu.squirrelvideo.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yb on 15-7-6.
 */
public class VideoTypeBean implements Serializable {

    private static final String TAG = VideoTypeBean.class.getSimpleName() + ":";
    public String mTitle;
    public List<String> mVideoSubTypeList;

    @Override
    public String toString() {
        return "VideoTypeBean{" +
                "mTitle='" + mTitle + '\'' +
                ", mVideoSubTypeList=" + mVideoSubTypeList +
                '}';
    }


}
