/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.client;

import com.surfs.nas.StoragePool;
import com.surfs.nas.StorageSources;
import static com.surfs.nas.client.SurFile.checkPath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SurFileFactory {

    private static final Map<StoragePool, Map<String, SurFile>> factorys = Collections.synchronizedMap(new HashMap<StoragePool, Map<String, SurFile>>());

    public static final long expiresIn = 1000 * 60 * 3;

    private static final Thread clear;

    static {
        clear = new Thread() {
            @Override
            public void run() {
                while (!this.isInterrupted()) {
                    try {
                        sleep(1000 * 5);
                        doclear();
                    } catch (InterruptedException r) {
                        break;
                    } catch (Exception r) {
                    }
                }
            }
        };
        clear.setDaemon(true);
        clear.setName("SurFileFactory-Clean");
        clear.start();
    }


    private static void doclear() {
        List<Map<String, SurFile>> ls = new ArrayList<>(factorys.values());
        for (Map<String, SurFile> map : ls) {
            List<Entry<String, SurFile>> set = new ArrayList<>(map.entrySet());
            for (Entry<String, SurFile> ent : set) {
                if (System.currentTimeMillis() - ent.getValue().getInstanceTime() > expiresIn) {
                    map.remove(ent.getKey());
                }
            }
        }
    }

    /**
     *
     * @param path
     * @return SurFile
     * @throws IOException
     */
    public static SurFile newInstance(String path) throws IOException {
        return newInstance(path, StorageSources.getDefaultStoragePool());
    }

    /**
     *
     * @param path
     * @param poolname
     * @return SurFile
     * @throws IOException
     */
    public static SurFile newInstance(String path, String poolname) throws IOException {
        return newInstance(path, StorageSources.getStoragePool(poolname));
    }

    /**
     *
     * @param path
     * @param zfspool
     * @return SurFile
     * @throws IOException
     */
    public static SurFile newInstance(String path, StoragePool zfspool) throws IOException {
        Map<String, SurFile> map = factorys.get(zfspool);
        if (map == null) {
            map = Collections.synchronizedMap(new HashMap<String, SurFile>());
            factorys.put(zfspool, map);
        }
        path = checkPath(path);
        SurFile surfile = map.get(path);
        if (surfile == null) {
            surfile = new SurFile(path, zfspool);
            map.put(path, surfile);
        } else {
            if (System.currentTimeMillis() - surfile.getInstanceTime() > expiresIn) {
                surfile = new SurFile(path, zfspool);
                map.put(path, surfile);
            }
        }
        return surfile;
    }

    public static void moveInstance(SurFile sf) throws IOException {
        Map<String, SurFile> map = factorys.get(sf.getStoragePool());
        if (map != null) {
            map.remove(sf.getPath());
        }
    }

    public static SurFile putInstance(SurFile sf) throws IOException {
        Map<String, SurFile> map = factorys.get(sf.getStoragePool());
        if (map == null) {
            map = Collections.synchronizedMap(new HashMap<String, SurFile>());
            factorys.put(sf.getStoragePool(), map);
        }
        SurFile oldsf = map.get(sf.getPath());
        if (oldsf == null) {
            map.put(sf.getPath(), sf);
            return sf;
        } else {
            return oldsf;
        }
    }
}
