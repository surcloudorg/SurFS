package com.surfs.nas;

import com.surfs.nas.error.PoolNotFoundException;
import com.surfs.nas.transport.ThreadPool;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.Set;

public class StorageSources {

    static Map<String, StoragePool> storageMap = Collections.synchronizedMap(new HashMap());
    static StoragePool defaultPool = null;
    static StoragePool servicePool = null;

    static {
        StorageConfig.initClient();
    }

    /**
     * 初始化
     *
     * @param storage
     */
    static synchronized void putStorage(StoragePool storage) {
        String key = storage.getName();
        if (storageMap.containsKey(key)) {
            throw new ServiceConfigurationError("Duplicated pool name:" + key);
        }
        storageMap.put(key, storage);
        if (defaultPool == null) {
            defaultPool = storage;
        } else {
            if (!defaultPool.isDefaultPool()) {
                if (storage.isDefaultPool()) {
                    defaultPool = storage;
                }
            }
        }
    }

    /**
     * close
     */
    public static void terminate() {
        Collection<StoragePool> nodes = storageMap.values();
        for (StoragePool node : nodes) {
            node.close();
        }
        ThreadPool.shutdown();
    }

    /**
     *
     * @param name
     * @return
     * @throws com.surfs.nas.error.PoolNotFoundException
     */
    public static StoragePool getStoragePool(String name) throws PoolNotFoundException {
        if (name == null) {
            return getDefaultStoragePool();
        }
        StoragePool sp = findStoragePool(name);
        if (sp != null) {
            return sp;
        }
        throw new PoolNotFoundException("pool name '" + name + "' is not exist.");
    }

    public static StoragePool getDefaultStoragePool() {
        return defaultPool;
    }

    /**
     *
     * @param name
     * @return StoragePool
     */
    public static StoragePool findStoragePool(String name) {
        return storageMap.get(name);
    }

    /**
     * @return the localPool
     */
    public static StoragePool getServiceStoragePool() {
        return servicePool;
    }

    /**
     * @return Map<String, String>
     */
    public static Map<String, String> getStoragePoolMap() {
        Map<String, String> map = new HashMap<>();
        Set<String> set = storageMap.keySet();
        for (String name : set) {
            StoragePool pool = storageMap.get(name);
            map.put(name, pool.getComment());
        }
        return map;
    }
}
