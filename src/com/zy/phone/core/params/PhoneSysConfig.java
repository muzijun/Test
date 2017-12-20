package com.zy.phone.core.params;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.erm.integralwall.core.Constant;
import com.erm.integralwall.core.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
 * ���ߣ�liemng on 2017/3/31
 * ���䣺859686819@qq.com
 */

public class PhoneSysConfig {
    private TelephonyManager mTelephonyManager;// �ֻ�������
    private Context context;
    public static final int NETWORKTYPE_INVALID = 0;
    public static final int NETWORKTYPE_WAP = 1;
    public static final int NETWORKTYPE_2G = 2;
    public static final int NETWORKTYPE_3G = 3;
    public static final int NETWORKTYPE_WIFI = 4;

    public PhoneSysConfig(Context context) {
        this.context = context;

        mTelephonyManager = (TelephonyManager) context
                .getSystemService(context.TELEPHONY_SERVICE);
    }
    
//    public String getAllAppsPackage(boolean retain) {  
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("[");
//    	
//        PackageManager pManager = context.getPackageManager();  
//        //��ȡ�ֻ�������Ӧ��  
//        List<PackageInfo> paklist = pManager.getInstalledPackages(0);  
//        for (int i = 0; i < paklist.size(); i++) {  
//            PackageInfo pak = (PackageInfo) paklist.get(i);  
//            //�ж��Ƿ�Ϊ��ϵͳԤװ��Ӧ�ó���  
//            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0 && !retain) {  
//                // customs applications  
//            	stringBuffer.append(pak.packageName);
//            	stringBuffer.append(",");
//            }  else if(retain){
//            	stringBuffer.append(pak.packageName);
//            	stringBuffer.append(",");
//            }
//        }  
//        /**�����ѯ�ĵ�ǰ���ϲ�Ϊnull,Ӧ��ִ�����´���*/
//        if(stringBuffer.length() > 1){
//        	CharSequence subSequence = stringBuffer.subSequence(0, stringBuffer.length() -1);
//        	subSequence = subSequence + "]";
//        	return (String) subSequence;
//        }
//        
//        stringBuffer.append("]");
//        
//        return stringBuffer.toString();  
//    }  
    
    public JSONArray getAllAppsPackage(boolean retain){
    	
    	JSONArray jsonArray=new JSONArray();
    	
	    PackageManager pManager = context.getPackageManager();  
	    //��ȡ�ֻ�������Ӧ��  
	    List<PackageInfo> paklist = pManager.getInstalledPackages(0);  
	    for (int i = 0; i < paklist.size(); i++) {  
	        PackageInfo pak = (PackageInfo) paklist.get(i);  
	        if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM)<= 0 ) {  
                // customs applications  
	        	if(retain)
	        		jsonArray.put(pak.packageName);
            }  else {
            	jsonArray.put(pak.packageName);
            }
	    }  

	    jsonArray.put("test1").put("test2");

	   
		return jsonArray;
    }
    
    public String getIPAddress() {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//��ǰʹ��2G/3G/4G����
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//��ǰʹ����������
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//�õ�IPV4��ַ
                return ipAddress;
            }
        } else {
            //��ǰ����������,���������д�����
        }
        return null;
    }

    /**
     * ���õ���int���͵�IPת��ΪString����
     *
     * @param ip
     * @return
     */
    private String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * ��ȡ�ֻ�IMEI
     *
     * @return ����imei
     */
    public String getPhoneIMEI() {
        String imei = mTelephonyManager.getDeviceId();
        return imei;
    }

    /**
     * ��ȡ�ֻ�IMSI
     *
     * @return ����imsi
     */
    public String getPhoneIMSI() {
        String imsi = null;
        try {
            imsi = mTelephonyManager.getSubscriberId();
        } catch (Exception e) {
            imsi = "";
        }
        return imsi;
    }


    /**
     * ��ȡ�ֻ�MAC
     *
     * @return ����mac
     */
    public String getPhoneMAC() {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * ��ȡ�ֻ�ID
     *
     * @return ����id
     */
    public String getPhoneID() {
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
     * ��ȡ��Ӫ��
     *
     * @return
     */
    public String getOperators() {

        String ProvidersName = "";
        try {
            if (getPhoneIMSI().startsWith("46000") || getPhoneIMSI().startsWith("46002")) {
                ProvidersName = "2";
            } else if (getPhoneIMSI().startsWith("46001")) {
                ProvidersName = "3";
            } else if (getPhoneIMSI().startsWith("46003")) {
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
     * ��ȡ�ֻ��汾
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

    public String getResolution() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        return String.valueOf(screenWidth) + "X" + String.valueOf(screenHeight);
    }

    /**
     * ��ȡ�ֻ��汾
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
     * ��ȡ�ֻ�cpu��Ϣ
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
     * ��ȡ�ֻ�ϵͳ�汾
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
     * ��ȡ��������
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
