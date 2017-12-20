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
 * @author ע��AndroidMainfast�����Ȩ�ް��ձ����ģ���Ҫ����Ȩ����д��
 * 
 */
public class TestActiyity extends Activity implements OnClickListener, Integral {
	// ��ȡ����б�
	private Button getAdlist;
	// �鿴����
	private Button check_integral;
	// �۳�����
	private Button minus_integral;
	// ���ӻ���
	private Button add_integral;
	// ��ʾ����
	private TextView show_integral;
	// ��ӻ���
	private EditText add_textintegral;
	// �۳�����
	private EditText minus_textintgral;
	// ��Կ
	private String AdpCode = "e9ff112bc855bf4c";
	//private String AdpCode = "813a98d16020e764";
	
	//private String AdpCode = "acfad95c5ffa7273";
	
	//private String AdpCode = "00f2ab0e105e8850";
	//���ذ�·��
	private String sdcard = Environment.getExternalStorageDirectory() + "/zy/";


	// �û�ID�����ڼ�¼������Ӧ�õ�Ψһ�û���ʶ,û��Ϊ��

	private String Other = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testactivity);
		initView();
	}
	
	/**
	 * ��ʼ���ؼ���������Ϣ
	 */
	private void initView() {

		getAdlist = (Button) findViewById(R.id.getAdlist);
		check_integral = (Button) findViewById(R.id.check_integral);
		show_integral = (TextView) findViewById(R.id.show_integral);
		minus_integral = (Button) findViewById(R.id.minus_integral);
		add_integral = (Button) findViewById(R.id.add_integral);
		add_textintegral = (EditText) findViewById(R.id.add_textintegral);
		minus_textintgral = (EditText) findViewById(R.id.minus_textintgral);
		// ���õ����¼�
		getAdlist.setOnClickListener(this);
		check_integral.setOnClickListener(this);
		minus_integral.setOnClickListener(this);
		add_integral.setOnClickListener(this);
		/*
		 * ��ʼ����Ϣ initAd()�����������onCreate()���棬AdpCode �ǿ����ߵ���ԿOther �������ص���ʱ��һ�𷵻�.
		 */
		SDKInit.initAd(this, AdpCode, Other);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.getAdlist:
			// ��ʼ���б�
			SDKInit.initAdList(this);
			break;
		// �鿴����
		case R.id.check_integral:
			SDKInit.checkIntegral(this);
			break;
		// �۳�����
		case R.id.minus_integral:
			SDKInit.minusIntegral(this, getIntgral(minus_textintgral));
			break;
		// ���ӻ���
		case R.id.add_integral:

			SDKInit.addIntegral(this, getIntgral(add_textintegral));
			break;
		default:
			break;
		}
	}

	/**
	 * ��ȡ����
	 * 
	 * @param edit
	 * @return
	 */
	public String getIntgral(EditText edit) {

		return edit.getText().toString();
	}

	/********************************** ���лص����������߳�ִ�� **********************************/
	/**
	 * �鿴���� retcode 0���ɹ���1��ʧ�ܣ�2�����ֲ����۳� ���ػ���
	 */
	@Override
	public void retCheckIntegral(String retcode, String integral) {
		if (retcode.equals("0")) {
			show_integral.setText("�����ڵĻ���Ϊ��" + integral);
			//show_integral.setText(ActivityCacheUtils.getInstance().getString(ActivityCacheUtils.getInstance().getLatestPackName()));
		} else if (retcode.equals("1")) {
			show_integral.setText("�鿴����ʧ��");
		} else if (retcode.equals("2")) {
			show_integral.setText("���ֲ����۳�");
		}

	}

	/**
	 * �۳�
	 */
	@Override
	public void retMinusIntegral(String retcode, String integral) {
		if (retcode.equals("0")) {
			show_integral.setText("�����ڵĻ���Ϊ��" + integral);
		} else if (retcode.equals("1")) {
			show_integral.setText("�鿴����ʧ��");
		} else if (retcode.equals("2")) {
			show_integral.setText("���ֲ����۳�");
		}
	}

	/**
	 * ����
	 */
	@Override
	public void retAddIntegral(String retcode, String integral) {
		if (retcode.equals("0")) {
			show_integral.setText("�����ڵĻ���Ϊ��" + integral);
		} else if (retcode.equals("1")) {
			show_integral.setText("�鿴����ʧ��");
		} else if (retcode.equals("2")) {
			show_integral.setText("���ֲ����۳�");
		}
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d("TestActiyity","onKeyUp");
		return super.onKeyUp(keyCode,event);
	}

}
