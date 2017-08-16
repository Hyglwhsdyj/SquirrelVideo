package com.songshu.squirrelvideo.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class ParentVideoBean implements Serializable {

    /**
     * 视频名字
     */
    private String title;
    /**
     * 视频来源 的中文名
     */
    private String site;
    /**
     * 视频质量
     */
    private String quality;
    /**
     * 视频格式
     */
    private String format;

    /**
     * for RoboSpice
     */
    public ChildVideoBean[] files;

    /**
     * 子视频包含着（视频的播放地址） for VideoUrlNet
     */
    public List<ChildVideoBean> list;

    public String ts;//解析腾讯视频二次地址中html的开始标签<key>
    public String te;//结束标签

    public ParentVideoBean() {
    }


    public ParentVideoBean(String title, String site, String quality, String format, List<ChildVideoBean> list) {
        this.title = title;
        this.site = site;
        this.quality = quality;
        this.format = format;
        this.list = list;
    }

    public ParentVideoBean(String title, String site, String quality, String format, List<ChildVideoBean> list, String ts, String te) {
        this.title = title;
        this.site = site;
        this.quality = quality;
        this.format = format;
        this.list = list;
        this.ts = ts;
        this.te = te;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public List<ChildVideoBean> getList() {
        return list;
    }

    public void setList(List<ChildVideoBean> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "ParentVideoBean{" +
                "title='" + title + '\'' +
                ", site='" + site + '\'' +
                ", quality='" + quality + '\'' +
                ", format='" + format + '\'' +
                ", list=" + list +
                ", ts='" + ts + '\'' +
                ", te='" + te + '\'' +
                '}';
    }

    /**
     * 将数组转成list
     */
    public void transArrayToList() {
        if (files != null && files.length > 0) {
            list = Arrays.asList(files);
        }
    }

    /**
     * 将视频的本地地址设置给list
     *
     * @param videoLocalUrl
     */
    public void setListWithVideoLocalPath(String[] videoLocalUrl) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setUrl(videoLocalUrl[i]);
        }
    }
}
