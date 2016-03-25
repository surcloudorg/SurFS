package com.surfs.nas.client;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.surfs.nas.StoragePool;
import com.surfs.nas.NasMeta;
import com.surfs.nas.error.ArgumentException;
import com.surfs.nas.protocol.CloseRequest;
import com.surfs.nas.protocol.ReadRequest;
import com.surfs.nas.protocol.ReadResponse;
import com.surfs.nas.protocol.TruncRequest;
import com.surfs.nas.protocol.WriteRequest;
import com.surfs.nas.protocol.WriteResponse;
import com.surfs.nas.transport.LongResponse;
import com.surfs.nas.transport.TcpClient;
import java.io.FileNotFoundException;
import java.io.IOException;

public class NasRandomAccessor {

    public static Logger log = LogFactory.getLogger(NasRandomAccessor.class);

    SurFile file;
    StoragePool zfspool = null;
    AsynchWriter writer;
    boolean closed = false;
    NasMeta nasMeta;

    /**
     *
     * @param f
     * @throws IOException
     */
    public NasRandomAccessor(SurFile f) throws IOException {
        this.file = f;
        this.zfspool = f.getStoragePool();
        if (!f.exists()) {
            f.createNewFile();
        }
        nasMeta = file.getMeta();
        if (nasMeta == null) {
            throw new FileNotFoundException();
        }
        if (zfspool.getClientSourceMgr().getGlobleProperties().getWriteQueue() > 0
                && zfspool.getClientSourceMgr().getGlobleProperties().getUsecache() == 0) {
            writer = new AsynchWriter(this, zfspool.getClientSourceMgr().getGlobleProperties().getWriteQueue());
        }
    }

    public TcpClient getTcpClient() throws IOException {
        if (nasMeta.getRandomName().isEmpty()) {
            return zfspool.getClientSourceMgr().getClientByNode(nasMeta.getVolumeId());
        } else {
            return zfspool.getClientSourceMgr().getClientByVolume(nasMeta.getVolumeId());
        }
    }

