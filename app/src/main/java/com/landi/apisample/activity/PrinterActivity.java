package com.landi.apisample.activity;

import com.landi.apisample.R;
import com.landi.apisample.device.PrinterSample;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

/**
 * There are all printer operations samples in this Activity.
 * @author chenwei
 *
 */
public class PrinterActivity extends BaseActivity {
	
	private PrinterSample printerSample;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.printer);
		initPrintSample();
		//initPrintSample();

		// Create all listener to listen user input

		findViewById(R.id.button_add_title).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayInfo(" ------------ title added ------------ ");
				printerSample.addTitle();
			}
		});
		
		findViewById(R.id.button_add_hello_world).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayInfo(" ------------ tail added ------------ ");
				printerSample.addHelloWorld();
			}
		});
		
		findViewById(R.id.button_start_print).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				displayInfo(" ------------ start print ------------ ");
				printerSample.startPrint();
			}
		});
		
		
	}

	private void initPrintSample() {
		printerSample = new PrinterSample(this) {

            @Override
            protected void onDeviceServiceCrash() {
                // Handle in 'PrinterActivity'
                PrinterActivity.this.onDeviceServiceCrash();
            }

            @Override
            protected void displayPrinterInfo(String info) {
                // Handle in 'PrinterActivity'
                PrinterActivity.this.displayInfo(info);
            }
        };
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
