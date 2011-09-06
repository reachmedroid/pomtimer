package com.android.pomtimer;

import android.widget.TextView;

public class PomTimerTextUI implements PomTimerUI {
	TextView tvTimer;
	
	public PomTimerTextUI(TextView tvTimer) {
		this.tvTimer = tvTimer;
	}
	
	public TextView getTimerTextView() {
		return tvTimer;
	}
	
	public void setTimerTextView(TextView tvTimer) {
		this.tvTimer = tvTimer;
	}

	@Override
	public void updateTime(long millis) {
		String time = PomUtil.formatTime(millis);
		tvTimer.setText(time);
	}
}
