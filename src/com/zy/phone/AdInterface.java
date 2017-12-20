package com.zy.phone;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.zy.phone.service.ActivityCacheUtils;
import com.zy.phone.service.AdInfo;
import com.zy.phone.sqline.SqlPackageName;

/**
 * android和js交互接口
 * 
 * @author lws
 * 
 */
public class AdInterface {
	// 要交换的控件
	private WebView webview;
	private Activity activity;
	private PhoneInfo phoneInfo;
	//
	//
	private SharedPreferences sp_packageName;
	//
	private SharedPreferences.Editor editor;
	//
	private SharedPreferences prefs;
	// 保存包名下载时间

	private SharedPreferences prefs_time;
	// 下载管理
	private DownloadManager downloadManager;
	// 保存路径
	private String sdcard = Environment.getExternalStorageDirectory() + "";
	// 下载标示id
	private static final String DL_ID = "downloadId";
	//
	private DownloadChangeObserver downloadObserver;
	//
	private static final Uri CONTENT_URI = Uri
			.parse("content://downloads/my_downloads");
	private Handler handler;

	private SqlPackageName apn;

	/**
	 * 初始化
	 * 
	 * @param activity
	 * @param webview
	 * @param handler
	 */
	public AdInterface(Activity activity, WebView webview, Handler handler) {
		this.activity = activity;
		this.webview = webview;
		this.handler = handler;
		phoneInfo = new PhoneInfo(activity);
		prefs = activity.getPreferences(activity.MODE_PRIVATE);// PreferenceManager.getDefaultSharedPreferences(activity);

	}

	@JavascriptInterface
	public String getBrand(){
		return phoneInfo.getPhoneBrand();
	}
	
	@JavascriptInterface
	public String getPhoneVersion(){
		return phoneInfo.getPhoneVersion();
	}
	
	@JavascriptInterface
	public String getResolution(){
		return phoneInfo.getResolution();
	}
	
