package com.surfs.storage.web.controller.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.surfs.storage.monitor.service.ClusterService;

@Controller
@RequestMapping("/monitor")
public class ClusterController {
	
	@Autowired
	private ClusterService clusterService;

	@RequestMapping(value = "/showCluster.do")
	public ModelAndView showCluster() {
		return new ModelAndView("monitor/cluster", "clusterList",
				clusterService.getClusterList());
	}
	
}
