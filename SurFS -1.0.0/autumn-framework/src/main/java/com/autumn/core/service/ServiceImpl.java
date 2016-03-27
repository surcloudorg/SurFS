/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.service;

/**
 * <p>Title: 服务运行接口</p>
 *
 * <p>Description: start接口用来启动服务，stop接口关闭服务</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public interface ServiceImpl {

    /**
     * 启动服务
     *
     * @throws Exception 如果启动失败，框架将不会执行stop函数
     */
    public void start() throws Exception;

    /**
     * 关闭服务，在这里必须等待本服务启动的所有线程实例全部关闭完后返回
     */
    public void stop();
}
