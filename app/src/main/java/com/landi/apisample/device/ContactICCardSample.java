package com.landi.apisample.device;

import com.landi.apisample.device.card.ContactCpuCard;
import com.landi.apisample.device.card.PSamCardReader;
import com.landicorp.android.eptapi.card.InsertCpuCardDriver;
import com.landicorp.android.eptapi.card.PSamCard;
import com.landicorp.android.eptapi.device.InsertCardReader;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.utils.BytesUtil;

import android.content.Context;

/**
 * This sample show that how to use Contact IC Card
 * @author chenwei
 *
 */
public abstract class ContactICCardSample extends AbstractSample {
	private InsertCpuCardDriver cpuDriver;
	private int powerupMode = InsertCpuCardDriver.MODE_DEFAULT;
	private int powerupVol = InsertCpuCardDriver.VOL_DEFAULT;
	
	/**
	 * Create a listener to listen the port of contact ic card.
	 */
	private InsertCardReader.OnSearchListener onSearchListener = new InsertCardReader.OnSearchListener() {
		
		@Override
		public void onCrash() {
			onDeviceServiceCrash();
		}
		
		@Override
		public void onFail(int code) {
			displayICInfo("SEARCH ERROR - "+getErrorDescription(code));
		}
		
		@Override
		public void onCardInsert() {
			displayICInfo("IC card detected, wait for operations.");
			cpuDriver = (InsertCpuCardDriver) InsertCardReader.getInstance().getDriver("CPU");
			cpuDriver.setPowerupMode(powerupMode);
			cpuDriver.setPowerupVoltage(powerupVol);
			
			try {
				cpuDriver.powerup(onPowerupListener);
			} catch (RequestException e) {
				e.printStackTrace();
				onDeviceServiceCrash();
			}
		}
		
		public String getErrorDescription(int code){
			if(code == ERROR_FAILED){
				return "Other error(OS error,etc)"; 
			}
			
			if(code == ERROR_TIMEOUT){			 
				return "Communication error"; 		
			}
			return "unknown error ["+code+"]";
		}
	};
	
	/**
	 * Create a listener to listen the powerup result.
	 */
	InsertCpuCardDriver.OnPowerupListener onPowerupListener = new InsertCpuCardDriver.OnPowerupListener() {
		
		@Override
		public void onCrash() {
			onDeviceServiceCrash();
		}
		
		@Override
		public void onPowerup(int protocol, byte[] art) {
			/**
			 * Powerup by PSamCard object.
			 */
			if(cpuDriver instanceof PSamCard) {
				PSamCardReader reader = new PSamCardReader(cpuDriver) {
					@Override
					public void showFinalMessage(String msg) {
						ContactICCardSample.this.displayICInfo(msg);
					}
					
					@Override
					protected void showErrorMessage(String msg) {
						ContactICCardSample.this.showErrorMessage(msg);
					}
					
					@Override
					protected void onServiceCrash() {
						onDeviceServiceCrash();
					}
					
					@Override
					protected void onDataRead(byte[] data) {
						ContactICCardSample.this.displayICInfo("KEY-A - "+BytesUtil.bytes2HexString(data));						
					}
				};

				byte[] desKey = new byte[]{(byte)0x10, (byte)0x03, (byte)0x03, (byte)0x06, (byte)0x19};
				reader.startCalcKeyA(desKey);
				return;
			}
			
			/**
			 * Create a cpu card object to do some operations.
			 */
			ContactCpuCard card = new ContactCpuCard(cpuDriver) {

				@Override
				protected void onServiceCrash() {
					ContactICCardSample.this.onDeviceServiceCrash();
				}

				@Override
				protected void showErrorMessage(String msg) {
					ContactICCardSample.this.showErrorMessage(msg);
				}

				@Override
				public void showFinalMessage(String msg) {
					ContactICCardSample.this.displayICInfo(msg);
				}
				
			};
			card.startRead();
		}
		
		@Override
		public void onFail(int code) {
			displayICInfo("POWER UP ERROR - "+getErrorDescription(code));
			if(cpuDriver instanceof PSamCard) {
				displayICInfo("There is no PSAM card in SAM1 slot, please check it!");
				return;
			}
		}
		
		public String getErrorDescription(int code){		
			switch(code) {
			case ERROR_NOPOWER : return "Hardware error";
			case ERROR_ATRERR : return "ATR error";
			case ERROR_ATRERR_S : return "ATR error in SHB mode";
			case ERROR_NOCARD : return "The card is not present";
			case ERROR_FAILED : return "Other error(OS error,etc)";
			case ERROR_ERRTYPE : return "Card type is not right";
			case ERROR_TIMEOUT : return "Communication error"; 		
			}
			return "unknown error ["+code+"]";
		}
	};
	
	public ContactICCardSample(Context context) {
		super(context);
	}

	/**
	 * Search contact cpu card.
	 */
	public void searchCpuCard() {
		try {
			// Start search with 'onSearchListener'
			InsertCardReader.getInstance().searchCard(onSearchListener);
		} catch (RequestException e) {
			e.printStackTrace();
			onDeviceServiceCrash();
		}
	}
	
	/**
	 * Start PSAM card operations.
	 * It will read the card in SAM1.
	 */
	public void readPSAMCard() {
		try {
			cpuDriver = PSamCard.getCard(1);
			
			cpuDriver.setPowerupMode(powerupMode);
			cpuDriver.setPowerupVoltage(powerupVol);
			cpuDriver.powerup(onPowerupListener);
		} catch (RequestException e) {
			e.printStackTrace();
			onDeviceServiceCrash();
		}
	}

	/**
	 * Stop contact ic card search.
	 */
	public void stopSearch() {
		try {
			InsertCardReader.getInstance().stopSearch();
		} catch (RequestException e) {
			e.printStackTrace();
			onDeviceServiceCrash();
		}
	}

	/**
	 * Display final info.
	 * @param info
	 */
	protected abstract void displayICInfo(String info);

	public void setToEMVMode() {
		powerupMode = InsertCpuCardDriver.MODE_EMV;
	}
	
	public void setToSHBMode() {
		powerupMode = InsertCpuCardDriver.MODE_SHB;
	}

	public void setToBPS19200Mode() {
		powerupMode = InsertCpuCardDriver.MODE_BPS_192;
	}

	public void setTo3VOL() {
		powerupVol = InsertCpuCardDriver.VOL_3;
	}
	
	public void setTo1_8VOL() {
		powerupVol = InsertCpuCardDriver.VOL_18;
	}

	public void setTo5VOL() {
		powerupVol = InsertCpuCardDriver.VOL_5;
	}
	
}
