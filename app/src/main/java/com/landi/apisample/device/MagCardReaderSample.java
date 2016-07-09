package com.landi.apisample.device;

import android.content.Context;

import com.landicorp.android.eptapi.device.MagCardReader;
import com.landicorp.android.eptapi.exception.RequestException;
/**
 * This sample show that how to read magnetic card
 * @author chenwei
 *
 */
public abstract class MagCardReaderSample extends AbstractSample {
	
	// Create a OnSearchListener to listen the result of search card command
	MagCardReader.OnSearchListener listener = new MagCardReader.OnSearchListener() {
		@Override
		public void onCrash() {
			// The device service has a fatal exception
			onDeviceServiceCrash();
		}
		
		@Override
		public void onFail(int code) {
			// Read code
			String errorMessage = getErrorDescription(code);
			showErrorMessage("ERROR ["+errorMessage+"]");
		}
		
		@Override
		public void onCardStriped(boolean[] hasTrack, String[] track) {
			StringBuilder infoBuilder = new StringBuilder();
			// Make info 
			for(int i=0; i<3; i++) {
				infoBuilder.append("TRACK ");
				infoBuilder.append(i+1);
				infoBuilder.append(" - exist ? ");
				infoBuilder.append(hasTrack[i]);
				
				if(hasTrack[i]) {
					infoBuilder.append(" [");
					infoBuilder.append(track[i]);
					infoBuilder.append("]");
				}
				
				infoBuilder.append("\n");
				
			}
			infoBuilder.append("\n\n");
			// Delete last '\n'
			infoBuilder.deleteCharAt(infoBuilder.length()-1);
			// Display it
			displayMagCardInfo(infoBuilder.toString());
		}

		@Override
		public boolean checkValid(int[] trackStates, String[] track) {
			// Bank card always has track2
			if(trackStates[1] != TRACK_STATE_NULL && trackStates[1]  != TRACK_STATE_OK) {
				return false;
			}
			
			// Track2 too long or too short is not allowed
			if(track[1].length() < 21 || track[1].length() > 37) {
				return false;
			}
			
			// Track2 always has '=' to seperate the card no and other info
			if(track[1].indexOf('=') < 12) {
				return false;
			}
			
			if(trackStates[1] == TRACK_STATE_NULL && trackStates[2] == TRACK_STATE_NULL) {
				return false;
			}
			return true;
		};
		
		/**
		 * Get msg of the error code for display
		 * @param code
		 * @return
		 */
		public String getErrorDescription(int code) {
			switch(code) {
			case ERROR_NODATA:
				return "no data";
			case ERROR_NEEDSTART:
				return "need restart search";//This error never happened
			case ERROR_INVALID:
				return "has invalid track";//
			}
			return "unknown error - "+code;
		}
	};
	
	public MagCardReaderSample(Context context) {
		super(context);
	}
	
	/**
	 * Search card and show all track info
	 */
	public void searchCard() {
		// start search card
		try {
			// Enable track is optional operation. The default tracks are track2 and track3.
			MagCardReader.getInstance().enableTrack(MagCardReader.TRK2|MagCardReader.TRK3);
			MagCardReader.getInstance().setLRCCheckEnabled(true);
			MagCardReader.getInstance().searchCard(listener);
			showNormalMessage("Please stripe mag card!");
		} catch (RequestException e) {
			// the device service has a fatal exception
			e.printStackTrace();
			onDeviceServiceCrash();
		}
	}
	
	/**
	 * Search card and get track2
	 */
	public void searchForTrack2() {
		// start search card
		try {
			MagCardReader.getInstance().disableTrack(MagCardReader.TRK1|MagCardReader.TRK3);
			MagCardReader.getInstance().enableTrack(MagCardReader.TRK2);
			MagCardReader.getInstance().setLRCCheckEnabled(true);
			MagCardReader.getInstance().searchCard(listener);
			showNormalMessage("Please stripe mag card!");
		} catch (RequestException e) {
			// the device service has a fatal exception
			e.printStackTrace();
			onDeviceServiceCrash();
		}
	}
	
	/**
	 * This sample is only for test. The application released must use LRC check!
	 */
	public void searchCardWithoutLRCCheck() {
		// start search card
		try {
			MagCardReader.getInstance().enableTrack(
					MagCardReader.TRK1
					|MagCardReader.TRK2
					|MagCardReader.TRK3);
			MagCardReader.getInstance().setLRCCheckEnabled(false);
			MagCardReader.getInstance().searchCard(listener);
			showNormalMessage("Please stripe mag card!");
		} catch (RequestException e) {
			// the device service has a fatal exception
			e.printStackTrace();
			onDeviceServiceCrash();
		}
	}
	
	/**
	 * Stop search if card searching is started
	 */
	public void stopSearch() {
		try {
			MagCardReader.getInstance().stopSearch();
		} catch (RequestException e) {
			// the device service has a fatal exception
			e.printStackTrace();
			onDeviceServiceCrash();
		}
	}
	
	/**
	 * Display mag card info 
	 * @param cardInfo
	 */
	protected abstract void displayMagCardInfo(String cardInfo);
}
