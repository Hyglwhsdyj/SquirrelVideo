package com.songshu.squirrelvideo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.adapter.FuncsAdapter;
import com.songshu.squirrelvideo.adapter.FuncsDownloadAdapter;
import com.songshu.squirrelvideo.adapter.FuncsHealthAndOperaAdapter;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.db.FavorietsDaoImpl;
import com.songshu.squirrelvideo.db.HistoryDaoImpl;
import com.songshu.squirrelvideo.entity.DBDownloadBean;
import com.songshu.squirrelvideo.entity.DBFavoritesBean;
import com.songshu.squirrelvideo.entity.DBHistoryBean;
import com.songshu.squirrelvideo.entity.FuncsDBDataBean;
import com.songshu.squirrelvideo.entity.FuncsDBDownloadDataBean;
import com.songshu.squirrelvideo.entity.Pictures;
import com.songshu.squirrelvideo.fragment.RecommondFragment;
import com.songshu.squirrelvideo.manager.TitleManager;
import com.songshu.squirrelvideo.utils.DensityUtil;
import com.songshu.squirrelvideo.utils.DeviceUtils;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.view.swipe.SwipeBackActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yb on 15-7-14.
 */
public class FuncsActivity extends SwipeBackActivity implements View.OnClickListener {

    private static final String TAG = FuncsActivity.class.getSimpleName() + ":";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funcs);
        mContext = this;
        historyDaoImpl = new HistoryDaoImpl(this);
        favorietsDaoImpl = new FavorietsDaoImpl(this);
        initView();
        setListener();
        setAdpter();

        initDB();
        initDownDB();
        initParam();
    }


    private ImageView iv_go_back;
    private LinearLayout lv_funcs;
    private TextView tv_history;
    private TextView tv_favority;
    private TextView tv_download;
    private TextView tv_modify_confirm;
    private ListView lv_video;
    private ListView lv_download;
    private ImageView iv_no_contant_logo;
    private TextView tv_no_contant;
    private RelativeLayout rl_sdcard;
    private TextView tv_already_use;
    private TextView tv_available_use;

    private void initView() {
        iv_go_back = (ImageView) findViewById(R.id.iv_go_back);

        lv_funcs = (LinearLayout) findViewById(R.id.lv_funcs);
        tv_history = (TextView) findViewById(R.id.tv_history);
        tv_favority = (TextView) findViewById(R.id.tv_favority);
        tv_download = (TextView) findViewById(R.id.tv_download);

        tv_modify_confirm = (TextView) findViewById(R.id.tv_modify_confirm);

        lv_video = (ListView) findViewById(R.id.lv_video);
        if (Const.TITLE.equals(TitleManager.TITLE_HEALTH) || Const.TITLE.equals(TitleManager.TITLE_OPERA)) {// 曲艺 少儿 每行只显示 3个
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(DensityUtil.dip2px(mContext, 120), 0, DensityUtil.dip2px(mContext, 120), 0);
            lv_video.setLayoutParams(lp);
        }
        lv_download = (ListView) findViewById(R.id.lv_download);

        iv_no_contant_logo = (ImageView) findViewById(R.id.iv_no_contant_logo);
        tv_no_contant = (TextView) findViewById(R.id.tv_no_contant);

        rl_sdcard = (RelativeLayout) findViewById(R.id.rl_sdcard);
        tv_already_use = (TextView) findViewById(R.id.tv_already_use);
        tv_available_use = (TextView) findViewById(R.id.tv_available_use);
    }


    private void setListener() {
        iv_go_back.setOnClickListener(this);
        tv_history.setOnClickListener(funcsListener);
        tv_favority.setOnClickListener(funcsListener);
        tv_download.setOnClickListener(funcsListener);
        tv_modify_confirm.setOnClickListener(modify_confirm_listener);
    }


    private FuncsAdapter funcsAdapter;
    private FuncsHealthAndOperaAdapter mFuncsHealthAndOperaAdapter;
    private FuncsDownloadAdapter funcsDownloadAdapter;

    private void setAdpter() {
        if (Const.TITLE.equals(TitleManager.TITLE_HEALTH) || Const.TITLE.equals(TitleManager.TITLE_OPERA)) {// 曲艺 少儿 每行只显示 3个 不同 adapter
            mFuncsHealthAndOperaAdapter = new FuncsHealthAndOperaAdapter(mContext);
            lv_video.setAdapter(mFuncsHealthAndOperaAdapter);
            mFuncsHealthAndOperaAdapter.setOnDataDeleteListener(new FuncsHealthAndOperaAdapter.DataDeleteListener() {
                @Override
                public void itemDeleted(final FuncsDBDataBean mFuncsDBDataBean) {
                    switch (current_chose_type) {
                        case TYPE_HISTORY:
                            historyAdapterDataList.remove(mFuncsDBDataBean);
                            changeMiddleContantView(TYPE_HISTORY);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String realDBHistoryId = getRealDBHistoryID(mFuncsDBDataBean.id + "");
                                    boolean history_delete_result = historyDaoImpl.deleteHistory(realDBHistoryId, mFuncsDBDataBean.channel);
                                    while (!history_delete_result) {
                                        history_delete_result = historyDaoImpl.deleteHistory(realDBHistoryId, mFuncsDBDataBean.channel);
                                    }
                                }
                            });
                            break;

                        case TYPE_FAVORITY:
                            favorityAdapterDataList.remove(mFuncsDBDataBean);
                            changeMiddleContantView(TYPE_FAVORITY);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    boolean favority_delete_result = favorietsDaoImpl.deleteFavoriets(mFuncsDBDataBean.id + "", mFuncsDBDataBean.channel);
                                    while (!favority_delete_result) {
                                        favority_delete_result = favorietsDaoImpl.deleteFavoriets(mFuncsDBDataBean.id + "", mFuncsDBDataBean.channel);
                                    }
                                }
                            });
                            break;
                    }
                }
            });
        } else {
            funcsAdapter = new FuncsAdapter(mContext);
            lv_video.setAdapter(funcsAdapter);
            funcsAdapter.setOnDataDeleteListener(new FuncsAdapter.DataDeleteListener() {
                @Override
                public void itemDeleted(final FuncsDBDataBean mFuncsDBDataBean) {
                    switch (current_chose_type) {
                        case TYPE_HISTORY:
                            historyAdapterDataList.remove(mFuncsDBDataBean);
                            changeMiddleContantView(TYPE_HISTORY);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String realDBHistoryId = getRealDBHistoryID(mFuncsDBDataBean.id + "");
                                    boolean history_delete_result = historyDaoImpl.deleteHistory(realDBHistoryId, mFuncsDBDataBean.channel);
                                    while (!history_delete_result) {
                                        history_delete_result = historyDaoImpl.deleteHistory(realDBHistoryId, mFuncsDBDataBean.channel);
                                    }
                                }
                            });
                            break;

                        case TYPE_FAVORITY:
                            favorityAdapterDataList.remove(mFuncsDBDataBean);
                            changeMiddleContantView(TYPE_FAVORITY);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    boolean favority_delete_result = favorietsDaoImpl.deleteFavoriets(mFuncsDBDataBean.id + "", mFuncsDBDataBean.channel);
                                    while (!favority_delete_result) {
                                        favority_delete_result = favorietsDaoImpl.deleteFavoriets(mFuncsDBDataBean.id + "", mFuncsDBDataBean.channel);
                                    }
                                }
                            });
                            break;
                    }
                }
            });
        }


        funcsDownloadAdapter = new FuncsDownloadAdapter(mContext);
        lv_download.setAdapter(funcsDownloadAdapter);
        funcsDownloadAdapter.setOnAdapterModifyChangedListener(new FuncsDownloadAdapter.AdapterModifyChangedListener() {
            @Override
            public void itemDeleted(final FuncsDBDownloadDataBean mFuncsDBDownloadDataBean) {
                downloadAdapterDataList.remove(mFuncsDBDownloadDataBean);
                changeMiddleContantView(TYPE_DOWNLOAD);
                App.getMyDownloadManager().deleteDownload(mFuncsDBDownloadDataBean.id, mFuncsDBDownloadDataBean.channel);
            }

            @Override
            public void wantToPause(final FuncsDBDownloadDataBean mFuncsDBDownloadDataBean) {
                App.getMyDownloadManager().pauseDownload(mFuncsDBDownloadDataBean.id, mFuncsDBDownloadDataBean.channel);
            }

            @Override
            public void wantToDownload(final FuncsDBDownloadDataBean mFuncsDBDownloadDataBean) {
                App.getMyDownloadManager().startDownload(mFuncsDBDownloadDataBean.id, mFuncsDBDownloadDataBean.channel);
            }

            @Override
            public void waitClick(final FuncsDBDownloadDataBean mFuncsDBDownloadDataBean) {
                App.getMyDownloadManager().handleWaitTaskClick(mFuncsDBDownloadDataBean.id, mFuncsDBDownloadDataBean.channel);
            }
        });
    }


    public void onEventMainThread(AppEvent.DBOccusChanging e) {
        L.d(TAG, "... onEvent --> DBOccusChanging ...");
        initDB();
        initParam();
    }

    private HistoryDaoImpl historyDaoImpl; // 操作类
    private FavorietsDaoImpl favorietsDaoImpl;
    private boolean isHistoryTableEmpty = false; // 表是否存在
    private boolean isFavorityTableEmpty = false;
    private List<DBHistoryBean> history_list = new ArrayList<DBHistoryBean>(); // 数据库数据
    private List<DBFavoritesBean> favority_list = new ArrayList<DBFavoritesBean>(); // 数据库数据
    private List<FuncsDBDataBean> historyAdapterDataList = new LinkedList<FuncsDBDataBean>(); // 用与Adapter的数据集合,功能是本地的非数据库操作数据
    private List<FuncsDBDataBean> favorityAdapterDataList = new LinkedList<FuncsDBDataBean>(); // 用与Adapter的数据集合,功能是本地的非数据库操作数据

    private void initDB() {
        isHistoryTableEmpty = historyDaoImpl.isTableEmpty();
        isFavorityTableEmpty = favorietsDaoImpl.isTableEmpty();
        historyAdapterDataList.clear();
        favorityAdapterDataList.clear();
        if (!isHistoryTableEmpty) {
            history_list = historyDaoImpl.findHistory();
            if (history_list != null && history_list.size() > 0) {
                for (DBHistoryBean item : history_list) {
                    FuncsDBDataBean bean = new FuncsDBDataBean();
                    bean.id = Integer.parseInt(item.getVideoId().contains(Const.MYWIFEANDIBIRTH) ? item.getVideoId().split(Const.MYWIFEANDIBIRTH)[0] : item.getVideoId());
                    bean.channel = item.getVideoChannel();
                    bean.picture = new Pictures();
                    bean.picture.big = item.getVideoPic();
                    bean.picture.small = item.getVideoPic();
                    bean.title = item.getVideoName();
                    bean.isDeleteBtnShow = false;
                    historyAdapterDataList.add(bean);
                }
            }
        }
        if (!isFavorityTableEmpty) {
            favority_list = favorietsDaoImpl.findFavoriets();
            if (favority_list != null && favority_list.size() > 0) {
                for (DBFavoritesBean item : favority_list) {
                    FuncsDBDataBean bean = new FuncsDBDataBean();
                    bean.id = Integer.parseInt(item.getVideoId());
                    bean.channel = item.getVideoChannel();
                    bean.picture = new Pictures();
                    bean.picture.big = item.getVideoPic();
                    bean.picture.small = item.getVideoPic();
                    bean.title = item.getVideoName();
                    bean.isDeleteBtnShow = false;
                    favorityAdapterDataList.add(bean);
                }
            }
        }
        L.d(TAG, "initDB --> isHistoryTableEmpty : " + isHistoryTableEmpty + " , isFavorityTableEmpty : " + isFavorityTableEmpty + " , historyAdapterDataList : " + historyAdapterDataList + " , favorityAdapterDataList : " + favorityAdapterDataList);
    }

    private boolean isDownloadTableEmpty = false;
    private List<DBDownloadBean> download_list = new ArrayList<DBDownloadBean>(); // 数据库数据
    private List<FuncsDBDownloadDataBean> downloadAdapterDataList = new LinkedList<FuncsDBDownloadDataBean>(); // 用与Adapter的数据集合,功能是本地的非数据库操作数据

    private void initDownDB() {
        isDownloadTableEmpty = App.getMyDownloadManager().isDownloadTableEmpty();
        downloadAdapterDataList.clear();
        if (!isDownloadTableEmpty) {
            download_list = App.getMyDownloadManager().findAllKindsOfDownloadTasks();
            if (download_list != null && download_list.size() > 0) {
                for (DBDownloadBean item : download_list) {
                    FuncsDBDownloadDataBean bean = new FuncsDBDownloadDataBean();
                    bean.id = item.videoId;
                    bean.channel = item.videoChannel;
                    bean.picture = new Pictures();
                    bean.picture.big = item.videoPosterUrl;
                    bean.picture.small = item.videoPosterUrl;
                    bean.title = item.videoName;
                    bean.download_state = item.videoDownloadState;
                    bean.episode = item.videoWhichEpisode;
                    bean.videoCurrentProcess = item.videoCurrentProcess;
                    bean.isDeleteBtnShow = false;
                    downloadAdapterDataList.add(bean);
                }
            }
        }
        L.d(TAG, "initDB --> isDownloadTableEmpty : " + isDownloadTableEmpty + " , downloadAdapterDataList : " + downloadAdapterDataList);
    }

    private void initParam() {
        Intent intent = getIntent();
        int type_jump = intent.getIntExtra(RecommondFragment.TYPE_JUMP, RecommondFragment.TYPE_JUMP_HISTORY);
        current_chose_type = type_jump;
        changeTopFuncsView(type_jump);
        changeMiddleContantView(type_jump);
    }


    /**
     * 顶部右侧编辑和确定的监听
     */
    private View.OnClickListener modify_confirm_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView textView = (TextView) v;
            if (STATUS_EDITE.equals(textView.getText().toString().trim())) {// 进入编辑状态, 显示完成
                changeTopRightFuncView(RIGHT_BTN_STATUS_COMPLETE);
                if (current_chose_type == TYPE_DOWNLOAD) {
                    showDownloadAdapterDeleteIcon(true);
                } else {
                    showAdapterDeleteIcon(true);
                }
            } else if (STATUS_COMPLETE.equals(textView.getText().toString().trim())) {// 进入完成状态, 显示编辑
                changeTopRightFuncView(RIGHT_BTN_STATUS_EDITE);
                if (current_chose_type == TYPE_DOWNLOAD) {
                    showDownloadAdapterDeleteIcon(false);
                } else {
                    showAdapterDeleteIcon(false);
                }
            }
        }
    };


    private static int current_chose_type;
    public static final int TYPE_HISTORY = 0;
    public static final int TYPE_FAVORITY = 1;
    public static final int TYPE_DOWNLOAD = 2;
    /**
     * 顶部功能的监听
     */
    private View.OnClickListener funcsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_history:
                    if (current_chose_type == TYPE_HISTORY) return;
                    current_chose_type = TYPE_HISTORY;
                    changeTopFuncsView(TYPE_HISTORY);
                    changeMiddleContantView(TYPE_HISTORY);
                    break;
                case R.id.tv_favority:
                    if (current_chose_type == TYPE_FAVORITY) return;
                    current_chose_type = TYPE_FAVORITY;
                    changeTopFuncsView(TYPE_FAVORITY);
                    changeMiddleContantView(TYPE_FAVORITY);
                    break;
                case R.id.tv_download:
                    if (current_chose_type == TYPE_DOWNLOAD) return;
                    current_chose_type = TYPE_DOWNLOAD;
                    changeTopFuncsView(TYPE_DOWNLOAD);
                    changeMiddleContantView(TYPE_DOWNLOAD);
                    break;
            }
        }
    };


    /**
     * 修改顶部功能的背景和文字颜色
     *
     * @param which
     */
    private void changeTopFuncsView(int which) {
        switch (which) {
            case TYPE_HISTORY:// 历史 左
                tv_history.setBackgroundResource(R.drawable.func_act_top_btn_left_check_sharp);
                tv_history.setTextColor(getResources().getColor(R.color.func_top_btn));
                tv_favority.setBackgroundResource(R.drawable.func_act_top_btn_middle_uncheck_sharp);
                tv_favority.setTextColor(getResources().getColor(R.color.white));
                tv_download.setBackgroundResource(R.drawable.func_act_top_btn_right_uncheck_sharp);
                tv_download.setTextColor(getResources().getColor(R.color.white));
                break;
            case TYPE_FAVORITY: // 收藏 中
                tv_history.setBackgroundResource(R.drawable.func_act_top_btn_left_uncheck_sharp);
                tv_history.setTextColor(getResources().getColor(R.color.white));
                tv_favority.setBackgroundResource(R.drawable.func_act_top_btn_middle_check_sharp);
                tv_favority.setTextColor(getResources().getColor(R.color.func_top_btn));
                tv_download.setBackgroundResource(R.drawable.func_act_top_btn_right_uncheck_sharp);
                tv_download.setTextColor(getResources().getColor(R.color.white));
                break;
            case TYPE_DOWNLOAD: // 下载 右
                tv_history.setBackgroundResource(R.drawable.func_act_top_btn_left_uncheck_sharp);
                tv_history.setTextColor(getResources().getColor(R.color.white));
                tv_favority.setBackgroundResource(R.drawable.func_act_top_btn_middle_uncheck_sharp);
                tv_favority.setTextColor(getResources().getColor(R.color.white));
                tv_download.setBackgroundResource(R.drawable.func_act_top_btn_right_check_sharp);
                tv_download.setTextColor(getResources().getColor(R.color.func_top_btn));
                break;
        }

    }


    /**
     * 检查并改变对应的界面的数据库数据是否为空,并显示对应的文字
     *
     * @param which
     */
    private void changeMiddleContantView(int which) {
        switch (which) {
            case TYPE_HISTORY:
                if (historyAdapterDataList == null || historyAdapterDataList.size() == 0) {
                    lv_video.setVisibility(View.GONE);
                    lv_download.setVisibility(View.GONE);
                    rl_sdcard.setVisibility(View.GONE);
                    iv_no_contant_logo.setVisibility(View.VISIBLE);
                    tv_no_contant.setVisibility(View.VISIBLE);
                    tv_no_contant.setText("还没有历史记录");
                    changeTopRightFuncView(RIGHT_BTN_STATUS_HIDE);
                } else {
                    lv_video.setVisibility(View.VISIBLE);
                    lv_download.setVisibility(View.GONE);
                    rl_sdcard.setVisibility(View.GONE);
                    iv_no_contant_logo.setVisibility(View.GONE);
                    tv_no_contant.setVisibility(View.GONE);
                    changeTopRightFuncView(historyAdapterDataList.get(0).isDeleteBtnShow ? RIGHT_BTN_STATUS_COMPLETE : RIGHT_BTN_STATUS_EDITE);
                    updataListViewAdapter(which);
                }
                break;

            case TYPE_FAVORITY:
                if (favorityAdapterDataList == null || favorityAdapterDataList.size() == 0) {
                    lv_video.setVisibility(View.GONE);
                    lv_download.setVisibility(View.GONE);
                    rl_sdcard.setVisibility(View.GONE);
                    iv_no_contant_logo.setVisibility(View.VISIBLE);
                    tv_no_contant.setVisibility(View.VISIBLE);
                    tv_no_contant.setText("还没有收藏视频");
                    changeTopRightFuncView(RIGHT_BTN_STATUS_HIDE);
                } else {
                    lv_video.setVisibility(View.VISIBLE);
                    lv_download.setVisibility(View.GONE);
                    rl_sdcard.setVisibility(View.GONE);
                    iv_no_contant_logo.setVisibility(View.GONE);
                    tv_no_contant.setVisibility(View.GONE);
                    changeTopRightFuncView(favorityAdapterDataList.get(0).isDeleteBtnShow ? RIGHT_BTN_STATUS_COMPLETE : RIGHT_BTN_STATUS_EDITE);
                    updataListViewAdapter(which);
                }
                break;

            case TYPE_DOWNLOAD:
                if (downloadAdapterDataList == null || downloadAdapterDataList.size() == 0) {
                    lv_video.setVisibility(View.GONE);
                    lv_download.setVisibility(View.GONE);
                    rl_sdcard.setVisibility(View.GONE);
                    iv_no_contant_logo.setVisibility(View.VISIBLE);
                    tv_no_contant.setVisibility(View.VISIBLE);
                    tv_no_contant.setText("还没有下载视频");
                    changeTopRightFuncView(RIGHT_BTN_STATUS_HIDE);
                } else {
                    lv_download.setVisibility(View.VISIBLE);
                    lv_video.setVisibility(View.GONE);
                    rl_sdcard.setVisibility(View.VISIBLE);
                    tv_already_use.setText(getString(R.string.sd_alread_use, DeviceUtils.getAlreadyUseSize(mContext)));
                    tv_available_use.setText(getString(R.string.sd_available, DeviceUtils.getSDAvailableSize(mContext)));
                    iv_no_contant_logo.setVisibility(View.GONE);
                    tv_no_contant.setVisibility(View.GONE);
                    changeTopRightFuncView(downloadAdapterDataList.get(0).isDeleteBtnShow ? RIGHT_BTN_STATUS_COMPLETE : RIGHT_BTN_STATUS_EDITE);
                    updataListViewAdapter(which);
                }
                break;
        }

    }

    private static final int RIGHT_BTN_STATUS_HIDE = 0;
    private static final int RIGHT_BTN_STATUS_EDITE = 1;
    private static final int RIGHT_BTN_STATUS_COMPLETE = 2;

    private static final String STATUS_EDITE = "编辑";
    private static final String STATUS_COMPLETE = "完成";

    /**
     * 修改右上角编辑和完成功能的view状态
     *
     * @param type
     */
    private void changeTopRightFuncView(int type) {
        switch (type) {
            case RIGHT_BTN_STATUS_HIDE: // 隐藏
                tv_modify_confirm.setVisibility(View.GONE);
                break;
            case RIGHT_BTN_STATUS_EDITE: // 编辑状态
                tv_modify_confirm.setVisibility(View.VISIBLE);
                tv_modify_confirm.setText(STATUS_EDITE);
                tv_modify_confirm.setCompoundDrawables(getResources().getDrawable(R.drawable.delete_selector), null, null, null);
                tv_modify_confirm.setCompoundDrawablePadding(DensityUtil.px2dip(mContext, 10));
                break;
            case RIGHT_BTN_STATUS_COMPLETE: // 完成状态
                tv_modify_confirm.setVisibility(View.VISIBLE);
                tv_modify_confirm.setText(STATUS_COMPLETE);
                tv_modify_confirm.setCompoundDrawables(null, null, null, null);
                break;
        }

    }


    /**
     * 根据不同界面的切换,显示不同的数据
     *
     * @param type
     */
    private void updataListViewAdapter(int type) {
        switch (type) {
            case TYPE_HISTORY:
                if (Const.TITLE.equals(TitleManager.TITLE_HEALTH) || Const.TITLE.equals(TitleManager.TITLE_OPERA)) {
                    mFuncsHealthAndOperaAdapter.notifyData(historyAdapterDataList);
                } else {
                    funcsAdapter.notifyData(historyAdapterDataList);
                }
                break;
            case TYPE_FAVORITY:
                if (Const.TITLE.equals(TitleManager.TITLE_HEALTH) || Const.TITLE.equals(TitleManager.TITLE_OPERA)) {
                    mFuncsHealthAndOperaAdapter.notifyData(favorityAdapterDataList);
                } else {
                    funcsAdapter.notifyData(favorityAdapterDataList);
                }
                break;
            case TYPE_DOWNLOAD:
                funcsDownloadAdapter.notifyData(downloadAdapterDataList);
                break;
        }

    }


    /**
     * 修改要传递给adapter的对象的布尔值,即是否显示删除图标
     *
     * @param isWantShow
     */
    private void showAdapterDeleteIcon(boolean isWantShow) {
        if (Const.TITLE.equals(TitleManager.TITLE_HEALTH) || Const.TITLE.equals(TitleManager.TITLE_OPERA)) {
            mFuncsHealthAndOperaAdapter.showDeleteIcon(lv_video, isWantShow);
        } else {
            funcsAdapter.showDeleteIcon(lv_video, isWantShow);
        }
    }


    /**
     * 修改要传递给adapter的对象的布尔值,即是否显示删除图标
     *
     * @param isWantShow
     */
    private void showDownloadAdapterDeleteIcon(boolean isWantShow) {
        funcsDownloadAdapter.showDeleteIcon(lv_download, isWantShow);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_go_back:
                finish();
                break;
        }
    }


    /**
     * *******************************************************************
     */

    public void onEventMainThread(AppEvent.DownloadStartedEvent e) {
        L.d(TAG, "... onEvent --> DownloadStartedEvent ...");
        for (int i = 0; i < downloadAdapterDataList.size(); i++) {
            if ((downloadAdapterDataList.get(i).id.equals(e.bean.videoId)) && (downloadAdapterDataList.get(i).channel.equals(e.bean.videoChannel))) {
                downloadAdapterDataList.get(i).download_state = e.bean.videoDownloadState;
                funcsDownloadAdapter.updateStateForOneItem(false, lv_download, i);
                break;
            }
        }
    }


    public void onEventMainThread(AppEvent.DownloadPausedEvent e) {
        L.d(TAG, "... onEvent --> DownloadPausedEvent ...");
        for (int i = 0; i < downloadAdapterDataList.size(); i++) {
            if ((downloadAdapterDataList.get(i).id.equals(e.bean.videoId)) && (downloadAdapterDataList.get(i).channel.equals(e.bean.videoChannel))) {
                downloadAdapterDataList.get(i).download_state = e.bean.videoDownloadState;
                funcsDownloadAdapter.updateStateForOneItem(true, lv_download, i);
                break;
            }
        }
    }


    public void onEventMainThread(AppEvent.DownloadProcessEvent e) {
//        L.d(TAG, "... onEvent --> DownloadProcessEvent ...");
        for (int i = 0; i < downloadAdapterDataList.size(); i++) {
            if ((downloadAdapterDataList.get(i).id.equals(e.bean.videoId)) && (downloadAdapterDataList.get(i).channel.equals(e.bean.videoChannel))) {
                downloadAdapterDataList.get(i).videoCurrentProcess = e.bean.videoCurrentProcess;
                downloadAdapterDataList.get(i).download_state = e.bean.videoDownloadState;
                funcsDownloadAdapter.updateProcessForOneItem(lv_download, i);
                break;
            }
        }
    }


    public void onEventMainThread(AppEvent.DownloadFinishedEvent e) {
        L.d(TAG, "... onEvent --> DownloadFinishedEvent ...");
        for (int i = 0; i < downloadAdapterDataList.size(); i++) {
            if ((downloadAdapterDataList.get(i).id.equals(e.bean.videoId)) && (downloadAdapterDataList.get(i).channel.equals(e.bean.videoChannel))) {
                downloadAdapterDataList.get(i).download_state = e.bean.videoDownloadState;
                updataListViewAdapter(TYPE_DOWNLOAD);
                break;
            }
        }
    }


    public void onEventMainThread(AppEvent.ReachMaxDownloadTaskNumEvent e) {
        L.d(TAG, "... onEvent --> ReachMaxDownloadTaskNumEvent ...");
        App.showToast(mContext.getString(R.string.download_already_has_two_task));
    }


    public void onEventMainThread(AppEvent.DownloadNewPreComeEvent e) {
        L.d(TAG, "... onEvent --> onEventMainThread ...");
        initDownDB();
        updataListViewAdapter(TYPE_DOWNLOAD);
    }

    /**
     * 点击下载完毕的视频,直接播放,播放完毕后的回调处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.d(TAG, "............onActivityResult......requestCode : " + requestCode + " , resultCode : " + resultCode + "............");
        if (resultCode == RESULT_OK) {
            String currentPalyPos = String.valueOf(data.getLongExtra("currentPlayPos", 0l));
            String currentPlayIndex = data.getStringExtra("index");
            String fucVideoUrl = String.valueOf(data.getStringExtra("func_video_url"));
            String watchDate = String.valueOf(System.currentTimeMillis());
            DBDownloadBean dbDownloadBean = App.getMyDownloadManager().findDBDownloadBean(fucVideoUrl);
            DBHistoryBean history = new DBHistoryBean();
            history.setVideoName(dbDownloadBean.videoName);
            history.setVideoId(dbDownloadBean.videoId);
            history.setVideoPic(dbDownloadBean.videoPosterUrl);
            history.setVideoChannel(dbDownloadBean.videoChannel);
            history.setTime(currentPalyPos);
            history.setWatchDate(watchDate);
            if (requestCode == 200) {
                history.setVideoIndex(currentPlayIndex);
            }
            L.d(TAG, "待处理数据 --> DBHistoryBean : " + history);
            boolean flag = historyDaoImpl.addHistory(history);
            L.d(TAG, "数据库添加该条数据 --> " + history);
            if (!flag) {
                L.d(TAG, "数据库存在该条数据 --> 更新数据");
                historyDaoImpl.updateHistory(history);
            }
            initDB();
            initParam();
        }
    }


    /**
     * 获取观看历史数据库的真实id
     *
     * @param oriId
     * @return
     */
    private String getRealDBHistoryID(String oriId) {
        if (TextUtils.isEmpty(oriId)) return "";
        for (DBHistoryBean bean : history_list) {
            if (bean.getVideoId().contains(oriId) && bean.getVideoId().startsWith(oriId)) {
                return bean.getVideoId();
            }
        }
        return "";
    }

}