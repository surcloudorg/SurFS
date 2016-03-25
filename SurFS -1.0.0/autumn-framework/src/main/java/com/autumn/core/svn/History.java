package com.autumn.core.svn;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * <p>Title: 导出SVN操作历史记录</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class History {

    public static void test(String[] args) throws SVNException {
        History display = new History();
        display.setRepositoryUrl("https://liushepeng.autumn.com/svn/liushepeng/");
        display.setUserName("liushepeng");
        display.setPassWord("");
        display.get();
        Collection logEntries = display.getLogEntries();
        for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
            SVNLogEntry logEntry = (SVNLogEntry) entries.next();
            System.out.println("---------------------------------------------");
            System.out.println("revision: " + logEntry.getRevision());
            System.out.println("author: " + logEntry.getAuthor());
            System.out.println("date: " + logEntry.getDate());
            System.out.println("log message: " + logEntry.getMessage());
            if (logEntry.getChangedPaths().size() > 0) {
                System.out.println();
                System.out.println("changed paths:");
                Set changedPathsSet = logEntry.getChangedPaths().keySet();
                for (Iterator changedPaths = changedPathsSet.iterator();
                        changedPaths.hasNext();) {
                    SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(
                            changedPaths.next());
                    System.out.println(" "
                            + entryPath.getType()
                            + "	"
                            + entryPath.getPath()
                            + ((entryPath.getCopyPath() != null) ? " (from "
                            + entryPath.getCopyPath()
                            + " revision "
                            + entryPath.getCopyRevision() + ")"
                            : ""));
                }
            }
        }
    }
    private String repositoryUrl = "";
    private String userName;
    private String passWord;
    private Collection logEntries = null;

    public History() {
        Setup.setupLibrary();
    }

    /**
     * 获取操作历史记录
     *
     * @throws SVNException
     */
    public void get() throws SVNException {
        long startRevision = 0;
        long endRevision;
        SVNURL svnurl = SVNURL.parseURIEncoded(getRepositoryUrl());
        SVNRepository repository = SVNRepositoryFactory.create(svnurl);
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(
                getUserName(), getPassWord());
        repository.setAuthenticationManager(authManager);
        endRevision = repository.getLatestRevision();
        logEntries = repository.log(new String[]{""}, null, startRevision, endRevision, true, true);
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
     * @return the logEntries
     */
    public Collection getLogEntries() {
        return logEntries;
    }
}
