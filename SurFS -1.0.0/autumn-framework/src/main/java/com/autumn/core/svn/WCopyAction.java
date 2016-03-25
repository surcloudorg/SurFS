package com.autumn.core.svn;

import com.autumn.core.log.LogFactory;
import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.JdbcUtils;
import com.autumn.util.TextUtils;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * <p>Title: SVN工作拷贝</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class WCopyAction implements Runnable {

    public static final int CHECK = 0;
    public static final int UPDATE = 1;
    public static final int COMMIT = 2;
    public static final int ROLLBACK = 3;
    private String wcopyId = "0";//唯一工作区标识
    private int actionType = 0;
    private String repositoryUrl = "";
    private String userName;
    private String passWord;
    private String workingCopyPath = "";
    private String[] modified = null;
    private String[] updated = null;
    private ActionMessage msg = null;

    public WCopyAction(String wcopyId) {
        this.wcopyId = wcopyId;
    }

    public static void clearMessage(Connection con, String id) {
        try {
            String sql = "update svncodes set message=? where id=" + id;
            PreparedStatement prest = con.prepareStatement(sql);
            prest.setString(1, "");
            prest.executeUpdate();
        } catch (Exception e) {
        }
    }

    /**
     * 执行状态
     *
     * @return String
     */
    public String actionTypeString() {
        switch (actionType) {
            case 0:
                return "正在检查更新...";
            case 1:
                return "正在更新...";
            case 2:
                return "正在提交...";
            case 3:
                return "正在还原...";
        }
        return "正在检查更新...";
    }

    private void updateRecord() {
        Connection con = null;
        try {
            if (msg.getModified() != null) {
                Collections.sort(msg.getModified());
            }
            if (msg.getUpdated() != null) {
                Collections.sort(msg.getUpdated());
            }
            String ss = ActionMessage.toString(msg);
            if (msg.getMessage() == null && msg.getModified() == null
                    && msg.getUpdated() == null) {
                ss = "";
            }
            con = ConnectionFactory.getConnect(this.getClass());
            String sql = "update svncodes set message=? where id="
                    + wcopyId;
            PreparedStatement prest = con.prepareStatement(sql);
            prest.setString(1, ss);
            prest.executeUpdate();
        } catch (Exception e) {
            LogFactory.error("更新数据库message字段错误:" + e.getMessage(),
                    this.getClass());
        }
        JdbcUtils.closeConnect(con);
    }

    @Override
    public void run() {
        switch (actionType) {
            case 0:
                check();
                break;
            case 1:
                update();
                break;
            case 2:
                commit();
                break;
            case 3:
                rollback();
                break;
        }
        updateRecord();
        WCopyActionMgr.remove(this);
    }

    /**
     * checkout
     */
    public void checkout() {
        try {
            setMsg(new ActionMessage());
            WorkingCopy workingcopy = new WorkingCopy();
            workingcopy.setRepositoryUrl(this.getRepositoryUrl());
            workingcopy.setUserName(this.getUserName());
            workingcopy.setPassWord(this.getPassWord());
            workingcopy.setWorkingCopyPath(this.getWorkingCopyPath());
            workingcopy.checkout();
        } catch (Exception e) {
            String ss = TextUtils.Date2String(new java.util.Date(), "yyyy-MM-dd HH:mm") + ",执行导入错误:" + e.getMessage();
            getMsg().setMessage(ss);
        }
    }

    /**
     * 检查更新
     */
    public void check() {
        this.setActionType(CHECK);
        File file = new File(this.getWorkingCopyPath());
        if (!file.exists()) {
            checkout();
            return;
        }
        try {
            setMsg(new ActionMessage());
            WorkingCopy workingcopy = new WorkingCopy();
            workingcopy.setRepositoryUrl(this.getRepositoryUrl());
            workingcopy.setUserName(this.getUserName());
            workingcopy.setPassWord(this.getPassWord());
            workingcopy.setWorkingCopyPath(this.getWorkingCopyPath());
            StatusHandler sh = workingcopy.showStatus();
            List<Properties> infos = sh.getStatusList();
            for (Properties info : infos) {
                String pathChangeType = info.getProperty("pathChangeType");
                String remoteChangeType = info.getProperty("remoteChangeType");
                if (remoteChangeType != null) {
                    if (remoteChangeType.equals("M")) {
                        getMsg().addUpdated("[修改]" + info.getProperty("filename"));
                    } else if (remoteChangeType.equals("D")) { //本地被删除
                        getMsg().addUpdated("[删除]" + info.getProperty("filename"));
                    } else if (remoteChangeType.equals("A")) { //本地被删除
                        getMsg().addUpdated("[创建]" + info.getProperty("filename"));
                    } else {
                        getMsg().addUpdated("[修改]" + info.getProperty("filename"));
                    }
                } else {
                    if (pathChangeType.equals("M")) {
                        getMsg().addModified("[修改]" + info.getProperty("filename"));
                    } else if (pathChangeType.equals("!")
                            || pathChangeType.equals("D")) { //本地被删除
                        getMsg().addModified("[删除]" + info.getProperty("filename"));
                    } else if (pathChangeType.equals("?")
                            || pathChangeType.equals("A")) { //本地被删除
                        getMsg().addModified("[创建]" + info.getProperty("filename"));
                    } else {
                        getMsg().addModified("[修改]" + info.getProperty("filename"));
                    }
                }

            }
            if (getMsg().getModified() != null || getMsg().getUpdated() != null) {
                String ss = TextUtils.Date2String(new java.util.Date(),
                        "yyyy-MM-dd HH:mm");
                ss = ss + ",检查到本地" + getMsg().getModifiedSize() + "个文件被改动！";
                ss = ss + "服务器" + getMsg().getUpdatedSize() + "个文件被改动！";
                getMsg().setMessage(ss);
            }
        } catch (Exception e) {
            String ss = TextUtils.Date2String(new java.util.Date(), "yyyy-MM-dd HH:mm") + ",执行检查更新出现错误:" + e.getMessage();
            getMsg().setMessage(ss);
            getMsg().setModified(null);
            getMsg().setUpdated(null);
        }
    }

    /**
     * 更新
     */
    public void update() {
        try {
            WorkingCopy workingcopy = new WorkingCopy();
            workingcopy.setRepositoryUrl(this.getRepositoryUrl());
            workingcopy.setUserName(this.getUserName());
            workingcopy.setPassWord(this.getPassWord());
            workingcopy.setWorkingCopyPath(this.getWorkingCopyPath());
            if (getUpdated() == null) {
                for (String path : getMsg().getUpdated()) {
                    int index = path.indexOf("]");
                    String name = path.substring(index + 1);
                    File f = new File(name);
                    if (f.exists() && f.isFile()) {
                        f.delete();
                    }
                }
                workingcopy.update();
            } else {
                List<String> list = new ArrayList<String>();
                for (String ss : getUpdated()) {
                    if (getMsg().getUpdated().contains(ss)) {
                        list.add(ss);
                    }
                }
                int count = list.size();
                File[] fs = new File[count];
                for (int ii = 0; ii < count; ii++) {
                    String path = list.get(ii);
                    int index = path.indexOf("]");
                    String name = path.substring(index + 1);
                    File f = new File(name);
                    if (f.exists() && f.isFile()) {
                        f.delete();
                    }
                    fs[ii] = f;
                }
                workingcopy.update(fs);
            }
        } catch (Exception e) {
        }
        check();
    }

    /**
     * 提交
     */
    public void commit() {
        try {
            WorkingCopy workingcopy = new WorkingCopy();
            workingcopy.setRepositoryUrl(this.getRepositoryUrl());
            workingcopy.setUserName(this.getUserName());
            workingcopy.setPassWord(this.getPassWord());
            workingcopy.setWorkingCopyPath(this.getWorkingCopyPath());
            if (getModified() == null) {
                for (String path : getMsg().getModified()) {
                    String filename = path.substring(4);
                    try {
                        if (path.startsWith("[创建]")) {
                            workingcopy.addEntry(filename);
                        }
                        if (path.startsWith("[删除]")) {
                            workingcopy.delete(filename);
                        }
                    } catch (Exception e) {
                    }
                }
                workingcopy.commit();
            } else {
                List<String> list = new ArrayList<String>();
                for (String ss : getModified()) {
                    if (getMsg().getModified().contains(ss)) {
                        list.add(ss);
                    }
                }
                int count = list.size();
                File[] fs = new File[count];
                for (int ii = 0; ii < count; ii++) {
                    String path = list.get(ii);
                    String filename = path.substring(4);
                    File f = new File(filename);
                    try {
                        if (path.startsWith("[创建]")) {
                            workingcopy.addEntry(filename);
                        }
                        if (path.startsWith("[删除]")) {
                            workingcopy.delete(filename);
                        }
                    } catch (Exception e) {
                    }
                    fs[ii] = f;
                }
                workingcopy.commit(fs);
            }
        } catch (Exception e) {
        }
        check();
    }

    /**
     * 恢复本地更改
     */
    public void rollback() {
        try {
            WorkingCopy workingcopy = new WorkingCopy();
            workingcopy.setRepositoryUrl(this.getRepositoryUrl());
            workingcopy.setUserName(this.getUserName());
            workingcopy.setPassWord(this.getPassWord());
            workingcopy.setWorkingCopyPath(this.getWorkingCopyPath());
            if (getModified() == null) {
                for (String path : getMsg().getModified()) {
                    int index = path.indexOf("]");
                    String name = path.substring(index + 1);
                    File f = new File(name);
                    if (f.exists() && f.isFile()) {
                        f.delete();
                    }
                }
                workingcopy.update();
            } else {
                List<String> list = new ArrayList<String>();
                for (String ss : getModified()) {
                    if (getMsg().getModified().contains(ss)) {
                        list.add(ss);
                    }
                }
                int count = list.size();
                File[] fs = new File[count];
                for (int ii = 0; ii < count; ii++) {
                    String path = list.get(ii);
                    int index = path.indexOf("]");
                    String name = path.substring(index + 1);
                    File f = new File(name);
                    if (f.exists()) {
                        f.delete();
                    }
                    fs[ii] = f;
                }
                workingcopy.update(fs);
            }
        } catch (Exception e) {
        }
        check();
    }

    /**
     * @return the actionType
     */
    public int getActionType() {
        return actionType;
    }

    /**
     * @param actionType the actionType to set
     */
    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    /**
     * @return the repositoryUrl
     */
    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    /**
     * @param repositoryUrl the repositoryUrl to set
     */
    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the passWord
     */
    public String getPassWord() {
        return passWord;
    }

    /**
     * @param passWord the passWord to set
     */
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    /**
     * @return the workingCopyPath
     */
    public String getWorkingCopyPath() {
        return workingCopyPath;
    }

    /**
     * @param workingCopyPath the workingCopyPath to set
     */
    public void setWorkingCopyPath(String workingCopyPath) {
        this.workingCopyPath = workingCopyPath;
    }

    /**
     * @return the modified
     */
    public String[] getModified() {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(String[] modified) {
        this.modified = modified;
    }

    /**
     * @return the updated
     */
    public String[] getUpdated() {
        return updated;
    }

    /**
     * @param updated the updated to set
     */
    public void setUpdated(String[] updated) {
        this.updated = updated;
    }

    /**
     * @return the msg
     */
    public ActionMessage getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(ActionMessage msg) {
        this.msg = msg;
    }

    /**
     * @return the wcopyId
     */
    public String getWcopyId() {
        return wcopyId;
    }
}
