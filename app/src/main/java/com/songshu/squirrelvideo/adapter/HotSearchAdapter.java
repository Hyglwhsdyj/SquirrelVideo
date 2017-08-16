package com.songshu.squirrelvideo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.entity.HotSearchBean;

import java.util.List;

/**
 * Created by yb on 15-7-21.
 */
public class HotSearchAdapter extends MyBaseAdapter {

    private static final String TAG = HotSearchAdapter.class.getSimpleName() + ":";

    private Context mContext;
    private List<HotSearchBean> dataList;

    public HotSearchAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void notifyData(List<HotSearchBean> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int count = 0;
        if (dataList != null) {
            count = dataList.size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        if (dataList != null) {
            return dataList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContext, R.layout.item_hot_search, null);
            holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.iv_status = (ImageView) convertView.findViewById(R.id.iv_status);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        HotSearchBean hotSearchBean = dataList.get(position);
        holder.tv_num.setText(position + 1 + "");
        holder.tv_name.setText(hotSearchBean.content);
        holder.iv_status.setImageResource(hotSearchBean.click > hotSearchBean.heat ? R.drawable.movie_up : R.drawable.movie_down);

        return convertView;
    }

    class Holder {
        public TextView tv_num;
        public TextView tv_name;
        public ImageView iv_status;
    }
}
