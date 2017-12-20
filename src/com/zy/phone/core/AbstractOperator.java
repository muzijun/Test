package com.zy.phone.core;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.zy.phone.core.params.NetBzip;

import android.content.Context;

	public abstract class AbstractOperator {
		
	protected Reference<Context> mReference = null;
	
	protected Map<String, NetBzip> mapCache = new HashMap<String, NetBzip>();
	
	public AbstractOperator(Context context){
		mReference = new WeakReference<Context>(context);
	}
	
	public abstract void cancelAll();
	 
	public abstract boolean cancel(String url);
}
