package com.surfs.nas.mysql;

import com.autumn.core.sql.*;
import com.surfs.nas.NasMeta;
import com.surfs.nas.NasMetaAccessor;
import com.surfs.nas.UserAccount;
import com.surfs.nas.client.SurFile;
import static com.surfs.nas.client.SurFile.SurFileSeparator;
import com.surfs.nas.client.SurFileFactory;
import com.surfs.nas.error.FileExistException;
import com.surfs.nas.error.NosqlException;
import com.surfs.nas.error.VolumeNotFoundException;
import static com.surfs.nas.mysql.MysqlDataSource.FUNC_FINDDIRID;
import static com.surfs.nas.mysql.MysqlDataSource.FUNC_MKDIR;
import static com.surfs.nas.mysql.MysqlDataSource.FUNC_QUERY;
import static com.surfs.nas.mysql.MysqlDataSource.FUNC_RMDIR;
import static com.surfs.nas.mysql.MysqlDataSource.TABLE_DIRS;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import javax.sql.DataSource;
 

public class MysqlNasMetaAccessor extends JdbcTemplate implements NasMetaAccessor {

    private MysqlDataSource mysqlDataSource = null;

    protected MysqlNasMetaAccessor(MysqlDataSource mysqlDataSource, DataSource datasource) {
        super(datasource);
        this.mysqlDataSource = mysqlDataSource;
    }

    /**
     *
     * @param path 
     * @return long[] 
     * @throws IOException
     */
    @Override
    public long[] getDirectoryID(String path) throws IOException {
        //LogFactory.info("getDirectoryID:" + path, MysqlNasMetaAccessor.class);
        if (path.equals(SurFileSeparator)) {
            return null;
        }
        if (path.startsWith(SurFileSeparator)) {
            path = path.substring(1);
        }
        if (!path.endsWith(SurFileSeparator)) {
            path = path + "/";
        }
        path = path.replace(SurFileSeparator.charAt(0), '/');
        try {
            this.addParameter(path);
            String sql = "call " + FUNC_FINDDIRID + "(?)";
            ResultSet rs = this.query(sql);
            rs.next();
            String dirs = rs.getString("_dirs");
            if (dirs == null) {
                return null;
            } else {
                String[] ss = dirs.split("\\/");
                long[] ids = new long[ss.length];
                for (int ii = 0; ii < ids.length; ii++) {
                    ids[ii] = Long.parseLong(ss[ii]);
                }
                return ids;
            }
        } catch (Exception ex) {
            throw new NosqlException(ex);
        }
    }

    /**
     *
     * @param nasfile
     * @return int
     * @throws IOException
     */
    @Override
    public int getNasFile(SurFile nasfile) throws IOException {
        try {
            String sql = "call " + FUNC_QUERY + "(?,?)";
            String name = nasfile.getName();
            this.addParameter(name, nasfile.getParentFile().getId());
            ResultSet rs = this.query(sql);
            rs.next();
            int rectype = rs.getInt("restype");
            if (rectype == 1) {
                NasMeta nm = new NasMeta();
                nm.setParentId(nasfile.getParentFile().getId());
                nm.setFileId(rs.getLong("@_id"));
                nm.setFileName(name);
                nm.setRandomName(rs.getString("@_randomname"));
                nm.setVolumeId(rs.getString("@_volumeid"));
                nm.setLength(rs.getLong("@_length"));
                nm.setLastModified(rs.getLong("@_lastmodified"));
                nasfile.setId(nm.getFileId());
                nasfile.setMeta(nm);
            } else if (rectype == 2) {
                nasfile.setId(rs.getLong("@_id"));
            } else if (rectype == 0) {
                nasfile.setId(-1);
                nasfile.setMeta(null);
            } else {
                throw new NosqlException("数据库操作失败.");
            }
            return rectype;
        } catch (Exception sex) {
            throw new NosqlException(sex);
        }
    }

