package com.songshu.squirrelvideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.activity.MovieDetailActivity;
import com.songshu.squirrelvideo.activity.TeleplayDetailActivity;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.VideoBean;
import com.songshu.squirrelvideo.manager.TitleManager;
import com.songshu.squirrelvideo.utils.ImageLoaderUtils;
import com.songshu.squirrelvideo.utils.L;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yb on 15-7-6.
 */
public class HealthAndOperaFragmentAdapter extends MyBaseAdapter {

    private static final String TAG = HealthAndOperaFragmentAdapter.class.getSimpleName() + ":";
    private Context mContext;
    private List<VideoBean> dataList = new LinkedList<VideoBean>();

    public HealthAndOperaFragmentAdapter(Context mContext, List<VideoBean> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    public void notifyData(List<VideoBean> tempList) {
        L.d(TAG, "notifyData --> datalist size : " + tempList.size());
        dataList = tempList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int count = 0;
        if (dataList != null) {
            if (0 != (dataList.size() % 3)) {
                count = dataList.size() / 3 + 1;
            } else {
                count = dataList.size() / 3;
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
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContext, R.layout.item_health_opera_fragment, null);
            holder.ll_1 = (LinearLayout) convertView.findViewById(R.id.ll_1);
            holder.ll_2 = (LinearLayout) convertView.findViewById(R.id.ll_2);
            holder.ll_3 = (LinearLayout) convertView.findViewById(R.id.ll_3);
            holder.iv_video_pic_1 = (ImageView) convertView.findViewById(R.id.iv_video_pic_1);
            holder.tv_video_name_1 = (TextView) convertView.findViewById(R.id.tv_video_name_1);
            holder.iv_video_pic_2 = (ImageView) convertView.findViewById(R.id.iv_video_pic_2);
            holder.tv_video_name_2 = (TextView) convertView.findViewById(R.id.tv_video_name_2);
            holder.iv_video_pic_3 = (ImageView) convertView.findViewById(R.id.iv_video_pic_3);
            holder.tv_video_name_3 = (TextView) convertView.findViewById(R.id.tv_video_name_3);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.ll_1.setVisibility(View.GONE);
        holder.ll_2.setVisibility(View.GONE);
        holder.ll_3.setVisibility(View.GONE);

        if ((position * 3) < dataList.size()) {
            holder.ll_1.setVisibility(View.VISIBLE);
            VideoBean bean = dataList.get(position * 3);
            imageLoader.displayImage(bean.picture.big, holder.iv_video_pic_1, ImageLoaderUtils.getAdapterOptions());
            holder.tv_video_name_1.setText(bean.title);
            holder.ll_1.setOnClickListener(clickListener);
            holder.ll_1.setTag(bean);
        }

        if ((position * 3 + 1) < dataList.size()) {
            holder.ll_2.setVisibility(View.VISIBLE);
            VideoBean bean = dataList.get(position * 3 + 1);
            imageLoader.displayImage(bean.picture.big, holder.iv_video_pic_2, ImageLoaderUtils.getAdapterOptions());
            holder.tv_video_name_2.setText(bean.title);
            holder.ll_2.setOnClickListener(clickListener);
            holder.ll_2.setTag(bean);
        }

        if ((position * 3 + 2) < dataList.size()) {
            holder.ll_3.setVisibility(View.VISIBLE);
            VideoBean bean = dataList.get(position * 3 + 2);
            imageLoader.displayImage(bean.picture.big, holder.iv_video_pic_3, ImageLoaderUtils.getAdapterOptions());
            holder.tv_video_name_3.setText(bean.title);
            holder.ll_3.setOnClickListener(clickListener);
            holder.ll_3.setTag(bean);
        }

        return convertView;
    }

    class Holder {
        public LinearLayout ll_1;
        public LinearLayout ll_2;
        public LinearLayout ll_3;
        public ImageView iv_video_pic_1;
        public TextView tv_video_name_1;
        public ImageView iv_video_pic_2;
        public TextView tv_video_name_2;
        public ImageView iv_video_pic_3;
        public TextView tv_video_name_3;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VideoBean bean = (VideoBean) v.getTag();
            Intent mIntent = null;
            if (Const.TITLE.equals(TitleManager.TITLE_MOVIE)) {
                mIntent = new Intent(mContext, MovieDetailActivity.class);
                mIntent.putExtra("video_bean", bean);
                L.d(TAG, "start --> MovieDetailActivity --> VideoBean : " + bean);
            } else if (Const.TITLE.equals(TitleManager.TITLE_TELEPLAY)) {
                mIntent = new Intent(mContext, TeleplayDetailActivity.class);
                mIntent.putExtra("video_bean", bean);
                L.d(TAG, "start --> TeleplayDetailActivity --> VideoBean : " + bean);
            } else if (Const.TITLE.equals(TitleManager.TITLE_CHILD)) {
                mIntent = new Intent(mContext, TeleplayDetailActivity.class);
                mIntent.putExtra("video_bean", bean);
                L.d(TAG, "start --> TeleplayDetailActivity --> VideoBean : " + bean);
            } else if (Const.TITLE.equals(TitleManager.TITLE_OPERA)) {
                mIntent = new Intent(mContext, MovieDetailActivity.class);
                mIntent.putExtra("video_bean", bean);
                L.d(TAG, "start --> MovieDetailActivity --> VideoBean : " + bean);
            } else if (Const.TITLE.equals(TitleManager.TITLE_HEALTH)) {
                mIntent = new Intent(mContext, MovieDetailActivity.class);
                mIntent.putExtra("video_bean", bean);
                L.d(TAG, "start --> MovieDetailActivity --> VideoBean : " + bean);
            }
            if (mIntent != null) {
                mContext.startActivity(mIntent);
            }
        }
    };
}
