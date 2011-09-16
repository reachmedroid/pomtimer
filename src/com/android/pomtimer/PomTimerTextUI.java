package com.android.pomtimer;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class PomTimerTextUI implements PomTimerUI {
	static int ALPHA = 127;
	static float TEXT_SIZE = 30.0f;
	static int FRAME_DIMENSION = 200;
	
	TextView tvTimer;
	ImageView background;
	
	public PomTimerTextUI(Context context) {
		tvTimer = new TextView(context);
		tvTimer.setGravity(Gravity.CENTER);
		tvTimer.setTextSize(TEXT_SIZE);
		tvTimer.setPadding(0, FRAME_DIMENSION / 4, 0, 0);
		
		background = new ImageView(context);
		background.setImageResource(R.drawable.pombackground);
		background.setScaleType(ScaleType.FIT_XY);
		background.setAlpha(ALPHA);
	}
	
	@Override
	public void updateTime(long millis) {
		String time = PomUtil.formatTime(millis);
		tvTimer.setText(time);
	}

	@Override
	public View getInterface(Context context) {
		FrameLayout layout = new FrameLayout(context);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.
														LayoutParams(FRAME_DIMENSION, 
																		FRAME_DIMENSION, 
																		Gravity.CENTER);
		layout.setLayoutParams(layoutParams);
		layout.addView(background);
		layout.addView(tvTimer);
		layout.setId(R.id.timer);
		return layout;
	}
}
