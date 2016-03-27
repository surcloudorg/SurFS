/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.svn;

import java.io.File;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;

/**
 * <p>Title: SVNAPI初始化</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class Setup {

    private static boolean isSetup = false;

    public static void setupLibrary() {
        if (!isSetup) {
            DAVRepositoryFactory.setup();
            SVNRepositoryFactoryImpl.setup();
            FSRepositoryFactory.setup();
            isSetup = true;
        }
    }
    private static String localPath = null;

    public static void setLocalPath(String path) {
        if (path == null || path.trim().equals("")) {
            return;
        }
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        localPath = path + "svn" + File.separator;
    }

    public static String getLocalPath() {
        if (localPath == null) {
            String dir = System.getProperty("user.dir");
            if (dir == null) {
                dir = "";
            }
            if (!dir.endsWith(File.separator)) {
                dir = dir + File.separator;
            }
            return dir + "svn" + File.separator;
        } else {
            return localPath;
        }
    }
}
