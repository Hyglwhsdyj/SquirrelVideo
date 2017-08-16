package com.songshu.squirrelvideo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.manager.LocalMediaManager;
import com.songshu.squirrelvideo.service.MediaScannerService;
import com.songshu.squirrelvideo.utils.FileUtils;
import com.songshu.squirrelvideo.utils.L;


/**
 * Created by yb on 15-10-30.
 */
public class MediaScannerReceiver extends BroadcastReceiver {

    private static final String TAG = MediaScannerReceiver.class.getSimpleName() + ":";

    @Override
    public void onReceive(Context context, Intent intent) {
        L.d(TAG, "......onReceive......action......" + intent.getAction());
        String paths = FileUtils.getEverySDPath(App.getContext());
        L.d(TAG, "######storage_paths : " + paths);
        LocalMediaManager.setPathToVariables(paths.split("&"));
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            scanDirectory(context, Const.PARENT_SDCARD);
        } else if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
            scanDirectory(context, Const.PARENT_SDCARD);
        } else if (intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)) {
            scanDirectory(context, Const.PARENT_SDCARD);
        }
    }


    /**
     * 扫描文件夹
     */
    private void scanDirectory(Context context, String volume) {
        Bundle args = new Bundle();
        args.putString(MediaScannerService.EXTRA_DIRECTORY, volume);
        context.startService(new Intent(context, MediaScannerService.class).putExtras(args));
    }


    /**
     * 扫描文件
     *
     * @param context
     * @param path
     */
    private void scanFile(Context context, String path) {
        Bundle args = new Bundle();
        args.putString(MediaScannerService.EXTRA_FILE_PATH, path);
        context.startService(new Intent(context, MediaScannerService.class).putExtras(args));
    }

}
