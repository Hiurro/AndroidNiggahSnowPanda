package com.eventxiagency.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class HttpRequest {
	private HttpClient httpClient;
	private HttpPost httpPost;
	private HttpResponse response;
	public String responseString = null;
	private StatusLine statusLine;
	private ByteArrayOutputStream out;
	
	public HttpRequest(String url, List<String[]> datas)
	{
		try
		{
			httpClient = new DefaultHttpClient();
			httpPost = new HttpPost(url);
			List<NameValuePair> d = new ArrayList<NameValuePair>(datas.size());
			for(String[] s : datas)
			{
				d.add(new BasicNameValuePair(s[0], s[1]));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(d, "utf-8"));
			response = httpClient.execute(httpPost);
			statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == HttpStatus.SC_OK)
			{
				out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				responseString = out.toString();
			}
			else
			{
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		}
		catch(ClientProtocolException e)
		{
			System.out.println(e.toString());
		}
		catch(IOException e)
		{
			System.out.println(e.toString());
		}
	}
}
