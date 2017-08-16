package com.songshu.squirrelvideo.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.golshadi.majid.core.enums.TaskStates;
import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.db.FavorietsDaoImpl;
import com.songshu.squirrelvideo.db.HistoryDaoImpl;
import com.songshu.squirrelvideo.entity.DBDownloadBean;
import com.songshu.squirrelvideo.entity.DBFavoritesBean;
import com.songshu.squirrelvideo.entity.DBHistoryBean;
import com.songshu.squirrelvideo.entity.MovieDetailData;
import com.songshu.squirrelvideo.entity.MovieSourceBean;
import com.songshu.squirrelvideo.entity.PreDownloadBean;
import com.songshu.squirrelvideo.entity.VideoBean;
import com.songshu.squirrelvideo.listener.GetMovieDetailRequestListener;
import com.songshu.squirrelvideo.manager.DataManager;
import com.songshu.squirrelvideo.network.VideoUrlNet;
import com.songshu.squirrelvideo.player.PlayVideoActivity;
import com.songshu.squirrelvideo.request.GetMovieDetailRequest;
import com.songshu.squirrelvideo.utils.ImageLoaderUtils;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.NetworkUtil;
import com.songshu.squirrelvideo.utils.Util;
import com.songshu.squirrelvideo.view.CustomDialog;
import com.songshu.squirrelvideo.view.swipe.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by yb on 15-7-9.
 */
public class MovieDetailActivity extends SwipeBackActivity implements View.OnClickListener {

    private static final String TAG = MovieDetailActivity.class.getSimpleName() + ":";
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.checkAndFinishOtherAct(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mContext = this;

        initParams();
        initView();
        setListener();
        networkUI();
        loadData();
        initDownloadDB();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MovieDetailActivity"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MovieDetailActivity"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
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
        mHistoryDaoImpl = new HistoryDaoImpl(mContext);
        isHistory = mHistoryDaoImpl.isCheckExist(mVideoBean.id + "", mVideoBean.channel);
        historyBean = mHistoryDaoImpl.findHistory(mVideoBean.id + "", mVideoBean.channel);
        L.d(TAG, "initParams --> id : " + mVideoBean.id + " , channel : " + mVideoBean.channel + " , 是否收藏过 : " + isFavority + " , 是否有历史 : " + isHistory);
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
    private ImageView iv_download;
    private TextView tv_download;
    private RelativeLayout rl_play;
    private TextView tv_video_year;
    private TextView tv_video_actor;
    private TextView tv_video_type;
    private TextView tv_video_story;


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
        tv_download = (TextView) findViewById(R.id.tv_download);
        iv_download = (ImageView) findViewById(R.id.iv_download);
        rl_play = (RelativeLayout) findViewById(R.id.rl_play);
        tv_video_year = (TextView) findViewById(R.id.tv_video_year);
        tv_video_actor = (TextView) findViewById(R.id.tv_video_actor);
        tv_video_type = (TextView) findViewById(R.id.tv_video_type);
        tv_video_story = (TextView) findViewById(R.id.tv_video_story);

    }


    private void setListener() {
        iv_go_back.setOnClickListener(this);
        tv_right_error_content_retry.setOnClickListener(this);

        rl_collect_unselected.setOnClickListener(listener);
        rl_collect_selected.setOnClickListener(listener);
        rl_download.setOnClickListener(listener);

        rl_play.setOnClickListener(this);
    }


    private void preSetView() {
        imageLoader.displayImage(mVideoBean.picture.big, iv_video_poster, ImageLoaderUtils.getDefaultOptions());
        tv_video_name.setText(mVideoBean.title);
        L.d(TAG, "preSetView --> poster : " + mVideoBean.picture.big + " , title : " + mVideoBean.title);
        changeFavorityView();
    }

    private void loadData() {
        mHandler.post(movieDetailRequestRunnable);
    }

    private String cacheKey;

    private Runnable movieDetailRequestRunnable = new Runnable() {
        @Override
        public void run() {
            GetMovieDetailRequest request = new GetMovieDetailRequest(mVideoBean.channel, mVideoBean.id);
            cacheKey = request.getCacheKey();
            DataManager.getMovieDetail(request, new GetMovieDetailRequestListener(cacheKey, request.getChannel(), request.getVideo_id()));
            if (!request.isRefresh()) {
                DataManager.getMovieDetail(request, new GetMovieDetailRequestListener(cacheKey, request.getChannel(), request.getVideo_id()), true);
            }
        }
    };


