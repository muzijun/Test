package com.zy.phone.core;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.zy.phone.core.encrypt.RSACodeHelper;
import com.zy.phone.core.params.FormParams;
import com.zy.phone.core.params.FormParams.FormConfig;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class NetManager {
	
	private static final String TAG = NetManager.class.getSimpleName();
	
	private NetManager(){};
	
	private static NetManager mNetManager = null;
	
	private Reference<Context> mReference = null;

	private NetOperator mNetOperator;

	private FormParams mFormParams;

	private Map<String , String> mPakage2AdsIdMap = new HashMap<String, String>();
	
	
	public static NetManager getInstance(){
		if(null == mNetManager){
			synchronized (NetManager.class) {
				if(null == mNetManager)
					mNetManager = new NetManager();
			}
		}
		return mNetManager;
	}
	
	/**
	 * 创建该对象之后，紧接着必须调用该方法.
	 * @param context
	 */
	public void inject(Context context, FormConfig formConfig){
		/***/
		mReference = new WeakReference<Context>(context);
		mFormParams = new FormParams(context.getApplicationContext(), formConfig);
		mNetOperator = new NetOperator(context);
	}
	
	/**
	 * 获取广告列表
	 * @param listener 请求网络回调
	 */
	public void fetchAdvertsJsonByRequestParams(IResponseListener<JSONObject> listener){
		if(null != mNetOperator){
			String requestParmas = mFormParams.getAdsListParamsMap();
			Log.d(TAG, "fetchAdvertsJsonByRequestParams: " + requestParmas);
			mNetOperator.fetchJsonByRequestParams(Constant.ADVERTS_LIST_URL, requestParmas, listener);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * 获取广告详情
	 * @param listener 请求网络回调
	 */
	public void fetchAdvertsDetailJsonByRequestParams(String adsID, IResponseListener<JSONObject> listener){
		Log.d(TAG, "download have finished, next to notify server.");
		if(null != mNetOperator){
			Map<String, String> map = mFormParams.getBaseParamsMap();
			map.put(Constant.ADVERTS_ID, adsID);
			
			mNetOperator.fetchJsonByRequestParams(Constant.ADVERTS_DETAIL_URL, Utils.transitionObj2JsonString(map), listener);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * 用户完成安装之后的调用的接口.
	 * @param listener 请求网络回调
	 */
	public void notifyServerWhenInstalled(String adsID, IResponseListener<JSONObject> listener){
		if(null != mNetOperator){
			Map<String, String> map = mFormParams.getBaseParamsMap();
			map.put(Constant.ADVERTS_ID, adsID);
			
			String obj2JsonString = Utils.transitionObj2JsonString(map);
			
			/**enable encrypt*/
			try {
				obj2JsonString = RSACodeHelper.encode(RSACodeHelper.encryptByPublicKey(obj2JsonString.getBytes()));
				
//				String decrypt = new String(RSACodeHelper.decryptByPrivateKey(RSACodeHelper.decode(obj2JsonString)));
//				Log.d(TAG, "notifyServerWhenInstalled decrypt: " + decrypt);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mNetOperator.fetchJsonByRequestParams(Constant.WHEN_HAS_INSTALLED_URL, obj2JsonString, listener);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * 用户完成任务的时候回调的接口.
	 * @param listener 请求网络回调
	 */
	public void notifyServerWhenTaskFinished(String adsID, IResponseListener<JSONObject> listener){
		if(null != mNetOperator){
			Map<String, String> map = mFormParams.getBaseParamsMap();
			map.put(Constant.ADVERTS_ID, adsID);
			String obj2JsonString = Utils.transitionObj2JsonString(map);
			
			/**enable encrypt*/
			try {
				obj2JsonString = RSACodeHelper.encode(RSACodeHelper.encryptByPublicKey(obj2JsonString.getBytes()));
				
//				String decrypt = new String(RSACodeHelper.decryptByPrivateKey(RSACodeHelper.decode(obj2JsonString)));
//				Log.d(TAG, "notifyServerWhenInstalled decrypt: " + decrypt);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mNetOperator.fetchJsonByRequestParams(Constant.WHEN_TASK_FINISHED_URL, obj2JsonString, listener);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * 根据广告ID获取下载的URL.
	 * @param listener 请求网络回调
	 */
	public void fetchApkUrlByAdsID(String adsID, String pakage, IResponseListener<JSONObject> listener){
		if(TextUtils.isEmpty(adsID) || TextUtils.isEmpty(pakage)){
			Log.d(TAG, "fetch apk url params is null...");
			return;
		}
		
		/**cache pakage and advert id.*/
		mPakage2AdsIdMap.put(pakage, adsID);
		
		if(null != mNetOperator){
			Map<String, String> map = new HashMap<String, String>();
			map.put(Constant.ADVERTS_ID, adsID);
			
			mNetOperator.fetchJsonByRequestParams(Constant.FETCH_APK_DOWNLOAD_URL, Utils.transitionObj2JsonString(map), listener);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * 根据广告ID获取任务时长
	 * @param listener 请求网络回调
	 */
	public void fetchTaskTimeByAdsID(String adsID, IResponseListener<JSONObject> listener){
		if(null != mNetOperator){
			Map<String, String> map = mFormParams.getBaseParamsMap();
			map.put(Constant.ADVERTS_ID, adsID);
			mNetOperator.fetchJsonByRequestParams(Constant.FETCH_TASK_TIME_URL, Utils.transitionObj2JsonString(map), listener);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	
	public void cancel(String url){
		if(TextUtils.isEmpty(url))
			return;
		
		boolean flag = false;
		if(null != mNetOperator)
			flag = mNetOperator.cancel(url);
			
	}
	
	public void cancelAll(){
		if(null != mNetOperator)
			mNetOperator.cancelAll();
	}
}
