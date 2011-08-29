package com.android.pomtimer;

public interface OnPomBreakListener {
	public abstract void onPomBreakStart(long durationMillis);
	public abstract void onPomBreakFinish();
}
