package com.zy.phone.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.VolleyError;
import com.erm.integralwall.core.NetManager;
import com.erm.integralwall.core.download.ResponseProgressListenerImpl;
import com.erm.integralwall.core.net.IResponseListener;
import com.google.gson.Gson;
import com.zy.phone.R;
import com.zy.phone.test.DetailBzip.Task;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends Activity{
	
	private TaskAdapter mTaskAdapter;
	private ListView mTaskListView;
	private TextView mDetailTextVew;
	private Button mDownload;
	private Button mCancel;
	private int mAdvertID = 0;
	private String mUrl = null;
	private DetailBzip mDetailBzip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_detail_layout);
		mTaskListView = (ListView) findViewById(R.id.task_listview);
		mTaskAdapter = new TaskAdapter(this);
		mTaskListView.setAdapter(mTaskAdapter);
		
		mDetailTextVew = (TextView) findViewById(R.id.detail);
		Intent intent = getIntent();
		if(null != intent){
			mAdvertID = intent.getIntExtra("ID", 1995);
			NetManager.getInstance().fetchAdvertsDetailJsonByRequestParams(String.valueOf(mAdvertID), new IResponseListener<JSONObject>() {
				

				@Override
				public void onResponse(JSONObject jsonObject) {
					// TODO Auto-generated method stub
					Gson gson = new Gson();
					mDetailBzip = gson.fromJson(jsonObject.toString(), DetailBzip.class);
					mTaskAdapter.setUpData(mDetailBzip.getTask());
					mDetailTextVew.setText(mDetailBzip.toString());
				}
				
				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					System.out.println("fetchAdvertsDetailJsonByRequestParams VolleyError: " + error);
				}
				
				@Override
				public void cancel() {
					// TODO Auto-generated method stub
					
				}
			});
		} else {
			Toast.makeText(this, "鏃犳晥鐨勫弬鏁�", 1).show();;
		}
		
		mDownload = (Button) findViewById(R.id.download);
		mDownload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NetManager.getInstance().fetchApkUrlByAdsID(String.valueOf(mAdvertID), mDetailBzip.PackName, new IResponseListener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject jsonObject) {
						// TODO Auto-generated method stub
						String url;
						try {
							url = jsonObject.getString("Url");
							Log.d("ArMn", "download info:" + url);
							mUrl = url;
							download(mAdvertID + ".apk", url);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d("ArMn", "error msg: " + error);
					}
					
					@Override
					public void cancel() {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
		
		mCancel = (Button) findViewById(R.id.cancel);
		mCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NetManager.getInstance().cancel(mUrl);
			}
		});
	}
	
	private void download(String name, String url){
		
//		String SDPath = Environment.getExternalStorageDirectory() + "/zy/";
		String SDPath = Environment.getExternalStorageDirectory() + "/zy/";
		
		NetManager.getInstance().openOrDownload(url,
				SDPath, name, mDetailBzip.PackName, new ResponseProgressListenerImpl(DetailActivity.this) {
			
			@Override
			public void onSuccess(String path) {
				// TODO Auto-generated method stub
				Log.d("onSuccess", "path=" + path);
			}
			
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				Log.d("onStart", "======onStart=========");
			}
			
			@Override
			public void onProgress(int percent) {
				// TODO Auto-generated method stub
				Log.d("onResponse", "progress=" + percent);
				mDownload.setText("褰撳墠杩涘害=" + percent +"%");
			}
			
			@Override
			public void onFailure(String message) {
				// TODO Auto-generated method stub
				Toast.makeText(DetailActivity.this, message, Toast.LENGTH_LONG).show();
			}
		}, true, String.valueOf(mAdvertID));
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		NetManager.getInstance().cancel(mUrl);
	}

	static class TaskAdapter extends BaseAdapter{

		private Context mContext;
		
		public List<DetailBzip.Task> mTaskList = new ArrayList<Task>();
		public TaskAdapter(Context context) {
			// TODO Auto-generated constructor stub
			mContext = context;
		}
		
		public void setUpData(Map<String, DetailBzip.Task> taskMap){
			mTaskList.clear();
			
			Set<String> keySet = taskMap.keySet();
			Iterator<String> iterator = keySet.iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
				Task task = taskMap.get(key);
				mTaskList.add(task);
			}
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mTaskList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mTaskList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			ViewHolder mViewHolder = null;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.test_adverts_item, null, false);
				mViewHolder = new ViewHolder(convertView);
				convertView.setTag(mViewHolder);;
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}
			
			mViewHolder.mContent.setText(mTaskList.get(position).toString());
			return convertView;
		}
		
		static class ViewHolder {
			public TextView mContent;
			
			public  ViewHolder(View view){
				mContent = (TextView) view.findViewById(R.id.content);
			}
		}
	}
}
