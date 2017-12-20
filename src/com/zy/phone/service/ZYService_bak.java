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
	 * ��ʱ��
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
	 * ������ֹͣ����
	 */
	@Override
	public void onDestroy() {

		mTimer.cancel();
		mTimer.purge();
		mTimer = null;
		super.onDestroy();
	}
	/**
	 * ��ʱ��������
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
			//����
			sp_packageName = mContext.getSharedPreferences("zy_packageName",
					mContext.MODE_PRIVATE);
			//���������ʱ��
			AdsIdTime = mContext.getSharedPreferences("zy_AdsIdTime",
					mContext.MODE_PRIVATE);

			editor_packageName = sp_packageName.edit();
			editor_AdsIdTime = AdsIdTime.edit();
			//5.0ϵͳ���ð���һ��
			//�汾1.0.8����
			String packageName = "";
			if (Build.VERSION.SDK_INT > 19) {
				packageName = getCurrentPkgName20(mContext);
			} else {
				packageName = getCurrentPkgName19(mContext);
			}
			//ͨ����������Ϣ��������������ھͲ�����
			//�˴����������ʱ��
			String taskTime = sp_packageName.getString(packageName, "");
			//���ݰ���������ʱ���õ���Ӧ�Ĺ��ID
			String AdsId = sp_packageName.getString(packageName + taskTime, "");
			//������֤�ַ���
			String message = sp_packageName.getString("short_message", "");
			//���Ŀǰ�Ѽ�¼��ʱ��
			int Time = AdsIdTime.getInt(AdsId, 0);
			System.out.println(AdsId+" ����ʱ��:"+Time);
			//����Ӧ��һ���ʱ�䣬��һ�εļ�ʱʱ��
			int Above_Time = AdsIdTime.getInt("Above_Time" + AdsId, 0);
			//��������ʱ�䲻�����һ������¼��㣬˵���û����ף����˳�Ƶ���������μ�¼��ʱ����̫��
			if (Time - Above_Time >= 2) {
				editor_AdsIdTime.putInt(AdsId, 0);
				editor_AdsIdTime.putInt("Above_Time" + AdsId, 0);
				editor_AdsIdTime.commit();
			} else if (!taskTime.equals("0")) {//����ʱ�䲻��Ϊ0
				//�������ʱ��֪ͨ������
				if (!taskTime.equals("") && Time >= Integer.valueOf(taskTime)) {
					SharedPreferences.Editor init_editor = init.edit();
					init_editor.putBoolean("tasktime", true);
					init_editor.putString("appName", "");
					init_editor.commit();
					sendDate(AdsId, packageName);
				} else if (!taskTime.equals("")
						&& Time < Integer.valueOf(taskTime)
						&& message.equals("0")) {//����ʱ��
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
		 * 5.0����ϵͳ
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
		 * 5.0����ϵͳ
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
		 * 5.0����ϵͳ
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
		 * ��֪ͨ
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
		 * ��������
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
		 * ״̬��֪ͨ
		 * @param str_Title ����
		 * @param str_Integral ����
		 * @param str_Curreny ��λ
		 */
		private void initNotifiManager(String str_Title, String str_Integral,
				String str_Curreny) {
			mManager = (NotificationManager) mContext
					.getSystemService(mContext.NOTIFICATION_SERVICE);
			mNotification = new Notification();
			mNotification.icon = android.R.drawable.ic_lock_silent_mode_off;
			mNotification.tickerText = "���������";
			mNotification.flags = Notification.FLAG_AUTO_CANCEL;
			mNotification.defaults=Notification.DEFAULT_VIBRATE;
			mNotification.setLatestEventInfo(mContext, "����" + str_Title
					+ "�������", "�ѻ��" + str_Integral + str_Curreny, null);
			mManager.notify(0, mNotification);
		}
	}
}