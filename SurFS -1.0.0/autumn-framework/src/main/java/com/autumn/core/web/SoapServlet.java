/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.web;

import com.autumn.core.soap.SoapFactory;
import com.autumn.core.soap.SoapFilter;
import com.autumn.core.soap.SoapRequestWrapper;
import com.autumn.core.soap.SoapResponseWrapper;
import com.autumn.core.soap.SoapService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.transport.http.XFireServletController;

public class SoapServlet extends HttpServlet {

    private static XFireServletController controller;

    static{
        controller = new XFireServletController(XFireFactory.newInstance().getXFire(), Initializer.servletContext);
    }

    public static void doService(String uriStr, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain1) throws ServletException, IOException {
        if (uriStr.equalsIgnoreCase("services")) {
            controller.doService(request, response);
            return;
        }
        String queryString = request.getQueryString();
        if (queryString != null && queryString.equalsIgnoreCase("wsdl")) {
            String ss = request.getRequestURI();
            if (ss.contains("//")) {
                response.sendRedirect("/" + uriStr + "?wsdl");
            } else {
                controller.doService(request, response);
            }
            return;
        }
        uriStr = uriStr.substring(9);
        int index = uriStr.indexOf("/");
        if (index > 0) {
            uriStr = uriStr.substring(0, index);
        }
        SoapService soapService = SoapFactory.getSoapService(uriStr);
        if (soapService == null) {
            controller.doService(request, response);
            return;
        }
        try {
            SoapFactory.setSoapService(soapService);
            request.setAttribute(SoapFactory.SoapContextkey, soapService);
            SoapFilter insf = soapService.getSoapContext().getInfilerobj();
            SoapFilter outsf = soapService.getSoapContext().getOutfilerobj();
            if (insf != null) {
                request = new SoapRequestWrapper(insf, request);
            }
            if (outsf != null) {
                response = new SoapResponseWrapper(outsf, response);
            }
            controller.doService(request, response);
            if (response instanceof SoapResponseWrapper) {
                SoapResponseWrapper responsewrap = (SoapResponseWrapper) response;//if (responsewrap.getStatus() == HttpServletResponse.SC_OK) {
                responsewrap.flushBuffer();
            }
        } catch (ServletException e) {
            throw e;
        } finally {
            SoapFactory.removeSoapService();
        }
    }
}
