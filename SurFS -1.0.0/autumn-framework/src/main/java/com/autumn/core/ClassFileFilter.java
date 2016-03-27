/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title: 文件过滤器</p>
 *
 * <p>Description: 文件过滤器，遍历目录中的文件</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class ClassFileFilter implements FileFilter {

    private HashMap<String, Pattern> filters = null;
    private boolean showDir = true;

    /**
     * 构造函数
     */
    public ClassFileFilter() {
        this(true);
    }

    /**
     * 构造函数
     *
     * @param showDir boolean true显示目录
     */
    public ClassFileFilter(boolean showDir) {
        this.showDir = showDir;
    }

    /**
     * 添加过滤规则，如：*.txt
     *
     * @param filter String
     */
    public void addFilter(String filter) {
        if (filters == null) {
            filters = new HashMap<String, Pattern>();
        }
        filter = filter.replace('?', '"');
        filter = filter.replaceAll("\"", "[^?]");
        filter = filter.replace('*', '"');
        filter = filter.replaceAll("\"", "[^?]+");
        Pattern myRE = Pattern.compile(filter);
        filters.put(filter, myRE);
    }

    /**
     * 清除过滤
     *
     * @param filter String
     */
    public void clearFilter(String filter) {
        if (filters != null) {
            filters.clear();
        }
    }

    /**
     * 移除一个过滤规则
     *
     * @param filter String
     */
    public void removeFilter(String filter) {
        if (filters != null) {
            filters.remove(filter);
        }
    }

    /**
     * 执行过滤
     *
     * @param file File
     * @return boolean
     */
    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            if (file.getName().equalsIgnoreCase("CVS")
                    || file.getName().equalsIgnoreCase("CVSBase")) {
                return false;
            } else {
                return showDir;
            }
        } else {
            if (filters == null) {
                return true;
            }
            String filename = file.getName();
            Collection<Pattern> en = filters.values();
            for (Pattern myRE : en) {
                Matcher matcher = myRE.matcher(filename);
                String str = matcher.replaceAll("");
                if (str.equals("")) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 所有过滤规则
     *
     * @return Hashtable
     */
    public HashMap<String, Pattern> getFilters() {
        return filters;
    }

    /**
     * 是否显示目录
     *
     * @return boolean
     */
    public boolean isShowDir() {
        return showDir;
    }

    /**
     * 设置是否显示目录参数
     *
     * @param showDir boolean
     */
    public void setShowDir(boolean showDir) {
        this.showDir = showDir;
    }

    /**
     * 设置过滤规则
     *
     * @param filters Hashtable
     */
    public void setFilters(HashMap<String, Pattern> filters) {
        this.filters = filters;
    }
}
