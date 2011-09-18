package com.android.pomtimer;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class PomTimerGUI implements PomTimerUI {
	static int ALPHA = 150;
	
	TextView tvTimer;
	ImageView background;
	
	public PomTimerGUI(Context context) {
		Resources res = context.getResources();
		
		tvTimer = new TextView(context);
		tvTimer.setGravity(Gravity.CENTER);
		tvTimer.setTextSize(TypedValue.COMPLEX_UNIT_PX, 
								res.getDimension(R.dimen.textSize) );
		tvTimer.setPadding(0, (int)(res.getDimension(R.dimen.imageDimension) / 3), 0, 0);
		tvTimer.setTextColor(R.color.black);
		
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
	public View getUI(Context context) {
		FrameLayout layout = new FrameLayout(context);
		layout.addView(background);
		layout.addView(tvTimer);
		layout.setId(R.id.timer);
		return layout;
	}
}
