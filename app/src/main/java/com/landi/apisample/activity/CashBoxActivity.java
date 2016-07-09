package com.landi.apisample.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.landi.apisample.R;
import com.landi.apisample.device.CashBoxSample;

/**
 * There are all cashbox operations samples in this Activity.
 * @author chenwei
 *
 */
public class CashBoxActivity extends BaseActivity {
	
	private CashBoxSample cashboxSample;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.cashbox);
	    
	    cashboxSample = new CashBoxSample(this) {
			
			@Override
			protected void onDeviceServiceCrash() {
				// Handle in 'CashBoxActivity'
				CashBoxActivity.this.onDeviceServiceCrash();
			}

			@Override
			protected void displayDeviceInfo(String info) {
				// Handle in 'CashBoxActivity'
				CashBoxActivity.this.displayInfo(info);
			}
		};

		// Create all listener to listen user input
		
		findViewById(R.id.button_install).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayInfo(" ------------ install ------------ ");
				cashboxSample.installListener();
			}
		});
		
		findViewById(R.id.button_uninstall).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayInfo(" ------------ uninstall ------------ ");
				cashboxSample.uninstallListener();
			}
		});

		findViewById(R.id.button_open_box).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayInfo(" ------------ open box ------------ ");
				cashboxSample.openBox();
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
	 *  Sometimes you need to release the right of using device before other application 'onStart'.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		unbindDeviceService();
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