    private boolean isCached = false;

    private void initDownloadDB() {
        DBDownloadBean downloadBean = App.getMyDownloadManager().findDBDownloadBean(mVideoBean.id + "", mVideoBean.channel);
        L.d(TAG, "...... initDownloadDB ------> downloadBean : " + downloadBean);
        if (downloadBean != null) {
            switch (downloadBean.videoDownloadState) {
                case TaskStates.END:
                    isPermmitDownload = false;
                    isCached = true;
                    tv_download.setText("下载完毕");
                    iv_download.setImageResource(R.drawable.download_press);
                    break;
                default:
                    isCached = false;
                    isPermmitDownload = false;
                    tv_download.setText("下载中");
                    iv_download.setImageResource(R.drawable.download_press);
                    break;
            }
        }
    }


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
            case R.id.rl_play:
                if (NetworkUtil.isMobileNetwork(mContext)) {
                    new CustomDialog.Builder(mContext)
                            .setMessage(getString(R.string.dialog_custom_message_3g))
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    play();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create(R.layout.dlg_custom_ori)
                            .show();
                } else {
                    play();
                }
                break;
        }
    }

    private void play() {
        App.closeSnappyDb();
        Intent intent = new Intent(mContext, PlayVideoActivity.class);
        historyBean = mHistoryDaoImpl.findHistory(mVideoBean.id + "", mVideoBean.channel);
        if (isHistory && historyBean != null) { // 有播放记录
            intent.putExtra("cached", isCached);
            intent.putExtra("url", youkuSourceBean.values);
            intent.putExtra("channel", mVideoBean.channel);
            intent.putExtra("source", youkuSourceBean.name);
            intent.putExtra("isHistory", isHistory);
            intent.putExtra("lastPosition", historyBean.getTime());
            startActivityForResult(intent, 10);
            L.d(TAG, "startActivityForResult --> PlayVideoActivity --> url : " + youkuSourceBean.values + " , channel : " + mVideoBean.channel + " , source : " + youkuSourceBean.name + " , lastPosition : " + historyBean.getTime());

        } else { // 无播放记录
            intent.putExtra("cached", isCached);
            intent.putExtra("url", youkuSourceBean.values);
            intent.putExtra("channel", mVideoBean.channel);
            intent.putExtra("source", youkuSourceBean.name);
            intent.putExtra("isHistory", isHistory);
            startActivityForResult(intent, 10);
            L.d(TAG, "startActivityForResult --> PlayVideoActivity --> url : " + youkuSourceBean.values + " , channel : " + mVideoBean.channel + " , source : " + youkuSourceBean.name);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK) { // 修改播放历史记录
            L.d(TAG, "AHAHJAJAJADKFJDSLKFDJFLKDJFKLDFJDKLFJDKSLFJDSFKJDSLKFDSJFLKDSJFLKDSJFKLDS");
            isHistory = true;
            String currentPalyPos = String.valueOf(data.getLongExtra("currentPlayPos", 0l));
            String watchDate = String.valueOf(System.currentTimeMillis());
            DBHistoryBean history = new DBHistoryBean(
                    mMovieDetailData.title,
                    mMovieDetailData.type,
                    mMovieDetailData.id,
                    mMovieDetailData.poster_url,
                    mVideoBean.channel,
                    currentPalyPos,
                    watchDate
            );
            L.d(TAG, "待处理数据 --> DBHistoryBean : " + history);
            boolean flag = mHistoryDaoImpl.addHistory(history);
            L.d(TAG, "数据库添加该条数据 --> " + history);
            if (!flag) {
                L.d(TAG, "数据库存在该条数据 --> 更新数据");
                mHistoryDaoImpl.updateHistory(history);
            }
            sentDBOccusChangingEvent();
        }
    }


    private MovieDetailData mMovieDetailData;

    public void onEventMainThread(AppEvent.SucGetMovieDetailEvent e) {
        L.d(TAG, "Event --> MovieDetail --> local_cache_key : " + cacheKey + " , net_cache_key :　" + e.reqKey);
        if (!cacheKey.equals(e.reqKey)) return;

        successUI();
        preSetView();
        mMovieDetailData = e.mMovieDetailNetData.data;
        postSetView();
        choseQQSource();
        choseIQiYiSource();
        choseSoHuSource();
        choseLetvSource();
        choseYouKuSource();
        choseTudouSource();
        choseOtherSource();
    }

    private MovieSourceBean youkuSourceBean;//为了获取优酷源对象

    private void choseQQSource() {
        if (mMovieDetailData.source.length <= 0) return;
        for (MovieSourceBean bean : mMovieDetailData.source) {
            if (bean.name.equals("qq")) {
                youkuSourceBean = bean;
                L.d(TAG, "QQ Source --> name : " + youkuSourceBean.name + " , title : " + youkuSourceBean.title + " , values : " + youkuSourceBean.values);
                break;
            }
        }
    }

    private void choseYouKuSource() {
        if (mMovieDetailData.source.length <= 0) return;
        if (youkuSourceBean != null) return;
        for (MovieSourceBean bean : mMovieDetailData.source) {
            if (bean.name.equals("youku")) {
                youkuSourceBean = bean;
                L.d(TAG, "YouKu Source --> name : " + youkuSourceBean.name + " , title : " + youkuSourceBean.title + " , values : " + youkuSourceBean.values);
                break;
            }
        }
    }

    private void choseSoHuSource() {
        if (mMovieDetailData.source.length <= 0) return;
        if (youkuSourceBean != null) return;
        for (MovieSourceBean bean : mMovieDetailData.source) {
            if (bean.name.equals("sohu")) {
                youkuSourceBean = bean;
                L.d(TAG, "SoHu Source --> name : " + youkuSourceBean.name + " , title : " + youkuSourceBean.title + " , values : " + youkuSourceBean.values);
                break;
            }
        }
    }


    private void choseIQiYiSource() {
        if (mMovieDetailData.source.length <= 0) return;
        if (youkuSourceBean != null) return;
        for (MovieSourceBean bean : mMovieDetailData.source) {
            if (bean.name.equals("iqiyi")) {
                youkuSourceBean = bean;
                L.d(TAG, "IQiYi Source --> name : " + youkuSourceBean.name + " , title : " + youkuSourceBean.title + " , values : " + youkuSourceBean.values);
                break;
            }
        }
    }

    private void choseLetvSource() {
        if (mMovieDetailData.source.length <= 0) return;
        if (youkuSourceBean != null) return;
        for (MovieSourceBean bean : mMovieDetailData.source) {
            if (bean.name.equals("letv")) {
                youkuSourceBean = bean;
                L.d(TAG, "Letv Source --> name : " + youkuSourceBean.name + " , title : " + youkuSourceBean.title + " , values : " + youkuSourceBean.values);
                break;
            }
        }
    }

    private void choseTudouSource() {
        if (mMovieDetailData.source.length <= 0) return;
        if (youkuSourceBean != null) return;
        for (MovieSourceBean bean : mMovieDetailData.source) {
            if (bean.name.equals("tudou")) {
                youkuSourceBean = bean;
                L.d(TAG, "Tudou Source --> name : " + youkuSourceBean.name + " , title : " + youkuSourceBean.title + " , values : " + youkuSourceBean.values);
                break;
            }
        }
    }

    private void choseOtherSource() {
        if (mMovieDetailData.source.length <= 0) return;
        if (youkuSourceBean != null) return;
        for (MovieSourceBean bean : mMovieDetailData.source) {
            youkuSourceBean = bean;
            L.d(TAG, "OtherSource --> name : " + youkuSourceBean.name + " , title : " + youkuSourceBean.title + " , values : " + youkuSourceBean.values);
            break;
        }
    }


    public void onEventMainThread(AppEvent.FailGetMovieDetailEvent e) {
        L.d(TAG, "... onEvent --> MovieDetail --> Fail ...");
        errorUI();
    }

    private void postSetView() {
        if (!TextUtils.isEmpty(mMovieDetailData.showtimes)) {
            String years = mMovieDetailData.showtimes;
            String year_str = (years.contains("-") ? years.substring(0, years.indexOf("-")) : years)
                    + (TextUtils.isEmpty(mMovieDetailData.area) ? "" : (" | " + mMovieDetailData.area + " | " + getMovieLanguage(mMovieDetailData.area)));
            setVideoYear(year_str);
            L.d(TAG, "postSetView --> year : " + year_str);
        }

        if (!TextUtils.isEmpty(mMovieDetailData.starring)) {
            String actors = mMovieDetailData.starring;
            String[] actor_split = actors.split("/");
            StringBuilder actor_str = new StringBuilder();
            actor_str.append(getString(R.string.character_starring));
            for (int i = 0; i < actor_split.length; i++) {
                actor_str.append(actor_split[i]).append(" ");
            }
            setVideoActor(actor_str.toString().substring(0, actor_str.toString().length() - 1));// 去掉最后的空格字符
            L.d(TAG, "postSetView --> actor : " + actor_str.toString());
        }

        if (!TextUtils.isEmpty(mMovieDetailData.type)) {
            String types = mMovieDetailData.type;
            String[] type_split = types.split("/");
            StringBuilder type_str = new StringBuilder();
            type_str.append(getString(R.string.character_types));
            for (int i = 0; i < type_split.length; i++) {
                type_str.append(type_split[i]).append("|");
            }
            setVideoType(type_str.toString().substring(0, type_str.toString().length() - 1));// 去掉最后的|字符
            L.d(TAG, "postSetView --> type : " + type_str.toString());
        }

        if (!TextUtils.isEmpty(mMovieDetailData.intro)) {
            StringBuilder story_str = new StringBuilder();
            story_str.append(getString(R.string.character_intros)).append(mMovieDetailData.intro);
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


    private boolean isPermmitDownload = true;// 是否允许用户点击下载功能按钮

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
                                    mMovieDetailData.id,
                                    mMovieDetailData.title,
                                    mMovieDetailData.type,
                                    mMovieDetailData.poster_url,
                                    mVideoBean.channel
                            )
                    );
                    isFavority = isAddSuccess;
                    sentDBOccusChangingEvent();
                    changeFavorityView();
                    break;
                case R.id.rl_collect_selected:
                    boolean isDeleteSuccess = mFavorietsDaoImpl.deleteFavoriets(mMovieDetailData.id + "", mVideoBean.channel);
                    isFavority = !isDeleteSuccess;
                    sentDBOccusChangingEvent();
                    changeFavorityView();
                    break;
                case R.id.rl_download:
                    if (!NetworkUtil.isNetworkAvailable(mContext)) {
                        App.showToast(mContext.getString(R.string.please_check_network));
                        return;
                    }
                    if (!isPermmitDownload) return;
                    loadRealVideoPath();
                    break;
            }
        }

    };


    /**
     * 加载真正的播放地址
     */
    private void loadRealVideoPath() {
        isPermmitDownload = false;
        tv_download.setText("下载中");
        iv_download.setImageResource(R.drawable.download_press);
        App.getMyDownloadManager().initStartDownload(
                mVideoBean.id + "",
                mVideoBean.channel,
                mMovieDetailData.title,
                mMovieDetailData.poster_url,
                youkuSourceBean.values,
                "",
                youkuSourceBean.name);
    }


    public void onEventMainThread(AppEvent.SucGetVideoRealPathEvent e) {
        isPermmitDownload = false;
    }


    public void onEventMainThread(AppEvent.FailGetVideoRealPathEvent e) {
        isPermmitDownload = true;
        iv_download.setImageResource(R.drawable.download_default);
        tv_download.setText("下载");
    }


    /**
     * 发送收藏数据库发生变化的event事件
     */
    private void sentDBOccusChangingEvent() {
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(movieDetailRequestRunnable);
    }


    /**
     * *******************************************************************************
     */


    public void onEventMainThread(AppEvent.DownloadFinishedEvent e) {
        L.d(TAG, "... onEvent --> Download --> complete ...");
        L.d(TAG, "------> OnDownloadDetailCompleted : " + e.bean);
        L.d(TAG, "------> mVideoBean : " + mVideoBean);
        DBDownloadBean bean = e.bean;
        if (bean.videoId.equals(mVideoBean.id + "") && bean.videoChannel.equals(mVideoBean.channel)) {
            isPermmitDownload = false;
            isCached = true;
            tv_download.setText("下载完毕");
            iv_download.setImageResource(R.drawable.download_press);
        }
    }

}
