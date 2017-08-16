package com.songshu.squirrelvideo.listener;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.entity.MovieRecommNetData;
import com.songshu.squirrelvideo.utils.L;

import de.greenrobot.event.EventBus;

/**
 * Created by yb on 15-7-6.
 */
public class GetMovieRecommRequestListener extends BaseListener implements RequestListener<MovieRecommNetData> {
    private static final String TAG = GetMovieRecommRequestListener.class.getSimpleName() + ":";

    private String channel;
    private String limit;

    public GetMovieRecommRequestListener(String reqKey, String channel, String limit) {
        mReqKey = reqKey;
        this.channel = channel;
        this.limit = limit;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        L.d(TAG, "onRequestFailure:" + spiceException);
        spiceException.printStackTrace();
        EventBus.getDefault().post(new AppEvent.FailGetMovieRecommEvent(mReqKey, channel, limit));
    }

    @Override
    public void onRequestSuccess(final MovieRecommNetData mMovieRecommNetData) {
        L.d(TAG, "onRequestSuccess:mReqKey:" + mReqKey);
        EventBus.getDefault().post(new AppEvent.SucGetMovieRecommEvent(mReqKey, mMovieRecommNetData, channel, limit));
    }
}
