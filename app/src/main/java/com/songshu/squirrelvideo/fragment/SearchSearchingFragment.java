package com.songshu.squirrelvideo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.adapter.QuicklySearchAdapter;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.HotSearchBean;
import com.songshu.squirrelvideo.entity.SearchResultSingleVideoBean;
import com.songshu.squirrelvideo.listener.GetSearchResultDataRequestListener;
import com.songshu.squirrelvideo.manager.DataManager;
import com.songshu.squirrelvideo.request.GetSearchResultDataRequest;
import com.songshu.squirrelvideo.utils.L;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yb on 15-7-21.
 */
public class SearchSearchingFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = SearchSearchingFragment.class.getSimpleName() + ":";

    private View rootView;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_searching, container, false);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        initView();
        setAdapter();
        setListener();
    }


    private ListView lv_content_down_searching;

    private void initView() {
        lv_content_down_searching = (ListView) rootView.findViewById(R.id.lv_content_down_searching);
    }

    private QuicklySearchAdapter mQuicklySearchAdapter;

    private void setAdapter() {
        mQuicklySearchAdapter = new QuicklySearchAdapter(mContext);
        lv_content_down_searching.setAdapter(mQuicklySearchAdapter);
    }

    private void setListener() {
        lv_content_down_searching.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSearchSearchingFragmentItemClickListener != null) {
                    L.d(TAG, "QuicklySearchItemClick : " + quicklySearchDataList.get(position));
                    mSearchSearchingFragmentItemClickListener.quicklySearchItemClick(quicklySearchDataList.get(position));
                }
            }
        });
    }


    /**
     * 提供请求数据的接口
     *
     * @param search_name
     */
    public void join(String search_name) {
        mQuicklySearchAdapter.notifyData(null);
        loadData(search_name);
    }

    private String local_cache_key;

    private void loadData(final String search_name) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                GetSearchResultDataRequest request = new GetSearchResultDataRequest(search_name, 9, Const.CHANNEL);
                local_cache_key = request.getCacheKey();
                DataManager.getSearchResultData(request, new GetSearchResultDataRequestListener(local_cache_key, request.getQuery_str(), request.getLimit(), request.getChannel()), true);
            }
        });
    }


    private List<SearchResultSingleVideoBean> quicklySearchDataList = new ArrayList<SearchResultSingleVideoBean>();

    public void onEventMainThread(AppEvent.SucGetSearchResultDataEvent e) {
        L.d(TAG, "Event --> HotSearch --> local_cache_key : " + local_cache_key + " , net_cache_key :　" + e.reqKey);
        if (!local_cache_key.equals(e.reqKey)) return;
        quicklySearchDataList = Arrays.asList(e.mSearchResultBeanNetData.data.datalist);
        mQuicklySearchAdapter.notifyData(quicklySearchDataList);
    }

    public void onEventMainThread(AppEvent.FailGetSearchResultDataEvent e) {
        L.d(TAG, "... onEvent --> FailGetHotSearchEvent ...");
        mQuicklySearchAdapter.notifyData(null);
    }


    private SearchSearchingFragmentItemClickListener mSearchSearchingFragmentItemClickListener;

    public void setOnSearchSearchingFragmentItemClickListener(SearchSearchingFragmentItemClickListener mSearchSearchingFragmentItemClickListener) {
        this.mSearchSearchingFragmentItemClickListener = mSearchSearchingFragmentItemClickListener;
    }

    public interface SearchSearchingFragmentItemClickListener {
        void quicklySearchItemClick(SearchResultSingleVideoBean bean);
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SearchHasResultFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SearchHasResultFragment");
    }

    @Override
    public void onClick(View v) {

    }


    /**
     * 空方法
     *
     * @param e
     */
    public void onEventMainThread(AppEvent.NothingHappen e) {
        L.d(TAG, "... onEvent --> NothingHappen ...");
    }
}
