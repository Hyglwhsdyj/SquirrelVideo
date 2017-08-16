package com.songshu.squirrelvideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.po.PoJoLocalMedia;
import com.songshu.squirrelvideo.player.PlayVideoActivity;
import com.songshu.squirrelvideo.utils.ImageLoaderUtils;
import com.songshu.squirrelvideo.utils.L;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yb on 15-11-1.
 */
public class LocalMediaAdapter extends MyBaseAdapter {

    private static final String TAG = LocalMediaAdapter.class.getSimpleName() + ":";
    private boolean isDeleteIconShow = false;
    private Context mContext;

    private List<PoJoLocalMedia> dataList;

    public LocalMediaAdapter() {
    }

    public LocalMediaAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void add(PoJoLocalMedia singleMedia) {
        if (dataList == null) {
            dataList = new ArrayList<PoJoLocalMedia>();
        }
        if (!dataList.contains(singleMedia)) {
            dataList.add(singleMedia);
            notifyDataSetChanged();
        }
    }

    public void notifyData(List<PoJoLocalMedia> datalist) {
        L.d(TAG, "notifyData --> datalist size : " + datalist.size());
        this.dataList = datalist;
        notifyDataSetChanged();
    }

    public void showDeleteIcon(ListView listview, boolean isShow) {
        if (dataList == null || dataList.size() == 0) return;
        isDeleteIconShow = isShow;
        for (PoJoLocalMedia bean : dataList) {
            bean.isDeleteShow = isShow;
        }

        int firstVisiblePosition = listview.getFirstVisiblePosition();
        for (int position = 0; position < getCount(); position++) {
            View wantUpdata = listview.getChildAt(position - firstVisiblePosition);
            getView(position, wantUpdata, null, true);
        }
    }

