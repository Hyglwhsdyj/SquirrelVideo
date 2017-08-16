package com.songshu.squirrelvideo.player;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.adapter.EpisodeGridViewAdapter;
import com.songshu.squirrelvideo.entity.ChildVideoBean;
import com.songshu.squirrelvideo.entity.PlayTeleplayBean;
import com.songshu.squirrelvideo.player.CommonGestures.TouchListener;
import com.songshu.squirrelvideo.utils.L;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.List;

public class MediaController extends FrameLayout {

    private static final String TAG = MediaController.class.getSimpleName();

    private MediaPlayerControlListener mPlayer;
    private Activity mContext;
    private static Context mCtx;
    private PopupWindow mWindow;
    private View mAnchor;
    private View mRoot;
    private View mMediaController;
    // 顶部媒体控制条
    private ImageButton mBackBtn;
    private TextView mFileName;
    private TextView mEpisode;
    // 选集
    private View mEpisodeView;
    private GridView mGridView;
    // 底部媒体控制条
    private ImageButton mPauseBtn;
    private SeekBar mProgress;
    private TextView mTime;
    private TextView mTime_left;

    private long mDuration;//记录视频的总时间
    private TextView mOperationInfo;//操作提示信息

    private Handler mHandler;
    private AudioManager mAM;
    private int mMaxVolume;
    private int mVolume;
    private float mBrightness = 0.01f;

    // 手势
    private CommonGestures mGestures;
    private int mVideoMode = VideoView.VIDEO_LAYOUT_SCALE;
    private int mSpeed = 0;
    private long toposition = -1l;//记录手势滑动的距离
    private boolean istoanotherposition = false;

    private boolean mShowing;
    private boolean mDragging;
    private boolean mInstantSeeking = true;

    private static final int DEFAULT_TIME_OUT = 500;
    private static final int DEFAULT_AUTO_TIME_OUT = 2850;
    private static final int DEFAULT_LONG_TIME_SHOW = 120000;
    private static final int DEFAULT_SEEKBAR_VALUE = 1000;

    private int mCurrentVolume;//当前音量
    private int mCurrentBright = 50;//当前亮度

    // 拖动进度条后的数据
    private static int lastPosition = 0;//用于记录上一次用户手动拖拽的位置单位
    private static long lastPositionTime = 0l;//用于记录上一次用户手动拖拽的位置的时间单位
    private static long lastPositionDuration = 0l;//用于记录上一次用户手动拖拽的位置的时间单位

    private List<ChildVideoBean> mVideoList;

    // 顶部媒体操作栏
    public MediaController(Context context) {
        super(context);
        mContext = (Activity) context;
        mCtx = context;
        initFloatingWindow();
        initResources();
    }

    public MediaController(Context context, List<ChildVideoBean> videoList) {
        this(context);
        mVideoList = videoList;
    }

    private void initFloatingWindow() {
        mWindow = new PopupWindow(mContext);
        mWindow.setFocusable(true);
        mWindow.setBackgroundDrawable(null);
        mWindow.setOutsideTouchable(true);
    }

