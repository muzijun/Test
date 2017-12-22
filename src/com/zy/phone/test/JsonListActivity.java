package com.zy.phone.test;

import com.zy.phone.R;
import com.zy.phone.SDKInit;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.erm.integralwall.core.AppTaskMananger;
import com.erm.integralwall.core.Constant;
import com.erm.integralwall.core.NetManager;
import com.erm.integralwall.core.net.IResponseListener;
import com.erm.integralwall.core.params.FormParams.FormConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class JsonListActivity extends Activity {
	private ListView mAdvertListView;
	private AdvertsAdapter mAdvertsAdapter;
	private static PowerManager.WakeLock wakeLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_activity_main);
		AppTaskMananger.opentask(this);
		mAdvertListView = (ListView) findViewById(R.id.ads_listview);
		mAdvertsAdapter = new AdvertsAdapter(this);
		mAdvertListView.setAdapter(mAdvertsAdapter);

		NetManager.getInstance().fetchAdvertsJsonByRequestParams(
				new IResponseListener<JSONObject>() {

					@Override
					public void onResponse(JSONObject jsonObject) {
						// TODO Auto-generated method stub
						try {
							org.json.JSONArray jsonArray = jsonObject
									.getJSONArray("AdsList");
							String arrayString = jsonArray.toString();
							java.lang.reflect.Type listType = new TypeToken<ArrayList<Advert>>() {
							}.getType();
							Gson gson = new Gson();
							List<Advert> list = gson.fromJson(arrayString,
									listType);
							mAdvertsAdapter.setUpdata(list);
							mAdvertsAdapter.notifyDataSetChanged();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						System.out
								.println("fetchAdvertsJsonByRequestParams VolleyError: "
										+ error);
					}

					@Override
					public void cancel() {
						// TODO Auto-generated method stub

					}
				});

		mAdvertListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
			registerForContextMenu(mAdvertListView);
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("选择");
		// 添加菜单项
		menu.add(0, Menu.FIRST, 0, "网页详情");
		menu.add(0, Menu.FIRST + 1, 0, "json详情");

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getItemId() == 1) {
			SDKInit.showUI(this, mAdvertsAdapter.getmAdvertsList().get(info.position).getDetailUrl());

		} else if (item.getItemId() == 2) {
			Advert item_content = mAdvertsAdapter.getItem(info.position);
			Intent intent = new Intent(JsonListActivity.this,
					DetailActivity.class);
			intent.putExtra("ID", item_content.getAdsId());
			startActivity(intent);
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 关闭服务，停止监听
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		NetManager.getInstance().cancelAll();
		AppTaskMananger.cancelTask(this);
		// unregisterReceiver(mTaskBroadcastReceiver);
		// mTaskBroadcastReceiver = null;
	}
}
