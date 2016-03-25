package com.autumn.core.sql;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;

/**
 * <p>Title:数据库操作错误码对照表</p>
 *
 * <p>Description: 对mysql,mssql,oracle错误进行分类</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public abstract class ErrorCodeSupport {

    private static final HashMap<Integer, Integer[]> mysql_code_map = new HashMap<Integer, Integer[]>();
    private static final HashMap<Integer, Integer[]> mssql_code_map = new HashMap<Integer, Integer[]>();
    private static final HashMap<Integer, Integer[]> oracle_code_map = new HashMap<Integer, Integer[]>();
    private static final HashMap<Integer, String[]> derby_code_map = new HashMap<Integer, String[]>();

    static {
        mysql_code_map.put(SortedSQLException.BadSqlGrammerException, new Integer[]{1054, 1064});
        mysql_code_map.put(SortedSQLException.CannotAcquireLockException, new Integer[]{1205});
        mysql_code_map.put(SortedSQLException.DataAccessResourceFailure, new Integer[]{1});
        mysql_code_map.put(SortedSQLException.DataIntegrityViolationException, new Integer[]{630, 839, 840, 893, 1169, 1215, 1216, 1217, 1451, 1452, 1557});
        mysql_code_map.put(SortedSQLException.DeadlockLoserException, new Integer[]{1213});
        mysql_code_map.put(SortedSQLException.DuplicateKeyException, new Integer[]{1062,1169});
        mysql_code_map.put(SortedSQLException.ObjectExistsException, new Integer[]{1050,1304});
        mysql_code_map.put(SortedSQLException.ObjectNotExistsException, new Integer[]{1146});
        
        mssql_code_map.put(SortedSQLException.BadSqlGrammerException, new Integer[]{156, 170, 207, 208});
        mssql_code_map.put(SortedSQLException.CannotAcquireLockException, new Integer[]{1222});
        mssql_code_map.put(SortedSQLException.DataAccessResourceFailure, new Integer[]{4060});
        mssql_code_map.put(SortedSQLException.DataIntegrityViolationException, new Integer[]{544, 8114, 8115});
        mssql_code_map.put(SortedSQLException.DeadlockLoserException, new Integer[]{1205});
        mssql_code_map.put(SortedSQLException.DuplicateKeyException, new Integer[]{2601, 2627});
        mssql_code_map.put(SortedSQLException.ObjectExistsException, new Integer[]{2714});
        mssql_code_map.put(SortedSQLException.ObjectNotExistsException, new Integer[]{2706});
        

        oracle_code_map.put(SortedSQLException.BadSqlGrammerException, new Integer[]{900, 903, 904, 917, 936, 942, 17006});
        oracle_code_map.put(SortedSQLException.CannotAcquireLockException, new Integer[]{54});
        oracle_code_map.put(SortedSQLException.DataAccessResourceFailure, new Integer[]{17002, 17447});
        oracle_code_map.put(SortedSQLException.DataIntegrityViolationException, new Integer[]{1400, 1722, 2291, 2292});
        oracle_code_map.put(SortedSQLException.DeadlockLoserException, new Integer[]{60});
        oracle_code_map.put(SortedSQLException.DuplicateKeyException, new Integer[]{1});
        oracle_code_map.put(SortedSQLException.ObjectExistsException, new Integer[]{955});
        oracle_code_map.put(SortedSQLException.ObjectNotExistsException, new Integer[]{942});

        derby_code_map.put(SortedSQLException.BadSqlGrammerException, new String[]{"42802", "42818", "42821", "42X01", "42X02", "42X03", "42X04", "42X05", "42X06", "42X07", "42X08"});
        derby_code_map.put(SortedSQLException.CannotAcquireLockException, new String[]{"40XL1"});
        derby_code_map.put(SortedSQLException.DataAccessResourceFailure, new String[]{"04501", "08004", "42Y07"});
        derby_code_map.put(SortedSQLException.DataIntegrityViolationException, new String[]{"22001", "22005", "23502", "23503", "23513", "X0Y32"});
        derby_code_map.put(SortedSQLException.DeadlockLoserException, new String[]{"40001"});
        derby_code_map.put(SortedSQLException.DuplicateKeyException, new String[]{"23505"});
        derby_code_map.put(SortedSQLException.ObjectExistsException, new String[]{"X0Y32"});
        derby_code_map.put(SortedSQLException.ObjectNotExistsException, new String[]{"42X05"});
    }

    /**
     * 获取错误类型
     *
     * @param code_map
     * @param code
     * @return int
     */
    private static int getExceptionType(HashMap<Integer, Integer[]> code_map, int code) {
        for(Map.Entry<Integer, Integer[]> entry:code_map.entrySet()){
            Integer[] codes =entry.getValue();
            if (ArrayUtils.contains(codes, code)) {
                return entry.getKey();
            }
        }
        return SortedSQLException.UncategorizedException;
    }

    /**
     * 获取错误类型
     *
     * @param code
     * @return int
     */
    protected static int getDerbyExceptionType(String code) {
        for(Map.Entry<Integer, String[]> entry:derby_code_map.entrySet()){
            String[] codes =entry.getValue();
            if (ArrayUtils.contains(codes, code)) {
                return entry.getKey();
            }
        }  
        return SortedSQLException.UncategorizedException;
    }

    /**
     * 获取错误类型
     *
     * @param code
     * @return int
     */
    protected static int getMysqlExceptionType(int code) {
        return getExceptionType(mysql_code_map, code);
    }

    /**
     * 获取错误类型
     *
     * @param code
     * @return int
     */
    protected static int getMssqlExceptionType(int code) {
        return getExceptionType(mssql_code_map, code);
    }

    /**
     * 获取错误类型
     *
     * @param code
     * @return int
     */
    protected static int getOracleExceptionType(int code) {
        return getExceptionType(oracle_code_map, code);
    }
}
