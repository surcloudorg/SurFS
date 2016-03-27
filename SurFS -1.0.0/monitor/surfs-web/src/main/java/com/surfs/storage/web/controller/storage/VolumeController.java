/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.web.controller.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.surfs.nas.NosqlDataSource;
import com.surfs.nas.ResourcesAccessor;
import com.surfs.nas.VolumeProperties;
import com.surfs.nas.client.Setup;
import com.surfs.nas.error.NosqlException;
import com.surfs.nas.error.VolumeNotFoundException;
import com.surfs.nas.server.HandleProgress;
import com.surfs.storage.web.utils.JsonUtils;
import com.surfs.storage.web.utils.WebUtils;

@Controller
@RequestMapping("/storage")
public class VolumeController {

	/**
	 * 根据serverhost查询服务卷信息
	 * 
	 * @param serverHost
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/showVolumeProperties.do")
	public ModelAndView showVolumeProperties(String serverHost,
			HttpSession session) throws IOException {
		List<VolumeProperties> listVolumeProperties = new ArrayList<VolumeProperties>();
		for (VolumeProperties volumeProperties : getAllVolumeProperty(session)) {
			if (serverHost != null
					&& serverHost.equals(volumeProperties.getServerHost())) {
				listVolumeProperties.add(volumeProperties);
			}
		}
		ModelAndView model = new ModelAndView("volume");
		model.addObject("listVolumeProperties", listVolumeProperties);
		model.addObject("volumeSpaces", JsonUtils
				.objectConvertJsonString(queryVolumeSpace(
						listVolumeProperties.toArray(), session)));
		model.addObject("currentThreadNum", JsonUtils
				.objectConvertJsonString(queryCurrentThreadNum(
						listVolumeProperties.toArray(), session)));
		return model;
	}

	/**
	 * 查询所有服务节点下的卷信息
	 * 
	 * @return
	 * @throws NosqlException
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/showListVolumeProperties.do")
	public ModelAndView showNodeProperties(HttpSession session)
			throws NosqlException, IOException {
		ModelAndView model = new ModelAndView("volume");
		VolumeProperties[] volumeProperty = getAllVolumeProperty(session);
		model.addObject("listVolumeProperties", volumeProperty);
		model.addObject("volumeSpaces", JsonUtils
				.objectConvertJsonString(queryVolumeSpace(volumeProperty,
						session)));
		model.addObject("currentThreadNum", JsonUtils
				.objectConvertJsonString(queryCurrentThreadNum(volumeProperty,
						session)));
		return model;
	}

	/**
	 * 获得所有卷信息
	 * 
	 * @return
	 * @throws IOException
	 */
	private VolumeProperties[] getAllVolumeProperty(HttpSession session)
			throws IOException {
		Setup setup = new Setup(WebUtils.getCrrentDataCenterKey(session));
		NosqlDataSource datasource = setup.getDataSource();
		List<VolumeProperties> listVolumeProperties = new ArrayList<VolumeProperties>(
				Arrays.asList(datasource.getResourcesAccessor()
						.listVolumeProperties()));
		for (Iterator<VolumeProperties> iterator = listVolumeProperties
				.iterator(); iterator.hasNext();) {
			VolumeProperties volumeProperties = iterator.next();
			if (!setup.isReady(volumeProperties.getServerHost())) {
				iterator.remove();
			}
		}

		return (VolumeProperties[]) listVolumeProperties
				.toArray(new VolumeProperties[listVolumeProperties.size()]);
	}

