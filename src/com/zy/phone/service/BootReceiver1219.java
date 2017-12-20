package com.zy.phone.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.zy.phone.JsonMerge;
import com.zy.phone.MyFile;
import com.zy.phone.PackageName;
import com.zy.phone.Variable;
import com.zy.phone.net.PostThread;

/**
 * 监听安装类
 * 
 * @author Administrator
 * 
 */
public class BootReceiver1219 extends BroadcastReceiver {
	// 接口
	private PostThread pt;
	// 保存包名
	private List<String> FileNameList = new ArrayList<String>();
	// 保存包名
	private List<String> FileTimeList = new ArrayList<String>();
	// 下载包路径
	private String sdcard = Environment.getExternalStorageDirectory() + "/zy/";
	//
	private Context context;
	SharedPreferences sp_packageTime;
	SharedPreferences sp_packageName;
	//
	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			
			//服务器返回可用的包名列表，已经经过SDK卡校验
			List<String> name = JsonMerge.analysisJsonArrString((String) msg.obj);
			//系统已经安装的包名
			List<String> sysname = PackageName.getStrAllApps(context);
			/**
			 * 安装提示，如果下载了没有安装，弹出提示框让用户安装
			 */
			for (int i = 0; i < name.size(); i++) {
				//下载的时候初始化下载时间，已经安装了就不弹，一个广告一天弹一次，一次弹一个。
				String data=sp_packageTime.getString(name.get(i)+"num", "");
				if (!sysname.contains(name.get(i))&&!data.equals(getDate())) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setDataAndType(Uri.parse("file:///"
							+ Environment.getExternalStorageDirectory()
									.getAbsolutePath() + "/zy/" + name.get(i)
							+ ".apk"),
							"application/vnd.android.package-archive");
					//context.startActivity(intent);
					sp_packageTime.edit().putString(name.get(i)+"num", getDate()).commit();
					break;
				} else {
					//删除已过期的包
					MyFile.deleteFile(sdcard + name.get(i) + ".apk");
				}
			}
		}
	};
	/**
	 * 获取日期
	 * 
	 * @return
	 */
	public static String getDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		String mYear = String.valueOf(calendar.get(Calendar.YEAR)); // 获取当前年份
		String mMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);// 获取当前月份
		String mDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
		return mYear + "年" + mMonth + "月" + mDay + "日";
	}
	
	/**
	 *监听app安装，在用户安装app时，检测sd卡有没有我们的包，如果有则弹出我们的安装包给用户安装
	 *不能老弹出，一个安装包一天弹一次，一次弹一个 
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		if (sp_packageName == null) {
			//
			sp_packageName = context.getSharedPreferences("zy_packageName",
					context.MODE_PRIVATE);
		}
		if (sp_packageTime == null) {
			sp_packageTime = context.getSharedPreferences("dwontime",
					context.MODE_PRIVATE);
		} 
		// 如果是广告才通知。
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
		
			String packageName = intent.getDataString().substring(
					intent.getDataString().lastIndexOf(":") + 1,
					intent.getDataString().length());
			String AdKey = packageName
					+ sp_packageName.getString(packageName, "");
			String getAdId = sp_packageName.getString(AdKey, "");
			

			// 通知后台
			if (!getAdId.equals("")) {
				
				pt = new PostThread(context, Variable.BOOTPACKAGENAME, getAdId,
						handler);
				pt.start();
				//安装完成启动app
				Intent LaunchIntent = context.getPackageManager()
						.getLaunchIntentForPackage(packageName);
				context.startActivity(LaunchIntent);
			} else {
				// 安装别人的广告弹出，不能老弹？已经安装了就不弹，一个广告一天弹一次，一次弹一个。
				JSONArray jsonarr = new JSONArray();
				this.getFileNameList(new File(sdcard));
				try {
					for (int i = 0; i < FileNameList.size(); i++) {
						FileTimeList.add(sp_packageTime.getString(
								FileNameList.get(i), ""));
						JSONObject json = new JSONObject();
						json.put("name", FileNameList.get(i));
						json.put("time", sp_packageTime.getString(
								FileNameList.get(i), ""));
						jsonarr.put(json);
					}
					// 读取服务器判断是否广告过期
					pt = new PostThread(context, Variable.OVERDUE,
							jsonarr.toString(), handler);
					pt.start();
				} catch (JSONException e) {
				}
			}
		}
	}

	private void getFileNameList(File path) {

		// 如果是文件夹的话
		if (path.isDirectory()) {
			File files[] = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				getFileNameList(files[i]);
			}
		}
		// 如果是文件的话直接加入
		else {
			// 进行文件的处理
			String filePath = path.getAbsolutePath();
			if (filePath.indexOf("apk") > 0) {
				// 文件名
				String fileName = filePath.substring(
						filePath.lastIndexOf("/") + 1,
						filePath.lastIndexOf("."));
				// 添加
				FileNameList.add(fileName);
			}
		}
	}

}
