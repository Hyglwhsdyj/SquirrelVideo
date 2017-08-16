package com.songshu.squirrelvideo.network;

import android.text.TextUtils;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.entity.ChildVideoBean;
import com.songshu.squirrelvideo.entity.ParentVideoBean;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.Util;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by yb on 15-7-13.
 */
public class VideoUrlNet extends ParentNet implements VideoRealUrlNet.OnVideoRealUrlListener {

    private static final String TAG = VideoUrlNet.class.getSimpleName() + ":";
    private OnNewVideoUrlListener mListener;
    private boolean isCancel;

    public static final String SOURCE_QQ = "QQ";
    public static final String SOURCE_YOUKU = "优酷网";
    public static final String SOURCE_SOHU = "搜狐";
    public static final String SOURCE_QIYI = "奇艺网";
    public static final String SOURCE_LETV = "乐视网";
    public static final String SOURCE_TUDOU = "土豆网";

    private ParentVideoBean parentVideoBean;
    private List<ChildVideoBean> childVideoBeanList;
    private Map<String, String> urlMap;
    private Map<String, String> qqUrlMap;

    private VideoRealUrlNet realUrlNet;

    private String cacheKeyChannel;
    private String cacheKeySource;
    private String cacheKeyUrl;

    public String getCacheKey() {
        String ori = "TAG:" + TAG + ",cacheKeyChannel:" + cacheKeyChannel + ",cacheKeySource:" + cacheKeySource + ",cacheKeyUrl:" + cacheKeyUrl;
        String md5 = Util.MD5(ori);
        L.d(TAG, "ori : " + ori + " , md5Key : " + md5);
        return md5;
    }

