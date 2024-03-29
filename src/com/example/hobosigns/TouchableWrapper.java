package com.example.hobosigns;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout {

	public TouchableWrapper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TouchableWrapper(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TouchableWrapper(Context context) {
		super(context);
	}

	private long lastTouched = 0;
	private static final long SCROLL_TIME = 200L; // 200 Milliseconds, but you
													// can adjust that to your
													// liking
	private UpdateMapAfterUserInterection updateMapAfterUserInterection;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastTouched = SystemClock.uptimeMillis();
			break;
		case MotionEvent.ACTION_UP:
			final long now = SystemClock.uptimeMillis();
			if (now - lastTouched > SCROLL_TIME) {
				// Update the map
				if (updateMapAfterUserInterection != null)
					updateMapAfterUserInterection
							.onUpdateMapAfterUserInterection();
			}
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	// Map Activity must implement this interface
	public interface UpdateMapAfterUserInterection {
		public void onUpdateMapAfterUserInterection();
	}

	public void setUpdateMapAfterUserInterection(
			UpdateMapAfterUserInterection mUpdateMapAfterUserInterection) {
		this.updateMapAfterUserInterection = mUpdateMapAfterUserInterection;
	}
}
