package com.songshu.squirrelvideo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.adapter.SpecificAdapter;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.CategoryBean;
import com.songshu.squirrelvideo.entity.MovieSelectionData;
import com.songshu.squirrelvideo.entity.Pictures;
import com.songshu.squirrelvideo.entity.VideoBean;
import com.songshu.squirrelvideo.entity.VideoTypeTransformBean;
import com.songshu.squirrelvideo.listener.GetMovieSelectionRequestListener;
import com.songshu.squirrelvideo.manager.DataManager;
import com.songshu.squirrelvideo.manager.TitleManager;
import com.songshu.squirrelvideo.request.GetMovieSelectionRequest;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.NetworkUtil;
import com.songshu.squirrelvideo.utils.Util;
import com.songshu.squirrelvideo.view.PullToRefreshListView;
import com.songshu.squirrelvideo.view.swipe.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yb on 15-7-8.
 */
public class SpecificCategoryActivity extends SwipeBackActivity implements View.OnClickListener {

    private static final String TAG = SpecificCategoryActivity.class.getSimpleName() + ":";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.checkAndFinishOtherAct(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_category);
        mContext = this;

        initData();
        iniParams();
        initView();
        setView();
        setListener();
        setAdapter();
        networkUI();
        loadFromLocal();
        setBackIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SpecificCategoryActivity"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SpecificCategoryActivity"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }


    CategoryBean mCategoryBean;

    private void initData() {
        Intent intent = getIntent();
        mCategoryBean = (CategoryBean) intent.getSerializableExtra("category_bean");
    }

    private String channel;
    private String type;
    private String area = "0";
    private String year = "0";

    private void iniParams() {
        channel = TitleManager.getEngNameMap().get(mCategoryBean.title);
        List<VideoTypeTransformBean> videoTypeTransformBeans = TitleManager.getSubTitleMap().get(mCategoryBean.title);
        for (VideoTypeTransformBean bean : videoTypeTransformBeans) {
            if (bean.sub_title.equals(mCategoryBean.sub_title)) {
                type = bean.type;
                break;
            }
        }
        L.d(TAG, "decideParam --> channel : " + channel + " , type : " + type + " , area : " + area + " , year : " + year);
    }

    private ImageView iv_go_back;
    private TextView tv_sub_title;
    private RelativeLayout rl_selected;
    private RelativeLayout rl_un_selected;

    private PullToRefreshListView lv_video;

    private LinearLayout ll_right_error_content;
    private TextView tv_right_error_content_retry;

    private LinearLayout ll_right_loading;
    private ImageView iv_right_loading;

    private void initView() {
        iv_go_back = (ImageView) findViewById(R.id.iv_go_back);
        tv_sub_title = (TextView) findViewById(R.id.tv_sub_title);
        rl_selected = (RelativeLayout) findViewById(R.id.rl_selected);
        rl_un_selected = (RelativeLayout) findViewById(R.id.rl_un_selected);
        lv_video = (PullToRefreshListView) findViewById(R.id.lv_video);

        ll_right_error_content = (LinearLayout) findViewById(R.id.ll_right_error_content);
        tv_right_error_content_retry = (TextView) findViewById(R.id.tv_right_error_content_retry);

        ll_right_loading = (LinearLayout) findViewById(R.id.ll_right_loading);
        iv_right_loading = (ImageView) findViewById(R.id.iv_right_loading);
    }

    private void setView() {
        tv_sub_title.setText(mCategoryBean.sub_title);
        changeSelectedStatus(mCategoryBean.is_selected);
    }

    private void setListener() {
        iv_go_back.setOnClickListener(this);
        rl_selected.setOnClickListener(listener);
        rl_un_selected.setOnClickListener(listener);
        tv_right_error_content_retry.setOnClickListener(this);
    }

    private SpecificAdapter mSpecificAdapter;
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
        mSpecificAdapter = new SpecificAdapter(mContext, videoBeanList);
        lv_video.setAdapter(mSpecificAdapter);
    }

    private boolean isDataLoadFromLocal = false;

    private void loadFromLocal() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_go_back:
                finish();
                break;
            case R.id.tv_right_error_content_retry:
                networkUI();
                loadData();
                break;
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private Intent intent;

    /**
     * 组装数据,返回到子类别选择页
     */
    private void setBackIntent() {
        intent = new Intent();
        intent.putExtra("category_bean", mCategoryBean);
        setResult(0, intent);
    }

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
        mSpecificAdapter.notifyData(videoBeanList);

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


    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
    }


    /**
     * 添加与否的监听
     */
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changeDataSelectedStatus(!mCategoryBean.is_selected);
            changeSelectedStatus(mCategoryBean.is_selected);
        }
    };

    /**
     * 网络布局
     */
    private void networkUI() {
        ll_right_loading.setVisibility(View.VISIBLE);
        iv_right_loading.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.loading_anim));
        lv_video.setVisibility(View.GONE);
        ll_right_error_content.setVisibility(View.GONE);
    }

    /**
     * 成功布局
     */
    private void successUI() {
        ll_right_loading.setVisibility(View.GONE);
        iv_right_loading.clearAnimation();
        lv_video.setVisibility(View.VISIBLE);
        ll_right_error_content.setVisibility(View.GONE);
    }

    /**
     * 出错布局
     */
    private void errorUI() {
        ll_right_loading.setVisibility(View.GONE);
        iv_right_loading.clearAnimation();
        lv_video.setVisibility(View.GONE);
        ll_right_error_content.setVisibility(View.VISIBLE);
    }

    /**
     * 修改已选布局
     *
     * @param isSelected
     */
    private void changeSelectedStatus(boolean isSelected) {
        if (isSelected) {
            rl_selected.setVisibility(View.VISIBLE);
            rl_un_selected.setVisibility(View.GONE);
        } else {
            rl_selected.setVisibility(View.GONE);
            rl_un_selected.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 修改数据中的已选状态
     *
     * @param isSelected
     */
    private void changeDataSelectedStatus(boolean isSelected) {
        mCategoryBean.is_selected = isSelected;
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
