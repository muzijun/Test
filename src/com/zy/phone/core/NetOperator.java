package com.zy.phone.core;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.zy.phone.core.params.NetBzip;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class NetOperator extends AbstractOperator{

	private RequestQueue mNewRequestQueue;
	
	private static final Handler mHandler= new Handler();
	
	public NetOperator(Context context){
		super(context);
		mNewRequestQueue = Volley.newRequestQueue(context);
	}
	
	
	/**
	 * 根据指定参数和URl完成相应的参数请求.
	 * @param url
	 * @param map
	 * @param listener
	 */
	public void fetchJsonByRequestParams(final String url,final String jsonString,final IResponseListener<JSONObject> listener){
		
		new Thread(){
			public void run() {
				JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonString/*new JSONObject(map)*/, new Listener<JSONObject>() {

					@Override
					public void onResponse(final JSONObject jsonObject) {
						// TODO Auto-generated method stub
						if(mapCache.containsKey(url))
							mapCache.remove(url);
						
						
						if(null != mReference && null != mReference.get() && null != listener){
							mHandler.post(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									listener.onResponse(jsonObject);
								}
							});
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError error) {
						// TODO Auto-generated method stub
						if(mapCache.containsKey(url))
							mapCache.remove(url);
						
						
						if(null != mReference && null != mReference.get() && null != listener){
							mHandler.post(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									listener.onErrorResponse(error);
								}
							});
						}
					}
				}){
					
					@Override
					public void cancel() {
						// TODO Auto-generated method stub
						if(mapCache.containsKey(url))
							mapCache.remove(url);
						
						if(null != mReference && null != mReference.get() && null != listener){
							mHandler.post(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									listener.cancel();
								}
							});
						}
						super.cancel();
					}
				};
				jsonObjectRequest.setTag(NetOperator.class);
				Request<JSONObject> request = mNewRequestQueue.add(jsonObjectRequest);
				/**用于处理取消操作使用.*/
				NetBzip netBzip = new NetBzip();
				netBzip.obj = request;
				mapCache.put(url, netBzip);
			};
		}.start();
	}
	@Override
	public void cancelAll(){
		if(null != mNewRequestQueue)
			mNewRequestQueue.cancelAll(NetOperator.class);
	}
	@Override
	public boolean cancel(String url){
		if(mapCache.size() <= 0)
			return false;
		
		if(mapCache.containsKey(url)){
			NetBzip netBzip = mapCache.get(url);
			if(null != netBzip && null != netBzip.obj){
				netBzip.obj.cancel();
				return true;
			}
		}
		return false;
	}
}
