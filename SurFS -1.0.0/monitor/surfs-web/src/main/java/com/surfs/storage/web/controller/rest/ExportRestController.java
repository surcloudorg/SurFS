package com.surfs.storage.web.controller.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.surfs.storage.block.service.BlockUserService;
import com.surfs.storage.block.service.ExportService;
import com.surfs.storage.web.utils.WebUtils;

@Controller
@RequestMapping("/service/block")
public class ExportRestController {

	@Autowired
	private ExportService exportService;
	
	@Autowired
	private BlockUserService userService;

	@RequestMapping(method = RequestMethod.GET, value = "/addRemoteTarget/{target:.*}")
	@ResponseBody
	public String addRemoteTarget(@PathVariable String target)
			throws UnsupportedEncodingException {
		return exportService.addTarget(URLDecoder.decode(target, "UTF-8"));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/delRemoteTarget/{target:.*}")
	@ResponseBody
	public String delRemoteTarget(@PathVariable String target)
			throws UnsupportedEncodingException {
		return exportService.delTarget(URLDecoder.decode(target, "UTF-8"));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/addRemoteDevice")
	@ResponseBody
	public void addRemoteDevice(@RequestBody Map<String, Object> args) throws IOException {
		exportService.addDevice(args);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/delRemoteDevice")
	@ResponseBody
	public String delRemoteDevice(@RequestParam String device,
			@RequestParam String target) throws UnsupportedEncodingException {
		return exportService.delDevice(URLDecoder.decode(device, "UTF-8"),
				URLDecoder.decode(target, "UTF-8"));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/getRemoteVolDevJson")
	@ResponseBody
	public String getRemoteVolDevJson() {
		return exportService.getVolDevJson();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/getRemoteTarget")
	@ResponseBody
	public String getRemoteTarget() {
		return exportService.getExportInfoJson();
	}
	
	@RequestMapping(method=RequestMethod.POST,value="/addRemoteBlockUserTarget")
	@ResponseBody
	public void addRemoteBlockUserTarget(@RequestBody List<Map<String, String>> listMap, HttpSession session) {
		try {
			userService.addBlockUserTarget(WebUtils
					.getCrrentDataCenterKey(session), listMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
