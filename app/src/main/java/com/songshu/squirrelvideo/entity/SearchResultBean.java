package com.songshu.squirrelvideo.entity;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by yb on 15-7-24.
 */
public class SearchResultBean implements Serializable {

//    public JsonObject total;
    public SearchResultSingleVideoBean[] datalist;

    @Override
    public String toString() {
        return "SearchResultBean{" +
//                "total=" + total +
                ", datalist=" + Arrays.toString(datalist) +
                '}';
    }
}
