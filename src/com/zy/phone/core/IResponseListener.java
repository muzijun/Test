package com.zy.phone.core;

import com.android.volley.VolleyError;

public interface IResponseListener<T> {
	
	void onResponse(T t);

	void onErrorResponse(VolleyError error);
	
	void cancel();
}
