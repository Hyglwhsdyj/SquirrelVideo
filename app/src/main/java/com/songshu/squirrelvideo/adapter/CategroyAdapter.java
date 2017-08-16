package com.songshu.squirrelvideo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.RoundedImageView;
import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.activity.CategoryActivity;
import com.songshu.squirrelvideo.activity.SpecificCategoryActivity;
import com.songshu.squirrelvideo.entity.CategoryBean;
import com.songshu.squirrelvideo.utils.L;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yb on 15-7-6.
 */
public class CategroyAdapter extends MyBaseAdapter implements View.OnClickListener {

    private static final String TAG = CategroyAdapter.class.getSimpleName() + ":";
    private Context mContext;
    private Activity act;
    private List<CategoryBean> dataList = new LinkedList<CategoryBean>();

    public CategroyAdapter(Activity act, List<CategoryBean> dataList) {
        this.mContext = act;
        this.dataList = dataList;
        this.act = act;
    }

    public void notifyData(List<CategoryBean> tempList) {
        L.d(TAG, "notifyData --> datalist size : " + tempList.size());
        dataList = tempList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int count = 0;
        if (dataList != null) {
            if (0 != (dataList.size() % 2)) {
                count = dataList.size() / 2 + 1;
            } else {
                count = dataList.size() / 2;
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
            convertView = View.inflate(mContext, R.layout.item_category, null);

            holder.ll_whole_1 = (LinearLayout) convertView.findViewById(R.id.ll_whole_1);
            holder.ll_whole_2 = (LinearLayout) convertView.findViewById(R.id.ll_whole_2);

            holder.rl_whole_1 = (RelativeLayout) convertView.findViewById(R.id.rl_whole_1);
            holder.rl_whole_2 = (RelativeLayout) convertView.findViewById(R.id.rl_whole_2);

            holder.riv_1 = (RoundedImageView) convertView.findViewById(R.id.riv_1);
            holder.riv_2 = (RoundedImageView) convertView.findViewById(R.id.riv_2);
            holder.tv_sub_title_1 = (TextView) convertView.findViewById(R.id.tv_sub_title_1);
            holder.tv_sub_title_2 = (TextView) convertView.findViewById(R.id.tv_sub_title_2);
            holder.rl_selected_1 = (RelativeLayout) convertView.findViewById(R.id.rl_selected_1);
            holder.rl_selected_2 = (RelativeLayout) convertView.findViewById(R.id.rl_selected_2);
            holder.rl_un_selected_1 = (RelativeLayout) convertView.findViewById(R.id.rl_un_selected_1);
            holder.rl_un_selected_2 = (RelativeLayout) convertView.findViewById(R.id.rl_un_selected_2);
            holder.iv_line_1 = (ImageView) convertView.findViewById(R.id.iv_line_1);
            holder.iv_line_2 = (ImageView) convertView.findViewById(R.id.iv_line_2);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
            holder.iv_line_1.setVisibility(View.VISIBLE);
            holder.iv_line_2.setVisibility(View.VISIBLE);
        }

        holder.ll_whole_1.setVisibility(View.GONE);
        holder.ll_whole_2.setVisibility(View.GONE);

        if (position == (getCount() - 1)) {// 取消最后一行的下划线
            holder.iv_line_1.setVisibility(View.GONE);
            holder.iv_line_2.setVisibility(View.GONE);
        }

        if ((position * 2) < dataList.size()) {
            holder.ll_whole_1.setVisibility(View.VISIBLE);
            CategoryBean bean = dataList.get(position * 2);
            holder.riv_1.setOnClickListener(listener);
            holder.riv_1.setTag(bean);
            holder.riv_1.setImageResource(bean.poster_url);
            holder.tv_sub_title_1.setText(bean.sub_title);
            if (bean.is_selected) {
                holder.rl_selected_1.setVisibility(View.VISIBLE);
                holder.rl_un_selected_1.setVisibility(View.GONE);
            } else {
                holder.rl_un_selected_1.setVisibility(View.VISIBLE);
                holder.rl_selected_1.setVisibility(View.GONE);
            }

            holder.rl_selected_1.setOnClickListener(this);
            holder.rl_un_selected_1.setOnClickListener(this);
            holder.rl_selected_1.setTag(bean.sub_title);
            holder.rl_un_selected_1.setTag(bean.sub_title);
        }

        if ((position * 2 + 1) < dataList.size()) {
            holder.ll_whole_2.setVisibility(View.VISIBLE);
            CategoryBean bean = dataList.get(position * 2 + 1);
            holder.riv_2.setOnClickListener(listener);
            holder.riv_2.setTag(bean);
            holder.riv_2.setImageResource(bean.poster_url);
            holder.tv_sub_title_2.setText(bean.sub_title);
            if (bean.is_selected) {
                holder.rl_selected_2.setVisibility(View.VISIBLE);
                holder.rl_un_selected_2.setVisibility(View.GONE);
            } else {
                holder.rl_un_selected_2.setVisibility(View.VISIBLE);
                holder.rl_selected_2.setVisibility(View.GONE);
            }

            holder.rl_selected_2.setOnClickListener(this);
            holder.rl_un_selected_2.setOnClickListener(this);
            holder.rl_selected_2.setTag(bean.sub_title);
            holder.rl_un_selected_2.setTag(bean.sub_title);
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        String sub_title = (String) v.getTag();
        for (CategoryBean bean : dataList) {
            if (sub_title.equals(bean.sub_title)) {
                bean.is_selected = !bean.is_selected;
                break;
            }
        }
        notifyDataSetChanged();
        ((CategoryActivity) act).itemCheckOrUncheckCallBack();

    }

    class Holder {

        public RelativeLayout rl_whole_1;
        public RelativeLayout rl_whole_2;

        public RoundedImageView riv_1;
        public RoundedImageView riv_2;

        public TextView tv_sub_title_1;
        public TextView tv_sub_title_2;

        public RelativeLayout rl_selected_1;
        public RelativeLayout rl_selected_2;

        public RelativeLayout rl_un_selected_1;
        public RelativeLayout rl_un_selected_2;

        public ImageView iv_line_1;
        public ImageView iv_line_2;

        public LinearLayout ll_whole_1;
        public LinearLayout ll_whole_2;

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            jumpActivity((CategoryBean) v.getTag());
        }
    };

    private void jumpActivity(CategoryBean bean) {
        Intent intent = new Intent(mContext, SpecificCategoryActivity.class);
        intent.putExtra("category_bean", bean);
        L.d(TAG, "jumpActivity --> SpecificCategoryActivity --> CategoryBean : " + bean);
        act.startActivityForResult(intent, 100);
    }

}
