package com.songshu.squirrelvideo.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by yb on 15-8-3.
 */
public class PreDownloadBean implements Serializable {
    public String id;
    public String channel;
    public String fileName;// 视频名称,中文或英文
    public String poster_url;// 视频图片地址
    public Map<Integer, String> urlMap;// key是分段数字,value是分段的原网络地址
    public String videoWhichEpisode;// 连续剧和少儿频道才有的字段,表示点击的是第几集
    public String videoHtmlUrl;// html地址
    public long videoDownDate;
}
