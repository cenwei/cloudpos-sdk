package com.landi.apisample.device;

import android.content.Context;

import com.landicorp.android.eptapi.device.CashBox;
import com.landicorp.android.eptapi.exception.RequestException;

/**
 * This code sample is about cashbox operations.
 * @author chenwei
 *
 */
public abstract class CashBoxSample extends AbstractSample {
	/**
	 * Get CashBox object and assign the device name.
	 * In fact, the device name is unused but reserved.
	 */
	private CashBox box = new CashBox("USBH");
	
	private CashBox.OnBoxOpenListener onOpenListener = new CashBox.OnBoxOpenListener() {
		
		@Override
		public void onCrash() {
			onDeviceServiceCrash();
		}
		
		@Override
		public void onBoxOpened() {
			displayDeviceInfo("OPEN SUCCESS!");
		}
		
		@Override
		public void onBoxOpenFail(int error) {
			displayDeviceInfo("OPEN FAIL - "+getErrorDescription(error));
		}
		
		public String getErrorDescription(int error) {
			switch(error) {
			case  CashBox.ERROR_DEVICE_NOT_EXIST:
				return "device is not exist or disabled";
			case  CashBox.ERROR_IS_ALEADY_OPEN:
				return "device is aleady opened";
			case  CashBox.ERROR_FAIL:
				return "open error";
			}
			return "unknown error ["+error+"]";
		}

	};
		
	public CashBoxSample(Context context) {
		super(context);
	}
	
	/**
	 * Open cashbox.
	 * Make sure your cashbox is connected.
	 */
	public void openBox() {
		try {
			box.openBox();
		} catch (RequestException e) {
			e.printStackTrace();
			onDeviceServiceCrash();
		}
	}
	
	/**
	 * For the listener is not certainly need. It can be set or unset as you want.
	 */
	public void installListener() {
		box.setOnBoxOpenListener(onOpenListener);
	}
	
	/**
	 * For the listener is not certainly need. It can be set or unset as you want.
	 */
	public void uninstallListener() {
		box.setOnBoxOpenListener(null);
	}
	
	/**
	 * Display info 
	 * @param info
	 */
	protected abstract void displayDeviceInfo(String info);
}
