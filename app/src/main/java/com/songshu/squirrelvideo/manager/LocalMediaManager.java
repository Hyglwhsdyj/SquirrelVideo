package com.songshu.squirrelvideo.manager;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.utils.FileUtils;
import com.songshu.squirrelvideo.utils.L;

import java.io.File;

/**
 * Created by yb on 15-10-31.
 * 管理本地视频文件的类
 */
public class LocalMediaManager {

    private static final String TAG = LocalMediaManager.class.getSimpleName() + ":";

    /**
     03wifi   04
     /mnt/sdcard 内部存储
     /mnt/extsd  外置SD卡
     /mnt/usbhost1 外置U盘

     02  03
     /storage/sdcard0 内部存储
     /storage/sdcard1  外置SD卡
     /mnt/usbotg 外置U盘
     */

    /**
     * 根据存储卡情况创建对应的指定文件夹
     */
    public static void initLocalMediaFiles() {
        String sdPaths = FileUtils.getEverySDPath(App.getContext());
        L.d(TAG, "######storage_paths : " + sdPaths);
        String[] paths = sdPaths.split("&");
        for (int i = 0; i < paths.length; i++) {
            FileUtils.createIfNoExists(paths[i] + File.separator + App.getContext().getString(R.string.local_media_movie) + File.separator);
            FileUtils.createIfNoExists(paths[i] + File.separator + App.getContext().getString(R.string.local_media_teleplay) + File.separator);
            FileUtils.createIfNoExists(paths[i] + File.separator + App.getContext().getString(R.string.local_media_opera) + File.separator);
            FileUtils.createIfNoExists(paths[i] + File.separator + App.getContext().getString(R.string.local_media_dance) + File.separator);
            FileUtils.createIfNoExists(paths[i] + File.separator + App.getContext().getString(R.string.local_media_child) + File.separator);
            FileUtils.createIfNoExists(paths[i] + File.separator + App.getContext().getString(R.string.local_media_health) + File.separator);
        }
        FileUtils.getLocalMediaThumbnailCacheFile();
        FileUtils.createIfNoExists(FileUtils.getLocalMediaThumbnailCacheFile().getAbsolutePath() + File.separator + ".nomedia");
        setPathToVariables(paths);
    }


    /**
     * 设置存储卡路径的全局参数
     *
     * @param paths
     */
    public static void setPathToVariables(String[] paths) {
        Const.INTERNAL_SDCARD = "has_no_internal_card";
        Const.EXTERNAL_SDCARD = "has_no_external_card";
        if (paths.length > 0) {
            Const.INTERNAL_SDCARD = paths[0];
            if (paths.length > 1) Const.EXTERNAL_SDCARD = paths[1];
        }
        L.d(TAG, "......PARENT_SDCARD......" + Const.PARENT_SDCARD);
        L.d(TAG, "......INTERNAL_SDCARD......" + Const.INTERNAL_SDCARD);
        L.d(TAG, "......EXTERNAL_SDCARD......" + Const.EXTERNAL_SDCARD);
    }

    /**
     * 判断该文件名的文件或文件夹是否需要忽略
     *
     * @param path
     * @return
     */
    public static boolean needIgnore(String path) {
        if (path.equals("proc")
                || path.equals("protect_f")
                || path.equals("protect_s")
                || path.equals("res")
                || path.equals("root")
                || path.equals("sbchk")
                || path.equals("sbin")
                || path.equals("sys")
                || path.equals("system")
                || path.equals("acct")
                || path.equals("cache")
                || path.equals("config")
                || path.equals("d")
                || path.equals("etc")
                || path.equals("dev")
                || path.equals("vender")
                || path.equals("bootloader")
                || path.equals("charger")
                || path.equals("databk")
                || path.equals("data")
                ) {
            return true;
        }
        return false;
    }

}
