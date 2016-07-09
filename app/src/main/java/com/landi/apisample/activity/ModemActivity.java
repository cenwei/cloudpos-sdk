package com.landi.apisample.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.landi.apisample.R;
import com.landi.apisample.device.comm.ModemDevice;
import com.landi.apisample.device.comm.OnStateChangeListener;

/**
 * There are all modem operations samples in this Activity.
 * 
 * @author chenwei
 * 
 */
public class ModemActivity extends BaseActivity implements
		OnStateChangeListener {

	private ModemDevice device;
	private EditText hostsEdit;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modem);

		device = ModemDevice.getInstance();
		device.listenState(this);

		hostsEdit = (EditText) findViewById(R.id.hosts_text);
		// Default values
		hostsEdit.setText("123456\n3456789");

		// Create all listener to listen user input

		findViewById(R.id.button_start).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// Add all hosts
						String[] hosts = hostsEdit.getText().toString()
								.split("\n");
						if (hosts.length == 0) {
							displayInfo("Please input hosts you want to connect(phone number).");
							return;
						}
						for (String host : hosts) {
							if (host.isEmpty()) {
								continue;
							}
							displayInfo("add host - " + host);
							device.addHost(host);
						}
						displayInfo(" ------------ Dial Up ------------ ");
						device.dialUp();
					}
				});

		findViewById(R.id.button_stop).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						displayInfo(" ------------ Dial Off ------------ ");
						device.dialOff();
					}
				});
	}

	@Override
	protected void onPause() {

		device.stopListen();
		device.dialOff();

		super.onPause();
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
	public void onStateChanged(final int state) {
		// read or write data with host server.
		// device.read(data, offset, len, timeout);
	}

	@Override
	public void onStateMessageReceived(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				displayInfo("STATE CHANGED - " + msg);
			}
		});
	}
}
