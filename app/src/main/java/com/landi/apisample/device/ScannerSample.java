package com.landi.apisample.device;

import android.content.Context;

import com.landicorp.android.eptapi.device.Scanner;

/**
 * This sample show that how to use scanner
 * 
 * @author chenwei
 * 
 */
public abstract class ScannerSample extends AbstractSample {
	/**
	 * Create a scanner object and use COM2 to connect to scanner. Scanner is
	 * based on serial port, and not use device service.
	 */
	private Scanner scanner;

	private Scanner.OnScanListener listener = new Scanner.OnScanListener() {

		@Override
		public void onScanSuccess(String code) {
			scanner.close();
		}

		@Override
		public void onScanFail(int error) {
			scanner.close();
			displayDeviceInfo("OPEN ERR - " + getErrorDescription(error));
		}

		@Override
		public void onCrash() {
			scanner.close();
			// onDeviceServiceCrash();
		}

		String getErrorDescription(int code) {
			switch (code) {
			case ERROR_COMMERR:
				return "Communicate error";
			case ERROR_TIMEOUT:
				return "Communicate timeout";
			case ERROR_FAIL:
				return "Other error(OS error,etc)"; 
			}
			return "unknown error ("+code+")";
		}
	};

	public ScannerSample(Context context) {
		super(context);

		scanner = new Scanner("COM2");
		scanner.setOnScanListener(listener);
	}

	protected abstract void displayDeviceInfo(String info);

	/**
	 * Start to scan.
	 */
	public void start() {
		if (Scanner.ERROR_NONE != scanner.open()) {
			displayDeviceInfo("#SCANNER OPEN ERROR#");
			return;
		}
		scanner.scan();
	}

	/**
	 * Stop scanning.
	 */
	public void stop() {
		scanner.close();
	}
}
