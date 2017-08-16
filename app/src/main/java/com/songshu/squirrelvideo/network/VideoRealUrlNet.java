package com.songshu.squirrelvideo.network;

import android.text.TextUtils;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.songshu.squirrelvideo.utils.L;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by yb on 15-7-13.
 */
public class VideoRealUrlNet extends ParentNet {

    private static final String TAG = VideoRealUrlNet.class.getSimpleName() + ":";
    private OnVideoRealUrlListener mListener;
    private boolean isCancel;
    private String ts;
    private String te;

    public void getVideoRealPath(final String source, final String oriUrl, String ts, String te) {
        this.ts = ts;
        this.te = te;
        getVideoRealPath(source, oriUrl);
    }

    public void getVideoRealPath(final String source, final String oriUrl) {

        L.d(TAG, "请求真实视频播放的源地址 : " + oriUrl);

        RestClient.getStraight(oriUrl, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                debugHeaders(TAG, headers);
                debugStatusCode(TAG, statusCode);
                if (isCancel) {
                    L.d(TAG, "网络请求中断");
                    return;
                }

                if (VideoUrlNet.SOURCE_QIYI.equals(source)) {
                    try {
                        String qiyi_real_Url = response.getString("l");
                        if (!TextUtils.isEmpty(qiyi_real_Url)) {
                            mListener.requestVideoRealUrlSuccess(source, oriUrl, qiyi_real_Url);
                        } else {
                            mListener.requestVideoRealUrlFailure(source, oriUrl);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mListener.requestVideoRealUrlFailure(source, oriUrl);
                    }

                } else if (VideoUrlNet.SOURCE_LETV.equals(source)) {
                    try {
                        String letv_real_url = response.getString("location");
                        if (!TextUtils.isEmpty(letv_real_url)) {
                            mListener.requestVideoRealUrlSuccess(source, oriUrl, letv_real_url);
                        } else {
                            mListener.requestVideoRealUrlFailure(source, oriUrl);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mListener.requestVideoRealUrlFailure(source, oriUrl);
                    }
                }

                L.d(TAG, response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                debugHeaders(TAG, headers);
                debugStatusCode(TAG, statusCode);
                debugThrowable(TAG, throwable);
                if (isCancel) {
                    L.d(TAG, "网络请求中断");
                    return;
                }
                mListener.requestVideoRealUrlFailure(source, oriUrl);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                L.d(TAG, ".....................................onFailure........................................");
                debugStatusCode(TAG, statusCode);
                debugHeaders(TAG, headers);
                debugThrowable(TAG, throwable);
                L.d(TAG, "responseString : " + responseString);
                if (isCancel) {
                    L.d(TAG, "网络请求中断");
                    return;
                }
                if (VideoUrlNet.SOURCE_QQ.equals(source)) {
                    if (!TextUtils.isEmpty(responseString)
                            && responseString.contains(VideoRealUrlNet.this.ts)
                            && responseString.contains(VideoRealUrlNet.this.te)) {
                        mListener.requestVideoRealUrlSuccess(source, oriUrl, responseString);
                    } else {
                        mListener.requestVideoRealUrlFailure(source, oriUrl);
                    }
                } else {
                    mListener.requestVideoRealUrlFailure(source, oriUrl);
                }
            }

        });
    }


    public interface OnVideoRealUrlListener {
        /**
         * 请求视频地址成功
         */
        void requestVideoRealUrlSuccess(String source, String oriUrl, String realUrl);

        /**
         * 请求视频地址失败
         */
        void requestVideoRealUrlFailure(String source, String oriUrl);
    }


    public void setListener(OnVideoRealUrlListener mListener) {
        this.mListener = mListener;
    }

    public void setCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }


}
