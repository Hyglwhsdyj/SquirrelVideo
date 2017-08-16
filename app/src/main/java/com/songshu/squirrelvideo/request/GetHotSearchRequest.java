package com.songshu.squirrelvideo.request;

import com.songshu.squirrelvideo.common.IAppApi;
import com.songshu.squirrelvideo.entity.HotSearchBeanNetData;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.Util;

/**
 * Created by yb on 15-7-6.
 */
public class GetHotSearchRequest extends AppAsyncRequest<HotSearchBeanNetData, IAppApi> implements AppRequest {
    private static final String TAG = GetHotSearchRequest.class.getSimpleName() + ":";
    private String limit;
    private String channel;

    public GetHotSearchRequest(String limit, String channel) {
        super(HotSearchBeanNetData.class, IAppApi.class);
        this.limit = limit;
        this.channel = channel;
    }

    @Override
    public HotSearchBeanNetData loadDataFromNetwork() throws Exception {
        L.d(TAG, "loadDataFromNetwork:" + toString());
        HotSearchBeanNetData data = getService().getHotSearch(limit, channel);
        return data;
    }

    @Override
    public String getCacheKey() {
        String ori = "TAG:" + TAG + ",limit:" + limit + ",channel:" + channel;
        String md5 = Util.MD5(ori);
        L.d(TAG, "ori : " + ori + " , md5Key : " + md5);
        return md5;
    }

    @Override
    public Class getReqClass() {
        return getClass();
    }

    public String getLimit() {
        return limit;
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return "GetHotSearchRequest{" +
                "limit='" + limit + '\'' +
                "channel='" + channel + '\'' +
                '}';
    }
}
