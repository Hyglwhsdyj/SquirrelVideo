package com.songshu.squirrelvideo.manager;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.entity.HotSearchBeanNetData;
import com.songshu.squirrelvideo.entity.MovieDetailNetData;
import com.songshu.squirrelvideo.entity.MovieRecommNetData;
import com.songshu.squirrelvideo.entity.MovieSelectionNetData;
import com.songshu.squirrelvideo.entity.SearchResultBeanNetData;
import com.songshu.squirrelvideo.entity.TeleplayDetailNetData;
import com.songshu.squirrelvideo.listener.GetHotSearchRequestListener;
import com.songshu.squirrelvideo.listener.GetMovieDetailRequestListener;
import com.songshu.squirrelvideo.listener.GetMovieRecommRequestListener;
import com.songshu.squirrelvideo.listener.GetMovieSelectionRequestListener;
import com.songshu.squirrelvideo.listener.GetSearchResultDataRequestListener;
import com.songshu.squirrelvideo.listener.GetTeleplayDetailRequestListener;
import com.songshu.squirrelvideo.request.GetHotSearchRequest;
import com.songshu.squirrelvideo.request.GetMovieDetailRequest;
import com.songshu.squirrelvideo.request.GetMovieRecommRequest;
import com.songshu.squirrelvideo.request.GetMovieSelectionRequest;
import com.songshu.squirrelvideo.request.GetSearchResultDataRequest;
import com.songshu.squirrelvideo.request.GetTeleplayDetailRequest;
import com.songshu.squirrelvideo.utils.L;

import java.util.concurrent.ExecutionException;

import de.greenrobot.event.EventBus;

/**
 * Created by yb on 15-7-6.
 */
