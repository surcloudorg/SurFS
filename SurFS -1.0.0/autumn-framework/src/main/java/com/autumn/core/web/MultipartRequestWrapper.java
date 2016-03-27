/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.web;

import com.autumn.util.TextUtils;
import java.io.File;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

/**
 * Title: WEB框架
 *
 * Description: 处理多媒体请求
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
@SuppressWarnings("unchecked")
public class MultipartRequestWrapper extends QueryRequestWrapper {

    private static long DEFAULT_SIZE_MAX = 250 * 1024 * 1024;//最大允许上传250M
    private static long DEFAULT_SIZE_THRESHOLD = 256 * 1024;//>256K,保存至磁盘
    private static File repositoryPath = null;

    static {
        DEFAULT_SIZE_MAX = TextUtils.getTrueLongValue(Initializer.servletContext.getInitParameter("multipartRequestMaxSize"), 250 * 1024 * 1024);
        DEFAULT_SIZE_THRESHOLD = TextUtils.getTrueLongValue(Initializer.servletContext.getInitParameter("multipartRequestThresholdSize"), 256 * 1024);
        getRepositoryPath();
    }

    /**
     * 上传的临时目录
     */
    private static void getRepositoryPath() {
        String tmp = Initializer.servletContext.getInitParameter("multipartRequestTmpDirectory");
        if (tmp != null && (!tmp.trim().isEmpty())) {
            File f = new File(tmp.trim());
            if (f.exists() && f.isDirectory()) {
                repositoryPath = f;
                return;
            } else {
                if (f.mkdirs()) {
                    repositoryPath = f;
                    return;
                }
            }
        }
        String usepath = System.getProperty("user.dir");
        usepath = usepath == null ? "" : usepath;
        String rootdir = usepath.endsWith(File.separator) ? usepath + "tmp" : usepath + File.separator + "tmp";
        File f = new File(rootdir);
        if (!f.exists()) {
            f.mkdirs();
        }
        repositoryPath = f;
    }
    protected Map<String, Object> fileparameters;//文件
    protected List<String> fileFormNames = new ArrayList<String>();

    public MultipartRequestWrapper(HttpServletRequest request) throws ServletException {
        super(request);
        this.fileparameters = new HashMap<String, Object>();
        try {
            handleRequest(request);
        } catch (ServletException e) {
            finish();
            throw e;
        }
    }

    /**
     * 初始化
     *
     * @param request
     * @throws ServletException
     */
    private void handleRequest(HttpServletRequest request) throws ServletException {
        DiskFileItemFactory factory = new DiskFileItemFactory((int) DEFAULT_SIZE_THRESHOLD, repositoryPath);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding(request.getCharacterEncoding());
        upload.setSizeMax(DEFAULT_SIZE_MAX);
        List items = null;
        try {
            items = upload.parseRequest(new ServletRequestContext(request));
        } catch (FileUploadException e) {
            throw new ServletException(e);
        }
        for (Object obj : items) {
            FileItem item = (FileItem) obj;
            if (item.isFormField()) {
                addTextParameter(request, item);
            } else {
                addFileParameter(item);
            }
        }
    }

    /**
     * 解析字符串参数
     *
     * @param request
     * @param item
     */
    private void addTextParameter(HttpServletRequest request, FileItem item) {
        String name = item.getFieldName();
        String value = null;
        String encoding = null;
        if (item instanceof FileItem) {
            encoding = ((DiskFileItem) item).getCharSet();
        }
        if (encoding == null) {
            encoding = request.getCharacterEncoding();
        }
        try {
            value = encoding == null ? item.getString() : item.getString(encoding);
        } catch (Exception e) {
        }
        setParameter(name, value == null ? "" : value);
    }

    /**
     * 加参数
     *
     * @param name
     * @param value
     */
    private void setParameter(String name, String value) {
        if (!parameterNames.contains(name)) {
            parameterNames.add(name);
        }
        String key = name.toLowerCase();
        Object mValue = parameters.get(key);
        if (mValue == null) {
            parameters.put(key, value);
        } else {
            if (mValue instanceof List) {
                List l = (List) mValue;
                l.add(value);
            } else {
                List l = new ArrayList();
                l.add(mValue);
                l.add(value);
                parameters.put(key, l);
            }
        }
    }

    /**
     * 解析文件参数
     *
     * @param request
     * @param item
     */
    private void addFileParameter(FileItem item) {
        if (!fileFormNames.contains(item.getFieldName())) {
            fileFormNames.add(item.getFieldName());
        }
        String name = item.getFieldName().toLowerCase();
        Object o = fileparameters.get(name);
        if (o == null) {
            fileparameters.put(name, item);
        } else {
            if (o instanceof List) {
                List l = (List) o;
                l.add(item);
            } else {
                List l = new ArrayList();
                l.add(o);
                l.add(item);
                fileparameters.put(name, l);
            }
        }
    }

    /**
     * 取文件
     *
     * @param name
     * @return FileField
     */
    public FileItem getFileForm(String name) {
        Object mValue = fileparameters.get(name.toLowerCase());
        if (mValue == null) {
            return null;
        }
        if (mValue instanceof List) {
            List l = (List) mValue;
            if (l.isEmpty()) {
                return null;
            } else {
                return (FileItem) l.get(0);
            }
        } else {
            return (FileItem) mValue;
        }
    }

    /**
     * 文件
     *
     * @param name
     * @return FileField[]
     */
    public FileItem[] getFileForms(String name) {
        Object mValue = fileparameters.get(name.toLowerCase());
        if (mValue == null) {
            return null;
        }
        if (mValue instanceof List) {
            List l = (List) mValue;
            return (FileItem[]) l.toArray(new FileItem[l.size()]);
        } else {
            FileItem[] s = new FileItem[1];
            s[0] = (FileItem) mValue;
            return s;
        }
    }

    /**
     * 文件名
     *
     * @return Enumeration<String>
     */
    public Enumeration<String> getFileFormNames() {
        return Collections.enumeration(fileFormNames);
    }

    public Map<String, FileItem[]> getFileItemMap() {
        Map map = new HashMap<String, FileItem[]>();
        for (String s : fileFormNames) {
            map.put(s, this.getFileForms(s));
        }
        return map;
    }

    /**
     * 清除
     */
    public final void finish() {
        Iterator iter = fileparameters.values().iterator();
        Object o;
        while (iter.hasNext()) {
            o = iter.next();
            if (o instanceof List) {
                for (Iterator i = ((List) o).iterator(); i.hasNext();) {
                    ((FileItem) i.next()).delete();
                }
            } else {
                ((FileItem) o).delete();
            }
        }
    }
}
