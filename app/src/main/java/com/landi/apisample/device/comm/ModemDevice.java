package com.landi.apisample.device.comm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.landicorp.android.eptapi.device.Modem;
import com.landicorp.android.eptapi.utils.BytesUtil;
import com.landicorp.android.eptapi.utils.Log;

/**
 * Modem device. 
 * If you want to use in real project, this code sample needs to be completed.
 * 
 * Set parameters, dial up the hosts you set, then use read/write to send/receive data.
 * If it is connecting, reading or writing, use dialOff or abortIO before to do other things.
 * 
 * @author chenwei
 *
 */
public class ModemDevice {
	private static final String TAG = "ModemDevice";
	private Modem driver = new Modem();
	private List<String> hostList;
	private String prefix = "";
	private Thread dialThread;
	private Thread listenStateThread;
	private boolean isListening;
	private boolean isDialing;
	private int bps = Modem.BPS_1200;
	private String dialWay = "DT";
	private String patch;
	private boolean isFirstUse = true;
	
	//Network status
	public static final int STATE_CONNECTED = 0;
	public static final int STATE_CONNECTING = 1;
	public static final int STATE_DISCONNECTED = 2;
	public static final int STATE_DISCONNECTING = 3;
	public static final int STATE_ERROR = 4;
	public static final int STATE_TIMEOUT = 5;
	
	private static ModemDevice instance = new ModemDevice();
	public static ModemDevice getInstance() {
		return instance;
	}

	/**
	 * Set patch.
	 * The same modem driver maybe not effective at some place, then it needs patch.
	 * @param patch
	 */
	public void setPatch(String patch) {
		if(patch != null && patch.isEmpty()) {
			patch = null;
		}
		String oldPatch = this.patch;
		this.patch = patch;
		if(oldPatch != patch && !(oldPatch+"").equals(patch+"")) {
			applyPatch(false);
		}
	}
	
	/**
	 * Apply patch.
	 */
	public void applyPatch() {
		applyPatch(true);
	}
	
	/**
	 * Some patch need reset module.
	 * @param forceReset
	 */
	private void applyPatch(boolean forceReset) {
		if(patch != null) {
			driver.open();
			driver.dial("AT+PATCH="+patch);
			driver.close();
			Log.i(TAG, "-------------------apply patch----------------------"+("AT+PATCH="+patch));
		}
		else if(forceReset){
			driver.open();
			driver.dial("AT+PATCH="+patch);
			driver.reset();
			driver.close();
			Log.i(TAG, "-------------------apply patch----------------------");
		}
	}
	
	/**
	 * This can use more than one host(phone number).
	 * @param host
	 */
	public void addHost(String host) {
		if(host == null) {
			return;
		}
		
		if(hostList == null) {
			hostList = new ArrayList<String>();
		}
		
		hostList.add(host);
	}
	
	/**
	 * Delete all hosts.
	 */
	public void clearHosts() {
		if(hostList == null) {
			return;
		}
		
		hostList = new ArrayList<String>();
	}
	
	/**
	 * DT or DP
	 * @return
	 */
	public String getDialWay() {
		return dialWay;
	}

	/**
	 * DP(Pulse) or DT(audio)
	 * @return
	 */
	public void setDialWay(String dialWay) {
		if(dialWay != null) {
			this.dialWay = dialWay;
		}
	}

	/**
	 * Dial prefix. 
	 * It will apply to every host on dialing.
	 * @param pre
	 */
	public void setPrefixNumber(String pre) {
		if(pre == null) {
			pre = "";
		}
		prefix = pre;
	}
	
