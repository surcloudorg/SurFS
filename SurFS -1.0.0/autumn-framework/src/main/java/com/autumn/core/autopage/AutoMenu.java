package com.autumn.core.autopage;

import com.autumn.core.web.LoginUser;
import com.autumn.core.web.Menu;
import com.autumn.core.web.WebDirectory;
import com.autumn.core.web.WebFactory;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title: AUTOACTION-菜单</p>
 *
 * <p>Description: 生成web目录菜单</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class AutoMenu {

    private HttpServletRequest request = null;
    private HttpServletResponse response = null;
    private String init = "";//输入初始显示的action
    private String title = "";//页面标题
    private List rows = new ArrayList();

    public AutoMenu(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        this.request = httpServletRequest;
        this.response = httpServletResponse;
        execute();
    }

    /**
     * 执行
     */
    private void execute() {
        Menu menu;
        String uri = request.getRequestURI().toLowerCase();
        uri = uri.substring(request.getContextPath().length() + 1); //去掉根目录
        while (uri.startsWith("/")) {
            uri = uri.substring(1);
        }
        String dirName = "";
        int index = uri.indexOf("/");
        if (index > 0) { //获取目录名
            dirName = uri.substring(0, index);
        }
        WebDirectory cfg = WebFactory.getWebDirectory(dirName);
        if (cfg == null) {
            return;
        }
        int type = cfg.getLogintype();
        if (type < 1) {
            return;
        } else if (type == 2) {///无需验证
            menu = new Menu(cfg.getId());
        } else {
            LoginUser loginuser = LoginUser.getLoginUser(request);
            menu = new Menu(loginuser);
        }
        rows = menu.getSubMenu();
        for (Object obj : rows) {
            Menu me = (Menu) obj;
            if (!me.getAction().equals("")) {
                me.setAction(me.getAction());
            }
        }
        setInit(request.getParameter("init"));
        if (getInit() == null) {
            setInit("");
        } else {
            setInit(getInit().trim());
        }
        title = cfg.getTitle();
        if (getInit().equals("")) {
            if (!rows.isEmpty()) {
                init = ((Menu) rows.get(0)).getAction();
                request.setAttribute("smmenu", this);
            }
        } else {
            if (!rows.isEmpty()) {
                String str = request.getContextPath() + "/" + cfg.getDirName() + "/" + ((Menu) rows.get(0)).getAction();
                try {
                    response.sendRedirect(str);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * @return the init
     */
    public String getInit() {
        return init;
    }

    /**
     * @param init the init to set
     */
    public void setInit(String init) {
        this.init = init;
    }

    /**
     * @return the rows
     */
    public List getRows() {
        return rows;
    }

    /**
     * @param rows the rows to set
     */
    public void setRows(List rows) {
        this.rows = rows;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }
}
