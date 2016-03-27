/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.svn;

import com.autumn.core.log.LogFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * <p>Title: 导出SVN目录</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class Export {

    private String repositoryUrl = "";//svn目录
    private String userName;
    private String passWord;
    private String localDir = "";//本地目录
    private long latestRevision = -1;//版本
    private List<String> addFiles = new ArrayList<String>();//导出的文件列表

    /**
     * 测试
     *
     * @param args
     * @throws SVNException
     */
    public static void test(String[] args) throws SVNException {
        Export export = new Export();
        export.setLocalDir("D:\\class");
        export.setRepositoryUrl("https://liushepeng.autumn.com/svn/liushepeng/");
        export.setUserName("liushepeng");
        export.setPassWord("");
        export.doExport();
    }

    public Export() {
        Setup.setupLibrary();
    }

    public void doExport() throws SVNException {
        SVNURL svnurl = SVNURL.parseURIEncoded(getRepositoryUrl());
        File exportDir = new File(getLocalDir());
        if (exportDir.exists()) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.IO_ERROR,
                    "目录已经存在", exportDir);
            throw new SVNException(err);
        }
        exportDir.mkdirs();
        SVNRepository repository = SVNRepositoryFactory.create(svnurl);
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(getUserName(), getPassWord());
        repository.setAuthenticationManager(authManager);
        SVNNodeKind nodeKind = repository.checkPath("", -1);
        if (nodeKind == SVNNodeKind.NONE) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "SVN地址没有文件", getRepositoryUrl());
            throw new SVNException(err);
        } else if (nodeKind == SVNNodeKind.FILE) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "SVN地址不是一个目录", getRepositoryUrl());
            throw new SVNException(err);
        }
        if (latestRevision == -1) {
            setLatestRevision(repository.getLatestRevision());
        }
        ISVNReporterBaton reporterBaton = new ExportReporterBaton(getLatestRevision());
        ISVNEditor exportEditor = new ExportEditor(exportDir, getAddFiles());
        repository.update(getLatestRevision(), null, true, reporterBaton, exportEditor);
        LogFactory.info("Export操作完毕，文件数：" + getAddFiles().size() + ",版本："
                + getLatestRevision(), this.getClass());
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
     * @return the localDir
     */
    public String getLocalDir() {
        return localDir;
    }

    /**
     * @param localDir the localDir to set
     */
    public void setLocalDir(String localDir) {
        this.localDir = localDir;
    }

    /**
     * @return the latestRevision
     */
    public long getLatestRevision() {
        return latestRevision;
    }

    /**
     * @param latestRevision the latestRevision to set
     */
    public void setLatestRevision(long latestRevision) {
        this.latestRevision = latestRevision;
    }

    /**
     * @return the addFiles
     */
    public List<String> getAddFiles() {
        return addFiles;
    }

    /**
     * @param addFiles the addFiles to set
     */
    public void setAddFiles(List<String> addFiles) {
        this.addFiles = addFiles;
    }
}