	/**
	 * 下载计算
	 * 
	 * @param d1
	 * @param d2
	 * @param scale
	 *            保存几位小数
	 * @return
	 */
	private double div(double d1, double d2, int scale) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.divide(bd2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 获取包名
	 * 
	 * @return 将系统应用包名发给页面
	 */
	@JavascriptInterface
	public String getPackageName() {
		List<String> packagename = getMerge(getSqlName(), getSystemName());
		String json_packagename = "[";
		for (int i = 0; i < packagename.size(); i++) {
			if (i == packagename.size() - 1) {
				json_packagename += "\"" + packagename.get(i) + "\"";
			} else {
				json_packagename += "\"" + packagename.get(i) + "\",";
			}
		}
		json_packagename += "]";
		return json_packagename;
	}

	/**
	 * 保存信息
	 * 
	 * @param packageName
	 *            包名
	 * @param AdId
	 *            广告id
	 * @param taskTime
	 *            任务时间
	 * @param step
	 *            第几次任务
	 * @return
	 */
	@JavascriptInterface
	public boolean saveAdDel(String packageName, String AdId, String taskTime,
			String step) {
		String add[] = step.split(";");
		if (!add[0].equals("1")) {
			add[1] = "0";
		}
		sp_packageName = activity.getSharedPreferences("zy_packageName",
				activity.MODE_PRIVATE);
		editor = sp_packageName.edit();
		editor.putString("short_message", add[1]);
		editor.commit();

		if (!sp_packageName.getString(AdId, "").equals(add[0] + packageName)) {
			if (sp_packageName.getString(packageName, "").equals("")) {
				editor.putString(AdId, add[0] + packageName);
				editor.putString(packageName, taskTime);
				editor.putString(packageName + taskTime, AdId);
				editor.commit();
				return false;
			} else {
				String tasktime = sp_packageName.getString(packageName, "");
				if (!tasktime.equals(taskTime)) {
					editor.putString(AdId, add[0] + packageName);
					editor.putString(packageName, taskTime);
					editor.putString(packageName + taskTime, AdId);
					editor.commit();
					return true;
				}
			}
		}
		return true;
	}
	
	/**
	 * 保存信息
	 * 版本1.1.2及
	 * @param packageName
	 *            包名
	 * @param AdId
	 *            广告id
	 * @param taskTime
	 *            任务时间
	 * @param step
	 *            第几次任务
	 * @return
	 */
	@JavascriptInterface
	public boolean saveAdDel(String packageName,String AdId, String taskTime,String stepp,String appName,String taskInfo,String activitys) {
		String add[] = stepp.split(";");
		if (!add[0].equals("1")) {
			add[1] = "0";
		}
		String step = add[0];
		String shortMessage = add[1];
		//初始化监听数据
		AdInfo adInfo = ActivityCacheUtils.getInstance().getAdInfo(packageName);
		if(null == adInfo){
			adInfo = new AdInfo();
			adInfo.setPackageName(packageName);
			ActivityCacheUtils.getInstance().addAdInfo(packageName,adInfo);
		}
		if(!step.equals(adInfo.getStep())){
			adInfo.setAdId(Integer.valueOf(AdId));
			adInfo.setTaskTime(Integer.valueOf(taskTime));
			adInfo.setStep(step);
			adInfo.setAppName(appName); //应用名称
			adInfo.setTaskInfo(taskInfo); //任务描述
			adInfo.setShortMessage(shortMessage);
			if(!shortMessage.equals("0") && !shortMessage.trim().equals("")){
				adInfo.setRegister(true);
			}else{
				adInfo.setRegister(false);
			}
			//若为注册，则监听Activity活动路径
			if(adInfo.isRegister()){
				if(activitys != null && !activitys.trim().equals("")){
					String[] array = activitys.split(";");
					ArrayList<String> list = new ArrayList<String>();
					for(String str : array){				
						list.add(str);
					}
					adInfo.setActivitys(list);
				}
			}
			//5.0以上版本都为激活
			if (Build.VERSION.SDK_INT >= 20) {
				//adInfo.setRegister(false);
			}
		}
		sp_packageName = activity.getSharedPreferences("zy_packageName",
				activity.MODE_PRIVATE);
		editor = sp_packageName.edit();
		editor.putString("short_message", add[1]);
		editor.commit();

		if (!sp_packageName.getString(AdId, "").equals(add[0] + packageName)) {
			if (sp_packageName.getString(packageName, "").equals("")) {
				editor.putString(AdId, add[0] + packageName);
				editor.putString(packageName, taskTime);
				editor.putString(packageName + taskTime, AdId);
				editor.commit();
				return false;
			} else {
				String tasktime = sp_packageName.getString(packageName, "");
				if (!tasktime.equals(taskTime)) {
					editor.putString(AdId, add[0] + packageName);
					editor.putString(packageName, taskTime);
					editor.putString(packageName + taskTime, AdId);
					editor.commit();
					return true;
				}
			}
		}
		return true;

	}

	/**
	 * 关闭广告
	 */
	@JavascriptInterface
	public void getCloseAd() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				activity.finish();
			}
		});

	}

	/**
	 * 残留文件
	 * 
	 * @param file
	 * @return
	 */
	@JavascriptInterface
	public String isAdFile(String file) {
		JSONArray jsonarr = new JSONArray();
		List<String> filepath = JsonMerge.analysisJsonArrString(file);
		for (int i = 0; i < filepath.size(); i++) {
			if (MyFile.existFile(sdcard + filepath.get(i))) {
				jsonarr.put(filepath.get(i));
			}
		}
		return jsonarr.toString();
	}

	/**
	 * sd卡是否下载过
	 * 
	 * @return
	 */
	@JavascriptInterface
	public String isAdDown(String name) {
		String path = sdcard + "/zy/" + name + ".apk";
		if (MyFile.existFile(path)) {
			return "true";
		}
		return "false";

	}

	/**
	 * 下载进度
	 * 
	 * @param percentage
	 */
	@JavascriptInterface
	public void checkDownStep(final double percentage) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				webview.loadUrl("javascript:checkDownStep('" + percentage
						+ "')");
			}
		});
	}

	/**
	 * 打开app
	 * 
	 * @param packageName
	 */
	@JavascriptInterface
	public void openApp(final String packageName) {
		editor.putString("LastName", packageName);
		editor.commit();
		PackageManager packageManager = activity.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = packageManager.getPackageInfo(packageName, 0);

			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);

			List<ResolveInfo> apps = packageManager.queryIntentActivities(
					resolveIntent, 0);

			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				String className = ri.activityInfo.name;
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ComponentName cn = new ComponentName(packageName, className);
				intent.setComponent(cn);

				activity.startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 是否有这包名
	 * 
	 * @param packageName
	 * @return
	 */
	@JavascriptInterface
	public boolean isAvilible(String packageName) {
		final PackageManager packageManager = activity.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		for (int i = 0; i < pinfo.size(); i++) {
			if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
				return true;
		}
		return false;
	}
	
	
	/**
	 * 打开app
	 * 版本1.1.1之后
	 * @param packageName
	 */
	@JavascriptInterface
	public void openApp(final String packageName,final String appName) {
		editor.putString("LastName", packageName);
		editor.putString("zy."+packageName, appName);
		editor.commit();
		PackageManager packageManager = activity.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = packageManager.getPackageInfo(packageName, 0);

			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);

			List<ResolveInfo> apps = packageManager.queryIntentActivities(
					resolveIntent, 0);

			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				String className = ri.activityInfo.name;
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ComponentName cn = new ComponentName(packageName, className);
				intent.setComponent(cn);

				activity.startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 打开app
	 * 版本1.1.2之后
	 * @param packageName
	 */
	@JavascriptInterface
	public void openAppByAdId(final String packageName,final String adId) {
		//初始化监听数据
		AdInfo adInfo = ActivityCacheUtils.getInstance().getAdInfo(packageName);
		if(null != adInfo){
			adInfo.setOpenFlag(true);  //任务详情提示
			adInfo.setAlertFlag(true); //任务未完成提示
			//记录打开时间
			if(adInfo.getStartTime() ==0 ){
				adInfo.setStartTime(System.currentTimeMillis());
			}
		}
		ActivityCacheUtils.getInstance().setLatestPackName(packageName);       //最近打开包名
		ActivityCacheUtils.getInstance().setLatestAdId(Integer.valueOf(adId)); //最近打开广告ID

		//initActivityCache(packageName,taskinfo,activitys);
		editor.putString("LastName", packageName);
		editor.putString("Taskinfo", adInfo.getTaskInfo());
		editor.putString("zy."+packageName, adInfo.getAppName());
		editor.commit();
		PackageManager packageManager = activity.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = packageManager.getPackageInfo(packageName, 0);

			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);

			List<ResolveInfo> apps = packageManager.queryIntentActivities(
					resolveIntent, 0);

			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				String className = ri.activityInfo.name;
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ComponentName cn = new ComponentName(packageName, className);
				intent.setComponent(cn);
				activity.startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 老版本1.0.0-1.0.7
	 * @param apkUrl
	 *            下载地址
	 * @param appName
	 *            应用名
	 * @param apppackage
	 *            包名
	 */
	@JavascriptInterface
	public void downloadApp(String apkUrl, String appName, String apppackage) {
		try {
			String nettype = new PhoneInfo(activity).getNetWorkType();
			// 先判断是否已下载
			if (!MyFile.existFile(sdcard + "/zy/" + apppackage + ".apk")) {
				if (prefs.getLong(DL_ID, 0) == 0) {
					// 自动下载要在wifi下才下
					downloadManager = (DownloadManager) activity
							.getSystemService(activity.DOWNLOAD_SERVICE);
					Environment.getExternalStoragePublicDirectory(
							Environment.DIRECTORY_DOWNLOADS).mkdir();
					Uri uri = Uri.parse(apkUrl);
					DownloadManager.Request request = new DownloadManager.Request(
							uri);
					request.setDestinationInExternalPublicDir("zy", apppackage
							+ ".apk");
					request.setTitle(appName);
					request.setDescription(Variable.DOWNLOAD);
					request.setShowRunningNotification(true);
					request.setVisibleInDownloadsUi(true);
					long downloadId = downloadManager.enqueue(request);
					activity.registerReceiver(receiver, new IntentFilter(
							DownloadManager.ACTION_DOWNLOAD_COMPLETE));
					downloadObserver = new DownloadChangeObserver(null);
					activity.getContentResolver().registerContentObserver(
							CONTENT_URI, true, downloadObserver);
					prefs.edit().putLong(DL_ID, downloadId).commit();
					prefs.edit().putString("name", apppackage).commit();
					editor.putString("zy."+apppackage, appName).commit();
				}
			} else {
				editor.putString("zy."+apppackage, appName).commit();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.parse("file:///"
						+ Environment.getExternalStorageDirectory()
								.getAbsolutePath() + "/zy/" + apppackage
						+ ".apk"), "application/vnd.android.package-archive");
				activity.startActivity(intent);
				
				if(null != appName && !"".equals(appName.trim())){
					Toast.makeText(activity,
							"经安全检测《" + appName + "》为官方版本，请放心使用",
							Toast.LENGTH_LONG).show();	
				}else{
					Toast.makeText(activity,
							"经安全检测,该应用为官方版本，请放心使用",
							Toast.LENGTH_LONG).show();	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 新版1.0.8以上版本可用
	 * @param apkUrl
	 *            下载地址
	 * @param appName
	 *            应用名
	 * @param apppackage
	 *            包名
	 */
	@JavascriptInterface
	public void downloadApp(String apkUrl, String appName, String apppackage,
			String wifi, String time) {
		try {
			// 先判断是否已下载
			if (!MyFile.existFile(sdcard + "/zy/" + apppackage + ".apk")) {
				String nettype = new PhoneInfo(activity).getNetWorkType();
				// 自动下载要在wifi下才下
				if ((wifi.equals("0") && nettype.equals("1"))
						|| wifi.equals("1")) {
					if (prefs.getLong(DL_ID, 0) == 0) {
						downloadManager = (DownloadManager) activity
								.getSystemService(activity.DOWNLOAD_SERVICE);
						Environment.getExternalStoragePublicDirectory(
								Environment.DIRECTORY_DOWNLOADS).mkdir();
						Uri uri = Uri.parse(apkUrl);
						DownloadManager.Request request = new DownloadManager.Request(
								uri);
						// 保存在sd卡
						request.setDestinationInExternalPublicDir("zy",
								apppackage + ".apk");
						request.setTitle(appName);// 提示
						request.setDescription(Variable.DOWNLOAD);// 提示
						request.setShowRunningNotification(true);
						request.setVisibleInDownloadsUi(true);
						long downloadId = downloadManager.enqueue(request);
						activity.registerReceiver(receiver, new IntentFilter(
								DownloadManager.ACTION_DOWNLOAD_COMPLETE));
						downloadObserver = new DownloadChangeObserver(null);
						activity.getContentResolver().registerContentObserver(
								CONTENT_URI, true, downloadObserver);
						prefs.edit().putLong(DL_ID, downloadId).commit();
						prefs.edit().putString("name", apppackage).commit();
						// 保存
						prefs_time = activity.getSharedPreferences("dwontime",
								activity.MODE_PRIVATE);
						prefs_time.edit().putString(apppackage, time).commit();
						editor.putString("zy."+apppackage, appName).commit();
					}
				}
			} else {
				editor.putString("zy."+apppackage, appName).commit();				
				
										
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.parse("file:///"
						+ Environment.getExternalStorageDirectory()
								.getAbsolutePath() + "/zy/" + apppackage
						+ ".apk"), "application/vnd.android.package-archive");
				activity.startActivity(intent);
				Thread.sleep(100);				
				if(null != appName && !"".equals(appName.trim())){
					Toast.makeText(activity,
							"经安全检测《" + appName + "》为官方版本，请放心使用",
							Toast.LENGTH_LONG).show();	
				}else{
					Toast.makeText(activity,
							"经安全检测,该应用为官方版本，请放心使用",
							Toast.LENGTH_LONG).show();	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 新版1.1.2以上版本可用
	 * @param apkUrl
	 *            下载地址
	 * @param appName
	 *            应用名
	 * @param apppackage
	 *            包名
	 * @param activityList
	 * 安卓5.0以下注册的活动Activity类名，根据Activity类名的活动路径，判定用户有没有注册成功
	 */
	@JavascriptInterface
	public void downloadAppOld(String apkUrl, String appName, String apppackage,String wifi, String time, String adId) {
		
		try {		
			//初始化监听数据
			AdInfo adInfo = ActivityCacheUtils.getInstance().getAdInfo(apppackage);
			adInfo.setOpenFlag(true);  //任务详情提示
			//adInfo.setAlertFlag(true); //任务未完成提示
			adInfo.setApkUrl(apkUrl);
			adInfo.setAppName(appName);
			ActivityCacheUtils.getInstance().setLatestPackName(apppackage);       //最近打开包名
			ActivityCacheUtils.getInstance().setLatestAdId(Integer.valueOf(adId)); //最近打开广告ID

			editor.putString("Taskinfo", adInfo.getTaskInfo()).commit();
			// 先判断是否已下载
			if (!MyFile.existFile(sdcard + "/zy/" + apppackage + ".apk")) {
				String nettype = new PhoneInfo(activity).getNetWorkType();
				// 自动下载要在wifi下才下
				if ((wifi.equals("0") && nettype.equals("1"))
						|| wifi.equals("1")) {
					if (prefs.getLong(DL_ID, 0) >= 0) {
						downloadManager = (DownloadManager) activity
								.getSystemService(activity.DOWNLOAD_SERVICE);
						Environment.getExternalStoragePublicDirectory(
								Environment.DIRECTORY_DOWNLOADS).mkdir();
						Uri uri = Uri.parse(apkUrl);
						DownloadManager.Request request = new DownloadManager.Request(
								uri);
						// 保存在sd卡
						request.setDestinationInExternalPublicDir("zy",
								apppackage + ".apk");
						request.setTitle(appName);// 提示
						request.setDescription(Variable.DOWNLOAD);// 提示
						request.setShowRunningNotification(true);
						request.setVisibleInDownloadsUi(true);
						long downloadId = downloadManager.enqueue(request);
						activity.registerReceiver(receiver, new IntentFilter(
								DownloadManager.ACTION_DOWNLOAD_COMPLETE));
						downloadObserver = new DownloadChangeObserver(null);
						activity.getContentResolver().registerContentObserver(
								CONTENT_URI, true, downloadObserver);
						prefs.edit().putLong(DL_ID, downloadId).commit();
						prefs.edit().putString("name", apppackage).commit();
						// 保存
						prefs_time = activity.getSharedPreferences("dwontime",
								activity.MODE_PRIVATE);
						prefs_time.edit().putString(apppackage, time).commit();
						editor.putString("zy."+apppackage, appName).commit();
						Toast.makeText(activity,"《" + appName + "》已加入下载队列...请稍后...",Toast.LENGTH_LONG).show();	
					} 
				}
			} else {
				editor.putString("zy."+apppackage, appName).commit();				
										
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.parse("file:///"
						+ Environment.getExternalStorageDirectory().getAbsolutePath()+ "/zy/" + apppackage
						+ ".apk"), "application/vnd.android.package-archive");
				activity.startActivity(intent);
				Thread.sleep(10);			
				if(null != appName && !"".equals(appName.trim())){
					Toast.makeText(activity,
							"经安全检测《" + appName + "》为官方版本，请放心使用",
							Toast.LENGTH_LONG).show();	
				}else{
					Toast.makeText(activity,
							"经安全检测,该应用为官方版本，请放心使用",
							Toast.LENGTH_LONG).show();	
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 新版1.1.2以上版本可用
	 * @param apkUrl
	 *            下载地址
	 * @param appName
	 *            应用名
	 * @param apppackage
	 *            包名
	 * @param activityList
	 * 安卓5.0以下注册的活动Activity类名，根据Activity类名的活动路径，判定用户有没有注册成功
	 */
	@JavascriptInterface
	public void downloadApp(String apkUrl, String appName, String apppackage,String wifi, String time, String adId) {
		try {		
			//初始化监听数据
			AdInfo adInfo = ActivityCacheUtils.getInstance().getAdInfo(apppackage);
			adInfo.setOpenFlag(true);  //任务详情提示
			//adInfo.setAlertFlag(true); //任务未完成提示
			adInfo.setApkUrl(apkUrl);
			adInfo.setAppName(appName);
			ActivityCacheUtils.getInstance().setLatestPackName(apppackage);       //最近打开包名
			ActivityCacheUtils.getInstance().setLatestAdId(Integer.valueOf(adId)); //最近打开广告ID

			editor.putString("Taskinfo", adInfo.getTaskInfo()).commit();
			// 先判断是否已下载
			if (!MyFile.existFile(sdcard + "/zy/" + apppackage + ".apk")) {
				String nettype = new PhoneInfo(activity).getNetWorkType();
				// 自动下载要在wifi下才下
				if ((wifi.equals("0") && nettype.equals("1"))
						|| wifi.equals("1")) {
					if (prefs.getLong(DL_ID, 0) >= 0) {
						downloadManager = (DownloadManager) activity
								.getSystemService(activity.DOWNLOAD_SERVICE);
						Environment.getExternalStoragePublicDirectory(
								Environment.DIRECTORY_DOWNLOADS).mkdir();
						Uri uri = Uri.parse(apkUrl);
						DownloadManager.Request request = new DownloadManager.Request(
								uri);
						// 保存在sd卡
						request.setDestinationInExternalPublicDir("zy",
								apppackage + ".apk");
						request.setTitle(appName);// 提示
						request.setDescription(Variable.DOWNLOAD);// 提示
						request.setShowRunningNotification(true);
						request.setVisibleInDownloadsUi(true);
						long downloadId = downloadManager.enqueue(request);
						activity.registerReceiver(receiver, new IntentFilter(
								DownloadManager.ACTION_DOWNLOAD_COMPLETE));
						downloadObserver = new DownloadChangeObserver(null);
						activity.getContentResolver().registerContentObserver(
								CONTENT_URI, true, downloadObserver);
						prefs.edit().putLong(DL_ID, downloadId).commit();
						prefs.edit().putString("name", apppackage).commit();
						// 保存
						prefs_time = activity.getSharedPreferences("dwontime",
								activity.MODE_PRIVATE);
						prefs_time.edit().putString(apppackage, time).commit();
						editor.putString("zy."+apppackage, appName).commit();
						Toast.makeText(activity,"《" + appName + "》已加入下载队列...请稍后...",Toast.LENGTH_LONG).show();	
					} 
				}
			} else {
				editor.putString("zy."+apppackage, appName).commit();														
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				
				File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/zy/" + apppackage+".apk");				
				if (Build.VERSION.SDK_INT >= 24) {
			            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			            Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName()+".fileprovider", file);
			            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
			     } else {
			            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			     }
				activity.startActivity(intent);
				Thread.sleep(10);			
				if(null != appName && !"".equals(appName.trim())){
					Toast.makeText(activity,
							"经安全检测《" + appName + "》为官方版本，请放心使用",
							Toast.LENGTH_LONG).show();	
				}else{
					Toast.makeText(activity,
							"经安全检测,该应用为官方版本，请放心使用",
							Toast.LENGTH_LONG).show();	
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 计算进度
	 */
	private void queryDownloadStatus() {
		DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterById(prefs.getLong(DL_ID, 0));
		Cursor c = downloadManager.query(query);
		if (c.moveToFirst()) {
			int status = c.getInt(c
					.getColumnIndex(DownloadManager.COLUMN_STATUS));
			int daonali = c
					.getInt(c
							.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
			int tatalsize = c.getInt(c
					.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
			checkDownStep(div(Double.valueOf(daonali),
					Double.valueOf(tatalsize), 3));
			switch (status) {
			case DownloadManager.STATUS_PAUSED:
			case DownloadManager.STATUS_PENDING:
			case DownloadManager.STATUS_RUNNING:
				break;
			case DownloadManager.STATUS_SUCCESSFUL:
				String name = prefs.getString("name", "");
				if (!"".equals(name)) {
					String appName = sp_packageName.getString("zy."+name,"");
					downloadApp("",appName, name);
					prefs.edit().clear().commit();
				}
				stopReceiver();
				break;
			case DownloadManager.STATUS_FAILED:
				stopReceiver();
				downloadManager.remove(prefs.getLong(DL_ID, 0));
				prefs.edit().clear().commit();
				break;
			}
		}
	}

	/**
	 * 获取sqline 包名
	 * 
	 * @return
	 */
	private List<String> getSqlName() {
		apn = new SqlPackageName(activity);
		List<PackageInfo> appList = PackageName.getAllApps(activity);
		List<String> packagename = new ArrayList<String>();
		String ScTypeInfo;
		if (!apn.tabIsExist("PackageName")) {
			apn.CreateTable();
		}
		try {
			ScTypeInfo = apn.GetDate("PNO=?", new String[] { "0" }, null, null,
					null);
			if (ScTypeInfo.equals("0")) {
				for (int i = 0; i < appList.size(); i++) {
					PackageInfo pinfo = appList.get(i);
					String applicationpackge = pinfo.applicationInfo.packageName;
					apn.Add(applicationpackge, "0");

				}
			} else {
				JSONArray jsonObjs = new JSONObject(ScTypeInfo)
						.getJSONArray("Data");
				for (int i = 0; i < jsonObjs.length(); i++) {
					JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
					packagename.add(jsonObj.getString("PName"));
				}
			}
			apn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return packagename;
	}

	/**
	 * 获取系统包名
	 * 
	 * @return
	 */

	private List<String> getSystemName() {
		List<String> packagename = new ArrayList<String>();
		List<PackageInfo> appList = PackageName.getAllApps(activity);

		for (int i = 0; i < appList.size(); i++) {
			PackageInfo pinfo = appList.get(i);
			String applicationpackge = pinfo.applicationInfo.packageName;
			packagename.add(applicationpackge);
		}
		return packagename;
	}

	/**
	 * 合并包名
	 * 
	 * @param sqlname
	 * @param sysname
	 * @return
	 */
	private List<String> getMerge(List<String> sqlname, List<String> sysname) {
		List<String> packagename = new ArrayList<String>();
		apn = new SqlPackageName(activity);
		packagename.addAll(sqlname);
		packagename.addAll(sysname);

		if (sqlname.size() == 0) {
			return sysname;
		}
		for (int i = 0; i < packagename.size() - 1; i++) {
			for (int j = packagename.size() - 1; j > i; j--) {
				if (packagename.get(j).equals(packagename.get(i))) {
					packagename.remove(j);
				}
			}
		}
		// 更新数据
		apn.DeleteByGUID("");
		apn.Add(packagename);
		apn.close();
		return packagename;

	}

	/**
	 * 停止监听下载进度F
	 */
	private void stopReceiver() {
		try {
			if (downloadObserver != null) {
				activity.getContentResolver().unregisterContentObserver(
						downloadObserver);
			}
			if (receiver != null) {
				activity.unregisterReceiver(receiver);
			}

		} catch (Exception e) {
		}

	}

	/**
	 * 注册监听下载进度
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			queryDownloadStatus();
		}
	};

	/**
	 * 绑定在下载监听，监听下载进度
	 * @author Administrator
	 * 
	 */
	class DownloadChangeObserver extends ContentObserver {
		public DownloadChangeObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			queryDownloadStatus();
		}

	}

	/**
	 * 弹出黑框提示
	 */
	@JavascriptInterface
	public void onHint(final String hint) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, hint, Toast.LENGTH_LONG).show();
			}
		});				
	}

}
