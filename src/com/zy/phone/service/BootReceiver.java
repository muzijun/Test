package com.zy.phone.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.zy.phone.JsonMerge;
import com.zy.phone.MyFile;
import com.zy.phone.PackageName;
import com.zy.phone.Variable;
import com.zy.phone.net.PostThread;

/**
 * ������װ��
 * 
 * @author Administrator
 * 
 */
public class BootReceiver extends BroadcastReceiver {
	// �ӿ�
	private PostThread pt;
	// �������
	private List<String> FileNameList = new ArrayList<String>();
	// �������
	private List<String> FileTimeList = new ArrayList<String>();
	// ���ذ�·��
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
			
			//���������ؿ��õİ����б��Ѿ�����SDK��У��
			List<String> name = JsonMerge.analysisJsonArrString((String) msg.obj);
			//ϵͳ�Ѿ���װ�İ���
			List<String> sysname = PackageName.getStrAllApps(context);
			/**
			 * ��װ��ʾ�����������û�а�װ��������ʾ�����û���װ
			 */
			for (int i = 0; i < name.size(); i++) {
				//���ص�ʱ���ʼ������ʱ�䣬�Ѿ���װ�˾Ͳ�����һ�����һ�쵯һ�Σ�һ�ε�һ����
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
					//ɾ���ѹ��ڵİ�
					MyFile.deleteFile(sdcard + name.get(i) + ".apk");
				}
			}
		}
	};
	/**
	 * ��ȡ����
	 * 
	 * @return
	 */
	public static String getDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		String mYear = String.valueOf(calendar.get(Calendar.YEAR)); // ��ȡ��ǰ���
		String mMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);// ��ȡ��ǰ�·�
		String mDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));// ��ȡ��ǰ�·ݵ����ں���
		return mYear + "��" + mMonth + "��" + mDay + "��";
	}
	
	/**
	 *����app��װ�����û���װappʱ�����sd����û�����ǵİ���������򵯳����ǵİ�װ�����û���װ
	 *�����ϵ�����һ����װ��һ�쵯һ�Σ�һ�ε�һ�� 
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		// ����ǹ���֪ͨ��
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
		
			String packageName = intent.getDataString().substring(
					intent.getDataString().lastIndexOf(":") + 1,
					intent.getDataString().length());
			AdInfo adInfo = ActivityCacheUtils.getInstance().getAdInfo(packageName);
			if(null != adInfo){
				PostThread pt = new PostThread(context, Variable.BOOTPACKAGENAME, String.valueOf(adInfo.getAdId()),
						handler);
				pt.start();
				try{
					//��װ�������app
					Intent LaunchIntent = context.getPackageManager()
							.getLaunchIntentForPackage(packageName);
					context.startActivity(LaunchIntent);
					if(adInfo.getStartTime() == 0 ){
						adInfo.setStartTime(System.currentTimeMillis());
					}	
					adInfo.setAlertFlag(true);
				}catch(Exception e){}

			}
		}
	}
}
