package com.surfs.nas.server;

import com.surfs.nas.protocol.RandomAccessAction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TcpActionMgr extends Thread {

    private final Map<Long, Map<Long, RandomAccessAction>> handles = new ConcurrentHashMap<>();

    private final ServerSourceMgr mgr;
    private final Queue<Volume> offlines = new ConcurrentLinkedQueue<>();

    public TcpActionMgr(ServerSourceMgr mgr) {
        this.mgr = mgr;
        this.setName("SessionMgr");
    }

    /**
     *
     * @param vol
     */
    public void offline(Volume vol) {
        offlines.add(vol);
    }

    /**
     *
     * @param vol
     */
    private void clearoffline(Volume vol) {
        List< Map<Long, RandomAccessAction>> col = new ArrayList<>(handles.values());
        for (Map<Long, RandomAccessAction> map : col) {
            List<RandomAccessAction> actions = new ArrayList<>(map.values());
            for (RandomAccessAction action : actions) {
                if (vol.getVolumeProperties().getVolumeID().equals(action.getMeta().getVolumeId())) {//下线
                    action.close();
                }
            }
        }
    }

    private void clear() {
        List< Map<Long, RandomAccessAction>> col = new ArrayList<>(handles.values());
        for (Map<Long, RandomAccessAction> map : col) {
            List<RandomAccessAction> actions = new ArrayList<>(map.values());
            for (RandomAccessAction action : actions) {
                if (action.isSessionTimeout()) {//过期
                    action.close();
                }
            }
        }
    }

    private boolean exiting = false;
    private Thread currentThread = null;

    public void shutdown() {
        exiting = true;
        if (currentThread != null) {
            currentThread.interrupt();
        }
    }

    @Override
    public void run() {
        currentThread = Thread.currentThread();
        while (!exiting) {
            try {
                if (!offlines.isEmpty()) {
                    Volume vol = offlines.poll();
                    clearoffline(vol);
                    continue;
                }
                sleep(2000);
                clear();
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     *
     * @param parentId
     * @param fileId
     * @return
     * @throws java.io.IOException
     */
    public RandomAccessAction putTcpAction(long parentId, long fileId) throws IOException {
        synchronized (this) {
            Map<Long, RandomAccessAction> map = handles.get(parentId);
            if (map == null) {
                map = new ConcurrentHashMap<>();
                handles.put(parentId, map);
            }
            RandomAccessAction action = map.get(fileId);
            if (action == null) {
                action = new RandomAccessAction(mgr, parentId, fileId);
                map.put(fileId, action);
            }
            return action;
        }
    }

    public void moveTcpAction(RandomAccessAction action) {
        synchronized (this) {
            Map<Long, RandomAccessAction> map = handles.get(action.getParentId());
            if (map != null) {
                map.remove(action.getFileId());
            }
        }
    }

    public RandomAccessAction getTcpAction(long pid, long fid) {
        synchronized (this) {
            Map<Long, RandomAccessAction> map = handles.get(pid);
            if (map != null) {
                return map.get(fid);
            }
            return null;
        }
    }

}
