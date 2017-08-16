package com.songshu.squirrelvideo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.entity.SearchResultSingleVideoBean;

import java.util.List;

/**
 * Created by yb on 15-7-21.
 */
public class QuicklySearchAdapter extends MyBaseAdapter {

    private static final String TAG = QuicklySearchAdapter.class.getSimpleName() + ":";

    private Context mContext;
    private List<SearchResultSingleVideoBean> dataList;

    public QuicklySearchAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void notifyData(List<SearchResultSingleVideoBean> dataList) {
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
            convertView = View.inflate(mContext, R.layout.item_quickly_searcch, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        SearchResultSingleVideoBean mSearchResultSingleVideoBean = dataList.get(position);
        holder.tv_name.setText(mSearchResultSingleVideoBean.title);

        return convertView;
    }

    class Holder {
        public TextView tv_name;
    }
}
