package com.surfs.storage.web.controller.block;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.surfs.storage.block.model.BlockUser;
import com.surfs.storage.block.model.Export;
import com.surfs.storage.block.model.ExportInfo;
import com.surfs.storage.block.service.ExportService;
import com.surfs.storage.web.utils.WebUtils;

@Controller
@RequestMapping("/block")
public class ExportController {
	
	@Autowired
	private ExportService exportService;
	@RequestMapping(method = RequestMethod.POST, value = "/showExport.do")
	public ModelAndView showExport(HttpSession session) throws Exception {
		List<ExportInfo> exportInfos = exportService.getExportInfos();
		for(ExportInfo exportInfo: exportInfos){
			for(Export export: exportInfo.getInfo()){
				String target = export.getTarget();
				List<BlockUser> blockUserList = exportService.queryListBLockUserNames(WebUtils.getCrrentDataCenterKey(session), target);
				export.setBlockUser(blockUserList);
			}
		}
		return new ModelAndView("block/export","exportInfos",exportInfos);
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/addRemoteTarget.do")
	@ResponseBody
	public String addRemoteTarget(@RequestBody Map<String, String> args) {
		return exportService.addRemoteTarget(args);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/delRemoteTarget.do")
	@ResponseBody
	public String delRemoteTarget(@RequestBody Map<String, String> args) {
		return exportService.delRemoteTarget(args);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/addRemoteDevice.do")
	@ResponseBody
	public String addRemoteDevice(@RequestBody Map<String, Object> args) {
		return exportService.addRemoteDevice(args);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/delRemoteDevice.do")
	@ResponseBody
	public String delRemoteDevice(@RequestBody Map<String, String> args) {
		return exportService.delRemoteDevice(args);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/getVolDevs.do")
	@ResponseBody
	public String getVolDevs(@RequestBody String ip) {
		return exportService.getRemoteVolDev(ip);
	}
}
