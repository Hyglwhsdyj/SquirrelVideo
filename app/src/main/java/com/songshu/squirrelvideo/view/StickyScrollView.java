package com.songshu.squirrelvideo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by yb on 15-7-10.
 */
public class StickyScrollView extends ScrollView {

    private OnScrollListener onScrollListener;

    public StickyScrollView(Context context) {
        this(context, null);
    }

    public StickyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置滚动接口
     *
     * @param onScrollListener
     */
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollListener != null)
            onScrollListener.onScroll(t);
    }

    /**
     * 滚动的回调接口
     */
    public interface OnScrollListener {
        /**
         * 返回MyScrollView Y方向滑动的距离
         *
         * @param scrollY
         */
        void onScroll(int scrollY);
    }

}
