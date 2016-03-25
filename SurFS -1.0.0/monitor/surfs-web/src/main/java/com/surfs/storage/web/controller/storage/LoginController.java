package com.surfs.storage.web.controller.storage;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.surfs.storage.web.dto.User;
import com.surfs.storage.web.utils.WebUtils;

@Controller
@RequestMapping("/storage")
public class LoginController {

	@RequestMapping(method = RequestMethod.POST, value = "/login.do")
	public ModelAndView login(HttpServletRequest request, User user) {
		String pwd = WebUtils.getPropertiesMessage(request, user.getUserName(),
				null);
		if (StringUtils.isNotBlank(pwd) && pwd.equals(user.getPassWord())) {
			//request.getSession().setAttribute("user", user);
			//request.getSession().setAttribute("dataCenter", user.getDataCenter());
			return new ModelAndView(
					"redirect:/storage/showGlobleProperties.do");
		}

		return new ModelAndView("redirect:/home.jsp?status=login_error");
	}

	@RequestMapping(method = RequestMethod.GET, value = "/test.do")
	@ResponseBody
	public void test(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setHeader("Content-Type", "text/event-stream");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println("data:" + new Date().toString());
			response.getWriter().println();
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
