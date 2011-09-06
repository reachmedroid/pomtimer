package com.android.pomtimer;

import android.os.CountDownTimer;

public class PomCountDownTimer extends CountDownTimer {
	
	long millisRemaining;
	OnPomTimerEventListener listener;
	PomTimerUI timerUI;

	public PomCountDownTimer(long millisInFuture, 
							long countDownInterval, 
							PomTimerUI timerUI,
							OnPomTimerEventListener listener) {
		super(millisInFuture, countDownInterval);
		this.setMillisRemaining(millisInFuture);
		this.setTimerUI(timerUI);
		this.setListener(listener);
		timerUI.updateTime(millisInFuture);
	}
	
	private void setMillisRemaining(long millisRemaining) {
		this.millisRemaining = millisRemaining;
	}
	
	public long getMillisRemaining() {
		return this.millisRemaining;
	}
	
	public void setListener(OnPomTimerEventListener listener) {
		this.listener = listener;
	}
	
	public void setTimerUI(PomTimerUI timerUI) {
		this.timerUI = timerUI;
	}
	
	public PomTimerUI getTimerUI() {
		return this.timerUI;
	}
	
	@Override
	public void onFinish() {
		listener.onPomTimerFinish();
	}

	@Override
	public void onTick(long millisUntilFinished) {
		getTimerUI().updateTime(millisUntilFinished);
		millisRemaining = millisUntilFinished;
	}
}