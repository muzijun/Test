package com.zy.phone.net;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.zy.phone.Compression;
import com.zy.phone.JsonMerge;
import com.zy.phone.PhoneInfo;
import com.zy.phone.RSACodeHelper;
import com.zy.phone.Variable;

/**
 * 参数
 * 
 * @author Administrator
 * 
 */
public class GetJson {
	private Context context;
	private PhoneInfo phone;
	private static SharedPreferences zy_init;

	public GetJson(Context context) {
		this.context = context;
		phone = new PhoneInfo(context);
		zy_init = context.getSharedPreferences("zy_init", context.MODE_PRIVATE);
	}

	/**
	 * 初始化接口
	 * 
	 * @return
	 */
	public String setinitjson() {
		String result = null;
		try {
			String[] _param = { "IMEI", "IMSI", "AndroidId", "Phone", "Other",
					"Version", "Model", "NetType", "SysVer", "Operator",
					"AppCode", "Mac","Brand","Resolution" };
			String[] _values = { phone.getIMEI(), phone.getIMSI(),
					phone.getId(), "", zy_init.getString("Other", ""),
					Variable.VERSION, phone.getPhoneModels(),
					phone.getNetWorkType(), phone.getPhoneVersion(),
					phone.getOperators(), zy_init.getString("AppCode", ""),
					phone.getMAC(),phone.getPhoneBrand(),phone.getResolution()};
			JSONObject param = JsonMerge.createJsonObj(_param, _values);
			String[] _PARAM = { "FUNC", "PARAM" };
			Object[] _VALUES = { "101001", param };
			result = JsonMerge.createJsonObjString1(_PARAM, _VALUES);
		} catch (Exception e) {
			result = "";
		}
		return RSACodeHelper.base64Enc(Compression
				.getGZipCompressed(RSACodeHelper.sPubEncrypt(result)));

	}
	
	/**
	 * 初始化接口
	 * 
	 * @return
	 */
	public String setinitjson2() {
		String result = null;
		try {
			String[] _param = { "IMEI", "IMSI", "AndroidId", "Phone", "Other",
					"Version", "Model", "NetType", "SysVer", "Operator",
					"AppCode", "Mac" };
			String[] _values = { phone.getIMEI(), phone.getIMSI(),
					phone.getId(), "", zy_init.getString("Other", ""),
					Variable.VERSION, phone.getPhoneModels(),
					phone.getNetWorkType(), phone.getPhoneVersion(),
					phone.getOperators(), zy_init.getString("AppCode", ""),
					phone.getMAC() };
			JSONObject param = JsonMerge.createJsonObj(_param, _values);
			String[] _PARAM = { "FUNC", "PARAM" };
			Object[] _VALUES = { "101001", param };
			result = JsonMerge.createJsonObjString1(_PARAM, _VALUES);
		} catch (Exception e) {
			result = "";
		}
		return RSACodeHelper.base64Enc(Compression
				.getGZipCompressed(RSACodeHelper.sPubEncrypt(result)));

	}

	/**
	 * 安装接口
	 * 
	 * @return
	 */
	public String getBootPackageName(String AdsId) {
		// PARAM参数
		String[] _param = { "AdsId", "AppCode" };

		String[] _values = { AdsId, zy_init.getString("AppCode", "") };
		JSONObject param = JsonMerge.createJsonObj(_param, _values);
		// 外层参数
		String[] _PARAM = { "FUNC", "Token", "PARAM" };

		Object[] _VALUES = { "201002", zy_init.getString("Token", ""), param };
		// 最终参赛
		String result = JsonMerge.createJsonObjString1(_PARAM, _VALUES);

		return RSACodeHelper.base64Enc(Compression
				.getGZipCompressed(RSACodeHelper.sPubEncrypt(result)));
	}

	/**
	 * 广告包接口
	 * 
	 * @return
	 */
	public static String getPackageNameOverdue(String param) {
		// 外层参数
		String[] _PARAM = { "FUNC", "PARAM" };

		Object[] _VALUES = { "201003", param };
		// 最终参赛
		String result = JsonMerge.createJsonObjString1(_PARAM, _VALUES);
		return RSACodeHelper.base64Enc(Compression
				.getGZipCompressed(RSACodeHelper.sPubEncrypt(result)));
	}

	/**
	 * 激活接口
	 * 
	 * @param AdsId
	 * @return
	 */
	public String sendTask(String AdsId) {
		// PARAM参数
		String[] _param = { "AdsId", "AppCode" };

		String[] _values = { AdsId, zy_init.getString("AppCode", "") };
		JSONObject param = JsonMerge.createJsonObj(_param, _values);
		// 外层参数
		String[] _PARAM = { "FUNC", "Token", "PARAM" };

		Object[] _VALUES = { "201001", zy_init.getString("Token", ""), param };
		// 最终参赛
		String result = JsonMerge.createJsonObjString1(_PARAM, _VALUES);
		return RSACodeHelper.base64Enc(Compression
				.getGZipCompressed(RSACodeHelper.sPubEncrypt(result)));
	}

	/**
	 * 查看手机积分
	 * 
	 * @return
	 */
	public static String checkIntegral() throws Exception {
		String[] _param = { "AppCode" };
		String[] _values = { zy_init.getString("AppCode", "") };
		JSONObject param = JsonMerge.createJsonObj(_param, _values);
		// 外层参数
		String[] _PARAM = { "FUNC","Token", "PARAM" };

		Object[] _VALUES = { "101002", zy_init.getString("Token", ""), param };
		// 最终参赛
		String result = JsonMerge.createJsonObjString1(_PARAM, _VALUES);
		return RSACodeHelper.base64Enc(Compression
				.getGZipCompressed(RSACodeHelper.sPubEncrypt(result)));
	}

	/**
	 * 增加手机积分
	 * @param integral
	 * @return
	 */
	public static String addIntegral(String integral) throws Exception {
		String[] _param = { "AppCode", "Integral" };
		String[] _values = { zy_init.getString("AppCode", ""), integral };
		JSONObject param = JsonMerge.createJsonObj(_param, _values);
		// 外层参数
		String[] _PARAM = { "FUNC", "Token", "PARAM" };

		Object[] _VALUES = { "101003", zy_init.getString("Token", ""), param };
		// 最终参赛
		String result = JsonMerge.createJsonObjString1(_PARAM, _VALUES);
		return RSACodeHelper.base64Enc(Compression
				.getGZipCompressed(RSACodeHelper.sPubEncrypt(result)));
	}

	/**
	 * 扣除积分
	 * 
	 * @param integral
	 * @return
	 */
	public static String minusIntegral(String integral) throws Exception {
		String[] _param = { "AppCode", "Integral" };
		String[] _values = { zy_init.getString("AppCode", ""), integral };
		JSONObject param = JsonMerge.createJsonObj(_param, _values);
		// 外层参数
		String[] _PARAM = { "FUNC", "Token", "PARAM" };

		Object[] _VALUES = { "101004", zy_init.getString("Token", ""), param };
		// 最终参赛
		String result = JsonMerge.createJsonObjString1(_PARAM, _VALUES);
		return RSACodeHelper.base64Enc(Compression
				.getGZipCompressed(RSACodeHelper.sPubEncrypt(result)));
	}
}
