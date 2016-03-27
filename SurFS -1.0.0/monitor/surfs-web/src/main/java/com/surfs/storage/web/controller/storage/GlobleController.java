/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.web.controller.storage;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.surfs.nas.GlobleProperties;
import com.surfs.nas.NosqlDataSource;
import com.surfs.nas.ResourcesAccessor;
import com.surfs.nas.StorageSources;
import com.surfs.nas.client.Setup;
import com.surfs.storage.web.utils.WebUtils;

@Controller
@RequestMapping("/storage")
public class GlobleController {
	
	@RequestMapping(value = "/globleProperties.do")
	public ModelAndView globleProperties(HttpSession session) {
		String dataCenterName = WebUtils.getCrrentDataCenterName(session);
		String dataCenterKey = WebUtils.getCrrentDataCenterKey(session);
		if (StringUtils.isBlank(dataCenterName)) {
			Map<String, String> dataCenterMap = getDataCenterMap();
			for (Entry<String, String> dataCenterObj : dataCenterMap.entrySet()) {
				dataCenterName = dataCenterObj.getValue();
				dataCenterKey = dataCenterObj.getKey();
				// set default dataCenterName
				session.setAttribute("dataCenterKey", dataCenterKey);
				session.setAttribute("dataCenterName", dataCenterName);
				// remove default dataCenterName
				dataCenterMap.remove(dataCenterKey);
				break;
			}
			if (dataCenterMap.size() > 0)
				session.setAttribute("dataCenterMap", dataCenterMap);
		}

		try {
			GlobleProperties globleProperties = new Setup(dataCenterKey).getDataSource().getResourcesAccessor().getGlobleProperties();
			
			return new ModelAndView("globle", "globleProperties", globleProperties);
		} catch (Exception e) {
			session.setAttribute("error", e.getMessage());
			return new ModelAndView("redirect:/login.jsp");
		}
	}
	
	public Map<String, String> getDataCenterMap() {
		return StorageSources.getStoragePoolMap();
	}
	
	@RequestMapping(value = "/switchDataCenter.do")
	public ModelAndView switchDataCenter(String dataCenterKey, HttpSession session) {
		
		Map<String, String> dataCenterMap = getDataCenterMap();
		String dataCenterName = dataCenterMap.get(dataCenterKey);
		session.setAttribute("dataCenterKey", dataCenterKey);
		session.setAttribute("dataCenterName", dataCenterName);
		dataCenterMap.remove(dataCenterKey);
		if (dataCenterMap.size() > 0)
			session.setAttribute("dataCenterMap", dataCenterMap);
		
		return new ModelAndView(
				"redirect:/storage/showGlobleProperties.do");
	}

	@RequestMapping(method = RequestMethod.POST, value = "/saveGlobleProperties.do")
	public ModelAndView saveGlobleProperties(GlobleProperties globleProperties, HttpSession session)
			throws IOException {
		NosqlDataSource datasource = new Setup(WebUtils.getCrrentDataCenterKey(session)).getDataSource();
		ResourcesAccessor accessor = datasource.getResourcesAccessor();
		if (!globleProperties.getMntPoint().startsWith("/"))
			globleProperties.setMntPoint("/" + globleProperties.getMntPoint());
		//globlePropertiesConvert(globleProperties);
		accessor.putGlobleProperties(globleProperties);
		accessor.updateClientSourceVersion(); // 使客户端生效
		accessor.updateServerSourceVersion(); // 使服务端生效
		return new ModelAndView("globle", "save_success", true);
	}
	
	public void globlePropertiesConvert(GlobleProperties globleProperties) {
		// KB转换为字节
		//globleProperties.setPrereadSize(globleProperties.getPrereadSize() * 1024);
		// MB转换为字节
		//globleProperties.setPkgMaxSize(globleProperties.getPkgMaxSize() * 1024 * 1024);
		// MB转换为字节
		//globleProperties.setPackThresholdSize(globleProperties.getPackThresholdSize() * 1024 * 1024);
		// KB转换为字节
		globleProperties.setBlocksize(globleProperties.getBlocksize() * 1024);
		// 秒转换为毫秒
		globleProperties.setErrRetryInterval(globleProperties.getErrRetryInterval() * 1000);
		// 秒转换为毫秒
		globleProperties.setSessionTimeout(globleProperties.getSessionTimeout() * 1000);
		// 天转换为毫秒
		//globleProperties.setFileTimeout(globleProperties.getFileTimeout() * 1000 * 60 * 60 * 24);
		// 分钟转换为毫秒
		globleProperties.setCheckSpaceInterval(globleProperties.getCheckSpaceInterval() * 1000);
		// 秒转换为毫秒
		globleProperties.setReadTimeout(globleProperties.getReadTimeout() * 1000);
		// 秒转换为毫秒
		globleProperties.setConnectTimeout(globleProperties.getConnectTimeout() * 1000);
		// 秒转换为毫秒
		//globleProperties.setConnectIdleTime(globleProperties.getConnectIdleTime() * 1000 * 60);
		// 秒转换为毫秒
		globleProperties.setReloadInterval(globleProperties.getReloadInterval() * 1000);
		// GB转换为字节
		globleProperties.setSpaceThresholdSize(globleProperties.getSpaceThresholdSize() * 1073741824);
	}

}
