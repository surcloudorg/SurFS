package com.autumn.core.soap;

import com.autumn.core.ClassManager;
import com.autumn.core.cfg.Config;
import com.autumn.core.cfg.ConfigListener;
import com.autumn.core.cfg.ConfigParser;
import com.autumn.core.jms.Buffer;
import com.autumn.core.jms.QueueInstance;
import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.JdbcUtils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * <p>Title: SOAP框架</p>
 *
 * <p>Description: soap上下文参数</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class SoapContext {

    private int id = 0;
    private String title = "";
    private String servicename = "";
    private String implClass = "";
    private String className = "";
    private int authtype = 0;
    private String style = "rpc";
    private String useType = "literal";
    private String ipList = "";
    private String infilter = "";
    private String outfilter = "";
    private String aegis = "";
    private String logname =  LogFactory.SYSTEM_LOGNAME;
    private SoapFilter infilerobj = null;
    private SoapFilter outfilerobj = null;
    private final ConfigParser params = new ConfigParser();//服务运行时配置（string，Config、properties格式）
    private Object soapInstance = null;//实例

    /**
     * 是否需要重新加载
     *
     * @return boolean
     */
    public boolean classNeedReload() {
        try {
            if (soapInstance == null) {
                return false;
            }
            Class cls = ClassManager.loadclass(soapInstance.getClass().getName());
            return cls != soapInstance.getClass();
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
     * @throws java.io.IOException
     */
    public void saveConfig() throws SQLException, IOException {
        Connection con = ConnectionFactory.getConnect(SoapContext.class);
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
     * @param con
     * @throws SQLException
     * @throws java.io.IOException
     */
    public void saveConfig(Connection con) throws SQLException, IOException {
        params.update();
        String param = params.getString();
        if (param != null) {
            SoapInitializer.updateConfig(con, id, param);
        }
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
     * @param params the params to set
     */
    public synchronized void setParams(String params) {
        this.params.parse(params);
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
        this.title = title == null ? "" : title.trim();
    }

    /**
     * @return the servicename
     */
    public String getServicename() {
        return servicename;
    }

    /**
     * @param servicename the servicename to set
     */
    public void setServicename(String servicename) {
        this.servicename = servicename == null ? "" : servicename.trim().toLowerCase();
    }

    /**
     * @return the implClass
     * @throws java.lang.ClassNotFoundException
     */
    public Class getImplClass() throws ClassNotFoundException {
        if (implClass == null || implClass.isEmpty()) {
            return null;
        } else {
            return ClassManager.loadclass(implClass);
        }
    }

    /**
     * @param implClass the implClass to set
     */
    public void setImplClass(String implClass) {
        this.implClass = implClass == null ? "" : implClass.trim();
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className == null ? "" : className.trim();
    }

    /**
     * @return the authtype
     */
    public int getAuthtype() {
        return authtype;
    }

    /**
     * @param authtype the authtype to set
     */
    public void setAuthtype(int authtype) {
        this.authtype = authtype;
    }

    /**
     * @return the style
     */
    public String getStyle() {
        return style;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(String style) {
        this.style = style == null ? "" : style.trim();
    }

    /**
     * @return the useType
     */
    public String getUseType() {
        return useType;
    }

    /**
     * @param useType the useType to set
     */
    public void setUseType(String useType) {
        this.useType = useType == null ? "" : useType.trim();
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
        this.ipList = ipList == null ? "" : ipList.trim();
    }

    /**
     * @return the infilter
     */
    public String getInfilter() {
        return infilter;
    }

    /**
     * @param infilter the infilter to set
     */
    public void setInfilter(String infilter) {
        this.infilter = infilter == null ? "" : infilter.trim();
    }

    /**
     * @return the outfilter
     */
    public String getOutfilter() {
        return outfilter;
    }

    /**
     * @param outfilter the outfilter to set
     */
    public void setOutfilter(String outfilter) {
        this.outfilter = outfilter == null ? "" : outfilter.trim();
    }

    /**
     * @return the aegis
     */
    public String getAegis() {
        return aegis;
    }

    /**
     * @param aegis the aegis to set
     */
    public void setAegis(String aegis) {
        this.aegis = aegis == null ? "" : aegis.trim();
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
        this.logname = logname == null ? "" : logname.trim();
    }

    /**
     * @return the infilerobj
     */
    public SoapFilter getInfilerobj() {
        return infilerobj;
    }

    /**
     * @param infilerobj the infilerobj to set
     */
    public void setInfilerobj(SoapFilter infilerobj) {
        this.infilerobj = infilerobj;
    }

    /**
     * @return the outfilerobj
     */
    public SoapFilter getOutfilerobj() {
        return outfilerobj;
    }

    /**
     * @param outfilerobj the outfilerobj to set
     */
    public void setOutfilerobj(SoapFilter outfilerobj) {
        this.outfilerobj = outfilerobj;
    }

    /**
     * @return the service
     */
    public Object getSoapInstance() {
        return soapInstance;
    }

    /**
     * 销毁服务
     */
    protected void destroy() {
        if (soapInstance != null) {
            if (soapInstance instanceof SoapInstance) {
                ((SoapInstance) soapInstance).contextDestroyed();
                this.getLogger().warn("执行{0}.contextDestroyed完毕！", new Object[]{soapInstance.getClass().getName()}, SoapContext.class);
            }
        }
    }

    /**
     * 获取磁盘缓存队列，如果是QueueInstance
     *
     * @return Buffer
     */
    public Buffer getBuffer() {
        if (soapInstance != null && soapInstance instanceof QueueInstance) {
            QueueInstance qi = (QueueInstance) soapInstance;
            return qi.getBuffer();
        } else {
            return null;
        }
    }

    /**
     * @param service the service to set
     */
    protected void setSoapInstance(Object service) {
        if (service instanceof ConfigListener) {
            if (this.getConfig() != null) {
                this.getConfig().setListener((ConfigListener) service);
            }
        }
        this.soapInstance = service;
    }
}
