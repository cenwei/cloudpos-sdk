package com.landi.apisample.activity;

import com.landi.apisample.R;
import com.landi.apisample.device.RFCardReaderSample;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

/**
 * There are all rf card operations samples in this Activity.
 * @author chenwei
 *
 */
public class RFCardActivity extends BaseActivity {
	
	private RFCardReaderSample rfCardSample;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.rf_card);
	    
	    rfCardSample = new RFCardReaderSample(this) {
			
			@Override
			protected void onDeviceServiceCrash() {
				// Handle in 'RFCardActivity'
				RFCardActivity.this.onDeviceServiceCrash();
			}
			
			@Override
			protected void displayRFCardInfo(String cardInfo) {
				// Handle in 'RFCardActivity'
				RFCardActivity.this.displayInfo(cardInfo);
			}
		};

		// Create all listener to listen user input
		
		findViewById(R.id.button_search_up_card_and_read).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayInfo(" ------------ start search  ------------ ");
				rfCardSample.searchCard();
			}
		});
		
		findViewById(R.id.button_stop_search).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayInfo(" ### stop search ### \n\n");
				rfCardSample.stopSearch();
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
	
	// Sometimes you need to release the right of using device before other application 'onStart'.
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
