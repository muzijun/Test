package com.zy.phone.sdk;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zy.phone.R;
import com.zy.phone.SDKInit;
import com.zy.phone.net.Integral;

/**
 * 
 * @author 注意AndroidMainfast里面的权限按照本案的，不要忘记权限填写。
 * 
 */
public class TestActiyity extends Activity implements OnClickListener, Integral {
	// 获取广告列表
	private Button getAdlist;
	// 查看积分
	private Button check_integral;
	// 扣除积分
	private Button minus_integral;
	// 增加积分
	private Button add_integral;
	// 显示积分
	private TextView show_integral;
	// 添加积分
	private EditText add_textintegral;
	// 扣除积分
	private EditText minus_textintgral;
	// 秘钥
	private String AdpCode = "e9ff112bc855bf4c";
	//private String AdpCode = "813a98d16020e764";
	
	//private String AdpCode = "acfad95c5ffa7273";
	
	//private String AdpCode = "00f2ab0e105e8850";
	//下载包路径
	private String sdcard = Environment.getExternalStorageDirectory() + "/zy/";


	// 用户ID，用于记录开发者应用的唯一用户标识,没有为空

	private String Other = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testactivity);
		initView();
	}
	
	/**
	 * 初始化控件和数据信息
	 */
	private void initView() {

		getAdlist = (Button) findViewById(R.id.getAdlist);
		check_integral = (Button) findViewById(R.id.check_integral);
		show_integral = (TextView) findViewById(R.id.show_integral);
		minus_integral = (Button) findViewById(R.id.minus_integral);
		add_integral = (Button) findViewById(R.id.add_integral);
		add_textintegral = (EditText) findViewById(R.id.add_textintegral);
		minus_textintgral = (EditText) findViewById(R.id.minus_textintgral);
		// 设置单击事件
		getAdlist.setOnClickListener(this);
		check_integral.setOnClickListener(this);
		minus_integral.setOnClickListener(this);
		add_integral.setOnClickListener(this);
		/*
		 * 初始化信息 initAd()这个方法放在onCreate()里面，AdpCode 是开发者的秘钥Other 服务器回调的时候一起返回.
		 */
		SDKInit.initAd(this, AdpCode, Other);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.getAdlist:
			// 初始化列表
			SDKInit.initAdList(this);
			break;
		// 查看积分
		case R.id.check_integral:
			SDKInit.checkIntegral(this);
			break;
		// 扣除积分
		case R.id.minus_integral:
			SDKInit.minusIntegral(this, getIntgral(minus_textintgral));
			break;
		// 增加积分
		case R.id.add_integral:

			SDKInit.addIntegral(this, getIntgral(add_textintegral));
			break;
		default:
			break;
		}
	}

	/**
	 * 获取积分
	 * 
	 * @param edit
	 * @return
	 */
	public String getIntgral(EditText edit) {

		return edit.getText().toString();
	}

	/********************************** 所有回调方法，主线程执行 **********************************/
	/**
	 * 查看积分 retcode 0：成功，1：失败，2：积分不够扣除 返回积分
	 */
	@Override
	public void retCheckIntegral(String retcode, String integral) {
		if (retcode.equals("0")) {
			show_integral.setText("您现在的积分为：" + integral);
			//show_integral.setText(ActivityCacheUtils.getInstance().getString(ActivityCacheUtils.getInstance().getLatestPackName()));
		} else if (retcode.equals("1")) {
			show_integral.setText("查看积分失败");
		} else if (retcode.equals("2")) {
			show_integral.setText("积分不够扣除");
		}

	}

	/**
	 * 扣除
	 */
	@Override
	public void retMinusIntegral(String retcode, String integral) {
		if (retcode.equals("0")) {
			show_integral.setText("您现在的积分为：" + integral);
		} else if (retcode.equals("1")) {
			show_integral.setText("查看积分失败");
		} else if (retcode.equals("2")) {
			show_integral.setText("积分不够扣除");
		}
	}

	/**
	 * 增加
	 */
	@Override
	public void retAddIntegral(String retcode, String integral) {
		if (retcode.equals("0")) {
			show_integral.setText("您现在的积分为：" + integral);
		} else if (retcode.equals("1")) {
			show_integral.setText("查看积分失败");
		} else if (retcode.equals("2")) {
			show_integral.setText("积分不够扣除");
		}
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d("TestActiyity","onKeyUp");
		return super.onKeyUp(keyCode,event);
	}

}
