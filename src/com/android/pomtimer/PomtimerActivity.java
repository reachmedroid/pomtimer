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
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View.OnClickListener;

public class PomtimerActivity extends Activity implements OnClickListener {
	
	static long CONST_DURATION_MILLIS = PomUtil.minsToMillis(1);
	static long INTERVAL = 1000;
	static String LONG_DURATION = "longDuration";
	static String SHORT_DURATION = "shortDuration";
	static int NOTIFICATION_ID = 1;
	
	int breakCount;
	int breakIcon;
	long millisRemaining;
	boolean onBreak;
	
	PomCountDownTimer timer;
	Button start;
	Button stop;
	Button reset;
	Button [] buttons;
	TextView tvBreak;

	OnPomBreakListener pomBreakListener = null;
	
	public void setOnPomBreakListener(OnPomBreakListener pl) {
		pomBreakListener = pl;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView tv = (TextView) findViewById(R.id.timer);
        String time = PomUtil.formatTime(CONST_DURATION_MILLIS);
        tv.setText(time);
        
        start = (Button) findViewById(R.id.startButton);
        stop = (Button) findViewById(R.id.stopButton);
        reset = (Button) findViewById(R.id.resetButton);
        
        buttons = new Button [] { start, stop, reset };
        
        for (Button b : buttons) {
        	b.setOnClickListener(this);
        }
        
        breakCount = 0;
        onBreak = false;
        millisRemaining = CONST_DURATION_MILLIS;
        
        tvBreak = (TextView) findViewById(R.id.breakStatusText);
        breakIcon = R.drawable.notification_icon;
        
        this.setOnPomBreakListener(new OnPomBreakListener() {

			@Override
			public void onPomBreakFinish() {
				enableButtons();
				timer = new PomCountDownTimer(CONST_DURATION_MILLIS, INTERVAL);
				setDisplayedTime(CONST_DURATION_MILLIS);
				
				CharSequence tickerText = "Pomtimer: Work Time!";
				CharSequence contentTitle = "Pomtimer";
				CharSequence contentText = "Time to go back to work";
				generateNotification(breakIcon, tickerText, contentTitle, contentText);
				
				tvBreak.setText(R.string.backToWorkText);
				timer.start();
			}

			@Override
			public void onPomBreakStart(long durationMillis) {
				disableButtons();
				
				// create a new timer for the break
				timer = new PomCountDownTimer(durationMillis, INTERVAL);
				setDisplayedTime(durationMillis);
				tvBreak.setText(R.string.breakText);
				
				timer.start();
			}
    	});
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
			timer = new PomCountDownTimer(millisRemaining, INTERVAL);
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
			timer = new PomCountDownTimer(millisRemaining, INTERVAL);
			setDisplayedTime(millisRemaining);
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
		notification.flags = Notification.FLAG_AUTO_CANCEL | 
							 Notification.DEFAULT_LIGHTS |
							 Notification.DEFAULT_SOUND;
		notificationManager.notify(NOTIFICATION_ID, notification);
	}
	
	public class PomCountDownTimer extends CountDownTimer {

		public PomCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			setDisplayedTime(0);
			
			// if not on break then notify of short break
			// if there has been 4 short breaks - notify of long break
			if (!onBreak) {
				CharSequence tickerText = "Pomtimer: Break time!";
				CharSequence contentTitle = "Pomtimer";
				CharSequence contentText;

				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				int mins = 0;
			
				if (breakCount == 4) {
					contentText = "Time for a long break!";
					mins = Integer.parseInt(settings.getString(LONG_DURATION, "0"));
					breakCount = 0;
				}
				else {
					contentText = "Time for a short break!";
					mins = Integer.parseInt(settings.getString(SHORT_DURATION, "0"));
					breakCount++;
				}
				generateNotification(breakIcon, tickerText, contentTitle, contentText);
				
				onBreak = true;
			
				// fires break event listener with duration
				pomBreakListener.onPomBreakStart(PomUtil.minsToMillis(mins));
			}
			else {
				onBreak = false;
				pomBreakListener.onPomBreakFinish();
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
			setDisplayedTime(millisUntilFinished);
			
			// store time elapsed
			millisRemaining = millisUntilFinished;
		}
	}
}