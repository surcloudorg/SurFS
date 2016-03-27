/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.web.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class LoginFilter implements Filter {

	protected static Pattern[] excludes_Pattern = null;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String excludes = filterConfig.getInitParameter("excludes");
		if (StringUtils.isBlank(excludes))
			return;
		String[] excludeArray = excludes.split(",");
		excludes_Pattern = new Pattern[excludeArray.length];
		for (int i = 0; i < excludeArray.length; i++) {
			excludes_Pattern[i] = Pattern.compile(excludeArray[i].trim());
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String uriStr = req.getRequestURI();
		String path = req.getContextPath();
		System.out.println(uriStr);
		if (excludes_Pattern != null) {
			for (Pattern exclude_Pattern : excludes_Pattern) {
				if (exclude_Pattern.matcher(uriStr).find()) {
					chain.doFilter(request, response);
					return;
				}
			}
        }
		
		Object user = req.getSession().getAttribute("user");
		if (user == null) {
			resp.sendRedirect(path + "/home.jsp?status=access_error");
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

}
