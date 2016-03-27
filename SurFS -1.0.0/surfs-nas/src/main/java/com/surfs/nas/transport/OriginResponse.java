/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.transport;

import static com.surfs.nas.GlobleProperties.charset;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class OriginResponse extends TcpResponse {

    private byte[] data;

    public OriginResponse(TcpRequest req) {
        super(req.getCommandType(), req.getSequence());
    }

    public OriginResponse(byte commandType, int sequence) {
        super(commandType, sequence);
    }

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
     * @param s String
     */
    public void setString(String s) {
        if (s != null) {
            this.data = s.getBytes(charset);
        }
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
     *
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
     * 设置实例
     *
     * @param <T>
     * @param objects
     */
    public final <T> void setObjects(T[] objects) {
        if (objects != null) {
            JSONArray arr = JSONArray.fromObject(objects);
            this.setString(arr.toString());
        }
    }

    /**
     * 获取对象
     *
     * @param <T>
     * @param type
     * @return T[]
     */
    public final <T> T[] getObjects(Class<T> type) {
        if (data == null) {
            return null;
        }
        String jsonstr = getString();
        JSONArray objs = JSONArray.fromObject(jsonstr);
        int size = objs.size();
        T[] res = (T[]) Array.newInstance(type, size);
        for (int ii = 0; ii < size; ii++) {
            Object obj = objs.get(ii);
            if (obj instanceof JSONObject) {
                JSONObject obj1 = (JSONObject) obj;
                res[ii] = (T) JSONObject.toBean(obj1, type);
            } else {
                res[ii] = (T) obj;
            }
        }
        return res;
    }

    /**
     * 设置实例
     *
     * @param <T>
     * @param objects
     */
    public final <T> void setObjects(Collection<T> objects) {
        if (objects != null) {
            JSONArray arr = JSONArray.fromObject(objects);
            this.setString(arr.toString());
        }
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
