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
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.zy.phone.IntegralQuery;
import com.zy.phone.Variable;
import com.zy.phone.net.GetJson;

public class ZYService extends Service {
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
		//releaseWakeLock();
		super.onDestroy();
	}
	/**
	 * ��ʱ��������
	 * @author lws
	 *
	 */
	class LockTask extends TimerTask {
		private Context mContext;
		private Notification mNotification;
		private NotificationManager mManager;
		private Handler handler = new Handler();
	
		public LockTask(Context context) {
			this.mContext = context;
		}

		@Override
		public void run() {
			//5.0ϵͳ���ð���һ��
			//�汾1.0.8����
			String packageName = "";
			if (Build.VERSION.SDK_INT > 19) {
				packageName = getCurrentPkgName20(mContext);
			} else {
				packageName = getCurrentPkgName19(mContext);
			}
										
			//Android5.1.1�İ汾���ò�������
			if(Build.VERSION.SDK_INT >= Variable.SDK_VERSION  || packageName==null  ||  packageName.trim().equals("")){
				ActivityCacheUtils instance = ActivityCacheUtils.getInstance();
				String latestPackName = instance.getLatestPackName();
				AdInfo adInfo = ActivityCacheUtils.getInstance().getAdInfo(latestPackName);					
				if(adInfo != null){
					if(adInfo.getStartTime() > 0 ){
						//����ʾ����δ��ʾ������ʾ֮
						if(adInfo.isOpenFlag()){
							adInfo.setOpenFlag(false);//��ʾ֮�󣬲�����ʾ
							onHint(adInfo.getTaskInfo()+" ���ɻ�ý���");
						}
												
						int Time = adInfo.getExeTime();
													
						if(Time > 0 && Time%10 == 0 && adInfo.getTaskTime() > Time){
							onHint(adInfo.getTaskInfo()+" ���ɻ�ý���");
						}
						//��ʱ��
						//System.out.println(latestPackName +"����ʱ��: "+ Time +" : "+adInfo.getTaskTime()+" : "+adInfo.getTryTimes());
						if(Time >= adInfo.getTaskTime()){
							//����ʱ���ѵ�,�÷�
							if(true){
								if(adInfo.getTryTimes() < 10){
									//ע�����񣬲��������Activity������Ϊע��ɹ��������ָ��û����������֮������ջ����						
									if(sendDate(String.valueOf(adInfo.getAdId()), adInfo.getPackageName())){
										onHint("��ϲ��,���桶" + adInfo.getAppName() + "���ѻ�ý��������������һ������ɣ�");
										ActivityCacheUtils.getInstance().remove(adInfo.getPackageName());
										ActivityCacheUtils.getInstance().setLatestPackName("");       //����򿪰���
										ActivityCacheUtils.getInstance().setLatestAdId(Integer.valueOf(0)); //����򿪹��ID
									}else{
										int tryTimes = adInfo.getTryTimes()+1;
										adInfo.setTryTimes(tryTimes);									
									}
								}else{
									ActivityCacheUtils.getInstance().remove(adInfo.getPackageName());
									ActivityCacheUtils.getInstance().setLatestPackName("");       //����򿪰���
									ActivityCacheUtils.getInstance().setLatestAdId(Integer.valueOf(0)); //����򿪹��ID
								}
								return ;
							}
						}else{	
							Time = Time + 1;
							adInfo.setExeTime(Time); //��������������ʱ��
							//����ʱ��δ��������
							return;
						}
					}
					return;
				}
				return;
			}
												
			AdInfo adInfo = ActivityCacheUtils.getInstance().getAdInfo(packageName);
			if(null == adInfo){
				String latestPackName = ActivityCacheUtils.getInstance().getLatestPackName();
				if(packageName!=null && !packageName.equals(latestPackName)){
					AdInfo ad = ActivityCacheUtils.getInstance().getAdInfo(latestPackName);
					//��δ������񣬼��˳���������ʾδ���
					if(ad !=null && ad.isAlertFlag()){
						ad.setAlertFlag(false);//��ʾ֮�󣬲�����ʾ
						if(ad.isRegister()){
							onHint("��Ӧ�á�"+ad.getAppName()+"����ע�ᣬ��ɺ�������ý���");
						}else{
							onHint("���ϧ����"+ad.getAppName()+"���Ľ�����û�õ������ٶ��û��, ʣ�� '"+(ad.getTaskTime()-ad.getExeTime())+"'�룡");
						}
					}
				}
				return ;//û����Ӧ��ذ���Ϣ���򷵻أ�˵�����ǻ���ǽ�ϵ�app
			}else{
				//�м�ذ���Ϣ���ռ�app�ĻActivity�켣
				if (Build.VERSION.SDK_INT <= 19) {
					String className = getCurrentActivityName19(mContext);				
					ActivityCacheUtils.getInstance().set(packageName,className);					
				}
				ActivityCacheUtils.getInstance().setLatestPackName(adInfo.getPackageName());
				ActivityCacheUtils.getInstance().setLatestAdId(adInfo.getAdId());
			}
			adInfo.setAlertFlag(true);//��ʾ֮�󣬲�����ʾ
			//ͨ����������Ϣ��������������ھͲ�����
			//�˴����������ʱ��
			//String taskTime = sp_packageName.getString(packageName, "");
			//���ݰ���������ʱ���õ���Ӧ�Ĺ��ID
			//String AdsId = sp_packageName.getString(packageName + taskTime, "");
			int AdsId = adInfo.getAdId();
			int taskTime = adInfo.getTaskTime();
			//���Ŀǰ�Ѽ�¼��ʱ��
			int Time = adInfo.getExeTime();
			
			boolean isfinish = false;
			boolean finishRegister = true;
			if (Build.VERSION.SDK_INT <= 19) {
				if(packageName!=null && packageName.equals(adInfo.getPackageName())){				
					if(adInfo.isRegister()){
						finishRegister = false;
						isfinish = ActivityCacheUtils.getInstance().checkFinish(packageName);
						if(isfinish){
							if(adInfo.getTryTimes() < 10){
								//ע�����񣬲��������Activity������Ϊע��ɹ��������ָ��û����������֮������ջ����						
								if(sendDate(String.valueOf(adInfo.getAdId()), packageName)){
									ActivityCacheUtils.getInstance().remove(packageName);
									finishRegister = true;
									onHint("��ϲ��,���桶" + adInfo.getAppName() + "���ѻ�ý��������������һ������ɣ�");
								}else{
									int tryTimes = adInfo.getTryTimes()+1;
									adInfo.setTryTimes(tryTimes);									
								}
							}else{
								ActivityCacheUtils.getInstance().remove(adInfo.getPackageName());
								ActivityCacheUtils.getInstance().setLatestPackName("");       //����򿪰���
								ActivityCacheUtils.getInstance().setLatestAdId(Integer.valueOf(0)); //����򿪹��ID
							}
							return ;
						}
					}
				}
			}
			
			//����ʾ����δ��ʾ������ʾ֮
			if(adInfo.isOpenFlag()){
				adInfo.setOpenFlag(false);//��ʾ֮�󣬲�����ʾ
				onHint(adInfo.getTaskInfo()+" ���ɻ�ý���");
			}
			if (taskTime > 0 && finishRegister) {//����ʱ�䲻��Ϊ0
				//�������ʱ��֪ͨ������
				if (Time >= Integer.valueOf(taskTime)) {
						if(adInfo.getTryTimes() < 10){
							if(sendDate(String.valueOf(AdsId), packageName)){
								onHint("��ϲ��,���桶" + adInfo.getAppName() + "���ѻ�ý��������������һ������ɣ�");
								ActivityCacheUtils.getInstance().remove(packageName); //������ɣ���ջ����¼
							}else{
								int tryTimes = adInfo.getTryTimes()+1;
								adInfo.setTryTimes(tryTimes);									
							}
						}else{
							ActivityCacheUtils.getInstance().remove(adInfo.getPackageName());
							ActivityCacheUtils.getInstance().setLatestPackName("");       //����򿪰���
							ActivityCacheUtils.getInstance().setLatestAdId(Integer.valueOf(0)); //����򿪹��ID
						}
				} else if (Time < Integer.valueOf(taskTime)) {//����ʱ��
					Time = Time + 1;
					adInfo.setExeTime(Time); //��������������ʱ��
				}
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
					try{
						Toast.makeText(getApplicationContext(), hint, Toast.LENGTH_LONG).show();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
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
			String pkgName = "";
			try {
				field = ActivityManager.RunningAppProcessInfo.class
						.getDeclaredField("processState");			
				ActivityManager am = (ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE);
				List appList = am.getRunningAppProcesses();
				// ActivityManager.RunningAppProcessInfo app : appList
				if(appList != null){
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
			//pkgName =  printForegroundTask(context) ;
			return pkgName;
		}

		/**
		 * ��֪ͨ
		 * @param AdId
		 * @param packageName
		 */
		private boolean sendDate(String AdId, String packageName) {
			boolean flag = false;
			try {
				String retSrc = IntegralQuery
						.analysisjson(new GetJson(mContext).sendTask(AdId));
				//System.out.println(retSrc+"  "+AdId+"   " + packageName);
				flag = getjson(retSrc, AdId, packageName);
				handler.sendEmptyMessage(0);
			} catch (Exception e) {

			}
			return flag;
		}
		/**
		 * ��������
		 * @param retSrc
		 * @param AdsId
		 * @param packageName
		 */
		private boolean getjson(String retSrc, String AdsId, String packageName) {
			
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
					if (str_ShowTip.equals("1")) {
						initNotifiManager(str_Title, str_Integral, str_Curreny);
					}
					return true;
				} else if (jsonObjs_01.getString("Code").equals("400")) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
			return false;
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
			//mNotification.tickerText = "���������";
			//mNotification.flags = Notification.FLAG_AUTO_CANCEL;
			//mNotification.defaults = Notification.DEFAULT_VIBRATE;
			mNotification.tickerText = "���������";
			mNotification.defaults = Notification.DEFAULT_SOUND;
			mNotification.flags = Notification.FLAG_AUTO_CANCEL;
			mNotification.setLatestEventInfo(mContext, "����" + str_Title
					+ "�������", "�ѻ��" + str_Integral + str_Curreny, null);
			mManager.notify(0, mNotification);
		}
	}
	
	WakeLock mWakeLock;
    private void acquireWakeLock()  
    {  
        if (null == mWakeLock)  
        {  
            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);  
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE,"");  
            if (null != mWakeLock)  
            {  
                try{
                	mWakeLock.acquire();  
                }catch(Exception e){
                }
            }  
        }  
    }  
  
    //�ͷ��豸��Դ��  
    private void releaseWakeLock()  
    {  
        if (null != mWakeLock)  
        {  
            mWakeLock.release();  
            mWakeLock = null;  
        }  
    } 
}