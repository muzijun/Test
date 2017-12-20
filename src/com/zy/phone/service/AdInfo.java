package com.zy.phone.service;

import java.util.List;

public class AdInfo {
	
	private Integer adId;
	
	private String packageName;
	
	private String appName;
	
	private int taskTime = 300; //默认300秒
	
	private String taskInfo;
	
	private String step;
	
	private int  exeTime;
	
	private String shortMessage;
	
	private String apkUrl;
	
	private boolean isRegister;
	
	private List<String> activitys;//注册流程Activity
	
	private boolean openFlag;
	
	private boolean alertFlag;
	
	private int tryTimes;
	
	//版本5.1.1之后，记录打开应用的时间
	
	private long startTime;

	public Integer getAdId() {
		return adId;
	}

	public void setAdId(Integer adId) {
		this.adId = adId;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public int getTaskTime() {
		return taskTime;
	}

	public void setTaskTime(int taskTime) {
		this.taskTime = taskTime;
	}

	public String getTaskInfo() {
		return taskInfo;
	}

	public void setTaskInfo(String taskInfo) {
		this.taskInfo = taskInfo;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public int getExeTime() {
		return exeTime;
	}

	public void setExeTime(int exeTime) {
		this.exeTime = exeTime;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public void setShortMessage(String shortMessage) {
		this.shortMessage = shortMessage;
	}

	public boolean isRegister() {
		return isRegister;
	}

	public void setRegister(boolean isRegister) {
		this.isRegister = isRegister;
	}

	public List<String> getActivitys() {
		return activitys;
	}

	public void setActivitys(List<String> activitys) {
		this.activitys = activitys;
	}

	public boolean isOpenFlag() {
		return openFlag;
	}

	public void setOpenFlag(boolean openFlag) {
		this.openFlag = openFlag;
	}

	public boolean isAlertFlag() {
		return alertFlag;
	}

	public void setAlertFlag(boolean alertFlag) {
		this.alertFlag = alertFlag;
	}

	public String getApkUrl() {
		return apkUrl;
	}

	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}

	public int getTryTimes() {
		return tryTimes;
	}

	public void setTryTimes(int tryTimes) {
		this.tryTimes = tryTimes;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}		
}
