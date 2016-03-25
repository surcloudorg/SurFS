package com.surfs.storage.web.controller.storage;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.surfs.storage.user.model.Users;
import com.surfs.storage.user.service.UsersService;
import com.surfs.storage.web.utils.WebUtils;

@Controller
@RequestMapping("/storage")
public class UsersController {

	@Autowired
	private UsersService usersService;

	@RequestMapping(method = RequestMethod.POST, value = "/showUsers.do")
	public ModelAndView showUsers(HttpSession session) {
		try {
			List<Users> usersList = usersService.queryAllUsers(WebUtils
					.getCrrentDataCenterKey(session));
			return new ModelAndView("users", "usersList", usersList);
		} catch (Exception e) {
			return new ModelAndView("redirect:/login.jsp?error="
					+ e.getMessage());
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/addUsers.do")
	@ResponseBody
	public void addUsers(@RequestBody Users users, HttpSession session) {
		try {
			usersService.addUsers(WebUtils.getCrrentDataCenterKey(session),
					users);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/saveUsers.do")
	@ResponseBody
	public void saveUsers(@RequestBody Users users, HttpSession session) {
		try {
			usersService.modifyUsers(WebUtils.getCrrentDataCenterKey(session),
					users);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/deleteUsers.do")
	@ResponseBody
	public void deleteUsersAndRelation(@RequestBody int usersId, HttpSession session) {
		try {
			usersService.removeUsersAndRelation(WebUtils.getCrrentDataCenterKey(session),
					usersId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/queryListUsersNotMount.do")
	@ResponseBody
	public List<Users> queryListUsersNotMount(@RequestBody int mountId, HttpSession session) {
		try {
			return usersService.queryListUsersNotMount(WebUtils
					.getCrrentDataCenterKey(session), mountId);
		} catch (Exception e) {
			return null;
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/queryListUsersMount.do")
	@ResponseBody
	public List<Users> queryListUsersMount(@RequestBody int mountId, HttpSession session) {
		try {
			return usersService.queryListUsersMount(WebUtils
					.getCrrentDataCenterKey(session), mountId);
		} catch (Exception e) {
			return null;
		}
	}
	
}
