package com.android.pomtimer;

import com.android.pomtimer.R;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View.OnClickListener;

public class PomtimerActivity extends Activity implements OnClickListener, 
														OnPomTimerEventListener{
	
	static long CONST_DURATION_MILLIS = PomUtil.minsToMillis(1);
	static long INTERVAL = 1000;
	static String LONG_DURATION = "longDuration";
	static String SHORT_DURATION = "shortDuration";
	static int NOTIFICATION_ID = 1;
	
	int breakCount;
	int breakIcon;
	boolean onBreak;
	
	PomCountDownTimer timer;
	PomTimerUI timerUI;
	
	Button start;
	Button stop;
	Button reset;
	Button [] buttons;
	TextView tvBreak;
	
	SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView tvTimer = (TextView) findViewById(R.id.timer);
        timerUI = new PomTimerTextUI(tvTimer);
        timer = new PomCountDownTimer(CONST_DURATION_MILLIS, INTERVAL, timerUI, this);
        
        start = (Button) findViewById(R.id.startButton);
        stop = (Button) findViewById(R.id.stopButton);
        reset = (Button) findViewById(R.id.resetButton);
        
        buttons = new Button [] { start, stop, reset };
        
        for (Button b : buttons) {
        	b.setOnClickListener(this);
        }
        
        breakCount = 0;
        onBreak = false;
        
        tvBreak = (TextView) findViewById(R.id.breakStatusText);
        breakIcon = R.drawable.notification_icon;
        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    MenuInflater inf = getMenuInflater();
	    inf.inflate(R.menu.touch_menu, menu);
	   	return true;
	}
	    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	   	case R.id.settings:
	    	Intent intent = new Intent(PomtimerActivity.this, Settings.class);
	    	PomtimerActivity.this.startActivity(intent);
	   		return true;
	    default:
	    	return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.startButton:
			// get the remaining time from the current timer
			long millisRemaining = timer.getMillisRemaining();
			timer = new PomCountDownTimer(millisRemaining, INTERVAL, timerUI, this);
			timer.start();
			break;
		case R.id.stopButton:
			timer.cancel();
			break;
		case R.id.resetButton:
			timer.cancel();
			if( breakCount > 0 )
				breakCount--;
			millisRemaining = CONST_DURATION_MILLIS;
			timer = new PomCountDownTimer(millisRemaining, INTERVAL, timerUI, this);
		}
	}
	
	public void setDisplayedTime(long millis) {
		String time = PomUtil.formatTime(millis);
		TextView tv = (TextView) findViewById(R.id.timer);
		tv.setText(time);
	}
	
	private void disableButtons() {
		for (Button b : buttons) {
			b.setEnabled(false);
		}
	}
	
	private void enableButtons() {
		for (Button b: buttons) {
			b.setEnabled(true);
		}
	}
	
	private long getBreakDuration(String breakType) {
		int mins = Integer.parseInt(settings.getString(breakType, "0"));
		long millis = PomUtil.minsToMillis(mins);
		return millis;
	}
	
	private long getBreakDuration() {
		long duration = breakCount == 4 ? getBreakDuration(LONG_DURATION) : getBreakDuration(SHORT_DURATION);
		return duration;
	}
	
	private void generateNotification(int icon, CharSequence tickerText, 
									  CharSequence contentTitle, CharSequence contentText) {
		
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
		
		long now = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, now);
		Context context = getApplicationContext();
		
		Intent notificationIntent = new Intent(context, PomtimerActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
									| Intent.FLAG_ACTIVITY_SINGLE_TOP );
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL 
							| Notification.DEFAULT_LIGHTS 
							| Notification.DEFAULT_SOUND;
		notificationManager.notify(NOTIFICATION_ID, notification);
	}

	@Override
	public void onPomTimerFinish() {
		long duration = 0;
		int resTvBreak;
		CharSequence tickerText;
		CharSequence contentTitle = getString(R.string.app_name);
		CharSequence contentText;
		
		if (onBreak) {
			enableButtons();
			duration = CONST_DURATION_MILLIS;
			resTvBreak = R.string.breakText;
			tickerText = getString(R.string.app_name) + ": " + getString(R.string.workTickerText);
			contentText = getString(R.string.workContentText);
			resTvBreak = R.string.backToWorkText;
			onBreak = false;
		}
		else {
			disableButtons();
			duration = getBreakDuration();
			resTvBreak = R.string.breakText;
			tickerText = getString(R.string.app_name) + ": " + getString(R.string.breakTickerText);
			contentText = breakCount == 4 ? getString(R.string.longBreakContentText) : getString(R.string.shortBreakContentText);
			resTvBreak = R.string.breakText;
			onBreak = true;
			breakCount = breakCount == 4 ? 0 : breakCount++;
		}
		
		// notify and start a new timer
		generateNotification(breakIcon, tickerText, contentTitle, contentText);
		timer = new PomCountDownTimer(duration, INTERVAL, timerUI, this);
		tvBreak.setText(resTvBreak);
		timer.start();
	}
}