    /**
     *
     * @param e
     * @throws IOException
     */
    private synchronized boolean checkException(Exception e) throws IOException {
        if (e instanceof FileNotFoundException) {
            try {
                file.queryMeta(true);
            } catch (IOException ie) {
                return false;
            }
            if (file.isFile()) {
                nasMeta = file.getMeta();
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param tr
     * @param retrytimes 
     * @throws java.io.IOException
     */
    public void synchWrite(WriteRequest tr, int retrytimes) throws IOException {
        int times = 0;
        while (true) {
            try {
                TcpClient tcpclient = getTcpClient();
                WriteResponse response = (WriteResponse) tcpclient.get(tr);
                if (response.getRandomName() != null) {
                    nasMeta.setRandomName(response.getRandomName());
                    nasMeta.setVolumeId(response.getVolumeId());
                }
                break;
            } catch (Exception e) {
                if (checkException(e)) {
                    tr.setCreate(!nasMeta.getRandomName().isEmpty());
                    tr.setParentId(nasMeta.getParentId());
                    tr.setFileId(nasMeta.getFileId());
                    tr.setResponse(null);
                    continue;
                }
                if (retrytimes == 0 && closed) {
                    throw e instanceof IOException ? (IOException) e : new IOException(e);
                }
                try {
                    if (retrytimes > 0 && times++ >= retrytimes) {
                        throw e instanceof IOException ? (IOException) e : new IOException(e);
                    }
                    if (tr.getResponse() != null) {
                        tr.setResponse(null);
                        Thread.sleep(zfspool.getClientSourceMgr().getGlobleProperties().getReadTimeout() * 1000);
                    }
                } catch (InterruptedException ex) {
                    throw e instanceof IOException ? (IOException) e : new IOException(e);
                }
            }
        }
    }

    /**
     *
     * @param b
     * @param off
     * @param len
     * @param pos
     * @throws IOException
     */
    public void write(byte b[], int off, int len, long pos) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0)
                || ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        checkStatue();
        WriteRequest tr = new WriteRequest();
        tr.setCreate(!nasMeta.getRandomName().isEmpty());
        tr.setParentId(nasMeta.getParentId());
        tr.setFileId(nasMeta.getFileId());
        tr.setOffset(pos);
        tr.setContent(b);
        tr.setPos(off);
        tr.setLen(len);
        if (nasMeta.getLength() < pos + len) {
            file.updateMeta(pos + len);
        }
        if (writer == null) {
            synchWrite(tr, zfspool.getClientSourceMgr().getGlobleProperties().getErrRetryTimes());
        } else {
            byte[] bs = new byte[len];
            System.arraycopy(b, off, bs, 0, len);
            tr.setPos(0);
            tr.setContent(bs);
            writer.write(tr);
        }
    }

    /**
     *
     * @param tr
     */
    private ReadResponse synchRead(ReadRequest tr) throws IOException {
        int retrytimes = 0;
        while (true) {
            try {
                TcpClient tcpclient = getTcpClient();
                ReadResponse response = (ReadResponse) tcpclient.get(tr);
                return response;
            } catch (Exception e) {
                if (checkException(e)) {
                    tr.setCreate(!nasMeta.getRandomName().isEmpty());
                    tr.setParentId(nasMeta.getParentId());
                    tr.setFileId(nasMeta.getFileId());
                    tr.setResponse(null);
                    continue;
                }
                try {
                    if (retrytimes++ >= zfspool.getClientSourceMgr().getGlobleProperties().getErrRetryTimes()) {
                        throw e instanceof IOException ? (IOException) e : new IOException(e);
                    }
                    if (tr.getResponse() != null) {
                        tr.setResponse(null);
                        Thread.sleep(zfspool.getClientSourceMgr().getGlobleProperties().getReadTimeout() * 1000);
                    }
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    /**
     *
     * @param b
     * @param off
     * @param len
     * @param pos
     * @return int
     * @throws IOException
     */
    public int read(byte b[], int off, int len, long pos) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        checkStatue();
        if (nasMeta.getRandomName().isEmpty()) {
            return -1;
        }
        flush();
        ReadRequest tr = new ReadRequest();
        tr.setCreate(!nasMeta.getRandomName().isEmpty());
        tr.setParentId(nasMeta.getParentId());
        tr.setFileId(nasMeta.getFileId());
        tr.setOffset(pos);
        tr.setSize(len);
        ReadResponse response = synchRead(tr);
        if (response.getRandomName() != null) {
            nasMeta.setRandomName(response.getRandomName());
            nasMeta.setVolumeId(response.getVolumeId());
        }
        if (response.getContent() != null) {
            int length = response.getLen();
            System.arraycopy(response.getContent(), 0, b, off, length);
            return length;
        } else {
            return -1;
        }

    }

    /**
     *
     * @param tr
     */
    private WriteResponse synchTrunc(TruncRequest tr) throws IOException {
        int retrytimes = 0;
        while (true) {
            try {
                TcpClient tcpclient = getTcpClient();
                WriteResponse response = (WriteResponse) tcpclient.get(tr);
                return response;
            } catch (Exception e) {
                if (checkException(e)) {
                    tr.setCreate(!nasMeta.getRandomName().isEmpty());
                    tr.setParentId(nasMeta.getParentId());
                    tr.setFileId(nasMeta.getFileId());
                    tr.setResponse(null);
                    continue;
                }
                try {
                    if (retrytimes++ >= zfspool.getClientSourceMgr().getGlobleProperties().getErrRetryTimes()) {
                        throw e instanceof IOException ? (IOException) e : new IOException(e);
                    }
                    if (tr.getResponse() != null) {
                        tr.setResponse(null);
                        Thread.sleep(zfspool.getClientSourceMgr().getGlobleProperties().getReadTimeout() * 1000);
                    }
                } catch (InterruptedException ex) {
                    throw e instanceof IOException ? (IOException) e : new IOException(e);
                }
            }
        }
    }

    /**
     *
     * @param newlength
     * @throws IOException
     */
    public void setLength(long newlength) throws IOException {
        if (newlength < 0) {
            throw new ArgumentException("offset can't be negative");
        }
        checkStatue();
        flush();
        TruncRequest tr = new TruncRequest();
        tr.setCreate(!nasMeta.getRandomName().isEmpty());
        tr.setParentId(nasMeta.getParentId());
        tr.setFileId(nasMeta.getFileId());
        tr.setLength(newlength);
        file.updateMeta(newlength);
        log.info("[{1}] trunc:length={0}", new Object[]{newlength, file.getPath()});
        WriteResponse response = (WriteResponse) synchTrunc(tr);
        if (response.getRandomName() != null) {
            nasMeta.setRandomName(response.getRandomName());
            nasMeta.setVolumeId(response.getVolumeId());
        }
    }

    private void checkStatue() throws IOException {
        if (closed) {
            throw new IOException("Stream closed");
        }
    }

    private void flush() throws IOException {
        if (writer != null) {
            writer.join();
        }
    }

    /**
     *
     * @throws IOException
     */
    public synchronized void close() throws IOException {
        if (!closed) {
            try {
                closed = true;
                flush();
                TcpClient tcpclient = getTcpClient();
                CloseRequest tr =new CloseRequest();
                tr.setParentId(nasMeta.getParentId());
                tr.setFileId(nasMeta.getFileId());
                LongResponse response = (LongResponse) tcpclient.get(tr);
                if (response.getValue() > 0) {
                    file.updateMeta(response.getValue());
                }
            } catch (IOException e) {
            }
        }
    }
}
