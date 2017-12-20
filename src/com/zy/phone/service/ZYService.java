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
		//releaseWakeLock();
		super.onDestroy();
	}
	/**
	 * 计时器计算类
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
			//5.0系统的拿包不一样
			//版本1.0.8加上
			String packageName = "";
			if (Build.VERSION.SDK_INT > 19) {
				packageName = getCurrentPkgName20(mContext);
			} else {
				packageName = getCurrentPkgName19(mContext);
			}
										
			//Android5.1.1的版本，拿不到包名
			if(Build.VERSION.SDK_INT >= Variable.SDK_VERSION  || packageName==null  ||  packageName.trim().equals("")){
				ActivityCacheUtils instance = ActivityCacheUtils.getInstance();
				String latestPackName = instance.getLatestPackName();
				AdInfo adInfo = ActivityCacheUtils.getInstance().getAdInfo(latestPackName);					
				if(adInfo != null){
					if(adInfo.getStartTime() > 0 ){
						//打开提示，若未提示，则提示之
						if(adInfo.isOpenFlag()){
							adInfo.setOpenFlag(false);//提示之后，不再提示
							onHint(adInfo.getTaskInfo()+" 即可获得奖励");
						}
												
						int Time = adInfo.getExeTime();
													
						if(Time > 0 && Time%10 == 0 && adInfo.getTaskTime() > Time){
							onHint(adInfo.getTaskInfo()+" 即可获得奖励");
						}
						//打开时间
						//System.out.println(latestPackName +"体验时间: "+ Time +" : "+adInfo.getTaskTime()+" : "+adInfo.getTryTimes());
						if(Time >= adInfo.getTaskTime()){
							//体验时间已到,得分
							if(true){
								if(adInfo.getTryTimes() < 10){
									//注册任务，并完成所有Activity，则认为注册成功，并返分给用户，完成任务之后，则清空缓存表						
									if(sendDate(String.valueOf(adInfo.getAdId()), adInfo.getPackageName())){
										onHint("恭喜您,试玩《" + adInfo.getAppName() + "》已获得奖励！继续完成下一个任务吧！");
										ActivityCacheUtils.getInstance().remove(adInfo.getPackageName());
										ActivityCacheUtils.getInstance().setLatestPackName("");       //最近打开包名
										ActivityCacheUtils.getInstance().setLatestAdId(Integer.valueOf(0)); //最近打开广告ID
									}else{
										int tryTimes = adInfo.getTryTimes()+1;
										adInfo.setTryTimes(tryTimes);									
									}
								}else{
									ActivityCacheUtils.getInstance().remove(adInfo.getPackageName());
									ActivityCacheUtils.getInstance().setLatestPackName("");       //最近打开包名
									ActivityCacheUtils.getInstance().setLatestAdId(Integer.valueOf(0)); //最近打开广告ID
								}
								return ;
							}
						}else{	
							Time = Time + 1;
							adInfo.setExeTime(Time); //设置任务已体验时间
							//体验时间未到，继续
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
					//若未完成任务，即退出任务，则提示未完成
					if(ad !=null && ad.isAlertFlag()){
						ad.setAlertFlag(false);//提示之后，不再提示
						if(ad.isRegister()){
							onHint("该应用《"+ad.getAppName()+"》需注册，完成后立即获得奖励");
						}else{
							onHint("真可惜，《"+ad.getAppName()+"》的奖励还没得到，请再多用会吧, 剩余 '"+(ad.getTaskTime()-ad.getExeTime())+"'秒！");
						}
					}
				}
				return ;//没有相应监控包信息，则返回，说明不是积分墙上的app
			}else{
				//有监控包信息，收集app的活动Activity轨迹
				if (Build.VERSION.SDK_INT <= 19) {
					String className = getCurrentActivityName19(mContext);				
					ActivityCacheUtils.getInstance().set(packageName,className);					
				}
				ActivityCacheUtils.getInstance().setLatestPackName(adInfo.getPackageName());
				ActivityCacheUtils.getInstance().setLatestAdId(adInfo.getAdId());
			}
			adInfo.setAlertFlag(true);//提示之后，不再提示
			//通过包名拿信息，如果包名不存在就不计算
			//此次任务的体验时长
			//String taskTime = sp_packageName.getString(packageName, "");
			//根据包名和体验时长拿到对应的广告ID
			//String AdsId = sp_packageName.getString(packageName + taskTime, "");
			int AdsId = adInfo.getAdId();
			int taskTime = adInfo.getTaskTime();
			//广告目前已记录的时长
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
								//注册任务，并完成所有Activity，则认为注册成功，并返分给用户，完成任务之后，则清空缓存表						
								if(sendDate(String.valueOf(adInfo.getAdId()), packageName)){
									ActivityCacheUtils.getInstance().remove(packageName);
									finishRegister = true;
									onHint("恭喜您,试玩《" + adInfo.getAppName() + "》已获得奖励！继续完成下一个任务吧！");
								}else{
									int tryTimes = adInfo.getTryTimes()+1;
									adInfo.setTryTimes(tryTimes);									
								}
							}else{
								ActivityCacheUtils.getInstance().remove(adInfo.getPackageName());
								ActivityCacheUtils.getInstance().setLatestPackName("");       //最近打开包名
								ActivityCacheUtils.getInstance().setLatestAdId(Integer.valueOf(0)); //最近打开广告ID
							}
							return ;
						}
					}
				}
			}
			
			//打开提示，若未提示，则提示之
			if(adInfo.isOpenFlag()){
				adInfo.setOpenFlag(false);//提示之后，不再提示
				onHint(adInfo.getTaskInfo()+" 即可获得奖励");
			}
			if (taskTime > 0 && finishRegister) {//任务时间不能为0
				//完成体验时间通知服务器
				if (Time >= Integer.valueOf(taskTime)) {
						if(adInfo.getTryTimes() < 10){
							if(sendDate(String.valueOf(AdsId), packageName)){
								onHint("恭喜您,试玩《" + adInfo.getAppName() + "》已获得奖励！继续完成下一个任务吧！");
								ActivityCacheUtils.getInstance().remove(packageName); //任务完成，清空缓存记录
							}else{
								int tryTimes = adInfo.getTryTimes()+1;
								adInfo.setTryTimes(tryTimes);									
							}
						}else{
							ActivityCacheUtils.getInstance().remove(adInfo.getPackageName());
							ActivityCacheUtils.getInstance().setLatestPackName("");       //最近打开包名
							ActivityCacheUtils.getInstance().setLatestAdId(Integer.valueOf(0)); //最近打开广告ID
						}
				} else if (Time < Integer.valueOf(taskTime)) {//计算时间
					Time = Time + 1;
					adInfo.setExeTime(Time); //设置任务已体验时间
				}
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
					try{
						Toast.makeText(getApplicationContext(), hint, Toast.LENGTH_LONG).show();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
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
		 * 发通知
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
		 * 解析数据
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
			//mNotification.tickerText = "任务已完成";
			//mNotification.flags = Notification.FLAG_AUTO_CANCEL;
			//mNotification.defaults = Notification.DEFAULT_VIBRATE;
			mNotification.tickerText = "任务已完成";
			mNotification.defaults = Notification.DEFAULT_SOUND;
			mNotification.flags = Notification.FLAG_AUTO_CANCEL;
			mNotification.setLatestEventInfo(mContext, "您的" + str_Title
					+ "任务完成", "已获得" + str_Integral + str_Curreny, null);
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
  
    //释放设备电源锁  
    private void releaseWakeLock()  
    {  
        if (null != mWakeLock)  
        {  
            mWakeLock.release();  
            mWakeLock = null;  
        }  
    } 
}