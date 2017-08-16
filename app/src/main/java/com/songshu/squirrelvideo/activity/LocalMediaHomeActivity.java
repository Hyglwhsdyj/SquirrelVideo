package com.songshu.squirrelvideo.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.adapter.LocalMediaAdapter;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.db.ormdb.MyOrmSQLiteDaoGeter;
import com.songshu.squirrelvideo.entity.po.PoJoLocalMedia;
import com.songshu.squirrelvideo.manager.TitleManager;
import com.songshu.squirrelvideo.service.MediaScannerService;
import com.songshu.squirrelvideo.utils.FileUtils;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.Util;
import com.songshu.squirrelvideo.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yb on 15-10-31.
 */
public class LocalMediaHomeActivity extends BaseActivity implements View.OnClickListener, MediaScannerService.IMediaScannerObserver, LocalMediaAdapter.DataDeleteListener {
    private static final String TAG = LocalMediaHomeActivity.class.getSimpleName() + ":";

    private Context mContext;
    private TextView tv_title;
    private LinearLayout ll_sub_title;
    private TextView local_media_btn_delete;
    private TextView local_media_btn_scan;
    private TextView local_media_btn_cancel;
    private ListView lv_video;
    private LinearLayout ll_msg;
    private ImageView iv_loading;
    private ImageView iv_result;
    private TextView tv_msg;


    private Map<String, TextView> mTextViews = new HashMap<String, TextView>();
    private TextView currentTextView;
    private MediaScannerService mMediaScannerService;
    private MyOrmSQLiteDaoGeter<PoJoLocalMedia> mMyOrmSQLiteDaoGeter;
    private LocalMediaAdapter mLocalMediaAdapter;
    private Map<String, ArrayList<PoJoLocalMedia>> localResult = new HashMap<String, ArrayList<PoJoLocalMedia>>();
    public static boolean isHomeActivityOnResume = false;
    private boolean dosedUserInAppFirstTimeAfterCleaningOldApp = true;


    private ServiceConnection mMediaScannerServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMediaScannerService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaScannerService = ((MediaScannerService.MediaScannerServiceBinder) service).getService();
            mMediaScannerService.addObserver(LocalMediaHomeActivity.this);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.d(TAG, "...... LocalMediaHomeActivity --> onCreate ......");
        mContext = LocalMediaHomeActivity.this;
        setContentView(R.layout.activity_local_media_home);
        mMyOrmSQLiteDaoGeter = new MyOrmSQLiteDaoGeter<PoJoLocalMedia>();

        initView();
        setSubTitleView();
        setSubTitleListener();
        setUpFucBtnViewStatus(false);
        setItemDeleteBtnStatus(false);

        dosedUserInAppFirstTimeAfterCleaningOldApp = true;
        isHomeActivityOnResume = false;

        new DataTask().execute();
        bindService(new Intent(App.getContext(), MediaScannerService.class), mMediaScannerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        L.d(TAG, "...... LocalMediaHomeActivity --> onNewIntent ......");
        super.onNewIntent(intent);
        if (dosedUserInAppFirstTimeAfterCleaningOldApp) {
            App.getActivityManager().emptyStackExceptMainAct();
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        L.d(TAG, "...... LocalMediaHomeActivity --> onResume ......");
        super.onResume();
        MobclickAgent.onResume(this);          //统计时长
        dosedUserInAppFirstTimeAfterCleaningOldApp = false;
        isHomeActivityOnResume = true;
    }

    @Override
    protected void onPause() {
        L.d(TAG, "...... LocalMediaHomeActivity --> onPause ......");
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        L.d(TAG, "...... LocalMediaHomeActivity --> onDestroy ......");
        unbindService(mMediaScannerServiceConnection);
        super.onDestroy();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);// 主标题
        tv_title.setText(TitleManager.TITLE_LOCAL);
        ll_sub_title = (LinearLayout) findViewById(R.id.ll_sub_title);// 子标题侧栏
        local_media_btn_delete = (TextView) findViewById(R.id.local_media_btn_delete);// 删除
        local_media_btn_scan = (TextView) findViewById(R.id.local_media_btn_scan);// 扫描
        local_media_btn_cancel = (TextView) findViewById(R.id.local_media_btn_cancel);// 取消
        lv_video = (ListView) findViewById(R.id.lv_video);// 展示视频
        ll_msg = (LinearLayout) findViewById(R.id.ll_msg);// 进度条布局
        iv_loading = (ImageView) findViewById(R.id.iv_loading);
        iv_result = (ImageView) findViewById(R.id.iv_result);
        tv_msg = (TextView) findViewById(R.id.tv_msg);

        local_media_btn_delete.setOnClickListener(this);
        local_media_btn_scan.setOnClickListener(this);
        local_media_btn_cancel.setOnClickListener(this);

        mLocalMediaAdapter = new LocalMediaAdapter(this);
        mLocalMediaAdapter.setOnDataDeleteListener(this);
        lv_video.setAdapter(mLocalMediaAdapter);
    }


