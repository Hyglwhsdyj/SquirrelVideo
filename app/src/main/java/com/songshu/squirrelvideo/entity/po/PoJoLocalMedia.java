package com.songshu.squirrelvideo.entity.po;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.songshu.squirrelvideo.utils.FileUtils;

import java.io.File;

/**
 * Created by yb on 15-10-30.
 * 本地视频对象
 */
@DatabaseTable(tableName = "localmedia")
public class PoJoLocalMedia {

    @DatabaseField(generatedId = true)
    public long id;
    /**
     * 视频标题
     */
    @DatabaseField
    public String title;
    /**
     * 视频标题拼音
     */
    @DatabaseField
    public String title_key;
    /**
     * 视频路径
     */
    @DatabaseField
    public String path;
    /**
     * 最后一次访问时间
     */
    @DatabaseField
    public long last_access_time;
    /**
     * 最后一次修改时间
     */
    @DatabaseField
    public long last_modify_time;
    /**
     * 视频时长
     */
    @DatabaseField
    public int duration;
    /**
     * 视频播放进度
     */
    @DatabaseField
    public int position;
    /**
     * 视频缩略图路径
     */
    @DatabaseField
    public String thumb_path;
    /**
     * 文件大小
     */
    @DatabaseField
    public long file_size;
    /**
     * 视频宽度
     */
    @DatabaseField
    public int width;
    /**
     * 视频高度
     */
    @DatabaseField
    public int height;
    /**
     * MIME类型
     */
    @DatabaseField
    public String mime_type;
    /**
     * 0 本地视频 1 网络视频
     */
    @DatabaseField
    public int type = 0;
    /**
     * 文件的类别,即上一级目录名称
     */
    @DatabaseField
    public String category;
    /**
     * 文件位于的存储卡
     */
    @DatabaseField
    public String sdcard_name;
    /**
     * 删除图标是否显示
     */
    public boolean isDeleteShow;


    public PoJoLocalMedia() {

    }

    public PoJoLocalMedia(File f) {
        title = f.getName();
        path = FileUtils.getCanonical(f);
        String[] paths = path.split("/");
        sdcard_name = File.separator + paths[1] + File.separator + paths[2];
        last_modify_time = f.lastModified();
        file_size = f.length();
    }

    public PoJoLocalMedia(String path, String mimeType) {
        this(new File(path));
        this.mime_type = mimeType;
    }

    @Override
    public String toString() {
        return "PoJoLocalMedia{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", title_key='" + title_key + '\'' +
                ", path='" + path + '\'' +
                ", last_access_time=" + last_access_time +
                ", last_modify_time=" + last_modify_time +
                ", duration=" + duration +
                ", position=" + position +
                ", thumb_path='" + thumb_path + '\'' +
                ", file_size=" + file_size +
                ", width=" + width +
                ", height=" + height +
                ", mime_type='" + mime_type + '\'' +
                ", type=" + type +
                ", category='" + category + '\'' +
                ", sdcard_name='" + sdcard_name + '\'' +
                ", isDeleteShow=" + isDeleteShow +
                '}';
    }
}
