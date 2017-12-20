package com.zy.phone.sdk;

import java.io.File;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.SmsMessage;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zy.phone.AdInterface;
import com.zy.phone.MyFile;
import com.zy.phone.MyProgressBar;
import com.zy.phone.Variable;
import com.zy.phone.net.PostThread;
import com.zy.phone.service.ActivityCacheUtils;
import com.zy.phone.service.AdInfo;
import com.zy.phone.service.ZYService;

public class SDKActivity extends Activity {
	// 显示内容
	private WebView adlist;
	// 加载提示
	private MyProgressBar upload_prompt;
	// 显示url
	private String adUrL;
	// 初始化数据
	private static SharedPreferences zy_init;
	// 包名
	private SharedPreferences sp_packageName;

	private String token = "?Token=";

	private String AppCode = "&AppCode=";

	private String Version = "&Version=";
	// sdk位置
	private String Sdpath = Environment.getExternalStorageDirectory() + "/zy/";

	private bineConnection bine;
	// 是否返回
	private boolean back = true;
	// 是否退出
	private boolean isBind = false;

	private String infourl;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 显示
			adUrL = zy_init.getString("applist_url", "") + token
					+ zy_init.getString("Token", "") + AppCode
					+ zy_init.getString("AppCode", "") + Version
					+ Variable.VERSION;
			adlist.loadUrl(adUrL);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(getView());
		init();
		// 初始化数据
		PostThread pThead = new PostThread(this, Variable.INIT, handler);
		pThead.start();

	}

	/**
	 * 初始化布局
	 */
	private void init() {

		zy_init = getSharedPreferences("zy_init", MODE_PRIVATE);
		sp_packageName = getSharedPreferences("zy_packageName", MODE_PRIVATE);

		adlist.getSettings().setDefaultTextEncodingName("UTF-8");
		adlist.getSettings().setJavaScriptEnabled(true);		
		adlist.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		
         // 开启 DOM storage API 功能  
		adlist.getSettings().setDomStorageEnabled(true);  
						
		adlist.addJavascriptInterface(new AdInterface(this, adlist, handler),
				"android");
		adlist.setWebChromeClient(new UpdataWebViewClient());
		adlist.setWebViewClient(new WebViewClientDemo());
		adlist.setDownloadListener(new MyWebViewDownLoadListener());
		
		MyFile.foundFilePath(Sdpath);
		Intent startservice = new Intent(SDKActivity.this, ZYService.class);
		isBind = bindService(startservice, bine = new bineConnection(),
				BIND_AUTO_CREATE);
		// 为了保障提示正确，初始化
		SharedPreferences.Editor editor_init = zy_init.edit();
		editor_init.putBoolean("tasktime", true).commit();
		SharedPreferences.Editor editor_name = sp_packageName.edit();
		editor_name.putString("LastName", "").commit();
		// 启动监听
		registerScreenActionReceiver();
	}

	/**
	 * 初始化控件，列表页面框框
	 * 
	 * @return
	 */
	private View getView() {
		LinearLayout adlist_con = new LinearLayout(this);
		LinearLayout.LayoutParams params_adlist_con = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		adlist_con.setOrientation(LinearLayout.VERTICAL);
		params_adlist_con.gravity = Gravity.CENTER;
		adlist_con.setGravity(Gravity.CENTER);
		adlist_con.setLayoutParams(params_adlist_con);
		adlist = new WebView(this);
		LinearLayout.LayoutParams params_adlist_web = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		adlist.setLayoutParams(params_adlist_web);
		adlist.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		adlist.setVisibility(View.GONE);
		adlist_con.addView(adlist);
		upload_prompt = new MyProgressBar(this);
		LinearLayout.LayoutParams params_upload_prompt = new LinearLayout.LayoutParams(
				getScreenWidth() / 7, getScreenWidth() / 7);
		upload_prompt.setLayoutParams(params_upload_prompt);
		params_upload_prompt.gravity = Gravity.CENTER;
		return adlist_con;
	}

	 public final static int FILECHOOSER_RESULTCODE = 1;
	 public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;

	 private ValueCallback<Uri> mFilePathCallback4;
	 private ValueCallback<Uri[]> mFilePathCallback5;
	
	 private String sdcard = Environment.getExternalStorageDirectory() + "";
	 private void startDownload(String url) {
		 	String apkName= getApkFileName(url);
			if (!MyFile.existFile(sdcard + "/zy/" + apkName)) {
		 	//if(!ActivityCacheUtils.getInstance().hasApkNames(apkName)){
		 		ActivityCacheUtils.getInstance().addApkNames(apkName);
				DownloadManager dm = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
		        DownloadManager.Request request = new DownloadManager.Request(
		                Uri.parse(url));
		        request.setMimeType("application/vnd.android.package-archive");
		        request.setVisibleInDownloadsUi(true);		       
		       //request.setDestinationInExternalFilesDir(this,Environment.DIRECTORY_DOWNLOADS,apkName);
		    	request.setDestinationInExternalPublicDir("zy",apkName);
		        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		        dm.enqueue(request); 		        
	 		}
	        Toast.makeText(SDKActivity.this, apkName+" 已加入下载队列，请耐心等待下载完成",Toast.LENGTH_LONG).show();
	 }
	 
	    public static String getApkFileName(String url){
	    	 if(url==null){
	    		 return null;
	    	 }
	    	 int start = url.indexOf(".apk");
	         String u = url.substring(0,start);
	         if(u == null ) 
	        	 return null;
	         int end = u.lastIndexOf("/");
	         String apk = url.substring(end+1,start)+".apk";
	         return apk;
	    }
	    
	 private class MyWebViewDownLoadListener implements DownloadListener{
		//添加监听事件即可
		public void onDownloadStart(String url, String userAgent, String contentDisposition,
		    String mimetype,long contentLength)          {
		             //Uri uri = Uri.parse(url);
		             // Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		             //startActivity(intent);
		             startDownload(url);
		         }
		     }
	 
	/**
	 * 拦截返回做对应的提示
	 * 点击“返回”按钮的拦截
	 * 如果是广告详情页面返回，则拦截提示，如果是列表页返回，则退出。
	 * @author Administrator
	 */
	private class WebViewClientDemo extends WebViewClient{	
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (!url.startsWith("http:") && !url.startsWith("https:")) {  
				 return true;
            }              
			if (url.lastIndexOf("AdsIntro") >= 0) {
				infourl = url;
				back = false;
			} else {
				back = true;
				if(Build.VERSION.SDK_INT >= Variable.SDK_VERSION){
					ActivityCacheUtils instance = ActivityCacheUtils.getInstance();
					String latestPackName = instance.getLatestPackName();
					AdInfo adInfo = ActivityCacheUtils.getInstance().getAdInfo(latestPackName);					
					if(adInfo != null){
						if(adInfo.getStartTime() > 0 ){
							//若未完成任务，即退出任务，则提示未完成
							if(adInfo.isAlertFlag()){
								adInfo.setAlertFlag(false);//提示之后，不再提示
								if(adInfo.isRegister()){
									Toast.makeText(SDKActivity.this, "该应用《"+adInfo.getAppName()+"》需先注册，注册后试用2分钟  即可获得奖励",
											Toast.LENGTH_LONG).show();
								}else{
									Toast.makeText(SDKActivity.this, "真可惜,《" + adInfo.getAppName() + "》的奖励还未得到，请再多用会吧",
											Toast.LENGTH_LONG).show();
								}
							}
						}
					}
				}
			}
			view.loadUrl(url);
			return true;
		}	
		
		 	@Override  
	        public void onPageStarted(WebView view, String url, Bitmap favicon) {  
				 if (url.lastIndexOf("AdsIntro") >= 0) {
						back = false;
					} else {
						back = true;
					}
		            super.onPageStarted(view, url, favicon);  
		         }				
    		}
	
		@Override
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
			        if (resultCode != Activity.RESULT_OK) {
			        	//back=true;
			            //return;
			        }
	                try {
	                    if (mFilePathCallback4 == null && mFilePathCallback5 == null) {
	                    	back=true;
	                    	return;
	                    }
	                    if (mFilePathCallback5 != null) {
	              	      	onActivityResultAboveL(requestCode, resultCode, data);
	              	    }else{
		                    String sourcePath = ImageUtil.retrievePath(this, mSourceIntent, data);
		                    Uri uri = Uri.fromFile(new File(sourcePath));
		                    mFilePathCallback4.onReceiveValue(uri);
	              	    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	    }

	private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
	  if (mFilePathCallback5 == null) {
		  return;
	  }
	  Uri[] results = null;
	  if (resultCode == Activity.RESULT_OK) {
	    if (data == null) {
	    } else {
	      String dataString = data.getDataString();
	      ClipData clipData = data.getClipData();
	      if (clipData != null) {
	        results = new Uri[clipData.getItemCount()];
	        for (int i = 0; i < clipData.getItemCount(); i++) {
	          ClipData.Item item = clipData.getItemAt(i);
	          results[i] = item.getUri();
	        }
	      }
	      if (dataString != null)
	        results = new Uri[]{Uri.parse(dataString)};
	    }
	  }
	  mFilePathCallback5.onReceiveValue(results);
	  mFilePathCallback5 = null;
	  return;
	}
   
	private static final int REQUEST_CODE_PICK_IMAGE = 0;
	private Intent mSourceIntent;
	
	public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {
    	 mFilePathCallback4 = uploadMsg;
    	 mSourceIntent = ImageUtil.choosePicture();
    	 startActivityForResult(mSourceIntent, REQUEST_CODE_PICK_IMAGE);
    	 back = false;
    }
	
	public void openFileChooserCallBack(ValueCallback<Uri[]> uploadMsg) {
    	mFilePathCallback5 = uploadMsg;
    	mSourceIntent = ImageUtil.choosePicture();
    	startActivityForResult(mSourceIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    	back = false;
     }

	/**
	 * 进度提示
	 * 
	 * @author Administrator
	 * 
	 */
	private class UpdataWebViewClient extends WebChromeClient {
		public void onProgressChanged(WebView view, int progress) {
			SDKActivity.this.setProgress(progress * 100);
			upload_prompt.setText(progress);
			if (progress == 100) {
				upload_prompt.setVisibility(View.GONE);
				adlist.setVisibility(View.VISIBLE);
			}
		}
				
		public void openFileChooser(ValueCallback<Uri> filePathCallback)
	    {
			openFileChooserCallBack(filePathCallback,"");
	    }

	    public void openFileChooser(ValueCallback filePathCallback, String acceptType)
	    {
	    	openFileChooserCallBack(filePathCallback,acceptType);
	    }

	    public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, String capture)
	    {
	    	openFileChooserCallBack(filePathCallback,acceptType);
	    }
	  
	    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
	    {
	    	  openFileChooserCallBack(filePathCallback);
              return true;            
	    }
	}

	/**
	 * 屏幕的宽度
	 * 
	 * @return
	 */
	private int getScreenWidth() {
		return getWindowManager().getDefaultDisplay().getWidth();
	}

	/**
	 * 关闭服务，停止监听
	 */
	@Override
	protected void onDestroy() {
		if (isBind) {
			unbindService(bine);
			isBind = false;
		}
		if (receiver != null) {
			unregisterReceiver(receiver);
		}

		super.onDestroy();
	}

	/**
	 * 重新刷新页面
	 */
	@Override
	protected void onResume() {
		if (back) {
			adlist.reload();
		}
		super.onResume();
	}

	/**
	 * 服务启动检查
	 * 
	 * @author Administrator
	 * 
	 */
	private class bineConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	}

	/**
	 * 软件退出，如体验时间还没到提示，如果没有注册提示
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果体验时间没有到提示
		if (keyCode == KeyEvent.KEYCODE_BACK && back) {
			super.finish();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK && !back) {
			back = true;
			if(Build.VERSION.SDK_INT >= Variable.SDK_VERSION){
				ActivityCacheUtils instance = ActivityCacheUtils.getInstance();
				String latestPackName = instance.getLatestPackName();
				AdInfo adInfo = ActivityCacheUtils.getInstance().getAdInfo(latestPackName);					
				if(adInfo != null){
					if(adInfo.getStartTime() > 0 ){
						//若未完成任务，即退出任务，则提示未完成
						if(adInfo.isAlertFlag()){
							adInfo.setAlertFlag(false);//提示之后，不再提示
							if(adInfo.isRegister()){
								Toast.makeText(SDKActivity.this, "该应用《"+adInfo.getAppName()+"》需先注册，注册后试用2分钟  即可获得奖励",
										Toast.LENGTH_LONG).show();
							}else{
								Toast.makeText(SDKActivity.this, "真可惜,《" + adInfo.getAppName() + "》的奖励还未得到，请再多用会吧",
										Toast.LENGTH_LONG).show();
							}
						}
					}
				}
			}
			adlist.goBack();
			return true;
		}
		return super.onKeyDown(keyCode,event);
	}
	/**
	 * 
	 * 监听开锁瓶，短信。
	 **/
	private void registerScreenActionReceiver() {
		final IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");//
		registerReceiver(receiver, filter);
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			Editor editor_packageName = sp_packageName.edit();

			if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) { // 锁屏
				if (isBind) {
					unbindService(bine);
					isBind = false;
				}
			}
			if ("android.intent.action.USER_PRESENT".equals(intent.getAction())) { // 解锁
				Intent startservice = new Intent(SDKActivity.this,
						ZYService.class);
				isBind = bindService(startservice, bine = new bineConnection(),
						BIND_AUTO_CREATE);
			}
			if ("android.provider.Telephony.SMS_RECEIVED".equals(intent
					.getAction())) { // 短信
				SmsMessage msg = null;
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					Object[] pdusObj = (Object[]) bundle.get("pdus");
					for (Object p : pdusObj) {
						msg = SmsMessage.createFromPdu((byte[]) p);
						String msgTxt = msg.getMessageBody();
						if (msgTxt.indexOf(sp_packageName.getString(
								"short_message", "")) >= 0) {
							editor_packageName.putString("short_message", "0"); //为0表示，已收到短信？
							editor_packageName.commit();
						}
					}
				}
			}
		}
	};
	
	
}
