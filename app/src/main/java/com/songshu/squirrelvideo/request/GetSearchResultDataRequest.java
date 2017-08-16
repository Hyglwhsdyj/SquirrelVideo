package com.songshu.squirrelvideo.request;

import com.songshu.squirrelvideo.common.IAppApi;
import com.songshu.squirrelvideo.entity.SearchResultBeanNetData;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.Util;

/**
 * Created by yb on 15-7-6.
 */
public class GetSearchResultDataRequest extends AppAsyncRequest<SearchResultBeanNetData, IAppApi> implements AppRequest {
    private static final String TAG = GetSearchResultDataRequest.class.getSimpleName() + ":";
    private String query_str;
    private int limit;
    private String channel;

    public GetSearchResultDataRequest(String query_str, int limit, String channel) {
        super(SearchResultBeanNetData.class, IAppApi.class);
        this.query_str = query_str;
        this.limit = limit;
        this.channel = channel;
    }

    @Override
    public SearchResultBeanNetData loadDataFromNetwork() throws Exception {
        L.d(TAG, "loadDataFromNetwork:" + toString());
        SearchResultBeanNetData data = getService().getSearchResultData(query_str, limit, channel);
        return data;
    }

    @Override
    public String getCacheKey() {
        String ori = "TAG:" + TAG + ",query_str:" + query_str + ",channel:" + channel + ",limit:" + limit;
        String md5 = Util.MD5(ori);
        L.d(TAG, "ori : " + ori + " , md5Key : " + md5);
        return md5;
    }

    @Override
    public Class getReqClass() {
        return getClass();
    }

    public String getQuery_str() {
        return query_str;
    }

    public int getLimit() {
        return limit;
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return "GetSearchResultDataRequest{" +
                "query_str='" + query_str + '\'' +
                ", limit=" + limit +
                ", channel='" + channel + '\'' +
                '}';
    }
}
