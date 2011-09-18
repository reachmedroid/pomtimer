package com.android.pomtimer;

import android.content.Context;
import android.view.View;

public interface PomTimerUI {
	public View getUI(Context context);
	public void updateTime(long millis);
}
