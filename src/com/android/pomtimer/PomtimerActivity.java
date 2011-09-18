package com.android.pomtimer;

import com.android.pomtimer.R;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View.OnClickListener;

public class PomtimerActivity extends Activity implements OnClickListener, 
															OnPomTimerEventListener{
	static long CONST_DURATION_MILLIS = PomUtil.minsToMillis(1);
	static long INTERVAL = 200;
	static String LONG_DURATION = "longDuration";
	static String SHORT_DURATION = "shortDuration";
	static String ALARM_TONE = "alarmTone";
	static int NOTIFICATION_ID = 1;
	static int MAX_SHORT_BREAKS = 4;
	
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
        
        timerUI = new PomTimerGUI(this);
        View timerUIView = timerUI.getUI(this);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.buttonsLayout);
        layout.addView(timerUIView);
        
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
        tvBreak.setText(R.string.welcomeMsg);
        
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
			millisRemaining = CONST_DURATION_MILLIS;
			timer = new PomCountDownTimer(millisRemaining, INTERVAL, timerUI, this);
		}
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
		long duration = breakCount == MAX_SHORT_BREAKS ? getBreakDuration(LONG_DURATION) : getBreakDuration(SHORT_DURATION);
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
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP );
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS;
		String alarmUri = settings.getString(ALARM_TONE, "DEFAULT_ALARM_ALERT_URI");
		notification.sound = Uri.parse(alarmUri);
		notificationManager.notify(NOTIFICATION_ID, notification);
	}

	@Override
	public void onPomTimerFinish() {
		long duration = 0;
		CharSequence tvBreakMsg;
		CharSequence tickerText;
		CharSequence contentTitle = getString(R.string.app_name);
		CharSequence contentText;
		Resources resources = getResources();
		String tickerFormat = resources.getString(R.string.tickerTextHeading);
		
		if (onBreak) {
			enableButtons();
			duration = CONST_DURATION_MILLIS;
			String workTickerText = resources.getString(R.string.workTickerText);
			tickerText = String.format(tickerFormat, workTickerText);
			contentText = getString(R.string.workContentText);
			tvBreakMsg = resources.getString(R.string.backToWorkMsg);
			onBreak = false;
		}
		else {
			disableButtons();
			duration = getBreakDuration();
			String breakTickerText = resources.getString(R.string.breakTickerText);
			tickerText = String.format(tickerFormat, breakTickerText);
			onBreak = true;
			
			if (breakCount == MAX_SHORT_BREAKS) {
				contentText = getString(R.string.longBreakContentText);
				tvBreakMsg = resources.getString(R.string.longBreakMsg);
				breakCount = 0;
			}
			else {
				contentText =  getString(R.string.shortBreakContentText);
				breakCount += 1;
				String tvBreakFormat = resources.getString(R.string.shortBreakMsg);
				tvBreakMsg = String.format(tvBreakFormat, breakCount);
			}
		}
		
		// notify and start a new timer
		generateNotification(breakIcon, tickerText, contentTitle, contentText);
		timer = new PomCountDownTimer(duration, INTERVAL, timerUI, this);
		tvBreak.setText(tvBreakMsg);
		timer.start();
	}
}