package com.autumn.core.web;

import com.autumn.util.TextUtils;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title: WEB框架</p>
 *
 * <p>Description: web目录认证操作过滤器</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class WebDirectoryFilter {

    private WebDirectory webDirectory = null;
    private AuthenticateFilter authenticateFilter = null;

    public WebDirectoryFilter(WebDirectory wd) {
        this.webDirectory = wd;
        this.authenticateFilter = new AuthenticateFilter(wd);
    }

    /**
     * 处理请求
     *
     * @param httpRequest
     * @param httpResponse
     * @param filterChain
     * @param actionid
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void doService(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
            FilterChain filterChain, String actionid)
            throws IOException, ServletException {
        httpRequest.setCharacterEncoding(webDirectory.getCharset());
        if (webDirectory.getLogintype() < 1) { //被禁用
            DispatchFilter.gotoError(httpRequest, httpResponse, "目录被禁用:" + webDirectory.getDirName());
            return;
        }
        String cip = LoginIpcheck.getAddr(httpRequest);
        if (!TextUtils.checkIpRange(webDirectory.getIpList(), cip)) {
            DispatchFilter.gotoError(httpRequest, httpResponse, "无效ip:" + cip + "," + actionid);
            return;
        }
        if (!authenticateFilter.doFilter(httpRequest, httpResponse)) {
            return;
        }
        doCustomFilter(httpRequest, httpResponse);
        if (httpResponse.isCommitted()) {//已提交
            return;
        }
        if (!doSourceFilter(httpRequest, httpResponse, filterChain)) {
            return;
        }
        ActionMap am = webDirectory.getAction(actionid);
        if (am != null) {
            DispatchFilter.noCache(httpResponse);
            DispatchFilter.actionServlet.doService(httpRequest, httpResponse, webDirectory, am);
        } else {
            this.doCompiledJspFilter(httpRequest, httpResponse, filterChain, actionid);
        }
    }

    /**
     * 执行本地jsp文件
     *
     * @param request
     * @param response
     * @param filterChain
     * @param actionid
     * @return boolean
     * @throws ServletException
     * @throws IOException
     */
    private void doCompiledJspFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String actionid) throws ServletException, IOException {
        if (actionid.endsWith(".jsp")) {
            String sevlet = actionid;
            int index = actionid.lastIndexOf("/");
            if (index >= 0) {
                sevlet = actionid.substring(index + 1);
            }
            CompiledJspFilter jsm = CompiledJspFilter.getMap(sevlet);
            if (jsm != null) {
                DispatchFilter.noCache(response);
                jsm.getJspbase()._jspService(request, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 执行本地jsp,Source文件
     *
     * @param request
     * @param response
     * @param filterChain
     * @param actionid
     * @return boolean
     * @throws ServletException
     * @throws IOException
     */
    private boolean doSourceFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getSession(true).getServletContext().getRealPath(request.getRequestURI());
        if (CompiledJspFilter.findSource(path)) {
            DispatchFilter.noCache(response);
            filterChain.doFilter(request, response);
            return false;
        }
        return true;
    }

    /**
     * 执行目录自定义过滤器
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    private void doCustomFilter(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        Filter myFilter = webDirectory.getFilter();
        if (myFilter == null) {
            return;
        }
        try {
            WebFactory.setWebService(WebFactory.getWebService(webDirectory.getDirName()));
            myFilter.doFilter(request, response, new CustomFilterChain());
        } catch (ServletException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            WebFactory.removeWebService();
        }
    }

    /**
     * 最后一个过滤器，什么也不做
     */
    private static class CustomFilterChain implements FilterChain {

        @Override
        public void doFilter(ServletRequest sr, ServletResponse sr1) throws IOException, ServletException {
        }
    }
}
