package com.zy.phone.test;

import java.util.ArrayList;
import java.util.List;

import com.zy.phone.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdvertsAdapter extends BaseAdapter{

	private Context mContext;
	private List<Advert> mAdvertsList = new ArrayList<Advert>();

	public AdvertsAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}
	
	public void setUpdata(List<Advert> list){
		mAdvertsList.clear();
		mAdvertsList.addAll(list);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mAdvertsList.size();
	}

	@Override
	public Advert getItem(int position) {
		// TODO Auto-generated method stub
		return mAdvertsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder mViewHolder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.test_adverts_item, null, false);
			mViewHolder = new ViewHolder(convertView);
			convertView.setTag(mViewHolder);;
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		mViewHolder.mContent.setText(mAdvertsList.get(position).toString());
		return convertView;
	}
	
	
	public List<Advert> getmAdvertsList() {
		return mAdvertsList;
	}

	public void setmAdvertsList(List<Advert> mAdvertsList) {
		this.mAdvertsList = mAdvertsList;
	}


	static class ViewHolder {
		public TextView mContent;
		
		public  ViewHolder(View view){
			mContent = (TextView) view.findViewById(R.id.content);
		}
	}

}
