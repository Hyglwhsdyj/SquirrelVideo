package com.songshu.squirrelvideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.activity.MovieDetailActivity;
import com.songshu.squirrelvideo.activity.TeleplayDetailActivity;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.FuncsDBDataBean;
import com.songshu.squirrelvideo.entity.Pictures;
import com.songshu.squirrelvideo.entity.VideoBean;
import com.songshu.squirrelvideo.manager.TitleManager;
import com.songshu.squirrelvideo.utils.ImageLoaderUtils;
import com.songshu.squirrelvideo.utils.L;

import java.util.List;

/**
 * Created by yb on 15-7-9.
 */
public class FuncsHealthAndOperaAdapter extends MyBaseAdapter {

    private static final String TAG = FuncsHealthAndOperaAdapter.class.getSimpleName() + ":";
    private Context mContext;
    private List<FuncsDBDataBean> dataList;
    private boolean isDeleteIconShow = false;


    public FuncsHealthAndOperaAdapter(Context mContext) {
        this.mContext = mContext;
    }


    public void notifyData(List<FuncsDBDataBean> tempList) {
        L.d(TAG, "notifyData --> datalist size : " + tempList.size());
        dataList = tempList;
        notifyDataSetChanged();
    }

    public void showDeleteIcon(ListView listview, boolean isShow) {
        isDeleteIconShow = isShow;
        for (FuncsDBDataBean bean : dataList) {
            bean.isDeleteBtnShow = isShow;
        }

        int firstVisiblePosition = listview.getFirstVisiblePosition();
        for (int position = 0; position < getCount(); position++) {
            View wantUpdata = listview.getChildAt(position - firstVisiblePosition);
            getView(position, wantUpdata, null, true);
        }

    }

    public List<FuncsDBDataBean> getDataList() {
        return dataList;
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
        return getView(position, convertView, parent, false);
    }

    public View getView(int position, View convertView, ViewGroup parent, boolean isShowDeleteOnly) {
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContext, R.layout.item_health_opera_funcs, null);
            holder.rl_1 = (RelativeLayout) convertView.findViewById(R.id.rl_1);
            holder.rl_2 = (RelativeLayout) convertView.findViewById(R.id.rl_2);
            holder.rl_3 = (RelativeLayout) convertView.findViewById(R.id.rl_3);
            holder.iv_delete_1 = (ImageView) convertView.findViewById(R.id.iv_delete_1);
            holder.iv_delete_2 = (ImageView) convertView.findViewById(R.id.iv_delete_2);
            holder.iv_delete_3 = (ImageView) convertView.findViewById(R.id.iv_delete_3);
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

        holder.rl_1.setVisibility(View.GONE);
        holder.rl_2.setVisibility(View.GONE);
        holder.rl_3.setVisibility(View.GONE);

        if ((position * 3) < dataList.size()) {
            holder.rl_1.setVisibility(View.VISIBLE);
            FuncsDBDataBean bean = dataList.get(position * 3);
            if (!isShowDeleteOnly) {
                imageLoader.displayImage(bean.picture.big, holder.iv_video_pic_1, ImageLoaderUtils.getAdapterOptions());
                holder.tv_video_name_1.setText(bean.title);
                holder.rl_1.setOnClickListener(clickListener);
                holder.rl_1.setTag(bean);
                holder.iv_delete_1.setOnClickListener(deleteClickListener);
                holder.iv_delete_1.setTag(bean);
            }
            holder.iv_delete_1.setVisibility(bean.isDeleteBtnShow ? View.VISIBLE : View.GONE);
        }

        if ((position * 3 + 1) < dataList.size()) {
            holder.rl_2.setVisibility(View.VISIBLE);
            FuncsDBDataBean bean = dataList.get(position * 3 + 1);
            if (!isShowDeleteOnly) {
                imageLoader.displayImage(bean.picture.big, holder.iv_video_pic_2, ImageLoaderUtils.getAdapterOptions());
                holder.tv_video_name_2.setText(bean.title);
                holder.rl_2.setOnClickListener(clickListener);
                holder.rl_2.setTag(bean);
                holder.iv_delete_2.setOnClickListener(deleteClickListener);
                holder.iv_delete_2.setTag(bean);
            }
            holder.iv_delete_2.setVisibility(bean.isDeleteBtnShow ? View.VISIBLE : View.GONE);

        }

        if ((position * 3 + 2) < dataList.size()) {
            holder.rl_3.setVisibility(View.VISIBLE);
            FuncsDBDataBean bean = dataList.get(position * 3 + 2);
            if (!isShowDeleteOnly) {
                imageLoader.displayImage(bean.picture.big, holder.iv_video_pic_3, ImageLoaderUtils.getAdapterOptions());
                holder.tv_video_name_3.setText(bean.title);
                holder.rl_3.setOnClickListener(clickListener);
                holder.rl_3.setTag(bean);
                holder.iv_delete_3.setOnClickListener(deleteClickListener);
                holder.iv_delete_3.setTag(bean);
            }
            holder.iv_delete_3.setVisibility(bean.isDeleteBtnShow ? View.VISIBLE : View.GONE);

        }

        return convertView;

    }

    class Holder {
        public RelativeLayout rl_1;
        public RelativeLayout rl_2;
        public RelativeLayout rl_3;
        public ImageView iv_delete_1;
        public ImageView iv_delete_2;
        public ImageView iv_delete_3;
        public LinearLayout ll_1;
        public LinearLayout ll_2;
        public LinearLayout ll_3;
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
            FuncsDBDataBean mFuncsDBDataBean = (FuncsDBDataBean) v.getTag();
            if (mDataDeleteListener != null) {
                mDataDeleteListener.itemDeleted(mFuncsDBDataBean);
            }
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isDeleteIconShow) return;
            FuncsDBDataBean mFuncsDBDataBean = (FuncsDBDataBean) v.getTag();
            VideoBean bean = new VideoBean();
            bean.id = mFuncsDBDataBean.id;
            bean.channel = mFuncsDBDataBean.channel;
            bean.title = mFuncsDBDataBean.title;
            bean.picture = new Pictures();
            bean.picture.big = mFuncsDBDataBean.picture.big;
            bean.picture.small = mFuncsDBDataBean.picture.small;
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

    private DataDeleteListener mDataDeleteListener;

    public interface DataDeleteListener {
        void itemDeleted(FuncsDBDataBean mFuncsDBDataBean);
    }

    public void setOnDataDeleteListener(DataDeleteListener mDataDeleteListener) {
        if (mDataDeleteListener != null) {
            this.mDataDeleteListener = mDataDeleteListener;
        }
    }
}