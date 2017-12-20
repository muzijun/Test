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
 * android��js�����ӿ�
 * 
 * @author lws
 * 
 */
public class AdInterface {
	// Ҫ�����Ŀؼ�
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
	// �����������ʱ��

	private SharedPreferences prefs_time;
	// ���ع���
	private DownloadManager downloadManager;
	// ����·��
	private String sdcard = Environment.getExternalStorageDirectory() + "";
	// ���ر�ʾid
	private static final String DL_ID = "downloadId";
	//
	private DownloadChangeObserver downloadObserver;
	//
	private static final Uri CONTENT_URI = Uri
			.parse("content://downloads/my_downloads");
	private Handler handler;

	private SqlPackageName apn;

	/**
	 * ��ʼ��
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
	 * ���ؼ���
	 * 
	 * @param d1
	 * @param d2
	 * @param scale
	 *            ���漸λС��
	 * @return
	 */
	private double div(double d1, double d2, int scale) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.divide(bd2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * ��ȡ����
	 * 
	 * @return ��ϵͳӦ�ð�������ҳ��
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
	 * ������Ϣ
	 * 
	 * @param packageName
	 *            ����
	 * @param AdId
	 *            ���id
	 * @param taskTime
	 *            ����ʱ��
	 * @param step
	 *            �ڼ�������
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
	 * ������Ϣ
	 * �汾1.1.2��
	 * @param packageName
	 *            ����
	 * @param AdId
	 *            ���id
	 * @param taskTime
	 *            ����ʱ��
	 * @param step
	 *            �ڼ�������
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
		//��ʼ����������
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
			adInfo.setAppName(appName); //Ӧ������
			adInfo.setTaskInfo(taskInfo); //��������
			adInfo.setShortMessage(shortMessage);
			if(!shortMessage.equals("0") && !shortMessage.trim().equals("")){
				adInfo.setRegister(true);
			}else{
				adInfo.setRegister(false);
			}
			//��Ϊע�ᣬ�����Activity�·��
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
			//5.0���ϰ汾��Ϊ����
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
	 * �رչ��
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
	 * �����ļ�
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
	 * sd���Ƿ����ع�
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
	 * ���ؽ���
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
	 * ��app
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
	 * �Ƿ��������
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
	 * ��app
	 * �汾1.1.1֮��
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
	 * ��app
	 * �汾1.1.2֮��
	 * @param packageName
	 */
	@JavascriptInterface
	public void openAppByAdId(final String packageName,final String adId) {
		//��ʼ����������
		AdInfo adInfo = ActivityCacheUtils.getInstance().getAdInfo(packageName);
		if(null != adInfo){
			adInfo.setOpenFlag(true);  //����������ʾ
			adInfo.setAlertFlag(true); //����δ�����ʾ
			//��¼��ʱ��
			if(adInfo.getStartTime() ==0 ){
				adInfo.setStartTime(System.currentTimeMillis());
			}
		}
		ActivityCacheUtils.getInstance().setLatestPackName(packageName);       //����򿪰���
		ActivityCacheUtils.getInstance().setLatestAdId(Integer.valueOf(adId)); //����򿪹��ID

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
	 * �ϰ汾1.0.0-1.0.7
	 * @param apkUrl
	 *            ���ص�ַ
	 * @param appName
	 *            Ӧ����
	 * @param apppackage
	 *            ����
	 */
	@JavascriptInterface
	public void downloadApp(String apkUrl, String appName, String apppackage) {
		try {
			String nettype = new PhoneInfo(activity).getNetWorkType();
			// ���ж��Ƿ�������
			if (!MyFile.existFile(sdcard + "/zy/" + apppackage + ".apk")) {
				if (prefs.getLong(DL_ID, 0) == 0) {
					// �Զ�����Ҫ��wifi�²���
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
							"����ȫ��⡶" + appName + "��Ϊ�ٷ��汾�������ʹ��",
							Toast.LENGTH_LONG).show();	
				}else{
					Toast.makeText(activity,
							"����ȫ���,��Ӧ��Ϊ�ٷ��汾�������ʹ��",
							Toast.LENGTH_LONG).show();	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * �°�1.0.8���ϰ汾����
	 * @param apkUrl
	 *            ���ص�ַ
	 * @param appName
	 *            Ӧ����
	 * @param apppackage
	 *            ����
	 */
	@JavascriptInterface
	public void downloadApp(String apkUrl, String appName, String apppackage,
			String wifi, String time) {
		try {
			// ���ж��Ƿ�������
			if (!MyFile.existFile(sdcard + "/zy/" + apppackage + ".apk")) {
				String nettype = new PhoneInfo(activity).getNetWorkType();
				// �Զ�����Ҫ��wifi�²���
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
						// ������sd��
						request.setDestinationInExternalPublicDir("zy",
								apppackage + ".apk");
						request.setTitle(appName);// ��ʾ
						request.setDescription(Variable.DOWNLOAD);// ��ʾ
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
						// ����
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
							"����ȫ��⡶" + appName + "��Ϊ�ٷ��汾�������ʹ��",
							Toast.LENGTH_LONG).show();	
				}else{
					Toast.makeText(activity,
							"����ȫ���,��Ӧ��Ϊ�ٷ��汾�������ʹ��",
							Toast.LENGTH_LONG).show();	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * �°�1.1.2���ϰ汾����
	 * @param apkUrl
	 *            ���ص�ַ
	 * @param appName
	 *            Ӧ����
	 * @param apppackage
	 *            ����
	 * @param activityList
	 * ��׿5.0����ע��ĻActivity����������Activity�����Ļ·�����ж��û���û��ע��ɹ�
	 */
	@JavascriptInterface
	public void downloadAppOld(String apkUrl, String appName, String apppackage,String wifi, String time, String adId) {
		
		try {		
			//��ʼ����������
			AdInfo adInfo = ActivityCacheUtils.getInstance().getAdInfo(apppackage);
			adInfo.setOpenFlag(true);  //����������ʾ
			//adInfo.setAlertFlag(true); //����δ�����ʾ
			adInfo.setApkUrl(apkUrl);
			adInfo.setAppName(appName);
			ActivityCacheUtils.getInstance().setLatestPackName(apppackage);       //����򿪰���
			ActivityCacheUtils.getInstance().setLatestAdId(Integer.valueOf(adId)); //����򿪹��ID

			editor.putString("Taskinfo", adInfo.getTaskInfo()).commit();
			// ���ж��Ƿ�������
			if (!MyFile.existFile(sdcard + "/zy/" + apppackage + ".apk")) {
				String nettype = new PhoneInfo(activity).getNetWorkType();
				// �Զ�����Ҫ��wifi�²���
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
						// ������sd��
						request.setDestinationInExternalPublicDir("zy",
								apppackage + ".apk");
						request.setTitle(appName);// ��ʾ
						request.setDescription(Variable.DOWNLOAD);// ��ʾ
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
						// ����
						prefs_time = activity.getSharedPreferences("dwontime",
								activity.MODE_PRIVATE);
						prefs_time.edit().putString(apppackage, time).commit();
						editor.putString("zy."+apppackage, appName).commit();
						Toast.makeText(activity,"��" + appName + "���Ѽ������ض���...���Ժ�...",Toast.LENGTH_LONG).show();	
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
							"����ȫ��⡶" + appName + "��Ϊ�ٷ��汾�������ʹ��",
							Toast.LENGTH_LONG).show();	
				}else{
					Toast.makeText(activity,
							"����ȫ���,��Ӧ��Ϊ�ٷ��汾�������ʹ��",
							Toast.LENGTH_LONG).show();	
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * �°�1.1.2���ϰ汾����
	 * @param apkUrl
	 *            ���ص�ַ
	 * @param appName
	 *            Ӧ����
	 * @param apppackage
	 *            ����
	 * @param activityList
	 * ��׿5.0����ע��ĻActivity����������Activity�����Ļ·�����ж��û���û��ע��ɹ�
	 */
	@JavascriptInterface
	public void downloadApp(String apkUrl, String appName, String apppackage,String wifi, String time, String adId) {
		try {		
			//��ʼ����������
			AdInfo adInfo = ActivityCacheUtils.getInstance().getAdInfo(apppackage);
			adInfo.setOpenFlag(true);  //����������ʾ
			//adInfo.setAlertFlag(true); //����δ�����ʾ
			adInfo.setApkUrl(apkUrl);
			adInfo.setAppName(appName);
			ActivityCacheUtils.getInstance().setLatestPackName(apppackage);       //����򿪰���
			ActivityCacheUtils.getInstance().setLatestAdId(Integer.valueOf(adId)); //����򿪹��ID

			editor.putString("Taskinfo", adInfo.getTaskInfo()).commit();
			// ���ж��Ƿ�������
			if (!MyFile.existFile(sdcard + "/zy/" + apppackage + ".apk")) {
				String nettype = new PhoneInfo(activity).getNetWorkType();
				// �Զ�����Ҫ��wifi�²���
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
						// ������sd��
						request.setDestinationInExternalPublicDir("zy",
								apppackage + ".apk");
						request.setTitle(appName);// ��ʾ
						request.setDescription(Variable.DOWNLOAD);// ��ʾ
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
						// ����
						prefs_time = activity.getSharedPreferences("dwontime",
								activity.MODE_PRIVATE);
						prefs_time.edit().putString(apppackage, time).commit();
						editor.putString("zy."+apppackage, appName).commit();
						Toast.makeText(activity,"��" + appName + "���Ѽ������ض���...���Ժ�...",Toast.LENGTH_LONG).show();	
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
							"����ȫ��⡶" + appName + "��Ϊ�ٷ��汾�������ʹ��",
							Toast.LENGTH_LONG).show();	
				}else{
					Toast.makeText(activity,
							"����ȫ���,��Ӧ��Ϊ�ٷ��汾�������ʹ��",
							Toast.LENGTH_LONG).show();	
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �������
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
	 * ��ȡsqline ����
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
	 * ��ȡϵͳ����
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
	 * �ϲ�����
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
		// ��������
		apn.DeleteByGUID("");
		apn.Add(packagename);
		apn.close();
		return packagename;

	}

	/**
	 * ֹͣ�������ؽ���F
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
	 * ע��������ؽ���
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			queryDownloadStatus();
		}
	};

	/**
	 * �������ؼ������������ؽ���
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
	 * �����ڿ���ʾ
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
