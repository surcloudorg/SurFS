/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.protocol;

import com.surfs.nas.log.LogFactory;
import com.surfs.nas.log.Logger;
import com.surfs.nas.NasMeta;
import com.surfs.nas.StorageSources;
import com.surfs.nas.error.SessionTimeoutException;
import com.surfs.nas.error.VolumeBusyException;
import com.surfs.nas.transport.TcpResponse;
import com.surfs.nas.transport.ErrorResponse;
import com.surfs.nas.server.AsychMetaUpdater;
import com.surfs.nas.server.HandleProgress;
import com.surfs.nas.server.ServerSourceMgr;
import com.surfs.nas.server.UUID;
import com.surfs.nas.server.Volume;
import com.surfs.nas.transport.LongResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class RandomAccessAction {

    private static final Logger log = LogFactory.getLogger(RandomAccessAction.class);

    private Volume volume = null;
    private HandleProgress progress = null;
    private RandomAccessFile randomAccessFile = null;
    private FileChannel fc = null;
    private AsychMetaUpdater update = null;
    private final ServerSourceMgr mgr;
    private long activeTime = System.currentTimeMillis();
    private NasMeta meta;
    private final long parentId;
    private final long fileId;
    private boolean closed = false;

    public RandomAccessAction(ServerSourceMgr mgr, long parentId, long fileId) {
        this.mgr = mgr;
        this.parentId = parentId;
        this.fileId = fileId;
    }

    public boolean isSessionTimeout() {
        return System.currentTimeMillis() - activeTime > mgr.getGlobleProperties().getSessionTimeout() * 1000;
    }

    /**
     *
     * @throws VolumeBusyException
     * @throws IOException
     */
    private void initOp() throws VolumeBusyException, IOException {
        synchronized (this) {
            activeTime = System.currentTimeMillis();
            if (randomAccessFile == null) {
                this.meta = StorageSources.getServiceStoragePool().getDatasource().getNasMetaAccessor().queryNasMeta(getParentId(), getFileId());
                if (meta == null) {
                     
                    throw new FileNotFoundException("");
                }
                this.progress = new HandleProgress(getParentId(), meta.getFileName());
                if (meta.getRandomName().isEmpty()) {
                    volume = mgr.getSelector().getVolume();
                    meta.setRandomName(UUID.makeUUID());
                    meta.setVolumeId(volume.getVolumeProperties().getVolumeID());
                    HandleProgress.updateNasMeta(meta);
                } else {
                    volume = mgr.getSelector().getVolume(getMeta().getVolumeId());
                }
                volume.addHandlerWBlock(progress);
                File f = HandleProgress.newFile(volume.getPath(), getMeta().getRandomName());
                update = new AsychMetaUpdater(mgr, meta, f);
                try {
                    randomAccessFile = HandleProgress.openFile(f);
                    fc = randomAccessFile.getChannel();
                } catch (IOException r) {
                    getVolume().setState(false);
                    throw r;
                }
                update.start(true);
                activeTime = System.currentTimeMillis();
                
            } else {
                if (closed) {
                    throw new SessionTimeoutException("Stream closed");
                }
            }
        }
        if (getVolume().isOffline()) {
            throw new VolumeBusyException("");
        }
    }

    /**
     *
     * @param res
     * @return TcpResponse
     * @throws SessionTimeoutException
     */
    public TcpResponse write(WriteRequest res) throws IOException {
        initOp();
        try {
            if (res.getContent() != null) {
                ByteBuffer buf = ByteBuffer.wrap(res.getContent());
                int wc = 0;
                while (wc < res.getContent().length) {
                    wc = wc + fc.write(buf, res.getOffset() + wc);
                }
                log.debug("[{3}]WRITE:pos={0},len={1},res={2}", new Object[]{res.getOffset(), res.getContent().length, wc, progress.getFileName()});
                progress.addWriteCount(wc);
                flush();
            }
            WriteResponse tr = new WriteResponse(res);
            if (!res.isCreate()) {
                tr.setRandomName(meta.getRandomName());
                tr.setVolumeId(meta.getVolumeId());
            }
            return tr;
        } catch (Exception ex) {
            if (!closed) {
                doFinal();
                getVolume().setState(false);
                log.trace(progress.getFileName() + "write err!", ex);
                return new ErrorResponse(res, ex);
            } else {
                throw new SessionTimeoutException(ex);
            }
        }
    }

    /**
     *
     * @param res
     * @return TcpResponse
     * @throws java.io.IOException
     */
    public TcpResponse read(ReadRequest res) throws IOException {
        initOp();
        try {
            ByteBuffer buf = res.getSize() > 0 ? ByteBuffer.allocate(res.getSize())
                    : ByteBuffer.allocate(getVolume().getGlobleProperties().getBlocksize() * 1024);
            int count = fc.read(buf, res.getOffset());
            ReadResponse tr = new ReadResponse(res);
            log.debug("[{3}]READ:pos={0},len={1},res={2}", new Object[]{res.getOffset(), res.getSize(), count, progress.getFileName()});
            if (!res.isCreate()) {
                tr.setRandomName(meta.getRandomName());
                tr.setVolumeId(meta.getVolumeId());
            }
            if (count > 0) {
                progress.addReadCount(count);
                tr.setLen(count);
                tr.setPos(buf.arrayOffset());
                tr.setContent(buf.array());
            }
            return tr;
        } catch (Exception ex) {
            if (!closed) {
                doFinal();
                log.error("[{0}]read err:{1}:{2}", new Object[]{progress.getFileName(),
                    ex.getClass().getSimpleName(), ex.getMessage()});
                return new ErrorResponse(res, ex);
            } else {
                throw new SessionTimeoutException(ex);
            }
        }
    }

    /**
     *
     * @param res
     * @return
     * @throws java.io.IOException
     */
    public TcpResponse trunc(TruncRequest res) throws IOException {
        initOp();
        try {
           
            fc.truncate(res.getLength());
            randomAccessFile.setLength(res.getLength());
            flush();
            WriteResponse tr = new WriteResponse(res);
            if (!res.isCreate()) {
                tr.setRandomName(meta.getRandomName());
                tr.setVolumeId(meta.getVolumeId());
            }
            return tr;
        } catch (Exception ex) {
            if (!closed) {
                doFinal();
                log.error("[{0}]TRUNC err:{1}:{2}", new Object[]{progress.getFileName(),
                    ex.getClass().getSimpleName(), ex.getMessage()});
                return new ErrorResponse(res, ex);
            } else {
                throw new SessionTimeoutException(ex);
            }
        }
    }

    /**
     *
     * @throws Exception
     */
    private void flush() throws IOException {
        if (mgr.getGlobleProperties().getUsecache() == 0) {
            update.start(false);
        } else if (mgr.getGlobleProperties().getUsecache() == 1) {
            fc.force(false);
            update.start(false);
        } else {
            fc.force(true);
            update.start(true);
        }
    }

    private void doFinal() {
        synchronized (this) {
            mgr.getTcpActionMgr().moveTcpAction(this);
            closed = true;
            if (fc != null) {
                try {
                    fc.force(true);
                    fc.close();
                } catch (IOException ex) {
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException ex) {
                }
            }
            if (update != null) {
                update.start(true);
            }
            if (getVolume() != null) {
                getVolume().delHandler(progress);
            }
        }
    }

    public LongResponse complete(CloseRequest res) {
        if (progress != null) {
            log.info("[{0}]closed,write[{1}]b,read[{2}]b,otime[{3}]ms", new Object[]{progress.getFileName(),
                progress.getWriteCount(), progress.getReadCount(), progress.executeTime()});
        }
        doFinal();
        LongResponse tr = new LongResponse(res);
        if (update != null) {
            tr.setValue(update.curLength());
        }
        return tr;
    }

    public void close() {
        if (progress != null) {
            log.info("[{0}]closed!write[{1}]b,read[{2}]b,otime[{3}]ms", new Object[]{progress.getFileName(),
                progress.getWriteCount(), progress.getReadCount(), progress.executeTime()});
        }
        doFinal();
    }

    /**
     * @return the volume
     */
    public Volume getVolume() {
        return volume;
    }

    /**
     * @return the meta
     */
    public NasMeta getMeta() {
        return meta;
    }

    /**
     * @return the parentId
     */
    public long getParentId() {
        return parentId;
    }

    /**
     * @return the fileId
     */
    public long getFileId() {
        return fileId;
    }
}
