package com.zy.phone;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class IntegralQuery {
	/**
	 * 获取数据
	 * 
	 * @param integral
	 * @param str_integral
	 * @param or
	 * @return
	 * @throws Exception
	 */
	public static String analysisjson(String str_params) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpParams params = null;
		params = client.getParams();

		HttpConnectionParams.setConnectionTimeout(params, 30000);// 设置网络的连接的超时时间
		HttpConnectionParams.setSoTimeout(params, 30000);// 设置读数据的超时时间

		HttpPost request = new HttpPost(Variable.URL);
		request.setParams(params);
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("MSG", str_params));
		request.setEntity(new UrlEncodedFormEntity(postParameters, HTTP.UTF_8));
		HttpResponse httpResponse = client.execute(request);
		String retSrc = EntityUtils.toString(httpResponse.getEntity(),
				HTTP.UTF_8);
		//System.out.println("retSrc:"+retSrc.trim());
		return retSrc;
	}
}
