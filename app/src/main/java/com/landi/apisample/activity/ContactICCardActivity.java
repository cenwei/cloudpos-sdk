package com.landi.apisample.activity;

import com.landi.apisample.R;
import com.landi.apisample.device.ContactICCardSample;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

/**
 * There are all contact IC card operations samples in this Activity.
 * @author chenwei
 *
 */
public class ContactICCardActivity extends BaseActivity {

	/**
	 * Contact IC card sample.
	 */
	private ContactICCardSample contactICSample;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_ic_card);
        
        // Create all samples
        
        contactICSample = new ContactICCardSample(this) {
			
			@Override
			protected void onDeviceServiceCrash() {
				// Handle in 'ContactICCardActivity'
				ContactICCardActivity.this.onDeviceServiceCrash();
			}
			
			@Override
			protected void displayICInfo(String cardInfo) {
				// Handle in 'ContactICCardActivity'
				ContactICCardActivity.this.displayInfo(cardInfo);
			}
		};
		
		// Create all listener to listen user input
		
		findViewById(R.id.button_search_cpu_card_and_read).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayInfo(" ------------ start search  ------------ ");
				contactICSample.searchCpuCard();
			}
		});
		
		findViewById(R.id.button_stop_search).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayInfo(" ### stop search ### \n\n");
				contactICSample.stopSearch();
			}
		});
		
		findViewById(R.id.button_read_psam).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayInfo(" ------------ start read  ------------ ");
				contactICSample.readPSAMCard();
			}
		});

		// Mode radio buttons
		// Powerup mode and vol is used on deferent cards.
		
		findViewById(R.id.radioButton_emv).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				contactICSample.setToEMVMode();
			}
		});
		

		findViewById(R.id.radioButton_shb).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				contactICSample.setToSHBMode();
			}
		});

		findViewById(R.id.radioButton_bps192).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				contactICSample.setToBPS19200Mode();
			}
		});
		
		// Vol radio buttons
		
		findViewById(R.id.radioButton_vol5).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				contactICSample.setTo5VOL();
			}
		});
		

		findViewById(R.id.radioButton_vol3).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				contactICSample.setTo3VOL();
			}
		});

		findViewById(R.id.radioButton_vol18).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				contactICSample.setTo1_8VOL();
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
	 * @param info
	 */
	public void displayInfo(String info) {
		EditText infoEditText = (EditText) findViewById(R.id.info_text);
		String text = infoEditText.getText().toString();
		if(text.isEmpty()) {
			infoEditText.setText(info);
		}
		else {
			infoEditText.setText(text + "\n" + info);
		}
		infoEditText.setSelection(infoEditText.length());
	}

}
