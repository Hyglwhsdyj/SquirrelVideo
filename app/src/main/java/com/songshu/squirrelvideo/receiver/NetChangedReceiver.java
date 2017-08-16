package com.songshu.squirrelvideo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.NetworkUtil;

/**
 * Created by yb on 15-10-23.
 */
public class NetChangedReceiver extends BroadcastReceiver {

    private static final String TAG = NetChangedReceiver.class.getSimpleName() + ":";

    @Override
    public void onReceive(Context context, Intent intent) {
        L.d(TAG, "......onReceive......action......" + intent.getAction());
        if (!NetworkUtil.isNetworkAvailable(context)) {
            App.showToast(R.string.net_changed_no_network_toast);
            App.getMyDownloadManager().pauseAllDownloadingTask();
        }
    }
}
