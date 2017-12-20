package com.zy.phone.core.params;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zy.phone.core.Constant;
import com.zy.phone.core.params.PhoneSysConfig;
import com.google.gson.JsonArray;

import android.content.Context;
import android.text.TextUtils;

/**用于合成网络请求头信息**/
public class FormParams {

    private PhoneSysConfig mPhoneInfo;

    public FormParams(Context application){
        mPhoneInfo = new PhoneSysConfig(application);
    }
    
    /**
     * 获取广告列表所需的部分参数
     * @return
     */
    public String getAdsListParamsMap(String other){
    	
    	String retVal = "{}";
    	
		//--- 'true' get all install package, but not system app.
    	JSONArray jsonArray =  mPhoneInfo.getAllAppsPackage(true);
    	
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put(Constant.PACKAGE, jsonArray);
			jsonObj.put(Constant.ADP_CODE, Constant.APP_CODE);
			jsonObj.put(Constant.IMEI, mPhoneInfo.getPhoneIMEI());
			jsonObj.put(Constant.IP, mPhoneInfo.getIPAddress());
			jsonObj.put(Constant.SDK_VERSION, Constant.SDK_VERSION_CODE);
			jsonObj.put(Constant.IMSI, mPhoneInfo.getPhoneIMSI());
			jsonObj.put(Constant.ANDROID_ID, mPhoneInfo.getPhoneID());
			jsonObj.put(Constant.SYSTEM_VERSION, mPhoneInfo.getPhoneVersion());
			jsonObj.put(Constant.MODEL, mPhoneInfo.getPhoneModels());
			jsonObj.put(Constant.MAC, mPhoneInfo.getPhoneMAC());
			jsonObj.put(Constant.OPERATOR, mPhoneInfo.getOperators());
			jsonObj.put(Constant.NETTYPE, mPhoneInfo.getNetWorkType());
			jsonObj.put(Constant.BRAND, mPhoneInfo.getPhoneBrand());
			jsonObj.put(Constant.RESOLUTION, mPhoneInfo.getResolution());
			jsonObj.put(Constant.OTHER, TextUtils.isEmpty(other) ? "ArMn" : other);
			retVal = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return retVal;
    }
    
    /**
     * 获取基础手机参数.
     * @return
     */
    public Map<String, String> getBaseParamsMap(){
    	HashMap<String,String> map = new HashMap<String, String>();
		map.put(Constant.ADP_CODE, Constant.APP_CODE);
		map.put(Constant.IMEI,mPhoneInfo.getPhoneIMEI());
		return map;
    }
    
//    /**
//     * 获取广告详情所需的部分参数
//     * @return
//     */
//    public Map<String, String> getAdsDetailParamsMap(){
//		HashMap<String,String> map = new HashMap<String, String>();
//		map.put(Constant.ADP_CODE, Constant.APP_CODE);
//		map.put(Constant.IMEI, mPhoneInfo.getPhoneIMEI());
//		
//		//---get all install package, but not system app.
//		map.put(Constant.PACKAGE, mPhoneInfo.getAllAppsPackage(false));
//		
//        return map;
//    }

    public JSONObject createJsonObj(String[] _param, String[] _values) {
        JSONObject jsonObj = new JSONObject();
        try {
            for (int i = 0; i < _param.length; i++) {
                jsonObj.put(_param[i], _values[i]);
            }
        } catch (Exception e) {
            return null;
        }
        return jsonObj;
    }

    /**
     * 生成json
     * @param _param
     * @param _values
     * @return 返回String对象
     */
    public String createJsonObj2String(String[] _param, Object[] _values) {
        JSONObject jsonObj = new JSONObject();
        try {
            for (int i = 0; i < _param.length; i++) {
                jsonObj.put(_param[i], _values[i]);
            }
        } catch (Exception e) {
            return null;
        }
        return jsonObj.toString();
    }
}
