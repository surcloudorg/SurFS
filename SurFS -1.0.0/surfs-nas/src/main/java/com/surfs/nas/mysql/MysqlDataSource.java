/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.mysql;

 
import com.surfs.nas.sql.SortedSQLException;
import com.surfs.nas.sql.ScriptExecuter;
import com.surfs.nas.sql.ConnectionParam;
import com.surfs.nas.sql.SmartDataSource;
import com.surfs.nas.sql.JdbcUtils;
import com.surfs.nas.sql.ConnectionFactory;
 
import com.surfs.nas.NasMetaAccessor;
import com.surfs.nas.NosqlDataSource;
import com.surfs.nas.ResourcesAccessor;
import static com.surfs.nas.ResourcesAccessor.TABLE_NODE;
import static com.surfs.nas.ResourcesAccessor.TABLE_SERVICE;
import static com.surfs.nas.ResourcesAccessor.TABLE_VOLUME;
import com.surfs.nas.error.NosqlException;
import com.surfs.nas.util.Config;
import com.surfs.nas.util.Function;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;


public class MysqlDataSource implements NosqlDataSource {

    static final String TABLE_DIRS = "Directorys";
    static final String FUNC_FINDDIRID = "FindDir";
    static final String FUNC_MKDIR = "MkDir";
    static final String FUNC_RMDIR = "RmDir";
    static final String FUNC_QUERY = "QueryFile";

    private DataSource source;

    /**
     *
     * @param cfg
     * @throws IOException
     */
    public MysqlDataSource(Config cfg) throws IOException {
        init(cfg);
    }

    /**
     *
     * @param cfg
     * @throws NosqlException
     */
    private void init(Config cfg) throws NosqlException {
        final String JDBCNAME = "temp";
        try {
            ConnectionParam params = new ConnectionParam(JDBCNAME);
            params.setDriver("com.mysql.jdbc.Driver");
            params.setUrl(cfg.getAttributeValue("datasource.serverurl"));
            params.setUser(cfg.getAttributeValue("datasource.user"));
            params.setPassword(cfg.getAttributeValue("datasource.password"));
            params.setMaxConnection(cfg.getAttributeIntValue("datasource.maxConnections", 100));
            params.setTestsql("select 1");
            source = ConnectionFactory.bind(params);
        } catch (Exception r) {
            try {
                ConnectionFactory.unbind(JDBCNAME);
            } catch (NameNotFoundException ex) {
            }
            throw new NosqlException(r);
        }
        Connection con = getConnection();
        try {
            checkTableExists(TABLE_VOLUME);
            checkTableExists(TABLE_NODE);
            checkTableExists(TABLE_SERVICE);
            checkTableExists(TABLE_DIRS);
            checkTableExists("Mount");
            checkTableExists("Users");
            checkTableExists("UsersMount");
            checkTableExists(FUNC_FINDDIRID);
            checkTableExists(FUNC_MKDIR);
            checkTableExists(FUNC_RMDIR);
            checkTableExists(FUNC_QUERY);
        } catch (NosqlException r) {
            throw r;
        } catch (Exception r) {
            throw new NosqlException(r);
        } finally {
            JdbcUtils.closeConnect(con);
        }
    }

    /**
     *
     * @param tabname 
     * @return String 
     * @throws IOException
     */
    private void checkTableExists(String tabname) throws IOException {
        Connection con = getConnection();
        try {
            InputStream is = MysqlDataSource.class.getResourceAsStream(tabname + ".sql");
            try {
                String text = new String(Function.read(is), "utf-8");
                text = text.replaceFirst("\\?", tabname);
                ScriptExecuter.doScript(con, text);
            } catch (SortedSQLException ex) {
                if (SortedSQLException.ObjectExistsException != ex.getExceptionType()) {
                    throw new NosqlException(ex);
                }
            }
        } finally {
            JdbcUtils.closeConnect(con);
        }
    }

    /**
     *
     * @return Connection
     * @throws NosqlException
     */
    public Connection getConnection() throws NosqlException {
        try {
            return source.getConnection();
        } catch (SQLException ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public ResourcesAccessor getResourcesAccessor() throws NosqlException {
        return new MysqlResourcesAccessor(source);
    }

    @Override
    public void shutdown() {
        ((SmartDataSource) source).close();
    }

    @Override
    public NasMetaAccessor getNasMetaAccessor() throws IOException {
        return new MysqlNasMetaAccessor(this, source);
    }

}
