/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.tcp;

import java.util.EventObject;

/**
 * <p>Title:Tcp客户端/服务端组件</p>
 *
 * <p>Description: tcp连接状态事件</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class ConnectInfoEvent extends EventObject {

    public static final int LOGIN_OK = 200;
    public static final int LOGIN_ERR = 403;
    public static final int CONNECT_BREAKED = 302;
    public static final int CONNECT_OK = 301;
    public static final int CONNECT_ERR = 302;
    public static final int CONNECTING = 300;
    public static final int SENDACTIVETEST = 201;
    public static final int RECDACTIVETEST = 202;
    public static final int RECEIVED = 203;
    public static final int ACCEPT_OK = 100;
    public static final int ACCEPT_ERR = 101;
    private int stateCode;
    private String description;

    /**
     * 默认构造方法
     *
     * @param source Object
     */
    public ConnectInfoEvent(Object source) {
        super(source);
    }

    /**
     * 构造方法
     *
     * @param source Object
     * @param stateCode int 状态
     * @param description String 描述0连接成功，1登陆成功...
     */
    public ConnectInfoEvent(Object source, int stateCode, String description) {
        super(source);
        this.stateCode = stateCode;
        this.description = description;
    }

    /**
     * 设置事件类型
     *
     * @param stateCode int
     */
    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    /**
     * 设置事件描述信息
     *
     * @param description String
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取事件类型
     *
     * @return int
     */
    public int getStateCode() {
        return stateCode;
    }

    /**
     * 获取事件描述信息
     *
     * @return String
     */
    public String getDescription() {
        return description;
    }
}
