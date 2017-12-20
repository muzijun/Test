package com.zy.phone.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import com.zy.phone.IntegralQuery;
import com.zy.phone.net.GetJson;

public class ZYService_bak extends Service {
	private Timer mTimer;
	public static final int FOREGROUND_ID = 0;
	/**
	 * 计时器
	 */
	private void startTimer() {

		if (mTimer == null) {
			mTimer = new Timer();
			LockTask lockTask = new LockTask(this);
			mTimer.scheduleAtFixedRate(lockTask, 0L, 1000L);
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {

		startTimer();
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		startForeground(FOREGROUND_ID, new Notification());
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	/**
	 * 销毁是停止计算
	 */
	@Override
	public void onDestroy() {

		mTimer.cancel();
		mTimer.purge();
		mTimer = null;
		super.onDestroy();
	}
	/**
	 * 计时器计算类
	 * @author lws
	 *
	 */
	class LockTask extends TimerTask {
		private Context mContext;
		private SharedPreferences sp_packageName;
		private SharedPreferences AdsIdTime;
		private SharedPreferences.Editor editor_packageName;
		private SharedPreferences.Editor editor_AdsIdTime;
		private Notification mNotification;
		private NotificationManager mManager;
		private SharedPreferences init;
		private Handler handler = new Handler();

		public LockTask(Context context) {
			this.mContext = context;

			init = context
					.getSharedPreferences("zy_init", context.MODE_PRIVATE);
		}

		@Override
		public void run() {
			//包名
			sp_packageName = mContext.getSharedPreferences("zy_packageName",
					mContext.MODE_PRIVATE);
			//广告已体验时间
			AdsIdTime = mContext.getSharedPreferences("zy_AdsIdTime",
					mContext.MODE_PRIVATE);

			editor_packageName = sp_packageName.edit();
			editor_AdsIdTime = AdsIdTime.edit();
			//5.0系统的拿包不一样
			//版本1.0.8加上
			String packageName = "";
			if (Build.VERSION.SDK_INT > 19) {
				packageName = getCurrentPkgName20(mContext);
			} else {
				packageName = getCurrentPkgName19(mContext);
			}
			//通过包名拿信息，如果包名不存在就不计算
			//此次任务的体验时长
			String taskTime = sp_packageName.getString(packageName, "");
			//根据包名和体验时长拿到对应的广告ID
			String AdsId = sp_packageName.getString(packageName + taskTime, "");
			//短信验证字符串
			String message = sp_packageName.getString("short_message", "");
			//广告目前已记录的时长
			int Time = AdsIdTime.getInt(AdsId, 0);
			System.out.println(AdsId+" 体验时间:"+Time);
			//广告对应上一秒的时间，上一次的计时时间
			int Above_Time = AdsIdTime.getInt("Above_Time" + AdsId, 0);
			//如果计算的时间不是相差一秒就重新计算，说明用户作弊，用了超频，导致两次记录的时间间隔太长
			if (Time - Above_Time >= 2) {
				editor_AdsIdTime.putInt(AdsId, 0);
				editor_AdsIdTime.putInt("Above_Time" + AdsId, 0);
				editor_AdsIdTime.commit();
			} else if (!taskTime.equals("0")) {//任务时间不能为0
				//完成体验时间通知服务器
				if (!taskTime.equals("") && Time >= Integer.valueOf(taskTime)) {
					SharedPreferences.Editor init_editor = init.edit();
					init_editor.putBoolean("tasktime", true);
					init_editor.putString("appName", "");
					init_editor.commit();
					sendDate(AdsId, packageName);
				} else if (!taskTime.equals("")
						&& Time < Integer.valueOf(taskTime)
						&& message.equals("0")) {//计算时间
					SharedPreferences.Editor init_editor = init.edit();
					init_editor.putBoolean("tasktime", false);
					init_editor.putString("appName", "zy."+packageName);
					init_editor.commit();
					editor_AdsIdTime.putInt("Above_Time" + AdsId, Time);
					Time = Time + 1;
					editor_AdsIdTime.putInt(AdsId, Time);
					editor_AdsIdTime.commit();

				}
			}
		}
		/**
		 * 5.0以下系统
		 * @param context
		 * @return
		 */
		public String getCurrentPkgName19(Context context) {
			ActivityManager mActivityManager = (ActivityManager) context
					.getSystemService("activity");
			ComponentName topActivity = mActivityManager.getRunningTasks(1)
					.get(0).topActivity;
			return topActivity.getPackageName();
		}
		
		/**
		 * 5.0以下系统
		 * @param context
		 * @return
		 */
		public String getCurrentActivityName19(Context context) {
			ActivityManager mActivityManager = (ActivityManager) context
					.getSystemService("activity");
			ComponentName topActivity = mActivityManager.getRunningTasks(1)
					.get(0).topActivity;
			return topActivity.getClassName();
		}
		/**
		 * 5.0以上系统
		 * @param context
		 * @return
		 */
		public String getCurrentPkgName20(Context context) {
			ActivityManager.RunningAppProcessInfo currentInfo = null;
			Field field = null;
			int START_TASK_TO_FRONT = 2;
			String pkgName = null;
			try {
				field = ActivityManager.RunningAppProcessInfo.class
						.getDeclaredField("processState");
			} catch (Exception e) {
				e.printStackTrace();
			}
			ActivityManager am = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			List appList = am.getRunningAppProcesses();
			// ActivityManager.RunningAppProcessInfo app : appList
			for (int i = 0; i < appList.size(); i++) {
				ActivityManager.RunningAppProcessInfo app = (RunningAppProcessInfo) appList
						.get(i);
				if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					Integer state = null;
					try {
						state = field.getInt(app);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (state != null && state == START_TASK_TO_FRONT) {
						currentInfo = app;
						break;
					}
				}
			}
			if (currentInfo != null) {
				pkgName = currentInfo.processName;
			}
			return pkgName;
		}
		/**
		 * 发通知
		 * @param AdId
		 * @param packageName
		 */
		private void sendDate(String AdId, String packageName) {
			try {
				String retSrc = IntegralQuery
						.analysisjson(new GetJson(mContext).sendTask(AdId));
				getjson(retSrc, AdId, packageName);
				handler.sendEmptyMessage(0);
			} catch (Exception e) {

			}
		}
		/**
		 * 解析数据
		 * @param retSrc
		 * @param AdsId
		 * @param packageName
		 */
		private void getjson(String retSrc, String AdsId, String packageName) {
			try {
				JSONObject jsonObjs_01 = new JSONObject(retSrc);
				String str_Code = jsonObjs_01.getString("Code");
				String str_Param = jsonObjs_01.getString("Param");
				String str_Title;
				String str_Curreny;
				String str_Integral;
				String str_ShowTip;
				if (str_Code.equals("200")) {
					JSONObject jsonObjs_02 = new JSONObject(str_Param);
					str_Title = jsonObjs_02.getString("Title");
					str_Curreny = jsonObjs_02.getString("Currency");
					str_Integral = jsonObjs_02.getString("Integral");
					str_ShowTip = jsonObjs_02.getString("ShowTip");
					editor_AdsIdTime.putInt(AdsId, 0);
					editor_AdsIdTime.putInt("Above_Time" + AdsId, 0);
					editor_packageName.putString(packageName, "0");
					editor_packageName.putString("LastName", "");
					editor_AdsIdTime.commit();
					editor_packageName.commit();
					if (str_ShowTip.equals("1")) {
						initNotifiManager(str_Title, str_Integral, str_Curreny);
					}
				} else if (jsonObjs_01.getString("Code").equals("400")) {
					editor_AdsIdTime.putInt(AdsId, 0);
					editor_AdsIdTime.commit();
				}
			} catch (Exception e) {
				e.printStackTrace();
				editor_AdsIdTime.putInt(AdsId, 0);
				editor_AdsIdTime.commit();
			}
		}
		/**
		 * 状态栏通知
		 * @param str_Title 标题
		 * @param str_Integral 积分
		 * @param str_Curreny 单位
		 */
		private void initNotifiManager(String str_Title, String str_Integral,
				String str_Curreny) {
			mManager = (NotificationManager) mContext
					.getSystemService(mContext.NOTIFICATION_SERVICE);
			mNotification = new Notification();
			mNotification.icon = android.R.drawable.ic_lock_silent_mode_off;
			mNotification.tickerText = "任务已完成";
			mNotification.flags = Notification.FLAG_AUTO_CANCEL;
			mNotification.defaults=Notification.DEFAULT_VIBRATE;
			mNotification.setLatestEventInfo(mContext, "您的" + str_Title
					+ "任务完成", "已获得" + str_Integral + str_Curreny, null);
			mManager.notify(0, mNotification);
		}
	}
}