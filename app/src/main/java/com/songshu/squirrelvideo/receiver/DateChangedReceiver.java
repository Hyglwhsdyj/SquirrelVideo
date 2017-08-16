package com.songshu.squirrelvideo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.songshu.squirrelvideo.utils.FileUtils;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.TimeUtils;

import java.io.File;

/**
 * Created by yb on 15-11-6.
 */
public class DateChangedReceiver extends BroadcastReceiver {
    private static final String TAG = DateChangedReceiver.class.getSimpleName() + ":";

    @Override
    public void onReceive(Context context, Intent intent) {
        L.d(TAG, "......onReceive......action : " + intent.getAction());
        for (File file : FileUtils.getLogCacheFile().listFiles()) {
            if (!file.getPath().contains(TimeUtils.getCurrentTimeInString(TimeUtils.DATE_FORMAT_DATE))) {
                file.delete();
            }
        }
    }

}