    /**
     *
     * @param nasfile
     * @return　long 
     * @throws java.io.IOException
     */
    @Override
    public int mkDirectory(SurFile nasfile) throws IOException {
        //LogFactory.info("mkDirectory:" + nasfile.getPath(), MysqlNasMetaAccessor.class);
        String sql = "call " + FUNC_MKDIR + "(?,?)";
        String name = nasfile.getName();
        try {
            this.addParameter(name, nasfile.getParentFile().getId());
            ResultSet rs = this.query(sql);
            rs.next();
            int rectype = rs.getInt("restype");
            if (rectype == 1) {
                NasMeta nm = new NasMeta();
                nm.setParentId(nasfile.getParentFile().getId());
                nm.setFileId(rs.getLong("@_id"));
                nm.setFileName(name);
                nm.setRandomName(rs.getString("@_randomname"));
                nm.setVolumeId(rs.getString("@_volumeid"));
                nm.setLength(rs.getLong("@_length"));
                nm.setLastModified(rs.getLong("@_lastmodified"));
                nasfile.setId(nm.getFileId());
                nasfile.setMeta(nm);
            } else if (rectype == 2) {
                nasfile.setId(rs.getLong("@_id"));
            } else if (rectype == 0) {
                nasfile.setId(rs.getLong("@_id"));
            } else if (rectype == 3) { 
                nasfile.getParentFile().setId(-1);
            } else if (rectype == 4) {    
                throw new NosqlException("");
            } else if (rectype == 5) {    
                throw new NosqlException("");
            }
            return rectype;
        } catch (SQLException ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public int rmDirectory(SurFile nasfile) throws IOException {
        //LogFactory.info("rmDirectory:" + nasfile.getPath(), MysqlNasMetaAccessor.class);
        String sql = "call " + FUNC_RMDIR + "(?)";
        try {
            this.addParameter(nasfile.getId());
            ResultSet rs = this.query(sql);
            rs.next();
            int rectype = rs.getInt("restype");
            if (rectype == 1) {
            } else if (rectype == 2) {
                //nasfile.setId(rs.getLong("_id"));
            } else if (rectype == 0) {//OK
                nasfile.setId(-1);
            } else if (rectype == 4) {    
                throw new NosqlException("");
            } else if (rectype == 5) {  
                throw new NosqlException("");
            }
            return rectype;
        } catch (SQLException ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public void mvDirectory(SurFile src, SurFile dst) throws IOException {
        //LogFactory.info("mvDirectory:" + src.getPath(), MysqlNasMetaAccessor.class);
        String sql = "update " + TABLE_DIRS + " set parentid=?, dirname=? where dirid=?";
        try {
            this.addParameter(dst.getParentFile().getId(), dst.getName(), src.getId());
            this.update(sql);
            dst.setId(src.getId());
            src.setId(-1);
        } catch (SortedSQLException ex) {
            if (SortedSQLException.DuplicateKeyException == ex.getExceptionType()) {
                throw new FileExistException("");
            } else {
                throw new NosqlException(ex);
            }
        }
    }

    @Override
    public void updateDirectory(long dirid) throws IOException {
        //LogFactory.info("updateDirectory:" + dirid, MysqlNasMetaAccessor.class);
        String sql = "update Directorys set length=(select COALESCE(SUM(length),0) from " + dirid + "_Files), lastmodified=? where dirid=?";
        try {
            this.addParameter(System.currentTimeMillis(), dirid);
            this.update(sql);
        } catch (SortedSQLException se) {
            if (se.getExceptionType() != SortedSQLException.ObjectNotExistsException) {
                throw new NosqlException(se);
            }
        }
    }

    @Override
    public void deleteNasMeta(NasMeta metadata) throws IOException {
        String sql = "delete from " + metadata.getParentId() + "_Files where fileid=?";
        try {
            this.addParameter(metadata.getFileId());
            this.update(sql);
        } catch (SortedSQLException ex) {
            if (SortedSQLException.ObjectNotExistsException != ex.getExceptionType()) {
                throw new NosqlException(ex);
            }
        }
    }

    @Override
    public void storeNasMeta(NasMeta metadata) throws IOException {
        //LogFactory.info("storeNasMeta:" + metadata.getFileName(), MysqlNasMetaAccessor.class);
        long id = 0;
        String sql = "insert into " + metadata.getParentId()
                + "_Files(filename,volumeid,randomname,length,lastmodified) values(?,?,?,?,?)";
        try {
            this.addParameter(metadata.getFileName(), metadata.getVolumeId(),
                    metadata.getRandomName(), metadata.getLength(), metadata.getLastModified());
            id = this.insert(sql);
            if (id > 0) {
                metadata.setFileId(id);
            }
        } catch (SortedSQLException ex) {
            if (SortedSQLException.DuplicateKeyException != ex.getExceptionType()) {
                throw new NosqlException(ex);
            }
        }
        if (id == 0) {
            sql = "select fileid,volumeid,randomname,length,lastmodified from " + metadata.getParentId() + "_Files where filename=?";
            try {
                this.addParameter(metadata.getFileName());
                ResultSet rs = this.query(sql);
                rs.next();
                metadata.setFileId(rs.getLong("fileid"));
                metadata.setRandomName(rs.getString("randomname"));
                metadata.setVolumeId(rs.getString("volumeid"));
                metadata.setLength(rs.getLong("length"));
                metadata.setLastModified(rs.getLong("lastmodified"));
            } catch (SQLException ex) {
                throw new NosqlException(ex);
            }
        }
    }

    @Override
    public void updateNasMeta(NasMeta metadata, boolean length) throws IOException {
        String sql = length
                ? "update " + metadata.getParentId() + "_Files set length=?,lastmodified=? where fileid=?"
                : "update " + metadata.getParentId() + "_Files set volumeid=?,randomname=? where fileid=?";
        try {
            if (length) {
                this.addParameter(metadata.getLength(), metadata.getLastModified(), metadata.getFileId());
            } else {
                this.addParameter(metadata.getVolumeId(), metadata.getRandomName(), metadata.getFileId());
            }
            update(sql);
        } catch (SortedSQLException se) {
            if (se.getExceptionType() != SortedSQLException.ObjectNotExistsException) {
                throw new NosqlException(se);
            }
        }
    }

    @Override
    public NasMeta queryNasMeta(long pid, long fid) throws IOException {
        String sql = "select * from " + pid + "_Files where fileid=?";
        try {
            this.addParameter(fid);
            ResultSet rs = this.query(sql);
            if (rs.next()) {
                NasMeta nm = new NasMeta();
                nm.setParentId(pid);
                nm.setFileId(fid);
                nm.setFileName(rs.getString("filename"));
                nm.setRandomName(rs.getString("randomname"));
                nm.setVolumeId(rs.getString("volumeid"));
                nm.setLength(rs.getLong("length"));
                nm.setLastModified(rs.getLong("lastmodified"));
                return nm;
            }
            return null;
        } catch (SQLException ex) {
            if (ex instanceof SortedSQLException
                    && SortedSQLException.ObjectNotExistsException == ((SortedSQLException) ex).getExceptionType()) {
                return null;
            } else {
                throw new NosqlException(ex);
            }
        }
    }

    @Override
    public long totalDirectory(long dirid) throws IOException {
        //LogFactory.info("totalDirectory:" + dirid, MysqlNasMetaAccessor.class);
        Map<Long, Long> idmap = new HashMap<>();
        Map<Long, Long> idlength = new HashMap<>();
        String sql = "select dirid,parentid,length from Directorys where dirid=? or parentid>0";
        try {
            this.addParameter(dirid);
            ResultSet rs = this.query(sql);
            while (rs.next()) {
                long _dirid = rs.getLong("dirid");
                idmap.put(_dirid, rs.getLong("parentid"));
                idlength.put(_dirid, rs.getLong("length"));
            }
        } catch (SQLException ex) {
            throw new NosqlException(ex);
        }
        long total = 0;
        
        List<Long> ids = new ArrayList<>();
        if (!idmap.containsKey(dirid)) {
            return 0;
        }
        ids.add(dirid);
        idmap.remove(dirid);
        boolean exists = false;
        while (true) {
            Map<Long, Long> tmpmap = new HashMap<>(idmap);
            Set<Entry<Long, Long>> set = tmpmap.entrySet();
            for (Entry<Long, Long> ent : set) {
                if (ids.contains(ent.getValue())) {
                    ids.add(ent.getKey());
                    idmap.remove(ent.getKey());
                    exists = true;
                }
            }
            if (!exists) {
                break;
            } else {
                exists = false;
            }
        }
        
        for (Long id : ids) {
            total = total + idlength.get(id);
        }
        return total;
    }

    @Override
    public List<String> getMountList() throws IOException {
        //LogFactory.info("getMountList:", MysqlNasMetaAccessor.class);
        String sql = "select path from Mount";
        try {
            List<String> ls = new ArrayList<>();
            ResultSet rs = this.query(sql);
            while (rs.next()) {
                String p = rs.getString("path");
                ls.add(p);
            }
            return ls;
        } catch (SQLException ex) {
            throw new NosqlException(ex);
        }
    }

    /**
     *
     * @param path
     * @return long
     * @throws IOException
     */
    @Override
    public long getQuata(String path) throws IOException {
        //LogFactory.info("getQuata:" + path, MysqlNasMetaAccessor.class);
        String sql = "select quota from Mount where path=?";
        try {
            path = path.endsWith(SurFile.SurFileSeparator) ? path.substring(0, path.length() - 1) : path;
            this.addParameter(path);
            ResultSet rs = this.query(sql);
            if (!rs.next()) {
                throw new VolumeNotFoundException("");
            } else {
                return rs.getLong("quota");
            }
        } catch (SQLException ex) {
            throw new NosqlException(ex);
        } catch (VolumeNotFoundException ie) {
            throw ie;
        }
    }

    @Override
    public UserAccount getUserAccount(String username) throws IOException {
        //LogFactory.info("getUserAccount:" + username, MysqlNasMetaAccessor.class);
        String sql = "select pwd,realName,comment from Users where usersName=?";
        try {
            this.addParameter(username);
            ResultSet rs = this.query(sql);
            if (!rs.next()) {
                throw new IOException("");
            } else {
                UserAccount ua = new UserAccount();
                ua.setUserName(username);
                ua.setPassword(rs.getString("pwd"));
                ua.setRealName(rs.getString("realName"));
                ua.setComment(rs.getString("comment"));
                return ua;
            }
        } catch (SQLException ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public Map<String, String> getPermission(String sharename) throws IOException {
        //LogFactory.info("getPermission:" + sharename, MysqlNasMetaAccessor.class);
        Map<String, String> map = new HashMap<>();
        String sql = "select Users.usersName, UsersMount.permission from UsersMount "
                + "inner join Mount on UsersMount.mountId=Mount.mountId "
                + "inner join Users on UsersMount.usersId=Users.usersId "
                + "where Mount.path=?";
        try {
            this.addParameter(sharename);
            ResultSet rs = this.query(sql);
            while (rs.next()) {
                String name = rs.getString("usersName");
                String permission = rs.getString("permission");
                map.put(name, permission);
            }
            return map;
        } catch (SQLException ex) {
            throw new NosqlException(ex);
        }
    }

    public static final int defaultTop = 50000;
    
    @Override
    public SurFile[] listNasMeta(SurFile nasFile, int top) throws IOException {  
        top = top <= 0 ? defaultTop : top;
        List<SurFile> ls = new ArrayList<>();
        String sql = "select dirid,dirname from " + TABLE_DIRS + " where parentid=? limit 0," + String.valueOf(top);
        try {
            this.addParameter(nasFile.getId());
            ResultSet rs = this.query(sql);
            while (rs.next()) {
                ls.add(new SurFile(nasFile, rs.getString("dirname"), rs.getLong("dirid")));
            }
        } catch (SQLException ex) {
            throw new NosqlException(ex);
        }
        top = top - ls.size();
        if (top > 0) {
            sql = "select fileid,filename,volumeid,randomname,length,lastmodified from "
                    + nasFile.getId()
                    + "_Files limit 0," + String.valueOf(top);
            try {
                ResultSet rs = this.query(sql);
                while (rs.next()) {
                    NasMeta metadata = new NasMeta();
                    metadata.setParentId(nasFile.getId());
                    metadata.setFileId(rs.getLong("fileid"));
                    metadata.setFileName(rs.getString("filename"));
                    metadata.setRandomName(rs.getString("randomname"));
                    metadata.setVolumeId(rs.getString("volumeid"));
                    metadata.setLength(rs.getLong("length"));
                    metadata.setLastModified(rs.getLong("lastmodified"));
                    SurFile sf = new SurFile(nasFile, metadata);
                    sf = SurFileFactory.putInstance(sf);
                    ls.add(sf);
                }
            } catch (com.autumn.core.sql.SortedSQLException se) {
                if (se.getExceptionType() != SortedSQLException.ObjectNotExistsException) {
                    throw new NosqlException(se);
                }
            } catch (SQLException ex) {
                throw new NosqlException(ex);
            }
        }
        SurFile[] nfs = new SurFile[ls.size()];
        return ls.toArray(nfs);
    }

    @Override
    public void mvNasMeta(SurFile src, SurFile dst) throws IOException {
        if (src.getParentFile().getId() == dst.getParentFile().getId()) {
            String sql = "update " + dst.getParentFile().getId() + "_Files set filename=? where fileid=?";
            try {
                this.addParameter(dst.getName(), src.getId());
                this.update(sql);
                dst.setId(src.getId());
                NasMeta nm = src.getMeta();
                nm.setFileName(dst.getName());
                dst.setMeta(nm);
                src.setId(-1);
                src.setMeta(null);
            } catch (SortedSQLException ex) {
                if (SortedSQLException.DuplicateKeyException == ex.getExceptionType()) {
                    throw new FileExistException("存在同名文件");
                } else {
                    throw new NosqlException(ex);
                }
            }
        } else {
            long id = -1;
            Connection con = mysqlDataSource.getConnection();
            try {
                String sql = "insert into " + dst.getParentFile().getId() + "_Files(filename,volumeid,randomname,length,lastmodified)values(?,?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, dst.getName());
                ps.setString(2, src.getMeta().getVolumeId());
                ps.setString(3, src.getMeta().getRandomName());
                ps.setLong(4, src.getMeta().getLength());
                ps.setLong(5, src.getMeta().getLastModified());
                int rc = ps.executeUpdate();
                if (rc < 1) {
                    throw new FileExistException("");
                } else {
                    ResultSet rs = ps.getGeneratedKeys();
                    rs.next();
                    id = rs.getLong(1);
                    sql = "delete from " + src.getParentFile().getId() + "_Files where fileid=?";
                    ps = con.prepareStatement(sql);
                    ps.setLong(1, src.getId());
                    ps.executeUpdate();
                }
                dst.setId(id);
                NasMeta nm = src.getMeta();
                nm.setFileId(id);
                nm.setFileName(dst.getName());
                nm.setParentId(dst.getParentFile().getId());
                dst.setMeta(nm);
                src.setId(-1);
                src.setMeta(null);
            } catch (SQLException ex) {
                if (ex instanceof SortedSQLException
                        && SortedSQLException.DuplicateKeyException == ((SortedSQLException) ex).getExceptionType()) {
                    throw new FileExistException("");
                } else {
                    throw new NosqlException(ex);
                }
            } finally {
                JdbcUtils.closeConnect(con);
            }
        }
    }
}
