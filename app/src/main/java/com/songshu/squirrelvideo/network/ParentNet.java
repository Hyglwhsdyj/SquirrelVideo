package com.songshu.squirrelvideo.network;

/**
 * Created by yb on 15-7-13.
 */

import com.songshu.squirrelvideo.utils.L;

import org.apache.http.Header;

import java.util.Locale;

/**
 * 所有网络操作类的父类
 */
public class ParentNet {

    /**
     * 打印HTTP请求返回的状态码
     *
     * @param TAG
     * @param statusCode
     */
    public void debugStatusCode(String TAG, int statusCode) {
        String msg = String.format(Locale.US, "Return Status Code: %d", statusCode);
        L.d(TAG + "返回HTTP状态码", msg);
    }

    /**
     * 打印HTTP请求的头部信息
     *
     * @param TAG
     * @param headers
     */
    public void debugHeaders(String TAG, Header[] headers) {
        if (headers != null) {
            L.d(TAG, "返回HTTP头部信息:");
            StringBuilder builder = new StringBuilder();
            for (Header h : headers) {
                String _h = String.format(Locale.US, "%s : %s", h.getName(), h.getValue());
                // Log.d(TAG, _h);
                L.d(TAG, _h);
                builder.append(_h);
                builder.append("\n");
            }
        }
    }

    /**
     * 打印HTTP请求的异常信息
     *
     * @param TAG
     * @param t
     */
    public void debugThrowable(String TAG, Throwable t) {
        if (t != null) {
            System.err.println(TAG + "请求出现错误" + "," + t.toString());
        }
    }
}
