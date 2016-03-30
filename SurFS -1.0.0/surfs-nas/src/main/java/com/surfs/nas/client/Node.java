/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.client;

import com.surfs.nas.GlobleProperties;
import com.surfs.nas.NodeProperties;
import com.surfs.nas.transport.TcpClient;
import com.surfs.nas.transport.ThreadPool;

public final class Node {

    private boolean ready = true;
    private boolean diskfull = false;
    GlobleProperties globleProperties = null;
    NodeProperties nodeProperties = null;

    TcpClient tcpclient;
    TestSpeedHandler testSpeedHandler = null;
    NodeChargeGetter chargeGetter = null;

    Node() {
    }

    public Node(GlobleProperties globleProperties, NodeProperties nodeProperties) {
        this.setNodeProperties(nodeProperties);
        this.setGlobleProperties(globleProperties);
    }

    /**
     *
     * @param diskfull
     */
    public synchronized void setDiskFull(boolean diskfull) {
        if (diskfull) {
            lastFullTime = System.currentTimeMillis();
        }
        this.diskfull = diskfull;
    }

    /**
     *
     * @param ready
     */
    public synchronized void setState(boolean ready) {
        if (!ready) {
            lastErrTime = System.currentTimeMillis();
        }
        this.ready = ready;
    }

    /**
     * @param globleProperties
     */
    void setGlobleProperties(GlobleProperties globleProperties) {
        if (this.globleProperties == null || this.globleProperties.getBalanceRule() != globleProperties.getBalanceRule()) {
            this.globleProperties = globleProperties;
            if (GlobleProperties.BalanceRule_NEAR == globleProperties.getBalanceRule()) {
                ThreadPool.stopThread(chargeGetter);
                chargeGetter = null;
                if (testSpeedHandler == null) {
                    testSpeedHandler = new TestSpeedHandler(this);
                    testSpeedHandler.restart();
                }
            } else if (GlobleProperties.BalanceRule_DYNAMIC == globleProperties.getBalanceRule()) {
                if (testSpeedHandler != null) {
                    testSpeedHandler.terminate();
                    testSpeedHandler = null;
                }
                if (chargeGetter == null) {
                    chargeGetter = new NodeChargeGetter(this);
                    chargeGetter.start();
                }
            } else {
                ThreadPool.stopThread(chargeGetter);
                chargeGetter = null;
                if (testSpeedHandler != null) {
                    testSpeedHandler.terminate();
                    testSpeedHandler = null;
                }
            }
        }
        this.globleProperties = globleProperties;
    }

    /**
     * @return the globleProperties
     */
    public GlobleProperties getGlobleProperties() {
        return globleProperties;
    }

    /**
     * @return the nodeProperties
     */
    public NodeProperties getNodeProperties() {
        return nodeProperties;
    }

    /**
     * @param nodeProperties the nodeProperties to set
     */
    void setNodeProperties(NodeProperties nodeProperties) {
        if (this.nodeProperties == null) {
            this.tcpclient = new TcpClient(this);
            this.nodeProperties = nodeProperties;
        } else {
            if (this.nodeProperties.getPort() != nodeProperties.getPort()) {
                this.nodeProperties = nodeProperties;
                this.tcpclient.destory();
                this.tcpclient = new TcpClient(this);
            } else {
                this.nodeProperties = nodeProperties;
            }
        }
    }

    /**
     * 关闭线程
     */
    public void destroy() {
        tcpclient.destory();
        if (testSpeedHandler != null) {
            testSpeedHandler.terminate();
        }
        ThreadPool.stopThread(chargeGetter);
    }

    long lastErrTime;
    long lastFullTime;


    private boolean checkFullTime() {
        if (diskfull) {
            if (System.currentTimeMillis() - lastFullTime > globleProperties.getErrRetryInterval() * 1000 * 20) {
                setDiskFull(false);
            }
        }
        return diskfull;
    }

    /**
     * @return the ready
     */
    public boolean isReady() {
        if (!ready) {
            if (System.currentTimeMillis() - lastErrTime > globleProperties.getErrRetryInterval() * 1000) {
                setState(true);
            }
        }
        return ready && (checkFullTime() == false);
    }
}
