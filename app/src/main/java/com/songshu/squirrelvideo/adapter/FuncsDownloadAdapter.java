package com.songshu.squirrelvideo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.golshadi.majid.core.enums.TaskStates;
import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.db.HistoryDaoImpl;
import com.songshu.squirrelvideo.entity.DBDownloadBean;
import com.songshu.squirrelvideo.entity.DBHistoryBean;
import com.songshu.squirrelvideo.entity.FuncsDBDownloadDataBean;
import com.songshu.squirrelvideo.player.PlayVideoActivity;
import com.songshu.squirrelvideo.utils.ImageLoaderUtils;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.view.CycleProcessBar;

import java.io.File;
import java.util.List;

/**
 * Created by yb on 15-7-9.
 */
public class FuncsDownloadAdapter extends MyBaseAdapter {

    private static final String TAG = FuncsDownloadAdapter.class.getSimpleName() + ":";
    private Context mContext;
    private List<FuncsDBDownloadDataBean> dataList;


    public FuncsDownloadAdapter(Context mContext) {
        this.mContext = mContext;
    }


    public void notifyData(List<FuncsDBDownloadDataBean> tempList) {
        L.d(TAG, "notifyData --> datalist size : " + tempList.size());
        dataList = tempList;
        notifyDataSetChanged();
    }


    /**
     * 显示/关闭删除按钮
     *
     * @param listview
     * @param isShow
     */
    public void showDeleteIcon(ListView listview, boolean isShow) {
        for (FuncsDBDownloadDataBean bean : dataList) {
            bean.isDeleteBtnShow = isShow;
        }

        int firstVisiblePosition = listview.getFirstVisiblePosition();
        for (int position = 0; position < getCount(); position++) {
            View wantUpdata = listview.getChildAt(position - firstVisiblePosition);
            getView(position, wantUpdata, null, true);
        }

    }

