package com.surfs.nas.server;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.autumn.util.TextUtils;
import static com.surfs.nas.GlobleProperties.charset;
import com.surfs.nas.transport.ThreadPool;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class VolumeDirectoryMaker extends Thread {

    private static final int dirnum = 256 * 256 + 256;
    private final Logger log = LogFactory.getLogger(VolumeDirectoryMaker.class);
    private File root = null;
    private final List<String> dirname = new ArrayList<>();
    private String volumeID;
    private int progress = 0;
    private int status = -1;

    public VolumeDirectoryMaker(File dir) {
        this.root = dir;
        this.setDaemon(true);
        this.setName("VolumeDirMaker");
    }

    public synchronized void startMake() {
        if (status == -1) {
            status = 0;
            ThreadPool.pool.execute(this);
        }
    }

    public synchronized void stopMake() {
        status = 2;
    }

    /**
     *
     * @return
     */
    public boolean initialized() {
        try {
            File fileid = new File(getRoot(), "volume.cfg");
            String id = VolumeScaner.readVolumeID(fileid);
            return id != null && (!id.isEmpty());
        } catch (Exception r) {
            return false;
        }
    }

    public VolumeInfo getInfo() {
        VolumeInfo info = new VolumeInfo();
        info.setPath(getRoot().getAbsolutePath());
        info.setInitialized(initialized());
        info.setStatus(status);
        info.setProgress(getRate());
        info.setVolumeID(volumeID);
        return info;
    }

    @Override
    public void run() {
        dirname.clear();
        for (int ii = 0; ii < 256; ii++) {
            String n = Integer.toHexString(ii);
            if (n.length() == 1) {
                n = "0" + n;
            }
            dirname.add(n);
        }
        try {
            if (status == 2) {
                return;
            }
            File cache = new File(getRoot(), "tmp");
            cache.mkdir();
            mkdir(cache, cache.listFiles());
            if (status == 2) {
                return;
            }
            File[] firstdir = getRoot().listFiles();
            mkdir(getRoot(), firstdir);
            if (status == 2) {
                return;
            }
            firstdir = getRoot().listFiles();
            for (File subfile : firstdir) {
                if (status == 2) {
                    return;
                }
                if (dirname.contains(subfile.getName())) {
                    File[] subdir = subfile.listFiles();
                    if (subdir == null) {
                        throw new Exception();
                    }
                    mkdir(subfile, subdir);
                }
            }
            createConfig();
            status = 3;
        } catch (Throwable r) {
            status = 1;

        }
    }

    /**
     * 创建空文件
     */
    private void createConfig() {
        try {
            File fileid = new File(getRoot(), "volume.cfg");
            String id = VolumeScaner.readVolumeID(fileid);
            if (id != null && (!id.isEmpty())) {//可以读到
                volumeID = id;
                return;
            }
            id = TextUtils.Date2String(new Date(), "yyyy_MM_dd_HHmmssS");
            OutputStream os = new FileOutputStream(fileid);
            os.write(("volumeID=" + id + "\r\n").getBytes(charset));
            os.close();
            volumeID = id;
        } catch (Exception ex) {
        }
    }

    /**
     *
     * @param root
     * @param fs
     */
    private void mkdir(File rootf, File[] fs) {
        if (status == 2) {
            return;
        }
        List<String> fnames = new ArrayList<>();
        for (File f : fs) {
            fnames.add(f.getName());
        }
        for (String n : dirname) {
            if (!fnames.contains(n)) {
                progress++;
                File f = new File(rootf, n);
                if (f.mkdir()) {
                    log.info("create " + f.getAbsolutePath() + " ok!");
                } else {
                    log.error("create " + f.getAbsolutePath() + " err!");
                }
            }
        }
    }

    /**
     * @return the volumeID
     */
    public String getVolumeID() {
        return volumeID;
    }

    /**
     * @return the rate
     */
    public int getRate() {
        return (int) ((float) progress * 100f / (float) dirnum);
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return the root
     */
    public File getRoot() {
        return root;
    }
}
