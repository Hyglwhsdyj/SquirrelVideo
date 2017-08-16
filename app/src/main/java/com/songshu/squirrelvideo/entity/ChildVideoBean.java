package com.songshu.squirrelvideo.entity;

import java.io.Serializable;

public class ChildVideoBean implements Serializable {

    /**
     * 请求视频的源地址
     */
    private String ori_url;

    /**
     * 视频播放地址
     */
    private String url;
    /**
     * 视频类型
     */
    private String type;
    /**
     * 视频时长的总秒数
     */
    private String seconds;
    /**
     * 视频的时长
     */
    private String time;
    /**
     * 视频字节大小
     */
    private String bytes;
    /**
     * 视频尺寸
     */
    private String size;

    public String U;//腾讯视频2次请求的播放地址

    public ChildVideoBean() {
    }

    public ChildVideoBean(String ori_url, String url, String type, String seconds, String time, String bytes, String size, String U) {
        this.ori_url = ori_url;
        this.url = url;
        this.type = type;
        this.seconds = seconds;
        this.time = time;
        this.bytes = bytes;
        this.size = size;
        this.U = U;
    }

    public ChildVideoBean(String ori_url, String url, String type, String seconds, String time, String bytes, String size) {
        this.ori_url = ori_url;
        this.url = url;
        this.type = type;
        this.seconds = seconds;
        this.time = time;
        this.bytes = bytes;
        this.size = size;
    }

    public ChildVideoBean(String url, String type, String bytes, String seconds, String time, String size) {
        this.url = url;
        this.type = type;
        this.bytes = bytes;
        this.seconds = seconds;
        this.time = time;
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBytes() {
        return bytes;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
    }

    public long getSecond() {
        return Math.round(Double.parseDouble(seconds));
    }

    public void setSecond(String seconds) {
        this.seconds = seconds;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getOri_url() {
        return ori_url;
    }

    public void setOri_url(String ori_url) {
        this.ori_url = ori_url;
    }

    @Override
    public String toString() {
        return "ChildVideoBean{" +
                "ori_url='" + ori_url + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", seconds='" + seconds + '\'' +
                ", time='" + time + '\'' +
                ", bytes='" + bytes + '\'' +
                ", size='" + size + '\'' +
                ", U='" + U + '\'' +
                '}';
    }
}
