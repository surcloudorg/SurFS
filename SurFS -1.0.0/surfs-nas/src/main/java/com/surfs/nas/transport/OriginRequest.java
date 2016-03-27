/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.transport;

import static com.surfs.nas.GlobleProperties.charset;
import java.io.IOException;
import net.sf.json.JSONObject;

public abstract class OriginRequest extends TcpRequest {

    public OriginRequest(byte commandType) {
        super(commandType);
    }

    public OriginRequest(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    protected byte[] data;

    @Override
    protected void read(TcpCommandDecoder m_in) throws IOException {
        this.data = m_in.readBytes();
    }

    @Override
    protected void write(TcpCommandEncoder m_out) throws IOException {
        m_out.writeBytes(data);
    }

    /**
     *
     * @return the content
     */
    public final String getString() {
        if (data == null) {
            return null;
        } else {
            return new String(data, charset);
        }
    }

    /**
     * @param s the data to set
     */
    public final void setString(String s) {
        if (s != null) {
            this.data = s.getBytes(charset);
        }
    }

    /**
     *
     * @param <T>
     * @param object
     */
    public final <T> void setObject(T object) {
        if (object != null) {
            JSONObject obj = JSONObject.fromObject(object);
            this.setString(obj.toString());
        }
    }

    /**
     * @param <T>
     * @param type
     * @return T
     */
    public final <T> T getObject(Class<T> type) {
        if (data == null) {
            return null;
        }
        String jsonstr = getString();
        JSONObject obj = JSONObject.fromObject(jsonstr);
        Object object = JSONObject.toBean(obj, type);
        return (T) object;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(byte[] data) {
        this.data = data;
    }
}
