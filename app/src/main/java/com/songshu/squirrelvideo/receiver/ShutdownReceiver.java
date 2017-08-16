package com.songshu.squirrelvideo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.utils.L;

/**
 * Created by yb on 15-10-23.
 */
public class ShutdownReceiver extends BroadcastReceiver {

    private static final String TAG = ShutdownReceiver.class.getSimpleName() + ":";

    @Override
    public void onReceive(Context context, Intent intent) {
        L.d(TAG, "......onReceive......action......" + intent.getAction());
        App.getMyDownloadManager().pauseAllDownloadingTask();
    }
}
