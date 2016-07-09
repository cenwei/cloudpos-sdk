package com.landi.apisample.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.landi.apisample.R;
import com.landi.apisample.device.InnerScannerSample;

/**
 * There are all inner scanner operations samples in this Activity.
 * @author chenwei
 *
 */
public class InnerScannerActivity extends BaseActivity {
	
	private InnerScannerSample scannerSample;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.inner_scanner);
	    
	    scannerSample = new InnerScannerSample(this) {
			
			@Override
			protected void onDeviceServiceCrash() {
				// Handle in 'InnerScannerActivity'
				InnerScannerActivity.this.onDeviceServiceCrash();
			}

			@Override
			protected void displayDeviceInfo(String info) {
				// Handle in 'InnerScannerActivity'
				InnerScannerActivity.this.displayInfo(info);
			}
		};

		// Create all listener to listen user input
		
		findViewById(R.id.button_start_scan).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayInfo(" ------------ start ------------ ");
				scannerSample.start();
			}
		});
		
		findViewById(R.id.button_stop).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayInfo(" ------------ stop ------------ ");
				scannerSample.stop();
			}
		});
		
	}
	
	 @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		
		bindDeviceService();
	}
	
	/**
	 * Sometimes you need to release the right of using device before other application 'onStart'.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		unbindDeviceService();
		
		// It's very important for release memory.
		scannerSample.stopListen();
	}
	
	/**
	 * If device service crashed, quit application or try to relogin service again.
	 */
	public void onDeviceServiceCrash() {
		bindDeviceService();
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

	
}