    @TargetApi(11)
    public void setWindowLayoutType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                Method setWindowLayoutType = PopupWindow.class.getMethod("setWindowLayoutType", new Class[]{int.class});
                setWindowLayoutType.invoke(mWindow, WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);
            } catch (Exception e) {
            }
        }
    }

    private void initResources() {
        mHandler = new MediaControllerHandler(this);
        mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mCurrentVolume = (int) ((float) mAM.getStreamVolume(AudioManager.STREAM_MUSIC) / mMaxVolume * 100);
        L.d(TAG, "当前音量=" + mCurrentVolume);
        mGestures = new CommonGestures(mContext);
        mGestures.setTouchListener(mTouchListener, true);

        removeAllViews();

        mRoot = inflateLayout();
        mWindow.setContentView(mRoot);
        mWindow.setWidth(android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        findViewItems(mRoot);
        mRoot.setSystemUiVisibility(View.INVISIBLE);
    }

    private View inflateLayout() {
        return View.inflate(mContext, R.layout.view_media_controller, this);
    }

    private void findViewItems(View v) {
        mMediaController = v.findViewById(R.id.new_mediacontroller_container_layout);

        mBackBtn = (ImageButton) v.findViewById(R.id.new_mediacontroller_back_btn);
        mFileName = (TextView) v.findViewById(R.id.new_mediacontroller_video_title_tv);
        mEpisode = (TextView) v.findViewById(R.id.new_mediacontroller_episode_tv);

        mPauseBtn = (ImageButton) v.findViewById(R.id.new_mediacontroller_pause_start_btn);

        mProgress = (SeekBar) v.findViewById(R.id.new_mediacontroller_seekbar);
        mProgress.setOnSeekBarChangeListener(mSeekListener);
        mProgress.setMax(DEFAULT_SEEKBAR_VALUE);
        mTime = (TextView) v.findViewById(R.id.new_mediacontroller_video_time_tv);
        mTime_left = (TextView) v.findViewById(R.id.new_mediacontroller_video_time_left_tv);

        mEpisodeView = v.findViewById(R.id.new_mediacontroller_episode_layout);
        mGridView = (GridView) v.findViewById(R.id.new_mediacontroller_episode_listview);

        mOperationInfo = (TextView) v.findViewById(R.id.new_mediacontroller_operation_info);
        // 绑定单击事件
        mBackBtn.setOnClickListener(mBackListener);
        mPauseBtn.setOnClickListener(mPauseListener);
        mEpisode.setOnClickListener(mEpisodeListener);

    }

    //---------------------------------------外部设置------------------------------------------------

    public void setMediaPlayer(MediaPlayerControlListener player) {
        mPlayer = player;
        updatePausePlay();
    }

    public void setAnchorView(View view) {
        mAnchor = view;
        int[] location = new int[2];
        mAnchor.getLocationOnScreen(location);
        mAnchor.getLocationOnScreen(location);
        Rect anchorRect = new Rect(location[0], location[1], location[0] + mAnchor.getWidth(), location[1] + mAnchor.getHeight());
        setWindowLayoutType();
        mWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY, anchorRect.left, anchorRect.bottom);
    }

    public void release() {
        if (mWindow != null) {
            mWindow.dismiss();
            mWindow = null;
        }
    }

    public void setFileName(String name) {
        mFileName.setText(name);
    }

    /**
     * 隐藏选集按钮
     */
    public void setHideEpisodeBtn() {
        mEpisode.setVisibility(View.GONE);
    }

    public void setEpisodeValue(final List<PlayTeleplayBean> list, String channel, int pos) {
        final EpisodeGridViewAdapter mAdapter = new EpisodeGridViewAdapter(mContext, list);
        mGridView.setAdapter(mAdapter);
        mAdapter.setSelectItem(pos);
        mAdapter.notifyDataSetChanged();
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int positon, long id) {
                mAdapter.setSelectItem(positon);
                mAdapter.notifyDataSetChanged();
                String url = null;
                PlayTeleplayBean teleplayBean = (PlayTeleplayBean) list.get(positon);
                url = teleplayBean.videoUrl;
                mPlayer.switchEpisode(url, positon);
            }
        });
    }

    //---------------------------------------基本的listener------------------------------------------------

    // 暂停、播放
    private OnClickListener mPauseListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPlayer.isPlaying())
                show(DEFAULT_LONG_TIME_SHOW);
            else
                show(DEFAULT_AUTO_TIME_OUT);
            doPauseResume();
        }
    };

    // 退出播放器
    private OnClickListener mBackListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPlayer != null)
                mPlayer.exitPlayer();
        }
    };

    private boolean isShowEpisode;
    // 选集
    private OnClickListener mEpisodeListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!isShowEpisode) {
                mHandler.removeMessages(MSG_FADE_OUT);
                mEpisode.setTextColor(mContext.getResources().getColor(R.color.yellow));
                mEpisodeView.setVisibility(View.VISIBLE);
                isShowEpisode = true;
            } else {
                isShowEpisode = false;
                mEpisode.setTextColor(mContext.getResources().getColor(R.color.white));
                mEpisodeView.setVisibility(View.GONE);
                mHandler.removeMessages(MSG_FADE_OUT);
                mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_FADE_OUT), DEFAULT_TIME_OUT);
            }
        }
    };


    // 播放进度条
    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        private boolean wasStopped = false;
        long mVideo_current_length;

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mDragging = true;
            show(DEFAULT_LONG_TIME_SHOW);
            mVideo_current_length = mPlayer.getCurrentPosition() / 10001;
            mHandler.removeMessages(MSG_SHOW_PROGRESS);
            wasStopped = !mPlayer.isPlaying();
            if (mInstantSeeking) {
                mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
                if (wasStopped) {
                    mPlayer.start();
                }
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser)
                return;
            mHandler.removeMessages(MSG_SHOW_PROGRESS);
            long newposition = (mDuration * progress) / 1000;
            String time = length2time(newposition);
