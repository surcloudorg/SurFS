/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.web.controller.storage;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LogoutController {
	
	@RequestMapping(method = RequestMethod.POST, value = "/storage/logout.do")
	public ModelAndView logoutStorage(HttpServletRequest request) {
		return logout(request);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/monitor/logout.do")
	public ModelAndView logoutMonitor(HttpServletRequest request) {
		return logout(request);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/block/logout.do")
	public ModelAndView logoutBlock(HttpServletRequest request) {
		return logout(request);
	}
	
	private ModelAndView logout(HttpServletRequest request) {
		request.getSession().invalidate();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/");
		return modelAndView;
	}
		
}
