package com.songshu.squirrelvideo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songshu.squirrelvideo.R;

import java.util.List;

/**
 * Created by yb on 15-7-21.
 */
public class SearchHistoryAdapter extends MyBaseAdapter {

    private static final String TAG = SearchHistoryAdapter.class.getSimpleName() + ":";

    private Context mContext;
    private List<String> dataList;

    public SearchHistoryAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void notifyData(List<String> dataList) {
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
            convertView = View.inflate(mContext, R.layout.item_search_history, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        String name = dataList.get(position);
        holder.tv_name.setText(name);
        holder.tv_name.setTag(position);
        holder.tv_name.setOnClickListener(itemAddClickListener);
        holder.iv_delete.setTag(name);
        holder.iv_delete.setOnClickListener(itemDeleteClickListener);

        return convertView;
    }

    class Holder {
        public TextView tv_name;
        public ImageView iv_delete;
    }

    private View.OnClickListener itemDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = (String) v.getTag();
            if (listener != null) {
                listener.dataDelete(name);
            }
        }
    };


    private View.OnClickListener itemAddClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            if (listener != null) {
                listener.dataAdd(position);
            }
        }
    };


    private DataChangeListener listener;

    public void setOnDataChangeListener(DataChangeListener listener) {
        this.listener = listener;
    }

    public interface DataChangeListener {
        void dataDelete(String name);
        void dataAdd(int position);
    }

}
