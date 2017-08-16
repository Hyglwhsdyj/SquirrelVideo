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
import com.songshu.squirrelvideo.entity.MovieSelectionData;
import com.songshu.squirrelvideo.entity.Pictures;
import com.songshu.squirrelvideo.entity.VideoBean;
import com.songshu.squirrelvideo.entity.VideoTypeBean;
import com.songshu.squirrelvideo.entity.VideoTypeTransformBean;
import com.songshu.squirrelvideo.listener.GetMovieSelectionRequestListener;
import com.songshu.squirrelvideo.manager.DataManager;
import com.songshu.squirrelvideo.manager.TitleManager;
import com.songshu.squirrelvideo.request.GetMovieSelectionRequest;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.NetworkUtil;
import com.songshu.squirrelvideo.utils.Util;
import com.songshu.squirrelvideo.view.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yb on 15-7-7.
 */
public class SelectionFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = SelectionFragment.class.getSimpleName() + ":";

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
        decideParam();
        initView();
        setListener();
        setAdapter();
    }

    private boolean isJoin = false;

    @Override
    public void join() {
        L.d(TAG, "............join..........isJoin.........." + isJoin);
        if (!isJoin) {
            isJoin = true;
            networkUI();
            loadForLocal();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SelectionFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SelectionFragment");
    }

    private String sub_title;
    private VideoTypeBean videoTypeBean;

    private void initParam() {
        Bundle bundle = getArguments();
        sub_title = bundle.getString("sub_title");
        videoTypeBean = (VideoTypeBean) bundle.getSerializable("video_type");
        L.d(TAG, "initParam --> sub_title : " + sub_title + " , video_type : " + videoTypeBean);
    }

    private String channel;
    private String type;
    private String area = "0";
    private String year = "0";

    private void decideParam() {
        channel = TitleManager.getEngNameMap().get(videoTypeBean.mTitle);
        List<VideoTypeTransformBean> videoTypeTransformBeans = TitleManager.getSubTitleMap().get(videoTypeBean.mTitle);
        for (VideoTypeTransformBean bean : videoTypeTransformBeans) {
            if (bean.sub_title.equals(sub_title)) {
                type = bean.type;
                break;
            }
        }
        L.d(TAG, "decideParam --> channel : " + channel + " , type : " + type + " , area : " + area + " , year : " + year);
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
    private boolean isUpPull;
    private int paramPage = 1;
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
                paramPage = 1;
                loadData();
            }
        });

        lv_video.setOnLoadListener(new PullToRefreshListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    lv_video.onLoadMoreComplete();
                    App.showToast(getString(R.string.please_check_network));
                    return;
                }
                isUpPull = true;
                paramPage++;
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

    private void loadForLocal() {
        isDataLoadFromLocal = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                GetMovieSelectionRequest request = new GetMovieSelectionRequest(channel, type, area, year, paramPage);
                cacheKey = request.getCacheKey();
                DataManager.getMovieSelection(request, new GetMovieSelectionRequestListener(cacheKey, request.getChannel(), request.getType(), request.getArea(), request.getYear(), request.getPage()));
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
            GetMovieSelectionRequest request = new GetMovieSelectionRequest(channel, type, area, year, paramPage);
            cacheKey = request.getCacheKey();
            DataManager.getMovieSelection(request, new GetMovieSelectionRequestListener(cacheKey, request.getChannel(), request.getType(), request.getArea(), request.getYear(), request.getPage()), true);
        }
    };


    private List<VideoBean> compareList;
    private final int VIDEO_NUMBER = 12;//每页返回的视频数量，默认为12个

    public void onEventMainThread(AppEvent.SucGetMovieSelectionEvent e) {
        L.d(TAG, "Event --> MovieSelection --> local_cache_key : " + cacheKey + " , net_cache_key :　" + e.reqKey);
        if (!cacheKey.equals(e.reqKey)) return;

        List<VideoBean> tmpList = new LinkedList<VideoBean>();
        for (MovieSelectionData data : e.mMovieSelectionNetData.data) {
            VideoBean bean = new VideoBean();
            bean.id = data.id;
            bean.channel = TitleManager.getEngNameMap().get(Const.TITLE);
            bean.title = data.title;
            bean.picture = new Pictures();
            bean.picture.big = data.poster_url;
            bean.picture.small = data.poster_url;
            bean.done = data.done;
            bean.last = data.last;
            bean.uptime = data.uptime;
            tmpList.add(bean);
        }

        if (paramPage == 1) {
            if (isPull) {
                isPull = false;
                lv_video.onRefreshComplete();
                if (compareList != null && Util.compareTwoArrayLength((VideoBean[]) compareList.toArray(new VideoBean[compareList.size()]), (VideoBean[]) tmpList.toArray(new VideoBean[tmpList.size()]))) {
                    App.showToast(getString(R.string.no_update_data));
                }
            } else {
                successUI();
            }

            if (compareList != null && Util.compareTwoArrayLength((VideoBean[]) compareList.toArray(new VideoBean[compareList.size()]), (VideoBean[]) tmpList.toArray(new VideoBean[tmpList.size()]))) {
                L.d(TAG, "onEventMainThread --> 比较返回的结果 : true");
                return;
            }
            compareList = tmpList;
            L.d(TAG, "dataForCompare : length : " + compareList.size());

            videoBeanList = tmpList;
            // if (e.mMovieSelectionNetData.data.length < VIDEO_NUMBER) lv_video.onLoadMoreComplete_full(); // 初始进来就显示了全部资源
        } else if (paramPage > 1) {
            if (isUpPull) {
                isUpPull = false;
                if (e.mMovieSelectionNetData.data.length == 0) {//没有数据了
                    paramPage--;
                    lv_video.onLoadMoreComplete_full();
                } else {
                    if (e.mMovieSelectionNetData.data.length < VIDEO_NUMBER) {
                        lv_video.onLoadMoreComplete_full();
                    } else {
                        lv_video.onLoadMoreComplete();//成功加载了
                    }
                }
            }
            videoBeanList.addAll(tmpList);
        }
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


    public void onEventMainThread(AppEvent.FailGetMovieSelectionEvent e) {
        L.d(TAG, "... onEvent --> MovieSelection --> Fail ...");
        if (isUpPull) {
            if (paramPage != 1) {
                paramPage--;
            }
            isUpPull = false;
            lv_video.onLoadMoreComplete();
        } else {
            errorUI();
        }
    }


    private void networkUI() {
        ll_right_loading.setVisibility(View.VISIBLE);
        iv_right_loading.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.loading_anim));
//        ((AnimationDrawable) iv_right_loading.getBackground()).start();
        ll_right_content.setVisibility(View.GONE);
        ll_right_error_content.setVisibility(View.GONE);
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