//            setOperationInfo(time + "/" + length2time(mDuration), 0)
            mTime_left.setText(time);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mInstantSeeking) {
                mPlayer.seekTo((mDuration * seekBar.getProgress()) / 1000);
                //拖拽后及时设置进度条,在视频加载成功后才开始更新进度条,没成功前使用这里设置的进度值
                lastPositionTime = (mDuration * seekBar.getProgress()) / 1000;
                lastPositionDuration = mPlayer.getDuration();
                if (lastPositionDuration > 0) {
                    long pos = 1000L * lastPositionTime / lastPositionDuration;
                    mProgress.setProgress((int) pos);
                    lastPosition = (int) pos;
                }
            } else if (wasStopped) {
                mPlayer.pause();
            }
            mOperationInfo.setVisibility(View.INVISIBLE);
            show(100);
            mHandler.removeMessages(MSG_SHOW_PROGRESS);
            mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mDragging = false;
        }

    };

    //--------------------------------------事件分发-------------------------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestures.onTouchEvent(event) || super.onTouchEvent(event);
    }

    private long startTime;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int keyMode = event.getAction();

        if (keyMode == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_MUTE:
                    return super.dispatchKeyEvent(event);
                case KeyEvent.KEYCODE_VOLUME_UP:
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
                    int step = keyCode == KeyEvent.KEYCODE_VOLUME_UP ? 1 : -1;
                    setVolume(mVolume + step);
                    mHandler.removeMessages(MSG_HIDE_OPERATION_VOLLUM);
                    mHandler.sendEmptyMessageDelayed(MSG_HIDE_OPERATION_VOLLUM, 500);
                    return true;
            }

            if (event.getRepeatCount() == 0 && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
                doPauseResume();
                show(DEFAULT_TIME_OUT);
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                    updatePausePlay();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                long stopTime = System.currentTimeMillis();
                long currentTime = stopTime - startTime;
                if (currentTime < 2000) {
                    release();
                    mPlayer.stop(currentTime);
                } else {
                    startTime = stopTime;
                    mPlayer.stop(currentTime);
                }
                return true;
            } else {
                show(DEFAULT_TIME_OUT);
            }
            return super.dispatchKeyEvent(event);

        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    private TouchListener mTouchListener = new TouchListener() {
        long mVideo_current_length;
        String total_length;

        @Override
        public void onGestureBegin() {
            mBrightness = mContext.getWindow().getAttributes().screenBrightness;
            mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;
            if (mVolume < 0)
                mVolume = 0;
            initDuration();
            mSpeed = 0;
            toposition = -1l;
            istoanotherposition = false;
            total_length = length2time(mDuration);
            // 当前播放长度
            mVideo_current_length = mPlayer.getCurrentPosition();
        }

        @Override
        public void onGestureEnd() {
            if (istoanotherposition) {
                istoanotherposition = false;
                mPlayer.seekTo(toposition);
            }
        }

        @Override
        public void onSingleTap() {
            if (mShowing)
                hide();
            else
                show(DEFAULT_AUTO_TIME_OUT);
        }

        @Override
        public void onDoubleTap() {
        }

        @Override
        public void onRightSlide(float percent) {
            int v = (int) (percent * mMaxVolume) + mVolume;
            setVolume(v);
        }

        @Override
        public void onLeftSlide(float percent) {
            setBrightness(mBrightness + percent);
        }

        @Override
        public void onVideoSpeed(float distanceX) {
            hide();
            if (distanceX > 0) { // 往左滑动 --
                --mSpeed;
            } else if (distanceX < 0) { // 往右滑动  ++
                ++mSpeed;
            }
            // 快进长度
            int i = mSpeed * 1000;
            // 快进之后的长度
            long mVideo_start_length = mVideo_current_length + i;
            if (mVideo_start_length >= mDuration) {
                mVideo_start_length = mDuration;
            } else if (mVideo_start_length <= 0) {
                mVideo_start_length = 0L;
            }
            istoanotherposition = true;
            toposition = mVideo_start_length;
            String start_length = length2time(mVideo_start_length);
            setOperationInfo(start_length + "/" + total_length, 0);
        }

        @Override
        public void onLongPress() {
            // 长按暂时没用到
        }

    };

    //---------------------------------------Handler----------------------------------------------------

    private static final int MSG_FADE_OUT = 1;
    private static final int MSG_SHOW_PROGRESS = 2;
    private static final int MSG_HIDE_SYSTEM_UI = 3;
    private static final int MSG_HIDE_OPERATION_INFO = 5;
    private static final int MSG_HIDE_OPERATION_VOLLUM = 6;

    private static class MediaControllerHandler extends Handler {
        private WeakReference<MediaController> mc;

        public MediaControllerHandler(MediaController mc) {
            this.mc = new WeakReference<MediaController>(mc);
        }

        @Override
        public void handleMessage(Message msg) {
            MediaController c = mc.get();
            if (c == null) return;

            switch (msg.what) {
                case MSG_FADE_OUT:
                    c.hide();
                    break;
                case MSG_SHOW_PROGRESS:
                    c.setProgress();
                    if (!c.mDragging && c.mShowing) {
                        msg = obtainMessage(MSG_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 500);
                        c.updatePausePlay();
                    }
                    break;
                case MSG_HIDE_SYSTEM_UI:
                    break;
                case MSG_HIDE_OPERATION_INFO:
                    c.mOperationInfo.setVisibility(View.INVISIBLE);
                    break;
                case MSG_HIDE_OPERATION_VOLLUM:
                    break;
            }
        }
    }

    //--------------------------------------内部调用逻辑-------------------------------------------------

    private void show(int timeout) {
        if (timeout != 0) {
            mHandler.removeMessages(MSG_FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_FADE_OUT), timeout);
        }
        if (!mShowing) {
            mHandler.removeMessages(MSG_HIDE_SYSTEM_UI);
            if (PlayVideoActivity.isBufferComplete) {//缓冲完毕才进行自动更新操作
                mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS);
            } else {// 没缓冲完毕前进行部分数据的更新
                mProgress.setProgress(lastPosition);
                mTime.setText(length2time(lastPositionDuration));
                mTime_left.setText(length2time(lastPositionTime));
            }
            showSystemUi(true);
            fullScreen(false);
            mPauseBtn.requestFocus();

            mMediaController.setVisibility(View.VISIBLE);
            ObjectAnimator anim = ObjectAnimator.ofFloat(mMediaController, "alpha", 0F, 1F).setDuration(500);
            anim.start();

            updatePausePlay();
            mShowing = true;
        }

    }

    private void hide() {
        if (mShowing) {
            fullScreen(true);
            ObjectAnimator anim = ObjectAnimator.ofFloat(mMediaController, "alpha", 1F, 0F).setDuration(200);
            anim.start();
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    L.d(TAG, "............onAnimationEnd...........");
                    mMediaController.setVisibility(View.GONE);
                    mHandler.removeMessages(MSG_HIDE_SYSTEM_UI);
                    mHandler.sendEmptyMessageDelayed(MSG_HIDE_SYSTEM_UI, DEFAULT_TIME_OUT);
                    resumeBtnInitStatus();
                    mShowing = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }


    private void setOperationInfo(String info, long time) {
        mOperationInfo.setText(info);
        mOperationInfo.setVisibility(View.VISIBLE);
        mHandler.removeMessages(MSG_HIDE_OPERATION_INFO);
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_OPERATION_INFO, time);
    }

    @TargetApi(11)
    private void showSystemUi(boolean visible) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            int flag = visible ? 0 : View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LOW_PROFILE;
            mRoot.setSystemUiVisibility(flag);
        }
    }

    private void updatePausePlay() {
        if (mPlayer.isPlaying())
            mPauseBtn.setImageResource(R.drawable.media_controller_pause_selector);
        else
            mPauseBtn.setImageResource(R.drawable.media_controller_play_selector);
    }

    private void doPauseResume() {
        if (mPlayer.isPlaying())
            mPlayer.pause();
        else
            mPlayer.start();
        updatePausePlay();
    }


    /**
     * 是否全屏,true全屏，去掉标题栏，false显示标题栏
     *
     * @param enable
     */
    protected void fullScreen(boolean enable) {
        if (enable) {
            mRoot.setSystemUiVisibility(View.INVISIBLE);
        } else {
            mRoot.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    private long setProgress() {
        if (mPlayer == null || mDragging)
            return 0;

        long position = mPlayer.getCurrentPosition();
        long duration = mPlayer.getDuration();
        if (duration > 0) {
            long pos = 1000L * position / duration;
            mProgress.setProgress((int) pos);
        }

        mDuration = duration;
        mTime_left.setText(length2time(position));
        mTime.setText(length2time(duration));

        return position;
    }

    public void initDuration() {
        if (mPlayer != null && mDuration <= 0l) {
            mDuration = mPlayer.getDuration();
        }
    }

    /**
     * 将进度长度转变为进度时间
     */
    private String length2time(long length) {
        int totalSeconds = (int) (length / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * 设置音量
     *
     * @param v
     */
    private void setVolume(int v) {
        hide();
        if (v > mMaxVolume)
            v = mMaxVolume;
        else if (v < 0)
            v = 0;
        mAM.setStreamVolume(AudioManager.STREAM_MUSIC, v, 0);
        mCurrentVolume = (int) (100 * v / mMaxVolume);
        if (mCurrentVolume <= 0)
            setOperationInfo(mContext.getString(R.string.media_controller_mute), 500);
        else
            setOperationInfo(mContext.getString(R.string.media_controller_volume) + String.valueOf(mCurrentVolume) + "%", 500);
    }

    /**
     * 设置亮度
     *
     * @param f
     */
    private void setBrightness(float f) {
        hide();
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.screenBrightness = f;
        if (lp.screenBrightness > 1.0f)
            lp.screenBrightness = 1.0f;
        else if (lp.screenBrightness < 0.01f)
            lp.screenBrightness = 0.01f;
        mContext.getWindow().setAttributes(lp);
        mCurrentBright = (int) ((lp.screenBrightness - 0.01f) / 0.99f * 100);
        //mBrightVolumeProgress.setProgressAndThumb(mCurrentBright);
        setOperationInfo(mContext.getString(R.string.media_controller_brightness) + String.valueOf(mCurrentBright) + "%", 500);
    }

    /**
     * 恢复按钮的初始状态
     */
    private void resumeBtnInitStatus() {
        isShowEpisode = false;
        mEpisode.setTextColor(mContext.getResources().getColor(R.color.white));
        mEpisodeView.setVisibility(View.GONE);

    }


    //-----------------------------------------------------------------------------------------------

    public interface MediaPlayerControlListener {

        void start();

        void pause();

        void stop(long pressBackTime);

        void seekTo(long pos);

        void setVideoQuality(int quality);

        boolean isPlaying();

        long getDuration();

        long getCurrentPosition();

        int getBufferPercentage();

        void previous();

        void next();

        long goForward();

        long goBack();

        void toggleVideoMode(int mode);

        void removeLoadingView();

        void exitPlayer();

        void switchEpisode(String url, int pos);//切换剧集

        void replay();//重播
    }

}
