package com.surfs.storage.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.autumn.core.log.LogFactory;

public class HttpUtils {

	public static String getUrl(String ip, String port, String servicePath,
			String serviceName, String... args) throws UnsupportedEncodingException {
		StringBuilder params = new StringBuilder();
		params.append("http://");
		params.append(ip);
		params.append(":");
		params.append(port);
		params.append(servicePath);
		params.append(serviceName);
		
		// args
		for (String arg : args) {
			params.append("/");
			params.append(URLEncoder.encode(arg, "UTF-8"));
		}
		
		return params.toString();
	}
	
	public static String getUrlForParams(String ip, String port, String servicePath,
			String serviceName, Map<String, String> args) throws UnsupportedEncodingException {
		StringBuilder params = new StringBuilder();
		params.append("http://");
		params.append(ip);
		params.append(":");
		params.append(port);
		params.append(servicePath);
		params.append(serviceName);
		params.append("?");
		
		int size = args.size();
		// args
		for (Entry<String, String> arg : args.entrySet()) {
			--size;
			params.append(arg.getKey()+"=");
			params.append(URLEncoder.encode(arg.getValue(), "UTF-8"));
			if (size != 0)
				params.append("&");
		}
		
		return params.toString();
	}
	
	public static String invokeHttpForGet(String path, String... agrs) throws IOException {
		URL url = new URL(path);
		LogFactory.info("rest url:" + url.toString());
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(10000);
            con.setReadTimeout(1000 * 60 * 30);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            
            for (String string : agrs) {
            	con.setRequestProperty("Content-type", "application/json");
            	con.setRequestMethod("POST");
            	OutputStream out = con.getOutputStream();
            	out.write(string.getBytes("UTF-8"));
            }
           
            if(con.getResponseCode() != 200)
            	throw new ConnectException(con.getResponseMessage());
            	
            InputStream is = con.getInputStream();
            
            return readResponse(is);
            
        } catch(IOException e) {
        	throw e;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
	}
	
	private static String readResponse(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        IOUtils.copy(is, os);
        return new String(os.toByteArray(), "UTF-8");
    }
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		Map<String, String> args1 = new HashMap<String, String>();
		args1.put("1", "1");
		args1.put("2", "2");
		args1.put("3", "3");
		
		String url = getUrlForParams("127.0.0.1","8080","/service","/test",args1);
		System.out.println(url);
	}
	
}
