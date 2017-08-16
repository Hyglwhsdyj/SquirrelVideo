package com.songshu.squirrelvideo.listener;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.entity.MovieDetailNetData;
import com.songshu.squirrelvideo.utils.L;

import de.greenrobot.event.EventBus;

/**
 * Created by yb on 15-7-6.
 */
public class GetMovieDetailRequestListener extends BaseListener implements RequestListener<MovieDetailNetData> {
    private static final String TAG = GetMovieDetailRequestListener.class.getSimpleName() + ":";

    private String channel;
    private int video_id;

    public GetMovieDetailRequestListener(String reqKey, String channel, int video_id) {
        mReqKey = reqKey;
        this.channel = channel;
        this.video_id = video_id;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        L.d(TAG, "onRequestFailure:" + spiceException);
        spiceException.printStackTrace();
        EventBus.getDefault().post(new AppEvent.FailGetMovieDetailEvent(mReqKey, channel, video_id));
    }

    @Override
    public void onRequestSuccess(final MovieDetailNetData mMovieDetailNetData) {
        L.d(TAG, "onRequestSuccess:mReqKey:" + mReqKey);
        EventBus.getDefault().post(new AppEvent.SucGetMovieDetailEvent(mReqKey, mMovieDetailNetData, channel, video_id));
    }
}
