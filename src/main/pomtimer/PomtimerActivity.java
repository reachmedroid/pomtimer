package main.pomtimer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View.OnClickListener;

public class PomtimerActivity extends Activity implements OnClickListener {
	
	static long CONST_DURATION_MILLIS = 25 * 60 * 1000;
	static long INTERVAL = 1000;
	static final int BREAK_NOTIFICATION_ID = 1;
	
	PomCountDownTimer timer;
	int breakCount = 0;
	long millisRemaining = CONST_DURATION_MILLIS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView tv = (TextView) findViewById(R.id.timer);
        String time = PomUtil.formatTime(CONST_DURATION_MILLIS);
        tv.setText(time);
        
        Button start = (Button) findViewById(R.id.startButton);
        start.setOnClickListener(this);
        
        Button stop = (Button) findViewById(R.id.stopButton);
        stop.setOnClickListener(this);
        
        Button reset = (Button) findViewById(R.id.resetButton);
        reset.setOnClickListener(this);
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
	    	Intent intent = new Intent(PomtimerActivity.this, SettingsActivity.class);
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
	
	public class PomCountDownTimer extends CountDownTimer {

		public PomCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			// notify of short break
			// if there has been 4 breaks - notify of long break
			setDisplayedTime(0);
			
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager nManager = (NotificationManager) getSystemService(ns);
			
			int icon = R.drawable.notification_icon;
			long now = System.currentTimeMillis();
			
			CharSequence tickerText = "Pomtimer: Break time!";
			CharSequence contentTitle = "Pomtimer";
			CharSequence contentText;
			
			Notification notification = new Notification(icon, tickerText, now);
			
			if (breakCount == 4) {
				contentText = "Time for a long break!";
				breakCount = 0;
			}
			else {
				contentText = "Time for a short break!";
				breakCount++;
			}
			
			Context context = getApplicationContext();
			Intent notificationIntent = new Intent(context, PomtimerActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			
			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			nManager.notify(BREAK_NOTIFICATION_ID, notification);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			setDisplayedTime(millisUntilFinished);
			
			// store time elapsed
			millisRemaining = millisUntilFinished;
		}
	}
}