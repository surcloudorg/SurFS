package com.autumn.core.svn;

import com.autumn.util.IOUtils;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: SVN操作信息</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class ActionMessage implements Serializable {

    /**
     * ActionMessage-->String
     *
     * @param message
     * @return String
     * @throws IOException
     */
    public static String toString(ActionMessage message) throws IOException {
        return IOUtils.objectToString(message);
    }

    /**
     * String-->ActionMessage
     *
     * @param message
     * @return ActionMessage
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static ActionMessage parse(String message) throws IOException,
            ClassNotFoundException {
        return (ActionMessage) IOUtils.stringToObject(message);
    }
    private static final long serialVersionUID = 20100908121253435L;
    private String message = null;//消息
    private List<String> modified = null;//本地更改的文件
    private List<String> updated = null;//服务器更改的文件

    public ActionMessage() {
    }

    /**
     * 添加本地更改的文件
     *
     * @param path
     */
    public void addModified(String path) {
        if (modified == null) {
            modified = new ArrayList<String>();
        }
        modified.add(path);
    }

    /**
     * 服务器更改的文件数目
     *
     * @return int
     */
    public int getUpdatedSize() {
        if (updated == null) {
            return 0;
        } else {
            return updated.size();
        }
    }

    /**
     * 本地更改的文件数目
     *
     * @return int
     */
    public int getModifiedSize() {
        if (modified == null) {
            return 0;
        } else {
            return modified.size();
        }
    }

    /**
     * 移除指定的本地更改文件
     *
     * @param path
     */
    public void removeModified(String path) {
        if (modified != null) {
            modified.remove(path);
        }
    }

    /**
     * 移除本地更改文件
     */
    public void clearModified() {
        if (modified != null) {
            modified = null;
        }
    }

    /**
     * 添加服务器更改的文件
     *
     * @param path
     */
    public void addUpdated(String path) {
        if (updated == null) {
            updated = new ArrayList<String>();
        }
        updated.add(path);
    }

    /**
     * 移除指定的服务器更改的文件
     *
     * @param path
     */
    public void removeUpdated(String path) {
        if (updated != null) {
            updated.remove(path);
        }
    }

    /**
     * 移除服务器更改的文件
     */
    public void clearUpdated() {
        if (updated != null) {
            updated = null;
        }
    }

    /**
     * @return 本地更改的文件
     */
    public List<String> getModified() {
        return modified;
    }

    /**
     * @return 服务器更改的文件
     */
    public List<String> getUpdated() {
        return updated;
    }

    /**
     * @return 操作消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param modified 本地更改的文件
     */
    public void setModified(List<String> modified) {
        this.modified = modified;
    }

    /**
     * @param updated 服务器更改的文件
     */
    public void setUpdated(List<String> updated) {
        this.updated = updated;
    }

    /**
     * @param message 操作消息
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