	/**
	 * Listen the network status.(phone line status)
	 * @param listener
	 */
	public void listenState(final OnStateChangeListener listener) {
		stopListen();
		isListening = true;
		// Start listening process.
		listenStateThread = new Thread() {
			public void run() {
				// First get phone status and notify it.
				int lastLineStatus = driver.getLineStatus(null);
				int lastDeviceStatus = convertStatus(lastLineStatus);

				listener.onStateMessageReceived(getStatusMessage(lastLineStatus));
				listener.onStateChanged(lastDeviceStatus);

				// Listen and notify status if it changed.
				while (isListening) {
					int currentLineStatus = driver.getLineStatus(null);

					if(currentLineStatus != lastLineStatus) {
						lastLineStatus = currentLineStatus;
						listener.onStateMessageReceived(getStatusMessage(currentLineStatus));

						int currentDeviceStatus = convertStatus(currentLineStatus);
						if(currentDeviceStatus != lastDeviceStatus) {
							lastDeviceStatus = currentDeviceStatus;
							listener.onStateChanged(currentDeviceStatus);
						}
					}
					else {
						// 50 msec interval.
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
						}
					}
				}
				
				Log.d(TAG, "-----------------------listen stoped------------------------");
			};
		};
		// Start
		listenStateThread.start();
	}
	
	/**
	 * Convert phone line status to network status.
	 * @param status
	 * @return
	 */
	private int convertStatus(int status) {
		switch(status) {
		case Modem.STATE_OK:
			return STATE_CONNECTED;
		case Modem.STATE_LINEBUSY:
		case Modem.STATE_INPROCESS:
		case Modem.STATE_NOCARRIER:
		case Modem.STATE_NODIALTONE:
		case Modem.STATE_TIMEOUT:
			return STATE_CONNECTING;
		default:
			return STATE_DISCONNECTED;
		}
	}
	
	/**
	 * Get phone line status message for show
	 * @param status
	 * @return
	 */
	private String getStatusMessage(int status) {
		switch(status) {
		case Modem.STATE_OK:
			return "host connected";
		case Modem.STATE_ERROR:
			return "connect faild";
		case Modem.STATE_LINEBUSY:
			return "line busy";
		case Modem.STATE_INPROCESS:
			return "connecting";
		case Modem.STATE_NOCARRIER:
			return "dial faild,no carrier!";
		case Modem.STATE_NODIALTONE:
			return "no dial tone";
		case Modem.STATE_TIMEOUT:
			return "connect timeout";
		default:
			return String.format("STAT[%d]", status);
		}
	}
	
	/**
	 * Output log.
	 * @param msg
	 * @param ret
	 * @return
	 */
	private int logIfFail(String msg, int ret) {
		if(ret != 0 && ret != '0') {
			Log.w(TAG, "-------------["+msg+"]---------ret-----------"+ret);
		}
		return ret;
	}
	
	/**
	 * Dial up all the hosts you set untill connected one.
	 */
	public void dialUp() {
		//Check status, maybe you invoke this method before.
		switch(convertStatus(driver.getLineStatus(null))) {
		case STATE_CONNECTING:
			//Unexpectly, maybe it was started by other application.
			//Dial off first to be in charge of modem.
			if(dialThread == null || !dialThread.isAlive()) {
				driver.dialOff();
				break;
			}
			//it was connected, so do nothing.
		case STATE_CONNECTED:
			if(!isFirstUse) {
				Log.i(TAG, "---------------------- is connected, no need dial again ---------------------------");
				return;
			}
		}
		//Ok, this application use dial up method. Now we known the modem status is in charge.
		isFirstUse = false;
		
		//Start dialing up process.
		if(dialThread == null || !dialThread.isAlive()) {
			isDialing = true;
			dialThread = new Thread() {
				@Override
				public void run() {
					List<String> hostList = ModemDevice.this.hostList;
					if(hostList == null) {
						Log.w(TAG, "host list is null");
						return;
					}
					//Init modem and use sync mode.
					logIfFail("initSdlc", driver.initSdlc(bps, Modem.SDLCAD, Modem.SST));
					logIfFail("ioctl settimeout", driver.ioctl(Modem.CMD_SET_DCDTIMEOUT, "30"));
					logIfFail("ioctl snrm time", driver.ioctl(Modem.CMD_SNRM_TIME, "30"));
					
					for(int i=0; i<hostList.size(); i++) {
						String host = hostList.get(i);
						if(!isDialing) {
							break;
						}

						// First dial off, then dial new host up.
						// The operation should be success then we can listen the status to detect connecting.
						logIfFail("on dialOff before dial", driver.dialOff());
						if(Modem.ERROR_NONE != logIfFail("on dial", driver.dial("AT"+getDialWay()+prefix+host))) {
							continue;
						}
						
						// 60 sec timeout.
						int timeout = 60*1000;
						long startTime = System.currentTimeMillis();
						while(isDialing) {
							// 50 msec interval.
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
							}
							int currentStatus = driver.getLineStatus(null);
							switch(currentStatus) {
							case Modem.STATE_INPROCESS:
								continue;
							// Sometimes it has no dial tone, but connecting indeed.
							// We have to calculate timeout to detect if this status is an error.
							case Modem.STATE_NODIALTONE:
								if(startTime+timeout > System.currentTimeMillis()) {
									// Maybe fine.
									continue;
								}
								Log.d(TAG, "-----------------------dial [timeout]-----------------------"+currentStatus);
								break;
							//Connected.
							case Modem.STATE_OK:
								Log.d(TAG, "-----------------------dial end-[succ]-----------------------"+currentStatus);
								return;
							}

							break;
						}
						
						// Try next host.
						
					}
					// Fail
				}
			};
			
			dialThread.start();
		}
		else {
			Log.i(TAG, "----------------------- dialing -----------------------");
		}
	}
	
	/**
	 * Dial off phone.
	 */
	public void dialOff() {
		//If dialing process is not started, dial off directly.
		if(dialThread == null || !dialThread.isAlive()) {
			driver.dialOff();
			return;
		}
		//Wait for process ending.
		isDialing = false;
		while(dialThread.isAlive()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
		//Dial off.
		driver.dialOff();
	}
	
	/**
	 * Reset modem.
	 */
	public void reset() {
		dialOff();
		driver.reset();
	}

	/**
	 * Stop listen.
	 */
	public void stopListen() {
		if(listenStateThread != null && listenStateThread.isAlive()) {
			isListening = false;
			if(Thread.currentThread().getId() != listenStateThread.getId()) {
				while(listenStateThread.isAlive()) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	/**
	 * Read data
	 * @param data		the data buffer
	 * @param offset	offset of data buffer
	 * @param len		expect length
	 * @param timeout	msec
	 * @return			true length it read
	 * @throws CommunicateException
	 * @throws IOException
	 */
	public int read(byte[] data, int offset, int len, int timeout)
			throws CommunicateException, IOException {
		long startTime = System.currentTimeMillis();
		while(true) {
			if(startTime + timeout < System.currentTimeMillis()) {
				throw new CommunicateException("Data receive timeout");
			}
			if(!driver.isInputBufferEmpty()) {
				break;
			}
			
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
			}
		}
		
		int recvLen = driver.read(1, data, offset, len);
		if(recvLen == -2) {
			throw new CommunicateException("Data receive timeout");
		}
		
		if(recvLen == -1) {
			throw new IOException();
		}
		
		return recvLen;
	}

	/**
	 * Write data
	 * @param data		the data you want to send to remote host
	 * @param offset	offset of the data
	 * @param len		length of data will send
	 * @param timeout	msec
	 * @return	
	 * @throws CommunicateException
	 * @throws IOException
	 */
	public int write(byte[] data, int offset, int len, int timeout)
			throws CommunicateException, IOException {
		if(offset == 0 && len == data.length) {
			return driver.write(timeout/1000, data);
		}
		return driver.write(timeout/1000, BytesUtil.subBytes(data, offset, len));
	}

	/**
	 * To abort the process of reading or writing.
	 */
	public void abortIO() {
		dialOff();
	}

	/**
	 * Clear the input buffer.
	 */
	public void clearInputBuffer() {
		driver.clearInputBuffer();
	}
}
