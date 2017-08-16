package com.songshu.squirrelvideo.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.golshadi.majid.core.enums.TaskStates;
import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.adapter.DownloadEpisodeGridViewAdapter;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.db.FavorietsDaoImpl;
import com.songshu.squirrelvideo.db.HistoryDaoImpl;
import com.songshu.squirrelvideo.entity.DBDownloadBean;
import com.songshu.squirrelvideo.entity.DBFavoritesBean;
import com.songshu.squirrelvideo.entity.DBHistoryBean;
import com.songshu.squirrelvideo.entity.PlayTeleplayBean;
import com.songshu.squirrelvideo.entity.TeleplayDetailData;
import com.songshu.squirrelvideo.entity.TeleplaySourceBean;
import com.songshu.squirrelvideo.entity.VideoBean;
import com.songshu.squirrelvideo.listener.GetTeleplayDetailRequestListener;
import com.songshu.squirrelvideo.manager.DataManager;
import com.songshu.squirrelvideo.request.GetTeleplayDetailRequest;
import com.songshu.squirrelvideo.utils.ImageLoaderUtils;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.NetworkUtil;
import com.songshu.squirrelvideo.utils.Util;
import com.songshu.squirrelvideo.view.EpisodeView;
import com.songshu.squirrelvideo.view.swipe.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * Created by yb on 15-7-9.
 */
public class TeleplayDetailActivity extends SwipeBackActivity implements View.OnClickListener {

    private static final String TAG = TeleplayDetailActivity.class.getSimpleName() + ":";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.checkAndFinishOtherAct(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teleplay_detail);
        mContext = this;

