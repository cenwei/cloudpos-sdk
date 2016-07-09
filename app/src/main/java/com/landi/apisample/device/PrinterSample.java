package com.landi.apisample.device;

import android.content.Context;
import android.util.Log;

import com.landicorp.android.eptapi.device.Printer;
import com.landicorp.android.eptapi.device.Printer.Format;
import com.landicorp.android.eptapi.exception.RequestException;
/**
 * This sample show that how to use printer.
 * @author chenwei
 *
 */
public abstract class PrinterSample extends AbstractSample {
	/**
	 * Make a print progress to design the receipt.
	 */
	private Printer.Progress progress = new Printer.Progress() {
		/*@Override
		public void doPrint(Printer printer) throws Exception {
			// Design the receipt
			Format format = new Format();
			// Use this 5x7 dot and 1 times width, 2 times height
			format.setAscSize(Format.ASC_DOT5x7);
			format.setAscScale(Format.ASC_SC1x2);						
			printer.setFormat(format);
			printer.printText("          Landi Pay\n");
			format.setAscScale(Format.ASC_SC1x1);			
			printer.setFormat(format);
			printer.printText("--Public utility bill payment receipt--\n");
			printer.printText("\n");
			printer.printText("Transaction : Repayment\n");
			printer.printText("Credit Card No.: XXXX XXXX XXXX XXXX\n");
			printer.printText("Term No.: 2200306\n");
			printer.printText("Amount: RMB 100.00\n");
			printer.printText("Reference No.: 191017234668\n");
			printer.printText("\n");
			printer.printText("---The Client Stub---\n");
			// CHS Text Format - 16x16 dot and 1 times width, 1 times height
			format.setHzScale(Format.HZ_SC1x1);
			format.setHzSize(Format.HZ_DOT16x16);
			printer.printText("-------\n");
			printer.printText("\n");	
			printer.printBarCode("8799128883");

			printer.feedLine(3);
		}*/

		@Override
		public void doPrint(Printer printer) throws Exception {
			// Design the receipt
			Format format = new Format();
			// Use this 5x7 dot and 1 times width, 2 times height
			format.setHzScale(Format.HZ_SC1x1);
			format.setHzSize(Format.HZ_DOT16x16);
			printer.printText("DEMO签购单:\n");
			printer.setFormat(format);
			printer.printText("商户名(MERCHANT NAME):\n");
			//format.setHzScale(Format.HZ_SC1x1);
			printer.setFormat(format);
			printer.printText("--Public utility bill payment receipt--\n");
			printer.printText("\n");
			printer.printText("Transaction : Repayment\n");
			printer.printText("Credit Card No.: XXXX XXXX XXXX XXXX\n");
			printer.printText("Term No.: 2200306\n");
			printer.printText("Amount: RMB 100.00\n");
			printer.printText("Reference No.: 191017234668\n");
			printer.printText("\n");
			printer.printText("---The Client Stub---\n");
			// CHS Text Format - 16x16 dot and 1 times width, 1 times height
			format.setHzScale(Format.HZ_SC1x1);
			format.setHzSize(Format.HZ_DOT16x16);
			printer.printText("-------\n");
			printer.printText("\n");
			printer.printBarCode("8799128883");

			printer.feedLine(3);
			Log.d("#PrinterSample","打印结束.");

		}
		
		@Override
		public void onCrash() {
			onDeviceServiceCrash();
		}
		
		@Override
		public void onFinish(int code) {

			/**
			 * The result is fine.
			 */
			if(code == Printer.ERROR_NONE) {
				displayPrinterInfo("正常结束打印");

			}
			/**
			 * Has some error. Here is display it, but you may want to hanle 
			 * the error such as ERROR_OVERHEAT��ERROR_BUSY��ERROR_PAPERENDED 
			 * to start again in the right time later.
			 */
			else {
				displayPrinterInfo("PRINT ERR - "+getErrorDescription(code));
			}

		}
		
		public String getErrorDescription(int code){
			switch(code) {
			case Printer.ERROR_PAPERENDED: return "Paper-out, the operation is invalid this time"; 
			case Printer.ERROR_HARDERR: return "Hardware fault, can not find HP signal"; 
			case Printer.ERROR_OVERHEAT: return "Overheat"; 
			case Printer.ERROR_BUFOVERFLOW: return "The operation buffer mode position is out of range"; 
			case Printer.ERROR_LOWVOL: return "Low voltage protect"; 
			case Printer.ERROR_PAPERENDING: return "Paper-out, permit the latter operation"; 
			case Printer.ERROR_MOTORERR: return "The printer core fault (too fast or too slow)"; 
			case Printer.ERROR_PENOFOUND: return "Automatic positioning did not find the alignment position, the paper back to its original position"; 
			case Printer.ERROR_PAPERJAM: return "paper got jammed"; 
			case Printer.ERROR_NOBM: return "Black mark not found"; 
			case Printer.ERROR_BUSY: return "The printer is busy"; 
			case Printer.ERROR_BMBLACK: return "Black label detection to black signal"; 
			case Printer.ERROR_WORKON: return "The printer power is open";
			case Printer.ERROR_LIFTHEAD: return "Printer head lift"; 
			case Printer.ERROR_LOWTEMP: return "Low temperature protect"; 
			}
			return "unknown error ("+code+")";
		}
	};
	
	public PrinterSample(Context context) {
		super(context);
		
		// Init print progress. You can also do it in 'Progress.doPrint' method, 
		// but it would not be done after all step invoked.
		progress.addStep(new Printer.Step() {
			@Override
			public void doPrint(Printer printer) throws Exception {
				// Make the print method can print more than one line.
				printer.setAutoTrunc(true);				
				// Default mode is real mode, now set it to virtual mode.
				printer.setMode(Printer.MODE_VIRTUAL);
			}
		});
	}
	
	/**
	 * Search card and show all track info
	 */
	public void startPrint() {
		try {
			progress.start();
		} catch (RequestException e) {
			e.printStackTrace();
			onDeviceServiceCrash();
		}
	}
	
	/**
	 * Add title to the beginning of receipt. It will be printed before 'Printer.Progress.doPrint'
	 */
	public void addTitle() {
		progress.addStep(new Printer.Step() {
			@Override
			public void doPrint(Printer printer) throws Exception {
				printer.setFormat(Format.asc(Format.ASC_DOT24x12, Format.ASC_SC1x2));
				printer.startUnderline();
				printer.printMid("This is the title you add!");
				printer.endUnderline();
				printer.printText("\n");
			}
		});
	}
	
	/**
	 * Add 'hello world' to the beginning of receipt. It will be printed before 'Printer.Progress.doPrint'
	 */
	public void addHelloWorld() {
		progress.addStep(new Printer.Step() {
			@Override
			public void doPrint(Printer printer) throws Exception {
				printer.setFormat(Format.asc(Format.ASC_DOT24x12, Format.ASC_SC1x3));
				printer.printMid("Hello world!");
				printer.printText("\n");
			}
		});
	}
	
	/**
	 * Display printer info 
	 * @param info
	 */
	protected abstract void displayPrinterInfo(String info);
}
