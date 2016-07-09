package com.landi.apisample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.landi.apisample.activity.BaseActivity;
import com.landi.apisample.activity.CashBoxActivity;
import com.landi.apisample.activity.ContactICCardActivity;
import com.landi.apisample.activity.InnerScannerActivity;
import com.landi.apisample.activity.MagCardActivity;
import com.landi.apisample.activity.ModemActivity;
import com.landi.apisample.activity.PrinterActivity;
import com.landi.apisample.activity.RFCardActivity;
import com.landi.apisample.activity.ScannerActivity;
import com.landi.apisample.activity.SerialPortActivity;

public class MainActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
        // Each button can start a activity to show the module functions.
        
        findViewById(R.id.mag_card).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(MagCardActivity.class);
			}
        	
        });
        
        findViewById(R.id.contact_ic_card).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(ContactICCardActivity.class);
			}
        	
        });
        
        findViewById(R.id.rf_card).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(RFCardActivity.class);
			}
        	
        });
        

        findViewById(R.id.printer).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(PrinterActivity.class);
			}
        	
        });
        
        findViewById(R.id.serial).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(SerialPortActivity.class);
			}
        	
        });
        
        findViewById(R.id.cashbox).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(CashBoxActivity.class);
			}
        	
        });
        
        findViewById(R.id.inner_scanner).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(InnerScannerActivity.class);
			}
        	
        });
        
        findViewById(R.id.scanner).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(ScannerActivity.class);
			}
        	
        });

        findViewById(R.id.modem).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(ModemActivity.class);
			}
        	
        });
    }
    
    public void startActivity(Class<? extends Activity> activityClass) {
    	Intent intent = new Intent(this, activityClass);
    	startActivity(intent);
    }
}