        initParams();
        initView();
        setListener();
        setAdapter();
        networkUI();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("TeleplayDetailActivity"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("TeleplayDetailActivity"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    private VideoBean mVideoBean;
    private FavorietsDaoImpl mFavorietsDaoImpl;
    private boolean isFavority = false;
    private HistoryDaoImpl mHistoryDaoImpl;
    private boolean isHistory = false;
    private DBHistoryBean historyBean;

    private void initParams() {
        Intent intent = getIntent();
        mVideoBean = (VideoBean) intent.getSerializableExtra("video_bean");
        mFavorietsDaoImpl = new FavorietsDaoImpl(mContext);
        isFavority = mFavorietsDaoImpl.isCheckExist(mVideoBean.id + "", mVideoBean.channel);
        L.d(TAG, "initParams --> id : " + mVideoBean.id + " , channel : " + mVideoBean.channel + " , 是否收藏过 : " + isFavority + " , 是否有历史 : " + isHistory);
        mHistoryDaoImpl = new HistoryDaoImpl(mContext);
        isHistory = mHistoryDaoImpl.isCheckExist(mVideoBean.id + "", mVideoBean.channel);
        historyBean = mHistoryDaoImpl.findHistory(mVideoBean.id + "", mVideoBean.channel);
    }

    private ImageView iv_go_back;
    private LinearLayout ll_right_error_content;
    private TextView tv_right_error_content_retry;
    private LinearLayout ll_right_loading;
    private ImageView iv_right_loading;

    private ScrollView sv_content;
    private ImageView iv_video_poster;
    private TextView tv_video_name;
    private RelativeLayout rl_collect_unselected;
    private RelativeLayout rl_collect_selected;
    private RelativeLayout rl_download;
    private TextView tv_video_year;
    private TextView tv_video_actor;
    private TextView tv_video_type;
    private TextView tv_video_story;

    private RelativeLayout rl_episode_download_selection;
    private ImageView iv_close;
    private GridView gv_selection;
    private TextView tv_downloading_nums;

    private FrameLayout fl_episode;
    private View mView;
    private EpisodeView mEpisodeView;


    private void initView() {
        iv_go_back = (ImageView) findViewById(R.id.iv_go_back);
        ll_right_error_content = (LinearLayout) findViewById(R.id.ll_right_error_content);
        tv_right_error_content_retry = (TextView) findViewById(R.id.tv_right_error_content_retry);
        ll_right_loading = (LinearLayout) findViewById(R.id.ll_right_loading);
        iv_right_loading = (ImageView) findViewById(R.id.iv_right_loading);

        sv_content = (ScrollView) findViewById(R.id.sv_content);
        iv_video_poster = (ImageView) findViewById(R.id.iv_video_poster);
        tv_video_name = (TextView) findViewById(R.id.tv_video_name);
        rl_collect_unselected = (RelativeLayout) findViewById(R.id.rl_collect_unselected);
        rl_collect_selected = (RelativeLayout) findViewById(R.id.rl_collect_selected);
        rl_download = (RelativeLayout) findViewById(R.id.rl_download);
        tv_video_year = (TextView) findViewById(R.id.tv_video_year);
        tv_video_actor = (TextView) findViewById(R.id.tv_video_actor);
        tv_video_type = (TextView) findViewById(R.id.tv_video_type);
        tv_video_story = (TextView) findViewById(R.id.tv_video_story);

        rl_episode_download_selection = (RelativeLayout) findViewById(R.id.rl_episode_download_selection);// 右侧剧集下载界面
        rl_episode_download_selection.setVisibility(View.GONE);
        rl_download.setClickable(false);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        gv_selection = (GridView) findViewById(R.id.gv_selection);
        tv_downloading_nums = (TextView) findViewById(R.id.tv_downloading_nums);
        tv_downloading_nums.setVisibility(View.GONE);

        fl_episode = (FrameLayout) findViewById(R.id.fl_episode);
        mView = View.inflate(mContext, R.layout.view_episode, null);
        mEpisodeView = new EpisodeView(mContext, mView, mVideoBean.channel);
    }


    private void setListener() {
        iv_go_back.setOnClickListener(this);
        tv_right_error_content_retry.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        rl_download.setOnClickListener(this);

        rl_collect_unselected.setOnClickListener(listener);
        rl_collect_selected.setOnClickListener(listener);

    }


    private DownloadEpisodeGridViewAdapter mDownloadEpisodeGridViewAdapter;

    private void setAdapter() {
        mDownloadEpisodeGridViewAdapter = new DownloadEpisodeGridViewAdapter(mContext);
        gv_selection.setAdapter(mDownloadEpisodeGridViewAdapter);
        gv_selection.setOnItemClickListener(downloadEpisodeItemClickListener);
    }


    private void preSetView() {
        imageLoader.displayImage(mVideoBean.picture.big, iv_video_poster, ImageLoaderUtils.getDefaultOptions());
        tv_video_name.setText(mVideoBean.title);
        L.d(TAG, "preSetView --> poster : " + mVideoBean.picture.big + " , title : " + mVideoBean.title);
        changeFavorityView();
    }

    private void loadData() {
        mHandler.post(teleplayDetailRequestRunnable);
    }

    private String cacheKey;

    private Runnable teleplayDetailRequestRunnable = new Runnable() {
        @Override
        public void run() {
            GetTeleplayDetailRequest request = new GetTeleplayDetailRequest(mVideoBean.channel, mVideoBean.id);
            cacheKey = request.getCacheKey();
            DataManager.getTeleplayDetail(request, new GetTeleplayDetailRequestListener(cacheKey, request.getChannel(), request.getVideo_id()));
            if (!request.isRefresh()) {
                DataManager.getTeleplayDetail(request, new GetTeleplayDetailRequestListener(cacheKey, request.getChannel(), request.getVideo_id()), true);
            }
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
            case R.id.rl_download:
                handleDownloadEpisodeView();
                break;
            case R.id.iv_close:
                handleDownloadEpisodeView();
                break;
        }
    }


    /**
     * 处理右侧剧集下载界面的显示与隐藏
     */
    private void handleDownloadEpisodeView() {
        if (rl_episode_download_selection.getVisibility() == View.VISIBLE) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(rl_episode_download_selection, "translationX", 0f, 395f).setDuration(500);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rl_episode_download_selection.setVisibility(View.GONE);
                }
            });
            anim.start();
        } else {
            rl_episode_download_selection.setVisibility(View.VISIBLE);
            ObjectAnimator anim = ObjectAnimator.ofFloat(rl_episode_download_selection, "translationX", 395f, 0f).setDuration(500);
            anim.start();
        }
    }


    private TeleplayDetailData mTeleplayDetailData;

    public void onEventMainThread(AppEvent.SucGetTeleplayDetailEvent e) {
        L.d(TAG, "Event --> TeleplayDetail --> local_cache_key : " + cacheKey + " , net_cache_key :　" + e.reqKey);
        if (!cacheKey.equals(e.reqKey)) return;
        if (mTeleplayDetailData != null && mTeleplayDetailData.isSourceUNchanged(e.mTeleplayDetailNetData.data.source)) return;
        successUI();
        preSetView();
        mTeleplayDetailData = e.mTeleplayDetailNetData.data;
        postSetView();
        choseQQSource();
        choseIQiYiSource();
        choseSoHuSource();
        choseLetvSource();
        choseYouKuSource();
        choseTudouSource();
        choseOtherSource();
        setSourceBeanToEpisodeView();
        initDownloadDB();
    }

    public void onEventMainThread(AppEvent.FailGetTeleplayDetailEvent e) {
        L.d(TAG, "... onEvent --> TeleplayDetail --> Fail ...");
        errorUI();
    }

    private TeleplaySourceBean youkuSourceBean;//为了获取优酷源对象

    private void choseQQSource() {
        if (mTeleplayDetailData.source.length <= 0) return;
        for (TeleplaySourceBean bean : mTeleplayDetailData.source) {
            if (bean.name.equals("qq")) {
                youkuSourceBean = bean;
                L.d(TAG, "QQ Source --> name : " + youkuSourceBean.name + " , title : " + youkuSourceBean.title + " , list size : " + youkuSourceBean.mPlayTeleplayBeanList.size());
                break;
            }
        }
    }

    private void choseYouKuSource() {
        if (mTeleplayDetailData.source.length <= 0) return;
        if (youkuSourceBean != null) return;
        for (TeleplaySourceBean bean : mTeleplayDetailData.source) {
            if (bean.name.equals("youku")) {
                youkuSourceBean = bean;
                L.d(TAG, "YouKu Source --> name : " + youkuSourceBean.name + " , title : " + youkuSourceBean.title + " , list size : " + youkuSourceBean.mPlayTeleplayBeanList.size());
                break;
            }
        }
    }

    private void choseSoHuSource() {
        if (mTeleplayDetailData.source.length <= 0) return;
        if (youkuSourceBean != null) return;
        for (TeleplaySourceBean bean : mTeleplayDetailData.source) {
            if (bean.name.equals("sohu")) {
                youkuSourceBean = bean;
                L.d(TAG, "SoHu Source --> name : " + youkuSourceBean.name + " , title : " + youkuSourceBean.title + " , list size : " + youkuSourceBean.mPlayTeleplayBeanList.size());
                break;
            }
        }
    }

    private void choseIQiYiSource() {
        if (mTeleplayDetailData.source.length <= 0) return;
        if (youkuSourceBean != null) return;
        for (TeleplaySourceBean bean : mTeleplayDetailData.source) {
            if (bean.name.equals("iqiyi")) {
                youkuSourceBean = bean;
                L.d(TAG, "IQiYi Source --> name : " + youkuSourceBean.name + " , title : " + youkuSourceBean.title + " , list size : " + youkuSourceBean.mPlayTeleplayBeanList.size());
                break;
            }
        }
    }

    private void choseLetvSource() {
        if (mTeleplayDetailData.source.length <= 0) return;
        if (youkuSourceBean != null) return;
        for (TeleplaySourceBean bean : mTeleplayDetailData.source) {
            if (bean.name.equals("letv")) {
                youkuSourceBean = bean;
                L.d(TAG, "Letv Source --> name : " + youkuSourceBean.name + " , title : " + youkuSourceBean.title + " , list size : " + youkuSourceBean.mPlayTeleplayBeanList.size());
                break;
            }
        }
    }

    private void choseTudouSource() {
        if (mTeleplayDetailData.source.length <= 0) return;
        if (youkuSourceBean != null) return;
        for (TeleplaySourceBean bean : mTeleplayDetailData.source) {
            if (bean.name.equals("tudou")) {
                youkuSourceBean = bean;
                L.d(TAG, "Tudou Source --> name : " + youkuSourceBean.name + " , title : " + youkuSourceBean.title + " , list size : " + youkuSourceBean.mPlayTeleplayBeanList.size());
                break;
            }
        }
    }

    private void choseOtherSource() {
        if (mTeleplayDetailData.source.length <= 0) return;
        if (youkuSourceBean != null) return;
        for (TeleplaySourceBean bean : mTeleplayDetailData.source) {
            youkuSourceBean = bean;
            L.d(TAG, "otherSourceBean --> name : " + youkuSourceBean.name + " , title : " + youkuSourceBean.title + " , list size : " + youkuSourceBean.mPlayTeleplayBeanList.size());
            break;
        }
    }

    private void setSourceBeanToEpisodeView() {
        mEpisodeView.addTabItemAndData(youkuSourceBean, mVideoBean.id + "");
        fl_episode.removeAllViews();
        fl_episode.addView(mView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }


    private void initDownloadDB() {
        int downloadingNums = 0;
        for (int i = 0; i < youkuSourceBean.mPlayTeleplayBeanList.size(); i++) {
            int status = App.getMyDownloadManager().getStatusByHtmlUrl(youkuSourceBean.mPlayTeleplayBeanList.get(i).videoUrl);
            youkuSourceBean.mPlayTeleplayBeanList.get(i).videoDownloadState = status;
            if (status != TaskStates.NOINCLUDE && status != TaskStates.END) {
                downloadingNums += 1;
            }
        }
        setDownloadingNums(downloadingNums);
        setSourceBeanToDownloadSelectionView();
    }


    /**
     * 设置下载个数View
     */
    private void setDownloadingNums(int downloadingNums) {
        L.d(TAG, "...........setDownloadingNums........." + downloadingNums);
        tv_downloading_nums.setVisibility(View.VISIBLE);
        tv_downloading_nums.setText(getString(R.string.layout_download_episode_selection_download, downloadingNums));
    }


    /**
     * 将数据传给右侧下载剧集的View并刷新,此时下载按键才可以点击
     */
    private void setSourceBeanToDownloadSelectionView() {
        L.d(TAG, "......setSourceBeanToDownloadSelectionView......");
        mDownloadEpisodeGridViewAdapter.updataAdapterData(youkuSourceBean);
        rl_download.setClickable(true);
    }


    private void postSetView() {
        if (!TextUtils.isEmpty(mTeleplayDetailData.showtimes)) {
            String years = mTeleplayDetailData.showtimes;
            String year_str = (years.contains("-") ? years.substring(0, years.indexOf("-")) : years)
                    + (TextUtils.isEmpty(mTeleplayDetailData.area) ? "" : (" | " + mTeleplayDetailData.area + " | " + getMovieLanguage(mTeleplayDetailData.area)));
            setVideoYear(year_str);
            L.d(TAG, "postSetView --> year : " + year_str);
        }

        if (!TextUtils.isEmpty(mTeleplayDetailData.starring)) {
            String actors = mTeleplayDetailData.starring;
            String[] actor_split = actors.split("/");
            StringBuilder actor_str = new StringBuilder();
            actor_str.append(getString(R.string.character_starring));
            for (int i = 0; i < actor_split.length; i++) {
                actor_str.append(actor_split[i]).append(" ");
            }
            setVideoActor(actor_str.toString().substring(0, actor_str.toString().length() - 1));// 去掉最后的空格字符
            L.d(TAG, "postSetView --> actor : " + actor_str.toString());
        }

        if (!TextUtils.isEmpty(mTeleplayDetailData.type)) {
            String types = mTeleplayDetailData.type;
            String[] type_split = types.split("/");
            StringBuilder type_str = new StringBuilder();
            type_str.append(getString(R.string.character_types));
            for (int i = 0; i < type_split.length; i++) {
                type_str.append(type_split[i]).append("|");
            }
            setVideoType(type_str.toString().substring(0, type_str.toString().length() - 1));// 去掉最后的|字符
            L.d(TAG, "postSetView --> type : " + type_str.toString());
        }

        if (!TextUtils.isEmpty(mTeleplayDetailData.intro)) {
            StringBuilder story_str = new StringBuilder();
            story_str.append(getString(R.string.character_intros)).append(mTeleplayDetailData.intro);
            setVideoStory(story_str.toString());
            L.d(TAG, "postSetView --> story : " + story_str.toString());
        }

    }


    private String getMovieLanguage(String ori) {
        if (ori.contains(getString(R.string.character_language_da_lu))
                || ori.contains(getString(R.string.character_language_xiang_gang))
                || ori.contains(getString(R.string.character_language_tai_wan))) {
            return getString(R.string.character_language_guoyu);
        } else if (ori.contains(getString(R.string.character_language_yin_du))) {
            return getString(R.string.character_language_yinduyu);
        } else if (ori.contains(getString(R.string.character_language_mei_guo))) {
            return getString(R.string.character_language_yingyu);
        } else if (ori.contains(getString(R.string.character_language_qita))) {
            return getString(R.string.character_language_weizhi);
        } else {
            return ori.substring(0, 1) + getString(R.string.character_language);
        }
    }

    /**
     * 设置视频年份和地域
     *
     * @param year
     */
    private void setVideoYear(String year) {
        tv_video_year.setVisibility(View.VISIBLE);
        tv_video_year.setText(year);
    }


    /**
     * 设置视频主演
     *
     * @param actor
     */
    private void setVideoActor(String actor) {
        tv_video_actor.setVisibility(View.VISIBLE);
        tv_video_actor.setText(actor);
    }

    /**
     * 设置视频类型
     *
     * @param type
     */
    private void setVideoType(String type) {
        tv_video_type.setVisibility(View.VISIBLE);
        tv_video_type.setText(type);
    }

    /**
     * 设置视频简介
     *
     * @param story
     */
    private void setVideoStory(String story) {
        tv_video_story.setVisibility(View.VISIBLE);
        tv_video_story.setText(story);
    }

    /**
     * 收藏和下载的点击相应事件
     */
    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_collect_unselected:
                    boolean isAddSuccess = mFavorietsDaoImpl.addFavoriets(
                            new DBFavoritesBean(
                                    mTeleplayDetailData.id,
                                    mTeleplayDetailData.title,
                                    mTeleplayDetailData.type,
                                    mTeleplayDetailData.poster_url,
                                    mVideoBean.channel,
                                    mTeleplayDetailData.last
                            )
                    );
                    isFavority = isAddSuccess;
                    sentFavorityDBOccusChangingEvent();
                    changeFavorityView();
                    break;
                case R.id.rl_collect_selected:
                    boolean isDeleteSuccess = mFavorietsDaoImpl.deleteFavoriets(mTeleplayDetailData.id + "", mVideoBean.channel);
                    isFavority = !isDeleteSuccess;
                    sentFavorityDBOccusChangingEvent();
                    changeFavorityView();
                    break;
            }
        }

    };


    /**
     * 下载剧集的item点击监听
     */
    AdapterView.OnItemClickListener downloadEpisodeItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PlayTeleplayBean bean = mDownloadEpisodeGridViewAdapter.getCurrentDataList().get(position);
            if (!checkItemCanClick(bean)) return;
            L.d(TAG, "下载集数=" + bean.videoNumber + " , 下载的网络地址=" + bean.videoUrl);
            if (NetworkUtil.isNetworkAvailable(mContext)) {
                setPreLayoutChange(bean);
                App.getMyDownloadManager().initStartDownload(
                        mVideoBean.id + Const.MYWIFEANDIBIRTH + bean.videoNumber,// 因为主键唯一,对于一个连续剧,id肯定是一样的,这里添加了下载剧集的数以及其他特征数字
                        mVideoBean.channel,
                        mTeleplayDetailData.title,
                        mTeleplayDetailData.poster_url,
                        bean.videoUrl,
                        bean.videoNumber + "",
                        youkuSourceBean.name);
            } else {
                App.showToast(mContext.getString(R.string.please_check_network));
            }
        }
    };


    /**
     * 判断该item是否可以再次点击,即是否已经在下载
     */
    public boolean checkItemCanClick(PlayTeleplayBean bean) {
        if (bean.videoDownloadState == TaskStates.NOINCLUDE) {
            return true;
        }
        return false;
    }


    /**
     * 修改右侧布局中的gridview和底部下载数量的变化
     */
    public void setPreLayoutChange(PlayTeleplayBean bean) {
        bean.videoDownloadState = TaskStates.INIT;
        mDownloadEpisodeGridViewAdapter.notifyDataSetChanged();
        int oldNum = getDownloadingTextViewNums();
        int newNum = oldNum + 1;
        setDownloadingNums(newNum);
    }


    /**
     * 获取右下方下载任务的具体个数
     */
    private int getDownloadingTextViewNums() {
        String oldStr = tv_downloading_nums.getText().toString().trim();//下载 (23)
        String[] strs = oldStr.split("\\(");// ["下载 (", "23)"]
        String rightPart = strs[1];// "23)"
        int oldNums = Integer.parseInt(rightPart.substring(0, rightPart.length() - 1));// 23
        L.d(TAG, "......getDownloadingTextViewNums......" + oldNums);
        return oldNums;
    }


    public void onEventMainThread(final AppEvent.FailGetVideoRealPathEvent e) {
        L.d(TAG, "... onEvent --> VideoRealPath --> Fail ...");
        for (int i = 0; i < mDownloadEpisodeGridViewAdapter.getCurrentDataList().size(); i++) {
            if (mDownloadEpisodeGridViewAdapter.getCurrentDataList().get(i).videoUrl.equals(e.play_url)) {
                mDownloadEpisodeGridViewAdapter.getCurrentDataList().get(i).videoDownloadState = TaskStates.NOINCLUDE;
                mDownloadEpisodeGridViewAdapter.notifyDataSetChanged();
                break;
            }
        }
        int oldNum = getDownloadingTextViewNums();
        int newNum = oldNum - 1;
        setDownloadingNums(newNum);
    }


    public void onEventMainThread(final AppEvent.DownloadFinishedEvent e) {
        L.d(TAG, "... onEvent --> Download --> complete ...");
        L.d(TAG, "------> OnDownloadDetailCompleted : " + e.bean);
        L.d(TAG, "------> mVideoBean : " + mVideoBean);
        DBDownloadBean bean = e.bean;
        String realTeleplayVideoId = bean.videoId.split(Const.MYWIFEANDIBIRTH)[0];
        if (realTeleplayVideoId.equals(mVideoBean.id + "") && bean.videoChannel.equals(mVideoBean.channel)) {
            for (int i = 0; i < mDownloadEpisodeGridViewAdapter.getCurrentDataList().size(); i++) {
                if (mDownloadEpisodeGridViewAdapter.getCurrentDataList().get(i).videoUrl.equals(bean.videoHtmlUrl)) {
                    mDownloadEpisodeGridViewAdapter.getCurrentDataList().get(i).videoDownloadState = TaskStates.END;
                    mDownloadEpisodeGridViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
            int oldNum = getDownloadingTextViewNums();
            int newNum = oldNum - 1;
            setDownloadingNums(newNum);
        }
    }


    /**
     * 发送收藏数据库发生变化的event事件
     */
    private void sentFavorityDBOccusChangingEvent() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new AppEvent.DBOccusChanging());
            }
        });
    }


    /**
     * 更新是否收藏过的界面
     */
    private void changeFavorityView() {
        if (isFavority) {
            rl_collect_unselected.setVisibility(View.GONE);
            rl_collect_selected.setVisibility(View.VISIBLE);
        } else {
            rl_collect_unselected.setVisibility(View.VISIBLE);
            rl_collect_selected.setVisibility(View.GONE);
        }
    }

    /**
     * 网络布局
     */
    private void networkUI() {
        ll_right_loading.setVisibility(View.VISIBLE);
        iv_right_loading.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.loading_anim));
        sv_content.setVisibility(View.GONE);
        ll_right_error_content.setVisibility(View.GONE);
    }

    /**
     * 成功布局
     */
    private void successUI() {
        ll_right_loading.setVisibility(View.GONE);
        iv_right_loading.clearAnimation();
        sv_content.setVisibility(View.VISIBLE);
        ll_right_error_content.setVisibility(View.GONE);
    }

    /**
     * 出错布局
     */
    private void errorUI() {
        ll_right_loading.setVisibility(View.GONE);
        iv_right_loading.clearAnimation();
        sv_content.setVisibility(View.GONE);
        ll_right_error_content.setVisibility(View.VISIBLE);
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
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(teleplayDetailRequestRunnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK) { // 修改播放历史记录
            L.d(TAG, "AHAHJAJAJADKFJDSLKFDJFLKDJFKLDFJDKLFJDKSLFJDSFKJDSLKFDSJFLKDSJFLKDSJFKLDS");
            isHistory = true;
            String currentPlayPos = String.valueOf(data.getLongExtra("currentPlayPos", 0l));
            String currentPlayIndex = data.getStringExtra("index");
            String watchDate = String.valueOf(System.currentTimeMillis());
            DBHistoryBean history = new DBHistoryBean(
                    mTeleplayDetailData.title,
                    mTeleplayDetailData.type,
                    mTeleplayDetailData.id + Const.MYWIFEANDIBIRTH + currentPlayIndex,
                    mTeleplayDetailData.poster_url,
                    mVideoBean.channel,
                    "",
                    currentPlayPos,
                    "",
                    currentPlayIndex,
                    watchDate
            );
            L.d(TAG, "待处理数据 --> DBHistoryBean : " + history);
            boolean flag = mHistoryDaoImpl.addHistory(history);
            L.d(TAG, "数据库添加该条数据 --> " + history);
            if (!flag) {
                L.d(TAG, "数据库存在该条数据 --> 更新数据");
                mHistoryDaoImpl.updateHistory(history);
            }
            sentFavorityDBOccusChangingEvent();
        }
    }

}
