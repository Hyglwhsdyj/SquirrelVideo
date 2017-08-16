package com.songshu.squirrelvideo.listener;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.entity.SearchResultBeanNetData;
import com.songshu.squirrelvideo.utils.L;

import de.greenrobot.event.EventBus;

/**
 * Created by yb on 15-7-6.
 */
public class GetSearchResultDataRequestListener extends BaseListener implements RequestListener<SearchResultBeanNetData> {
    private static final String TAG = GetSearchResultDataRequestListener.class.getSimpleName() + ":";

    private String query_str;
    private int limit;
    private String channel;

    public GetSearchResultDataRequestListener(String reqKey, String query_str, int limit, String channel) {
        mReqKey = reqKey;
        this.query_str = query_str;
        this.channel = channel;
        this.limit = limit;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        L.d(TAG, "onRequestFailure:" + spiceException);
        spiceException.printStackTrace();
        EventBus.getDefault().post(new AppEvent.FailGetSearchResultDataEvent(mReqKey, query_str, limit, channel));
    }

    @Override
    public void onRequestSuccess(final SearchResultBeanNetData mSearchResultBeanNetData) {
        L.d(TAG, "onRequestSuccess:mReqKey:" + mReqKey);
        EventBus.getDefault().post(new AppEvent.SucGetSearchResultDataEvent(mReqKey, mSearchResultBeanNetData, query_str, limit, channel));
    }
}
