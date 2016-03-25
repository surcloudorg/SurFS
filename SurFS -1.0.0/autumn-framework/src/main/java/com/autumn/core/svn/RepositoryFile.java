package com.autumn.core.svn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * <p>Title: SVN下载文件，获取文件属性</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class RepositoryFile {

    private String repositoryUrl = "";
    private String userName;
    private String passWord;
    private String fileName = "";
    private long latestRevision = -1;
    private ByteArrayOutputStream baos = null;
    private SVNProperties fileProperties = null;

    /**
     * 测试
     *
     */
    public static void test(String[] args) throws SVNException, IOException {
        RepositoryFile display = new RepositoryFile();
        display.setRepositoryUrl("https://liushepeng.autumn.com/svn/liushepeng/");
        display.setUserName("liushepeng");
        display.setPassWord("");
        display.setFileName("built-jar.properties");
        display.get();
        SVNProperties property = display.getFileProperties();
        Iterator set = property.nameSet().iterator();
        while (set.hasNext()) {
            String name = set.next().toString();
            System.out.println(name + ":" + property.getStringValue(name));
        }
        if (display.isTextType()) {
            display.getBaos().writeTo(System.out);
        }
    }

    public RepositoryFile() {
        Setup.setupLibrary();
    }

    /**
     * 下载
     *
     * @throws SVNException
     */
    public void get() throws SVNException {
        SVNURL svnurl = SVNURL.parseURIEncoded(getRepositoryUrl());
        SVNRepository repository = SVNRepositoryFactory.create(svnurl);
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(
                getUserName(), getPassWord());
        repository.setAuthenticationManager(authManager);
        fileProperties = new SVNProperties();
        baos = new ByteArrayOutputStream();
        SVNNodeKind nodeKind = repository.checkPath(getFileName(), -1);
        if (nodeKind == SVNNodeKind.NONE) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "SVN地址没有文件", getRepositoryUrl());
            throw new SVNException(err);
        } else if (nodeKind == SVNNodeKind.DIR) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "SVN地址不是一个文件", getRepositoryUrl());
            throw new SVNException(err);
        }
        repository.getFile(getFileName(), -1, getFileProperties(), getBaos());
        setLatestRevision(repository.getLatestRevision());
    }

    /**
     *
     * @return 是否是纯文本
     */
    public boolean isTextType() {
        return SVNProperty.isTextMimeType(getMimeType());
    }

    /**
     * @return 文件类型
     */
    public String getMimeType() {
        if (getFileProperties() == null) {
            return null;
        }
        return getFileProperties().getStringValue(SVNProperty.MIME_TYPE);

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
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
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
     * @return the baos
     */
    public ByteArrayOutputStream getBaos() {
        return baos;
    }

    /**
     * @return the fileProperties
     */
    public SVNProperties getFileProperties() {
        return fileProperties;
    }
}
