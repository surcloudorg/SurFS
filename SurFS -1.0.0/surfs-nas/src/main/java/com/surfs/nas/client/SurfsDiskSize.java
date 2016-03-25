package com.surfs.nas.client;

import com.surfs.nas.error.DiskFullException;
import java.io.IOException;
import java.util.Map;

public class SurfsDiskSize extends Thread {

    private final SurFile root;
    private long m_total = 0;
    private long m_free = 0;
    private final long m_blockperunit = 16065;
    private final long m_blocksize = 512;

    public SurfsDiskSize(SurFile root) {
        this.root = root;
    }

    public String getDiskName() {
        return root.getPath();
    }

    /**
     *
     * @throws DiskFullException
     */
    public void checkDiskSpace() throws DiskFullException {
        if (m_free <= 0) {
            throw new DiskFullException("No space left on device");
        }
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                getSpace();
                int interval = 1000 * 10;
                try {
                    interval = root.getStoragePool().getClientSourceMgr().getGlobleProperties().getCheckSpaceInterval() * 1000;
                } catch (IOException ex) {
                }
                sleep(interval);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

    protected void getDirSpace() throws IOException {
        m_total = root.getStoragePool().getDatasource().getNasMetaAccessor().getQuata(root.getPath());
        long len = root.length();
        m_free = m_total - len;
    }

    private void getRootSpace() {
        try {
            Map<String, Long> map = root.getStoragePool().getSpace();
            m_total = map.get("totalSpace");
            m_free = map.get("freeSpace");
        } catch (Exception e) {
        }
    }

    private void getSpace() {
        if (root.getId() == 0) {
            getRootSpace();
        } else {
            try {
                getDirSpace();
            } catch (IOException ex) {
            }
        }
    }

    public long getFree() {
        //getSpace();
        return m_free;
    }

    public long getTotal() {
        //getSpace();
        return m_total;
    }

    public long getFreeUnits() {
        //getSpace();
        return m_free / m_blocksize / m_blockperunit;
    }

    public long getTotalUnits() {
        //getSpace();
        return m_total / m_blocksize / m_blockperunit;
    }

    /**
     * @return the m_blockperunit
     */
    public long getBlockPerunit() {
        return m_blockperunit;
    }

    /**
     * @return the m_blocksize
     */
    public long getBlockSize() {
        return m_blocksize;
    }
}
