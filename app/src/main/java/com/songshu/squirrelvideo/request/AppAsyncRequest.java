package com.songshu.squirrelvideo.request;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by yb on 15-7-6.
 */
public abstract class AppAsyncRequest<T, R> extends RetrofitSpiceRequest<T, R> {
    /**
     * isRefresh param means request data from net or not .
     */
    private boolean isRefresh;


    public AppAsyncRequest(Class<T> clazz, Class<R> retrofitedInterfaceClass) {
        super(clazz, retrofitedInterfaceClass);
    }

    public void setIsRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public boolean isRefresh() {
        return isRefresh;
    }
}
