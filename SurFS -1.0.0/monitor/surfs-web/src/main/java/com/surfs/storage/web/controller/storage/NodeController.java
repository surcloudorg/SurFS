/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.web.controller.storage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.surfs.nas.NodeProperties;
import com.surfs.nas.NosqlDataSource;
import com.surfs.nas.ResourcesAccessor;
import com.surfs.nas.client.Setup;
import com.surfs.storage.web.utils.WebUtils;

@Controller
@RequestMapping("/storage")
public class NodeController {

	@RequestMapping(value = "/showNodeProperties.do")
	public ModelAndView showNodeProperties(HttpSession session)
			throws IOException {
		NosqlDataSource datasource = new Setup(
				WebUtils.getCrrentDataCenterKey(session)).getDataSource();
		return new ModelAndView("node", "listNodeProperties", datasource
				.getResourcesAccessor().listNodeProperties());
	}

	@RequestMapping(method = RequestMethod.POST, value = "/saveNodeProperties.do")
	@ResponseBody
	public void saveNodeProperties(@RequestBody NodeProperties nodeProperties,
			HttpSession session) throws IOException {
		NosqlDataSource datasource = new Setup(
				WebUtils.getCrrentDataCenterKey(session)).getDataSource();
		ResourcesAccessor accessor = datasource.getResourcesAccessor();
		accessor.putNodeProperties(nodeProperties);
		accessor.updateClientSourceVersion(); // 使客户端生效
		accessor.updateServerSourceVersion(); // 使服务端生效
	}

	@RequestMapping(method = RequestMethod.POST, value = "/isReadyNodes.do")
	@ResponseBody
	public Map<String, Boolean> isReadyNodes(HttpSession session)
			throws IOException {
		Setup setup = new Setup(WebUtils.getCrrentDataCenterKey(session));
		NodeProperties[] nodeProperty = setup.getDataSource()
				.getResourcesAccessor().listNodeProperties();
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		for (NodeProperties nodeProperties : nodeProperty) {
			boolean isReady = setup.isReady(nodeProperties.getServerHost());
			map.put(nodeProperties.getServerHost(), isReady);
		}
		return map;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/deleteNode.do")
	@ResponseBody
	public void deleteNode(@RequestBody String serverHost, HttpSession session)
			throws IOException {
		new Setup(WebUtils.getCrrentDataCenterKey(session)).getDataSource()
				.getResourcesAccessor().deleteNodeProperties(serverHost);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/scanNodeVolume.do")
	public ModelAndView scanNodeVolume(String serverHost, HttpSession session)
			throws IOException {
		try {
			new Setup(WebUtils.getCrrentDataCenterKey(session)).scan(serverHost);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView(
				"redirect:/storage/showNodeProperties.do");
	}

}