public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName() + ":";

    public static void getMovieRecomm(GetMovieRecommRequest request, GetMovieRecommRequestListener listener) {
        getMovieRecomm(request, listener, false);
    }

    public static void getMovieRecomm(GetMovieRecommRequest request, GetMovieRecommRequestListener listener, boolean isRefresh) {
        request.setIsRefresh(isRefresh);
        if (isRefresh) {
            App.getSpiceManager().execute(request, request.getCacheKey(), DurationInMillis.ALWAYS_EXPIRED, listener);
        } else {
            MovieRecommNetData cacheData = null;
            try {
                cacheData = App.getSpiceManager().getDataFromCache(MovieRecommNetData.class, request.getCacheKey()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (CacheLoadingException e) {
                e.printStackTrace();
            }
            if (cacheData != null) {
                L.d(TAG, "------> old getMovieRecomm data not null, post event to UI");
                EventBus.getDefault().post(new AppEvent.SucGetMovieRecommEvent(request.getCacheKey(), cacheData, request.getChannel(), request.getLimit()));
            } else {
                L.d(TAG, "old getMovieRecomm data null, load from net ------>");
                request.setIsRefresh(true);
                App.getSpiceManager().execute(request, request.getCacheKey(), DurationInMillis.ALWAYS_EXPIRED, listener);
            }
        }
    }

    public static void getMovieSelection(GetMovieSelectionRequest request, GetMovieSelectionRequestListener listener) {
        getMovieSelection(request, listener, false);
    }

    public static void getMovieSelection(GetMovieSelectionRequest request, GetMovieSelectionRequestListener listener, boolean isRefresh) {
        request.setIsRefresh(isRefresh);
        if (isRefresh) {
            App.getSpiceManager().execute(request, request.getCacheKey(), DurationInMillis.ALWAYS_EXPIRED, listener);
        } else {
            MovieSelectionNetData cacheData = null;
            try {
                cacheData = App.getSpiceManager().getDataFromCache(MovieSelectionNetData.class, request.getCacheKey()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (CacheLoadingException e) {
                e.printStackTrace();
            }
            if (cacheData != null) {
                L.d(TAG, "------> old getMovieSelection data not null, post event to UI");
                EventBus.getDefault().post(new AppEvent.SucGetMovieSelectionEvent(request.getCacheKey(), cacheData, request.getChannel(), request.getType(), request.getArea(), request.getYear(), request.getPage()));
            } else {
                L.d(TAG, "old getMovieSelection data null, load from net ------>");
                request.setIsRefresh(true);
                App.getSpiceManager().execute(request, request.getCacheKey(), DurationInMillis.ALWAYS_EXPIRED, listener);
            }
        }
    }

    public static void getMovieDetail(GetMovieDetailRequest request, GetMovieDetailRequestListener listener) {
        getMovieDetail(request, listener, false);
    }

    public static void getMovieDetail(GetMovieDetailRequest request, GetMovieDetailRequestListener listener, boolean isRefresh) {
        request.setIsRefresh(isRefresh);
        if (isRefresh) {
            App.getSpiceManager().execute(request, request.getCacheKey(), DurationInMillis.ALWAYS_EXPIRED, listener);
        } else {
            MovieDetailNetData cacheData = null;
            try {
                cacheData = App.getSpiceManager().getDataFromCache(MovieDetailNetData.class, request.getCacheKey()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (CacheLoadingException e) {
                e.printStackTrace();
            }
            if (cacheData != null) {
                L.d(TAG, "------> old getMovieDetail data not null, post event to UI");
                EventBus.getDefault().post(new AppEvent.SucGetMovieDetailEvent(request.getCacheKey(), cacheData, request.getChannel(), request.getVideo_id()));
            } else {
                L.d(TAG, "old getMovieDetail data null, load from net ------>");
                request.setIsRefresh(true);
                App.getSpiceManager().execute(request, request.getCacheKey(), DurationInMillis.ALWAYS_EXPIRED, listener);
            }
        }
    }


    public static void getTeleplayDetail(GetTeleplayDetailRequest request, GetTeleplayDetailRequestListener listener) {
        getTeleplayDetail(request, listener, false);
    }

    public static void getTeleplayDetail(GetTeleplayDetailRequest request, GetTeleplayDetailRequestListener listener, boolean isRefresh) {
        request.setIsRefresh(isRefresh);
        if (isRefresh) {
            App.getSpiceManager().execute(request, request.getCacheKey(), DurationInMillis.ALWAYS_EXPIRED, listener);
        } else {
            TeleplayDetailNetData cacheData = null;
            try {
                cacheData = App.getSpiceManager().getDataFromCache(TeleplayDetailNetData.class, request.getCacheKey()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (CacheLoadingException e) {
                e.printStackTrace();
            }
            if (cacheData != null) {
                L.d(TAG, "------> old getTeleplayDetail data not null, post event to UI");
                EventBus.getDefault().post(new AppEvent.SucGetTeleplayDetailEvent(request.getCacheKey(), cacheData, request.getChannel(), request.getVideo_id()));
            } else {
                L.d(TAG, "old getTeleplayDetail data null, load from net ------>");
                request.setIsRefresh(true);
                App.getSpiceManager().execute(request, request.getCacheKey(), DurationInMillis.ALWAYS_EXPIRED, listener);
            }
        }
    }


    public static void getHotSearch(GetHotSearchRequest request, GetHotSearchRequestListener listener) {
        getHotSearch(request, listener, false);
    }

    public static void getHotSearch(GetHotSearchRequest request, GetHotSearchRequestListener listener, boolean isRefresh) {
        request.setIsRefresh(isRefresh);
        if (isRefresh) {
            App.getSpiceManager().execute(request, request.getCacheKey(), DurationInMillis.ALWAYS_EXPIRED, listener);
        } else {
            HotSearchBeanNetData cacheData = null;
            try {
                cacheData = App.getSpiceManager().getDataFromCache(HotSearchBeanNetData.class, request.getCacheKey()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (CacheLoadingException e) {
                e.printStackTrace();
            }
            if (cacheData != null) {
                L.d(TAG, "------> old getHotSearch data not null, post event to UI");
                EventBus.getDefault().post(new AppEvent.SucGetHotSearchEvent(request.getCacheKey(), cacheData, request.getLimit(), request.getChannel()));
            } else {
                L.d(TAG, "old getHotSearch data null, load from net ------>");
                request.setIsRefresh(true);
                App.getSpiceManager().execute(request, request.getCacheKey(), DurationInMillis.ALWAYS_EXPIRED, listener);
            }
        }
    }


    public static void getSearchResultData(GetSearchResultDataRequest request, GetSearchResultDataRequestListener listener) {
        getSearchResultData(request, listener, false);
    }

    public static void getSearchResultData(GetSearchResultDataRequest request, GetSearchResultDataRequestListener listener, boolean isRefresh) {
        request.setIsRefresh(isRefresh);
        if (isRefresh) {
            App.getSpiceManager().execute(request, request.getCacheKey(), DurationInMillis.ALWAYS_EXPIRED, listener);
        } else {
            SearchResultBeanNetData cacheData = null;
            try {
                cacheData = App.getSpiceManager().getDataFromCache(SearchResultBeanNetData.class, request.getCacheKey()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (CacheLoadingException e) {
                e.printStackTrace();
            }
            if (cacheData != null) {
                L.d(TAG, "------> old getSearchResultData data not null, post event to UI");
                EventBus.getDefault().post(new AppEvent.SucGetSearchResultDataEvent(request.getCacheKey(), cacheData, request.getQuery_str(), request.getLimit(), request.getChannel()));
            } else {
                L.d(TAG, "old getSearchResultData data null, load from net ------>");
                request.setIsRefresh(true);
                App.getSpiceManager().execute(request, request.getCacheKey(), DurationInMillis.ALWAYS_EXPIRED, listener);
            }
        }
    }

}
