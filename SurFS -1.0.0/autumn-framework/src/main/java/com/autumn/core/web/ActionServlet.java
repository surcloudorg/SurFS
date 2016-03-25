package com.autumn.core.web;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * <p>Title: WEB框架-servlet</p>
 *
 * <p>Description: 处理.do的web请求</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
@SuppressWarnings("unchecked")
public class ActionServlet {

    static final String defauMethodName = "execute";

    /**
     * 加载action，并填充参数值
     *
     * @param request
     * @param action
     * @return Action
     * @throws Exception
     */
    private Action loadClass(HttpServletRequest request, ActionMap action) throws Exception {
        Class cl = action.getActionClass();
        if (cl.isAnnotationPresent(NonForm.class)) {//不需注入
            return (Action) cl.newInstance();
        }
        RequestFill rfill = new RequestFill(request, cl);
        rfill.fill(action);
        return (Action) rfill.getObject();
    }

    /**
     * 执行函数
     *
     * @param myaction
     * @param methodname
     * @return Forward
     */
    private Forward execute(ActionMap action, Action myaction) throws Exception {
        Method method = action.getMethod();
        if (method.getName().equals(defauMethodName)) {
            return myaction.execute();
        }
        Object obj = method.invoke(myaction, new Object[]{});
        if (obj == null) {
            return null;
        }
        if (obj instanceof Forward) {
            return (Forward) obj;
        } else {
            String ctype = action.getContentType();
            return new ObjectForward(obj, ctype);
        }
    }

    /**
     * 处理请求
     *
     * @param request
     * @param response
     * @param cfg
     * @param actionMap
     * @throws ServletException
     * @throws IOException
     */
    protected void doService(HttpServletRequest request, HttpServletResponse response, WebDirectory cfg, ActionMap actionMap) throws ServletException, IOException {
        Logger log = cfg != null ? cfg.getLogger() : LogFactory.getLogger(LogFactory.SYSTEM_LOGNAME);
        try {
            ActionContext actioncontext = new ActionContext(request, response, cfg, actionMap);
            ActionContext.setActionContext(actioncontext);
            if (cfg != null) {
                WebFactory.setWebService(WebFactory.getWebService(cfg.getDirName()));
            }
            setPermission(request, cfg, actioncontext, actionMap);
            if (actioncontext.getPermission() == 0) { //禁止访问
                HttpSession mySession = request.getSession(true);
                mySession.setAttribute("ERROR_MSG", "没有权限访问[" + request.getRequestURI() + "]");
                response.sendRedirect((request).getContextPath() + "/error.jsp");
            } else {
                HttpServletRequest newrequest = null;
                try {
                    if (ServletFileUpload.isMultipartContent(request)) {
                        if (actionMap.isMultipartRequest()) {
                            newrequest = new MultipartRequestWrapper(request);
                        } else {
                            newrequest = new QueryRequestWrapper(request);
                        }
                    } else {
                        newrequest = new QueryRequestWrapper(request);
                    }
                    actioncontext.request = newrequest;
                    Action myaction = loadClass(newrequest, actionMap);
                    Forward myforward = execute(actionMap, myaction);
                    if (myforward != null) {
                        myforward.doForward(myaction);
                    }
                } catch (Throwable r) {
                    throw r;
                } finally {
                    actioncontext.closeConnect();
                    if (newrequest != null && newrequest instanceof MultipartRequestWrapper) {
                        ((MultipartRequestWrapper) newrequest).finish();
                    }
                }
            }
        } catch (Throwable s) {
            log.trace("请求[" + request.getRequestURI() + "]失败!", s, ActionServlet.class);
            try {
                HttpSession mySession = request.getSession(true);
                mySession.setAttribute("ERROR_MSG", "请求[" + request.getRequestURI() + "]失败:" + s.getMessage());
                response.sendRedirect((request).getContextPath() + "/error.jsp");
            } catch (Exception d) {
            }
        } finally {
            ActionContext.removeActionContext();
            WebFactory.removeWebService();
        }
    }

    /**
     * 设置请求权限
     *
     * @param request
     * @param cfg
     * @param actioncontext
     * @param action
     */
    private void setPermission(HttpServletRequest request, WebDirectory cfg, ActionContext actioncontext, ActionMap action) {
        LoginUser loginuser = LoginUser.getLoginUser(request);
        if (loginuser != null) {
            if (cfg == null) {
                actioncontext.setPermission(1);
            } else {
                if (cfg.getLogintype() == 2 || cfg.getLogintype() == 3) {
                    actioncontext.setPermission(1);
                } else {
                    String Permstr = loginuser.getPermission();
                    int ii = action.getPermissionorder();
                    if (ii < 0) {
                        actioncontext.setPermission(1);
                    } else {
                        try {
                            actioncontext.setPermission(Integer.parseInt(Permstr.charAt(ii) + ""));
                        } catch (Exception e) {
                            actioncontext.setPermission(0);
                        }
                    }
                }
            }
        } else {
            actioncontext.setPermission(cfg == null ? 1 : (cfg.getLogintype() == 2 ? 1 : 0));
        }
    }
}
