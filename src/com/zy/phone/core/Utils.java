package com.zy.phone.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.erm.integralwall.core.Utils;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
public class Utils {

	public static void installApp(Context context, String path) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 设置目标应用安装包路径
		intent.setDataAndType(Uri.fromFile(new File(path)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	public static boolean isAppInstalled(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		List<String> pName = new ArrayList<String>();
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
			}
		}
		return pName.contains(packageName);
	}

	public static boolean isValidApk(String path) {
		if (null == path)
			return false;
		/** .apk占4位长度 */
		if (path.length() < 4)
			return false;
		return path.endsWith(".apk");
	}

	public static boolean isApkExist(String path) {

		if (TextUtils.isEmpty(path))
			return false;

		File file = new File(path);
		return file.exists();
	}

	public static String transitionObj2JsonString(Map<String, String> map) {
		JSONObject jsonObject = new JSONObject(map);
		return jsonObject.toString();
	}

	// private static String getTopRunningPkgNameAboveAndroidL2(Context context,
	// long time_ms) {
	//
	// // 通过Android 5.0 之后提供的新api来获取最近一段时间内的应用的相关信息
	// String topPackageName = null;
	//
	// if (Build.VERSION.SDK_INT >= 21) {
	//
	// try {
	// // 根据最近time_ms毫秒内的应用统计信息进行排序获取当前顶端的包名
	// long time = System.currentTimeMillis();
	// UsageStatsManager usage = (UsageStatsManager) context
	// .getSystemService("usagestats");
	// List<UsageStats> usageStatsList = usage.queryUsageStats(
	// UsageStatsManager.INTERVAL_BEST, time - time_ms, time);
	// if (usageStatsList != null && usageStatsList.size() > 0) {
	// SortedMap<Long, UsageStats> runningTask = new TreeMap<Long,
	// UsageStats>();
	// for (UsageStats usageStats : usageStatsList) {
	// runningTask.put(usageStats.getLastTimeUsed(),
	// usageStats);
	// }
	// if (runningTask.isEmpty()) {
	// return null;
	// }
	// topPackageName = runningTask.get(runningTask.lastKey())
	// .getPackageName();
	// Log.i("test", "##当前顶端应用包名:" + topPackageName);
	// }
	// }
	//
	// catch (Throwable e) {
	// e.printStackTrace();
	// }
	// }
	//
	// return topPackageName;
	// }

	/**
	 * 5.0以上系统
	 * 
	 * @param context
	 * @return
	 */
	private static String getCurrentPkgName20(Context context) {
		ActivityManager.RunningAppProcessInfo currentInfo = null;
		Field field = null;
		int START_TASK_TO_FRONT = 2;
		String pkgName = "";
		try {
			field = ActivityManager.RunningAppProcessInfo.class
					.getDeclaredField("processState");
			ActivityManager am = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			List appList = am.getRunningAppProcesses();
			// ActivityManager.RunningAppProcessInfo app : appList
			if (appList != null) {
				for (int i = 0; i < appList.size(); i++) {
					ActivityManager.RunningAppProcessInfo app = (RunningAppProcessInfo) appList
							.get(i);
					if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
						Integer state = null;
						try {
							state = field.getInt(app);
							if (state != null && state == START_TASK_TO_FRONT) {
								currentInfo = app;
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			if (currentInfo != null) {
				pkgName = currentInfo.processName;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// pkgName = printForegroundTask(context) ;
		return pkgName;
	}

	/**
	 * 5.0以下系统
	 * 
	 * @param context
	 * @return
	 */
	private static String getCurrentPkgName19(Context context) {
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService("activity");
		ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;
		return topActivity.getPackageName();
	}

	/**
	 * proc另外一种获取包名
	 * 
	 * @return
	 */
	private static String getForegroundApp() {
		final int AID_APP = 10000;
		final int AID_USER = 100000;
		File[] files = new File("/proc").listFiles();
		int lowestOomScore = Integer.MAX_VALUE;
		String foregroundProcess = null;
		for (File file : files) {
			if (!file.isDirectory()) {
				continue;
			}
			int pid;

			try {
				pid = Integer.parseInt(file.getName());
			} catch (NumberFormatException e) {
				continue;
			}

			try {
				String cgroup = read(String.format("/proc/%d/cgroup", pid));
				String[] lines = cgroup.split("\n");
				String cpuSubsystem;
				String cpuaccctSubsystem;

				if (lines.length == 2) {// 有的手机里cgroup包含2行或者3行，我们取cpu和cpuacct两行数据
					cpuSubsystem = lines[0];
					cpuaccctSubsystem = lines[1];
				} else if (lines.length == 3) {
					cpuSubsystem = lines[0];
					cpuaccctSubsystem = lines[2];
				} else {
					continue;
				}

				if (!cpuaccctSubsystem.endsWith(Integer.toString(pid))) {
					// not an application process
					continue;
				}
				if (cpuSubsystem.endsWith("bg_non_interactive")) {
					// background policy
					continue;
				}

				String cmdline = read(String.format("/proc/%d/cmdline", pid));
				if (cmdline.contains("com.android.systemui")) {
					continue;
				}
				int uid = Integer.parseInt(cpuaccctSubsystem.split(":")[2]
						.split("/")[1].replace("uid_", ""));
				if (uid >= 1000 && uid <= 1038) {
					// system process
					continue;
				}
				int appId = uid - AID_APP;
				int userId = 0;
				// loop until we get the correct user id.
				// 100000 is the offset for each user.

				while (appId > AID_USER) {
					appId -= AID_USER;
					userId++;
				}

				if (appId < 0) {
					continue;
				}
				// u{user_id}_a{app_id} is used on API 17+ for multiple user
				// account support.
				// String uidName = String.format("u%d_a%d", userId, appId);
				File oomScoreAdj = new File(String.format(
						"/proc/%d/oom_score_adj", pid));
				if (oomScoreAdj.canRead()) {
					int oomAdj = Integer.parseInt(read(oomScoreAdj
							.getAbsolutePath()));
					if (oomAdj != 0) {
						continue;
					}
				}
				int oomscore = Integer.parseInt(read(String.format(
						"/proc/%d/oom_score", pid)));
				if (oomscore < lowestOomScore) {
					lowestOomScore = oomscore;
					foregroundProcess = cmdline;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return foregroundProcess;
	}

	private static String read(String path) throws IOException {
		StringBuilder output = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		output.append(reader.readLine());

		for (String line = reader.readLine(); line != null; line = reader
				.readLine()) {
			output.append('\n').append(line);
		}
		reader.close();
		return output.toString().trim();// 不调用trim()，包名后会带有乱码
	}

	/**
	 * 是否能取出包名
	 * 
	 * @param mContext
	 * @return
	 */
	public static String Istoppackagenull(Context mContext) {
		String packageName = "";
		if (Build.VERSION.SDK_INT > 19) {
			packageName = Utils.getCurrentPkgName20(mContext);
		} else {
			packageName = Utils.getCurrentPkgName19(mContext);
		}
		if (packageName == null || packageName.trim().equals("")) {
//			packageName = Utils.getForegroundApp();
		}

		return packageName;

	}

	/**
	 * 通过包名打开对应的app
	 * @param context
	 * @param packagename包名
	 * @return
	 */
	public static void openApp(Context context,String packagename) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = packageManager.getPackageInfo(packagename, 0);

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
				ComponentName cn = new ComponentName(packagename, className);
				intent.setComponent(cn);
				context.startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

