package com.songshu.squirrelvideo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.FragmentActivity;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.songshu.squirrelvideo.application.App;

import de.greenrobot.event.EventBus;

/**
 * Created by yb on 15-7-4.
 */
public class  BaseActivity extends FragmentActivity {

    private static final String TAG = BaseActivity.class.getSimpleName() + ":";

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private HandlerThread mHandlerThread;
    protected Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandlerThread = new HandlerThread("base.activity", Thread.MIN_PRIORITY);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        App.getActivityManager().pushActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
