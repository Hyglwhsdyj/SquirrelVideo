package com.songshu.squirrelvideo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.adapter.SearchHasResultAdapter;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.SearchResultBeanNetData;
import com.songshu.squirrelvideo.entity.SearchResultSingleVideoBean;
import com.songshu.squirrelvideo.listener.GetSearchResultDataRequestListener;
import com.songshu.squirrelvideo.manager.DataManager;
import com.songshu.squirrelvideo.request.GetSearchResultDataRequest;
import com.songshu.squirrelvideo.utils.L;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yb on 15-7-21.
 */
public class SearchHasResultFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = SearchHasResultFragment.class.getSimpleName() + ":";

    private View rootView;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_has_result, container, false);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        initView();
        initAdapter();
    }


    private ListView lv_content_down_result;

    private void initView() {
        lv_content_down_result = (ListView) rootView.findViewById(R.id.lv_content_down_result);
    }

    private SearchHasResultAdapter mSearchHasResultAdapter;

    private void initAdapter() {
        mSearchHasResultAdapter = new SearchHasResultAdapter(mContext);
        lv_content_down_result.setAdapter(mSearchHasResultAdapter);
    }


    private String search_name;

    /**
     * 给外界提供进入的参数
     */
    public void join(String search_name) {
        this.search_name = search_name;
        L.d(TAG, "search_name --> " + search_name);
        mSearchHasResultAdapter.notifyData(null);
        loadData();
    }

    public String local_cache_key;

    private void loadData() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                GetSearchResultDataRequest request = new GetSearchResultDataRequest(search_name, 10, Const.CHANNEL);
                local_cache_key = request.getCacheKey();
                DataManager.getSearchResultData(request, new GetSearchResultDataRequestListener(request.getCacheKey(), request.getQuery_str(), request.getLimit(), request.getChannel()), true);
            }
        });
    }

    private SearchResultBeanNetData mSearchResultBeanNetData;
    private List<SearchResultSingleVideoBean> list = new ArrayList<SearchResultSingleVideoBean>();

    public void onEventMainThread(AppEvent.SucGetSearchResultDataEvent e) {
        L.d(TAG, "Event --> Search_Result --> local_cache_key : " + local_cache_key + " , net_cache_key :　" + e.reqKey);
        if (!local_cache_key.equals(e.reqKey)) return;
        mSearchResultBeanNetData = e.mSearchResultBeanNetData;
        list.clear();
        if (mSearchResultBeanNetData.data.datalist != null && mSearchResultBeanNetData.data.datalist.length > 0) {
            for (int i = 0; i < mSearchResultBeanNetData.data.datalist.length; i++) {
                list.add(mSearchResultBeanNetData.data.datalist[i]);
            }
        } else {
            if (mSearchHasResultFragmentListener != null) {
                mSearchHasResultFragmentListener.noSearchResult(search_name);
            }
        }
        mSearchHasResultAdapter.notifyData(list);
    }


    public void onEventMainThread(AppEvent.FailGetSearchResultDataEvent e) {
        L.d(TAG, "... onEvent --> Search_Result --> Fail ...");
        if (mSearchHasResultFragmentListener != null) {
            mSearchHasResultFragmentListener.noSearchResult(search_name);
        }
    }


    private SearchHasResultFragmentListener mSearchHasResultFragmentListener;

    public void setOnSearchHasResultFragmentListener(SearchHasResultFragmentListener mSearchHasResultFragmentListener) {
        this.mSearchHasResultFragmentListener = mSearchHasResultFragmentListener;
    }

    public interface SearchHasResultFragmentListener {
        void noSearchResult(String search_name);
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
}
