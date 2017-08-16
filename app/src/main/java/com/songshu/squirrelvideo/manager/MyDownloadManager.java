package com.songshu.squirrelvideo.manager;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.golshadi.majid.core.DownloadManagerPro;
import com.golshadi.majid.core.enums.TaskStates;
import com.golshadi.majid.report.ReportStructure;
import com.golshadi.majid.report.listener.DownloadManagerListener;
import com.snappydb.SnappydbException;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.db.DownloadDaoImpl;
import com.songshu.squirrelvideo.entity.DBDownloadBean;
import com.songshu.squirrelvideo.entity.ParentVideoBean;
import com.songshu.squirrelvideo.entity.ParentVideoForSnappyDBBean;
import com.songshu.squirrelvideo.entity.PreDownloadBean;
import com.songshu.squirrelvideo.network.VideoUrlNet;
import com.songshu.squirrelvideo.utils.FileUtils;
import com.songshu.squirrelvideo.utils.L;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by yb on 15-8-3.
 */
public class MyDownloadManager implements DownloadManagerListener {

    private static final String TAG = MyDownloadManager.class.getSimpleName() + ":";
    private static final String DOWNLOAD_ROOT_PATH = FileUtils.getDownloadManagerCacheFile().getAbsolutePath();
    private static final String DOWNLOAD_INIT_PATH = "/squirrelvideo/cache/downloadmanager";
    private Context mContext;
    private DownloadManagerPro mDownloadManagerPro;
    private DownloadDaoImpl mDownloadDaoImpl;
    private HandlerThread mHandlerThread;
    private Handler mHandler;

