package com.autumn.core.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>Title:数据库操作失败分析</p>
 *
 * <p>Description: 主要判断不同数据库产品对重复键的错误码和死锁错误码</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class SortedSQLException extends SQLException {

    private static final long serialVersionUID = 20120701000100L;
    public static final int DeadlockLoserException = 1;//当前操作是死锁牺牲者，遭到访问拒绝
    public static final int CannotAcquireLockException = 2;//无法获取锁
    public static final int BadSqlGrammerException = 3;//sql表达式错误
    public static final int DuplicateKeyException = 4;//重复键
    public static final int DataIntegrityViolationException = 5;//当一个更新会破坏数据完整性遭到拒绝
    public static final int DataAccessResourceFailure = 6;//访问资源彻底失败，一般为连接故障
    public static final int ObjectExistsException = 7;//创建重复对象
    public static final int ObjectNotExistsException = 8;//表不存在
    public static final int UncategorizedException = -1;//未分类错误
    private String productName = null;//数据库产品，mysql/Microsoft SQL Server/oracle
    protected int exceptionType = UncategorizedException;

    /**
     * 创建
     *
     * @param con
     * @param se
     */
    protected SortedSQLException(Connection con, Exception se) {
        super(se.getMessage(),
                (se instanceof SQLException) ? ((SQLException) se).getSQLState() : "",
                (se instanceof SQLException) ? ((SQLException) se).getErrorCode() : -1);
        try {
            this.productName = con.getMetaData().getDatabaseProductName();
            check();
        } catch (Exception e) {
            exceptionType = DataAccessResourceFailure;
        }
    }

    /**
     * 创建
     *
     * @param con
     * @param se
     */
    protected SortedSQLException(Connection con, SQLException se) {
        super(se.getMessage(), se.getSQLState(), se.getErrorCode());
        try {
            this.productName = con.getMetaData().getDatabaseProductName();
            check();
        } catch (Exception e) {
            exceptionType = DataAccessResourceFailure;
        }
    }

    /**
     * 创建
     *
     * @param se
     */
    protected SortedSQLException(SQLException se) {
        super(se.getMessage(), se.getSQLState(), se.getErrorCode());
    }

    /**
     * 数据库错误码
     */
    private void check() {
        if (productName.equalsIgnoreCase("mysql")) {
            exceptionType = ErrorCodeSupport.getMysqlExceptionType(this.getErrorCode());
        } else if (productName.equalsIgnoreCase("Microsoft SQL Server")) {
            exceptionType = ErrorCodeSupport.getMssqlExceptionType(this.getErrorCode());
        } else if (productName.equalsIgnoreCase("oracle")) {
            exceptionType = ErrorCodeSupport.getOracleExceptionType(this.getErrorCode());
        } else if (productName.equalsIgnoreCase("apache derby")) {
            exceptionType = ErrorCodeSupport.getDerbyExceptionType(this.getSQLState());
        } else {
            exceptionType = UncategorizedException;
        }
    }

    /**
     * @return the exceptionType
     */
    public int getExceptionType() {
        return exceptionType;
    }
}
