package com.songshu.squirrelvideo.listener;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.entity.MovieSelectionNetData;
import com.songshu.squirrelvideo.utils.L;

import de.greenrobot.event.EventBus;

/**
 * Created by yb on 15-7-6.
 */
public class GetMovieSelectionRequestListener extends BaseListener implements RequestListener<MovieSelectionNetData> {
    private static final String TAG = GetMovieSelectionRequestListener.class.getSimpleName() + ":";

    private String channel;
    private String type;
    private String area;
    private String year;
    private int page;

    public GetMovieSelectionRequestListener(String reqKey, String channel, String type, String area, String year, int page) {
        mReqKey = reqKey;
        this.channel = channel;
        this.type = type;
        this.area = area;
        this.year = year;
        this.page = page;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        L.d(TAG, "onRequestFailure:" + spiceException);
        spiceException.printStackTrace();
        EventBus.getDefault().post(new AppEvent.FailGetMovieSelectionEvent(mReqKey, channel, type, area, year, page));
    }

    @Override
    public void onRequestSuccess(final MovieSelectionNetData mMovieSelectionNetData) {
        L.d(TAG, "onRequestSuccess:mReqKey:" + mReqKey);
        EventBus.getDefault().post(new AppEvent.SucGetMovieSelectionEvent(mReqKey, mMovieSelectionNetData, channel, type, area, year, page));
    }
}