    public MyDownloadManager(Context context) {
        L.d(TAG, "------MyDownloadManager------");
        if (mContext == null) {
            mContext = context;
            mHandlerThread = new HandlerThread("download.manager", Thread.MIN_PRIORITY);
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper());
        }
        if (mDownloadManagerPro == null) {
            mDownloadManagerPro = new DownloadManagerPro(mContext);
            mDownloadManagerPro.init(DOWNLOAD_INIT_PATH, 1, this);
        }
        if (mDownloadDaoImpl == null) {
            mDownloadDaoImpl = new DownloadDaoImpl();
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    private Map<String, PreDownloadBean> downloadBeanMap = new HashMap<String, PreDownloadBean>();


    public void initStartDownload(String id, final String channel, String title, String posterUrl
            , final String videoUrl, String videoNumber, final String sourceName) {
        L.d(TAG, "------initStartDownload------");
        L.d(TAG, "------id------" + id);
        L.d(TAG, "------channel------" + channel);
        L.d(TAG, "------title------" + title);
        L.d(TAG, "------posterUrl------" + posterUrl);
        L.d(TAG, "------videoUrl------" + videoUrl);
        L.d(TAG, "------videoNumber------" + videoNumber);
        L.d(TAG, "------sourceName------" + sourceName);
        final PreDownloadBean bean = new PreDownloadBean();
        bean.id = id;
        bean.channel = channel;
        bean.fileName = title;
        bean.poster_url = posterUrl;
        bean.videoDownDate = System.currentTimeMillis();
        bean.videoHtmlUrl = videoUrl;
        bean.videoWhichEpisode = videoNumber;
        downloadBeanMap.put(videoUrl, bean);

        mHandler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                VideoUrlNet mVideoUrlNet = new VideoUrlNet();
                mVideoUrlNet.getVideoPath(channel, sourceName, videoUrl);
            }
        });
    }


    public void onEventMainThread(final AppEvent.SucGetVideoRealPathEvent e) {
        L.d(TAG, "------SucEvent------VideoRealPath------" + e.play_url);
        if (!downloadBeanMap.containsKey(e.play_url)) return;
        PreDownloadBean preDownloadBean = downloadBeanMap.get(e.play_url);
        preDownload(preDownloadBean, e.mParentVideoData);
        downloadBeanMap.remove(e.play_url);
    }


    public void onEventMainThread(final AppEvent.FailGetVideoRealPathEvent e) {
        L.d(TAG, "------FailEvent------VideoRealPath------");
        if (!downloadBeanMap.containsKey(e.play_url)) return;
        App.showToast("该视频无法下载");
        downloadBeanMap.remove(e.play_url);
    }


    /**
     * 外界下载视频的接口
     *
     * @param bean
     */
    private void preDownload(final PreDownloadBean bean, final ParentVideoBean mParentVideoBean) {
        mHandler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                DBDownloadBean mDBDownloadBean = new DBDownloadBean();
                mDBDownloadBean.videoId = bean.id;
                mDBDownloadBean.videoChannel = bean.channel;
                mDBDownloadBean.videoName = bean.fileName;
                mDBDownloadBean.videoPosterUrl = bean.poster_url;
                mDBDownloadBean.videoDownDate = bean.videoDownDate;
                mDBDownloadBean.videoHtmlUrl = bean.videoHtmlUrl;
                mDBDownloadBean.videoCurrentProcess = 0;
                StringBuilder sb_net_url = new StringBuilder();
                StringBuilder sb_local_url = new StringBuilder();
                StringBuilder sb_task_id = new StringBuilder();
                for (int i = 1; i <= mParentVideoBean.list.size(); i++) {
                    String net_url = mParentVideoBean.list.get(i - 1).getUrl();
                    sb_net_url.append(net_url).append(",");
                    String local_url = DOWNLOAD_ROOT_PATH + File.separator + bean.fileName
                            + (TextUtils.isEmpty(bean.videoWhichEpisode) ? "" : "_" + bean.videoWhichEpisode) + "_" + i;
                    sb_local_url.append(local_url).append(",");
                    int taskId = addTask(
                            local_url.substring(local_url.lastIndexOf("/") + 1)
                            , net_url
                            , true
                            , false);
                    sb_task_id.append(taskId).append(",");
                }
                mDBDownloadBean.videoNetUrl = sb_net_url.toString().substring(0, sb_net_url.toString().length() - 1);
                mDBDownloadBean.videoLocalUrl = sb_local_url.toString().substring(0, sb_local_url.toString().length() - 1);
                mDBDownloadBean.videoTaskId = sb_task_id.toString().substring(0, sb_task_id.toString().length() - 1);
                mDBDownloadBean.videoSegment = 0;
                mDBDownloadBean.videoDownloadState = TaskStates.WAITING;
                mDBDownloadBean.videoWhichEpisode = TextUtils.isEmpty(bean.videoWhichEpisode) ? null : bean.videoWhichEpisode;
                mDownloadDaoImpl.addDownload(mDBDownloadBean);

                if (getActivationTaskNum() < 2) {
                    startDownload(Integer.parseInt(mDBDownloadBean.videoTaskId.split(",")[mDBDownloadBean.videoSegment]));
                    mDBDownloadBean.videoDownloadState = TaskStates.DOWNLOADING;
                    mDownloadDaoImpl.updateDownload(mDBDownloadBean);
                }

                // 把存有本地地址的播放对象存入snappy db
                mParentVideoBean.transArrayToList();
                mParentVideoBean.setListWithVideoLocalPath(mDBDownloadBean.videoLocalUrl.split(","));
                ParentVideoForSnappyDBBean snappyDBBean = new ParentVideoForSnappyDBBean(
                        mParentVideoBean.getTitle(),
                        mParentVideoBean.getSite(),
                        mParentVideoBean.getQuality(),
                        mParentVideoBean.getFormat(),
                        mParentVideoBean.list
                );
                snappyDBBean.setObjectToSnappyDB(mDBDownloadBean.videoHtmlUrl);

                EventBus.getDefault().post(new AppEvent.DownloadNewPreComeEvent());//发出通知 新的下载任务到来
            }
        });

    }


    /**
     * *************************************************************************************************************
     * 向外提供的下载接口
     * *************************************************************************************************************
     */


    /**
     * 向外界提供的删除任务接口
     *
     * @param id
     * @param channel
     */
    public void deleteDownload(final String id, final String channel) {
        mHandler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                DBDownloadBean download = mDownloadDaoImpl.findDownload(id, channel);
                if (download == null) return;
                String[] videoTaskIds = download.videoTaskId.split(",");
                if (download.videoSegment != videoTaskIds.length) {// 如果该任务尚未下载完毕
                    ReportStructure mReportStructure = getTaskStatus(Integer.parseInt(videoTaskIds[download.videoSegment]));
                    if (mReportStructure.getState() == TaskStates.DOWNLOADING) {
                        pauseDownload(Integer.parseInt(videoTaskIds[download.videoSegment]));
                    }
                }
                for (int i = 0; i < videoTaskIds.length; i++) {
                    boolean otherDB = deleteDownload(Integer.parseInt(videoTaskIds[i]), true);
                    L.d(TAG, "------otherDB------deleteDownload------" + otherDB);
                }
                boolean localDB = mDownloadDaoImpl.deleteDownload(id, channel);
                L.d(TAG, "------localDB------deleteDownload------" + localDB);

                //删除本地视频文件
                for (String localFilePath : download.videoLocalUrl.split(",")) {
                    FileUtils.deleteFile(localFilePath);
                }

                //删除带有本地播放地址的播放对象
                try {
                    App.getSnappyDb().del(download.videoHtmlUrl);
                    App.closeSnappyDb();
                } catch (SnappydbException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 向外界提供的删除任务接口
     *
     * @param id
     * @param channel
     */
    public void pauseDownload(final String id, final String channel) {
        mHandler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                DBDownloadBean download = mDownloadDaoImpl.findDownload(id, channel);
                String[] videoTaskIds = download.videoTaskId.split(",");
                pauseDownload(Integer.parseInt(videoTaskIds[download.videoSegment]));
                download.videoDownloadState = TaskStates.PAUSED;
                mDownloadDaoImpl.updateDownload(download);
                EventBus.getDefault().post(new AppEvent.DownloadPausedEvent(download));
            }
        });
    }


    /**
     * 向外界提供的删除任务接口
     * 从暂停到开始下载 调用此方法 由于开始下载到正式下载可能存在一定的等待时间,于是这里直接发event
     *
     * @param id
     * @param channel
     */
    public void startDownload(final String id, final String channel) {
        mHandler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                if (getActivationTaskNum() >= 2) {
                    EventBus.getDefault().post(new AppEvent.ReachMaxDownloadTaskNumEvent());
                } else {
                    DBDownloadBean download = mDownloadDaoImpl.findDownload(id, channel);
                    if (download == null) return;
                    startDownload(Integer.parseInt(download.videoTaskId.split(",")[download.videoSegment]));
                    download.videoDownloadState = TaskStates.DOWNLOADING;
                    mDownloadDaoImpl.updateDownload(download);
                    EventBus.getDefault().post(new AppEvent.DownloadStartedEvent(download));
                }
            }
        });
    }


    /**
     * 处于等待状态的点击判断接口
     *
     * @param id
     * @param channel
     */
    public void handleWaitTaskClick(final String id, final String channel) {
        mHandler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                if (getActivationTaskNum() >= 2) {
                    EventBus.getDefault().post(new AppEvent.ReachMaxDownloadTaskNumEvent());
                } else {
                    DBDownloadBean goToDownload = mDownloadDaoImpl.findDownload(id, channel);
                    if (goToDownload == null) return;
                    startDownload(Integer.parseInt(goToDownload.videoTaskId.split(",")[goToDownload.videoSegment]));
                    goToDownload.videoDownloadState = TaskStates.DOWNLOADING;
                    mDownloadDaoImpl.updateDownload(goToDownload);
                    EventBus.getDefault().post(new AppEvent.DownloadStartedEvent(goToDownload));
                }
            }
        });
    }


    /**
     * 暂停所有下载任务
     * 退出程序时调用,为了能够在退出app前完成操作,这里不能用子线程
     */
    public void pauseAllDownloadingTask() {
        L.d(TAG, "------pauseAllDownloadingTask------");
        ArrayList<DBDownloadBean> allDownloadingDownload = (ArrayList<DBDownloadBean>) mDownloadDaoImpl.findAllDownloadingDownload();
        L.d(TAG, "------allDownloadingDownload------" + allDownloadingDownload);
        for (DBDownloadBean bean : allDownloadingDownload) {
            String[] videoTaskIds = bean.videoTaskId.split(",");
            pauseDownload(Integer.parseInt(videoTaskIds[bean.videoSegment]));
            bean.videoDownloadState = TaskStates.PAUSED;
            mDownloadDaoImpl.updateDownload(bean);
        }
    }


    /**
     * 获取处于激活状态的任务数量
     *
     * @return
     */
    public int getActivationTaskNum() {
        return mDownloadDaoImpl.findAllDownloadingDownloadNums();
    }


    /**
     * 查找制定id和channel的 download db bean
     *
     * @param id
     * @param channel
     * @return
     */
    public DBDownloadBean findDBDownloadBean(String id, String channel) {
        return mDownloadDaoImpl.findDownload(id + "", channel);
    }

    /**
     * 查找指定oriUrl的 download db bean
     *
     * @param oriUrl
     * @return
     */
    public DBDownloadBean findDBDownloadBean(String oriUrl) {
        return mDownloadDaoImpl.findDownload(oriUrl);
    }

    /**
     * 根据htmlurl去查找任务的状态,如果没有这个任务则返回-2
     *
     * @param videoHtmlUrl
     * @return
     */
    public int getStatusByHtmlUrl(String videoHtmlUrl) {
        return mDownloadDaoImpl.findDownloadByHtmlUrl(videoHtmlUrl);
    }


    /**
     * 查看某个channel下的下载表是否为空
     *
     * @return
     */
    public boolean isDownloadTableEmpty() {
        return mDownloadDaoImpl.isTableEmpty();
    }


    /**
     * 查找所有channel的下载任务
     *
     * @return
     */
    public List<DBDownloadBean> findAllKindsOfDownloadTasks() {
        return mDownloadDaoImpl.findDownload();
    }


    /**
     * *************************************************************************************************************
     * 下载信息状态变回回调
     * *************************************************************************************************************
     */


    @Override
    public void OnDownloadStarted(final long taskId) {
        L.d(TAG, taskId + "------OnDownloadStarted------");
        mHandler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                // AsyncStartDownload init会卡主获取源的size，不算真正的下载，用户这个时候暂停，因为没有下载线程AsyncWorker,因此暂停没有意思，
                // 此时还是在等待init的size返回，但用户已经暂停，
                // 等到init的size返回，Moderator中start方法被调用，准备工作做好，开始执行AsyncWorker下载线程，但之前会回调listener，回调这里，
                // 判断用户是否点击了暂停，即下载任务的状态，决定是否要暂停真正的下载任务
                DBDownloadBean bean = mDownloadDaoImpl.findDownloadByTaskId(taskId + "");
                if (bean == null) return;
                if (bean.videoDownloadState == TaskStates.PAUSED) {
                    L.d(TAG, "------用户已经点击了暂停------");
                    pauseDownload(Integer.parseInt(taskId + ""));
                }
            }
        });
