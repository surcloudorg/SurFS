/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.sql;

import java.sql.Connection;
import java.sql.SQLException;
 
public class SortedSQLException extends SQLException {

    private static final long serialVersionUID = 20120701000100L;
    public static final int DeadlockLoserException = 1;
    public static final int CannotAcquireLockException = 2;
    public static final int BadSqlGrammerException = 3;
    public static final int DuplicateKeyException = 4;
    public static final int DataIntegrityViolationException = 5;
    public static final int DataAccessResourceFailure = 6;
    public static final int ObjectExistsException = 7;
    public static final int ObjectNotExistsException = 8;
    public static final int UncategorizedException = -1;
    private String productName = null;
    protected int exceptionType = UncategorizedException;

    /**
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
     *
     * @param se
     */
    protected SortedSQLException(SQLException se) {
        super(se.getMessage(), se.getSQLState(), se.getErrorCode());
    }

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
