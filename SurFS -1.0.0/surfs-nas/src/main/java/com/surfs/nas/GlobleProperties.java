/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas;

import java.io.Serializable;
import java.nio.charset.Charset;
import net.sf.json.JSONObject;

public class GlobleProperties implements Serializable {

    private static final long serialVersionUID = 20141113153000L;
    public static final Charset charset = Charset.forName("utf-8");

    public static final String key_globle_config = "GLOBLE_CONFIG";
    public static final String key_server_config_version = "SERVER_CONFIG_VERSION";
    public static final String key_client_config_version = "CLIENT_CONFIG_VERSION";

    public static final int BalanceRule_NEAR = 0;
    public static final int BalanceRule_LOOP = 1;
    public static final int BalanceRule_DYNAMIC = 2;

    private int blocksize = 128;
    private int errRetryInterval = 15;
    private int errRetryTimes = 3;
    private int writeQueue = 8;
    private int readTimeout = 15;
    private int sessionTimeout = 5;
    private int connectTimeout = 15;
    private String mntPoint = "/surfs/";
    private int balanceRule = 1;
    private int reloadInterval = 5;
    private int checkSpaceInterval = 10;
    private long spaceThresholdSize = 20l;
    private int usecache = 0;

    @Override
    public String toString() {
        JSONObject obj = JSONObject.fromObject(this);
        return obj.toString();
    }

    /**
     * @return the blocksize
     */
    public int getBlocksize() {
        return blocksize;
    }

    /**
     * 8K-1M
     *
     * @param blocksize the blocksize to set
     */
    public void setBlocksize(int blocksize) {
        this.blocksize = blocksize > 1024 ? 1024
                : (blocksize < 8 ? 8 : blocksize);
    }

    /**
     * @return the errRetryInterval
     */
    public int getErrRetryInterval() {
        return errRetryInterval;
    }

    /**
     * 15-180s
     *
     * @param errRetryInterval the errRetryInterval to set
     */
    public void setErrRetryInterval(int errRetryInterval) {
        this.errRetryInterval = errRetryInterval > 180 ? 180
                : (errRetryInterval < 15 ? 15 : errRetryInterval);
    }

    /**
     * @return the errRetryTimes
     */
    public int getErrRetryTimes() {
        return errRetryTimes;
    }

    /**
     * 2-5
     *
     * @param errRetryTimes the errRetryTimes to set
     */
    public void setErrRetryTimes(int errRetryTimes) {
        this.errRetryTimes = errRetryTimes > 8 ? 8
                : (errRetryTimes < 3 ? 3 : errRetryTimes);
    }

    /**
     * @return the readTimeout
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * 5-60s
     *
     * @param readTimeout the readTimeout to set
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout > 60 ? 60
                : (readTimeout < 5 ? 5 : readTimeout);
    }

    /**
     * @return the connectTimeout
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 5-60s
     *
     * @param connectTimeout the connectTimeout to set
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout > 60 ? 60
                : (connectTimeout < 5 ? 5 : connectTimeout);
    }

    /**
     * @return the mntPoint
     */
    public String getMntPoint() {
        return mntPoint;
    }

    /**
     *
     * @param mntPoint the mntPoint to set
     */
    public void setMntPoint(String mntPoint) {
        this.mntPoint = mntPoint;
    }

    /**
     * @return the balanceRule
     */
    public int getBalanceRule() {
        return balanceRule;
    }

    /**
     * 0,1,2
     *
     * @param balanceRule the balanceRule to set
     */
    public void setBalanceRule(int balanceRule) {
        this.balanceRule = balanceRule > 2 || balanceRule < 0 ? 1 : balanceRule;
    }

    /**
     * @return the reloadInterval
     */
    public int getReloadInterval() {
        return reloadInterval;
    }

    /**
     * 3-30s
     *
     * @param reloadInterval the reloadInterval to set
     */
    public void setReloadInterval(int reloadInterval) {
        this.reloadInterval = reloadInterval > 30 ? 30
                : (reloadInterval < 3 ? 3 : reloadInterval);
    }

    /**
     * @return the checkSpaceInterval
     */
    public int getCheckSpaceInterval() {
        return checkSpaceInterval;
    }

    /**
     * 5-60s
     *
     * @param checkSpaceInterval the checkSpaceInterval to set
     */
    public void setCheckSpaceInterval(int checkSpaceInterval) {
        this.checkSpaceInterval = checkSpaceInterval > 60 ? 60
                : (checkSpaceInterval < 5 ? 5 : checkSpaceInterval);
    }

    /**
     * @return the spaceThresholdSize
     */
    public long getSpaceThresholdSize() {
        return spaceThresholdSize;
    }

    /**
     * 0-1T
     *
     * @param spaceThresholdSize the spaceThresholdSize to set
     */
    public void setSpaceThresholdSize(long spaceThresholdSize) {
        this.spaceThresholdSize = spaceThresholdSize > 1024l ? 1024l
                : (spaceThresholdSize < 0 ? 0 : spaceThresholdSize);
    }

    /**
     * @return the usecache
     */
    public int getUsecache() {
        return usecache;
    }

    /**
     * @param usecache the usecache to set
     */
    public void setUsecache(int usecache) {
        this.usecache = usecache > 2 || usecache < 0 ? 0 : usecache;
    }

    /**
     * @return the sessionTimeout
     */
    public int getSessionTimeout() {
        return sessionTimeout;
    }

    /**
     * @param sessionTimeout 3-30s
     */
    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout > 30 ? 30
                : (sessionTimeout < 3 ? 3 : sessionTimeout);
    }

    /**
     * @return the writeQueue
     */
    public int getWriteQueue() {
        return writeQueue;
    }

    /**
     * (0-5)
     *
     * @param writeQueue the writeQueue to set
     */
    public void setWriteQueue(int writeQueue) {
        this.writeQueue = writeQueue > 20 ? 20 : (writeQueue < 0 ? 0 : writeQueue);
    }
}
