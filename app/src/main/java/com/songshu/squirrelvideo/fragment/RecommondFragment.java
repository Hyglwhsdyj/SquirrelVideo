package com.songshu.squirrelvideo.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.activity.FuncsActivity;
import com.songshu.squirrelvideo.activity.SearchActivity;
import com.songshu.squirrelvideo.adapter.HealthAndOperaFragmentAdapter;
import com.songshu.squirrelvideo.adapter.MovieRecommAdapter;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.MovieRecommData;
import com.songshu.squirrelvideo.entity.VideoBean;
import com.songshu.squirrelvideo.entity.VideoTypeBean;
import com.songshu.squirrelvideo.listener.GetMovieRecommRequestListener;
import com.songshu.squirrelvideo.manager.DataManager;
import com.songshu.squirrelvideo.manager.TitleManager;
import com.songshu.squirrelvideo.request.GetMovieRecommRequest;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.NetworkUtil;
import com.songshu.squirrelvideo.utils.Util;
import com.songshu.squirrelvideo.view.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yb on 15-7-7.
 */
public class RecommondFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = RecommondFragment.class.getSimpleName() + ":";

    private View rootView;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recommond, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParam();
        decideChannel();
        initView();
        setListener();
        setAdapter();
        networkUI();
        loadFromLocal();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("RecommondFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("RecommondFragment");
    }

    private VideoTypeBean videoTypeBean;

    private void initParam() {
        Bundle bundle = getArguments();
        videoTypeBean = (VideoTypeBean) bundle.getSerializable("video_type");
        L.d(TAG, "initParam --> video_type : " + videoTypeBean);
    }

    private String channel;

    private void decideChannel() {
        channel = TitleManager.getEngNameMap().get(videoTypeBean.mTitle);
        L.d(TAG, "decideChannel --> channel : " + channel);
    }

    private TextView tv_top_bar_search;
    private TextView tv_top_bar_history;
    private TextView tv_top_bar_collect;
    private TextView tv_top_bar_download;
    private LinearLayout ll_right_error_content;
    private TextView tv_right_error_content_retry;
    private TextView tv_right_error_content_go;
    private LinearLayout ll_right_loading;
    private LinearLayout ll_right_content;
    private ImageView iv_right_loading;
    private PullToRefreshListView lv_video;

    private void initView() {
        mContext = getActivity();
        ll_right_content = (LinearLayout) rootView.findViewById(R.id.ll_right_content);
        lv_video = (PullToRefreshListView) rootView.findViewById(R.id.lv_video);
        tv_top_bar_search = (TextView) rootView.findViewById(R.id.tv_top_bar_search);
        tv_top_bar_history = (TextView) rootView.findViewById(R.id.tv_top_bar_history);
        tv_top_bar_collect = (TextView) rootView.findViewById(R.id.tv_top_bar_collect);
        tv_top_bar_download = (TextView) rootView.findViewById(R.id.tv_top_bar_download);

        ll_right_error_content = (LinearLayout) rootView.findViewById(R.id.ll_right_error_content);
        tv_right_error_content_retry = (TextView) rootView.findViewById(R.id.tv_right_error_content_retry);
        tv_right_error_content_go = (TextView) rootView.findViewById(R.id.tv_right_error_content_go);

        ll_right_loading = (LinearLayout) rootView.findViewById(R.id.ll_right_loading);
        iv_right_loading = (ImageView) rootView.findViewById(R.id.iv_right_loading);
    }

    private void setListener() {
        tv_top_bar_search.setOnClickListener(this);
        tv_top_bar_history.setOnClickListener(this);
        tv_top_bar_collect.setOnClickListener(this);
        tv_top_bar_download.setOnClickListener(this);
        tv_right_error_content_retry.setOnClickListener(this);
        tv_right_error_content_go.setOnClickListener(this);
    }

    private MovieRecommAdapter mMovieRecommAdapter;
    private HealthAndOperaFragmentAdapter mHealthAndOperaFragmentAdapter;
    private List<VideoBean> videoBeanList;
    private boolean isPull;

    private void setAdapter() {
        lv_video.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    lv_video.onRefreshComplete();
                    App.showToast(getString(R.string.please_check_network));
                    return;
                }
                isPull = true;
                loadData();

            }
        });

        videoBeanList = null;
        if (Const.TITLE.equals(TitleManager.TITLE_HEALTH) || Const.TITLE.equals(TitleManager.TITLE_OPERA)) {// 曲艺 少儿 每行显示 3个 不同 adapter
            mHealthAndOperaFragmentAdapter = new HealthAndOperaFragmentAdapter(mContext, videoBeanList);
            lv_video.setAdapter(mHealthAndOperaFragmentAdapter);
        } else {
            mMovieRecommAdapter = new MovieRecommAdapter(mContext, videoBeanList);
            lv_video.setAdapter(mMovieRecommAdapter);
        }
    }

    private boolean isDataLoadFromLocal = false;

    private void loadFromLocal() {
        isDataLoadFromLocal = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                GetMovieRecommRequest request = new GetMovieRecommRequest(channel, "4");
                cacheKey = request.getCacheKey();
                DataManager.getMovieRecomm(request, new GetMovieRecommRequestListener(cacheKey, request.getChannel(), request.getLimit()));
            }
        });
    }

    private void loadData() {
        mHandler.post(runnable);
    }

    private String cacheKey;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            GetMovieRecommRequest request = new GetMovieRecommRequest(channel, "4");
            cacheKey = request.getCacheKey();
            DataManager.getMovieRecomm(request, new GetMovieRecommRequestListener(cacheKey, request.getChannel(), request.getLimit()), true);
        }
    };

    private MovieRecommData[] dataForCompare;

    public void onEventMainThread(AppEvent.SucGetMovieRecommEvent e) {
        L.d(TAG, "Event --> MovieRecomm --> local_cache_key : " + cacheKey + " , net_cache_key :　" + e.reqKey);
        if (!cacheKey.equals(e.reqKey)) return;

        if (isPull) {
            isPull = false;
            lv_video.onRefreshComplete();
            if (Util.compareTwoArrayLength(dataForCompare, e.mMovieRecommNetData.data)) {
                App.showToast(getString(R.string.no_update_data));
            }
        } else {
            successUI();
        }

        if (Util.compareTwoArrayLength(dataForCompare, e.mMovieRecommNetData.data)) {
            L.d(TAG, "onEventMainThread --> 比较返回的结果 : true");
            return;
        }
        dataForCompare = e.mMovieRecommNetData.data;
        L.d(TAG, "dataForCompare : length : " + dataForCompare.length);

        List<VideoBean> tmpList = new LinkedList<VideoBean>();
        MovieRecommData[] data = e.mMovieRecommNetData.data;
        for (int i = 0; i < data.length; i++) {
            L.d(TAG, "id : " + data[i].category_id + " , name : " + data[i].category_name + " , list : " + Arrays.asList(data[i].video_list));
            tmpList.addAll(Arrays.asList(data[i].video_list));
        }
        videoBeanList = tmpList;
        L.d(TAG, "videoBeanList size : " + videoBeanList.size());
        if (Const.TITLE.equals(TitleManager.TITLE_HEALTH) || Const.TITLE.equals(TitleManager.TITLE_OPERA)) {
            mHealthAndOperaFragmentAdapter.notifyData(videoBeanList);
        } else {
            mMovieRecommAdapter.notifyData(videoBeanList);
        }

        if (isDataLoadFromLocal) { // 从本地缓存并显示界面之后, 请求网络, 仅一次
            L.d(TAG, "...............................这里就应该出现一次...............................");
            isDataLoadFromLocal = false;
            loadData();
        }
    }

    public void onEventMainThread(AppEvent.FailGetMovieRecommEvent e) {
        L.d(TAG, "... onEvent --> MovieRecomm --> Fail ...");
        if (isPull) {
            isPull = false;
            lv_video.onRefreshComplete();
        } else {
            errorUI();
        }
    }


    private void networkUI() {
        ll_right_loading.setVisibility(View.VISIBLE);
        iv_right_loading.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.loading_anim));
        ll_right_content.setVisibility(View.GONE);
    }

    private void successUI() {
        ll_right_loading.setVisibility(View.GONE);
        iv_right_loading.clearAnimation();
//        ((AnimationDrawable) iv_right_loading.getBackground()).stop();
        ll_right_content.setVisibility(View.VISIBLE);
        lv_video.setVisibility(View.VISIBLE);
        ll_right_error_content.setVisibility(View.GONE);

    }

    private void errorUI() {
        ll_right_loading.setVisibility(View.GONE);
        iv_right_loading.clearAnimation();
//        ((AnimationDrawable) iv_right_loading.getBackground()).stop();
        ll_right_content.setVisibility(View.VISIBLE);
        lv_video.setVisibility(View.GONE);
        ll_right_error_content.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_top_bar_search:
                startActivity(new Intent(mContext, SearchActivity.class));
                break;
            case R.id.tv_top_bar_history:
                jumpActivity(TYPE_JUMP_HISTORY);
                break;
            case R.id.tv_top_bar_collect:
                jumpActivity(TYPE_JUMP_COLLECT);
                break;
            case R.id.tv_top_bar_download:
                jumpActivity(TYPE_JUMP_DOWNLOAD);
                break;
            case R.id.tv_right_error_content_retry:
                networkUI();
                loadData();
                break;
            case R.id.tv_right_error_content_go:
                Intent intent = App.getContext().getPackageManager().getLaunchIntentForPackage("com.songshu.squirrelvideo");
                if (intent != null) {
                    intent.putExtra("video_type", 5);
                    App.getContext().startActivity(intent);
                } else {
                }
                getActivity().finish();
                break;
        }
    }

    public static final String TYPE_JUMP = "type_jump";
    public static final int TYPE_JUMP_HISTORY = 0;
    public static final int TYPE_JUMP_COLLECT = 1;
    public static final int TYPE_JUMP_DOWNLOAD = 2;

    private void jumpActivity(int type_jump) {
        Intent mIntent = new Intent(mContext, FuncsActivity.class);
        mIntent.putExtra(TYPE_JUMP, type_jump);
        startActivity(mIntent);
        L.d(TAG, "jumpActivity -- > type_jump : " + type_jump);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
    }
}
