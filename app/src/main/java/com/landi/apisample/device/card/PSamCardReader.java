package com.landi.apisample.device.card;

import com.landicorp.android.eptapi.card.InsertCpuCardDriver;
import com.landicorp.android.eptapi.utils.BytesUtil;

/**
 * This code sample show that how to use 
 * PSAM card to generate a key-A for mifare one card app.
 * @author chenwei
 *
 */
public abstract class PSamCardReader extends ContactCpuCard {
	private byte[] desKey;

	public PSamCardReader(InsertCpuCardDriver driver) {
		super(driver);
	}

	/**
	 * Init des calculate module of PSAM card.
	 * @param next
	 */
	protected void initDesCalc(final NextStep next) {
		exchangeApdu("801A440110" + BytesUtil.bytes2HexString(this.desKey),
				new ResponseHandler() {
					@Override
					public void onResponse(byte[] responseData) {
						if (isSW1Right()) {
							next.invoke();
						} else {
							showFinalMessage("Unrecognized card!");
						}
					}
				});
	}
	
	/**
	 * Calculate data by DES
	 * @param next
	 */
	protected void calcDes(byte[] data){
		exchangeApdu("80FA010010"+BytesUtil.bytes2HexString(data), new ResponseHandler() {
			@Override
			public void onResponse(byte[] responseData) {
				if (isSW1Right()) {
					onDataRead(BytesUtil.subBytes(responseData, 0, 4));
				}
				else {
					showFinalMessage("Unrecognized card!");
				}
			}
		});			
	}
	
	/**
	 * Calculate TAC.
	 * If success, TAC data will return by 'onDataRead'.
	 * @param key
	 * @param data
	 */
	public void startCalcTac(byte[] key, final byte[] data){
		this.desKey = key;
		initDesCalc(new NextStep(){
			@Override
			public void invoke() {
				calcDes(data);
			}
		});
	}
	
	/**
	 * Calculate Key-A.
	 * If success, key data will return by 'onDataRead'.
	 * @param key
	 * @param data
	 */
	public void startCalcKeyA(byte[] key) {
		this.desKey = key;
		
		selectMFile(new NextStep() {
			@Override
			public void invoke() {
				calcKeyA();
			}
		});

	}
	
	/**
	 * final operation of key-a calculation 
	 */
	protected void calcKeyA() {
		if(desKey.length != 17){
			showFinalMessage("The des key is not valid!");
			return;
		}
		
		exchangeApdu("80FC010111" + BytesUtil.bytes2HexString(desKey), new ResponseHandler() {
			@Override
			public void onResponse(byte[] responseData) {
				if (isSW1Right()) {
					onDataRead(BytesUtil.subBytes(responseData, 0, 30));
				}
				else {
					showFinalMessage("Unrecognized card!");
				}
			}
		});		
	}	
	
	/**
	 * Response TAC or Key-A.
	 * @param data
	 */
	protected abstract void onDataRead(byte[] data);

}
