package com.landi.apisample.device;

import android.content.Context;

import com.landicorp.android.eptapi.device.InnerScanner;

/**
 * This sample show that how to use inner scanner
 * @author chenwei
 *
 */
public abstract class InnerScannerSample extends AbstractSample {

	private InnerScanner scanner;
	private InnerScanner.OnScanListener listener = new InnerScanner.OnScanListener() {

		@Override
		public void onScanSuccess(String code) {
			displayDeviceInfo("SCAN - " + code);
		}

		@Override
		public void onScanFail(int error) {
			displayDeviceInfo("SCAN ERR - " + getErrorDescription(error));
		}
		
		@Override
		public void onCrash() {
			onDeviceServiceCrash();
		}
		
		String getErrorDescription(int code) {
			switch(code) {
			case ERROR_TIMEOUT:
				return "Scan timeout";
			case ERROR_FAIL:
				return "Other error(OS error,etc)"; 
			}
			return "unknown error ["+code+"]";
		}
	};
	
	public InnerScannerSample(Context context) {
		super(context);

		scanner = InnerScanner.getInstance();
		scanner.setOnScanListener(listener);
	}

	protected abstract void displayDeviceInfo(String info);

	/**
	 * Start to scan.
	 */
	public void start() {
		scanner.start(20);
	}

	/**
	 * Stop scanning.
	 */
	public void stop() {
		scanner.stop();
	}

	/**
	 * Inner scanner object is singlton. So the listener must be release in the
	 * right time.
	 */
	public void stopListen() {
		scanner.stopListen();
	}
}
