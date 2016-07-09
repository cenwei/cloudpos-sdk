package com.landi.apisample.device;

import java.io.IOException;

import android.content.Context;
import android.os.Handler;

import com.landi.apisample.device.comm.CommunicateException;
import com.landi.apisample.device.comm.SerialPortConnection;
import com.landicorp.android.eptapi.device.SerialPort;

/**
 * This code sample is about Serial Port operations.
 * @author chenwei
 *
 */
public abstract class SerialPortSample extends AbstractSample {
	private SerialPort serialPort;
	private boolean isUserAbort;
	
	/**
	 * Serial port API all use blocking mode. 
	 * So you need a thread to do the I/O work, to keep the UI Thread active.
	 * 
	 */
	private Thread workThread = new Thread() {
		private SerialPortConnection connection;
		private Handler handler = new Handler();
		@Override
		public void run() {
			try {
				connection = new SerialPortConnection(serialPort);
				connection.connect();
				
				String[] messages = {"How do you do.", "What's you name?", "Nice to meet you!"};
				for(int i=0; !isUserAbort && i<messages.length; i++) {
					showStatus("SEND - "+messages[i]);
					byte[] responseMessage = connection.sendAndRecv(messages[i].getBytes());
					String msg = new String(responseMessage);
					showStatus("RECV - "+msg);
					// Cycle
					if(i == messages.length-1) {
						i = 0;
					}
				}
			} catch (CommunicateException e) {
				e.printStackTrace();
				showStatus(e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				connection.disconnect();
			}
			
			
			if(isUserAbort) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						onConnectionAborted();
					}
				});
			}
		}
		
		/**
		 * Display info in UI thread.
		 * @param status
		 */
		public void showStatus(final String status) {

			handler.post(new Runnable(){
				@Override
				public void run() {
					displayDeviceInfo(status);
				}
			});
		}
	};
	
	public SerialPortSample(Context context) {
		super(context);
		// It's use the adb usb port as a virtual serial port. Mini usb port.
		// You can also use COM1¡¢COM2¡¢USBH and so on.
		serialPort = new SerialPort("USBD");
	}
	
	/**
	 * Start communication.
	 */
	public void start() {
		if(workThread.isAlive()) {
			showErrorMessage("The connection is started, shutdown it down first!");
			return;
		}
		
		workThread.start();
	}
	
	/**
	 * Abort communication and return immediately. 
	 * If abort success, it will occur 'onConnectionAborted' method.
	 */
	public void abort() {
		isUserAbort = true;
	}
	
	public boolean isStarted() {
		return workThread.isAlive();
	}
	
	/**
	 * Display info 
	 * @param info
	 */
	protected abstract void displayDeviceInfo(String info);
	
	/**
	 * If connection aborted by user, it will occur when the connection shutdown.
	 */
	protected abstract void onConnectionAborted();
}
