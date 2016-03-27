/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.autumn.core.log.LogFactory;

public class CmdUtils {
	
	public static CmdResponse executeCommand(String cmd) {
		BufferedReader bufRead = null;
		try {
			//bufRead = executeCmdForReader(cmd);
			Process pro = Runtime.getRuntime().exec(cmd);
			bufRead = new BufferedReader(new InputStreamReader(
					pro.getInputStream(), "UTF-8"));
			// 0-success,others-failure
			int status = pro.waitFor();
			String response = bufRead.readLine();
			LogFactory.info("cmd:" + cmd);
			LogFactory.info("status:" + status);
			LogFactory.info("response:" + response);
			return new CmdResponse(status, response);
		} catch (Exception e) {
			return new CmdResponse(500, e.getMessage());
		} finally {
			if (bufRead != null)
				try {
					bufRead.close();
				} catch (Exception e) {
					return new CmdResponse(500, e.getMessage());
				}
		}
	}
	
	public static Map<String, Object> command(String cmd) {
		Map<String, Object> resp = new HashMap<String, Object>();
		BufferedReader bufRead = null;
		try {
			//bufRead = executeCmdForReader(cmd);
			Process pro = Runtime.getRuntime().exec(cmd);
			bufRead = new BufferedReader(new InputStreamReader(
					pro.getInputStream(), "UTF-8"));
			// 0-success,others-failure
			int status = pro.waitFor();
			LogFactory.info("cmd:" + cmd);
			LogFactory.info("status:" + status);
			String response = null;
			while ((response = bufRead.readLine()) != null) {
				if (response.indexOf("avg") >= 0) {
					String[] ms = response.split("=")[1].split("\\/");
					resp.put("status", status);
					resp.put("latency", Float.parseFloat(ms[1]));
					LogFactory.info("latency:" + ms[1]);
					return resp;
				}
			}
		} catch (Exception e) {
			resp.put("status", 1);
			resp.put("response", "time out");
			return resp;
		} finally {
			if (bufRead != null)
				try {
					bufRead.close();
				} catch (Exception e) {
				}
		}
		resp.put("status", 1);
		resp.put("response", "time out");
		return resp;
	}

	public static String executeCmdForString(String cmd) {
		BufferedReader bufRead = null;
		try {
			bufRead = executeCmdForReader(cmd);
			return bufRead.readLine();
		} catch (IOException e) {
			LogFactory.error(e.getMessage());
		} finally {
			if (bufRead != null)
				try {
					bufRead.close();
				} catch (IOException e) {
					LogFactory.error(e.getMessage());
				}
		}
		return null;
	}

	public static BufferedReader executeCmdForReader(String cmd) throws IOException {
		Process pro = Runtime.getRuntime().exec(cmd);
		BufferedReader bufReader = new BufferedReader(new InputStreamReader(
				pro.getInputStream(), "UTF-8"));
		return bufReader;
	}
	
	public static String getRemoteIp() {
		String result = CmdUtils
				.executeCmdForString(Constant.REMOTE_HOST_CMD);
		if (StringUtils.isBlank(result))
			throw new NullPointerException("remote ip is empty");
		String[] results = result.split("=");
		return results[1];
	}
	
	public static String getLocalhostIp() {
		try {
			InetAddress inet = InetAddress.getLocalHost();
			return inet.getHostAddress();
		} catch (UnknownHostException e) {
			throw new NullPointerException("local ip is empty");
		}
	}
	
	public static String getProjectPath() {
		return System.getProperty("user.dir");
	}
	
	public static void main(String[] args) {
		System.out.println(getProjectPath());
	}

}
