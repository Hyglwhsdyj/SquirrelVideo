package com.songshu.squirrelvideo.player;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.golshadi.majid.core.enums.TaskStates;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.activity.BaseActivity;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.ChildVideoBean;
import com.songshu.squirrelvideo.entity.ParentVideoBean;
import com.songshu.squirrelvideo.entity.ParentVideoForSnappyDBBean;
import com.songshu.squirrelvideo.entity.PlayTeleplayBean;
import com.songshu.squirrelvideo.network.VideoUrlNet;
import com.songshu.squirrelvideo.player.OrientationDetector.ScreenOrientationListener;
import com.songshu.squirrelvideo.player.PlayerService.PlayerListener;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.NetworkUtil;
import com.songshu.squirrelvideo.utils.Util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 视频测试地址
 * http://v.duole.com/api/video/get_real_src?channel=movie&platform=iOS&source=iqiyi&play_url=http://www.iqiyi.com/v_19rrhnuyz8.html
 * http://v.duole.com/api/video/get_real_src?channel=teleplay&platform=Android&source=youku&play_url=http://v.youku.com/v_show/id_XNzc0NDI5NzUy.html
 * http://v.duole.com/api/video/get_real_src?channel=variety&platform=Android&source=youku&play_url=http://v.youku.com/v_show/id_XNzgxNTM1MzA4.html
 */
public class PlayVideoActivity extends BaseActivity implements MediaController.MediaPlayerControlListener, VideoView.SurfaceCallback, VideoUrlNet.OnNewVideoUrlListener {

    private static final String TAG = PlayVideoActivity.class.getSimpleName();

    private static final IntentFilter SCREEN_FILTER = new IntentFilter(Intent.ACTION_SCREEN_ON);
    private ScreenReceiver mScreenReceiver;

    // 返回键
    private ImageButton mBackBtn;

    private View mRootView;
    private VideoView mVideoView;
    /**
     * 重新加载
     */
    private View mReloadView;
    private TextView new_player_reload_tv_btn;


    private View mVideoLoadingLayout;
    private TextView mVideoLoadingText;

    private Uri mUri;

    private MediaController mMediaController;
    private PlayerService mPlayerService;
    private ServiceConnection mServiceConnection;

    /**
     * 监听屏幕方向的改变
     */
    private OrientationDetector mOrientationDetector;

    /**
     * Activity是否已经调用onCreate方法，true为
     */
    private boolean isCreated = false;

    /**
     * true硬解，false为软解
     */
    private boolean isHWCodec = false;

    /**
     * true为Service服务已绑定, false为解除服务绑定
     */
    private boolean isServiceConnected = false;

    /**
     * true为SurfaceView的回调方法已被调用，false为反
     */
    private boolean isSurfaceCreated = false;

    /**
     * true已经注册广播，false未注册广播
     */
    private boolean isReceiverRegistered = false;

    /**
     * true为播放器关闭完成，false为反
     */
    private boolean isCloaseComplete = false;

    /**
     * 是否播放完毕
     */
    private boolean isEnd;

    /**
     * true从开头开始播放，false从上次播放的位置开始
     */
    private boolean isFromStart;

//	private float lastPosition;
//	private float startPosition = -1.1f;

    /**
     * 电视剧、少儿是有“剧集”，其他类型的电影/健身/曲艺视频没有剧集
     */
    private String mEpisodeType = "teleplay,child";
    /**
     * true为有剧集，false为其反
     */
    private boolean isEpisode;
    private String mChannel;
    private String mSource;
    private boolean isCache;
    /**
     * 视频的网页地址
     */
    private String mVideoUrl;
    /**
     * 选集集合
     */
    private List<PlayTeleplayBean> mEpisodeList;
    /**
     * 当前播放的第几集
     */
    private int mPos;
    private boolean isHistory;
    private long lastPosition;

    private DB mSnappyDb;


    /**
     * 获取视频地址的网络操作类
     */
    private VideoUrlNet mVideoUrlNet;

    static {
        SCREEN_FILTER.addAction(Intent.ACTION_SCREEN_OFF);
    }


