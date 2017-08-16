package com.songshu.squirrelvideo.player;

import android.app.Activity;
import android.support.v4.view.GestureDetectorCompat;
import android.view.Display;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class CommonGestures {

    private boolean mGestureEnabled;

    private int mode = 0;
    private boolean firstScroll = true;
    private GestureDetectorCompat mDoubleTapGestureDetector;
    private GestureDetectorCompat mTapGestureDetector;

    private Activity mContext;

    public CommonGestures(Activity ctx) {
        mContext = ctx;
        mDoubleTapGestureDetector = new GestureDetectorCompat(mContext, new DoubleTapGestureListener());
        mTapGestureDetector = new GestureDetectorCompat(mContext, new TapGestureListener());
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (mListener == null)
            return false;

        if (mTapGestureDetector.onTouchEvent(event))
            return true;

        if (mDoubleTapGestureDetector.onTouchEvent(event))
            return true;

		/*if (event.getPointerCount() > 1) {
            return true;
		}*/
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                firstScroll = true;
                mode = 0;
                mListener.onGestureEnd();
                break;
        }
        return false;
    }

    private class TapGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mListener != null)
                mListener.onSingleTap();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (mListener != null && mGestureEnabled)
                mListener.onLongPress();
        }
    }

    private class DoubleTapGestureListener extends SimpleOnGestureListener {
        private boolean mDown = false;

        @Override
        public boolean onDown(MotionEvent e) {
            mDown = true;
            return super.onDown(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mListener != null && mGestureEnabled)
                mListener.onDoubleTap();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mListener != null && mGestureEnabled && e1 != null && e2 != null) {
                if (mDown) {
                    mListener.onGestureBegin();
                    mDown = false;
                }
                float mOldX = e1.getX(), mOldY = e1.getY();
                Display disp = mContext.getWindowManager().getDefaultDisplay();
                int windowWidth = disp.getWidth();
                int windowHeight = disp.getHeight();
                if (firstScroll) {
                    if (Math.abs(e2.getY(0) - mOldY) * 2 > Math.abs(e2.getX(0) - mOldX)) {
						/*if (mOldX > windowWidth * 2.75 / 5) {
							mode=1;
						} else if (mOldX < windowWidth*2.25 / 5.0) {
							mode=2;
						}*/
                        if (mOldX > windowWidth / 2) {
                            mode = 1;
                        } else if (mOldX < windowWidth / 2) {
                            mode = 2;
                        }
                    } else {
                        mode = 3;
                    }
                    firstScroll = false;
                }
                if (mode == 1) {// 在屏幕的右边滑动
                    mListener.onRightSlide((mOldY - e2.getY(0)) / windowHeight);
                } else if (mode == 2) {// 在屏幕的左边滑动
                    mListener.onLeftSlide((mOldY - e2.getY(0)) / windowHeight);
                } else if (mode == 3) {// 在x轴上滑动
                    mListener.onVideoSpeed(distanceX);
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    public void setTouchListener(TouchListener listener, boolean enable) {
        mListener = listener;
        mGestureEnabled = enable;
    }

    private TouchListener mListener;

    public interface TouchListener {
        void onGestureBegin();

        void onGestureEnd();

        void onLeftSlide(float percent);

        void onRightSlide(float percent);

        void onVideoSpeed(float distanceX);

        void onSingleTap();

        void onDoubleTap();

        void onLongPress();
    }
}
