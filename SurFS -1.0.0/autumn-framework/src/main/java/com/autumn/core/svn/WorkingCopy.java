package com.autumn.core.svn;

import java.io.*;
import java.util.List;
import java.util.Properties;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.internal.wc.*;
import org.tmatesoft.svn.core.wc.*;

/**
 * <p>Title: SVN工作拷贝API</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class WorkingCopy {

    /**
     * 测试
     *
     */
    public static void test(String[] args) throws SVNException {
        WorkingCopy workingcopy = new WorkingCopy();
        workingcopy.setRepositoryUrl("https://liushepeng.autumn.com/svn/liushepeng/classes");
        workingcopy.setUserName("liushepeng");
        workingcopy.setPassWord("");
        workingcopy.setWorkingCopyPath("D:\\srcs");
        //SVNCommitInfo info = workingcopy.makeDirectory();
        //SVNCommitInfo info = workingcopy.importDirectory();
        //workingcopy.checkout();      
        //List<SVNInfo> infos = workingcopy.showInfo().getInfos();
        //for (SVNInfo info : infos) {
        //InfoHandler.printSVNInfo(info);
        //}
        //workingcopy.delete("D:\\srcs\\com\\autumn\\services\\reidx\\TestInsert.class");
        //workingcopy.addEntry("D:\\srcs\\com\\autumn\\services\\reidx\\TestInsert.class");
        //workingcopy.commit();
        StatusHandler sh = workingcopy.showStatus();
        List<Properties> infos = sh.getStatusList();
        for (Properties info : infos) {
            StatusHandler.printProperties(info);
        }
    }
    private String repositoryUrl = "";
    private String userName;
    private String passWord;
    private String workingCopyPath = "";
    private SVNURL svnUrl = null;
    private SVNClientManager clientManager;
    private ISVNEventHandler commitEventHandler;
    private ISVNEventHandler updateEventHandler;
    private ISVNEventHandler wcEventHandler;

    public WorkingCopy() {
        Setup.setupLibrary();
    }

    /**
     * init
     *
     * @throws SVNException
     */
    private void init() throws SVNException {
        if (getClientManager() != null) {
            return;
        }
        setSvnUrl(SVNURL.parseURIEncoded(getRepositoryUrl()));
        commitEventHandler = new CommitEventHandler();
        updateEventHandler = new UpdateEventHandler();
        wcEventHandler = new WCEventHandler();
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        clientManager = SVNClientManager.newInstance(options, getUserName(), getPassWord());
        getClientManager().getCommitClient().setEventHandler(getCommitEventHandler());
        getClientManager().getUpdateClient().setEventHandler(getUpdateEventHandler());
        getClientManager().getWCClient().setEventHandler(getWcEventHandler());
    }

    /**
     * getWorkingCopyName
     *
     * @return String
     */
    private String getWorkingCopyName() {
        if (getWorkingCopyPath() == null) {
            return null;
        }
        int index = getWorkingCopyPath().lastIndexOf(File.separator);
        if (index >= 0) {
            return getWorkingCopyPath().substring(index + 1);
        } else {
            return getWorkingCopyPath();
        }
    }

    /**
     * makeDirectory
     *
     * @return SVNCommitInfo
     * @throws SVNException
     */
    public SVNCommitInfo makeDirectory() throws SVNException {
        return makeDirectory(null);
    }

    /**
     * makeDirectory
     *
     * @param dir
     * @return SVNCommitInfo
     * @throws SVNException
     */
    public SVNCommitInfo makeDirectory(String dir) throws SVNException {
        init();
        SVNURL url;
        if (dir == null) {
            url = getSvnUrl().appendPath(getWorkingCopyName(), false);
        } else {
            url = getSvnUrl().appendPath(dir, false);
        }
        return getClientManager().getCommitClient().doMkDir(new SVNURL[]{url}, "making a new directory at '" + url + "'");
    }

    /**
     * importDirectory
     *
     * @return SVNCommitInfo
     * @throws SVNException
     */
    public SVNCommitInfo importDirectory() throws SVNException {
        return importDirectory(null, null);
    }

    /**
     * importDirectory
     *
     * @param localPath
     * @param svnPath
     * @return SVNCommitInfo
     * @throws SVNException
     */
    public SVNCommitInfo importDirectory(String localPath, String svnPath) throws SVNException {
        init();
        File file;
        if (localPath == null) {
            file = new File(getWorkingCopyPath());
        } else {
            file = new File(localPath);
        }
        String commitMessage = "importing a new directory '" + file.getAbsolutePath() + "'";
        SVNURL dstURL;
        if (svnPath != null) {
            dstURL = getSvnUrl().appendPath(svnPath, false);
        } else {
            dstURL = getSvnUrl().appendPath(getWorkingCopyName(), false);
        }
        return getClientManager().getCommitClient().doImport(file, dstURL, commitMessage, null, true, false, SVNDepth.INFINITY);
        //return clientManager.getCommitClient().doImport(file, dstURL,commitMessage, true);
    }

    /**
     * checkout
     *
     * @return long
     * @throws SVNException
     */
    public long checkout() throws SVNException {
        return checkout(null, null);
    }

    /**
     * checkout
     *
     * @param localPath
     * @param svnPath
     * @return long
     * @throws SVNException
     */
    public long checkout(String localPath, String svnPath) throws SVNException {
        init();
        String lpath = localPath;
        if (lpath == null) {
            lpath = getWorkingCopyPath();
        }
        SVNURL dstURL = getSvnUrl();
        if (svnPath != null) {
            dstURL = getSvnUrl().appendPath(svnPath, false);
        }
        File file = new File(lpath);
        if (file.exists()) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN,
                    "文件(" + lpath + ")已存在", getRepositoryUrl());
            throw new SVNException(err);
        } else {
            file.mkdirs();
        }
        SVNUpdateClient updateClient = getClientManager().getUpdateClient();
        updateClient.setIgnoreExternals(false);
        return updateClient.doCheckout(dstURL, file, null, SVNRevision.HEAD, null, true);
    }

    /**
     * showInfo
     *
     * @return InfoHandler
     * @throws SVNException
     */
    public InfoHandler showInfo() throws SVNException {
        return showInfo(null);
    }

    /**
     * showInfo
     *
     * @param localPath
     * @return InfoHandler
     * @throws SVNException
     */
    public InfoHandler showInfo(String localPath) throws SVNException {
        init();
        String lpath = localPath;
        if (lpath == null) {
            lpath = getWorkingCopyPath();
        }
        File file = new File(lpath);
        InfoHandler info = new InfoHandler();
        //clientManager.getWCClient().doInfo(file, SVNRevision.WORKING, true, info);
        clientManager.getWCClient().doInfo(file, SVNRevision.WORKING, SVNRevision.HEAD, SVNDepth.INFINITY, null, info);
        return info;
    }

    /**
     * addEntry
     *
     * @param localPath
     * @throws SVNException
     */
    public void addEntry(String localPath) throws SVNException {
        init();
        File file = new File(localPath);
        //clientManager.getWCClient().doAdd(file, false, false, false, true);
        clientManager.getWCClient().doAdd(file, false, false, false, null, true, true);
    }

    /**
     * addEntry
     *
     * @param files
     * @throws SVNException
     */
    public void addEntry(File[] files) throws SVNException {
        init();
        getClientManager().getWCClient().doAdd(files, false, false, false, null, false, false, false);
        //Add(files, false, false, false, SVNDepth.EMPTY, false, false, false);
    }

    /**
     * update
     * 
     * @return long
     * @throws SVNException
     */
    public long update() throws SVNException {
        init();
        String lpath = getWorkingCopyPath();
        File file = new File(lpath);
        SVNUpdateClient updateClient = getClientManager().getUpdateClient();
        updateClient.setIgnoreExternals(false);
        return updateClient.doUpdate(file, SVNRevision.HEAD, null, true, true);
    }

    /**
     * update
     *
     * @param localPath
     * @return long
     * @throws SVNException
     */
    public long update(String localPath) throws SVNException {
        init();
        String lpath = localPath;
        if (lpath == null || lpath.equals("")) {
            lpath = getWorkingCopyPath();
        }
        File file = new File(lpath);
        SVNUpdateClient updateClient = getClientManager().getUpdateClient();
        updateClient.setIgnoreExternals(false);
        return updateClient.doUpdate(file, SVNRevision.HEAD, null, true, true);
        //return updateClient.doUpdate(file, SVNRevision.HEAD, true);
    }

    /**
     * update
     *
     * @param files
     * @return long[]
     * @throws SVNException
     */
    public long[] update(File[] files) throws SVNException {
        init();
        SVNUpdateClient updateClient = getClientManager().getUpdateClient();
        updateClient.setIgnoreExternals(false);
        return updateClient.doUpdate(files, SVNRevision.HEAD, SVNDepth.EMPTY, false, false);
    }

    /**
     * showStatus
     *
     * @return StatusHandler
     * @throws SVNException
     */
    public StatusHandler showStatus() throws SVNException {
        return showStatus(null);
    }

    /**
     * showStatus
     *
     * @param localPath
     * @return StatusHandler
     * @throws SVNException
     */
    public StatusHandler showStatus(String localPath) throws SVNException {
        init();
        String lpath = localPath;
        if (lpath == null) {
            lpath = getWorkingCopyPath();
        }
        File file = new File(lpath);
        boolean isRemote = true;
        boolean isReportAll = false;
        boolean isIncludeIgnored = true;
        boolean isCollectParentExternals = false;
        StatusHandler sh = new StatusHandler(isRemote);
        //clientManager.getStatusClient().doStatus(file, isRecursive, isRemote,isReportAll,isIncludeIgnored,isCollectParentExternals, sh);
        getClientManager().getStatusClient().doStatus(file, SVNRevision.HEAD, null, isRemote, isReportAll, isIncludeIgnored, isCollectParentExternals, sh, null);
        return sh;
    }

    /**
     * commit
     *
     * @return SVNCommitInfo
     * @throws SVNException
     */
    public SVNCommitInfo commit() throws SVNException {
        return commit("");
    }

    /**
     * commit
     *
     * @param localPath
     * @return SVNCommitInfo
     * @throws SVNException
     */
    public SVNCommitInfo commit(String localPath) throws SVNException {
        init();
        String lpath = localPath;
        if (lpath == null || lpath.equals("")) {
            lpath = getWorkingCopyPath();
        }
        File file = new File(lpath);
        return getClientManager().getCommitClient().doCommit(new File[]{file}, true, null, null, null, true, true, null);
        //return clientManager.getCommitClient().doCommit(new File[]{file}, false, "", false, true);        
    }

    /**
     * commit
     *
     * @param files
     * @return SVNCommitInfo
     * @throws SVNException
     */
    public SVNCommitInfo commit(File[] files) throws SVNException {
        init();
        return getClientManager().getCommitClient().doCommit(files, true, null, null, null, true, true, SVNDepth.EMPTY);
        //return clientManager.getCommitClient().doCommit(files, false, "", false, false);
    }

    /**
     * lock
     *
     * @param localPath
     * @throws SVNException
     */
    public void lock(String localPath) throws SVNException {
        init();
        File file = new File(localPath);
        getClientManager().getWCClient().doLock(new File[]{file}, true, "locking " + file.getAbsolutePath());
    }

    /**
     * delete
     *
     * @param localPath
     * @throws SVNException
     */
    public void delete(String localPath) throws SVNException {
        init();
        File file = new File(localPath);
        getClientManager().getWCClient().doDelete(file, true, false);
    }

    /**
     * delete
     *
     * @param file
     * @throws SVNException
     */
    public void delete(File file) throws SVNException {
        init();
        getClientManager().getWCClient().doDelete(file, true, false);
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
     * @return the svnUrl
     */
    public SVNURL getSvnUrl() {
        return svnUrl;
    }

    /**
     * @param svnUrl the svnUrl to set
     */
    public void setSvnUrl(SVNURL svnUrl) {
        this.svnUrl = svnUrl;
    }

    /**
     * @return the clientManager
     */
    public SVNClientManager getClientManager() {
        return clientManager;
    }

    /**
     * @return the commitEventHandler
     */
    public ISVNEventHandler getCommitEventHandler() {
        return commitEventHandler;
    }

    /**
     * @return the updateEventHandler
     */
    public ISVNEventHandler getUpdateEventHandler() {
        return updateEventHandler;
    }

    /**
     * @return the wcEventHandler
     */
    public ISVNEventHandler getWcEventHandler() {
        return wcEventHandler;
    }
}
