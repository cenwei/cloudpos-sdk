package com.landi.apisample.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

public abstract class AbstractSample {
	private Context context;
	public AbstractSample(Context context) {
		this.context = context;
	}

	@SuppressLint("ShowToast")
	protected void showNormalMessage(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	@SuppressLint("ShowToast")
	protected void showErrorMessage(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	protected abstract void onDeviceServiceCrash();
}
