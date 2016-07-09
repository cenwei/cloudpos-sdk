package com.landi.apisample.device.card;

import com.landicorp.android.eptapi.card.InsertCpuCardDriver;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.utils.BytesUtil;

/**
 * This code sample show how to exchange APDU with the cpu card.
 * But this code inside the operation is not complete for a real cpu card.
 * 
 * @author chenwei
 *
 */
public abstract class ContactCpuCard extends InsertCpuCardDriver.OnExchangeListener{
	private InsertCpuCardDriver driver;
	private ResponseHandler respHandler;
	private int CARD_SW1;
	private int CARD_SW2;
	
	public ContactCpuCard(InsertCpuCardDriver driver) {
		this.driver = driver;
	}

	public void startRead() {
		selectMFile(new NextStep() {
			@Override
			public void invoke() {
				selectDF(new NextStep() {
					@Override
					public void invoke() {
						//If you want to finish this process
						powerdown();
					}

				});
			}
		});
	}
	
	/**
	 * Show error message in reading process.
	 * @param msg
	 */
	protected abstract void showErrorMessage(String msg);
	
	/**
	 * Select MFile
	 * @param next
	 */
	protected void selectMFile(final NextStep next) {
		exchangeApdu("00A40000023F0000", new ResponseHandler() {
			@Override
			public void onResponse(byte[] responseData) {
				next.invoke();
			}
		});
	}
	
	/**
	 * Select DFile
	 * @param next
	 */
	protected void selectDF(final NextStep next) {
		exchangeApdu("00B096000020", new ResponseHandler() {
			@Override
			public void onResponse(byte[] responseData) {
				next.invoke();
			}
		});
	}

	@Override
	public void onFail(int errorCode) {
		showFinalMessage("ERROR - "+getErrorDescription(errorCode));
	}
	
	/**
	 * 
	 * @param msg
	 */
	public abstract void showFinalMessage(String msg);

	@Override
	public void onSuccess(byte[] responseData) {
		if (respHandler != null) {
			CARD_SW1 = responseData[responseData.length - 2] & 0xff;
			CARD_SW2 = responseData[responseData.length - 1] & 0xff;
			respHandler.onResponse(responseData);
		}
	}
	
	/**
	 * To operate the card through the exchange of apdu.
	 * @param apdu
	 */
	protected void exchangeApdu(String apdu, ResponseHandler h) {
		this.respHandler = h;
		exchangeApdu(BytesUtil.hexString2Bytes(apdu));
	}
	
	/**
	 * To operate the card through the exchange of apdu.
	 * @param apdu
	 */
	protected void exchangeApdu(byte[] apdu) {
		try {
			driver.exchangeApdu(apdu, this);
		} catch (RequestException e) {
			onServiceCrash();
		}
	}
	
	protected void powerdown() {
		try {
			driver.powerdown();
			showFinalMessage("Contact cpu card operate success!");
		} catch (RequestException e) {
			onServiceCrash();
		}
	}

	@Override
	public void onCrash() {
		onServiceCrash();
		
	}
	
	public String getErrorDescription(int code){
		switch(code) {
		case ERROR_DATAERR : return "Read response data error"; 					 
		case ERROR_NOPOWER : return "The card does not power up"; 					 
		case ERROR_NOCARD : return "The card is not present"; 
		case ERROR_SWDIFF : return "SW1!=0X90 or SW2!=0X00";
		case ERROR_FAILED : return "Other error(OS error,etc)"; 					 
		case ERROR_ERRTYPE : return "Card type is not right"; 					 
		case ERROR_TIMEOUT : return "Communication error"; 			 
		}
		return "unknown error ["+code+"]";
	}
	
	protected boolean isSW1Right() {
		return CARD_SW1 == 0x90;
	}
	
	protected int getSW2() {
		return CARD_SW2;
	}
	
	protected int getSW1() {
		return CARD_SW1;
	}
	
	protected abstract void onServiceCrash();
	
	
	protected interface ResponseHandler {
		void onResponse(byte[] responseData);
	}
	
	protected interface NextStep {
		void invoke();
	}
}
