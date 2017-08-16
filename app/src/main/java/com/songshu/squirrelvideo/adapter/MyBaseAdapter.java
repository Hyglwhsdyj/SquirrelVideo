package com.songshu.squirrelvideo.adapter;

import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by yb on 15-7-9.
 */
public abstract class MyBaseAdapter extends BaseAdapter {
    protected ImageLoader imageLoader = ImageLoader.getInstance();
}
