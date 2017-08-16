package com.songshu.squirrelvideo.service;

import android.os.Environment;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.common.IAppApi;
import com.songshu.squirrelvideo.utils.FileUtils;
import com.songshu.squirrelvideo.utils.L;

import java.io.File;

/**
 * Created by yb on 15-7-4.
 */
public class AppRetrofitSpiceService extends RetrofitGsonSpiceService {

    private static final String TAG = AppRetrofitSpiceService.class.getSimpleName() + ":";

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(IAppApi.class);
    }

    @Override
    protected String getServerUrl() {
        L.d(TAG, "getServerUrl : " + Const.DOMAIN);
        return Const.DOMAIN;
    }

    @Override
    public File getCacheFolder() {
        L.d(TAG, "......StorageState : " + Environment.getExternalStorageState());
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return null;
        }
        return FileUtils.getRetrofitSpiceCacheFile();
    }
}
