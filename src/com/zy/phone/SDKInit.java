package com.zy.phone;

import java.util.List;

import org.json.JSONObject;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.erm.integralwall.core.Constant;
import com.erm.integralwall.core.NetManager;
import com.erm.integralwall.core.params.FormParams.FormConfig;
import com.zy.phone.net.GetJson;
import com.zy.phone.net.Integral;
import com.zy.phone.sdk.SDKActivity;
import com.zy.phone.sdk.SDKActivityNoDisplay;

/**
 * 初始化接口
 * 
 * @author lws
 * 
 */
public class SDKInit {
	private static SharedPreferences init;
	private static int or;
	private static Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
		}
	};

	/**
	 * 初始化信息
	 * 
	 * @param context
	 *            上下文
	 * @param AppCode
	 *            密钥
	 * @param Other
	 *            回调对密
	 */
	public static void initAd(Context context, String AppCode, String Other) {

		if (AppCode != null && !AppCode.equals("")) {
			init = context
					.getSharedPreferences("zy_init", context.MODE_PRIVATE);
			SharedPreferences.Editor editor = init.edit();
			editor.putString("AppCode", AppCode);
			editor.putString("Other", Other);
			editor.putString("Token", "");
			NetManager.getInstance().inject(context, null,
					new FormConfig().setAppCode(AppCode).setUserId(Other));
			editor.commit();
		} else {
			Toast.makeText(context, Variable.IS_KEY, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 显示列表框框
	 * 
	 * @param context
	 */
	public static void initAdList(Context context) {
		if (MyFile.existSDCard()) {
			// 进行判断如果已经有同样jar打开就不在显示
			ActivityManager mActivityManager = (ActivityManager) context
					.getSystemService(context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager
					.getRunningServices(300);
			final String musicClassName = "com.zy.phone.service.ZYService";
			boolean b = MusicServiceIsStart(mServiceList, musicClassName);
			Intent appcode = new Intent(context, SDKActivity.class);
			context.startActivity(appcode);
		} else {
			Toast.makeText(context, Variable.NO_SPACE, Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * json接口跳入网页详情
	 * @param url 地址
	 */
	public static void showUI(Context context,String url) {
		Intent appcode = new Intent(context, SDKActivity.class);
		appcode.putExtra("url", url);
		context.startActivity(appcode);
	}

	/**
	 * 显示列表框框
	 * 
	 * @param context
	 */
	public static void initAdListNoDisplay(Context context) {
		if (MyFile.existSDCard()) {
			// 进行判断如果已经有同样jar打开就不在显示
			SharedPreferences zy_init = context.getSharedPreferences("zy_init",
					context.MODE_PRIVATE);
			ActivityManager mActivityManager = (ActivityManager) context
					.getSystemService(context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager
					.getRunningServices(300);
			final String musicClassName = "com.zy.phone.service.ZYService";
			boolean b = MusicServiceIsStart(mServiceList, musicClassName);
			Intent appcode = new Intent(context, SDKActivityNoDisplay.class);
			context.startActivity(appcode);
		} else {
			Toast.makeText(context, Variable.NO_SPACE, Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * 查看积分
	 * 
	 * @param integral
	 *            积分接口
	 */
	public static void checkIntegral(Integral integral) {
		try {
			or = 0;
			ThisThead tt = new ThisThead(integral, GetJson.checkIntegral());
			tt.start();
		} catch (Exception e) {
			integral.retCheckIntegral("1", "0");
		}
	}

	/**
	 * 增加积分
	 * 
	 * @param integral
	 *            接口
	 * @param number_integral
	 */
	public static void addIntegral(Integral integral, String number_integral) {
		try {
			if (!number_integral.equals("") && number_integral != null
					&& Integer.valueOf(number_integral) > 0) {
				or = 1;
				ThisThead tt = new ThisThead(integral,
						GetJson.addIntegral(number_integral));
				tt.start();
			} else {
				integral.retAddIntegral("1", "0");
			}
		} catch (Exception e) {
			integral.retAddIntegral("1", "0");
		}

	}

	/**
	 * 扣除积分
	 * 
	 * @param integral
	 * @param number_integral
	 */
	public static void minusIntegral(Integral integral, String number_integral) {
		try {
			if (!number_integral.equals("") && number_integral != null
					&& Integer.valueOf(number_integral) > 0) {
				or = 2;
				ThisThead tt = new ThisThead(integral,
						GetJson.minusIntegral(number_integral));
				tt.start();
			} else {
				integral.retMinusIntegral("1", "0");
			}
		} catch (Exception e) {
			integral.retMinusIntegral("1", "0");
		}

	}

	/**
	 * 子线程
	 * 
	 * @author lws
	 * 
	 */
	static class ThisThead extends Thread {
		Integral integral;
		String str_integral;

		ThisThead(Integral integral, String str_integral) {
			this.integral = integral;
			this.str_integral = str_integral;
		}

		@Override
		public void run() {
			super.run();
			try {
				String retSrc = IntegralQuery.analysisjson(str_integral);
				JSONObject jsonObjs_01 = new JSONObject(retSrc);
				String Str_Code = jsonObjs_01.getString("Code");
				String Str_Param = jsonObjs_01.getString("Param");

				// 成功，操作用户积分
				if (Str_Code.equals("200")) {
					JSONObject jsonObjs_02 = new JSONObject(Str_Param);
					final String Str_Integral = jsonObjs_02
							.getString("Integral");

					handler.post(new Runnable() {
						@Override
						public void run() {
							if (or == 0) {
								integral.retCheckIntegral("0", Str_Integral);
							} else if (or == 2) {
								integral.retMinusIntegral("0", Str_Integral);
							} else if (or == 1) {
								integral.retAddIntegral("0", Str_Integral);
							}
						}
					});
				}
				// 操作积分失败，用户积分不做变更
				if (Str_Code.equals("409")) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							if (or == 0) {
								integral.retCheckIntegral("0", "0");
							} else if (or == 2) {
								integral.retMinusIntegral("0", "0");
							} else if (or == 1) {
								integral.retAddIntegral("0", "0");
							}
						}
					});
				}
			} catch (Exception e) {
				// 失败，不操作用户积分
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (or == 0) {
							integral.retCheckIntegral("0", "0");
						} else if (or == 2) {
							integral.retMinusIntegral("0", "0");
						} else if (or == 1) {
							integral.retAddIntegral("0", "0");
						}
					}
				});
			}
		}

	}

	/**
	 * 判断后台是否有此服务
	 * 
	 * @param mServiceList
	 * @param className
	 * @return
	 */
	private static boolean MusicServiceIsStart(
			List<ActivityManager.RunningServiceInfo> mServiceList,
			String className) {
		for (int i = 0; i < mServiceList.size(); i++) {
			if (className.equals(mServiceList.get(i).service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
