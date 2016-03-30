/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas;

import java.io.IOException;

public interface ResourcesAccessor {

    public static final String TABLE_VOLUME = "VolumeRegistry";
    public static final String TABLE_NODE = "NodeRegistry";
    public static final String TABLE_SERVICE = "GlobleProperties";

    /**
     *
     * @param volumeProperties
     * @throws IOException
     */
    public void putVolumeProperties(VolumeProperties volumeProperties) throws IOException;

    /**
     *
     * @param volumeID
     * @return VolumeProperties
     * @throws IOException
     */
    public VolumeProperties getVolumeProperties(String volumeID) throws IOException;

    /**
     *
     * @param volumeID
     * @throws IOException
     */
    public void deleteVolumeProperties(String volumeID) throws IOException;

    /**
     *
     * @return VolumeProperties[]
     * @throws IOException
     */
    public VolumeProperties[] listVolumeProperties() throws IOException;

    /**
     *
     * @param nodeProperties
     * @throws IOException
     */
    public void putNodeProperties(NodeProperties nodeProperties) throws IOException;

    /**
     *
     * @param serverip
     * @return NodeProperties
     * @throws IOException
     */
    public NodeProperties getNodeProperties(String serverip) throws IOException;

    /**
     *
     * @param serverip
     * @throws IOException
     */
    public void deleteNodeProperties(String serverip) throws IOException;

    /**
     *
     * @return NodeProperties[]
     * @throws IOException
     */
    public NodeProperties[] listNodeProperties() throws IOException;

    /**
     *
     * @param globleProperties
     * @throws IOException
     */
    public void putGlobleProperties(GlobleProperties globleProperties) throws IOException;

    /**
     *
     * @return GlobleProperties
     * @throws IOException
     */
    public GlobleProperties getGlobleProperties() throws IOException;

    /**
     *
     * @throws IOException
     */
    public void updateServerSourceVersion() throws IOException;

    /**
     *
     * @throws IOException
     */
    public void updateClientSourceVersion() throws IOException;

    /**
     *
     * @return String
     * @throws IOException
     */
    public String getServerSourceVersion() throws IOException;

    /**
     *
     * @return String
     * @throws IOException
     */
    public String getClientSourceVersion() throws IOException;
}
