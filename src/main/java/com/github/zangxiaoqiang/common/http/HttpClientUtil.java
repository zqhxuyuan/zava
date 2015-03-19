package com.github.zangxiaoqiang.common.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpClientUtil {
	public static String getStringFromStream(InputStream input) {
		String body = null;
		try {
			ByteArrayOutputStream bao = new ByteArrayOutputStream(512);
			byte[] bb = new byte[512];
			int len = 0;
			while ((len = input.read(bb)) > 0) {
				bao.write(bb, 0, len);
			}
			body = bao.toString();
		} catch (Exception e) {
			body = "";
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return body;
	}

	public static String requestGet(String url) throws Exception {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpgets = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpgets);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instreams = entity.getContent();
			String str = getStringFromStream(instreams);
			// Do not need the rest
			httpgets.abort();
			return str;
		}
		return "";
	}
}
