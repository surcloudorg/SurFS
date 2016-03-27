/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.jms;

import java.io.Serializable;
import java.util.HashMap;

/**
 * <p>Title: 处理失败的数据模型</p>
 *
 * <p>Description: 包含处理失败时间，处理时临时数据</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p> extended message
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class MessageWrapper<V> implements Serializable {

    private static final long serialVersionUID = 201207201039554533L;
    protected long lastHT = 0;//最后一次处理失败的时间
    private HashMap<String, Object> additiveAttribute = new HashMap<String, Object>();
    private V message = null;

    public MessageWrapper(V message) {
        this.message = message;
    }

    /**
     * 获取附加属性值
     *
     * @param key
     * @return Object
     */
    public Object getAdditiveAttribute(String key) {
        return additiveAttribute.get(key);
    }


    /**
     * 添加一个属性值
     *
     * @param key
     * @param value
     * @return Object
     */
    public Object putAdditiveAttribute(String key, Object value) {
        return additiveAttribute.put(key, value);
    }

    /**
     * 移除一个属性值
     *
     * @param key
     * @return Object
     */
    public Object removeAdditiveAttribute(String key) {
        return additiveAttribute.remove(key);
    }

    /**
     * 清楚属性
     */
    public void clearAdditiveAttribute() {
        additiveAttribute.clear();
    }

    /**
     * @return the message
     */
    public V getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(V message) {
        this.message = message;
    }
}
