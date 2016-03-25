package com.autumn.core.web;

import com.autumn.core.log.LogFactory;
import com.autumn.util.IOUtils;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.jasper.runtime.HttpJspBase;

/**
 * <p>
 * Title: WEB框架-过滤器</p>
 *
 * <p>
 * Description: web目录跳转，登录权限等功能</p>
 *
 * <p>
 * Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>
 * Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class DispatchFilter implements Filter {

    private static final long serialVersionUID = 20120711100000L;
    protected static ActionServlet actionServlet = new ActionServlet();
    protected static String encoding = "ISO-8859-1";//字符集编码
    protected static Pattern exclude_Pattern = null;
    private final CompressionFilter compressionFilter = new CompressionFilter();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        compressionFilter.init(filterConfig);
        encoding = filterConfig.getInitParameter("encoding");
        if (encoding == null || encoding.trim().equals("")) {
            encoding = "ISO-8859-1";
        }
        String excludes = filterConfig.getInitParameter("excludes");
        if (excludes != null && (!excludes.trim().isEmpty())) {
            try {
                exclude_Pattern = Pattern.compile(excludes);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            String uriStr = req.getRequestURI().toLowerCase();
            while (uriStr.contains("//")) {
                uriStr = uriStr.replaceAll("//", "/");
            }
            HttpServletRequest httpRequest = compressionFilter.getHttpServletRequest(request);
            HttpServletResponse httpResponse = compressionFilter.getHttpServletResponse(request, response);
            uriStr = uriStr.substring(httpRequest.getContextPath().length() + 1);
            if (exclude_Pattern != null) {
                if (exclude_Pattern.matcher(uriStr).find()) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }           
            if (uriStr.equalsIgnoreCase("favicon.ico")) {
                URL url = DispatchFilter.class.getResource("/resources/autumn.jpg");
                IOUtils.copy(url.openStream(), httpResponse.getOutputStream());
                return;
            }
            if (uriStr.isEmpty()||uriStr.equalsIgnoreCase("/")) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
                return;
            }
            if (uriStr.startsWith("services/") || uriStr.equals("services")) {
                SoapServlet.doService(uriStr, httpRequest, httpResponse, filterChain);
            } else if (uriStr.startsWith("error.jsp")) {
                gotoJSP(httpRequest, httpResponse, filterChain, "error.jsp");
            } else if (uriStr.startsWith("login.jsp")) {
                gotoJSP(httpRequest, httpResponse, filterChain, "login.jsp");
                httpRequest.getSession(true).invalidate();
            } else if (uriStr.startsWith("login.do")) {
                request.setCharacterEncoding(encoding);
                ActionMap action = new ActionMap("login.do");
                action.setClassname(LoginValidate.class.getName());
                actionServlet.doService(httpRequest, httpResponse, null, action);
            } else if (uriStr.startsWith("js/") || uriStr.startsWith("img/")|| uriStr.startsWith("css/")) {
                boolean b = SourceServlet.write(httpRequest, httpResponse, uriStr);
                if (!b) {
                    filterChain.doFilter(request, response);
                }
            } else {
                int index = uriStr.indexOf("/");
                String dirName = index > 0 ? uriStr.substring(0, index) : uriStr;//获取目录名
                WebDirectory cfg = WebFactory.getWebDirectory(dirName);
                if (cfg == null) { //找不到目录
                    cfg = WebFactory.getWebDirectory("root");
                } else {
                    uriStr = uriStr.substring(index + 1);
                }
                if (cfg == null) {
                    if ("".equals(uriStr)) {
                        gotoJSP(httpRequest, httpResponse, filterChain, "login.jsp");
                    } else {
                        gotoError(httpRequest, httpResponse, "访问的页面不存在:" + uriStr);
                    }
                } else {
                    cfg.doService(httpRequest, httpResponse, filterChain, uriStr);
                }
            }
            if (httpResponse instanceof CompressionResponse) {
                CompressionResponse responsewrap = (CompressionResponse) httpResponse;
                responsewrap.flushBuffer();
            }
        } catch (IOException | ServletException e) {
            LogFactory.trace("请求[" + ((HttpServletRequest) request).getRequestURI()
                    + "]发生未知错误!" ,e, DispatchFilter.class);
        }
    }

    /**
     * 跳转到登录页
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    private void gotoJSP(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String jsppage) throws ServletException, IOException {
        HttpSession hs = request.getSession(true);
        String ss = hs.getServletContext().getRealPath(jsppage);
        if (CompiledJspFilter.findSource(ss)) {
            filterChain.doFilter(request, response);
        } else {
            CompiledJspFilter jm = CompiledJspFilter.getMap(jsppage);
            if (jm != null) {//处理jsp
                HttpJspBase base = jm.getJspbase();
                base._jspService(request, response);
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }

    /**
     * 错误重定向
     *
     * @param httpRequest HttpServletRequest
     * @param httpResponse HttpServletResponse
     * @param msg String
     * @throws IOException
     */
    protected static void gotoError(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String msg) throws IOException {
        LogFactory.warn(msg, DispatchFilter.class);
        HttpSession mySession = httpRequest.getSession(true);
        mySession.setAttribute("ERROR_MSG", msg);
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/error.jsp");
    }

    /**
     * 清除缓存
     *
     * @param httpResponse
     * @throws IOException
     */
    protected static void noCache(HttpServletResponse httpResponse) throws IOException {
        httpResponse.setHeader("Cache-Control", "no-cache");
        httpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0
        httpResponse.setDateHeader("Expires", -1);
    }

    @Override
    public void destroy() {
    }
}
