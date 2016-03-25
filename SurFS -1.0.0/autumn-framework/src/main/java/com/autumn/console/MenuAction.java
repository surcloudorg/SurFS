package com.autumn.console;

import com.autumn.core.web.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: 根据目录中的控制器（action）生成菜单</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class MenuAction extends Action {

    private String init = "";
    private List rows = new ArrayList();

    @Override
    public Forward execute() {
        Menu menu = new Menu(this);
        rows = menu.getSubMenu();
        for (Object obj : rows) {
            Menu me = (Menu) obj;
            if (!me.getAction().equals("")) {
                me.setAction(me.getAction());
            }
        }
        if (init.equals("")) {
            init = ((Menu) rows.get(0)).getAction();
            this.setAttribute("smmenu", this);
            try {
                String forward = this.getActionMap().getProperties().getProperty("forward");
                if (forward == null || forward.trim().equals("")) {
                    return new ActionForward("menulist.jsp");
                } else {
                    return new ActionForward(forward.trim());
                }
            } catch (Exception r) {
                return new ActionForward("menulist.jsp");
            }
        } else {
            String sendurl = ((Menu) rows.get(0)).getAction();
            return new RedirectForward(sendurl);
        }
    }

    /**
     * 目录表
     *
     * @return List
     */
    public List getRows() {
        return rows;
    }

    /**
     * get初始显示action
     *
     * @return String
     */
    public String getInit() {
        return init;
    }

    /**
     * set初始显示action
     *
     * @param init
     */
    public void setInit(String init) {
        this.init = init;
    }
}
