package com.landi.apisample.activity;

import com.landi.apisample.R;
import com.landi.apisample.device.SerialPortSample;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

/**
 * There are all serial port operations samples in this Activity.
 * @author chenwei
 *
 */
public class SerialPortActivity extends BaseActivity {
	
	private SerialPortSample serialPortSample;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.serial_port);
	    
	    serialPortSample = new SerialPortSample(this) {
			
			@Override
			protected void onDeviceServiceCrash() {
				// Serial is not using device service. So do nothing.
			}

			@Override
			protected void displayDeviceInfo(String info) {
				// Handle in 'SerialPortActivity'
				SerialPortActivity.this.displayInfo(info);
			}

			@Override
			protected void onConnectionAborted() {
				// Handle in 'SerialPortActivity'
				SerialPortActivity.this.finish();
			}
		};

		// Create all listener to listen user input
		
		findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayInfo(" ------------ comm start ------------ ");
				serialPortSample.start();
			}
		});
		
		findViewById(R.id.button_stop).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayInfo(" ------------ comm aborting ------------ ");
				serialPortSample.abort();
			}
		});
		
		
	}
	
	 @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
	
	/**
	 * All device operation result infomation will be displayed by this method.
	 * 
	 * @param info
	 */
	public void displayInfo(String info) {
		EditText infoEditText = (EditText) findViewById(R.id.info_text);
		String text = infoEditText.getText().toString();
		if (text.isEmpty()) {
			infoEditText.setText(info);
		} else {
			infoEditText.setText(text + "\n" + info);
		}
		infoEditText.setSelection(infoEditText.length());
	}

	@Override
	public void onBackPressed() {
		if(serialPortSample.isStarted()) {
			serialPortSample.abort();
		}
		else {
			super.onBackPressed();
		}
	}
}
