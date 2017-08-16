package com.songshu.squirrelvideo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;

import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

/**
 * Created by yb on 15-7-7.
 */
public class BaseFragment extends Fragment {

    private static final String TAG = BaseFragment.class.getSimpleName() + ":";

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private HandlerThread mHandlerThread;
    protected Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandlerThread = new HandlerThread("base.fragment", Thread.MIN_PRIORITY);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void join(){

    }
}