    public List<PoJoLocalMedia> getDataList() {
        return dataList;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (dataList != null) {
            if (0 != (dataList.size() % 4)) {
                count = dataList.size() / 4 + 1;
            } else {
                count = dataList.size() / 4;
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
            convertView = View.inflate(mContext, R.layout.item_local_media_adapter, null);
            holder.rl_1 = (RelativeLayout) convertView.findViewById(R.id.rl_1);
            holder.rl_2 = (RelativeLayout) convertView.findViewById(R.id.rl_2);
            holder.rl_3 = (RelativeLayout) convertView.findViewById(R.id.rl_3);
            holder.rl_4 = (RelativeLayout) convertView.findViewById(R.id.rl_4);
            holder.iv_delete_1 = (ImageView) convertView.findViewById(R.id.iv_delete_1);
            holder.iv_delete_2 = (ImageView) convertView.findViewById(R.id.iv_delete_2);
            holder.iv_delete_3 = (ImageView) convertView.findViewById(R.id.iv_delete_3);
            holder.iv_delete_4 = (ImageView) convertView.findViewById(R.id.iv_delete_4);
            holder.ll_1 = (LinearLayout) convertView.findViewById(R.id.ll_1);
            holder.ll_2 = (LinearLayout) convertView.findViewById(R.id.ll_2);
            holder.ll_3 = (LinearLayout) convertView.findViewById(R.id.ll_3);
            holder.ll_4 = (LinearLayout) convertView.findViewById(R.id.ll_4);
            holder.iv_video_pic_1 = (ImageView) convertView.findViewById(R.id.iv_video_pic_1);
            holder.tv_video_name_1 = (TextView) convertView.findViewById(R.id.tv_video_name_1);
            holder.iv_video_pic_2 = (ImageView) convertView.findViewById(R.id.iv_video_pic_2);
            holder.tv_video_name_2 = (TextView) convertView.findViewById(R.id.tv_video_name_2);
            holder.iv_video_pic_3 = (ImageView) convertView.findViewById(R.id.iv_video_pic_3);
            holder.tv_video_name_3 = (TextView) convertView.findViewById(R.id.tv_video_name_3);
            holder.iv_video_pic_4 = (ImageView) convertView.findViewById(R.id.iv_video_pic_4);
            holder.tv_video_name_4 = (TextView) convertView.findViewById(R.id.tv_video_name_4);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.rl_1.setVisibility(View.GONE);
        holder.rl_2.setVisibility(View.GONE);
        holder.rl_3.setVisibility(View.GONE);
        holder.rl_4.setVisibility(View.GONE);

        if ((position * 4) < dataList.size()) {
            holder.rl_1.setVisibility(View.VISIBLE);
            PoJoLocalMedia bean = dataList.get(position * 4);
            if (!isShowDeleteOnly) {
                imageLoader.displayImage(
                        Const.IMAGELOADER_LOCAL_MEDIA_PROFIX + bean.thumb_path,
                        holder.iv_video_pic_1,
                        ImageLoaderUtils.getLocalMediaOptions());
                holder.tv_video_name_1.setText(bean.title);
                holder.rl_1.setOnClickListener(clickListener);
                holder.rl_1.setTag(bean);
                holder.iv_delete_1.setOnClickListener(deleteClickListener);
                holder.iv_delete_1.setTag(bean);
            }
            holder.iv_delete_1.setVisibility(bean.isDeleteShow ? View.VISIBLE : View.GONE);
        }

        if ((position * 4 + 1) < dataList.size()) {
            holder.rl_2.setVisibility(View.VISIBLE);
            PoJoLocalMedia bean = dataList.get(position * 4 + 1);
            if (!isShowDeleteOnly) {
                imageLoader.displayImage(
                        Const.IMAGELOADER_LOCAL_MEDIA_PROFIX + bean.thumb_path,
                        holder.iv_video_pic_2,
                        ImageLoaderUtils.getLocalMediaOptions());
                holder.tv_video_name_2.setText(bean.title);
                holder.rl_2.setOnClickListener(clickListener);
                holder.rl_2.setTag(bean);
                holder.iv_delete_2.setOnClickListener(deleteClickListener);
                holder.iv_delete_2.setTag(bean);
            }
            holder.iv_delete_2.setVisibility(bean.isDeleteShow ? View.VISIBLE : View.GONE);

        }

        if ((position * 4 + 2) < dataList.size()) {
            holder.rl_3.setVisibility(View.VISIBLE);
            PoJoLocalMedia bean = dataList.get(position * 4 + 2);
            if (!isShowDeleteOnly) {
                imageLoader.displayImage(
                        Const.IMAGELOADER_LOCAL_MEDIA_PROFIX + bean.thumb_path,
                        holder.iv_video_pic_3,
                        ImageLoaderUtils.getLocalMediaOptions());
                holder.tv_video_name_3.setText(bean.title);
                holder.rl_3.setOnClickListener(clickListener);
                holder.rl_3.setTag(bean);
                holder.iv_delete_3.setOnClickListener(deleteClickListener);
                holder.iv_delete_3.setTag(bean);
            }
            holder.iv_delete_3.setVisibility(bean.isDeleteShow ? View.VISIBLE : View.GONE);

        }

        if ((position * 4 + 3) < dataList.size()) {
            holder.rl_4.setVisibility(View.VISIBLE);
            PoJoLocalMedia bean = dataList.get(position * 4 + 3);
            if (!isShowDeleteOnly) {
                imageLoader.displayImage(
                        Const.IMAGELOADER_LOCAL_MEDIA_PROFIX + bean.thumb_path,
                        holder.iv_video_pic_4,
                        ImageLoaderUtils.getLocalMediaOptions());
                holder.tv_video_name_4.setText(bean.title);
                holder.rl_4.setOnClickListener(clickListener);
                holder.rl_4.setTag(bean);
                holder.iv_delete_4.setOnClickListener(deleteClickListener);
                holder.iv_delete_4.setTag(bean);
            }
            holder.iv_delete_4.setVisibility(bean.isDeleteShow ? View.VISIBLE : View.GONE);

        }

        return convertView;

    }

    class Holder {
        public RelativeLayout rl_1;
        public RelativeLayout rl_2;
        public RelativeLayout rl_3;
        public RelativeLayout rl_4;
        public ImageView iv_delete_1;
        public ImageView iv_delete_2;
        public ImageView iv_delete_3;
        public ImageView iv_delete_4;
        public LinearLayout ll_1;
        public LinearLayout ll_2;
        public LinearLayout ll_3;
        public LinearLayout ll_4;
        public ImageView iv_video_pic_1;
        public TextView tv_video_name_1;
        public ImageView iv_video_pic_2;
        public TextView tv_video_name_2;
        public ImageView iv_video_pic_3;
        public TextView tv_video_name_3;
        public ImageView iv_video_pic_4;
        public TextView tv_video_name_4;
    }

    private View.OnClickListener deleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PoJoLocalMedia mPoJoLocalMedia = (PoJoLocalMedia) v.getTag();
            if (mDataDeleteListener != null) {
                mDataDeleteListener.itemDeleted(mPoJoLocalMedia);
            }
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isDeleteIconShow) return;
            PoJoLocalMedia mPoJoLocalMedia = (PoJoLocalMedia) v.getTag();
            L.d(TAG, "......onClick......" + mPoJoLocalMedia.title + "......");
            File tmpFile = new File(mPoJoLocalMedia.path);
            if (!tmpFile.exists() || !tmpFile.canRead() || !tmpFile.isFile()) {
                if (mDataDeleteListener != null) {
                    mDataDeleteListener.itemNowNotAvailable(mPoJoLocalMedia);
                }
                App.showToast(R.string.local_media_play_video_file_not_available);
                return;
            }
            Intent videoIntent = new Intent(mContext, PlayVideoActivity.class);
            videoIntent.putExtra("video_url", mPoJoLocalMedia.path);
            videoIntent.putExtra("video_title", mPoJoLocalMedia.title);
            mContext.startActivity(videoIntent);
        }
    };

    private DataDeleteListener mDataDeleteListener;

    public interface DataDeleteListener {
        /**
         * 用户点击删除
         * @param mPoJoLocalMedia
         */
        void itemDeleted(PoJoLocalMedia mPoJoLocalMedia);

        /**
         * 用户点击了不存在的视频文件
         * @param mPoJoLocalMedia
         */
        void itemNowNotAvailable(PoJoLocalMedia mPoJoLocalMedia);
    }

    public void setOnDataDeleteListener(DataDeleteListener mDataDeleteListener) {
        if (mDataDeleteListener != null) {
            this.mDataDeleteListener = mDataDeleteListener;
        }
    }
}
