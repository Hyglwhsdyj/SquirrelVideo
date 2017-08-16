package com.songshu.squirrelvideo.request;

import com.songshu.squirrelvideo.common.IAppApi;
import com.songshu.squirrelvideo.entity.MovieRecommNetData;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.Util;

/**
 * Created by yb on 15-7-6.
 */
public class GetMovieRecommRequest extends AppAsyncRequest<MovieRecommNetData, IAppApi> implements AppRequest {
    private static final String TAG = GetMovieRecommRequest.class.getSimpleName() + ":";
    private String channel;
    private String limit;

    public GetMovieRecommRequest(String channel, String limit) {
        super(MovieRecommNetData.class, IAppApi.class);
        this.channel = channel;
        this.limit = limit;
    }

    @Override
    public MovieRecommNetData loadDataFromNetwork() throws Exception {
        L.d(TAG, "loadDataFromNetwork:" + toString());
        MovieRecommNetData data = getService().getMovieRecomm(channel, limit);
        return data;
    }

    @Override
    public String getCacheKey() {
        String ori = "TAG:" + TAG + ",channel:" + channel + ",limit:" + limit;
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
        return "GetMovieRecommRequest{" +
                "channel='" + channel + '\'' +
                ", limit='" + limit + '\'' +
                '}';
    }

    public String getChannel() {
        return channel;
    }

    public String getLimit() {
        return limit;
    }

}
