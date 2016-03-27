/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.web.controller.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.autumn.core.log.LogFactory;
import com.surfs.storage.monitor.model.Disk;
import com.surfs.storage.monitor.service.DiskService;

@Controller
@RequestMapping("/monitor")
public class DiskController {
	
	@Autowired
	private DiskService diskService;
	
	@RequestMapping(value = "/showDisk.do")
	public ModelAndView showDiskInfos() {
		LogFactory.info("call showDiskInfos!");
		
		Map<String, Integer> diskCount =  new HashMap<>();
		
		Map<String, Map<String, Map<String, Disk>>> mapDisks = diskService.getDiskInfos();
		
		for (Entry<String, Map<String, Map<String, Disk>>> mapmapmap : mapDisks.entrySet()) {
			Map<String, Map<String, Disk>> mapmap = mapmapmap.getValue();
			for (Entry<String, Map<String, Disk>> map : mapmap.entrySet()) {
				Map<String, Disk> m = map.getValue();
				for (Entry<String, Disk> md : m.entrySet()) {
					Disk disk = md.getValue();
					if (diskCount.containsKey(disk.getStatus())) {
						Integer num = diskCount.get(disk.getStatus());
						diskCount.put(disk.getStatus(), num.intValue() + 1);
					} else {
						diskCount.put(disk.getStatus(), 1);
					}
				}
			}
		}
		
		if (!diskCount.containsKey("FREE"))
			diskCount.put("FREE", 0);
		if (!diskCount.containsKey("USED"))
			diskCount.put("USED", 0);
		if (!diskCount.containsKey("FAIL"))
			diskCount.put("FAIL", 0);
			
		
		ModelAndView mav = new ModelAndView("monitor/disk", "mapDisks", diskService.getDiskInfos());
		mav.addObject("diskCount", diskCount);
		
		return mav;
	}
	
	@RequestMapping(value = "/showDiskLogDetail.do")
	@ResponseBody
	public Map<String, String> showDiskLogDetail(@RequestBody String date) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("logInfo", diskService.readLocationLog(date));
		return map;
	}
	
	@RequestMapping(value = "/showDiskLog.do")
	public ModelAndView showDiskLog() {
		return new ModelAndView("monitor/log");
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/getDiskInfo.do")
	@ResponseBody
	public Map<String, Map<String, Map<String, Disk>>> getDiskInfos() {
		return diskService.getDiskInfos();
	}
	
	@RequestMapping(value = "/showPoolUsedDisks.do")
	public ModelAndView showPoolUsedDisks() {
		Map<String, Map<String, List<Disk>>> mmList = diskService.getPoolUsedDisks();
		return new ModelAndView("monitor/udisks", "mmList", mmList);
	}
	
}
