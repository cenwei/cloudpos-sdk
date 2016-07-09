package com.landi.apisample.device.comm;

import java.io.IOException;

import com.landicorp.android.eptapi.device.SerialPort;
import com.landicorp.android.eptapi.utils.BytesUtil;

/**
 * The code sample show how to communication by serial port.
 * @author chenwei
 *
 */
public class SerialPortConnection {
	private SerialPort serialPort;
	private int sendTimeout = 5000;	// msec
	private int recvTimeout = 5000;	// msec
	
	public SerialPortConnection(SerialPort serialPort) {
		this.serialPort = serialPort;
	}

	/**
	 * Connect to host
	 * @throws CommunicateException
	 * @throws IOException
	 */
	public void connect() throws CommunicateException, IOException {
		// open device and init
		if(SerialPort.ERROR_NONE != serialPort.open()) {
			throw new IOException();
		}
		
		serialPort.init(SerialPort.BPS_9600, SerialPort.PAR_NOPAR, SerialPort.DBS_8);
		serialPort.clearInputBuffer();
	}

	/**
	 * Disconnect
	 */
	public void disconnect() {
		serialPort.close();
	}
	
	/**
	 * Send data to host and recv response data.
	 * @param data
	 * @return
	 * @throws CommunicateException
	 * @throws IOException
	 */
	public byte[] sendAndRecv(byte[] data) throws CommunicateException, IOException {
		// make data packet	- format (0x02 datalen data 0x03 lrc)
		byte[] len = BytesUtil.hexString2Bytes(String.format("%04d", data.length));
		byte[] d = BytesUtil.merage(new byte[]{0x02}, len, data, new byte[]{0x03, 0});
		// calc lrc
		byte lrc = 0;
		for(int i=1; i<d.length-1; i++) {
			lrc ^= d[i];
		}
		
		d[d.length - 1] = lrc;
		
		if(d.length != serialPort.write(d, sendTimeout)) {
			serialPort.close();
			throw new IOException();
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// read first byte of data header in limit seconds
		long startTime = System.currentTimeMillis();
		byte[] recvHead = new byte[1];
		while(true) {
			if(1 != serialPort.read(recvHead, recvTimeout)) {
				serialPort.close();
				throw new CommunicateException("data recv timeout");
			}

			if(System.currentTimeMillis() - startTime > recvTimeout) {
				serialPort.close();
				throw new CommunicateException("data recv timeout");
			}
			
			if(recvHead[0] == 0x02){
				break;
			}

			try {
				Thread.sleep(100, 0);
			} catch (InterruptedException e) {
			}
		}

		// read data header
		byte[] recvLenBuffer = new byte[2];
		if(recvLenBuffer.length != serialPort.read(recvLenBuffer, recvTimeout)) {
			serialPort.close();
			throw new CommunicateException("data recv timeout");
		}
		// calc response data body len
		int recvLen = Integer.parseInt(BytesUtil.bytes2HexString(recvLenBuffer));
		// read data body
		byte[] respData = new byte[recvLen];
		if(respData.length != serialPort.read(respData, recvTimeout)) {
			serialPort.close();
			throw new CommunicateException("data recv timeout");
		}
		// read lrc and ending
		byte[] recvEnd = new byte[2];
		if(recvEnd.length != serialPort.read(recvEnd, recvTimeout)) {
			serialPort.close();
			throw new CommunicateException("data recv timeout");
		}
		
		if(recvEnd[0] != 0x03) {
			serialPort.close();
			throw new IOException();
		}
		
		// check lrc
		lrc = 0;
		for(int i=0; i<recvLenBuffer.length; i++) {
			lrc ^= recvLenBuffer[i];
		}
		
		for(int i=0; i<respData.length; i++) {
			lrc ^= respData[i];
		}

		lrc ^= 0x03;
		
		if(lrc != recvEnd[1]) {
			serialPort.close();
			throw new IOException();
		}
		
		return respData;
	}
}
