/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.client;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.surfs.nas.NasMeta;
import com.surfs.nas.NasMetaAccessor;
import com.surfs.nas.StoragePool;
import com.surfs.nas.StorageSources;
import static com.surfs.nas.client.SurFileFactory.expiresIn;
import com.surfs.nas.error.ArgumentException;
import com.surfs.nas.error.FileExistException;
import static com.surfs.nas.mysql.MysqlNasMetaAccessor.defaultTop;
import com.surfs.nas.protocol.UpdateQuotaRequest;
import com.surfs.nas.transport.TcpClient;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class SurFile {

    public static Logger log = LogFactory.getLogger(SurFile.class);
    public static long _globalCreateDate = System.currentTimeMillis();
    public static final String SurFileSeparator = "/";
    private static final String regex = "[^\\s\\\\/:\\*\\?\\\"<>\\|]*(\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|]$";

    /**
     *
     * @param fname
     * @throws ArgumentException
     */
    public static void checkFileName(String fname) throws ArgumentException {
        if (fname == null) {
            throw new ArgumentException("");
        }
        if (fname.length() > 255) {
            throw new ArgumentException("");
        }
        if (!fname.matches(regex)) {
            throw new ArgumentException("");
        }
    }

    /**
     *
     * @param path
     * @return String 
     * @throws com.surfs.nas.error.ArgumentException
     */
    public static String checkPath(String path) throws ArgumentException {
        if (path == null || path.trim().isEmpty()) {
            return SurFileSeparator;
        }
        String str = "";
        StringTokenizer st = new StringTokenizer(path, "/\\");
        while (st.hasMoreTokens()) { 
            String pp = st.nextToken().trim();
            if (!pp.isEmpty()) {
                checkFileName(pp);
                str = str.concat(SurFileSeparator).concat(pp);
            }
        }
        return str.isEmpty() ? SurFileSeparator : str;
    }

    private String path = null;
    private long id = -1; 
    private NasMeta meta = null;
    private SurFile parent;
    private StoragePool zfspool = null;
    private long instanceTime = System.currentTimeMillis();

    public SurFile(SurFile file) throws IOException {
        path = file.path;
        id = file.id;
        meta = file.meta;
        parent = file.parent;
        zfspool = file.zfspool;
    }

    SurFile(String spath) throws IOException {
        this(spath, StorageSources.getDefaultStoragePool());
    }

    SurFile(String spath, String poolname) throws IOException {
        this(spath, StorageSources.getStoragePool(poolname));
    }

    /**
     *
     * @param spath
     * @param zfspool
     * @throws java.io.IOException
     */
    SurFile(String paths, StoragePool zfspool) throws IOException {
        this.zfspool = zfspool;
        this.path = paths == null ? SurFileSeparator : paths;
        if (path.equals(SurFileSeparator)) {
            id = 0;
        } else {
            String parentPath = getParent();
            if (parentPath.equals(SurFileSeparator)) {
                this.parent = new SurFile(null, zfspool);
            } else {
                NasMetaAccessor mda = zfspool.getDatasource().getNasMetaAccessor();
                long[] parentids = mda.getDirectoryID(parentPath);
                int deep = parentPath.split("\\" + SurFileSeparator).length - 2;
                this.parent = new SurFile(parentPath, parentids, deep, zfspool);
            }
            if (this.parent.id >= 0) {
                zfspool.getDatasource().getNasMetaAccessor().getNasFile(this);
            }
        }
    }

    /**
     *
     * @param parent 
     * @param name 
     * @throws IOException
     */
    public SurFile(SurFile parent, String name) throws IOException {
        checkFileName(name);
        if (parent.isFile()) {
            throw new FileExistException("");
        }
        this.zfspool = parent.zfspool;
        this.parent = parent;
        this.path = parent.path + SurFileSeparator + name;
        if (this.parent.id >= 0) {
            zfspool.getDatasource().getNasMetaAccessor().getNasFile(this);
        }
    }

    /**
     *
     * @param parent
     * @param nmeta
     */
    public SurFile(SurFile parent, NasMeta nmeta) {
        this.zfspool = parent.zfspool;
        this.parent = parent;
        this.path = parent.path + SurFileSeparator + nmeta.getFileName();
        this.id = nmeta.getFileId();
        this.meta = nmeta;
    }

    /**
     *
     * @param parent
     * @param name
     * @param dirid
     */
    public SurFile(SurFile parent, String name, long dirid) {
        this.zfspool = parent.zfspool;
        this.parent = parent;
        this.path = parent.path + SurFileSeparator + name;
        this.id = dirid;
    }


    private SurFile(String spath, long[] ids, int deep, StoragePool zfspool) {
        this.path = spath;
        this.zfspool = zfspool;
        if (spath.equals(SurFileSeparator)) {
            this.id = 0;
        } else {
            if (ids != null && ids.length > deep) {
                this.id = ids[deep];
            }
            this.parent = new SurFile(getParent(), ids, --deep, zfspool);
        }
    }

    public int getFileId() {
        return path.toUpperCase().hashCode();
    }

    /**
     * @return NasFile
     */
    public SurFile getParentFile() {
        return this.parent;
    }

    /**
     *
     * @return String
     */
    public final String getParent() {
        if (getId() == 0) {
            return null;
        } else {
            int index = getPath().lastIndexOf(SurFileSeparator);
            return index <= 0 ? SurFileSeparator : getPath().substring(0, index);
        }
    }

    /**
     *
     * @return String
     */
    public String getName() {
        if (getId() == 0) {
            return "";
        } else {
            int index = getPath().lastIndexOf(SurFileSeparator);
            return getPath().substring(index + 1);
        }
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     *
     * @return 0
     * @throws java.io.IOException
     */
    public int mkdir() throws IOException {
        if (getId() >= 0) {
            if (getMeta() != null) {
                return 1;
            } else {
                return 2;
            }
        } else {
            SurFile nf = this.getParentFile();
            if (nf.getId() < 0) {
                return 3;
            } else {
                NasMetaAccessor mda = zfspool.getDatasource().getNasMetaAccessor();
                return mda.mkDirectory(this);
            }
        }
    }

    /**
     *
     * @return boolean
     */
    public boolean exists() {
        return id != -1;
    }

    /**
     *
     * @return boolean
     * @throws java.io.IOException
     */
    public boolean isFile() throws IOException {
        if (id >= 0) {
            return this.meta != null;
        } else {
            throw new FileNotFoundException("");
        }
    }

    /**
     *
     * @return boolean
     * @throws java.io.IOException
     */
    public boolean isDirectory() throws IOException {
        if (id >= 0) {
            return this.meta == null;
        } else {
            throw new FileNotFoundException("");
        }
    }

    /**
     *
     * @return int 0
     * @throws java.io.IOException
     */
    public int delete() throws IOException {
        SurFileFactory.moveInstance(this);
        if (id == -1) {
            return -1;
        }
        if (id == 0) {
            return 2;
        }
        NasMeta nasmeta = meta;
        if (nasmeta == null) {
            NasMetaAccessor mda = zfspool.getDatasource().getNasMetaAccessor();
            int res = mda.rmDirectory(this);
            return res;
        } else {
            NasMetaAccessor mda = zfspool.getDatasource().getNasMetaAccessor();
            mda.deleteNasMeta(nasmeta);
            AsynchDelete.delete(zfspool, nasmeta);
            this.setId(-1);
            this.setMeta(null);
            return 0;
        }
    }

    /**
     * @throws IOException
     */
    public void deletes() throws IOException {
        deletes(this);
    }

    private void deletes(SurFile f) throws IOException {
        SurFile[] list = f.listFiles();
        for (SurFile sf : list) {
            deletes(sf);
        }
        f.delete();
    }

    /**
     *
     * @param nasfile
     * @throws java.io.IOException
     */
    public static void mkdirs(SurFile nasfile) throws IOException {
        if (nasfile.getId() == -1) {
            SurFile nf;
            List<SurFile> list = new ArrayList<>();
            list.add(nasfile);
            while ((nf = nasfile.getParentFile()) != null) {
                if (nf.getId() >= 0) {
                    break;
                } else {
                    list.add(0, nf);
                    nasfile = nf;
                }
            }
            for (SurFile nfile : list) { 
                int b = nfile.mkdir();
                if (b == 1) {
                    throw new FileExistException("");
                } else if (b == 2) {
                } else {
                    mkdirs(nfile);
                }
            }
        }
    }

    /**
     *
     * @throws IOException
     */
    public void mkdirs() throws IOException {
        mkdirs(this);
    }

    /**
     *
     * @return true 
     * @throws IOException
     */
    public boolean createNewFile() throws IOException {
        if (this.exists()) {
            return false;
        } else {
            if (this.parent.id == -1) {
                throw new FileNotFoundException("");
            }
        }
        TcpClient tcpclient = zfspool.getClientSourceMgr().getClient();
        String host = tcpclient.getNode().getNodeProperties().getServerHost();
        NasMeta fmd = new NasMeta();
        fmd.setParentId(this.parent.id);
        fmd.setFileName(getName());
        fmd.setVolumeId(host);
        NasMetaAccessor mda = zfspool.getDatasource().getNasMetaAccessor();
        mda.storeNasMeta(fmd);
        this.setId(fmd.getFileId());
        this.setMeta(fmd);
        return true;
    }

    /**
     * ls
     *
     * @return NasFile[]
     * @throws java.io.IOException
     */
    public SurFile[] listFiles() throws IOException {
        if (id >= 0) {
            if (meta != null) {
                return new SurFile[]{};
            }
        } else {
            return new SurFile[]{};
        }
        NasMetaAccessor mda = zfspool.getDatasource().getNasMetaAccessor();
        return mda.listNasMeta(this, defaultTop);
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public boolean isEmptyDirectory() throws IOException {
        if (id >= 0) {
            if (meta != null) {
                return true;
            }
        } else {
            return true;
        }
        NasMetaAccessor mda = zfspool.getDatasource().getNasMetaAccessor();
        SurFile[] sf = mda.listNasMeta(this, 1);
        return sf.length == 0;
    }

    /**
     *
     * @param dst
     * @throws IOException
     */
    public void renameTo(SurFile dst) throws IOException {
        SurFileFactory.moveInstance(dst);
        SurFileFactory.moveInstance(this);
        if (zfspool != dst.zfspool) {
            throw new UnsupportedOperationException("");
        }
        if (!this.exists()) {
            throw new FileNotFoundException("");
        }
        if (dst.exists() && dst.isDirectory()) {
            throw new FileExistException("");
        }
        if (dst.parent == null || dst.parent.id == -1) {
            throw new FileNotFoundException("");
        }
        if (this.isDirectory()) {
            zfspool.getDatasource().getNasMetaAccessor().mvDirectory(this, dst);
        } else {
            zfspool.getDatasource().getNasMetaAccessor().mvNasMeta(this, dst);
        }
        if (getParentFile().getId() != dst.getParentFile().getId()) {
            quota(this);
            quota(dst);
        }
    }

    /**
     *
     * @param file
     */
    private void quota(SurFile file) {
        TcpClient tcpclient;
        try {
            tcpclient = zfspool.getClientSourceMgr().getClient();
            UpdateQuotaRequest tr =new UpdateQuotaRequest();
            tr.setDirid(file.parent.id);
            tcpclient.get(tr);
        } catch (Exception e) {
        }
    }

    /**
     *
     * @param len
     */
    public void updateMeta(long len) {
        if (meta != null) {
            meta.setLength(len);
            active();
        }
    }

    private synchronized void active() {
        instanceTime = System.currentTimeMillis();
    }

    /**
     *
     * @param force
     * @throws IOException
     */
    public void queryMeta(boolean force) throws IOException {
        if (force || System.currentTimeMillis() - instanceTime > expiresIn) {//更新meta
            zfspool.getDatasource().getNasMetaAccessor().getNasFile(this);
            active();
        }
    }

    /**
     *
     * @return 0
     * @throws java.io.IOException
     */
    public long lastModified() throws IOException {
        if (this.isFile()) {
            queryMeta(false);
            return meta.getLastModified();
        } else {
            return _globalCreateDate;
        }
    }

    /**
     *
     * @return long
     * @throws java.io.IOException
     */
    public long length() throws IOException {
        if (this.isFile()) {
            queryMeta(false);
            return meta.getLength();
        } else {
            if (this.id == 0) {
                Map<String, Long> map = this.zfspool.getSpace();
                return map.get("totalSpace") - map.get("freeSpace");
            } else {
                return zfspool.getDatasource().getNasMetaAccessor().totalDirectory(id);
            }
        }
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public synchronized void setId(long id) {
        this.id = id;
    }

    /**
     * @return the meta
     */
    public NasMeta getMeta() {
        return meta;
    }

    /**
     * @param meta the meta to set
     */
    public synchronized void setMeta(NasMeta meta) {
        this.meta = meta;
    }

    /**
     * @return the lastActiveTime
     */
    public long getInstanceTime() {
        return instanceTime;
    }

    /**
     * @return the zfspool
     */
    public StoragePool getStoragePool() {
        return zfspool;
    }

}
