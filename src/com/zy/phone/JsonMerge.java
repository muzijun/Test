package com.zy.phone;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;

public class JsonMerge {
	/**
	 * ����json
	 * @param _param
	 * @param _values
	 * @return ����json����
	 */
	public static JSONObject createJsonObj(String[] _param, String[] _values) {
		JSONObject jsonobj = new JSONObject();
		try {
			for (int i = 0; i < _param.length; i++) {
				jsonobj.put(_param[i], _values[i]);
			}
		} catch (Exception e) {
			return null;
		}
		return jsonobj;
	}
	/**
	 * ����json
	 * @param _param
	 * @param _values
	 * @return ����json����
	 */
	public static JSONObject createJsonObj(List<String> _param, List<String> _values) {
		JSONObject jsonobj = new JSONObject();
		try {
			for (int i = 0; i < _param.size(); i++) {
				jsonobj.put(_param.get(i), _values.get(i));
			}
		} catch (Exception e) {
			return null;
		}
		return jsonobj;
	}
	/**
	 * ����json
	 * @param _param
	 * @param _values
	 * @return ����String����
	 */
	public static String createJsonArrString(String[] _values) {
		JSONArray jsonarr = new JSONArray();
		for (int i = 0; i < _values.length; i++) {
			jsonarr.put(_values[i]);
		}
		return jsonarr.toString();
	}
	/**
	 * ����json
	 * @param _param
	 * @param _values
	 * @return ����String����
	 */
	public static String createJsonObjString1(String[] _param, Object[] _values) {
		JSONObject jsonobj = new JSONObject();
		try {
			for (int i = 0; i < _param.length; i++) {
				jsonobj.put(_param[i], _values[i]);
			}
		} catch (Exception e) {
			return null;
		}
		return jsonobj.toString();
	}
	/**
	 * ����jsonarr
	 * @param strJsonarr 
	 * @return list
	 */
	public static List<String> analysisJsonArrString(String strJsonarr){
		JSONArray jsonarr;
		List<String> listjson=new ArrayList<String>();
		try {
			 jsonarr = new JSONArray(strJsonarr);
			for(int i=0;i<jsonarr.length();i++){
				listjson.add((String) jsonarr.get(i));
			}
		} catch (Exception e) {
		}
		return listjson;
		
		
	}
	
	/**
	 * ����json
	 * @param _param
	 * @param _values
	 * @return ����String����
	 */
	public static String getJsonObjString(Activity act, String jsonobj) {
		JSONObject returnjson = null;
		String Code = "";
		String Param = "";
		try {
			returnjson = new JSONObject(jsonobj);
			Code = returnjson.getString("Code");
			if (Code.equals("200")) {
				Param = returnjson.getString("Param");
			} 
		} catch (Exception e) {
			return Param;
		}
		return Param;
	}
}
