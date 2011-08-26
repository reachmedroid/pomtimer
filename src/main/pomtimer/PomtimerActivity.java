package main.pomtimer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

public class PomtimerActivity extends Activity {
	
	Button start;
	Button stop;
	Button reset;
	TextView tv;
	
	CountDownTimer timer = new CountDownTimer(1500000, 1000) {
    	
    	@Override
    	public void onTick(long millisUntilFinished) {
    		// get minutes remaining
    		
    		// get seconds remaining
    	}

		@Override
		public void onFinish() {
			
			
		}
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView) findViewById(R.id.timer);
        
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
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
	
	View.OnClickListener startHandler = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			
		}
	};
}