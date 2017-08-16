package com.songshu.squirrelvideo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.entity.PlayTeleplayBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yb on 15-7-10.
 */
public class EpisodeGridViewAdapter extends BaseAdapter {

    private static final String TAG = EpisodeGridViewAdapter.class.getSimpleName() + ":";


    private Context mContext;
    private List<PlayTeleplayBean> dataList = new ArrayList<PlayTeleplayBean>();
    private int selectItem = -1;

    public EpisodeGridViewAdapter(Context mContext, List<PlayTeleplayBean> dataList) {
        this.mContext = mContext;
        this.dataList.addAll(dataList);
    }

    public void updataAdapterData(List<PlayTeleplayBean> list) {
        dataList = list;
        notifyDataSetChanged();
    }

    public List<PlayTeleplayBean> getCurrentDataList() {
        return dataList;
    }

    /**
     * 设置选中哪一项
     *
     * @param selectItem
     */
    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
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
            convertView = View.inflate(mContext, R.layout.item_episode_grid_view, null);
            holder.rl_episode_num_bg = (RelativeLayout) convertView.findViewById(R.id.rl_episode_num_bg);
            holder.tv_episode_num = (TextView) convertView.findViewById(R.id.tv_episode_num);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PlayTeleplayBean bean = dataList.get(position);
        holder.tv_episode_num.setText(bean.videoNumber + "");
//        if (TextUtils.isEmpty(bean.videoUrl)) { // 视频播放地址不为空
//            if (position == selectItem) {
//                holder.rl_episode_num_bg.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.));
//            } else {
//                holder.rl_episode_num_bg.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.));
//            }
//        } else { // 视频播放地址为空
//            holder.rl_episode_num_bg.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.));
//        }
        return convertView;
    }

    private class ViewHolder {
        RelativeLayout rl_episode_num_bg; // 背景
        TextView tv_episode_num; // 剧集序号
    }
}
