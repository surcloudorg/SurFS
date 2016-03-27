/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.cfg;

/**
 * <p>Title: 配置更改监听器接口</p>
 *
 * <p>Description: 定义参数更改，呼叫方法的接口</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public interface ConfigListener {

    /**
     * 呼叫方法
     *
     * @param method Method
     * @return Object
     */
    public Object callMethod(Method method);

    /**
     * 改变属性
     *
     * @param property Property
     * @return boolean true更改成功 false 不允许更改
     */
    public boolean changeProperty(Property property);
}
