package com.surfs.storage.monitor.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.autumn.core.log.LogFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.surfs.storage.common.util.CmdUtils;
import com.surfs.storage.common.util.Constant;
import com.surfs.storage.common.util.HttpUtils;
import com.surfs.storage.monitor.model.Monitor;
import com.surfs.storage.monitor.service.ClusterService;
import com.surfs.storage.monitor.service.MonitorConstant;

@Service("clusterService")
public class ClusterServiceImpl implements ClusterService {

	@Override
	public List<Monitor> getClusterList() {
		List<Monitor> list = new ArrayList<>();
		Monitor local = getLocalStatus();
		Monitor remote = getRemoteStatus();
		
		list.add(local);
		list.add(remote);
		return list;
	}
	
	private Monitor getStatus(String ip, String zpoolJson, String networkJson) {
		Monitor monitor = new Monitor();
		monitor.setIp(ip);
		Map<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();
		monitor.setMap(maps);
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			if (!StringUtils.isBlank(zpoolJson)) {
				Map<String, String> zpoolMap = objectMapper.readValue(
						zpoolJson, new TypeReference<Map<String, String>>() {});
				maps.put("zpool status", zpoolMap);
			}
			if (!StringUtils.isBlank(networkJson)) {
				Map<String, String> networkMap = objectMapper.readValue(
						zpoolJson, new TypeReference<Map<String, String>>() {});
				maps.put("network status", networkMap);
			}
		} catch (IOException e) {
			LogFactory.trace("remote is " + ip, e);
		}

		return monitor;
	}

	@Override
	public Monitor getRemoteStatus() {
		String ip = CmdUtils.getRemoteIp();
		// String ip = "10.0.33.52";
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String url = HttpUtils.getUrl(ip, Constant.REST_SERVICE_PORT,
					MonitorConstant.CLUSTER_SERVICE_PATH,
					MonitorConstant.CLUSTER_SERVICE_STATUS_NAME);
			String remoteJson = HttpUtils.invokeHttpForGet(url);
			if (!StringUtils.isBlank(remoteJson)) {
				return objectMapper.readValue(
						remoteJson, new TypeReference<Monitor>() {});
			}
		} catch (IOException e) {
			LogFactory.error(e.getMessage());
		}
		return null;
	}
	
	@Override
	public Monitor getLocalStatus() {
		String ip = CmdUtils.getLocalhostIp();
		
		String zpoolJson = getZpoolStatus();
		String networkJson =getNetworkStatus();
		
		return getStatus(ip, zpoolJson, networkJson);
	}

	private String getNetworkStatus() {
		return CmdUtils
				.executeCmdForString(MonitorConstant.NETWORK_STATUS_PATH);
	}

	private String getZpoolStatus() {
		return CmdUtils.executeCmdForString(MonitorConstant.ZPOOL_STATUS_PATH);
	}

}
