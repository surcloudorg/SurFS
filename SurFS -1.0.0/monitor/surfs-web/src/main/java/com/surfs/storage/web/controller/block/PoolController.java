package com.surfs.storage.web.controller.block;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.surfs.storage.block.model.ZpoolInfo;
import com.surfs.storage.block.service.PoolService;

@Controller
@RequestMapping("/block")
public class PoolController {
	
	@Autowired
	private PoolService poolService;
	
	@RequestMapping(value = "/showPool.do")
	public ModelAndView showPool() {
		List<ZpoolInfo> zpoolInfos = poolService.getZpoolInfos();
		return new ModelAndView("block/pool", "zpoolInfos", zpoolInfos);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/deleteRemoteVol.do")
	@ResponseBody
	public String deleteRemoteVol(@RequestBody Map<String, String> args) {
		return poolService.deleteRemoteVol(args);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/addRemoteVol.do")
	@ResponseBody
	public String addRemoteVol(@RequestBody Map<String, String> args) {
		return poolService.addRemoteVol(args);
	}
	
}
