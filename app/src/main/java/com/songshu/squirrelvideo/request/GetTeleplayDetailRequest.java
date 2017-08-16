package com.songshu.squirrelvideo.request;

import com.songshu.squirrelvideo.common.IAppApi;
import com.songshu.squirrelvideo.entity.MovieDetailNetData;
import com.songshu.squirrelvideo.entity.TeleplayDetailNetData;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.Util;

/**
 * Created by yb on 15-7-6.
 */
public class GetTeleplayDetailRequest extends AppAsyncRequest<TeleplayDetailNetData, IAppApi> implements AppRequest {
    private static final String TAG = GetTeleplayDetailRequest.class.getSimpleName() + ":";
    private String channel;
    private int video_id;

    public GetTeleplayDetailRequest(String channel, int video_id) {
        super(TeleplayDetailNetData.class, IAppApi.class);
        this.channel = channel;
        this.video_id = video_id;
    }

    @Override
    public TeleplayDetailNetData loadDataFromNetwork() throws Exception {
        L.d(TAG, "loadDataFromNetwork:" + toString());
        TeleplayDetailNetData data = getService().getTeleplayDetail(channel, video_id, "1");
        return data;
    }

    @Override
    public String getCacheKey() {
        String ori = "TAG:" + TAG + ",channel:" + channel + ",video_id:" + video_id;
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
        return "GetTeleplayDetailRequest{" +
                "channel='" + channel + '\'' +
                ", video_id=" + video_id +
                '}';
    }

    public String getChannel() {
        return channel;
    }

    public int getVideo_id() {
        return video_id;
    }
}
