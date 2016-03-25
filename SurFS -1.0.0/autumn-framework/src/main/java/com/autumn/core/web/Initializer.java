package com.autumn.core.web;

import com.autumn.core.*;
import com.autumn.core.log.LogFactory;
import com.autumn.core.log.LogInitializer;
import com.autumn.core.service.ServiceConfig;
import com.autumn.core.service.ServiceFactory;
import com.autumn.core.service.ServiceInitializer;
import com.autumn.core.soap.SoapContext;
import com.autumn.core.soap.SoapFactory;
import com.autumn.core.soap.SoapInitializer;
import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.ConnectionParam;
import com.autumn.core.sql.DataSourceInitializer;
import com.autumn.core.sql.JdbcUtils;
import com.autumn.util.TextUtils;
import java.io.File;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.derby.jdbc.EmbeddedDriver;

/**
 * <p>Title: WEB框架</p>
 *
 * <p>Description: web服务初始化</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class Initializer implements ServletContextListener {

    static ServletContext servletContext = null;
    private static String webpath = null;//web服务目录，目的是指定重载class的路径

    public static String getWebpath() {
        return webpath;
    }

    /**
     * 设置网络超时
     */
    private void setTimeout() {
        long defaultConnectTimeout = TextUtils.getTrueLongValue(System.getProperty("sun.net.client.defaultConnectTimeout"), 30000);
        System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(defaultConnectTimeout));
        long defaultReadTimeout = TextUtils.getTrueLongValue(System.getProperty("sun.net.client.defaultReadTimeout"), 30000);
        System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(defaultReadTimeout));
        System.setProperty("com.autumn.core.starttime", TextUtils.Date2String(new Date()));
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        setTimeout();

        servletContext = sce.getServletContext();

        webpath = servletContext.getRealPath("") + (servletContext.getRealPath("").endsWith(File.separator) ? "" : File.separator);//web服务目录

        //实例名
        WebFactory.instanceName = servletContext.getServletContextName();
        if (WebFactory.instanceName != null && WebFactory.instanceName.equals("")) {
            WebFactory.instanceName = null;
        }

        //创建数据库连接
        String driver = servletContext.getInitParameter("dbdriver");
        String url = servletContext.getInitParameter("dburl");
        String user = servletContext.getInitParameter("dbuser");
        String pwd = servletContext.getInitParameter("dbpwd");
        Connection con;
        try {
            if (driver == null || driver.isEmpty()) {
                driver = EmbeddedDriver.class.getName();
                url =  ConnectionParam.getDerbyUrl("autumn");
                user = "";
                pwd = "";
            }
            con = JdbcUtils.getConnect(driver, url, user, pwd);
            CreateTable ct = new CreateTable(con);
            ct.dotask();//创建表
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //初始化日志配置  
        initLogger(servletContext.getInitParameter("logPath"), con);
        //初始化线程池
        ThreadPools.init();
        //初始化数据库连接池
        DataSourceInitializer.createSystemSource(con, url, driver, user, pwd);
        DataSourceInitializer.initDataSource(con);

        //添加class路径
        String path = webpath + "WEB-INF" + File.separator + "classes";
        ClassManager.addClassPath(path);

        //加载服务        
        initWebs(con);
        
        initSoaps(con);
        
        initServices(con);

        //检测帐号
        LoginUser.checkAdministrator(con);

        //关闭连接
        JdbcUtils.closeConnect(con);

        //加载console.jsp
        CompiledJspFilter.loadMap();

        //解压缩资源文件
        SourceServlet.init();
        //加载系统变量清除线程
        SystemAttributeClear.startClear();
    }

    /**
     * 初始化日志
     *
     * @param con
     */
    private void initLogger(String logpath, Connection con) {
        LogFactory.setLogPath(logpath);
        LogInitializer.createSystemLogger(con);
        LogInitializer.createErrorLogger(con);
        LogInitializer.initLogger(con);
        LogFactory.setLog4jLogger();//将commons-logging-1.1.jar产生的日志重定向到log4j
        LogFactory.setSystemErr();
        LogFactory.configJDKLog();
        LogFactory.configLog4j();
        LogFactory.warn("初始化日志系统完毕！路径：" + (logpath == null ? "" : logpath), Initializer.class);
    }

    /**
     * 初始化web服务
     *
     * @param con
     */
    private void initWebs(Connection con) {
        WebInitializer.createRootDirectory(con);
        WebFactory.initService();
        List<WebDirectory> webs = WebInitializer.initService(con);
        WebFactory.startService(webs);
    }

    /**
     * 初始化服务
     *
     * @param con
     */
    private void initServices(Connection con) {
        List<ServiceConfig> webs = ServiceInitializer.initService(con);
        ServiceFactory.startService(webs);
    }

    /**
     * 初始化soap服务
     *
     * @param con
     */
    private void initSoaps(Connection con) {
        List<SoapContext> webs = SoapInitializer.initService(con);
        SoapFactory.startService(webs);
    }

    //退出
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServiceFactory.stopService();//停止服务
        WebFactory.stopService();//停止web
        SoapFactory.stopService();//停止soap
        ConnectionFactory.stop();//关闭数据库
        ThreadPools.shutdownThreadpools();//关闭线程池
        MultiThreadedHttpConnectionManager.shutdownAll();//关闭网络连接池
        SystemAttributeClear.stopClear();//关闭系统变量清除线程
        JVMController.stop();//关闭监控
    }
}
