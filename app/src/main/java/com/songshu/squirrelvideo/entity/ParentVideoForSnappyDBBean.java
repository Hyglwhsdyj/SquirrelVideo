package com.songshu.squirrelvideo.entity;

import com.snappydb.SnappydbException;
import com.songshu.squirrelvideo.application.App;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class ParentVideoForSnappyDBBean implements Serializable{

    /**
     * 视频名字
     */
    private String title;
    /**
     * 视频来源
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


    public ParentVideoForSnappyDBBean() {
    }

    public ParentVideoForSnappyDBBean(String title, String site, String quality, String format , List<ChildVideoBean> list) {
        this.title = title;
        this.site = site;
        this.quality = quality;
        this.format = format;
//        this.list = list;
        files = list.toArray(new ChildVideoBean[list.size()]);
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


    @Override
    public String toString() {
        return "ParentVideoBean{" +
                "title='" + title + '\'' +
                ", site='" + site + '\'' +
                ", quality='" + quality + '\'' +
                ", format='" + format + '\'' +
                /*", list=" + list +*/
                '}';
    }

    /**
     * 把对象存入snappydb
     * @param videoHtmlUrl
     */
    public void setObjectToSnappyDB(String videoHtmlUrl){
        try {
            App.getSnappyDb().put(videoHtmlUrl, this);
            App.closeSnappyDb();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

}
