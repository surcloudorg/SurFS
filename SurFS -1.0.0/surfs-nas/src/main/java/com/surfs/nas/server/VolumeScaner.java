/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.server;

import com.surfs.nas.ReusableThread;
import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.surfs.nas.NosqlDataSource;
import com.surfs.nas.StorageSources;
import com.surfs.nas.ResourcesAccessor;
import com.surfs.nas.VolumeProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;


public class VolumeScaner extends ReusableThread {

    static final Logger log = LogFactory.getLogger(VolumeScaner.class);
    private ServerSourceMgr mgr = null;
    private final Map<String, VolumeDirectoryMaker> dirMap = new HashMap<>();

    public VolumeScaner(ServerSourceMgr mgr) {
        super(3000);
        this.mgr = mgr;
    }

    private long scanTime = 0;

    @Override
    public void doTask() throws IOException {
        if (System.currentTimeMillis() - scanTime > mgr.getGlobleProperties().getReloadInterval() * 1000) {
            scan();
            scanTime = System.currentTimeMillis();
        }
    }

    public VolumeDirectoryMaker getVolumeDirectoryMaker(String path) {
        return dirMap.get(path);
    }

    /**
     *
     * @return
     */
    public synchronized List<VolumeInfo> listVolume() {
        String mntpoint = mgr.getGlobleProperties().getMntPoint() == null ? "/surfs" : mgr.getGlobleProperties().getMntPoint();
        File root = new File(mntpoint);
        List<Entry<String, VolumeDirectoryMaker>> ls = new ArrayList<>(dirMap.entrySet());
        for (Entry<String, VolumeDirectoryMaker> ent : ls) {
            File f = ent.getValue().getRoot();
            if ((!f.exists()) || (!f.isDirectory())) {
                dirMap.remove(ent.getKey());
                continue;
            }
            File pf = f.getParentFile();
            if (!pf.getAbsolutePath().equals(root.getAbsolutePath())) {
                dirMap.remove(ent.getKey());
            }
        }
        File[] paths = root.listFiles();
        paths = paths == null ? new File[0] : paths;
        for (File path : paths) {
            if (path.isDirectory()) {
                VolumeDirectoryMaker maker = new VolumeDirectoryMaker(path);
                dirMap.put(path.getAbsolutePath(), maker);
            }
        }
        List<VolumeInfo> list = new ArrayList<>();
        Collection<VolumeDirectoryMaker> makers = dirMap.values();
        for (VolumeDirectoryMaker maker : makers) {
            list.add(maker.getInfo());
        }
        return list;
    }

    /**
     *
     * @throws IOException
     */
    private void scan() throws IOException {
        NosqlDataSource ds = StorageSources.getServiceStoragePool().getDatasource();
        ResourcesAccessor configDataAccessor = ds.getResourcesAccessor();

        Map<VolumeProperties, File> vols = new HashMap<>();
        String mntpoint = mgr.getGlobleProperties().getMntPoint() == null ? "/surfs" : mgr.getGlobleProperties().getMntPoint();
        File root = new File(mntpoint);
        File[] paths = root.listFiles();
        paths = paths == null ? new File[0] : paths;
        for (File path : paths) {
            if (path.isDirectory()) {
                File fileid = new File(path, "volume.cfg");
                if (fileid.exists() && fileid.length() < 1024 * 1024) {
                    String id = readVolumeID(fileid);
                    if (!"".equals(id)) { 
                        VolumeProperties vp = configDataAccessor.getVolumeProperties(id);
                        if (vp == null) {
                            vp = new VolumeProperties();
                            vp.setVolumeID(id);
                            vp.setServerHost(ServerSourceMgr.getLocalHostName());
                        } else {
                            if (!vp.getServerHost().equals(ServerSourceMgr.getLocalHostName())) {
                                vp.setServerHost(ServerSourceMgr.getLocalHostName());
                            }
                        }
                        vols.put(vp, path);
                    }
                }
            }
        }
        List<String> volIDs = new ArrayList<>();
        Set<Map.Entry<VolumeProperties, File>> volset = vols.entrySet();
        for (Map.Entry<VolumeProperties, File> ent : volset) {
            configDataAccessor.putVolumeProperties(ent.getKey());
            Volume vol = mgr.getVolumeMap().get(ent.getKey().getVolumeID());
            if (vol == null) {
                vol = new Volume(mgr, ent.getKey(), ent.getValue());
                mgr.getVolumeMap().put(ent.getKey().getVolumeID(), vol);
            } else {
                vol.setVolumeProperties(ent.getKey());
                if (!vol.getPath().getAbsolutePath().equals(ent.getValue().getAbsolutePath())) {
                    vol.setPath(ent.getValue());
                    if (this.isAlive()) {
                        vol.init();
                    }
                }
                vol.setState(true);
            }
            if (mgr.getSelector() != null) {
                mgr.getSelector().spaceChange();
            }
            mgr.versionUpdater.restart();
            volIDs.add(ent.getKey().getVolumeID());
        }
        List<String> set = new ArrayList<>(mgr.getVolumeMap().keySet());
        for (String vol : set) { 
            if (!volIDs.contains(vol)) {
                Volume volume = mgr.getVolumeMap().remove(vol);
                volume.destroy();
            }
        }
    }

    /**
     *
     * @param f
     * @return String
     * @throws java.io.IOException
     */
    public static String readVolumeID(File f) throws IOException {
        if (!f.exists()) {
            return "";
        }
        Properties p = new Properties();
        p.load(new FileInputStream(f));
        String id = p.getProperty("volumeID");
        if (id == null || id.trim().isEmpty()) {     
            return "";
        } else {
            return id.trim();
        }
    }
}
