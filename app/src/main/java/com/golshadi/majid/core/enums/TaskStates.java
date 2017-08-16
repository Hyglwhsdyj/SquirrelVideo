package com.golshadi.majid.core.enums;

/**
 * Created by Majid Golshadi on 4/15/2014.
 */
public class TaskStates {

    public static final int NOINCLUDE = -2; // 未添加该任务
    public static final int WAITING = -1; // 添加后 等待状态
    public static final int INIT = 0; // 添加后 初始化
    public static final int READY = 1; // 准备
    public static final int DOWNLOADING = 2; // 下载中
    public static final int PAUSED = 3; // 暂停
    public static final int DOWNLOAD_FINISHED = 4; // 下载完毕等待删除临时文件重命名新文件
    public static final int END = 5; // 该任务下载并重构完毕

}