    private DifferentEntranceBroadcastReceiver mDifferentEntranceBroadcastReceiver;
    private UserPressLittleWhitePot mUserPressLittleWhitePot;
    private VideoCallEventReceiver mVideoCallEventReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        L.d(TAG, "............onCreate............");
        Util.checkAndFinishVideoAct(this);
        super.onCreate(savedInstanceState);
        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
            return;

        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mPlayerService = null;
                isServiceConnected = false;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mPlayerService = ((PlayerService.LocalBinder) service).getService();
                isServiceConnected = true;
                if (isSurfaceCreated)
                    vPlayerHandler.sendEmptyMessage(OPEN_FILE);
                L.d(TAG, "onServiceConnected...");
            }
        };
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_play_video);
        initViewControls();
        initData();
        manageReceivers();
        isCreated = true;

        mDifferentEntranceBroadcastReceiver = new DifferentEntranceBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.songshu.different.entrance.broadcast.receiver");
        registerReceiver(mDifferentEntranceBroadcastReceiver, filter);

        mUserPressLittleWhitePot = new UserPressLittleWhitePot();
        IntentFilter userPressWhiteFilter = new IntentFilter("com.songshu.notify_home");
        registerReceiver(mUserPressLittleWhitePot, userPressWhiteFilter);

        mVideoCallEventReceiver = new VideoCallEventReceiver();
        IntentFilter videoCallIntentFilter = new IntentFilter();
        videoCallIntentFilter.addAction(BROAD_CAST_START_VIDEO_TELEPHONY);
        videoCallIntentFilter.addAction(BROAD_CAST_FINISH_VIDEO_TELEPHONY);
        registerReceiver(mVideoCallEventReceiver, videoCallIntentFilter);
    }

    @Override
    protected void onStart() {
        L.d(TAG, "............onStart............");
        super.onStart();
        if (!isCreated)
            return;
        Intent serviceIntent = new Intent(this, PlayerService.class);
        serviceIntent.putExtra("isHWCodec", isHWCodec);
        bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        L.d(TAG, "onStart...");
    }


    @Override
    public void onResume() {
        L.d(TAG, "............onResume............");
        super.onResume();
        if (!isCreated)
            return;

        if (mRootView != null)
            mRootView.invalidate();
        // 开启监听屏幕方向
        mOrientationDetector.enable();
    }


    private boolean isUserWantFinish = false;

    protected void myFinish() {
        L.d(TAG, "............myFinish............");
        isUserWantFinish = true;
        if (!isCreated)
            return;
        if (isInitialized()) {
            savePosition();
            if (mPlayerService != null && mPlayerService.isPlaying())
                stopPlayer();
            mPlayerService.releaseSurface();
        }
        if (isServiceConnected) {
            unbindService(mServiceConnection);
            isServiceConnected = false;
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        L.d(TAG, "............onPause..............");
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.d(TAG, "............onStop..............");
        if (!isUserWantFinish) {//遇到了如弹窗等非用户行为的中断事件
            L.d(TAG, "............not user want to stop.............." + isUserWantFinish);
            savePlayHistory();
            if (mPlayerService != null && mPlayerService.isPlaying())
                stopPlayer();
            mPlayerService.releaseSurface();
            if (isServiceConnected) {
                unbindService(mServiceConnection);
                isServiceConnected = false;
            }
            PlayVideoActivity.this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.d(TAG, "............onDestroy............");
        if (!isCreated)
            return;
        manageReceivers();
        if (isInitialized() && !mPlayerService.isPlaying())
            //release();
            if (mMediaController != null)
                mMediaController.release();
        try {
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
        }

        unregisterReceiver(mDifferentEntranceBroadcastReceiver);
        unregisterReceiver(mUserPressLittleWhitePot);
        unregisterReceiver(mVideoCallEventReceiver);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        L.d(TAG, "onConfigurationChanged");
        if (isInitialized()) {
            setVideoLayout();
            attachMediaController();
        }
    }

    /**
     * 初始化控件
     */
    private void initViewControls() {
        mRootView = findViewById(R.id.new_root);
        mVideoView = (VideoView) findViewById(R.id.new_video);
        mVideoView.initialize(this, this, isHWCodec);

        mVideoLoadingLayout = findViewById(R.id.new_player_view_loading);
        mVideoLoadingText = (TextView) findViewById(R.id.new_player_buffer_tv);

        mBackBtn = (ImageButton) findViewById(R.id.new_player_back);
        mBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                myFinish();
            }
        });
        // 重试
        mReloadView = findViewById(R.id.new_player_reload_video_layout);
        new_player_reload_tv_btn = (TextView) findViewById(R.id.new_player_reload_tv_btn);
        new_player_reload_tv_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                tryWhenOpenFileOrRequestVideoUrlFail();
            }
        });

    }

    /**
     * 初始化数据
     */
    private void initData() {

        mOrientationDetector = new OrientationDetector(this);
        mOrientationDetector.setmListener(new ScreenOrientationListener() {// 屏幕方向改变时调用
            @Override
            public void onChangeScreenOrientation(int orientation) {
                autoRotation();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {

            if (!TextUtils.isEmpty(intent.getStringExtra("video_url"))
                    && !TextUtils.isEmpty(intent.getStringExtra("video_title"))) {
                playLocalVideo(intent.getStringExtra("video_url"), intent.getStringExtra("video_title"));
                return;
            }

            mVideoUrlNet = new VideoUrlNet();
            mVideoUrlNet.setListener(this);
            mVideoUrl = intent.getStringExtra("url");
            mChannel = intent.getStringExtra("channel");
            mSource = intent.getStringExtra("source");

            isHistory = intent.getBooleanExtra("isHistory", false);
            isCache = intent.getBooleanExtra("cached", false);

            L.d(TAG, "url : " + mVideoUrl + " , channel : " + mChannel + " , source : " + mSource + " , isHistory : " + isHistory + " , isCache : " + isCache);

            if (mEpisodeType.contains(mChannel) && !isCache) {
                isEpisode = true;
                mEpisodeList = (List<PlayTeleplayBean>) intent.getSerializableExtra("list");
                mPos = intent.getIntExtra("pos", 0);
                L.d(TAG, "剧集List=" + mEpisodeList.size());
            } else {
                isEpisode = false;
            }

            if (isHistory) {
                lastPosition = Long.parseLong(intent.getStringExtra("lastPosition"));
                L.d(TAG, "播放上次的历史记录的最后位置=" + lastPosition);
            }

            if (isCache) {
                requestVideoUrlFromLocalSuccess(mVideoUrl);
            } else {
                mVideoUrlNet.getVideoPath(mChannel, mSource, mVideoUrl);
            }
        }
    }

    /**
     * 管理广播（注册广播和销毁广播）
     */
    private void manageReceivers() {
        if (!isReceiverRegistered) {
            mScreenReceiver = new ScreenReceiver();
            registerReceiver(mScreenReceiver, SCREEN_FILTER);

            isReceiverRegistered = true;
        } else {
            if (mScreenReceiver != null) {
                unregisterReceiver(mScreenReceiver);
            }
            isReceiverRegistered = false;
        }
    }

    private class ScreenReceiver extends BroadcastReceiver {
        private boolean screenOn = true;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                screenOn = false;
                stopPlayer();
                L.d(TAG, "屏幕关闭");
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                screenOn = true;
                L.d(TAG, "屏幕打开");
            }
        }

    }


    private void attachMediaController() {
        if (mMediaController != null) {
            mMediaController.release();
        }
        mMediaController = new MediaController(this, mUrlList);
        mMediaController.setFileName(isLocalMediaPlay ? localMediaTitle : mParentVideo.getTitle());
        mMediaController.setMediaPlayer(this);
        mMediaController.setAnchorView(mVideoView.getRootView());
        // 如果视频类型为“电影或者微电影就把媒体控制条上的选集按钮隐藏掉”
        if (!isEpisode) {
            mMediaController.setHideEpisodeBtn();
        } else {
            // 给选集ListView绑定值
            mMediaController.setEpisodeValue(mEpisodeList, mChannel, mPos);
        }
    }

    /**
     * 判断mediaplayer是否可用，true为可用，false为不可用
     *
     * @return
     */
    private boolean isInitialized() {
        return (isCreated && mPlayerService != null && mPlayerService.isInitialized());
    }

    /**
     * 开始视频播放
     */
    protected void startPlayer() {
        if (isInitialized() && mScreenReceiver.screenOn && !mPlayerService.isBuffering()) {
            if (!mPlayerService.isPlaying()) {
                mPlayerService.start();
            }
        }
    }

    /**
     * 停止视频播放
     */
    protected void stopPlayer() {
        if (isInitialized()) {
            mPlayerService.stop();
            L.d(TAG, "stopPlayer停止播放");
        }
    }

    /**
     * 保存播放位置
     */
    private void savePosition() {
        if (mPlayerService != null && mUri != null && mUrlList != null) {
            /*if (isEnd)
                lastPosition = 1.0f;
			else
				lastPosition = (float) (mPlayerService.getCurrentPosition() / (double) mPlayerService.getDuration());*/
            currentPos = mPlayerService.getDuration() - mPlayerService.getCurrentPosition();
        }
    }

	/*private float getStartPosition() {
        if (isFromStart)
			return 1.1f;
		if (startPosition <= 0.0f || startPosition >= 1.0f)
			return lastPosition;
		return startPosition;
	}*/

    /**
     * 重新打开视频
     */
    public void reOpen() {
        reOpen(mUri, "123", false);
    }

    private void reOpen(Uri path, String name, boolean fromStart) {
        if (isInitialized()) {
            mPlayerService.release();
            mPlayerService.releaseContext();
        }
        mUri = path;
        if (mRootView != null)
            mRootView.invalidate();
        mOpened.set(false);
    }

    /**
     * 根据是否开启屏幕旋转，来改变屏幕的方向
     */
    private void autoRotation() {
        boolean enableScreenRotation = false;
        /**
         * flag 为1时表示开启了自动旋转功能
         * flag 为2时表示关闭了自动旋转功能
         */
        int flag = Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);

        if (flag == 1) {
            enableScreenRotation = true;
        } else {
            enableScreenRotation = false;
        }

        if (enableScreenRotation) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private int mVideoMode = VideoView.VIDEO_LAYOUT_SCALE;

    private void setVideoLayout() {
        mVideoView.setVideoLayout(mVideoMode, Const.DEFAULT_ASPECT_RATIO,
                mPlayerService.getVideoWidth(), mPlayerService.getVideoHeight(),
                mPlayerService.getVideoAspectRatio());
    }

    /**
     * 设置MediaPlayer的一些属性，比如：缓冲大小、音量等
     */
    private void setPlayerAttribute() {
        if (!isInitialized())
            return;
        mPlayerService.setBuffer(Const.SMALL_BUFFER_SIZE);
        mPlayerService.setVideoQuality(Const.DEFAULT_VIDEO_QUALITY);
        mPlayerService.setVolume(Const.DEFAULT_STREAM_VOLUME, Const.DEFAULT_STREAM_VOLUME);
        mPlayerService.setDeinterlace(Const.DEFAULT_DEINTERLACE);
    }

    private AtomicBoolean mOpened = new AtomicBoolean(Boolean.FALSE);
    private Object mOpenLock = new Object();
    private static final int OPEN_FILE = 0;
    private static final int OPEN_START = 1;
    private static final int OPEN_SUCCESS = 2;
    private static final int OPEN_FAILED = 3;
    private static final int HW_FAILED = 4;
    private static final int BUFFER_START = 11;
    private static final int BUFFER_PROGRESS = 12;
    private static final int BUFFER_COMPLETE = 13;
    private static final int CLOSE_START = 21;
    private static final int CLOSE_COMPLETE = 22;

    private Handler vPlayerHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case OPEN_FILE:
                    L.d(TAG, ".......................OPEN_FILE........................");
                    synchronized (mOpenLock) {
                        if (!mOpened.get() && mPlayerService != null) {
                            mOpened.set(true);
                            mPlayerService.setListener(playerListener);
                            //if (mPlayerService.isInitialized())
                            //mUri = mPlayerService.getUri();
                            if (mVideoView != null)
                                mPlayerService.setDisplay(mVideoView.getHolder());
                            if (mUri != null)
                                //mVideoView.setVideoPath(url);
                                //mPlayerService.initialize(mUri,getStartPosition(),playerListener, false);
                                mPlayerService.initialize(mUri, currentPos, playerListener, false);
                        }
                    }
                    break;
                case OPEN_START:
                    L.d(TAG, ".......................OPEN_START........................");
                    if (!isCache) {
                        mVideoLoadingText.setText(isLocalMediaPlay ? R.string.local_media_play_video_null_text : R.string.video_layout_before_loading);
                        setVideoLoadingLayoutVisibility(View.VISIBLE);
                    } else {
                        setVideoLoadingLayoutVisibility(View.GONE);
                    }
                    break;
                case OPEN_SUCCESS:
                    L.d(TAG, ".......................OPEN_SUCCESS........................");
                    setPlayerAttribute();
                    mBackBtn.setVisibility(View.GONE);
                    setVideoLoadingLayoutVisibility(View.GONE);
                    setVideoLayout();
                    mPlayerService.start();
                    attachMediaController();
                    break;
                case OPEN_FAILED:
                    L.d(TAG, ".......................OPEN_FAILED........................");
                    //resultFinish(RESULT_FAILED);
                    if (mMediaController != null) {
                        mMediaController.release();
                    }
                    setVideoLoadingLayoutVisibility(View.VISIBLE);
                    currentPos = getCurrentPosition();
                    reOpen(Uri.parse(mUrlList.get(mPlayIndex).getUrl()), "", false);
                    vPlayerHandler.sendEmptyMessage(OPEN_FILE);
                    break;
                case BUFFER_START:
                    L.d(TAG, ".......................BUFFER_START........................");
                    if (!isCache) {
                        mVideoLoadingText.setText(isLocalMediaPlay ? "" : getString(R.string.video_layout_buffering_progress, 0));
                        setVideoLoadingLayoutVisibility(View.VISIBLE);
                        vPlayerHandler.sendEmptyMessageDelayed(BUFFER_PROGRESS, 1000);
                    } else {
                        setVideoLoadingLayoutVisibility(View.GONE);
                    }
                    break;
                case BUFFER_PROGRESS:
                    L.d(TAG, ".......................BUFFER_PROGRESS........................");
                    if (!isCache) {
                        if (mPlayerService.getBufferProgress() >= 100) {
                            setVideoLoadingLayoutVisibility(View.GONE);
                        } else {
                            int bufferPercent = (int) mPlayerService.getBufferProgress();
                            mVideoLoadingText.setText(isLocalMediaPlay ? "" : getString(R.string.video_layout_buffering_progress, bufferPercent));
                            vPlayerHandler.sendEmptyMessageDelayed(BUFFER_PROGRESS, 1000);
                            stopPlayer();
                        }
                    } else {
                        setVideoLoadingLayoutVisibility(View.GONE);
                    }
                    break;
                case BUFFER_COMPLETE:
                    L.d(TAG, ".......................BUFFER_COMPLETE........................");
                    isBufferComplete = true;
                    setVideoLoadingLayoutVisibility(View.GONE);
                    vPlayerHandler.removeMessages(BUFFER_PROGRESS);
                    break;
                case CLOSE_START:
                    L.d(TAG, ".......................CLOSE_START........................");
                    isBufferComplete = false;
                    setVideoLoadingLayoutVisibility(View.GONE);
                    break;
                case CLOSE_COMPLETE:
                    L.d(TAG, ".......................CLOSE_COMPLETE........................");
                    isCloaseComplete = true;
                    break;
                case HW_FAILED:
                    L.d(TAG, ".......................HW_FAILED........................");
                    break;
            }
        }
    };

    public static boolean isBufferComplete = false;//是否缓冲完毕的标记

    private PlayerService.PlayerListener playerListener = new PlayerListener() {

        @Override
        public void onVideoSizeChanged(int width, int height) {
            if (mVideoView != null)
                setVideoLayout();
        }

        // 视频播放完时调用
        @Override
        public void onPlaybackComplete() {
            if (isLocalMediaPlay) {
                finish();
                return;
            }
            mPlayIndex++;
            if (mPlayIndex > mTotalIndex) {
                L.d(TAG, "视频全部播放完毕mPlayIndex=" + mPlayIndex + ",mTotalIndex=" + mTotalIndex);
                autoPlayNext();
            } else {
                // 播放下一段视频
                currentPos = 0l;
                reOpen(Uri.parse(mUrlList.get(mPlayIndex).getUrl()), "", false);
                vPlayerHandler.sendEmptyMessage(OPEN_FILE);
            }
        }

        // 当视频播放完成后自动播放下一集
        private void autoPlayNext() {
            if (isEpisode) {
                if (mPos < mEpisodeList.size() - 1) {
                    mPos += 1;
                    App.showToast(getString(R.string.play_next_item));

                    /*if (mChannel.equals(Constants.VIDEO_TYPE_VARIETY)) {
                        VarietySourceBean variety = (VarietySourceBean) mEpisodeList.get(mPos);
                        mVideoUrl = variety.getUrl();
                    } else */
                    if (mChannel.equals("teleplay") || mChannel.equals("child")) {
                        PlayTeleplayBean teleplay = (PlayTeleplayBean) mEpisodeList.get(mPos);
                        mVideoUrl = teleplay.videoUrl;
                    }
//                    if (App.getMyDownloadManager().getStatusByHtmlUrl(mVideoUrl) == TaskStates.END) {
                    if (checkDownloadStatusFromProvider(mVideoUrl) == TaskStates.END) {
                        L.d(TAG, "......autoPlayNext------>本地缓存:" + mVideoUrl + "......");
                        isCache = true;
                        setVideoLoadingLayoutVisibility(View.GONE);
                        requestVideoUrlFromLocalSuccess(mVideoUrl);
                    } else {
                        L.d(TAG, "......autoPlayNext------>网络下载:" + mVideoUrl + "......");
                        isCache = false;
                        mVideoLoadingText.setText(getString(R.string.video_layout_before_loading));
                        setVideoLoadingLayoutVisibility(View.VISIBLE);
                        mVideoUrlNet.getVideoPath(mChannel, mSource, mVideoUrl);
                    }
                } else {
                    myFinish();
                }
            } else {
                myFinish();
            }
        }

        @Override
        public void onOpenSuccess() {
            vPlayerHandler.sendEmptyMessage(OPEN_SUCCESS);
        }

        @Override
        public void onOpenStart() {
            vPlayerHandler.sendEmptyMessage(OPEN_START);
        }

        @Override
        public void onOpenFailed() {
            vPlayerHandler.sendEmptyMessage(OPEN_FAILED);
        }

        @Override
        public void onCloseStart() {
            vPlayerHandler.sendEmptyMessage(CLOSE_START);
        }

        @Override
        public void onCloseComplete() {
            vPlayerHandler.sendEmptyMessage(CLOSE_COMPLETE);
        }

        @Override
        public void onBufferStart() {
            vPlayerHandler.sendEmptyMessage(BUFFER_START);
            stopPlayer();
        }

        @Override
        public void onBufferComplete() {
            vPlayerHandler.sendEmptyMessage(BUFFER_COMPLETE);
            if (mPlayerService != null && !mPlayerService.needResume())
                startPlayer();
        }

        @Override
        public void onHWRenderFailed() {
        }

        @Override
        public void onDownloadRateChanged(int kbPerSec) {
        }
    };

    /**
     * 管理“视频缓冲提示”信息
     *
     * @param visibility
     */
    private void setVideoLoadingLayoutVisibility(int visibility) {
        if (mVideoLoadingLayout != null)
            mVideoLoadingLayout.setVisibility(visibility);
    }

    @Override
    public void start() {
        if (isInitialized())
            mPlayerService.start();
    }

    @Override
    public void pause() {
        if (isInitialized())
            mPlayerService.stop();
    }

    private long currentPos = 0l;

    @Override
    public void seekTo(long pos) {
        //if (isInitialized())
        //mPlayerService.seekTo((float) ((double) pos / mPlayerService.getDuration()));
        if (isLocalMediaPlay) {
            mPlayerService.seekTo(pos);
            return;
        }
        if (isInitialized()) {
            if (mUrlList.size() == 1) {
                mPlayerService.seekTo(pos);
                return;
            }
            long time = 0;
            for (int i = 0; i < mUrlList.size(); i++) {
                // 转换为毫秒
                time += mUrlList.get(i).getSecond() * 1000;
                if (time > pos) {
                    currentPos = time - pos;
                    if (i == mPlayIndex) {
                        mPlayerService.seekTo(mPlayerService.getDuration() - currentPos);

                    } else {
                        mPlayIndex = i;
                        reOpen(Uri.parse(mUrlList.get(mPlayIndex).getUrl()), "", false);
                        vPlayerHandler.sendEmptyMessage(OPEN_FILE);
                    }
                    break;
                } else if (time == pos) {
                    myFinish();
                }
            }
        }
    }

    @Override
    public void setVideoQuality(int quality) {

    }

    @Override
    public boolean isPlaying() {
        if (isInitialized())
            return mPlayerService.isPlaying();
        return false;
    }

    @Override
    public long getDuration() {
        if (isLocalMediaPlay) {
            return mPlayerService.getDuration();
        }
        if (isInitialized())
            //return mPlayerService.getDuration();
            if (mUrlList == null) return 0l;
        if (mUrlList.size() == 1) {
            return mPlayerService.getDuration();
        } else if (mUrlList.size() > 1) {
            return totalTime();
        }
        return 0;
    }

    /**
     * 计算视频的总时间
     */
    private long totalTime() {
        long time = 0;
        for (int i = 0; i < mUrlList.size(); i++) {
            time += mUrlList.get(i).getSecond();
        }
        return time * 1000;
    }

    @Override
    public long getCurrentPosition() {
        //if (isInitialized())
        //return mPlayerService.getCurrentPosition();
        if (isLocalMediaPlay) {
            return mPlayerService.getCurrentPosition();
        }
        if (isInitialized()) {
            if (mPlayIndex == 0) {
                return mPlayerService.getCurrentPosition();
            } else {
                long time = 0;
                for (int i = 0; i <= (mPlayIndex - 1); i++) {
                    time += mUrlList.get(i).getSecond();
                }
                long currentPosition = time * 1000 + mPlayerService.getCurrentPosition();
                return currentPosition;
            }
        }
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        if (isLocalMediaPlay) {
            return (int) (mPlayerService.getBufferProgress() * 100);
        }
        if (isInitialized())
            return (int) (mPlayerService.getBufferProgress() * 100);
        return 0;
    }

    @Override
    public void previous() {

    }

    @Override
    public void next() {

    }

    @Override
    public long goForward() {
        return 0;
    }

    @Override
    public long goBack() {
        return 0;
    }

    @Override
    public void toggleVideoMode(int mode) {
        mVideoMode = mode;
        setVideoLayout();
    }

    @Override
    public void removeLoadingView() {

    }

    @Override
    public void switchEpisode(String url, int pos) {
        if (url.isEmpty()) {
            App.showToast(getString(R.string.sorry_for_out_of_data_video_url));
            return;
        }
        isHistory = false;
        stopPlayer();
        mPos = pos;
        mVideoUrl = url;
//        if (App.getMyDownloadManager().getStatusByHtmlUrl(mVideoUrl) == TaskStates.END) {
        if (checkDownloadStatusFromProvider(mVideoUrl) == TaskStates.END) {
            L.d(TAG, "......switchEpisode------>本地缓存:" + mVideoUrl + "......");
            isCache = true;
            setVideoLoadingLayoutVisibility(View.GONE);
            requestVideoUrlFromLocalSuccess(mVideoUrl);
        } else {
            L.d(TAG, "......switchEpisode------>网络下载:" + mVideoUrl + "......");
            isCache = false;
            mVideoLoadingText.setText(getString(R.string.video_layout_before_loading));
            setVideoLoadingLayoutVisibility(View.VISIBLE);
            mVideoUrlNet.getVideoPath(mChannel, mSource, mVideoUrl);
        }
    }

    @Override
    public void exitPlayer() {
        savePlayHistory();
        myFinish();
    }

    @Override
    public void stop(long pressBackTime) {
        if (pressBackTime < 2000) {
            savePlayHistory();
            myFinish();
        } else {
            App.showToast(getString(R.string.press_again_and_exit));
        }
    }

    /**
     * 保存播放历史，返回至“视频详情页面”
     */
    private void savePlayHistory() {
        Bundle b = new Bundle();
        b.putString("index", (mPos + 1) + "");
        b.putString("func_video_url", mVideoUrl);
        b.putLong("currentPlayPos", getCurrentPosition());
        Intent intent = new Intent();
        intent.putExtras(b);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void replay() {
        setVideoLoadingLayoutVisibility(View.VISIBLE);

        mOpened.set(false);
        mUri = Uri.parse(mUrlList.get(mPlayIndex).getUrl());
        vPlayerHandler.sendEmptyMessage(OPEN_FILE);
    }

    @Override
    public void onSurfaceCreated(SurfaceHolder holder) {
        // 先走的Surface的回调，然后再绑定服务
        L.d(TAG, "onSurfaceCreated.." + isServiceConnected);
        isSurfaceCreated = true;
        if (isServiceConnected) {
            vPlayerHandler.sendEmptyMessage(OPEN_FILE);
        }
        if (mPlayerService != null)
            mPlayerService.setDisplay(holder);
    }

    // 视频画面改变时调用
    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        L.d(TAG, "onSurfaceChanged");
        if (mPlayerService != null)
            setVideoLayout();
    }

    @Override
    public void onSurfaceDestroyed(SurfaceHolder holder) {
        L.d(TAG, "onSurfaceDestroyed...");
        if (mPlayerService != null && mPlayerService.isInitialized()) {
            if (mPlayerService.isPlaying()) {
                mPlayerService.stop();
                mPlayerService.setState(PlayerService.STATE_NEED_RESUME);
            }
            mPlayerService.releaseSurface();
            if (mPlayerService.needResume())
                mPlayerService.start();
            isSurfaceCreated = false;
        }
    }

    private List<ChildVideoBean> mUrlList;
    private ParentVideoBean mParentVideo;
    /**
     * 当前播放到第几段地址
     */
    public int mPlayIndex;
    /**
     * 一共有多少段
     */
    public int mTotalIndex;


    @Override
    public void requestVideoUrlSuccess(ParentVideoBean parentVideo) {
        L.d(TAG, "requestVideoUrlSuccess --> ");
        handleVideoBeanAndPlay(parentVideo);
    }

    private static int tryTimes = 0;

    @Override
    public void requestVideoUrlFailure() {
        if (mMediaController != null) {
            mMediaController.release();
        }
        // 请求视频地址失败后重新请求
        tryWhenOpenFileOrRequestVideoUrlFail();
    }


    /**
     * 该视频存在缓存
     */
    private void requestVideoUrlFromLocalSuccess(String videoUrl) {
        L.d(TAG, "............requestVideoUrlFromLocalSuccess : " + videoUrl);
        openSnappyDB();
        ParentVideoForSnappyDBBean bean = getSnappyParentVideoBean(videoUrl);
        closeSnappDB();
        handleVideoBeanAndPlay(
                new ParentVideoBean(
                        bean.getTitle(),
                        bean.getSite(),
                        bean.getQuality(),
                        bean.getFormat(),
                        Arrays.asList(bean.files)
                )
        );
    }


    private void tryWhenOpenFileOrRequestVideoUrlFail() {
        L.d(TAG, "......tryWhenOpenFileOrRequestVideoUrlFail......");
        if (isEpisode) {// 是剧集
            if (checkDownloadStatusFromProvider(mVideoUrl) == TaskStates.END) {
                setVideoLoadingLayoutVisibility(View.GONE);
                requestVideoUrlFromLocalSuccess(mVideoUrl);
            } else {
                setVideoLoadingLayoutVisibility(View.VISIBLE);
                mVideoUrlNet.getVideoPath(mChannel, mSource, mVideoUrl);
            }
        } else {// 是电影
            if (isCache) { // 有缓存
                setVideoLoadingLayoutVisibility(View.GONE);
                requestVideoUrlFromLocalSuccess(mVideoUrl);
            } else {// 无缓存
                setVideoLoadingLayoutVisibility(View.VISIBLE);
                mVideoUrlNet.getVideoPath(mChannel, mSource, mVideoUrl);
            }
        }
    }


    private ParentVideoForSnappyDBBean getSnappyParentVideoBean(String videoUrl) {
        ParentVideoForSnappyDBBean bean = null;
        try {
            bean = mSnappyDb.getObject(videoUrl, ParentVideoForSnappyDBBean.class);
        } catch (SnappydbException e) {
            L.d(TAG, "......getSnappyParentVideoBean......SnappydbException : " + e);
            e.printStackTrace();
        }
        L.d(TAG, "......bean : " + bean);
        return bean;
    }


    private void openSnappyDB() {
        try {
            mSnappyDb = DBFactory.open(this);
        } catch (SnappydbException e) {
            L.d(TAG, "......openSnappyDB......SnappydbException : " + e);
            e.printStackTrace();
        }
    }


    private void closeSnappDB() {
        try {
            mSnappyDb.close();
        } catch (SnappydbException e) {
            L.d(TAG, "......closeSnappDB......SnappydbException : " + e);
            e.printStackTrace();
        }
    }


    /**
     * 从provider中获取下载状态
     *
     * @param videoUrl
     * @return
     */
    private int checkDownloadStatusFromProvider(String videoUrl) {
        Uri uri = Uri.parse("content://com.songshu.squirrelvideo.db.download");
        Cursor cursor = getContentResolver().query(
                uri,
                null,
                "videoChannel=? and videoHtmlUrl=?",
                new String[]{mChannel, videoUrl},
                null
        );
        while (cursor.moveToNext()) {
            int videoDownloadState = cursor.getInt(cursor.getColumnIndex("videoDownloadState"));
            cursor.close();
            L.d(TAG, "............checkDownloadStatusFromProvider.........." + videoDownloadState);
            return videoDownloadState;
        }
        cursor.close();
        L.d(TAG, "............checkDownloadStatusFromProvider.........." + TaskStates.NOINCLUDE);
        return TaskStates.NOINCLUDE;
    }


    /**
     * 获取真实播放地址后的处理
     *
     * @param parentVideo
     */
    private void handleVideoBeanAndPlay(ParentVideoBean parentVideo) {
        mParentVideo = parentVideo;
        mUrlList = mParentVideo.getList();
        mTotalIndex = mUrlList.size() - 1;
        mPlayIndex = 0;
        currentPos = 0l;

        if (isHistory) { // 有播放历史记录时
            long time = 0;
            for (int i = 0; i < mUrlList.size(); i++) {
                // 转换为毫秒
                time += mUrlList.get(i).getSecond() * 1000;
                if (time > lastPosition) {
                    currentPos = time - lastPosition;
                    mPlayIndex = i;
                    break;
                }
            }
        }

        mOpened.set(false);
        mUri = Uri.parse(mUrlList.get(mPlayIndex).getUrl());
        vPlayerHandler.sendEmptyMessage(OPEN_FILE);
    }


    /**
     * 空方法
     *
     * @param e
     */
    public void onEventMainThread(AppEvent.NothingHappen e) {
        L.d(TAG, "... onEvent --> NothingHappen ...");
    }


    /**
     * 判读是否是同一个入口广播
     */
    public class DifferentEntranceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            myFinish();
        }
    }


    /**
     * 用户点击小白点的广播接受
     */
    public class UserPressLittleWhitePot extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            L.d(TAG, "......UserPressLittleWhitePot......intent : " + intent);
            savePlayHistory();
            myFinish();
        }
    }


    public static final String BROAD_CAST_START_VIDEO_TELEPHONY = "com.songshu.videotelephony_start";
    public static final String BROAD_CAST_FINISH_VIDEO_TELEPHONY = "com.songshu.videotelephony_finish";

    public class VideoCallEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            L.d(TAG, "......VideoCallEventReceiver......action : " + intent.getAction());
            if (TextUtils.isEmpty(intent.getAction())) return;
            if (BROAD_CAST_START_VIDEO_TELEPHONY.equals(intent.getAction())) {
                if (isInitialized()) mPlayerService.stop();
            } else if (BROAD_CAST_FINISH_VIDEO_TELEPHONY.equals(intent.getAction())) {

            }
        }
    }


    private static boolean isLocalMediaPlay = false;
    private static String localMediaTitle;

    /**
     * 播放本地视频
     */
    private void playLocalVideo(String video_url, String video_title) {
        L.d(TAG, "......playLocalVideo......" + video_url);
        isLocalMediaPlay = true;
        localMediaTitle = video_title;
        isEpisode = false;
        mTotalIndex = 1;
        mPlayIndex = 0;
        currentPos = 0l;
        mOpened.set(false);
        mUri = Uri.parse(video_url);
        vPlayerHandler.sendEmptyMessage(OPEN_FILE);
    }
}