    public void getVideoPath(final String channel, final String source, final String url) {
        cacheKeyChannel = channel;
        cacheKeySource = source;
        cacheKeyUrl = url;

        realUrlNet = new VideoRealUrlNet();
        realUrlNet.setListener(this);

        RequestParams params = new RequestParams();
        params.put("channel", channel);
        params.put("platform", "Android");
        params.put("source", source);
        params.put("play_url", url);
        params.put("clientparse", 1);

        L.d(TAG, "param : " + params.toString());

        RestClient.get("/api/video/get_real_src", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                debugHeaders(TAG, headers);
                debugStatusCode(TAG, statusCode);
                if (isCancel) {
                    L.d(TAG, "网络请求中断");
                    return;
                }

                try {
                    JSONObject jsonObject = response.getJSONObject("data");
                    int isok = jsonObject.getInt("isok");
                    L.d(TAG, "................ isok : " + isok);
                    if (isok == 1) { // 等于1的时候代表有视频地址，其他值没有
                        String title = jsonObject.getString("title");
                        String site = jsonObject.getString("site");
                        String quality = jsonObject.getString("quality");
                        String format = jsonObject.getString("format");
                        String ts = "";
                        String te = "";
                        if (SOURCE_QQ.equals(site)) {
                            ts = jsonObject.getString("ts");
                            te = jsonObject.getString("te");
                        }
                        L.d(TAG, "视频基本信息 ----> title : " + title + " , site : " + site + " , quality : " + quality + " , format : " + format);
                        childVideoBeanList = new ArrayList<ChildVideoBean>();
                        JSONArray jsonArray = jsonObject.getJSONArray("files");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            String url = jsonObject2.getString("url");
                            String type = jsonObject2.getString("type");
                            String bytes = jsonObject2.getString("bytes");
                            String seconds = jsonObject2.getString("seconds");
                            String time = jsonObject2.getString("time");
                            String size = jsonObject2.getString("size");
                            String U = "";
                            if (SOURCE_QQ.equals(site)) {
                                U = jsonObject2.getString("U");
                            }
                            childVideoBeanList.add(new ChildVideoBean(url, url, type, seconds, time, bytes, size, U));
                        }
                        parentVideoBean = new ParentVideoBean(title, site, quality, format, childVideoBeanList, ts, te);

                        if (!TextUtils.isEmpty(site) && SOURCE_QIYI.equals(site)) {
                            L.d(TAG, "解析爱奇艺真实播放地址 ---->");
                            urlMap = new HashMap<String, String>();
                            for (int i = 0; i < childVideoBeanList.size(); i++) {
                                urlMap.put(childVideoBeanList.get(i).getOri_url(), "");
                            }
                            for (String oriUrl : urlMap.keySet()) {
                                realUrlNet.getVideoRealPath(SOURCE_QIYI, oriUrl);
                            }

                        } else if (!TextUtils.isEmpty(site) && SOURCE_LETV.equals(site)) {
                            L.d(TAG, "解析乐视真实播放地址 ---->");
                            realUrlNet.getVideoRealPath(SOURCE_LETV, childVideoBeanList.get(0).getOri_url());

                        } else if (!TextUtils.isEmpty(site) && SOURCE_QQ.equals(site)) {
                            L.d(TAG, "解析腾讯真实播放地址 ---->");
                            qqUrlMap = new HashMap<String, String>();
                            for (int i = 0; i < childVideoBeanList.size(); i++) {
                                qqUrlMap.put(childVideoBeanList.get(i).getOri_url(), "");
                            }
                            for (String oriUrl : qqUrlMap.keySet()) {
                                realUrlNet.getVideoRealPath(SOURCE_QQ, oriUrl, ts, te);
                            }
                        } else if (
                                (!TextUtils.isEmpty(site) && SOURCE_QQ.equals(site)) ||
                                        (!TextUtils.isEmpty(site) && SOURCE_YOUKU.equals(site)) ||
                                        (!TextUtils.isEmpty(site) && SOURCE_SOHU.equals(site)) ||
                                        (!TextUtils.isEmpty(site) && SOURCE_TUDOU.equals(site))
                                ) {
                            if (mListener != null) {
                                mListener.requestVideoUrlSuccess(parentVideoBean);
                            } else {
                                EventBus.getDefault().post(new AppEvent.SucGetVideoRealPathEvent(getCacheKey(), parentVideoBean, channel, source, url));
                            }

                        } else {
                            if (mListener != null) {
                                mListener.requestVideoUrlFailure();
                            } else {
                                EventBus.getDefault().post(new AppEvent.FailGetVideoRealPathEvent(getCacheKey(), channel, source, url));
                            }
                        }
                    } else {
                        if (mListener != null) {
                            mListener.requestVideoUrlFailure();
                        } else {
                            EventBus.getDefault().post(new AppEvent.FailGetVideoRealPathEvent(getCacheKey(), channel, source, url));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (mListener != null) {
                        mListener.requestVideoUrlFailure();
                    } else {
                        EventBus.getDefault().post(new AppEvent.FailGetVideoRealPathEvent(getCacheKey(), channel, source, url));
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
                if (mListener != null) {
                    mListener.requestVideoUrlFailure();
                } else {
                    EventBus.getDefault().post(new AppEvent.FailGetVideoRealPathEvent(getCacheKey(), channel, source, url));
                }
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
                if (mListener != null) {
                    mListener.requestVideoUrlFailure();
                } else {
                    EventBus.getDefault().post(new AppEvent.FailGetVideoRealPathEvent(getCacheKey(), channel, source, url));
                }
            }

        });
    }

    @Override
    public void requestVideoRealUrlSuccess(String source, String oriUrl, String realUrl) {
        L.d(TAG, "requestVideoRealUrlSuccess --> source : " + source + " , oriUrl : " + oriUrl + " , realUrl : " + realUrl);
        if (SOURCE_QIYI.equals(source)) {
            if (urlMap.containsKey(oriUrl)) {
                for (ChildVideoBean bean : childVideoBeanList) {
                    if (bean.getOri_url().equals(oriUrl)) {
                        bean.setUrl(realUrl);
                        break;
                    }
                }
                urlMap.remove(oriUrl);
                if (urlMap.isEmpty()) {
                    L.d(TAG, "............urlMap空了,获取了所有分片段的真实播放地址,向播放器返回parentVideoBean对象.............");
                    if (mListener != null) {
                        mListener.requestVideoUrlSuccess(parentVideoBean);
                    } else {
                        EventBus.getDefault().post(new AppEvent.SucGetVideoRealPathEvent(getCacheKey(), parentVideoBean, cacheKeyChannel, cacheKeySource, cacheKeyUrl));
                    }
                }
            } else {
                L.d(TAG, "requestVideoRealUrlSuccess --> map里没有这个地址,我擦");
            }

        } else if (SOURCE_LETV.equals(source)) {
            if (childVideoBeanList.get(0).getOri_url().equals(oriUrl)) {
                childVideoBeanList.get(0).setUrl(realUrl);
                if (mListener != null) {
                    mListener.requestVideoUrlSuccess(parentVideoBean);
                } else {
                    EventBus.getDefault().post(new AppEvent.SucGetVideoRealPathEvent(getCacheKey(), parentVideoBean, cacheKeyChannel, cacheKeySource, cacheKeyUrl));
                }
            }
        } else if (SOURCE_QQ.equals(source)) {
            if (qqUrlMap.containsKey(oriUrl)) {
                for (ChildVideoBean bean : childVideoBeanList) {
                    if (bean.getOri_url().equals(oriUrl)) {
                        String left = realUrl.split(parentVideoBean.ts)[1];
                        String key = left.split(parentVideoBean.te)[0];
                        bean.setUrl(bean.U.replace("vvvkk123", key));
                        L.d(TAG, "............QQ 视频 子片段 真实播放地址 : " + bean.getUrl());
                        break;
                    }
                }
                qqUrlMap.remove(oriUrl);
                if (qqUrlMap.isEmpty()) {
                    L.d(TAG, "............urlMap空了,获取了所有分片段的真实播放地址,向播放器返回parentVideoBean对象.............");
                    if (mListener != null) {
                        mListener.requestVideoUrlSuccess(parentVideoBean);
                    } else {
                        EventBus.getDefault().post(new AppEvent.SucGetVideoRealPathEvent(getCacheKey(), parentVideoBean, cacheKeyChannel, cacheKeySource, cacheKeyUrl));
                    }
                }
            } else {
                L.d(TAG, "requestVideoRealUrlSuccess --> map里没有这个地址,我擦");
            }
        }

    }

    @Override
    public void requestVideoRealUrlFailure(String source, String oriUrl) {
        L.d(TAG, "请求真实播放地址失败,源地址 : " + oriUrl);
        if (mListener != null) {
            mListener.requestVideoUrlFailure();
        } else {
            EventBus.getDefault().post(new AppEvent.FailGetVideoRealPathEvent(getCacheKey(), cacheKeyChannel, cacheKeySource, cacheKeyUrl));
        }
    }


    public interface OnNewVideoUrlListener {
        /**
         * 请求视频地址成功
         */
        void requestVideoUrlSuccess(ParentVideoBean parentVideo);

        /**
         * 请求视频地址失败
         */
        void requestVideoUrlFailure();
    }


    public void setListener(OnNewVideoUrlListener mListener) {
        this.mListener = mListener;
    }

    public void setCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }


}
