package com.songshu.squirrelvideo.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.songshu.squirrelvideo.R;

import java.io.File;

/**
 * Created by yb on 15-7-6.
 */
public class ImageLoaderUtils {
    private static final String TAG = ImageLoaderUtils.class.getSimpleName() + ":";

    public static void init(Context mContext) {
        File cacheDir = FileUtils.getImageLoaderCacheFile();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LRULimitedMemoryCache(2 * 1024 * 1024))
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheExtraOptions(480, 320, null)
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }

    private static DisplayImageOptions adapterOptions;

    public static DisplayImageOptions getAdapterOptions() {
        adapterOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.video_load_default)
                .showImageForEmptyUri(R.drawable.video_load_default)
                .showImageOnFail(R.drawable.video_load_default)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(500))
                .build();
        return adapterOptions;
    }

    private static DisplayImageOptions defaultOptions;

    public static DisplayImageOptions getDefaultOptions() {
        defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.video_load_default)
                .showImageForEmptyUri(R.drawable.video_load_default)
                .showImageOnFail(R.drawable.video_load_default)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheInMemory(true).cacheOnDisc(true).considerExifParams(true)
                .build();
        return defaultOptions;
    }

    /**
     * 加载本地图片
     * @return
     */
    public static DisplayImageOptions getLocalMediaOptions() {
        defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.video_load_default)
                .showImageForEmptyUri(R.drawable.video_load_default)
                .showImageOnFail(R.drawable.video_load_default)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheInMemory(true).considerExifParams(true)
                .build();
        return defaultOptions;
    }
}
