package com.songshu.squirrelvideo.request;

import com.songshu.squirrelvideo.common.IAppApi;
import com.songshu.squirrelvideo.entity.MovieSelectionNetData;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.Util;

/**
 * Created by yb on 15-7-6.
 */
public class GetMovieSelectionRequest extends AppAsyncRequest<MovieSelectionNetData, IAppApi> implements AppRequest {
    private static final String TAG = GetMovieSelectionRequest.class.getSimpleName() + ":";
    private String channel;
    private String type;
    private String area;
    private String year;
    private int page;

    public GetMovieSelectionRequest(String channel, String type, String area, String year, int page) {
        super(MovieSelectionNetData.class, IAppApi.class);
        this.channel = channel;
        this.type = type;
        this.area = area;
        this.year = year;
        this.page = page;
    }

    @Override
    public MovieSelectionNetData loadDataFromNetwork() throws Exception {
        L.d(TAG, "loadDataFromNetwork:" + toString());
        MovieSelectionNetData data = getService().getMovieSelection(channel, type, area, year, page);
        return data;
    }

    @Override
    public String getCacheKey() {
        String ori = "TAG:" + TAG + ",channel:" + channel + ",type:" + type + ",area:" + area + ",year:" + year + ",type:" + page;
        String md5 = Util.MD5(ori);
        L.d(TAG, "ori : " + ori + " , md5Key : " + md5);
        return md5;
    }

    @Override
    public Class getReqClass() {
        return getClass();
    }

    @Override
    public String toString() {
        return "GetMovieSelectionRequest{" +
                "channel='" + channel + '\'' +
                ", type='" + type + '\'' +
                ", area='" + area + '\'' +
                ", year='" + year + '\'' +
                ", page=" + page +
                '}';
    }

    public String getChannel() {
        return channel;
    }

    public String getType() {
        return type;
    }

    public String getArea() {
        return area;
    }

    public String getYear() {
        return year;
    }

    public int getPage() {
        return page;
    }
}
