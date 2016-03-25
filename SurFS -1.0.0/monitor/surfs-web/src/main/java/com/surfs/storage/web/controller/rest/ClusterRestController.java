package com.surfs.storage.web.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.surfs.storage.monitor.model.Monitor;
import com.surfs.storage.monitor.service.ClusterService;

@Controller
@RequestMapping("/service/cluster")
public class ClusterRestController {
	
	@Autowired
	private ClusterService clusterService;
	
	@RequestMapping(method = RequestMethod.GET, value = "/getRemoteClusterInfos")
	@ResponseBody
	public Monitor getRemoteClusterInfos() {
		return clusterService.getLocalStatus();
	}

}