    /**
     * 更新某一个具体的item的process进度
     */
    public void updateProcessForOneItem(ListView listview, int itemIndex) {
        int firstVisiblePosition = listview.getFirstVisiblePosition();
        int position = itemIndex / 5;
        View wantUpdata = listview.getChildAt(position - firstVisiblePosition);
        switch (itemIndex % 5) {
            case 0:
                FuncsDBDownloadDataBean bean0 = dataList.get(position * 5);
                if (bean0.download_state == TaskStates.DOWNLOADING) {
                    RelativeLayout rl_download_layer_1 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_1);
                    CycleProcessBar cp_download_process_1 = (CycleProcessBar) wantUpdata.findViewById(R.id.cp_download_process_1);
                    ImageView iv_download_pause_1 = (ImageView) wantUpdata.findViewById(R.id.iv_download_pause_1);
                    RelativeLayout rl_download_layer_hold_1 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_hold_1);
                    LinearLayout ll_1 = (LinearLayout) wantUpdata.findViewById(R.id.ll_1);
                    rl_download_layer_1.setVisibility(View.VISIBLE);
                    cp_download_process_1.setVisibility(View.VISIBLE);
                    iv_download_pause_1.setVisibility(View.GONE);
                    rl_download_layer_hold_1.setVisibility(View.GONE);
                    ll_1.setClickable(false);
                    cp_download_process_1.setProgress(bean0.videoCurrentProcess);
                }
                break;
            case 1:
                FuncsDBDownloadDataBean bean1 = dataList.get(position * 5 + 1);
                if (bean1.download_state == TaskStates.DOWNLOADING) {
                    RelativeLayout rl_download_layer_2 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_2);
                    CycleProcessBar cp_download_process_2 = (CycleProcessBar) wantUpdata.findViewById(R.id.cp_download_process_2);
                    ImageView iv_download_pause_2 = (ImageView) wantUpdata.findViewById(R.id.iv_download_pause_2);
                    RelativeLayout rl_download_layer_hold_2 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_hold_2);
                    LinearLayout ll_2 = (LinearLayout) wantUpdata.findViewById(R.id.ll_2);
                    rl_download_layer_2.setVisibility(View.VISIBLE);
                    cp_download_process_2.setVisibility(View.VISIBLE);
                    iv_download_pause_2.setVisibility(View.GONE);
                    rl_download_layer_hold_2.setVisibility(View.GONE);
                    ll_2.setClickable(false);
                    cp_download_process_2.setProgress(bean1.videoCurrentProcess);
                }
                break;
            case 2:
                FuncsDBDownloadDataBean bean2 = dataList.get(position * 5 + 2);
                if (bean2.download_state == TaskStates.DOWNLOADING) {
                    RelativeLayout rl_download_layer_3 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_3);
                    CycleProcessBar cp_download_process_3 = (CycleProcessBar) wantUpdata.findViewById(R.id.cp_download_process_3);
                    ImageView iv_download_pause_3 = (ImageView) wantUpdata.findViewById(R.id.iv_download_pause_3);
                    RelativeLayout rl_download_layer_hold_3 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_hold_3);
                    LinearLayout ll_3 = (LinearLayout) wantUpdata.findViewById(R.id.ll_3);
                    rl_download_layer_3.setVisibility(View.VISIBLE);
                    cp_download_process_3.setVisibility(View.VISIBLE);
                    iv_download_pause_3.setVisibility(View.GONE);
                    rl_download_layer_hold_3.setVisibility(View.GONE);
                    ll_3.setClickable(false);
                    cp_download_process_3.setProgress(bean2.videoCurrentProcess);
                }
                break;
            case 3:
                FuncsDBDownloadDataBean bean3 = dataList.get(position * 5 + 3);
                if (bean3.download_state == TaskStates.DOWNLOADING) {
                    RelativeLayout rl_download_layer_4 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_4);
                    CycleProcessBar cp_download_process_4 = (CycleProcessBar) wantUpdata.findViewById(R.id.cp_download_process_4);
                    ImageView iv_download_pause_4 = (ImageView) wantUpdata.findViewById(R.id.iv_download_pause_4);
                    RelativeLayout rl_download_layer_hold_4 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_hold_4);
                    LinearLayout ll_4 = (LinearLayout) wantUpdata.findViewById(R.id.ll_4);
                    rl_download_layer_4.setVisibility(View.VISIBLE);
                    cp_download_process_4.setVisibility(View.VISIBLE);
                    iv_download_pause_4.setVisibility(View.GONE);
                    rl_download_layer_hold_4.setVisibility(View.GONE);
                    ll_4.setClickable(false);
                    cp_download_process_4.setProgress(bean3.videoCurrentProcess);
                }
                break;
            case 4:
                FuncsDBDownloadDataBean bean4 = dataList.get(position * 5 + 4);
                if (bean4.download_state == TaskStates.DOWNLOADING) {
                    RelativeLayout rl_download_layer_5 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_5);
                    CycleProcessBar cp_download_process_5 = (CycleProcessBar) wantUpdata.findViewById(R.id.cp_download_process_5);
                    ImageView iv_download_pause_5 = (ImageView) wantUpdata.findViewById(R.id.iv_download_pause_5);
                    RelativeLayout rl_download_layer_hold_5 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_hold_5);
                    LinearLayout ll_5 = (LinearLayout) wantUpdata.findViewById(R.id.ll_5);
                    rl_download_layer_5.setVisibility(View.VISIBLE);
                    cp_download_process_5.setVisibility(View.VISIBLE);
                    iv_download_pause_5.setVisibility(View.GONE);
                    rl_download_layer_hold_5.setVisibility(View.GONE);
                    ll_5.setClickable(false);
                    cp_download_process_5.setProgress(bean4.videoCurrentProcess);
                }
                break;
        }

    }


    /**
     * 更新单个item的下载和暂停接口
     *
     * @param isPause
     * @param listview
     * @param itemIndex
     */
    public void updateStateForOneItem(boolean isPause, ListView listview, int itemIndex) {
        int firstVisiblePosition = listview.getFirstVisiblePosition();
        int position = itemIndex / 5;
        View wantUpdata = listview.getChildAt(position - firstVisiblePosition);
        switch (itemIndex % 5) {
            case 0:
                FuncsDBDownloadDataBean bean0 = dataList.get(position * 5);
                if (isPause) {
                    RelativeLayout rl_download_layer_1 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_1);
                    CycleProcessBar cp_download_process_1 = (CycleProcessBar) wantUpdata.findViewById(R.id.cp_download_process_1);
                    ImageView iv_download_pause_1 = (ImageView) wantUpdata.findViewById(R.id.iv_download_pause_1);
                    RelativeLayout rl_download_layer_hold_1 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_hold_1);
                    LinearLayout ll_1 = (LinearLayout) wantUpdata.findViewById(R.id.ll_1);
                    rl_download_layer_1.setVisibility(View.VISIBLE);
                    cp_download_process_1.setVisibility(View.GONE);
                    iv_download_pause_1.setVisibility(View.VISIBLE);
                    rl_download_layer_hold_1.setVisibility(View.GONE);
                    ll_1.setClickable(false);
                } else {
                    RelativeLayout rl_download_layer_1 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_1);
                    CycleProcessBar cp_download_process_1 = (CycleProcessBar) wantUpdata.findViewById(R.id.cp_download_process_1);
                    ImageView iv_download_pause_1 = (ImageView) wantUpdata.findViewById(R.id.iv_download_pause_1);
                    RelativeLayout rl_download_layer_hold_1 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_hold_1);
                    LinearLayout ll_1 = (LinearLayout) wantUpdata.findViewById(R.id.ll_1);
                    rl_download_layer_1.setVisibility(View.VISIBLE);
                    cp_download_process_1.setVisibility(View.VISIBLE);
                    cp_download_process_1.setProgress(bean0.videoCurrentProcess);
                    iv_download_pause_1.setVisibility(View.GONE);
                    rl_download_layer_hold_1.setVisibility(View.GONE);
                    ll_1.setClickable(false);
                }
                break;
            case 1:
                FuncsDBDownloadDataBean bean1 = dataList.get(position * 5 + 1);
                if (isPause) {
                    RelativeLayout rl_download_layer_2 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_2);
                    CycleProcessBar cp_download_process_2 = (CycleProcessBar) wantUpdata.findViewById(R.id.cp_download_process_2);
                    ImageView iv_download_pause_2 = (ImageView) wantUpdata.findViewById(R.id.iv_download_pause_2);
                    RelativeLayout rl_download_layer_hold_2 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_hold_2);
                    LinearLayout ll_2 = (LinearLayout) wantUpdata.findViewById(R.id.ll_2);
                    rl_download_layer_2.setVisibility(View.VISIBLE);
                    cp_download_process_2.setVisibility(View.GONE);
                    iv_download_pause_2.setVisibility(View.VISIBLE);
                    rl_download_layer_hold_2.setVisibility(View.GONE);
                    ll_2.setClickable(false);
                } else {
                    RelativeLayout rl_download_layer_2 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_2);
                    CycleProcessBar cp_download_process_2 = (CycleProcessBar) wantUpdata.findViewById(R.id.cp_download_process_2);
                    ImageView iv_download_pause_2 = (ImageView) wantUpdata.findViewById(R.id.iv_download_pause_2);
                    RelativeLayout rl_download_layer_hold_2 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_hold_2);
                    LinearLayout ll_2 = (LinearLayout) wantUpdata.findViewById(R.id.ll_2);
                    rl_download_layer_2.setVisibility(View.VISIBLE);
                    cp_download_process_2.setVisibility(View.VISIBLE);
                    cp_download_process_2.setProgress(bean1.videoCurrentProcess);
                    iv_download_pause_2.setVisibility(View.GONE);
                    rl_download_layer_hold_2.setVisibility(View.GONE);
                    ll_2.setClickable(false);
                }
                break;
            case 2:
                FuncsDBDownloadDataBean bean2 = dataList.get(position * 5 + 2);
                if (isPause) {
                    RelativeLayout rl_download_layer_3 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_3);
                    CycleProcessBar cp_download_process_3 = (CycleProcessBar) wantUpdata.findViewById(R.id.cp_download_process_3);
                    ImageView iv_download_pause_3 = (ImageView) wantUpdata.findViewById(R.id.iv_download_pause_3);
                    RelativeLayout rl_download_layer_hold_3 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_hold_3);
                    LinearLayout ll_3 = (LinearLayout) wantUpdata.findViewById(R.id.ll_3);
                    rl_download_layer_3.setVisibility(View.VISIBLE);
                    cp_download_process_3.setVisibility(View.GONE);
                    iv_download_pause_3.setVisibility(View.VISIBLE);
                    rl_download_layer_hold_3.setVisibility(View.GONE);
                    ll_3.setClickable(false);
                } else {
                    RelativeLayout rl_download_layer_3 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_3);
                    CycleProcessBar cp_download_process_3 = (CycleProcessBar) wantUpdata.findViewById(R.id.cp_download_process_3);
                    ImageView iv_download_pause_3 = (ImageView) wantUpdata.findViewById(R.id.iv_download_pause_3);
                    RelativeLayout rl_download_layer_hold_3 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_hold_3);
                    LinearLayout ll_3 = (LinearLayout) wantUpdata.findViewById(R.id.ll_3);
                    rl_download_layer_3.setVisibility(View.VISIBLE);
                    cp_download_process_3.setVisibility(View.VISIBLE);
                    cp_download_process_3.setProgress(bean2.videoCurrentProcess);
                    iv_download_pause_3.setVisibility(View.GONE);
                    rl_download_layer_hold_3.setVisibility(View.GONE);
                    ll_3.setClickable(false);
                }
                break;
            case 3:
                FuncsDBDownloadDataBean bean3 = dataList.get(position * 5 + 3);
                if (isPause) {
                    RelativeLayout rl_download_layer_4 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_4);
                    CycleProcessBar cp_download_process_4 = (CycleProcessBar) wantUpdata.findViewById(R.id.cp_download_process_4);
                    ImageView iv_download_pause_4 = (ImageView) wantUpdata.findViewById(R.id.iv_download_pause_4);
                    RelativeLayout rl_download_layer_hold_4 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_hold_4);
                    LinearLayout ll_4 = (LinearLayout) wantUpdata.findViewById(R.id.ll_4);
                    rl_download_layer_4.setVisibility(View.VISIBLE);
                    cp_download_process_4.setVisibility(View.GONE);
                    iv_download_pause_4.setVisibility(View.VISIBLE);
                    rl_download_layer_hold_4.setVisibility(View.GONE);
                    ll_4.setClickable(false);
                } else {
                    RelativeLayout rl_download_layer_4 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_4);
                    CycleProcessBar cp_download_process_4 = (CycleProcessBar) wantUpdata.findViewById(R.id.cp_download_process_4);
                    ImageView iv_download_pause_4 = (ImageView) wantUpdata.findViewById(R.id.iv_download_pause_4);
                    RelativeLayout rl_download_layer_hold_4 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_hold_4);
                    LinearLayout ll_4 = (LinearLayout) wantUpdata.findViewById(R.id.ll_4);
                    rl_download_layer_4.setVisibility(View.VISIBLE);
                    cp_download_process_4.setVisibility(View.VISIBLE);
                    cp_download_process_4.setProgress(bean3.videoCurrentProcess);
                    iv_download_pause_4.setVisibility(View.GONE);
                    rl_download_layer_hold_4.setVisibility(View.GONE);
                    ll_4.setClickable(false);
                }
                break;
            case 4:
                FuncsDBDownloadDataBean bean4 = dataList.get(position * 5 + 4);
                if (isPause) {
                    RelativeLayout rl_download_layer_5 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_5);
                    CycleProcessBar cp_download_process_5 = (CycleProcessBar) wantUpdata.findViewById(R.id.cp_download_process_5);
                    ImageView iv_download_pause_5 = (ImageView) wantUpdata.findViewById(R.id.iv_download_pause_5);
                    RelativeLayout rl_download_layer_hold_5 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_hold_5);
                    LinearLayout ll_5 = (LinearLayout) wantUpdata.findViewById(R.id.ll_5);
                    rl_download_layer_5.setVisibility(View.VISIBLE);
                    cp_download_process_5.setVisibility(View.GONE);
                    iv_download_pause_5.setVisibility(View.VISIBLE);
                    rl_download_layer_hold_5.setVisibility(View.GONE);
                    ll_5.setClickable(false);
                } else {
                    RelativeLayout rl_download_layer_5 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_5);
                    CycleProcessBar cp_download_process_5 = (CycleProcessBar) wantUpdata.findViewById(R.id.cp_download_process_5);
                    ImageView iv_download_pause_5 = (ImageView) wantUpdata.findViewById(R.id.iv_download_pause_5);
                    RelativeLayout rl_download_layer_hold_5 = (RelativeLayout) wantUpdata.findViewById(R.id.rl_download_layer_hold_5);
                    LinearLayout ll_5 = (LinearLayout) wantUpdata.findViewById(R.id.ll_5);
                    rl_download_layer_5.setVisibility(View.VISIBLE);
                    cp_download_process_5.setVisibility(View.VISIBLE);
                    cp_download_process_5.setProgress(bean4.videoCurrentProcess);
                    iv_download_pause_5.setVisibility(View.GONE);
                    rl_download_layer_hold_5.setVisibility(View.GONE);
                    ll_5.setClickable(false);
                }
                break;
        }

    }


    public List<FuncsDBDownloadDataBean> getDataList() {
        return dataList;
    }


    @Override
    public int getCount() {
        int count = 0;
        if (dataList != null) {
            if (0 != (dataList.size() % 5)) {
                count = dataList.size() / 5 + 1;
            } else {
                count = dataList.size() / 5;
            }
        }
        return count;
    }


    @Override
    public Object getItem(int position) {
        return null;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, false);
    }


    public View getView(int position, View convertView, ViewGroup parent, boolean isShowDeleteOnly) {
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContext, R.layout.item_download_funcs, null);
            holder.rl_1 = (RelativeLayout) convertView.findViewById(R.id.rl_1);
            holder.rl_2 = (RelativeLayout) convertView.findViewById(R.id.rl_2);
            holder.rl_3 = (RelativeLayout) convertView.findViewById(R.id.rl_3);
            holder.rl_4 = (RelativeLayout) convertView.findViewById(R.id.rl_4);
            holder.rl_5 = (RelativeLayout) convertView.findViewById(R.id.rl_5);
            holder.iv_delete_1 = (ImageView) convertView.findViewById(R.id.iv_delete_1);
            holder.iv_delete_2 = (ImageView) convertView.findViewById(R.id.iv_delete_2);
            holder.iv_delete_3 = (ImageView) convertView.findViewById(R.id.iv_delete_3);
            holder.iv_delete_4 = (ImageView) convertView.findViewById(R.id.iv_delete_4);
            holder.iv_delete_5 = (ImageView) convertView.findViewById(R.id.iv_delete_5);
            holder.ll_1 = (LinearLayout) convertView.findViewById(R.id.ll_1);
            holder.ll_2 = (LinearLayout) convertView.findViewById(R.id.ll_2);
            holder.ll_3 = (LinearLayout) convertView.findViewById(R.id.ll_3);
            holder.ll_4 = (LinearLayout) convertView.findViewById(R.id.ll_4);
            holder.ll_5 = (LinearLayout) convertView.findViewById(R.id.ll_5);
            holder.iv_video_pic_1 = (ImageView) convertView.findViewById(R.id.iv_video_pic_1);
            holder.iv_video_pic_2 = (ImageView) convertView.findViewById(R.id.iv_video_pic_2);
            holder.iv_video_pic_3 = (ImageView) convertView.findViewById(R.id.iv_video_pic_3);
            holder.iv_video_pic_4 = (ImageView) convertView.findViewById(R.id.iv_video_pic_4);
            holder.iv_video_pic_5 = (ImageView) convertView.findViewById(R.id.iv_video_pic_5);
            holder.tv_video_name_1 = (TextView) convertView.findViewById(R.id.tv_video_name_1);
            holder.tv_video_name_2 = (TextView) convertView.findViewById(R.id.tv_video_name_2);
            holder.tv_video_name_3 = (TextView) convertView.findViewById(R.id.tv_video_name_3);
            holder.tv_video_name_4 = (TextView) convertView.findViewById(R.id.tv_video_name_4);
            holder.tv_video_name_5 = (TextView) convertView.findViewById(R.id.tv_video_name_5);
            holder.tv_video_episode_name_1 = (TextView) convertView.findViewById(R.id.tv_video_episode_name_1);
            holder.tv_video_episode_name_2 = (TextView) convertView.findViewById(R.id.tv_video_episode_name_2);
            holder.tv_video_episode_name_3 = (TextView) convertView.findViewById(R.id.tv_video_episode_name_3);
            holder.tv_video_episode_name_4 = (TextView) convertView.findViewById(R.id.tv_video_episode_name_4);
            holder.tv_video_episode_name_5 = (TextView) convertView.findViewById(R.id.tv_video_episode_name_5);
            holder.rl_download_layer_1 = (RelativeLayout) convertView.findViewById(R.id.rl_download_layer_1);
            holder.rl_download_layer_2 = (RelativeLayout) convertView.findViewById(R.id.rl_download_layer_2);
            holder.rl_download_layer_3 = (RelativeLayout) convertView.findViewById(R.id.rl_download_layer_3);
            holder.rl_download_layer_4 = (RelativeLayout) convertView.findViewById(R.id.rl_download_layer_4);
            holder.rl_download_layer_5 = (RelativeLayout) convertView.findViewById(R.id.rl_download_layer_5);
            holder.cp_download_process_1 = (CycleProcessBar) convertView.findViewById(R.id.cp_download_process_1);
            holder.cp_download_process_2 = (CycleProcessBar) convertView.findViewById(R.id.cp_download_process_2);
            holder.cp_download_process_3 = (CycleProcessBar) convertView.findViewById(R.id.cp_download_process_3);
            holder.cp_download_process_4 = (CycleProcessBar) convertView.findViewById(R.id.cp_download_process_4);
            holder.cp_download_process_5 = (CycleProcessBar) convertView.findViewById(R.id.cp_download_process_5);
            holder.iv_download_pause_1 = (ImageView) convertView.findViewById(R.id.iv_download_pause_1);
            holder.iv_download_pause_2 = (ImageView) convertView.findViewById(R.id.iv_download_pause_2);
            holder.iv_download_pause_3 = (ImageView) convertView.findViewById(R.id.iv_download_pause_3);
            holder.iv_download_pause_4 = (ImageView) convertView.findViewById(R.id.iv_download_pause_4);
            holder.iv_download_pause_5 = (ImageView) convertView.findViewById(R.id.iv_download_pause_5);
            holder.rl_download_layer_hold_1 = (RelativeLayout) convertView.findViewById(R.id.rl_download_layer_hold_1);
            holder.rl_download_layer_hold_2 = (RelativeLayout) convertView.findViewById(R.id.rl_download_layer_hold_2);
            holder.rl_download_layer_hold_3 = (RelativeLayout) convertView.findViewById(R.id.rl_download_layer_hold_3);
            holder.rl_download_layer_hold_4 = (RelativeLayout) convertView.findViewById(R.id.rl_download_layer_hold_4);
            holder.rl_download_layer_hold_5 = (RelativeLayout) convertView.findViewById(R.id.rl_download_layer_hold_5);
            holder.iv_download_hold_1 = (ImageView) convertView.findViewById(R.id.iv_download_hold_1);
            holder.iv_download_hold_2 = (ImageView) convertView.findViewById(R.id.iv_download_hold_2);
            holder.iv_download_hold_3 = (ImageView) convertView.findViewById(R.id.iv_download_hold_3);
            holder.iv_download_hold_4 = (ImageView) convertView.findViewById(R.id.iv_download_hold_4);
            holder.iv_download_hold_5 = (ImageView) convertView.findViewById(R.id.iv_download_hold_5);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.rl_1.setVisibility(View.GONE);
        holder.rl_2.setVisibility(View.GONE);
        holder.rl_3.setVisibility(View.GONE);
        holder.rl_4.setVisibility(View.GONE);
        holder.rl_5.setVisibility(View.GONE);

        if ((position * 5) < dataList.size()) {
            holder.rl_1.setVisibility(View.VISIBLE);
            FuncsDBDownloadDataBean bean = dataList.get(position * 5);
            if (!isShowDeleteOnly) {
                imageLoader.displayImage(bean.picture.big, holder.iv_video_pic_1, ImageLoaderUtils.getAdapterOptions());
                if (TextUtils.isEmpty(bean.episode)) {
                    holder.tv_video_episode_name_1.setVisibility(View.GONE);
                } else {
                    holder.tv_video_episode_name_1.setVisibility(View.VISIBLE);
                    holder.tv_video_episode_name_1.setText(mContext.getString(R.string.func_activity_episode_num, bean.episode));
                }
                holder.tv_video_name_1.setText(bean.title);
                holder.ll_1.setOnClickListener(clickListener);
                holder.ll_1.setTag(bean);
                holder.iv_delete_1.setOnClickListener(deleteClickListener);
                holder.iv_delete_1.setTag(bean);
                holder.iv_download_pause_1.setOnClickListener(wantDownloadClickListener);
                holder.iv_download_pause_1.setTag(bean);
                holder.cp_download_process_1.setOnClickListener(wantPauseClickListener);
                holder.cp_download_process_1.setTag(bean);
                holder.rl_download_layer_hold_1.setOnClickListener(waitClickClickListener);
                holder.rl_download_layer_hold_1.setTag(bean);
                switch (bean.download_state) {
                    case TaskStates.WAITING:
                        holder.rl_download_layer_1.setVisibility(View.GONE);
                        holder.rl_download_layer_hold_1.setVisibility(View.VISIBLE);
                        holder.ll_1.setClickable(false);
                        break;

                    case TaskStates.READY:
                    case TaskStates.DOWNLOADING:
                        holder.rl_download_layer_1.setVisibility(View.VISIBLE);
                        holder.cp_download_process_1.setVisibility(View.VISIBLE);
                        holder.cp_download_process_1.setProgress(bean.videoCurrentProcess);
                        holder.iv_download_pause_1.setVisibility(View.GONE);
                        holder.rl_download_layer_hold_1.setVisibility(View.GONE);
                        holder.ll_1.setClickable(false);
                        break;

                    case TaskStates.PAUSED:
                        holder.rl_download_layer_1.setVisibility(View.VISIBLE);
                        holder.cp_download_process_1.setVisibility(View.GONE);
                        holder.iv_download_pause_1.setVisibility(View.VISIBLE);
                        holder.rl_download_layer_hold_1.setVisibility(View.GONE);
                        holder.ll_1.setClickable(false);
                        break;

                    case TaskStates.END:
                        holder.rl_download_layer_1.setVisibility(View.GONE);
                        holder.rl_download_layer_hold_1.setVisibility(View.GONE);
                        holder.ll_1.setClickable(true);
                        break;
                }

            }
            holder.iv_delete_1.setVisibility(bean.isDeleteBtnShow ? View.VISIBLE : View.GONE);
        }

        if ((position * 5 + 1) < dataList.size()) {
            holder.rl_2.setVisibility(View.VISIBLE);
            FuncsDBDownloadDataBean bean = dataList.get(position * 5 + 1);
            if (!isShowDeleteOnly) {
                imageLoader.displayImage(bean.picture.big, holder.iv_video_pic_2, ImageLoaderUtils.getAdapterOptions());
                if (TextUtils.isEmpty(bean.episode)) {
                    holder.tv_video_episode_name_2.setVisibility(View.GONE);
                } else {
                    holder.tv_video_episode_name_2.setVisibility(View.VISIBLE);
                    holder.tv_video_episode_name_2.setText(mContext.getString(R.string.func_activity_episode_num, bean.episode));
                }
                holder.tv_video_name_2.setText(bean.title);
                holder.ll_2.setOnClickListener(clickListener);
                holder.ll_2.setTag(bean);
                holder.iv_delete_2.setOnClickListener(deleteClickListener);
                holder.iv_delete_2.setTag(bean);
                holder.iv_download_pause_2.setOnClickListener(wantDownloadClickListener);
                holder.iv_download_pause_2.setTag(bean);
                holder.cp_download_process_2.setOnClickListener(wantPauseClickListener);
                holder.cp_download_process_2.setTag(bean);
                holder.rl_download_layer_hold_2.setOnClickListener(waitClickClickListener);
                holder.rl_download_layer_hold_2.setTag(bean);
                switch (bean.download_state) {
                    case TaskStates.WAITING:
                        holder.rl_download_layer_2.setVisibility(View.GONE);
                        holder.rl_download_layer_hold_2.setVisibility(View.VISIBLE);
                        holder.ll_2.setClickable(false);
                        break;

                    case TaskStates.DOWNLOADING:
                        holder.rl_download_layer_2.setVisibility(View.VISIBLE);
                        holder.cp_download_process_2.setVisibility(View.VISIBLE);
                        holder.cp_download_process_2.setProgress(bean.videoCurrentProcess);
                        holder.iv_download_pause_2.setVisibility(View.GONE);
                        holder.rl_download_layer_hold_2.setVisibility(View.GONE);
                        holder.ll_2.setClickable(false);
                        break;

                    case TaskStates.PAUSED:
                        holder.rl_download_layer_2.setVisibility(View.VISIBLE);
                        holder.cp_download_process_2.setVisibility(View.GONE);
                        holder.iv_download_pause_2.setVisibility(View.VISIBLE);
                        holder.rl_download_layer_hold_2.setVisibility(View.GONE);
                        holder.ll_2.setClickable(false);
                        break;

                    case TaskStates.END:
                        holder.rl_download_layer_2.setVisibility(View.GONE);
                        holder.rl_download_layer_hold_2.setVisibility(View.GONE);
                        holder.ll_2.setClickable(true);
                        break;
                }
            }
            holder.iv_delete_2.setVisibility(bean.isDeleteBtnShow ? View.VISIBLE : View.GONE);

        }

        if ((position * 5 + 2) < dataList.size()) {
            holder.rl_3.setVisibility(View.VISIBLE);
            FuncsDBDownloadDataBean bean = dataList.get(position * 5 + 2);
            if (!isShowDeleteOnly) {
                imageLoader.displayImage(bean.picture.big, holder.iv_video_pic_3, ImageLoaderUtils.getAdapterOptions());
                if (TextUtils.isEmpty(bean.episode)) {
                    holder.tv_video_episode_name_3.setVisibility(View.GONE);
                } else {
                    holder.tv_video_episode_name_3.setVisibility(View.VISIBLE);
                    holder.tv_video_episode_name_3.setText(mContext.getString(R.string.func_activity_episode_num, bean.episode));
                }
                holder.tv_video_name_3.setText(bean.title);
                holder.ll_3.setOnClickListener(clickListener);
                holder.ll_3.setTag(bean);
                holder.iv_delete_3.setOnClickListener(deleteClickListener);
                holder.iv_delete_3.setTag(bean);
                holder.iv_download_pause_3.setOnClickListener(wantDownloadClickListener);
                holder.iv_download_pause_3.setTag(bean);
                holder.cp_download_process_3.setOnClickListener(wantPauseClickListener);
                holder.cp_download_process_3.setTag(bean);
                holder.rl_download_layer_hold_3.setOnClickListener(waitClickClickListener);
                holder.rl_download_layer_hold_3.setTag(bean);
                switch (bean.download_state) {
                    case TaskStates.WAITING:
                        holder.rl_download_layer_3.setVisibility(View.GONE);
                        holder.rl_download_layer_hold_3.setVisibility(View.VISIBLE);
                        holder.ll_3.setClickable(false);
                        break;

                    case TaskStates.DOWNLOADING:
                        holder.rl_download_layer_3.setVisibility(View.VISIBLE);
                        holder.cp_download_process_3.setVisibility(View.VISIBLE);
                        holder.cp_download_process_3.setProgress(bean.videoCurrentProcess);
                        holder.iv_download_pause_3.setVisibility(View.GONE);
                        holder.rl_download_layer_hold_3.setVisibility(View.GONE);
                        holder.ll_3.setClickable(false);
                        break;

                    case TaskStates.PAUSED:
                        holder.rl_download_layer_3.setVisibility(View.VISIBLE);
                        holder.cp_download_process_3.setVisibility(View.GONE);
                        holder.iv_download_pause_3.setVisibility(View.VISIBLE);
                        holder.rl_download_layer_hold_3.setVisibility(View.GONE);
                        holder.ll_3.setClickable(false);
                        break;

                    case TaskStates.END:
                        holder.rl_download_layer_3.setVisibility(View.GONE);
                        holder.rl_download_layer_hold_3.setVisibility(View.GONE);
                        holder.ll_3.setClickable(true);
                        break;
                }
            }
            holder.iv_delete_3.setVisibility(bean.isDeleteBtnShow ? View.VISIBLE : View.GONE);

        }

        if ((position * 5 + 3) < dataList.size()) {
            holder.rl_4.setVisibility(View.VISIBLE);
            FuncsDBDownloadDataBean bean = dataList.get(position * 5 + 3);
            if (!isShowDeleteOnly) {
                imageLoader.displayImage(bean.picture.big, holder.iv_video_pic_4, ImageLoaderUtils.getAdapterOptions());
                if (TextUtils.isEmpty(bean.episode)) {
                    holder.tv_video_episode_name_4.setVisibility(View.GONE);
                } else {
                    holder.tv_video_episode_name_4.setVisibility(View.VISIBLE);
                    holder.tv_video_episode_name_4.setText(mContext.getString(R.string.func_activity_episode_num, bean.episode));
                }
                holder.tv_video_name_4.setText(bean.title);
                holder.ll_4.setOnClickListener(clickListener);
                holder.ll_4.setTag(bean);
                holder.iv_delete_4.setOnClickListener(deleteClickListener);
                holder.iv_delete_4.setTag(bean);
                holder.iv_download_pause_4.setOnClickListener(wantDownloadClickListener);
                holder.iv_download_pause_4.setTag(bean);
                holder.cp_download_process_4.setOnClickListener(wantPauseClickListener);
                holder.cp_download_process_4.setTag(bean);
                holder.rl_download_layer_hold_4.setOnClickListener(waitClickClickListener);
                holder.rl_download_layer_hold_4.setTag(bean);
                switch (bean.download_state) {
                    case TaskStates.WAITING:
                        holder.rl_download_layer_4.setVisibility(View.GONE);
                        holder.rl_download_layer_hold_4.setVisibility(View.VISIBLE);
                        holder.ll_4.setClickable(false);
                        break;

                    case TaskStates.DOWNLOADING:
                        holder.rl_download_layer_4.setVisibility(View.VISIBLE);
                        holder.cp_download_process_4.setVisibility(View.VISIBLE);
                        holder.cp_download_process_4.setProgress(bean.videoCurrentProcess);
                        holder.iv_download_pause_4.setVisibility(View.GONE);
                        holder.rl_download_layer_hold_4.setVisibility(View.GONE);
                        holder.ll_4.setClickable(false);
                        break;

                    case TaskStates.PAUSED:
                        holder.rl_download_layer_4.setVisibility(View.VISIBLE);
                        holder.cp_download_process_4.setVisibility(View.GONE);
                        holder.iv_download_pause_4.setVisibility(View.VISIBLE);
                        holder.rl_download_layer_hold_4.setVisibility(View.GONE);
                        holder.ll_4.setClickable(false);
                        break;

                    case TaskStates.END:
                        holder.rl_download_layer_4.setVisibility(View.GONE);
                        holder.rl_download_layer_hold_4.setVisibility(View.GONE);
                        holder.ll_4.setClickable(true);
                        break;
                }
            }
            holder.iv_delete_4.setVisibility(bean.isDeleteBtnShow ? View.VISIBLE : View.GONE);

        }

        if ((position * 5 + 4) < dataList.size()) {
            holder.rl_5.setVisibility(View.VISIBLE);
            FuncsDBDownloadDataBean bean = dataList.get(position * 5 + 4);
            if (!isShowDeleteOnly) {
                imageLoader.displayImage(bean.picture.big, holder.iv_video_pic_5, ImageLoaderUtils.getAdapterOptions());
                if (TextUtils.isEmpty(bean.episode)) {
                    holder.tv_video_episode_name_5.setVisibility(View.GONE);
                } else {
                    holder.tv_video_episode_name_5.setVisibility(View.VISIBLE);
                    holder.tv_video_episode_name_5.setText(mContext.getString(R.string.func_activity_episode_num, bean.episode));
                }
                holder.tv_video_name_5.setText(bean.title);
                holder.ll_5.setOnClickListener(clickListener);
                holder.ll_5.setTag(bean);
                holder.iv_delete_5.setOnClickListener(deleteClickListener);
                holder.iv_delete_5.setTag(bean);
                holder.iv_download_pause_5.setOnClickListener(wantDownloadClickListener);
                holder.iv_download_pause_5.setTag(bean);
                holder.cp_download_process_5.setOnClickListener(wantPauseClickListener);
                holder.cp_download_process_5.setTag(bean);
                holder.rl_download_layer_hold_5.setOnClickListener(waitClickClickListener);
                holder.rl_download_layer_hold_5.setTag(bean);
                switch (bean.download_state) {
                    case TaskStates.WAITING:
                        holder.rl_download_layer_5.setVisibility(View.GONE);
                        holder.rl_download_layer_hold_5.setVisibility(View.VISIBLE);
                        holder.ll_5.setClickable(false);
                        break;

                    case TaskStates.DOWNLOADING:
                        holder.rl_download_layer_5.setVisibility(View.VISIBLE);
                        holder.cp_download_process_5.setVisibility(View.VISIBLE);
                        holder.cp_download_process_5.setProgress(bean.videoCurrentProcess);
                        holder.iv_download_pause_5.setVisibility(View.GONE);
                        holder.rl_download_layer_hold_5.setVisibility(View.GONE);
                        holder.ll_5.setClickable(false);
                        break;

                    case TaskStates.PAUSED:
                        holder.rl_download_layer_5.setVisibility(View.VISIBLE);
                        holder.cp_download_process_5.setVisibility(View.GONE);
                        holder.iv_download_pause_5.setVisibility(View.VISIBLE);
                        holder.rl_download_layer_hold_5.setVisibility(View.GONE);
                        holder.ll_5.setClickable(false);
                        break;

                    case TaskStates.END:
                        holder.rl_download_layer_5.setVisibility(View.GONE);
                        holder.rl_download_layer_hold_5.setVisibility(View.GONE);
                        holder.ll_5.setClickable(true);
                        break;
                }
            }
            holder.iv_delete_5.setVisibility(bean.isDeleteBtnShow ? View.VISIBLE : View.GONE);

        }

        return convertView;

    }


    class Holder {
        public RelativeLayout rl_1;
        public RelativeLayout rl_2;
        public RelativeLayout rl_3;
        public RelativeLayout rl_4;
        public RelativeLayout rl_5;
        public ImageView iv_delete_1;
        public ImageView iv_delete_2;
        public ImageView iv_delete_3;
        public ImageView iv_delete_4;
        public ImageView iv_delete_5;
        public LinearLayout ll_1;
        public LinearLayout ll_2;
        public LinearLayout ll_3;
        public LinearLayout ll_4;
        public LinearLayout ll_5;
        public ImageView iv_video_pic_1;
        public TextView tv_video_name_1;
        public ImageView iv_video_pic_2;
        public TextView tv_video_name_2;
        public ImageView iv_video_pic_3;
        public TextView tv_video_name_3;
        public ImageView iv_video_pic_4;
        public TextView tv_video_name_4;
        public ImageView iv_video_pic_5;
        public TextView tv_video_name_5;
        public TextView tv_video_episode_name_1;
        public TextView tv_video_episode_name_2;
        public TextView tv_video_episode_name_3;
        public TextView tv_video_episode_name_4;
        public TextView tv_video_episode_name_5;
        public RelativeLayout rl_download_layer_1;
        public RelativeLayout rl_download_layer_2;
        public RelativeLayout rl_download_layer_3;
        public RelativeLayout rl_download_layer_4;
        public RelativeLayout rl_download_layer_5;
        public CycleProcessBar cp_download_process_1;
        public CycleProcessBar cp_download_process_2;
        public CycleProcessBar cp_download_process_3;
        public CycleProcessBar cp_download_process_4;
        public CycleProcessBar cp_download_process_5;
        public ImageView iv_download_pause_1;
        public ImageView iv_download_pause_2;
        public ImageView iv_download_pause_3;
        public ImageView iv_download_pause_4;
        public ImageView iv_download_pause_5;
        public RelativeLayout rl_download_layer_hold_1;
        public RelativeLayout rl_download_layer_hold_2;
        public RelativeLayout rl_download_layer_hold_3;
        public RelativeLayout rl_download_layer_hold_4;
        public RelativeLayout rl_download_layer_hold_5;
        public ImageView iv_download_hold_1;
        public ImageView iv_download_hold_2;
        public ImageView iv_download_hold_3;
        public ImageView iv_download_hold_4;
        public ImageView iv_download_hold_5;

    }


    private View.OnClickListener deleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            FuncsDBDownloadDataBean mFuncsDBDownloadDataBean = (FuncsDBDownloadDataBean) v.getTag();
            if (mAdapterModifyChangedListener != null) {
                mAdapterModifyChangedListener.itemDeleted(mFuncsDBDownloadDataBean);
            }
        }
    };


    private View.OnClickListener wantPauseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            FuncsDBDownloadDataBean mFuncsDBDownloadDataBean = (FuncsDBDownloadDataBean) v.getTag();
            if (mAdapterModifyChangedListener != null) {
                mAdapterModifyChangedListener.wantToPause(mFuncsDBDownloadDataBean);
            }
        }
    };


    private View.OnClickListener wantDownloadClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            FuncsDBDownloadDataBean mFuncsDBDownloadDataBean = (FuncsDBDownloadDataBean) v.getTag();
            if (mAdapterModifyChangedListener != null) {
                mAdapterModifyChangedListener.wantToDownload(mFuncsDBDownloadDataBean);
            }
        }
    };


    private View.OnClickListener waitClickClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            FuncsDBDownloadDataBean mFuncsDBDownloadDataBean = (FuncsDBDownloadDataBean) v.getTag();
            if (mAdapterModifyChangedListener != null) {
                mAdapterModifyChangedListener.waitClick(mFuncsDBDownloadDataBean);
            }
        }
    };


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            App.closeSnappyDb();
            FuncsDBDownloadDataBean mFuncsDBDownloadDataBean = (FuncsDBDownloadDataBean) v.getTag();
            String id = mFuncsDBDownloadDataBean.id;
            String channel = mFuncsDBDownloadDataBean.channel;
            DBDownloadBean downloadBean = App.getMyDownloadManager().findDBDownloadBean(id, channel);
            if (!checkDownloadVideoIntegrity(downloadBean)) {
                if (mAdapterModifyChangedListener != null) {
                    mAdapterModifyChangedListener.itemDeleted(mFuncsDBDownloadDataBean);
                }
                App.showToast(R.string.local_media_play_video_file_not_available);
                return;
            }
            HistoryDaoImpl mHistoryDaoImpl = new HistoryDaoImpl(mContext);
            DBHistoryBean historyBean = mHistoryDaoImpl.findHistory(id, channel);
            boolean isHistory = mHistoryDaoImpl.isCheckExist(id, channel);
            Intent intent = new Intent(mContext, PlayVideoActivity.class);
            if (TextUtils.isEmpty(mFuncsDBDownloadDataBean.episode)) {//是电影
                intent.putExtra("cached", true);
                intent.putExtra("url", downloadBean.videoHtmlUrl);
                intent.putExtra("channel", channel);
                intent.putExtra("isHistory", isHistory);
                if (isHistory && historyBean != null) { // 有播放记录
                    intent.putExtra("lastPosition", historyBean.getTime());
                }
                ((Activity) mContext).startActivityForResult(intent, 100);
                L.d(TAG, "startActivityForResult --> PlayVideoActivity --> url : " + downloadBean.videoHtmlUrl + " , channel : " + channel + " , isHistory : " + isHistory);
            } else {// 连续剧
                String pos = mFuncsDBDownloadDataBean.id.split(Const.MYWIFEANDIBIRTH)[1];
                intent.putExtra("cached", true);
                intent.putExtra("url", downloadBean.videoHtmlUrl);
                intent.putExtra("channel", channel);
                intent.putExtra("pos", pos);
                intent.putExtra("isHistory", isHistory);
                if (isHistory && historyBean != null) { // 有播放记录
                    intent.putExtra("lastPosition", historyBean.getTime());
                }
                ((FragmentActivity) mContext).startActivityForResult(intent, 200);
                L.d(TAG, "startActivityForResult --> PlayVideoActivity --> url : " + downloadBean.videoHtmlUrl + " , channel : " + channel + " , list : " + " , pos : " + pos);
            }

        }
    };

    /**
     * 检测该视频文件的完整性
     */
    private boolean checkDownloadVideoIntegrity(DBDownloadBean downloadBean) {
        for (String local : downloadBean.videoLocalUrl.split(",")) {
            File localFile = new File(local);
            if (!localFile.exists() || !localFile.isFile() || localFile.length() <= 0 || !localFile.canRead()) {
                return false;
            }
        }
        return true;
    }

    private AdapterModifyChangedListener mAdapterModifyChangedListener;

    public interface AdapterModifyChangedListener {
        void itemDeleted(FuncsDBDownloadDataBean mFuncsDBDownloadDataBean);

        void wantToPause(FuncsDBDownloadDataBean mFuncsDBDownloadDataBean);

        void wantToDownload(FuncsDBDownloadDataBean mFuncsDBDownloadDataBean);

        void waitClick(FuncsDBDownloadDataBean mFuncsDBDownloadDataBean);
    }

    public void setOnAdapterModifyChangedListener(AdapterModifyChangedListener mAdapterModifyChangedListener) {
        if (mAdapterModifyChangedListener != null) {
            this.mAdapterModifyChangedListener = mAdapterModifyChangedListener;
        }
    }

}