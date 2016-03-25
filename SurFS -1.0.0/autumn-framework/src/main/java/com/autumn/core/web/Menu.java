package com.autumn.core.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>Title: WEB框架</p>
 *
 * <p>Description: 菜单生成</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class Menu {

    /**
     * 获取权限
     *
     * @param loginuser
     * @param actionId
     * @return int
     */
    public static int getActionPermission(LoginUser loginuser, String actionId) {
        if (loginuser == null) {
            return 0;
        }
        WebDirectory wc = WebFactory.getWebDirectory(loginuser.getDirid().intValue());
        ActionMap action = wc.getAction(actionId.toLowerCase());
        return getActionPermission(loginuser, action);
    }

    /**
     * 获取权限
     *
     * @param loginuser
     * @param action
     * @return int
     */
    public static int getActionPermission(LoginUser loginuser, ActionMap action) {
        if (loginuser == null) {
            return 0;
        }
        String Permstr = loginuser.getPermission();
        if (action == null) {
            return 0;
        } else {
            int ii = action.getPermissionorder();
            if (ii < 0) {
                return 1;
            }
            try {
                return Integer.parseInt(Permstr.charAt(ii) + "");
            } catch (Exception e) {
                return 0;
            }
        }
    }
    private LoginUser loginuser = null;
    private List<ActionMap> actions = new ArrayList<ActionMap>(); //所有显示菜单的action
    private int dirId = 0;
    private String fullMenu = "";
    private String menu = "";
    private String showMenu = "";
    private String action = "";

    /**
     * 获取菜单
     *
     * @return List<Menu>
     */
    public List<Menu> getSubMenu() {
        int count = getActions().size();
        List<String> menus = new ArrayList<String>();
        for (int ii = 0; ii < count; ii++) {
            ActionMap action1 = getActions().get(ii);
            String configmenu = action1.getMenu();
            if (getFullMenu().equals("")) { //一级菜单
                if (configmenu.indexOf(".") < 0) {
                    String s = configmenu + ";" + configmenu + ";"
                            + action1.getActionid();
                    if (!menus.contains(s)) {
                        menus.add(s);
                    }
                }
            } else {
                if (configmenu.startsWith(getMenu())) {
                    String smenu = configmenu.replaceFirst(getMenu(), "");
                    String s = smenu + ";" + configmenu + ";"
                            + action1.getActionid();
                    if (!menus.contains(s)) {
                        menus.add(s);
                    }
                }
            }
        }
        try { //排序
            Collections.sort(menus);
        } catch (Exception e) {
        }
        List<Menu> menuss = new ArrayList<Menu>();
        for (Object str : menus) {
            String[] strs = str.toString().split(";");
            Menu m = new Menu();
            m.setActions(this.getActions());
            m.setDirId(this.dirId);
            m.setLoginuser(this.loginuser);
            m.setFullMenu(strs[1]);
            m.setAction(strs[2]);
            String s = strs[0];
            String[] sss = s.split("\\.");
            String showMenu1 = sss[0];
            int index = showMenu1.indexOf(")");
            if (index >= 0) {
                showMenu1 = showMenu1.substring(index + 1);
            }
            m.setShowMenu(showMenu1);
            String menu1 = "";
            for (String str1 : sss) {
                index = str1.indexOf(")");
                if (index >= 0) {
                    str1 = str1.substring(index + 1);
                }
                menu1 = menu1 + str1 + ".";
            }
            m.setMenu(menu1);
            menuss.add(m);
        }
        return menuss;
    }

    public Menu() {
    }

    public Menu(Action act) {
        if (action == null) {
            return;
        }
        this.loginuser = act.getLoginUser();
        if (this.loginuser == null) {
            dirId = act.getActionMap().getDirid();
            unloginMenu();
        } else {
            loginMenu();
        }
        init(act);
    }

    public Menu(int dirId) {
        this.dirId = dirId;
        unloginMenu();
    }

    public Menu(LoginUser loginuser) {
        this.loginuser = loginuser;
        loginMenu();
    }

    /**
     * 初始化
     *
     * @param act
     */
    private void init(Action act) {
        String ss = act.getActionMap().getMenu();
        if (ss == null || ss.equalsIgnoreCase("NA") || ss.trim().equals("")) {
            return;
        }
        String[] sss = ss.split("\\.");
        setFullMenu(ss.trim());
        setAction(act.getActionMap().getActionid());
        setShowMenu(sss[sss.length - 1]);
        int index = getShowMenu().indexOf(")");
        if (index >= 0) {
            setShowMenu(getShowMenu().substring(index + 1));
        }
        for (String str : sss) {
            index = str.indexOf(")");
            if (index >= 0) {
                str = str.substring(index + 1);
            }
            setMenu(getMenu() + str + ".");
        }
    }

    /**
     * 获取菜单
     */
    private void unloginMenu() {
        WebDirectory wc = WebFactory.getWebDirectory(dirId);
        if (wc == null) {
            return;
        }
        Collection<ActionMap> en = wc.getActionsMap().values();
        for (ActionMap action1 : en) {
            String m = action1.getMenu();
            if (!(m == null || m.equalsIgnoreCase("NA") || m.trim().equals(""))) {
                getActions().add(action1);
            }
        }
    }

    /**
     * 获取菜单，仅包含有权限访问的action
     */
    private void loginMenu() {
        if (loginuser == null) {
            return;
        }
        WebDirectory wc = WebFactory.getWebDirectory(loginuser.getDirid().intValue());
        if (wc == null) {
            return;
        }
        Collection<ActionMap> en = wc.getActionsMap().values();
        for (ActionMap action1 : en) {
            String m = action1.getMenu();
            if (!(m == null || m.equalsIgnoreCase("NA") || m.trim().equals(""))) {
                if (getActionPermission(loginuser, action1) > 0) {
                    getActions().add(action1);
                }
            }
        }
    }

    /**
     * 设置actions
     *
     * @param actions
     */
    public void setActions(List<ActionMap> actions) {
        this.actions = actions;
    }

    /**
     * 设置登录帐号
     *
     * @param loginuser
     */
    public void setLoginuser(LoginUser loginuser) {
        this.loginuser = loginuser;
    }

    /**
     * @return the fullMenu
     */
    public String getFullMenu() {
        return fullMenu;
    }

    /**
     * @param fullMenu the fullMenu to set
     */
    public void setFullMenu(String fullMenu) {
        this.fullMenu = fullMenu;
    }

    /**
     * @return the showMenu
     */
    public String getShowMenu() {
        return showMenu;
    }

    /**
     * @param showMenu the showMenu to set
     */
    public void setShowMenu(String showMenu) {
        this.showMenu = showMenu;
    }

    /**
     * @return the menu
     */
    public String getMenu() {
        return menu;
    }

    /**
     * @param menu the menu to set
     */
    public void setMenu(String menu) {
        this.menu = menu;
    }

    /**
     * @param dirId the dirId to set
     */
    public void setDirId(int dirId) {
        this.dirId = dirId;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the actions
     */
    public List<ActionMap> getActions() {
        return actions;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }
}