//        mHandler.postAtFrontOfQueue(new Runnable() {
//            @Override
//            public void run() {
//                DBDownloadBean bean = mDownloadDaoImpl.findDownloadByTaskId(taskId + "");
//                if (bean == null) return;
//                bean.videoDownloadState = TaskStates.DOWNLOADING;
//                mDownloadDaoImpl.updateDownload(bean);
//            }
//        });
    }


    @Override
    public void OnDownloadPaused(final long taskId) {
        L.d(TAG, taskId + "------OnDownloadPaused------");
//        mHandler.postAtFrontOfQueue(new Runnable() {
//            @Override
//            public void run() {
//                DBDownloadBean bean = mDownloadDaoImpl.findDownloadByTaskId(taskId + "");
//                if (bean == null) return;
//                bean.videoDownloadState = TaskStates.PAUSED;
//                mDownloadDaoImpl.updateDownload(bean);
//                EventBus.getDefault().post(new AppEvent.DownloadPausedEvent(bean));
//            }
//        });

    }


    @Override
    public void onDownloadProcess(final long taskId, final double percent, long downloadedLength) {
//        L.d(TAG, "...... onDownloadProcess ------> taskId : " + taskId + " , percent :　" + percent + " , downloadedLength : " + downloadedLength);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                DBDownloadBean bean = mDownloadDaoImpl.findDownloadByTaskId(taskId + "");
                if (bean == null) return;
                int segmentNum = bean.videoTaskId.split(",").length;
                int currentSeg = bean.videoSegment;
                double realPercent = (100.0 / segmentNum) * currentSeg + (100.0 / segmentNum) * (percent / 100.0);
                L.d(TAG, taskId + "------process------" + realPercent);
                int beforeDot = Integer.parseInt(String.valueOf(realPercent).substring(0, String.valueOf(realPercent).indexOf(".")));
                int currentProcess = (beforeDot >= 0 && beforeDot < 100) ? beforeDot + 1 : 100;
                bean.videoCurrentProcess = currentProcess;
                mDownloadDaoImpl.updateDownload(bean);
                EventBus.getDefault().post(new AppEvent.DownloadProcessEvent(bean));
            }
        });
    }


    @Override
    public void connectionLost(final long taskId) {
        L.d(TAG, taskId + "------失去连接------");
        mHandler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                DBDownloadBean bean = mDownloadDaoImpl.findDownloadByTaskId(taskId + "");
                if (bean == null) return;
                startDownload(Integer.parseInt(bean.videoTaskId.split(",")[bean.videoSegment]));
                bean.videoDownloadState = TaskStates.DOWNLOADING;
                mDownloadDaoImpl.updateDownload(bean);
            }
        });
    }


    @Override
    public void OnDownloadFinished(final long taskId) {
    }


    @Override
    public void OnDownloadRebuildStart(long taskId) {
        L.d(TAG, taskId + "______构建文件开始______");
    }


    @Override
    public void OnDownloadRebuildFinished(long taskId) {
        L.d(TAG, taskId + "______构建文件结束______");
    }


    @Override
    public void OnDownloadCompleted(final long taskId) {
        L.d(TAG, taskId + "______整个下载流程结束______");
        mHandler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                DBDownloadBean bean = mDownloadDaoImpl.findDownloadByTaskId(taskId + "");
                if (bean == null) return;
                bean.videoSegment += 1;
                if (bean.videoSegment == bean.videoTaskId.split(",").length) {
                    bean.videoDownloadState = TaskStates.END;
                    mDownloadDaoImpl.updateDownload(bean);
                    EventBus.getDefault().post(new AppEvent.DownloadFinishedEvent(bean));
                } else {
                    if (bean.videoDownloadState != TaskStates.PAUSED) {
                        L.d(TAG, taskId + "______继续继续______");
                        // 如果用户在i分段complete_finish阶段暂停，内部程序会继续，直到rebuild完毕，回调download_complete,
                        // 这里判断如果用户暂停了，则返回，不进行i+1段的下载，直到用户点击继续下载按键
                        startDownload(Integer.parseInt(bean.videoTaskId.split(",")[bean.videoSegment]));
                        bean.videoDownloadState = TaskStates.DOWNLOADING;
                    }
                    mDownloadDaoImpl.updateDownload(bean);
                }
            }
        });
    }

    /**
     * *************************************************************************************************************
     * 下载框架主要的接口
     * *************************************************************************************************************
     */


    /**
     * 添加任务
     *
     * @param saveName
     * @param url
     * @param overwrite
     * @param priority
     * @return
     */
    private int addTask(String saveName, String url, boolean overwrite, boolean priority) {
        L.d(TAG, "------addTask------saveName : " + saveName + " , url : " + url + " , overwrite : " + overwrite + " , priority : " + priority);
        return mDownloadManagerPro.addTask(saveName, url, overwrite, priority);
    }

    /**
     * 开始下载任务
     *
     * @param taskId
     */
    private void startDownload(int taskId) {
        L.d(TAG, taskId + "------startDownload------");
        try {
            mDownloadManagerPro.startDownload(taskId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 暂停下载任务
     *
     * @param taskId
     */
    private void pauseDownload(int taskId) {
        L.d(TAG, taskId + "------pauseDownload------");
        mDownloadManagerPro.pauseDownload(taskId);
    }


    /**
     * 删除下载任务
     *
     * @param taskId
     * @param deleteTaskFile 是否删除文件
     * @return
     */
    private boolean deleteDownload(int taskId, boolean deleteTaskFile) {
        L.d(TAG, taskId + "------deleteDownload------");
        return mDownloadManagerPro.delete(taskId, deleteTaskFile);
    }


    /**
     * 获取下载任务的具体信息
     *
     * @param taskId
     * @return
     */
    private ReportStructure getTaskStatus(int taskId) {
        L.d(TAG, taskId + "------getTaskStatus------");
        return mDownloadManagerPro.singleDownloadStatus(taskId);
    }


    /**
     * *************************************************************************************************************
     * 获取相同状态的任务报告集合
     * *************************************************************************************************************
     */


    /**
     * 获取所有处理init状态的下载任务集合
     *
     * @return
     */
    private List<ReportStructure> getStatusInitTasks() {
        List<ReportStructure> list = mDownloadManagerPro.downloadTasksInSameState(TaskStates.INIT);
        L.d(TAG, "------getStatusInitTasks------" + list.size());
        return list;
    }


    /**
     * 获取所有处理ready状态的下载任务集合
     *
     * @return
     */
    private List<ReportStructure> getStatusReadyTasks() {
        List<ReportStructure> list = mDownloadManagerPro.downloadTasksInSameState(TaskStates.READY);
        L.d(TAG, "------getStatusReadyTasks------" + list.size());
        return list;
    }


    /**
     * 获取所有处理downloading状态的下载任务集合
     *
     * @return
     */
    private List<ReportStructure> getStatusDownloadingTasks() {
        List<ReportStructure> list = mDownloadManagerPro.downloadTasksInSameState(TaskStates.DOWNLOADING);
        L.d(TAG, "------getStatusDownloadingTasks------" + list.size());
        return list;
    }


    /**
     * 获取所有处理paused状态的下载任务集合
     *
     * @return
     */
    public List<ReportStructure> getStatusPauseTasks() {
        L.d(TAG, "------getStatusPauseTasks------");
        return mDownloadManagerPro.downloadTasksInSameState(TaskStates.PAUSED);
    }


    /**
     * 获取所有处理download_finish状态的下载任务集合
     *
     * @return
     */
    private List<ReportStructure> getStatusDownloadFinishedTasks() {
        L.d(TAG, "------getStatusDownloadFinishedTasks------");
        return mDownloadManagerPro.downloadTasksInSameState(TaskStates.DOWNLOAD_FINISHED);
    }


    /**
     * 获取所有处理end状态的下载任务集合
     *
     * @return
     */
    private List<ReportStructure> getStatusEndTasks() {
        L.d(TAG, "------getStatusEndTasks------");
        return mDownloadManagerPro.downloadTasksInSameState(TaskStates.END);
    }


    /**
     * *************************************************************************************************************
     * 关闭下载框架的数据库
     * *************************************************************************************************************
     */


    /**
     * 关闭数据库,释放资源
     */
    private void disconnectDB() {
        mDownloadManagerPro.dispose();
    }

}
