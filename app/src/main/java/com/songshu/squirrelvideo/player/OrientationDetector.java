package com.songshu.squirrelvideo.player;

import android.content.Context;
import android.view.OrientationEventListener;

/**
 * 因为Activity中onConfigurationChanged方法，
 * 只能检测到垂直方向切换至水平方向的改变，无法检测到水平方向切换至水平方向的改变（注：180度的改变），
 * 所以用OrientationEventListener来监听，屏幕方向的改变，目前只监听水平方向的改变。
 */
public class OrientationDetector extends OrientationEventListener{

	public interface ScreenOrientationListener {
		void onChangeScreenOrientation(int orientation);
	}

	private ScreenOrientationListener mListener;

	private boolean mIsRotate1;
	private boolean mIsRotate2;

	public OrientationDetector(Context context) {
		super(context);
	}

	public void setmListener(ScreenOrientationListener mListener) {
		this.mListener = mListener;
	}

	@Override
	public void onOrientationChanged(int orientation) {
		// 手机平放时，检测不到有效角度
		if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) return;

		// 只检测4个角度的改变（0度、90度、180度、270度）
		if (orientation > 350 || orientation < 10) {
			orientation = 0;
		} else if (orientation > 80 && orientation < 100 && !mIsRotate1) {
			mIsRotate1 = true;
			mIsRotate2 = false;

			orientation = 90;
			mListener.onChangeScreenOrientation(orientation);

		} else if (orientation > 170 && orientation < 190) {
			orientation = 180;

		} else if (orientation > 260 && orientation < 280 && !mIsRotate2) {
			mIsRotate2 = true;
			mIsRotate1 = false;

			orientation = 270;
			mListener.onChangeScreenOrientation(orientation);
		} else {
			return;
		}
	}

}
