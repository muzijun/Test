package com.zy.phone;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
/**
 * 获取系统应用包名
 * @author lws
 *
 */
public class PackageName {
	/**
	 * 返回数据
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
	 * 返回包名
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
