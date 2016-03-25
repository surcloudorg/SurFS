package com.autumn.core.svn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * <p>Title: SVN文件列表</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class RepositoryTree {

    private String repositoryUrl = "";
    private String userName;
    private String passWord;
    private String root = null;
    private String uuid = null;
    private long latestRevision = -1;
    private List<SVNDirEntry> fileLists = null;

    /**
     * 测试
     */
    public static void test(String[] args) throws SVNException {
        RepositoryTree display = new RepositoryTree();
        display.setRepositoryUrl(
                "https://liushepeng.autumn.com/svn/liushepeng/");
        display.setUserName("liushepeng");
        display.setPassWord("781105");
        display.list();
        List<SVNDirEntry> list = display.getFileLists();
        System.out.println("root:" + display.getRoot());
        System.out.println("uuid:" + display.getUuid());
        for (SVNDirEntry entry : list) {
            String path = entry.getURL().toString().replaceFirst(display.getRepositoryUrl(), "");
            System.out.println(path + " (author: '"
                    + entry.getAuthor()
                    + "'; revision: " + entry.getRevision()
                    + "; date: " + entry.getDate() + ")");
        }
        System.out.println("最终版本:" + display.getLatestRevision());
    }

    public RepositoryTree() {
        Setup.setupLibrary();
    }

    public RepositoryTree(String repositoryUrl, String userName,
            String passWord) {
        Setup.setupLibrary();
        this.repositoryUrl = repositoryUrl;
        this.userName = userName;
        this.passWord = passWord;
    }

    /**
     * @return 指定文件是否存在
     */
    public boolean exists() {
        try {
            SVNURL svnurl = SVNURL.parseURIEncoded(getRepositoryUrl());
            SVNRepository repository = SVNRepositoryFactory.create(svnurl);
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(
                    getUserName(), getPassWord());
            repository.setAuthenticationManager(authManager);
            SVNNodeKind nodeKind = repository.checkPath("", -1);
            if (nodeKind == SVNNodeKind.NONE) {
                SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN,
                        "SVN地址没有文件", getRepositoryUrl());
                throw new SVNException(err);
            } else if (nodeKind == SVNNodeKind.FILE) {
                SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN,
                        "SVN地址不是一个目录", getRepositoryUrl());
                throw new SVNException(err);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * list
     *
     * @throws SVNException
     */
    public void list() throws SVNException {
        SVNURL svnurl = SVNURL.parseURIEncoded(getRepositoryUrl());
        SVNRepository repository = SVNRepositoryFactory.create(svnurl);
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(
                getUserName(), getPassWord());
        repository.setAuthenticationManager(authManager);
        SVNNodeKind nodeKind = repository.checkPath("", -1);
        if (nodeKind == SVNNodeKind.NONE) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN,
                    "SVN地址没有文件", getRepositoryUrl());
            throw new SVNException(err);
        } else if (nodeKind == SVNNodeKind.FILE) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN,
                    "SVN地址不是一个目录", getRepositoryUrl());
            throw new SVNException(err);
        }
        root = repository.getRepositoryRoot(true).toString();
        uuid = repository.getRepositoryUUID(true);
        fileLists = listEntries(repository, "");
        latestRevision = repository.getLatestRevision();
    }

    /**
     * list
     *
     * @param repository
     * @param path
     * @return List<SVNDirEntry>
     * @throws SVNException
     */
    public static List<SVNDirEntry> listEntries(SVNRepository repository,
            String path) throws
            SVNException {
        List<SVNDirEntry> list = new ArrayList<SVNDirEntry>();
        Collection entries = repository.getDir(path, -1, null,
                (Collection) null);
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            SVNDirEntry entry = (SVNDirEntry) iterator.next();
            if (entry.getKind() == SVNNodeKind.DIR) {
                List<SVNDirEntry> ls = listEntries(repository,
                        (path.equals("")) ? entry.getName()
                        : path + "/" + entry.getName());
                list.addAll(ls);
            } else {
                list.add(entry);
            }
        }
        return list;
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
     * @return the root
     */
    public String getRoot() {
        return root;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @return the latestRevision
     */
    public long getLatestRevision() {
        return latestRevision;
    }

    /**
     * @return the fileLists
     */
    public List<SVNDirEntry> getFileLists() {
        return fileLists;
    }
}
