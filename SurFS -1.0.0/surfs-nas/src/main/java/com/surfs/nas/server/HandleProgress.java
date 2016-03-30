/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.server;

import com.surfs.nas.util.TextUtils;
import com.surfs.nas.NasMeta;
import com.surfs.nas.StorageSources;
import com.surfs.nas.error.ArgumentException;
import com.surfs.nas.log.LogFactory;
import com.surfs.nas.log.Logger;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;
 

 
public final class HandleProgress implements Comparable {

    private static final Logger log = LogFactory.getLogger(HandleProgress.class);

    private long startTime = System.currentTimeMillis();
    private String fileName = null;
    private long parentId = 0;
    private long readCount = 0;
    private long writeCount = 0;

    public HandleProgress(){
    }
    
    public HandleProgress(long parentId, String fileName) {
        this.parentId = parentId;
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return parentId + " " + fileName + ",startTime["
                + TextUtils.Date2String(new Date(getStartTime())) + "],read[" + getReadCount() + "]B,write[" + getWriteCount() + "]B,otime["
                + String.valueOf(System.currentTimeMillis() - getStartTime())
                + "]ms";
    }

    @Override
    public int compareTo(Object o) {
        HandleProgress inf = (HandleProgress) o;
        return (int) (getStartTime() - inf.getStartTime());
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     *
     * @param handles
     * @return String
     */
    public static String toString(List<HandleProgress> handles) {
        StringBuilder sb = new StringBuilder();
        for (HandleProgress hp : handles) {
            sb.append(hp).append("\r\n");
        }
        return sb.toString();
    }

    /**
     *
     * @param handles
     * @return String
     */
    public static String toString(HandleProgress[] handles) {
        StringBuilder sb = new StringBuilder();
        for (HandleProgress hp : handles) {
            sb.append(hp).append("\r\n");
        }
        return sb.toString();
    }


    /**
     * @return the readCount
     */
    public long getReadCount() {
        return readCount;
    }

    /**
     * @param count the readCount to set
     */
    public synchronized void addReadCount(long count) {
        this.setReadCount(getReadCount() + count);
    }

    /**
     * @return the writeCount
     */
    public long getWriteCount() {
        return writeCount;
    }

    /**
     * @param count the writeCount to set
     */
    public synchronized void addWriteCount(long count) {
        this.setWriteCount(writeCount + count);
    }

    /**
     * @return the startTime
     */
    public long executeTime() {
        return System.currentTimeMillis() - getStartTime();
    }

    /**
     * String ->file
     *
     * @param root
     * @param fileName
     * @return File
     * @throws ArgumentException
     */
    public static File newFile(File root, String fileName) throws ArgumentException {
        try {
            String subpath = fileName.substring(0, 2)
                    + File.separator + fileName.substring(2, 4)
                    + File.separator + fileName;
            return new File(root, subpath);
        } catch (Exception r) {
            throw new ArgumentException("Invalid file name:" + fileName);
        }
    }

    /**
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        long l = System.currentTimeMillis();
        boolean b = file.delete();
        log.info("[{0}]del:{1},otime[{2}]", new Object[]{file.getAbsolutePath(), b, (System.currentTimeMillis() - l)});
        return b;
    }

    /**
     *
     * @param dfile
     * @return RandomAccessFile
     * @throws IOException
     */
    public static RandomAccessFile openFile(File dfile) throws IOException {
        long l = System.currentTimeMillis();
        RandomAccessFile sos = new RandomAccessFile(dfile, "rw");
        log.info("[{0}]open!otime[{1}]", new Object[]{dfile.getAbsolutePath(), (System.currentTimeMillis() - l)});
        return sos;
    }

    /**
     *
     * @param fmd
     * @throws IOException
     */
    public static void updateNasMeta(NasMeta fmd) throws IOException {
        long l = System.currentTimeMillis();
        StorageSources.getServiceStoragePool().getDatasource().getNasMetaAccessor().updateNasMeta(fmd, false);
        log.info("[{0}]update,otime[{1}]", new Object[]{fmd.getFileName(), (System.currentTimeMillis() - l)});
    }

    /**
  
     *
     * @param metadata
     * @throws IOException
     */
    public static void deleteNasMeta(NasMeta metadata) throws IOException {
        long l = System.currentTimeMillis();
        StorageSources.getServiceStoragePool().getDatasource().getNasMetaAccessor().deleteNasMeta(metadata);
        log.warn("[{0}]delete,otime[{1}]", new Object[]{metadata.getFileName(), (System.currentTimeMillis() - l)});
    }

    /**
     * @return the parentId
     */
    public long getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the startTime
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @param readCount the readCount to set
     */
    public void setReadCount(long readCount) {
        this.readCount = readCount;
    }

    /**
     * @param writeCount the writeCount to set
     */
    public void setWriteCount(long writeCount) {
        this.writeCount = writeCount;
    }
}
