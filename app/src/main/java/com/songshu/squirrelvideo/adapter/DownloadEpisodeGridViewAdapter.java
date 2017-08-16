package com.songshu.squirrelvideo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.golshadi.majid.core.enums.TaskStates;
import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.entity.PlayTeleplayBean;
import com.songshu.squirrelvideo.entity.TeleplaySourceBean;
import com.songshu.squirrelvideo.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yb on 15-7-10.
 */
public class DownloadEpisodeGridViewAdapter extends BaseAdapter {

    private static final String TAG = DownloadEpisodeGridViewAdapter.class.getSimpleName() + ":";


    private Context mContext;
    private List<PlayTeleplayBean> dataList = new ArrayList<PlayTeleplayBean>();
    private TeleplaySourceBean bean;

    public DownloadEpisodeGridViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void updataAdapterData(TeleplaySourceBean bean) {
        this.bean = bean;
        dataList = bean.mPlayTeleplayBeanList;
        notifyDataSetChanged();
    }

    public List<PlayTeleplayBean> getCurrentDataList() {
        return dataList;
    }


    @Override
    public int getCount() {
        int count = 0;
        if (dataList != null && dataList.size() > 0) {
            count = dataList.size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_download_episode_grid_view, null);
            holder.tv_episode_num = (TextView) convertView.findViewById(R.id.tv_episode_num);
            holder.iv_episode_downloading = (ImageView) convertView.findViewById(R.id.iv_episode_downloading);
            holder.iv_episode_already_download = (ImageView) convertView.findViewById(R.id.iv_episode_already_download);
            convertView.setLayoutParams(new AbsListView.LayoutParams(DensityUtil.dip2px(mContext, 80),DensityUtil.dip2px(mContext, 80)));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PlayTeleplayBean bean = dataList.get(position);
        holder.tv_episode_num.setText(bean.videoNumber + "");
        switch (bean.videoDownloadState) {
            case TaskStates.NOINCLUDE:
                holder.iv_episode_downloading.setVisibility(View.GONE);
                holder.iv_episode_already_download.setVisibility(View.GONE);
                break;
            case TaskStates.END:
                holder.iv_episode_downloading.setVisibility(View.GONE);
                holder.iv_episode_already_download.setVisibility(View.VISIBLE);
                break;
            default:
                holder.iv_episode_downloading.setVisibility(View.VISIBLE);
                holder.iv_episode_already_download.setVisibility(View.GONE);
                break;
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tv_episode_num; // 剧集序号
        ImageView iv_episode_downloading;
        ImageView iv_episode_already_download;
    }
}
