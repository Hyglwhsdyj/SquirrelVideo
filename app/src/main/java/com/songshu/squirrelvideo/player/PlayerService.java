package com.songshu.squirrelvideo.player;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.SurfaceHolder;

import com.songshu.squirrelvideo.utils.L;

import java.io.IOException;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnHWRenderFailedListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import io.vov.vitamio.Vitamio;

/**
 * @Description: 播放器的后台服务
 * @author: ysx
 * @date: 2014年9月10日 下午4:10:00
 */
public class PlayerService extends Service implements
        OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener,
        OnVideoSizeChangedListener, OnErrorListener, OnInfoListener,
        OnSeekCompleteListener {

    private static final String TAG = PlayerService.class.getSimpleName() + ":";

    private final IBinder mBinder = new LocalBinder();
    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mPlayer;
    private PlayerListener mListener;
    private Uri mUri;
    /**
     * 视频观看位置
     */
    //private float mSeekTo = -1f;
    private long mSeekTo = -1;

    /**
     * mediaplayer是否已经初始化，true为初始化，false为反
     */
    private boolean isInitialized;
    private boolean isPrepared;
    private boolean isVideoSizeKnown;

    /**
     * 监听手机状态
     */
    private TelephonyManager mTelephonyManager;

    private int mCurrentState;
    public static final int STATE_PREPARED = -1;
    public static final int STATE_PLAYING = 0;
    public static final int STATE_NEED_RESUME = 1;
    public static final int STATE_STOPED = 2;
    public static final int STATE_RINGING = 3;

    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        L.d(TAG, "BIND OK:" + intent.getPackage());
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isInitialized = false;
        // 获取电话服务
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // 注册对PhoneStateListener中的LISTEN_CALL_STATE状态监听
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        L.d(TAG, "onCreate...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Vitamio.isInitialized(this)) {
            initPlayer(intent.getBooleanExtra("isHWCodec", false));
        } else {
            stopSelf();
        }
        L.d(TAG, "CREATE OK");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release(true);
        releaseContext();
        L.d(TAG, "onDestory");
    }

    public void setListener(PlayerListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 初始播放器、注册播放器的回调事件
     *
     * @param isHWCodec true为硬解，false为软解
     */
    private void initPlayer(boolean isHWCodec) {
        mPlayer = new MediaPlayer(getApplicationContext(), isHWCodec);
        mPlayer.setOnHWRenderFailedListener(new OnHWRenderFailedListener() {
            @Override
            public void onFailed() {
                if (mListener != null)
                    mListener.onHWRenderFailed();
            }
        });
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnBufferingUpdateListener(this);
        mPlayer.setOnInfoListener(this);
        mPlayer.setOnVideoSizeChangedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
    }

    /**
     * @param videoPath 视频路径
     * @param startPos  视频播放位置
     * @param listener  监听回调
     * @param isHWCodec 是否开启硬解
     * @return
     */
    public boolean initialize(Uri videoPath, long startPos, PlayerListener listener, boolean isHWCodec) {
        if (mPlayer == null)
            initPlayer(isHWCodec);
        mListener = listener;
        mUri = videoPath;
        mSeekTo = startPos;
        L.d(TAG, "视频路径=" + mUri + ",已播放的位置=" + mSeekTo);
        mListener.onOpenStart();
        openVideo();
        L.d(TAG, "initialize...");
        return isInitialized;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * 打开视频
     */
    private void openVideo() {
        if (mUri == null || mPlayer == null)
            return;

        mPlayer.reset();
        isInitialized = false;
        isPrepared = false;
        isVideoSizeKnown = false;

        try {
            mPlayer.setScreenOnWhilePlaying(true);
            mPlayer.setDataSource(this, mUri);
            if (mSurfaceHolder != null && mSurfaceHolder.getSurface() != null
                    && mSurfaceHolder.getSurface().isValid())
                mPlayer.setDisplay(mSurfaceHolder);
            mPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开视频成功
     */
    private void openSuccess() {
        isInitialized = true;
        /*if (mSeekTo > 0 && mSeekTo < 1)
            seekTo(mSeekTo);*/
        //if (mSeekTo > 0 && mSeekTo < 1)
        if (mSeekTo > 0) {
            long temp = getDuration() - mSeekTo;
            seekTo(temp);
        }

        mSeekTo = -1;
        mListener.onOpenSuccess();
    }

    public Uri getUri() {
        return mUri;
    }

    public void setState(int state) {
        mCurrentState = state;
    }

    public boolean needResume() {
        return isInitialized
                && (mCurrentState == STATE_NEED_RESUME || mCurrentState == STATE_PREPARED);
    }

    public boolean ringingState() {
        return isInitialized && mCurrentState == STATE_RINGING;
    }

    public void stop() {
        if (isInitialized)
            mPlayer.pause();
    }

    public void start() {
        if (isInitialized) {
            mPlayer.start();
            setState(STATE_PLAYING);
        }
    }

    public void setDisplay(SurfaceHolder surface) {
        mSurfaceHolder = surface;
        if (mPlayer != null)
            mPlayer.setDisplay(surface);
    }

    public boolean isPlaying() {
        return (isInitialized && mPlayer.isPlaying());
    }

    public int getVideoWidth() {
        if (isInitialized)
            return mPlayer.getVideoWidth();
        return 0;
    }

    public int getVideoHeight() {
        if (isInitialized)
            return mPlayer.getVideoHeight();
        return 0;
    }

    public float getVideoAspectRatio() {
        if (isInitialized)
            return mPlayer.getVideoAspectRatio();
        return 0f;
    }

    public long getDuration() {
        if (isInitialized)
            return mPlayer.getDuration();
        return 0;
    }

    public long getCurrentPosition() {
        if (isInitialized)
            return mPlayer.getCurrentPosition();
        return 0;
    }

    public Bitmap getCurrentFrame() {
        if (isInitialized)
            return mPlayer.getCurrentFrame();
        return null;
    }

    public float getBufferProgress() {
        if (isInitialized)
            return mPlayer.getBufferProgress();
        return 0f;
    }

    public void seekTo(float percent) {
        if (isInitialized) {
            float pos = (float) ((double) percent / getDuration());
            //mPlayer.seekTo((int) (percent * getDuration()));
            mPlayer.seekTo((int) (pos * getDuration()));
        }
    }

    public void setAudioTrack(int num) {
        if (isInitialized)
            mPlayer.selectTrack(num);
    }

    public int getAudioTrack() {
        if (isInitialized)
            return mPlayer.getAudioTrack();
        return 0;
    }

    protected boolean isBuffering() {
        return (isInitialized && mPlayer.isBuffering());
    }

    /**
     * 设置缓冲大小，默认为1M
     *
     * @param bufSize
     */
    protected void setBuffer(int bufSize) {
        if (isInitialized)
            mPlayer.setBufferSize(bufSize);
    }

    /**
     * 设置音量
     *
     * @param left
     * @param right
     */
    protected void setVolume(float left, float right) {
        if (isInitialized) {
            if (left <= 0f)
                left = 0f;
            else if (left >= 1f)
                left = 1f;
            if (right <= 0f)
                right = 0f;
            else if (right >= 1f)
                right = 1f;
            mPlayer.setVolume(left, right);
        }
    }

    /**
     * 设置视频画面质量，有三个参数：流畅、普通、高清
     *
     * @param quality
     */
    protected void setVideoQuality(int quality) {
        if (isInitialized)
            mPlayer.setVideoQuality(quality);
    }

    /**
     * 设置视频反交错
     *
     * @param deinterlace
     */
    protected void setDeinterlace(boolean deinterlace) {
        if (isInitialized)
            mPlayer.setDeinterlace(deinterlace);
    }


    public void release() {
        release(true);
    }

    private void release(boolean all) {
        if (mPlayer != null) {
            if (mListener != null)
                mListener.onCloseStart();
            mPlayer.reset();
            isInitialized = false;
            isPrepared = false;
            isVideoSizeKnown = false;
            if (mListener != null)
                mListener.onCloseComplete();
        }
        if (all) {
            mListener = null;
            mUri = null;
        }
    }

    public void releaseContext() {
        if (mPlayer != null)
            mPlayer.release();
        mPlayer = null;
    }

    public void releaseSurface() {
        if (isInitialized)
            mPlayer.releaseDisplay();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        openSuccess();
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mListener != null)
                    mListener.onBufferStart();
                else
                    mPlayer.pause();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (mListener != null)
                    mListener.onBufferComplete();
                else
                    mPlayer.start();
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                if (mListener != null)
                    mListener.onDownloadRateChanged(extra);
                break;
        }
        return true;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mListener.onOpenFailed();
        // 如果返回false的话，会发生调用此方法后，紧接着调用onCompletion方法，所以此处返回值为true
        //return false;
        return true;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        isVideoSizeKnown = true;
        if (mListener != null)
            mListener.onVideoSizeChanged(width, height);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mListener != null) {
            mListener.onPlaybackComplete();
        } else {
            release(true);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
    }

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                // 电话闲置时
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                // 电话挂断时
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                // 电话响起时
                case TelephonyManager.CALL_STATE_RINGING:
                    if (isPlaying()) {
                        stop();
                        setState(STATE_RINGING);
                    }
                    break;
            }
        }

    };

    public interface PlayerListener {
        /**
         * 硬解开启失败
         */
        void onHWRenderFailed();

        /**
         * 视频画面尺寸发生改变
         */
        void onVideoSizeChanged(int width, int height);

        /**
         * 开始打开视频
         */
        void onOpenStart();

        /**
         * 打开视频成功
         */
        void onOpenSuccess();

        /**
         * 打开视频失败
         */
        void onOpenFailed();

        /**
         * 视频缓冲开始
         */
        void onBufferStart();

        /**
         * 视频缓冲结束
         */
        void onBufferComplete();

        /**
         * 监听网速变化
         */
        void onDownloadRateChanged(int kbPerSec);

        /**
         * 视频播放完成
         */
        void onPlaybackComplete();

        /**
         * 开始关闭播放器
         */
        void onCloseStart();

        /**
         * 关闭播放器成功
         */
        void onCloseComplete();
    }

}
