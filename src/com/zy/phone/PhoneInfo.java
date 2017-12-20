package com.zy.phone;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 获取手机信息
 * 
 * @author lws
 * 
 */
public class PhoneInfo {
	private TelephonyManager tm;// 手机管理类
	private Context context;
	public static final int NETWORKTYPE_INVALID = 0;
	public static final int NETWORKTYPE_WAP = 1;
	public static final int NETWORKTYPE_2G = 2;
	public static final int NETWORKTYPE_3G = 3;
	public static final int NETWORKTYPE_WIFI = 4;

	public PhoneInfo(Context context) {
		this.context = context;
		tm = (TelephonyManager) context
				.getSystemService(context.TELEPHONY_SERVICE);
	}

	/**
	 * 获取手机IMEI
	 * 
	 * @return 返回imei
	 */
	public String getIMEI() {
		String imei = tm.getDeviceId();
		return imei;
	}

	/**
	 * 获取手机IMSI
	 * 
	 * @return 返回imsi
	 */
	public String getIMSI() {
		String imsi = null;
		try {
			imsi = tm.getSubscriberId();
		} catch (Exception e) {
			imsi = "";
		}	
		return imsi;
	}
	
	

	/**
	 * 获取手机MAC
	 * 
	 * @return 返回mac
	 */
	public String getMAC() {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	/**
	 * 获取手机ID
	 * 
	 * @return 返回id
	 */
	public String getId() {
		String androidId = null;
		try {
			androidId = android.os.Build.ID;
			androidId = ""
					+ android.provider.Settings.Secure.getString(
							context.getContentResolver(),
							android.provider.Settings.Secure.ANDROID_ID);
		} catch (Exception e) {
			androidId = "";
		}
		return androidId;
	}

	/**
	 * 获取运营商
	 * 
	 * @return
	 */
	public String getOperators() {

		String ProvidersName = "";
		try {
			if (getIMSI().startsWith("46000") || getIMSI().startsWith("46002")) {
				ProvidersName = "2";
			} else if (getIMSI().startsWith("46001")) {
				ProvidersName = "3";
			} else if (getIMSI().startsWith("46003")) {
				ProvidersName = "1";
			} else {
				ProvidersName = "0";
			}
		} catch (Exception e) {
			ProvidersName = "";
		}
		return ProvidersName;
	}

	/**
	 * 获取手机版本
	 * 
	 * @return
	 */
	public String getPhoneModels() {
		String PhoneType = "";
		try {
			PhoneType = Build.MODEL;
		} catch (Exception e) {
			PhoneType = "";
		}
		return PhoneType;
	}
	
	public String getResolution(){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = displayMetrics.heightPixels;
		return String.valueOf(screenWidth)+"X"+String.valueOf(screenHeight);				
	}
	
	/**
	 * 获取手机版本
	 * 
	 * @return
	 */
	public String getPhoneBrand() {
		String PhoneType = "";
		try {
			PhoneType = Build.BRAND;
		} catch (Exception e) {
			PhoneType = "";
		}
		return PhoneType;
	}
	
	
	/**
	 * 获取手机cpu信息
	 * 
	 * @return
	 */
	public static String getCpuInfo() {
		if (Build.CPU_ABI.equalsIgnoreCase("x86")) {
			return "0";
		}
		return "1";
	}

	/**
	 * 获取手机系统版本
	 * 
	 * @return
	 */
	public String getPhoneVersion() {
		String phoneInfo = "";
		try {
			phoneInfo = android.os.Build.VERSION.RELEASE;
		} catch (Exception e) {
			phoneInfo = "";
		}
		return phoneInfo;

	}

	/**
	 * 获取网络类型
	 * 
	 * @return
	 */
	public String getNetWorkType() {
		String mNetWorkType = "";
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			String type = networkInfo.getTypeName();

			if (type.equalsIgnoreCase("WIFI")) {
				mNetWorkType = "1";
			} else if (type.equalsIgnoreCase("MOBILE")) {
				String proxyHost = android.net.Proxy.getDefaultHost();
				int inttype = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? NETWORKTYPE_3G
						: NETWORKTYPE_2G)
						: NETWORKTYPE_WAP;
				if (inttype == 1) {
					mNetWorkType = "3";
				} else if (inttype == 2) {
					mNetWorkType = "2";
				} else if (inttype == 3) {
					mNetWorkType = "3";
				}
			}
		} else {
			mNetWorkType = "";
		}

		return mNetWorkType;
	}

	private static boolean isFastMobileNetwork(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		switch (telephonyManager.getNetworkType()) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return false; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return false; // ~ 14-64 kbps
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return false; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return true; // ~ 400-1000 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return true; // ~ 600-1400 kbps
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return false; // ~ 100 kbps
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return true; // ~ 2-14 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return true; // ~ 700-1700 kbps
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return true; // ~ 1-23 Mbps
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return true; // ~ 400-7000 kbps
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return false;
		default:
			return false;
		}
	}
}
