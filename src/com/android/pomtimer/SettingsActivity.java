package com.android.pomtimer;

import main.pomtimer.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnClickListener {
	
	static String LONG_DURATION = "longDuration";
	static String SHORT_DURATION = "shortDuration";
	
	int longDuration;
	int shortDuration;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        // set click listener to button
        Button updateButton = (Button)findViewById(R.id.updateButton);
        updateButton.setOnClickListener(this);
        
        // restore preferences
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        longDuration = settings.getInt(LONG_DURATION, 0);
        shortDuration = settings.getInt(SHORT_DURATION, 0);
        
        // update the settings UI with current values
        EditText longTextView = (EditText) findViewById(R.id.longText);
		EditText shortTextView = (EditText) findViewById(R.id.shortText);
		longTextView.setText(Integer.toString(longDuration));
		shortTextView.setText(Integer.toString(shortDuration));
	}
	
	@Override
    protected void onStop() {
    	saveSettings();
    	super.onStop();
    }
	
	private void saveSettings() {
    	SharedPreferences settings = getPreferences(MODE_PRIVATE);
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putInt(LONG_DURATION, this.longDuration);
    	editor.putInt(SHORT_DURATION, this.shortDuration);
    	editor.commit();
    }
		
	 public void onClick(View v) {
		// get values entered into text fields
		EditText longTextView = (EditText) findViewById(R.id.longText);
		EditText shortTextView = (EditText) findViewById(R.id.shortText);
		
		String longText = longTextView.getText().toString();
		String shortText = shortTextView.getText().toString();
		
		// set them to class variables
		longDuration = PomUtil.isNullOrBlank(longText)? 0 : Integer.parseInt(longText);
		shortDuration = PomUtil.isNullOrBlank(shortText) ? 0 : Integer.parseInt(shortText);
		
		saveSettings();
		
		Toast toast = Toast.makeText(getApplicationContext(), R.string.settingsSaved, Toast.LENGTH_SHORT);
		toast.show();
	}
}
