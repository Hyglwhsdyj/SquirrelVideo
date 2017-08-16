package com.songshu.squirrelvideo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.adapter.HotSearchAdapter;
import com.songshu.squirrelvideo.adapter.SearchHistoryAdapter;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.db.SearchHistoryDaoImpl;
import com.songshu.squirrelvideo.entity.DBSearchHistoryBean;
import com.songshu.squirrelvideo.entity.HotSearchBean;
import com.songshu.squirrelvideo.listener.GetHotSearchRequestListener;
import com.songshu.squirrelvideo.manager.DataManager;
import com.songshu.squirrelvideo.request.GetHotSearchRequest;
import com.songshu.squirrelvideo.utils.L;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yb on 15-7-21.
 */
public class SearchDefaultFragment extends BaseFragment {

    private static final String TAG = SearchDefaultFragment.class.getSimpleName() + ":";

    private View rootView;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_default, container, false);
        return rootView;
    }

    private SearchHistoryDaoImpl mSearchHistoryDaoImpl;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mSearchHistoryDaoImpl = new SearchHistoryDaoImpl(mContext);
        initView();
        setAdapter();
        setListener();
        getHotSearchData();
        getSearchHistory();
    }


    /**
     * 外界提供接口, 加载数据
     */
    public void join() {
        getHotSearchData();
        getSearchHistory();
    }


    private ListView lv_hot_search;
    private ListView lv_search_history;
    private TextView clear_search_history;
    private HotSearchAdapter mHotSearchAdapter;
    private SearchHistoryAdapter mSearchHistoryAdapter;

    private void initView() {
        lv_hot_search = (ListView) rootView.findViewById(R.id.lv_hot_search);
        lv_search_history = (ListView) rootView.findViewById(R.id.lv_search_history);
        clear_search_history = (TextView) rootView.findViewById(R.id.clear_search_history);
        mHotSearchAdapter = new HotSearchAdapter(mContext);
        mSearchHistoryAdapter = new SearchHistoryAdapter(mContext);
    }


    private void setAdapter() {
        lv_hot_search.setAdapter(mHotSearchAdapter);
        lv_search_history.setAdapter(mSearchHistoryAdapter);
    }


    private void setListener() {
        lv_hot_search.setOnItemClickListener(hotSearchItemClickListener);
        clear_search_history.setOnClickListener(clearListener);
        mSearchHistoryAdapter.setOnDataChangeListener(mDataChangeListener);
    }


    private String cache_key_local;

    private void getHotSearchData() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                GetHotSearchRequest request = new GetHotSearchRequest("9", Const.CHANNEL);
                cache_key_local = request.getCacheKey();
                DataManager.getHotSearch(request, new GetHotSearchRequestListener(cache_key_local, request.getLimit(), request.getChannel()), true);
            }
        });
    }


    private List<HotSearchBean> hotSearchDataList = new ArrayList<HotSearchBean>();

    public void onEventMainThread(AppEvent.SucGetHotSearchEvent e) {
        L.d(TAG, "Event --> HotSearch --> local_cache_key : " + cache_key_local + " , net_cache_key :　" + e.reqKey);
        if (!cache_key_local.equals(e.reqKey)) return;
        hotSearchDataList = Arrays.asList(e.mHotSearchBeanNetData.data);
        mHotSearchAdapter.notifyData(hotSearchDataList);
    }

    public void onEventMainThread(AppEvent.FailGetHotSearchEvent e) {
        L.d(TAG, "... onEvent --> FailGetHotSearchEvent ...");
        // TODO 获取热门搜索失败
        mHotSearchAdapter.notifyData(null);
    }


    private void getSearchHistory() {
        loadSearchHistoryDataFromDbAndSetAdapter();
    }


    /**
     * 清空按钮的点击事件
     */
    private View.OnClickListener clearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean result = mSearchHistoryDaoImpl.deleteAllSearchHistory();
            if (result) {
                loadSearchHistoryDataFromDbAndSetAdapter();
            }
        }
    };


    /**
     * 热门搜索的点击事件监听
     */
    private AdapterView.OnItemClickListener hotSearchItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            setSearchDataToDB(hotSearchDataList.get(position).content);
            loadSearchHistoryDataFromDbAndSetAdapter();
            if (mSearchDefaultFragmentItemClickListener != null) {
                L.d(TAG, "HotSearchItemClick : " + hotSearchDataList.get(position));
                mSearchDefaultFragmentItemClickListener.hotSearchItemClick(hotSearchDataList.get(position));
            }
        }
    };


    /**
     * item删除时的监听
     */
    private SearchHistoryAdapter.DataChangeListener mDataChangeListener = new SearchHistoryAdapter.DataChangeListener() {
        @Override
        public void dataDelete(String name) {
            boolean result = mSearchHistoryDaoImpl.deleteSearchHistoryItem(name, Const.CHANNEL);
            L.d(TAG, "要删除的名字 : " + name + " , 删除某一条搜索记录是否成功 : " + result);
            if (result) {
                loadSearchHistoryDataFromDbAndSetAdapter();
            }
        }

        @Override
        public void dataAdd(int position) {
            L.d(TAG, "点击的位置 : " + position);
            setSearchDataToDB(searchHistorys.get(position).historyTitle);
            loadSearchHistoryDataFromDbAndSetAdapter();
            if (mSearchDefaultFragmentItemClickListener != null) {
                L.d(TAG, "SearchHistoryItemClick : " + searchHistorys.get(position));
                mSearchDefaultFragmentItemClickListener.SearchHistoryItemClick(searchHistorys.get(position));
            }
        }

    };


    /**
     * 向数据库添加数据
     *
     * @param historyTitle
     */
    private void setSearchDataToDB(String historyTitle) {
        DBSearchHistoryBean bean = new DBSearchHistoryBean(historyTitle, Const.CHANNEL, String.valueOf(System.currentTimeMillis()));
        L.d(TAG, "setSearchDataToDB --> DBSearchHistoryBean : " + bean);
        boolean add = mSearchHistoryDaoImpl.addSearchHistory(bean);
        L.d(TAG, "是否成功存入数据库 : " + add);
        if (!add) {
            boolean updata = mSearchHistoryDaoImpl.updataSearchHistory(bean);
            L.d(TAG, "是否成功更新数据库 : " + updata);
        }
    }


    private List<DBSearchHistoryBean> searchHistorys = new ArrayList<DBSearchHistoryBean>();
    private List<String> dataList = new ArrayList<String>();


    /**
     * 获取数据库数据,并更新适配器
     */
    private void loadSearchHistoryDataFromDbAndSetAdapter() {
        boolean tableEmpty = mSearchHistoryDaoImpl.isTableEmpty();
        dataList.clear();
        if (!tableEmpty) {
            searchHistorys = mSearchHistoryDaoImpl.findSearchHistorys();
            L.d(TAG, "数据库原始数据 : " + searchHistorys);

            if (searchHistorys != null && searchHistorys.size() > 0) {
                for (DBSearchHistoryBean bean : searchHistorys) {
                    dataList.add(bean.historyTitle);
                }
            }
            L.d(TAG, "用于Adapter显示的数据 : " + dataList);
        }
        mSearchHistoryAdapter.notifyData(dataList);
    }

    @Override
    public void onResume() {
        L.d(TAG, ".......onResume......");
        super.onResume();
        MobclickAgent.onPageStart("SearchDefaultFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SearchDefaultFragment");
    }

    /**
     * 空方法
     *
     * @param e
     */
    public void onEventMainThread(AppEvent.NothingHappen e) {
        L.d(TAG, "... onEvent --> NothingHappen ...");
    }


    private SearchDefaultFragmentItemClickListener mSearchDefaultFragmentItemClickListener;

    public void setOnSearchDefaultFragmentItemClickListener(SearchDefaultFragmentItemClickListener mSearchDefaultFragmentItemClickListener) {
        this.mSearchDefaultFragmentItemClickListener = mSearchDefaultFragmentItemClickListener;
    }

    public interface SearchDefaultFragmentItemClickListener {
        void hotSearchItemClick(HotSearchBean bean);

        void SearchHistoryItemClick(DBSearchHistoryBean bean);
    }

}
