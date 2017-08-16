package com.songshu.squirrelvideo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by yb on 15-7-10.
 */
public class MyHorizontalScrollView extends HorizontalScrollView {

    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHorizontalScrollView(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        /*switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			System.out.println("move..");
			getParent().requestDisallowInterceptTouchEvent(true);
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			System.out.println("cancel..");
			getParent().requestDisallowInterceptTouchEvent(false);
			break;
		}*/

        return super.onInterceptTouchEvent(ev);
    }


}
