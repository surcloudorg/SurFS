/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * <p>Title: 定制ObjectInputStream</p>
 *
 * <p>Description: 在Object凡序列化过程中，需要指定classloader</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class ObjectInputStreamWCL extends ObjectInputStream {

    protected ClassLoader classLoader = this.getClass().getClassLoader();

    /**
     * @param in
     * @throws IOException
     */
    public ObjectInputStreamWCL(InputStream in) throws IOException {
        super(in);
    }

    /**
     * 指定classloader
     *
     * @param in
     * @param cl
     * @throws IOException
     */
    public ObjectInputStreamWCL(InputStream in, ClassLoader cl) throws IOException {
        super(in);
        this.classLoader = cl;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();
        try {
            return Class.forName(name, false, this.classLoader);
        } catch (ClassNotFoundException ex) {
            return super.resolveClass(desc);
        }
    }
}
