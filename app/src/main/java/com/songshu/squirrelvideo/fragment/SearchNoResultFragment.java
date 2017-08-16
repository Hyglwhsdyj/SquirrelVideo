package com.songshu.squirrelvideo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.utils.L;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by yb on 15-7-21.
 */
public class SearchNoResultFragment extends BaseFragment {

    private static final String TAG = SearchNoResultFragment.class.getSimpleName() + ":";

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_not_result, container, false);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SearchNoResultFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SearchNoResultFragment");
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
