package com.surfs.nas.server;

import com.surfs.nas.StoragePool;
import com.surfs.nas.StorageSources;
import com.autumn.core.cfg.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;


public class HttpServiceListener implements ConfigListener {

    @Override
    public Object callMethod(Method method) {
        StoragePool pool = StorageSources.getServiceStoragePool();
        if (pool == null) {
            return "";
        }
        try {
            String ss = method.getMethodName();
            if (ss.endsWith(".showVols")) {
                List<Volume> list = new ArrayList<>(pool.getServerSourceMgr().getVolumeMap().values());
                StringBuilder sb = new StringBuilder();
                for (Volume vol : list) {
                    sb.append(vol.toString());
                }
                return sb.toString();
            } else if (ss.endsWith(".serverInfo")) {
                StringBuilder sb = new StringBuilder();
                sb.append("name:").append(pool.getName()).append("\r\n");
                sb.append("comment:").append(pool.getComment()).append("\r\n");
                sb.append("type:").append(pool.getDbprovider()).append("\r\n");
                sb.append(pool.getServerSourceMgr().getTcpServer().getMonitor().toString());
                return sb.toString();
            } else if (ss.endsWith(".handlers")) {
                String volid = method.getParamValue("volumeid");
                Volume vol = pool.getServerSourceMgr().getVolumeMap().get(volid);
                if (vol == null) {
                    return "";
                } else {
                    BlockingQueue<HandleProgress> queue = vol.getHandlers();
                    if (queue == null) {
                        return "";
                    } else {
                        List<HandleProgress> list = new ArrayList(queue);
                        Collections.sort(list);
                        return HandleProgress.toString(list);
                    }
                }
            } else {
                return  ss;
            }
        } catch (Exception r) {
            return "err:" + r.getMessage();
        }
    }

    @Override
    public boolean changeProperty(Property property) {
        return false;
    }

}
