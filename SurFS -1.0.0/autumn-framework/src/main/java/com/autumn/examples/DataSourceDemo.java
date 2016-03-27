/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.examples;

import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.ConnectionParam;
import java.sql.Connection;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;

/**
 * Title: 数据库连接池使用演示
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class DataSourceDemo {

    public static void main(String[] args) throws Exception {
        String datasourcename = "mypool";//连接池的名字，在JVM中应唯一
        //设置连接池参数
        ConnectionParam param = new ConnectionParam(datasourcename);
        param.setDriver("com.mysql.jdbc.Driver");
        param.setUrl("jdbc:mysql://10.0.0.9:3306/publicbase?characterEncoding=utf8");
        param.setUser("surdoc");
        param.setPassword("surdoc_23");
        param.setTestsql("select 1");//测试物理连接是否断开的指令
        param.setMaxConnection(10);//最大允许10个物理连接
        //其他参数默认即可

        //创建连接池
        try {
            ConnectionFactory.rebind(param);
        } catch (Exception r) {
            //一般是因为连接参数如url,访问账户设置错误
            r.printStackTrace();
            return;
        }

        //通过连接池名，获取数据源DataSource实例
        try {
            DataSource dataSource = ConnectionFactory.lookup(datasourcename);
        } catch (NameNotFoundException r) {
            //一般是因为输入了一个不存在的连接池名
        }

        //获取物理连接
        //Connection con=dataSource.getConnection();//除了通过数据源DataSource实例获取连接
        //还可以通过下面方法取得连接
        Connection con = ConnectionFactory.getConnect(datasourcename, DataSourceDemo.class);
        try {
            String dbname = con.getMetaData().getDatabaseProductName();
            System.out.println(dbname);
        } finally {
            con.close(); //切记关闭con
        }

        //可以通过上述方式创建多个数据库连接池，用datasourcename区分就可以
        //可以注销连接池
        ConnectionFactory.unbind(datasourcename);
    }
}
