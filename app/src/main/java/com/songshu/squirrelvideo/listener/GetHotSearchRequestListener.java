package com.songshu.squirrelvideo.listener;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.entity.HotSearchBeanNetData;
import com.songshu.squirrelvideo.utils.L;

import de.greenrobot.event.EventBus;

/**
 * Created by yb on 15-7-6.
 */
public class GetHotSearchRequestListener extends BaseListener implements RequestListener<HotSearchBeanNetData> {
    private static final String TAG = GetHotSearchRequestListener.class.getSimpleName() + ":";

    private String limit;
    private String channel;


    public GetHotSearchRequestListener(String reqKey, String limit, String channel) {
        mReqKey = reqKey;
        this.limit = limit;
        this.channel = channel;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        L.d(TAG, "onRequestFailure:" + spiceException);
        spiceException.printStackTrace();
        EventBus.getDefault().post(new AppEvent.FailGetHotSearchEvent(mReqKey, limit, channel));
    }

    @Override
    public void onRequestSuccess(final HotSearchBeanNetData mHotSearchBeanNetData) {
        L.d(TAG, "onRequestSuccess:mReqKey:" + mReqKey);
        EventBus.getDefault().post(new AppEvent.SucGetHotSearchEvent(mReqKey, mHotSearchBeanNetData, limit, channel));
    }
}
