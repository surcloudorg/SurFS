package com.surfs.storage.web.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContext;

public abstract class WebUtils {
	
	public static String getPropertiesMessage(HttpServletRequest request,
			String messageId, Object[] params) {
		RequestContext requestContext = new RequestContext(request);

		WebApplicationContext webContext = requestContext
				.getWebApplicationContext();
		String message = webContext.getMessage(messageId, params, "",
				requestContext.getLocale());
		return message;
	}
	
	public static String getCrrentDataCenterName(HttpSession session) {
		/*return "uspod1/uscluster2";*/
		Object dataCenterName = session.getAttribute("dataCenterName");
		return dataCenterName != null ? dataCenterName.toString() :null;
	}
	
	public static String getCrrentDataCenterKey(HttpSession session) {
		/*return "uspod1/uscluster2";*/
		Object dataCenterKey = session.getAttribute("dataCenterKey");
		return dataCenterKey != null ? dataCenterKey.toString() :null;
	}

}
