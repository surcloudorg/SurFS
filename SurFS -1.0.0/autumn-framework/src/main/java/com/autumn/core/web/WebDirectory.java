package com.autumn.core.web;

import com.autumn.core.ClassManager;
import com.autumn.core.autopage.AutoAction;
import com.autumn.core.cfg.Config;
import com.autumn.core.cfg.ConfigListener;
import com.autumn.core.cfg.ConfigParser;
import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.JdbcUtils;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title: WEB框架</p>
 *
 * <p>Description: WEB目录服务配置</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class WebDirectory {

    private int id = 0; //目录ID
    private String title = ""; //目录标识
    private String dirName = ""; //目录名，如：/console,强制小写
    private String defaultPage = ""; //首页
    private String ipList = null; //需要验证ip，null不验证
    private int logintype = 1; //需要验证账号0禁止访问，1需要登录，2不需登录，3公共目录,4需要basic认证
    private String logname =  LogFactory.SYSTEM_LOGNAME;
    private String classname = "";
    private String charset = null;//request编码
    private ConfigParser params = new ConfigParser();//服务运行时配置（string，Config、properties格式）
    private ConcurrentHashMap<String, ActionMap> actionsMap = new ConcurrentHashMap<String, ActionMap>(); //action映射表
    private Object webInstance = null;

    /**
     * 是否需要重新加载
     *
     * @return boolean
     */
    public boolean classNeedReload() {
        try {
            if (webInstance == null) {
                return false;
            }
            Class cls = ClassManager.loadclass(webInstance.getClass().getName());
            return cls != webInstance.getClass();
        } catch (ClassNotFoundException ex) {
            return true;
        }
    }

    /**
     * 获取日志输出
     *
     * @return Logger
     */
    public Logger getLogger() {
        return LogFactory.getLogger(logname);
    }

    /**
     * 保存params
     *
     * @throws SQLException
     */
    public void saveConfig() throws SQLException, IOException {
        Connection con = ConnectionFactory.getConnect(WebDirectory.class);
        try {
            saveConfig(con);
        } catch (SQLException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        } finally {
            JdbcUtils.closeConnect(con);
        }
    }

    /**
     * 保存params
     *
     * @throws SQLException
     */
    public void saveConfig(Connection con) throws SQLException, IOException {
        params.update();
        String param = params.getString();
        if (param != null) {
            WebInitializer.updateConfig(con, id, param);
        }
    }

    /**
     * 添加ActionsMap
     *
     * @param map
     */
    public void addAction(ActionMap map) {
        if (map.classname.equals(AutoAction.class.getName())) {
            ActionMap am = map.clone();
            am.setFunctionName("insert");
            actionsMap.put(map.getActionid() + ".insert", am);
            ActionMap amm = map.clone();
            amm.setFunctionName("update");
            actionsMap.put(map.getActionid() + ".update", amm);
        }
        actionsMap.put(map.getActionid(), map);
    }

    /**
     * 搜索Action
     *
     * @param key
     * @return ActionMap
     */
    public ActionMap getAction(String key) {
        if(actionsMap.isEmpty()){
            return null;
        }
        ActionMap am = actionsMap.get(key);
        if (am != null) {
            return am;
        }
        Set<Entry<String, ActionMap>> set = actionsMap.entrySet();
        for (Entry<String, ActionMap> en : set) {
            ActionMap amap = en.getValue();
            if (amap.isRegex() && key.matches(en.getKey())) {
                return amap;
            }
        }
        return null;
    }

    /**
     * 获取ActionsMap
     *
     * @return HashMap<String, ActionMap>
     */
    public HashMap<String, ActionMap> getActionsMap() {
        return new HashMap<String, ActionMap>(actionsMap);
    }

    /**
     * 创建
     *
     * @param id
     * @param dirName
     */
    public WebDirectory(int id, String dirName) {
        this.id = id;
        setDirName(dirName);
    }

    /**
     * 获取配置监听器
     *
     * @return ConfigListener
     */
    public ConfigListener getListener() {
        if (getWebInstance() != null) {
            if (getWebInstance() instanceof ConfigListener) {
                return (ConfigListener) getWebInstance();
            }
        }
        return null;
    }

    /**
     * 注销web目录服务
     */
    public void destory() {
        if (getWebInstance() != null) {
            if (getWebInstance() instanceof Filter) {
                ((Filter) getWebInstance()).destroy();
                this.getLogger().warn("执行{0}.contextDestroyed完毕！", new Object[]{getWebInstance().getClass().getName()}, WebDirectory.class);
            }
        }
    }

    /**
     * 获取web过滤器
     *
     * @return Filter
     */
    public Filter getFilter() {
        if (getWebInstance() != null) {
            if (getWebInstance() instanceof Filter) {
                return (Filter) getWebInstance();
            }
        }
        return null;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the dirName
     */
    public String getDirName() {
        return dirName;
    }

    /**
     * @param dirName the dirName to set
     */
    public final void setDirName(String dirName) {
        this.dirName = dirName == null ? "" : dirName.trim().toLowerCase();
    }

    /**
     * @return the defaultPage
     */
    public String getDefaultPage() {
        return defaultPage;
    }

    /**
     * @param defaultPage the defaultPage to set
     */
    public void setDefaultPage(String defaultPage) {
        this.defaultPage = defaultPage;
    }

    /**
     * @return the ipList
     */
    public String getIpList() {
        return ipList;
    }

    /**
     * @param ipList the ipList to set
     */
    public void setIpList(String ipList) {
        this.ipList = ipList;
    }

    /**
     * @return the logintype
     */
    public int getLogintype() {
        return logintype;
    }

    /**
     * @param logintype the logintype to set
     */
    public void setLogintype(int logintype) {
        this.logintype = logintype;
    }

    /**
     * @param params the params to set
     */
    public synchronized void setParams(String params) {
        this.params.parse(params);
    }

    /**
     * 获取配置
     *
     * @return Properties
     */
    public Properties getProperties() {
        return params.getProperties();
    }

    /**
     * 获取配置
     *
     * @return Config
     */
    public Config getConfig() {
        return params.getConfig();
    }

    /**
     * @return the params
     */
    public String getParams() {
        return params.getString();
    }

    /**
     * 获取数据库连接
     *
     * @return Connection
     */
    public Connection getConnect() {
        if (getConfig() != null) {
            String jdbc = getConfig().getAttributeValue("config.datasource");
            if (jdbc != null) {
                return ConnectionFactory.getConnect(jdbc, this.getClass());
            }
        }
        if (getProperties() != null) {
            String jdbc = getProperties().getProperty("datasource");
            if (jdbc != null) {
                return ConnectionFactory.getConnect(jdbc, this.getClass());
            }
        }
        return null;
    }

    /**
     * @return the logname
     */
    public String getLogname() {
        return logname;
    }

    /**
     * @param logname the logname to set
     */
    public void setLogname(String logname) {
        this.logname = logname;
    }

    /**
     * @return the classname
     */
    public String getClassname() {
        return classname;
    }

    /**
     * @param classname the classname to set
     */
    public void setClassname(String classname) {
        this.classname = classname;
    }

    /**
     * @return the charset
     */
    public String getCharset() {
        if (charset == null) {
            return DispatchFilter.encoding;
        }
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    public void setCharset(String charset) {
        if (!(charset == null || charset.trim().isEmpty())) {
            if (Charset.isSupported(charset.trim())) {
                this.charset = charset.trim();
            }
        }
    }

    /**
     * @param webInstance the webInstance to set
     */
    protected void setWebInstance(Object webInstance) {
        if (webInstance instanceof ConfigListener) {
            if (this.getConfig() != null) {
                this.getConfig().setListener((ConfigListener) webInstance);
            }
        }
        this.webInstance = webInstance;
    }

    /**
     * @return the webInstance
     */
    public Object getWebInstance() {
        return webInstance;
    }
    WebDirectoryFilter filter = new WebDirectoryFilter(this);

    /**
     * 处理请求
     *
     * @param request
     * @param response
     * @param filterChain
     * @param actionid
     */
    public void doService(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String actionid) throws IOException, ServletException {
        filter.doService(request, response, filterChain, actionid);
    }
}
