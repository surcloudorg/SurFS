/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.console;

/**
 * <p>
 * Title: 框架控制台</p>
 *
 * <p>
 * Description: 文件操作-文件属性类</p>
 *
 * <p>
 * Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>
 * Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class FileProp implements Comparable {

    private String filename = "";//文件名
    private String path = "";//路径
    private boolean dir = false;//是否目录
    private String size = "";//大小
    private String lastmodify = "";//最后修改时间

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the dir
     */
    public boolean isDir() {
        return dir;
    }

    /**
     * @param dir the dir to set
     */
    public void setDir(boolean dir) {
        this.dir = dir;
    }

    /**
     * @return the size
     */
    public String getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * @return the lastmodify
     */
    public String getLastmodify() {
        return lastmodify;
    }

    /**
     * @param lastmodify the lastmodify to set
     */
    public void setLastmodify(String lastmodify) {
        this.lastmodify = lastmodify;
    }

    @Override
    public int compareTo(Object o) {
        FileProp p = (FileProp) o;
        String p1 = (p.isDir() ? 0 : 1) + p.filename;
        String p2 = (this.isDir() ? 0 : 1) + this.filename;
        return p2.compareToIgnoreCase(p1);
    }
}
