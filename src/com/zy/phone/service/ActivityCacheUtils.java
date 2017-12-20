package com.zy.phone.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActivityCacheUtils {

    private static ActivityCacheUtils instance = new ActivityCacheUtils();
 
    private ActivityCacheUtils(){}
 
    public static ActivityCacheUtils getInstance() {
        return instance;
    }
    
    //包名，对应activity活动表，根据activity的活动轨迹，判断用户有没有完成注册   //包名，对应activity活动表，根据activity的活动轨迹，判断用户有没有完成注册
    private Map<String,ArrayList<String>> caches = new HashMap<String,ArrayList<String>>();
    
    private String latestPackName;
    
    private int latestAdId;
    
    private Map<String,AdInfo> ads = new HashMap<String,AdInfo>();
    
    private Set<String> apkNames = new HashSet<String>();
    
    public void addApkNames(String apkname){
    	apkNames.add(apkname);
    }
    
    public boolean hasApkNames(String apkname){
    	if(apkNames.contains(apkname)){
    		return true;
    	}
    	return false;
    }
    
    public AdInfo getAdInfo(String packName){
    	return ads.get(packName);
    } 
    
    public void addAdInfo(String packName,AdInfo adInfo){
    	ads.put(packName,adInfo);
    } 
        
    public void set(String packName,String activityName){ 
    	if(!caches.keySet().contains(packName)){
    		ArrayList<String> activitys = new ArrayList<String>();   		
    		activitys.add(activityName);
    		caches.put(packName,activitys);   		
    	}else{
    		ArrayList<String> activitys = caches.get(packName);
    		if(!activitys.contains(activityName)){
    			activitys.add(activityName);
    		}    		
    	}    	
    	//System.out.println(caches.get(packName));
    }
      
    public boolean checkFinish(String packName){
    	AdInfo adInfo = ads.get(packName);
    	if(null == adInfo) return false;
    	List<String> list = adInfo.getActivitys();//注册必须Activity
    	ArrayList<String> activeAcList = caches.get(adInfo.getPackageName());//用户活动activity
    	boolean isfinish = false;
    	if(list!=null){
	    	for(String pack : list ){
	    		if(activeAcList != null){    			
		    		if(!activeAcList.contains(pack.trim())){
		    			isfinish = false;
		    			break;
		    		}else{
		    			isfinish = true;
		    		}
	    		}
	    	}
    	}
    	return isfinish;
    }
    
    public void remove(String packName){
     	ads.remove(packName);
    	caches.remove(packName);
    	latestPackName = "";
    	latestAdId = 0;
    }
           
    public ArrayList<String> get(String packName){
    	return caches.get(packName);
    }
         
    public String getString(String packName){
    	 ArrayList<String> list =  caches.get(packName);
    	 if(list==null)return "";
    	 String strList = "";
    	 boolean flag = false;
    	 for(String str : list){
    		 if(flag){
    			 strList = strList+";"+str;
    		 }else{
    			 strList = str;
    		 }   		 
    		 flag=true;
    	 }
    	return strList;
    }

	public String getLatestPackName() {
		return latestPackName;
	}

	public void setLatestPackName(String latestPackName) {
		this.latestPackName = latestPackName;
	}

	public int getLatestAdId() {
		return latestAdId;
	}

	public void setLatestAdId(int latestAdId) {
		this.latestAdId = latestAdId;
	} 
}
