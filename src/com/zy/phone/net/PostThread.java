package com.zy.phone.net;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.zy.phone.IntegralQuery;
import com.zy.phone.Variable;

/**
 * 安装和初始化线程
 * 
 * @author Administrator
 * 
 */
public class PostThread extends Thread {
	private Context context;
	private int num;
	private String AdId;
	private GetJson getjson;
	private SharedPreferences init;
	private Handler handler;
	private boolean flag = true;

	public PostThread(Context context, int num, Handler handler) {
		this.context = context;
		this.num = num;
		this.handler = handler;
		getjson = new GetJson(context);
	}

	public PostThread(Context context, int num, String AdId, Handler handler) {
		this.context = context;
		this.num = num;
		this.AdId = AdId;
		this.handler = handler;
		getjson = new GetJson(context);

	}

	@Override
	public void run() {
		super.run();
		try {
			String retSrc = null;
			//10次机会反复调用服务器接口
			for(int index=0;index<10;index++){
				retSrc = IntegralQuery.analysisjson(strjson());
				if (num != Variable.OVERDUE) {
					if(analysisjson(retSrc)){
						//System.out.println("第"+(index+1)+"次调用服务器接口成功");
						break;
					}else{
						try{
							//System.out.println("即将开始第"+(index+1)+"次调用服务器接口...");
							Thread.sleep(1000);
						}catch(Exception e){
							
						}						
					}
				}else{
					break;
				}
			}
			Message message = new Message();
			message.obj = retSrc;
			handler.sendMessage(message);
		} catch (Exception e) {
			handler.sendEmptyMessage(1);
		}
	}

	/**
	 * 判断是哪一种接口
	 * 
	 * @return
	 */
	private String strjson() {
		try {
			if (num == Variable.INIT) {
				return getjson.setinitjson();
			} else if (num == Variable.BOOTPACKAGENAME) {
				return getjson.getBootPackageName(AdId);
			} else if (num == Variable.OVERDUE) {
				//return getjson.getPackageNameOverdue(AdId);
				return null;
			}
		} catch (Exception e) {
			return "";
		}
		return null;
	}

	/**
	 * 解析数据
	 * 
	 * @param retSrc
	 */
	private boolean analysisjson(String retSrc) {
		if(null == retSrc) return false;
		try {
			JSONObject jsonObjs_01 = new JSONObject(retSrc);
			String Str_Code = jsonObjs_01.getString("Code");
			if (Str_Code.equals("200")) {
				if (num == Variable.INIT) {
					String Str_Param = jsonObjs_01.getString("Param");
					JSONObject jsonObjs_02 = new JSONObject(Str_Param);
					String Token = jsonObjs_02.getString("Token");
					String applist_url = jsonObjs_02.getString("AdsList");
					init = context.getSharedPreferences("zy_init",
							context.MODE_PRIVATE);
					SharedPreferences.Editor editor = init.edit();
					editor.putString("Token", Token);
					editor.putString("applist_url", applist_url);
					editor.commit();
				} else if (num == Variable.BOOTPACKAGENAME) {
					String Str_Param = jsonObjs_01.getString("Param");
					JSONObject jsonObjs_02 = new JSONObject(Str_Param);
					String AdsId = jsonObjs_02.getString("AdsId");
					SharedPreferences sp_packageName = context
							.getSharedPreferences("zy_packageName",
									context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sp_packageName.edit();
					editor.putBoolean("Installation" + AdsId, true);
				}
				return true;
			} else {
				if (num == Variable.INIT) {
					String Str_Param = jsonObjs_01.getString("Param");
					JSONObject jsonObjs_02 = new JSONObject(Str_Param);
					String applist_url = jsonObjs_02.getString("AdsList");
					init = context.getSharedPreferences("zy_init",
							context.MODE_PRIVATE);
					SharedPreferences.Editor editor = init.edit();
					editor.putString("applist_url", applist_url);
					editor.commit();
				}
				return true;
				
			}
			
		} catch (Exception e) {

		}
		
		return false;
	}
}