	/**
	 * 修改单个卷信息配置
	 * 
	 * @param volumeProperties
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/saveVolumeProperties.do")
	@ResponseBody
	public void saveVolumeProperties(
			@RequestBody VolumeProperties volumeProperties, HttpSession session)
			throws IOException {
		NosqlDataSource datasource = new Setup(
				WebUtils.getCrrentDataCenterKey(session)).getDataSource();
		ResourcesAccessor accessor = datasource.getResourcesAccessor();
		accessor.putVolumeProperties(volumeProperties);
		accessor.updateClientSourceVersion(); // 使客户端生效
		accessor.updateServerSourceVersion(); // 使服务端生效
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/queryVolumeSpace.do")
	@ResponseBody
	public List<Map<String, String>> queryVolumeSpace(
			@RequestBody String[] arrayVolumeID, HttpSession session)
			throws IOException {
		List<VolumeProperties> listVolumeProperties = new ArrayList<VolumeProperties>();
		for (VolumeProperties volumeProperties : getAllVolumeProperty(session)) {
			for (String volumeID : arrayVolumeID) {
				if (volumeID.equals(volumeProperties.getVolumeID())) {
					listVolumeProperties.add(volumeProperties);
				}
			}
		}
		return queryVolumeSpace(listVolumeProperties.toArray(), session);
	}

	/**
	 * 根据卷id查询卷空间信息
	 * 
	 * @param volumeProperty
	 * @return
	 * @throws IOException
	 */
	private List<Map<String, String>> queryVolumeSpace(Object[] volumeProperty,
			HttpSession session) throws IOException {
		List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();

		Setup setup = new Setup(WebUtils.getCrrentDataCenterKey(session));

		for (Object obj : volumeProperty) {
			VolumeProperties volumeProperties = (VolumeProperties) obj;
			try {
				String volumeJson = setup.getVolumeSpace(volumeProperties
						.getVolumeID());
				Map<String, String> map = JsonUtils.jsonStringConvertMap(
						volumeJson, "freeSpace", "totalSpace");
				map.put("volumeID", volumeProperties.getVolumeID());
				listMap.add(map);
			} catch (VolumeNotFoundException e) {
				continue;
			}
		}
		return listMap;
	}

	/**
	 * 根据卷id查询卷的当前并发读写线程数
	 * 
	 * @param volumeProperty
	 * @return
	 * @throws IOException
	 */
	private List<Map<String, Object>> queryCurrentThreadNum(
			Object[] volumeProperty, HttpSession session) throws IOException {
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		Setup setup = new Setup(WebUtils.getCrrentDataCenterKey(session));
		for (Object obj : volumeProperty) {
			VolumeProperties volumeProperties = (VolumeProperties) obj;
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				int currentThreadNum = setup
						.getThreadInfo(volumeProperties.getVolumeID()).length;
				map.put("currentThreadNum", currentThreadNum);
			} catch (VolumeNotFoundException e) {
			} finally {
				map.put("volumeID", volumeProperties.getVolumeID());
				listMap.add(map);
			}
		}
		return listMap;
	}

	/**
	 * 定时根据卷id查询卷的当前并发读写线程数
	 * 
	 * @param arrayVolumeID
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/queryCurrentThreadNum.do")
	@ResponseBody
	public List<Map<String, Object>> queryCurrentThreadNum(
			@RequestBody String[] arrayVolumeID, HttpSession session)
			throws IOException {
		List<VolumeProperties> listVolumeProperties = new ArrayList<VolumeProperties>();
		for (VolumeProperties volumeProperties : getAllVolumeProperty(session)) {
			for (String volumeID : arrayVolumeID) {
				if (volumeID.equals(volumeProperties.getVolumeID())) {
					listVolumeProperties.add(volumeProperties);
				}
			}
		}
		return queryCurrentThreadNum(listVolumeProperties.toArray(), session);
	}
	

	/**
	 * 查询指定卷的当前线程详细信息
	 * 
	 * @param volumeID
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/showDetailThread.do")
	@ResponseBody
	public Map<String, String> showDetailThread(@RequestBody String volumeID, HttpSession session) throws IOException {
		Setup setup = new Setup(WebUtils.getCrrentDataCenterKey(session));
		HandleProgress[] hp = setup.getThreadInfo(volumeID);
		
		/*// write
		if ("write".equals(threadType))
			hp = setup.getWriteThreadInfo(volumeID);
		// read
		else
			hp = setup.getReadThreadInfo(volumeID);*/
		
		StringBuilder sb = new StringBuilder();
		for (HandleProgress handleProgress : hp) {
			String hpString = handleProgress.toString();
			if (hpString != null && hpString.length() > 0)
				sb.append(hpString).append("<br>");
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("threadInfo", sb.toString());
		return map;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/deleteVolume.do")
	@ResponseBody
	public void deleteVolume(@RequestBody String volumeID, HttpSession session)
			throws IOException {
		new Setup(WebUtils.getCrrentDataCenterKey(session)).getDataSource()
				.getResourcesAccessor().deleteVolumeProperties(volumeID);
	}

}
