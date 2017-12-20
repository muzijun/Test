package com.zy.phone;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
/**
 * ��ȡϵͳӦ�ð���
 * @author lws
 *
 */
public class PackageName {
	/**
	 * ��������
	 * @param context
	 * @return
	 */
	public static List<PackageInfo> getAllApps(Context context) {  
	    List<PackageInfo> apps = new ArrayList<PackageInfo>();  
	    PackageManager pManager = context.getPackageManager();  
	    List<PackageInfo> paklist = pManager.getInstalledPackages(0);  
	    for (int i = 0; i < paklist.size(); i++) {  
	        PackageInfo pak = (PackageInfo) paklist.get(i);  
	        if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {  
	            apps.add(pak);  
	        }  
	    }  
	    
	    return apps;  
	}
	/**
	 * ���ذ���
	 * @param context
	 * @return
	 */
	public static List<String> getStrAllApps(Context context) {  
	    List<String> apps = new ArrayList<String>();  
	    PackageManager pManager = context.getPackageManager();  
	    List<PackageInfo> paklist = pManager.getInstalledPackages(0);  
	    for (int i = 0; i < paklist.size(); i++) {  
	        PackageInfo pak = (PackageInfo) paklist.get(i);  
	        if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {  
	            apps.add(pak.applicationInfo.packageName);  
	        }  
	    }	    
	    return apps;  
	}
}
