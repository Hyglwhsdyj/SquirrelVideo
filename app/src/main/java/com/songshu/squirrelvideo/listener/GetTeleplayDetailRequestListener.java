package com.songshu.squirrelvideo.listener;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.entity.MovieDetailNetData;
import com.songshu.squirrelvideo.entity.TeleplayDetailNetData;
import com.songshu.squirrelvideo.utils.L;

import de.greenrobot.event.EventBus;

/**
 * Created by yb on 15-7-6.
 */
public class GetTeleplayDetailRequestListener extends BaseListener implements RequestListener<TeleplayDetailNetData> {
    private static final String TAG = GetTeleplayDetailRequestListener.class.getSimpleName() + ":";

    private String channel;
    private int video_id;

    public GetTeleplayDetailRequestListener(String reqKey, String channel, int video_id) {
        mReqKey = reqKey;
        this.channel = channel;
        this.video_id = video_id;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        L.d(TAG, "onRequestFailure:" + spiceException);
        spiceException.printStackTrace();
        EventBus.getDefault().post(new AppEvent.FailGetTeleplayDetailEvent(mReqKey, channel, video_id));
    }

    @Override
    public void onRequestSuccess(final TeleplayDetailNetData mTeleplayDetailNetData) {
        L.d(TAG, "onRequestSuccess:mReqKey:" + mReqKey);
        EventBus.getDefault().post(new AppEvent.SucGetTeleplayDetailEvent(mReqKey, mTeleplayDetailNetData, channel, video_id));
    }
}
