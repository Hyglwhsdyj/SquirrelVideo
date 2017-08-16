package com.songshu.squirrelvideo.network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.songshu.squirrelvideo.common.Const;

/**
 * Created by yb on 15-7-13.
 */
public class RestClient {
    private static AsyncHttpClient client = new AsyncHttpClient();

    static {
        /** 设置连接超时时间，如果不设置，默认为10s */
        client.setTimeout(10 * 1000);
    }

    /**
     * 获取接口的绝对地址路径
     *
     * @param relativeUrl
     * @return
     */
    private static String getAbsoluteUrl(String relativeUrl) {
        return Const.DOMAIN + relativeUrl;
    }

    public static void getStraight(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    public static AsyncHttpClient getClient() {
        return client;
    }
}