    /**
     * 向左侧子标题栏中生成并添加子标题
     */
    private void setSubTitleView() {
        ll_sub_title.removeAllViews();
        for (String subTitle : TitleManager.SUBTITLE_DEFAULT_LOCAL) {
            TextView item = getSubTitleTextView(subTitle);
            item.setBackgroundResource(0);
            mTextViews.put(subTitle, item);
            ll_sub_title.addView(item);
        }
        TextView entairTX = mTextViews.get(TitleManager.LOCAL_SUB_TITLE_ENTAIR);
        entairTX.setBackgroundResource(R.drawable.left_arraw);
        currentTextView = entairTX;
    }


    /**
     * 增加子标题的监听
     */
    private void setSubTitleListener() {
        if (mTextViews.size() > 0) {
            for (String sub_title : mTextViews.keySet()) {
                mTextViews.get(sub_title).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView item = (TextView) v;
                        L.d(TAG, "click : " + item.getText());
                        if (getCurrentTextViewText().equals(item.getText().toString().trim())) {
                            return;
                        }
                        changeSubTitleBackground(item);
                        currentTextView = item;
                        updateMiddleViewAndAdapterStatus(true);
                    }
                });
            }
        }
    }


    /**
     * 获取制定名称的textview对象
     *
     * @param sub_title
     * @return
     */
    private TextView getSubTitleTextView(String sub_title) {
        TextView item = new TextView(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80);
        item.setLayoutParams(lp);
        item.setGravity(Gravity.CENTER);
        item.setTextSize(28);
        item.setTextColor(Color.parseColor("#ffffff"));
        item.setText(sub_title);
        return item;
    }


    /**
     * 修改被选子标题的背景
     *
     * @param item
     */
    private void changeSubTitleBackground(TextView item) {
        for (String sub_title : mTextViews.keySet()) {
            if (mTextViews.get(sub_title).getText().toString().trim().equals(item.getText().toString().trim())) {
                mTextViews.get(sub_title).setBackgroundResource(R.drawable.left_arraw);
            } else {
                mTextViews.get(sub_title).setBackgroundResource(0);
            }
        }
    }


    //___________________________________________________________________________


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local_media_btn_scan:
                setUpFucBtnViewStatus(false);
                setItemDeleteBtnStatus(false);
                startService(new Intent(mContext, MediaScannerService.class).putExtra(MediaScannerService.EXTRA_DIRECTORY, Const.PARENT_SDCARD));
                break;

            case R.id.local_media_btn_delete:
                setUpFucBtnViewStatus(true);
                setItemDeleteBtnStatus(true);
                break;

            case R.id.local_media_btn_cancel:
                setUpFucBtnViewStatus(false);
                setItemDeleteBtnStatus(false);
                break;
        }

    }


    /**
     * 扫描服务的回调
     *
     * @param flag  0 开始扫描 1 正在扫描 2 扫描完成
     * @param media 扫描到的视频文件
     */
    @Override
    public void update(int flag, PoJoLocalMedia media) {
        L.d(TAG, "......update.....flag......" + flag);
        switch (flag) {
            case MediaScannerService.SCAN_STATUS_START:// 扫描开始
                setViewsStatus(STATUS_SCANNING);
                break;

            case MediaScannerService.SCAN_STATUS_RUNNING:// 扫到一个文件
                if (mLocalMediaAdapter != null
                        && media != null
                        && (getCurrentTextViewText().equals(media.category)
                        || getCurrentTextViewText().equals(TitleManager.LOCAL_SUB_TITLE_ENTAIR))) {
                    mLocalMediaAdapter.add(media);
                }
                break;

            case MediaScannerService.SCAN_STATUS_END:// 扫描完成
                new DataTask().execute();
                App.showToast(R.string.local_media_toast_scan_complete);
                break;
        }
    }


    @Override
    public void itemDeleted(final PoJoLocalMedia mPoJoLocalMedia) {
        new CustomDialog.Builder(mContext)
                .setMessage(mContext.getResources().getString(R.string.local_media_dialog_delete))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteLocalMedia(mPoJoLocalMedia);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create(R.layout.dlg_local_media_delete_item)
                .show();
    }

    @Override
    public void itemNowNotAvailable(PoJoLocalMedia mPoJoLocalMedia) {
        deleteLocalMedia(mPoJoLocalMedia);
    }


    /**
     * 获取本地缓存的视频信息任务
     */
    private class DataTask extends AsyncTask<Void, Void, Map<String, ArrayList<PoJoLocalMedia>>> {

        @Override
        protected void onPreExecute() {
            L.d(TAG, "......onPreExecute......");
            super.onPreExecute();
            setViewsStatus(STATUS_PRE_EXECUTE);
        }

        @Override
        protected Map<String, ArrayList<PoJoLocalMedia>> doInBackground(Void... params) {
            L.d(TAG, "......doInBackground......");
            Map<String, ArrayList<PoJoLocalMedia>> temp = new HashMap<String, ArrayList<PoJoLocalMedia>>();
            for (String str : TitleManager.SUBTITLE_DEFAULT_LOCAL) {
                ArrayList<PoJoLocalMedia> localMediaCategoryList =
                        (ArrayList<PoJoLocalMedia>) mMyOrmSQLiteDaoGeter.queryForEqByEq(PoJoLocalMedia.class, "category", str, "last_modify_time");
                ArrayList<PoJoLocalMedia> filterCanReadVideo = new ArrayList<PoJoLocalMedia>();
                for(PoJoLocalMedia pojo : localMediaCategoryList) {
                    File video = new File(pojo.path);
                    if (video.exists() && video.isFile() && video.canRead()) {
                        filterCanReadVideo.add(pojo);
                    }
                }
                temp.put(str, filterCanReadVideo);
            }
            //查询所有视频放到全部分类中
            temp.remove(TitleManager.LOCAL_SUB_TITLE_ENTAIR);
            ArrayList<PoJoLocalMedia> fullVideo = (ArrayList<PoJoLocalMedia>) mMyOrmSQLiteDaoGeter.queryForAllByOrder(PoJoLocalMedia.class, "last_modify_time");
            ArrayList<PoJoLocalMedia> filterFullReadVideo = new ArrayList<PoJoLocalMedia>();
            for(PoJoLocalMedia pojo : fullVideo) {
                File video = new File(pojo.path);
                if (video.exists() && video.isFile() && video.canRead()) {
                    filterFullReadVideo.add(pojo);
                }
            }
            temp.put(TitleManager.LOCAL_SUB_TITLE_ENTAIR,filterFullReadVideo);
            return temp;
        }

        @Override
        protected void onPostExecute(Map<String, ArrayList<PoJoLocalMedia>> result) {
            L.d(TAG, "......onPostExecute......");
            super.onPostExecute(result);
            if (localResult != null) {
                localResult.clear();
            }
            localResult = result;
            updateMiddleViewAndAdapterStatus(true);
        }
    }


    /**
     * 删除文件的一整套流程
     * @param mPoJoLocalMedia
     */
    private void deleteLocalMedia(PoJoLocalMedia mPoJoLocalMedia) {
        FileUtils.deleteFile(mPoJoLocalMedia.path);
        FileUtils.deleteFile(mPoJoLocalMedia.thumb_path);
        for (String str : localResult.keySet()) {
            Iterator<PoJoLocalMedia> it = localResult.get(str).iterator();
            while (it.hasNext()) {
                PoJoLocalMedia next = it.next();
                if (next.title.equals(mPoJoLocalMedia.title)
                        && next.path.equals(mPoJoLocalMedia.path)
                        && next.thumb_path.equals(mPoJoLocalMedia.thumb_path)) {
                    it.remove();
                }
            }
        }
        mMyOrmSQLiteDaoGeter.remove(mPoJoLocalMedia);
        updateMiddleViewAndAdapterStatus(false);
    }

    //_______________________________________________________________________________________


    private static final int STATUS_PRE_EXECUTE = 100;//读取数据库
    private static final int STATUS_SCANNING = 0;//正在扫描
    private static final int STATUS_HAVE_RESULT = 1;//有数据
    private static final int STATUS_NO_RESULT = 2;//无数据

    /**
     * 修改扫描开始以及结束后的状态
     *
     * @param status
     */
    private void setViewsStatus(int status) {
        L.d(TAG, "......setViewsStatus......status......" + status);
        switch (status) {
            case STATUS_PRE_EXECUTE:
            case STATUS_SCANNING:
                for (String sub_title : mTextViews.keySet()) {
                    mTextViews.get(sub_title).setClickable(false);
                }
                lv_video.setVisibility(View.GONE);
                iv_loading.setVisibility(View.VISIBLE);
                iv_result.setVisibility(View.GONE);
                tv_msg.setVisibility(View.VISIBLE);
                tv_msg.setText(getString(R.string.local_media_loading));
                local_media_btn_delete.setClickable(false);
                local_media_btn_scan.setClickable(false);
                local_media_btn_cancel.setClickable(false);
                break;
            case STATUS_HAVE_RESULT:
                for (String sub_title : mTextViews.keySet()) {
                    mTextViews.get(sub_title).setClickable(true);
                }
                lv_video.setVisibility(View.VISIBLE);
                iv_loading.setVisibility(View.GONE);
                iv_result.setVisibility(View.GONE);
                tv_msg.setVisibility(View.GONE);
                local_media_btn_delete.setClickable(true);
                local_media_btn_scan.setClickable(true);
                local_media_btn_cancel.setClickable(true);
                break;
            case STATUS_NO_RESULT:
                for (String sub_title : mTextViews.keySet()) {
                    mTextViews.get(sub_title).setClickable(true);
                }
                lv_video.setVisibility(View.GONE);
                iv_loading.setVisibility(View.GONE);
                iv_result.setVisibility(View.VISIBLE);
                tv_msg.setVisibility(View.VISIBLE);
                tv_msg.setText(getString(R.string.local_media_no_result));
                local_media_btn_delete.setClickable(true);
                local_media_btn_scan.setClickable(true);
                local_media_btn_cancel.setClickable(true);
                break;
        }
    }


    /**
     * 修改顶部功能按键的状态
     *
     * @param isWantDelete
     */
    private void setUpFucBtnViewStatus(boolean isWantDelete) {
        L.d(TAG, "......setUpFucBtnViewStatus......isWantDelete......" + isWantDelete);
        if (isWantDelete) {
            local_media_btn_scan.setVisibility(View.GONE);
            local_media_btn_delete.setVisibility(View.GONE);
            local_media_btn_cancel.setVisibility(View.VISIBLE);
        } else {
            local_media_btn_scan.setVisibility(View.VISIBLE);
            local_media_btn_delete.setVisibility(View.VISIBLE);
            local_media_btn_cancel.setVisibility(View.GONE);
        }
    }


    /**
     * 更新删除图标的显示与否状态
     *
     * @param wantShow
     */
    private void setItemDeleteBtnStatus(boolean wantShow) {
        L.d(TAG, "......setItemDeleteBtnStatus......wantShow......" + wantShow);
        mLocalMediaAdapter.showDeleteIcon(lv_video, wantShow);
    }


    /**
     * 更新adaper数据
     *
     * @param category
     */
    private void updateAdapterData(String category) {
        L.d(TAG, "......updateAdapterData......category......" + category);
        if (localResult == null || localResult.size() == 0 ||
                localResult.get(category) == null || localResult.get(category).size() == 0) return;
        mLocalMediaAdapter.notifyData(localResult.get(category));
    }


    /**
     * 根据数据更新中间提示图片和信息的变化，以及有数据时adapter的变化
     */
    private void updateMiddleViewAndAdapterStatus(boolean setDefaultUpFuncStatus) {
        if (setDefaultUpFuncStatus) {
            setUpFucBtnViewStatus(false);
            setItemDeleteBtnStatus(false);
        }
        if (localResult.get(getCurrentTextViewText()).size() == 0) {
            setViewsStatus(STATUS_NO_RESULT);
        } else {
            updateAdapterData(getCurrentTextViewText());
            setViewsStatus(STATUS_HAVE_RESULT);
        }
    }


    /**
     * 获取当前子标题的名称
     *
     * @return
     */
    @NonNull
    private String getCurrentTextViewText() {
        return currentTextView.getText().toString().trim();
    }

    //___________________________________________________________________________


    public void onEventMainThread(AppEvent.NothingHappen e) {
        L.d(TAG, "... onEvent --> NothingHappen ...");
    }


    @Override
    public void onBackPressed() {
        exitApp();
    }

    /**
     * 退出app
     */
    private void exitApp() {
        Util.exitThisApp(mContext);
    }

}
