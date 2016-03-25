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
import com.surfs.storage.block.model.BlockUserTarget;
import com.surfs.storage.block.service.BlockUserService;
import com.surfs.storage.web.utils.WebUtils;

@Controller
@RequestMapping("block")
public class BlockUserController {

	@Autowired
	private BlockUserService userService;
	
	@RequestMapping(method = RequestMethod.POST,value="/showBlockUser.do")
	public ModelAndView showBlockUser(HttpSession session){
		try{
			List<BlockUser> blockUserList = userService.queryAllBlockUser(WebUtils.getCrrentDataCenterKey(session));
			return new ModelAndView("block/blockUser","blockUserList",blockUserList);
		}catch(Exception e){
			e.printStackTrace();
			return new ModelAndView("redirect:/login.jsp?error="+ e.getMessage());
		}
	}
	
	@RequestMapping(method=RequestMethod.POST,value="saveBlockUser.do")
	@ResponseBody
	public String saveBlockUser(@RequestBody BlockUser blockUser,HttpSession session){
		try {
			userService.modifyBlockUser(WebUtils.getCrrentDataCenterKey(session), blockUser);
		} catch (Exception e) {
			// TODO Auto-generaFted catch block
			 e.printStackTrace();
			 return "error";
		}
		return null;
	}
	
	@RequestMapping(method=RequestMethod.POST,value="addBlockUser.do")
	@ResponseBody
	public String addBlockUser(@RequestBody BlockUser blockUser,HttpSession session){
		try {
			userService.addBlockUser(WebUtils.getCrrentDataCenterKey(session), blockUser);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
		return null;
	}
	
	@RequestMapping(method=RequestMethod.POST,value="deleteBlockUser.do")
	@ResponseBody
	public boolean deleteBlockUser(@RequestBody int userId,HttpSession session)throws Exception{
		List<BlockUserTarget> blockUserTargetList = userService.queryBlockUser(WebUtils.getCrrentDataCenterKey(session), userId);
		if(blockUserTargetList.isEmpty()){
			userService.removeBlockUser(WebUtils.getCrrentDataCenterKey(session), userId);
			return true;
		}else{
			return false;
		}
	}
	@RequestMapping(method=RequestMethod.POST,value="/addBlockUserTarget.do")
	@ResponseBody
	public void addBlockUserTarget(@RequestBody Map<String, Object> map, HttpSession session){
		try {
			userService.addRemoteBlockUserTarget(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*	@RequestMapping(method=RequestMethod.POST,value="/addBlockUserTarget.do")
	@ResponseBody
	public void addBlockUserTarget(@RequestBody List<Map<String, String>> listMap, HttpSession session){
		try {
			userService.addBlockUserTarget(WebUtils.getCrrentDataCenterKey(session), listMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	@RequestMapping(method=RequestMethod.POST,value="/deleteBlockUserTarget.do")
	@ResponseBody
	public void deleteBlockUserTarget(@RequestBody Map<String,String> args,HttpSession session){
		try {
			userService.removeBlockUserTarget(WebUtils.getCrrentDataCenterKey(session), args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(method=RequestMethod.POST,value="/queryListBlockUserNotTarget.do")
	@ResponseBody
	public List<BlockUser> queryListBlockUserNotTarget(@RequestBody String target,HttpSession session){
		try {
			return userService.queryListBlockUserNotTarget(WebUtils.getCrrentDataCenterKey(session), target);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	@RequestMapping(method=RequestMethod.POST,value="/queryListBlockUserTarget.do")
	@ResponseBody
	public List<BlockUser> queryListBlockUserTarget(@RequestBody String target,HttpSession session){
		try {
			return userService.queryListBlockUserTarget(WebUtils.getCrrentDataCenterKey(session), target);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
